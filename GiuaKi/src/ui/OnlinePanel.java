package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OnlinePanel extends JPanel {

    private final String currentUsername;
    private final DefaultListModel<String> onlineModel = new DefaultListModel<>();
    private final JList<String> onlineList = new JList<>(onlineModel);
    private final JTextField txtSearch = new JTextField();
    private final JLabel lblTitle = new JLabel("Đoạn chat");

    private final JPanel topContainer = new JPanel();
    private final JPanel titleRow = new JPanel(new BorderLayout());
    private final JPanel searchPanel;
    private final JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    private final JButton tabAll;
    private final JButton tabOnline;
    private final JButton btnTheme;
    private final JButton btnPlus;

    private Consumer<String> userSelectedListener;
    private boolean updating = false;
    private boolean isOnlineTabSelected = false;
    private List<String> allUsers = new ArrayList<>();

    public OnlinePanel(String currentUsername) {
        this.currentUsername = currentUsername;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(320, 0));
        setBackground(ThemeManager.getSidebarBg());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.getBorderColor()));

        ThemeManager.addThemeChangeListener(this::applyTheme);

        // Header Section
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.setBorder(new EmptyBorder(14, 16, 8, 16));

        // Row 1: Title + Action Buttons
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ThemeManager.getTextPrimary());

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        actionBtns.setOpaque(false);

        btnTheme = createIconButton(UIIcons.createMoonIcon(18, ThemeManager.getTextSecondary()), "Đổi giao diện");
        btnTheme.addActionListener(e -> toggleThemeMenu(btnTheme));

        // Plus "+" icon button as requested
        btnPlus = createIconButton(UIIcons.createPlusIcon(18, ThemeManager.getTextSecondary()), "Tạo cuộc trò chuyện mới");

        actionBtns.add(btnTheme);
        actionBtns.add(btnPlus);

        titleRow.add(lblTitle, BorderLayout.WEST);
        titleRow.add(actionBtns, BorderLayout.EAST);
        topContainer.add(titleRow);

        topContainer.add(Box.createVerticalStrut(10));

        // Row 2: Search Input
        searchPanel = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getInputBg());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(6, 12, 6, 12));
        searchPanel.setPreferredSize(new Dimension(0, 36));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchIcon = new JLabel(UIIcons.createSearchIcon(16, ThemeManager.getTextMuted()));
        txtSearch.setOpaque(false);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setForeground(ThemeManager.getTextPrimary());
        txtSearch.setCaretColor(ThemeManager.getTextPrimary());
        txtSearch.setBorder(null);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filterList(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filterList(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filterList(); }
        });

        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        topContainer.add(searchPanel);

        topContainer.add(Box.createVerticalStrut(10));

        // Row 3: Filter Tabs ("Tất cả", "Trực tuyến")
        tabAll = createFilterTab("Tất cả", true);
        tabOnline = createFilterTab("Trực tuyến", false);

        tabAll.addActionListener(e -> {
            isOnlineTabSelected = false;
            updateTabStyles();
        });

        tabOnline.addActionListener(e -> {
            isOnlineTabSelected = true;
            updateTabStyles();
        });

        filterRow.setOpaque(false);
        filterRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        filterRow.add(tabAll);
        filterRow.add(tabOnline);
        topContainer.add(filterRow);

        add(topContainer, BorderLayout.NORTH);

        // Online User List
        onlineList.setOpaque(false);
        onlineList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        onlineList.setFixedCellHeight(68);
        onlineList.setBorder(new EmptyBorder(4, 8, 4, 8));
        onlineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        onlineList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String name = String.valueOf(value);

                JPanel cell = new JPanel(new BorderLayout(14, 0)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        if (isSelected) {
                            g2.setColor(ThemeManager.getActiveBg());
                            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                        } else {
                            g2.setColor(ThemeManager.getSidebarBg());
                            g2.fillRect(0, 0, getWidth(), getHeight());
                        }
                        g2.dispose();
                    }
                };
                cell.setOpaque(false);
                cell.setBorder(new EmptyBorder(8, 12, 8, 12));

                // Avatar
                JPanel avatarPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        Color c1 = new Color((name.hashCode() & 0xFF0000) >> 16, (name.hashCode() & 0x00FF00) >> 8, name.hashCode() & 0x0000FF);
                        if (c1.getRed() + c1.getGreen() + c1.getBlue() > 600 || c1.getRed() + c1.getGreen() + c1.getBlue() < 150) {
                            c1 = ThemeManager.getAccent();
                        }

                        g2.setColor(c1);
                        g2.fill(new Ellipse2D.Float(0, 0, 48, 48));

                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 19));
                        String initial = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase();
                        FontMetrics fm = g2.getFontMetrics();
                        int tx = (48 - fm.stringWidth(initial)) / 2;
                        int ty = ((48 - fm.getHeight()) / 2) + fm.getAscent();
                        g2.drawString(initial, tx, ty);

                        g2.setColor(ThemeManager.getOnlineGreen());
                        g2.fill(new Ellipse2D.Float(34, 34, 13, 13));
                        g2.setColor(ThemeManager.getSidebarBg());
                        g2.setStroke(new BasicStroke(2.0f));
                        g2.draw(new Ellipse2D.Float(34, 34, 13, 13));

                        g2.dispose();
                    }
                };
                avatarPanel.setOpaque(false);
                avatarPanel.setPreferredSize(new Dimension(48, 48));

                JPanel textBox = new JPanel(new GridLayout(2, 1, 0, 2));
                textBox.setOpaque(false);

                JLabel lblName = new JLabel(name);
                lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
                lblName.setForeground(ThemeManager.getTextPrimary());

                JLabel lblSub = new JLabel("Đang hoạt động");
                lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblSub.setForeground(ThemeManager.getTextSecondary());

                textBox.add(lblName);
                textBox.add(lblSub);

                cell.add(avatarPanel, BorderLayout.WEST);
                cell.add(textBox, BorderLayout.CENTER);

                return cell;
            }
        });

        onlineList.addListSelectionListener(e -> {
            if (updating || e.getValueIsAdjusting()) return;
            String user = onlineList.getSelectedValue();
            if (user != null && userSelectedListener != null) {
                userSelectedListener.accept(user);
            }
        });

        JScrollPane scrollPane = new JScrollPane(onlineList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void toggleThemeMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(ThemeManager.getSidebarBg());
        menu.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));

        JMenuItem itemDark = createMenuItem("Tối (Dark Mode)");
        JMenuItem itemLight = createMenuItem("Sáng (Light Mode)");
        JMenuItem itemAuto = createMenuItem("Tự động (Theo giờ 6h - 18h)");
        JMenuItem itemSystem = createMenuItem("Theo hệ thống");

        itemDark.addActionListener(e -> ThemeManager.setThemeMode(ThemeManager.ThemeMode.DARK));
        itemLight.addActionListener(e -> ThemeManager.setThemeMode(ThemeManager.ThemeMode.LIGHT));
        itemAuto.addActionListener(e -> ThemeManager.setThemeMode(ThemeManager.ThemeMode.AUTO_TIME));
        itemSystem.addActionListener(e -> ThemeManager.setThemeMode(ThemeManager.ThemeMode.SYSTEM));

        menu.add(itemDark);
        menu.add(itemLight);
        menu.add(itemAuto);
        menu.add(itemSystem);

        menu.show(invoker, 0, invoker.getHeight() + 4);
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setBackground(ThemeManager.getSidebarBg());
        item.setForeground(ThemeManager.getTextPrimary());
        item.setBorder(new EmptyBorder(8, 14, 8, 14));
        return item;
    }

    private JButton createIconButton(Icon icon, String tooltip) {
        JButton btn = new JButton(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(ThemeManager.getHoverBg());
                    g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(null);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(34, 34));
        return btn;
    }

    private JButton createFilterTab(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isSel = (this == tabAll && !isOnlineTabSelected) || (this == tabOnline && isOnlineTabSelected);
                if (isSel) {
                    g2.setColor(ThemeManager.getActiveBg());
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                } else if (getModel().isRollover()) {
                    g2.setColor(ThemeManager.getHoverBg());
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateTabStyles() {
        tabAll.setForeground(isOnlineTabSelected ? ThemeManager.getTextSecondary() : ThemeManager.getAccent());
        tabOnline.setForeground(isOnlineTabSelected ? ThemeManager.getAccent() : ThemeManager.getTextSecondary());
        tabAll.repaint();
        tabOnline.repaint();
    }

    public void setUserSelectedListener(Consumer<String> listener) {
        userSelectedListener = listener;
    }

    public void updateUsers(List<String> users) {
        updating = true;
        allUsers.clear();
        for (String u : users) {
            if (!u.equals(currentUsername)) {
                allUsers.add(u);
            }
        }
        filterList();
        updating = false;
    }

    private void filterList() {
        String query = txtSearch.getText().trim().toLowerCase();
        onlineModel.clear();
        for (String u : allUsers) {
            if (query.isEmpty() || u.toLowerCase().contains(query)) {
                onlineModel.addElement(u);
            }
        }
    }

    public void selectUser(String user) {
        if (user == null) return;
        updating = true;
        onlineList.setSelectedValue(user, true);
        updating = false;
    }

    public void clearSelection() {
        updating = true;
        onlineList.clearSelection();
        updating = false;
    }

    private void applyTheme() {
        setBackground(ThemeManager.getSidebarBg());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.getBorderColor()));
        lblTitle.setForeground(ThemeManager.getTextPrimary());
        txtSearch.setForeground(ThemeManager.getTextPrimary());
        txtSearch.setCaretColor(ThemeManager.getTextPrimary());

        btnTheme.setIcon(ThemeManager.isDark() ? UIIcons.createMoonIcon(18, ThemeManager.getTextSecondary()) : UIIcons.createSunIcon(18, ThemeManager.getTextSecondary()));
        btnPlus.setIcon(UIIcons.createPlusIcon(18, ThemeManager.getTextSecondary()));

        updateTabStyles();
        searchPanel.repaint();
        onlineList.repaint();
        revalidate();
        repaint();
    }
}