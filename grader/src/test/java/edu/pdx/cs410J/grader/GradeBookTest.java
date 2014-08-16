package edu.pdx.cs410J.grader;

import org.junit.Test;

import static edu.pdx.cs410J.grader.GradeBook.LetterGradeRanges.LetterGradeRange;
import static edu.pdx.cs410J.grader.GradeBook.LetterGradeRanges.LetterGradeRange.InvalidLetterGradeRange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GradeBookTest {

  @Test
  public void defaultLetterGradeRanges() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges();
    assertLetterGradeRange(ranges, LetterGrade.A, 94, 100);
    assertLetterGradeRange(ranges, LetterGrade.A_MINUS, 90, 93);
    assertLetterGradeRange(ranges, LetterGrade.B_PLUS, 87, 89);
    assertLetterGradeRange(ranges, LetterGrade.B, 83, 86);
    assertLetterGradeRange(ranges, LetterGrade.B_MINUS, 80, 82);
    assertLetterGradeRange(ranges, LetterGrade.C_PLUS, 77, 79);
    assertLetterGradeRange(ranges, LetterGrade.C, 73, 76);
    assertLetterGradeRange(ranges, LetterGrade.C_MINUS, 70, 72);
    assertLetterGradeRange(ranges, LetterGrade.D_PLUS, 67, 69);
    assertLetterGradeRange(ranges, LetterGrade.D, 63, 66);
    assertLetterGradeRange(ranges, LetterGrade.D_MINUS, 60, 62);
    assertLetterGradeRange(ranges, LetterGrade.F, 0, 59);
  }

  private void assertLetterGradeRange(GradeBook.LetterGradeRanges ranges, LetterGrade letterGrade, int minimum, int maximum) {
    LetterGradeRange range = ranges.getRange(letterGrade);
    assertThat("No range for " + letterGrade, range, not(nullValue()));
    assertThat("Less than for " + letterGrade, range.maximum(), equalTo(maximum));
    assertThat(range.minimum(), equalTo(minimum));
  }

  @Test(expected = InvalidLetterGradeRange.class)
  public void minimumValueGreaterThanMaximumValue() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges();
    LetterGradeRange range = ranges.getRange(LetterGrade.A);
    range.setRange(100, 99);
  }

  @Test(expected = InvalidLetterGradeRange.class)
  public void minimumValueEqualsMaximumValue() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges();
    LetterGradeRange range = ranges.getRange(LetterGrade.A);
    range.setRange(99, 99);
  }

  @Test
  public void defaultLetterGradeRangesAreValid() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges();
    ranges.validate();
  }

  @Test(expected = InvalidLetterGradeRange.class)
  public void rangesDoNotStartAtZero() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges();
    LetterGradeRange range = ranges.getRange(LetterGrade.F);
    range.setRange(2, range.maximum());
    ranges.validate();
  }

  @Test(expected = InvalidLetterGradeRange.class)
  public void rangesDoNotContain100() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges();
    LetterGradeRange range = ranges.getRange(LetterGrade.A);
    range.setRange(range.minimum(), 99);
    ranges.validate();
  }
}
