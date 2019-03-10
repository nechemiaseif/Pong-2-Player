// Nechemia Seif - HW 5 - Pong 1.1

package edu.mco364;

        import javax.swing.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.awt.BorderLayout;
        import java.awt.GridLayout;
        import java.awt.Graphics;
        import java.awt.Point;
        import java.awt.Color;
        import java.awt.event.MouseWheelEvent;
        import java.awt.event.MouseWheelListener;
        import java.awt.event.KeyEvent;
        import java.awt.event.KeyListener;
        import static java.awt.event.KeyEvent.VK_UP;
        import java.awt.event.WindowAdapter;
        import java.awt.event.WindowEvent;
        import java.io.*;
        import java.net.InetAddress;
        import java.net.Socket;
        import java.util.Properties;

public class PongClient extends JFrame {

    private JLabel playerScoreLabel, computerScoreLabel,
            highScoreLabel;
    private Point computerPaddlePosition, playerPaddlePosition,
            ball, delta;
    private int playerScore, computerScore, highScore;
    private boolean playerPaddleMovingUp;
    private Timer timer;
    private JTextField enterField; // enters information from user
    private JPanel displayArea; // display information to user
    private ObjectOutputStream output; // output stream to server
    private ObjectInputStream input; // input stream from server
    private String message = ""; // message from server
    private String pongServer; // host server for this application
    private Socket client; // socket to communicate with server
    private Color currentColor;


    public PongClient(String host) {

        super("Pong 1.1");

        pongServer = host;

        setVisible(true);
        setSize(700, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        playerPaddlePosition = new Point(620, 200);
        computerPaddlePosition = new Point(70, 200);
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
                play();
            }
        });

        setWindowEvents();
    }

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

    private void connectToServer() throws IOException
    {
        displayMessage( "Attempting connection\n" );

        // create Socket to make connection to server
        client = new Socket( InetAddress.getByName( pongServer ), 12345 );

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
                    Graphics g = getGraphics();
                    //g.setColor(currentColor);
                    g.fillRect(p.x, p.y, 10, 60);
                    repaint();
                }
                //else if (o instanceof Color) {
                    //currentColor = (Color) o;
                //}

            } // end try
            catch ( ClassNotFoundException classNotFoundException )
            {
                displayMessage( "\nUnknown object type received" );
            } // end catch

        } while (true);
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
    // end class Client


    private void play() {
        timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBall();
                updatePlayerPaddle();
                updateOpponentPaddle();
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
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                playerPaddleMovingUp
                        = e.getKeyCode() == VK_UP ? true : false;
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
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
        if (ball.x == computerPaddlePosition.x + 10
                && ball.y >= computerPaddlePosition.y
                && ball.y <= computerPaddlePosition.y + 60) {
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

        sendData(playerPaddlePosition);
    }

    private void updateOpponentPaddle() {
        if (computerPaddlePosition.y < 50) {
            computerPaddlePosition
                    .setLocation(computerPaddlePosition.x, 50);
        } else if (ball.y >= 390 && ball.y <= 450) {
            computerPaddlePosition
                    .setLocation(computerPaddlePosition.x, 390);
        } else {
            computerPaddlePosition
                    .setLocation(computerPaddlePosition.x, ball.y);
        }
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
        g.fillRect(computerPaddlePosition.x,
                computerPaddlePosition.y, 10, 60);
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
