package edu.pdx.cs410J.familyTree;

import java.util.*;

/**
 * This class represents a family tree.  Essentially, it is a
 * collection of people.  Family trees are always rooted at the person
 * with id 1.
 *
 * @author David Whitlock
 */
public class FamilyTree {
  
  private Map people;     // Maps Integer ids to Persons

  /**
   * Creates an empty family tree.
   */
  public FamilyTree() {
    this.people = new HashMap();
  }

  /**
   * Returns a collection of <code>Person</code>s that are in this family
   * tree.
   */
  public Collection getPeople() {
    return this.people.values();
  }

  /**
   * Returns a person in this family tree with a given id.  If no
   * person with that id exists in this family tree, than a new
   * <code>Person</code> instance is created.  That is, this method
   * should never return null.
   */
  public Person getPerson(int id) {
    Person person;
    Integer key = new Integer(id);
    if (this.people.containsKey(key)) {
      person = (Person) this.people.get(key);

    } else {
      person = new Person(id);
      this.people.put(key, person);
    }

    return person;
  }

  /**
   * Adds a person to this family tree.  If a person with the same id
   * as the person being added already exists in this family tree, the
   * old person is removed and replaced with the new.
   */
  public void addPerson(Person person) {
    this.people.put(new Integer(person.getId()), person);
  }

}
