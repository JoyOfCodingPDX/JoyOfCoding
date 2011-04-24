package edu.pdx.cs410J.net;

/**
 * Transfers money between two <code>BankAccount</code>s.
 * Has the potential to deadlock.
 */
public class Transfer implements Runnable {
  private BankAccount src;
  private BankAccount dest;
  private int amount;

  /**
   * Sets up a transfer between two accounts.
   */
  public Transfer(BankAccount src, 
                  BankAccount dest, int amount) {
    this.src = src;
    this.dest = dest;
    this.amount = amount;
  }

  /**
   * Performs the transfer.
   */
  public void run() {
    System.out.println("Transferring " + this.amount);

    // Have to obtain locks on both accounts
    synchronized(this.src) {
      int srcBalance = src.getBalance();

      synchronized(this.dest) {
        int destBalance = dest.getBalance();

        src.setBalance(srcBalance - this.amount);
        dest.setBalance(destBalance + this.amount);
      }
    }
  }

  /**
   * Creates and performs a <code>Transfer</code>
   */
  public static void main(String[] args) {
    BankAccount acc1 = new BankAccount();
    acc1.setBalance(1000);
    BankAccount acc2 = new BankAccount();
    acc2.setBalance(500);

    (new Thread(new Transfer(acc1, acc2, 300))).start();
    (new Thread(new Transfer(acc2, acc1, 100))).start();
  }
}
