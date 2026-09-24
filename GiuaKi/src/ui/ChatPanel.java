package ui;

import client.TCPClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ChatPanel extends JPanel {

    private final TCPClient controller;
    private final JLabel lblChatTitle = new JLabel("");
    private final JLabel lblSubStatus = new JLabel("");
    private final JLabel lblEmpty = new JLabel("Chọn một cuộc trò chuyện để bắt đầu");
    private final JPanel avatarHeader = new JPanel();
    private final TypingIndicator typingIndicator = new TypingIndicator();
    private final JPanel headerPanel;
    private final JPanel bottomContainer;
    private final JPanel bottomInputRow;
    private final JPanel emptyChatPanel;
    private final JButton btnFile;
    private final JButton btnSend;
    private JScrollPane chatScrollPane;
    private DarkRoundedTextField txtMessage;
    private final Map<String, JPanel> conversations = new HashMap<>();
    private String selectedUser;
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    public ChatPanel(TCPClient controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(ThemeManager.getChatBg());

        ThemeManager.addThemeChangeListener(this::applyTheme);

        headerPanel = createHeaderPanel();
        emptyChatPanel = createEmptyChatPanel();
        createChatScroll();
        
        bottomContainer = new JPanel();
        bottomInputRow = new JPanel(new BorderLayout(10, 0));
        btnFile = createIconButton(UIIcons.createAttachIcon(24, ThemeManager.getAccent()), "Đính kèm file");
        btnSend = createIconButton(UIIcons.createSendIcon(24, ThemeManager.getAccent()), "Gửi tin nhắn");

        createInputArea();

        // Hide header and bottom input bar until a user is selected
        headerPanel.setVisible(false);
        bottomContainer.setVisible(false);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(ThemeManager.getHeaderBg());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
                new EmptyBorder(10, 20, 10, 20)
        ));

        // Left Header: Avatar + Name + Status
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftHeader.setOpaque(false);

        avatarHeader.setOpaque(false);
        avatarHeader.setPreferredSize(new Dimension(40, 40));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);

        lblChatTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblChatTitle.setForeground(ThemeManager.getTextPrimary());

        lblSubStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubStatus.setForeground(ThemeManager.getTextSecondary());

        titleBox.add(lblChatTitle);
        titleBox.add(lblSubStatus);

        leftHeader.add(avatarHeader);
        leftHeader.add(titleBox);
        header.add(leftHeader, BorderLayout.WEST);

        // Right Header: Phone / Video / Info Icons
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightHeader.setOpaque(false);

        JButton btnCall = createIconButton(UIIcons.createPhoneIcon(22, ThemeManager.getAccent()), "Bắt đầu cuộc gọi");
        JButton btnVideo = createIconButton(UIIcons.createVideoIcon(22, ThemeManager.getAccent()), "Bắt đầu gọi Video");
        JButton btnInfo = createIconButton(UIIcons.createInfoIcon(22, ThemeManager.getAccent()), "Thông tin cuộc trò chuyện");

        rightHeader.add(btnCall);
        rightHeader.add(btnVideo);
        rightHeader.add(btnInfo);
        header.add(rightHeader, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        return header;
    }

    private void createChatScroll() {
        chatScrollPane = new JScrollPane(emptyChatPanel);
        chatScrollPane.setOpaque(true);
        chatScrollPane.setBackground(ThemeManager.getChatBg());
        chatScrollPane.getViewport().setOpaque(true);
        chatScrollPane.getViewport().setBackground(ThemeManager.getChatBg());
        chatScrollPane.setBorder(null);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(chatScrollPane, BorderLayout.CENTER);
    }

    private final JPanel typingRow = new JPanel(new BorderLayout());

    private void createInputArea() {
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setBackground(ThemeManager.getHeaderBg());
        bottomContainer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.getBorderColor()));

        // Typing Row
        typingRow.setOpaque(false);
        typingRow.setBorder(new EmptyBorder(4, 20, 2, 20));
        typingIndicator.setAlignmentX(Component.LEFT_ALIGNMENT);
        typingRow.add(typingIndicator, BorderLayout.WEST);

        // Input Row
        bottomInputRow.setBackground(ThemeManager.getHeaderBg());
        bottomInputRow.setBorder(new EmptyBorder(8, 16, 12, 16));

        txtMessage = new DarkRoundedTextField("Aa");
        txtMessage.setEnabled(false);

        bottomInputRow.add(btnFile, BorderLayout.WEST);
        bottomInputRow.add(txtMessage, BorderLayout.CENTER);
        bottomInputRow.add(btnSend, BorderLayout.EAST);

        bottomContainer.add(typingRow);
        bottomContainer.add(bottomInputRow);
        add(bottomContainer, BorderLayout.SOUTH);

        btnSend.addActionListener(e -> controller.sendMessage());
        txtMessage.addActionListener(e -> controller.sendMessage());
        btnFile.addActionListener(e -> controller.chooseFile());

        txtMessage.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { notifyTyping(); }
            @Override public void removeUpdate(DocumentEvent e) { notifyTyping(); }
            @Override public void changedUpdate(DocumentEvent e) { notifyTyping(); }
        });
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
        btn.setPreferredSize(new Dimension(38, 38));
        return btn;
    }

    private void notifyTyping() {
        controller.typingChanged(selectedUser, txtMessage.getText());
    }

    public void showTyping(String sender, boolean typing) {
        SwingUtilities.invokeLater(() -> {
            if (selectedUser == null || !selectedUser.equals(sender)) return;
            if (typing) {
                typingIndicator.showTyping(sender);
            } else {
                typingIndicator.hideTyping();
            }
            bottomContainer.revalidate();
            bottomContainer.repaint();
        });
    }

    private JPanel createEmptyChatPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setBackground(ThemeManager.getChatBg());

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        // Large Messenger Badge Icon
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 198, 255), getWidth(), getHeight(), new Color(0, 114, 255));
                g2.setPaint(gp);
                g2.fillOval(0, 0, 72, 72);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(18, 20, 36, 26, 12, 12);
                int[] px = {22, 32, 26};
                int[] py = {42, 42, 50};
                g2.fillPolygon(px, py, 3);

                g2.setColor(new Color(0, 114, 255));
                g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(30, 26, 39, 32);
                g2.drawLine(39, 32, 33, 34);
                g2.drawLine(33, 34, 41, 40);

                g2.dispose();
            }
        };
        iconCircle.setPreferredSize(new Dimension(72, 72));
        iconCircle.setMaximumSize(new Dimension(72, 72));
        iconCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconCircle.setOpaque(false);

        JLabel title = new JLabel("Messenger cho Desktop");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ThemeManager.getTextPrimary());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblEmpty.setText("Chọn một cuộc trò chuyện từ danh sách bên trái để bắt đầu nhắn tin");
        lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEmpty.setForeground(ThemeManager.getTextSecondary());
        lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(iconCircle);
        box.add(Box.createVerticalStrut(14));
        box.add(title);
        box.add(Box.createVerticalStrut(6));
        box.add(lblEmpty);

        panel.add(box);
        return panel;
    }

    public void openConversation(String user) {
        if (selectedUser != null && !selectedUser.equals(user)) {
            controller.typingChanged(selectedUser, "");
            txtMessage.setText("");
        }

        selectedUser = user;
        lblChatTitle.setText(user);
        lblSubStatus.setText("Đang hoạt động");

        // Show header & input bar when user is selected
        headerPanel.setVisible(true);
        bottomContainer.setVisible(true);
        revalidate();
        repaint();

        // Header avatar
        avatarHeader.removeAll();
        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getAccent());
                g2.fill(new Ellipse2D.Float(0, 0, 40, 40));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
                String initial = user.isEmpty() ? "?" : user.substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int tx = (40 - fm.stringWidth(initial)) / 2;
                int ty = ((40 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, tx, ty);
                g2.dispose();
            }
        };
        avatarCircle.setOpaque(false);
        avatarCircle.setPreferredSize(new Dimension(40, 40));
        avatarHeader.add(avatarCircle);
        avatarHeader.revalidate();
        avatarHeader.repaint();

        typingIndicator.hideTyping();
        txtMessage.setEnabled(true);
        txtMessage.requestFocusInWindow();

        chatScrollPane.setViewportView(getConversationPanel(user));
        scrollToBottom();
    }

    public void closeConversation() {
        if (selectedUser != null) {
            controller.typingChanged(selectedUser, "");
        }
        selectedUser = null;
        lblChatTitle.setText("");
        lblSubStatus.setText("");
        avatarHeader.removeAll();
        txtMessage.setEnabled(false);
        txtMessage.setText("");
        typingIndicator.hideTyping();

        headerPanel.setVisible(false);
        bottomContainer.setVisible(false);
        chatScrollPane.setViewportView(emptyChatPanel);
        revalidate();
        repaint();
    }

    private JPanel getConversationPanel(String user) {
        return conversations.computeIfAbsent(user, key -> {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setOpaque(true);
            panel.setBackground(ThemeManager.getChatBg());
            panel.setBorder(new EmptyBorder(16, 16, 16, 16));
            return panel;
        });
    }

    public String getSelectedUser() { return selectedUser; }
    public String getMessage() { return txtMessage.getText().trim(); }
    public void clearMessage() { txtMessage.setText(""); }

    public File chooseFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public void showMyMessage(String target, String message) {
        addMessage(target, message, true);
    }

    public void showOtherMessage(String sender, String message) {
        if (sender.equals(selectedUser)) {
            typingIndicator.hideTyping();
        }
        addMessage(sender, message, false);
    }

    private void addMessage(String conversationUser, String message, boolean mine) {
        SwingUtilities.invokeLater(() -> {
            JPanel chatPanel = getConversationPanel(conversationUser);

            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(4, 0, 4, 0));

            Color bubbleBg = mine ? ThemeManager.getMyBubble() : ThemeManager.getOtherBubble();
            RoundedPanel bubble = new RoundedPanel(18, bubbleBg);
            bubble.setLayout(new BorderLayout());

            JLabel text = new JLabel();
            text.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            text.setForeground(mine ? Color.WHITE : ThemeManager.getTextPrimary());
            text.setBorder(new EmptyBorder(8, 14, 8, 14));

            FontMetrics fm = text.getFontMetrics(text.getFont());
            int stringWidth = fm.stringWidth(message);

            if (stringWidth > 360) {
                text.setText("<html><body style='width: 360px; word-wrap: break-word;'>" + escapeHtml(message) + "</body></html>");
            } else {
                text.setText(escapeHtml(message));
            }

            bubble.add(text, BorderLayout.CENTER);

            JLabel lblTime = new JLabel(LocalTime.now().format(timeFormat));
            lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblTime.setForeground(ThemeManager.getTextSecondary());

            JPanel messageArea = new JPanel();
            messageArea.setLayout(new BoxLayout(messageArea, BoxLayout.Y_AXIS));
            messageArea.setOpaque(false);

            if (mine) {
                bubble.setAlignmentX(Component.RIGHT_ALIGNMENT);
                lblTime.setAlignmentX(Component.RIGHT_ALIGNMENT);
            } else {
                bubble.setAlignmentX(Component.LEFT_ALIGNMENT);
                lblTime.setAlignmentX(Component.LEFT_ALIGNMENT);
            }

            messageArea.add(bubble);
            messageArea.add(Box.createVerticalStrut(3));
            messageArea.add(lblTime);

            JPanel side = new JPanel(new FlowLayout(mine ? FlowLayout.RIGHT : FlowLayout.LEFT, 8, 0));
            side.setOpaque(false);

            if (!mine) {
                JPanel otherAvatar = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(ThemeManager.getAccent());
                        g2.fill(new Ellipse2D.Float(0, 0, 28, 28));
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        String initial = conversationUser.isEmpty() ? "?" : conversationUser.substring(0, 1).toUpperCase();
                        FontMetrics afm = g2.getFontMetrics();
                        int tx = (28 - afm.stringWidth(initial)) / 2;
                        int ty = ((28 - afm.getHeight()) / 2) + afm.getAscent();
                        g2.drawString(initial, tx, ty);
                        g2.dispose();
                    }
                };
                otherAvatar.setOpaque(false);
                otherAvatar.setPreferredSize(new Dimension(28, 28));
                side.add(otherAvatar);
            }

            side.add(messageArea);
            row.add(side, mine ? BorderLayout.EAST : BorderLayout.WEST);

            Dimension preferred = row.getPreferredSize();
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferred.height));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            chatPanel.add(row);
            chatPanel.add(Box.createVerticalStrut(4));
            chatPanel.revalidate();
            chatPanel.repaint();

            if (conversationUser.equals(selectedUser)) {
                scrollToBottom();
            }
        });
    }

    public void showMyFile(String target, String fileName) {
        addMessage(target, "📎 File: " + fileName, true);
    }

    public void showFile(String sender, String fileName, byte[] data) {
        SwingUtilities.invokeLater(() -> {
            JPanel chatPanel = getConversationPanel(sender);

            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(4, 0, 4, 0));

            JPanel fileArea = new JPanel();
            fileArea.setLayout(new BoxLayout(fileArea, BoxLayout.Y_AXIS));
            fileArea.setOpaque(false);

            RoundedPanel bubble = new RoundedPanel(16, ThemeManager.getOtherBubble());
            bubble.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

            JLabel label = new JLabel("📄 " + fileName);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(ThemeManager.getTextPrimary());

            JButton download = new JButton("Tải về");
            download.setFont(new Font("Segoe UI", Font.BOLD, 12));
            download.setForeground(Color.WHITE);
            download.setBackground(ThemeManager.getAccent());
            download.setFocusPainted(false);
            download.setBorder(new EmptyBorder(6, 12, 6, 12));
            download.setCursor(new Cursor(Cursor.HAND_CURSOR));
            download.addActionListener(e -> saveFile(fileName, data));

            bubble.add(label);
            bubble.add(download);
            bubble.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblTime = new JLabel(LocalTime.now().format(timeFormat));
            lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblTime.setForeground(ThemeManager.getTextSecondary());
            lblTime.setAlignmentX(Component.LEFT_ALIGNMENT);

            fileArea.add(bubble);
            fileArea.add(Box.createVerticalStrut(3));
            fileArea.add(lblTime);

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            left.setOpaque(false);
            left.add(fileArea);

            row.add(left, BorderLayout.WEST);

            Dimension preferred = row.getPreferredSize();
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferred.height));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            chatPanel.add(row);
            chatPanel.add(Box.createVerticalStrut(4));
            chatPanel.revalidate();
            chatPanel.repaint();

            if (sender.equals(selectedUser)) {
                scrollToBottom();
            }
        });
    }

    private void saveFile(String fileName, byte[] data) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(fileName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(chooser.getSelectedFile().toPath(), data);
                JOptionPane.showMessageDialog(this, "Đã lưu file thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu file!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = chatScrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void applyTheme() {
        setBackground(ThemeManager.getChatBg());
        headerPanel.setBackground(ThemeManager.getHeaderBg());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
                new EmptyBorder(10, 20, 10, 20)
        ));

        lblChatTitle.setForeground(ThemeManager.getTextPrimary());
        lblSubStatus.setForeground(ThemeManager.getTextSecondary());
        lblEmpty.setForeground(ThemeManager.getTextSecondary());

        emptyChatPanel.setBackground(ThemeManager.getChatBg());
        if (chatScrollPane != null) {
            chatScrollPane.setBackground(ThemeManager.getChatBg());
            chatScrollPane.getViewport().setBackground(ThemeManager.getChatBg());
        }

        bottomContainer.setBackground(ThemeManager.getHeaderBg());
        bottomContainer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.getBorderColor()));
        bottomInputRow.setBackground(ThemeManager.getHeaderBg());

        if (txtMessage != null) {
            txtMessage.repaint();
        }

        btnFile.setIcon(UIIcons.createAttachIcon(24, ThemeManager.getAccent()));
        btnSend.setIcon(UIIcons.createSendIcon(24, ThemeManager.getAccent()));

        for (JPanel panel : conversations.values()) {
            panel.setBackground(ThemeManager.getChatBg());
        }
        revalidate();
        repaint();
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color color;

        public RoundedPanel(int radius, Color color) {
            this.radius = radius;
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class DarkRoundedTextField extends JTextField {
        private final String placeholder;

        public DarkRoundedTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(new EmptyBorder(10, 16, 10, 16));
            setPreferredSize(new Dimension(100, 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ThemeManager.getInputBg());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            g2.dispose();

            setForeground(ThemeManager.getTextPrimary());
            setCaretColor(ThemeManager.getTextPrimary());

            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D tg = (Graphics2D) g.create();
                tg.setColor(ThemeManager.getTextSecondary());
                tg.setFont(getFont());
                FontMetrics fm = tg.getFontMetrics();
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                tg.drawString(placeholder, 16, y);
                tg.dispose();
            }
        }
    }
}