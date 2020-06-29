/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slug.invaders.assets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import slug.invaders.Main;

/**
 *
 * @author Måknuzzz
 */
public enum Image {
    ICON                ("assets/icon.png"),
    PLAYER_DEFAULT      ("assets/player_default.png"),
    PLAYER_RIGHT        ("assets/player_right.png"),
    PLAYER_LEFT         ("assets/player_left.png"),
    BACKGROUND          ("assets/background.png"),
    SLUG_1_DOWN         ("assets/slug_1_down.png"),
    SLUG_1_UP           ("assets/slug_1_up.png"),
    SLUG_1_LEFT         ("assets/slug_1_left.png"),
    SLUG_1_RIGHT        ("assets/slug_1_right.png"),
    PLANT_1             ("assets/plants/plant1.png"),
    PLANT_2             ("assets/plants/plant2.png"),
    PLANT_3             ("assets/plants/plant3.png"),
    PLANT_4             ("assets/plants/plant4.png"),
    PLANT_5             ("assets/plants/plant5.png"),
    PLANT_6             ("assets/plants/plant6.png"),
    PLANT_7             ("assets/plants/plant7.png"),
    BLOOD_POOL          ("assets/blood_pool.png"),
    BLOOD_POOL_FLIP     ("assets/blood_pool_flip.png"),
    BLOOD_POOL_UPSIDE   ("assets/blood_pool_upside.png"),
    BLOOD_POOL_HORIZ    ("assets/blood_pool_horiz.png"),
    SALT_BUCKET         ("assets/salt_bucket.png"),
    FLASH               ("assets/flash.png"),
    BULLET_HORIZONTAL   ("assets/bullet_horiz.png"),
    BULLET_VERTICAL     ("assets/bullet_verti.png"),
    WAVE_ICON_RIGHT     ("assets/wave_icon_right.png"),
    WAVE_ICON_LEFT      ("assets/wave_icon_left.png"),
    WAVE                ("assets/wave.png"),
    TITLE_LOGO          ("assets/title_logo.png"),
    SALT_BOMB_1         ("assets/saltbomb/state1.png"),
    SALT_BOMB_2         ("assets/saltbomb/state2.png"),
    SALT_BOMB_3         ("assets/saltbomb/state3.png"),
    SALT_BOMB_4         ("assets/saltbomb/state4.png"),
    SALT_BOMB_DEBRIS    ("assets/saltbomb/debris.png");
    
    private final String path;
    
    private Image(String path) {
        this.path = path;
    }
    
    public BufferedImage getBufferedImage() throws IOException {
        return ImageIO.read(Main.class.getResourceAsStream(path));
    }
    
    public java.awt.Image getImage() {
        return new ImageIcon(Main.class.getResource(path)).getImage();
    }
    
    public Icon getIcon() {
        return new ImageIcon(getImage());
    }
}
