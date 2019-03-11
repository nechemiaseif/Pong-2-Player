package edu.mco364;

import javax.swing.JFrame;

public class ClientMain {

    public static void main( String[] args )
    {
        PongClient application;

        if ( args.length == 0 )
            application = new PongClient( "192.168.1.10" );
        else
            application = new PongClient( args[ 0 ] );

        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        application.runClient();
    }

}
