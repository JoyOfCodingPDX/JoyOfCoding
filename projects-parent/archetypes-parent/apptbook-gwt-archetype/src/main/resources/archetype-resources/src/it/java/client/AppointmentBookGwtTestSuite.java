#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;

public class AppointmentBookGwtTestSuite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Appointment Book GWT Integration Tests");

    suite.addTestSuite(AppointmentBookGwtIT.class);

    return suite;
  }

}
