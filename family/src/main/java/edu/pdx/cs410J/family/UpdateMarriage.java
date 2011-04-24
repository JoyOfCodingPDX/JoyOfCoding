package edu.pdx.cs410J.family;

import java.io.*;
import java.rmi.*;
import java.text.*;
import java.util.*;

/**
 * This program is an RMI client that adds a person to a remote family
 * tree
 */
public class UpdateMarriage {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java UpdateMarriage familyName host port " +
                "husbandId wifeId");
    err.println("  -date Date");
    err.println("  -location Location");
    err.println("");
    err.println("This program updates the information about a marriage "
                + "between two people in a remote family tree.");
    err.println("If the marriage does not already exist, a "
                + "new person will be created.");
    err.println("");
    err.println("Dates should be in the form MM/DD/YYYY");
    err.println("");
    System.exit(1);
  }

  public static void main(String[] args) {
    String familyName = null;
    String host = null;
    int port = -1;
    int husbandId = -1;
    int wifeId = -1;
    Date date = null;
    String location = null;

    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-date")) {
        if (++i >= args.length) {
          usage("Missing date");
        }

        try {
          date = df.parse(args[i]);
          
        } catch (ParseException ex) {
          usage("Malformed date: " + args[i]);
        }

      } else if (args[i].equals("-location")) {
        if (++i >= args.length) {
          usage("Missing location");
        }

        location = args[i];

      } else if (familyName == null) {
        familyName = args[i];

      } else if (host == null) {
        host = args[i];

      } else if (port == -1) {
        try {
          port = Integer.parseInt(args[i]);

        } catch (NumberFormatException ex) {
          usage("Invalid port: " + args[i]);
        }

      } else if (husbandId == -1) {
        try {
          husbandId = Integer.parseInt(args[i]);

        } catch (NumberFormatException ex) {
          usage("Invalid husband id: " + args[i]);
        }

      } else if (wifeId == -1) {
        try {
          wifeId = Integer.parseInt(args[i]);

        } catch (NumberFormatException ex) {
          usage("Invalid wife id: " + args[i]);
        }

      } else {
        usage("Spurious command line: " + args[i]);
      }
    }

    if (familyName == null) {
      usage("Missing family name");

    } else if (host == null) {
      usage("Missing host");

    } else if (port == -1) {
      usage("Missing port");

    } else if (husbandId == -1) {
      usage("Missing husband id");

    } else if (wifeId == -1) {
      usage("Missing wife id");
    }

    // Install an RMISecurityManager, if there is not a
    // SecurityManager already installed
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    // Look up the remote family tree object
    String name = "rmi://" + host + ":" + port + "/" + familyName;

    try {
      RemoteFamilyTree tree = 
        (RemoteFamilyTree) Naming.lookup(name);

      RemoteMarriage marriage = tree.getMarriage(husbandId, wifeId);
      if (marriage == null) {
        marriage = tree.createMarriage(husbandId, wifeId);
      }

      if (date != null) {
        marriage.setDate(date);
      }

      if (location != null) {
        marriage.setLocation(location);
      }

      out.println("Updated: " + marriage.getDescription());

    } catch (Exception ex) {
      ex.printStackTrace(err);
    }

  }

}
