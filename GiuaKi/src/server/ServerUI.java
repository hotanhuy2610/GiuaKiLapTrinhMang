package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class ServerUI extends JFrame {

    private final JTextArea txtLog =
            new JTextArea();

    private final JLabel lblStatus =
            new JLabel(
                    "● Server đang chạy"
            );

    private final JLabel lblLastIP =
            new JLabel(
                    "IP truy cập gần nhất: Chưa có"
            );

    private final JLabel lblClientCount =
            new JLabel(
                    "Client đang kết nối: 0"
            );


    private final DateTimeFormatter timeFormat =
            DateTimeFormatter.ofPattern(
                    "HH:mm:ss"
            );


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ServerUI() {

        setTitle(
                "Chat Server"
        );


        setSize(
                650,
                500
        );


        setLocationRelativeTo(
                null
        );


        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );


        setLayout(
                new BorderLayout()
        );


        createHeader();

        createLogArea();


        setVisible(
                true
        );
    }


    // =====================================================
    // HEADER
    // =====================================================

    private void createHeader() {

        JPanel header =
                new JPanel();


        header.setLayout(
                new BoxLayout(
                        header,
                        BoxLayout.Y_AXIS
                )
        );


        header.setBackground(
                Color.WHITE
        );


        header.setBorder(
                new EmptyBorder(
                        18,
                        20,
                        18,
                        20
                )
        );


        JLabel lblTitle =
                new JLabel(
                        "CHAT SERVER"
                );


        lblTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );


        lblTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        lblStatus.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );


        lblStatus.setForeground(
                new Color(
                        0,
                        150,
                        70
                )
        );


        lblStatus.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        JLabel lblPort =
                new JLabel(
                        "TCP Port: "
                                + TCPServer.TCP_PORT
                                + "     |     UDP Port: "
                                + TCPServer.UDP_PORT
                );


        lblPort.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );


        lblPort.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        lblLastIP.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );


        lblLastIP.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        lblClientCount.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );


        lblClientCount.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        header.add(
                lblTitle
        );


        header.add(
                Box.createVerticalStrut(
                        10
                )
        );


        header.add(
                lblStatus
        );


        header.add(
                Box.createVerticalStrut(
                        8
                )
        );


        header.add(
                lblPort
        );


        header.add(
                Box.createVerticalStrut(
                        8
                )
        );


        header.add(
                lblLastIP
        );


        header.add(
                Box.createVerticalStrut(
                        8
                )
        );


        header.add(
                lblClientCount
        );


        add(
                header,
                BorderLayout.NORTH
        );
    }


    // =====================================================
    // LOG
    // =====================================================

    private void createLogArea() {

        JPanel center =
                new JPanel(
                        new BorderLayout()
                );


        center.setBorder(
                new EmptyBorder(
                        10,
                        20,
                        20,
                        20
                )
        );


        JLabel lblLog =
                new JLabel(
                        "Nhật ký hoạt động"
                );


        lblLog.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );


        lblLog.setBorder(
                new EmptyBorder(
                        0,
                        0,
                        8,
                        0
                )
        );


        txtLog.setEditable(
                false
        );


        txtLog.setFont(
                new Font(
                        "Consolas",
                        Font.PLAIN,
                        14
                )
        );


        txtLog.setLineWrap(
                true
        );


        txtLog.setWrapStyleWord(
                true
        );


        JScrollPane scrollPane =
                new JScrollPane(
                        txtLog
                );


        center.add(
                lblLog,
                BorderLayout.NORTH
        );


        center.add(
                scrollPane,
                BorderLayout.CENTER
        );


        add(
                center,
                BorderLayout.CENTER
        );
    }


    // =====================================================
    // CLIENT MỚI TRUY CẬP
    // =====================================================

    public void clientAccess(
            String ip) {

        SwingUtilities.invokeLater(
                () -> {

                    lblLastIP.setText(
                            "IP vừa truy cập: "
                                    + ip
                    );


                }
        );
    }


    // =====================================================
    // CLIENT LOGIN
    // =====================================================

    public void clientLogin(
            String username,
            String ip) {

        addLog(
                username
                        + " đã đăng nhập - IP: "
                        + ip
        );
    }


    // =====================================================
    // CLIENT LOGOUT
    // =====================================================

    public void clientLogout(
            String username) {

        addLog(
                username
                        + " đã ngắt kết nối."
        );
    }


    // =====================================================
    // SỐ CLIENT
    // =====================================================

    public void updateClientCount(
            int count) {

        SwingUtilities.invokeLater(
                () ->
                        lblClientCount.setText(
                                "Client đang kết nối: "
                                        + count
                        )
        );
    }


    // =====================================================
    // ADD LOG
    // =====================================================

    public void addLog(
            String message) {

        SwingUtilities.invokeLater(
                () -> {

                    String time =
                            LocalTime
                                    .now()
                                    .format(
                                            timeFormat
                                    );


                    txtLog.append(
                            "["
                                    + time
                                    + "] "
                                    + message
                                    + "\n"
                    );


                    txtLog.setCaretPosition(
                            txtLog
                                    .getDocument()
                                    .getLength()
                    );
                }
        );
    }
}