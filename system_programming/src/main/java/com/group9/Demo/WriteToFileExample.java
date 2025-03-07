package com.group9.Demo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteToFileExample {
  public static void main(String[] args) {
    String msv = 
      "22810310334 - Nguyễn Quốc Bình Dương\n" +
      "22810310123 - Nguyễn Thị Liên\n" +
      "22810310128 - Mai Văn Hoàng\n" +
      "22810310321 - Trịnh Thanh Tùng\n" +
      "22810310350 - Bùi Đình Huy\n"
    ;

    File file = new File("msv.txt");
    if (file.exists()) {
      System.out.println("msv.txt đã tồn tại. Đang ghi đè");
    }

    try (FileWriter fos = new FileWriter(file)) {
      fos.write(msv);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
