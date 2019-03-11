// Nechemia Seif - HW 5 - Pong 1.1

package edu.mco364;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class PongClient extends PongNetwork {

    private String pongServer;
    private Socket client;


    public PongClient(String host) { // TODO: ctor with param

        super("Pong Client");
        pongServer = host;
    }

    public void runClient()
    {
        try {
            this.connectToServer();
            super.getStreams(client);
            super.processConnection();
        }
        catch ( EOFException eofException )
        {
            // TODO: display message
        }
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        }
        finally
        {
            closeConnection(client);
        }
    }

    private void connectToServer() throws IOException
    {
        client = new Socket( InetAddress.getByName( pongServer ), 12345 );
    }
}
