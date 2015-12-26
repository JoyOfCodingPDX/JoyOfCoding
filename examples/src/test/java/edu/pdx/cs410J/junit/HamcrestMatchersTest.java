package edu.pdx.cs410J.junit;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This test demonstrates the use of various matchers from the Hamcrest library
 * for performing more advanced assertions in unit tests.
 *
 * <a href="http://hamcrest.org/JavaHamcrest">http://hamcrest.org/JavaHamcrest</a>
 *
 * @since Summer 2013
 */
public class HamcrestMatchersTest {

  @Test
  public void isEqualTo() {
    Integer int1 = new Integer("123");
    Integer int2 = new Integer("123");
    assertThat(int1, is(equalTo(int2)));
  }

  @Test
  public void isNullValue() {
    assertThat(null, is(nullValue()));
  }

  @Test
  public void isSameInstance() {
    Object o = new Object();
    assertThat(o, is(sameInstance(o)));
  }

  @Test
  public void strings() {
    String s = "Hamcrest is awesome";
    assertThat(s, startsWith("Hamcrest"));
    assertThat(s, endsWith("awesome"));
    assertThat(s, containsString("is"));
    assertThat(s, is(not(isEmptyString())));
    assertThat(s, is(equalToIgnoringCase("HAMCREST IS AWESOME")));
  }

  @Test
  public void everyItemIsNotNull() {
    List<String> list = Arrays.asList("a", "b", "c");
    assertThat(list, everyItem(is(notNullValue(String.class))));
  }

  @Test
  public void numbers() {
    double pi = 3.1415;
    assertThat(pi, is(greaterThan(2.0)));
    assertThat(pi, is(both(greaterThan(1.0)).and(lessThan(4.0))));
    assertThat(pi, is(closeTo(3.14, 0.01)));
  }

  @Test
  public void arrays() {
    Integer[] array = { 1, 2, 3, 4, 5};
    assertThat(array, hasItemInArray(4));
    assertThat(array, is(arrayWithSize(5)));
    assertThat(array, is(not(emptyArray())));
    assertThat(4, isIn(array));
  }
}
