package edu.pdx.cs399J;

import java.io.*;
import java.util.*;
import junit.framework.*;

/**
 * This is a set of JUnit unit tests that test the functionality of an
 * {@link AbstractLRUMap LRU Map}.
 */
public class LRUMapTest extends TestCase {

  /** The name of the LRU Map Class */
  private static String LRU_MAP_CLASS_NAME;

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new <code>LRUMapTest</code> with the given test method
   * name. 
   */
  public LRUMapTest(String name) {
    super(name);
  }

  ///////////////////////  Test Methods  ///////////////////////

  ////////  Creating a new map

  /**
   * Tests creating a LRU Map with a bogus class name
   *
   * @see AbstractLRUMap.createLRUMap(String, int)
   */
  public void testCreateNoSuchClass() {
    try {
      AbstractLRUMap.createLRUMap("BOGUS", 4);

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  /**
   * Tests creating an LRU Map that does not subclass {@link
   * AbstractLRUMap}. 
   *
   * @see AbstractLRUMap.createLRUMap(String, int)
   */
  public void testCreateNotAMap() {
    try {
      AbstractLRUMap.createLRUMap("java.lang.Object", 4);

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  /**
   * Tests creating an LRU Map from an interface class
   *
   * @see AbstractLRUMap.createLRUMap(String, int)
   */
  public void testCreateInterface() {
    try {
      AbstractLRUMap.createLRUMap("java.lang.Cloneable", 4);

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  /**
   * Tests creating an LRU Map from a class without the required
   * one-argument constructor.
   *
   * @see AbstractLRUMap.createLRUMap(String, int)
   */
  public void testCreateNoOneArgConstructor() {
    try {
      String className = NoOneArgConstructorLRUMap.class.getName();
      AbstractLRUMap.createLRUMap(className, 4);

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  /**
   * Tests creating a new LRU Map
   *
   * @see AbstractLRUMap.createLRUMap(String, int)
   */
  public void testCreate() {
    AbstractLRUMap.createLRUMap(LRU_MAP_CLASS_NAME, 4);
  }

  /**
   * Tests the {@link AbstractLRUMap#getStudentNames()} methods
   */
  public void testGetStudentNames() {
    AbstractLRUMap map = 
      AbstractLRUMap.createLRUMap(LRU_MAP_CLASS_NAME, 4);
    Iterator students = map.getStudentNames().iterator();
    System.out.println("\n\nImplemented by:");
    while (students.hasNext()) {
      System.out.println("  " + students.next());
    }
    System.out.println("\n");
  }

  /**
   * Tests that the size of the LRU map is bounded
   *
   * @see AbstractLRUMap#put(Object, Object)
   */
  public void testMapSizeIsBounded() {
    int maxMappings = 4;
    AbstractLRUMap map = 
      AbstractLRUMap.createLRUMap(LRU_MAP_CLASS_NAME, maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(new Integer(i), String.valueOf(i));
      assertEquals(i+1, map.size());
    }

    map.put(new Integer(maxMappings + 1),
            String.valueOf(maxMappings + 1));

    assertEquals(maxMappings, map.size());
  }

  /**
   * Makes sure that the LRU mapping is removed
   *
   * @see AbstractLRUMap#put(Object, Object)
   */
  public void testLRUMappingIsRemoved() {
    int maxMappings = 4;
    AbstractLRUMap map = 
      AbstractLRUMap.createLRUMap(LRU_MAP_CLASS_NAME, maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(new Integer(i), String.valueOf(i));
      assertEquals(i+1, map.size());
    }

    assertTrue(map.containsKey(new Integer(0)));

    map.put(new Integer(maxMappings + 1),
            String.valueOf(maxMappings + 1));

    assertTrue(!map.containsKey(new Integer(0)));
  }

  /**
   * Tests that getting an element from a map puts it at the end of
   * the LRU queue.
   *
   * @see AbstractLRUMap#get(Object)
   */
  public void testGetFromMap() {
    int maxMappings = 4;
    AbstractLRUMap map = 
      AbstractLRUMap.createLRUMap(LRU_MAP_CLASS_NAME, maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(new Integer(i), String.valueOf(i));
      assertEquals(i+1, map.size());
    }

    assertTrue(map.containsKey(new Integer(0)));
    assertNotNull(map.get(new Integer(0)));

    map.put(new Integer(maxMappings + 1),
            String.valueOf(maxMappings + 1));

    assertTrue(!map.containsKey(new Integer(1)));
  }

  /**
   * Tests getting an non-existent entry
   *
   * @see AbstractLRUMap#get(Object)
   */
  public void testGetNonExistent() {
    int maxMappings = 4;
    AbstractLRUMap map = 
      AbstractLRUMap.createLRUMap(LRU_MAP_CLASS_NAME, maxMappings);
    assertEquals(null, map.get("Test"));
  }

  /**
   * Tests that removing and element doesn't effect the LRU order
   *
   * @see AbstractLRUMap#remove(Object)
   */
  public void testRemoveFromMap() {
    int maxMappings = 4;
    AbstractLRUMap map = 
      AbstractLRUMap.createLRUMap(LRU_MAP_CLASS_NAME, maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(new Integer(i), String.valueOf(i));
      assertEquals(i+1, map.size());
    }

    assertTrue(map.containsKey(new Integer(0)));
    assertNotNull(map.remove(new Integer(0)));

    map.put(new Integer(maxMappings + 1),
            String.valueOf(maxMappings + 1));
    assertTrue(map.containsKey(new Integer(1)));

    map.put(new Integer(maxMappings + 2),
            String.valueOf(maxMappings + 2));

    assertTrue(!map.containsKey(new Integer(1)));
  }

  /**
   * Tests removing an non-existent entry
   *
   * @see AbstractLRUMap#remove(Object)
   */
  public void testRemoveNonExistent() {
    int maxMappings = 4;
    AbstractLRUMap map = 
      AbstractLRUMap.createLRUMap(LRU_MAP_CLASS_NAME, maxMappings);
    assertEquals(null, map.remove("Test"));
  }

  /**
   * Tests clearing a map
   *
   * @see AbstractLRUMap#clear()
   */
  public void testClear() {
    int maxMappings = 4;
    AbstractLRUMap map = 
      AbstractLRUMap.createLRUMap(LRU_MAP_CLASS_NAME, maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(new Integer(i), String.valueOf(i));
      assertEquals(i+1, map.size());
    }

    map.clear();
    assertEquals(0, map.size());
  }

  ///////////////////////  Inner Classes  //////////////////////

  /**
   * An LRU Map class that doesn't have a one-arg constructor
   */
  private class NoOneArgConstructorLRUMap extends AbstractLRUMap {
    public NoOneArgConstructorLRUMap() {
      super(4);
    }

    public void clear() {
      throw new IllegalStateException("Shouldn't be invoked!");
    }

    public Object put(Object key, Object value) {
      throw new IllegalStateException("Shouldn't be invoked!");
    }

    public Object get(Object key) {
      throw new IllegalStateException("Shouldn't be invoked!");
    }

    public Object remove(Object key) {
      throw new IllegalStateException("Shouldn't be invoked!");
    }

    public List getStudentNames() {
      throw new IllegalStateException("Shouldn't be invoked!");
    }

    public Set entrySet() {
      throw new IllegalStateException("Shouldn't be invoked!");
    }
  }

  ///////////////////////  Main Program  ///////////////////////

  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java LRUMapTest className (testName)*");
    err.println("");
    err.println("This program tests an LRUMap class with a given " +
                "name.  If you only want to run a subset of the " +
                "test, their names can be individually specified.");
    err.println("");
    System.exit(1);
  }
  
  /**
   * Main program that runs the tests
   */
  public static void main(String[] args) {
    String className = null;
    List testNames = new ArrayList();
    
    for (int i = 0; i < args.length; i++) {
      if (className == null) {
        className = args[i];

      } else {
        testNames.add(args[i]);
      }
    }

    if (className == null) {
      usage("Missing class name");
    }

    LRU_MAP_CLASS_NAME = className;

    // Create the TestSuite
    TestSuite suite;

    if (testNames.isEmpty()) {
      // Run all of the tests
      suite = new TestSuite(LRUMapTest.class);

    } else {
      suite = new TestSuite();
      
      Iterator iter = testNames.iterator();
      while (iter.hasNext()) {
        String testName = (String) iter.next();
        suite.addTest(new LRUMapTest(testName));
      }
    }

    // Run the tests
    junit.textui.TestRunner.run(suite);
  }

}
