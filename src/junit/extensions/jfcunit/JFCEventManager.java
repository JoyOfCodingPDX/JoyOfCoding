package junit.extensions.jfcunit;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;

import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

/**
 * This class provides a recording capabilities for AWTEvents.
 * AWTEvents are translated into their coresponding
 * Event Data types. A application may register a listener against
 * this class, to receive the event data.
 *
 * This class temporarily holds events until either a different
 * event type is received or a timer expires on the event.
 *
 * @author Kevin Wilson
 */
public class JFCEventManager implements AWTEventListener, EventDataConstants {

    /** Event Mapping Property */
    static final String EVENT_MAPPING_PROPERTY = "junit.extensions.JFCUnit.eventMapping";

    /** Used to turn on debug info */
    static final String EVENT_DEBUG = "JFCEventManager.debug";

    static {

        if (UIManager.get(EVENT_MAPPING_PROPERTY) == null) {
            ArrayList mapping = new ArrayList();
            mapping.add(new String[] { "junit.extensions.jfcunit.JComboBoxMouseEventData", "javax.swing.JList" });
            mapping.add(new String[] { "junit.extensions.jfcunit.JListMouseEventData", "javax.swing.JList" });
            mapping.add(new String[] { "junit.extensions.jfcunit.JTableMouseEventData", "javax.swing.JTable" });
            mapping.add(new String[] { "junit.extensions.jfcunit.JTableHeaderMouseEventData", "javax.swing.table.JTableHeader" });
            mapping.add(new String[] { "junit.extensions.jfcunit.JTreeMouseEventData", "javax.swing.JTree" });
            mapping.add(new String[] { "junit.extensions.jfcunit.JTabbedPaneMouseEventData", "javax.swing.JTabbedPane" });
            mapping.add(new String[] { "junit.extensions.jfcunit.JTextComponentMouseEventData", "javax.swing.text.JTextComponent" });
            mapping.add(new String[] { "junit.extensions.jfcunit.MouseEventData", "*" });
            mapping.add(new String[] { "junit.extensions.jfcunit.StringEventData", "*" });
            mapping.add(new String[] { "junit.extensions.jfcunit.KeyEventData", "*" });

            UIManager.put(EVENT_MAPPING_PROPERTY, mapping);
        }
    }

    /**
     * This is a singleton class. There should never be more than
     * one recording session.
     */
    private static JFCEventManager singleton = null;

    /** Debug flag set by UIManager property JFCEventManager.debug="true" */
    private static boolean debug = false;

    /**
     * Returns a singleton instance of this class. This
     * method should be used instead of the constructor.
     * to attempt to consolidate events.
     *
     * @return JFCEventManager singleton instance.
     */
    static JFCEventManager getEventManager() {
        return JFCEventManager.getEventManager(DEFAULT_HOLDTIME);
    }

    /**
     * Returns a singleton instance of this class. This
     * method should be used instead of the constructor.
     *
     * @param _holdTime druration a event should be held
     *                  to attempt to consolidate events.
     *
     * @return JFCEventManager singleton instance.
     */
    static JFCEventManager getEventManager(long _holdTime) {
        if (singleton == null) {
            singleton = new JFCEventManager(_holdTime);
        }
        return singleton;
    }

    /** Recording state */
    private boolean recording = false;

    /** Pending event held for consolidation. */
    private AbstractEventData pendingEvent = null;

    /** Timer thread used to send the pending event. */
    private Thread timerThread;

    /** Time when the last event was recorded. */
    private volatile long lastEventTime = 0;

    /**
     * Hold time for the timer thread before
     * firing the  pending event.
     */
    private long holdTime;

    /** Listener list */
    private EventListenerList listenerList = new EventListenerList();

    /**
     * Private constructor.
     * The method getEventManager() should be used instead.
     * @param _holdTime duration to hold a pending event.
     */
    private JFCEventManager(long _holdTime) {
        setHoldTime(_holdTime);
    }

    /**
     * Set the maximum hold time for a event.
     * @param _holdTime maximum duration in millis to
     *  hold a event.
     */
    public final void setHoldTime(long _holdTime) {
        holdTime = _holdTime;
    }

    /**
     * Get the maximum hold time for a event.
     * @return long maximum hold time.
     */
    public final long getHoldTime() {
        return holdTime;
    }

