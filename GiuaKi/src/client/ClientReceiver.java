package client;

import java.io.DataInputStream;
import java.io.IOException;


public class ClientReceiver extends Thread {

    private final DataInputStream is;

    private final TCPClient client;


    public ClientReceiver(
            DataInputStream is,
            TCPClient client) {

        this.is =
                is;

        this.client =
                client;
    }


    @Override
    public void run() {

        try {

            while (true) {

                String type =
                        is.readUTF();


                if (type.equals("MESSAGE")) {

                    String sender =
                            is.readUTF();


                    String message =
                            is.readUTF();


                    client.receiveMessage(
                            sender,
                            message
                    );
                }


                else if (type.equals("FILE")) {

                    String sender =
                            is.readUTF();


                    String fileName =
                            is.readUTF();


                    int size =
                            is.readInt();


                    byte[] data =
                            new byte[size];


                    is.readFully(
                            data
                    );


                    client.receiveFile(
                            sender,
                            fileName,
                            data
                    );
                }


                else if (type.equals("ERROR")) {

                    String message =
                            is.readUTF();


                    client.receiveError(
                            message
                    );
                }
            }

        } catch (IOException e) {

            client.serverDisconnected();
        }
    }
}