package edu.pdx.cs410G.mvc;

import java.util.Date;

/**
 * This class represents a check in a checkbook.
 */
public class Check {

  /** The number of the check */
  private int number;

  /** The date on which the check was written */
  private Date date;

  /** To whom the check was written */
  private String receiver;

  /** The amount of the check.  Negative means withdrawl. */
  private double amount;

  /** Did the check clear? */
  private boolean hasCleared;

  ////////////////////  Constructors  ////////////////////

  /**
   * Creates a new <code>Check</code> with a given number.  Initially,
   * a check has not cleared.
   */
  public Check(int number) {
    this.number = number;
    this.hasCleared = false;
  }

  //////////////////  Accessors and Mutators  /////////////////

  /**
   * Returns the number of this check
   */
  public int getNumber() {
    return this.number;
  }

  /**
   * Returns the date on which this check was written
   */
  public Date getDate() {
    return this.date;
  }

  /**
   * Sets the data on which this check was written
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Returns the party to which the check was issued
   */
  public String getReceiver() {
    return this.receiver;
  }

  /**
   * Sets the party to which this check was issued
   */
  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  /**
   * Returns the amount for which this check was written
   */
  public double getAmount() {
    return this.amount;
  }

  /**
   * Sets the amount for which this check was written
   */
  public void setAmount(double amount) {
    this.amount = amount;
  }

  /**
   * Returns whether or not this check has cleared
   */
  public boolean hasCleared() {
    return this.hasCleared;
  }

  /**
   * Sets whether or not this check has cleared
   */
  public void setHasCleared(boolean hasCleared) {
    this.hasCleared = hasCleared;
  }

}
