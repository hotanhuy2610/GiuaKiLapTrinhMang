package server;

public class FileTransferService {

    private final TCPServer server;


    public FileTransferService(
            TCPServer server) {

        this.server =
                server;
    }


    public void sendPrivateFile(
            ClientHandler sender,
            String target,
            String fileName,
            byte[] data) {

        ClientHandler receiver =
                server.getClient(
                        target
                );


        if (receiver == null) {

            sender.sendError(
                    target
                            + " hien khong online."
            );

            return;
        }


        receiver.sendFile(
                sender.getUsername(),
                fileName,
                data
        );
    }
}