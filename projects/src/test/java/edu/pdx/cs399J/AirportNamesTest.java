package edu.pdx.cs399J;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Demonstrates how {@link InvokeMainTestCase} is used to test running a <code>main</code> method of the
 * {@link AirportNames} class.
 */
public class AirportNamesTest extends InvokeMainTestCase {

    /**
     * Tests that invoking the <code>main</code> method of {@link AirportNames} returns an exit code of zero
     */
    @Test
    public void testExitCode() {
        MainMethodResult result = invokeMain(AirportNames.class);
        assertEquals(new Integer(0), result.getExitCode());
    }


}