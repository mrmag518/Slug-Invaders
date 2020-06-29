package slug.invaders;

import java.awt.BasicStroke;
import java.awt.Color;

public class Config {
    public static final String GAME_VERSION = "1.5";
    public static final int GAME_WIDTH = 1280;
    public static final int GAME_HEIGHT = 720;
    public static final int SLUG_BASE_SPAWN_AMOUNT = 5;
    public static final int BLOOD_POOL_TIMER = 3000;
    public static final long BUCKET_UPTIME = 4000;
    public static final Color AMMO_RECEIVED_COLOR = Color.yellow;
    public static final Color WAVE_BAR_COLOR = Color.yellow.brighter();
    public static final Color INFO_BAR_COLOR = new Color((float)250/255, (float)250/255, (float)250/255, 0.9f);
    public static final BasicStroke LASER_SIGHT_STROKE = new BasicStroke(3);
    public static final Color LASER_SIGHT_COLOR = new Color(0, 255, 0);
    public static final float GUNSHOT_VOLUME = 0.25f;
    public static final float WEAK_GUNSHOT_VOLUME = 0.25f;
    public static final float SLUG_DEATH_VOLUME = 0.2f;
    public static final Color OVERLAY_COLOR = new Color(200, 200, 200, 153);
}
