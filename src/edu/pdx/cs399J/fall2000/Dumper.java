package edu.pdx.cs399J;

/**
 * This interface allows the contents of an appointment book to be
 * dumped to some destination.
 *
 * @author David Whitlock
 */
public interface Dumper {

  /**
   * Dumps an appointment book to some destination.
   *
   * @param book
   *        The appointment book whose contents are to be dumped
   */
  public void dump(AbstractAppointmentBook book);

}
