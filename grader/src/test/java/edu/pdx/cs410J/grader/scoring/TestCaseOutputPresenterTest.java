package edu.pdx.cs410J.grader.scoring;

import org.junit.Test;

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
    TestCaseOutput testCase = new TestCaseOutput()
      .setDescription(description)
      .setCommand(command)
      .setOutput(output)
      .setPointsDeducted(null);

    this.bus.post(new TestCaseSelected(testCase));

    verify(view).setDescription(description);
    verify(view).setCommand(command);
    verify(view).setOutput(output);
    verify(view).setPointsDeducted("");
  }

  @Test
  public void pointDeductedAreDisplayedInView() {
    TestCaseOutput testCase = new TestCaseOutput()
      .setPointsDeducted(0.5);

    this.bus.post(new TestCaseSelected(testCase));

    verify(view).setPointsDeducted("0.5");
  }
}
