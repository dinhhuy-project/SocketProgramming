package com.group9.Demo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadFromFileExample {
  public static void main(String[] args) {
    File file = new File("msv.txt");
    if (!file.exists()) {
      System.out.println("msv.txt không tồn tại. đang dừng");
      System.exit(1);
    }

    try (FileReader fin = new FileReader(file)) {
      int c;
      while ((c = fin.read()) > -1) {
        System.out.print((char)c);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
