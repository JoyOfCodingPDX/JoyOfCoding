package edu.pdx.cs410J.net;

/**
 * This class models a <code>McDonalds</code>.  There are a bunch of
 * <code>McCustomer</code>s who all want a BigMac(tm).  There are a
 * bunch of liberal arts majors, er, <code>McEmployee</code>s who cook
 * the BigMacs(tm).  Each <code>McCustomer</code> and
 * <code>McEmployee</code> runs in his or her own thread.
 */
public class McDonalds {
  private static java.io.PrintStream err = System.err;
  private int nBigMacs;

  /**
   * Creates a new <code>McDonalds</code> with a given number of
   * BigMacs to cook.
   */
  public McDonalds(int nBigMacs) {
    this.nBigMacs = nBigMacs;
  }

  /**
   * Returns <code>true</code> if there are more BigMacs to cook.
   */
  public synchronized boolean moreBigMacs() {
    if (this.nBigMacs <= 0) {
      return false;

    } else {
      this.nBigMacs--;
      return true;
    }
  }

  /**
   * Read the number of <code>McCustomer</code>s and the number of
   * <code>McEmployee</code>s from the command line.  Spin off threads
   * for each one and what minimum wage at work.
   */
  public static void main(String[] args) {
    int nCustomers = 0;
    int nEmployees = 0;

    try {
      nCustomers = Integer.parseInt(args[0]);
      nEmployees = Integer.parseInt(args[1]);

    } catch (NumberFormatException ex) {
      err.println("** NumberFormatException");
      System.exit(1);
    }

    // Each customer wants a BigMac(tm)
    McDonalds mcDonalds = new McDonalds(nCustomers);

    // The customers enter...
    for (int i = 0; i < nCustomers; i++) {
      McCustomer customer = new McCustomer(i, mcDonalds);
      (new Thread(customer)).start();
    }

    // The employees start cooking...
    for (int i = 0; i < nEmployees; i++) {
      McEmployee employee = new McEmployee(i, mcDonalds);
      (new Thread(employee)).start();
    }

    // Our work here is done.
  }
}
