package edu.pdx.cs410J.family.gwt.client;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import edu.pdx.cs410J.family.Person;
import edu.pdx.cs410J.family.Marriage;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Displays information about a {@link Person}
 */
public class PersonPanel extends DockPanel {
  private final FamilyTreeGWT familyUI;

  private Person person;
  private List<Marriage> marriages = new ArrayList<Marriage>();


  private final Label name;
  private final Label dob;
  private final Label dod;
  private final Label fatherName;
  private final Label motherName;
  private final ListBox marriagesList;

  /**
   * Creates a <code>PersonPanel</code> for displaying
   * <code>Person</code>s
   * @param familyUI The parent
   */
  public PersonPanel(FamilyTreeGWT familyUI) {
    this.familyUI = familyUI;
    VerticalPanel panel = new VerticalPanel();
    panel.setSpacing(3);

    this.name = new Label();
    this.name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    panel.add(this.name);

    this.dob = new Label();
    this.dob.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    panel.add(this.dob);

    this.dod = new Label();
    this.dod.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    panel.add(this.dod);

    this.fatherName = new Label();
    this.fatherName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    this.fatherName.addClickListener(new ClickListener() {
      public void onClick(Widget widget) {
        PersonPanel.this.familyUI.displayFather();
      }
    });
    panel.add(this.fatherName);

    this.motherName = new Label();
    this.motherName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    this.motherName.addClickListener(new ClickListener() {
      public void onClick(Widget widget) {
        PersonPanel.this.familyUI.displayMother();
      }
    });
    panel.add(this.motherName);

    fillInLabels();

    panel.add(new Label("Marriages:"));
    this.marriagesList = new ListBox();
    this.marriagesList.setVisibleItemCount(10);
    this.marriagesList.setWidth("100%");
    ScrollPanel scroll = new ScrollPanel(this.marriagesList);
    panel.add(scroll);
    panel.setWidth("100%");
//    panel.setCellWidth(scroll, "100%");
 
    this.add(panel, DockPanel.NORTH);
  }

  private void fillInLabels() {
    this.name.setText("Name:");
    this.dob.setText("Born:");
    this.dod.setText("Died:");
    this.fatherName.setText("Father:");
    this.motherName.setText("Mother:");
  }

  /**
   * Dispays information aout a <code>Person</code> in this
   * <code>PersonPanel</code>
   */
  public void showPerson(Person person) {
    this.person = person;

    fillInLabels();

    if (this.person == null) {
      // When no person is selected
      return;
    }

    DateTimeFormat df = DateTimeFormat.getLongDateFormat();

    this.name.setText("Name: " + person.getFullName());

    Date dob = person.getDateOfBirth();
    if (dob != null) {
      this.dob.setText("Born: " + df.format(dob));
    }

    Date dod = person.getDateOfDeath();
    if (dod != null) {
      this.dod.setText("Died: " + df.format(dod));
    }

    Person father = person.getFather();
    if (father != null) {
      this.fatherName.setText("Father: " + father.getFullName());
    }

    Person mother = person.getMother();
    if (mother != null) {
      this.motherName.setText("Mother: " + mother.getFullName());
    }

    this.marriagesList.clear();

    this.marriages = new ArrayList<Marriage>(person.getMarriages());

    for (Marriage marriage : this.marriages) {
      StringBuffer sb = new StringBuffer();
      Person spouse = (marriage.getHusband().getId() == person.getId()
		       ? marriage.getWife()
		       : marriage.getHusband());
      sb.append(spouse.getFullName());

      Date date = marriage.getDate();
      if (date != null) {
	sb.append(" on " + df.format(date));
      }

      String location = marriage.getLocation();
      if (location != null || !location.equals("")) {
	sb.append(" in " + location);
      }

      this.marriagesList.addItem(sb.toString());
    }
  }

  /**
   * Displays and returns the current person's father
   */
  public Person showFather() {
    Person father = this.person.getFather();
    if (father != null) {
      this.showPerson(father);
    }
    return father;
  }

  /**
   * Displays the current person's mother
   */
  public Person showMother() {
    Person mother = this.person.getMother();
    if (mother != null) {
      this.showPerson(mother);
    }
    return mother;
  }
}
