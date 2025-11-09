package view;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

public class SeatSelectionPanel extends JPanel {

    private Connection connection;
    private JFrame parentFrame;
    private int movieId;
    private String movieTitle;
    private ImageIcon movieLogo;
    private HashSet<String> selectedSeats = new HashSet<>();

    public SeatSelectionPanel(Connection connection, JFrame parentFrame, int movieId, String movieTitle) {
        this.parentFrame = parentFrame;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.connection = connection;
        // Fetch the movie logo from the database
        this.movieLogo = fetchMovieLogoFromDatabase(movieId);

        // Set layout
        setLayout(new BorderLayout());

        // Top Section: Movie Title and Logo
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(movieTitle, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        JLabel logoLabel = new JLabel();
        if (movieLogo != null) {
            logoLabel.setIcon(movieLogo);
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(logoLabel);

        add(topPanel, BorderLayout.NORTH);

        // Middle Section: Seat Layout
        JPanel seatPanel = new JPanel(new GridLayout(3, 5, 10, 10)); // 3 rows, 5 columns with spacing
        ArrayList<String> unavailableSeats = fetchUnavailableSeats(movieId);

        for (char row = 'A'; row <= 'C'; row++) {
            for (int col = 1; col <= 5; col++) {
                String seatId = row + String.valueOf(col);

                JButton seatButton = new JButton(seatId);
                seatButton.setOpaque(true);
                seatButton.setBorderPainted(false);

                // Set initial color based on availability
                if (unavailableSeats.contains(seatId)) {
                    seatButton.setBackground(Color.RED);
                    seatButton.setEnabled(false); // Disable unavailable seats
                } else {
                    seatButton.setBackground(Color.GREEN);
                }

                // Toggle seat selection
                seatButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (seatButton.getBackground() == Color.GREEN) {
                            seatButton.setBackground(Color.GRAY);
                            selectedSeats.add(seatId);
                        } else if (seatButton.getBackground() == Color.GRAY) {
                            seatButton.setBackground(Color.GREEN);
                            selectedSeats.remove(seatId);
                        }
                    }
                });

                seatPanel.add(seatButton);
            }
        }

        add(seatPanel, BorderLayout.CENTER);

        // Bottom Section: Checkout Button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton checkoutButton = new JButton("Checkout");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(checkoutButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPaymentScreen();
            }

        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.getContentPane().removeAll();
                parentFrame.getContentPane().add(new MovieSelectionPanel(connection, parentFrame)); // Back to seat selection
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
    }

    private ImageIcon fetchMovieLogoFromDatabase(int movieId) {
        ImageIcon logo = null;
        try{
            String query = "SELECT movie_logo FROM movies WHERE movie_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, movieId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                byte[] logoBytes = resultSet.getBytes("movie_logo");
                ImageIcon rawLogo = new ImageIcon(logoBytes);
                Image scaledImage = rawLogo.getImage().getScaledInstance(180, 270, Image.SCALE_SMOOTH);
                logo = new ImageIcon(scaledImage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching movie logo: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return logo;
    }

    private ArrayList<String> fetchUnavailableSeats(int movieId) {
        ArrayList<String> unavailableSeats = new ArrayList<>();
        try{
            String query = "SELECT seat_id FROM seats WHERE movie_id = ? AND is_booked = 1";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, movieId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                unavailableSeats.add(resultSet.getString("seat_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching unavailable seats: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return unavailableSeats;
    }

    private void loadPaymentScreen() {
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(new PaymentScreenPanel(connection, parentFrame, movieId, movieTitle, selectedSeats));
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
