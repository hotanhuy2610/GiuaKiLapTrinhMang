package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;


public class UDPOnlineServer {

    private final int port;

    private DatagramSocket socket;


    // username -> thông tin UDP Client
    private final Map<String, ClientInfo> onlineUsers =
            new ConcurrentHashMap<>();


    public UDPOnlineServer(int port) {
        this.port = port;
    }


    // =====================================================
    // START UDP SERVER
    // =====================================================

    public void start() {

        try {

            socket =
                    new DatagramSocket(
                            port
                    );


            System.out.println(
                    "UDP Online Server dang chay tai port "
                            + port
            );


            // Thread kiểm tra Client còn online
            Thread timeoutThread =
                    new Thread(
                            this::checkTimeout
                    );


            timeoutThread.setDaemon(
                    true
            );


            timeoutThread.start();


            byte[] buffer =
                    new byte[8192];


            while (true) {

                DatagramPacket packet =
                        new DatagramPacket(
                                buffer,
                                buffer.length
                        );


                // Chờ UDP Client gửi tới
                socket.receive(
                        packet
                );


                String data =
                        new String(
                                packet.getData(),
                                0,
                                packet.getLength(),
                                StandardCharsets.UTF_8
                        );


                handlePacket(
                        data,
                        packet.getAddress(),
                        packet.getPort()
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =====================================================
    // XỬ LÝ PACKET UDP
    // =====================================================

    private void handlePacket(
            String data,
            InetAddress address,
            int clientPort) {


        // =================================================
        // TYPING
        //
        // Client gửi:
        //
        // TYPING|Huy|Nam|1
        // TYPING|Huy|Nam|0
        //
        // 1 = đang gõ
        // 0 = ngừng gõ
        // =================================================

        if (data.startsWith(
                "TYPING|"
        )) {

            String[] typingParts =
                    data.split(
                            "\\|",
                            4
                    );


            if (typingParts.length == 4) {

                String sender =
                        typingParts[1];


                String target =
                        typingParts[2];


                boolean typing =
                        typingParts[3]
                                .equals(
                                        "1"
                                );


                sendToUser(target, "TYPING|" + sender + "|" + (typing ? "1" : "0"));
            }


            return;
        }


        // =================================================
        // ONLINE
        // =================================================

        String[] parts =
                data.split(
                        "\\|",
                        2
                );


        if (parts.length < 2) {

            return;
        }


        String command =
                parts[0];


        String username =
                parts[1].trim();


        // =================================================
        // LOGIN
        // =================================================

        if (command.equals(
                "LOGIN"
        )) {

            onlineUsers.put(
                    username,

                    new ClientInfo(
                            address,
                            clientPort,
                            System.currentTimeMillis()
                    )
            );


            System.out.println(
                    "[UDP] "
                            + username
                            + " online"
            );


            broadcastOnlineList();
        }


        // =================================================
        // PING
        // =================================================

        else if (command.equals(
                "PING"
        )) {

            onlineUsers.put(
                    username,

                    new ClientInfo(
                            address,
                            clientPort,
                            System.currentTimeMillis()
                    )
            );
        }


        // =================================================
        // LOGOUT
        // =================================================

        else if (command.equals(
                "LOGOUT"
        )) {

            onlineUsers.remove(
                    username
            );


            System.out.println(
                    "[UDP] "
                            + username
                            + " offline"
            );


            broadcastOnlineList();
        }
    }


    // =====================================================
    // GỬI UDP TỚI 1 USER
    //
    // TypingService sử dụng hàm này
    // =====================================================

    public void sendToUser(
            String username,
            String message) {

        try {

            ClientInfo client =
                    onlineUsers.get(
                            username
                    );


            // User không online
            if (client == null) {

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
                            client.address,
                            client.port
                    );


            socket.send(
                    packet
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =====================================================
    // GỬI DANH SÁCH ONLINE
    // =====================================================

    private void broadcastOnlineList() {

        try {

            ArrayList<String> users =
                    new ArrayList<>(
                            onlineUsers.keySet()
                    );


            Collections.sort(
                    users
            );


            String message =
                    "ONLINE\n"
                            + String.join(
                            "\n",
                            users
                    );


            byte[] data =
                    message.getBytes(
                            StandardCharsets.UTF_8
                    );


            for (ClientInfo client :
                    onlineUsers.values()) {

                DatagramPacket packet =
                        new DatagramPacket(
                                data,
                                data.length,
                                client.address,
                                client.port
                        );


                socket.send(
                        packet
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =====================================================
    // KIỂM TRA TIMEOUT
    // =====================================================

    private void checkTimeout() {

        while (true) {

            try {

                Thread.sleep(
                        3000
                );


                long now =
                        System.currentTimeMillis();


                for (String username :
                        new ArrayList<>(
                                onlineUsers.keySet()
                        )) {

                    ClientInfo info =
                            onlineUsers.get(
                                    username
                            );


                    if (info != null
                            && now - info.lastSeen
                            > 10000) {

                        onlineUsers.remove(
                                username
                        );


                        System.out.println(
                                "[UDP] "
                                        + username
                                        + " timeout"
                        );
                    }
                }


                broadcastOnlineList();

            } catch (InterruptedException e) {

                return;
            }
        }
    }


    // =====================================================
    // THÔNG TIN UDP CLIENT
    // =====================================================

    private static class ClientInfo {

        private final InetAddress address;

        private final int port;

        private final long lastSeen;


        ClientInfo(
                InetAddress address,
                int port,
                long lastSeen) {

            this.address =
                    address;

            this.port =
                    port;

            this.lastSeen =
                    lastSeen;
        }
    }
}