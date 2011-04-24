package edu.pdx.cs410J.family;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * This is a dialog for creating a new <code>Person</code>.
 */
@SuppressWarnings("serial")
public class AddPersonDialog extends JDialog {

  // The person we're creating
  private Person newPerson = null;

  private Person mother;
  private Person father;

  /**
   * Creates a new <code>AddPersonDialog</code> with a given owner
   * <code>JFrame</code> and <code>FamilyTree</code>.
   */
  public AddPersonDialog(JFrame owner, FamilyTree tree) {
    super(owner, "Add New Person", true /* modal */);

    Container pane = this.getContentPane();
    pane.setLayout(new BorderLayout());

    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new GridLayout(0, 2));
    Border infoBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5); 
    infoPanel.setBorder(infoBorder);

    infoPanel.add(new JLabel("id:"));
    final JTextField idField = new JTextField();
    infoPanel.add(idField);

    final ButtonGroup group = new ButtonGroup();

    final JRadioButton male = new JRadioButton("male", true);
    group.add(male);
    infoPanel.add(male);
    
    final JRadioButton female = new JRadioButton("female");
    group.add(female);
    infoPanel.add(female);

    infoPanel.add(new JLabel("First name:"));
    final JTextField firstNameField = new JTextField();
    infoPanel.add(firstNameField);

    infoPanel.add(new JLabel("Middle name:"));
    final JTextField middleNameField = new JTextField();
    infoPanel.add(middleNameField);

    infoPanel.add(new JLabel("Last name:"));
    final JTextField lastNameField = new JTextField();
    infoPanel.add(lastNameField);

    infoPanel.add(new JLabel("Date of Birth:"));
    final JTextField dobField = new JTextField();
    infoPanel.add(dobField);

    infoPanel.add(new JLabel("Date of Death:"));
    final JTextField dodField = new JTextField();
    infoPanel.add(dodField);

    infoPanel.add(new JLabel("Father:"));
    JPanel fatherPanel = new JPanel();
    fatherPanel.setLayout(new FlowLayout());
    final JTextField fatherText = new JTextField("Click to choose");
    fatherText.setEditable(false);
    fatherText.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          // Pop up a PersonChooseDialog, place name in TextField
          System.out.println("Clicked father");
        }
      });
    fatherPanel.add(fatherText);
    infoPanel.add(fatherPanel);

    infoPanel.add(new JLabel("Mother:"));
    JPanel motherPanel = new JPanel();
    motherPanel.setLayout(new FlowLayout());
    final JTextField motherText = new JTextField("Click to choose");
    motherText.setEditable(false);
    motherText.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          // Pop up a PersonChooseDialog, place name in TextField
          System.out.println("Clicked mother");
        }
      });
    motherPanel.add(motherText);
    infoPanel.add(motherPanel);

    pane.add(infoPanel, BorderLayout.NORTH);

    // "Add" and "Cancel" buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel,
					BoxLayout.X_AXIS));
    buttonPanel.add(Box.createHorizontalGlue());

    JButton addButton = new JButton("Add");
    addButton.addActionListener(new ActionListener() {
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

          Person.Gender gender;
          if (group.getSelection().equals(male)) {
            gender = Person.MALE;

          } else {
            gender = Person.FEMALE;
          }
          
          // Okay, everything parsed alright
          newPerson = new Person(id, gender);
          newPerson.setFirstName(firstNameField.getText());
          newPerson.setMiddleName(middleNameField.getText());
          newPerson.setLastName(lastNameField.getText());
          newPerson.setDateOfBirth(dob);
          newPerson.setDateOfDeath(dod);

          if (mother != null) {
            newPerson.setMother(mother);
          }

          if (father != null) {
            newPerson.setFather(father);
          }

          // We're all happy
	  AddPersonDialog.this.dispose();
	}
      });
    buttonPanel.add(addButton);

    buttonPanel.add(Box.createHorizontalGlue());

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  // Read my lips, no new Person!

	  AddPersonDialog.this.newPerson = null;

	  AddPersonDialog.this.dispose();
	}
      });
    buttonPanel.add(cancelButton);

    buttonPanel.add(Box.createHorizontalGlue());

    pane.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Returns the <code>Person</code> created by this
   * <code>AddPersonDialog</code>.
   */
  public Person getPerson() {
    return this.newPerson;
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
    final JFrame frame = new JFrame("Testing AddPersonDialog");
    JButton button = new JButton("Click me");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          FamilyTree tree = new FamilyTree();
          AddPersonDialog dialog = 
            new AddPersonDialog(frame, tree);
          dialog.pack();
          dialog.setLocationRelativeTo(frame);
          dialog.setVisible(true);

          Person newPerson = dialog.getPerson();
          if (newPerson != null) {
            tree.addPerson(newPerson);
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
