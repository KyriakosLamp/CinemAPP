#!/bin/bash

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile the project
echo "Compiling CinemAPP..."
javac -cp "other/sqlite-jdbc-3.47.1.0.jar" -d bin src/main/*.java src/model/*.java src/view/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "To run the application, use:"
    echo "java -cp \"bin:other/sqlite-jdbc-3.47.1.0.jar\" main.MainApp"
else
    echo "Compilation failed!"
    exit 1
fi