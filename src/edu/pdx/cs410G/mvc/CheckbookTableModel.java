package edu.pdx.cs410G.mvc;

import java.util.*;
import javax.swing.table.*;

/**
 * This {@link TabelModel} represents the {@link Check}s in a checkbook.
 */
public class CheckbookTableModel extends AbstractTableModel
  implements CheckbookConstants {

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
      check.setDescription((String) value);
      break;

    case AMOUNT_COLUMN:
      check.setAmount(((Double) value).doubleValue());
      // Update the table accordingly
      this.fireTableDataChanged();
      break;

    case TRANSACTION_COLUMN: 
      if (value.equals(WITHDRAWL)) {
	check.setAmount(-1.0 * Math.abs(check.getAmount()));
	this.fireTableDataChanged();

      } else if (value.equals(DEPOSIT)) {
	check.setAmount(Math.abs(check.getAmount()));
	this.fireTableDataChanged();
      }
      break;
      
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

  public String getColumnName(int columnIndex) {
    return columnNames[columnIndex];
  }

  public Object getValueAt(int row, int column) {
    Check check = (Check) this.checks.get(row);
    switch (column) {
    case NUMBER_COLUMN: 
      return new Integer(check.getNumber());

    case DATE_COLUMN:
      return check.getDate();

    case DESCRIPTION_COLUMN:
      return check.getDescription();

    case AMOUNT_COLUMN:
      return new Double(Math.abs(check.getAmount()));

    case TRANSACTION_COLUMN: {
      if (check.getAmount() < 0) {
	return WITHDRAWL;

      } else if (check.getAmount() > 0) {
	return DEPOSIT;

      } else {
	return "";
      }
    }
    case BALANCE_COLUMN:
      return new Double(computeBalance(row));

    case CLEARED_COLUMN:
      return new Boolean(check.hasCleared());

    default: {
      String s = "Don't know how to set column: " + column;
      throw new IllegalArgumentException(s);
      }
    }
  }

  public boolean isCellEditable(int row, int column) {
    // Neither the "Number" nor "Balance" column is editable
    if (column == NUMBER_COLUMN || column == BALANCE_COLUMN) {
      return false;

    } else {
      return true;
    }
  }

  /**
   * Computes the balance of the checkbook up to and including the
   * check in the given row.
   */
  private double computeBalance(int row) {
    double amount = ((Check) this.checks.get(row)).getAmount();
    if (row == 0) {
      return amount;

    } else {
      return amount + computeBalance(row - 1);
    }
  }

}
