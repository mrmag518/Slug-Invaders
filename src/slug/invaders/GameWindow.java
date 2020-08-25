package slug.invaders;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import kuusisto.tinysound.TinySound;
import slug.invaders.assets.ImageCache;
import slug.invaders.compute.WaveManager;
import slug.invaders.screens.MenuScreen;
import slug.invaders.screens.PlayScreen;
import slug.invaders.screens.ScreenManager;
import slug.invaders.util.Debug;
import slug.invaders.util.Input;
import slug.invaders.util.Log;

public class GameWindow {
    private final JFrame frame;
    private final GamePanel panel;
    private final Input input;
    public static GameWindow instance;
    private ControllerManager controllers;
    
    public GameWindow() {
        this.frame = new JFrame("Slug Invaders");
        this.panel = new GamePanel();
        this.input = new Input(panel);
    }
    
    public void run() {
        Log.info("[GameWindow] Initializing sound lib .. ");
        long startSoundLib = System.currentTimeMillis();
        TinySound.init();
        Log.info("[GameWindow] Sound lib initialized. (TinySound by Finn Kuusisto) - Took " + (System.currentTimeMillis()-startSoundLib) + "ms");
        GameWindow.instance = this;
        //Util.setMainIcon(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupPanel();
        frame.add(panel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        try {
            ImageCache.load();
        } catch (IOException ex) {
            Log.error("[GameWindow] Failed to cache image data. Shutting down ..");
            ex.printStackTrace();
            System.exit(0);
        }
        
        long startCM = System.currentTimeMillis();
        controllers = new ControllerManager();
        controllers.initSDLGamepad();
        Log.info("[GameWindow] Loaded " + controllers.getNumControllers() + " controller(s). Took " + (System.currentTimeMillis()-startCM) + "ms");
        
        ScreenManager.setGameWindow(this);
        ScreenManager.setScreen(new MenuScreen());
        panel.startGameLoop();
        loadGlobalInputHandlers();
        
        frame.setVisible(true);
        Log.info("[GameWindow] Window should now be visible and ready.");
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Log.info("Game exiting ..");
                TinySound.shutdown();
            }
        });
    }
    
    private void setupPanel() {
        panel.setPreferredSize(new Dimension(Config.GAME_WIDTH, Config.GAME_HEIGHT));
        panel.setFocusable(true);
        panel.setBackground(Color.gray);
    }
    
    private void loadGlobalInputHandlers() {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_F3 || e.getKeyCode() == KeyEvent.VK_F) {
                    Debug.ENABLED = !Debug.ENABLED;
                }
                input.registerKey(e.getKeyCode());
                
                // sketchy
                if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                    if(ScreenManager.getCurrentScreen() instanceof PlayScreen) {
                        PlayScreen screen = (PlayScreen)ScreenManager.getCurrentScreen();
                        boolean forceDirectionUP = e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W;
                        screen.requestSalt(forceDirectionUP);
                    }
                }
                
                if(ScreenManager.getCurrentScreen() instanceof PlayScreen) {
                    if(e.getKeyCode() == KeyEvent.VK_K) {
                        if(e.isAltDown()) {
                            PlayScreen screen = (PlayScreen)ScreenManager.getCurrentScreen();
                            screen.getPlayer().kill();
                        }
                    }
                }
                
                /**
                 * Debug / cheats
                 */
                if(e.isControlDown() && e.isControlDown()) {
                    if(ScreenManager.getCurrentScreen() instanceof PlayScreen) {
                        PlayScreen screen = (PlayScreen)ScreenManager.getCurrentScreen();
                        
                        if(e.getKeyCode() == KeyEvent.VK_INSERT) {
                            WaveManager.setWaveNumber(9000);
                            Log.info("[GameWindow] 9000 AND 1 KÆÆÆZ ACTIVATED");
                        } else if(e.getKeyCode() == KeyEvent.VK_W) {
                            screen.spawnWavePerk();
                        } else if(e.getKeyCode() == KeyEvent.VK_V) {
                            screen.grantWavePerk();
                        } else if(e.getKeyCode() == KeyEvent.VK_PLUS) {
                            screen.getPlayer().addStrongSaltAmmo(20);
                        } else if(e.getKeyCode() == KeyEvent.VK_MINUS) {
                            screen.getPlayer().removeStrongSaltAmmo(20);
                        } else if(e.getKeyCode() == KeyEvent.VK_B) {
                            screen.spawnBomb();
                        } else if(e.getKeyCode() == KeyEvent.VK_S) {
                            Log.info("[GameWindow] Salt bucket spawn cheat activated. Attempting to spawn bucket ..");
                            screen.createBucket(false);
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                input.unregisterKey(e.getKeyCode());
                
                // sketchy
                if(ScreenManager.getCurrentScreen() instanceof PlayScreen) {
                    PlayScreen screen = (PlayScreen)ScreenManager.getCurrentScreen();
                    screen.canShoot = true;
                }
            }
        });
    }
    
    public Input getInput() {
        return input;
    }
    
    public int getFPS() {
        return ScreenManager.getFPS();
    }
    
    public int getWidth() {
        return panel.getWidth();
    }
    
    public int getHeight() {
        return panel.getHeight();
    }
    
    public GamePanel getPanel() {
        return panel;
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    public ControllerManager getControllers() {
        return controllers;
    }
    
   public ControllerState getFirstController() {
       return controllers.getState(0);
   }
}
