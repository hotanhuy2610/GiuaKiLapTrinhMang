package client;

import javax.swing.Timer;


public class TypingClientService {

    private final UDPOnlineClient udpClient;


    private String currentTarget;


    private boolean typing =
            false;


    // Sau khoảng 1.2 giây không gõ
    // sẽ gửi trạng thái ngừng soạn
    private final Timer stopTimer;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public TypingClientService(
            UDPOnlineClient udpClient) {

        this.udpClient =
                udpClient;


        stopTimer =
                new Timer(
                        2000,
                        e ->
                                stopTyping()
                );


        stopTimer.setRepeats(
                false
        );
    }


    // =====================================================
    // Ô NHẬP THAY ĐỔI
    // =====================================================

    public void onTextChanged(
            String target,
            String text) {

        // Chưa chọn người chat
        if (target == null) {

            stopTyping();

            return;
        }


        // Ô nhập rỗng
        if (text == null
                || text.isBlank()) {

            stopTyping();

            return;
        }


        // Nếu chuyển sang người khác
        if (currentTarget != null
                && !currentTarget.equals(
                target
        )) {

            udpClient.sendTyping(
                    currentTarget,
                    false
            );
        }


        currentTarget =
                target;


        typing =
                true;


        // Gửi TRUE mỗi lần gõ.
        // UDP có thể mất packet nên gửi lại là hợp lý.
        udpClient.sendTyping(
                target,
                true
        );


        // Gõ tiếp -> reset 1.2 giây
        stopTimer.restart();
    }


    // =====================================================
    // NGỪNG SOẠN
    // =====================================================

    public void stopTyping() {

        if (!typing) {

            return;
        }


        if (currentTarget != null) {

            udpClient.sendTyping(
                    currentTarget,
                    false
            );
        }


        typing =
                false;


        currentTarget =
                null;


        stopTimer.stop();
    }
}