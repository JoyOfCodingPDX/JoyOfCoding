package edu.pdx.cs410G.mvc;

import java.util.Date;

/**
 * This interface provides a handful of constants that are used by the
 * Checkbook example classes.
 */
public interface CheckbookConstants {
  /** The names of the table columns */
  static final String[] columnNames = 
  { "Number", "Date", "Description", "Amount", "Transaction",
    "Balance", "Cleared" };

  /** The types of the table columns */
  static final Class[] columnTypes =
  { Integer.class, Date.class, String.class, Double.class,
    String.class, Double.class, Boolean.class };

  static final int NUMBER_COLUMN = 0;
  static final int DATE_COLUMN = 1;
  static final int DESCRIPTION_COLUMN = 2;
  static final int AMOUNT_COLUMN = 3;
  static final int TRANSACTION_COLUMN = 4;
  static final int BALANCE_COLUMN = 5;
  static final int CLEARED_COLUMN = 6;

  static final String DEPOSIT = "Deposit";
  static final String WITHDRAWL = "Withdrawl";

}