    /**
     * Set the recording state.
     *
     * @param _recording true if recording is to be enabled.
     * otherwise false.
     */
    public final void setRecording(boolean _recording) {
        if (_recording && !(this.recording)) {
            Boolean value = (Boolean) UIManager.get(EVENT_DEBUG);
            if (value == null) {
                debug = true;
            } else {
                debug = value.booleanValue();
            }

            Toolkit.getDefaultToolkit().addAWTEventListener(
                this,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK);
            timerThread = new Thread(new Runnable() {
                public void run() {
                    while (recording) {
                        try {
                            Thread.currentThread().sleep(holdTime);
                        } catch (InterruptedException ie) {
                        }
                        synchronized (JFCEventManager.this) {
                            long time = System.currentTimeMillis();
                            if (lastEventTime == 0) {
                                lastEventTime = System.currentTimeMillis();
                            }

                            if (pendingEvent != null && time - lastEventTime > holdTime) {
                                fireEventData();
                            }
                        }
                    }
                    if (pendingEvent != null) {
                        fireEventData();
                    }
                }
            });

            timerThread.start();
            this.recording = _recording;

        } else if (!_recording && (this.recording)) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
            this.recording = _recording;
            timerThread.interrupt();
            try {
                timerThread.join();
            } catch (InterruptedException ie) {
            }
        }
        this.recording = _recording;
    }

    /**
     * Get the current recording state.
     *
     * @return boolean recording state. True if recording is enabled.
     * Otherwise, false.
     */
    public final boolean getRecording() {
        return recording;
    }

    /**
     * Add a listener.
     *
     * @param _jl Listener to be added.
     */
    public final void addJFCEventDataListener(JFCEventDataListener _jl) {
        listenerList.add(JFCEventDataListener.class, _jl);
    }

    /**
     * Remove a listener
     *
     * @param _jl Listener to be removed.
     */
    public final void removeJFCEventDataListener(JFCEventDataListener _jl) {
        listenerList.remove(JFCEventDataListener.class, _jl);
    }

    /**
     * Remove all listeners
     */
    public final void removeAllJFCEventDataListeners() {
        Object listeners[] = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == JFCEventDataListener.class) {
                listenerList.remove(JFCEventDataListener.class, (EventListener) listeners[i + 1]);
            }
        }
    }

    /**
     * Fire event data to the listeners.
     */
    protected final void fireEventData() {
        if (pendingEvent != null && pendingEvent.isValid()) {
            if (debug) {
                System.err.println("JFCEventManager.outputEvent:" + pendingEvent);
            }
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == JFCEventDataListener.class) {
                    ((JFCEventDataListener) listeners[i + 1]).handleEvent(pendingEvent);
                }
            }
        }
        pendingEvent = null;
    }

    /**
     * This method implements the AWTEventListener interface.
     * This method will be accessed for every AWTEvent which is
     * of the type: MOUSE_MOTION_EVENT, MOUSE_EVENT, KEY_EVENT, or
     * TEXT_EVENT.
     *
     * @param _ae Event to be processed.
     */
    public void eventDispatched(AWTEvent _ae) {
        processEventData(_ae);
    }

    /**
     * This method converts the AWTEvent to the corresponding
     * AbstractEventData.
     *
     * @param _ae AWTEvent to be processed.
     */
    protected synchronized void processEventData(AWTEvent _ae) {
        if (debug) {
            System.err.println("JFCEventManager.inputEvent:" + ((InputEvent) _ae).getWhen() + " " + _ae);
        }
        lastEventTime = System.currentTimeMillis();
        if (_ae instanceof MouseEvent && ((MouseEvent) _ae).getID() == MouseEvent.MOUSE_MOVED) {
            fireEventData();
            return;
        }

        if (pendingEvent != null && !pendingEvent.canConsume(_ae)) {
            if (!convertDrag(_ae)) {
                fireEventData();
            }
        }
        if (pendingEvent == null) {
            pendingEvent = createEvent(_ae);
        }
        if (pendingEvent != null) {
            pendingEvent.consume(_ae);
            lastEventTime = 0;
        }
    }

    /**
     * Converts the event to a drag event if necessary.
     * @param ae Event to be processed.
     * @return true if converted to drag event.
     */
    public boolean convertDrag(AWTEvent ae) {
        if (!(pendingEvent instanceof AbstractMouseEventData) || !(ae instanceof MouseEvent)) {
            return false;
        }
        MouseEvent me = (MouseEvent) ae;
        if (me.getID() != MouseEvent.MOUSE_DRAGGED) {
            return false;
        }
        pendingEvent = new DragEventData((JFCTestCase) null, (AbstractMouseEventData) pendingEvent);
        return true;
    }

    /**
     * Create a event for the data given.
     * @param ae Event to be processed.
     * @return true if the event is created.
     */
    public AbstractEventData createEvent(AWTEvent ae) {
        Object source = ae.getSource();
        String className = source.getClass().getName();

        ArrayList mapping = (ArrayList) UIManager.get(EVENT_MAPPING_PROPERTY);
        Iterator iter = mapping.iterator();
        while (iter.hasNext()) {
            String[] map = (String[]) iter.next();
            Class target = null;
            try {
                if (!map[1].equals("*")) {
                    target = Class.forName(map[1]);
                } else {
                    target = Object.class;
                }
            } catch (Exception e) {
            }

            if (target.isInstance(source)) {
                // Attempt to consume event.
                try {
                    if (debug) {
                        System.err.println("JFCEventManager.createEvent check class:" + map[0] + " can handle " + ae);
                    }
                    Class cls = Class.forName(map[0]);
                    AbstractEventData data = (AbstractEventData) cls.newInstance();
                    if (data.canConsume(ae)) {
                        return data;
                    }
                } catch (Exception e) {
                    System.err.println("Exception attempting to create instnce for:" + map[0]);
                    e.printStackTrace();
                }
            }
        }
        if (debug) {
            System.err.println("JFCEventManager.createEvent No EventData structure for:" + ae);
        }
        return null;
    }
}