package edu.pdx.cs399J.j2se15;

import java.util.*;

/**
 * Demonstrates J2SE's "enumerated type" facility.
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 * @since Summer 2004
 */
public class EnumeratedTypes {

  /**
   * A enumerated type for the days of the week
   */
  private enum Day { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY,
                     FRIDAY, SATURDAY }

  /**
   * Translates a {@link Day} into Spanish
   */
  private static String enEspanol(Day day) {
    switch (day) {
    case SUNDAY:
      return "Domingo";
    case MONDAY:
      return "Lunes";
    case TUESDAY:
      return "Martes";
    case WEDNESDAY:
      return "Miercoles";
    case THURSDAY:
      return "Jueves";
    case FRIDAY:
      return "Viernes";
    case SATURDAY:
      return "Sabado";
    default:
      String s = "Unknown day: " + day;
      throw new IllegalArgumentException(s);
    }
  }

  /**
   * Demonstrates enumerated types by adding several to a {@link
   * SortedSet} and printing them out using their {@link
   * Enum#toString()} method and in Spanish.
   */
  public static void main(String[] args) {
    SortedSet set = new TreeSet();
    set.add(Day.WEDNESDAY);
    set.add(Day.MONDAY);
    set.add(Day.FRIDAY);

    System.out.print("Sorted days: ");
    for (Iterator iter = set.iterator(); iter.hasNext(); ) {
      System.out.print(iter.next() + " ");
    }

    System.out.print("\nEn espanol: ");
    for (Iterator iter = set.iterator(); iter.hasNext(); ) {
      Day day = (Day) iter.next();
      System.out.print(enEspanol(day) + " ");
    }
    System.out.println("");
  }

}
