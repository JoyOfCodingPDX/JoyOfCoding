package edu.pdx.cs410J;

/**
 * Classes that implement this interface dump the contents of an
 * airline to some destination.
 */
public interface Dumper {

  /**
   * Dumps an airline to some destination.
   */
  public void dump(AbstractAirline airline);

}
