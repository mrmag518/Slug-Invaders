 package slug.invaders.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import slug.invaders.Config;
import slug.invaders.GameWindow;
import slug.invaders.Main;
import slug.invaders.assets.ImageCache;
import slug.invaders.assets.sounds.SoundLib;
import slug.invaders.data.AmmoReceived;
import slug.invaders.data.BloodPool;
import slug.invaders.data.Lane;
import slug.invaders.data.Player;
import slug.invaders.data.SaltBomb;
import slug.invaders.data.SaltBucket;
import slug.invaders.data.SaltWave;
import slug.invaders.data.WaveManager;
import slug.invaders.data.WavePerk;
import slug.invaders.util.Input;
import slug.invaders.util.Log;
import slug.invaders.util.Loop;
import slug.invaders.util.RenderUtil;

public class PlayScreen implements Screen {
    private GameWindow window = null;
    private Input input = null;
    private String presetName = "Unknown";
    private Player player = null;
    private long playtimeStart = 0;
    public boolean canShoot = true;
    public boolean canShootController = true;
    public final Font waveBarFont = new Font("Comic Sans MS", Font.BOLD, 13);
    public final Font infoBarFont = new Font("Comic Sans MS", Font.BOLD, 9);
    public final Font waveDisplayFont = new Font("Comic Sans MS", Font.BOLD, 64);
    private int waveDisplayX = -200;
    private final String waveDisplayDemoText = "Wave 0 approaching ..";
    private boolean spawningWave = false;
    private static boolean aimForHuman = false;
    private String currentWaitMessage = "this is not supposed to be seen";
    private final String[] waitMessages = {"any minute..", "just wait", "hold on", "they'll be here..", "they're coming", "soon", "they aren't THAT fast", 
        "they be sluggin, you be hatin", "sliding..", "just a second", "over there!", "where?", "don't slip now..", "don't be so eager", "hustlin'",
        "absoloutely none.. yet", "right around the corner", "looked behind you?", "coming for you", "they don't have legs", "on their way", "ready?",
        "bad weather, tomorrow instead?"};
    private SaltBucket bucket = null;
    private final List<AmmoReceived> ammo_rec_list = new ArrayList<>(); // list of ammo received announcements currently being displayed
    private WavePerk wavePerk = null;
    private SaltWave saltWave = null;
    private final CopyOnWriteArrayList<SaltBomb> bombs = new CopyOnWriteArrayList<>();
    
