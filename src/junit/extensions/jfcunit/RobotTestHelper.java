package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.AWTException;

/**
 * Class that provides facilities for locating components within a GUI. To use
 * create a new instance of RobotTestHelper in your setUp. Windows can only be
 * located once they have been shown once to the user.
 *
 * After calling the helper.enterClickAndLeave() the JFCTestCase.sleep(long delay)
 * method should be called to prevent subsequent clicks from being treated as a
 * double click. A sleep dalay of 300ms can be used as a guideline.
 *
 * @author Vijay Aravamudhan
 * @author Kevin Wilson
 */
public class RobotTestHelper extends TestHelper {

    /** Robot instance. */
    private static Robot robot;

    /**
     * Construct a new RobotTestHelper
     *
     * @exception AWTException may be thrown when
     *            initializing the Robot.
     */
    public RobotTestHelper() throws AWTException {
        super();
        if (robot == null) {
            robot = new Robot();
        }
    }

    /**
     * Press the modifiers.
     *
     * @param comp      Component
     * @param modifiers Modifiers to be pressed.
     */
    private void pressModifiers(Component comp, int modifiers) {
        if ((modifiers & InputEvent.ALT_GRAPH_MASK) > 0) {
            robot.keyPress(KeyEvent.VK_ALT_GRAPH);
        }
        if ((modifiers & InputEvent.ALT_MASK) > 0) {
            robot.keyPress(KeyEvent.VK_ALT);
        }
        if ((modifiers & InputEvent.META_MASK) > 0) {
            robot.keyPress(KeyEvent.VK_META);
        }
        if ((modifiers & InputEvent.CTRL_MASK) > 0) {
            robot.keyPress(KeyEvent.VK_CONTROL);
        }
        if ((modifiers & InputEvent.SHIFT_MASK) > 0) {
            robot.keyPress(KeyEvent.VK_SHIFT);
        }
    }

