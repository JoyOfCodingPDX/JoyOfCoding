package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.Point;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.AWTEvent;

/**
 * DragEventSource is a wrapper for Drag Events.
 * The event encapsulates the source and destination
 * locations for the drag with xxxMouseEventData
 * types.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class DragEventData extends AbstractEventData {

    /** Source of the drag event */
    private AbstractMouseEventData source;

    /** Destination of the drag event */
    private AbstractMouseEventData dest;

    /** List of points in the drag path */
    private Vector points = new Vector();

    /**
     * Constructor
     * Assumes a null destination and default sleep time.
     * @param testCase TestCase to fire the sleeps upon.
     * @param source   Source event data for the drag event.
     */
    public DragEventData(JFCTestCase testCase,
        AbstractMouseEventData source) {
        this(testCase, source, null, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor for a drag event. Assumes the
     * default sleepTime.
     *
     * @param _testCase TestCase.
     * @param _source AbstractMouseEventData indicating the
     * starting location of the drag event.
     * @param _dest AbstractMouseEventData indicating the
     * ending location of the drag event.
     */
    public DragEventData(JFCTestCase _testCase,
            AbstractMouseEventData _source,
            AbstractMouseEventData _dest) {
        this (_testCase, _source, _dest, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor for a drag event.
     *
     * @param _testCase TestCase.
     * @param _source AbstractMouseEventData indicating the
     * starting location of the drag event.
     * @param _dest AbstractMouseEventData indicating the
     * ending location of the drag event.
     * @param _delay sleepTime of the event.
     */
    public DragEventData(JFCTestCase _testCase,
        AbstractMouseEventData _source,
        AbstractMouseEventData _dest,
        long _delay) {
        setTestCase(_testCase);
        setSource(_source);
        setDest(_dest);
        setSleepTime(_delay);
        if (getSource() != null && getSource().isValid()) {
            setValid(true);
        }
    }

    /**
     * Get the Source of the drag event.
     *
     * @return AbstractMouseEventData for the source.
     */
    public AbstractMouseEventData getSource() {
        return source;
    }

    /**
     * Set the Source of the drag event.
     *
     * @param _source Start location of the drag event.
     */
    public void setSource(AbstractMouseEventData _source) {
        source = _source;
    }

    /**
     * Get the destination MouseEventData.
     *
     * @return AbstractMouseEventData destination.
     */
    public AbstractMouseEventData getDest() {
        return dest;
    }

    /**
     * Set the Destination of the drag event.
     *
     * @param _dest destination MouseEventData.
     */
    public void setDest(AbstractMouseEventData _dest) {
        dest = _dest;
    }

    /**
     * This method is provided here to close the
     * abstract base class.
     *
     * @return null This method always returns null.
     */
    public Component getComponent() {
        return null;
    }

    /**
     * This method is provided here to close the abstract
     * base class.
     * @return false Always returns false.
     * @exception Exception thrown.
     */
    public boolean prepareComponent() throws Exception {
        boolean result = true;
        if (dest != null) {
            result = dest.prepareComponent();
            points.add(dest.getLocationOnScreen());
        }
        return source.prepareComponent() & result;
    }

    /**
     * Add a point to the drag path.
     * @param p Point to be hit along the drag path.
     */
    public void addPoint(Point p) {
        points.add(p);
    }

    /**
     * Get the list of points in the drag path.
     * @return Point[] list of points.
     */
    public Point[] getPoints() {
        return (Point[])points.toArray(new Point[0]);
    }

    /**
     * Set the points in the drag path.
     * @param points Set of points to be hit in the
     *               drag path.
     */
    public void setPoints(Point[] points) {
        this.points.clear();
        for (int i = 0; i < points.length; i++) {
            this.points.add(points[i]);
        }
    }

    /**
     * Consume the event.
     *
     * @param ae Event to be consumed.
     * @return boolean true if the event was consumed.
     */
    public boolean consume(AWTEvent ae) {
        if (!(ae instanceof MouseEvent)) {
            return false;
        }
        MouseEvent me = (MouseEvent)ae;
        if (me.getID() == MouseEvent.MOUSE_DRAGGED) {
            Point p = new Point(me.getX(), me.getY());
            addPoint(p);
        } else {
            return getSource().consume(ae);
        }
        return true;
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
        MouseEvent me = (MouseEvent)ae;
        if (me.getID() == MouseEvent.MOUSE_DRAGGED) {
            return true;
        }

        if (isValid()) {
            return getSource().canConsume(ae);
        }
        return false;
    }

    /**
     * Return a string representing the eventdata.
     * @return String description of the event data.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(1000);
        buf.append("DragEventData:");
        if (!isValid()) {
            buf.append(" invalid");
            return buf.toString();
        }
        buf.append("(Source:" + getSource().toString() + ")");
        buf.append("Points:");

        for (int i = 0; i < points.size(); i++) {
            if (i != 0) {
                buf.append(",");
            }
            buf.append(points.get(i));
        }
        return buf.toString();
    }
}
