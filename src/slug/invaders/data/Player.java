package slug.invaders.data;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import slug.invaders.Config;
import slug.invaders.GameWindow;
import slug.invaders.Main;
import slug.invaders.assets.Image;
import slug.invaders.assets.sounds.SoundLib;
import slug.invaders.data.SaltBullet.BulletDirection;
import slug.invaders.screens.PlayScreen;
import slug.invaders.util.Hitbox;
import slug.invaders.util.Log;
import slug.invaders.util.Util;

public class Player {
    private int score = 0;
    private String nickname = "Unknown";
    private final Hitbox hitbox;
    private int moveSpeed = 12;
    private final BufferedImage FRAME_DEFAULT;
    private final BufferedImage FRAME_RIGHT;
    private final BufferedImage FRAME_LEFT;
    private AnimationFrame frame = AnimationFrame.DEFAULT;
    public final CopyOnWriteArrayList<SaltBullet> bullets = new CopyOnWriteArrayList<>();
    private final PlayScreen screen;
    private boolean canMove = true;
    private boolean isDead = false;
    private static int killcount = 0;
    private int strongSaltAmmo = 10;
    private boolean lasersight = false;
    private long lastStepPlayTime = 0;
    private int moveFrames = 0; // Amount of frames the player has been consistently moving for.
        
    public Player(String nickname, PlayScreen screen) throws IOException {
        this.nickname = nickname;
        this.FRAME_DEFAULT = Image.PLAYER_DEFAULT.getBufferedImage();
        this.FRAME_LEFT = Image.PLAYER_LEFT.getBufferedImage();
        this.FRAME_RIGHT = Image.PLAYER_RIGHT.getBufferedImage();
        this.screen = screen;
        this.hitbox = new Hitbox(0, 0, FRAME_DEFAULT.getWidth(), FRAME_DEFAULT.getHeight());
    }
    
    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
    
    public int getMoveSpeed() {
        return moveSpeed;
    }
    
    public void setX(int x) {
        hitbox.setX(x);
    }
    
    public int getX() {
        return hitbox.getX();
    }
    
    public void setY(int y) {
        hitbox.setY(y);
    }
    
    public int getY() {
        return hitbox.getY();
    }
    
