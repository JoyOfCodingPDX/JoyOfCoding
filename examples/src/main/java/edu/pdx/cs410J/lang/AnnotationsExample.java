package edu.pdx.cs410J.lang;

import java.util.Date;

/**
 * Demonstrates several of the built-in annotations
 *
 * @deprecated This class shouldn't be used anymore
 */
@Deprecated
public class AnnotationsExample {

  private final Date now = new Date();

  @SuppressWarnings({"deprecation"})
  @Override
  public boolean equals(Object o) {
    if (o instanceof AnnotationsExample) {
      AnnotationsExample other = (AnnotationsExample) o;
      return this.now.getDay() == other.now.getDay() &&
        this.now.getMonth() == other.now.getMonth() &&
        this.now.getYear() == other.now.getYear();
    }

    return false;
  }

}