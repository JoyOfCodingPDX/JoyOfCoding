package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TestCaseOutputPresenter extends ScorePresenter {
  private final TestCaseOutputView view;

  @Inject
  public TestCaseOutputPresenter(EventBus bus, TestCaseOutputView view) {
    super(bus);
    this.view = view;
  }

  @Subscribe
  public void populateViewWhenTestCaseSelected(TestCaseSelected selected) {
    TestCaseOutput testCase = selected.getTestCaseOutput();
    view.setCommand(testCase.getCommand());
    view.setDescription(testCase.getDescription());
    view.setOutput(testCase.getOutput());

    Double pointsDeducted = testCase.getPointsDeducted();
    if (pointsDeducted == null) {
      view.setPointsDeducted("");

    } else {
      view.setPointsDeducted(format.format(pointsDeducted));
    }
  }
}
