package slug.invaders.compute;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import slug.invaders.Config;
import slug.invaders.assets.ImageCache;
import slug.invaders.screens.PlayScreen;
import slug.invaders.util.Hitbox;

public class WavePerk {
    private final PlayScreen screen;
    private final Hitbox hitbox;
    private final int direction;
    private final int speed;
    private BufferedImage image;
    private final int originalSize;
    private int size;
    private boolean expanding = true;
    private static Font font = new Font("Comic Sans MS", Font.BOLD, 32);
    
    public WavePerk(PlayScreen screen, int y, int direction, int speed) {
        this.direction = direction;
        this.image = direction == 0 ? ImageCache.WAVE_ICON_RIGHT : ImageCache.WAVE_ICON_LEFT;
        originalSize = image.getHeight();
        size = originalSize;
        int x = direction == 0 ? -image.getWidth() : Config.GAME_WIDTH;
        this.hitbox = new Hitbox(x, y, image.getWidth(), image.getHeight());
        this.speed = speed;
        this.screen = screen;
    }
    
    public void move() {
        if(direction == 0) {
            // Towards right
            int x = hitbox.getX();
            x += speed;
            
            if(x >= Config.GAME_WIDTH) {
                screen.despawnWavePerk();
            }
            hitbox.setX(x);
        } else {
            // Towards left
            int x = hitbox.getX();
            x -= speed;
            
            if(x <= 0) {
                screen.despawnWavePerk();
            }
            hitbox.setX(x);
        }
    }
    
    public void draw(Graphics2D g) {
        move();
        g.drawImage(image, hitbox.getX(), hitbox.getY(), null);
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("+", hitbox.getX() + 5, hitbox.getBottomY() - 5);
    }
    
    public Hitbox getHitbox() {
        return hitbox;
    }
}
