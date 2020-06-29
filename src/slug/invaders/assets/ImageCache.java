package slug.invaders.assets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import slug.invaders.util.Log;

public class ImageCache {
    public static BufferedImage SLUG_1_DOWN         = null;
    public static BufferedImage SLUG_1_UP           = null;
    public static BufferedImage SLUG_1_LEFT         = null;
    public static BufferedImage SLUG_1_RIGHT        = null;
    public static BufferedImage BLOOD_POOL          = null;
    public static BufferedImage BLOOD_POOL_FLIP     = null;
    public static BufferedImage BLOOD_POOL_UPSIDE   = null;
    public static BufferedImage BLOOD_POOL_HORIZ    = null;
    public static BufferedImage SALT_BUCKET         = null;
    public static BufferedImage FLASH               = null;
    public static BufferedImage BACKGROUND          = null;
    public static BufferedImage BULLET_HORIZONTAL   = null;
    public static BufferedImage BULLET_VERTICAL     = null;
    public static BufferedImage PLANT_1             = null;
    public static BufferedImage PLANT_2             = null;
    public static BufferedImage PLANT_3             = null;
    public static BufferedImage PLANT_4             = null;
    public static BufferedImage PLANT_5             = null;
    public static BufferedImage PLANT_6             = null;
    public static BufferedImage PLANT_7             = null;
    public static BufferedImage WAVE_ICON_RIGHT     = null;
    public static BufferedImage WAVE_ICON_LEFT      = null;
    public static BufferedImage WAVE                = null;
    public static BufferedImage TITLE_LOGO          = null;
    public static BufferedImage SALT_BOMB_1         = null;
    public static BufferedImage SALT_BOMB_2         = null;
    public static BufferedImage SALT_BOMB_3         = null;
    public static BufferedImage SALT_BOMB_4         = null;
    public static BufferedImage SALT_BOMB_DEBRIS    = null;
    
    public static void load() throws IOException {
        Log.info("[ImageCache] Loading and caching image data ..");
        long start = System.currentTimeMillis();
        
        SLUG_1_DOWN         = Image.SLUG_1_DOWN.getBufferedImage();
        SLUG_1_UP           = Image.SLUG_1_UP.getBufferedImage();
        SLUG_1_LEFT         = Image.SLUG_1_LEFT.getBufferedImage();
        SLUG_1_RIGHT        = Image.SLUG_1_RIGHT.getBufferedImage();
        BLOOD_POOL          = Image.BLOOD_POOL.getBufferedImage();
        BLOOD_POOL_FLIP     = Image.BLOOD_POOL_FLIP.getBufferedImage();
        BLOOD_POOL_UPSIDE   = Image.BLOOD_POOL_UPSIDE.getBufferedImage();
        BLOOD_POOL_HORIZ    = Image.BLOOD_POOL_HORIZ.getBufferedImage();
        SALT_BUCKET         = Image.SALT_BUCKET.getBufferedImage();
        FLASH               = Image.FLASH.getBufferedImage();
        BACKGROUND          = Image.BACKGROUND.getBufferedImage();
        BULLET_HORIZONTAL   = Image.BULLET_HORIZONTAL.getBufferedImage();
        BULLET_VERTICAL     = Image.BULLET_VERTICAL.getBufferedImage();
        PLANT_1             = Image.PLANT_1.getBufferedImage();
        PLANT_2             = Image.PLANT_2.getBufferedImage();
        PLANT_3             = Image.PLANT_3.getBufferedImage();
        PLANT_4             = Image.PLANT_4.getBufferedImage();
        PLANT_5             = Image.PLANT_5.getBufferedImage();
        PLANT_6             = Image.PLANT_6.getBufferedImage();
        PLANT_7             = Image.PLANT_7.getBufferedImage();
        WAVE_ICON_RIGHT     = Image.WAVE_ICON_RIGHT.getBufferedImage();
        WAVE_ICON_LEFT      = Image.WAVE_ICON_LEFT.getBufferedImage();
        WAVE                = Image.WAVE.getBufferedImage();
        TITLE_LOGO          = Image.TITLE_LOGO.getBufferedImage();
        SALT_BOMB_1         = Image.SALT_BOMB_1.getBufferedImage();
        SALT_BOMB_2         = Image.SALT_BOMB_2.getBufferedImage();
        SALT_BOMB_3         = Image.SALT_BOMB_3.getBufferedImage();
        SALT_BOMB_4         = Image.SALT_BOMB_4.getBufferedImage();
        SALT_BOMB_DEBRIS    = Image.SALT_BOMB_DEBRIS.getBufferedImage();
        
        Log.info("[ImageCache] Finished. Took " + (System.currentTimeMillis()-start) + "ms");
    }
}
