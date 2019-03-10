package edu.mco364;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import static java.awt.event.KeyEvent.VK_UP;

public class PongServer extends JFrame {

    private JLabel playerScoreLabel, computerScoreLabel,
            highScoreLabel;
    private Point opponentPaddlePosition, playerPaddlePosition,
            ball, delta;
    private int playerScore, computerScore, highScore;
    private boolean playerPaddleMovingUp;
    private Timer timer;
    private JTextField enterField; // enters information from user
    private JPanel displayArea; // display information to user
    private ObjectOutputStream output; // output stream to client
    private ObjectInputStream input; // input stream from client
    private ServerSocket server; // server socket
    private Socket connection; // connection to client
    private int counter = 1; // counter of number of connections
    private Color currentColor = Color.BLACK;


    public PongServer() {

        super("Pong Server");

        setVisible(true);
        setSize(700, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        playerPaddlePosition = new Point(620, 200);
        opponentPaddlePosition = new Point(70, 200);
        ball = new Point(335, 235);
        delta = new Point(5, 5);

        computerScoreLabel = new JLabel("Computer: " + computerScore);
        highScoreLabel = new JLabel("High Score: " + highScore);
        playerScoreLabel = new JLabel("Player: " + playerScore);

        JPanel scorePanel = new JPanel();

        scorePanel.setLayout(new GridLayout(1, 3));

        scorePanel.add(computerScoreLabel);
        scorePanel.add(highScoreLabel);
        scorePanel.add(playerScoreLabel);

        add(scorePanel, BorderLayout.SOUTH);

        JButton startButton = new JButton("START");

        add(startButton, BorderLayout.NORTH);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData(e.getActionCommand());
                play();
            }
        });

        setWindowEvents();
    }

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
        //setTextFieldEditable( true );

        do // process messages sent from client
        {
            try // read message and display it
            {
                Object o = input.readObject();
                if (o instanceof Point) {
                    opponentPaddlePosition.setLocation((Point) o);
                    //repaint();
                }
                else if(o instanceof String && o.equals("START")) {
                    play();
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
        //setTextFieldEditable( false ); // disable enterField

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
/*    private void setTextFieldEditable( final boolean editable )
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
    } // end method setTextFieldEditable*/
 // end class Server


    private void play() {
        timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBall();
                updatePlayerPaddle();
                repaint();
            }
        });

        timer.start();

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                playerPaddleMovingUp
                        = e.getWheelRotation() < 0 ? true : false;
            }
        });

        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                playerPaddleMovingUp
                        = e.getKeyCode() == VK_UP ? true : false;
            }

/*            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }*/
        });

    }

    private void updateBall() {

        if (ball.x >= 635 || ball.x <= 50) {
            restartGame();
        }
        if (ball.y <= 50 || ball.y >= 435) {
            delta.y = -delta.y;
        }
        if (ball.x == playerPaddlePosition.x - 15
                && ball.y >= playerPaddlePosition.y
                && ball.y <= playerPaddlePosition.y + 60) {
            delta.x = -delta.x;
            playerScore++;
        }
        if (ball.x == opponentPaddlePosition.x + 10
                && ball.y >= opponentPaddlePosition.y
                && ball.y <= opponentPaddlePosition.y + 60) {
            delta.x = -delta.x;
            computerScore++;
        }
        ball.x += delta.x;
        ball.y += delta.y;

        updateScoreLabels();
    }

    private void updatePlayerPaddle() {
        if (playerPaddleMovingUp) {
            playerPaddlePosition
                    .setLocation(playerPaddlePosition.x,
                            playerPaddlePosition.y > 50 ?
                                    playerPaddlePosition.y - 5 : 50);
            if (playerPaddlePosition.y == 50) {
                playerPaddleMovingUp = false;
            }
        } else {
            playerPaddlePosition
                    .setLocation(playerPaddlePosition.x,
                            playerPaddlePosition.y < 390 ?
                                    playerPaddlePosition.y + 5 : 390);
            if (playerPaddlePosition.y == 390) {
                playerPaddleMovingUp = true;
            }
        }
        sendData(playerPaddlePosition.getLocation());
    }


    private void restartGame() {
        ball.x = 335;
        ball.y = 235;
        playerScore = 0;
        computerScore = 0;
        highScore = getHighScore();
        updateScoreLabels();
    }

    private void updateScoreLabels() {
        computerScoreLabel.setText("Computer: " + computerScore);
        highScoreLabel.setText("High Score: " + getHighScore());
        playerScoreLabel.setText("Player: " + playerScore);
    }

    private int getHighScore() {
        if (playerScore > highScore) {
            highScore = playerScore;
        }
        return highScore;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.drawRect(50, 50, 600, 400);
        g.fillRect(playerPaddlePosition.x,
                playerPaddlePosition.y, 10, 60);
        g.fillRect(opponentPaddlePosition.x,
                opponentPaddlePosition.y, 10, 60);
        g.fillOval(ball.x, ball.y, 15, 15);

    }

    private void setWindowEvents() {

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                Properties appProps = new Properties();
                try {
                    InputStream is
                            = new FileInputStream("pongProperties.bin");
                    appProps.load(is);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                highScore = Integer
                        .parseInt(appProps.getProperty("High_Score"));
                highScoreLabel.setText("High Score: " + highScore);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                Properties appProps = new Properties();
                appProps.setProperty("High_Score", highScore + "");
                try {
                    OutputStream os
                            = new FileOutputStream("pongProperties.bin");
                    appProps.store(os, highScore + "");
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
            }

        });
    }



}