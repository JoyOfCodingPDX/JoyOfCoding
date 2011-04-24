package edu.pdx.cs410J.net;

/**
 * This program starts up a bunch of {@link WorkingThread}s and also
 * starts an <code>InterruptingThread</code> that will interrupt each
 * <code>WorkingThread</code> after a given number of seconds.
 */
public class InterruptingThread extends Thread {
  /** The group of threads to interrupt */
  private ThreadGroup group;

  /** The number of milliseconds to wait before interrupting */
  private int sleep;

  public InterruptingThread(String name) {
    super(name);
  }

  public void run() {
    System.out.println(this + " sleeping for " + this.sleep + " ms");
    try {
      Thread.sleep(this.sleep);

    } catch (InterruptedException ex) {
      System.err.println("WHY?");
      System.exit(1);
    }

    System.out.println(this + " interrupting workers");
    this.group.interrupt();
  }

  public static void main(String[] args) {
    int sleep = Integer.parseInt(args[0]) * 1000;

    ThreadGroup group = new ThreadGroup("Worker threads");
    for (int i = 0; i < 5; i++) {
      Thread thread = new WorkingThread(group, "Worker " + i);
      thread.start();
    }

    InterruptingThread interrupting = 
      new InterruptingThread("interrupter");
    interrupting.group = group;
    interrupting.sleep = sleep;
    interrupting.start();
  }

}
