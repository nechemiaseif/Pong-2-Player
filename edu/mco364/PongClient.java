package edu.mco364;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class PongClient extends PongNetwork {

    private String pongServer;
    private Socket client;

    public PongClient(String host) {

        super("Pong Client");
        pongServer = host;
    }

    public void runClient() {
        try {
            this.connectToServer();
            super.getStreams(client);
            super.processConnection();
        } catch (EOFException eofException) {
            eofException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            closeConnection(client);
        }
    }

    private void connectToServer() throws IOException {
        client = new Socket(InetAddress.getByName(pongServer), 12345);
    }
}
