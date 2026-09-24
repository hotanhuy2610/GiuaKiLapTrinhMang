package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket socket;

    private final TCPServer server;

    private final DataInputStream is;

    private final DataOutputStream os;

    private String username;

    private boolean disconnected =
            false;


    public ClientHandler(
            Socket socket,
            TCPServer server)
            throws IOException {

        this.socket =
                socket;

        this.server =
                server;


        is =
                new DataInputStream(
                        socket.getInputStream()
                );


        os =
                new DataOutputStream(
                        socket.getOutputStream()
                );
    }


    @Override
    public void run() {

        boolean registered =
                false;


        try {

            username =
                    is.readUTF();


            registered =
                    server.registerClient(
                            username,
                            this
                    );


            if (!registered) {

                sendError(
                        "Ten "
                                + username
                                + " dang duoc su dung."
                );

                return;
            }


            String clientIP =
                    socket
                            .getInetAddress()
                            .getHostAddress();


            System.out.println(
                    username
                            + " da ket noi TCP - IP: "
                            + clientIP
            );


            server.notifyClientLogin(
                    username,
                    clientIP
            );


            while (true) {

                String type =
                        is.readUTF();


                if (type.equals("MESSAGE")) {

                    String target =
                            is.readUTF();


                    String message =
                            is.readUTF();


                    System.out.println(
                            username
                                    + " -> "
                                    + target
                                    + ": "
                                    + message
                    );


                    server
                            .getMessageService()
                            .sendPrivateMessage(
                                    this,
                                    target,
                                    message
                            );
                }


                else if (type.equals("FILE")) {

                    String target =
                            is.readUTF();


                    String fileName =
                            is.readUTF();


                    int fileSize =
                            is.readInt();


                    byte[] data =
                            new byte[fileSize];


                    is.readFully(
                            data
                    );


                    System.out.println(
                            username
                                    + " gui file "
                                    + fileName
                                    + " -> "
                                    + target
                    );


                    server
                            .getFileTransferService()
                            .sendPrivateFile(
                                    this,
                                    target,
                                    fileName,
                                    data
                            );
                }
            }

        } catch (IOException ignored) {

        } finally {

            if (registered) {

                server.removeClient(
                        username,
                        this
                );
            }


            disconnect();
        }
    }


    public void sendMessage(
            String sender,
            String message) {

        try {

            synchronized (os) {

                os.writeUTF(
                        "MESSAGE"
                );


                os.writeUTF(
                        sender
                );


                os.writeUTF(
                        message
                );


                os.flush();
            }

        } catch (IOException e) {

            disconnect();
        }
    }


    public void sendFile(
            String sender,
            String fileName,
            byte[] data) {

        try {

            synchronized (os) {

                os.writeUTF(
                        "FILE"
                );


                os.writeUTF(
                        sender
                );


                os.writeUTF(
                        fileName
                );


                os.writeInt(
                        data.length
                );


                os.write(
                        data
                );


                os.flush();
            }

        } catch (IOException e) {

            disconnect();
        }
    }


    public void sendError(
            String message) {

        try {

            synchronized (os) {

                os.writeUTF(
                        "ERROR"
                );


                os.writeUTF(
                        message
                );


                os.flush();
            }

        } catch (IOException ignored) {

        }
    }


    private synchronized void disconnect() {

        if (disconnected) {

            return;
        }


        disconnected =
                true;


        if (username != null) {

            System.out.println(
                    username
                            + " da thoat TCP"
            );
        }


        try {

            is.close();

        } catch (IOException ignored) {

        }


        try {

            os.close();

        } catch (IOException ignored) {

        }


        try {

            if (!socket.isClosed()) {

                socket.close();
            }

        } catch (IOException ignored) {

        }
    }


    public String getUsername() {

        return username;
    }
}