package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.AWTEvent;
import java.awt.event.KeyEvent;

/**
 * Data container class that holds all the data necessary for JFCUnit to fire mouse events.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class KeyEventData extends AbstractKeyEventData {
    /**
     * The key code to be fired.
     */
    private int keyCode = 0;

    /**
     * The component to be fired upon.
     */
    private Component comp = null;

    /**
     * Default Constructor
     */
    public KeyEventData() {
        super();
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp     The component on which to trigger the event.
     * @param _keyCode  The key to be sent to the component
     */
    public KeyEventData(JFCTestCase _testCase, Component _comp, int _keyCode) {
        this(_testCase, _comp, _keyCode, DEFAULT_KEY_MODIFIERS);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _keyCode           The key to be sent to the component
     * @param _sleepTime The wait time in ms between each event.
     */
    public KeyEventData(JFCTestCase _testCase, Component _comp, int _keyCode, long _sleepTime) {
        this(_testCase, _comp, _keyCode, DEFAULT_KEY_MODIFIERS, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _keyCode          The key to be sent to the component
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public KeyEventData(JFCTestCase _testCase, Component _comp,
                        int _keyCode, int _modifiers,
                        long _sleepTime) {
        setTestCase(_testCase);
        setSource(_comp);
        setKeyCode(_keyCode);
        setModifiers(_modifiers);
        setSleepTime(_sleepTime);
        setValid(true);
    }

    /**
     * Set the key code to be sent.
     *
     * @param _keyCode Key code.
     */
    public void setKeyCode(int _keyCode) {
        keyCode = _keyCode;
    }

    /**
     * Get the key code to be sent.
     *
     * @return int key code to be sent.
     */
    public int getKeyCode() {
        return keyCode;
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
        return "KeyEventData:" + keyCode + " on " + getSource() + " " + getModifiers() + " " + getSleepTime();
    }

    /**
     * Consume the event.
     *
     * @param ae Event to be consumed.
     * @return boolean true if the event was consumed.
     */
    public boolean consume(AWTEvent ae) {
        if (super.consume(ae)) {
            return true;
        }
        setSource((Component) ae.getSource());
        setKeyCode(((KeyEvent) ae).getKeyCode());
        setModifiers(((KeyEvent) ae).getModifiers());
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
            &&  (((KeyEvent) ae).getKeyChar() == KeyEvent.CHAR_UNDEFINED);
    }
}