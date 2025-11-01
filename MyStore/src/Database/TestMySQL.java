package Database;

import java.sql.*;

public class TestMySQL {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydata";
        String user = "root";
        String password = "26012014";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Kết nối MySQL thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
