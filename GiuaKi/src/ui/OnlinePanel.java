package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

import java.util.List;
import java.util.function.Consumer;


public class OnlinePanel extends JPanel {

    private final String currentUsername;


    private final DefaultListModel<String> onlineModel =
            new DefaultListModel<>();


    private final JList<String> onlineList =
            new JList<>(
                    onlineModel
            );


    private final JLabel lblTitle =
            new JLabel(
                    "Đang online"
            );


    private Consumer<String> userSelectedListener;

    private boolean updating =
            false;


    public OnlinePanel(
            String currentUsername) {

        this.currentUsername =
                currentUsername;


        setLayout(
                new BorderLayout()
        );


        setPreferredSize(
                new Dimension(
                        210,
                        0
                )
        );


        setBackground(
                new Color(
                        248,
                        248,
                        248
                )
        );


        setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        0,
                        1,
                        new Color(
                                225,
                                225,
                                225
                        )
                )
        );


        lblTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );


        lblTitle.setBorder(
                new EmptyBorder(
                        20,
                        15,
                        15,
                        10
                )
        );


        add(
                lblTitle,
                BorderLayout.NORTH
        );


        onlineList.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );


        onlineList.setFixedCellHeight(
                48
        );


        onlineList.setBorder(
                new EmptyBorder(
                        5,
                        8,
                        5,
                        8
                )
        );


        onlineList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        onlineList.setCellRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus) {

                        JLabel label =
                                (JLabel)
                                        super
                                                .getListCellRendererComponent(
                                                        list,
                                                        value,
                                                        index,
                                                        isSelected,
                                                        cellHasFocus
                                                );


                        String name =
                                String.valueOf(
                                        value
                                );


                        label.setText(
                                "<html>"
                                        + "<font color='#25B84A'>●</font>"
                                        + "&nbsp;&nbsp;"
                                        + name
                                        + "</html>"
                        );


                        label.setBorder(
                                new EmptyBorder(
                                        5,
                                        8,
                                        5,
                                        8
                                )
                        );


                        return label;
                    }
                }
        );


        onlineList.addListSelectionListener(
                e -> {

                    if (updating
                            || e.getValueIsAdjusting()) {

                        return;
                    }


                    String user =
                            onlineList
                                    .getSelectedValue();


                    if (user != null
                            && userSelectedListener
                            != null) {

                        userSelectedListener.accept(
                                user
                        );
                    }
                }
        );


        JScrollPane scrollPane =
                new JScrollPane(
                        onlineList
                );


        scrollPane.setBorder(
                null
        );


        add(
                scrollPane,
                BorderLayout.CENTER
        );
    }


    public void setUserSelectedListener(
            Consumer<String> listener) {

        userSelectedListener =
                listener;
    }


    public void updateUsers(
            List<String> users) {

        updating =
                true;


        onlineModel.clear();


        for (String user : users) {

            if (!user.equals(
                    currentUsername
            )) {

                onlineModel.addElement(
                        user
                );
            }
        }


        lblTitle.setText(
                "Đang online ("
                        + onlineModel.size()
                        + ")"
        );


        updating =
                false;
    }


    public void selectUser(
            String user) {

        if (user == null) {

            return;
        }


        updating =
                true;


        onlineList.setSelectedValue(
                user,
                true
        );


        updating =
                false;
    }


    public void clearSelection() {

        updating =
                true;


        onlineList.clearSelection();


        updating =
                false;
    }
}