package server;

import ui.UIIcons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ServerUI extends JFrame {

    private final JTextArea txtLog = new JTextArea();
    private final JLabel lblStatus = new JLabel(" Server đang chạy (Đã kết nối)");
    private final JLabel lblStatusIcon = new JLabel();
    private final JLabel lblLastIP = new JLabel("Chưa có");
    private final JLabel lblClientCount = new JLabel("0");
    private final JButton btnStart = createActionButton("Bắt đầu", UIIcons.createPlayIcon(16, new Color(52, 211, 153)));
    private final JButton btnStop = createActionButton("Dừng", UIIcons.createStopIcon(16, new Color(248, 113, 113)));
    private final JButton btnClearLog = createActionButton("Xóa Log", UIIcons.createTrashIcon(16, new Color(148, 163, 184)));

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private boolean isRunning = true;

    public ServerUI() {
        setTitle("Chat Server Admin Console");
        setSize(780, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(22, 27, 34)); // Dark slate background #161B22
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        createHeader(root);
        createLogArea(root);
        createBottomBar(root);

        setContentPane(root);
        setVisible(true);
    }

    private void createHeader(JPanel root) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(22, 24, 14, 24));

        // Title Row: CHAT SERVER CONTROLLER + Status Badge
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel lblTitle = new JLabel("CHAT SERVER CONTROLLER");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(56, 189, 248)); // Neon Cyan Accent

        // Glowing Status Pill Badge
        JPanel statusBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(13, 34, 27)); // Dark green background
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(new Color(16, 185, 129)); // Glowing border #10B981
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 20, 20));
                g2.dispose();
            }
        };
        statusBadge.setOpaque(false);
        statusBadge.setBorder(new EmptyBorder(2, 10, 2, 12));

        lblStatusIcon.setIcon(UIIcons.createPulseIcon(18, new Color(52, 211, 153)));
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setForeground(new Color(52, 211, 153));

        statusBadge.add(lblStatusIcon);
        statusBadge.add(lblStatus);

        titleRow.add(lblTitle, BorderLayout.WEST);
        titleRow.add(statusBadge, BorderLayout.EAST);
        header.add(titleRow);

        header.add(Box.createVerticalStrut(18));

        // 4 Metrics Cards Grid
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        metricsPanel.setOpaque(false);

        metricsPanel.add(createMetricCard("TCP PORT", String.valueOf(TCPServer.TCP_PORT), UIIcons.createPortIcon(20, new Color(99, 102, 241)), new Color(99, 102, 241)));
        metricsPanel.add(createMetricCard("UDP PORT", String.valueOf(TCPServer.UDP_PORT), UIIcons.createPortIcon(20, new Color(59, 130, 246)), new Color(59, 130, 246)));
        metricsPanel.add(createMetricCard("CLIENTS", lblClientCount, UIIcons.createUsersIcon(20, new Color(16, 185, 129)), new Color(16, 185, 129)));
        metricsPanel.add(createMetricCard("LAST ACCESS IP", lblLastIP, UIIcons.createPinIcon(20, new Color(245, 158, 11)), new Color(245, 158, 11)));

        header.add(metricsPanel);
        root.add(header, BorderLayout.NORTH);
    }

    private JPanel createMetricCard(String labelStr, Object valueComp, Icon icon, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 41, 59)); // Slate 800
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(accentColor);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JPanel topBox = new JPanel(new BorderLayout());
        topBox.setOpaque(false);

        JLabel label = new JLabel(labelStr);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(148, 163, 184));

        JLabel lblIcon = new JLabel(icon);

        topBox.add(label, BorderLayout.WEST);
        topBox.add(lblIcon, BorderLayout.EAST);

        JLabel val;
        if (valueComp instanceof String) {
            val = new JLabel((String) valueComp);
        } else {
            val = (JLabel) valueComp;
        }
        val.setFont(new Font("Segoe UI", Font.BOLD, 18));
        val.setForeground(new Color(248, 250, 252));

        card.add(topBox, BorderLayout.NORTH);
        card.add(val, BorderLayout.SOUTH);

        return card;
    }

    private void createLogArea(JPanel root) {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(4, 24, 12, 24));

        JLabel lblLog = new JLabel("Nhật ký hoạt động (System Log)");
        lblLog.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLog.setForeground(new Color(226, 232, 240));
        lblLog.setBorder(new EmptyBorder(0, 0, 8, 0));

        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtLog.setBackground(new Color(13, 17, 23)); // Dark Console #0D1117
        txtLog.setForeground(new Color(241, 245, 249));
        txtLog.setCaretColor(Color.WHITE);
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        txtLog.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(48, 54, 61), 1));
        scrollPane.getViewport().setBackground(new Color(13, 17, 23));

        center.add(lblLog, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
    }

    private void createBottomBar(JPanel root) {
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(4, 24, 20, 24));

        // Left Action Buttons: Bắt đầu, Dừng
        JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBtns.setOpaque(false);

        btnStart.addActionListener(e -> {
            isRunning = true;
            lblStatus.setText(" Server đang chạy (Đã kết nối)");
            lblStatus.setForeground(new Color(52, 211, 153));
            addLog("Server được khởi động lại.");
        });

        btnStop.addActionListener(e -> {
            isRunning = false;
            lblStatus.setText(" Server đã dừng");
            lblStatus.setForeground(new Color(248, 113, 113));
            addLog("Server tạm dừng chấp nhận kết nối mới.");
        });

        leftBtns.add(btnStart);
        leftBtns.add(btnStop);

        // Right Action Button: Xóa Log
        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightBtns.setOpaque(false);

        btnClearLog.addActionListener(e -> txtLog.setText(""));
        rightBtns.add(btnClearLog);

        bottomBar.add(leftBtns, BorderLayout.WEST);
        bottomBar.add(rightBtns, BorderLayout.EAST);

        root.add(bottomBar, BorderLayout.SOUTH);
    }

    private JButton createActionButton(String text, Icon icon) {
        JButton btn = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? new Color(51, 65, 85) : getModel().isRollover() ? new Color(30, 41, 59) : new Color(22, 27, 34);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(51, 65, 85));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(241, 245, 249));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void clientAccess(String ip) {
        SwingUtilities.invokeLater(() -> lblLastIP.setText(ip));
    }

    public void clientLogin(String username, String ip) {
        addLog(username + " đã đăng nhập - IP: " + ip);
    }

    public void clientLogout(String username) {
        addLog(username + " đã ngắt kết nối.");
    }

    public void updateClientCount(int count) {
        SwingUtilities.invokeLater(() -> lblClientCount.setText(String.valueOf(count)));
    }

    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String time = LocalTime.now().format(timeFormat);
            txtLog.append("[" + time + "] " + message + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }
}