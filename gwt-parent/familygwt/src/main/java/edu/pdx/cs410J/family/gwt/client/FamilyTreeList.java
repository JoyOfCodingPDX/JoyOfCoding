package edu.pdx.cs410J.family.gwt.client;

import com.google.gwt.user.client.ui.ListBox;
import edu.pdx.cs410J.family.Person;
import edu.pdx.cs410J.family.FamilyTree;

import java.util.*;

/**
 * Lists the people in a family tree
 */
public class FamilyTreeList extends ListBox {
  private Map<Integer, Person> indexToPerson = new HashMap<Integer, Person>();

  public FamilyTreeList() {
    setVisibleItemCount(5);
    setSize("200px", "100%");
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
    this.clear();

    Iterator<Person> iter = sortedPeople.iterator();
    for (int i = 0; iter.hasNext(); i++) {
      Person person = iter.next();
      this.addItem(person.getFullName() + " (" + person.getId() + ")");
      indexToPerson.put(new Integer(i), person);
    }

  }

  /**
   * Returns the person in the list that is currently selected
   */
  public Person getSelectedPerson() {
    return indexToPerson.get(getSelectedIndex());
  }

  /**
   * Sets the selected person.
   */
  public void setSelectedPerson(Person person) {
    Integer index = null;
    for (Map.Entry<Integer, Person> integerPersonEntry : this.indexToPerson.entrySet()) {
      Map.Entry<Integer, Person> entry = integerPersonEntry;
      if (entry.getValue().equals(person)) {
        index = entry.getKey();
      }
    }

    if (index != null) {
      this.setSelectedIndex(index.intValue());
    }
  }

}
