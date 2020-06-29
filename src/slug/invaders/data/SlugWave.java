package slug.invaders.data;

import java.awt.Graphics2D;
import java.util.concurrent.CopyOnWriteArrayList;
import slug.invaders.Main;
import slug.invaders.screens.PlayScreen;
import slug.invaders.screens.ScreenManager;
import slug.invaders.util.Log;
import slug.invaders.util.Loop;

public class SlugWave {
    private static boolean randomHumanAims = false;
    private final CopyOnWriteArrayList<Slug> slugs = new CopyOnWriteArrayList<>();
    private final int waveNumber;
    private int spawnCounter = 0;
    private int slugAmount = 0;
    private int slugsKilled = 0;
    private int slugsEscaped = 0;
    
    public SlugWave(int waveNumber) {
        this.waveNumber = waveNumber;
    }
    
    /**
     * Start spawning the slugs.
     */
    public void spawn() {
        int amount = waveNumber * 2 + 5;
        slugAmount = amount;
        
        long spawnInterval = 1000 - ((waveNumber * 10) - 1);
        
        if(spawnInterval < 100) {
            spawnInterval = 100;
        }
        
        Loop l = new Loop(() -> {
            long moveInterval = 32 - (waveNumber + 1 / 2);
            
            if(moveInterval < 10) {
                moveInterval = 10;
            }
            
            // Define health.
            int health = getSlugHealth();
            Log.info("[SlugWave] Spawning slug " + (spawnCounter+1) + " of " + amount + ", move interval: " + moveInterval + "ms, health: " + health);
            
            // Find and define a random lane.
            Lane lane = findLane();
            
            // Combats camping in one lane.
            boolean humanAim = false;
            if(aimRandomlyForHuman()) {
                if(Main.rand.nextInt(100) < 50) {
                    humanAim = true;
                }
            }

            // Create slug and assign it to the lane.
            Slug slug = new Slug(this, lane, moveInterval, health, humanAim);
            slug.spawn();
            spawnCounter++;

            if(spawnCounter >= amount) {
                Log.info("[SlugWave] Stopping spawn task. Reached the spawn amount. (" + amount + ")");
                Loop.scheduleStop("SLUG_SPAWNER");
            }
        }, 250, spawnInterval, "SLUG_SPAWNER");
        l.start();
        
        Log.info("[SlugWave] " + amount + " slugs will spawn at an interval of " + spawnInterval + "ms");
        
        startWaveChecker();
    }
    
    public Lane findLane() {
        return Lane.getLanes().get(Main.rand.nextInt(Lane.getLanes().size()));
    }
    
    public void draw(Graphics2D g, Player player) {
        for(Slug s : slugs) {
            s.draw(g, player);
        }
    }
    
    public int getWaveNumber() {
        return waveNumber;
    }
    
    public CopyOnWriteArrayList<Slug> getSlugs() {
        return slugs;
    }
    
    public int getSlugAmount() {
        return slugAmount;
    }
    
    public int getSlugsKilled() {
        return slugsKilled;
    }
    
    public int getSlugsEscaped() {
        return slugsEscaped;
    }
    
    public int getSlugsLeft() {
        return slugAmount - (getSlugsEscaped() + getSlugsKilled());
    }
    
    public void countKill() {
        slugsKilled++;
    }
    
    public void countEscapee() {
        Log.info("[SlugWave] Slug escaped.");
        slugsEscaped++;
    }
    
    public void startWaveChecker() {
        Log.info("[SlugWave] Wave Checker loop started.");
        
        Loop l = new Loop(() -> {
            if(slugsKilled+slugsEscaped >= slugAmount) {
                onWavePassed();
                Loop.scheduleStop("WAVE_CHECKER");
            }
        }, 500, 500, "WAVE_CHECKER");
        l.start();
    }
    
    public void onWavePassed() {
        Log.info("[SlugWave] Wave " + WaveManager.getWaveNumber() + " passed!");
        getSlugs().clear();
        WaveManager.finishWave();
        
        PlayScreen screen = (PlayScreen)ScreenManager.getCurrentScreen();
        screen.startNextWaveTimer();
    }
    
    public static void setRandomHumanAims(boolean enabled) {
        Log.info("[SlugWave] Random human aims set to: " + enabled);
        SlugWave.randomHumanAims = enabled;
    }
    
    public static boolean aimRandomlyForHuman() {
        return SlugWave.randomHumanAims;
    }
    
    public int getSlugHealth() {
        /*int health = 1 + waveNumber / 10;
        
        if(health > 4) {
            health = 4;
        }*/
        int health = 1;
        
        if(waveNumber >= 10 && waveNumber < 20) {
            health = 2;
        } else if(waveNumber >= 20 && waveNumber < 30) {
            health = 3;
        } else if(waveNumber > 30) {
            health = 4;
        }
        return health;
    }
}
