package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TestCaseOutputPresenter extends ScorePresenter {
  private final TestCaseOutputView view;
  private TestCaseOutput testCase;

  @Inject
  public TestCaseOutputPresenter(EventBus bus, TestCaseOutputView view) {
    super(bus);
    this.view = view;

    this.view.addPointsDeductedChangeListener(this::setPointsDeducted);
    this.view.addGraderCommentChangeListener(this::storeGraderComment);
  }

  private void setPointsDeducted(String pointsDeducted) {
    try {
      Double value = getValidScoreValue(pointsDeducted);
      this.testCase.setPointsDeducted(value);
      publishEvent(new TestCaseOutputUpdated(this.testCase));

      this.view.setPointsDeductedIsValid(true);

    } catch (InvalidScoreValue ex) {
      this.view.setPointsDeductedIsValid(false);
    }

  }

  private void storeGraderComment(String comment) {
    this.testCase.setGraderComment(comment);
  }

  @Subscribe
  public void populateViewWhenTestCaseSelected(TestCaseSelected selected) {
    testCase = selected.getTestCaseOutput();
    view.setCommand(testCase.getCommand());
    view.setDescription(testCase.getDescription());
    view.setOutput(testCase.getOutput());
    view.setGraderComment(testCase.getGraderComment());

    Double pointsDeducted = testCase.getPointsDeducted();
    if (pointsDeducted == null) {
      view.setPointsDeducted("");

    } else {
      view.setPointsDeducted(format.format(pointsDeducted));
    }
    view.setPointsDeductedIsValid(true);
  }

  @Override
  protected boolean isScoreInValidRange(Double score) {
    return score > 0.0;
  }
}
