package edu.mco364;

import javax.swing.JFrame;

public class ClientMain {

    public static void main( String[] args )
    {
        PongClient application; // declare client application

        // if no command line args
        if ( args.length == 0 )
            application = new PongClient( "192.168.1.10" ); // connect to localhost
        else
            application = new PongClient( args[ 0 ] ); // use args to connect

        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        application.runClient(); // run client application
    } // end main

}
