package com.group9.pong;

import java.awt.event.*;
import java.io.IOException;
import java.awt.*;
import javax.swing.*;

import com.group9.net.NetworkScanner;

public class Pong {
  public static void main( String[] args ) throws IOException {
    NetworkScanner.main(args);
    JFrame frame = new JFrame("Pong Game");
    GamePanel panel = new GamePanel();

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.add(panel);
    frame.pack();
    frame.setVisible(true);
  }
}

class GamePanel extends JPanel implements ActionListener {
  private final int VIEW_WIDTH = 800, VIEW_HEIGHT = 600;
  private final int PADDLE_WIDTH = 10, PADDLE_HEIGHT = 100;
  private final int BALL_SIZE = 15;

  private int paddle1Y = VIEW_HEIGHT / 2 - PADDLE_HEIGHT / 2;
  private int paddle2Y = VIEW_HEIGHT / 2 - PADDLE_HEIGHT / 2;
  private int ballX = VIEW_WIDTH / 2, ballY = VIEW_HEIGHT / 2;
  private int ballXSpeed = 4, ballYSpeed = 4;
  private int PADDLE_SPEED = 10;
  private int paddle1Speed = 0, paddle2Speed = 0;
  
  private int p1Score = 0;
  private int p2Score = 0;

  Timer timer;

  public GamePanel() {
    this.setPreferredSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));
    this.setBackground(Color.BLACK);
    this.setFocusable(true);
    this.addKeyListener(new KeyHandler());

    timer = new Timer(10, this);
    timer.start();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setColor(Color.WHITE);

    // Draw paddles and ball
    g.fillRect(50, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
    g.fillRect(VIEW_WIDTH - 60, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);
    g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // Move paddles
    paddle1Y += paddle1Speed;
    paddle2Y += paddle2Speed;

    // Ball movement
    ballX += ballXSpeed;
    ballY += ballYSpeed;

    // Ball collision with top and bottom
    if (ballY <= 0 || ballY >= VIEW_HEIGHT - BALL_SIZE) {
      ballYSpeed *= -1;
    }

    // Ball out of bounds
    if (ballX < 0 || ballX > VIEW_WIDTH) {
      ballX = VIEW_WIDTH / 2;
      ballY = VIEW_HEIGHT / 2;
      if (ballXSpeed > 0) 
      ballXSpeed *= -1;
    }

    // Ball collision with paddles
    if ((ballX <= 60 && ballY + BALL_SIZE >= paddle1Y && ballY <= paddle1Y + PADDLE_HEIGHT) ||
  (ballX >= VIEW_WIDTH - 75 && ballY + BALL_SIZE >= paddle2Y && ballY <= paddle2Y + PADDLE_HEIGHT)) {
      ballXSpeed *= -1;
    }

    repaint();
  }

  private class KeyHandler extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_W) paddle1Speed = -PADDLE_SPEED;
      if (e.getKeyCode() == KeyEvent.VK_S) paddle1Speed = PADDLE_SPEED;
      if (e.getKeyCode() == KeyEvent.VK_UP) paddle2Speed = -PADDLE_SPEED;
      if (e.getKeyCode() == KeyEvent.VK_DOWN) paddle2Speed = PADDLE_SPEED;
    }

    public void keyReleased(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S) paddle1Speed = 0;
      if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) paddle2Speed = 0;
    }
  }
}
