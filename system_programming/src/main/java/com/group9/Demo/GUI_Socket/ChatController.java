package com.group9.Demo.GUI_Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import javax.swing.JOptionPane;

import com.group9.Demo.GUI_Socket.ChatView.ConnectionStatus;
import com.group9.net.NetworkScanner;

public class ChatController {
  private ChatView view;

  private static final int SERVER_PORT = 8079;
  private Socket clientSocket;
  private PrintWriter out;

  public ChatController(ChatView view) {
    this.view = view;

    new Thread(() -> startServer()).start();

    String localIP = NetworkScanner.getHostPrivateAddress();
    view.setIpLabel(localIP);

    view.onMessageActionPerformed(() -> sendMessage());
    view.onSendPressed(() -> {
      if (clientSocket == null || clientSocket.isClosed()) {
        new Thread(() -> startClient()).start();
      }
      sendMessage();
    });
  }

  public ChatView getView() {
    return view;
  }

    // hàm chạy server để lắng nghe kết nối từ máy khác
  public void startServer() {
    try (ServerSocket serversocket = new ServerSocket(SERVER_PORT)) {
      System.out.println("server is listening on port " + SERVER_PORT);
      clientSocket = serversocket.accept();
      out = new PrintWriter(clientSocket.getOutputStream());
      view.setConnectionStatus(ConnectionStatus.CONNECTED);

      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      while(!clientSocket.isClosed()) {
        receiveMessage(input);
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  public void startClient() {
    try {
      String peer_ip = view.getTargetIp();
      clientSocket = new Socket(peer_ip, SERVER_PORT);
      System.out.println("Connected to " + peer_ip + ":" + SERVER_PORT);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      view.setConnectionStatus(ConnectionStatus.CONNECTED);

      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      while (!clientSocket.isClosed()) {
        receiveMessage(input);
      }
    } catch (IOException e) {
      System.out.println("Host not found!");
      JOptionPane.showMessageDialog(
        view,
        "Connection failed: Host is not available!",
        "connection error",
        JOptionPane.ERROR_MESSAGE
      );
      view.setConnectionStatus(ConnectionStatus.CONNECTION_FAILED);
      return;
    }
  }

  private void receiveMessage(BufferedReader in) throws IOException {
    String message;
    while ((message = in.readLine()) != null) {
      System.out.println("received from other device: " + message);
      view.insertMessage(
        "[" + clientSocket.getInetAddress().getHostAddress() + "]: " + message + "\n"
      );
    }
  }

  private void sendMessage() {
    if (out == null) return;
    String message = view.getMessageText().trim();
    if (!message.isEmpty()) {
      out.println(message);
      view.insertMessage("[You]: " + message + "\n");
      view.clearMessageField();
    }
  }
}
