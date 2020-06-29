package slug.invaders.data;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import slug.invaders.Config;
import slug.invaders.Main;
import slug.invaders.assets.ImageCache;
import slug.invaders.util.Log;
import slug.invaders.util.Util;

public class SaltBullet {
    private BufferedImage bullet;
    private final Player player;
    private int x = 0;
    private int y = 0;
    private int speed = 40;
    private final BulletDirection direction;
    private int flashFrames = 0;
    private final boolean isWeak;
    
    public SaltBullet(Player player, BulletDirection direction, boolean isWeak) {
        this.player = player;
        this.direction = direction;
        this.isWeak = isWeak;
        this.speed = isWeak ? 20 : 40;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getY() {
        return y;
    }
    
    public void setX(int x) {
        this.x = x;              
    }
    
    public int getX() {
        return x;
    }
    
    public void init() {
        switch(direction) {
            case UP: {
                this.bullet = ImageCache.BULLET_VERTICAL;
                this.x = player.getX() + Util.centerValue(bullet.getWidth(), player.getImage().getWidth());
                this.y = player.getY() - bullet.getHeight();
                break;
            }
            
            case LEFT: {
                this.bullet = ImageCache.BULLET_HORIZONTAL;
                this.x = player.getX() - bullet.getWidth();
                this.y = player.getY() + Util.centerValue(bullet.getHeight(), player.getImage().getHeight());
                break;
            }
            
            case RIGHT: {
                this.bullet = ImageCache.BULLET_HORIZONTAL;
                this.x = player.getHitbox().getRightX();
                this.y = player.getY() + Util.centerValue(bullet.getHeight(), player.getImage().getHeight());
                break;
            }
        }
    }
    
    public void move() {
        switch(direction) {
            case UP: {
                setY(getY() - speed);
                
                if(y+bullet.getHeight() < 0) {
                    player.getBullets().remove(this);
                }
                break;
            }
            
            case LEFT: {
                setX(getX() - speed);
                
                if(x + bullet.getWidth() < 0) {
                    player.getBullets().remove(this);
                }
                break;
            }
            
            case RIGHT: {
                setX(getX() + speed);
                
                if(x > Config.GAME_WIDTH) {
                    player.getBullets().remove(this);
                }
                break;
            }
        }
        
        for(SaltBomb bomb : player.getPlayScreen().getBombs()) {
            if(Util.isWithinArea(x, y, bomb.getHitbox())) {
                bomb.damage();
                player.getBullets().remove(this);
                return;
            }
        }
        
        if(WaveManager.hasActiveWave()) {
            for(Slug s : WaveManager.getCurrent().getSlugs()) {
                if(Util.isWithinArea(x, y, s.getHitbox())) {
                    // Test if this salt is weak, if it is, only give it a 50% chance of being effective.
                    if(isWeak) {
                        if(Main.rand.nextInt(100) <= 50) {
                            s.damage(1);
                        }
                    } else {
                        s.damage(1);
                    }
                    player.getBullets().remove(this);
                }
            }
        }
        
        if(player.getPlayScreen().getWavePerk() != null) {
            WavePerk wavePerk = player.getPlayScreen().getWavePerk();
            
            if(Util.isWithinArea(x, y, wavePerk.getHitbox())) {
                player.getBullets().remove(this);
                player.getPlayScreen().grantWavePerk();
            }
        }
    }
    
    public void draw(Graphics2D g) {
        g.drawImage(bullet, x, y, null);
        
        if(flashFrames < 5) {
            flashFrames++;
            
            switch(direction) {
                case UP: {
                    int x = player.getX() + Util.centerValue(ImageCache.FLASH.getWidth(), player.getHitbox().getWidth());
                    g.drawImage(ImageCache.FLASH, x, player.getY() - ImageCache.FLASH.getWidth() / 2, null);
                    break;
                }
                
                case LEFT: {
                    int y = player.getY() + Util.centerValue(ImageCache.FLASH.getHeight(), player.getHitbox().getHeight());
                    g.drawImage(ImageCache.FLASH, player.getX() - ImageCache.FLASH.getWidth() / 2, y, null);
                    break;
                }
                
                case RIGHT: {
                    int y = player.getY() + Util.centerValue(ImageCache.FLASH.getHeight(), player.getHitbox().getHeight());
                    g.drawImage(ImageCache.FLASH, player.getHitbox().getRightX(), y, null);
                    break;
                }
            }
        }
    }
    
    public BufferedImage getBulletImage() {
        return bullet;
    }
    
    public BulletDirection getDirection() {
        return direction;
    }
    
    public enum BulletDirection {
        UP, LEFT, RIGHT;
    }
}
