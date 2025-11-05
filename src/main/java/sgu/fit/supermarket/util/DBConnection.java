package sgu.fit.supermarket.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/supermarketDB?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "minhzu@123";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}