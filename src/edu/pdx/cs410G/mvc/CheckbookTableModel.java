package edu.pdx.cs410G.mvc;

import java.util.*;
import javax.swing.table.*;

/**
 * This {@link TabelModel} represents the {@link Check}s in a checkbook.
 */
public class CheckbookTableModel extends AbstractTableModel {

  /** The names of the table columns */
  private static final String[] columnNames = 
  { "Number", "Date", "Description", "Amount", "Transaction",
    "Cleared" };

  /** The types of the table columns */
  private static final Class[] columnTypes =
  { Integer.class, Date.class, String.class, Double.class,
    String.class, Boolean.class };

  private static final int NUMBER_COLUMN = 0;
  private static final int DATE_COLUMN = 1;
  private static final int DESCRIPTION_COLUMN = 2;
  private static final int AMOUNT_COLUMN = 3;
  private static final int TRANSACTION_COLUMN = 4;
  private static final int CLEARED_COLUMN = 5;

  ////////////////////  Instance Fields  ////////////////////

  /** The checks in the checkbook */
  private List checks;

  /** The next check number */
  private int nextNumber = 0;

  ////////////////////  Constructors  ////////////////////

  /**
   * Creates a new <code>CheckbookTableModel</code> with no checks in
   * the checkbook
   */
  public CheckbookTableModel() {
    this.checks = new ArrayList();
  }

  ////////////////////  Checkbook Methods  ////////////////////

  /**
   * Creates a new <code>Check</code> and adds it to this checkbook
   * model
   */
  public void createCheck() {
    this.checks.add(new Check(nextNumber++));
    int row = checks.size() - 1;
    this.fireTableRowsInserted(row, row);
  }

  ////////////////////  TabelModel Methods  ////////////////////

  public int findColumn(String columnName) {
    for (int i = 0; i < columnNames.length; i++) {
      if (columnName.equals(columnNames[i])) {
	return i;
      }
    }

    return -1;
  }

  public Class getColumnClass(int column) {
    return columnTypes[column];
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    // All columns except "number" are editable
    if (columnIndex == 0) {
      return false;

    } else {
      return true;
    }
  }

  public void setValueAt(Object value, int row, int column) {
    Check check = (Check) this.checks.get(row);
    switch (column) {
    case NUMBER_COLUMN: {
      String s = "Shouldn't be able to set number?";
      throw new IllegalStateException(s);
    }

    case DATE_COLUMN:
      check.setDate((Date) value);
      break;

    case DESCRIPTION_COLUMN:
      check.setReceiver((String) value);
      break;

    case AMOUNT_COLUMN:
      check.setAmount(((Double) value).doubleValue());
      break;

    case TRANSACTION_COLUMN: {
      String s = "Not implemented yet!";
      throw new UnsupportedOperationException(s);
    }
    case CLEARED_COLUMN:
      check.setHasCleared(((Boolean) value).booleanValue());
      break;

    default: {
      String s = "Don't know how to set column: " + column;
      throw new IllegalArgumentException(s);
      }
    }
  }

  public int getRowCount() {
    return this.checks.size();
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public Object getValueAt(int row, int column) {
    Check check = (Check) this.checks.get(row);
    switch (column) {
    case NUMBER_COLUMN: 
      return new Integer(check.getNumber());

    case DATE_COLUMN:
      return check.getDate();

    case DESCRIPTION_COLUMN:
      return check.getReceiver();

    case AMOUNT_COLUMN:
      return new Double(Math.abs(check.getAmount()));

    case TRANSACTION_COLUMN: {
      String s = "Not implemented yet!";
      return s;
    }
    case CLEARED_COLUMN:
      return new Boolean(check.hasCleared());

    default: {
      String s = "Don't know how to set column: " + column;
      throw new IllegalArgumentException(s);
      }
    }
  }

}
