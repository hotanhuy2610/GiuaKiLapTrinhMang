package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import java.net.Socket;

import java.nio.file.Files;


public class ClientConnection {

    private Socket socket;

    private DataInputStream is;

    private DataOutputStream os;

    private final TCPClient client;


    public ClientConnection(
            TCPClient client) {

        this.client =
                client;
    }


    public void connect(
            String serverIP,
            String username)
            throws IOException {

        socket =
                new Socket(
                        serverIP,
                        2020
                );


        is =
                new DataInputStream(
                        socket.getInputStream()
                );


        os =
                new DataOutputStream(
                        socket.getOutputStream()
                );


        os.writeUTF(
                username
        );


        os.flush();


        ClientReceiver receiver =
                new ClientReceiver(
                        is,
                        client
                );


        receiver.start();
    }


    public void sendMessage(
            String target,
            String message)
            throws IOException {

        synchronized (os) {

            os.writeUTF(
                    "MESSAGE"
            );


            os.writeUTF(
                    target
            );


            os.writeUTF(
                    message
            );


            os.flush();
        }
    }


    public void sendFile(
            String target,
            File file)
            throws IOException {

        byte[] data =
                Files.readAllBytes(
                        file.toPath()
                );


        synchronized (os) {

            os.writeUTF(
                    "FILE"
            );


            os.writeUTF(
                    target
            );


            os.writeUTF(
                    file.getName()
            );


            os.writeInt(
                    data.length
            );


            os.write(
                    data
            );


            os.flush();
        }
    }


    public boolean isConnected() {

        return socket != null
                && socket.isConnected()
                && !socket.isClosed();
    }


    public void close() {

        try {

            if (is != null) {

                is.close();
            }

        } catch (IOException ignored) {

        }


        try {

            if (os != null) {

                os.close();
            }

        } catch (IOException ignored) {

        }


        try {

            if (socket != null
                    && !socket.isClosed()) {

                socket.close();
            }

        } catch (IOException ignored) {

        }
    }
}