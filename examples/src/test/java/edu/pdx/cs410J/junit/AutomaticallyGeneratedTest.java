package edu.pdx.cs410J.junit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


/**
 * The class demonstrates how JUnit 5's {@link DynamicTest} class can be used to generate test cases
 * dynamically.
 */
public class AutomaticallyGeneratedTest
{
    @BeforeAll
    public static void setUp() {
        System.out.println("BeforeClass");
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("AfterClass");
    }

    @TestFactory
    Stream<DynamicTest> generateDynamicTests() {
        return getTestNames().stream().map(name -> dynamicTest(name, () -> new GeneratedTest(name)));
    }

    public static List<String> getTestNames() {
        return Arrays.asList( "One", "Two", "Three" );
    }

    public static class GeneratedTest
    {
        private final String name;

        public GeneratedTest(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void run()
        {
            assertNotNull(getName());
            // assertEquals( 3, getName().length() );
        }
    }

}