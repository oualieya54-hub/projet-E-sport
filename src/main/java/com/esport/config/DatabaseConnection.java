package com.esport.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection — singleton MySQL connection.
 * WAMP uses port 3307 with MariaDB.
 */
public class DatabaseConnection {

    private static final String HOST     = "localhost";
    private static final int    PORT     = 3307;
    private static final String DATABASE = "esport_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Connected to: " + DATABASE);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing: " + e.getMessage());
        }
    }

    public static boolean testConnection() {
        try {
            return getConnection() != null && !getConnection().isClosed();
        } catch (SQLException e) {
            System.err.println("[DB] Test FAILED: " + e.getMessage());
            return false;
        }
    }
}