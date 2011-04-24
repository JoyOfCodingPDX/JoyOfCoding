package edu.pdx.cs410J.net;

import java.util.Random;

/**
 * This thread will work until and then wait until it is interrupted.
 */
public class WorkingThread extends Thread {

  public WorkingThread(ThreadGroup group, String name) {
    super(group, name);
  }

  public void run() {
    Random random = new Random();

    while (true) {
      // Have I been interrupted?
      if (this.isInterrupted()) {
        System.out.println(this + " is done");
        return;
      }

      // Do some work
      int work = Math.abs(random.nextInt(100000));
      System.out.println(this + " working for " + work);
      for (int l = 0; l < work; l++);

      // Sleep
      try {
        int sleep = random.nextInt(2000);
        System.out.println(this + " sleeping for " + sleep + " ms");
        Thread.sleep(sleep);

      } catch (InterruptedException ex) {
        System.out.println(this + " interrupted while sleeping");
        return;
      }
    }
  }

}
