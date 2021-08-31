package com.company;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class WorkersScreen extends JFrame {
    private JPanel rootPanel5;
    private JList list1;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField wageField;
    private JTextField phoneField;
    private JTextField mailField;
    private JTextField addressField;
    private JTextField birthdateField;
    private JButton addButton;
    private JButton removeButton;
    private JTextField textField1;
    private JButton muhasebeciOlarakEkleButton;

    private DefaultListModel model = new DefaultListModel();

    private static int calisanID;
    private Random rand = new Random();
    private ArrayList<String> personelNames = new ArrayList<>();

    int tempMonth;

    WorkersScreen() {
        add(rootPanel5);
        setTitle("Çalışanlar");
        setSize(550,500);
        setResizable(false);
        setLocationRelativeTo(null);

        updateList();

        try {
            Connection con = DBConnect.getConnection();

            assert con != null;
            PreparedStatement statement = con.prepareStatement("SELECT MAX(calisanid) calisanid FROM calisan");
            ResultSet result = statement.executeQuery();

            if(result.first())
                calisanID = result.getInt("calisanid") + 1;
            else calisanID = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        addButton.addActionListener(actionEvent -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            int wage = Integer.parseInt(wageField.getText());
            String phoneNumber = phoneField.getText();
            String email = mailField.getText();
            String address = addressField.getText();
            String birthday = birthdateField.getText();

            String adresIDOfPersonel = "";

            String year = "" + Calendar.getInstance().get(Calendar.YEAR);
            String month = "" + Calendar.getInstance().get(Calendar.MONTH);
            tempMonth = Integer.parseInt(month);
            tempMonth++;
            month = "" + tempMonth;
            String day = "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            int adresId;

            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement statement = con.prepareStatement("SELECT adi, soyadi FROM calisan ");
                ResultSet result = statement.executeQuery();

                personelNames.clear();
                while(result.next()) {
                    personelNames.add(result.getString("adi") + " " + result.getString("soyadi"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(birthday.substring(0,4)) >= 18 && wage >= 2000) {
                if (!personelNames.contains(firstName + " " + lastName)) {
                    try {
                        Connection con = DBConnect.getConnection();

                        adresId = rand.nextInt(8999) + 1000;

                        assert con != null;
                        PreparedStatement postedAdres = con.prepareStatement("INSERT INTO adres (adresid, adres) VALUES ('" + adresId + "', '" + address + "')");
                        postedAdres.executeUpdate();
                        PreparedStatement postedCalisan = con.prepareStatement("INSERT INTO calisan (calisanid, adi, soyadi, baslamatarihi, maas, telno, mailadres, adresid, muhasebecitelno, dogumtarihi) VALUES ('" +
                                calisanID + "', '" + firstName + "', '" + lastName + "', '" + year + "-" + month + "-" + day + "', '" + wage + "', '" + phoneNumber + "', '" + email + "', '" + adresId + "', NULL, '" +
                                birthday + "')");
                        postedCalisan.executeUpdate();
                        calisanID++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String[] options = {"Hayır, yeni ekle", "Evet, güncelle"};
                    int response = JOptionPane.showOptionDialog(null, "Bilgileri güncellemek ister misiniz?",
                            "Uyarı", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (response == 0) {
                        try {
                            Connection con = DBConnect.getConnection();

                            adresId = rand.nextInt(8999) + 1000;

                            assert con != null;
                            PreparedStatement postedAdres = con.prepareStatement("INSERT INTO adres (adresid, adres) VALUES ('" + adresId + "', '" + address + "')");
                            postedAdres.executeUpdate();
                            PreparedStatement postedCalisan = con.prepareStatement("INSERT INTO calisan (calisanid, adi, soyadi, baslamatarihi, maas, telno, mailadres, adresid, muhasebecitelno, dogumtarihi) VALUES ('" +
                                    calisanID + "', '" + firstName + "', '" + lastName + "', '" + year + "-" + month + "-" + day + "', '" + wage + "', '" + phoneNumber + "', '" + email + "', '" + adresId + "', NULL, '" +
                                    birthday + "')");
                            postedCalisan.executeUpdate();
                            calisanID++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        String input = JOptionPane.showInputDialog(null, "Çalışan ID");
                        try {
                            Connection con = DBConnect.getConnection();

                            assert con != null;
                            PreparedStatement statement = con.prepareStatement("SELECT adresid FROM calisan WHERE calisanid = '" + input + "'");
                            ResultSet result = statement.executeQuery();

                            while (result.next()) {
                                adresIDOfPersonel = result.getString("adresid");
                            }

                            PreparedStatement postedAdres = con.prepareStatement("UPDATE adres SET adres = '" + address + "' WHERE adresid = '" + adresIDOfPersonel + "'");
                            postedAdres.executeUpdate();
                            PreparedStatement postedCalisan = con.prepareStatement("UPDATE calisan SET maas = '" + wage + "', telno = '" + phoneNumber + "', mailadres = '" + email +
                                    "' WHERE calisanid = '" + input + "'");
                            postedCalisan.executeUpdate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                model.clear();
                updateList();
                firstNameField.setText("");
                lastNameField.setText("");
                wageField.setText("");
                phoneField.setText("");
                mailField.setText("");
                addressField.setText("");
                birthdateField.setText("");
            } else JOptionPane.showMessageDialog(null,"Girilen bilgileri kontrol ediniz!", "HATA!", JOptionPane.INFORMATION_MESSAGE);
        });

        removeButton.addActionListener(actionEvent -> {
            String calisanIDCheck = textField1.getText();
            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement posted = con.prepareStatement("DELETE FROM calisan WHERE calisanid = " + Integer.parseInt(calisanIDCheck));
                posted.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            model.clear();
            updateList();
            textField1.setText("");
        });

        muhasebeciOlarakEkleButton.addActionListener(actionEvent -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            int wage = Integer.parseInt(wageField.getText());
            String phoneNumber = phoneField.getText();
            String email = mailField.getText();
            String address = addressField.getText();
            String birthday = birthdateField.getText();

            String year = "" + Calendar.getInstance().get(Calendar.YEAR);
            String month = "" + Calendar.getInstance().get(Calendar.MONTH);
            tempMonth = Integer.parseInt(month);
            tempMonth++;
            month = "" + tempMonth;
            String day = "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            int adresId;

            try {
                Connection con = DBConnect.getConnection();

                adresId = rand.nextInt(8999) + 1000;

                assert con != null;
                PreparedStatement postedAdres = con.prepareStatement("INSERT INTO adres (adresid, adres) VALUES ('" + adresId + "', '" + address + "')");
                postedAdres.executeUpdate();
                PreparedStatement postedCalisan = con.prepareStatement("INSERT INTO calisan (calisanid, adi, soyadi, baslamatarihi, maas, telno, mailadres, adresid, muhasebecitelno, dogumtarihi) VALUES ('" +
                        calisanID + "', '" + firstName + "', '" + lastName + "', '" + year + "-" + month + "-" + day + "', '" + wage + "', '" + phoneNumber + "', '" + email + "', '" + adresId + "', NULL, '" +
                        birthday + "')");
                postedCalisan.executeUpdate();
                calisanID++;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement posted = con.prepareStatement("INSERT INTO muhasebeci (ad, soyad, email) VALUES ('" + firstName + "', '" + lastName + "', '" + email + "')");
                posted.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            model.clear();
            updateList();
            firstNameField.setText("");
            lastNameField.setText("");
            wageField.setText("");
            phoneField.setText("");
            mailField.setText("");
            addressField.setText("");
            birthdateField.setText("");
        });
    }

    void updateList() {
        try {
            Connection con = DBConnect.getConnection();

            assert con != null;
            PreparedStatement statement = con.prepareStatement("SELECT adi, soyadi FROM calisan ORDER BY adi ASC");
            ResultSet result = statement.executeQuery();

            while(result.next()) {
                model.addElement(result.getString("adi") + " " + result.getString("soyadi"));
                list1.setModel(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}