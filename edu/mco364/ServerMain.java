package edu.mco364;

import javax.swing.JFrame;

public class ServerMain {

    public static void main( String[] args )
    {
        PongServer application = new PongServer();
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        application.runServer();
    }
}
