package org.example.connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 Singleton
 */
public class connexionDB {

    private static final String URL      = "jdbc:mysql://localhost:3306/esport_eshop";
    private static final String USER     = "root";
    private static final String PASSWORD = "";
    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";

    // pattern singleton
    private static Connection instance = null;

    private connexionDB() {}

    public static Connection getInstance() {
        try {
            if (instance == null || instance.isClosed()) {
                Class.forName(DRIVER);
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion MySQL établie avec succès !");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable : " + e.getMessage());
            System.err.println("   → Ajoute mysql-connector-java.jar dans les librairies du projet !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion MySQL : " + e.getMessage());
            System.err.println("   → Vérifie que WAMP est lancé et que la base 'esport_eshop' existe.");
        }
        return instance;
    }

    public static void fermer() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                System.out.println("🔒 Connexion MySQL fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture : " + e.getMessage());
        }
    }

    /**
     * utile pour debuuger
     */
    public static boolean testerConnexion() {
        Connection conn = getInstance();
        return conn != null;
    }
}

