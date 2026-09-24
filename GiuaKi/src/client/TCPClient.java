package client;

import ui.ChatUI;

import javax.swing.*;

import java.io.File;
import java.io.IOException;

import java.util.List;


public class TCPClient {

    private ChatUI ui;

    private ClientConnection connection;

    private UDPOnlineClient udpOnlineClient;

    private TypingClientService typingClientService;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public TCPClient() {

        ui =
                new ChatUI(
                        this
                );


        connection =
                new ClientConnection(
                        this
                );


        new Thread(
                this::connectServer
        ).start();
    }


    // =====================================================
    // KẾT NỐI SERVER
    // =====================================================

    private void connectServer() {

        try {

            // =========================================
            // TCP
            // =========================================

            connection.connect(
                    ui.getServerIP(),
                    ui.getUsername()
            );


            // =========================================
            // UDP
            // =========================================

            udpOnlineClient =
                    new UDPOnlineClient(
                            ui.getUsername(),
                            ui.getServerIP(),
                            this
                    );


            udpOnlineClient.start();


            // =========================================
            // TYPING SERVICE
            // =========================================

            typingClientService =
                    new TypingClientService(
                            udpOnlineClient
                    );

        } catch (IOException e) {

            ui.showError(
                    "Không kết nối được Server!"
            );
        }
    }


    // =====================================================
    // GỬI MESSAGE TCP
    // =====================================================

    public void sendMessage() {

        String target =
                ui.getSelectedUser();


        if (target == null) {

            ui.showError(
                    "Hãy chọn người muốn nhắn!"
            );

            return;
        }


        String message =
                ui.getMessage();


        if (message.isEmpty()) {

            return;
        }


        if (connection == null
                || !connection.isConnected()) {

            ui.showError(
                    "Chưa kết nối Server!"
            );

            return;
        }


        try {

            // Ngừng trạng thái đang soạn
            if (typingClientService != null) {

                typingClientService.stopTyping();
            }


            connection.sendMessage(
                    target,
                    message
            );


            ui.showMyMessage(
                    target,
                    message
            );


            ui.clearMessage();

        } catch (IOException e) {

            ui.showError(
                    "Không gửi được tin nhắn!"
            );
        }
    }


    // =====================================================
    // GỬI FILE TCP
    // =====================================================

    public void chooseFile() {

        String target =
                ui.getSelectedUser();


        if (target == null) {

            ui.showError(
                    "Hãy chọn người nhận file!"
            );

            return;
        }


        File file =
                ui.chooseFile();


        if (file == null) {

            return;
        }


        new Thread(
                () -> {

                    try {

                        connection.sendFile(
                                target,
                                file
                        );


                        ui.showMyFile(
                                target,
                                file.getName()
                        );

                    } catch (IOException e) {

                        ui.showError(
                                "Không gửi được file!"
                        );
                    }
                }
        ).start();
    }


    // =====================================================
    // Ô NHẬP THAY ĐỔI
    // =====================================================

    public void typingChanged(
            String target,
            String text) {

        if (typingClientService != null) {

            typingClientService.onTextChanged(
                    target,
                    text
            );
        }
    }


    // =====================================================
    // NHẬN TYPING UDP
    // =====================================================

    public void receiveTyping(
            String sender,
            boolean typing) {

        ui.showTyping(
                sender,
                typing
        );
    }


    // =====================================================
    // NHẬN MESSAGE TCP
    // =====================================================

    public void receiveMessage(
            String sender,
            String message) {

        ui.showOtherMessage(
                sender,
                message
        );
    }


    // =====================================================
    // NHẬN FILE TCP
    // =====================================================

    public void receiveFile(
            String sender,
            String fileName,
            byte[] data) {

        ui.showFile(
                sender,
                fileName,
                data
        );
    }


    // =====================================================
    // DANH SÁCH ONLINE UDP
    // =====================================================

    public void receiveOnlineUsers(
            List<String> users) {

        ui.updateOnlineUsers(
                users
        );
    }


    // =====================================================
    // ERROR
    // =====================================================

    public void receiveError(
            String message) {

        ui.showError(
                message
        );
    }


    // =====================================================
    // SERVER DISCONNECT
    // =====================================================

    public void serverDisconnected() {

        if (typingClientService != null) {

            typingClientService.stopTyping();
        }


        if (udpOnlineClient != null) {

            udpOnlineClient.close();
        }


        ui.showDisconnected();
    }


    // =====================================================
    // CLOSE
    // =====================================================

    public void closeConnection() {

        if (typingClientService != null) {

            typingClientService.stopTyping();
        }


        if (udpOnlineClient != null) {

            udpOnlineClient.close();
        }


        if (connection != null) {

            connection.close();
        }
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                TCPClient::new
        );
    }
}