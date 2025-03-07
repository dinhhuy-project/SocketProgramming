package com.group9.pong;

import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;

class PongPanel extends JPanel implements ActionListener {
  GameData states = new GameData();

  // Timer object control the flow of time of the game
  Timer timer;

  public PongPanel() {
    this.setPreferredSize(new Dimension(GameData.VIEW_WIDTH, GameData.VIEW_HEIGHT));
    this.setBackground(Color.BLACK);
    this.setFocusable(true);

    timer = new Timer(16, this);
    timer.start();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(Color.WHITE);

    // Draw paddles and ball
    g.fillRect(50, states.paddle1Y, GameData.PADDLE_WIDTH, GameData.PADDLE_HEIGHT);
    g.fillRect(GameData.VIEW_WIDTH - 60, states.paddle2Y, GameData.PADDLE_WIDTH, GameData.PADDLE_HEIGHT);
    g.fillOval(states.ballX, states.ballY, GameData.BALL_SIZE, GameData.BALL_SIZE);

    // Draw scores
    g2d.setFont(new Font("Arial", Font.BOLD, 40));
    g2d.drawString(
      String.valueOf(states.score1) + "  |  " + String.valueOf(states.score2),
      GameData.VIEW_WIDTH / 2 - 30,
      50
    );
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  public void update(GameData gameData) {
    states = gameData;
    repaint();
  }
}
