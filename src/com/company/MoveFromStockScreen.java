package com.company;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MoveFromStockScreen extends JFrame {

    private JTextField textField1;
    private JPanel rootPanel8;
    private JTextField textField2;
    private JButton getirButton;
    String maxAmount;

    private ArrayList<String> productNames = new ArrayList<>();

    MoveFromStockScreen() {
        add(rootPanel8);
        setTitle("Stoktan Getir");
        setSize(500,200);
        setResizable(false);
        setLocationRelativeTo(null);

        getirButton.addActionListener(actionEvent -> {
            int amount = Integer.parseInt(textField2.getText());

            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement statement = con.prepareStatement("SELECT urunadi FROM urunler");
                ResultSet result = statement.executeQuery();

                productNames.clear();
                while(result.next()) {
                    productNames.add(result.getString("urunadi"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            AutoCompleteDecorator.decorate(textField1, productNames, false);

            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement statement = con.prepareStatement("SELECT stokmiktari FROM urunler WHERE urunadi = '" + textField1.getText() + "'");
                ResultSet result = statement.executeQuery();

                while(result.next()) {
                    maxAmount = result.getString("stokmiktari");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(Integer.parseInt(textField2.getText()) > Integer.parseInt(maxAmount)) {
                JOptionPane.showMessageDialog(null,"Miktar aşıldı! Stoktaki miktar: " + maxAmount, "HATA!", JOptionPane.INFORMATION_MESSAGE);
                textField2.setText("");
            } else {
                int yeniStok = Integer.parseInt(maxAmount) - amount;

                try {
                    Connection con = DBConnect.getConnection();

                    assert con != null;
                    PreparedStatement posted = con.prepareStatement("UPDATE urunler SET stokmiktari = '" + yeniStok + "', reyonmiktari = '" + amount + "' WHERE urunadi = '" + textField1.getText() + "'");
                    posted.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}