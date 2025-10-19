package Database;

import java.sql.*;


public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/mydata";
    private static final String USER = "root";
    private static final String PASSWORD = "17012005";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

