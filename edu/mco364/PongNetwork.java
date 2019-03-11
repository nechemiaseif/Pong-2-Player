package edu.mco364;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PongNetwork extends Pong {

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean clickedStart;

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
                if (o instanceof Point && ((Point) o).x == 620 || ((Point) o).x == 70) {
                    super.setOpponentPaddlePosition((Point) o);
                }
                else if(o instanceof Point) {
                    super.setBallPosition((Point) o);
                }
                else if(o instanceof String && o.equals("START")) {
                    System.out.println("String: " + o);
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

    void updateBall() {
        if(this.clickedStart) {
            super.updateBall();
            sendData(super.getBallPosition());
        }
    }

    void clickStartButton() {
        super.clickStartButton();
        this.clickedStart = true;
        sendData(super.getActionCommand());
    }

    void updatePlayerPaddle(){
        super.updatePlayerPaddle();
        sendData(super.getPlayerPaddlePosition());
    }
}
