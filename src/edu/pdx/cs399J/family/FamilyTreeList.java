package edu.pdx.cs410J.familyTree;

import java.util.*;
import javax.swing.*;

/**
 * A <code>FamilyTreeList</code> is a <code>JList</code> that contains
 * the names of the people in a family tree.
 */
public class FamilyTreeList extends JList {
  private Map indexToPerson = new HashMap();

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
    SortedSet sortedPeople = new TreeSet(new Comparator() {
        // Sort id's from lowest to highest

        public int compare(Object o1, Object o2) {
          Person p1 = (Person) o1;
          Person p2 = (Person) o2;
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
    int index = this.getSelectedIndex();
    Person person = (Person) this.indexToPerson.get(new Integer(index));
    return person;
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
