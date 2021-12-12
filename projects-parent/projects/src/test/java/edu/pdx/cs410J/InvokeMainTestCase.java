package edu.pdx.cs410J;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Modifier;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

/**
 * The superclass of test classes that invoke a main method to test a Java program.
 *
 * @author David Whitlock
 * @since Summer 2010
 */
public abstract class InvokeMainTestCase
{
    /**
     * Invokes the <code>main</code> method of the given class with the given arguments and returns an object
     * that represents the result of invoking that method.
     * @param mainClass The class whose main method is invoked
     * @param args The arguments passed to the main method
     * @return The result of the method invocation
     */
    protected MainMethodResult invokeMain( Class<?> mainClass, String... args )
    {
        return new MainMethodResult(mainClass, args).invoke(false);
    }

    /**
     * Invokes the <code>main</code> method of the given class with the given arguments and returns an object
     * that represents the result of invoking that method.
     *
     * This method will not throw a {@link MainClassContainsMutableStaticFields} exception
     * if the <code>mainClass</code> has mutable <code>static</code> fields.
     *
     * @param mainClass The class whose main method is invoked
     * @param args The arguments passed to the main method
     * @return The result of the method invocation
     */
    protected MainMethodResult invokeMainAllowingMutableStaticFields( Class<?> mainClass, String... args )
    {
        return new MainMethodResult(mainClass, args).invoke(true);
    }

    /**
     * Invokes the <code>main</code> method of a class and captures information about the invocation such as the data
     * written to standard out and standard error and the exit code.
     */
    protected static class MainMethodResult
    {
        private final Class<?> mainClass;
        private final String[] args;
        private Integer exitCode;
        private String out;
        private String err;

        MainMethodResult( Class<?> mainClass, String[] args )
        {
            this.mainClass = mainClass;
            this.args = args;
        }

        /**
         * Invokes the main method
         * @return This <code>MainMethodResult</code>
         */
        public MainMethodResult invoke(boolean allowMutableStaticFields)
        {
            Method main;
            try
            {
                main = mainClass.getMethod("main", String[].class);
            }
            catch ( NoSuchMethodException e )
            {
                throw new IllegalArgumentException( "Class " + mainClass.getName() + " does not have a main method" );
            }

            if (!allowMutableStaticFields) {
                checkForMutableStaticFields();
            }

            try
            {
                invokeMain( main );
            }
            catch ( IllegalAccessException e )
            {
                throw new IllegalArgumentException( "Cannot invoke main method of " + mainClass.getName(), e);
            }
            catch ( InvocationTargetException e )
            {
                throw new UncaughtExceptionInMain(e.getCause());
            }
            return this;
        }

        private void checkForMutableStaticFields() {
            List<String> mutableStaticFieldNames = new ArrayList<>();
            for (Field field : mainClass.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                    mutableStaticFieldNames.add(field.getName());
                }
            }

            if (!mutableStaticFieldNames.isEmpty()) {
                throw new MainClassContainsMutableStaticFields(mainClass.getName(), mutableStaticFieldNames);
            }

        }

        private void invokeMain( Method main )
            throws IllegalAccessException, InvocationTargetException
        {
            SecurityManager oldSecurityManager = System.getSecurityManager();
            PrintStream oldOut = System.out;
            PrintStream oldErr = System.err;
            try {
                MainMethodResult.ExitStatusSecurityManager essm = new ExitStatusSecurityManager(oldSecurityManager);
                System.setSecurityManager( essm );

                ByteArrayOutputStream newOut = new ByteArrayOutputStream();
                ByteArrayOutputStream newErr = new ByteArrayOutputStream();
                System.setOut( new PrintStream(newOut) );
                System.setErr( new PrintStream(newErr) );

                try {
                    main.invoke( null, (Object) copyOfArgs());

                } catch ( InvocationTargetException ex ) {
                    if ( ex.getCause() instanceof ExitException ) {
                        this.exitCode = ((ExitException) ex.getCause()).getExitCode();

                    } else {
                        throw ex;
                    }
                }

                this.out = newOut.toString();
                this.err = newErr.toString();

            } finally {
                System.setSecurityManager( oldSecurityManager );
                System.setOut( oldOut );
                System.setErr( oldErr );
            }
        }

        @SuppressWarnings("StringOperationCanBeSimplified")
        private String[] copyOfArgs() {
            String[] copy = new String[this.args.length];
            String[] strings = this.args;
            for (int i = 0; i < strings.length; i++) {
                String arg = strings[i];
                copy[i] = new String(arg);
            }
            return copy;
        }

        /**
         * Returns the exit code of this program (the argument to {@link System#exit(int)}
         * @return <code>null</code> if {@link System#exit(int)} was not invoked
         */
        public Integer getExitCode()
        {
            return this.exitCode;
        }

        /**
         * Returns the data written to standard out
         * @return the data written to standard out
         */
        public String getTextWrittenToStandardOut()
        {
            return out;
        }

        /**
         * Returns the data written to standard err
         * @return the data written to standard err
         */
        public String getTextWrittenToStandardError()
        {
            return err;
        }

        /**
         * A {@link SecurityManager} that delegates security checks to another {@link SecurityManager}, but captures
         * the exit code called by {@link System#exit(int)}
         */
        private static class ExitStatusSecurityManager extends SecurityManager
        {
            private final SecurityManager delegate;

            public ExitStatusSecurityManager( SecurityManager manager )
            {
                this.delegate = manager;
            }

            @Override
            public void checkPermission( Permission perm )
            {
                if (this.delegate != null) {
                    this.delegate.checkPermission( perm );
                }
            }

            @Override
            public void checkPermission( Permission perm, Object context )
            {
                if (this.delegate != null) {
                    this.delegate.checkPermission( perm, context );
                }
            }

            @Override
            public void checkExit( int status )
            {
                if (this.delegate != null) {
                    this.delegate.checkExit( status );
                }

                throw new ExitException(status);
            }

        }

        /**
         * An exception that is thrown when the the main method calls {@link System#exit(int)}.  This lets us capture
         * the exit code without the VM actually exiting.
         */
        private static class ExitException extends SecurityException
        {
            private final int exitCode;

            public ExitException( int exitCode )
            {
                this.exitCode = exitCode;
            }

            public int getExitCode()
            {
                return exitCode;
            }
        }
    }
}