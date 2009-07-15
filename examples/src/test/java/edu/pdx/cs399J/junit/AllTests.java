package edu.pdx.cs399J.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This program runs all of the unit tests in the junit package
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({SectionTest.class, CourseTest.class})
public class AllTests {

}
