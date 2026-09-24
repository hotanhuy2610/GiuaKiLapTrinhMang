package ui;

import javax.swing.*;
import java.awt.*;

public class TypingIndicator extends JPanel {

    private final JLabel label = new JLabel("");
    private final Timer animationTimer;
    private final Timer safetyTimer;
    private int dotCount = 1;
    private String username = "";

    public TypingIndicator() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0));
        setOpaque(false);

        label.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        label.setForeground(ThemeManager.getTextSecondary());

        // Custom animated 3 dots icon
        JPanel dotsIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getAccent());

                int r = 3;
                int y = getHeight() / 2;
                for (int i = 0; i < 3; i++) {
                    int x = 4 + i * 9;
                    if (i == (dotCount - 1)) {
                        g2.fillOval(x - r - 1, y - r - 1, (r + 1) * 2, (r + 1) * 2);
                    } else {
                        g2.fillOval(x - r, y - r, r * 2, r * 2);
                    }
                }
                g2.dispose();
            }
        };
        dotsIcon.setOpaque(false);
        dotsIcon.setPreferredSize(new Dimension(30, 18));

        add(dotsIcon);
        add(label);
        setVisible(false);

        ThemeManager.addThemeChangeListener(this::applyTheme);

        animationTimer = new Timer(350, e -> {
            dotCount = (dotCount % 3) + 1;
            updateText();
            dotsIcon.repaint();
        });

        safetyTimer = new Timer(4000, e -> hideTyping());
        safetyTimer.setRepeats(false);
    }

    public void showTyping(String username) {
        this.username = username;
        dotCount = 1;
        updateText();
        setVisible(true);

        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
        safetyTimer.restart();
    }

    public void hideTyping() {
        animationTimer.stop();
        safetyTimer.stop();
        label.setText("");
        setVisible(false);
    }

    private void updateText() {
        label.setText(username + " đang soạn tin...");
    }

    private void applyTheme() {
        label.setForeground(ThemeManager.getTextSecondary());
        repaint();
    }
}