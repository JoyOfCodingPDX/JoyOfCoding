package edu.pdx.cs410J.di;

/**
 * The abstract super class of mock objects used for testing
 */
public abstract class MockObject
{
    /**
     * Throws an exception because this method (or rather the method of a mock object that calls it) is not expected
     * to be invoked during expected regular test execution.
     */
    protected void shouldNotInvoke()
    {
        throw new UnsupportedOperationException( "Did not expect this method to be invoked" );
    }
}
