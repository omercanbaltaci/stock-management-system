package com.company;

import java.sql.*;

class DBConnect {
    static Connection getConnection() throws Exception {
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/stokyonetim?serverTimezone=GMT";
            String username = "root";
            String password = "*******";

            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, username, password);
            
            return con;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
