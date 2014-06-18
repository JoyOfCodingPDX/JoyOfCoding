package edu.pdx.cs410J.family.web;

import edu.pdx.cs410J.family.FamilyTree;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Manages the <code>FamilyTree</code> associated with a user's session
 */
public class FamilyTreeManager {
  private static final String ATTRIBUTE = "FamilyTree";


  /**
   * Sets the <code>FamilyTree</code> associated with the current user's session
   */
  public static void setFamilyTree(FamilyTree tree, HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    session.setAttribute(ATTRIBUTE, tree);
  }

  /**
   * Returns the <code>FamilyTree</code> currently associated with the user's session
   */
  public static FamilyTree getFamilyTree(HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    return (FamilyTree) session.getAttribute(ATTRIBUTE);
  }
}
