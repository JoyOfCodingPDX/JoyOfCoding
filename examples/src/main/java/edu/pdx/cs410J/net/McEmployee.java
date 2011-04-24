package edu.pdx.cs410J.net;

/**
 * This class represents an employee of a <code>McDonalds</code> who
 * makes BigMacs(tm).
 */
public class McEmployee implements Runnable {

  private String name;
  private McDonalds mcDonalds;

  /**
   * Creates a new <code>McEmployee</code>
   */
  public McEmployee(int id, McDonalds mcDonalds) {
    this.name = "Employee " + id;
    this.mcDonalds = mcDonalds;
  }

  /**
   * Keep making BigMacs
   */
  public void run() {
    System.out.println(this.name + " arrives at work");

    while (this.mcDonalds.moreBigMacs()) {
      System.out.println(this.name + " starts a BigMac");

      // It takes time to cook a BigMac
      long wait  = (long) (Math.random() * 10000);
      try {
	Thread.sleep(wait);

      } catch (InterruptedException ex) {
	return;
      }

      System.out.println(this.name + " finishes a BigMac");

      synchronized(this.mcDonalds) {
	this.mcDonalds.notify();
      }
    }
  }

}
