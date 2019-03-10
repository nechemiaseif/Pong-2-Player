package edu.mco364;

// Fig. 27.7: Client.java
// Client portion of a stream-socket connection between client and server.
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.*;

public class Client extends JFrame 
{
   private JTextField enterField; // enters information from user
   private JPanel displayArea; // display information to user
   private ObjectOutputStream output; // output stream to server
   private ObjectInputStream input; // input stream from server
   private String message = ""; // message from server
   private String chatServer; // host server for this application
   private Socket client; // socket to communicate with server
   private Color currentColor;

   // initialize chatServer and set up GUI
   public Client( String host )
   {
      super( "Client" );

      chatServer = host; // set server to which this client connects

      displayArea = new JPanel(); // create displayArea
      displayArea.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseDragged(MouseEvent e) {
            sendData(e.getPoint());
         }
      });
      add(displayArea , BorderLayout.CENTER );
      displayArea.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            currentColor = JColorChooser.showDialog(edu.mco364.Client.this, "Choose a Color", currentColor);
            sendData(currentColor);
         }
      });
      setSize( 500, 500 ); // set size of window
      setVisible( true ); // show window
   } // end Client constructor

   // connect to server and process messages from server
   public void runClient() 
   {
      try // connect to server, get streams, process connection
      {
         connectToServer(); // create a Socket to make connection
         getStreams(); // get the input and output streams
         processConnection(); // process connection
      } // end try
      catch ( EOFException eofException ) 
      {
         displayMessage( "\nClient terminated connection" );
      } // end catch
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
      finally 
      {
         closeConnection(); // close connection
      } // end finally
   } // end method runClient

   // connect to server
   private void connectToServer() throws IOException
   {      
      displayMessage( "Attempting connection\n" );

      // create Socket to make connection to server
      client = new Socket( InetAddress.getByName( chatServer ), 12345 );

      // display connection information
      displayMessage( "Connected to: " + 
         client.getInetAddress().getHostName() );
   } // end method connectToServer

   // get streams to send and receive data
   private void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( client.getOutputStream() );      
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( client.getInputStream() );

      displayMessage( "\nGot I/O streams\n" );
   } // end method getStreams

   // process connection with server
   private void processConnection() throws IOException
   {
      // enable enterField so client user can send messages
      setTextFieldEditable( true );

      do // process messages sent from server
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

      } while ( !message.equals( "SERVER>>> TERMINATE" ) );
   } // end method processConnection

   // close streams and socket
   private void closeConnection() 
   {
      displayMessage( "\nClosing connection" );
      setTextFieldEditable( false ); // disable enterField

      try 
      {
         output.close(); // close output stream
         input.close(); // close input stream
         client.close(); // close socket
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method closeConnection

   // send message to server
   private void sendData( Object o )
   {
      try // send object to server
      {
         output.writeObject( o );
         output.flush(); // flush data to output

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
         }  // end anonymous inner class
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

            } // end method run
         } // end anonymous inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method setTextFieldEditable
} // end class Client

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
