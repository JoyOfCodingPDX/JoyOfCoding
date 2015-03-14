package edu.pdx.cs410J.java8;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamOperations {

  public void printElementsInStream() {
    Stream<String> stream = Arrays.asList("A", "B", "C").stream();
    stream.forEach(new Consumer<String>() {
      @Override
      public void accept(String s) {
        System.out.println(s);
      }
    });

    stream.forEach(System.out::println);
  }

  public void sumIntsUsingStream() {
    int[] ints = {1, 2, 3};
    IntStream stream = IntStream.of(ints);
    long count = stream.count();

    stream = IntStream.of(ints);
    long sum = stream.sum();

    stream = IntStream.of(ints);
    IntSummaryStatistics stats = stream.summaryStatistics();
  }

  public void findStringsWithMoreThanOneCharacter() {
    Stream<String> strings = Stream.of("A", "BB", "C", "");
    Stream<String> moreThanTwoChars = strings.filter(new Predicate<String>() {
      @Override
      public boolean test(String s) {
        return s.length() > 1;
      }
    });

    moreThanTwoChars = strings.filter(s -> s.length() > 1);
  }

  public void parseListOfStrings() {
    Stream<String> strings = Stream.of("1", "2", "3");
    Stream<Integer> ints = strings.map(new Function<String, Integer>() {
      @Override
      public Integer apply(String s) {
        return Integer.parseInt(s);
      }
    });

    IntStream stream = strings.mapToInt(Integer::parseInt);
  }

  public String getLongestString(Stream<String> strings) {
    Optional<String> longest = strings.max((s1, s2) -> s1.length() > s2.length() ? 1 : 0);
    return longest.orElse("");
  }
}
