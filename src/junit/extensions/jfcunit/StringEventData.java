package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.AWTEvent;
import java.awt.event.KeyEvent;


/**
 * Data container class that holds all the data necessary for JFCUnit to fire mouse events.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class StringEventData extends AbstractKeyEventData {
    /**
     * The string code to be fired.
     */
    private String string = null;

    /**
     * The string code to be fired.
     */
    private Component comp = null;

    /**
     * Default Constructor
     */
    public StringEventData() {
        super();
        setValid(false);
    }
    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp     The component on which to trigger the event.
     * @param _string   The string to be sent to the component
     */
    public StringEventData(JFCTestCase _testCase, Component _comp, String _string) {
        this(_testCase, _comp, _string, DEFAULT_KEY_MODIFIERS);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _string    The string to be sent to the component
     * @param _sleepTime The wait time in ms between each event.
     */
    public StringEventData(JFCTestCase _testCase, Component _comp, String _string, long _sleepTime) {
        this(_testCase, _comp, _string, DEFAULT_KEY_MODIFIERS, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _string    The string to be sent to the component
     * @param _modifiers The modifier string values that need to be passed onto the event.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public StringEventData(JFCTestCase _testCase, Component _comp,
                           String _string, int _modifiers,
                           long _sleepTime) {
        setTestCase(_testCase);
        setSource(_comp);
        setString(_string);
        setModifiers(_modifiers);
        setSleepTime(_sleepTime);
        setValid(true);
    }

    /**
     * Set the string to be sent.
     *
     * @param _string Key code.
     */
    public void setString(String _string) {
        string = _string;
    }

    /**
     * Get the string to be sent.
     *
     * @return String string to be sent.
     */
    public String getString() {
        return string;
    }

    /**
     * Set the attribute value
     *
     * @param _comp   The new value of the attribute
     */
    public void setSource(Component _comp) {
        comp = _comp;
    }

    /**
     * Get the attribute value
     *
     * @return Component    The value of the attribute
     */
    public Component getSource() {
        return comp;
    }

    /**
     * The component on which the event has to be fired.
     *
     * @return The component
     */
    public Component getComponent() {
        // by default, the component is the same as the source
        return getSource();
    }


    /**
     * Generate a description of the event.
     *
     * @return String description of the event.
     */
    public String toString() {
        return "StringEventData:" + getString() + " on " + getComponent() + " " + getModifiers() + " " + getSleepTime();
    }

    /**
     * Consume the event.
     * @param ae Event to be consumed.
     * @return boolean true if the event was consumed.
     */
    public boolean consume(AWTEvent ae) {
        if (super.consume(ae)) {
            return true;
        }
        setSource((Component)ae.getSource());
        StringBuffer buf = new StringBuffer();
        if (isValid()) {
            buf.append(getString());
        }
        buf.append(((KeyEvent) ae).getKeyChar());
        setString(buf.toString());
        setValid(true);
        return true;
    }

    /**
     * Check if this event can consume the AWTEvent.
     *
     * @param ae Event to be consumed.
     * @return boolean true if the event can be consumed.
     */
    public boolean canConsume(AWTEvent ae) {
            if (!(ae instanceof KeyEvent)) {
                return false;
            }
            return super.canConsume(ae)
                && (isMetaChar(((KeyEvent) ae).getKeyCode()) ||
                    (((KeyEvent) ae).getKeyChar() != KeyEvent.CHAR_UNDEFINED)
                );
    }
}