package slug.invaders.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import slug.invaders.Config;
import slug.invaders.Main;
import slug.invaders.assets.ImageCache;
import slug.invaders.assets.sounds.SoundLib;
import slug.invaders.screens.PlayScreen;
import slug.invaders.util.Debug;
import slug.invaders.util.Hitbox;
import slug.invaders.util.Log;
import slug.invaders.util.Util;

public class Slug {
    private Lane lane;
    private final SlugWave wave;
    private Hitbox hitbox;
    //private int y = 0;
    //private int x = 0;
    private long moveInterval = 17; // Lower interval means faster slug.
    private long lastMove = System.currentTimeMillis();
    private boolean stealingPlant = false;
    private SlugState state = SlugState.DOWN;
    private boolean retreating = false;
    private Lane targetLane = null;
    private boolean aimingForHuman = false;
    private int health = 1;
    private int moveDelay = 0; // The slug will not move as long as this is above 0. Counts down 1 every move tick.
    
    public Slug(SlugWave wave, Lane lane, long moveInterval, int health, boolean humanAim) {
        this.wave = wave;
        this.lane = lane;
        this.moveInterval = moveInterval;
        this.health = health;
        this.aimingForHuman = humanAim;
    }
    
    public void spawn() {
        hitbox = new Hitbox(0, -getImage().getHeight(), getImage().getWidth(), getImage().getHeight());
        hitbox.setX(lane.getX() + Util.centerValue(getImage().getWidth(), lane.getWidth()));
        wave.getSlugs().add(this);
    }
    
    public void damage(int amount) {
        health -= amount;
        
        if(health < 1) {
            kill();
            return;
        }
        hitbox.setY(hitbox.getY() - 10);
        moveDelay = 5;
        SoundLib.SLUG_HITMARK.play();
        Log.info("[Slug] Slug damaged. (Damage: " + amount + ")");
    }
    
    public void kill() {
        Log.info("[Slug] Slug killed.");
        SoundLib.SLUG_DEATH.play(Config.SLUG_DEATH_VOLUME);
        wave.getSlugs().remove(this);
        wave.countKill();
        Player.increaseKillcount(1);
        
        if(isStealing()) {
            setStealingPlant(false);
            lane.deattachPlant();
        }
        new BloodPool(this).spawn();
    }
    
    public int getX() {
        return hitbox.getX();
    }
    
    public int getY() {
        return hitbox.getY();
    }
    
    public Hitbox getHitbox() {
        return hitbox;
    }
    
    public SlugState getState() {
        return state;
    }
    
    public void setState(SlugState state) {
        this.state = state;
    }
    
    public void addMoveDelay(int delay) {
        this.moveDelay += delay;
    }
    
    public void setMoveDelay(int delay) {
        this.moveDelay = delay;
    }
    
    public BufferedImage getImage() {
        switch(state) {
            case DOWN: return ImageCache.SLUG_1_DOWN;
            case UP: return ImageCache.SLUG_1_UP;
            case LEFT: return ImageCache.SLUG_1_LEFT;
            case RIGHT: return ImageCache.SLUG_1_RIGHT;
        }
        return null;
    }
    
