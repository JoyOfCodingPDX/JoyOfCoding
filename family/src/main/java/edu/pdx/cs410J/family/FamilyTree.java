package edu.pdx.cs410J.family;

import java.util.*;
import java.io.Serializable;

/**
 * This class represents a family tree.  Essentially, it is a
 * collection of people.  Family trees are always rooted at the person
 * with id 1.
 *
 * @author David Whitlock
 */
public class FamilyTree implements Serializable {
  
  private Map<Integer, Person> people;     // Maps Integer ids to Persons

  /**
   * Creates an empty family tree.
   */
  public FamilyTree() {
    this.people = new HashMap<Integer, Person>();
  }

  /**
   * Returns a collection of <code>Person</code>s that are in this family
   * tree.
   */
  public Collection<Person> getPeople() {
    return this.people.values();
  }

  /**
   * Returns whether or not this family tree contains a person with
   * the given id.
   */
  public boolean containsPerson(int id) {
    return this.people.containsKey(new Integer(id));
  }

  /**
   * Returns a person in this family tree with a given id.  If no
   * person with that id exists in this family tree, then
   * <code>null</code> is returned.
   */
  public Person getPerson(int id) {
    return this.people.get(new Integer(id));
  }

  /**
   * Adds a person to this family tree.  If a person with the same id
   * as the person being added already exists in this family tree, the
   * old person is removed and replaced with the new.
   */
  public void addPerson(Person person) {
    this.people.put(person.getId(), person);
  }

  //////////////////////// Utility Methods  ///////////////////////

  public String toString() {
    return "A FamilyTree with " + this.people.size() + " people";
  }

}
