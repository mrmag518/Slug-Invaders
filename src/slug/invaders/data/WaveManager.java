package slug.invaders.data;

import slug.invaders.util.Log;

public class WaveManager {
    private static SlugWave currentWave = null;
    private static int waveNumber = 0;
    
    public static SlugWave getCurrent() {
        return currentWave;
    }
    
    public static void finishWave() {
        currentWave = null;
    }
    
    public static boolean hasActiveWave() {
        return currentWave != null;
    }
    
    public static void spawnNew() {
        waveNumber++;
        SlugWave wave = new SlugWave(waveNumber);
        currentWave = wave;
        wave.spawn();
        Log.info("[WaveManager] Spawned new wave. Wave: " + waveNumber);
    }
    
    public static int getWaveNumber() {
        return waveNumber;
    }
    
    public static void setWaveNumber(int number) {
        waveNumber = number;
    }
}
