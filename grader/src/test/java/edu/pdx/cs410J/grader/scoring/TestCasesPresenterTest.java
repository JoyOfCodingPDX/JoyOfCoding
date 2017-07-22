package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.scoring.TestCasesView.TestCaseNameSelectedListener;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

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

  private interface TestCaseOutputSelectedHandler {
    @Subscribe
    public void handle(TestCaseSelected event);
  }
}
