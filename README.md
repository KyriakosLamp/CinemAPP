# CinemAPP

A Java Swing-based cinema booking application that allows users to browse movies, select seats, and complete bookings.

## Features

- Browse available movies with posters and descriptions
- Interactive seat selection with real-time availability
- Complete booking workflow with database persistence
- Support for SQLite and MySQL databases

## Prerequisites

- Java 8 or higher
- SQLite JDBC driver (included in `other/` directory)
- MySQL Connector (optional, included in `other/` directory)

## Setup & Installation

1. Clone the repository:
```bash
git clone https://github.com/KyriakosLamp/CinemAPP
cd CinemAPP
```

2. Build the project:
```bash
./build.sh
```

3. Run the application:
```bash
java -cp "bin:other/sqlite-jdbc-3.47.1.0.jar" main.MainApp
```

### Manual Compilation (Alternative)
```bash
mkdir -p bin
javac -cp "other/sqlite-jdbc-3.47.1.0.jar" -d bin src/main/*.java src/model/*.java src/view/*.java
```

## Database Configuration

The application uses SQLite by default. To switch to MySQL:

1. Edit `src/main/MainApp.java`
2. Change `String dbType = "sqlite";` to `String dbType = "mysql";`
3. Update MySQL connection details in the `connectMySQL()` method

## Project Structure

```
CinemAPP/
├── src/
│   ├── main/MainApp.java          # Application entry point
│   ├── model/                     # Data models
│   │   ├── Movie.java
│   │   ├── Seat.java
│   │   ├── Booking.java
│   │   └── User.java
│   └── view/                      # UI components
│       ├── MovieSelectionPanel.java
│       ├── SeatSelectionPanel.java
│       └── PaymentScreenPanel.java
├── other/
│   ├── cinema.db                  # SQLite database
│   └── sqlite-jdbc-3.47.1.0.jar  # JDBC driver
└── README.md
```

## Usage

1. **Movie Selection**: Browse and select from available movies
2. **Seat Selection**: Choose seats from the 3x5 theater layout
   - Green: Available
   - Red: Booked
   - Gray: Selected
3. **Checkout**: Confirm booking and complete transaction

## License

This project is for educational purposes.