package slug.invaders.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import slug.invaders.Config;
import slug.invaders.GameWindow;
import slug.invaders.Main;
import slug.invaders.assets.ImageCache;
import slug.invaders.assets.sounds.SoundLib;
import slug.invaders.util.Hitbox;
import slug.invaders.util.Log;
import slug.invaders.util.RenderUtil;
import slug.invaders.util.Util;

public class HighscoreScreen implements Screen {
    private GameWindow window;
    
    private final Font titleFont = new Font("Comic Sans MS", Font.BOLD, 72);
    private final Color titleColor = Color.white;
    private final String titleText = "Highscores";
    
    private final Hitbox displayBox = new Hitbox(0, 0, 0, 0);
    private final Color displayBoxColor = new Color(200, 200, 200, 100);
    private final Font displayFont = new Font("Comic Sans MS", Font.BOLD, 24);
    private int displayBoxPlaytimeX = 0;
    
    private List<HPacket> list = null;
    
    private final Hitbox menuBtn = new Hitbox(0, 0, 0, 0);
    private final Font menuBtnFont = new Font("Comic Sans MS", Font.BOLD, 16);
    private final String menuBtnText = "MAIN MENU";
    
    private final Hitbox playBtn = new Hitbox(0, 0, 0, 0);
    private final Font playBtnFont = menuBtnFont;
    private final String playBtnText = "PLAY";
    
    private MouseAdapter adapter = null;
    private long lastWipe = 0;
    
