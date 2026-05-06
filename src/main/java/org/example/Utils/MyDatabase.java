package org.example.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton de connexion JDBC à la base de données.
 * Usage : Connection conn = MyDatabase.getInstance().getConnection();
 */
public class MyDatabase {

    private static final String URL      = "jdbc:mysql://localhost:3306/MyDatabase";
    private static final String USER     = "root";
    private static final String PASSWORD = "";  // ← ton mot de passe MySQL

    private static MyDatabase instance;
    private Connection connection;

    private MyDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion BDD établie.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Erreur connexion BDD : " + e.getMessage());
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("❌ Reconnexion BDD : " + e.getMessage());
        }
        return connection;
    }
}