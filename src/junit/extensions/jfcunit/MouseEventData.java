package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;

/**
 * Data container class that holds all the data necessary for JFCUnit to fire mouse events.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class MouseEventData extends AbstractMouseEventData {
    /**
     * Default value specifying whether the mouse event being fired
     * would trigger a popup or not.
     */
    public static final boolean DEFAULT_ISPOPUPTRIGGER = false;

    /**
     * The Component on which to trigger the event.
     */
    private Component comp;

    /**
     * Default Constructor
     */
    public MouseEventData() {
        super();
        setValid(false);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comp     The component on which to trigger the event.
     */
    public MouseEventData(JFCTestCase _testCase, Component _comp) {
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
    public MouseEventData(JFCTestCase _testCase, Component _comp, int _numberOfClicks) {
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
    public MouseEventData(JFCTestCase _testCase, Component _comp, long _sleepTime) {
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
    public MouseEventData(JFCTestCase _testCase, Component _comp, int _numberOfClicks, int _modifiers) {
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
    public MouseEventData(JFCTestCase _testCase, Component _comp, int _numberOfClicks, boolean _isPopupTrigger) {
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
    public MouseEventData(JFCTestCase _testCase, Component _comp, int _numberOfClicks, long _sleepTime) {
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
    public MouseEventData(JFCTestCase _testCase, Component _comp,
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
    public MouseEventData(JFCTestCase _testCase, Component _comp,
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
    public MouseEventData(JFCTestCase _testCase, Component _comp,
                          int _numberOfClicks, int _modifiers,
                          boolean _isPopupTrigger, long _sleepTime) {
        this(_testCase, _comp, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, DEFAULT_POSITION, null);
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
    public MouseEventData(JFCTestCase _testCase, Component _comp,
                          int _numberOfClicks, int _modifiers,
                          boolean _isPopupTrigger, long _sleepTime,
                          int _position) {
        this(_testCase, _comp, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, _position, null);
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
    public MouseEventData(JFCTestCase _testCase, Component _comp,
                          int _numberOfClicks, int _modifiers,
                          boolean _isPopupTrigger, long _sleepTime,
                          Point _referencePoint) {
        this(_testCase, _comp, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, CUSTOM, _referencePoint);
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
     *                                   If _position is CUSTOM then the point is a offset from
     *                                   the location of the component. If the _position is PERCENT
     *                                   then the location is a percentage offset of the hight and width.
     *                                   Otherwise, the _referencePoint is unused.
     */
    public MouseEventData(JFCTestCase _testCase, Component _comp,
                          int _numberOfClicks, int _modifiers,
                          boolean _isPopupTrigger, long _sleepTime,
                          int _position, Point _referencePoint) {
        setTestCase(_testCase);
        setSource(_comp);
        setNumberOfClicks(_numberOfClicks);
        setModifiers(_modifiers);
        setPopupTrigger(_isPopupTrigger);
        setSleepTime(_sleepTime);
        setPosition(_position);
        setReferencePoint(_referencePoint);
        setValid(true);
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
     * Prepare the component for firing the mouse event.
     *
     * @return boolean true if the component is ready.
     */
    public boolean prepareComponent() {
        if (!isValidForProcessing(getSource())) {
            return false;
        }

        comp.requestFocus();
        JFCTestCase testCase = getTestCase();
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }

        Rectangle bounds = comp.getBounds();
        bounds.setLocation(comp.getLocationOnScreen());
        setLocationOnScreen(calculatePoint(bounds));
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
        Component source = (Component)me.getSource();
        setSource(source);
        setModifiers(me.getModifiers());
        setNumberOfClicks(me.getClickCount());
        setPopupTrigger(me.isPopupTrigger());
        Point p = new Point(me.getX(), me.getY());

        Point screen = source.getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);

        setPosition(CENTER);
        setReferencePoint(null);

        setValid(true);
        return true;
    }

    /**
     * Returns true if the event can be consumed by
     * this instnace of event data.
     *
     * @param ae Event to be consumed.
     * @return true if the event was consumed.
     */
    public boolean canConsume(AWTEvent ae) {
        return super.canConsume(ae);
    }
}