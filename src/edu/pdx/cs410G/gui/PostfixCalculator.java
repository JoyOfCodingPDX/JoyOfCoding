package edu.pdx.cs410J.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * This program is a simple GUI for evaluating arithmetic expressions
 * in postfix notation (e.g. <code>5 6 + 7 -</code> evaluates to
 * <code>4</code>).
 */
public class PostfixCalculator {

  /** The operand stack */
  private LinkedList stack = new LinkedList();

  /**
   * Evaluate an arithmetic expression in postfix notation and return
   * the result.
   */
  private double evaluate(String expr) {
    StringTokenizer st = new StringTokenizer(expr, " ");
    while (st.hasMoreTokens()) {
      String s = st.nextToken();
      
      if (s.equals("+")) {
        // Push the sum of top two stack elements on the stack
        double d1 = ((Double) stack.removeFirst()).doubleValue();
        double d2 = ((Double) stack.removeFirst()).doubleValue();
        stack.addFirst(new Double(d2 + d1));

      } else if (s.equals("-")) {
        // Push the difference of top two stack elements on the stack
        double d1 = ((Double) stack.removeFirst()).doubleValue();
        double d2 = ((Double) stack.removeFirst()).doubleValue();
        stack.addFirst(new Double(d2 - d1));

      } else if (s.equals("*")) {
        // Push the difference of top two stack elements on the stack
        double d1 = ((Double) stack.removeFirst()).doubleValue();
        double d2 = ((Double) stack.removeFirst()).doubleValue();
        stack.addFirst(new Double(d2 * d1));

      } else if (s.equals("-")) {
        // Push the difference of top two stack elements on the stack
        double d1 = ((Double) stack.removeFirst()).doubleValue();
        double d2 = ((Double) stack.removeFirst()).doubleValue();
        stack.addFirst(new Double(d2 / d1));

      } else {
        // It must be a number
        stack.addFirst(new Double(s));
      }
    }

    // return the value on top of the stack
    return ((Double) stack.removeFirst()).doubleValue();
  }

  public static void main(String[] args) {
    if (args.length > 0) {
      System.out.println((new PostfixCalculator()).evaluate(args[0]));
      return;
    }

    Panel p = new Panel();
    p.setLayout(new BorderLayout());
    final Button b = new Button("Calculate");
    p.add(b, BorderLayout.WEST);
    final TextField expr = new TextField(15);
    p.add(expr, BorderLayout.CENTER);

    final PostfixCalculator calc = new PostfixCalculator();

    b.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          double result = calc.evaluate(expr.getText());
          expr.setText(Double.toString(result));
        }
      });

    Frame f = new Frame("Postfix Calculator");
    f.setLayout(new BorderLayout());
    f.add(p, BorderLayout.CENTER);

    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });  
    
    f.pack();
    f.setVisible(true);
  }
}
