package slug.invaders.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

public class Input {
    private final JPanel panel;
    private int last_x = -1;
    private int last_y = -1;
    private final Set<Integer> keydata = new HashSet<>();
    
    public Input(JPanel panel) {
        this.panel = panel;
    }
    
    public int getMouseX() {
        try {
            last_x = panel.getMousePosition().x;
        } catch(Exception ex) {}
        return last_x;
    }
    
    public int getMouseY() {
        try {
            last_y = panel.getMousePosition().y;
        } catch(Exception ex) {}
        return last_y;
    }
    
    public boolean isMouseInWindow() {
        return panel.getMousePosition() != null;
    }
    
    public boolean isAnyKeyPressed() {
        return !keydata.isEmpty();
    }
    
    public boolean isKeyPressed(int key) {
        return keydata.contains(key);
    }
    
    public void registerKey(int key) {
        keydata.add(key);
    }
    
    public void unregisterKey(int key) {
        keydata.remove(key);
    }
    
    @Override
    public String toString() {
        return "x: " + getMouseX() + ", y: " + getMouseY() + ", mouseInFrame: " + isMouseInWindow();
    }
}
