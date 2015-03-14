package edu.pdx.cs410J.java8;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class InternalIteration {

  public void evolutionOfIteration(String[] arrayOfStrings, List<String> listOfStrings) {
    for (int i = 0; i < arrayOfStrings.length; i++) {
      String string = arrayOfStrings[i];
      System.out.println(string);
    }

    Iterator<String> iter = listOfStrings.iterator();
    while(iter.hasNext()) {
      String string = iter.next();
      System.out.println(string);
    }

    for(String string : listOfStrings) {
      System.out.println(string);
    }

    listOfStrings.forEach(new Consumer<String>() {
      @Override
      public void accept(String string) {
        System.out.print(string);
      }
    });

    listOfStrings.forEach(System.out::println);
  }
}
