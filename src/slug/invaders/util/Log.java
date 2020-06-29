package slug.invaders.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/y HH:mm:ss");
    
    public static void info(String output) {
        String formatted = "[" + DATE_FORMAT.format(new Date()) + "] " + output;
        System.out.println(formatted);
    }
    
    public static void error(String output) {
        String formatted = "[" + DATE_FORMAT.format(new Date()) + "] " + output;
        System.err.println(formatted);
    }
    
    public static SimpleDateFormat getDateFormat() {
        return DATE_FORMAT;
    }
}
