package edu.pdx.cs410J.net;

/**
 * A <code>Counter</code> is something that counts in its own thread.
 */
public class Counter extends Thread {

  private String name;

  /**
   * Creates a new <code>Counter</code> with a given name
   */
  public Counter(String name) {
    this.name = name;
  }

  /**
   * The code that performs the counting.
   */
  public void run() {
    // Wait for a random amount of time and then print a number
    for (int i = 1; i <= 6; i++) {
      try {
	long time = (long) (Math.random() * 1000);

	Thread.sleep(time);

      } catch (InterruptedException ex) {
	return;
      }

      System.out.println(this.name + ": " + i);
    }
  }

}
