package edu.pdx.cs410J.grader;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract superclass of several <code>notable</code> classes.  It 
 * is the result of refactoring some code.
 * 
 * @author David Whitlock
 * @since Summer 2007
 */
public abstract class NotableImpl implements Notable {

	private List<String> notes = new ArrayList<String>();
	private boolean dirty;

	/**
	   * Returns notes about this <code>Assignment</code>
	   */
	public List<String> getNotes() {
	    return this.notes;
	  }

	/**
	   * Adds a note about this <code>Assignment</code>
	   */
	public void addNote(String note) {
	    this.setDirty(true);
	    this.notes.add(note);
	  }

	public void removeNote(String note) {
	    this.setDirty(true);
	    this.notes.remove(note);
	  }

	/**
	   * Sets the dirtiness of this <code>Assignment</code>
	   */
	public void setDirty(boolean dirty) {
	    this.dirty = dirty;
	  }

	/**
	   * Returns <code>true</code> if this <code>Assignment</code> has been
	   * modified.
	   */
	public boolean isDirty() {
	    return this.dirty;
	  }

	/**
	   * Marks this <code>Assignment</code> as being clean
	   */
	public void makeClean() {
	    this.setDirty(false);
	  }


}
