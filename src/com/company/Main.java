package com.company;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main {

    public static void main(String[] args) throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        DBConnect.getConnection();

        ArrayList<String> barcodes = new ArrayList<>();

        Runtime.getRuntime().exec(new String[]{"cmd", "/c","start chrome http://localhost:33333/"});

        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });

        while(true) {
            Thread.sleep(3000);
            try {
                Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

                String path = "C:/ProgramData/TEC-IT/Scan-IT to Office Tools/SDBC_Sample.mdb";
                String url = "jdbc:ucanaccess://" + path;

                Connection con = DriverManager.getConnection(url);

                try {
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT BarcodeData FROM SDBC_Sample");

                    barcodes.clear();
                    while(rs.next()) {
                        barcodes.add(rs.getString("BarcodeData"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            while(!barcodes.isEmpty()) {
                BarcodeScreen barcodeScreen = new BarcodeScreen(barcodes.get(0));
                barcodeScreen.setVisible(true);
                barcodes.clear();
                Thread.sleep(10000);

                try {
                    Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

                    String path = "C:/ProgramData/TEC-IT/Scan-IT to Office Tools/SDBC_Sample.mdb";
                    String url = "jdbc:ucanaccess://" + path;

                    Connection con = DriverManager.getConnection(url);

                    try {
                        Statement st = con.createStatement();
                        st.executeUpdate("DELETE FROM SDBC_Sample");

                        barcodes.clear();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}