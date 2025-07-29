# The default recipe runs everything
default: compile server client

# Compile Java source files
compile:
    @echo "Compiling Java files..."
    javac -d bin *.java

# Run the server
server: compile
    @echo "Starting server..."
    java -cp bin BLServer
    @echo "Server started in background."

# Run the client
client: compile
    @echo "Starting client..."
    # Give the server a moment to start up
    sleep 2
    java -cp bin BLClient

# Clean up compiled classes
clean:
    @echo "Cleaning up compiled classes..."
    rm -rf bin/