package edu.pdx.cs410G.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.*;

/**
 * This program demonstrates how user threads cooperate with Swing's
 * event dispatcher thread.
 *
 * @see SwingUtilities#invokeLater
 */
public class MultipleThreads extends JPanel {

  /** The total number of counts to make */
  private static final int TOTAL = 10000;

  /** The number of concurrent threads */
  private static final int THREAD_COUNT = 4;

  /** The progress bars */
  private JProgressBar[] bars;

  /** The counter threads */
  private Thread[] threads;

  ////////////////////////  Constructors  ////////////////////////

  /**
   * The GUI consists of two {@link JProgressBar}s that are updated by
   * the {@link Counter} threads.  When you click on one of the
   * progress bars its thread will stop.
   */
  public MultipleThreads() {
    this.threads = new Thread[THREAD_COUNT];
    for (int i = 0; i < THREAD_COUNT; i++) {
      this.threads[i] = new Counter(i);
    }

    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.bars = new JProgressBar[THREAD_COUNT];
    for (int i = 0; i < THREAD_COUNT; i++) {
      JProgressBar bar = new JProgressBar(0, TOTAL);
      bar.setStringPainted(true);
      final int thread = i;
      bar.addMouseListener(new MouseAdapter() {
	  public void mouseClicked(MouseEvent e) {
	    MultipleThreads.this.threads[thread].interrupt();
	  }
	});

      this.add(bar);
      this.bars[i] = bar;
    }

  }

  //////////////////////  Instance Methods  //////////////////////

  /**
   * Starts up the counter threads
   */
  private void startCounterThreads() {
    for (int i = 0; i < THREAD_COUNT; i++) {
      this.threads[i].start();
    }
  }

  //////////////////////  Inner Classes  ///////////////////////

  /**
   * This thread counts to {@link TOTAL} in random increments.
   * Between each increment it rests.
   */
  class Counter extends Thread {

    /** The id of this counter */
    private int id;

    Counter(int id) {
      this.id = id;
    }

    public void run() {
      Random random = new Random();
      int count = 0;
      while (count < TOTAL) {
	int work = random.nextInt(TOTAL/20);
	for (int i = 0; i < work; i++) {
	  count++;
	}

	// If I've been interrupted, stop
	if (Thread.interrupted()) {
	  return;
	}

	// Update the progress bar
	final int value = count;
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
	      MultipleThreads.this.bars[id].setValue(value);
	    }
	  });

	// Catch my breath
	try {
	  long time = (long) (random.nextDouble() * 1000.0);
	  Thread.sleep(time);

	} catch (InterruptedException ex) {
	  // Interruped while I sleep, stop
	  return;
	}

      }
    }
  }

  ///////////////////////  Main Program  ///////////////////////

  public static void main(String[] args) {
    JFrame frame = new JFrame("Multiple Threads in Swing");
    MultipleThreads panel = new MultipleThreads();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);

    panel.startCounterThreads();
  }

}
