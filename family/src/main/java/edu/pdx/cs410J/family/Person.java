package edu.pdx.cs410J.family;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.io.Serializable;

/**
 * This class represents a person in a family tree.  Each person has a
 * required unique id.  Additionally, a person may have a first,
 * middle, and last name, a date of birth, a date of death, and may be
 * involved in one or more marriages.
 *
 * @author David Whitlock
 */
public class Person implements Serializable {
  public static Gender MALE = Gender.MALE;
  public static Gender FEMALE = Gender.FEMALE;

  public enum Gender { FEMALE, MALE, UNKNOWN };

  /** A constant representing the id of an unknown person */
  public static final int UNKNOWN = -1;

  private int id;
  private Gender gender;
  private String firstName;
  private String middleName;
  private String lastName;
  private Person father;
  private Person mother;
  private Collection<Marriage> marriages;
  private Date dob;             // Date of birth
  private Date dod;             // Date of death

  /** The id of this person's mother.  We need this for the parsers
      who read a person's id before the Person is created. */
  private int motherId = UNKNOWN;

  /** The id of this person's father.  We need this for the parsers
      who read a person's id before the Person is created. */
  private int fatherId = UNKNOWN;

  /**
   * Creates a new <code>Person</code> with a given id and gender.
   * An {@link #UNKNOWN} person cannot be created. 
   *
   * @throws FamilyTreeException
   *         <code>id</code> is less than 1 or <code>gender</code> is
   *         neither {@link #MALE} nor {@link #FEMALE}
   */
  public Person(int id, Gender gender) {
    if (id < 1) {
      String m = "A person's id must be greater than 1: " + id;
      throw new FamilyTreeException(m);
    }

    if (gender != MALE && gender != FEMALE) {
      String s = "Gender must be MALE or FEMALE";
      throw new FamilyTreeException(s);
    }

    this.id = id;
    this.gender = gender;
    this.marriages = new ArrayList<Marriage>();
  }

  /**
   * Creates a person of unknown gender.  Note that this method is
   * package protected and is meant to be invoked by parsers, etc. in
   * which we have to create a person before we know its gender.
   */
  Person(int id) {
    if (id < 1) {
      String m = "A person's id must be greater than 1: " + id;
      throw new FamilyTreeException(m);
    }

    this.id = id;
    this.gender = Gender.UNKNOWN;
    this.marriages = new ArrayList<Marriage>();
  }

  /**
   * Default constructor for deserialization
   */
  private Person() {
    
  }

  ///////////////////////  Instance Methods  ///////////////////////

  /**
   * Returns this person's id.
   */
  public int getId() {
    return this.id;
  }

  /**
   * Sets this person's gender.  Note that this method is package
   * protected.
   *
   * @throws FamilyTreeException
   *         The <code>gender</code> is neither {@link #MALE} nor
   *         {@link #FEMALE}.
   * 
   * @see #Person(int)
   */
  void setGender(Gender gender) {
    if (gender == MALE || gender == FEMALE) {
      this.gender = gender;

    } else {
      String s = "Invalid gender: " + gender;
      throw new FamilyTreeException(s);
    }
  }

