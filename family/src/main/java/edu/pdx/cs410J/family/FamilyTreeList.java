package edu.pdx.cs410J.family;

import java.util.*;
import javax.swing.*;

/**
 * A <code>FamilyTreeList</code> is a <code>JList</code> that contains
 * the names of the people in a family tree.
 */
@SuppressWarnings("serial")
public class FamilyTreeList extends JList {
  private Map<Integer, Person> indexToPerson = new HashMap<Integer, Person>();

  /**
   * Creates a <code>JList</code> populates it with the name of 
   */
  public FamilyTreeList() {
    this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.clearSelection();
  }

  /**
   * Fills in the <code>JList</code> with the contents of a
   * <code>FamilyTree</code>.
   */
  public void fillInList(FamilyTree tree) {
    SortedSet<Person> sortedPeople = new TreeSet<Person>(new Comparator<Person>() {
        // Sort id's from lowest to highest

        public int compare(Person p1, Person p2) {
          return p1.getId() - p2.getId();
        }

        public boolean equals(Object o) {
          return true;
        }
      });

    sortedPeople.addAll(tree.getPeople());
    String[] array = new String[sortedPeople.size()];

    Iterator iter = sortedPeople.iterator();
    for (int i = 0; iter.hasNext(); i++) {
      Person person = (Person) iter.next();
      array[i] = person.getFullName() + " (" + person.getId() + ")";
      indexToPerson.put(new Integer(i), person);
    }

    this.setListData(array);
    this.clearSelection();
  }

  /**
   * Returns the currently selected person.
   */
  public Person getSelectedPerson() {
    return this.indexToPerson.get(this.getSelectedIndex());
  }

  /**
   * Sets the selected person.
   */
  public void setSelectedPerson(Person person) {
    Integer index = null;
    Iterator iter = this.indexToPerson.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      if (entry.getValue().equals(person)) {
        index = (Integer) entry.getKey();
      }
    }

    if (index == null) {
      return;

    } else {
      this.setSelectedIndex(index.intValue());
    }
  }

}
