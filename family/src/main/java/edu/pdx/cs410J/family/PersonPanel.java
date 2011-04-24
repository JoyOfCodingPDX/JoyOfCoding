package edu.pdx.cs410J.family;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 * This class is a <code>JPanel</code> that displays a
 * <code>Person</code>.
 */
@SuppressWarnings("serial")
public class PersonPanel extends JPanel {
  private FamilyTreePanel familyGUI;  // Used for callbacks

  private Person person;  // Who are we displaying?
  private ArrayList<Marriage> marriages = new ArrayList<Marriage>();

  // Some GUI components we care about
  private JLabel name;
  private JLabel dob;
  private JLabel dod;
  private JLabel fatherName;
  private JLabel motherName;
  private JList marriagesList;


  /**
   * Creates a <code>PersonPanel</code> for displaying
   * <code>Person</code>s
   */ 
  public PersonPanel(final FamilyTreePanel familyGUI) {
    this.familyGUI = familyGUI;

    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

    this.name = new JLabel("Name: ");
    this.name.setToolTipText("The name of this person");
    this.name.setAlignmentX(Component.LEFT_ALIGNMENT);
    infoPanel.add(this.name);

    this.dob = new JLabel("Born: ");
    this.dob.setToolTipText("The day this person was born");
    this.dob.setAlignmentX(Component.LEFT_ALIGNMENT);
    infoPanel.add(this.dob);

    this.dod = new JLabel("Died: ");
    this.dod.setToolTipText("The day this person died");
    this.dod.setAlignmentX(Component.LEFT_ALIGNMENT);
    infoPanel.add(this.dod);

    this.fatherName = new JLabel("Father: ");
    this.fatherName.setToolTipText("Click to view this person's " +
                                   "father");
    this.fatherName.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.fatherName.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          PersonPanel.this.familyGUI.displayFather();
        }
      });
    infoPanel.add(this.fatherName);

    this.motherName = new JLabel("Mother: ");
    this.motherName.setToolTipText("Click to view this person's " +
                                   "mother");
    this.motherName.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.motherName.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          PersonPanel.this.familyGUI.displayMother();
        }
      });
    infoPanel.add(motherName);

    Border infoBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    infoPanel.setBorder(infoBorder);

    this.add(infoPanel);

    JPanel marriagePanel = new JPanel();
    marriagePanel.setToolTipText("The marriages this person " + 
                                 "is involed in");
    marriagePanel.setLayout(new BoxLayout(marriagePanel,
                                          BoxLayout.Y_AXIS));
    Border border = BorderFactory.createCompoundBorder(
                 BorderFactory.createTitledBorder("Marriages"),
                 BorderFactory.createEmptyBorder(5,5,5,5));
    marriagePanel.setBorder(border);
    this.marriagesList = new JList();
    this.marriagesList.setMinimumSize(new Dimension(100, 30));
    this.marriagesList.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            // Double-click to edit marriage
            int index = marriagesList.getSelectedIndex();
            Marriage marriage = null;

            if (index < marriages.size()) {
              marriage = (Marriage) marriages.get(index);
            }

            if (marriage != null && familyGUI.canEdit()) { 
              EditMarriageDialog dialog = 
                new EditMarriageDialog(marriage, 
                                       familyGUI.getFrame(), 
                                       familyGUI.getFamilyTree());
              dialog.pack();
              dialog.setLocationRelativeTo(familyGUI);
              dialog.setVisible(true);
              
              if (dialog.getMarriage() != null) {
                // Assume a change was made and update person panel
                familyGUI.setDirty(true);
                familyGUI.showPerson(person);
              }
            }
          }
        }
      });

    JScrollPane scrollPane = new JScrollPane(this.marriagesList);
    marriagePanel.add(scrollPane);

    // If the GUI can't edit a person, don't bother display the
    // buttons to do so.
    if (familyGUI.canEdit()) {
      JButton addMarriageButton = new JButton("Add Marriage");
      addMarriageButton.setToolTipText("Notes a marriage involving " +
                                       "this person"); 
      addMarriageButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            PersonPanel.this.familyGUI.addMarriage();
          }
        });
      JPanel addMarriagePanel = new JPanel();
      addMarriagePanel.setLayout(new BoxLayout(addMarriagePanel,
                                               BoxLayout.X_AXIS));
      addMarriagePanel.add(Box.createHorizontalGlue());
      addMarriagePanel.add(addMarriageButton);
      marriagePanel.add(addMarriagePanel);
      marriagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      this.add(marriagePanel);
      
      JPanel editPanel = new JPanel();
      JButton editButton = new JButton("Edit");
      editButton.setToolTipText("Click to edit this person");
      editButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            PersonPanel.this.familyGUI.editPerson();
          }
        });
      editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));
      editPanel.add(Box.createHorizontalGlue());
      editPanel.add(editButton);
      editPanel.add(Box.createHorizontalGlue());
      editPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      this.add(editPanel);
    }

    this.add(Box.createVerticalGlue());

  }

  /**
   * Fills in the labels with default text values
   */
  private void fillInLabels() {
    this.name.setText("Name");
    this.dob.setText("Born:");
    this.dod.setText("Died:");
    this.fatherName.setText("Father:");
    this.motherName.setText("Mother:");
    this.marriagesList.setListData(new Vector());
  }

  /**
   * Displays information about a <code>Person</code> in this
   * <code>PersonPanel</code>.
   */
  public void showPerson(Person person) {
    this.person = person;

//      System.out.println("Display person: " + person.getFullName());

    // Re-initialize all fields
    fillInLabels();

    if (this.person == null) {
      // When no person is selected
      return;
    }

    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

    this.name.setText(person.getFullName());

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

    this.marriages = new ArrayList<Marriage>(person.getMarriages());
    Vector<String> list = new Vector<String>();
    for (Marriage marriage : person.getMarriages()) {
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

      list.add(sb.toString());
    }

    this.marriagesList.setListData(list);
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
