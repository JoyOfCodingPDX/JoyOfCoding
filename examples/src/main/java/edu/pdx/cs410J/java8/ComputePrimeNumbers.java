package edu.pdx.cs410J.java8;

import java.util.stream.IntStream;

/**
 * Demonstrates the use of parallel streams by printing the prime numbers that
 * are less than a given integer.
 */
public class ComputePrimeNumbers {

  public static void main(String[] args) {
    int maximumValue = Integer.parseInt(args[0]);

    long serialDuration = timeOperation(
      () -> computePrimeNumbers(IntStream.rangeClosed(0, maximumValue)));
    long parallelDuration = timeOperation(
      () -> computePrimeNumbers(IntStream.rangeClosed(0, maximumValue).parallel()));

    System.out.println(String.format("Printed primes less than %d (serial)   in %d nanoseconds", maximumValue, serialDuration));
    System.out.println(String.format("Printed primes less than %d (parallel) in %d nanoseconds", maximumValue, parallelDuration));
  }

  private static void computePrimeNumbers(IntStream stream) {
    long count = stream.filter(ComputePrimeNumbers::isPrime)
      .count();
    System.out.println("Computed " + count + " primes");
  }

  private static long timeOperation(Runnable operation) {
    long start = System.nanoTime();

    operation.run();

    return System.nanoTime() - start;
  }

  /**
   * See http://en.wikipedia.org/wiki/Primality_test
   */
  private static boolean isPrime(int n) {
    if (n <= 3) {
      return n > 1;

    } else if (n % 2 == 0 || n % 3 == 0) {
      return false;

    } else {
      for (int i = 5; i * i <= n; i += 6) {
        if (n % i == 0 || n % (i + 2) == 0) {
          return false;
        }
      }
      return true;
    }
  }
}
