package edu.pdx.cs410J.junit;

/**
 * This class specifies the grades a student can get a section of
 * a course
 */
public final class Grade {
  /** A grade representing an A */
  public static final Grade A = new Grade();

  /** A grade representing an B */
  public static final Grade B = new Grade();

  /** A grade representing an C */
  public static final Grade C = new Grade();

  /** A grade representing an D */
  public static final Grade D = new Grade();

  /** A grade representing an F */
  public static final Grade F = new Grade();

  /**
   * Private constructor so that no one else can make a grade.
   */
  private Grade() {

  }

}