    @Override
    public void create(GameWindow window) {
        currentWaitMessage = getRandomWaitMessage();
        this.window = window;
        this.input = window.getInput();
        try {
            this.player = new Player(presetName, this);
            player.reset();
        } catch (IOException ex) {
            Log.error("[PlayScreen] Failed to load player object. Shutting down ..");
            System.exit(0);
        }
        Lane.loadLanes(this);
        
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                initWaveSpawn();
            }
        }, 1000);
        
        waveDisplayX = -window.getPanel().getGraphics().getFontMetrics().stringWidth(waveDisplayDemoText) - 10;
        
        createBucket(true);
        
        Loop bucket_spawner = new Loop(() -> {
            if(WaveManager.hasActiveWave()) {
                if(bucket != null || saltWave != null) {
                    return;
                }
                
                if(Main.rand.nextInt(100) < 45) {
                    Log.info("[PlayScreen] Creating salt bucket ..");
                    createBucket(false);
                }
            }
        }, 15000, 6000, "BUCKET_SPAWNER");
        bucket_spawner.start();
        
        Loop waveperk_spawner = new Loop(() -> {
            if(Main.rand.nextInt(100) < 15) {
                if(getWavePerk() == null) {
                    if(WaveManager.hasActiveWave() && WaveManager.getWaveNumber() >= 5 && saltWave == null) {
                        spawnWavePerk();
                    }
                }
            }
        }, 10000, 5000, "wp_spawner");
        waveperk_spawner.start();
        
        Loop bomb_spawner = new Loop(() -> {
            if(WaveManager.hasActiveWave()) {
                if(Main.rand.nextInt(100) < 15 && WaveManager.getWaveNumber() >= 3) {
                    spawnBomb();
                }
            }
        }, 5000, 5000, "bomb_spawner");
        bomb_spawner.start();
        
        window.getFrame().setTitle(window.getFrame().getTitle() + " - Playing as: " + presetName);
    }

    @Override
    public void update(double dt) {
        if(spawningWave) {
            int i = window.getWidth() / 2 - window.getPanel().getGraphics().getFontMetrics(waveDisplayFont).stringWidth(waveDisplayDemoText) / 2 - 40;
            
            if(waveDisplayX < i) {
                waveDisplayX += 30;
            } else if(waveDisplayX >= i && waveDisplayX <= i + 100) {
                waveDisplayX += 2;
            } else {
                waveDisplayX += 30;
            }
            
            if(waveDisplayX > window.getWidth()) {
                spawningWave = false;
                WaveManager.spawnNew();
            }
        }
        
        /**
         * Check for player move inputs.
         */
        if(input.isKeyPressed(KeyEvent.VK_RIGHT) || input.isKeyPressed(KeyEvent.VK_D) || window.getFirstController().dpadRight) {
            player.moveRight(input.isKeyPressed(KeyEvent.VK_SHIFT) || input.isKeyPressed(KeyEvent.VK_UP) || window.getFirstController().rightStickClick || window.getFirstController().leftStickClick || window.getFirstController().leftTrigger >= 0.8);
        } else if(input.isKeyPressed(KeyEvent.VK_LEFT) || input.isKeyPressed(KeyEvent.VK_A) || window.getFirstController().dpadLeft) {
            player.moveLeft(input.isKeyPressed(KeyEvent.VK_SHIFT) || input.isKeyPressed(KeyEvent.VK_UP) || window.getFirstController().rightStickClick || window.getFirstController().leftStickClick || window.getFirstController().leftTrigger >= 0.8);
        } else if(window.getFirstController().leftStickX >= 0.5){
            player.moveRight(input.isKeyPressed(KeyEvent.VK_SHIFT) || input.isKeyPressed(KeyEvent.VK_UP) || window.getFirstController().rightStickClick || window.getFirstController().leftStickClick || window.getFirstController().leftTrigger >= 0.8);
        } else if(window.getFirstController().leftStickX <= -0.5){
            player.moveLeft(input.isKeyPressed(KeyEvent.VK_SHIFT) || input.isKeyPressed(KeyEvent.VK_UP) || window.getFirstController().rightStickClick || window.getFirstController().leftStickClick || window.getFirstController().leftTrigger >= 0.8);
        } else {
            player.idle();
        }
        
        /**
         * Specifically handling of controller inputs for shooting.
         */
        if(window.getFirstController().a || window.getFirstController().rightTrigger >= 0.6) {
            if(canShootController) {
                player.shoot(false);
                canShootController = false;
            }
        } else {
            canShootController = true;
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(ImageCache.BACKGROUND, 0, 0, null);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw blood pools
        for(BloodPool pool : BloodPool.getPools()) {
            pool.draw(g);
        }
        
        // Update and Draw Lanes.
        if(WaveManager.hasActiveWave()) {
            WaveManager.getCurrent().draw(g, player);
        }
        
        for(Lane lane : Lane.getLanes()) {
            lane.draw(g);
        }
        
        if(wavePerk != null) {
            wavePerk.draw(g);
        }
        
        if(bucket != null) {
            bucket.render(g);
        }
        
        // General Information Display
        String waveBarText = "Wave: " + WaveManager.getWaveNumber() 
                + " - Slugs: " + (WaveManager.getCurrent() == null ? currentWaitMessage : WaveManager.getCurrent().getSlugsLeft() == 69 ? "69 ;)" : WaveManager.getCurrent().getSlugsLeft());
        g.setFont(waveBarFont);
        g.setColor(Config.WAVE_BAR_COLOR);
        g.drawString(waveBarText, 8, 20);
        
        // Strong Salt Ammo
        g.setColor(Config.INFO_BAR_COLOR);
        g.drawString("Strong Salt: " + player.getStrongSaltAmmo(), 8, Config.GAME_HEIGHT - 20);
        g.setFont(infoBarFont);
        g.drawString("Killcount: " + Player.getKillcount(), 8, Config.GAME_HEIGHT - 8);
        
        player.draw(g);
        for(AmmoReceived ar : ammo_rec_list) {
            ar.render(g);
        }
        
        for(SaltBomb bomb : bombs) {
            bomb.move();
            
            if(bomb.getState() == SaltBomb.SaltBombState.EXPLODING) {
                bombs.remove(bomb);
            } else {
                bomb.draw(g);
            }
        }
        
        if(saltWave != null) {
            saltWave.draw(g);
        }
        
        // Wave Spawning Display Effect
        if(spawningWave) {
            String waveDisplayText = "Wave " + (WaveManager.getWaveNumber()+1) + " approaching ..";
            g.setColor(Color.white);
            g.setFont(waveDisplayFont);
            g.drawString(waveDisplayText, waveDisplayX, RenderUtil.centerStringY(waveDisplayText, window.getHeight(), g, -50));
        }
    }

    @Override
    public GameWindow getGameWindow() {
        return window;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public void requestSalt(boolean forceDirectionUP) {
        if(canShoot) {
            player.shoot(forceDirectionUP);
            canShoot = false;
        }
    }
    
    public CopyOnWriteArrayList<SaltBomb> getBombs() {
        return bombs;
    }
    
    public void spawnBomb() {
        Log.info("[PlayScreen] Spawning bomb ..");
        SaltBomb bomb = new SaltBomb(Main.rand.nextInt(Config.GAME_WIDTH - ImageCache.SALT_BOMB_1.getWidth()));
        bombs.add(bomb);
    }
    
    public void initWaveSpawn() {
        Log.info("[PlayScreen] Initializing new wave spawn ..");
        waveDisplayX = -window.getPanel().getGraphics().getFontMetrics(waveDisplayFont).stringWidth(waveDisplayDemoText) - 10;
        spawningWave = true;
        
        if(WaveManager.getWaveNumber() <= 0) {
            playtimeStart = System.currentTimeMillis();
        }
    }
    
    public void startNextWaveTimer() {
        Log.info("[PlayScreen] Next wave initializing in 3 seconds ..");
        
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                SoundLib.WAVE_COMPLETED.play();
            }
        }, 500);
        
        currentWaitMessage = getRandomWaitMessage();
        
        Timer tt = new Timer();
        tt.schedule(new TimerTask() {
            @Override
            public void run() {
                initWaveSpawn();
            }
        }, 3000);
    }
    
    public static void startAimingForHuman() {
        Log.info("[PlayScreen] Slugs will now aim for the player.");
        aimForHuman = true;
    }
    
    public static boolean isAimingForHuman() {
        return aimForHuman;
    }
    
    public String getRandomWaitMessage() {
        return waitMessages[Main.rand.nextInt(waitMessages.length)];
    }
    
    public long getPlaytimeMS() {
        return (System.currentTimeMillis()-playtimeStart);
    }
    
    public void onGameOver(int wave) {
        Log.info("[PlayScreen] Game over!");
        Main.highscores.put(player.getNickname(), wave + ":" + getPlaytimeMS());
        Main.saveHighscores();
        GameOverScreen screen = new GameOverScreen();
        screen.onGameOver(wave, getPlaytimeMS());
        ScreenManager.setScreen(screen);
    }
    
    public void createBucket(boolean first) {
        Log.info("[PlayScreen] Creating a salt bucket ..");
        
        if(bucket != null) {
            bucket.despawn();
        }
        int x = Main.rand.nextInt(Config.GAME_WIDTH);
        
        if(x+ImageCache.SALT_BUCKET.getWidth() > Config.GAME_WIDTH) {
            x = Config.GAME_WIDTH-ImageCache.SALT_BUCKET.getWidth();
        }
        bucket = new SaltBucket(this, x, WaveManager.hasActiveWave() ? WaveManager.getCurrent().getSlugAmount() : 10);
        bucket.spawn(first ? 8000 : Config.BUCKET_UPTIME);
    }
    
    /**
     * Null the bucket object.
     * This will force the bucket to disappear.
     * The proper method to remove a bucket from the map is to call the despawn(); method inside the bucket class.
     */
    public void forceRemoveBucket() {
        bucket = null;
    }
    
    public void displayAmmoReceived(int x, int ammoAmount) {
        AmmoReceived ar = new AmmoReceived(x, ammoAmount);
        ammo_rec_list.add(ar);
        ar.startAnimation(this);
    }
    
    public List<AmmoReceived> getAmmoReceivedList() {
        return ammo_rec_list;
    }
    
    public void presetPlayerName(String name) {
        this.presetName = name;
    }
    
    public void spawnWavePerk() {
        int y = Main.rand.nextInt(window.getHeight());
        
        if(y < 0) {
            y += 50;
        } else if(y > 400) {
            y = 400;
        }
        Log.info("[PlayScreen] Spawning wave perk at y=" + y + " ..");
        this.wavePerk = new WavePerk(this, y, Main.rand.nextInt(2), Main.rand.ints(2, 6).findAny().getAsInt());
    }
    
    public void despawnWavePerk() {
        Log.info("[PlayScreen] Despawning wave perk ..");
        this.wavePerk = null;
    }
    
    public WavePerk getWavePerk() {
        return wavePerk;
    }
    
    public void grantWavePerk() {
        Log.info("[PlayScreen] Granting wave perk ..");
        SoundLib.SPLASH.play(0.7f);
        despawnWavePerk();
        this.saltWave = new SaltWave(this);
    }
    
    public void despawnWave() {
        Log.info("[PlayScreen] Despawning salt wave ..");
        this.saltWave = null;
    }
    
    public SaltWave getSaltWave() {
        return saltWave;
    }
    
    public SaltBucket getBucket() {
        return bucket;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
