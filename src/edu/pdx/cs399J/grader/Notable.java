package edu.pdx.cs399J.grader;

import java.util.*;

/**
 * Classes that implement this interface have notes associated with
 * them.
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
}
