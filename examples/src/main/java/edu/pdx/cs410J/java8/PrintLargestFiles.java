package edu.pdx.cs410J.java8;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

public class PrintLargestFiles {

  public static void main(String[] args) throws IOException {
    Path path = Paths.get(args[0]);

    printLargestFiles(path);
  }

  private static void printLargestFiles(Path root) throws IOException {
    Stream<Path> stream = Files.walk(root);
    stream
      .sorted(new FileSizeComparator().reversed())
      .limit(10)
      .forEach(PrintLargestFiles::printFileAndSize);
  }

  private static void printFileAndSize(Path path) {
    try {
      System.out.println(Files.size(path) + " " + path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }

  private static class FileSizeComparator implements Comparator<Path> {
    @Override
    public int compare(Path o1, Path o2) {
      try {
        long size1 = Files.size(o1);
        long size2 = Files.size(o2);
        return size1 > size2 ? 1 : (size2 > size1 ? -1 : 0);
      } catch (IOException ex) {
        throw new UncheckedIOException(ex);
      }
    }
  }
}
