package server;

import javax.swing.*;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;


public class TCPServer {

    public static final int TCP_PORT =
            2020;

    public static final int UDP_PORT =
            2021;


    // =====================================================
    // DANH SÁCH CLIENT
    // =====================================================

    private final Map<String, ClientHandler> clients =
            new ConcurrentHashMap<>();




    // =====================================================
    // SERVER UI
    // =====================================================

    private final ServerUI ui;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public TCPServer(
            ServerUI ui) {

        this.ui =
                ui;
    }


    // =====================================================
    // START TCP SERVER
    // =====================================================

    public void start() {

        try (
                ServerSocket serverSocket =
                        new ServerSocket(
                                TCP_PORT
                        )
        ) {


            log(
                    "TCP Server đang chạy tại port "
                            + TCP_PORT
            );


            log(
                    "Đang chờ Client kết nối..."
            );


            while (true) {

                // =================================================
                // CHỜ CLIENT KẾT NỐI
                // =================================================

                Socket socket =
                        serverSocket.accept();


                // =================================================
                // LẤY IP CLIENT
                // =================================================

                String clientIP =
                        socket
                                .getInetAddress()
                                .getHostAddress();


                System.out.println(
                        "Có máy truy cập TCP - IP: "
                                + clientIP
                );


                // =================================================
                // HIỂN THỊ IP LÊN GIAO DIỆN SERVER
                // =================================================

                if (ui != null) {

                    ui.clientAccess(
                            clientIP
                    );
                }


                // =================================================
                // TẠO CLIENT HANDLER
                // =================================================

                try {

                    ClientHandler handler =
                            new ClientHandler(
                                    socket,
                                    this
                            );


                    handler.start();

                } catch (IOException e) {

                    socket.close();


                    log(
                            "Lỗi tạo ClientHandler cho IP: "
                                    + clientIP
                    );
                }
            }

        } catch (IOException e) {

            log(
                    "Không thể mở TCP Server: "
                            + e.getMessage()
            );


            e.printStackTrace();
        }
    }


    // =====================================================
    // REGISTER CLIENT
    // =====================================================

    public boolean registerClient(
            String username,
            ClientHandler handler) {

        boolean success =
                clients.putIfAbsent(
                        username,
                        handler
                ) == null;


        if (success) {

            updateClientCount();
        }


        return success;
    }


    // =====================================================
    // REMOVE CLIENT
    // =====================================================

    public void removeClient(
            String username,
            ClientHandler handler) {

        if (username != null) {

            boolean removed =
                    clients.remove(
                            username,
                            handler
                    );


            if (removed) {

                if (ui != null) {

                    ui.clientLogout(
                            username
                    );
                }


                updateClientCount();
            }
        }
    }


    // =====================================================
    // USER ĐÃ LOGIN
    // =====================================================

    public void notifyClientLogin(
            String username,
            String ip) {

        if (ui != null) {

            ui.clientLogin(
                    username,
                    ip
            );
        }
    }


    // =====================================================
    // UPDATE CLIENT COUNT
    // =====================================================

    private void updateClientCount() {

        if (ui != null) {

            ui.updateClientCount(
                    clients.size()
            );
        }
    }


    // =====================================================
    // GET CLIENT
    // =====================================================

    public ClientHandler getClient(
            String username) {

        return clients.get(
                username
        );
    }




    // =====================================================
    // LOG
    // =====================================================

    public void log(
            String message) {

        System.out.println(
                message
        );


        if (ui != null) {

            ui.addLog(
                    message
            );
        }
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {


        SwingUtilities.invokeLater(
                () -> {

                    // =============================================
                    // MỞ GIAO DIỆN SERVER
                    // =============================================

                    ServerUI ui =
                            new ServerUI();


                    TCPServer server =
                            new TCPServer(
                                    ui
                            );


                    // =============================================
                    // UDP SERVER
                    // =============================================

                    Thread udpThread =
                            new Thread(
                                    () -> {

                                        ui.addLog(
                                                "UDP Server đang chạy tại port "
                                                        + UDP_PORT
                                        );


                                        new UDPOnlineServer(
                                                UDP_PORT
                                        ).start();
                                    }
                            );


                    udpThread.setDaemon(
                            true
                    );


                    udpThread.start();


                    // =============================================
                    // TCP SERVER
                    //
                    // Không chạy trực tiếp trên Swing Thread
                    // vì accept() sẽ chờ Client.
                    // =============================================

                    Thread tcpThread =
                            new Thread(
                                    server::start
                            );


                    tcpThread.start();
                }
        );
    }
}