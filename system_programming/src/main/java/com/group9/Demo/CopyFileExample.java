package com.group9.Demo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CopyFileExample {
  public static void main(String[] args) {
    File oldFile = new File("msv.txt");
    File newFile = new File("msv_copy.txt");

    if (!oldFile.exists()) {
      System.out.println("msv.txt không tồn tại. đang dừng");
      System.exit(1);
    }

    if (newFile.exists()) {
      System.out.println("msv_copy.txt tồn tại. đang ghi đè");
      System.exit(1);
    }

    try (FileReader fr = new FileReader(oldFile)) {
      try (FileWriter fw = new FileWriter(newFile)) {
        int c;
        while ((c = fr.read()) > -1) {
          fw.write(c);
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
