package edu.pdx.cs410J.examples;

/**
 * A <code>McCustomer</code> arrives a <code>McDonalds</code> and
 * waits for a BigMac.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/examples/McCustomer.java">
 * View Source</A></EM></P>
 */
public class McCustomer implements Runnable {
  private String name;
  private McDonalds mcDonalds;

  /**
   * Creates a new <code>McCustomer</code>
   */
  public McCustomer(int id, McDonalds mcDonalds) {
    this.name = "Customer " + id;
    this.mcDonalds = mcDonalds;
  }

  /**
   * Go to a <code>McDonalds</code> and wait for a BigMac
   */
  public void run() {
    System.out.println(this.name + " wants a BigMac");

    try {
      synchronized(this.mcDonalds) {
	this.mcDonalds.wait();
      }

    } catch (InterruptedException ex) {
      return;
    }

    System.out.println(this.name + " got a BigMac");
  }

}
