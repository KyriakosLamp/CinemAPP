package main;


import view.MovieSelectionPanel;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainApp {

    private static Connection connection;

    public static void main(String[] args) {

// Switch between MySQL and SQLite
        String dbType = "sqlite";

        try {
            if ("mysql".equalsIgnoreCase(dbType)) {
                connection = connectMySQL();
            } else if ("sqlite".equalsIgnoreCase(dbType)) {
                connection = connectSQLite();
            } else {
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
            }

            // Register a shutdown hook to close the connection
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (connection != null) {
                    try {
                        connection.close();
                        System.out.println("Database connection closed.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }));

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("CinemApp");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(700, 520); // Set initial size
                frame.setResizable(true);

                // Add LobbyPanel to the frame
                frame.getContentPane().add(new MovieSelectionPanel(connection, frame));
                frame.setVisible(true);
            });

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Connection connectMySQL() throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/cinemadb";
        String dbUser = "root";
        String dbPassword = "";
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    private static Connection connectSQLite() throws SQLException {
        String dbUrl = "jdbc:sqlite:./other/cinema.db";
        return DriverManager.getConnection(dbUrl);
    }

    public static Connection getConnection() {
        return connection;
    }
}


