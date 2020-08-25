package slug.invaders.compute;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import org.imgscalr.Scalr;
import slug.invaders.Main;
import slug.invaders.assets.ImageCache;
import slug.invaders.util.Debug;
import slug.invaders.util.Hitbox;
import slug.invaders.util.Log;
import slug.invaders.util.Loop;
import slug.invaders.util.Util;

public class BloodPool {
    private static final CopyOnWriteArrayList<BloodPool> pools = new CopyOnWriteArrayList<>();
    private final Slug slug;
    private final Hitbox hitbox;
    private long spawnTime = 0;
    private BufferedImage originalImage = getRandomImage();
    private BufferedImage image = null;
    private final int maxSize = ImageCache.BLOOD_POOL.getWidth();
    private int size = 0;
    
    public BloodPool(Slug slug) {
        this.slug = slug;
        
        if(slug.getState() == Slug.SlugState.LEFT || slug.getState() == Slug.SlugState.RIGHT) {
            originalImage = ImageCache.BLOOD_POOL_HORIZ;
        }
        this.image = originalImage;
        
        int x = slug.getX() + Util.centerValue(slug.getHitbox().getWidth(), originalImage.getWidth());
        int y = slug.getHitbox().getBottomY() - originalImage.getHeight();
        this.hitbox = new Hitbox(x, y, maxSize, originalImage.getHeight());
        
        this.size = maxSize / 10;
        this.image = Scalr.resize(originalImage, size, (BufferedImageOp)null);
    }
    
    public void spawn() {
        
        
        spawnTime = System.currentTimeMillis();
        pools.add(this);
        startExpansion();
    }
    
    private void startExpansion() {
        Loop l = new Loop(() -> {
            size += 8;
            
            if(size >= maxSize) {
                size = maxSize;
                Loop.scheduleStop("EXP_" + spawnTime);
                
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startRetraction();
                    }
                }, 3000);
            }
            image = Scalr.resize(originalImage, size, (BufferedImageOp)null);
        }, 16, 16, "EXP_" + spawnTime);
        l.start();
    }
    
    private void startRetraction() {
        Loop l = new Loop(() -> {
            size--;
            
            if(size <= maxSize / 10) {
                size = maxSize / 10;
                Loop.scheduleStop("EXP_" + spawnTime);
                pools.remove(this);
            }
            image = Scalr.resize(originalImage, size, (BufferedImageOp)null);
        }, 16, 16, "EXP_" + spawnTime);
        l.start();
    }
    
    public void draw(Graphics2D g) {
        g.drawImage(image, hitbox.getX() + Util.centerValue(image.getWidth(), hitbox.getWidth()), hitbox.getY() + Util.centerValue(image.getHeight(), hitbox.getHeight()), null);
        
        if(Debug.ENABLED) {
            g.setColor(Color.blue);
            g.drawRect(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
        }
        
        /*if(System.currentTimeMillis()-spawnTime >= Config.BLOOD_POOL_TIMER) {
            
            pools.remove(this);
        }*/
    }
    
    public static CopyOnWriteArrayList<BloodPool> getPools() {
        return pools;
    }
    
    public static BufferedImage getRandomImage() {
        int i = Main.rand.nextInt(3);
        
        Log.info("" + i);
        
        switch(i) {
            case 0: return ImageCache.BLOOD_POOL;
            case 1: return ImageCache.BLOOD_POOL_FLIP;
            case 2: return ImageCache.BLOOD_POOL_UPSIDE;
            default: return ImageCache.BLOOD_POOL;
        }
    }
}
