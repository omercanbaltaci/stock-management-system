package com.company;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com. twilio.type.PhoneNumber;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import javax.mail.*;

public class MainScreen extends JFrame {
    private JPanel rootPanel2;
    private JButton urunEkleButton;
    private DefaultTableModel model = new DefaultTableModel();
    private JTable productTable;
    private JButton reloadTable;
    private JButton urunSatButton;
    private JButton workersButton;
    private JButton stockButton;
    private JButton updateProductButton;
    private JLabel mainLabel1;
    private JButton exportButton;
    private JButton mailGonderButton;

    JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

    private static AddProductScreen addProductScreen = new AddProductScreen();
    private static SellProductScreen sellProductScreen = new SellProductScreen();
    private static WorkersScreen workersScreen = new WorkersScreen();
    private static UpdateProductScreen updateProductScreen = new UpdateProductScreen();
    private static MoveFromStockScreen moveFromStockScreen = new MoveFromStockScreen();

    SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();

    private ArrayList<Integer> alislar = new ArrayList<>();
    private ArrayList<Integer> satislar = new ArrayList<>();
    private ArrayList<String> bitenUrunler = new ArrayList<>();
    String bitenUrunlerMessage;

    int alislarToplam = 0;
    int satislarToplam = 0;
    int placeHolder = 0;
    int tempMonth;

    public static final String ACCOUNT_SID = "ACc480008526b6c40d973321409e610cf5";
    public static final String AUTH_TOKEN = "8c0eb7e73739740dd5fa9de6cb0d9ea0";

    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String USERNAME = "omercanbaltaci905@gmail.com";
    private static final String PASSWORD = "c*V@d84_JR3Pp)v";

    private static final String EMAIL_FROM = "omercanbaltaci905@gmail.com";

    private static final String EMAIL_SUBJECT = "Belgeler";
    private static final String EMAIL_TEXT = "";

