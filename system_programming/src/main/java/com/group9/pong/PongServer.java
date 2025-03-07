package com.group9.pong;

import javax.swing.*;

import com.group9.net.NetworkScanner;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class PongServer extends JFrame {
  private static final int PORT = 8079;

  GameData states;

  // Network components
  private ServerSocket serverSocket;
  private boolean stopped;

  private Socket player1Socketh = null;
  private DataInputStream in1;
  private DataOutputStream out1;
  private Socket player2Socket = null;
  private DataInputStream in2;
  private DataOutputStream out2;

  public PongServer() {
    try {
      stopped = false;
      serverSocket = new ServerSocket(PORT);
      String serverIP = NetworkScanner.getHostPrivateAddress();
      System.out.println(serverIP + " listening on port: " + PORT);
      while (!stopped) {
        Socket socket = serverSocket.accept();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> handleClient(socket, in, out)).start();
      }

      ExecutorService pool = Executors.newFixedThreadPool(2);
      pool.execute(() -> receivePacket(in1));
      pool.execute(() -> receivePacket(in2));

      gameLoop();
    } catch (IOException ex) {
    }
  }

  private void handleClient(Socket socket, DataInputStream in, DataOutputStream out) {
  }

  private void receivePacket(DataInputStream in) {
    PongPacket packet;
  }

  private void sendPacket(DataOutputStream out) {
    try {
      out.write(states.toBytes());
    } catch (IOException ex) {}
  }

  private int playerWon() {
    if (states.score1 >= 10) return 1;
    if (states.score2 >= 10) return 2;
    return 0;
  }

  private void shutdown() {
    stopped = true;
  }

  private void gameLoop() {
    states.reset();
    long previous;
    while (playerWon() == 0) {
      previous = System.currentTimeMillis(); 

      // Move paddles
      states.paddle1Y += states.paddle1Speed;
      states.paddle2Y += states.paddle2Speed;

      // Ball movement
      states.ballX += states.ballXSpeed;
      states.ballY += states.ballYSpeed;

      // Ball collision with top and bottom
      if (states.ballY <= 0 || states.ballY >= GameData.VIEW_HEIGHT - GameData.BALL_SIZE) {
        states.ballYSpeed *= -1;
      }

      // Ball out of bounds
      if (states.ballX < 0 || states.ballX > GameData.VIEW_WIDTH) {
        states.ballX = GameData.VIEW_WIDTH / 2;
        states.ballY = GameData.VIEW_HEIGHT / 2;
        if (states.ballXSpeed > 0) states.score1++;
        else states.score2++;
        states.ballXSpeed *= -1;
      }

      // Ball collision with paddles
      if ((states.ballX <= 60 && states.ballY + GameData.BALL_SIZE >= states.paddle1Y && states.ballY <= states.paddle1Y + GameData.PADDLE_HEIGHT)
      || (states.ballX >= GameData.VIEW_WIDTH - 75 && states.ballY + GameData.BALL_SIZE >= states.paddle2Y && states.ballY <= states.paddle2Y + GameData.PADDLE_HEIGHT)) {
        states.ballXSpeed *= -1;
      }

      System.out.println(states);
      sendPacket(out1);
      sendPacket(out2);

      long current = System.currentTimeMillis() - previous;
      if (current < 16) {
        try { Thread.sleep(16 - current); } catch (InterruptedException ex) {};
      }
    }
  }
}
