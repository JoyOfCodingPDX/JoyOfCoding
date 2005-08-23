package edu.pdx.cs410E.family;

import java.sql.*;

/**
 * This program binds an instance of an
 * <code>RdbRemoteFamilyTree</code> in the RMI namespace for testing.
 */
public class RdbRemoteFamilyTreeTest extends RemoteFamilyTreeTest {

  public RdbRemoteFamilyTreeTest(String name) {
    super(name);
  }

  public static void main(String[] args) throws Throwable {
    RemoteFamilyTreeTest test = new RdbRemoteFamilyTreeTest("setup");

//     // Have to create the database first
//     Class.forName(RdbRemoteFamilyTree.DRIVER_NAME).newInstance();
//     String url = "jdbc:cloudscape:rmi:" + test.getFamilyName() +
//       ";create=true";
//     Connection conn = DriverManager.getConnection(url);
//     try {
//       CreateFamilyTreeTables.createTables(conn);

//     } catch (SQLException ex) {
//       ex.printStackTrace(System.err);
//       throw ex;
//     }

    test.bind(new RdbRemoteFamilyTree(test.getFamilyName()));
  }

}
