package edu.pdx.cs410J.grader.scoring.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.scoring.TestCasesView;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

@Singleton
public class TestCasesPanel extends JPanelWithJList implements TestCasesView{
  private final JList<String> testCaseNames;

  public TestCasesPanel() {
    testCaseNames = new JList<>();
    testCaseNames.setVisibleRowCount(20);

    this.setLayout(new BorderLayout());

    this.add(new JScrollPane(testCaseNames), BorderLayout.CENTER);
  }

  @Override
  public void setTestCaseNames(List<String> testCaseNames) {
    this.testCaseNames.setListData(new Vector<>(testCaseNames));
    this.updateUI();
  }

  @Override
  public void setSelectedTestCaseName(int index) {
    this.testCaseNames.setSelectedIndex(index);
  }

  @Override
  public void addTestCaseNameSelectedListener(TestCaseNameSelectedListener listener) {
    registerListenerOnListItemSelection(this.testCaseNames, listener::testCaseNameSelected);
  }
}
