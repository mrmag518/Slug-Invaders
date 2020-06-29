package slug.invaders.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import slug.invaders.GameWindow;
import slug.invaders.assets.ImageCache;
import slug.invaders.assets.sounds.SoundLib;
import slug.invaders.util.Hitbox;
import slug.invaders.util.Log;
import slug.invaders.util.RenderUtil;
import slug.invaders.util.Util;

public class PlayerScreen implements Screen {
    private GameWindow window;
    private long creationTime = 0;
    private final Color overlayColor = new Color(200, 200, 200, 153);
    
    private final String screenTitle = "Choose your nickname";
    private final Color screenTitleColor = new Color((float)255/255, (float)230/255, (float)0);
    private final Font screenFont = new Font("Comic Sans MS", Font.BOLD, 62);
    
    private final Font nameFont = new Font("Comic Sans MS", Font.BOLD, 30);
    private JTextField nameField = null;
    private final Hitbox nameButton = new Hitbox(0, 0, 0, 0);
    private final String nameButtonText = "PLAY";
    
    private final Font nameButtonFont = new Font("Comic Sans MS", Font.BOLD, 24);
    private MouseAdapter mouseAdapter = null;
    private KeyAdapter keyAdapter = null;
    private String pmessage = "this text should not display (error!)";
    private Color pmessageColor = new Color(200, 0, 0, 255);
    private long pmessageTime = 0;
    private Font pmessageFont = new Font("Comic Sans MS", Font.BOLD, 32);
    private long pmessageLastAlphaChange = 0;
    
    @Override
    public void create(GameWindow window) {
        this.window = window;
        //nameY = RenderUtil.centerStringY(nameText, window.getHeight(), window.getPanel().getGraphics(), 0);
        nameField = new JTextField();
        //nameField.setBounds(535, nameY-32, 220, 40);
        nameField.setBounds(Util.centerValue(300, window.getWidth()), Util.centerValue(40, window.getHeight()), 300, 50);
        nameField.setFont(nameFont);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        window.getPanel().add(nameField);
        
        nameButton.setX(nameField.getX());
        nameButton.setHeight(nameField.getHeight());
        nameButton.setY(nameField.getY() + nameField.getHeight() + 10);
        nameButton.setWidth(nameField.getWidth());
        
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(Util.isWithinArea(e.getX(), e.getY(), nameButton)) {
                    playButtonClicked();
                }
            }
        };
        window.getPanel().addMouseListener(mouseAdapter);
        
        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    playButtonClicked();
                } else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    SoundLib.MENU_SWING.play(1.0, -0.7);
                    disposeDisposables();
                    MenuScreen screen = new MenuScreen();
                    ScreenManager.setScreen(screen);
                }
            }
        };
        window.getPanel().addKeyListener(keyAdapter);
        nameField.addKeyListener(keyAdapter);
        
        nameField.requestFocus();
        
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public void update(double dt) {
        if(window.getFirstController().b || window.getFirstController().back) {
            if(System.currentTimeMillis()-creationTime >= 1000) {
                SoundLib.MENU_SWING.play(1.0, -0.7);
                disposeDisposables();
                MenuScreen screen = new MenuScreen();
                ScreenManager.setScreen(screen);
                return;
            }
        }
        
        if(window.getFirstController().a || window.getFirstController().start) {
            if(System.currentTimeMillis()-creationTime >= 1000) {
                playButtonClicked();
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(ImageCache.BACKGROUND, 0, 0, null);
        g.setColor(overlayColor);
        g.fillRect(0, 0, window.getWidth(), window.getHeight());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        /**
         * Screen title
         */
        g.setFont(screenFont);
        g.setColor(screenTitleColor);
        g.drawString(screenTitle, RenderUtil.centerStringX(screenTitle, window.getWidth(), g, 0), 200);
        
        //g.setFont(nameFont);
        //g.setColor(Color.white);
        //g.drawString(nameText, 300, nameY);
        
        g.setColor(Color.gray);
        g.fillRect(nameButton.getX(), nameButton.getY(), nameButton.getWidth(), nameButton.getHeight());
        g.setColor(Color.white);
        g.setFont(nameButtonFont);
        g.drawString(nameButtonText, RenderUtil.centerStringX(nameButtonText, nameButton.getWidth(), g, nameButton.getX()), RenderUtil.centerStringY(nameButtonText, nameButton.getHeight(), g, nameButton.getY()));
        
        if(System.currentTimeMillis()-pmessageLastAlphaChange >= 15) {
            if(System.currentTimeMillis()-pmessageTime > 500) {
                int alpha = pmessageColor.getAlpha();

                alpha -= 2;

                if(alpha < 1) {
                    alpha = 0;
                }
                pmessageColor = new Color(pmessageColor.getRed(), 0, 0, alpha);
                pmessageLastAlphaChange = System.currentTimeMillis();
            }
        }
        
        if(System.currentTimeMillis()-pmessageTime < 3000) {
            g.setColor(pmessageColor);
            g.setFont(pmessageFont);
            g.drawString(pmessage, RenderUtil.centerStringX(pmessage, window.getWidth(), g, 0), nameField.getY() + 150);
        }
    }

    @Override
    public GameWindow getGameWindow() {
        return window;
    }
    
    public void playButtonClicked() {
        String name = nameField.getText();
        
        if(name.isEmpty() || name.replaceAll(" ", "").isEmpty()) {
            displayMessage("Please enter a nickname");
            return;
        }
        
        if(name.length() < 3) {
            displayMessage("Nickname too short. Min length: 3");
            return;
        }
        
        if(name.length() > 16) {
            displayMessage("Nickname too long. Max length: 16");
            return;
        }
        
        if(name.contains(" ")) {
            displayMessage("Nickname cannot contain spaces");
            return;
        }
        
        if(name.equalsIgnoreCase("Unknown")) {
            displayMessage("Your nickname cannot be 'Unknown'");
            return;
        }
        
        for(char c : name.toCharArray()) {
            if(!Character.isLetterOrDigit(c)) {
                displayMessage("You may only use letters and digits");
                return;
            }
        }
        Log.info("[PlayerScreen] Presetting name: " + name + " ..");
        SoundLib.PLAYERSCREEN_HIT.play(0.7f);
        disposeDisposables();
        PlayScreen screen = new PlayScreen();
        screen.presetPlayerName(name);
        ScreenManager.setScreen(screen);
    }
    
    public void displayMessage(String text) {
        pmessage = text;
        pmessageTime = System.currentTimeMillis();
        pmessageColor = new Color(pmessageColor.getRed(), 0, 0, 255);
    }
    
    public void disposeDisposables() {
        window.getPanel().removeMouseListener(mouseAdapter);
        window.getPanel().removeKeyListener(keyAdapter);
        window.getPanel().remove(nameField);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
