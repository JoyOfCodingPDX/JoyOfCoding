package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.Point;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;

import javax.swing.text.JTextComponent;

/**
 * Data container class that holds all the data necessary for JFCUnit to fire mouse events.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class JTextComponentMouseEventData extends AbstractMouseEventData {
    /**
     * The Component on which to trigger the event.
     */
    private JTextComponent comp;

    /**
     * Offset into the text component in characters.
     */
    private int offset = INVALID_TEXT_OFFSET;
    /**
     * Constructor
     */
    public JTextComponentMouseEventData() {
        super();
        setValid(false);
    }
    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp     The component on which to trigger the event.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp) {
        this(_testCase, _comp, DEFAULT_NUMBEROFCLICKS);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp     The component on which to trigger the event.
     * @param _numberOfClicks
     *                 Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp, int _numberOfClicks) {
        this(_testCase, _comp, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _sleepTime
     *                  The wait time in ms between each event.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp, long _sleepTime) {
        this(_testCase, _comp, DEFAULT_NUMBEROFCLICKS, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp, int _numberOfClicks, int _modifiers) {
        this(_testCase, _comp, _numberOfClicks, _modifiers, DEFAULT_ISPOPUPTRIGGER);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp     The component on which to trigger the event.
     * @param _numberOfClicks
     *                 Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                 boolean specifying whether this event will show a popup.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp, int _numberOfClicks, boolean _isPopupTrigger) {
        this(_testCase, _comp, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _sleepTime
     *                  The wait time in ms between each event.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp, int _numberOfClicks, long _sleepTime) {
        this(_testCase, _comp, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, DEFAULT_ISPOPUPTRIGGER, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                  boolean specifying whether this event will show a popup.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp,
                                        int _numberOfClicks, int _modifiers,
                                        boolean _isPopupTrigger) {
        this(_testCase, _comp, _numberOfClicks, _modifiers, _isPopupTrigger, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                  boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                  The wait time in ms between each event.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp,
                                        int _numberOfClicks, boolean _isPopupTrigger,
                                        long _sleepTime) {
        this(_testCase, _comp, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp,
                                        int _numberOfClicks, int _modifiers,
                                        boolean _isPopupTrigger, long _sleepTime) {
        this(_testCase, _comp, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, DEFAULT_POSITION, null, INVALID_TEXT_OFFSET);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _position  The relative mouse position within the cell.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp,
                                        int _numberOfClicks, int _modifiers,
                                        boolean _isPopupTrigger, long _sleepTime,
                                        int _position) {
        this(_testCase, _comp, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, _position, null, INVALID_TEXT_OFFSET);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _position  This parameter will be ignored. It will be set to OFFSET.
     * @param _offset        The offset into the text component.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp,
                                        int _numberOfClicks, int _modifiers,
                                        boolean _isPopupTrigger, long _sleepTime,
                                        int _position, int _offset) {
        this(_testCase, _comp, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, OFFSET, null, _offset);
    }


    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _referencePoint     The CUSTOM mouse position within the cell.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp,
                                        int _numberOfClicks, int _modifiers,
                                        boolean _isPopupTrigger, long _sleepTime,
                                        Point _referencePoint) {
        this(_testCase, _comp, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, CUSTOM, _referencePoint, INVALID_TEXT_OFFSET);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp      The component on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _position  The relative mouse position within the cell.
     * @param _referencePoint
     *                   If _position is CUSTOM then the point is a offset from
     *                   the location of the component. If the _position is PERCENT
     *                   then the location is a percentage offset of the hight and width.
     *                   Otherwise, the _referencePoint is unused.
     * @param _offset    The character offset into the text component.
     */
    public JTextComponentMouseEventData(JFCTestCase _testCase, JTextComponent _comp,
                                        int _numberOfClicks, int _modifiers,
                                        boolean _isPopupTrigger, long _sleepTime,
                                        int _position, Point _referencePoint, int _offset) {
        setTestCase(_testCase);
        setSource(_comp);
        setNumberOfClicks(_numberOfClicks);
        setModifiers(_modifiers);
        setPopupTrigger(_isPopupTrigger);
        setSleepTime(_sleepTime);
        setPosition(_position);
        setReferencePoint(_referencePoint);
        setOffset(_offset);
        setValid(true);
    }

    /**
     * Set the offset
     *
     * @param _offset   The new value of the offset
     */
    public void setOffset(int _offset) {
        offset = _offset;
    }

    /**
     * Get the offset
     *
     * @return int    The value of the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Set the attribute value
     *
     * @param _comp   The new value of the attribute
     */
    public void setSource(JTextComponent _comp) {
        comp = _comp;
    }

    /**
     * Get the attribute value
     *
     * @return Component    The value of the attribute
     */
    public JTextComponent getSource() {
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
     * Prepare the component to receive the event.
     *
     * @return true if the component is ready to receive the event.
     * @exception Exception may be thrown from
     *            JTextComponent.modelToView()
     */
    public boolean prepareComponent() throws Exception {
        if (!isValidForProcessing(getSource())) {
            return false;
        }

        comp.requestFocus();
        JFCTestCase testCase = getTestCase();
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }

        Point p = null;
        if (getPosition() == OFFSET) {
            setPosition(EAST);
            p = calculatePoint(comp.modelToView(offset));
        } else {
            p = calculatePoint(comp.getBounds());
        }
        Point screen = comp.getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);

        return true;
    }

    /**
     * Consume the event.
     * @param ae AWTEvent to be consumed.
     * @return boolean true if the event was consumed.
     */
    public boolean consume(AWTEvent ae) {
        if (super.consume(ae)) {
            return true;
        }
        MouseEvent me = (MouseEvent) ae;
        JTextComponent source = (JTextComponent)me.getSource();
        setSource(source);
        setModifiers(me.getModifiers());
        setNumberOfClicks(me.getClickCount());
        setPopupTrigger(me.isPopupTrigger());
        Point p = new Point(me.getX(), me.getY());
        Point screen = source.getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);

        int offset = ((JTextComponent)source).viewToModel(p);
        setOffset(offset);

        setPosition(CENTER);
        setReferencePoint(null);

        setValid(true);
        return true;
    }

    /**
     * Check if this event can consume the event given.
     * @param ae AWTEvent to be consumed.
     * @return true if the event may be consumed.
     */
    public boolean canConsume(AWTEvent ae) {
        if ((ae.getSource() instanceof JTextComponent)
            && super.canConsume(ae)
            && sameSource(ae)) {
            return true;
        }
        return false;
    }

    /**
     * Get string description of event.
     * @return String description of event.
     */
    public String toString() {
        if (!isValid()) {
            return super.toString();
        }
        StringBuffer buf = new StringBuffer(1000);
        buf.append(super.toString());
        buf.append(" offset: " + getOffset());
        return buf.toString();
    }
}