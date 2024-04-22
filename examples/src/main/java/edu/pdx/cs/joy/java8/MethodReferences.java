package edu.pdx.cs.joy.java8;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MethodReferences {

  public static void main(String[] args) {

    Runnable run = IllegalStateException::new;
    Callable<List> call = ArrayList::new;

  }
}
