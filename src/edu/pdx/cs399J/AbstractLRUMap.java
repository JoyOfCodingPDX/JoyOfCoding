package edu.pdx.cs399J;

import java.lang.reflect.*;
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

  ///////////////////////  Static Methods  //////////////////////

  /**
   * A factory method that uses Java reflection to create an instance
   * of an <code>AbstractLRUMap</code> with the given name.  It will
   * invoke the one-argument constructor of the LRU map class with the
   * given maximum number of mappings.
   *
   * @param className
   *        The name of a class that implements
   *        <code>AbstractLRUMap</code>. 
   * @param capacity
   *        The maximum number of mappings in the newly-created LRU
   *        map 
   *
   * @throws IllegalArgumentException
   *         A map cannot be created
   */
  @SuppressWarnings("unchecked")
public static <K, V> AbstractLRUMap<K, V> createLRUMap(String className, 
                                            int capacity) {
    // First, load the class
    Class<AbstractLRUMap<K, V>> c;
    try {
      c = (Class<AbstractLRUMap<K, V>>) Class.forName(className);
      if (c.isInterface()) {
        String s = "Cannot create an LRU Map from an interface: " +
          className;
        throw new IllegalArgumentException(s);
      }

    } catch (ClassNotFoundException ex) {
      String s = "Could not load class \"" + className + "\"";
      throw new IllegalArgumentException(s);
    }

    // Make sure that the class is an LRU Map
    if (!AbstractLRUMap.class.isAssignableFrom(c)) {
      String s = "Class \"" + className + "\" is not an " + 
        "AbstractLRUMap";
      throw new IllegalArgumentException(s);
    }

    // Get the one-argument constructor
    Constructor<AbstractLRUMap<K, V>> init;
    try {
      init = c.getConstructor(new Class[] { int.class });
      init.setAccessible(true);

    } catch (NoSuchMethodException ex) {
      String s = "Class \"" + className + "\" doesn't have a " +
        "one-argument constructor";
      throw new IllegalArgumentException(s);
    }

    // Invoke the constructor
    try {
      Object[] args = new Object[] { new Integer(capacity) };
      return init.newInstance(args);

    } catch (InstantiationException ex) {
      String s = "While creating a \"" + className + "\": " + ex;
      throw new IllegalArgumentException(s);

    } catch (InvocationTargetException ex) {
      String s = "While creating a \"" + className + "\": " +
        ex.getTargetException();
      throw new IllegalArgumentException(s);

    } catch (IllegalAccessException ex) {
      String s = "Huh? Couldn't access constructor for \"" + className
        + "\"";
      throw new IllegalArgumentException(s);
    }
  }

  //////////////////////  Instance Methods  /////////////////////

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
    Iterator<? extends K> iter = map.keySet().iterator();
    while (iter.hasNext()) {
      K key = iter.next();
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
