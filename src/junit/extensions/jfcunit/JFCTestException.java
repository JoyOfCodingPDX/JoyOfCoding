package junit.extensions.jfcunit;

/**
 * Exception to indicate problems relating to JFCUnit
 *
 * @author Matt Caswell
 */
public class JFCTestException extends Exception {

    /**
     * Construct a new JFCTestException with a given message
     * @param mesg The message for this exception
     */
    public JFCTestException(String mesg) {
        super(mesg);
    }
}
