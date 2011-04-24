package edu.pdx.cs410J.net;

/**
 * This class demonstrates concurrency in Java by having Big Bird and
 * Mr. Snuffleupagus race to see who can count to 10 first.
 */
public class CountingRace {

  /**
   * Create a Big Bird and Snuffy and have them race.
   */
  public static void main(String[] args) {
    Counter bigBird = new Counter("BigBird");
    Counter snuffy = new Counter("Snuffy");

    bigBird.start();
    snuffy.start();
  }
}
