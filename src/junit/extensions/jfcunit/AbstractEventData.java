package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.AWTEvent;
import java.awt.event.KeyEvent;


import javax.swing.SwingUtilities;

/**
 * Abstract data container class that holds most of the data necessary for JFCUnit to fire events.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public abstract class AbstractEventData implements EventDataConstants {
    /**
     * The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     */
    private JFCTestCase testCase;

    /**
     * The modifier key values that need to be passed onto the event.
     */
    private int modifiers;

    /**
     * The wait time in ms between each event.
     */
    private long sleepTime;

    /**
     * The position to place the mouse within the component.
     */
    private int position;

    /**
     * The reference point to place the mouse. Either custom set or
     * derived from the position.
     */
    private Point referencePoint;

    /**
     * The screen location to place the mouse.
     */
    private Point screenLocation;

    /**
     * State of event data. If invalid then data has not been loaded
     * into the event.
     */
    private boolean valid;


    /**
     * Set the attribute value
     *
     * @param _testCase The new value of the attribute
     */
    public void setTestCase(JFCTestCase _testCase) {
        testCase = _testCase;
    }

    /**
     * Get the attribute value
     *
     * @return JFCTestCase    The value of the attribute
     */
    public JFCTestCase getTestCase() {
        return testCase;
    }

    /**
     * Set the attribute value
     *
     * @param _modifiers The new value of the attribute
     */
    public void setModifiers(int _modifiers) {
        modifiers = _modifiers;
    }

    /**
     * Get the attribute value
     *
     * @return int    The value of the attribute
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Set the attribute value
     *
     * @param _sleepTime The new value of the attribute
     */
    public void setSleepTime(long _sleepTime) {
        sleepTime = _sleepTime;
    }

    /**
     * Get the attribute value
     *
     * @return long    The value of the attribute
     */
    public long getSleepTime() {
        return sleepTime;
    }

    /**
     * Checks whether a component is valid for processing using JFCUnit.
     *
     * @param comp   The component to be tested.
     * @return Whether this component is not null and is showing.
     */
    protected boolean isValidForProcessing(Component comp) {
        return (comp != null && comp.isShowing());
    }


    /**
     * The source component on which the event has to be fired.
     *
     * @return The source component
     */
    public abstract Component getComponent();

    /**
     * Return the root container of the component.
     * @return root container.
     */
    public Component getRoot() {
        return getRoot(getComponent());
    }


    /**
     * Return the root container of the component.
     * @param comp Component to obtain the root for.
     * @return root container.
     */
    public Component getRoot(Component comp) {
        return SwingUtilities.getRoot(comp);
    }

    /**
     * Set the attribute value
     *
     * @param _referencePoint The value of the attribute
     */
    public void setReferencePoint(Point _referencePoint) {
        referencePoint = _referencePoint;

    }

    /**
     * Get the attribute value
     *
     * @return Point The value of the attribute
     */
    public Point getReferencePoint() {
        return referencePoint;
    }

    /**
     * Set the attribute value
     *
     * @param _position The value of the attribute
     */
    public void setPosition(int _position) {
        position = _position;
    }

    /**
     * Get the attribute value
     *
     * @return int The value of the attribute
     */
    public int getPosition() {
        return position;
    }

    /**
     * Get the screen location for the event.
     *
     * @return Point screen location for the event.
     */
    public final Point getLocationOnScreen() {
        return screenLocation;
    }

    /**
     * Set the screen location for the event.
     *
     * @param _screenLocation screenLocation for event.
     */
    protected final void setLocationOnScreen(Point _screenLocation) {
        screenLocation = _screenLocation;
    }

    /**
     * A utility method to calculate the point at which the events are
     * to be fired based on the position and referencePoint specified.
     *
     * @param _rect  The rectangle containing the source component.
     * @return The new point at which the mouse events have to be fired.
     */
    protected Point calculatePoint(Rectangle _rect) {
        // if the user has set a specific point or if this calculation has already been done
        // once, then just return.
        if (position == CUSTOM) {
            return referencePoint;
        }

        int x = 0;
        int y = 0;

        if (position == PERCENT) {
            x = _rect.x + (int)(_rect.width * (referencePoint.x / 100));
            y = _rect.y + (int)(_rect.height * (referencePoint.y / 100));
        } else {
            // Calculate the Y position
            if (position == NORTH || position == NORTH_WEST || position == NORTH_EAST) {
                y = _rect.y;
            } else if (position == SOUTH || position == SOUTH_WEST || position == SOUTH_EAST) {
                y = _rect.y + _rect.height;
            } else {
                y = _rect.y + (int) (_rect.height / 2);
            }

            // Calculate the X position
            if (position == WEST || position == NORTH_WEST || position == SOUTH_WEST) {
                x = _rect.x;
            } else if (position == EAST || position == NORTH_EAST || position == SOUTH_EAST) {
                x = _rect.x + _rect.width;
            } else {
                x = _rect.x + (int) (_rect.width / 2);
            }
        }
        return new Point(x, y);
    }

    /**
     * Prepare the component to receive the event.
     * By requestFocus and calculating the screen location.
     *
     * @return true if the component is ready to receive the event.
     * @exception Exception may be generated when preparing
     *            components.
     */
    public boolean prepareComponent()  throws Exception {
        if (!isValidForProcessing(getComponent())) {
            return false;
        }
        JFCTestCase testCase = getTestCase();
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }
        getComponent().requestFocus();
        Point p = calculatePoint(getComponent().getBounds());
        Point screen = getComponent().getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);
        return true;
    }

    // The next set of API's deal with creating event data
    // Based upon a AWT Event for recording.
    /**
     * Consume the event.
     *
     * @param ae Event to be consumed.
     * @return boolean true if the event was consumed.
     */
    abstract boolean consume(AWTEvent ae);

    /**
      * Check if this event can consume the AWTEvent.
     *
     * @param ae Event to be consumed.
     * @return boolean true if the event can be consumed.
     */
    protected boolean canConsume(AWTEvent ae) {
        if (!isValid()) {
            return true;
        }
        return (ae.getSource().equals(getComponent()));
    }

    /**
     * Returns true if the keyCode is
     * a meta character.
     *
     * @param keyCode code to be checked.
     * @return boolean true if the keyCode
     * is a meta key.
     */
    public boolean isMetaChar(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_ALT:
            case KeyEvent.VK_ALT_GRAPH:
            case KeyEvent.VK_CAPS_LOCK:
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_NUM_LOCK:
            case KeyEvent.VK_META:
            case KeyEvent.VK_SHIFT :
                return true;
        }

        return false;
    }

    /**
     * Set valid state.
     * @param valid true if the event has data configured.
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Get the valid state.
     * @return boolean true if the event is valid.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Get the String description of the abstract event.
     * @return String description of the event.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(1000);
        buf.append(getClass().getName());
        if (!isValid()) {
            buf.append(" invalid");
            return buf.toString();
        }
        buf.append(" testCase: " + testCase);
        buf.append(" modifiers: " + modifiers);
        buf.append(" sleepTime: " + sleepTime);
        buf.append(" position:" + POSITIONSTRINGS[position]);
        buf.append(" refPoint: " + referencePoint);
        buf.append(" screenLoc: " + screenLocation);
        buf.append(" component: " + getComponent());
        return buf.toString();
    }
}
