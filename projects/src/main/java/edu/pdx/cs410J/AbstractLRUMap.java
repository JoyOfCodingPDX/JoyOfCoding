package edu.pdx.cs410J;

import java.util.*;

/**
 * This is the abstract superclass of an "LRU Map".  An LRU Map is a
 * Java {@link Map} that has a bounded number of mappings.  If a new
 * mapping is added to an LRU Map that already has the maximum number
 * of mappings, then the least recently used mapping is removed.  An
 * element is used when it is added to or gotten from the Map.
 */
public abstract class AbstractLRUMap<K, V> extends AbstractMap<K, V> {

  /** The maximum number of mappings that this map can hold */
  protected int capacity;

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new LRU Map that will hold a given number of mappings
   *
   * @param capacity The maximum number of mappings in this map
   * @throws IllegalArgumentException
   *         <code>capacity</code> is negative
   */
  protected AbstractLRUMap(int capacity) {
    if (capacity < 0) {
      String s = "Max mappings must be greater than 0";
      throw new IllegalArgumentException(s);
    }

    this.capacity = capacity;
  }

  /**
   * Returns the names of the students who implemented this LRU Map.
   */
  public abstract List<String> getStudentNames();

  /**
   * When a mapping is made in an LRU that already contains the
   * maximum number of mappings, the Least Recently Used element is
   * removed.
   */
  public abstract V put(K key, V value);

  /**
   * Getting an element from a map marks the mapping as "used".  Note
   * that the type of <code>key</code> must be <code>Object</code>
   * so that, after type erasure, it will be compatible with the
   * pre-generic API.
   */
  public abstract V get(Object key);

	/**
	 * Removes the given key from this map.  Note that the type of
	 * <code>key</code> must be <code>Object</code> so that, after type
	 * erasure, it will be compatible with the pre-generic API.
	 */
  public abstract V remove(Object key);

  public void putAll(Map<? extends K, ? extends V> map) {
    for (K key : map.keySet()) {
      V value = map.get(key);
      this.put(key, value);
    }

    // I couldn't figure out how to use the entrySet().  Nasty.
//    Iterator<Map.Entry<? extends K, ? extends V>> iter = map.entrySet().iterator();
//     while (iter.hasNext()) {
//       Map.Entry<? extends K, ? extends V> entry = iter.next();
//       this.put(entry.getKey(), entry.getValue());
//     }
  }

  public abstract void clear();

}
