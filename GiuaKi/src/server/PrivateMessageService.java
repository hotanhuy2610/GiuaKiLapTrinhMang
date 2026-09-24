package server;

public class PrivateMessageService {

    private final TCPServer server;


    public PrivateMessageService(
            TCPServer server) {

        this.server =
                server;
    }


    public void sendPrivateMessage(
            ClientHandler sender,
            String target,
            String message) {

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


        receiver.sendMessage(
                sender.getUsername(),
                message
        );
    }
}