    public void move(Player player) {
        if(moveDelay > 0) {
            moveDelay--;
            return;
        }
        
        // Checks to see if the salt wave is active and if it is, it will kill this slug.
        if(player.getPlayScreen().getSaltWave() != null) {
            if(player.getPlayScreen().getSaltWave().getHitbox().overlaps(hitbox)) {
                kill();
                return;
            }
        }
        
        // If the slug is aiming for the human, execute the following AI behaviour..
        if(PlayScreen.isAimingForHuman() || aimingForHuman) {
            if(Util.isWithinArea(getX(), getY(), player.getHitbox())) {
                // todo damage?? animation?
                player.kill();
                return;
            } else if(Util.isWithinArea(hitbox.getRightX(), getY(), player.getHitbox())) {
                // todo damage?? animation
                player.kill();
                return;
            }
            int targetY = Config.GAME_HEIGHT - player.getImage().getHeight() + 40;
            
            if(getY() >= targetY) {
                int targetX = player.getX() + (player.getHitbox().getWidth() / 2);
                
                if(targetX > getX()) {
                    // Move right
                    setState(SlugState.RIGHT);
                    int newX = hitbox.getX() + 2;
                    hitbox.setX(newX);
                } else if(targetX < getX()) {
                    // Move left
                    setState(SlugState.LEFT);
                    int newX = hitbox.getX() - 2;
                    hitbox.setX(newX);
                }
            } else {
                setState(SlugState.DOWN);
                int newY = getY() + 2;
                
                if(newY >= targetY) {
                    newY = targetY;
                }
                hitbox.setY(newY);
            }
            return;
        }
        
        
        if(retreating) {
            hitbox.setY(hitbox.getY() - 2);
            
            if(getY() <= -getHitbox().getHeight()) {
                wave.getSlugs().remove(this);
                wave.countEscapee();
            }
            return;
        }
        
        if(targetLane != null) {
            if(targetLane.hasThief() || !targetLane.hasPlant()) {
                targetLane = findNewLane(false);
                
                if(targetLane.getX() == lane.getX()) {
                    targetLane = null;
                }
                return;
            }
            int laneX = targetLane.getX();
            
            if(laneX > getX()) {
                // Move right
                setState(SlugState.RIGHT);
                int newX = hitbox.getX() + 2;
                
                if(newX >= laneX) {
                    newX = laneX;
                    lane = targetLane;
                    targetLane = null;
                    setState(SlugState.DOWN);
                }
                hitbox.setX(newX);
            } else if(laneX < getX()) {
                // Move left
                setState(SlugState.LEFT);
                int newX = hitbox.getX() - 2;
                
                if(newX <= laneX) {
                    newX = laneX;
                    lane = targetLane;
                    targetLane = null;
                    setState(SlugState.DOWN);
                }
                hitbox.setX(newX);
            } else {
                Log.error("[Slug] Well something went wrong here.. slug brain error");
            }
            return;
        }
        
        if(isStealing()) {
            hitbox.setY(getY() - 2);
            lane.getPlant().setY(hitbox.getY() + 10);
            
            if(getY() <= -getHitbox().getHeight()) {
                lane.removePlant();
                wave.getSlugs().remove(this);
                wave.countEscapee();
            }
            return;
        } else if(lane.hasThief() || !lane.hasPlant()) {
            Lane newLane = findNewLane(true);
            
            if(lane.getX() == newLane.getX()) {
                newLane = findNewLane(false);
                
                if(lane.getX() == newLane.getX()) {
                    retreating = true;
                    setState(SlugState.UP);
                    Log.info("[Slug] A slug couldn't find a lane, retreating ..");
                    return;
                } else {
                    targetLane = newLane;
                }
            } else {
                targetLane = newLane;
            }
        }
        hitbox.setY(getY() + 2);
        
        boolean plantFound = false;
        for(Lane l : Lane.getLanes()) {
            if(l.hasPlant()) {
                plantFound = true;
            }
        }
        if(!plantFound) {
            PlayScreen.startAimingForHuman();
            return;
        }
        
        if(!lane.hasThief()) {
            if(lane.hasPlant()) {
                if(hitbox.getBottomY() >= lane.getPlant().getHitbox().getBottomY()) {
                    setState(SlugState.UP);
                    lane.attachPlant(this);
                    return;
                }
            }
        }
        
        if(getHitbox().getY() >= Config.GAME_HEIGHT) {
            wave.getSlugs().remove(this);
            wave.countEscapee();
        }
    }
    
    public void draw(Graphics2D g, Player player) {
        if(System.currentTimeMillis()-lastMove >= moveInterval) {
            move(player);
            lastMove = System.currentTimeMillis();
        }
        g.drawImage(getImage(), getX(), getY(), null);
        
        if(Debug.ENABLED) {
            g.setColor(Color.blue);
            g.drawRect(getX(), getY(), hitbox.getWidth(), hitbox.getHeight());
        }
    }
    
    /**
     * Find a new lane for this slug.
     * 
     * @param strict - Strict lane searching means that the slug can only find a lane that has a plant AND does not have a thief in it already.
     * @return 
     */
    public Lane findNewLane(boolean strict) {
        List<Lane> potentials = new ArrayList<>();
        
        for(Lane l : Lane.getLanes()) {
            if(strict) {
                if(l.hasPlant() && !l.hasThief()) {
                    potentials.add(l);
                }
            } else {
                if(l.hasPlant()) {
                    potentials.add(l);
                }
            }
        }
        
        if(!potentials.isEmpty()) {
            return potentials.get(Main.rand.nextInt(potentials.size()));
        }
        return lane;
    }
    
    public void setStealingPlant(boolean stealing) {
        this.stealingPlant = stealing;
    }
    
    public boolean isStealing() {
        return stealingPlant;
    }
    
    public enum SlugState {
        DOWN, UP, LEFT, RIGHT;
    }
}
