package com.group9.Demo.GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class ChatView extends JPanel {
  public static enum ConnectionStatus {
    NOT_CONNECTED,
    WAITING_TO_CONNECT,
    CONNECTED,
    CONNECTION_FAILED
  }

  private JTextPane textPane;
  private StyledDocument doc;
  private JButton sendButton;
  private JTextField messageField;
  private JTextField IPField;
  private JLabel statusLabel;
  private JLabel ipLabel;

  public ChatView() {
    setLayout(new BorderLayout());
    // Chat Area
    textPane = new JTextPane();
    textPane.setEditable(false);
    textPane.setFocusable(false);
    textPane.setMargin(new Insets(10, 10, 10, 10));
    textPane.setFont(new Font("Arial", Font.PLAIN, 18));
    doc = textPane.getStyledDocument();

    add(new JScrollPane(textPane), BorderLayout.CENTER);

    // Input Panel
    JPanel inputPanel = new JPanel(new BorderLayout());
    {
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
    }

    JPanel fieldsPanel = new JPanel(new BorderLayout());
    {
      fieldsPanel.add(IPField, BorderLayout.NORTH);
      fieldsPanel.add(messageField, BorderLayout.CENTER);

      inputPanel.add(fieldsPanel, BorderLayout.CENTER);
      inputPanel.add(sendButton, BorderLayout.EAST);
    }

    add(inputPanel, BorderLayout.SOUTH);

    // Status Bar
    JPanel statusBar = new JPanel(new BorderLayout());
    {
      statusLabel = new JLabel();
      statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
      setConnectionStatus(ConnectionStatus.NOT_CONNECTED);
      statusBar.add(statusLabel, BorderLayout.WEST);

      ipLabel = new JLabel();
      ipLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
      setIpLabel("Unknown");
      statusBar.add(ipLabel, BorderLayout.EAST);
    }

    add(statusBar, BorderLayout.NORTH);
    setVisible(true);
  }

  public void setIpLabel(String label) {
    ipLabel.setText("My IP: " + label);
  }

  public void onSendPressed(ActionListener listener) {
    sendButton.addActionListener(listener);
  }

  public void onMessageActionPerformed(ActionListener listener) {
    messageField.addActionListener(listener);
  }

  public void insertMessage(String message) {
    try {
      doc.insertString(doc.getLength(), message, null);
    } catch (BadLocationException ex) {
      ex.printStackTrace();
    }
  }

  public String getMessageText() {
    return messageField.getText();
  }

  public void clearMessageField() {
    messageField.setText("");
  }

  public String getTargetIp() {
    return IPField.getText();
  }

  public void setConnectionStatus(ConnectionStatus status) {
    String statusText = "";
    Color statusColor = Color.BLACK;
    switch (status) {
      case CONNECTED:
        statusText = "Connected";
        statusColor = Color.GREEN;
        break;
      case WAITING_TO_CONNECT:
        statusText = "Waiting to connect...";
        statusColor = Color.YELLOW;
        break;
      case NOT_CONNECTED:
        statusText = "Not Connected";
        statusColor = Color.RED;
        break;
      case CONNECTION_FAILED:
        statusText = "Connection Failed";
        statusColor = Color.RED;
        break;
    }
    statusLabel.setForeground(statusColor);
    statusLabel.setText(statusText);
  }
}
