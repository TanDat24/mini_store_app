package sgu.fit.supermarket.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

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

    /**
     * Test database connection
     */
    public static void testConnection() {
        Connection conn = null;
        try {
            System.out.println("Đang thử kết nối đến database...");
            conn = getConnection();
            
            if (conn != null) {
                System.out.println("✓ Kết nối thành công!");
                
                // Lấy thông tin database
                DatabaseMetaData metaData = conn.getMetaData();
                System.out.println("Database: " + metaData.getDatabaseProductName());
                System.out.println("Version: " + metaData.getDatabaseProductVersion());
                System.out.println("Driver: " + metaData.getDriverName());
                System.out.println("URL: " + metaData.getURL());
                System.out.println("Username: " + metaData.getUserName());
            } else {
                System.out.println("✗ Kết nối thất bại!");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi khi kết nối: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    System.out.println("Đã đóng kết nối.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Main method để test kết nối
     */
    public static void main(String[] args) {
        testConnection();
    }
}