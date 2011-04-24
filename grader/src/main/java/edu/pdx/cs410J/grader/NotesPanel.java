package edu.pdx.cs410J.grader;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This panel displays and edits notes.
 *
 * @author David Whitlock
 * @version $Revision: 1.6 $
 * @since Fall 2000
 */
@SuppressWarnings("serial")
public class NotesPanel extends JPanel {

  private Notable notable;

  // GUI components we care about
  private JList notesList;
  private JButton add;

  /**
   * Creates a new <code>NotePanel</code> for displaying the notes of
   * some <code>Notable</code> object.
   */
  public NotesPanel() {
    this.setLayout(new BorderLayout());

    Border notesBorder = BorderFactory.createTitledBorder("Notes");
    this.setBorder(notesBorder);

    this.notesList = new JList();
    this.add(new JScrollPane(notesList), BorderLayout.CENTER);

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());

    final JTextField field = new JTextField(20);
    panel.add(field);

    ActionListener action = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String note = field.getText();
          if (note != null && !note.equals("") && notable != null) {
            notable.addNote(note);
            setNotable(notable);
          }

          field.setText("");
        }
      };
    field.addActionListener(action);

    this.add = new JButton("Add");
    this.add.setEnabled(false);
    this.add.addActionListener(action);
    panel.add(this.add);

    final JButton delete = new JButton("Delete");
    delete.setEnabled(false);
    delete.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String note = (String) notesList.getSelectedValue();
          if (note == null) {
            return;
          }

          if (notable != null) {
            notable.removeNote(note);
            setNotable(notable);
          }
        }
      });
    notesList.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if (notesList.isSelectionEmpty()) {
            delete.setEnabled(false);

          } else {
            delete.setEnabled(true);
          }
        }
      });
    panel.add(delete);

    this.add(panel, BorderLayout.SOUTH);
  }

  /**
   * Sets the <code>Notable</code> that is being displayed/edited by
   * this <code>NotesPanel</code>.
   */
  public void setNotable(Notable notable) {
    clearNotes();   // Start from scratch
    this.notable = notable;
    this.notesList.setListData(notable.getNotes().toArray());
    this.add.setEnabled(true);
  }

  /**
   * Returns the <code>Notable</code> edited by this
   * <code>NotesPanel</code>. 
   */
  public Notable getNotable() {
    return this.notable;
  }

  /**
   * Clears the contents of the notes list
   */
  public void clearNotes() {
    this.notesList.setListData(new Vector());
    this.notable = null;
  }

}
