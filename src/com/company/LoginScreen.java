package com.company;

import javax.swing.*;

public class LoginScreen extends JFrame {
    private JPanel rootPanel;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton enterButton;

    private static MainScreen mainScreen = new MainScreen();

    private String ID = "yonetici";
    private String PASS = "123456";

    LoginScreen() {
        add(rootPanel);
        setTitle("Giriş");
        setSize(300,300);
        setResizable(false);
        setLocationRelativeTo(null);

        enterButton.addActionListener(actionEvent -> {
            if(textField1.getText().equals(ID) && String.valueOf(passwordField1.getPassword()).equals(PASS)) {
                dispose();
                mainScreen.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,"Yanlış kullanıcı adı veya şifre!", "HATA!", JOptionPane.INFORMATION_MESSAGE);
                textField1.setText("");
                passwordField1.setText("");
            }
        });
    }
}