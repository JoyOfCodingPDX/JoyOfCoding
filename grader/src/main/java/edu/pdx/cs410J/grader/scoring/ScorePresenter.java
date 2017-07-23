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

  protected Double getValidScoreValue(String score) throws InvalidScoreValue {
    if ("".equals(score)) {
      return null;

    } else {
      try {
        Double value = Double.parseDouble(score);
        if (isScoreInValidRange(value)) {
          return value;

        } else {
          throw new InvalidScoreValue(score);
        }

      } catch (NumberFormatException ex) {
        throw new InvalidScoreValue(score);
      }
    }
  }

  protected abstract boolean isScoreInValidRange(Double score);

  protected class InvalidScoreValue extends Exception {
    public InvalidScoreValue(String score) {
      super(score);
    }
  }
}
