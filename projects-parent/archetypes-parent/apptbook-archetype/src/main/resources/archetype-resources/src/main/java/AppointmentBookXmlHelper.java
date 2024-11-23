#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs.joy.ProjectXmlHelper;

public class AppointmentBookXmlHelper extends ProjectXmlHelper {

  /**
   * The Public ID for the Family Tree DTD
   */
  protected static final String PUBLIC_ID =
    "-//Joy of Coding at PSU//DTD Appointment Book//EN";

  protected AppointmentBookXmlHelper() {
    super(PUBLIC_ID, "apptbook.dtd");
  }
}
