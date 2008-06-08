package edu.pdx.cs410E.jdbc;

import java.io.*;
import java.sql.*;

/**
 * This program loads a JDBC database driver and prints information
 about it gleaned from the {@link Driver} class.
 */
public class DriverInfo {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java DriverInfo DriverClass URL");
    err.println("");
    err.println("Example DriverClass: COM.cloudscape.core.RmiJdbcDriver");
    err.println("Example URL: jdbc:cloudscape:rmi:myDB");
    err.println("");
    System.exit(1);
  }

  public static void main(String[] args) {
    String driverName = null;
    String url = null;

    for (int i = 0; i < args.length; i++) {
      if (driverName == null) {
        driverName = args[i];

      } else if (url == null) {
        url = args[i];

      } else {
        usage("Spurious command line: " + args[i]);
      }
    }

    if (driverName == null) {
      usage("Missing driver name");
    }

    try {
      Class.forName(driverName).newInstance();

    } catch (Exception ex) {
      err.println("Could not load driver: " + ex);
      System.exit(1);
    }

    try {
      Driver driver = DriverManager.getDriver(url);
      out.println("Version " + driver.getMajorVersion() + "." +
                  driver.getMinorVersion());
      out.println((!driver.jdbcCompliant() ? "Not " : "") + 
                  "JDBC Compliant");

    } catch (SQLException ex) {
      ex.printStackTrace(err);
    }
    

  }
}
