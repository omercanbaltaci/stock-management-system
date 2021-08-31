package com.company;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

public class BarcodeScreen extends JFrame {
    private JPanel rootPanel9;
    private JButton satButton;
    private JLabel urunAdiLabel;
    private JTextField textField1;

    int tempMonth, price, totalPrice;
    String label;

    BarcodeScreen(String a) {
        add(rootPanel9);
        setTitle("Satış");
        setSize(300,150);
        setResizable(true);
        setLocationRelativeTo(null);

        try {
            Connection con = DBConnect.getConnection();

            assert con != null;
            PreparedStatement statement = con.prepareStatement("SELECT urunadi FROM urunler WHERE barkodno = '" + a + "'");
            ResultSet result = statement.executeQuery();

            while(result.next()) {
                label = result.getString("urunadi");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        urunAdiLabel.setText(label);

        satButton.addActionListener(actionEvent -> {
            try {
                Connection con = DBConnect.getConnection();

                int stokMiktari = 0;
                int reyonMiktari = 0;

                assert con != null;
                PreparedStatement statement1 = con.prepareStatement("SELECT stokmiktari FROM urunler WHERE barkodno = '" + a + "'");

                ResultSet rs1 = statement1.executeQuery();
                while(rs1.next()) {
                    stokMiktari = rs1.getInt("stokmiktari");
                }

                PreparedStatement statement2 = con.prepareStatement("SELECT reyonmiktari FROM urunler WHERE barkodno = '" + a + "'");

                ResultSet rs2 = statement2.executeQuery();
                while(rs2.next()) {
                    reyonMiktari = rs2.getInt("reyonmiktari");
                }

                if(Integer.parseInt(textField1.getText()) > reyonMiktari) {
                    reyonMiktari = 0;
                    stokMiktari = stokMiktari - (Integer.parseInt(textField1.getText()) - reyonMiktari);
                } else reyonMiktari = reyonMiktari - Integer.parseInt(textField1.getText());

                PreparedStatement posted = con.prepareStatement("UPDATE urunler SET stokmiktari = '" + stokMiktari + "' WHERE barkodno = '" + a + "'");
                posted.executeUpdate();
                posted = con.prepareStatement("UPDATE urunler SET reyonMiktari = '" + reyonMiktari + "' WHERE barkodno = '" + a + "'");
                posted.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }

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

                assert con != null;
                PreparedStatement statement = con.prepareStatement("SELECT satisfiyati FROM urunler WHERE barkodno = '" + a + "'");
                ResultSet result = statement.executeQuery();

                while(result.next()) {
                    price = result.getInt("satisfiyati");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            totalPrice = price * Integer.parseInt(textField1.getText());
            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement posted = con.prepareStatement("INSERT INTO alissatis (tarih, alis, satis) VALUES ('" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + "', NULL, '" + totalPrice + "')");
                posted.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}