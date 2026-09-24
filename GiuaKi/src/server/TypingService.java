package server;

public class TypingService {

    private final UDPOnlineServer udpServer;


    public TypingService(
            UDPOnlineServer udpServer) {

        this.udpServer =
                udpServer;
    }


    // =====================================================
    // XỬ LÝ TRẠNG THÁI ĐANG SOẠN TIN
    // =====================================================

    public void handleTyping(
            String sender,
            String target,
            boolean typing) {

        // Server gửi cho người nhận:
        //
        // TYPING|Huy|1
        // TYPING|Huy|0

        String message =
                "TYPING|"
                        + sender
                        + "|"
                        + (typing ? "1" : "0");


        udpServer.sendToUser(
                target,
                message
        );
    }
}