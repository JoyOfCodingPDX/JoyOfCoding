package edu.pdx.cs410J.security;

/**
 * <P>This class demonstrates <code>SecurityManager</code>s and
 * <code>Permission</code>s by attempting to access the
 * <code>user.name</code> system property.</P>
 *
 * <P>This code should be run in three different modes:</P>
 * <OL>
 * <LI>With no security enabled</LI>
 * <LI>With the default (applet level) security manager:
 * <code>-Djava.security.manager</code></LI>
 * <LI>With the a policy file containing the following permissions:
 * <PRE>
 * grant {
 *   permission java.util.PropertyPermission "user.home", "read";
 * };
 * </PRE>
 * </LI>
 * </OL>
 */
public class PrintUser {
  public static void main(String[] args) {
    String userName = System.getProperty("user.name");
    System.out.println("User: " + userName);
  }
}
