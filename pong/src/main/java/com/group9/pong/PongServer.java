package com.group9.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Random;

public class PongServer extends JFrame {

  // Constants
  private static final int WIDTH = 800;
  private static final int HEIGHT = 600;
  private static final int PADDLE_WIDTH = 15;
  private static final int PADDLE_HEIGHT = 100;
  private static final int BALL_SIZE = 20;
  private static final int PADDLE_SPEED = 10;
  private static final int INITIAL_BALL_SPEED = 5;
  private static final int PORT = 12345;

  // Network components
  private Socket socket;
  private ServerSocket serverSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private boolean isServer;
  private String serverIP;
  private PongPanel gamePanel;
  private boolean connected = false;

  public PongServer() {
    // Create initial connection panel
    JPanel connectionPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    JButton hostButton = new JButton("Host Game (Server)");
    JButton joinButton = new JButton("Join Game (Client)");
    JTextField ipField = new JTextField("localhost", 15);
    JLabel ipLabel = new JLabel("Server IP:");
    JLabel statusLabel = new JLabel("Select an option to start");

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    connectionPanel.add(statusLabel, gbc);

    gbc.gridy = 1;
    gbc.gridwidth = 1;
    connectionPanel.add(ipLabel, gbc);

    gbc.gridx = 1;
    connectionPanel.add(ipField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    connectionPanel.add(hostButton, gbc);

    gbc.gridx = 1;
    connectionPanel.add(joinButton, gbc);

    // Set up host button action
    hostButton.addActionListener(e -> {
      isServer = true;
      statusLabel.setText("Starting server...");

      new Thread(() -> {
        try {
          serverSocket = new ServerSocket(PORT);
          statusLabel.setText("Waiting for client to connect...");
          socket = serverSocket.accept();

          // Set up streams
          setupStreams(socket);

          // Start the game
          SwingUtilities.invokeLater(() -> startGame());
          statusLabel.setText("Client connected! Starting game...");
        } catch (IOException ex) {
          statusLabel.setText("Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      }).start();
    });

    // Set up join button action
    joinButton.addActionListener(e -> {
      isServer = false;
      serverIP = ipField.getText();
      statusLabel.setText("Connecting to server at " + serverIP + "...");

      new Thread(() -> {
        try {
          socket = new Socket(serverIP, PORT);

          // Set up streams
          setupStreams(socket);

          // Start the game
          SwingUtilities.invokeLater(() -> startGame());
          statusLabel.setText("Connected to server! Starting game...");
        } catch (IOException ex) {
          statusLabel.setText("Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      }).start();
    });

    // Set up frame
    setTitle("Network Pong");
    setSize(WIDTH, HEIGHT);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setLocationRelativeTo(null);

    // Add connection panel
    add(connectionPanel);
    setVisible(true);
  }

  private void setupStreams(Socket s) throws IOException {
    if (isServer) {
      out = new ObjectOutputStream(s.getOutputStream());
      out.flush();
      in = new ObjectInputStream(s.getInputStream());
    } else {
      in = new ObjectInputStream(s.getInputStream());
      out = new ObjectOutputStream(s.getOutputStream());
      out.flush();
    }
    connected = true;
  }

  private void startGame() {
    gamePanel = new PongPanel();

    // Remove connection panel and add game panel
    getContentPane().removeAll();
    add(gamePanel);

    // Start a thread to receive game data
    new Thread(() -> receiveData()).start();

    getContentPane().revalidate();
    getContentPane().repaint();
    gamePanel.requestFocus();
  }

  private void receiveData() {
    try {
      while (connected) {
        GameData data = (GameData) in.readObject();

        // Update game state based on received data
        if (isServer) {
          gamePanel.player2Y = data.myPaddleY;
        } else {
          gamePanel.player1Y = data.myPaddleY;
          gamePanel.ballX = data.ballX;
          gamePanel.ballY = data.ballY;
          gamePanel.player1Score = data.player1Score;
          gamePanel.player2Score = data.player2Score;
          gamePanel.gameRunning = data.gameRunning;
          gamePanel.paused = data.paused;
        }

        // Repaint with new data
        gamePanel.repaint();
      }
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("Connection closed: " + e.getMessage());
      connected = false;
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new PongServer());
  }

  // Game data class for network transmission
  private static class GameData implements Serializable {
    private static final long serialVersionUID = 1L;

    int myPaddleY;
    int ballX;
    int ballY;
    int player1Score;
    int player2Score;
    boolean gameRunning;
    boolean paused;
  }

  // Panel for the game
  private class PongPanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private int player1Y, player2Y;
    private int ballX, ballY;
    private int ballXDir, ballYDir;
    private int player1Score, player2Score;
    private boolean gameRunning;
    private boolean paused;

    public PongPanel() {
      setBackground(Color.BLACK);
      setFocusable(true);
      addKeyListener(this);

      // Initialize game state
      resetGame();

      // Start the game timer
      timer = new Timer(16, this); // ~60fps
      timer.start();
    }

    private void resetGame() {
      // Initial paddle positions
      player1Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
      player2Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;

      // Initial ball position (center)
      ballX = WIDTH / 2 - BALL_SIZE / 2;
      ballY = HEIGHT / 2 - BALL_SIZE / 2;

      // Set initial ball direction
      Random rand = new Random();
      ballXDir = (rand.nextBoolean() ? 1 : -1) * INITIAL_BALL_SPEED;
      ballYDir = (rand.nextBoolean() ? 1 : -1) * INITIAL_BALL_SPEED;

      gameRunning = true;
      paused = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      // Use anti-aliasing for smoother rendering
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      // Draw center line
      g2d.setColor(Color.WHITE);
      g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0));
      g2d.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);

      // Draw paddles
      g2d.setColor(Color.WHITE);
      g2d.fillRect(20, player1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
      g2d.fillRect(WIDTH - 20 - PADDLE_WIDTH, player2Y, PADDLE_WIDTH, PADDLE_HEIGHT);

      // Draw ball
      g2d.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

      // Draw scores
      g2d.setFont(new Font("Arial", Font.BOLD, 30));
      g2d.drawString(String.valueOf(player1Score), WIDTH / 4, 50);
      g2d.drawString(String.valueOf(player2Score), 3 * WIDTH / 4, 50);

      // Draw connection status
      String playerType = isServer ? "Server (Left Paddle)" : "Client (Right Paddle)";
      g2d.setFont(new Font("Arial", Font.PLAIN, 12));
      g2d.drawString(playerType, 10, HEIGHT - 10);

      // Draw pause message
      if (paused) {
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.drawString("PAUSED - Press SPACE to resume", WIDTH / 2 - 240, HEIGHT / 2);
      }

      // Draw game start instruction
      if (!gameRunning && player1Score == 0 && player2Score == 0) {
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        if (isServer) {
          g2d.drawString("Press SPACE to start", WIDTH / 2 - 150, HEIGHT / 2);
        } else {
          g2d.drawString("Waiting for server to start...", WIDTH / 2 - 180, HEIGHT / 2);
        }
      }

      // Draw winner message
      if (!gameRunning && (player1Score > 0 || player2Score > 0)) {
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        if (player1Score >= 10) {
          g2d.drawString("Left Player Wins!", WIDTH / 2 - 120, HEIGHT / 2);
        } else if (player2Score >= 10) {
          g2d.drawString("Right Player Wins!", WIDTH / 2 - 120, HEIGHT / 2);
        }
        if (isServer) {
          g2d.setFont(new Font("Arial", Font.PLAIN, 20));
          g2d.drawString("Press SPACE to play again", WIDTH / 2 - 130, HEIGHT / 2 + 40);
        }
      }
    }

    private void update() {
      if (!gameRunning || paused) {
        return;
      }

      // Server controls game logic
      if (isServer) {
        // Move the ball
        ballX += ballXDir;
        ballY += ballYDir;

        // Ball collision with top and bottom walls
        if (ballY <= 0 || ballY >= HEIGHT - BALL_SIZE) {
          ballYDir = -ballYDir;
        }

        // Ball collision with player 1 paddle
        if (ballX <= 20 + PADDLE_WIDTH && ballY + BALL_SIZE >= player1Y && ballY <= player1Y + PADDLE_HEIGHT) {
          ballXDir = Math.abs(ballXDir); // Ensure positive x direction (right)
          // Add a little randomness to y direction
          if (Math.random() > 0.5) {
            ballYDir += (Math.random() > 0.5) ? 1 : -1;
          }
        }

        // Ball collision with player 2 paddle
        if (ballX + BALL_SIZE >= WIDTH - 20 - PADDLE_WIDTH && ballY + BALL_SIZE >= player2Y && ballY <= player2Y + PADDLE_HEIGHT) {
          ballXDir = -Math.abs(ballXDir); // Ensure negative x direction (left)
          // Add a little randomness to y direction
          if (Math.random() > 0.5) {
            ballYDir += (Math.random() > 0.5) ? 1 : -1;
          }
        }

        // Limit ball Y speed
        ballYDir = Math.max(-8, Math.min(8, ballYDir));

        // Ball out of bounds (score)
        if (ballX < 0) {
          // Player 2 scores
          player2Score++;
          if (player2Score >= 10) {
            gameRunning = false;
          } else {
            resetBall();
          }
        } else if (ballX > WIDTH) {
          // Player 1 scores
          player1Score++;
          if (player1Score >= 10) {
            gameRunning = false;
          } else {
            resetBall();
          }
        }
      }

      // Send game data
      sendGameData();
    }

    private void sendGameData() {
      try {
        GameData data = new GameData();

        // Each player sends their paddle position
        if (isServer) {
          data.myPaddleY = player1Y;
          data.ballX = ballX;
          data.ballY = ballY;
          data.player1Score = player1Score;
          data.player2Score = player2Score;
          data.gameRunning = gameRunning;
          data.paused = paused;
        } else {
          data.myPaddleY = player2Y;
        }

        out.writeObject(data);
        out.flush();
        out.reset(); // Clear the cache to avoid memory issues
      } catch (IOException e) {
        System.out.println("Error sending data: " + e.getMessage());
        connected = false;
      }
    }

    private void resetBall() {
      ballX = WIDTH / 2 - BALL_SIZE / 2;
      ballY = HEIGHT / 2 - BALL_SIZE / 2;

      // Reset ball direction with a bit of randomness
      Random rand = new Random();
      ballXDir = (rand.nextBoolean() ? 1 : -1) * INITIAL_BALL_SPEED;
      ballYDir = (rand.nextBoolean() ? 1 : -1) * INITIAL_BALL_SPEED;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      update();
      repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();

      // Server controls left paddle (player 1)
      if (isServer) {
        if (key == KeyEvent.VK_W && player1Y > 0) {
          player1Y -= PADDLE_SPEED;
        }
        if (key == KeyEvent.VK_S && player1Y < HEIGHT - PADDLE_HEIGHT) {
          player1Y += PADDLE_SPEED;
        }

        // Space to start/restart/pause game (server only)
        if (key == KeyEvent.VK_SPACE) {
          if (!gameRunning) {
            player1Score = 0;
            player2Score = 0;
            resetGame();
          } else {
            paused = !paused;
          }
        }
      } 
      // Client controls right paddle (player 2)
      else {
        if (key == KeyEvent.VK_UP && player2Y > 0) {
          player2Y -= PADDLE_SPEED;
        }
        if (key == KeyEvent.VK_DOWN && player2Y < HEIGHT - PADDLE_HEIGHT) {
          player2Y += PADDLE_SPEED;
        }
      }

      // Escape to exit
      if (key == KeyEvent.VK_ESCAPE) {
        try {
          if (socket != null) socket.close();
          if (serverSocket != null) serverSocket.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
        System.exit(0);
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
      // Not needed for this implementation
    }

    @Override
    public void keyTyped(KeyEvent e) {
      // Not needed for this implementation
    }
  }
}
