package slug.invaders.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import slug.invaders.Config;
import slug.invaders.GameWindow;
import slug.invaders.assets.ImageCache;
import slug.invaders.util.Hitbox;
import slug.invaders.util.Log;
import slug.invaders.util.Loop;
import slug.invaders.util.RenderUtil;
import slug.invaders.util.Util;

public class GameOverScreen implements Screen {
    private GameWindow window;
    public final Font gameOverFont = new Font("Comic Sans MS", Font.BOLD, 92);
    public final Font gameOverInfoFont = new Font("Comic Sans MS", Font.BOLD, 32);
    public final String gameOverText = "GAME OVER!";
    public String gameOverInfoText = "You survived until wave %w with a playtime of %p minutes";
    private float alpha = 0f;
    private Color grayScreen = new Color(0.14f, 0.14f, 0.14f, alpha);
    private Hitbox playAgainBtn = new Hitbox(0, 0, 0, 0);
    private String playAgainBtnText = "PLAY AGAIN";
    private Font playAgainBtnFont = new Font("Comic Sans MS", Font.BOLD, 32);
    private MouseAdapter adapter = null;
    
    @Override
    public void create(GameWindow window) {
        this.window = window;
        playAgainBtn.setHeight(65);
        playAgainBtn.setWidth(320);
        playAgainBtn.setX(Util.centerValue(playAgainBtn.getWidth(), window.getWidth()));
        playAgainBtn.setY(window.getHeight() - 170);
        
        adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(Util.isWithinArea(e.getX(), e.getY(), playAgainBtn)) {
                    Log.info("[GameOverScreen] Attempting to restart application ..");
                    try {
                        Util.restartApplication();
                    } catch (URISyntaxException | IOException ex) {
                        Log.error("[GameOverScreen] Failed to restart application!");
                        ex.printStackTrace();
                    }
                }
            }
        };
        window.getPanel().addMouseListener(adapter);
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(ImageCache.BACKGROUND, 0, 0, null);
        g.setColor(grayScreen);
        g.fillRect(0, 0, window.getWidth(), window.getHeight());
        
        g.setColor(Color.red);
        g.setFont(gameOverFont);
        g.drawString(gameOverText, RenderUtil.centerStringX(gameOverText, Config.GAME_WIDTH, g, 0), RenderUtil.centerStringY(gameOverText, Config.GAME_HEIGHT, g, -70));
        g.setFont(gameOverInfoFont);
        g.drawString(gameOverInfoText, RenderUtil.centerStringX(gameOverInfoText, Config.GAME_WIDTH, g, 0), RenderUtil.centerStringY(gameOverInfoText, Config.GAME_HEIGHT, g, 40));
        
        g.setColor(Color.gray);
        g.fillRect(playAgainBtn.getX(), playAgainBtn.getY(), playAgainBtn.getWidth(), playAgainBtn.getHeight());
        g.setFont(playAgainBtnFont);
        g.setColor(Color.cyan);
        g.drawString(playAgainBtnText, RenderUtil.centerStringX(playAgainBtnText, playAgainBtn.getWidth(), g, playAgainBtn.getX()), RenderUtil.centerStringY(playAgainBtnText, playAgainBtn.getHeight(), g, playAgainBtn.getY()));
    }

    @Override
    public GameWindow getGameWindow() {
        return window;
    }
    
    public void onGameOver(int wave, long playtimeMS) {
        Loop.scheduleStop("BUCKET_SPAWNER");
        Loop.scheduleStop("bomb_spawner");
        Loop.scheduleStop("wp_spawner");
        Loop.scheduleStop("SLUG_SPAWNER");
        
        double playtimeMinutes = (playtimeMS / 1000.0) / 60.0;
        
        gameOverInfoText = gameOverInfoText.replace("%w", String.valueOf(wave));
        gameOverInfoText = gameOverInfoText.replace("%p", Util.cleanDouble(playtimeMinutes, 2));
        
        Loop l = new Loop(() -> {
            alpha += 0.06f;
            
            if(alpha >= 0.6f) {
                alpha = 0.6f;
                Loop.scheduleStop("GRAY_SCREEN_LOOP");
            }
            grayScreen = new Color(0.19f, 0.19f, 0.19f, alpha);
        }, 16, 16, "GRAY_SCREEN_LOOP");
        l.start();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
