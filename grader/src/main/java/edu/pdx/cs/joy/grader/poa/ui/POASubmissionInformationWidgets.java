package edu.pdx.cs.joy.grader.poa.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs.joy.grader.poa.POASubmissionView;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.io.StringReader;

@Singleton
public class POASubmissionInformationWidgets implements POASubmissionView {

  private final JLabel subjectLabel;
  private final JLabel submitterLabel;
  private final JLabel submissionTimeLabel;
  private final JEditorPane submissionContent;
  private final POAGradeWidgets gradesWidgets;

  @Inject
  public POASubmissionInformationWidgets(POAGradeWidgets gradesWidgets) {
    this.gradesWidgets = gradesWidgets;

    this.subjectLabel = new JLabel();
    this.submitterLabel = new JLabel();
    this.submissionTimeLabel = new JLabel();
    this.submissionContent = new JEditorPane();
    this.submissionContent.setEditable(false);
  }

  public JComponent getSubjectWidget() {
    return createLabeledWidget("Subject:", this.subjectLabel);
  }

  private JPanel createLabeledWidget(String label, JLabel widget) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
    panel.add(new JLabel(label));
    panel.add(Box.createHorizontalStrut(3));
    panel.add(widget);
    return panel;
  }

  public JComponent getSubmitterWidget() {
    return createLabeledWidget("Submitted by:", this.submitterLabel);
  }

  public JComponent getSubmissionTimeWidget() {
    JPanel panel = createLabeledWidget("Submitted on:", this.submissionTimeLabel);
    panel.add(Box.createHorizontalGlue());
    panel.add(gradesWidgets.getIsLateCheckbox());
    return panel;
  }

  @Override
  public void setSubmissionSubject(String subject) {
    this.subjectLabel.setText(subject);
  }

  @Override
  public void setSubmissionSubmitter(String submitter) {
    this.submitterLabel.setText(submitter);
  }

  @Override
  public void setSubmissionTime(String time) {
    this.submissionTimeLabel.setText(time);
  }

  @Override
  public void setContent(String content, POAContentType contentType) throws CouldNotParseContent {
    this.submissionContent.setContentType(contentType.getContentType());
    if (contentType == POAContentType.HTML) {
      try {
        displayHtmlContent(content);

      } catch (IOException | BadLocationException ex) {
        throw new CouldNotParseContent(ex);
      }
    } else {
      this.submissionContent.setText(content);
    }
    scrollPaneToTop();
  }

  private void displayHtmlContent(String html) throws IOException, BadLocationException {
    HTMLEditorKit kit = (HTMLEditorKit) this.submissionContent.getEditorKit();
    HTMLDocument document = (HTMLDocument) this.submissionContent.getDocument();
    document.getDocumentProperties().put("IgnoreCharsetDirective", true);
    StringReader reader = new StringReader(html);
    kit.read(reader, document, 0);
  }

  private void scrollPaneToTop() {
    this.submissionContent.setCaretPosition(0);
  }

  public JComponent getSubmissionContentWidget() {
    return new JScrollPane(this.submissionContent);
  }
}
