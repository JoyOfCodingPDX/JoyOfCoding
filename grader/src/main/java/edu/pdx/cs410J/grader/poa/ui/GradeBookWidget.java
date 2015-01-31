package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.GradeBookView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

@Singleton
public class GradeBookWidget extends JPanel implements GradeBookView {
  private final JLabel gradeBookName;
  private final JButton loadGradeBookButton;
  private final JButton saveGradeBookButton;
  private final Component parentComponent;

  @Inject
  public GradeBookWidget(TopLevelJFrame parentFrame) {
    this.parentComponent = parentFrame;
    gradeBookName = new JLabel();
    loadGradeBookButton = new JButton("Load grade book");
    saveGradeBookButton = new JButton("Save grade book");
    saveGradeBookButton.setEnabled(false);

    this.setLayout(new FlowLayout());
    this.add(gradeBookName);
    this.add(loadGradeBookButton);
    this.add(saveGradeBookButton);
  }

  @Override
  public void setGradeBookName(String className) {
    this.gradeBookName.setText(className);
  }

  @Override
  public void addGradeBookFileListener(FileSelectedListener listener) {
    this.loadGradeBookButton.addActionListener(e -> displayFileChooser(listener));
  }

  @Override
  public void canSaveGradeBook(boolean canSaveGradeBook) {
    this.saveGradeBookButton.setEnabled(canSaveGradeBook);
  }

  private void displayFileChooser(FileSelectedListener listener) {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Select Grade Book file");
    chooser.setApproveButtonMnemonic('l');
    chooser.setApproveButtonText("Load");
    chooser.setApproveButtonToolTipText("Load Grade Book from file");
    chooser.setFileFilter(new FileNameExtensionFilter("Grade Book XML files", "xml"));
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    int result = chooser.showOpenDialog(parentComponent);
    if (result == JFileChooser.APPROVE_OPTION) {
      listener.fileSelected(chooser.getSelectedFile());
    }
  }
}
