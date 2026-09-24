package ui;

import javax.swing.*;

import java.awt.*;


public class TypingIndicator
        extends JPanel {

    private final JLabel label =
            new JLabel(
                    ""
            );


    private final Timer animationTimer;

    private final Timer safetyTimer;


    private int dotCount =
            1;


    private String username =
            "";


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public TypingIndicator() {

        setLayout(
                new FlowLayout(
                        FlowLayout.LEFT,
                        0,
                        0
                )
        );


        setOpaque(
                false
        );


        label.setFont(
                new Font(
                        "Arial",
                        Font.ITALIC,
                        11
                )
        );


        label.setForeground(
                new Color(
                        110,
                        110,
                        110
                )
        );


        add(
                label
        );


        setVisible(
                false
        );


        // =========================================
        // .  ..  ...
        // =========================================

        animationTimer =
                new Timer(
                        400,
                        e -> {

                            dotCount++;


                            if (dotCount > 3) {

                                dotCount =
                                        1;
                            }


                            updateText();
                        }
                );


        // =========================================
        // Nếu UDP STOP bị mất
        // thì sau 2.3 giây tự ẩn
        // =========================================

        safetyTimer =
                new Timer(
                        4000,
                        e ->
                                hideTyping()
                );


        safetyTimer.setRepeats(
                false
        );
    }


    // =====================================================
    // SHOW
    // =====================================================

    public void showTyping(
            String username) {

        this.username =
                username;


        dotCount =
                1;


        updateText();


        setVisible(
                true
        );


        if (!animationTimer.isRunning()) {

            animationTimer.start();
        }


        // Mỗi TYPING=true mới tới
        // sẽ reset timeout
        safetyTimer.restart();
    }


    // =====================================================
    // HIDE
    // =====================================================

    public void hideTyping() {

        animationTimer.stop();

        safetyTimer.stop();


        label.setText(
                ""
        );


        setVisible(
                false
        );
    }


    // =====================================================
    // UPDATE
    // =====================================================

    private void updateText() {

        String dots =
                ".".repeat(
                        dotCount
                );


        label.setText(
                username
                        + " đang soạn tin"
                        + dots
        );
    }
}