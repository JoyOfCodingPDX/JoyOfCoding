package junit.extensions.jfcunit;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Class that provides facilities for locating components within a GUI. To use
 * create a new instance of JFCTestHelper in your setUp. Windows can only be
 * located once they have been shown once to the user.
 *
 * @author Matt Caswell
 * @author Vijay Aravamudhan
 */
public class JFCTestHelper extends TestHelper {
    /**
     * Construct a new JFCTestHelper
     */
    public JFCTestHelper() {
        super();
    }

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method can also differentiate between
     * upper and lower case characters in the specified string.
     *
     * @param evtData The event data container.
     * @exception Exception may be thrown when processing Source or Destination
     *                   prepareComponent()
     */
    public void sendString(StringEventData evtData) throws Exception  {
        if (evtData == null || !evtData.prepareComponent()) {
            return;
        }

        Component ultimate = evtData.getRoot();
        EventQueue compQueue = ultimate.getToolkit().getSystemEventQueue();
        int modifiers = evtData.getModifiers();

        char[] chars = evtData.getString().toCharArray();
        pressModifiers(compQueue, ultimate, modifiers);
        for (int i = 0; i < chars.length; i++) {

            int keyCode = evtData.getCode(chars[i]);

            // Send the shift when appropriate
            if (Character.isUpperCase(chars[i]) && (modifiers & KeyEvent.SHIFT_MASK) == 0) {
                pressModifiers(compQueue, ultimate, KeyEvent.SHIFT_MASK);
            }

            long t = System.currentTimeMillis();
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_PRESSED, t, modifiers, keyCode, chars[i]));

