package edu.pdx.cs410J.net;

/**
 * Synchronized methods ensure that the data in the balance is
 * accessed correctly.
 */
public class SynchronizedBankAccount extends BankAccount {
  private static int nextId = 1;
  int id = nextId++;

  public synchronized int getBalance() {
    return super.getBalance();
  }

  public synchronized void setBalance(int balance) {
    super.setBalance(balance);
  } 

  public synchronized void doTransaction(int trans) {
    // Will not attempt to re-obtain lock
    int balance = this.getBalance();
    balance += trans;
    this.setBalance(balance);
  }
}
