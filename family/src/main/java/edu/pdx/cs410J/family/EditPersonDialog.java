package edu.pdx.cs410J.family;

import edu.pdx.cs410J.family.Person.Gender;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * This is a dialog for editing a <code>Person</code>.  The
 * <code>Person</code> may or may not already exist.
 */
@SuppressWarnings("serial")
public class EditPersonDialog extends JDialog {

  // The person we're editing
  private Person person = null;

  private Person mother;
  private Person father;
  private Person child;

  // GUI components we need to hold on to
  private JTextField idField = new JTextField();
  private JTextField firstNameField = new JTextField();
  private JTextField middleNameField = new JTextField();
  private JTextField lastNameField = new JTextField();
  private JTextField dobField = new JTextField();
  private JTextField dodField = new JTextField();
  private JTextField fatherField = new JTextField("Click to choose");
  private JTextField motherField = new JTextField("Click to choose");
  private JTextField childField = new JTextField("Click to choose");
  private JRadioButton male = new JRadioButton("male", true);
  private JRadioButton female = new JRadioButton("female");


  /**
   * Creates a new <code>EditPersonDialog</code> for adding a new
   * <code>Person</code> to a family tree.
   *
   * @param owner
   *        The parent <code>JFrame</code> of this dialog box
   * @param tree
   *        The existing family tree
   */
  public EditPersonDialog(JFrame owner, FamilyTree tree) {
    this(owner, "Add New Person", tree);
  }

  /**
   * Creates a new <code>EditPersonDialog</code> for editing an
   * existing Person.
   */
  public EditPersonDialog(Person person, JFrame owner, 
                          FamilyTree tree) {
    this(owner, "Edit Person " + person.getId(), tree);
    this.person = person;

    // Fill in information about the person
    this.idField.setText("" + person.getId());
    this.idField.setEditable(false);

    if (this.person.getGender() == Person.FEMALE) {
      female.setSelected(true);
      female.setEnabled(true);
      male.setEnabled(false);
      male.setSelected(false);
    }
    
    if (this.person.getGender() == Person.MALE) {
      male.setSelected(true);
      male.setEnabled(true);
      female.setSelected(false);
      female.setEnabled(false);
    }

    this.firstNameField.setText(person.getFirstName());
    this.middleNameField.setText(person.getMiddleName());
    this.lastNameField.setText(person.getLastName());
    
    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
    Date dob = person.getDateOfBirth();
    if (dob != null) {
      this.dobField.setText(df.format(dob));
    }
    Date dod = person.getDateOfDeath();
    if (dod != null) {
      this.dodField.setText(df.format(dod));
    }

    this.father = person.getFather();
    if (this.father != null) {
      this.fatherField.setText(this.father.getFullName());
    }

    this.mother = person.getMother();
    if (this.mother != null) {
      this.motherField.setText(this.mother.getFullName());
    }
  }

  /**
   * General constructor called by others.
   */ 
  private EditPersonDialog(JFrame owner, String title, 
                           FamilyTree tree) {
    super(owner, title, true /* modal */);
    setupComponents(tree);
  }

