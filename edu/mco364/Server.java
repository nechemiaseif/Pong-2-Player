package edu.mco364;

// Fig. 27.5: Server.java
// Server portion of a client/server stream-socket connection. 
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

enum Planet {
   MERCURY (2.4E17, "Jublop"), VENUS(5,""), EARTH(7,""), MARS(42,"");

   Planet(double mass, String watusiName) {
      this.mass = mass;
      this.watusiName = watusiName;
   }
   private double mass;
   private String watusiName;

   public String toString(){
      return String.format("%s %d", watusiName, mass);
   }
}

public class Server extends JFrame 
{
   private JTextField enterField; // inputs message from user
    private JPanel displayArea; // display information to user
    private ObjectOutputStream output; // output stream to client
    private ObjectInputStream input; // input stream from client
    private ServerSocket server; // server socket
    private Socket connection; // connection to client
    private int counter = 1; // counter of number of connections
    private Color currentColor = Color.BLACK;
   // set up GUI
   public Server()
   {
      super( "Server" );

      displayArea = new JPanel(); // create displayArea
      displayArea.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseDragged(MouseEvent e) {
            sendData(e.getPoint());
         }
      });
      displayArea.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            currentColor = JColorChooser.showDialog(Server.this, "Choose a Color", currentColor);
            sendData(currentColor);
         }
      });
      add(displayArea , BorderLayout.CENTER );

      setSize( 500, 500 ); // set size of window
      setVisible(true);
   } // end Server constructor

   // set up and run server 
   public void runServer()
   {
      try // set up server to receive connections; process connections
      {
         server = new ServerSocket( 12345, 100 ); // create ServerSocket

         while ( true ) 
         {
            try 
            {
               waitForConnection(); // wait for a connection
               getStreams(); // get input & output streams
               processConnection(); // process connection
            } // end try
            catch ( EOFException eofException ) 
            {
               displayMessage( "\nServer terminated connection" );
            } // end catch
            finally 
            {
               closeConnection(); //  close connection
               ++counter;
            } // end finally
         } // end while
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method runServer

   // wait for connection to arrive, then display connection info
   private void waitForConnection() throws IOException
   {
      displayMessage( "Waiting for connection\n" );
      connection = server.accept(); // allow server to accept connection            
      displayMessage( "Connection " + counter + " received from: " +
         connection.getInetAddress().getHostName() );
   } // end method waitForConnection

   // get streams to send and receive data
   private void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( connection.getOutputStream() );
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( connection.getInputStream() );

      displayMessage( "\nGot I/O streams\n" );
   } // end method getStreams

   // process connection with client
   private void processConnection() throws IOException
   {
      // enable enterField so server user can send messages
      setTextFieldEditable( true );

      do // process messages sent from client
      { 
         try // read message and display it
         {
            Object o = input.readObject();
            if (o instanceof Point) {
               Point p = (Point) o; // read new message
               Graphics g = displayArea.getGraphics();
               g.setColor(currentColor);
               g.fillOval(p.x, p.y, 30, 30);
            }
            else if (o instanceof Color) {
               currentColor = (Color) o;
            }
         } // end try
         catch ( ClassNotFoundException classNotFoundException ) 
         {
            displayMessage( "\nUnknown object type received" );
         } // end catch

      } while ( true );
   } // end method processConnection

   // close streams and socket
   private void closeConnection() 
   {
      displayMessage( "\nTerminating connection\n" );
      setTextFieldEditable( false ); // disable enterField

      try 
      {
         output.close(); // close output stream
         input.close(); // close input stream
         connection.close(); // close socket
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method closeConnection

   // send message to client
   private void sendData( Object o )
   {
      try // send object to client
      {
         output.writeObject( o );
         output.flush(); // flush output to client
      } // end try
      catch ( IOException ioException ) 
      {
      } // end catch
   } // end method sendData

   // manipulates displayArea in the event-dispatch thread
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() // updates displayArea
            {

            } // end method run
         } // end anonymous inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method displayMessage

   // manipulates enterField in the event-dispatch thread
   private void setTextFieldEditable( final boolean editable )
   {
      SwingUtilities.invokeLater(
         new Runnable()
         {
            public void run() // sets enterField's editability
            {
               enterField.setEditable( editable );
            } // end method run
         }  // end inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method setTextFieldEditable
} // end class Server

/**************************************************************************
 * (C) Copyright 1992-2010 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/