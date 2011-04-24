package edu.pdx.cs410J.j2se15;

/**
 * Uses the generic {@link Tuple} class to return a host/port
 * combination from a single method.
 *
 * @author David Whitlock
 * @since Summer 2005
 */
public class TupleExample {

  /**
   * Returns the host and port on which the server runs
   */
  public static Tuple<String, Integer> getHostAndPort() {
    return new Tuple<String, Integer>("pippin", 12345);
  }

  /**
   * Main program that uses a <code>Tuple</code>
   */
  public static void main(String[] args) {
    Tuple<String, Integer> tuple;

    tuple = getHostAndPort();

    String host = tuple.getFirst();
    int port = tuple.getSecond();

    System.out.println("Running on " + host + ":" + port);
  }

}