    MainScreen() {
        add(rootPanel2);
        setTitle("Stok Yönetim Sistemi");
        setSize(900,900);
        setResizable(true);
        setLocationRelativeTo(null);

        productTable.setAutoCreateRowSorter(true);
        updateTable();
        updateLabels();

        Properties prop = System.getProperties();
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", SMTP_SERVER);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "587");

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        MimeBodyPart p1 = new MimeBodyPart();

        urunEkleButton.addActionListener(actionEvent -> addProductScreen.setVisible(true));

        urunSatButton.addActionListener(actionEvent -> sellProductScreen.setVisible(true));

        reloadTable.addActionListener(actionEvent -> {
            model.setRowCount(0);
            model.setColumnCount(0);
            updateTable();
            mainLabel1.setText("");
            alislar.clear();
            satislar.clear();
            bitenUrunler.clear();
            updateLabels();

            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement statement = con.prepareStatement("SELECT urunadi FROM urunler WHERE stokmiktari = 0 ORDER BY urunadi ASC");
                ResultSet result = statement.executeQuery();

                while(result.next()) {
                    bitenUrunler.add(result.getString("urunadi"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(!bitenUrunler.isEmpty()) {
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                bitenUrunlerMessage = Arrays.toString(bitenUrunler.toArray()).replace("[", "").replace("]", "");
                Message message = Message.creator(new PhoneNumber("+905438981600"), new PhoneNumber("+13345106793"), "\n\nTükenmiş ürünler: \n" + bitenUrunlerMessage).create();
                System.out.println(message.getSid());
            }
        });

        workersButton.addActionListener(actionEvent -> workersScreen.setVisible(true));

        updateProductButton.addActionListener(actionEvent -> updateProductScreen.setVisible(true));

        stockButton.addActionListener(actionEvent -> moveFromStockScreen.setVisible(true));

        exportButton.addActionListener(actionEvent -> {
            String excelFilePath1 = "Ürün Dökümü.xlsx";
            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement statement = con.prepareStatement("SELECT * FROM urunler");
                ResultSet result = statement.executeQuery();

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("Ürün Dökümü");

                writeHeaderLineProducts(sheet);
                writeDataLinesProducts(result, workbook, sheet);

                FileOutputStream outputStream = new FileOutputStream(excelFilePath1);
                workbook.write(outputStream);
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String excelFilePath2 = "Çalışan Dökümü.xlsx";
            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement statement = con.prepareStatement("SELECT * FROM calisan");
                ResultSet result = statement.executeQuery();

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("Çalışan Dökümü");

                writeHeaderLineWorkers(sheet);
                writeDataLinesWorkers(result, workbook, sheet);

                FileOutputStream outputStream = new FileOutputStream(excelFilePath2);
                workbook.write(outputStream);
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String excelFilePath3 = "İstatistik Dökümü.xlsx";
            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement statement = con.prepareStatement("SELECT * FROM istatistik");
                ResultSet result = statement.executeQuery();

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("İstatistik Dökümü");

                writeHeaderLineStatistics(sheet);
                writeDataLinesStatistics(result, workbook, sheet);

                FileOutputStream outputStream = new FileOutputStream(excelFilePath3);
                workbook.write(outputStream);
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mailGonderButton.addActionListener(actionEvent -> {
            File[] files;
            j.setMultiSelectionEnabled(true);
            j.showOpenDialog(null);
            files = j.getSelectedFiles();

            String receiver = null;
            Multipart multipart = new MimeMultipart();

            try {
                Connection con = DBConnect.getConnection();

                assert con != null;
                PreparedStatement statement = con.prepareStatement("SELECT email FROM muhasebeci");
                ResultSet result = statement.executeQuery();

                while(result.next()) {
                    receiver = result.getString("email");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                msg.setFrom(new InternetAddress(EMAIL_FROM));
                assert receiver != null;
                msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(receiver, false));
                msg.setSubject(EMAIL_SUBJECT);
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(EMAIL_TEXT);
                assert false;
                multipart.addBodyPart(messageBodyPart);

                assert false;
                for(int i = 0; i <= files.length - 1; i++) {
                    p1.attachFile(files[i].getAbsolutePath());
                    assert false;
                    multipart.addBodyPart(p1);
                }

                msg.setContent(multipart);

                Transport.send(msg);
            } catch (MessagingException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    void updateTable() {
        model.addColumn("Ürün Adı");
        model.addColumn("Fiyatı");
        model.addColumn("Stok Miktarı");
        model.addColumn("Reyon Miktarı");
        try {
            Connection con = DBConnect.getConnection();

            assert con != null;
            PreparedStatement statement = con.prepareStatement("SELECT urunadi, satisfiyati, stokmiktari, reyonmiktari FROM urunler ORDER BY urunadi ASC");
            ResultSet result = statement.executeQuery();

            while(result.next()) {
                model.addRow(new Object[] {result.getString("urunadi"), "" + result.getInt("satisfiyati"), "" + result.getInt("stokmiktari"), "" + result.getInt("reyonmiktari")});
                productTable.setModel(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateLabels() {
        String year = "" + Calendar.getInstance().get(Calendar.YEAR);
        String month = "" + Calendar.getInstance().get(Calendar.MONTH);
        tempMonth = Integer.parseInt(month);
        tempMonth++;
        month = "" + tempMonth;
        String day = "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        placeHolder = Integer.parseInt(day);
        placeHolder++;

        try {
            Connection con = DBConnect.getConnection();

            assert con != null;
            PreparedStatement statement = con.prepareStatement("SELECT alis FROM alissatis WHERE tarih >= '" + year + "-" + month + "-" + day + "' AND tarih < '" + year + "-" + month + "-" + placeHolder + "'");
            ResultSet result = statement.executeQuery();

            while(result.next()) {
                alislar.add(result.getInt("alis"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Connection con = DBConnect.getConnection();

            assert con != null;
            PreparedStatement statement = con.prepareStatement("SELECT satis FROM alissatis WHERE tarih >= '" + year + "-" + month + "-" + day + "' AND tarih < '" + year + "-" + month + "-" + placeHolder + "'");
            ResultSet result = statement.executeQuery();

            while(result.next()) {
                satislar.add(result.getInt("satis"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        alislarToplam = sum(alislar);
        satislarToplam = sum(satislar);

        mainLabel1.setText("<html><center>" + formatter.format(date) + "<br/>ALIŞ: " + alislarToplam + " TL<br/>SATIŞ: " + satislarToplam + " TL</center></html>");
        mainLabel1.setFont(new Font(null, Font.BOLD, 25));
    }

    public static int sum(ArrayList<Integer> list) {
        int sum = 0;
        for(int i: list)
            sum += i;
        return sum;
    }

    private void writeHeaderLineProducts(XSSFSheet sheet) {

        Row headerRow = sheet.createRow(0);

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Barkod No");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("Ürün Adı");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("Alış Fiyatı");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("Satış Fiyatı");

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("Stok Miktarı");

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue("Reyon Miktarı");

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue("Tipi");

        headerCell = headerRow.createCell(7);
        headerCell.setCellValue("Son Tüketim Tarihi");

        headerCell = headerRow.createCell(8);
        headerCell.setCellValue("Reyona Konma Tarihi");

        headerCell = headerRow.createCell(9);
        headerCell.setCellValue("Stoğa Eklenme Tarihi");
    }

    private void writeDataLinesProducts(ResultSet result, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        int rowCount = 1;

        while(result.next()) {
            String barcodeNo = result.getString("barkodno");
            String productName = result.getString("urunadi");
            Date stt = result.getDate("sontuketimtarihi");
            Date raf = result.getDate("raftarihi");
            int buyingPrice = result.getInt("alisfiyati");
            int sellingPrice = result.getInt("satisfiyati");
            int amount = result.getInt("stokmiktari");
            int otherAmount = result.getInt("reyonmiktari");
            Date otherDate = result.getDate("stokeklenmetarihi");
            String type = result.getString("tip");

            Row row = sheet.createRow(rowCount++);

            int columnCount = 0;
            Cell cell = row.createCell(columnCount++);
            cell.setCellValue(barcodeNo);

            cell = row.createCell(columnCount++);
            cell.setCellValue(productName);

            cell = row.createCell(columnCount++);
            cell.setCellValue(buyingPrice);

            cell = row.createCell(columnCount++);
            cell.setCellValue(sellingPrice);

            cell = row.createCell(columnCount++);
            cell.setCellValue(amount);

            cell = row.createCell(columnCount++);
            cell.setCellValue(otherAmount);

            cell = row.createCell(columnCount++);
            cell.setCellValue(type);

            cell = row.createCell(columnCount++);

            CellStyle cellStyle = workbook.createCellStyle();
            CreationHelper creationHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));
            cell.setCellStyle(cellStyle);

            cell.setCellValue(stt);

            cell = row.createCell(columnCount++);
            cell.setCellValue(raf);

            cell = row.createCell(columnCount);
            cell.setCellValue(otherDate);
        }
    }

    private void writeHeaderLineWorkers(XSSFSheet sheet) {

        Row headerRow = sheet.createRow(0);

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Çalışan ID");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("Adı");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("Soyadı");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("İşe Başlama Tarihi");

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("Maaş");

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue("Telefon Numarası");

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue("Mail Adresi");

        headerCell = headerRow.createCell(7);
        headerCell.setCellValue("Doğum Tarihi");
    }

    private void writeDataLinesWorkers(ResultSet result, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        int rowCount = 1;

        while(result.next()) {
            int calisanID = result.getInt("calisanid");
            String name = result.getString("adi");
            String surname = result.getString("soyadi");
            Date baslamaTarihi = result.getDate("baslamatarihi");
            int maas = result.getInt("maas");
            String telNO = result.getString("telno");
            String mail = result.getString("mailadres");
            Date dogumTarihi = result.getDate("dogumtarihi");

            Row row = sheet.createRow(rowCount++);

            int columnCount = 0;
            Cell cell = row.createCell(columnCount++);
            cell.setCellValue(calisanID);

            cell = row.createCell(columnCount++);
            cell.setCellValue(name);

            cell = row.createCell(columnCount++);
            cell.setCellValue(surname);

            cell = row.createCell(columnCount++);
            cell.setCellValue(maas);

            cell = row.createCell(columnCount++);
            cell.setCellValue(telNO);

            cell = row.createCell(columnCount++);
            cell.setCellValue(mail);

            cell = row.createCell(columnCount++);

            CellStyle cellStyle = workbook.createCellStyle();
            CreationHelper creationHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));
            cell.setCellStyle(cellStyle);

            cell.setCellValue(baslamaTarihi);

            cell = row.createCell(columnCount);
            cell.setCellValue(dogumTarihi);
        }
    }

    private void writeHeaderLineStatistics(XSSFSheet sheet) {

        Row headerRow = sheet.createRow(0);

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Günlük Alış");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("Günlük Satış");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("Aylık Alış");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("Aylık Satış");

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("Yıllık Alış");

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue("Yıllık Satış");

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue("Tarih");
    }

    private void writeDataLinesStatistics(ResultSet result, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        int rowCount = 1;

        while(result.next()) {
            int gunlukalis = result.getInt("gunlukalis");
            int gunluksatis = result.getInt("gunluksatis");
            int aylikalis = result.getInt("aylikalis");
            int ayliksatis = result.getInt("ayliksatis");
            int yillikalis = result.getInt("yillikalis");
            int yilliksatis = result.getInt("yilliksatis");
            Date tarih = result.getDate("tarih");

            Row row = sheet.createRow(rowCount++);

            int columnCount = 0;
            Cell cell = row.createCell(columnCount++);
            cell.setCellValue(gunlukalis);

            cell = row.createCell(columnCount++);
            cell.setCellValue(gunluksatis);

            cell = row.createCell(columnCount++);
            cell.setCellValue(aylikalis);

            cell = row.createCell(columnCount++);
            cell.setCellValue(ayliksatis);

            cell = row.createCell(columnCount++);
            cell.setCellValue(yillikalis);

            cell = row.createCell(columnCount++);
            cell.setCellValue(yilliksatis);

            cell = row.createCell(columnCount);

            CellStyle cellStyle = workbook.createCellStyle();
            CreationHelper creationHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));
            cell.setCellStyle(cellStyle);

            cell.setCellValue(tarih);
        }
    }
}