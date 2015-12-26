package edu.pdx.cs410J.j2se15;

/**
 * This class demonstrates <I>covariant returns</I> in JDK 1.5.  The
 * Java compiler allows an overriding method to change the method's
 * return type to be a subclass of the overriden method's return
 * type.  However, you have to be extra careful when using covariant
 * returns with legacy code.  If someone else overrides a method and
 * doesn't change the return type, their code will not compile.
 *
 * @author David Whitlock
 * @since Winter 2004
 */
public class CovariantReturns {

  /**
   * An animal that can be cloned.  Such pointed political commentary.
   */
  static abstract class Animal implements Cloneable {

    public abstract Object clone();

  }

  static class Human extends Animal {

    public Human clone() {
      return new Human();
    }
  }

  static class Student extends Human {

    public Student clone() {
      return new Student();
    }
  }

  /**
   * A main class that clones some animals.  Note that we don't need
   * to cast the result of the <code>clone</code> method.
   */
  public static void main(String[] args) {
    Human human = new Human();
    @SuppressWarnings("unused")
    Human human2 = human.clone();

    Student student = new Student();
    @SuppressWarnings("unused")
    Student student2 = student.clone();
  }

}
