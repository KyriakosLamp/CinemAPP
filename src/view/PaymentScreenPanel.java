package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;


public class PaymentScreenPanel extends JPanel {
    private Connection connection;
    private JFrame parentFrame;
    private int movieId;
    private String movieTitle;
    private HashSet<String> selectedSeats;

    public PaymentScreenPanel(Connection connection,JFrame parentFrame, int movieId, String movieTitle ,  HashSet<String> selectedSeats) {
        this.connection = connection;
        this.parentFrame = parentFrame;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.selectedSeats = selectedSeats;

        // Set layout
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Checkout Screen", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Confirmation Text
        String seatText = selectedSeats.isEmpty() ? "No seats selected" : selectedSeats.toString();
        JLabel confirmationLabel = new JLabel(
                "<html>You are about to buy seats: <b>" + seatText + "</b> for the movie: <b>" + movieTitle + "</b>.</html>",
                SwingConstants.CENTER
        );
        confirmationLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(confirmationLabel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton proceedButton = new JButton("Proceed");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(proceedButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Proceed Button Logic
        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedSeats.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, "No seats selected!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    boolean success = updateDatabase(movieId, selectedSeats);
                    if (success) {
                        JOptionPane.showMessageDialog(parentFrame, "Booking successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Optionally navigate to a new screen or reset the app
                        parentFrame.getContentPane().removeAll();
                        parentFrame.getContentPane().add(new MovieSelectionPanel(connection, parentFrame)); // Back to lobby
                        parentFrame.revalidate();
                        parentFrame.repaint();
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, "Failed to complete booking!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Cancel Button Logic
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.getContentPane().removeAll();
                parentFrame.getContentPane().add(new SeatSelectionPanel(connection, parentFrame, movieId, movieTitle)); // Back to seat selection
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
    }

    private boolean updateDatabase(int movieId, HashSet<String> selectedSeats) {
        String query = "UPDATE seats SET is_booked = 1 WHERE movie_id = ? AND seat_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            for (String seatId : selectedSeats) {
                statement.setInt(1, movieId);
                statement.setString(2, seatId);
                statement.addBatch(); // Add to batch
            }

            statement.executeBatch(); // Execute all updates in a single batch
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