  /**
   * Adds all of the components to this <code>EditPersonDialog</code>.
   */
  private void setupComponents(final FamilyTree tree) {
    Container pane = this.getContentPane();
    pane.setLayout(new BorderLayout());

    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new GridLayout(0, 2));
    Border infoBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5); 
    infoPanel.setBorder(infoBorder);

    infoPanel.add(new JLabel("id:"));
    infoPanel.add(idField);

    final ButtonGroup group = new ButtonGroup();

    group.add(male);
    male.setActionCommand("male");
    infoPanel.add(male);
    
    group.add(female);
    female.setActionCommand("female");
    infoPanel.add(female);

    infoPanel.add(new JLabel("First name:"));
    infoPanel.add(firstNameField);

    infoPanel.add(new JLabel("Middle name:"));
    infoPanel.add(middleNameField);

    infoPanel.add(new JLabel("Last name:"));
    infoPanel.add(lastNameField);

    infoPanel.add(new JLabel("Date of Birth:"));
    infoPanel.add(dobField);

    infoPanel.add(new JLabel("Date of Death:"));
    infoPanel.add(dodField);

    infoPanel.add(new JLabel("Father:"));
    fatherField.setEditable(false);
    fatherField.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          ChoosePersonDialog dialog = 
            new ChoosePersonDialog(tree, EditPersonDialog.this);

          dialog.pack();
          dialog.setLocationRelativeTo(EditPersonDialog.this);
          dialog.setVisible(true);
          
          Person father = dialog.getPerson();
          if (father != null) {
            EditPersonDialog.this.father = father;
            String fatherName =
              EditPersonDialog.this.father.getFullName(); 
            EditPersonDialog.this.fatherField.setText(fatherName);
          }
        }
      });
    infoPanel.add(fatherField);

    infoPanel.add(new JLabel("Mother:"));
    motherField.setEditable(false);
    motherField.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          ChoosePersonDialog dialog = 
            new ChoosePersonDialog(tree, EditPersonDialog.this);

          dialog.pack();
          dialog.setLocationRelativeTo(EditPersonDialog.this);
          dialog.setVisible(true);
          
          Person mother = dialog.getPerson();
          if (mother != null) {
            EditPersonDialog.this.mother = mother;
            String motherName =
              EditPersonDialog.this.mother.getFullName(); 
            EditPersonDialog.this.motherField.setText(motherName);
          }
        }
      });
    infoPanel.add(motherField);

    infoPanel.add(new JLabel("Parent of:"));
    childField.setEditable(false);
    childField.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          ChoosePersonDialog dialog = 
            new ChoosePersonDialog(tree, EditPersonDialog.this);

          dialog.pack();
          dialog.setLocationRelativeTo(EditPersonDialog.this);
          dialog.setVisible(true);
          
          Person child = dialog.getPerson();
          if (child != null) {
            EditPersonDialog.this.child = child;
            String childName = EditPersonDialog.this.child.getFullName();
            EditPersonDialog.this.childField.setText(childName);
          }
        }
      });
    infoPanel.add(childField);

    pane.add(infoPanel, BorderLayout.NORTH);

    // "OK" and "Cancel" buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel,
					BoxLayout.X_AXIS));
    buttonPanel.add(Box.createHorizontalGlue());

    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  // Create a new person based on the information entered in
	  // this dialog
          int id = 0;
          try {
            id = Integer.parseInt(idField.getText());

          } catch (NumberFormatException ex) {
            error("Invalid id: " + idField.getText());
            return;
          }

          String text = null;

          text = dobField.getText();
          Date dob = null;
          if (text != null && !text.equals("")) {
            dob = parseDate(dobField.getText());
            if (dob == null) {
              // Parse error
              return;
            }
          }

          text = dodField.getText();
          Date dod = null;
          if (text != null && !text.equals("")) {
            dod = parseDate(dodField.getText());
            if (dod == null) {
              // Parse error
              return;
            }
          }
          
          Gender gender;
          if (group.getSelection().getActionCommand().equals("male")) {
            gender = Person.MALE;

          } else {
            gender = Person.FEMALE;
          }
 
          // Okay, everything parsed alright
          if (person == null) {
            person = new Person(id, gender);
          }

          person.setFirstName(firstNameField.getText());
          person.setMiddleName(middleNameField.getText());
          person.setLastName(lastNameField.getText());
          person.setDateOfBirth(dob);
          person.setDateOfDeath(dod);

          if (mother != null) {
            person.setMother(mother);
          }

          if (father != null) {
            person.setFather(father);
          }

          if (child != null) {
            if (person.getGender() == Person.MALE) {
              child.setFather(person);

            } else {
              child.setMother(person);
            }
          }

          // We're all happy
	  EditPersonDialog.this.dispose();
	}
      });
    buttonPanel.add(okButton);

    buttonPanel.add(Box.createHorizontalGlue());

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  // Read my lips, no new Person!

	  EditPersonDialog.this.person = null;

	  EditPersonDialog.this.dispose();
	}
      });
    buttonPanel.add(cancelButton);

    buttonPanel.add(Box.createHorizontalGlue());

    pane.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Returns the <code>Person</code> created by this
   * <code>EditPersonDialog</code>.
   */
  public Person getPerson() {
    return this.person;
  }

  /**
   * Tries very, very hard to parse the a date.  We assume that the
   * text is neither empty nor <code>null</code>.
   */
  private Date parseDate(String text) {
    DateFormat formats[] = new DateFormat[] {
      DateFormat.getDateInstance(DateFormat.SHORT),
      DateFormat.getDateInstance(DateFormat.MEDIUM),
      DateFormat.getDateInstance(DateFormat.LONG),
      DateFormat.getDateInstance(DateFormat.FULL),
    };

    for (int i = 0; i < formats.length; i++) {
      DateFormat df = formats[i];
      try {
        Date date = df.parse(text);
        return date;

      } catch (ParseException ex) {
        continue;
      }
    }

    error("Could not parse date: " + text);
    return null;
  }

  /**
   * Pops up a dialog box with an error message in it.
   */
  private void error(String message) {
    JOptionPane.showMessageDialog(this, new String[] { message}, 
                                  "Error.",
                                  JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Simple test program.
   */
  public static void main(String[] args) {
    final JFrame frame = new JFrame("Testing EditPersonDialog");
    JButton button = new JButton("Click me");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          FamilyTree tree = new FamilyTree();
          EditPersonDialog dialog = 
            new EditPersonDialog(frame, tree);
          dialog.pack();
          dialog.setLocationRelativeTo(frame);
          dialog.setVisible(true);

          Person person = dialog.getPerson();
          if (person != null) {
            tree.addPerson(person);
            PrettyPrinter pretty = 
              new PrettyPrinter(new PrintWriter(System.out, true));
            pretty.dump(tree);
          }
        }
      });
    frame.getContentPane().add(button);

    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });
    frame.pack();
    frame.setVisible(true);
  }
}
