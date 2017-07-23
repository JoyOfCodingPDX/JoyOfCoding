package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.EventBus;
import edu.pdx.cs410J.grader.mvp.PresenterOnEventBus;

import java.text.NumberFormat;

public abstract class ScorePresenter extends PresenterOnEventBus {
  protected final NumberFormat format;

  public ScorePresenter(EventBus bus) {
    super(bus);

    format = NumberFormat.getNumberInstance();
    format.setMinimumFractionDigits(1);
  }
}
