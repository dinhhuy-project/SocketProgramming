package com.group9.pong;

import java.io.PrintStream;
import java.nio.ByteBuffer;

// Game data class for network transmission
class GameData {
  // Constants
  public static final int VIEW_WIDTH = 800;
  public static final int VIEW_HEIGHT = 600;

  public static final int PADDLE_WIDTH = 10;
  public static final int PADDLE_HEIGHT = 100;
  public static final int PADDLE_SPEED = 10;

  public static final int BALL_SIZE = 15;
  public static final int INITIAL_BALL_SPEED = 5;

  public int ballX, ballY;
  public int ballXSpeed, ballYSpeed;
  public int paddle1Y, paddle2Y;
  public int paddle1Speed, paddle2Speed;
  public int score1, score2;

  public GameData() {
    reset();
  }

  public void reset() {
    paddle1Y = GameData.VIEW_HEIGHT / 2 - GameData.PADDLE_HEIGHT / 2;
    paddle1Speed = 0;

    paddle2Y = GameData.VIEW_HEIGHT / 2 - GameData.PADDLE_HEIGHT / 2;
    paddle2Speed = 0;

    ballX = GameData.VIEW_WIDTH / 2;
    ballY = GameData.VIEW_HEIGHT / 2;

    ballXSpeed = GameData.INITIAL_BALL_SPEED; 
    ballYSpeed = GameData.INITIAL_BALL_SPEED;

    score1 = 0;
    score2 = 0;
  }

  public GameData(int paddle1y, int paddle2y, int ballX, int ballY, int ballXSpeed, int ballYSpeed, int paddle1Speed,
      int paddle2Speed, int score1, int score2) {
    this.paddle1Y = paddle1y;
    this.paddle2Y = paddle2y;
    this.ballX = ballX;
    this.ballY = ballY;
    this.ballXSpeed = ballXSpeed;
    this.ballYSpeed = ballYSpeed;
    this.paddle1Speed = paddle1Speed;
    this.paddle2Speed = paddle2Speed;
    this.score1 = score1;
    this.score2 = score2;
  }

  // Serialize the object into a byte array
  public byte[] toBytes() {
    ByteBuffer buffer = ByteBuffer.allocate(40); // 10 integers * 4 bytes each
    buffer.putInt(ballX);
    buffer.putInt(ballY);
    buffer.putInt(ballXSpeed);
    buffer.putInt(ballYSpeed);
    buffer.putInt(paddle1Y);
    buffer.putInt(paddle2Y);
    buffer.putInt(paddle1Speed);
    buffer.putInt(paddle2Speed);
    buffer.putInt(score1);
    buffer.putInt(score2);
    return buffer.array();
  }

  // Deserialize a byte array back into a GameData object
  public static GameData fromBytes(byte[] bytes) {
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    int ballX = buffer.getInt();
    int ballY = buffer.getInt();
    int ballXSpeed = buffer.getInt();
    int ballYSpeed = buffer.getInt();
    int paddle1Y = buffer.getInt();
    int paddle2Y = buffer.getInt();
    int paddle1Speed = buffer.getInt();
    int paddle2Speed = buffer.getInt();
    int score1 = buffer.getInt();
    int score2 = buffer.getInt();
    return new GameData(
      ballX, ballY,
      ballXSpeed, ballYSpeed,
      paddle1Y, paddle2Y,
      paddle1Speed, paddle2Speed,
      score1, score2
    );
  }

  @Override
  public String toString() {
    return "GameData{" +
    "ballX=" + ballX + ", ballY=" + ballY +
    "ballXSpeed=" + ballXSpeed + ", ballY=" + ballYSpeed +
    ", paddle1Y=" + paddle1Y + ", paddle2Y=" + paddle2Y +
    ", paddle1Speed=" + paddle1Speed + ", paddle2Speed=" + paddle2Speed +
    ", score1=" + score1 + ", score2=" + score2 +
    '}';
  }
}
