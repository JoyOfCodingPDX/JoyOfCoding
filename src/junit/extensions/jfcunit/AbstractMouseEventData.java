package junit.extensions.jfcunit;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.awt.Component;

/**
 * Abstract data container class that holds most of the data necessary for JFCUnit to fire mouse events.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public abstract class AbstractMouseEventData extends AbstractEventData {
    /**
     * Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     */
    private int numberOfClicks = 0;

    /**
     * boolean specifying whether this event will show a popup.
     */
    private boolean isPopupTrigger;

    /**
     * Set the attribute value
     *
     * @param _numberOfClicks The new value of the attribute
     */
    public void setNumberOfClicks(int _numberOfClicks) {
        numberOfClicks = _numberOfClicks;
    }

    /**
     * Get the attribute value
     *
     * @return int    The value of the attribute
     */
    public int getNumberOfClicks() {
        return numberOfClicks;
    }

    /**
     * Set the attribute value
     *
     * @param _isPopupTrigger The new value of the attribute
     */
    public void setPopupTrigger(boolean _isPopupTrigger) {
        isPopupTrigger = _isPopupTrigger;
    }

    /**
     * Get the attribute value
     *
     * @return boolean    The value of the attribute
     */
    public boolean getPopupTrigger() {
        return isPopupTrigger;
    }

    /**
     * Get the attribute value
     * @param ae Event to be processed.
     * @return boolean    The value of the attribute
     */
    public boolean consume(AWTEvent ae) {
        MouseEvent me = (MouseEvent)ae;
        int id = me.getID();
        if (id == MouseEvent.MOUSE_MOVED ||
            id == MouseEvent.MOUSE_ENTERED ||
            id == MouseEvent.MOUSE_EXITED ||
            (isValid() && ae.getSource() == getRoot())) {
            // Ignore the event.
            return true;
        }
        return false;
    }

    /**
     * Check if this event can consume the AWTEvent.
     *
     * @param ae Event to be consumed.
     * @return boolean true if the event can be consumed.
     */
    public boolean canConsume(AWTEvent ae) {
        if (!(ae instanceof MouseEvent)) {
            return false;
        }
        MouseEvent me=(MouseEvent)ae;
        if (me.getID() == MouseEvent.MOUSE_DRAGGED) {
            return false;
        }
        if (ae.getSource().equals(getRoot((Component)ae.getSource()))) {
            return false;
        }
        return true;
    }

    /**
     * Check if the event has the same source as this event data.
     * @param ae AWTEvent to be checked.
     * @return true if the events have the same source.
     */
    public boolean sameSource(AWTEvent ae) {
        if (isValid()) {
            return ((Component)ae.getSource() == getComponent());
        } else {
            return true;
        }
    }

    /**
     * Return a string representing the eventdata.
     * @return String description of the event data.
     */
    public String toString() {
        if (!isValid()) {
            return super.toString();
        }
        StringBuffer buf = new StringBuffer(1000);
        buf.append(super.toString());
        buf.append(" clicks: " + getNumberOfClicks());
        buf.append(" popup: " + getPopupTrigger());

        return buf.toString();
    }

}
