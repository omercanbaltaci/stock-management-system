package com.company;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class AddProductScreen extends JFrame {
    private JPanel rootPanel3;
    private JTextField productNameField;
    private JTextField sttField;
    private JTextField purchaseField;
    private JTextField sellingField;
    private JTextField amountField;
    private JButton addProductButton;
    private JRadioButton kiloRadioButton;
    private JRadioButton adetRadioButton;

    private ArrayList<String> productNames = new ArrayList<>();

    private Random rand = new Random();

    int tempMonth;

    AddProductScreen() {
        add(rootPanel3);
        setTitle("Ürün Ekle");
        setSize(600,400);
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
        AutoCompleteDecorator.decorate(productNameField, productNames, false);

        addProductButton.addActionListener(actionEvent -> {
            String urunAdi;
            String sonTuketimTarihi;
            String alisFiyati;
            String satisFiyati;
            String miktar;
            String tip = null;

            int randBarcode = rand.nextInt(899999) + 100000;

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

            if(!productNames.contains(productNameField.getText())) {
                urunAdi = productNameField.getText();
                sonTuketimTarihi = sttField.getText();
                alisFiyati = purchaseField.getText();
                satisFiyati = sellingField.getText();
                miktar = amountField.getText();

                String year = "" + Calendar.getInstance().get(Calendar.YEAR);
                String month = "" + Calendar.getInstance().get(Calendar.MONTH);
                tempMonth = Integer.parseInt(month);
                tempMonth++;
                month = "" + tempMonth;
                String day = "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                String hour = "" + Calendar.getInstance().get(Calendar.HOUR);
                String minute = "" + Calendar.getInstance().get(Calendar.MINUTE);
                String second = "" + Calendar.getInstance().get(Calendar.SECOND);

                final String QR_CODE_IMAGE_PATH = "./" + urunAdi + ".png";

                if(kiloRadioButton.isSelected())
                    tip = "kilo";
                else if(adetRadioButton.isSelected()) {
                    tip = "adet";
                    try {
                        generateQRCodeImage("" + randBarcode, 350, 350, QR_CODE_IMAGE_PATH);
                    } catch (WriterException | IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Connection con = DBConnect.getConnection();

                    assert con != null;
                    if(sttField.getText().equals("")) {
                        PreparedStatement posted = con.prepareStatement("INSERT INTO urunler (barkodno, urunadi, sontuketimtarihi, raftarihi, alisfiyati, satisfiyati, stokmiktari, reyonmiktari, " +
                                "stokeklenmetarihi, tip) VALUES ('" + "" + randBarcode + "', '" + urunAdi + "', NULL, " + "NULL, '"+ alisFiyati + "', '" + satisFiyati + "', '" + miktar +
                                "', NULL, '" + year + "-" + month + "-" + day + "', '" + tip + "')");
                        posted.executeUpdate();
                    }
                    else {
                        PreparedStatement posted = con.prepareStatement("INSERT INTO urunler (barkodno, urunadi, " + "sontuketimtarihi, raftarihi, alisfiyati, satisfiyati, stokmiktari, reyonmiktari, " +
                                "stokeklenmetarihi, tip) VALUES ('" + randBarcode + "', '" + urunAdi + "', '" + sonTuketimTarihi + "', NULL, '"+ alisFiyati + "', '" + satisFiyati + "', '" + miktar +
                                "', NULL, '" + year + "-" + month + "-" + day + "', '" + tip + "')");
                        posted.executeUpdate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Connection con = DBConnect.getConnection();

                    assert con != null;
                    PreparedStatement posted = con.prepareStatement("INSERT INTO alissatis (tarih, alis, satis) VALUES ('" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + "', '" +
                            Integer.parseInt(alisFiyati) * Integer.parseInt(miktar) + "', NULL)");
                    posted.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else {
                urunAdi = productNameField.getText();
                alisFiyati = purchaseField.getText();
                satisFiyati = sellingField.getText();
                miktar = amountField.getText();

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

                    assert con != null;
                    PreparedStatement statement = con.prepareStatement("SELECT stokmiktari FROM urunler WHERE urunadi = '" + urunAdi + "'");

                    ResultSet rs = statement.executeQuery();
                    while(rs.next()) {
                        String abc = rs.getString("stokmiktari");
                        stokMiktari = Integer.parseInt(abc);
                    }

                    int yeniMiktar = stokMiktari + Integer.parseInt(miktar);

                    PreparedStatement posted = con.prepareStatement("UPDATE urunler SET alisfiyati = '" + alisFiyati + "', satisfiyati = '" + satisFiyati + "', stokmiktari = '" + yeniMiktar +
                                "' WHERE urunadi = '" + urunAdi + "'");
                    posted.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Connection con = DBConnect.getConnection();

                    assert con != null;
                    PreparedStatement posted = con.prepareStatement("INSERT INTO alissatis (tarih, alis, satis, barkodno) VALUES ('" + year + "-" + month + "-" + day + " " + hour + ":" +
                            minute + ":" + second + "', '" + Integer.parseInt(alisFiyati) * Integer.parseInt(miktar) + "', NULL, " + randBarcode + ")");
                    posted.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dispose();
        });

        kiloRadioButton.addActionListener(actionEvent -> {
            adetRadioButton.setEnabled(false);
            if(!kiloRadioButton.isSelected())
                adetRadioButton.setEnabled(true);
        });
        adetRadioButton.addActionListener(actionEvent -> {
            kiloRadioButton.setEnabled(false);
            if(!adetRadioButton.isSelected())
                kiloRadioButton.setEnabled(true);
        });
    }

    private static void generateQRCodeImage(String text, int w, int h, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, w, h);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
}