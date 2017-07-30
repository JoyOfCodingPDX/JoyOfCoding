package edu.pdx.cs410J.grader.scoring.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.mvp.ui.TopLevelJFrame;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsLoaderSaverView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ProjectSubmissionsLoaderSaverPanel extends JPanel implements ProjectSubmissionsLoaderSaverView {

  private final TopLevelJFrame parent;
  private final List<DirectorySelectedListener> listeners = new ArrayList<>();

  @Inject
  public ProjectSubmissionsLoaderSaverPanel(TopLevelJFrame parent) {
    this.parent = parent;
    JButton load = new JButton("Load XML files");
    load.addActionListener(this::showDirectoryChooser);
    this.add(load);
  }

  private void showDirectoryChooser(ActionEvent actionEvent) {
    JFileChooser chooser = getFileChooser();
    int response = chooser.showOpenDialog(parent);

    if (response == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      loadFromDirectory(file);
    }
  }

  private void loadFromDirectory(File directory) {
    this.listeners.forEach(l -> l.directorySelected(directory));
  }

  private JFileChooser getFileChooser() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Choose a directory containing submission XML files");
//    chooser.setFileFilter(new FileFilter() {
//      @Override
//      public boolean accept(File f) {
//        return f.isDirectory() || f.getName().endsWith(".xml");
//      }
//
//      @Override
//      public String getDescription() {
//        return "Directory or .xml files";
//      }
//    });
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);

    return chooser;
  }

  @Override
  public void addDirectorySelectedListener(DirectorySelectedListener listener) {
    this.listeners.add(listener);
  }
}
