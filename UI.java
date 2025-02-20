import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
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
  private static JLabel statusLabel;

  private static final int SERVER_PORT = 5001; // Port của server
  private static String PEER_IP = "192.168.1.5"; // Địa chỉ IP của máy kia
  private static final int PEER_PORT = 5000; // Port của máy kia
  private static PrintWriter out;
  private static Socket clientSocket;

  public UI() {
    setTitle("Messenger - Java Chat");
    setSize(600, 800);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Chat Area
    textPane = new JTextPane();
    textPane.setEditable(false);
    textPane.setMargin(new Insets(10, 10, 10, 10));
    textPane.setFont(new Font("Arial", Font.PLAIN, 18));
    doc = textPane.getStyledDocument();

    // Tạo style căn trái
    leftAlign = new SimpleAttributeSet();
    StyleConstants.setAlignment(leftAlign, StyleConstants.ALIGN_LEFT);
    StyleConstants.setFontSize(leftAlign, 18);

    // Tạo style căn phải
    rightAlign = new SimpleAttributeSet();
    StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
    StyleConstants.setFontSize(rightAlign, 18);

    add(new JScrollPane(textPane), BorderLayout.CENTER);

    // Input Panel
    JPanel inputPanel = new JPanel(new BorderLayout());
    inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    JLabel inputLabel = new JLabel("Enter IP and Message:");
    inputPanel.add(inputLabel, BorderLayout.NORTH);

    IPField = new JTextField();
    IPField.setBorder(BorderFactory.createTitledBorder("IP Address"));
    IPField.setFont(new Font("Arial", Font.ROMAN_BASELINE, 18));
    messageField = new JTextField();
    messageField.setBorder(BorderFactory.createTitledBorder("Message"));
    messageField.setFont(new Font("Arial", Font.ROMAN_BASELINE, 18));
    sendButton = new JButton("Send");

    JPanel fieldsPanel = new JPanel(new BorderLayout());
    fieldsPanel.add(IPField, BorderLayout.NORTH);
    fieldsPanel.add(messageField, BorderLayout.CENTER);

    inputPanel.add(fieldsPanel, BorderLayout.CENTER);
    inputPanel.add(sendButton, BorderLayout.EAST);

    add(inputPanel, BorderLayout.SOUTH);

    // Status Bar
    JPanel statusPanel = new JPanel(new BorderLayout());
    statusLabel = new JLabel("Not Connected");
    statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
    statusLabel.setForeground(Color.RED);
    statusPanel.add(statusLabel, BorderLayout.WEST);

    JLabel ipLabel = new JLabel();
    try {
      String localIP = InetAddress.getLocalHost().getHostAddress();
      ipLabel.setText("My IP: " + localIP + ":" + SERVER_PORT);
    } catch (UnknownHostException e) {
      ipLabel.setText("My IP: Unknown");
    }
    ipLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
    statusPanel.add(ipLabel, BorderLayout.EAST);

    add(statusPanel, BorderLayout.NORTH);

    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendMessage();
      }
    });
    messageField.addActionListener(e -> sendMessage());

    setLocationRelativeTo(null);
    setVisible(true);
    new Peer();
  }

  private class Peer {
    public Peer() {
      // Chạy server trong một luồng riêng
      new Thread(() -> startServer()).start();
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
        statusLabel.setText("Connected to " + PEER_IP + ":" + PEER_PORT);
        statusLabel.setForeground(Color.GREEN);
      } catch (IOException e) {
        e.printStackTrace();
        statusLabel.setText("Connection Failed");
        statusLabel.setForeground(Color.RED);
      }
    }
  }

  private void sendMessage() {
    if (clientSocket == null || clientSocket.isClosed()) {
      try {
        PEER_IP = IPField.getText();
        clientSocket = new Socket(PEER_IP, PEER_PORT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        System.out.println("Connected to " + PEER_IP + ":" + PEER_PORT);
        statusLabel.setText("Connected to " + PEER_IP + ":" + PEER_PORT);
        statusLabel.setForeground(Color.GREEN);
      } catch (IOException e) {
        System.out.println("IP Address's not existed!");
        JOptionPane.showMessageDialog(this, "Connection Failed: IP Address does not exist!", "Connection Error",
            JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("Connection Failed");
        statusLabel.setForeground(Color.RED);
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
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    new UI();
  }
}