package slug.invaders.compute;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import slug.invaders.GameWindow;
import slug.invaders.Main;
import slug.invaders.assets.ImageCache;
import slug.invaders.assets.sounds.SoundLib;
import slug.invaders.screens.PlayScreen;
import slug.invaders.util.Debug;
import slug.invaders.util.Log;

public class Lane {
    private static final List<Lane> lanes = new ArrayList<>();
    private BufferedImage plantImage = null;
    private int x = 0;
    private Plant plant = null;
    private boolean plantAlive = true;
    private boolean drawPlant = true;
    private boolean plantBeingStolen = false;
    private Slug thief = null;
    
    public Lane(PlayScreen screen, int x, Plant plant, BufferedImage image) {
        this.x = x;
        this.plant = plant;
        this.plantImage = image;
    }
    
    public static void loadLanes(PlayScreen screen) {
        int laneAmount = GameWindow.instance.getWidth() / ImageCache.PLANT_1.getWidth();
        int x = 0;
        
        for(int i = 0; i < laneAmount; i++) {
            int r = Main.rand.nextInt(6);
            BufferedImage bi = null;
        
            switch(r) {
                case 0: bi = ImageCache.PLANT_1; break;
                case 1: bi = ImageCache.PLANT_2; break;
                case 2: bi = ImageCache.PLANT_3; break;
                case 3: bi = ImageCache.PLANT_4; break;
                case 4: {
                    if(Main.rand.nextInt(100) < 20) {
                        bi = ImageCache.PLANT_5;
                    } else {
                        if(Main.rand.nextBoolean()) {
                            bi = ImageCache.PLANT_1;
                        } else {
                            bi = ImageCache.PLANT_6;
                        }
                    }
                    break;
                }
                case 5: bi = ImageCache.PLANT_6; break;
                case 6: bi = ImageCache.PLANT_7; break;
            }
            lanes.add(new Lane(screen, x, new Plant(screen, x, bi), bi));
            x += bi.getWidth();
        }
    }
    
    public void draw(Graphics2D g) {
        if(drawPlant) {
            g.drawImage(plantImage, x, plant.getY(), null);
        }
        
        if(Debug.ENABLED) {
            g.setColor(Color.white);
            g.drawString("P: " + plantAlive, x + 5, 20);
            g.drawString("Thief: " + (getThief() == null ? "no" : "yes"), x + 5, 35);
            
            g.setColor(Debug.RED_W_TRANSPARENCY);
            g.drawLine(x, 0, x, GameWindow.instance.getHeight());
        }
    }
    
    public void attachPlant(Slug slug) {
        Log.info("[Lane] Attaching plant");
        SoundLib.SLUG_PICKUP_PLANT.play(0.7f);
        setThief(slug);
    }
    
    public void deattachPlant() {
        Log.info("[Lane] Deattaching plant");
        setThief(null);
    }
    
    public void setThief(Slug slug) {
        if(slug != null) slug.setStealingPlant(true);
        this.thief = slug;
    }
    
    public Slug getThief() {
        return thief;
    }
    
    public boolean hasThief() {
        return thief != null;
    }
    
    public void removePlant() {
        drawPlant = false;
        plantAlive = false;
        setThief(null);
        
        int plantAmount = 0;
        for(Lane l : Lane.getLanes()) {
            if(l.hasPlant()) {
                plantAmount++;
            }
        }
        
        if(plantAmount <= 0) {
            PlayScreen.startAimingForHuman();
        } else if(plantAmount < 5) {
            if(!SlugWave.aimRandomlyForHuman()) {
                SlugWave.setRandomHumanAims(true);
            }
        }
    }
    
    public boolean hasPlant() {
        return plantAlive;
    }
    
    public Plant getPlant() {
        return plant;
    }
    
    public int getX() {
        return x;
    }
    
    public int getWidth() {
        return plantImage.getWidth();
    }
    
    public static List<Lane> getLanes() {
        return lanes;
    }
}
