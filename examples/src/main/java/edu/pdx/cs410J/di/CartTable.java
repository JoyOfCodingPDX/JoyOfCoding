package edu.pdx.cs410J.di;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * A swing table that displays the contents of a {@link CartTableModel shopping cart}
 */
public class CartTable extends JTable
{
    public CartTable( final CartTableModel model )
    {
        super(model);
        TableColumnModel columns = this.getColumnModel();

        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = columns.getColumn( i );
            column.setCellRenderer( new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
                {
                    Component cell = super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
                    if (cell instanceof JLabel && row == model.getRowCount() - 1) {
                        JLabel label = (JLabel) cell;
                        Font font = label.getFont();
                        label.setFont( font.deriveFont( Font.BOLD ) );
                        return label;
                    }
                    return cell;
                }
            });
        }
    }
}
