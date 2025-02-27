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

    // System.out.println("Scanning network for port " + port + "...");

    for (int i = 1; i < 255; i++) {
      String host = subnet + "." + i;
      System.out.printf("Scanning: %s...", host);
      // if (isPortOpen(host, PORT_PRIMARY, 100) || isPortOpen(host, PORT_SECONDARY, 100)) {

      // }
    }

    System.out.println("Scan complete.");
  }

  public static List<InetAddress> getLocalEndPoints() {
    List<InetAddress> endpoints = new ArrayList<>();
    InetAddress addr = getHostPrivateAddress();

    String ip = addr.getHostAddress();
    String subnet = ip.substring(0, ip.lastIndexOf("."));

    for (int i = 2; i < 255; i++) {
      // InetSocketAddress addr = 
    }

    return endpoints;
  }

  private static InetSocketAddress getServiceAddress(String ip, int port, int timeout) {
    InetSocketAddress addr = null;
    try (Socket socket = new Socket()) {
      addr = new InetSocketAddress(ip, port);
      socket.connect(addr, timeout);
      return addr;
    } catch (IOException e) {
      return addr; // Port is closed or host unreachable
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