    /**
     * Release the modifiers.
     *
     * @param comp      Component
     * @param modifiers Modifiers to be pressed.
     */
    private void releaseModifiers(Component comp, int modifiers) {
        if ((modifiers & InputEvent.SHIFT_MASK) > 0) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }
        if ((modifiers & InputEvent.CTRL_MASK) > 0) {
            robot.keyRelease(KeyEvent.VK_CONTROL);
        }
        if ((modifiers & InputEvent.META_MASK) > 0) {
            robot.keyRelease(KeyEvent.VK_META);
        }
        if ((modifiers & InputEvent.ALT_MASK) > 0) {
            robot.keyRelease(KeyEvent.VK_ALT);
        }
        if ((modifiers & InputEvent.ALT_GRAPH_MASK) > 0) {
            robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
        }
    }

    /**
     * Click on the component.
     * @param comp       Component to be clicked upon.
     * @param x          X screen location.
     * @param y          Y screen location.
     * @param clickCount Number of clicks.
     * @param modifiers  Modifiers to be pressed when clicking.
     */
    private void click(Component comp, int x, int y, int clickCount, int modifiers) {
        // Do a little jitter to insure a move event.
        robot.mouseMove(x - 5, y - 5);
        robot.mouseMove(x, y);
        pressModifiers(comp, modifiers);
        int mouseButtonMask = modifiers & (InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK);
        for (int i = 1; i <= clickCount; i++)  {
            robot.mousePress(mouseButtonMask);
            robot.mouseRelease(mouseButtonMask);
        }
        releaseModifiers(comp, modifiers);
    }

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method can also differentiate between
     * upper and lower case characters in the specified string.
     *
     * @param evtData The event data container.
     * @exception Exception may be thrown.
     */
    public void sendString(StringEventData evtData) throws Exception {
        if (evtData == null || !evtData.prepareComponent()) {
            return;
        }

        int modifiers = evtData.getModifiers();
        Point screen = evtData.getLocationOnScreen();

        pressModifiers(evtData.getComponent(), modifiers);

        char[] chars = evtData.getString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int keyCode = evtData.getCode(chars[i]);

            // Send the shift when appropriate
            if (Character.isUpperCase(chars[i]) && (modifiers & KeyEvent.SHIFT_MASK) == 0) {
                pressModifiers(evtData.getComponent(), InputEvent.SHIFT_MASK);
            }

            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);

            // Release the shift.
            if (Character.isUpperCase(chars[i]) && (modifiers & KeyEvent.SHIFT_MASK) == 0) {
                releaseModifiers(evtData.getComponent(), InputEvent.SHIFT_MASK);
            }
        }
        releaseModifiers(evtData.getComponent(), modifiers);
        evtData.getTestCase().awtSleep(evtData.getSleepTime());
    }

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method should be used only to send Action key events.
     *
     * @param evtData The event data container.
     * @exception Exception may be thrown.
     */
    public void sendKeyAction(KeyEventData evtData) throws Exception {
        if (evtData == null || !evtData.prepareComponent()) {
            return;
        }

        int modifiers = evtData.getModifiers();
        int keyCode = evtData.getKeyCode();
        Point screen = evtData.getComponent().getLocationOnScreen();

        //click(evtData.getComponent(), screen.x, screen.y, 1, InputEvent.BUTTON1_MASK);
        pressModifiers(evtData.getComponent(), modifiers);
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
        releaseModifiers(evtData.getComponent(), InputEvent.SHIFT_MASK);
        evtData.getTestCase().awtSleep(evtData.getSleepTime());
    }

    /**
     * Helper method to fire appropriate events to click(s) a component with a
     * custom wait method
     *
     * @param evtData The event data container
     * @exception Exception may be thrown.
     */
    public void enterClickAndLeave(AbstractMouseEventData evtData) throws Exception {
        if (evtData == null || !evtData.prepareComponent()) {
            return;
        }
        int numberOfClicks = evtData.getNumberOfClicks();
        int modifiers = evtData.getModifiers();
        boolean isPopupTrigger = evtData.getPopupTrigger();

        Component ultimate = evtData.getRoot();

        // Translate the screen coordinates returned by the event to frame coordinates.
        Point screen = evtData.getLocationOnScreen();

        click(evtData.getComponent(), screen.x, screen.y, numberOfClicks, modifiers);
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
     * @exception Exception may be thrown.
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
        Point dstScreen = dstEvtData.getLocationOnScreen();
        if (srcEvtData == null || !srcEvtData.prepareComponent()) {
            return;
        }
        int numberOfClicks = srcEvtData.getNumberOfClicks();
        int modifiers = srcEvtData.getModifiers();
        boolean isPopupTrigger = srcEvtData.getPopupTrigger();
        Point srcScreen = srcEvtData.getLocationOnScreen();

        int xinc = 1;
        int yinc = 1;
        if (incr > 0) {
            xinc = (dstScreen.x - srcScreen.x) / incr;
            yinc = (dstScreen.y - srcScreen.y) / incr;
        }
        if (incr == 0) {
            xinc = dstScreen.x - srcScreen.x;
            yinc = dstScreen.y - srcScreen.y;
        }

        // try to clear the event queue
        //srcEvtData.getTestCase().awtSleep(srcEvtData.getSleepTime());
        drag(srcEvtData.getComponent(), srcScreen, dstScreen, xinc, yinc, modifiers,
            numberOfClicks, isPopupTrigger);
        srcEvtData.getTestCase().awtSleep(srcEvtData.getSleepTime());
    }

    /**
     * Drag from source to destination.
     * @param comp Source component for drag event.
     * @param src  Starting screen location.
     * @param dst  Ending screen location.
     * @param xinc Increments in the X direction between events.
     *             If zero then no intermediate events will be generated.
     * @param yinc Increments in the Y direction between events.
     *             If zero then no intermediate events will be generated.
     * @param modifiers
     *             Modifiers from InputEvent.
     * @param numberOfClicks
     *             Number of clicks to be performed on source
     *             before drag.
     * @param popupTrigger
     *             True if the event is to generate a popup.
     */
    public void drag(Component comp, Point src, Point dst,
        int xinc, int yinc,
        int modifiers,
        int numberOfClicks,
        boolean popupTrigger) {
        robot.mouseMove(src.x - 5, src.y - 5);
        robot.mouseMove(src.x, src.y);
        pressModifiers(comp, modifiers);
        int mouseButtonMask = modifiers & (InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK);
        robot.mousePress(mouseButtonMask);

        for (int i = 2; i <= numberOfClicks; i++)  {
            robot.mouseRelease(mouseButtonMask);
            robot.mousePress(mouseButtonMask);
        }

        int x = src.x;
        int y = src.y;
        while (true) {
            if (x != dst.x) {
                if (xinc > 0) {
                    if (dst.x - x < xinc) {
                        x = dst.x;
                    } else {
                        x = x + xinc;
                    }
                } else {
                    if (dst.x - x > xinc) {
                        x = dst.x;
                    } else {
                        x = x + xinc;
                    }
                }
            }

            if (y != dst.y) {
                if (yinc > 0) {
                    if (dst.y - y < yinc) {
                        y = dst.y;
                    } else {
                        y = y + yinc;
                    }
                } else {
                    if (dst.y - y > yinc) {
                        y = dst.y;
                    } else {
                        y = y + yinc;
                    }
                }
            }

            robot.mouseMove(x, y);
            if (x == dst.x && y == dst.y) {
                break;
            }
        }
        robot.mouseRelease(mouseButtonMask);
        releaseModifiers(comp, modifiers);
    }
}