  /**
   * Returns this person's gender
   */
  public Gender getGender() {
    return this.gender;
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
   *
   * @throws FamilyTreeException
   *         <code>father</code> is not {@link #MALE}
   */
  public void setFather(Person father) {
    if (father.getGender() != Person.MALE) {
      String s = "Father " + father + " must be MALE";
      throw new FamilyTreeException(s);
    }

    this.fatherId = father.getId();
    this.father = father;
  }

  /**
   * Returns the id of this person's father.
   *
   * @return {@link #UNKNOWN}, if this person's father is not known
   */
  public int getFatherId() {
    if (this.father == null) {
      return UNKNOWN;

    } else {
//       assert this.fatherId != UNKNOWN;
      return this.father.getId();
    }
  }

  /**
   * Returns this person's father.
   */
  public Person getFather() {
    return this.father;
  }

  /**
   * Sets the id of this person's father.  This method is package
   * protected because it is only intended to be access by parsers and
   * other objects that would see a peron's id before the person is
   * created.
   *
   * @see #patchUp
   */
  void setFatherId(int id) {
    this.fatherId = id;
  }

  /**
   * Sets this person's mother.
   *
   * @throws FamilyTreeException
   *         <code>mother</code>'s gender is not {@link #FEMALE}
   */
  public void setMother(Person mother) {
    if (mother.getGender() != Person.FEMALE) {
      String s = "Person " + mother.getId() + "(mother of " +
        this.getId() + ") must be FEMALE";
      throw new FamilyTreeException(s);
    }

    this.mother = mother;
  }

  /**
   * Returns the id of this person's mother.
   *
   * @return {@link #UNKNOWN}, if this person's father is not known
   */
  public int getMotherId() {
    if (this.mother == null) {
      return UNKNOWN;

    } else {
      return this.mother.getId();
    }
  }

  /**
   * Returns this person's mother.
   */
  public Person getMother() {
    return this.mother;
  }

  /**
   * Sets the id of this person's mother.  This method is package
   * protected because it is only intended to be accessed by parsers
   * and other objects that would see a peron's id before the person
   * is created.
   *
   * @see #patchUp
   */
  void setMotherId(int id) {
    this.motherId = id;
  }

  /**
   * "Patches up" a person's mother and father <code>Person</code>
   * objects.  This method is package protected because it is only
   * intended to be accessed by parsers and other objects that would
   * see a peron's id before the person is created.
   *
   * @throws FamilyTreeException
   *         Either the mother or father does not existin in
   *         <code>tree</code> or if the gender has not been set
   */
  void patchUp(FamilyTree tree) {
    if (this.father == null && this.fatherId != UNKNOWN) {
      Person father = tree.getPerson(this.fatherId);
      if (father == null) {
        String s = "Father " + this.fatherId + " does not exist";
        throw new FamilyTreeException(s);
      }
      this.setFather(father);
    }

    if (this.mother == null && this.motherId != UNKNOWN) {
      Person mother = tree.getPerson(this.motherId);
      if (mother == null) {
        String s = "Mother " + this.motherId + " does not exist";
        throw new FamilyTreeException(s);
      }
      this.setMother(mother);
    }

    if (this.gender == Gender.UNKNOWN) {
      String s = "Gender has not been set yet!";
      throw new FamilyTreeException(s);
    }
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
   *
   * @throws FamilyTreeException
   *         If this person's data of birth is known and
   *         <code>dod</code> occurs before it.
   */
  public void setDateOfDeath(Date dod) {
    if (this.dob != null && dod != null && this.dob.after(dod)) {
      String s = "Date of death (" + dod + 
        ") cannot occur before date of birth (" + this.dob + ")";
      throw new FamilyTreeException(s);
    }

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
   *
   * @throws FamilyTreeException
   *         If this person is not one of the spouses in the marriage
   */
  public void addMarriage(Marriage marriage) {
    if (this.getGender() == Person.MALE) {
      if (!marriage.getHusband().equals(this)) {
        String s = "This person (" + this.getFullName() + 
          ") is not the husband in " + marriage;
        throw new FamilyTreeException(s);
      }

    } else {
      if (!marriage.getWife().equals(this)) {
        String s = "This person (" + this.getFullName() + 
          ") is not the wife in " + marriage;
        throw new FamilyTreeException(s);
      }
    }

    this.marriages.add(marriage);
  }

  /**
   * Returns the marriages that this person was involved in.
   */
  public Collection<Marriage> getMarriages() {
    return this.marriages;
  }

  //////////////////////  Utility Methods  ////////////////////////

  /**
   * Determines whether or not this <code>Person</code> is equal to
   * another <code>Person</code>. Two <code>Person</code>s are
   * considered equal if they have the same id.
   */
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }

    if (!(o instanceof Person)) {
      return false;
    }

    Person other = (Person) o;
    return this.getId() == other.getId();
  }

  /**
   * Returns a brief description of this person.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();

    sb.append("Person ").append(this.id).append(": ").append(this.getFullName());
    if (this.dob != null) {
      sb.append("\nBorn: ");
      sb.append(this.dob);
    }
    if (this.dod != null) {
      sb.append(", Died: ");
      sb.append(this.dod);
    }

    if (this.mother != null) {
      sb.append("\nMother: ");
      sb.append(this.mother.getFullName());
    }
    if (this.father != null) {
      sb.append(", Father: ");
      sb.append(this.father.getFullName());
    }

    sb.append("\nMarried ");
    sb.append(this.marriages.size());
    sb.append(" times");

    return sb.toString();
  }

}
