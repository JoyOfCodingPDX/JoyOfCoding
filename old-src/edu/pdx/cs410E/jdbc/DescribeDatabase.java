package edu.pdx.cs410E.jdbc;

import java.io.*;
import java.sql.*;

/**
 * This program loads gets a connection to a database and prints out
 * information about the database.
 */
public class DescribeDatabase {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java DescribeDatabase DriverClass URL");
    err.println("");
    err.println("Example DriverClass: COM.cloudscape.core.RmiJdbcDriver");
    err.println("Example URL: jdbc:cloudscape:rmi:myDB;create=true");
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
      Connection conn = DriverManager.getConnection(url);
      DatabaseMetaData meta = conn.getMetaData();
      out.println("Connected with dirver " + meta.getDriverName() + 
                  " Version " + meta.getDriverVersion());
//       out.println("  JDBC Version " + meta.getJDBCMajorVersion() + "." +
//                   meta.getJDBCMinorVersion());

      out.println("User name: " + meta.getUserName());
      out.println("URL: " + meta.getURL());

      

      out.println("Catalogs:");
      ResultSet rs = meta.getCatalogs();
      while (rs.next()) {
        out.println("  " + rs.getString("TABLE_CAT"));
      }

      out.println("Schemas:");
      rs = meta.getSchemas();
      while (rs.next()) {
        out.println("  " + rs.getString("TABLE_SCHEM") + 
                    " in catalog " + rs.getString("TABLE_CATALOG"));
      }
      
    } catch (SQLException ex) {
      ex.printStackTrace(err);
    }
    

  }
}
