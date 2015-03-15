package edu.pdx.cs410J.java8;

import edu.pdx.cs410J.rmi.Movie;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
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

  public Movie findMovieWithId(long id, Stream<Movie> movies) {
    Optional<Movie> movie = movies.filter(m -> m.getId() == id).findAny();
    return movie.orElseThrow(() -> new IllegalArgumentException("Can't find movie"));
  }

  public long countMoviesWithActor(long actorId, Stream<Movie> movies) {
    return movies.parallel()
      .filter(m -> m.getActors().contains(actorId))
      .count();
  }

  public List<Movie> getMoviesWithActorUnsafe(long actorId, Stream<Movie> allMovies) {
    List<Movie> movies = new ArrayList<>();
    allMovies.parallel()
      .filter(m -> m.getActors().contains(actorId))
      .forEach(m -> movies.add(m));
    return movies;
  }

  public List<Movie> getMoviesWithActorSafe(long actorId, Stream<Movie> allMovies) {
    return allMovies.parallel()
      .filter(m -> m.getActors().contains(actorId))
      .collect(Collectors.toList());
  }

  public int countAwardsForMoviesWithActor(long actorId, Stream<Movie> allMovies) {
    return allMovies.filter(m -> m.getActors().contains(actorId))
      .map(m -> m.getNumberOfAwards())
      .reduce(0, new BinaryOperator<Integer>() {
        @Override
        public Integer apply(Integer i1, Integer i2) {
          return i1 + i2;
        }
      });
  }

  public String concatenateStrings(Stream<String> stream) {
    return stream.reduce("", (s1, s2) -> s1 + s2);
  }

  public String concatenateStrings2(Stream<String> stream) {
    Supplier<StringBuilder> supplier = () -> new StringBuilder();
    BiConsumer<StringBuilder, StringBuilder> combiner = (sb1, sb2) -> sb1.append(sb2);
    BiConsumer<StringBuilder, String> accumulator = (sb, s) -> sb.append(s);
    StringBuilder sb = stream.collect(supplier, accumulator, combiner);
    return sb.toString();
  }

  public String concatenateStrings3(Stream<String> stream) {
    return stream.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
  }

  public String concatenateStrings4(Stream<String> stream) {
    return stream.collect(Collectors.joining());
  }

  public SortedSet<String> getSortedStrings(Stream<String> stream) {
    return stream.collect(Collectors.toCollection(TreeSet::new));
  }

  public double getAverageStringLength(Stream<String> stream) {
    return stream.collect(Collectors.averagingInt(s -> s.length()));
  }
}
