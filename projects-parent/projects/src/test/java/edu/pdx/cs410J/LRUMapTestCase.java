package edu.pdx.cs410J;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This is a set of JUnit unit tests that test the functionality of an
 * {@link AbstractLRUMap LRU Map}.
 */
public abstract class LRUMapTestCase {

  /**
   * A factory method that creates a new instance of the LRU Map to be tested.
   *
   * @param capacity The maximum number of mappings in the newly-created LRU
   *                 map
   * @return A new LRU Map with the given capacity
   * @throws IllegalArgumentException A map cannot be created
   */
  public abstract <K, V> AbstractLRUMap<K, V> createLRUMap(int capacity);

  /**
   * Tests creating a new LRU Map
   *
   * @see LRUMapTestCase#createLRUMap(int)
   */
  @Test
  public void testCreate() {
    createLRUMap(4);
  }

  /**
   * Tests the {@link AbstractLRUMap#getStudentNames()} methods
   */
  @Test
  public void testGetStudentNames() {
    AbstractLRUMap map =
      createLRUMap(4);
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
  @Test
  public void testMapSizeIsBounded() {
    int maxMappings = 4;
    AbstractLRUMap<Integer, String> map =
      createLRUMap(maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(i, String.valueOf(i));
      assertEquals(i + 1, map.size());
    }

    map.put(maxMappings + 1,
      String.valueOf(maxMappings + 1));

    assertEquals(maxMappings, map.size());
  }

  /**
   * Makes sure that entrySet() Iterator iterates
   *
   * @see AbstractMap#containsKey(Object)
   */
  @Test
  public void testLRUMappingIterates() {
    int maxMappings = 4;
    AbstractLRUMap<Integer, String> map =
      createLRUMap(maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(i, String.valueOf(i));
      assertEquals(i + 1, map.size());
    }

    try {
      // get the iterator for this map's mappings 
      Iterator iter = map.entrySet().iterator();

      // iterate through the mappings 
      for (int i = 0; i < maxMappings; i++) {
        iter.next();
      }

      // there should be no more mappings so this should              
      // throw a NoSuchElementException error                         
      iter.next();
      System.out.println("Bad iterator returned by entrySet()."
        + "\nFix before continuing unit tests.");
      System.exit(1);

    } catch (IndexOutOfBoundsException ex) {
      // pass 
    } catch (NoSuchElementException ex) {
      // pass 
    }
  }

  /**
   * Makes sure that the LRU mapping is removed
   *
   * @see AbstractLRUMap#put(Object, Object)
   */
  @Test
  public void testLRUMappingIsRemoved() {
    int maxMappings = 4;
    AbstractLRUMap<Integer, String> map =
      createLRUMap(maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(i, String.valueOf(i));
      assertEquals(i + 1, map.size());
    }

    assertTrue(map.containsKey(new Integer(0)));

    map.put(maxMappings + 1,
      String.valueOf(maxMappings + 1));

    assertTrue(!map.containsKey(new Integer(0)));
  }

  /**
   * Tests that getting an element from a map puts it at the end of
   * the LRU queue.
   *
   * @see AbstractLRUMap#get(Object)
   */
  @Test
  public void testGetFromMap() {
    int maxMappings = 4;
    AbstractLRUMap<Integer, String> map =
      createLRUMap(maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(i, String.valueOf(i));
      assertEquals(i + 1, map.size());
    }

    assertTrue(map.containsKey(new Integer(0)));
    assertNotNull(map.get(new Integer(0)));

    map.put(maxMappings + 1,
      String.valueOf(maxMappings + 1));

    assertTrue(!map.containsKey(new Integer(1)));
  }

  /**
   * Tests getting an non-existent entry
   *
   * @see AbstractLRUMap#get(Object)
   */
  @Test
  public void testGetNonExistent() {
    int maxMappings = 4;
    AbstractLRUMap map =
      createLRUMap(maxMappings);
    assertEquals(null, map.get("Test"));
  }

  /**
   * Tests that removing and element doesn't effect the LRU order
   *
   * @see AbstractLRUMap#remove(Object)
   */
  @Test
  public void testRemoveFromMap() {
    int maxMappings = 4;
    AbstractLRUMap<Integer, String> map =
      createLRUMap(maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(i, String.valueOf(i));
      assertEquals(i + 1, map.size());
    }

    assertTrue(map.containsKey(new Integer(0)));
    assertNotNull(map.remove(new Integer(0)));

    map.put(maxMappings + 1,
      String.valueOf(maxMappings + 1));
    assertTrue(map.containsKey(new Integer(1)));

    map.put(maxMappings + 2,
      String.valueOf(maxMappings + 2));

    assertTrue(!map.containsKey(new Integer(1)));
  }

  /**
   * Tests removing an non-existent entry
   *
   * @see AbstractLRUMap#remove(Object)
   */
  @Test
  public void testRemoveNonExistent() {
    int maxMappings = 4;
    AbstractLRUMap map =
      createLRUMap(maxMappings);
    assertEquals(null, map.remove("Test"));
  }

  /**
   * Tests clearing a map
   *
   * @see AbstractLRUMap#clear()
   */
  @Test
  public void testClear() {
    int maxMappings = 4;
    AbstractLRUMap<Integer, String> map =
      createLRUMap(maxMappings);
    assertEquals(0, map.size());

    for (int i = 0; i < maxMappings; i++) {
      map.put(i, String.valueOf(i));
      assertEquals(i + 1, map.size());
    }

    map.clear();
    assertEquals(0, map.size());
  }
    /**                                                                                       
     * An obscure and amusing edge case.  Parent's method signature throws no exception, so this
     * method will accept any exception.                                                      
     */                                                                                       
    public void testNegativeSize()                                                            
    {                                                                                         
        try                                                                                   
        {                                                                                     
            createLRUMap( -1 );
            fail( "Negative sizes should not be accepted" );                                  
        }                                                                                     
        catch( Exception e )  // we don't know what kind of exception the implementor will throw
        {                                                                                     
            // success                                                                        
        }                                                                                     
    }  

    /**
     * This will accept EITHER an exception being thrown, or the creation of map that never stores data.
     */
    public void testZeroSize()
    {
        AbstractLRUMap<Integer, String> zmap;
        try
        {
            zmap = createLRUMap(0);
        }
        catch (Exception e)  // we don't know what kind of exception the implementor will throw
        {
            // success
            return;
        }
        assertEquals( 0, zmap.size() );
        zmap.put( 3, "Three" );
        assertEquals(0, zmap.size());
        assertNull( zmap.get( 3 ) );
        assertEquals( 0, zmap.entrySet().size() );
        assertNull( zmap.remove( 3 ) );

    }

    /*
    I wrote this to make sure I matched the behavior of a regular map.
    public void testRegMapCompare() {
        HashMap<Integer, String> zmap;
        zmap = new HashMap<Integer, String>(0);
        assertEquals(0, zmap.size());
        assertNull(zmap.get(3));
        assertEquals(0, zmap.entrySet().size());
        assertNull(zmap.remove(3));
    }
    */   

}
