#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link Project4} class by invoking its main method with various arguments 
 */
public class Project4Test extends InvokeMainTestCase {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = String.valueOf(8080);

    @Test
    public void testNoCommandLineArguments() {
        MainMethodResult result = invokeMain( Project4.class );
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains( Project4.MISSING_ARGS ));
    }

    @Test
    public void testEmptyServer() {
        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT );
        assertEquals(result.getErr(), new Integer(0), result.getExitCode());
        String out = result.getOut();
        assertTrue( out, out.contains( Messages.getMappingCount(0)));
    }

    @Test
    public void testNoValues() {
        String key = "KEY";
        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT, key );
        assertEquals(result.getErr(), new Integer(0), result.getExitCode());
        String out = result.getOut();
        assertTrue( out, out.contains( Messages.getMappingCount(0)));
        assertTrue( out, out.contains( Messages.formatKeyValuePair( key, null )));
    }

    @Test
    public void testAddValue() {
        String key = "KEY";
        String value = "VALUE";

        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT, key, value );
        assertEquals(result.getErr(), new Integer(0), result.getExitCode());
        String out = result.getOut();
        assertTrue( out, out.contains( Messages.mappedKeyValue( key, value )));

        result = invokeMain( Project4.class, HOSTNAME, PORT, key );
        out = result.getOut();
        assertTrue( out, out.contains( Messages.getMappingCount(1)));
        assertTrue( out, out.contains( Messages.formatKeyValuePair( key, value )));

        result = invokeMain( Project4.class, HOSTNAME, PORT );
        out = result.getOut();
        assertTrue( out, out.contains( Messages.getMappingCount(1)));
        assertTrue( out, out.contains( Messages.formatKeyValuePair( key, value )));
    }
}