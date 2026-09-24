package ui;

import client.TCPClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

public class ChatUI extends JFrame {

    private final TCPClient controller;
    private final String username;
    private final String serverIP;
    private final OnlinePanel onlinePanel;
    private final ChatPanel chatPanel;

    public ChatUI(TCPClient controller) {
        this.controller = controller;

        // Login Dialog
        LoginDialog loginDialog = new LoginDialog();
        boolean accepted = loginDialog.showDialog(null);

        if (!accepted) {
            System.exit(0);
        }

        username = loginDialog.getUsername();
        serverIP = loginDialog.getServerIP();

        // Frame setup
        setTitle("Messenger - " + username);
        setSize(960, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(ThemeManager.getAppBg());
        ThemeManager.addThemeChangeListener(() -> {
            getContentPane().setBackground(ThemeManager.getAppBg());
            repaint();
        });

        // Online & Chat Panels
        onlinePanel = new OnlinePanel(username);
        chatPanel = new ChatPanel(controller);

        onlinePanel.setUserSelectedListener(chatPanel::openConversation);

        add(onlinePanel, BorderLayout.WEST);
        add(chatPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.closeConnection();
            }
        });

        setVisible(true);
    }

    public void updateOnlineUsers(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            String current = chatPanel.getSelectedUser();
            onlinePanel.updateUsers(users);

            if (current == null) return;

            if (users.contains(current)) {
                onlinePanel.selectUser(current);
            } else {
                onlinePanel.clearSelection();
                chatPanel.closeConversation();
            }
        });
    }

    public void showTyping(String sender, boolean typing) {
        chatPanel.showTyping(sender, typing);
    }

    public String getUsername() { return username; }
    public String getServerIP() { return serverIP; }
    public String getSelectedUser() { return chatPanel.getSelectedUser(); }
    public String getMessage() { return chatPanel.getMessage(); }
    public void clearMessage() { chatPanel.clearMessage(); }
    public File chooseFile() { return chatPanel.chooseFile(); }

    public void showMyMessage(String target, String message) {
        chatPanel.showMyMessage(target, message);
    }

    public void showOtherMessage(String sender, String message) {
        chatPanel.showTyping(sender, false);
        chatPanel.showOtherMessage(sender, message);
    }

    public void showMyFile(String target, String fileName) {
        chatPanel.showMyFile(target, fileName);
    }

    public void showFile(String sender, String fileName, byte[] data) {
        chatPanel.showFile(sender, fileName, data);
    }

    public void showError(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message));
    }

    public void showDisconnected() {
        showError("Mất kết nối với Server!");
    }
}