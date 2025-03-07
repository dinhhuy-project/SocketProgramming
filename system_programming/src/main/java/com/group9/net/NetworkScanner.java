package com.group9.net;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Future;

public class NetworkScanner {
  static final int PORT_PRIMARY = 8079;  // Change this to the port you want to check
  static final int PORT_SECONDARY = 8078;  // Change this to the port you want to check

  public static List<String> getLocalEndPointAddress() {
    List<String> endpoints = new ArrayList<>();
    InetAddress addr = getHostPrivateAddress();

    String ip = addr.getHostAddress();
    String subnet = ip.substring(0, ip.lastIndexOf("."));

    for (int i = 1; i < 255; i++) {
      final int j = i;
      String peerAddr = subnet + "." + i;
      new Thread(() -> {
        if (isPortOpen(peerAddr, PORT_PRIMARY, 5000)) {
          endpoints.add(peerAddr + ":" + PORT_PRIMARY);
          return;
        }
        if (isPortOpen(subnet + "." + j, PORT_SECONDARY, 5000)) {
          endpoints.add(peerAddr + ":" + PORT_SECONDARY);
          return;
        }
      }).start();
    }

    return endpoints;
  }

  private static boolean isPortOpen(String ip, int port, int timeout) {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(ip, port), timeout);
      return true;
    } catch (IOException e) {
      return false; // Port is closed or host unreachable
    }
  }

  public static InetAddress getHostPrivateAddress() {
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface ni = interfaces.nextElement();
        Enumeration<InetAddress> addresses = ni.getInetAddresses();
        if (ni.isLoopback() || !ni.isUp()) break;
        while (addresses.hasMoreElements()) {
          InetAddress addr = addresses.nextElement();
          if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
            return addr;
          }
        }
      }
    } catch (SocketException ex) {
      System.err.println("Socket Error:" + ex.getMessage());
    }
    return null;
  }
}
