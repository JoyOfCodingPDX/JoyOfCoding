#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs410J.ProjectXmlHelper;

public class AppointmentBookXmlHelper extends ProjectXmlHelper {

  /**
   * The System ID for the Family Tree DTD
   */
  protected static final String SYSTEM_ID =
    "http://www.cs.pdx.edu/~whitlock/dtds/apptbook.dtd";

  /**
   * The Public ID for the Family Tree DTD
   */
  protected static final String PUBLIC_ID =
    "-//Portland State University//DTD CS410J Appointment Book//EN";

  protected AppointmentBookXmlHelper() {
    super(PUBLIC_ID, SYSTEM_ID, "apptbook.dtd");
  }
}
