package ui;

import client.TCPClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;


public class ChatPanel
        extends JPanel {

    private final TCPClient controller;


    // =====================================================
    // HEADER
    // =====================================================

    private final JLabel lblChatTitle =
            new JLabel(
                    ""
            );


    // =====================================================
    // TYPING
    // =====================================================

    private final TypingIndicator typingIndicator =
            new TypingIndicator();


    // =====================================================
    // CHAT
    // =====================================================

    private JScrollPane chatScrollPane;

    private JTextField txtMessage;


    // Mỗi user có 1 lịch sử riêng
    private final Map<String, JPanel> conversations =
            new HashMap<>();


    private String selectedUser;


    // =====================================================
    // TIME
    // =====================================================

    private final DateTimeFormatter timeFormat =
            DateTimeFormatter.ofPattern(
                    "HH:mm"
            );


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ChatPanel(
            TCPClient controller) {

        this.controller =
                controller;


        setLayout(
                new BorderLayout()
        );


        setBackground(
                Color.WHITE
        );


        createHeader();

        createChatScroll();

        createInputArea();
    }


    // =====================================================
    // HEADER
    // =====================================================

    private void createHeader() {

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );


        header.setBackground(
                Color.WHITE
        );


        header.setBorder(
                new EmptyBorder(
                        14,
                        20,
                        14,
                        20
                )
        );


        lblChatTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        19
                )
        );


        header.add(
                lblChatTitle,
                BorderLayout.WEST
        );


        add(
                header,
                BorderLayout.NORTH
        );
    }


    // =====================================================
    // CHAT SCROLL
    // =====================================================

    private void createChatScroll() {

        chatScrollPane =
                new JScrollPane(
                        createEmptyChatPanel()
                );


        chatScrollPane.setBorder(
                null
        );


        chatScrollPane
                .getVerticalScrollBar()
                .setUnitIncrement(
                        16
                );


        add(
                chatScrollPane,
                BorderLayout.CENTER
        );
    }


    // =====================================================
    // INPUT AREA
    // =====================================================

    private void createInputArea() {

        // =========================================
        // CONTAINER
        //
        // Dòng 1: Nam đang soạn tin...
        // Dòng 2: file + input + send
        // =========================================

        JPanel bottomContainer =
                new JPanel();


        bottomContainer.setLayout(
                new BoxLayout(
                        bottomContainer,
                        BoxLayout.Y_AXIS
                )
        );


        bottomContainer.setBackground(
                Color.WHITE
        );


        // =================================================
        // TYPING ROW
        // =================================================

        JPanel typingRow =
                new JPanel(
                        new BorderLayout()
                );


        typingRow.setBackground(
                Color.WHITE
        );


        typingRow.setBorder(
                new EmptyBorder(
                        0,
                        62,
                        3,
                        10
                )
        );


        typingRow.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        typingIndicator.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        typingRow.add(
                typingIndicator,
                BorderLayout.WEST
        );


        // =================================================
        // INPUT ROW
        // =================================================

        JPanel bottom =
                new JPanel(
                        new BorderLayout(
                                8,
                                0
                        )
                );


        bottom.setBackground(
                Color.WHITE
        );


        bottom.setBorder(
                new EmptyBorder(
                        5,
                        12,
                        10,
                        12
                )
        );


        bottom.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        // =================================================
        // FILE
        // =================================================

        JButton btnFile =
                createIconButton(
                        "/icons/file-icon.png",
                        "File"
                );


        // =================================================
        // MESSAGE INPUT
        // =================================================

        txtMessage =
                new RoundedTextField(
                        28,
                        "Nhập tin nhắn..."
                );


        txtMessage.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );


        txtMessage.setBorder(
                new EmptyBorder(
                        9,
                        16,
                        9,
                        16
                )
        );


        txtMessage.setPreferredSize(
                new Dimension(
                        100,
                        44
                )
        );


        txtMessage.setEnabled(
                false
        );


        // =================================================
        // SEND
        // =================================================

        JButton btnSend =
                createIconButton(
                        "/icons/send-icon.png",
                        "Gửi"
                );


        bottom.add(
                btnFile,
                BorderLayout.WEST
        );


        bottom.add(
                txtMessage,
                BorderLayout.CENTER
        );


        bottom.add(
                btnSend,
                BorderLayout.EAST
        );


        // =========================================
        // GHÉP 2 DÒNG
        // =========================================

        bottomContainer.add(
                typingRow
        );


        bottomContainer.add(
                bottom
        );


        add(
                bottomContainer,
                BorderLayout.SOUTH
        );


        // =================================================
        // EVENTS
        // =================================================

        btnSend.addActionListener(
                e ->
                        controller.sendMessage()
        );


        txtMessage.addActionListener(
                e ->
                        controller.sendMessage()
        );


        btnFile.addActionListener(
                e ->
                        controller.chooseFile()
        );


        // =================================================
        // PHÁT HIỆN USER ĐANG GÕ
        // =================================================

        txtMessage
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {

                                notifyTyping();
                            }


                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {

                                notifyTyping();
                            }


                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {

                                notifyTyping();
                            }
                        }
                );
    }


    // =====================================================
    // THÔNG BÁO ĐANG GÕ
    // =====================================================

    private void notifyTyping() {

        controller.typingChanged(
                selectedUser,
                txtMessage.getText()
        );
    }


    // =====================================================
    // NHẬN TYPING TỪ UDP
    // =====================================================

    public void showTyping(
            String sender,
            boolean typing) {

        SwingUtilities.invokeLater(
                () -> {

                    // Chỉ hiện nếu đang mở cuộc chat
                    // với đúng người đang gõ
                    if (selectedUser == null) {

                        return;
                    }


                    if (!selectedUser.equals(
                            sender
                    )) {

                        return;
                    }


                    if (typing) {

                        typingIndicator.showTyping(
                                sender
                        );

                    } else {

                        typingIndicator.hideTyping();
                    }
                }
        );
    }


    // =====================================================
    // EMPTY CHAT
    // =====================================================

    private JPanel createEmptyChatPanel() {

        JPanel panel =
                new JPanel();


        panel.setBackground(
                Color.WHITE
        );


        return panel;
    }


    // =====================================================
    // OPEN CONVERSATION
    // =====================================================

    public void openConversation(
            String user) {

        // Nếu đang gõ cho người cũ
        if (selectedUser != null
                && !selectedUser.equals(
                user
        )) {

            controller.typingChanged(
                    selectedUser,
                    ""
            );


            txtMessage.setText(
                    ""
            );
        }


        selectedUser =
                user;


        lblChatTitle.setText(
                user
        );


        // Khi chuyển cuộc trò chuyện
        // ẩn typing cũ
        typingIndicator.hideTyping();


        txtMessage.setEnabled(
                true
        );


        txtMessage.requestFocusInWindow();


        chatScrollPane.setViewportView(
                getConversationPanel(
                        user
                )
        );


        scrollToBottom();
    }


    // =====================================================
    // CLOSE CONVERSATION
    // =====================================================

    public void closeConversation() {

        if (selectedUser != null) {

            controller.typingChanged(
                    selectedUser,
                    ""
            );
        }


        selectedUser =
                null;


        lblChatTitle.setText(
                ""
        );


        txtMessage.setEnabled(
                false
        );


        txtMessage.setText(
                ""
        );


        typingIndicator.hideTyping();


        chatScrollPane.setViewportView(
                createEmptyChatPanel()
        );
    }


    // =====================================================
    // LỊCH SỬ CHAT RIÊNG
    // =====================================================

    private JPanel getConversationPanel(
            String user) {

        return conversations.computeIfAbsent(
                user,

                key -> {

                    JPanel panel =
                            new JPanel();


                    panel.setLayout(
                            new BoxLayout(
                                    panel,
                                    BoxLayout.Y_AXIS
                            )
                    );


                    panel.setBackground(
                            Color.WHITE
                    );


                    panel.setBorder(
                            new EmptyBorder(
                                    10,
                                    5,
                                    10,
                                    5
                            )
                    );


                    return panel;
                }
        );
    }


    // =====================================================
    // GET
    // =====================================================

    public String getSelectedUser() {

        return selectedUser;
    }


    public String getMessage() {

        return txtMessage
                .getText()
                .trim();
    }


    public void clearMessage() {

        txtMessage.setText(
                ""
        );
    }


    // =====================================================
    // CHỌN FILE
    // =====================================================

    public File chooseFile() {

        JFileChooser chooser =
                new JFileChooser();


        if (chooser.showOpenDialog(
                this
        ) == JFileChooser.APPROVE_OPTION) {

            return chooser
                    .getSelectedFile();
        }


        return null;
    }


    // =====================================================
    // MY MESSAGE
    // =====================================================

    public void showMyMessage(
            String target,
            String message) {

        addMessage(
                target,
                message,
                true
        );
    }


    // =====================================================
    // OTHER MESSAGE
    // =====================================================

    public void showOtherMessage(
            String sender,
            String message) {

        if (sender.equals(
                selectedUser
        )) {

            typingIndicator.hideTyping();
        }


        addMessage(
                sender,
                message,
                false
        );
    }


    // =====================================================
    // ADD MESSAGE
    // =====================================================

    private void addMessage(
            String conversationUser,
            String message,
            boolean mine) {

        SwingUtilities.invokeLater(
                () -> {

                    JPanel chatPanel =
                            getConversationPanel(
                                    conversationUser
                            );


                    JPanel row =
                            new JPanel(
                                    new BorderLayout()
                            );


                    row.setOpaque(
                            false
                    );


                    row.setBorder(
                            new EmptyBorder(
                                    3,
                                    10,
                                    3,
                                    10
                            )
                    );


                    // =====================================
                    // BUBBLE
                    // =====================================

                    RoundedPanel bubble =
                            new RoundedPanel(
                                    22,

                                    mine

                                            ? new Color(
                                            0,
                                            132,
                                            255
                                    )

                                            : new Color(
                                            235,
                                            235,
                                            235
                                    )
                            );


                    bubble.setLayout(
                            new BorderLayout()
                    );


                    JLabel text =
                            new JLabel(
                                    "<html>"
                                            + escapeHtml(
                                            message
                                    )
                                            + "</html>"
                            );


                    text.setFont(
                            new Font(
                                    "Arial",
                                    Font.PLAIN,
                                    15
                            )
                    );


                    text.setForeground(
                            mine
                                    ? Color.WHITE
                                    : Color.BLACK
                    );


                    text.setBorder(
                            new EmptyBorder(
                                    9,
                                    14,
                                    9,
                                    14
                            )
                    );


                    bubble.add(
                            text,
                            BorderLayout.CENTER
                    );


                    // =====================================
                    // TIME
                    // =====================================

                    JLabel lblTime =
                            new JLabel(
                                    LocalTime
                                            .now()
                                            .format(
                                                    timeFormat
                                            )
                            );


                    lblTime.setFont(
                            new Font(
                                    "Arial",
                                    Font.PLAIN,
                                    10
                            )
                    );


                    lblTime.setForeground(
                            Color.GRAY
                    );


                    // =====================================
                    // MESSAGE + TIME
                    // =====================================

                    JPanel messageArea =
                            new JPanel();


                    messageArea.setLayout(
                            new BoxLayout(
                                    messageArea,
                                    BoxLayout.Y_AXIS
                            )
                    );


                    messageArea.setOpaque(
                            false
                    );


                    if (mine) {

                        bubble.setAlignmentX(
                                Component.RIGHT_ALIGNMENT
                        );


                        lblTime.setAlignmentX(
                                Component.RIGHT_ALIGNMENT
                        );

                    } else {

                        bubble.setAlignmentX(
                                Component.LEFT_ALIGNMENT
                        );


                        lblTime.setAlignmentX(
                                Component.LEFT_ALIGNMENT
                        );
                    }


                    messageArea.add(
                            bubble
                    );


                    messageArea.add(
                            Box.createVerticalStrut(
                                    2
                            )
                    );


                    messageArea.add(
                            lblTime
                    );


                    // =====================================
                    // CĂN TRÁI / PHẢI
                    // =====================================

                    JPanel side =
                            new JPanel(
                                    new FlowLayout(
                                            mine
                                                    ? FlowLayout.RIGHT
                                                    : FlowLayout.LEFT,
                                            0,
                                            0
                                    )
                            );


                    side.setOpaque(
                            false
                    );


                    side.add(
                            messageArea
                    );


                    row.add(
                            side,

                            mine
                                    ? BorderLayout.EAST
                                    : BorderLayout.WEST
                    );


                    // Không cho row giãn chiều cao
                    Dimension preferred =
                            row.getPreferredSize();


                    row.setMaximumSize(
                            new Dimension(
                                    Integer.MAX_VALUE,
                                    preferred.height
                            )
                    );


                    row.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );


                    chatPanel.add(
                            row
                    );


                    chatPanel.add(
                            Box.createVerticalStrut(
                                    3
                            )
                    );


                    chatPanel.revalidate();

                    chatPanel.repaint();


                    if (conversationUser.equals(
                            selectedUser
                    )) {

                        scrollToBottom();
                    }
                }
        );
    }


    // =====================================================
    // FILE CỦA MÌNH
    // =====================================================

    public void showMyFile(
            String target,
            String fileName) {

        addMessage(
                target,
                "📎 " + fileName,
                true
        );
    }


    // =====================================================
    // FILE NGƯỜI KHÁC
    // =====================================================

    public void showFile(
            String sender,
            String fileName,
            byte[] data) {

        SwingUtilities.invokeLater(
                () -> {

                    JPanel chatPanel =
                            getConversationPanel(
                                    sender
                            );


                    JPanel row =
                            new JPanel(
                                    new BorderLayout()
                            );


                    row.setOpaque(
                            false
                    );


                    row.setBorder(
                            new EmptyBorder(
                                    3,
                                    10,
                                    3,
                                    10
                            )
                    );


                    JPanel fileArea =
                            new JPanel();


                    fileArea.setLayout(
                            new BoxLayout(
                                    fileArea,
                                    BoxLayout.Y_AXIS
                            )
                    );


                    fileArea.setOpaque(
                            false
                    );


                    RoundedPanel bubble =
                            new RoundedPanel(
                                    22,
                                    new Color(
                                            235,
                                            235,
                                            235
                                    )
                            );


                    bubble.setLayout(
                            new FlowLayout(
                                    FlowLayout.LEFT,
                                    8,
                                    5
                            )
                    );


                    JLabel label =
                            new JLabel(
                                    "📎 "
                                            + fileName
                            );


                    JButton download =
                            new JButton(
                                    "Tải"
                            );


                    download.addActionListener(
                            e ->
                                    saveFile(
                                            fileName,
                                            data
                                    )
                    );


                    bubble.add(
                            label
                    );


                    bubble.add(
                            download
                    );


                    bubble.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );


                    // TIME
                    JLabel lblTime =
                            new JLabel(
                                    LocalTime
                                            .now()
                                            .format(
                                                    timeFormat
                                            )
                            );


                    lblTime.setFont(
                            new Font(
                                    "Arial",
                                    Font.PLAIN,
                                    10
                            )
                    );


                    lblTime.setForeground(
                            Color.GRAY
                    );


                    lblTime.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );


                    fileArea.add(
                            bubble
                    );


                    fileArea.add(
                            Box.createVerticalStrut(
                                    2
                            )
                    );


                    fileArea.add(
                            lblTime
                    );


                    JPanel left =
                            new JPanel(
                                    new FlowLayout(
                                            FlowLayout.LEFT,
                                            0,
                                            0
                                    )
                            );


                    left.setOpaque(
                            false
                    );


                    left.add(
                            fileArea
                    );


                    row.add(
                            left,
                            BorderLayout.WEST
                    );


                    Dimension preferred =
                            row.getPreferredSize();


                    row.setMaximumSize(
                            new Dimension(
                                    Integer.MAX_VALUE,
                                    preferred.height
                            )
                    );


                    row.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );


                    chatPanel.add(
                            row
                    );


                    chatPanel.add(
                            Box.createVerticalStrut(
                                    3
                            )
                    );


                    chatPanel.revalidate();

                    chatPanel.repaint();


                    if (sender.equals(
                            selectedUser
                    )) {

                        scrollToBottom();
                    }
                }
        );
    }


    // =====================================================
    // SAVE FILE
    // =====================================================

    private void saveFile(
            String fileName,
            byte[] data) {

        JFileChooser chooser =
                new JFileChooser();


        chooser.setSelectedFile(
                new File(
                        fileName
                )
        );


        if (chooser.showSaveDialog(
                this
        ) == JFileChooser.APPROVE_OPTION) {

            try {

                Files.write(
                        chooser
                                .getSelectedFile()
                                .toPath(),
                        data
                );


                JOptionPane.showMessageDialog(
                        this,
                        "Đã lưu file!"
                );

            } catch (IOException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Không lưu được file!"
                );
            }
        }
    }


    // =====================================================
    // ICON
    // =====================================================

    private JButton createIconButton(
            String path,
            String tooltip) {

        JButton button =
                new JButton();


        ImageIcon icon =
                loadIcon(
                        path,
                        28,
                        28
                );


        if (icon != null) {

            button.setIcon(
                    icon
            );

        } else {

            button.setText(
                    tooltip
            );
        }


        button.setToolTipText(
                tooltip
        );


        button.setBorderPainted(
                false
        );


        button.setContentAreaFilled(
                false
        );


        button.setFocusPainted(
                false
        );


        button.setOpaque(
                false
        );


        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );


        button.setPreferredSize(
                new Dimension(
                        42,
                        42
                )
        );


        return button;
    }


    // =====================================================
    // LOAD ICON
    // =====================================================

    private ImageIcon loadIcon(
            String path,
            int width,
            int height) {

        ImageIcon original;


        java.net.URL url =
                getClass()
                        .getResource(
                                path
                        );


        if (url != null) {

            original =
                    new ImageIcon(
                            url
                    );

        } else {

            original =
                    new ImageIcon(
                            "resources"
                                    + path
                    );
        }


        if (original.getIconWidth()
                <= 0) {

            return null;
        }


        Image scaled =
                original
                        .getImage()
                        .getScaledInstance(
                                width,
                                height,
                                Image.SCALE_SMOOTH
                        );


        return new ImageIcon(
                scaled
        );
    }


    // =====================================================
    // SCROLL
    // =====================================================

    private void scrollToBottom() {

        SwingUtilities.invokeLater(
                () -> {

                    JScrollBar bar =
                            chatScrollPane
                                    .getVerticalScrollBar();


                    bar.setValue(
                            bar.getMaximum()
                    );
                }
        );
    }


    // =====================================================
    // ESCAPE HTML
    // =====================================================

    private String escapeHtml(
            String text) {

        return text
                .replace(
                        "&",
                        "&amp;"
                )
                .replace(
                        "<",
                        "&lt;"
                )
                .replace(
                        ">",
                        "&gt;"
                );
    }


    // =====================================================
    // BUBBLE
    // =====================================================

    private static class RoundedPanel
            extends JPanel {

        private final int radius;

        private final Color color;


        public RoundedPanel(
                int radius,
                Color color) {

            this.radius =
                    radius;

            this.color =
                    color;


            setOpaque(
                    false
            );
        }


        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D)
                            g.create();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            g2.setColor(
                    color
            );


            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    radius,
                    radius
            );


            g2.dispose();


            super.paintComponent(
                    g
            );
        }
    }


    // =====================================================
    // INPUT BO TRÒN
    // =====================================================

    private static class RoundedTextField
            extends JTextField {

        private final int radius;

        private final String placeholder;


        public RoundedTextField(
                int radius,
                String placeholder) {

            this.radius =
                    radius;

            this.placeholder =
                    placeholder;


            setOpaque(
                    false
            );
        }


        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D)
                            g.create();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            g2.setColor(
                    new Color(
                            245,
                            245,
                            245
                    )
            );


            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    radius,
                    radius
            );


            g2.dispose();


            super.paintComponent(
                    g
            );


            // =========================================
            // PLACEHOLDER
            // =========================================

            if (getText().isEmpty()
                    && !isFocusOwner()) {

                Graphics2D textGraphics =
                        (Graphics2D)
                                g.create();


                textGraphics.setColor(
                        Color.GRAY
                );


                textGraphics.setFont(
                        getFont()
                );


                FontMetrics fm =
                        textGraphics
                                .getFontMetrics();


                int y =
                        (
                                getHeight()
                                        + fm.getAscent()
                                        - fm.getDescent()
                        )
                                / 2;


                textGraphics.drawString(
                        placeholder,
                        16,
                        y
                );


                textGraphics.dispose();
            }
        }


        @Override
        protected void paintBorder(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D)
                            g.create();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            g2.setColor(
                    new Color(
                            220,
                            220,
                            220
                    )
            );


            g2.drawRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    radius,
                    radius
            );


            g2.dispose();
        }
    }
}