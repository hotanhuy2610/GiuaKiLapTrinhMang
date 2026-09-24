package ui;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    public enum ThemeMode {
        DARK, LIGHT, AUTO_TIME, SYSTEM
    }

    private static ThemeMode currentMode = ThemeMode.DARK;
    private static final List<Runnable> themeChangeListeners = new ArrayList<>();

    // Colors - Dark Mode (Messenger Dark)
    public static class Dark {
        public static final Color APP_BG = new Color(24, 25, 26);         // #18191A
        public static final Color SIDEBAR_BG = new Color(36, 37, 38);     // #242526
        public static final Color HEADER_BG = new Color(36, 37, 38);      // #242526
        public static final Color CHAT_BG = new Color(24, 25, 26);        // #18191A
        public static final Color INPUT_BG = new Color(58, 59, 60);       // #3A3B3C
        public static final Color HOVER_BG = new Color(58, 59, 60);       // #3A3B3C
        public static final Color ACTIVE_BG = new Color(45, 55, 72);      // #2D3748
        public static final Color BORDER = new Color(62, 64, 66);         // #3E4042
        
        public static final Color TEXT_PRIMARY = new Color(228, 230, 235); // #E4E6EB
        public static final Color TEXT_SECONDARY = new Color(176, 179, 184); // #B0B3B8
        public static final Color TEXT_MUTED = new Color(144, 149, 157);    // #90959D
        
        public static final Color MY_BUBBLE = new Color(0, 132, 255);      // #0084FF
        public static final Color OTHER_BUBBLE = new Color(58, 59, 60);    // #3A3B3C
        public static final Color ACCENT = new Color(0, 132, 255);        // #0084FF
        public static final Color ONLINE_GREEN = new Color(49, 162, 76);  // #31A24C
    }

    // Colors - Light Mode (Messenger Light)
    public static class Light {
        public static final Color APP_BG = new Color(240, 242, 245);      // #F0F2F5
        public static final Color SIDEBAR_BG = new Color(255, 255, 255);  // #FFFFFF
        public static final Color HEADER_BG = new Color(255, 255, 255);   // #FFFFFF
        public static final Color CHAT_BG = new Color(255, 255, 255);     // #FFFFFF
        public static final Color INPUT_BG = new Color(240, 242, 245);    // #F0F2F5
        public static final Color HOVER_BG = new Color(228, 230, 235);    // #E4E6EB
        public static final Color ACTIVE_BG = new Color(231, 243, 255);   // #E7F3FF
        public static final Color BORDER = new Color(228, 230, 235);      // #E4E6EB
        
        public static final Color TEXT_PRIMARY = new Color(5, 5, 5);       // #050505
        public static final Color TEXT_SECONDARY = new Color(101, 103, 107); // #65676B
        public static final Color TEXT_MUTED = new Color(140, 144, 150);   // #8C9096
        
        public static final Color MY_BUBBLE = new Color(0, 132, 255);      // #0084FF
        public static final Color OTHER_BUBBLE = new Color(240, 242, 245); // #F0F2F5
        public static final Color ACCENT = new Color(0, 132, 255);        // #0084FF
        public static final Color ONLINE_GREEN = new Color(49, 162, 76);  // #31A24C
    }

    public static ThemeMode getThemeMode() {
        return currentMode;
    }

    public static void setThemeMode(ThemeMode mode) {
        currentMode = mode;
        notifyListeners();
    }

    public static boolean isDark() {
        if (currentMode == ThemeMode.AUTO_TIME) {
            int hour = LocalTime.now().getHour();
            // 6:00 to 18:00 = Light mode; 18:00 to 6:00 = Dark mode
            return (hour < 6 || hour >= 18);
        }
        if (currentMode == ThemeMode.SYSTEM) {
            return true;
        }
        return currentMode == ThemeMode.DARK;
    }

    public static Color getAppBg() { return isDark() ? Dark.APP_BG : Light.APP_BG; }
    public static Color getSidebarBg() { return isDark() ? Dark.SIDEBAR_BG : Light.SIDEBAR_BG; }
    public static Color getHeaderBg() { return isDark() ? Dark.HEADER_BG : Light.HEADER_BG; }
    public static Color getChatBg() { return isDark() ? Dark.CHAT_BG : Light.CHAT_BG; }
    public static Color getInputBg() { return isDark() ? Dark.INPUT_BG : Light.INPUT_BG; }
    public static Color getHoverBg() { return isDark() ? Dark.HOVER_BG : Light.HOVER_BG; }
    public static Color getActiveBg() { return isDark() ? Dark.ACTIVE_BG : Light.ACTIVE_BG; }
    public static Color getBorderColor() { return isDark() ? Dark.BORDER : Light.BORDER; }
    
    public static Color getTextPrimary() { return isDark() ? Dark.TEXT_PRIMARY : Light.TEXT_PRIMARY; }
    public static Color getTextSecondary() { return isDark() ? Dark.TEXT_SECONDARY : Light.TEXT_SECONDARY; }
    public static Color getTextMuted() { return isDark() ? Dark.TEXT_MUTED : Light.TEXT_MUTED; }
    
    public static Color getMyBubble() { return isDark() ? Dark.MY_BUBBLE : Light.MY_BUBBLE; }
    public static Color getOtherBubble() { return isDark() ? Dark.OTHER_BUBBLE : Light.OTHER_BUBBLE; }
    public static Color getAccent() { return isDark() ? Dark.ACCENT : Light.ACCENT; }
    public static Color getOnlineGreen() { return isDark() ? Dark.ONLINE_GREEN : Light.ONLINE_GREEN; }

    public static void addThemeChangeListener(Runnable listener) {
        themeChangeListeners.add(listener);
    }

    private static void notifyListeners() {
        for (Runnable listener : themeChangeListeners) {
            try {
                listener.run();
            } catch (Exception ignored) {}
        }
    }
}
