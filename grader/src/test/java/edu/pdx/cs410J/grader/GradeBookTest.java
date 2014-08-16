package edu.pdx.cs410J.grader;

import org.junit.Test;

import static edu.pdx.cs410J.grader.GradeBook.LetterGradeRanges.LetterGradeRange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class GradeBookTest {

  @Test
  public void defaultLetterGradeRanges() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges();
    assertLetterGradeRange(ranges, LetterGrade.A, 100.0, 95.0);
    assertLetterGradeRange(ranges, LetterGrade.A_MINUS, 95.0, 90.0);
  }

  private void assertLetterGradeRange(GradeBook.LetterGradeRanges ranges, LetterGrade letterGrade, double lessThan, double greaterThanOrEqualTo) {
    LetterGradeRange range = ranges.getRange(letterGrade);
    assertThat("No range for " + letterGrade, range, not(nullValue()));
    assertThat(range.lessThan(), equalTo(lessThan));
    assertThat(range.getGreaterThanOrEqualTo(), equalTo(greaterThanOrEqualTo));
  }
}
