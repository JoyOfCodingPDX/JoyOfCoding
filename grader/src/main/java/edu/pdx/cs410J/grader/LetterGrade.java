package edu.pdx.cs410J.grader;

public enum LetterGrade {

  A("A")

  ;
  private final String stringValue;

  LetterGrade(String stringValue) {
    this.stringValue = stringValue;
  }

  public static LetterGrade fromString(String string) {
    for (LetterGrade grade : values()) {
      if (grade.asString().equals(string)) {
        return grade;
      }
    }

    throw new IllegalArgumentException("Could not find LetterGrade for string \"" + string + "\"");
  }

  public String asString() {
    return stringValue;
  }

}