            if (evtData.isTypedChar(keyCode)) {
                postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_TYPED, t, modifiers, KeyEvent.VK_UNDEFINED, chars[i]));
            }

            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_RELEASED, t, modifiers, keyCode, chars[i]));

            // Release the shift.
            if (Character.isUpperCase(chars[i]) && (modifiers & KeyEvent.SHIFT_MASK) == 0) {
                releaseModifiers(compQueue, ultimate, KeyEvent.SHIFT_MASK);
            }

        }
        releaseModifiers(compQueue, ultimate, modifiers);
        evtData.getTestCase().awtSleep(evtData.getSleepTime());
    }

    /**
     * Send the modifier key press event for the modifiers.
     * @param compQueue Queue to post the events on.
     * @param ultimate  Component to receive the events.
     * @param modifiers The modifiers to be pressed.
     */
    private void pressModifiers(EventQueue compQueue, Component ultimate, int modifiers) {
        long t = System.currentTimeMillis();
        int mods = 0;

        if ((modifiers & InputEvent.ALT_GRAPH_MASK) > 0) {
            mods = mods | InputEvent.ALT_GRAPH_MASK;
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_PRESSED, t, mods, KeyEvent.VK_ALT_GRAPH));
        }
        if ((modifiers & InputEvent.ALT_MASK) > 0) {
            mods = mods | InputEvent.ALT_MASK;
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_PRESSED, t, mods, KeyEvent.VK_ALT));
        }
        if ((modifiers & InputEvent.META_MASK) > 0) {
            mods = mods | InputEvent.META_MASK;
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_PRESSED, t, mods, KeyEvent.VK_META));
        }
        if ((modifiers & InputEvent.CTRL_MASK) > 0) {
            mods = mods | InputEvent.CTRL_MASK;
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_PRESSED, t, mods, KeyEvent.VK_CONTROL));
        }
        if ((modifiers & InputEvent.SHIFT_MASK) > 0) {
            mods = mods | InputEvent.SHIFT_MASK;
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_PRESSED, t, mods, KeyEvent.VK_SHIFT));
        }
    }

    /**
     * Send the modifier key release event for the modifiers.
     * @param compQueue Queue to post the events on.
     * @param ultimate  Component to receive the events.
     * @param modifiers The modifiers to be released.
     */
    private void releaseModifiers(EventQueue compQueue, Component ultimate, int modifiers) {
        long t = System.currentTimeMillis();
        int mods = modifiers;

        if ((modifiers & InputEvent.SHIFT_MASK) > 0) {
            mods = mods & (-1 ^ InputEvent.SHIFT_MASK);
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_RELEASED, t, mods, KeyEvent.VK_SHIFT));
        }
        if ((modifiers & InputEvent.CTRL_MASK) > 0) {
            mods = mods & (-1 ^ InputEvent.CTRL_MASK);
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_RELEASED, t, mods, KeyEvent.VK_CONTROL));
        }
        if ((modifiers & InputEvent.META_MASK) > 0) {
            mods = mods & (-1 ^ InputEvent.META_MASK);
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_RELEASED, t, mods, KeyEvent.VK_META));
        }
        if ((modifiers & InputEvent.ALT_MASK) > 0) {
            mods = mods & (-1 ^ InputEvent.ALT_MASK);
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_RELEASED, t, mods, KeyEvent.VK_ALT));
        }
        if ((modifiers & InputEvent.ALT_GRAPH_MASK) > 0) {
            mods = mods & (-1 ^ InputEvent.ALT_GRAPH_MASK);
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_RELEASED, t, mods, KeyEvent.VK_ALT_GRAPH));
        }
    }

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method should be used only to send Action key events.
     *
     * @param evtData  The test case that is firing these events
     * @exception Exception may be thrown when processing Source or Destination
     *                   prepareComponent()
     */
    public void sendKeyAction(KeyEventData evtData) throws Exception  {
        if (evtData == null || !evtData.prepareComponent()) {
            return;
        }

        Component ultimate = evtData.getRoot();
        EventQueue compQueue = ultimate.getToolkit().getSystemEventQueue();
        int modifiers = evtData.getModifiers();
        int keyCode = evtData.getKeyCode();
        long t = System.currentTimeMillis();
        pressModifiers(compQueue, ultimate, modifiers);

        postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_PRESSED, t, modifiers, keyCode, KeyEvent.CHAR_UNDEFINED));

        if (evtData.isTypedChar(keyCode)) {
            postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_TYPED, t, modifiers, KeyEvent.VK_UNDEFINED, (char) keyCode));
        }

        postEvent(compQueue, new KeyEvent(ultimate, KeyEvent.KEY_RELEASED, t, modifiers, keyCode, KeyEvent.CHAR_UNDEFINED));

        releaseModifiers(compQueue, ultimate, modifiers);
        evtData.getTestCase().awtSleep(evtData.getSleepTime());
    }

    /**
     * This method is just present so as to put debug statements in one central place,
     * without repeating everywhere.
     *
     * @param queue  The EventQueue on which to post the event.
     * @param evt    The event to be posted.
     */
    private void postEvent(EventQueue queue, AWTEvent evt) {
        queue.postEvent(evt);
    }

    /**
     * Helper method to fire appropriate events to click(s) a component with a
     * custom wait method
     *
     * @param evtData The event data container
     * @exception Exception may be thrown when processing Source or Destination
     *                   prepareComponent()
     */
    public void enterClickAndLeave(AbstractMouseEventData evtData) throws Exception {
        if (evtData == null || !evtData.prepareComponent()) {
            return;
        }
        int numberOfClicks = evtData.getNumberOfClicks();
        int modifiers = evtData.getModifiers();
        boolean isPopupTrigger = evtData.getPopupTrigger();

        Component ultimate = evtData.getRoot();
        EventQueue compQueue = ultimate.getToolkit().getSystemEventQueue();

        // Translate the screen coordinates returned by the event to frame coordinates.
        int x = evtData.getLocationOnScreen().x - ultimate.getLocationOnScreen().x;
        int y = evtData.getLocationOnScreen().y - ultimate.getLocationOnScreen().y;

        // try to clear the event queue
        evtData.getTestCase().awtSleep(evtData.getSleepTime());

        postEvent(compQueue,
                  new MouseEvent(ultimate, MouseEvent.MOUSE_ENTERED,
                                 System.currentTimeMillis(), modifiers,
                                 x, y, 0, isPopupTrigger));

        postEvent(compQueue,
                  new MouseEvent(ultimate, MouseEvent.MOUSE_MOVED,
                                 System.currentTimeMillis(), modifiers,
                                 x, y, 0, isPopupTrigger));

        for (int click = 1; click <= numberOfClicks; click++) {
            postEvent(compQueue,
                      new MouseEvent(ultimate, MouseEvent.MOUSE_PRESSED,
                                     System.currentTimeMillis(), modifiers,
                                     x, y, click, isPopupTrigger));

            postEvent(compQueue,
                      new MouseEvent(ultimate, MouseEvent.MOUSE_RELEASED,
                                     System.currentTimeMillis(), modifiers,
                                     x, y, click, isPopupTrigger));

            postEvent(compQueue,
                      new MouseEvent(ultimate, MouseEvent.MOUSE_CLICKED,
                                     System.currentTimeMillis(), modifiers,
                                     x, y, click, isPopupTrigger));
        }

        postEvent(compQueue,
                  new MouseEvent(ultimate, MouseEvent.MOUSE_EXITED,
                                 System.currentTimeMillis(), modifiers,
                                 x, y, 0, isPopupTrigger));

        // try to clear the event queue
        evtData.getTestCase().awtSleep(evtData.getSleepTime());
    }

    /**
     * Helper method to fire appropriate events to drag and drop a component with a
     * custom wait method
     *
     * @param srcEvtData The source  event data container
     * @param dstEvtData The destination event data container
     * @param incr       Amount to increment the coords while "moving" from
     *                   source to destination.
     * @exception Exception may be thrown when processing Source or Destination
     *                   prepareComponent()
     */
    public void enterDragAndLeave(AbstractMouseEventData srcEvtData, AbstractMouseEventData dstEvtData, int incr) throws Exception {
        // Is component 2 to be visible from the get go.
        // It may not be if autoScrolling is to take place.
        // If autoScrolling then when do we adjust the screen?
        // How do we calculate the target X/Y?

        // For now we will prepare the second component and
        // attempt to insure visibility
        if (dstEvtData == null || !dstEvtData.prepareComponent()) {
            return;
        }
        Component ultimate2 = dstEvtData.getRoot();
        Rectangle rect2 = ultimate2.getBounds();
        int x2 = dstEvtData.getLocationOnScreen().x - ultimate2.getLocationOnScreen().x;
        int y2 = dstEvtData.getLocationOnScreen().y - ultimate2.getLocationOnScreen().y;

        if (srcEvtData == null || !srcEvtData.prepareComponent()) {
            return;
        }
        int numberOfClicks = srcEvtData.getNumberOfClicks();
        int modifiers = srcEvtData.getModifiers();
        boolean isPopupTrigger = srcEvtData.getPopupTrigger();

        Component ultimate = srcEvtData.getRoot();
        Rectangle rect = ultimate.getBounds();
        int x = srcEvtData.getLocationOnScreen().x - ultimate.getLocationOnScreen().x;
        int y = srcEvtData.getLocationOnScreen().y - ultimate.getLocationOnScreen().y;

        int xinc = 1;
        int yinc = 1;
        if (incr > 0) {
            xinc = (x2 - x) / incr;
            yinc = (y2 - y) / incr;
        }
        if (incr == 0) {
            xinc = x2 - x;
            yinc = y2 - y;
        }

        EventQueue compQueue = ultimate.getToolkit().getSystemEventQueue();

        // try to clear the event queue
        srcEvtData.getTestCase().awtSleep(srcEvtData.getSleepTime());

        postEvent(compQueue,
                  new MouseEvent(ultimate, MouseEvent.MOUSE_ENTERED,
                                 System.currentTimeMillis(), modifiers,
                                 x, y, 0, isPopupTrigger));

        postEvent(compQueue,
                  new MouseEvent(ultimate, MouseEvent.MOUSE_MOVED,
                                 System.currentTimeMillis(), modifiers,
                                 x, y, 0, isPopupTrigger));

        postEvent(compQueue,
                  new MouseEvent(ultimate, MouseEvent.MOUSE_PRESSED,
                                 System.currentTimeMillis(), modifiers,
                                 x, y, numberOfClicks, isPopupTrigger));

        while (true) {
            if (x != x2) {
                if (xinc > 0) {
                    if (x2 - x < xinc) {
                        x = x2;
                    } else {
                        x = x + xinc;
                    }
                } else {
                    if (x2 - x > xinc) {
                        x = x2;
                    } else {
                        x = x + xinc;
                    }
                }
            }

            if (y != y2) {
                if (yinc > 0) {
                    if (y2 - y < yinc) {
                        y = y2;
                    } else {
                        y = y + yinc;
                    }
                } else {
                    if (y2 - y > yinc) {
                        y = y2;
                    } else {
                        y = y + yinc;
                    }
                }
            }

            if (rect == null && rect2.contains(new Point(x, y))) {
                // Fire Entered on component2
                rect = rect2;
                ultimate = ultimate2;
                dstEvtData.getTestCase().awtSleep(dstEvtData.getSleepTime());
            }
            if (rect != null && !rect.contains(new Point(x, y))) {
                // Fire Exit event on first component.
                srcEvtData.getTestCase().awtSleep(srcEvtData.getSleepTime());
                rect = null;
                ultimate = null;
            }

            if (ultimate == null) {
                continue;
            }

            postEvent(compQueue,
                      new MouseEvent(ultimate, MouseEvent.MOUSE_DRAGGED,
                                     System.currentTimeMillis(), modifiers,
                                     x, y, numberOfClicks, isPopupTrigger));
            if (x == x2 && y == y2) {
                break;
            }
        }

        postEvent(compQueue,
                  new MouseEvent(ultimate2, MouseEvent.MOUSE_RELEASED,
                                 System.currentTimeMillis(), modifiers,
                                 x2, y2, numberOfClicks, isPopupTrigger));

        postEvent(compQueue,
                  new MouseEvent(ultimate2, MouseEvent.MOUSE_CLICKED,
                                 System.currentTimeMillis(), modifiers,
                                 x2, y2, numberOfClicks, isPopupTrigger));

        postEvent(compQueue,
                  new MouseEvent(ultimate2, MouseEvent.MOUSE_EXITED,
                                 System.currentTimeMillis(), modifiers,
                                 x2, y2, 0, isPopupTrigger));

        // try to clear the event queue
        srcEvtData.getTestCase().awtSleep(srcEvtData.getSleepTime());
    }
}