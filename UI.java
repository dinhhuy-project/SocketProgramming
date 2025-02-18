import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.jar.JarOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class UI extends JFrame {
  private static JTextPane textPane;
  private static StyledDocument doc;
  private static SimpleAttributeSet leftAlign;
  private static SimpleAttributeSet rightAlign;
  private static JButton sendButton;
  private static JTextField messageField;
  private static JTextField IPField;

  private static final int SERVER_PORT = 5000; // Port của server
  private static String PEER_IP = "192.168.1.5"; // Địa chỉ IP của máy kia
  private static final int PEER_PORT = 5001; // Port của máy kia
  private static PrintWriter out;
  private static Socket clientSocket;

  public UI() {
    setTitle("Messenger - Java Chat");
    setSize(400, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Chat Area
    textPane = new JTextPane();
    textPane.setEditable(false);
    textPane.setMargin(new Insets(10, 10, 10, 10));
    doc = textPane.getStyledDocument();

    // Tạo style căn trái
    leftAlign = new SimpleAttributeSet();
    StyleConstants.setAlignment(leftAlign, StyleConstants.ALIGN_LEFT);
    StyleConstants.setFontSize(leftAlign, 16);

    // Tạo style căn phải
    rightAlign = new SimpleAttributeSet();
    StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
    StyleConstants.setFontSize(rightAlign, 16);

    add(new JScrollPane(textPane), BorderLayout.CENTER);
    
    // Input Panel
    JPanel inputPanel = new JPanel(new BorderLayout());
    IPField = new JTextField();
    messageField = new JTextField();
    sendButton = new JButton("Send");

    inputPanel.add(IPField, BorderLayout.NORTH);
    inputPanel.add(messageField, BorderLayout.CENTER);
    inputPanel.add(sendButton, BorderLayout.EAST);

    add(inputPanel, BorderLayout.SOUTH);

    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendMessage();
      }
    });
    messageField.addActionListener(e -> sendMessage());
    setVisible(true);
    new Peer();
  }

  private class Peer {
    public Peer() {
      // Chạy server trong một luồng riêng
      new Thread(() -> startServer()).start();

      // Chạy client sau một khoảng thời gian nhỏ để đảm bảo server khởi động trước
      // try {
      //   Thread.sleep(1000);
      // } catch (InterruptedException e) {
      //   e.printStackTrace();
      // }
      // startClient();
    }

    // Hàm chạy Server để lắng nghe kết nối từ máy khác
    public static void startServer() {
      try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
        System.out.println("Server is listening on port " + SERVER_PORT);
        Socket socket = serverSocket.accept();
        System.out.println("Connect from: " + socket.getInetAddress());

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String message;
        while ((message = input.readLine()) != null) {
          System.out.println("Received from other device: " + message);
          try {
            doc.setParagraphAttributes(doc.getLength(), 1, leftAlign, false);
            doc.insertString(doc.getLength(), message + "\n", leftAlign);
          } catch (BadLocationException e) {
            e.printStackTrace();
          }
        }

        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // Hàm chạy Client để gửi tin nhắn đến máy kia
    public static void startClient() {
      try {
        clientSocket = new Socket(PEER_IP, PEER_PORT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        System.out.println("Connected to " + PEER_IP + ":" + PEER_PORT);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendMessage() {
    if (!PEER_IP.equals(IPField.getText())) {
      try {
        clientSocket.close();
      } catch (IOException e) {
        return;
      }
    }
    if (clientSocket == null || clientSocket.isClosed()) {
      try {
        PEER_IP = IPField.getText();
        clientSocket = new Socket(PEER_IP, PEER_PORT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        System.out.println("Connected to " + PEER_IP + ":" + PEER_PORT);
      } catch (IOException e) {
        System.out.println("IP Address's not existed!");
        return;
      }
    }
    
    String message = messageField.getText().trim();
    if (!message.isEmpty()) {
      out.println(message);
      try {
        doc.setParagraphAttributes(doc.getLength(), 1, rightAlign, false);
        doc.insertString(doc.getLength(), message + "\n", rightAlign);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
      messageField.setText("");
    }
  }

  public static void main(String[] args) {
    new UI();
  }
}
