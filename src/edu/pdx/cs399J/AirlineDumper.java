package edu.pdx.cs399J;

/**
 * Classes that implement this interface dump the contents of an
 * airline to some destination.
 */
public interface AirlineDumper {

  /**
   * Dumps an airline to some destination.
   */
  public void dump(AbstractAirline airline);

}
