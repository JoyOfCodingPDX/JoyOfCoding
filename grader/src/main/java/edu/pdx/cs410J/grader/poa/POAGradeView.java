package edu.pdx.cs410J.grader.poa;

public interface POAGradeView {
  void setIsEnabled(boolean isEnabled);

  void setIsLate(boolean isLate);

  void addIsLateHandler(IsLateHandler handler);

  void setTotalPoints(String totalPoints);

  void addScoreValueHandler(ScoreValueHandler handler);

  void setErrorInScore(boolean errorInScore);

  void setScore(String score);

  void setScoreHasBeenRecorded(boolean hasScoreBeenRecorded);

  void addRecordGradeHandler(RecordGradeHandler handler);

  public interface IsLateHandler {
    void setIsLate(boolean isLate);
  }

  public interface ScoreValueHandler {
    void scoreValue(String value);
  }

  public interface RecordGradeHandler {
    void recordGrade();
  }
}
