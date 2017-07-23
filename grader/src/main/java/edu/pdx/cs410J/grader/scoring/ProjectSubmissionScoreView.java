package edu.pdx.cs410J.grader.scoring;

interface ProjectSubmissionScoreView {
  void setTotalPoints(String totalPoints);

  void setScore(String score);

  void addScoreChangedListener(ScoreChangedListener listener);

  void setScoreIsValid(boolean scoreIsInvalid);

  interface ScoreChangedListener {
    void scoreChanged(String newScore);
  }
}
