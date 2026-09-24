package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginDialog {

    private String username;
    private String serverIP;

    public boolean showDialog(Component parent) {
        JDialog dialog = new JDialog((Frame) null, "Messenger - Đăng nhập", true);
        dialog.setSize(450, 490);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getAppBg());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout(0, 15));
        content.setBorder(new EmptyBorder(28, 32, 24, 32));

        // Header Panel with Messenger Badge Icon & Typography
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);

        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Messenger Gradient Circle
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 198, 255), getWidth(), getHeight(), new Color(0, 114, 255));
                g2.setPaint(gp);
                g2.fillOval(0, 0, 48, 48);

                // Chat bubble inside icon
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(12, 13, 24, 17, 8, 8);
                int[] px = {14, 21, 17};
                int[] py = {27, 27, 33};
                g2.fillPolygon(px, py, 3);

                // Lightning accent inside chat bubble
                g2.setColor(new Color(0, 114, 255));
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(20, 17, 26, 21);
                g2.drawLine(26, 21, 22, 22);
                g2.drawLine(22, 22, 27, 26);

                g2.dispose();
            }
        };
        logoPanel.setPreferredSize(new Dimension(48, 48));
        logoPanel.setOpaque(false);

        JPanel textHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        textHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Chào mừng tới Messenger");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(ThemeManager.getTextPrimary());

        JLabel lblSub = new JLabel("Nhập thông tin để bắt đầu trò chuyện");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(ThemeManager.getTextSecondary());

        textHeader.add(lblTitle);
        textHeader.add(lblSub);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(textHeader, BorderLayout.CENTER);
        content.add(headerPanel, BorderLayout.NORTH);

        // Form Fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // 1. Username field
        formPanel.add(createLabel("Tên người dùng"));
        formPanel.add(Box.createVerticalStrut(6));
        JTextField txtUsername = createStyledTextField("Nhập tên của bạn...");
        txtUsername.setText("Huy");
        formPanel.add(txtUsername);

        formPanel.add(Box.createVerticalStrut(14));

        // 2. Server IP field
        formPanel.add(createLabel("Địa chỉ IP Server"));
        formPanel.add(Box.createVerticalStrut(6));
        JTextField txtIP = createStyledTextField("127.0.0.1");
        txtIP.setText("127.0.0.1");
        formPanel.add(txtIP);

        formPanel.add(Box.createVerticalStrut(14));

        // 3. Theme selector (Clean labels without broken emoji font boxes)
        formPanel.add(createLabel("Giao diện (Theme)"));
        formPanel.add(Box.createVerticalStrut(6));

        String[] themeOptions = {
            "Tối (Dark Mode)",
            "Sáng (Light Mode)",
            "Tự động (Theo giờ 6h - 18h)",
            "Theo hệ thống"
        };
        JComboBox<String> cbTheme = createStyledComboBox(themeOptions);

        ThemeManager.ThemeMode currentMode = ThemeManager.getThemeMode();
        if (currentMode == ThemeManager.ThemeMode.DARK) cbTheme.setSelectedIndex(0);
        else if (currentMode == ThemeManager.ThemeMode.LIGHT) cbTheme.setSelectedIndex(1);
        else if (currentMode == ThemeManager.ThemeMode.AUTO_TIME) cbTheme.setSelectedIndex(2);
        else cbTheme.setSelectedIndex(3);

        formPanel.add(cbTheme);
        content.add(formPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = createButton("Hủy", ThemeManager.getInputBg(), ThemeManager.getTextSecondary(), false);
        JButton btnConnect = createButton("Bắt đầu Chat", ThemeManager.getAccent(), Color.WHITE, true);

        final boolean[] success = {false};

        btnCancel.addActionListener(e -> dialog.dispose());

        btnConnect.addActionListener(e -> {
            username = txtUsername.getText().trim();
            serverIP = txtIP.getText().trim();

            if (username.isEmpty() || serverIP.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int selIndex = cbTheme.getSelectedIndex();
            if (selIndex == 0) ThemeManager.setThemeMode(ThemeManager.ThemeMode.DARK);
            else if (selIndex == 1) ThemeManager.setThemeMode(ThemeManager.ThemeMode.LIGHT);
            else if (selIndex == 2) ThemeManager.setThemeMode(ThemeManager.ThemeMode.AUTO_TIME);
            else ThemeManager.setThemeMode(ThemeManager.ThemeMode.SYSTEM);

            success[0] = true;
            dialog.dispose();
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnConnect);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.getRootPane().setDefaultButton(btnConnect);
        dialog.setContentPane(content);
        dialog.setVisible(true);

        return success[0];
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ThemeManager.getTextPrimary());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getInputBg());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                g2.setColor(ThemeManager.getBorderColor());
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));

                super.paintComponent(g2);
                g2.dispose();
            }
        };
        tf.setOpaque(false);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(ThemeManager.getTextPrimary());
        tf.setCaretColor(ThemeManager.getTextPrimary());
        tf.setBorder(new EmptyBorder(8, 14, 8, 14));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setPreferredSize(new Dimension(0, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(ThemeManager.getInputBg());
        cb.setForeground(ThemeManager.getTextPrimary());
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cb.setPreferredSize(new Dimension(0, 40));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        cb.setFocusable(false);

        cb.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2.setColor(ThemeManager.getTextSecondary());
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        int[] xPoints = {cx - 4, cx + 4, cx};
                        int[] yPoints = {cy - 2, cy - 2, cy + 3};
                        g2.fillPolygon(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                };
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setOpaque(false);
                button.setFocusable(false);
                return button;
            }

            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(ThemeManager.getInputBg());
                g2.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 10, 10));

                g2.setColor(ThemeManager.getBorderColor());
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, c.getWidth() - 1, c.getHeight() - 1, 10, 10));

                g2.dispose();
                super.paint(g, c);
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = (BasicComboPopup) super.createPopup();
                popup.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
                return popup;
            }
        });

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setBorder(new EmptyBorder(8, 12, 8, 12));

                if (index == -1) {
                    lbl.setOpaque(false);
                    lbl.setForeground(ThemeManager.getTextPrimary());
                    return lbl;
                }

                lbl.setOpaque(true);
                if (list != null) {
                    list.setBackground(ThemeManager.getSidebarBg());
                }

                if (isSelected) {
                    lbl.setBackground(ThemeManager.getAccent());
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setBackground(ThemeManager.getSidebarBg());
                    lbl.setForeground(ThemeManager.getTextPrimary());
                }
                return lbl;
            }
        });

        return cb;
    }

    private JButton createButton(String text, Color bg, Color fg, boolean isPrimary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentBg = getModel().isPressed() ? bg.darker() : getModel().isRollover() ? (isPrimary ? bg.brighter() : ThemeManager.getHoverBg()) : bg;
                g2.setColor(currentBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                if (!isPrimary) {
                    g2.setColor(ThemeManager.getBorderColor());
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public String getUsername() { return username; }
    public String getServerIP() { return serverIP; }
}