package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.Point;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import javax.swing.table.JTableHeader;

/**
 * Data container class that holds all the data necessary for JFCUnit to fire mouse events.
 * This class is specific to events on a JTableHeader.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class JTableHeaderMouseEventData extends AbstractMouseEventData {
    /**
     * The JTableHeader on which to trigger the event.
     */
    private JTableHeader header;

    /**
     * The zero-based column index of the specific cell on which to trigger the event.
     */
    private int columnIndex;

    /**
     * Constructor
     */
    public JTableHeaderMouseEventData() {
        super();
        setValid(false);
    }
    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _header   The component on which to trigger the event.
     * @param _columnIndex
     *                  The zero-based column index of the specific cell on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     */
    public JTableHeaderMouseEventData(JFCTestCase _testCase, JTableHeader _header, int _columnIndex, int _numberOfClicks) {
        this(_testCase, _header, _columnIndex, _numberOfClicks, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _header    The component on which to trigger the event.
     * @param _columnIndex
     *                   The zero-based column index of the specific cell on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTableHeaderMouseEventData(JFCTestCase _testCase, JTableHeader _header,
                                      int _columnIndex, int _numberOfClicks,
                                      long _sleepTime) {
        this(_testCase, _header, _columnIndex, _numberOfClicks, DEFAULT_ISPOPUPTRIGGER, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _header   The component on which to trigger the event.
     * @param _columnIndex
     *                  The zero-based column index of the specific cell on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                  boolean specifying whether this event will show a popup.
     */
    public JTableHeaderMouseEventData(JFCTestCase _testCase, JTableHeader _header,
                                      int _columnIndex, int _numberOfClicks,
                                      boolean _isPopupTrigger) {
        this(_testCase, _header, _columnIndex, _numberOfClicks, _isPopupTrigger, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _header    The component on which to trigger the event.
     * @param _columnIndex
     *                   The zero-based column of index the specific cell on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTableHeaderMouseEventData(JFCTestCase _testCase, JTableHeader _header,
                                      int _columnIndex, int _numberOfClicks,
                                      boolean _isPopupTrigger, long _sleepTime) {
        this(_testCase, _header, _columnIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _header    The component on which to trigger the event.
     * @param _columnIndex
     *                   The zero-based column index of the specific cell on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTableHeaderMouseEventData(JFCTestCase _testCase, JTableHeader _header,
                                      int _columnIndex, int _numberOfClicks,
                                      int _modifiers, boolean _isPopupTrigger,
                                      long _sleepTime) {
        this(_testCase, _header, _columnIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS,
             _isPopupTrigger, _sleepTime, DEFAULT_POSITION, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _header    The component on which to trigger the event.
     * @param _columnIndex
     *                   The zero-based column index of the specific cell on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _position  The relative mouse position within the cell.
     */
    public JTableHeaderMouseEventData(JFCTestCase _testCase, JTableHeader _header,
                                      int _columnIndex, int _numberOfClicks,
                                      int _modifiers, boolean _isPopupTrigger,
                                      long _sleepTime, int _position) {
        this(_testCase, _header, _columnIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS,
             _isPopupTrigger, _sleepTime, _position, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _header    The component on which to trigger the event.
     * @param _columnIndex
     *                   The zero-based column index of the specific cell on which to trigger the event.
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
    public JTableHeaderMouseEventData(JFCTestCase _testCase, JTableHeader _header,
                                      int _columnIndex, int _numberOfClicks,
                                      int _modifiers, boolean _isPopupTrigger,
                                      long _sleepTime, Point _referencePoint) {
        this(_testCase, _header, _columnIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime, CUSTOM, _referencePoint);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _header    The component on which to trigger the event.
     * @param _columnIndex
     *                   The zero-based column index of the specific cell on which to trigger the event.
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
    public JTableHeaderMouseEventData(JFCTestCase _testCase, JTableHeader _header,
                                      int _columnIndex, int _numberOfClicks,
                                      int _modifiers, boolean _isPopupTrigger,
                                      long _sleepTime, int _position,
                                      Point _referencePoint) {
        setTestCase(_testCase);
        setSource(_header);
        setNumberOfClicks(_numberOfClicks);
        setModifiers(_modifiers);
        setPopupTrigger(_isPopupTrigger);
        setColumnIndex(_columnIndex);
        setSleepTime(_sleepTime);
        setPosition(_position);
        setReferencePoint(_referencePoint);
        setValid(true);
    }

    /**
     * Set the attribute value
     *
     * @param _header The new value of the attribute
     */
    public void setSource(JTableHeader _header) {
        header = _header;
    }

    /**
     * Get the attribute value
     *
     * @return JTableHeader    The value of the attribute
     */
    public JTableHeader getSource() {
        return header;
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
     * @param _columnIndex The new value of the attribute
     */
    public void setColumnIndex(int _columnIndex) {
        columnIndex = _columnIndex;
    }

    /**
     * Get the attribute value
     *
     * @return int    The value of the attribute
     */
    public int getColumnIndex() {
        return columnIndex;
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

        header.requestFocus();
        JFCTestCase testCase = getTestCase();
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }

        Point p = calculatePoint(header.getHeaderRect(columnIndex));
        Point screen = header.getLocationOnScreen();
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
        JTableHeader source = (JTableHeader)me.getSource();
        setSource(source);
        setModifiers(me.getModifiers());
        setNumberOfClicks(me.getClickCount());
        setPopupTrigger(me.isPopupTrigger());
        Point p = new Point(me.getX(), me.getY());
        Point screen = source.getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);

        int column = source.columnAtPoint(p);

        setColumnIndex(column);
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
        if ((ae.getSource() instanceof JTableHeader)
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
        buf.append(" column: " + getColumnIndex());
        return buf.toString();
    }
}