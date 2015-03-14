package edu.pdx.cs410J.java8;

public class DefaultMethods {

  public interface InterfaceWithDefaultMethod {
    default void defaultMethod() {

    }
  }

  public interface AnotherInterfaceWithDefaultMethod {
    default void defaultMethod() {

    }
  }

  class WhatHappensWithTwoInterfacesWithSameDefaultMethod implements InterfaceWithDefaultMethod, AnotherInterfaceWithDefaultMethod {
    /**
     * If you don't override this method, this class won't compile because the
     * compiler cannot determine which <code>defaultMethod</code> implementation
     * should be associated with this class.
     */
    @Override
    public void defaultMethod() {

    }
  }
}
