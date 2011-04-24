package edu.pdx.cs410J.family;

import java.io.*;
import java.rmi.*;
import java.text.*;
import java.util.*;

/**
 * This program is an RMI client that queries a remote family tree
 */
public class GetLiving {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java GetLiving familyName host port");
    err.println("  -date Date");
    err.println("");
    err.println("This program queries a remote family tree to " +
                "determine which people are alive.");
    err.println("");
    err.println("Dates should be in the form MM/DD/YYYY");
    err.println("");
    System.exit(1);
  }

  public static void main(String[] args) {
    String familyName = null;
    String host = null;
    int port = -1;
    Date date = null;

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

      if (date != null) {
        Collection living = tree.getLiving(date);
        out.println("\n" + living.size() + " people were alive on " +
                    df.format(date) + "\n");
        Iterator iter = living.iterator();
        while (iter.hasNext()) {
          RemotePerson person = (RemotePerson) iter.next();
          out.println(person.getDescription());
          out.println("");
        }

      } else {
        Collection living = tree.getLiving();
        out.println("\n" + living.size() + " people are alive\n");
        Iterator iter = living.iterator();
        while (iter.hasNext()) {
          RemotePerson person = (RemotePerson) iter.next();
          out.println(person.getDescription());
          out.println("");
        }
      }

    } catch (Exception ex) {
      ex.printStackTrace(err);
    }

  }

}
