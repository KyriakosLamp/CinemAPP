package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Seat {
    private String id;        // Seat ID (e.g., "A1", "B2")
    private boolean isBooked; // Seat availability (true if booked, false otherwise)

    // Constructor
    public Seat(String id, boolean isBooked) {
        this.id = id;
        this.isBooked = isBooked;
    }

    // Getters
    public String getId() {
        return id;
    }

    public boolean isBooked() {
        return isBooked;
    }

    // Setters
    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    // Fetch all seats for a given movie
    public static List<Seat> getSeatsByMovie(Connection connection, int movieId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT seat_id, is_booked FROM Seats WHERE movie_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("seat_id");
                    boolean isBooked = rs.getBoolean("is_booked");

                    // Create a Seat object and add it to the list
                    seats.add(new Seat(id, isBooked));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching seats: " + e.getMessage());
        }

        return seats;
    }

    // Update the booking status of a seat
    public static boolean updateSeatStatus(Connection connection, String seatId, int movieId, boolean isBooked) {
        String sql = "UPDATE Seats SET is_booked = ? WHERE seat_id = ? AND movie_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isBooked);
            stmt.setString(2, seatId);
            stmt.setInt(3, movieId);

            return stmt.executeUpdate() > 0; // Returns true if the update was successful
        } catch (SQLException e) {
            System.err.println("Error updating seat status: " + e.getMessage());
        }

        return false;
    }

    // For debugging or display purposes
    @Override
    public String toString() {
        return "Seat{" +
                "id='" + id + '\'' +
                ", isBooked=" + isBooked +
                '}';
    }
}
