package edu.pdx.cs.joy.grader.gradebook;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GradeBookTest {

  @Test
  public void defaultLetterGradeRanges() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges undergrad = book.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    assertDefaultLetterGradeRanges(undergrad);

    GradeBook.LetterGradeRanges grad = book.getLetterGradeRanges(Student.Section.GRADUATE);
    assertDefaultLetterGradeRanges(grad);
  }

  private void assertDefaultLetterGradeRanges(GradeBook.LetterGradeRanges ranges) {
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
    GradeBook.LetterGradeRanges.LetterGradeRange range = ranges.getRange(letterGrade);
    assertThat("No range for " + letterGrade, range, not(nullValue()));
    assertThat("Less than for " + letterGrade, range.maximum(), equalTo(maximum));
    assertThat(range.minimum(), equalTo(minimum));
  }

  @Test
  public void minimumValueGreaterThanMaximumValue() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    GradeBook.LetterGradeRanges.LetterGradeRange range = ranges.getRange(LetterGrade.A);
    assertThrows(GradeBook.LetterGradeRanges.LetterGradeRange.InvalidLetterGradeRange.class, () ->
      range.setRange(100, 99)
    );
  }

  @Test
  public void minimumValueEqualsMaximumValue() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    GradeBook.LetterGradeRanges.LetterGradeRange range = ranges.getRange(LetterGrade.A);
    assertThrows(GradeBook.LetterGradeRanges.LetterGradeRange.InvalidLetterGradeRange.class, () ->
      range.setRange(99, 99)
    );
  }

  @Test
  public void defaultLetterGradeRangesAreValid() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    ranges.validate();
  }

  @Test
  public void rangesDoNotStartAtZero() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    GradeBook.LetterGradeRanges.LetterGradeRange range = ranges.getRange(LetterGrade.F);
    range.setRange(2, range.maximum());
    assertThrows(GradeBook.LetterGradeRanges.LetterGradeRange.InvalidLetterGradeRange.class, ranges::validate);
  }

  @Test
  public void rangesDoNotContain100() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    GradeBook.LetterGradeRanges.LetterGradeRange range = ranges.getRange(LetterGrade.A);
    range.setRange(range.minimum(), 99);
    assertThrows(GradeBook.LetterGradeRanges.LetterGradeRange.InvalidLetterGradeRange.class, ranges::validate);
  }

  @Test
  public void rangesAreContiguous() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    GradeBook.LetterGradeRanges.LetterGradeRange range = ranges.getRange(LetterGrade.A);
    range.setRange(range.minimum() + 1, range.maximum());
    assertThrows(GradeBook.LetterGradeRanges.LetterGradeRange.InvalidLetterGradeRange.class, ranges::validate);
  }

  @Test
  public void rangesAreContiguousNotARange() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    GradeBook.LetterGradeRanges.LetterGradeRange range = ranges.getRange(LetterGrade.C);
    range.setRange(range.minimum(), range.maximum() - 1);
    assertThrows(GradeBook.LetterGradeRanges.LetterGradeRange.InvalidLetterGradeRange.class, ranges::validate);
  }

  @Test
  public void scoreIsInRange() {
    GradeBook book = new GradeBook("test");
    GradeBook.LetterGradeRanges ranges = book.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    GradeBook.LetterGradeRanges.LetterGradeRange range = ranges.getRange(LetterGrade.A);
    range.setRange(90, 100);

    assertThat(range.isScoreInRange(90.0), is(true));
    assertThat(range.isScoreInRange(100.0), is(true));
    assertThat(range.isScoreInRange(95.0), is(true));
    assertThat(range.isScoreInRange(89.5), is(true));
    assertThat(range.isScoreInRange(101.0), is(false));
    assertThat(range.isScoreInRange(89.0), is(false));
    assertThat(range.isScoreInRange(89.4), is(false));

  }

  @Test
  public void scoresAreCorrectlyMappedToLetterGrades() {
    GradeBook book = new GradeBook("test");
    assertThat(book.getLetterGradeForScore(Student.Section.UNDERGRADUATE, 99.0), equalTo(LetterGrade.A));
    assertThat(book.getLetterGradeForScore(Student.Section.UNDERGRADUATE, 93.0), equalTo(LetterGrade.A_MINUS));
  }

}