    public Hitbox getHitbox() {
        return hitbox;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public void increaseScore() {
        setScore(score++);
    }
    
    public void decreaseScore() {
        setScore(score--);
    }
    
    public void setAnimationFrame(AnimationFrame frame) {
        this.frame = frame;
    }
    
    public int getStrongSaltAmmo() {
        return strongSaltAmmo;
    }
    
    public void addStrongSaltAmmo(int amount) {
        this.strongSaltAmmo += amount;
        Log.info("[Player] Added Strong Salt ammo. Ammo added: " + amount);
    }
    
    public void removeStrongSaltAmmo(int amount) {
        this.strongSaltAmmo -= amount;
        
        if(strongSaltAmmo < 0) {
            strongSaltAmmo = 0;
        }
        Log.info("[Player] Removed Strong Salt ammo. Ammo removed: " + amount);
    }
    
    public void expendStrongSaltAmmo() {
        removeStrongSaltAmmo(1);
    }
    
    public void reset() {
        Log.info("[Player] Resetting player ..");
        setScore(0);
        isDead = false;
        canMove = true;
        bullets.clear();
        strongSaltAmmo = 10;
        setAnimationFrame(AnimationFrame.DEFAULT);
        setX(Util.centerValue(getImage().getWidth(), GameWindow.instance.getWidth()));
        setY(GameWindow.instance.getHeight()-getImage().getHeight());
    }
    
    public void moveRight(boolean sprint) {
        if(!canMove) {
            return;
        }
        int speed = moveSpeed * (sprint ? 2 : 1);
        
        if(System.currentTimeMillis()-lastStepPlayTime >= 270) {
            if(Main.rand.nextBoolean()) {
                SoundLib.PLAYER_STEP_1.play(0.5f);
            } else {
                SoundLib.PLAYER_STEP_2.play(0.5f);
            }
            lastStepPlayTime = System.currentTimeMillis();
        }
        setAnimationFrame(AnimationFrame.RIGHT);
        setX(getX() + speed);
        
        if(getX()+getImage().getWidth() > GameWindow.instance.getWidth() + (getImage().getWidth() / 3)) {
            setAnimationFrame(AnimationFrame.DEFAULT);
            setX(GameWindow.instance.getWidth()-getImage().getWidth() + (getImage().getWidth() / 3));
        }
        
        moveFrames++;
    }
        
    public void moveLeft(boolean sprint) {
        if(!canMove) {
            return;
        }
        int speed = moveSpeed * (sprint ? 2 : 1);
        
        if(System.currentTimeMillis()-lastStepPlayTime >= 270) {
            if(Main.rand.nextBoolean()) {
                SoundLib.PLAYER_STEP_1.play(0.5f);
            } else {
                SoundLib.PLAYER_STEP_2.play(0.5f);
            }
            lastStepPlayTime = System.currentTimeMillis();
        }
        setAnimationFrame(AnimationFrame.LEFT);
        setX(getX() - speed);
        
        if(getX() < -(getImage().getWidth() / 3)) {
            setAnimationFrame(AnimationFrame.DEFAULT);
            setX(-(getImage().getWidth() / 3));
        }
        
        moveFrames++;
    }
    
    public void idle() {
        moveFrames = 0;
        
        if(frame != AnimationFrame.DEFAULT) {
            setAnimationFrame(AnimationFrame.DEFAULT);
        }
    }
    
    public void shoot(boolean forceDirectionUP) {
        if(isDead) {
            return;
        }
        boolean isWeakSalt = strongSaltAmmo <= 0;
        
        /**
         * Here we are trying to see if the player is currently in movement.
         * If the player is moving, we need to adjust the player and bullet animation and direction.
         */
        if(moveFrames > 15) {
            BulletDirection direction = BulletDirection.UP;
            
            /**
             * If forceDirectionUP is set to TRUE it means that the player should shoot a bullet straight up no matter what.
             */
            if(!forceDirectionUP) {
                switch(frame) {
                    case DEFAULT: direction = BulletDirection.UP; break;
                    case LEFT: direction = BulletDirection.LEFT; break;
                    case RIGHT: direction = BulletDirection.RIGHT; break;
                }
            } else {
                setAnimationFrame(AnimationFrame.DEFAULT);
                canMove = false;
                
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        canMove = true;
                    }
                }, 60);
            }
            SaltBullet b = new SaltBullet(this, direction, isWeakSalt);
            b.init();
            bullets.add(b);
            expendStrongSaltAmmo();
        } else {
            if(frame != AnimationFrame.DEFAULT) {
                setAnimationFrame(AnimationFrame.DEFAULT);
                canMove = false;

                SaltBullet b = new SaltBullet(this, BulletDirection.UP, isWeakSalt);
                b.init();
                bullets.add(b);
                expendStrongSaltAmmo();

                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        canMove = true;
                    }
                }, 80);
            } else {
                BulletDirection direction = BulletDirection.UP;
                SaltBullet b = new SaltBullet(this, direction, isWeakSalt);
                b.init();
                bullets.add(b);
                expendStrongSaltAmmo();
            }
        }
        
        if(isWeakSalt) {
            SoundLib.GUN_SHOT.play(Config.WEAK_GUNSHOT_VOLUME);
        } else {
            SoundLib.GUN_SHOT.play(Config.GUNSHOT_VOLUME);
        }
    }
    
    public void kill() {
        Log.info("[Player] Player killed!");
        SoundLib.PLAYER_DEATH.play();
        isDead = true;
        canMove = false;
        //new BloodPool(getX(), getY()).spawn();
        screen.onGameOver(WaveManager.getWaveNumber());
    }
    
    public void draw(Graphics2D g) {
        if(!isDead) {
            g.drawImage(getImage(), getX(), getY(), null);
            
            if(lasersight) {
                g.setColor(Config.LASER_SIGHT_COLOR);
                g.setStroke(Config.LASER_SIGHT_STROKE);
                
                if(frame == AnimationFrame.DEFAULT) {
                    int x = getX() + hitbox.getWidth()/2;
                    g.drawLine(x, getY(), x, 0);
                } else if(frame == AnimationFrame.LEFT) {
                    int y = getY() + hitbox.getHeight()/2;
                    g.drawLine(getX(), y, 0, y);
                } else {
                    int y = getY() + hitbox.getHeight()/2;
                    g.drawLine(hitbox.getRightX(), y, Config.GAME_WIDTH, y);
                }
            }
        }
        
        for(SaltBullet b : bullets) {
            b.move();
            b.draw(g);
        }
    }
    
    public CopyOnWriteArrayList<SaltBullet> getBullets() {
        return bullets;
    }
    
    public BufferedImage getImage() {
        return getFrameImage(frame);
    }
    
    public BufferedImage getFrameImage(AnimationFrame frame) {
        switch(frame) {
            case DEFAULT: return FRAME_DEFAULT;
            case LEFT: return FRAME_LEFT;
            case RIGHT: return FRAME_RIGHT;
        }
        return null;
    }
    
    public enum AnimationFrame {
        DEFAULT(), 
        RIGHT, 
        LEFT;
    }
    
    @Deprecated
    public void giveLasersight() {
        setLasersight(true);
        SoundLib.LASER_SIGHT_ACTIVATED.play();
        
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                setLasersight(false);
            }
        }, 15000);
    }
    
    public void setLasersight(boolean lasersight) {
        this.lasersight = lasersight;
    }
    
    public static void increaseKillcount(int amount) {
        killcount += amount;
    }
    
    public static int getKillcount() {
        return killcount;
    }
    
    public PlayScreen getPlayScreen() {
        return screen;
    }
}
