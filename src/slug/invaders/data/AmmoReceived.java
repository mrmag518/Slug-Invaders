package slug.invaders.data;

import java.awt.Font;
import java.awt.Graphics2D;
import slug.invaders.Config;
import slug.invaders.screens.PlayScreen;
import slug.invaders.util.Loop;

public class AmmoReceived {
    private static final Font font = new Font("Comic Sans MS", Font.BOLD, 24);
    private String text = "error";
    private final long ID = System.nanoTime();
    private int y = 0;
    private final int x;
    
    public AmmoReceived(int x, int amount) {
        this.x = x;
        this.text = "+" + amount;
    }
    
    public void render(Graphics2D g) {
        g.setFont(font);
        g.setColor(Config.AMMO_RECEIVED_COLOR);
        g.drawString(text, x, y);
    }
    
    public void startAnimation(final PlayScreen screen) {
        y = Config.GAME_HEIGHT - 80;
        
        Loop loop = new Loop(() -> {
            y += 2;
            
            if(y <= 0) {
                dispose(screen);
                Loop.scheduleStop("AMMO_REC_" + ID);
            }
        }, 16, 16, "AMMO_REC_" + ID);
        loop.start();
    }
    
    public void dispose(PlayScreen screen) {
        screen.getAmmoReceivedList().remove(this);
    }
}
