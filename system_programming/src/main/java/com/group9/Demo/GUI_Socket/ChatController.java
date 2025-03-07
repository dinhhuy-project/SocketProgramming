package com.group9.Demo.GUI_Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import com.group9.Demo.GUI_Socket.ChatView.ConnectionStatus;
import com.group9.net.NetworkScanner;

public class ChatController {
  private ChatView view;

  private static final int SERVER_PORT = 8079;
  private static final int CLIENT_PORT = 8078;

  private PrintWriter out;
  private Socket clientSocket;

  public ChatController(ChatView view) {
    this.view = view;

    new Thread(() -> startServer()).start();
    view.onMessageActionPerformed(e -> sendmessage());
    String localIP = NetworkScanner.getHostPrivateAddress();
    view.setIpLabel(localIP);
  }

  public ChatView getView() {
    return view;
  }

    // hàm chạy server để lắng nghe kết nối từ máy khác
  public void startServer() {
    try (ServerSocket serversocket = new ServerSocket(SERVER_PORT)) {
      System.out.println("server is listening on port " + SERVER_PORT);
      Socket socket = serversocket.accept();

      BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String message;
      while ((message = input.readLine()) != null) {
        System.out.println("received from other device: " + message);
        view.insertMessage(message);
      }

      socket.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  public void startClient() {
    try {
      String peer_ip = view.getTargetIp();
      clientSocket = new Socket(peer_ip, SERVER_PORT);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      System.out.println("Connected to " + peer_ip + ":" + SERVER_PORT);
      view.setConnectionStatus(ConnectionStatus.CONNECTED);
    } catch (IOException e) {
      System.out.println("Host not found!");
      JOptionPane.showMessageDialog(view, "Connection failed: ip address does not exist!", "connection error",
        JOptionPane.ERROR_MESSAGE);
      view.setConnectionStatus(ConnectionStatus.CONNECTION_FAILED);
      return;
    }
  }

  private void sendmessage() {
    if (clientSocket == null || clientSocket.isClosed()) {
      startClient();
    }

    String message = view.getMessageText().trim();
    if (!message.isEmpty()) {
      out.println(message);
      view.insertMessage(message + "\n");
      view.clearMessageField();
    }
  }
}
