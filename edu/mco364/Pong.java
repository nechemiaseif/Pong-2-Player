package edu.mco364;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

import static java.awt.event.KeyEvent.VK_UP;

public class Pong extends JFrame{

    private JLabel leftScoreLabel, rightScoreLabel,
            highScoreLabel;
    private Point opponentPaddlePosition, playerPaddlePosition,
            ball, delta;
    private int playerScore, opponentScore, highScore;
    private boolean playerPaddleMovingUp;
    private Timer timer;
    private boolean isServer;
    private String startButtonActionCommand;

    final int FRAME_WIDTH = 700;
    final int FRAME_HEIGHT = 500;
    final int RIGHT_PLAY_AREA_BOUNDARY_X_COORD = 650;
    final int LEFT_PLAY_AREA_BOUNDARY_X_COORD  = 50;
    final int UPPER_PLAY_AREA_BOUNDARY_Y_COORD = 50;
    final int LOWER_PLAY_AREA_BOUNDARY_Y_COORD = 450;
    final int BALL_WIDTH = 15;
    final int BALL_HEIGHT = 15;
    final int PADDLE_WIDTH = 10;
    final int PADDLE_HEIGHT = 60;
    final int PLAY_AREA_CENTER_X = FRAME_WIDTH / 2;
    final int PLAY_AREA_CENTER_Y = (FRAME_HEIGHT / 2) - UPPER_PLAY_AREA_BOUNDARY_Y_COORD;

    public Pong(String title) {

        super(title);
        setVisible(true);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        isServer = title.equals("Pong Server") ? true : false;

        playerPaddlePosition = isServer
                ? new Point(620, 200) : new Point(70, 200);
        opponentPaddlePosition = isServer
                ? new Point(70, 200): new Point(620, 200);
        ball = new Point(PLAY_AREA_CENTER_X, PLAY_AREA_CENTER_Y);
        delta = new Point(5, 5);

        leftScoreLabel = new JLabel();
        highScoreLabel = new JLabel();
        rightScoreLabel = new JLabel();

        updateScoreLabels();

        JPanel scorePanel = new JPanel();

        scorePanel.setLayout(new GridLayout(1, 3));

        scorePanel.add(leftScoreLabel);
        scorePanel.add(highScoreLabel);
        scorePanel.add(rightScoreLabel);

        add(scorePanel, BorderLayout.SOUTH);

        JButton startButton = new JButton("START");

        add(startButton, BorderLayout.NORTH);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setActionCommand(e.getActionCommand());
                clickStartButton();
            }
        });

        setWindowEvents();
    }

    void setActionCommand(String startButtonActionCommand) {
        this.startButtonActionCommand = startButtonActionCommand;
    }
    String getActionCommand() {
        return this.startButtonActionCommand;
    }

    void clickStartButton() {
        play();
    }

    void play() {
        timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBall();
                updatePlayerPaddle();
                updateScores();
                updateScoreLabels();
                repaint();
            }
        });

        timer.start();

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                playerPaddleMovingUp = e.getWheelRotation() < 0;
            }
        });

        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                playerPaddleMovingUp = e.getKeyCode() == VK_UP;
            }
        });
    }

    void updateBall() {

        if (ball.x >= RIGHT_PLAY_AREA_BOUNDARY_X_COORD - BALL_WIDTH
                || ball.x <= LEFT_PLAY_AREA_BOUNDARY_X_COORD) {
            restartGame();
        }
        if (ball.y <= UPPER_PLAY_AREA_BOUNDARY_Y_COORD
                || ball.y >= LOWER_PLAY_AREA_BOUNDARY_Y_COORD - BALL_HEIGHT) {
            delta.y = -delta.y;
        }
        if (ball.x == playerPaddlePosition.x + (isServer? -BALL_WIDTH : PADDLE_WIDTH)
                && ball.y >= playerPaddlePosition.y
                && ball.y <= playerPaddlePosition.y + PADDLE_HEIGHT) {
            delta.x = -delta.x;
        }
        if (ball.x == opponentPaddlePosition.x + (isServer? PADDLE_WIDTH : -BALL_WIDTH)
                && ball.y >= opponentPaddlePosition.y
                && ball.y <= opponentPaddlePosition.y + PADDLE_HEIGHT) {
            delta.x = -delta.x;
        }
        ball.x += delta.x;
        ball.y += delta.y;
    }

    void updateScores() {
        if (ball.x == playerPaddlePosition.x + (isServer? -BALL_WIDTH : PADDLE_WIDTH)
                && ball.y >= playerPaddlePosition.y
                && ball.y <= playerPaddlePosition.y + PADDLE_HEIGHT) {
            playerScore++;
        }
        if (ball.x == opponentPaddlePosition.x + (isServer? PADDLE_WIDTH : -BALL_WIDTH)
                && ball.y >= opponentPaddlePosition.y
                && ball.y <= opponentPaddlePosition.y + PADDLE_HEIGHT) {
            opponentScore++;
        }
    }

    void updatePlayerPaddle() {
        if (playerPaddleMovingUp) {
            playerPaddlePosition
                    .setLocation(playerPaddlePosition.x,
                            playerPaddlePosition.y > UPPER_PLAY_AREA_BOUNDARY_Y_COORD
                                    ? playerPaddlePosition.y - 5
                                    : UPPER_PLAY_AREA_BOUNDARY_Y_COORD);
            if (playerPaddlePosition.y == UPPER_PLAY_AREA_BOUNDARY_Y_COORD) {
                playerPaddleMovingUp = false;
            }
        } else {
            playerPaddlePosition
                    .setLocation(playerPaddlePosition.x,
                            playerPaddlePosition.y < LOWER_PLAY_AREA_BOUNDARY_Y_COORD - PADDLE_HEIGHT
                                    ? playerPaddlePosition.y + 5
                                    : LOWER_PLAY_AREA_BOUNDARY_Y_COORD - PADDLE_HEIGHT);
            if (playerPaddlePosition.y == LOWER_PLAY_AREA_BOUNDARY_Y_COORD - PADDLE_HEIGHT) {
                playerPaddleMovingUp = true;
            }
        }
    }

    void setOpponentPaddlePosition(Point opponentPaddlePosition) {
        this.opponentPaddlePosition.setLocation(opponentPaddlePosition.getLocation());
    }

    Point getPlayerPaddlePosition() {
        return this.playerPaddlePosition.getLocation();
    }

    void setBallPosition(Point ballPosition) {
        this.ball.setLocation(ballPosition.getLocation());
    }

    Point getBallPosition() {
        return this.ball.getLocation();
    }

    private void restartGame() {
        ball.x = PLAY_AREA_CENTER_X;
        ball.y = PLAY_AREA_CENTER_Y;
        playerScore = 0;
        opponentScore = 0;
        highScore = getHighScore();
        updateScoreLabels();
    }

    private void updateScoreLabels() {

        String playerScoreText = "Player: " + playerScore;
        String opponentScoreText = "Opponent: " + opponentScore;

        leftScoreLabel.setText(isServer ? opponentScoreText : playerScoreText);
        highScoreLabel.setText("High Score: " + getHighScore());
        rightScoreLabel.setText(isServer ? playerScoreText : opponentScoreText);
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
                playerPaddlePosition.y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(opponentPaddlePosition.x,
                opponentPaddlePosition.y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillOval(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT);

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
