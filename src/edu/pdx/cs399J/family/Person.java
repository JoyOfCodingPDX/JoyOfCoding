package edu.pdx.cs410J.familyTree;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This class represents a person in a family tree.  Each person has a
 * required unique id.  Additionally, a person may have a first,
 * middle, and last name, a date of birth, a date of death, and may be
 * involved in one or more marriages.
 *
 * @author David Whitlock
 */
public class Person {

  private int id;
  private String firstName;
  private String middleName;
  private String lastName;
  private Person father;
  private Person mother;
  private Collection marriages;
  private Date dob;             // Date of birth
  private Date dod;             // Date of death

  /**
   * Creates a new <code>Person</code> with a given id.
   *
   * @param id
   *        A unique number greater than 0 identifying this person.
   */
  public Person(int id) {
    if (id < 1) {
      String m = "A person's id must be greater than 1: " + id;
      throw new IllegalArgumentException(m);
    }

    this.id = id;
    this.marriages = new ArrayList();
  }

  /**
   * Returns this person's id.
   */
  public int getId() {
    return this.id;
  }

  /**
   * Sets this person's first name.
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Returns this person's first name.
   */
  public String getFirstName() {
    return this.firstName;
  }

  /**
   * Sets this person's middle name.
   */
  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  /**
   * Returns this person's middle name.
   */
  public String getMiddleName() {
    return this.middleName;
  }

  /**
   * Sets this person's last name.
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Returns this person's last name.
   */
  public String getLastName() {
    return this.lastName;
  }

  /**
   * Returns this person's full (first, middle, and last) name.
   */
  public String getFullName() {
    StringBuffer fullName = new StringBuffer();

    if (this.firstName != null) {
      fullName.append(this.firstName);
      fullName.append(' ');
    }

    if (this.middleName != null) {
      fullName.append(this.middleName);
      fullName.append(' ');
    }

    if (this.lastName != null) {
      fullName.append(this.lastName);
    }

    return fullName.toString().trim();
  }

  /**
   * Sets this person's father.
   */
  public void setFather(Person father) {
    this.father = father;
  }

  /**
   * Returns the id of this person's father.
   */
  public int getFatherId() {
    // Father's id is always twice of this person's id
    return this.id * 2;
  }

  /**
   * Returns this person's father.
   */
  public Person getFather() {
    return this.father;
  }

  /**
   * Sets this person's mother.
   */
  public void setMother(Person mother) {
    this.mother = mother;
  }

  /**
   * Returns the id of this person's mother.
   */
  public int getMotherId() {
    // Mother's id is always one more than the father's
    return this.getFatherId() + 1;
  }

  /**
   * Returns this person's mother.
   */
  public Person getMother() {
    return this.mother;
  }

  /**
   * Sets this person's date of birth.
   */
  public void setDateOfBirth(Date dob) {
    this.dob = dob;
  }

  /**
   * Returns this person's date of birth.
   */
  public Date getDateOfBirth() {
    return this.dob;
  }

  /**
   * Sets this person's date of death.
   */
  public void setDateOfDeath(Date dod) {
    this.dod = dod;
  }

  /**
   * Returns this person's date of death.
   */
  public Date getDateOfDeath() {
    return this.dod;
  }

  /**
   * Makes note of a marriage this person was involved in.
   */
  public void addMarriage(Marriage marriage) {
    this.marriages.add(marriage);
  }

  /**
   * Returns the marriages that this person was involved in.
   */
  public Collection getMarriages() {
    return this.marriages;
  }

  /**
   * Returns a brief description of this person.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

    sb.append("Person " + this.id + ": " + this.getFullName());
    if (this.dob != null) {
      sb.append("\nBorn: " + df.format(this.dob));
    }
    if (this.dod != null) {
      sb.append("Died: " + df.format(this.dod));
    }

    if (this.mother != null) {
      sb.append("\nMother: " + this.mother.getFullName());
    }
    if (this.father != null) {
      sb.append(", Father: " + this.father.getFullName());
    }

    sb.append("\nMarried " + this.marriages.size() + " times");

    return sb.toString();
  }

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
    Person me = new Person(1);
    me.setFirstName("David");
    me.setMiddleName("Michael");
    me.setLastName("Whitlock");

    return me;
  }

  /**
   * Returns a Person representing my mom.
   */
  static Person mom(Person me) {
    Person mom = new Person(me.getMotherId());
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
    Person dad = new Person(me.getFatherId());
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
