package com.group9.pong;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;

import com.group9.net.NetworkScanner;

public class MainPanel extends JPanel {
  PongPanel game;

  MainPanel(JFrame main) {
    // Create initial connection panel
    this.setLayout(new BorderLayout());
    this.setMinimumSize(new Dimension(600, 400));
    this.setPreferredSize(new Dimension(600, 400));
    this.setFont(new Font("Arial", Font.BOLD, 18));

    GridBagConstraints gbc;

    JLabel statusLabel = new JLabel("Select an option to start");
    JList<String> serverList = new JList<String>(
      new String[] {
        " ", " ",
        " ", " ",
        " ", " ",
        " ", " ",
        " ", " ",
      }
    );

    JPanel viewPanel = new JPanel(new GridBagLayout());
  {
      JButton joinButton = new JButton("Join Game");
      joinButton.setVisible(false);

      viewPanel.setBorder(new CompoundBorder(
        new EmptyBorder(new Insets(10, 10, 10, 10)),
        new LineBorder(Color.GRAY, 1, true)
      ));
      gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 1;
      gbc.insets = new Insets(10, 10, 10, 10);

      viewPanel.add(new JLabel("Available servers"), gbc);
      gbc.gridy++;
      viewPanel.add(serverList, gbc);
      gbc.gridy++;
      viewPanel.add(joinButton, gbc);
      gbc.gridy++;
      viewPanel.add(new JLabel("Connection status: "), gbc);
      gbc.gridy++;
      viewPanel.add(statusLabel, gbc);
      gbc.gridy++;
      gbc.weighty = 1;
      gbc.fill = GridBagConstraints.VERTICAL;
      viewPanel.add(Box.createVerticalGlue(), gbc);

      // Add join button action
      joinButton.addActionListener((e) -> {
        String hostIp = serverList.getSelectedValue();
        if (hostIp == null) return;

        String myIp = NetworkScanner.getHostPrivateAddress();
        if (!hostIp.split(":")[0].equals(myIp)) {
          game = new PongPanel();
          main.remove(this);
          main.dispose();
          JFrame jFrame = new JFrame("Pong game");
          jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          jFrame.setResizable(false);
          jFrame.add(game);
          jFrame.pack();
          jFrame.setVisible(true);
          jFrame.setLocationRelativeTo(null);
          return;
        }
      });

      serverList.addListSelectionListener((e) -> {
        String value = serverList.getSelectedValue();
        if (value == null) return;

        String myIp = NetworkScanner.getHostPrivateAddress();
        joinButton.setVisible(value != null && !value.isBlank());
        joinButton.setEnabled(true);
        if (value.split(":")[0].equals(myIp)) {
          joinButton.setEnabled(false);
          return;
        }
      });

    }

    JPanel controlPanel = new JPanel(new GridBagLayout());
  {
      controlPanel.setPreferredSize(new Dimension(200, controlPanel.getPreferredSize().height));
      JButton hostButton = new JButton("Host Game");
      JButton refreshButton = new JButton("Refresh");

      gbc = new GridBagConstraints();
      gbc.insets = new Insets(10, 0, 10, 10);
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 1;
      gbc.weighty = 1;
      gbc.fill = GridBagConstraints.BOTH;

      gbc.gridx = 0;
      controlPanel.add(hostButton, gbc);
      gbc.gridy++;
      controlPanel.add(refreshButton, gbc);

      // Set up host button action
      hostButton.addActionListener(e -> {
        statusLabel.setText("Starting server...");

        new Thread(() -> {
          statusLabel.setText("Waiting for client to connect...");
          new PongServer();
        }).start();
      });

      // Setup connect button action
      refreshButton.addActionListener(e -> {
        List<String> hosts = NetworkScanner.getLocalEndPointAddress();
        String[] filteredHosts = new String[10];
        for (int i = 0; i < 10; i++) {
          if (i < hosts.size()) {
            filteredHosts[i] = hosts.get(i);
          } else {
            filteredHosts[i] = " ";
          }
        }
        serverList.setListData(filteredHosts);
      });
    }

    this.add(viewPanel, BorderLayout.CENTER);
    this.add(controlPanel, BorderLayout.EAST);

    setVisible(true);
  }
}
