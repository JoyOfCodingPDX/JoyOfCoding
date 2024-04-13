package edu.pdx.cs.joy.lang;

/**
 * This class demonstrates the {@link Override} annotation.
 *
 * @author David Whitlock
 * @since Winter 2005
 */
public class OverrideExample extends OverrideSuperclass {

  /**
   * This method overrides a superclass's method.  If the superclass's
   * method is removed, this class will no longer compile because this
   * method has an {@link Override} annotation.
   */
  @Override public void methodToOverride() {

  }

  /**
   * This method used to do something interesting, but we don't need
   * it anymore.
   */
  @Deprecated public void deprecatedMethod() {

  }
}
