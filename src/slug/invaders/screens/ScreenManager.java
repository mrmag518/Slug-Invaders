package slug.invaders.screens;

import java.awt.Graphics2D;
import slug.invaders.GameWindow;
import slug.invaders.util.Log;

public class ScreenManager {
    /**
     * The current active screen that is being managed.
     */
    protected static Screen currentScreen = null;
    
    /**
     * An instance of the GameWindow class.
     */
    protected static GameWindow gameWindow = null;
    
    /**
     * Used to determine whether the current screen has been properly loaded or not.
     * Render & update calls will be blocked if this is false.
     */
    protected static boolean loaded = false;
    
    /**
     * The latest FPS report.
     * Should be updated once a second.
     */
    private static int fps = -1;
    
    /**
     * Defines the GameWindow instance.
     * @param window 
     */
    public static void setGameWindow(GameWindow window) {
        gameWindow = window;
    }
    
    /**
     * Set the current active screen.
     * Will handle switching between screens automatically.
     * @param screen 
     */
    public static void setScreen(Screen screen) {
        Log.info("[ScreenManager] Switching screens .. (" + (currentScreen != null ? currentScreen.toString() + " -> " + screen.toString().concat(")") : "null -> " + screen.toString().concat(")")));
        long start = System.currentTimeMillis();
        loaded = false;
        
        if(currentScreen != null) {
            Log.info("[ScreenManager] Disposing old screen ..");
        }
        currentScreen = screen;
        currentScreen.create(gameWindow);
        //gameWindow.registerInputHandlers(screen.getKeyAdapter(), screen.getMouseAdapter(), screen.getMouseWheelListener());
        loaded = true;
        
        Log.info("[ScreenManager] Screens switched! Took " + (System.currentTimeMillis()-start) + "ms");
    }
    
    /**
     * Returns the current active screen.
     * @return 
     */
    public static Screen getCurrentScreen() {
        return currentScreen;
    }
    
    /**
     * Request a screen to update logic.
     * @param dt 
     */
    public static void update(double dt) {
        if(currentScreen != null) {
            if(isLoaded()) {
                getCurrentScreen().update(dt);
            }
        } else {
            Log.error("[ScreenManager] Attempted to update an undefined screen. (null)");
        }
    }
    
    /**
     * Request a screen to render itself.
     * @param g 
     */
    public static void render(Graphics2D g) {
        if(currentScreen != null) {
            if(isLoaded()) {
                getCurrentScreen().render(g);
            }
        } else {
            Log.error("[ScreenManager] Attempted to render an undefined screen. (null)");
        }
    }
    
    /**
     * Returns the latest FPS information.
     * @return 
     */
    public static int getFPS() {
        return fps;
    }
    
    /**
     * Report the latest FPS information.
     * @param fps 
     */
    public static void reportFPS(int fps) {
        ScreenManager.fps = fps;
    }
    
    /**
     * Returns whether or not the current active screen has been loaded or not.
     * @return 
     */
    public static boolean isLoaded() {
        return loaded;
    }
}
