package com.company;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UpdateProductScreen extends JFrame {
    private JTextField nameField;
    private JTextField purchaseField;
    private JTextField sttField;
    private JTextField sellingField;
    private JPanel rootPanel7;
    private JTextField amountField;
    private JButton updateButton;

    private ArrayList<String> productNames = new ArrayList<>();

    UpdateProductScreen() {
        add(rootPanel7);
        setTitle("Ürün Güncelle");
        setSize(600,200);
        setResizable(false);
        setLocationRelativeTo(null);

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

        AutoCompleteDecorator.decorate(nameField, productNames, false);

        updateButton.addActionListener(actionEvent -> {
            int purchasePrice = Integer.parseInt(purchaseField.getText());
            int sellingPrice = Integer.parseInt(sellingField.getText());
            String stt = sttField.getText();
            int amount = Integer.parseInt(amountField.getText());

            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                if(!stt.equals("")) {
                    PreparedStatement statement = con.prepareStatement("UPDATE urunler SET alisfiyati = '" + purchasePrice + "', satisfiyati = '" + sellingPrice +
                            "', sontuketimtarihi = '" + stt + "', stokmiktari = '" + amount + "'");
                    statement.executeUpdate();
                } else {
                    PreparedStatement statement = con.prepareStatement("UPDATE urunler SET alisfiyati = '" + purchasePrice + "', satisfiyati = '" + sellingPrice +
                            "', sontuketimtarihi = NULL, stokmiktari = '" + amount + "'");
                    statement.executeUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}