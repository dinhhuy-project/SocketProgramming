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

public class NetworkScanner {
  static final int PORT_PRIMARY = 8079;  // Change this to the port you want to check
  static final int PORT_SECONDARY = 8078;  // Change this to the port you want to check
  public static void main(String[] args) {
    String subnet = "192.168.1"; // Change this to match your network

    for (int i = 1; i < 255; i++) {
      String host = subnet + "." + i;
      System.out.printf("Scanning: %s...", host);
      // if (isPortOpen(host, PORT_PRIMARY, 100) || isPortOpen(host, PORT_SECONDARY, 100)) {

      // }
    }

    System.out.println("Scan complete.");
  }

  public static List<String> getLocalEndPointAddress() {
    List<String> endpoints = new ArrayList<>();
    InetAddress addr = getHostPrivateAddress();

    String ip = addr.getHostAddress();
    String subnet = ip.substring(0, ip.lastIndexOf("."));

    for (int i = 2; i < 255; i++) {
      String peerAddr = subnet + "." + i;
      if (isPortOpen(peerAddr, PORT_PRIMARY, 100)) {
        endpoints.add(peerAddr + ":" + PORT_PRIMARY);
        continue;
      }
      if (isPortOpen(subnet + "." + i, PORT_SECONDARY, 100)) {
        endpoints.add(peerAddr + ":" + PORT_SECONDARY);
        continue;
      }
    }

    return endpoints;
  }

  private static boolean isPortOpen(String ip, int port, int timeout) {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(ip, port), timeout);
      socket.close();
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
