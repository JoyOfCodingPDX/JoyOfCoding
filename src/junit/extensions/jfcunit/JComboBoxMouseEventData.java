package junit.extensions.jfcunit;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.accessibility.Accessible;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;

/**
 * Data container class that holds all the data necessary for JFCUnit to fire mouse events.
 * This class is specific to events on a JComboBox.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class JComboBoxMouseEventData extends AbstractMouseEventData {
    /**
     * The JComboBox on which to trigger the event.
     */
    private JComboBox comboBox;

    /**
     * The list view associated with the JComboBox.
     */
    private JList listView;

    /**
     * The zero-based index of the specific element on which to trigger the event.
     */
    private int elementIndex;

    /**
     * Constructor
     */
    public JComboBoxMouseEventData() {
        super();
        setValid(false);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comboBox The JComboBox on which to trigger the event.
     * @param _elementIndex
     *                  The zero-based index of the specific element on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     */
    public JComboBoxMouseEventData(JFCTestCase _testCase, JComboBox _comboBox, int _elementIndex, int _numberOfClicks) {
        this(_testCase, _comboBox, _elementIndex, _numberOfClicks, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comboBox  The JComboBox on which to trigger the event.
     * @param _elementIndex
     *                   The zero-based index of the specific element on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JComboBoxMouseEventData(JFCTestCase _testCase, JComboBox _comboBox, int _elementIndex, int _numberOfClicks, long _sleepTime) {
        this(_testCase, _comboBox, _elementIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, DEFAULT_ISPOPUPTRIGGER, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comboBox  The JComboBox on which to trigger the event.
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
    public JComboBoxMouseEventData(
        JFCTestCase _testCase,
        JComboBox _comboBox,
        int _elementIndex,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime) {
        this(_testCase, _comboBox, _elementIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, DEFAULT_ISPOPUPTRIGGER, _sleepTime, DEFAULT_POSITION, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comboBox  The JComboBox on which to trigger the event.
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
    public JComboBoxMouseEventData(
        JFCTestCase _testCase,
        JComboBox _comboBox,
        int _elementIndex,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        Point _referencePoint) {
        this(_testCase, _comboBox, _elementIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, DEFAULT_ISPOPUPTRIGGER, _sleepTime, CUSTOM, _referencePoint);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comboBox  The JComboBox on which to trigger the event.
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
    public JComboBoxMouseEventData(
        JFCTestCase _testCase,
        JComboBox _comboBox,
        int _elementIndex,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        int _position) {
        this(_testCase, _comboBox, _elementIndex, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, DEFAULT_ISPOPUPTRIGGER, _sleepTime, _position, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _comboBox  The JComboBox on which to trigger the event.
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
    public JComboBoxMouseEventData(
        JFCTestCase _testCase,
        JComboBox _comboBox,
        int _elementIndex,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        int _position,
        Point _referencePoint) {
        setTestCase(_testCase);
        setSource(_comboBox);
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
     * @param _comboBox The new value of the attribute
     */
    public void setSource(JComboBox _comboBox) {
        comboBox = _comboBox;
    }

    /**
     * Get the attribute value
     *
     * @return JComboBox    The value of the attribute
     */
    public JComboBox getSource() {
        return comboBox;
    }

    /**
     * The component on which the event has to be fired.
     *
     * @return The component
     */
    public Component getComponent() {
        // by default, the component is the same as the source
        return listView;
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

        comboBox.requestFocus();
        JFCTestCase testCase = getTestCase();
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }
        comboBox.showPopup();
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }

        BasicComboPopup bcp = (BasicComboPopup) comboBox.getAccessibleContext().getAccessibleChild(0 /*popup*/
        );
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }
        listView = bcp.getList();

        listView.requestFocus();
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }

        listView.ensureIndexIsVisible(elementIndex);
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }

        Point p = calculatePoint(listView.getCellBounds(elementIndex, elementIndex));
        Point screen = listView.getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);
        return true;
    }

    /**
     * Get the parent combo box of the list.
     *
     * @param list List to check
     * @return JComboBox of parent.
     */
    private JComboBox getParentComboBox(JList list) {
        Accessible ap = list.getAccessibleContext().getAccessibleParent();
        while (ap != null) {
            if (ap instanceof JComboBox) {
                return (JComboBox) ap;
            }
            ap = ap.getAccessibleContext().getAccessibleParent();
        }
        return null;
    }

    /**
     * Get the parent combo box of the list.
     *
     * @param list List to check
     * @return true if combo box.
     */
    private boolean isComboBox(JList list) {
        Accessible ap = list.getAccessibleContext().getAccessibleParent();
        while (ap != null) {
            if ((ap instanceof ComboBoxUI) || (ap.getClass().getName().indexOf("ComboBoxUI") != -1)) {
                return true;
            }
            ap = ap.getAccessibleContext().getAccessibleParent();
        }
        return false;
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
        setSource(getParentComboBox(source));
        setModifiers(me.getModifiers());
        setNumberOfClicks(me.getClickCount());
        setPopupTrigger(me.isPopupTrigger());
        Point p = new Point(me.getX(), me.getY());
        Point screen = source.getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);

        int index = source.locationToIndex(new Point(me.getX(), me.getY()));
        setElementIndex(index);

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
        if (!super.canConsume(ae) || !(ae.getSource() instanceof JList)) {
            return false;
        }
        if (isValid()) {
            JList source = (JList) ae.getSource();
            int index = source.locationToIndex(new Point(((MouseEvent) ae).getX(), ((MouseEvent) ae).getY()));
            if (index != getElementIndex()) {
                return false;
            }
        }

        return isComboBox((JList) ae.getSource());
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