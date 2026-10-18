package com.cet.finder.util;

import com.cet.finder.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

/** Compatibility wrapper; credentials are configured only in DatabaseConnection. */
public final class DBConnection {
    private DBConnection() { }
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }
    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("MySQL connected successfully! Database: " + connection.getCatalog());
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}
