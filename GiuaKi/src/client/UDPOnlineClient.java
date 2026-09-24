package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;


public class UDPOnlineClient {

    // =====================================================
    // PORT UDP
    // =====================================================

    private static final int UDP_PORT =
            2021;


    // =====================================================
    // THÔNG TIN CLIENT
    // =====================================================

    private final String username;

    private final String serverIP;

    private final TCPClient controller;


    // =====================================================
    // UDP SOCKET
    // =====================================================

    private DatagramSocket socket;

    private InetAddress serverAddress;


    private volatile boolean running =
            false;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public UDPOnlineClient(
            String username,
            String serverIP,
            TCPClient controller) {

        this.username =
                username;

        this.serverIP =
                serverIP;

        this.controller =
                controller;
    }


    // =====================================================
    // START UDP CLIENT
    // =====================================================

    public void start() {

        try {

            socket =
                    new DatagramSocket();


            serverAddress =
                    InetAddress.getByName(
                            serverIP
                    );


            running =
                    true;


            // =================================================
            // THREAD NHẬN UDP
            // =================================================

            Thread receiveThread =
                    new Thread(
                            this::receiveLoop
                    );


            receiveThread.setDaemon(
                    true
            );


            receiveThread.start();


            // =================================================
            // THREAD HEARTBEAT
            // =================================================

            Thread heartbeatThread =
                    new Thread(
                            this::heartbeatLoop
                    );


            heartbeatThread.setDaemon(
                    true
            );


            heartbeatThread.start();


            // =================================================
            // BÁO SERVER USER ONLINE
            // =================================================

            send(
                    "LOGIN|" + username
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =====================================================
    // HEARTBEAT
    // =====================================================

    private void heartbeatLoop() {

        while (running) {

            try {

                Thread.sleep(
                        3000
                );


                if (running) {

                    send(
                            "PING|" + username
                    );
                }

            } catch (InterruptedException e) {

                return;
            }
        }
    }


    // =====================================================
    // NHẬN DỮ LIỆU UDP
    // =====================================================

    private void receiveLoop() {

        byte[] buffer =
                new byte[8192];


        while (running) {

            try {

                DatagramPacket packet =
                        new DatagramPacket(
                                buffer,
                                buffer.length
                        );


                socket.receive(
                        packet
                );


                String message =
                        new String(
                                packet.getData(),
                                0,
                                packet.getLength(),
                                StandardCharsets.UTF_8
                        );


                // =================================================
                // NHẬN DANH SÁCH ONLINE
                // =================================================

                if (message.startsWith(
                        "ONLINE\n"
                )) {

                    String body =
                            message.substring(
                                    "ONLINE\n".length()
                            );


                    List<String> users =
                            new ArrayList<>();


                    if (!body.isBlank()) {

                        String[] names =
                                body.split(
                                        "\n"
                                );


                        for (String name :
                                names) {

                            if (!name.isBlank()) {

                                users.add(
                                        name.trim()
                                );
                            }
                        }
                    }


                    controller.receiveOnlineUsers(
                            users
                    );
                }


                // =================================================
                // NHẬN TRẠNG THÁI ĐANG SOẠN
                //
                // Server gửi:
                //
                // TYPING|Nam|1
                // TYPING|Nam|0
                // =================================================

                else if (message.startsWith(
                        "TYPING|"
                )) {

                    String[] parts =
                            message.split(
                                    "\\|",
                                    3
                            );


                    if (parts.length == 3) {

                        String sender =
                                parts[1];


                        boolean typing =
                                parts[2]
                                        .equals(
                                                "1"
                                        );


                        controller.receiveTyping(
                                sender,
                                typing
                        );
                    }
                }

            } catch (Exception e) {

                if (running) {

                    e.printStackTrace();
                }
            }
        }
    }


    // =====================================================
    // GỬI TRẠNG THÁI ĐANG SOẠN
    //
    // Đây chính là method đang bị thiếu
    // =====================================================

    public void sendTyping(
            String target,
            boolean typing) {

        // UDP chưa chạy
        if (!running) {

            return;
        }


        // Chưa chọn người nhận
        if (target == null
                || target.isBlank()) {

            return;
        }


        // Ví dụ:
        //
        // Huy đang gõ cho Nam:
        // TYPING|Huy|Nam|1
        //
        // Huy ngừng gõ:
        // TYPING|Huy|Nam|0

        String message =
                "TYPING|"
                        + username
                        + "|"
                        + target
                        + "|"
                        + (typing
                        ? "1"
                        : "0");


        send(
                message
        );
    }


    // =====================================================
    // HÀM GỬI UDP CHUNG
    // =====================================================

    private void send(
            String message) {

        try {

            if (socket == null
                    || socket.isClosed()) {

                return;
            }


            if (serverAddress == null) {

                return;
            }


            byte[] data =
                    message.getBytes(
                            StandardCharsets.UTF_8
                    );


            DatagramPacket packet =
                    new DatagramPacket(
                            data,
                            data.length,
                            serverAddress,
                            UDP_PORT
                    );


            socket.send(
                    packet
            );

        } catch (Exception e) {

            if (running) {

                e.printStackTrace();
            }
        }
    }


    // =====================================================
    // CLOSE
    // =====================================================

    public void close() {

        if (!running) {

            return;
        }


        send(
                "LOGOUT|" + username
        );


        running =
                false;


        if (socket != null
                && !socket.isClosed()) {

            socket.close();
        }
    }
}