package edu.pdx.cs399J.grader;

import java.util.*;

/**
 * Classes that implement this interface have notes associated with
 * them.
 *
 * @author David Whitlock
 * @version $Revision: 1.3 $
 * @since Fall 2000
 */
public interface Notable {

  /**
   * Returns notes about this object.
   */
  public List getNotes();

  /**
   * Adds a note about this object.
   */
  public void addNote(String note);

  /**
   * Removes a note about this object
   *
   * @since Summer 2004
   */
  public void removeNote(String note);

}
