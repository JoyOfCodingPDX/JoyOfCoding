package junit.extensions.jfcunit;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JList;

/**
 * Data container class that holds all the data necessary for JFCUnit to fire mouse events.
 * This class is specific to events on a JList.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class JListMouseEventData extends AbstractMouseEventData {
    /**
     * The JList on which to trigger the event.
     */
    private JList list;

    /**
     * The zero-based index of the specific element on which to trigger the event.
     */
    private int elementIndex;

    /**
     * Constructor
     */
    public JListMouseEventData() {
        super();
        setValid(false);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _list     The JList on which to trigger the event.
     * @param _elementIndex
     *                  The zero-based index of the specific element on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     */
    public JListMouseEventData(JFCTestCase _testCase, JList _list, int _elementIndex, int _numberOfClicks) {
        this(_testCase, _list, _elementIndex, _numberOfClicks, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _list      The JList on which to trigger the event.
     * @param _elementIndex
     *                   The zero-based index of the specific element on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JListMouseEventData(JFCTestCase _testCase, JList _list, int _elementIndex, int _numberOfClicks, long _sleepTime) {
        this(_testCase, _list, _elementIndex, _numberOfClicks, DEFAULT_ISPOPUPTRIGGER, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _list     The JList on which to trigger the event.
     * @param _elementIndex
     *                  The zero-based index of the specific element on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                  boolean specifying whether this event will show a popup.
     */
    public JListMouseEventData(JFCTestCase _testCase, JList _list, int _elementIndex, int _numberOfClicks, boolean _isPopupTrigger) {
        this(_testCase, _list, _elementIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _list      The JList on which to trigger the event.
     * @param _elementIndex
     *                   The zero-based index of the specific element on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JListMouseEventData(JFCTestCase _testCase, JList _list, int _elementIndex, int _numberOfClicks, boolean _isPopupTrigger, long _sleepTime) {
        this(_testCase, _list, _elementIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _list      The JList on which to trigger the event.
     * @param _elementIndex
     *                   The zero-based index of the specific element on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JListMouseEventData(JFCTestCase _testCase, JList _list, int _elementIndex, int _numberOfClicks, int _modifiers, boolean _isPopupTrigger, long _sleepTime) {
        this(_testCase, _list, _elementIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime, DEFAULT_POSITION, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _list      The JList on which to trigger the event.
     * @param _elementIndex
     *                   The zero-based index of the specific element on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _position  The relative mouse position within the cell.
     */
    public JListMouseEventData(
        JFCTestCase _testCase,
        JList _list,
        int _elementIndex,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        int _position) {
        this(_testCase, _list, _elementIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime, _position, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _list      The JList on which to trigger the event.
     * @param _elementIndex
     *                   The zero-based index of the specific element on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _referencePoint
     *                   The CUSTOM mouse position within the cell.
     */
    public JListMouseEventData(
        JFCTestCase _testCase,
        JList _list,
        int _elementIndex,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        Point _referencePoint) {
        this(_testCase, _list, _elementIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime, CUSTOM, _referencePoint);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _list      The JList on which to trigger the event.
     * @param _elementIndex
     *                   The zero-based index of the specific element on which to trigger the event.
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
    public JListMouseEventData(
        JFCTestCase _testCase,
        JList _list,
        int _elementIndex,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        int _position,
        Point _referencePoint) {
        setTestCase(_testCase);
        setSource(_list);
        setNumberOfClicks(_numberOfClicks);
        setModifiers(_modifiers);
        setPopupTrigger(_isPopupTrigger);
        setElementIndex(_elementIndex);
        setSleepTime(_sleepTime);
        setPosition(_position);
        setReferencePoint(_referencePoint);
        setValid(true);
    }

    /**
     * Set the attribute value
     *
     * @param _list  The new value of the attribute
     */
    public void setSource(JList _list) {
        list = _list;
    }

    /**
     * Get the attribute value
     *
     * @return JList    The value of the attribute
     */
    public JList getSource() {
        return list;
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
     * Set the attribute value
     *
     * @param _elementIndex The new value of the attribute
     */
    public void setElementIndex(int _elementIndex) {
        elementIndex = _elementIndex;
    }

    /**
     * Get the attribute value
     *
     * @return int    The value of the attribute
     */
    public int getElementIndex() {
        return elementIndex;
    }

    /**
     * Prepare the component to receive the event.
     *
     * @return true if the component is ready to receive the event.
     */
    public boolean prepareComponent() {
        if (!isValidForProcessing(getSource())) {
            return false;
        }
        list.requestFocus();
        JFCTestCase testCase = getTestCase();
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }

        list.ensureIndexIsVisible(elementIndex);
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }
        Point p = calculatePoint(list.getCellBounds(elementIndex, elementIndex));
        Point screen = list.getLocationOnScreen();
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
        JList source = (JList) me.getSource();
        setSource(source);
        setModifiers(me.getModifiers());
        setNumberOfClicks(me.getClickCount());
        setPopupTrigger(me.isPopupTrigger());
        Point p = new Point(me.getX(), me.getY());
        Point screen = source.getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);

        int index = list.locationToIndex(new Point(me.getX(), me.getY()));
        setElementIndex(index);

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
        if (!super.canConsume(ae) || !(ae.getSource() instanceof JList) || !sameSource(ae)) {
            return false;
        }
        if (isValid()) {
            JList source = (JList) ae.getSource();
            int index = source.locationToIndex(new Point(((MouseEvent) ae).getX(), ((MouseEvent) ae).getY()));
            if (index != getElementIndex()) {
                return false;
            }
        }
        return true;
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
        buf.append(" index: " + getElementIndex());
        return buf.toString();
    }
}