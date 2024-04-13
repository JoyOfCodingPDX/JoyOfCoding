package edu.pdx.cs.joy.datesAndText;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterExample {

  public static void main(String[] args) {
    DateTimeFormatter format = DateTimeFormatter.ofPattern(args[0]);
    String formatted = ZonedDateTime.now().format(format);
    System.out.println(formatted);
  }
}
