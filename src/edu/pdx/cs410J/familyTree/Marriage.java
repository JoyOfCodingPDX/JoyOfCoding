package edu.pdx.cs410J.familyTree;

import java.text.*;
import java.util.*;

/**
 * This class represents a marriage between two people.  Ain't love
 * grand?  Each marriage consists of a husband, a wife, a date, and a
 * location.
 *
 * @author David Whitlock
 */
public class Marriage {

  private Person husband;
  private Person wife;
  private Date date;
  private String location;

  /**
   * Creates a marriage between a husband and a wife.
   */
  public Marriage(Person husband, Person wife) {
    this.husband = husband;
    this.wife = wife;
  }

  /**
   * Returns the husband in his marriage.
   */
  public Person getHusband() {
    return this.husband;
  }

  /**
   * Returns the wife in this marriage.
   */
  public Person getWife() {
    return this.wife;
  }

  /**
   * Returns the date on which the husband and wife were married.
   */
  public Date getDate() {
    return this.date;
  }

  /**
   * Sets the date on which the husband and wife were married.
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Returns the location at which the husband and wife were married.
   */
  public String getLocation() {
    return this.location.trim();
  }

  /**
   * Sets the location at which the husband and wife were married.
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Returns a brief description of this marriage.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.husband.getFullName() + " and " +
	      this.wife.getFullName() + " were married");

    if (this.date != null) {
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      sb.append(" on " + df.format(this.date));
    }
    if (this.location != null) {
      sb.append(" in " + this.location);
    }

    return sb.toString();
  }

  /**
   * A simple test program.
   */
  public static void main(String[] args) {
    // Create my parent's marriage
    Person me = Person.me();
    Person mom = Person.mom(me);
    Person dad = Person.dad(me);

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
