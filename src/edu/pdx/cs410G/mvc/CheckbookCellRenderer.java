package edu.pdx.cs410G.mvc;

import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;

/**
 * This {@link javax.swing.table.TableCellRender} is responsible for
 * drawing a given element in a <code>JTable</code>.  Specifically, it
 * will draw the balance of a checkbook in red if it is less than
 * zero.
 *
 * @author David Whitlock
 * @version $Revision: 1.2 $
 * @since Summer 2002
 */
public class CheckbookCellRenderer extends DefaultTableCellRenderer {

  /**
   * This method is invoked when a cell in a table is to be redrawn.
   * If we are redrawing the "Balance" column, then check to see if
   * the balance is negative.  If so, draw it in red.  Otherwise, draw
   * it in black.
   */
  public Component getTableCellRendererComponent(JTable table,
						 Object value,
						 boolean isSelected,
						 boolean hasFocus,
						 int row,
						 int column) {
    if (column == CheckbookTableModel.BALANCE_COLUMN) {
      if (value != null && value instanceof Number) {
	Color color;
	double balance = ((Double) value).doubleValue();
	if (balance < 0) {
	  color = Color.RED;
	} else {
	  color = Color.BLACK;
	}
	this.setForeground(color);
	this.setText(String.valueOf(balance));
	return this;
      }
    }

    // We don't care
    return super.getTableCellRendererComponent(table, value, isSelected,
					       hasFocus, row, column);
  }

}
