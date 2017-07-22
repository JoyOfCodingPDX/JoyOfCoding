package edu.pdx.cs410J.grader.scoring;

import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestCasesPresenterTest extends ProjectSubmissionTestCase {

  private TestCasesView view;

  @Override
  public void setUp() {
    super.setUp();
    view = mock(TestCasesView.class);
    new TestCasesPresenter(bus, view);
  }

  @Test
  public void selectingSubmissionPopulatesTestCases() {
    ProjectSubmission submission = createProjectSubmission("Project", "student");
    String testCaseName = "test1";
    submission.addTestCaseOutput(new TestCaseOutput().setName(testCaseName));

    this.bus.post(new ProjectSubmissionSelected(submission));

    verify(view).setTestCaseNames(Collections.singletonList(testCaseName));
  }
}
