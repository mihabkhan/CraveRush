import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import Util.DBUtil;

public class TestDBConnection {
    public static void main(String[] args) {
        Connection conn = DBUtil.getConnection();

        if (conn != null) {
            System.out.println("✅ Database connection successful!");
            try {
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM users";
                ResultSet rs = stmt.executeQuery(query);

                System.out.println("User Data:");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");

                    System.out.println("ID: " + id + ", Username: " + username + ", Password: " + password + ", Email: " + email);
                }

                conn.close();
            } catch (Exception e) {
                System.out.println("❌ Error during query execution:");
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ Failed to connect to the database.");
        }
    }
}
