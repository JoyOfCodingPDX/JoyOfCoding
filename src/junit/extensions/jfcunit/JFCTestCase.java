package junit.extensions.jfcunit;

import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

/**
 * Extend this class to create new tests for Swing based interfaces. This is a
 * subclass of TestCase, and therefore provides all of the facilities that you
 * would normally expect from TestCase.
 *
 * An important point to realise about coding with Swing classes is that most
 * methods are single threaded. This means that once a component has been
 * shown, its methods should normally only be accessed by the AWT thread.
 * While JFCTestCase runs its tests, the AWT thread is temporarily blocked to
 * prevent multi-threading issues. The effect of this is that any method calls
 * that you make on Swing components during a test will have no impact on the
 * GUI, until the AWT Thread is restarted. This can occur in one of two ways:
 * (1) The test runs to completion (2) The method "awtSleep" defined within
 * this class is called.
 *
 * @author Matt Caswell
 * @author Vijay Aravamudhan
 */
public class JFCTestCase extends TestCase {
    /** Time to wait between lock attempts. */
    private static final long LOCKWAIT = 25L;

    /** Default time to sleep */
    private static final long DEFAULTSLEEP = 100L;

    /** wait for full time */
    private boolean forcedWait = false;

    /** Continuation flag. */
    private boolean cont = false;

    /** Exception thrown by testcase. */
    private Throwable err;

    /** Lock instance. */
    private Object lock = new Object();

    /** Time to sleep. */
    private long sleepTime = DEFAULTSLEEP;

    /** Waiting flag */
    private volatile boolean waiting = false;

    /**
     * Constructs a new JFCTestCase
     * @param name The name of the test
     */
    public JFCTestCase(String name) {
        super(name);
    }

    /**
     * Default setUp which does nothing. Override this to set up the environment
     * for your test
     * @exception Exception may be thrown.
     */
    protected void setUp() throws Exception {
    }

    /**
     * Default tearDown which does nothing. Override this to tear down the
     * environment for your test.
     * @exception Exception may be thrown.
     */
    protected void tearDown() throws Exception {
    }

    /**
     * Sets the forcedWait
     *
     * @param forced New value for forcedWait
     */
    protected void setForcedWait(boolean forced) {
        forcedWait = forced;
    }

    /**
     * Resets the forcedWait to the default value (false)
     */
    protected void resetForcedWait() {
        forcedWait = false;
    }

    /**
     * Sets the sleepTime
     *
     * @param time New value for sleepTime
     */
    public void setSleepTime(long time) {
        sleepTime = time;
    }

    /**
     * Resets the sleepTime to the default value (DEFAULTSLEEP)
     */
    public void resetSleepTime() {
        sleepTime = DEFAULTSLEEP;
    }

    /**
     * Returns the value of sleepTime
     *
     * @return sleepTime
     */
    private long getSleepTime() {
        return sleepTime;
    }

    /**
     * Sets the cont
     *
     * @param c New value for cont
     */
    private void setContinue(boolean c) {
        cont = c;
    }

    /**
     * Returns the cont
     *
     * @return cont
     */
    private boolean getContinue() {
        return cont;
    }

    /**
     * Sets the err
     *
     * @param error New value for err
     */
    private void setError(Throwable error) {
        err = error;
    }

    /**
     * Resets the err to the default value (null)
     */
    private void resetError() {
        err = null;
    }

    /**
     * Checks of the current test case has any errors
     *
     * @return true if the current test case has any errors
     */
    private boolean hasError() {
        return (err != null);
    }

    /**
     * Returns the error
     *
     * @return error
     */
    private Throwable getError() {
        return err;
    }

    /**
     * Suspends the test for a maximum period of time, and allows the
     * AWT Thread to run. The time period is what was set by the user by calling
     * 'setSleepTime()' or 'awtSleep(sleepTime)'. If the notify occurs before this
     * time has elapsed, the thread will continue.
     */
    public void awtSleep() {
        lock.notify();
        try {
            lock.wait();
        } catch (InterruptedException ie) {
            //Shouldn't occur
        }
    }

