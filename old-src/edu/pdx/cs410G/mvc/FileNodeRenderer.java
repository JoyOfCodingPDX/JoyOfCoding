package edu.pdx.cs410G.mvc;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * This class draws a node in a {@link JTree}.  More specifically, if
 * the node is a {@link FileNode} and the node represents a hidden
 * file, the node will be {@link Color#RED}.
 */
public class FileNodeRenderer extends DefaultTreeCellRenderer {

  public Component getTreeCellRendererComponent(JTree tree,
						Object value,
						boolean selected,
						boolean expanded,
						boolean isLeaf,
						int row,
						boolean hasFocus) {
    JLabel c = (JLabel)
      super.getTreeCellRendererComponent(tree, value, selected,
					 expanded, isLeaf, row,
					 hasFocus);

    if (value instanceof FileNode) {
      FileNode node = (FileNode) value;
      File file = node.getFile();
      if (!file.isDirectory()) {
	c.setText(node.toString());
	if (file.isHidden()) {
	  c.setForeground(Color.RED);
	}
	return c;
      }
    } 

    return c;
  }

}
