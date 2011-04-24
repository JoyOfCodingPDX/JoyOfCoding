package edu.pdx.cs410J.family;

import java.text.DateFormat;
import java.text.ParseException;

/**
 * A main program used to demonstrate the {@link Marriage} class.
 *
 * @see PersonMain
 * @since Summer 2008
 */
public class MarriageMain {
  /**
   * A simple test program.
   */
  public static void main(String[] args) {
    // Create my parent's marriage
    Person me = PersonMain.me();
    Person mom = PersonMain.mom(me);
    Person dad = PersonMain.dad(me);

    Marriage marriage = new Marriage(dad, mom);
    marriage.setLocation("Durham, NH");

    try {
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      marriage.setDate(df.parse("Jul 12, 1969"));

    } catch (ParseException ex) {
      System.err.println("** Malformatted wedding day?");
      System.exit(1);
    }

    mom.addMarriage(marriage);
    dad.addMarriage(marriage);

    // Print out some info
    System.out.println(mom + "\n");
    System.out.println(dad + "\n");
    System.out.println(marriage + "\n");
  }
}
