package edu.pdx.cs410J.j2se15;

/**
 * Demonstrates adding behavior to enumerated types.
 *
 * @author David Whitlock
 * @version $Revision: 1.2 $
 * @since Summer 2004
 */
public class NumericOperators {

  private enum Operation {
    PLUS {
      double eval(double x, double y) {
        return x + y;
      }

      char getSymbol() {
        return '+';
      }
    },

    MINUS {
      double eval(double x, double y) {
        return x - y;
      }

      char getSymbol() {
        return '-';
      }
    },

    TIMES {
      double eval(double x, double y) {
        return x * y;
      }

      char getSymbol() {
        return '*';
      }
    },

    DIVIDE {
      double eval(double x, double y) {
        return x / y;
      }

      char getSymbol() {
        return '/';
      }
    };

    /** Evaluates this operation for the given operands */
    abstract double eval(double x, double y);

    /** Returns the symbol that represent this operation */
    abstract char getSymbol();
  }

  /**
   * Evaluates several expressions using the {@link Operation}
   * enumerated type.
   */
  public static void main(String[] args) {
    Operation[] ops = { Operation.PLUS, Operation.MINUS,
                        Operation.TIMES, Operation.DIVIDE };
    for (Operation op : ops) {
      System.out.println("5 " + op.getSymbol() + " 2 = " +
                         op.eval(5, 2));
    }
  }

}
