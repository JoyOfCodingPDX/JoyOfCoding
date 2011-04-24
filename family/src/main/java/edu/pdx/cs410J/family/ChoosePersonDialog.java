package edu.pdx.cs410J.family;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * A <code>ChoosePersonDialog</code> is used to select a person from a
 * family tree.
 */
@SuppressWarnings("serial")
public class ChoosePersonDialog extends JDialog {

  private Person person;  // The chosen person

  /**
   * Creates a new <code>ChoosePersonDialog</code> that displays the
   * people in a given <code>FamilyTree</code>.
   */
  public ChoosePersonDialog(FamilyTree tree, JDialog owner) {
    super(owner, "Select a Person", true /* modal */);

    // A ChoosePersonDialog consists of a FamilyTreeList and a couple
    // of buttons
    Container pane = this.getContentPane();
    pane.setLayout(new BorderLayout());

    final FamilyTreeList treeList = new FamilyTreeList();
    treeList.fillInList(tree);

    JScrollPane treePane = new JScrollPane(treeList);
    Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    treePane.setBorder(border);

    pane.add(treePane, BorderLayout.CENTER);

    JPanel buttons = new JPanel();
    buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
    buttons.add(Box.createHorizontalGlue());

    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // Get selected person and dispose
          ChoosePersonDialog.this.person =
            treeList.getSelectedPerson();
          ChoosePersonDialog.this.dispose();
        }
      });
    buttons.add(okButton);

    buttons.add(Box.createHorizontalGlue());

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // No person selected
          ChoosePersonDialog.this.person = null;
          ChoosePersonDialog.this.dispose();
        }
      });
    buttons.add(cancelButton);

    buttons.add(Box.createHorizontalGlue());

    pane.add(buttons, BorderLayout.SOUTH);
  }

  /**
   * Returns the choosen person
   */
  public Person getPerson() {
    return this.person;
  }

}
