package slug.invaders.data;

import java.awt.image.BufferedImage;
import slug.invaders.screens.PlayScreen;
import slug.invaders.util.Hitbox;

public class Plant {
    private final int playerToPlantGap = 10;
    private final Hitbox hitbox;
    
    public Plant(PlayScreen screen, int x, BufferedImage image) {
        this.hitbox = new Hitbox(x, screen.getPlayer().getY() - image.getHeight() - playerToPlantGap, image.getWidth(), image.getHeight());
    }
    
    public int getY() {
        return hitbox.getY();
    }
    
    public int getX() {
        return hitbox.getX();
    }
    
    public void setY(int y) {
        hitbox.setY(y);
    }
    
    public void setX(int x) {
        hitbox.setX(x);
    }
    
    public Hitbox getHitbox() {
        return hitbox;
    }
}
