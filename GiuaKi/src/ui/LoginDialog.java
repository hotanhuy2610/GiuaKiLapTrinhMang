package ui;

import javax.swing.*;

import java.awt.*;


public class LoginDialog {

    private String username;

    private String serverIP;


    public boolean showDialog(
            Component parent) {

        JTextField txtUsername =
                new JTextField();


        JTextField txtIP =
                new JTextField();


        JPanel panel =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                10,
                                10
                        )
                );


        panel.add(
                new JLabel("Tên:")
        );


        panel.add(
                txtUsername
        );


        panel.add(
                new JLabel("IP Server:")
        );


        panel.add(
                txtIP
        );


        int result =
                JOptionPane.showConfirmDialog(
                        parent,
                        panel,
                        "Kết nối Chat",
                        JOptionPane.OK_CANCEL_OPTION
                );


        if (result !=
                JOptionPane.OK_OPTION) {

            return false;
        }


        username =
                txtUsername
                        .getText()
                        .trim();


        serverIP =
                txtIP
                        .getText()
                        .trim();


        if (username.isEmpty()
                || serverIP.isEmpty()) {

            JOptionPane.showMessageDialog(
                    parent,
                    "Vui lòng nhập đầy đủ!"
            );


            return false;
        }


        return true;
    }


    public String getUsername() {

        return username;
    }


    public String getServerIP() {

        return serverIP;
    }
}