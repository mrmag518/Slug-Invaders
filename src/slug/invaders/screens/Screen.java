package slug.invaders.screens;

import java.awt.Graphics2D;
import slug.invaders.GameWindow;

public interface Screen {
    /**
     * Called when this screen is initialized and created.
     * @param window
     */
    public void create(GameWindow window);
    
    /**
     * Called whenever this screen is updated.
     * Update calls are always made before render calls.
     * @param dt 
     */
    public void update(double dt);
    
    /**
     * Called whenever this screen is rendered.
     * @param g 
     */
    public void render(Graphics2D g);
    
    /**
     * Returns the GameWindow object.
     * @return 
     */
    public GameWindow getGameWindow();

    @Override
    public String toString();
}
