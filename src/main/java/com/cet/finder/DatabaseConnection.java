
package com.cet.finder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = System.getProperty(
            "cet.db.url",
            System.getenv().getOrDefault(
                    "CET_DB_URL",
                    "jdbc:mysql://localhost:3306/college_info_db"
            )
    );

    private static final String USER = System.getProperty(
            "cet.db.user",
            System.getenv().getOrDefault(
                    "CET_DB_USER",
                    "root"
            )
    );

    private static final String PASSWORD = System.getProperty(
            "cet.db.password",
            System.getenv().getOrDefault(
                    "CET_DB_PASSWORD",
                    "Luz@2013"
            )
    );

    private DatabaseConnection() {
        // Utility class; prevent object creation.
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("MySQL connected successfully!");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("User: " + USER);
        } catch (SQLException e) {
            System.err.println("MySQL connection failed!");
            System.err.println("Error: " + e.getMessage());
        }
    }
}
