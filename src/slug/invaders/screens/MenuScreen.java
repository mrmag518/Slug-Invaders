package slug.invaders.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import slug.invaders.Config;
import slug.invaders.GameWindow;
import slug.invaders.assets.ImageCache;
import slug.invaders.assets.sounds.SoundLib;
import slug.invaders.util.Hitbox;
import slug.invaders.util.Input;
import slug.invaders.util.Log;
import slug.invaders.util.RenderUtil;
import slug.invaders.util.Util;

public class MenuScreen implements Screen {
    private GameWindow window;
    private final Font playFont = new Font("Comic Sans MS", Font.BOLD, 45);
    private final String playText = "Press any button to play!";
    private final Font versionFont = new Font("Comic Sans MS", Font.BOLD, 12);
    private int playTextOffsetX = 0;
    private boolean playTextGoingRight = true;
    private long start = 0;
    private Input input;
    private final Hitbox highscoreBtn = new Hitbox(0, 0, 0, 0);
    private final Color highscoreBtnColor = new Color(Color.gray.getRed(), Color.gray.getRed(), Color.gray.getRed(), 180);
    private final String highscoreBtnText = "View Highscores";
    private final Font highscoreBtnFont = new Font("Comic Sans MS", Font.BOLD, 32);
    private final Color highscoreBtnTextColor = new Color(115, 255, 133);
    private MouseAdapter adapter = null;
    
    @Override
    public void create(GameWindow window) {
        this.start = System.currentTimeMillis();
        this.window = window;
        this.input = window.getInput();
        
        highscoreBtn.setHeight(70);
        highscoreBtn.setWidth(400);
        highscoreBtn.setX(Util.centerValue(highscoreBtn.getWidth(), window.getWidth()));
        highscoreBtn.setY(window.getHeight() - 200);
        
        adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(Util.isWithinArea(e.getX(), e.getY(), highscoreBtn)) {
                    window.getPanel().removeMouseListener(adapter);
                    SoundLib.MENU_SWING.play();
                    HighscoreScreen screen = new HighscoreScreen();
                    ScreenManager.setScreen(screen);
                }
            }
        };
        window.getPanel().addMouseListener(adapter);
    }

    @Override
    public void update(double dt) {
        /**
         * Moving play text effect.
         */
        if(playTextGoingRight) {
            playTextOffsetX += 1;
            
            if(playTextOffsetX >= 15) {
                playTextOffsetX = 15;
                playTextGoingRight = false;
            }
        } else {
            playTextOffsetX -= 1;
            
            if(playTextOffsetX <= -15) {
                playTextOffsetX = -15;
                playTextGoingRight = true;
            }
        }
        
        /**
         * Switch to play screen is a button is pressed. 
         * Only works 500ms after this screen has been initialized.
         */
        if(System.currentTimeMillis()-start >= 500) {
            if(input.isAnyKeyPressed() || window.getFirstController().aJustPressed || window.getFirstController().startJustPressed) {
                window.getPanel().removeMouseListener(adapter);
                SoundLib.MENU_SWING.play();
                PlayerScreen screen = new PlayerScreen();
                ScreenManager.setScreen(screen);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawImage(ImageCache.BACKGROUND, 0, 0, null);
        
        g.setColor(Color.white);
        g.drawImage(ImageCache.TITLE_LOGO, Util.centerValue(ImageCache.TITLE_LOGO.getWidth(), Config.GAME_WIDTH), Util.centerValue(ImageCache.TITLE_LOGO.getHeight(), Config.GAME_HEIGHT) / 3, null);
        
        g.setFont(playFont);
        g.drawString(playText, RenderUtil.centerStringX(playText, window.getWidth(), g, playTextOffsetX), RenderUtil.centerStringY(playText, window.getHeight(), g, 0));
        
        g.setFont(versionFont);
        g.drawString("Version " + Config.GAME_VERSION, 8, window.getHeight() - 8);
        
        String creditText = "By Magnus & Jarand";
        g.drawString(creditText, getGameWindow().getWidth()-Util.getStringWidth(creditText, g)-8, window.getHeight() - 8);
        
        String controllerStatus = "Controller is " + (window.getControllers() == null ? "disconnected" : (window.getControllers().getState(0).isConnected ? "connceted" : "disconnected"));
        g.drawString(controllerStatus, RenderUtil.centerStringX(controllerStatus, window.getWidth(), g, 0), window.getHeight() - 8);
        
        g.setColor(highscoreBtnColor);
        g.fillRoundRect(highscoreBtn.getX(), highscoreBtn.getY(), highscoreBtn.getWidth(), highscoreBtn.getHeight(), 8, 8);
        
        if(Util.isWithinArea(input.getMouseX(), input.getMouseY(), highscoreBtn)) {
            g.setColor(highscoreBtnTextColor.brighter());
            g.setFont(highscoreBtnFont.deriveFont(Font.BOLD, highscoreBtnFont.getSize2D()+1));
        } else {
            g.setColor(highscoreBtnTextColor);
            g.setFont(highscoreBtnFont);
        }
        g.drawString(highscoreBtnText, RenderUtil.centerStringX(highscoreBtnText, highscoreBtn.getWidth(), g, highscoreBtn.getX()), RenderUtil.centerStringY(highscoreBtnText, highscoreBtn.getHeight(), g, highscoreBtn.getY()));
    }

    @Override
    public GameWindow getGameWindow() {
        return window;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
