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

  private void assertLetterGradeRange(GradeBook.LetterGradeRanges ranges, LetterGrade letterGrade, int greaterThanOrEqualTo, int lessThan) {
    LetterGradeRange range = ranges.getRange(letterGrade);
    assertThat("No range for " + letterGrade, range, not(nullValue()));
    assertThat("Less than for " + letterGrade, range.lessThan(), equalTo(lessThan));
    assertThat(range.getGreaterThanOrEqualTo(), equalTo(greaterThanOrEqualTo));
  }

  @Test(expected = InvalidLetterGradeRange.class)
  public void lessThanValueGreaterThanGreaterThanOrEqualToValue() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges();
    LetterGradeRange range = ranges.getRange(LetterGrade.A);
    range.setRange(100, 99);
  }

  public void defaultLetterGradeRangesAreValid() {

  }

  public void rangesDoNotStartAtZero() {

  }

  public void rangesDoNotContain100() {

  }
}