    /**
     * Suspends the test for the specified (maximum) period of time, and allows the AWT
     * Thread to run. Note that the sleep time for subsequent calls to awtSleep()
     * will use the time specified in the current call. If the notify occurs before
     * this time has elapsed, the thread will continue.
     * @param sleepTime The number of ms to sleep for
     */
    public void awtSleep(long sleepTime) {
        setSleepTime(sleepTime);
        awtSleep();
    }

    /**
     * Suspends the test for a period of time, and allows the
     * AWT Thread to run.
     * @param delay The minimum amount of time the test case thread
     *              should be delayed.
     */
    public void sleep(long delay) {
        long releaseTime = System.currentTimeMillis() + delay;
        releaseTime += delay;
        while (System.currentTimeMillis() < releaseTime) {
            lock.notify();
            try {
                Thread.currentThread().sleep(LOCKWAIT);
                lock.wait();
            } catch (InterruptedException ie) {
                //Shouldn't occur
            }
        }
        long actual = System.currentTimeMillis() - releaseTime + delay;
    }

    /**
     * Executes a test
     * @exception Throwable exceptions thrown by code.
     */
    protected void runTest() throws Throwable {
        final Method runMethod;

        try {
            runMethod = getClass().getMethod(getName(), new Class[0]);
        } catch (NoSuchMethodException e) {
            fail("Method \"" + getName() + "\" not found");
            return;
        }
        if (runMethod != null && !Modifier.isPublic(runMethod.getModifiers())) {
            fail("Method \"" + getName() + "\" should be public");
            return;
        }

        runCode(new Runnable() {
            public void run() {
                try {
                    runMethod.invoke(JFCTestCase.this, new Object[0]);
                } catch (InvocationTargetException e) {
                    setError(e.getTargetException());
                } catch (IllegalAccessException e) {
                    setError(e);
                }
            }
        });
    }

    /**
     * Sets up, executes and then tears down a test
     * @exception Throwable exceptions thrown by code.
     */
    public void runBare() throws Throwable {
        try {
            runCode(new Runnable() {
                public void run() {
                    try {
                        JFCTestCase.this.setUp();
                    } catch (Exception e) {
                        setError(e);
                    }
                }
            });

            runTest();
        } finally {
            runCode(new Runnable() {
                public void run() {
                    try {
                        JFCTestCase.this.tearDown();
                    } catch (Exception e) {
                        setError(e);
                    }
                }
            });
        }
    }

    /**
     * Note: This method will wait till the thread running code.run() completes
     * The above behavior is overridden if the setForcedWait() has been called with 'true'
     * Then the behavior is that the thread will wait for the time specified when awtSleep()
     * was called
     * @param code Code which is to be executed.
     * @exception Throwable exceptions thrown by code.
     */
    private synchronized void runCode(final Runnable code) throws Throwable {
        waiting = false;
        setContinue(true);
        resetError();
        new Thread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    waiting = true;
                    try {
                        lock.wait();
                    } catch (InterruptedException ie) {
                        //Shouldn't ever happen
                    }
                    code.run();
                    setContinue(false);
                    lock.notify();
                }
            }
        }, getName()).start();

        // here we have to wait for the code.run() to block
        while (!waiting) {
            try {
                Thread.currentThread().sleep(LOCKWAIT);
            } catch (InterruptedException ie) {
                //Shouldn't occur
            }
        }

        boolean condition = false;
        boolean firstTime = true;
        long elapsedTime = 0L;
        do {
            if (firstTime) {
                // flush out any other dialog boxes, etc which were opened by the call to code.run()
                Toolkit.getDefaultToolkit().getSystemEventQueue().invokeAndWait(new Runnable() {
                    public void run() {
                        synchronized (lock) {
                            lock.notify();
                            try {
                                lock.wait();
                            } catch (InterruptedException ie) {
                                //Shouldn't ever happen
                            }
                        }
                    }
                });
                if (forcedWait) {
                    firstTime = false;
                }
            }

            try {
                elapsedTime += LOCKWAIT;
                Thread.currentThread().sleep(LOCKWAIT);
            } catch (InterruptedException ie) {
                //Shouldn't occur
            }

            if (forcedWait) {
                condition = (elapsedTime <= getSleepTime());
            } else {
                condition = getContinue();
            }
        } while (condition);

        if (hasError()) {
            throw getError();
        }
    }
}
