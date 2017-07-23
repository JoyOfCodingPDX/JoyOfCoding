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
      .setOutput(output);

    this.bus.post(new TestCaseSelected(testCase));

    verify(view).setDescription(description);
    verify(view).setCommand(command);
    verify(view).setOutput(output);
  }
}
