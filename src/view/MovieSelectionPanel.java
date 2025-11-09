package view;
import model.Movie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class MovieSelectionPanel extends JPanel {

    private JFrame parentFrame;
    private Connection connection;

    public MovieSelectionPanel(Connection connection , JFrame parentFrame) {
        this.connection = connection;
        this.parentFrame = parentFrame;

        // Set layout
        setLayout(new BorderLayout());

        // Top panel with title and reset button
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Title
        JLabel title = new JLabel("Select a Movie", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(title, BorderLayout.CENTER);
        
        // Reset button
        JButton resetButton = new JButton("Reset Seats");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 12));
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetAllSeats();
            }
        });
        topPanel.add(resetButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // Create a container for the movies
        JPanel movieContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Horizontal layout
        movieContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fetch movies from the database and populate UI
        ArrayList<Movie> movies = fetchMoviesFromDatabase();
        for (Movie movie : movies) {
            JPanel moviePanel = createMoviePanel(connection, movie);
            movieContainer.add(moviePanel);
        }

        // Add movie container to the center
        add(movieContainer, BorderLayout.CENTER);
    }

    private ArrayList<Movie> fetchMoviesFromDatabase() {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            String query = "SELECT movie_id, movie_title, description, movie_logo FROM movies";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("movie_id");
                String title = resultSet.getString("movie_title");
                String description = resultSet.getString("description");
                byte[] logoBytes = resultSet.getBytes("movie_logo");

                // Scale logo to 180x270
                ImageIcon logo = new ImageIcon(logoBytes);
                Image scaledImage = logo.getImage().getScaledInstance(180, 270, Image.SCALE_SMOOTH);
                logo = new ImageIcon(scaledImage);

                movies.add(new Movie(id, title, description, logo));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching movies: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return movies;
    }

    private JPanel createMoviePanel(Connection connection, Movie movie) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setPreferredSize(new Dimension(200, 400)); // Enforce consistent size for movie panels

        // Movie Title
        JLabel titleLabel = new JLabel(movie.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        // Movie Logo
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(movie.getLogo());
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logoLabel);

        // Movie Description
        JTextArea descriptionArea = new JTextArea(movie.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setPreferredSize(new Dimension(200, 40));
        descriptionArea.setMaximumSize(new Dimension(200, 85));
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descriptionArea);

        // Select Button
        JButton selectButton = new JButton("Select");
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSeatSelectionPanel(connection, movie.getId(), movie.getTitle());
            }
        });
        panel.add(selectButton);

        return panel;
    }

    private void loadSeatSelectionPanel(Connection connection, int movieId, String movieTitle) {
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(new SeatSelectionPanel(connection, parentFrame, movieId, movieTitle));
        parentFrame.revalidate();
        parentFrame.repaint();
    }
    
    private void resetAllSeats() {
        try {
            String query = "UPDATE seats SET is_booked = 0";
            PreparedStatement statement = connection.prepareStatement(query);
            int rowsUpdated = statement.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "All seats have been reset! (" + rowsUpdated + " seats updated)", 
                                        "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error resetting seats: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
