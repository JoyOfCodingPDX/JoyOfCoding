package edu.pdx.cs410J.net;

import java.io.PrintStream;

/**
 * This class demonstrates how concurrent access to a bank account
 * needs to be synchronized in order to guarantee the balance is
 * always correct.
 */
public class ATM implements Runnable {
  protected static PrintStream out = System.out;

  protected String name;
  protected BankAccount account;
  protected int[] transactions;

  public ATM(String name, BankAccount account, int[] transactions) {
    this.name = name;
    this.account = account;
    this.transactions = transactions;
  }
  
  /**
   * Perform each transaction on the account
   */
  public void run() {
    for (int i = 0; i < transactions.length; i++) {
      // Get the balance
      int balance = account.getBalance();
      out.println(this.name + " got balance " + balance);

      // Perform the operation
      out.println(this.name + " perform " + transactions[i]);
      balance += transactions[i];

      // Set the balance
      out.println(this.name + " set balance to " + balance);
      account.setBalance(balance);
    }
  }

  /**
   * Create a couple of accounts and have them all perform the same
   * transactions, but in different orders.
   */
  public static void main(String[] args) {
    BankAccount account = new BankAccount();
    account.setBalance(1000);
    out.println("Initial balance: " + account.getBalance());

    // Make some ATMs
    int[] trans1 = {-200, 400, 100, -300};
    ATM atm1 = new ATM("ATM1", account, trans1);
    int[] trans2 = {400, 100, -300, -200};
    ATM atm2 = new ATM("ATM2", account, trans2);
    int[] trans3 = {-300, -200, 100, 400};
    ATM atm3 = new ATM("ATM3", account, trans3);

    // Make some threads and start them
    Thread t1 = new Thread(atm1);
    t1.start();
    Thread t2 = new Thread(atm2);
    t2.start();
    Thread t3 = new Thread(atm3);
    t3.start();

    // Wait for all threads to finish
    try {
      t1.join();
      t2.join();
      t3.join();

    } catch (InterruptedException ex) {
      return;
    }

    out.println("Final balance: " + account.getBalance());
  }

}
