package edu.pdx.cs410J.net;

/**
 * A <code>BackAccount</code> maintains a integer balance.  It takes
 * time to access accounts.
 */
public class BankAccount {
  private int balance;

  public int getBalance() {
    try {
      long time = (long) (Math.random() * 1000);
      Thread.sleep(time);
    } catch (InterruptedException ex) {
      
    }
    return this.balance;
  }

  public void setBalance(int balance) {
    try {
      long time = (long) (Math.random() * 1000);
      Thread.sleep(time);
    } catch (InterruptedException ex) { 

    }
    this.balance = balance;
  } 
}
