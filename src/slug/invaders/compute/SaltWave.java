package slug.invaders.compute;

import java.awt.Graphics2D;
import slug.invaders.Config;
import slug.invaders.assets.ImageCache;
import slug.invaders.screens.PlayScreen;
import slug.invaders.util.Hitbox;

public class SaltWave {
    private final Hitbox hitbox;
    private final PlayScreen screen;
    
    public SaltWave(PlayScreen screen) {
        this.hitbox = new Hitbox(-ImageCache.WAVE.getWidth(), 0, ImageCache.WAVE.getWidth(), ImageCache.WAVE.getHeight());
        this.screen = screen;
    }
    
    public void move() {
        int x = hitbox.getX();
        x += 10;
        
        if(x >= Config.GAME_WIDTH) {
            screen.despawnWave();
        }
        hitbox.setX(x);
    }
    
    public void draw(Graphics2D g) {
        move();
        g.drawImage(ImageCache.WAVE, hitbox.getX(), hitbox.getY(), null);
    }
    
    public Hitbox getHitbox() {
        return hitbox;
    }
}
