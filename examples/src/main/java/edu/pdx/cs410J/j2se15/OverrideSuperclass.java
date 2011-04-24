package edu.pdx.cs410J.j2se15;

/**
 * This class contains one method that is overridden in a {@linkplain
 * OverrideExample subclass}.  Because the subclass's method is
 * annotated with an {@link Override} annotation, it will no longer
 * compile if the method is removed from this class, the superclass.
 *
 * @author David Whitlock
 * @since Winter 2005
 */
public class OverrideSuperclass {

  /**
   * If this method is removed from this class, its subclass {@link
   * OverrideExample} will not complile.
   */
  public void methodToOverride() {

  }

}
