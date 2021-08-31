package com.company;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

public class SellProductScreen extends JFrame {

    private JPanel rootPanel4;
    private JTextField nameField;
    private JButton satButton;
    private JTextField amountField;

    private ArrayList<String> productNames = new ArrayList<>();

    int price = 0;
    int totalPrice = 0;
    int tempMonth;

    SellProductScreen() {
        add(rootPanel4);
        setTitle("Ürün Sat");
        setSize(500,150);
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

        satButton.addActionListener(actionEvent -> {
            String productName = nameField.getText();
            String productAmount = amountField.getText();

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

            if(!productNames.contains(productName))
                JOptionPane.showMessageDialog(null,"Ürün Bulunamadı!", "HATA!", JOptionPane.INFORMATION_MESSAGE);
            else {
                String year = "" + Calendar.getInstance().get(Calendar.YEAR);
                String month = "" + Calendar.getInstance().get(Calendar.MONTH);
                tempMonth = Integer.parseInt(month);
                tempMonth++;
                month = "" + tempMonth;
                String day = "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                String hour = "" + Calendar.getInstance().get(Calendar.HOUR);
                String minute = "" + Calendar.getInstance().get(Calendar.MINUTE);
                String second = "" + Calendar.getInstance().get(Calendar.SECOND);

                try {
                    Connection con = DBConnect.getConnection();

                    int stokMiktari = 0;
                    int reyonMiktari = 0;

                    assert con != null;
                    PreparedStatement statement1 = con.prepareStatement("SELECT stokmiktari FROM urunler WHERE urunadi = '" + productName + "'");

                    ResultSet rs1 = statement1.executeQuery();
                    while(rs1.next()) {
                        stokMiktari = rs1.getInt("stokmiktari");
                    }

                    PreparedStatement statement2 = con.prepareStatement("SELECT reyonmiktari FROM urunler WHERE urunadi = '" + productName + "'");

                    ResultSet rs2 = statement2.executeQuery();
                    while(rs2.next()) {
                        reyonMiktari = rs2.getInt("reyonmiktari");
                    }

                    if(Integer.parseInt(productAmount) > reyonMiktari) {
                        reyonMiktari = 0;
                        stokMiktari = stokMiktari - (Integer.parseInt(productAmount) - reyonMiktari);
                    } else reyonMiktari = reyonMiktari - Integer.parseInt(productAmount);

                    PreparedStatement posted = con.prepareStatement("UPDATE urunler SET stokmiktari = '" + stokMiktari + "' WHERE urunadi = '" + productName + "'");
                    posted.executeUpdate();
                    posted = con.prepareStatement("UPDATE urunler SET reyonMiktari = '" + reyonMiktari + "' WHERE urunadi = '" + productName + "'");
                    posted.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Connection con = DBConnect.getConnection();

                    assert con != null;
                    PreparedStatement statement = con.prepareStatement("SELECT satisfiyati FROM urunler WHERE urunadi = '" + productName + "'");
                    ResultSet result = statement.executeQuery();

                    while(result.next()) {
                        price = result.getInt("satisfiyati");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalPrice = price * Integer.parseInt(productAmount);
                try {
                    Connection con = DBConnect.getConnection();

                    assert con != null;
                    PreparedStatement posted = con.prepareStatement("INSERT INTO alissatis (tarih, alis, satis) VALUES ('" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + "', NULL, '" + totalPrice + "')");
                    posted.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            nameField.setText("");
            amountField.setText("");
        });
    }
}