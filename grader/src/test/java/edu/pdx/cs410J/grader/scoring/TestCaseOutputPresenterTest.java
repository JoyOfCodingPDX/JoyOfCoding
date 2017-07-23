package edu.pdx.cs410J.grader.scoring;

import edu.pdx.cs410J.grader.scoring.TestCaseOutputView.GraderCommentChangeListener;
import edu.pdx.cs410J.grader.scoring.TestCaseOutputView.PointsDeductedChangeListener;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestCaseOutputPresenterTest extends ProjectSubmissionTestCase {

  private TestCaseOutputView view;

  @Override
  public void setUp() {
    super.setUp();
    view = mock(TestCaseOutputView.class);
    new TestCaseOutputPresenter(bus, view);
  }

  @Test
  public void testCaseOutputDetailsAreViewedWhenTestCaseSelected() {
    String description = "My Test Case";
    String command = "My Command";
    String output = "My Output";
    String comment = "My Grader Comment";
    TestCaseOutput testCase = new TestCaseOutput()
      .setDescription(description)
      .setCommand(command)
      .setOutput(output)
      .setPointsDeducted(null)
      .setGraderComment(comment);

    this.bus.post(new TestCaseSelected(testCase));

    verify(view).setDescription(description);
    verify(view).setCommand(command);
    verify(view).setOutput(output);
    verify(view).setPointsDeducted("");
    verify(view).setGraderComment(comment);
  }

  @Test
  public void pointsDeductedAreDisplayedInView() {
    TestCaseOutput testCase = new TestCaseOutput()
      .setPointsDeducted(0.5);

    this.bus.post(new TestCaseSelected(testCase));

    verify(view).setPointsDeductedIsValid(true);
    verify(view).setPointsDeducted("0.5");
  }

  @Test
  public void invalidPointsDeductedPutViewInErrorState() {
    ArgumentCaptor<PointsDeductedChangeListener> listener = ArgumentCaptor.forClass(PointsDeductedChangeListener.class);
    verify(view).addPointsDeductedChangeListener(listener.capture());

    this.bus.post(new TestCaseSelected(new TestCaseOutput()));

    String invalidPoints = "QQ";
    listener.getValue().onPointsDeductedChange(invalidPoints);

    verify(view).setPointsDeductedIsValid(false);
  }

  @Test
  public void negativePointsDeductedPutViewInErrorState() {
    ArgumentCaptor<PointsDeductedChangeListener> listener = ArgumentCaptor.forClass(PointsDeductedChangeListener.class);
    verify(view).addPointsDeductedChangeListener(listener.capture());

    this.bus.post(new TestCaseSelected(new TestCaseOutput()));

    String invalidPoints = "-1.0";
    listener.getValue().onPointsDeductedChange(invalidPoints);

    verify(view).setPointsDeductedIsValid(false);
  }

  @Test
  public void graderCommentIsSavedInTestCaseOutput() {
    ArgumentCaptor<GraderCommentChangeListener> listener = ArgumentCaptor.forClass(GraderCommentChangeListener.class);
    verify(view).addGraderCommentChangeListener(listener.capture());

    TestCaseOutput testCase = new TestCaseOutput();

    this.bus.post(new TestCaseSelected(testCase));

    String comment = "My new comment";
    listener.getValue().onGraderCommentChange(comment);

    assertThat(testCase.getGraderComment(), equalTo(comment));
  }
}
