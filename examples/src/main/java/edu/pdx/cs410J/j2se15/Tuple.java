package edu.pdx.cs410J.j2se15;

/**
 * A tuple class that holds two generic values.  Demonstrates how
 * multiple values can be returned from a method in a type-safe manner
 * using Java generics.
 *
 * @see TupleExample
 *
 * @author David Whitlock
 * @since Summer 2005
 */
public final class Tuple<A,B> {
  private final A first;
  private final B second;

  public Tuple(A first, B second) {
    this.first = first;
    this.second = second;
  }

  public A getFirst() {
    return this.first;
  }

  public B getSecond() {
    return this.second;
  }
}