    @Override
    public void create(GameWindow window) {
        this.window = window;
        displayBox.setHeight(450);
        displayBox.setWidth(580);
        displayBox.setX(Util.centerValue(displayBox.getWidth(), window.getWidth()));
        displayBox.setY(200);
        displayBoxPlaytimeX = displayBox.getX()+displayBox.getWidth()-Util.getStringWidth("Playtime", displayFont)-40;
        
        menuBtn.setHeight(40);
        menuBtn.setWidth(150);
        menuBtn.setX(20);
        menuBtn.setY(window.getHeight() - 60);
        
        playBtn.setHeight(40);
        playBtn.setWidth(150);
        playBtn.setX(window.getWidth() - playBtn.getWidth() - 20);
        playBtn.setY(window.getHeight() - 60);
        
        Log.info("[HighscoreScreen] Reading data from highscores memory ..");
        list = new ArrayList<>();
        for(String key : Main.highscores.keySet()) {
            String data = Main.highscores.get(key);
            String[] split = data.split(":");
            
            if(split.length >= 2) {
                try {
                    int wave = Integer.parseInt(split[0]);
                    long playtime = Long.parseLong(split[1]);
                    
                    list.add(new HPacket(key, wave, playtime));
                    Log.info("[HighscoreScreen] Added player: " + key + ", data: " + data);
                } catch(NumberFormatException e) {
                    Log.info("[HighscoreScreen] Failed to parse a number for key: " + key + ", data: " + data);
                }
            } else {
                Log.info("[HighscoreScreen] Could not load player data correctly: " + key + ", data: " + data);
            }
        }
        Log.info("[HighscoreScreen] Data loaded. Sorting ..");
        Collections.sort(list, (HPacket h1, HPacket h2) -> (int) (h2.getWave()- h1.getWave()));
        Log.info("[HighscoreScreen] Data sorted.");
        
        adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(Util.isWithinArea(e.getX(), e.getY(), menuBtn)) {
                    Log.info("[HighscoreScreen] Switching back to main menu ..");
                    window.getPanel().removeMouseListener(adapter);
                    SoundLib.MENU_SWING.play(1.0, -0.7);
                    MenuScreen screen = new MenuScreen();
                    ScreenManager.setScreen(screen);
                } else if(Util.isWithinArea(e.getX(), e.getY(), playBtn)) {
                    Log.info("[HighscoreScreen] Switching to play screen ..");
                    window.getPanel().removeMouseListener(adapter);
                    SoundLib.MENU_SWING.play(1.0, 0.7);
                    PlayerScreen screen = new PlayerScreen();
                    ScreenManager.setScreen(screen);
                }
            }
        };
        window.getPanel().addMouseListener(adapter);
    }

    @Override
    public void update(double dt) {
        if(window.getInput().isKeyPressed(KeyEvent.VK_ESCAPE)) {
            Log.info("[HighscoreScreen] Switching back to main menu ..");
            window.getPanel().removeMouseListener(adapter);
            MenuScreen screen = new MenuScreen();
            ScreenManager.setScreen(screen);
        } else if(window.getInput().isKeyPressed(KeyEvent.VK_DELETE)) {
            if(System.currentTimeMillis()-lastWipe >= 1000) {
                wipeHighscores();
                lastWipe = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(ImageCache.BACKGROUND, 0, 0, null);
        g.setColor(Config.OVERLAY_COLOR);
        g.fillRect(0, 0, window.getWidth(), window.getHeight());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g.setFont(titleFont);
        g.setColor(titleColor);
        g.drawString(titleText, RenderUtil.centerStringX(titleText, window.getWidth(), g, 0), RenderUtil.centerStringY(titleText, window.getHeight(), g, -250));
        
        g.setColor(displayBoxColor);
        g.fillRect(displayBox.getX(), displayBox.getY(), displayBox.getWidth(), displayBox.getHeight());
        
        g.setColor(Color.gray);
        g.setFont(menuBtnFont);
        g.fillRect(menuBtn.getX(), menuBtn.getY(), menuBtn.getWidth(), menuBtn.getHeight());
        g.setColor(Color.white);
        g.drawString(menuBtnText, RenderUtil.centerStringX(menuBtnText, menuBtn.getWidth(), g, menuBtn.getX()), RenderUtil.centerStringY(menuBtnText, menuBtn.getHeight(), g, menuBtn.getY()));
        
        g.setColor(Color.gray);
        g.setFont(playBtnFont);
        g.fillRect(playBtn.getX(), playBtn.getY(), playBtn.getWidth(), playBtn.getHeight());
        g.setColor(Color.white);
        g.drawString(playBtnText, RenderUtil.centerStringX(playBtnText, playBtn.getWidth(), g, playBtn.getX()), RenderUtil.centerStringY(playBtnText, playBtn.getHeight(), g, playBtn.getY()));
        
        g.setColor(Color.white);
        g.setFont(displayFont);
        
        if(list.isEmpty()) {
            String s = "There are no scores to display";
            g.drawString(s, RenderUtil.centerStringX(s, displayBox.getWidth(), g, displayBox.getX()), displayBox.getY() + 30);
        } else {
            g.drawString("Rank", displayBox.getX() + 10, displayBox.getY() + 30);
            g.drawString("Nickname", displayBox.getX() + 90, displayBox.getY() + 30);
            g.drawString("Wave", displayBox.getX() + 340, displayBox.getY() + 30);
            g.drawString("Playtime", displayBoxPlaytimeX, displayBox.getY() + 30);
            
            g.setColor(Color.yellow);
            int lastY = displayBox.getY() + 65;
            int place = 1;
            for(HPacket packet : list) {
                g.drawString(place + ".", displayBox.getX() + 10, lastY);
                g.drawString(packet.getNickname(), displayBox.getX() + 90, lastY);
                g.drawString(packet.getWave() + "", displayBox.getX() + 340, lastY);
                g.drawString(String.format("%02d:%02d:%02d", 
                    TimeUnit.MILLISECONDS.toHours(packet.getPlaytime()),
                    TimeUnit.MILLISECONDS.toMinutes(packet.getPlaytime()) - 
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(packet.getPlaytime())),
                    TimeUnit.MILLISECONDS.toSeconds(packet.getPlaytime()) - 
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(packet.getPlaytime()))), displayBoxPlaytimeX, lastY);
                
                lastY += 27;
                place++;
                
                if(place > 15) {
                    break;
                }
            }
        }
    }

    @Override
    public GameWindow getGameWindow() {
        return window;
    }
    
    public void wipeHighscores() {
        list.clear();
        Main.highscores.clear();
        Main.saveHighscores();
    }
    
    public class HPacket {
        private final String nickname;
        private final int wave;
        private final long playtime;
        
        public HPacket(String nickname, int wave, long playtime) {
            this.nickname = nickname;
            this.wave = wave;
            this.playtime = playtime;
        }
        
        public int getWave() {
            return wave;
        }
        
        public long getPlaytime() {
            return playtime;
        }
        
        public String getNickname() {
            return nickname;
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
