package edu.mco364;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PongNetwork extends Pong {

    private ObjectOutputStream output;
    private ObjectInputStream input;

    public PongNetwork(String title) {
        super(title);
    }

    void getStreams(Socket sock) throws IOException
    {
        output = new ObjectOutputStream(sock.getOutputStream() );
        output.flush();

        input = new ObjectInputStream(sock.getInputStream() );
    }

    void processConnection() throws IOException
    {
        do
        {
            try
            {
                Object o = input.readObject();
                if (o instanceof Point) {
                    setOpponentPaddlePosition((Point) o);
                }
                else if(o instanceof String && o.equals("START")) {
                    super.play();
                }
            }
            catch ( ClassNotFoundException classNotFoundException )
            {
                //displayMessage( "\nUnknown object type received" );
            }

        } while ( true );
    }

    void closeConnection(Socket sock)
    {
        //displayMessage( "\nTerminating connection\n" );

        try
        {
            output.close();
            input.close();
            sock.close();
        }
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        }
    }

    void sendData( Object o )
    {
        try
        {
            output.writeObject( o );
            output.flush();
        }
        catch ( IOException ioException )
        {
            //TODO: write message
        }
    }

    void clickStartButton() {
        super.clickStartButton();
        sendData(super.getActionCommand());
    }

    void updatePlayerPaddle(){
        super.updatePlayerPaddle();
        sendData(super.getPlayerPaddlePosition());
    }
}
