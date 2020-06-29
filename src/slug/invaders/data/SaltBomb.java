package slug.invaders.data;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import slug.invaders.Main;
import slug.invaders.assets.ImageCache;
import slug.invaders.util.Hitbox;
import slug.invaders.util.Log;

public class SaltBomb {
    private SaltBombState state = SaltBombState.STATE_1;
    private final int x;
    private int y = 0;
    private int speed = 5;
    private Hitbox hitbox;
    
    public SaltBomb(int x) {
        this.x = x;
        this.y = -ImageCache.SALT_BOMB_1.getHeight();
        this.speed = Main.rand.ints(1, 3).findAny().getAsInt();
        this.hitbox = new Hitbox(x, y, ImageCache.SALT_BOMB_1.getWidth(), ImageCache.SALT_BOMB_1.getHeight());
    }
    
    public void damage() {
        switch(state) {
            case STATE_1: setState(SaltBombState.STATE_2); break;
            case STATE_2: setState(SaltBombState.STATE_3); break;
            case STATE_3: setState(SaltBombState.STATE_4); break;
            case STATE_4: explode(); break;
        }
        Log.info("[SaltBomb] Salt damaged. New state: " + state.name());
    }
    
    public void explode() {
        Log.info("[SaltBomb] Exploding ..");
        setState(SaltBombState.EXPLODING);
        
    }
    
    public void setState(SaltBombState state) {
        this.state = state;
    }
    
    public void move() {
        if(state == SaltBombState.EXPLODING) {
            Log.info("BOMB EXPLODING");
            return;
        }
        y += speed;
    }
    
    public void draw(Graphics2D g) {
        if(getImage() != null) {
            g.drawImage(getImage(), x, y, null);
        }
    }
    
    public SaltBombState getState() {
        return state;
    }
    
    public Hitbox getHitbox() {
        return hitbox;
    }
    
    public BufferedImage getImage() {
        switch(state) {
            case STATE_1: return ImageCache.SALT_BOMB_1;
            case STATE_2: return ImageCache.SALT_BOMB_2;
            case STATE_3: return ImageCache.SALT_BOMB_3;
            case STATE_4: return ImageCache.SALT_BOMB_4;
            default: return null;
        }
    }
    
    public enum SaltBombState {
        STATE_1, STATE_2, STATE_3, STATE_4, EXPLODING;
    }
}
