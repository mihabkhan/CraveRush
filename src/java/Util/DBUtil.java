package Util;
import java.sql.*;


public class DBUtil {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/craverush_db", "root", "password");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
