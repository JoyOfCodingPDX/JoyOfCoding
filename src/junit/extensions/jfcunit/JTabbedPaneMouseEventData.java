package junit.extensions.jfcunit;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * Data container class that holds all the data necessary for JFCUnit to fire mouse events.
 * This class is specific to events on a JTabbedPane.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class JTabbedPaneMouseEventData extends AbstractMouseEventData {
    /**
     * Default value specifying whether the mouse event being fired
     * would trigger a popup or not.
     */
    public static final boolean DEFAULT_ISPOPUPTRIGGER = false;

    /**
     * Default value for the tab _title.
     */
    public static final String DEFAULT_TITLE = "";

    /**
     * The JTabbedPane on which to trigger the event.
     */
    private JTabbedPane tabPane;

    /**
     * The zero-based tab index of the specific tab on which to trigger the event.
     */
    private int tabIndex;

    /**
     * The title of the specific tab on which to trigger the event.
     */
    private String title;
    /**
     * Constructor
     */
    public JTabbedPaneMouseEventData() {
        super();
        setValid(false);
    }
    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane  The component on which to trigger the event.
     * @param _tabIndex The zero-based tab index of the specific tab on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     */
    public JTabbedPaneMouseEventData(JFCTestCase _testCase, JTabbedPane _tabPane, int _tabIndex, int _numberOfClicks) {
        this(_testCase, _tabPane, _tabIndex, DEFAULT_TITLE, _numberOfClicks);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane  The component on which to trigger the event.
     * @param _tabIndex The zero-based tab index of the specific tab on which to trigger the event.
     * @param _title    The title of the specific tab on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     */
    public JTabbedPaneMouseEventData(JFCTestCase _testCase, JTabbedPane _tabPane, int _tabIndex, String _title, int _numberOfClicks) {
        this(_testCase, _tabPane, _tabIndex, _title, _numberOfClicks, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane   The component on which to trigger the event.
     * @param _tabIndex  The zero-based tab index of the specific tab on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTabbedPaneMouseEventData(JFCTestCase _testCase, JTabbedPane _tabPane, int _tabIndex, int _numberOfClicks, long _sleepTime) {
        this(_testCase, _tabPane, _tabIndex, DEFAULT_TITLE, _numberOfClicks, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane   The component on which to trigger the event.
     * @param _tabIndex  The zero-based tab index of the specific tab on which to trigger the event.
     * @param _title     The title of the specific tab on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTabbedPaneMouseEventData(JFCTestCase _testCase, JTabbedPane _tabPane, int _tabIndex, String _title, int _numberOfClicks, long _sleepTime) {
        this(_testCase, _tabPane, _tabIndex, _title, _numberOfClicks, DEFAULT_ISPOPUPTRIGGER, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane  The component on which to trigger the event.
     * @param _tabIndex The zero-based tab index of the specific tab on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                  boolean specifying whether this event will show a popup.
     */
    public JTabbedPaneMouseEventData(JFCTestCase _testCase, JTabbedPane _tabPane, int _tabIndex, int _numberOfClicks, boolean _isPopupTrigger) {
        this(_testCase, _tabPane, _tabIndex, DEFAULT_TITLE, _numberOfClicks, _isPopupTrigger);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane  The component on which to trigger the event.
     * @param _tabIndex The zero-based tab index of the specific tab on which to trigger the event.
     * @param _title    The title of the specific tab on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                  boolean specifying whether this event will show a popup.
     */
    public JTabbedPaneMouseEventData(JFCTestCase _testCase, JTabbedPane _tabPane, int _tabIndex, String _title, int _numberOfClicks, boolean _isPopupTrigger) {
        this(_testCase, _tabPane, _tabIndex, _title, _numberOfClicks, _isPopupTrigger, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane   The component on which to trigger the event.
     * @param _tabIndex  The zero-based tab index of the specific tab on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTabbedPaneMouseEventData(JFCTestCase _testCase, JTabbedPane _tabPane, int _tabIndex, int _numberOfClicks, boolean _isPopupTrigger, long _sleepTime) {
        this(_testCase, _tabPane, _tabIndex, DEFAULT_TITLE, _numberOfClicks, _isPopupTrigger, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane   The component on which to trigger the event.
     * @param _tabIndex  The zero-based tab index of the specific tab on which to trigger the event.
     * @param _title     The title of the specific tab on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTabbedPaneMouseEventData(
        JFCTestCase _testCase,
        JTabbedPane _tabPane,
        int _tabIndex,
        String _title,
        int _numberOfClicks,
        boolean _isPopupTrigger,
        long _sleepTime) {
        this(_testCase, _tabPane, _tabIndex, _title, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane   The component on which to trigger the event.
     * @param _tabIndex  The zero-based tab index of the specific tab on which to trigger the event.
     * @param _title     The title of the specific tab on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTabbedPaneMouseEventData(
        JFCTestCase _testCase,
        JTabbedPane _tabPane,
        int _tabIndex,
        String _title,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime) {
        this(_testCase, _tabPane, _tabIndex, _title, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime, DEFAULT_POSITION, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane   The component on which to trigger the event.
     * @param _tabIndex  The zero-based tab index of the specific tab on which to trigger the event.
     * @param _title     The title of the specific tab on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _position  The relative mouse position within the cell.
     */
    public JTabbedPaneMouseEventData(
        JFCTestCase _testCase,
        JTabbedPane _tabPane,
        int _tabIndex,
        String _title,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        int _position) {
        this(_testCase, _tabPane, _tabIndex, _title, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime, _position, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane   The component on which to trigger the event.
     * @param _tabIndex  The zero-based tab index of the specific tab on which to trigger the event.
     * @param _title     The title of the specific tab on which to trigger the event.
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
    public JTabbedPaneMouseEventData(
        JFCTestCase _testCase,
        JTabbedPane _tabPane,
        int _tabIndex,
        String _title,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        Point _referencePoint) {
        this(_testCase, _tabPane, _tabIndex, _title, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime, CUSTOM, _referencePoint);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tabPane   The component on which to trigger the event.
     * @param _tabIndex  The zero-based tab index of the specific tab on which to trigger the event.
     * @param _title     The title of the specific tab on which to trigger the event.
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
    public JTabbedPaneMouseEventData(
        JFCTestCase _testCase,
        JTabbedPane _tabPane,
        int _tabIndex,
        String _title,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        int _position,
        Point _referencePoint) {
        setTestCase(_testCase);
        setSource(_tabPane);
        setNumberOfClicks(_numberOfClicks);
        setModifiers(_modifiers);
        setPopupTrigger(_isPopupTrigger);
        setTabIndex(_tabIndex);
        setTitle(_title);
        setSleepTime(_sleepTime);
        setPosition(_position);
        setReferencePoint(_referencePoint);
        setValid(true);
    }

    /**
     * Set the attribute value
     *
     * @param _tabPane The new value of the attribute
     */
    public void setSource(JTabbedPane _tabPane) {
        tabPane = _tabPane;
    }

    /**
     * Get the attribute value
     *
     * @return JTabbedPane    The value of the attribute
     */
    public JTabbedPane getSource() {
        return tabPane;
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
     * @param _tabIndex The new value of the attribute
     */
    public void setTabIndex(int _tabIndex) {
        tabIndex = _tabIndex;
    }

    /**
     * Get the attribute value
     *
     * @return int    The value of the attribute
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * Set the attribute value
     *
     * @param _title The new value of the attribute
     */
    public void setTitle(String _title) {
        title = _title;
    }

    /**
     * Get the attribute value
     *
     * @return String    The value of the attribute
     */
    public String getTitle() {
        return title;
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

        // if the tab is not currently visible
        if (tabIndex < 0) {
            tabIndex = tabPane.indexOfTab(getTitle());
        }

        if (tabPane.getBoundsAt(tabIndex) == null) {
            return false;
        }

        tabPane.requestFocus();
        JFCTestCase testCase = getTestCase();
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }

        Point p = calculatePoint(tabPane.getBoundsAt(tabIndex));
        Point screen = tabPane.getLocationOnScreen();
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
        JTabbedPane source = (JTabbedPane) me.getSource();
        setSource(source);
        setModifiers(me.getModifiers());
        setNumberOfClicks(me.getClickCount());
        setPopupTrigger(me.isPopupTrigger());
        Point p = new Point(me.getX(), me.getY());
        Point screen = source.getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);

        int index = ((BasicTabbedPaneUI) source.getUI()).tabForCoordinate(source, me.getX(), me.getY());
        setTitle(source.getTitleAt(index));

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
        if ((ae.getSource() instanceof JTabbedPane) && super.canConsume(ae) && sameSource(ae)) {
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
        buf.append(" title: " + getTitle());
        buf.append(" index: " + getTabIndex());
        return buf.toString();
    }
}