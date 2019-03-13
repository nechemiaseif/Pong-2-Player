package edu.mco364;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class PongServer extends PongNetwork {

    private ServerSocket server;
    private Socket connection;

    public PongServer() {
        super("Pong Server");
    }

    public void runServer() {
        try {
            server = new ServerSocket(12345, 100);

            while (true) {
                try {
                    this.waitForConnection();
                    super.getStreams(connection);
                    super.processConnection();
                } catch (EOFException eofException) {
                    eofException.printStackTrace();
                } finally {
                    super.closeConnection(connection);
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void waitForConnection() throws IOException {
        connection = server.accept();
    }


}