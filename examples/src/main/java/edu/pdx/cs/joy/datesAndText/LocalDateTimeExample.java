package edu.pdx.cs.joy.datesAndText;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class LocalDateTimeExample {

  public static void main(String[] args) {
    LocalDateTime now = LocalDateTime.now();
    System.out.println("Now: " + now);

    int dayOfYear = now.get(ChronoField.DAY_OF_YEAR);
    System.out.println("Day of Year: " + dayOfYear);

    LocalDateTime yesterday = now.minusDays(1);
    System.out.println("Yesterday: " + yesterday);

    LocalDateTime newYears = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    System.out.println("New Years: " + newYears);
  }
}
