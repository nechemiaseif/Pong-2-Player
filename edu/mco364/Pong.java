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

    public Pong(String title) {

        super(title);
        setVisible(true);
        setSize(700, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        isServer = title.equals("Pong Server") ? true : false;

        playerPaddlePosition = isServer
                ? new Point(620, 200) : new Point(70, 200);
        opponentPaddlePosition = isServer
                ? new Point(70, 200): new Point(620, 200);
        ball = new Point(335, 235);
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
                updateScoreLabels();
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
        });

    }

    void updateBall() {

        if (ball.x >= 635 || ball.x <= 50) {
            restartGame();
        }
        if (ball.y <= 50 || ball.y >= 435) {
            delta.y = -delta.y;
        }
        if (ball.x == playerPaddlePosition.x + (isServer? -15 : 10)
                && ball.y >= playerPaddlePosition.y
                && ball.y <= playerPaddlePosition.y + 60) {
            delta.x = -delta.x;
            playerScore++;
        }
        if (ball.x == opponentPaddlePosition.x + (isServer? 10 : -15)
                && ball.y >= opponentPaddlePosition.y
                && ball.y <= opponentPaddlePosition.y + 60) {
            delta.x = -delta.x;
            opponentScore++;
        }
        ball.x += delta.x;
        ball.y += delta.y;
    }

    void updatePlayerPaddle() {
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
        ball.x = 335;
        ball.y = 235;
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
