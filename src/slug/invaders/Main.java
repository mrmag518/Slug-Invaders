package slug.invaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import javax.swing.SwingUtilities;
import slug.invaders.util.Log;
import slug.invaders.util.Util;

public class Main {
    public static final Random rand = new Random(System.nanoTime());
    public static final File installDir = new File(System.getProperty("user.home") + File.separator + "Documents", "Slug Invaders");
    public static HashMap<String, String> highscores = new HashMap<>();
    public static Properties highscoreData = new Properties();
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Log.info("Slug Invaders loading ..");
        
        Util.applyDesign();
        
        if(!installDir.exists()) {
            Log.info("Install directory not found! Creating .. (" + installDir.getPath() + ")");
            installDir.mkdir();
        }
        File highscoreFile = new File(installDir, "highscores.properties");
        
        if(!highscoreFile.exists()) {
            Log.info("Couldn't find highscores.properties! Creating a new one ..");
            
            try {
                highscoreFile.createNewFile();
            } catch (IOException ex) {
                Log.error("Error creating file " + highscoreFile.getPath() + "! Shutting down ..");
                ex.printStackTrace();
                System.exit(0);
                return;
            }
        }
        
        try {
            Log.info("Loading properties from " + highscoreFile.getPath() + " ..");
            highscoreData.load(new FileInputStream(highscoreFile));
        } catch (IOException ex) {
            Log.error("Failed to load properties from highscores.properties!");
            ex.printStackTrace();
        }
        
        Log.info("Reading highscore data from file ..");
        long startRead = System.currentTimeMillis();
        int i = 0;
        for(String key : highscoreData.stringPropertyNames()) {
            String value = highscoreData.getProperty(key);
            highscores.put(key, value);
            Log.info("- Loaded: " + key + "(" + value + ")"); i++;
        }
        Log.info("Read and injected " + i + " values. Took " + (System.currentTimeMillis()-startRead) + "ms");
        
        SwingUtilities.invokeLater(() -> {
            Log.info("Running game ..");
            new GameWindow().run();
            Log.info("Slug Invaders finished loading. Took " + (System.currentTimeMillis()-start) + "ms");
        });
    }
    
    public static void saveHighscores() {
        Log.info("Saving highscores ..");
        highscoreData.clear();
        highscoreData.putAll(highscores);
        try {
            highscoreData.store(new FileOutputStream(new File(installDir, "highscores.properties")), null);
        } catch (FileNotFoundException ex) {
            Log.error("Failed to save highscores!");
            ex.printStackTrace();
        } catch (IOException ex) {
            Log.error("Failed to save highscores!");
            ex.printStackTrace();
        }
        Log.info("Highscores saved!");
    }
}
