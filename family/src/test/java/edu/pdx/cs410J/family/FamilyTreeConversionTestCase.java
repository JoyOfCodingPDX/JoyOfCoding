package edu.pdx.cs410J.family;

import org.junit.Test;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * This is the abstract superclass that tests the conversion of a
 * {@link FamilyTree} from one form to another.  The test methods can
 * be shared by subclasses.  All the subclass has to do is override
 the {@link #getStringFor} and {@link #getFamilyTreeFor} methods.
 */
public abstract class FamilyTreeConversionTestCase
  extends FamilyTestCase {

  ////////  Helper methods

  /**
   * Converts a <code>FamilyTree</code> to a <code>String</code>
   */
  protected abstract String getStringFor(FamilyTree tree);

  /**
   * Converts a <code>String</code> to a <code>FamilyTree</code>
   */
  protected abstract FamilyTree getFamilyTreeFor(String string);

  ////////  Test methods

    @Test
  public void testConformingData() {
    Person father = new Person(1, Person.MALE);
    father.setDateOfBirth(new Date());
    father.setDateOfDeath(new Date());

    Person mother = new Person(2, Person.FEMALE);
    mother.setDateOfBirth(new Date());
    mother.setDateOfDeath(new Date());

    Person child = new Person(3, Person.FEMALE);
    child.setFather(father);
    child.setMother(mother);

    Marriage m = new Marriage(father, mother);
    m.setLocation("Here");
    m.setDate(new Date());

    father.addMarriage(m);
    mother.addMarriage(m);

    FamilyTree tree = new FamilyTree();
    tree.addPerson(father);
    tree.addPerson(mother);
    tree.addPerson(child);

    String string = getStringFor(tree);

    FamilyTree tree2 = null;

    try {
      tree2 = getFamilyTreeFor(string);

    } catch (FamilyTreeException ex) {
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw, true));
      fail(sw.toString());
    }

    assertEquals(tree, tree2);
  }

  public void _testFatherNotMale() {

  }

  public void _testMotherNotFemale() {

  }

  public void _testPersonNotMale() {
    // We expect the person to be male because he was previously
    // somebody's father
  }

  public void _testPersonNotFemale() {
    // We expect the person to be female because she was previously
    // somebody's mother
  }


}
