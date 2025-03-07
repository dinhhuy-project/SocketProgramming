package com.group9.Demo.GUI_Socket;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      ChatController controller = new ChatController(new ChatView()); 
      JFrame frame = new JFrame();
      frame.setTitle("Java Chat");
      frame.setSize(600, 800);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
      frame.add(controller.getView());
    });
  }
}
