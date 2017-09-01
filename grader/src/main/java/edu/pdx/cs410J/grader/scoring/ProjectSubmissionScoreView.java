package edu.pdx.cs410J.grader.scoring;

public interface ProjectSubmissionScoreView {
  void setTotalPoints(String totalPoints);

  void setScore(String score);

  void addScoreChangedListener(ScoreChangedListener listener);

  void setScoreIsValid(boolean scoreIsInvalid);

  void addScoreSavedListener(ScoreSavedListener listener);

  interface ScoreChangedListener {
    void scoreChanged(String newScore);
  }

  interface ScoreSavedListener {
    void submissionSaved();
  }
}
