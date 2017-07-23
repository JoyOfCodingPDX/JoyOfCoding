package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.scoring.TestCasesView.TestCaseNameSelectedListener;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
    String testCaseName = "test";
    submission.addTestCaseOutput(new TestCaseOutput().setName(testCaseName));

    this.bus.post(new ProjectSubmissionSelected(submission));

    verify(view).setTestCaseNames(Collections.singletonList(testCaseName));
  }

  @Test
  public void selectingTestCasePublishesEvent() {
    ArgumentCaptor<TestCaseNameSelectedListener> listener = ArgumentCaptor.forClass(TestCaseNameSelectedListener.class);
    verify(view).addTestCaseNameSelectedListener(listener.capture());

    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.addTestCaseOutput(new TestCaseOutput().setName("test0"));
    TestCaseOutput testCase1 = new TestCaseOutput().setName("test1");
    submission.addTestCaseOutput(testCase1);

    this.bus.post(new ProjectSubmissionSelected(submission));

    // When the user selects the second test case...
    TestCaseOutputSelectedHandler handler = mock(TestCaseOutputSelectedHandler.class);
    bus.register(handler);
    listener.getValue().testCaseNameSelected(1);

    // Then a TestCaseSelected event for that test case is published
    ArgumentCaptor<TestCaseSelected> eventCaptor = ArgumentCaptor.forClass(TestCaseSelected.class);
    verify(handler).handle(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getTestCaseOutput(), equalTo(testCase1));

    verifyNoMoreInteractions(handler);
  }

  @Test
  public void firstTestCaseIsSelectedWhenSubmissionIsSelected() {
    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.addTestCaseOutput(new TestCaseOutput().setName("test1"));
    submission.addTestCaseOutput(new TestCaseOutput().setName("test2"));

    this.bus.post(new ProjectSubmissionSelected(submission));

    verify(view).setSelectedTestCaseName(0);

  }

  @Test
  public void pointsDeductedIsDisplayedInView() {
    ProjectSubmission submission = createProjectSubmission("Project", "student");
    String testCaseName = "Test 1";
    double pointsDeducted = 0.5;
    TestCaseOutput testCase = new TestCaseOutput()
      .setName(testCaseName)
      .setPointsDeducted(pointsDeducted);
    submission.addTestCaseOutput(testCase);

    this.bus.post(new ProjectSubmissionSelected(submission));

    String expected = TestCasesPresenter.formatTestCase(testCaseName, pointsDeducted);
    verify(view).setTestCaseNames(Collections.singletonList(expected));
  }

  @Test
  public void viewIsUpdatedWhenPointsAreDeducted() {
    ProjectSubmission submission = createProjectSubmission("Project", "student");

    String testName0 = "Test 0";
    submission.addTestCaseOutput(new TestCaseOutput().setName(testName0));
    String testName1 = "Test 1";
    TestCaseOutput testCase1 = new TestCaseOutput().setName(testName1);
    assertThat(testCase1.getPointsDeducted(), equalTo(null));
    submission.addTestCaseOutput(testCase1);

    this.bus.post(new ProjectSubmissionSelected(submission));
    verify(view).setTestCaseNames(Arrays.asList(testName0, testName1));
    verify(view).setSelectedTestCaseName(0);

    double pointsDeducted = 0.5;
    testCase1.setPointsDeducted(pointsDeducted);
    this.bus.post(new TestCaseOutputUpdated(testCase1));

    String expected = TestCasesPresenter.formatTestCase(testName1, pointsDeducted);
    verify(view).setTestCaseNames(Arrays.asList(testName0, expected));
    verify(view).setSelectedTestCaseName(1);
  }

  private interface TestCaseOutputSelectedHandler {
    @Subscribe
    public void handle(TestCaseSelected event);
  }
}
