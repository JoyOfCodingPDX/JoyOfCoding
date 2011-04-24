package edu.pdx.cs410J.family;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * This is a dialog for editing a <code>Marriage</code>.
 *
 * @author David Whitlock
 * @version $Revision: 1.6 $
 * @since Fall 2000
 */
@SuppressWarnings("serial")
public class EditMarriageDialog extends JDialog {

  // The Marriage we're editing
  private Marriage marriage = null;

  private Person husband = null;
  private Person wife = null;

  private boolean changeHusband = true;
  private boolean changeWife = true;

  // GUI components we need to hold on to
  private JTextField husbandField = new JTextField("Click to choose");
  private JTextField wifeField = new JTextField("Click to choose");
  private JTextField dateField = new JTextField();
  private JTextField locationField = new JTextField();

  /**
   * Creates a new <code>EditMarriageField</code> for adding a new
   * <code>Marriage</code> to a family tree.
   */
  public EditMarriageDialog(JFrame owner, FamilyTree tree) {
    this(owner, "Add New Marriage", tree);
  }

  /**
   * Creates a new <code>EditMarriageDialog</code> for creating a new
   * marriage involving one person.
   */
  public EditMarriageDialog(Person spouse, JFrame owner, 
                            FamilyTree tree) {
    this(owner, "Creating Marriage", tree);

    if (spouse.getGender() == Person.MALE) {
      // husband
      this.husband = spouse;
      this.husbandField.setText(this.husband.getFullName());
      this.husbandField.setEditable(false);
      this.changeHusband = false;

    } else {
      // wife
      this.wife = spouse;
      this.wifeField.setText(this.wife.getFullName());
      this.wifeField.setEditable(false);
      this.changeWife = false;
    }

    this.marriage = null;
  }

  /**
   * Creates a new <code>EditMarriageDialog</code> for editing an
   * existing marriage.
   */
  public EditMarriageDialog(Marriage marriage, JFrame owner, 
                            FamilyTree tree) {
    this(owner, "Edit Marriage", tree);
    this.marriage = marriage;

    // Fill in information about the marriage
    this.husband = marriage.getHusband();
    this.husbandField.setText(this.husband.getFullName());
    this.husbandField.setEditable(false);

    this.wife = marriage.getWife();
    this.wifeField.setText(this.wife.getFullName());
    this.wifeField.setEditable(false);
    
    Date date = marriage.getDate();
    if (date != null) {
      DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
      this.dateField.setText(df.format(date));
    }

    String location = marriage.getLocation();
    this.locationField.setText(location);
  }

  /**
   * General constructor called by others
   */
  private EditMarriageDialog(JFrame owner, String title, 
                             FamilyTree tree) {
    super(owner, title, true /* modal */);
    setupComponents(tree);
  }

  /**
   * Adds all of the components to this <code>EditMarriageDialog</code>.
   */
  private void setupComponents(final FamilyTree tree) {
    Container pane = this.getContentPane();
    pane.setLayout(new BorderLayout());

    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new GridLayout(4, 2));
    Border infoBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    infoPanel.setBorder(infoBorder);

    infoPanel.add(new JLabel("Husband:"));
    husbandField.setEditable(false);
    husbandField.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (!changeHusband) {
            return;
          }

          ChoosePersonDialog dialog =
            new ChoosePersonDialog(tree, EditMarriageDialog.this);

          dialog.pack();
          dialog.setLocationRelativeTo(EditMarriageDialog.this);
          dialog.setVisible(true);
         
          Person husband = dialog.getPerson();
          if (husband != null) {
            EditMarriageDialog.this.husband = husband;
            String husbandName =
              EditMarriageDialog.this.husband.getFullName();
            EditMarriageDialog.this.husbandField.setText(husbandName);
          }
        }
      });
    infoPanel.add(husbandField);

    infoPanel.add(new JLabel("Wife:"));
    wifeField.setEditable(false);
    wifeField.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (!changeWife) {
            return;
          }

          ChoosePersonDialog dialog =
            new ChoosePersonDialog(tree, EditMarriageDialog.this);

          dialog.pack();
          dialog.setLocationRelativeTo(EditMarriageDialog.this);
          dialog.setVisible(true);
         
          Person wife = dialog.getPerson();
          if (wife != null) {
            EditMarriageDialog.this.wife = wife;
            String wifeName =
              EditMarriageDialog.this.wife.getFullName();
            EditMarriageDialog.this.wifeField.setText(wifeName);
          }
        }
      });
    infoPanel.add(wifeField);

    infoPanel.add(new JLabel("Date:"));
    infoPanel.add(dateField);

    infoPanel.add(new JLabel("Location:"));
    infoPanel.add(locationField);

    pane.add(infoPanel, BorderLayout.NORTH);

    // "OK" and "Cancel" buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel,
					BoxLayout.X_AXIS));
    buttonPanel.add(Box.createHorizontalGlue());

    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  // Create a new marriage based on the information entered in
	  // this dialog
          if (husband == null) {
            error("Missing husband");
            return;
          }

          if (wife == null) {
            error("Missing wife");
            return;
          }

          String text = null;

          text = dateField.getText();
          Date date = null;
          if (text != null && !text.equals("")) {
            date = parseDate(text);
            if (date == null) {
              // Parse error
              return;
            }
          }
          
          // Everything parsed alright
          if (marriage == null) {
            marriage = new Marriage(husband, wife);
            husband.addMarriage(marriage);
            wife.addMarriage(marriage);
          }

          marriage.setDate(date);
          marriage.setLocation(locationField.getText());

          // We're all happy
	  EditMarriageDialog.this.dispose();
	}
      });
    buttonPanel.add(okButton);

    buttonPanel.add(Box.createHorizontalGlue());

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  // Read my lips, no new Marriage!

	  EditMarriageDialog.this.marriage = null;

	  EditMarriageDialog.this.dispose();
	}
      });
    buttonPanel.add(cancelButton);

    buttonPanel.add(Box.createHorizontalGlue());

    pane.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Returns the <code>Marriage</code> edited by this
   * <code>EditMarriageDialog</code>.
   */
  public Marriage getMarriage() {
    return this.marriage;
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

}
