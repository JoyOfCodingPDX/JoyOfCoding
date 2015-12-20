package edu.pdx.cs410J.family;

import java.text.DateFormat;
import java.text.ParseException;

/**
 * A class with a main method for testing {@link Person}.  Once upon a time,
 * this functionality was part of the <code>Person</code> class.  However,
 * since GWT <a href="http://code.google.com/p/bunsenandbeaker/wiki/DevGuideDateAndNumberFormat">cannot
 * handle</a> <code>DateFormat</code> on the client side, I had to move it to
 * its own class.
 *
 * @since Summer 2008 
 */
public class PersonMain {
  /**
   * A simple test program.
   */
  public static void main(String[] args) {
    // Make some people and fill in information
    Person me = me();
    Person dad = dad(me);
    Person mom = mom(me);

    me.setMother(mom);
    me.setFather(dad);

    // Print out descriptions of these people
    System.out.println(me + "\n");
    System.out.println(mom + "\n");
    System.out.println(dad + "\n");

  }

  /**
   * Returns a Person representing me.
   */
  static Person me() {
    Person me = new Person(1, Person.MALE);
    me.setFirstName("David");
    me.setMiddleName("Michael");
    me.setLastName("Whitlock");

    return me;
  }

  /**
   * Returns a Person representing my mom.
   */
  static Person mom(Person me) {
    Person mom = new Person(2, Person.FEMALE);
    mom.setFirstName("Carolyn");
    mom.setMiddleName("Joyce");
    mom.setLastName("Granger");

    try {
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      mom.setDateOfBirth(df.parse("May 17, 1945"));

    } catch (ParseException ex) {
      System.err.println("** Malformatted mom's birthday?");
      System.exit(1);
    }

    return mom;
  }

  /**
   * Returns a Person representing my dad.
   */
  static Person dad(Person me) {
    Person dad = new Person(3, Person.MALE);
    dad.setFirstName("Stanley");
    dad.setMiddleName("Jay");
    dad.setLastName("Whitlock");

    try {
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      dad.setDateOfBirth(df.parse("Feb 27, 1948"));

    } catch (ParseException ex) {
      System.err.println("** Malformatted dad's birthday?");
      System.exit(1);
    }

    return dad;
  }
}
