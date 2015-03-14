package edu.pdx.cs410J.java8;

import java.io.File;

public class FinalClassFilesUsingJava8 {
  private static void findFiles(File dir) {
    String suffix = ".class";

    File[] classFiles =
      dir.listFiles((File dir1, String name) -> {
        return name.endsWith(suffix);
      });

    for (int i = 0; i < classFiles.length; i++)
      System.out.println(classFiles[i]);

    File[] subdirs =
      dir.listFiles(file -> file.isDirectory());

    for (int i = 0; i < subdirs.length; i++)
      findFiles(subdirs[i]);
  }

}
