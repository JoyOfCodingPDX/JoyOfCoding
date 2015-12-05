package edu.pdx.cs410J;

import java.io.IOException;

/**
 * This interface allows the contents of an appointment book to be
 * dumped to some destination.
 *
 * @author David Whitlock
 * @version $Revision: 1.2 $
 * @since Fall 2000
 */
public interface AppointmentBookDumper {

  /**
   * Dumps an appointment book to some destination.
   *
   * @param book
   *        The appointment book whose contents are to be dumped
   *
   * @throws IOException
   *         Something went wrong while dumping the appointment book
   */
  public void dump(AbstractAppointmentBook book) throws IOException;

}
