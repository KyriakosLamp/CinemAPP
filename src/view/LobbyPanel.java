package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class LobbyPanel extends JPanel {
    private Connection connection;
    private JTextField usernameField; // Field for entering the username
    private JPasswordField passwordField; // Field for entering the password
    private JButton signInButton; // Button to sign in

    public LobbyPanel(JFrame parentFrame) {
        // Set layout for the panel
        setLayout(new BorderLayout());

        // Create a title label
        JLabel titleLabel = new JLabel("Welcome to Cinemapp! Please Sign In", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Create the center panel for the form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10)); // 3 rows, 2 columns, with gaps

        // Add username label and text field
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);

        // Add password label and text field
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        // Add the form panel to the center
        add(formPanel, BorderLayout.CENTER);

        // Create the sign-in button
        signInButton = new JButton("Sign In");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(signInButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener to the sign-in button
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Simple placeholder action: Move to the MovieSelectionPanel
                // You can expand this with authentication logic later
                parentFrame.getContentPane().removeAll();
                parentFrame.getContentPane().add(new MovieSelectionPanel(connection, parentFrame));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
    }
}
