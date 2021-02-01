package edu.pdx.cs410J.junit;

import org.junit.internal.AssumptionViolatedException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * The class demonstrates how JUnit 4's {@link ParentRunner} class can be used to generate test cases
 * dynamically.
 */
@RunWith(AutomaticallyGeneratedTest.TestGenerator.class)
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


    public static class TestGenerator extends ParentRunner<GeneratedTest> {

        public TestGenerator( Class<?> testClass )
            throws InitializationError
        {
            super( testClass );
        }

        @Override
        protected List<GeneratedTest> getChildren()
        {
            List<GeneratedTest> tests = new ArrayList<GeneratedTest>();
            for (String name : getTestNames() ) {
                tests.add(new GeneratedTest( name ));                
            }
            return tests;
        }

        @Override
        protected Description describeChild( GeneratedTest generatedTest )
        {
            return Description.createSuiteDescription( generatedTest.getName() );
        }

        @Override
        protected void runChild( GeneratedTest generatedTest, RunNotifier notifier )
        {
            Description desc = describeChild( generatedTest );
            notifier.fireTestStarted( desc );
            try
            {
                generatedTest.run();
            }
            catch ( AssumptionViolatedException e )
            {
                notifier.fireTestAssumptionFailed( new Failure( desc, e) );
            }
            catch ( Throwable e )
            {
                notifier.fireTestFailure( new Failure( desc, e) );
            }
            finally
            {
                notifier.fireTestFinished( desc );
            }
        }
    }
}