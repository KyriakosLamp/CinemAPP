package model;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int id;          // Unique identifier for the movie
    private String title;    // Movie title
    private String description; // Movie description
    private ImageIcon logo;

    // Constructor
    public Movie(int id, String title, String description,ImageIcon logo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.logo = logo;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ImageIcon getLogo() {
        return logo;
    }

    // Fetch all movies from the database
    public static List<Movie> getAllMovies(Connection connection) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT movie_id, movie_title, description, movie_logo FROM movies";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("movie_id");
                String title = rs.getString("movie_title");
                String description = rs.getString("description");
                byte[] logoBytes = rs.getBytes("movie_logo");
                ImageIcon logo = new ImageIcon(logoBytes);

                // Create a Movie object and add it to the list
                movies.add(new Movie(id, title, description, logo));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching movies: " + e.getMessage());
        }

        return movies;
    }

    // Fetch a single movie by ID
    public static Movie getMovieById(Connection connection, int movieId) {
        String sql = "SELECT movie_id, movie_title, description, movie_logo FROM movies WHERE movie_id = ?";
        Movie movie = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("movie_id");
                    String title = rs.getString("movie_title");
                    String description = rs.getString("description");
                    byte[] logoBytes = rs.getBytes("movie_logo");
                    ImageIcon logo = new ImageIcon(logoBytes);

                    movie = new Movie(id, title, description, logo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching movie by ID: " + e.getMessage());
        }

        return movie;
    }

    // For debugging or display purposes
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
