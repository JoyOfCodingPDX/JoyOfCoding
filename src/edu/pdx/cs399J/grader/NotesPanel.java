package edu.pdx.cs410J.grader;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This panel displays and edits notes.
 */
public class NotesPanel extends JPanel {

  private Vector notes;
  private Notable notable;

  // GUI components we care about
  private JList notesList;

  /**
   * Creates a new <code>NotePanel</code> for displaying the notes of
   * some <code>Notable</code> object.
   */
  public NotesPanel() {
    this.notes = new Vector();

    this.setLayout(new BorderLayout());

    Border notesBorder = BorderFactory.createTitledBorder("Notes");
    this.setBorder(notesBorder);

    this.notesList = new JList();
    this.add(new JScrollPane(notesList), BorderLayout.CENTER);

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());

    final JTextField field = new JTextField(20);
    panel.add(field);

    JButton button = new JButton("Add");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String note = field.getText();
          if(note != null && !note.equals("")) {
            notes.add(note);
            notesList.setListData(notes);
          }

          if(notable != null) {
            notable.addNote(note);
          }

          field.setText("");
        }
      });
    panel.add(button);

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
    this.notes.addAll(notable.getNotes());
  }

  /**
   * Clears the contents of the notes list
   */
  public void clearNotes() {
    this.notesList.setListData(new Vector());
    this.notes = new Vector();
    this.notable = null;
  }

  /**
   * Adds all notes from this <code>NotesPanel</code> to a
   * <code>Notable</code>.  This method is intended to be called when
   * we are creating a <code>Notable</code>.
   */
  public void addAllNotesTo(Notable notable) {
    Iterator iter = this.notes.iterator();
    while(iter.hasNext()) {
      notable.addNote((String) iter.next());
    }
    this.setNotable(notable);
  }

}
