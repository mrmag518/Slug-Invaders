package slug.invaders.data;

import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;
import slug.invaders.Config;
import slug.invaders.Main;
import slug.invaders.assets.ImageCache;
import slug.invaders.assets.sounds.SoundLib;
import slug.invaders.screens.PlayScreen;
import slug.invaders.util.Hitbox;
import slug.invaders.util.Log;
import slug.invaders.util.Util;

public class SaltBucket {
    private final Hitbox hitbox;
    private final int y = Config.GAME_HEIGHT - 80;
    private final PlayScreen screen;
    private final int slugAmountCache;
    private boolean despawned = false;
    
    public SaltBucket(PlayScreen screen, int x, int slugAmount) {
        this.screen = screen;
        this.hitbox = new Hitbox(x, y, ImageCache.SALT_BUCKET.getWidth(), ImageCache.SALT_BUCKET.getHeight());
        this.slugAmountCache = slugAmount;
    }
    
    public void spawn(long uptime) {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if(screen.getBucket() != null && !despawned) {
                    Log.info("[SaltBucket] Bucked timed out, despawning ..");
                    despawn();
                }
            }
        }, uptime);
    }
    
    public void despawn() {
        despawned = true;
        screen.forceRemoveBucket();
    }
    
    public void render(Graphics2D g) {
        /*if(!WaveManager.hasActiveWave()) {
            return;
        }*/
        g.drawImage(ImageCache.SALT_BUCKET, hitbox.getX(), y, null);
        
        if(Util.isWithinArea(hitbox.getX() + hitbox.getWidth() / 2, hitbox.getY() + hitbox.getHeight() / 2, screen.getPlayer().getHitbox())) {
            Log.info("[SaltBucket] Player interacted with a bucket! Adding Strong Salt ammo ..");
            
            SoundLib.PICKUP_SALTBUCKET.play();
            int ammo = Main.rand.ints(15, 20).findAny().getAsInt() + slugAmountCache;
            
            if(WaveManager.hasActiveWave()) {
                ammo *= WaveManager.getCurrent().getSlugHealth();
            }
            Log.info("[SaltBucket] Adding " + ammo + " amount of ammo.");
            screen.getPlayer().addStrongSaltAmmo(ammo);
            screen.displayAmmoReceived(hitbox.getX(), ammo);
            despawn();
        }
    }
}
