package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * A helper class that provides facilities for locating and posting events to components
 * within a GUI. To use, create a new instance of TestHelper in your setUp.
 * Caveat: Windows can only be located once they have been shown once to the user.
 *
 * @author Matt Caswell
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public abstract class TestHelper {
    /**
     * The length of time that the test class will sleep for between each
     * event being posted.
     */
    public static final long DEFAULTSLEEP = 300L;

    /**
     * This method checks whether the title and the titlematch strings are equal.
     * Note: If the titlematch string is null, we need to return all showing windows,
     * so 'true' is returned.
     *
     * @param title      The title of the frame/dialog being checked
     * @param titlematch The filter (can be a sub-string)
     * @return True if either the filter string is null or if the
     *         filter string is a substring of the window title
     */
    private boolean checkIfTitleMatches(String title, String titlematch) {
        return ((titlematch == null) || (title != null && title.indexOf(titlematch) != -1));
    }

    /**
     * This method is used to clean up any existing open windows, etc
     * if a test fails midway. To be called in the tearDown() method.
     *
     * @param testCase The test case that is firing the event.
     */
    public void cleanUp(JFCTestCase testCase) {
        cleanUp(testCase, DEFAULTSLEEP);
    }

    /**
     * This method is used to clean up any existing open windows, etc
     * if a test fails midway. To be called in the tearDown() method.
     *
     * @param testCase The test case that is firing the event.
     * @param awtSleepTime
     *                 The wait time in ms between each event.
     */
    public void cleanUp(JFCTestCase testCase, long awtSleepTime) {
        Iterator iter = getWindows().iterator();
        while (iter.hasNext()) {
            disposeWindow((Window) iter.next(), testCase, awtSleepTime);
        }
    }

    /**
     * This method does the actual work of destroying the window.
     *
     * @param window   The window that needs to be destroyed.
     * @param testCase The test case that is firing the event.
     */
    public void disposeWindow(Window window, JFCTestCase testCase) {
        disposeWindow(window, testCase, DEFAULTSLEEP);
    }

    /**
     * This method does the actual work of destroying the window.
     *
     * @param window   The window that needs to be destroyed.
     * @param testCase The test case that is firing the event.
     * @param awtSleepTime
     *                 The wait time in ms between each event.
     */
    public void disposeWindow(Window window, JFCTestCase testCase, long awtSleepTime) {
        if (window != null) {
            if (!(window instanceof JDialog)) {
                // dispose of any showing dialogs first
                List dialogs = getShowingDialogs(window);
                for (int i = 0; i < dialogs.size(); i++) {
                    disposeWindow((JDialog) dialogs.get(i), testCase, awtSleepTime);
                }
            }
            window.setVisible(false);
            window.getToolkit().getSystemEventQueue().postEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            testCase.awtSleep(awtSleepTime);
        }
    }

    /**
     * A helper method to find the message being displayed in a dialog box.
     *
     * @param dialog The dialog box displaying the message
     * @return The messagebeing displayed
     */
    public String getMessageFromJDialog(JDialog dialog) {
        if (dialog == null || !dialog.isShowing()) {
            return null;
        }
        // since there are usually 2 JLabels, use a sufficiently high number ---> 10
        List list = findComponentList(new ComponentFinder(JLabel.class), dialog, new ArrayList(), 10);
        JLabel label;
        for (int i = 0; i < list.size(); i++) {
            label = (JLabel) list.get(i);
            if (label != null && label.getText() != null) {
                return label.getText();
            }
        }
        return null;
    }

    /**
     * Returns a single window that is showing with given string in the title.
     * If there is more than one window that match, then it is undefined which
     * one will be returned.
     *
     * @param title  The string that should be present in the title.
     * @return A showing window with the given title.
     * @exception JFCTestException
     *                   Thrown if a window cannot be found.
     */
    public Window getWindow(String title) throws JFCTestException {
        Iterator iter = getWindows(title).iterator();

        if (!iter.hasNext()) {
            throw new JFCTestException("Window with title '" + title + "' not found");
        }
        return (Window) iter.next();
    }

    /**
     * Returns a set of all the Windows that are currently visible.
     *
     * @return Set of Window objects.
     */
    public Set getWindows() {
        return getWindows(null);
    }

    /**
     * Returns a set of all the Windows that are currently visible and the title
     * contains the given titlematch string.
     *
     * @param titlematch The string to search for in the window's title.
     * @return Set of Window objects.
     */
    public Set getWindows(String titlematch) {
        return getWindows(new HashSet(), Frame.getFrames(), titlematch);
    }

    /**
     * Returns a set of all the Windows that are currently visible and the title
     * contains the given titlematch string.
     *
     * @param ret        The list of already filtered and accepted windows.
     * @param windows    The array of windows to filter and add.
     * @param titlematch The string to search for in the window's title.
     * @return Set of Window objects.
     */
    public Set getWindows(Set ret, Window[] windows, String titlematch) {
        return getWindows(ret, windows, new FrameFinder(titlematch));
    }

    /**
     * Returns a set of all the Windows that are currently visible and the title
     * contains the given titlematch string.
     *
     * @param ret     The list of already filtered and accepted windows.
     * @param windows The array of windows to filter and add.
     * @param finder  The FrameFinder which is used to filter using a title match
     * @return Set of Window objects.
     */
    public Set getWindows(Set ret, Window[] windows, Finder finder) {
        if (ret == null) {
            ret = new HashSet();
        }
        if (windows == null || finder == null) {
            return ret;
        }

        Frame frame;
        for (int i = 0; i < windows.length; i++) {
            if (!finder.testComponent(windows[i])) {
                continue;
            }

            frame = (Frame) windows[i];

            // if this test is run through any of the TestRunner classes which
            // brings up a GUI window, we need to skip that frame
            if ("JUnit".equalsIgnoreCase(frame.getTitle())) {
                continue;
            }

            if (!ret.contains(frame)) {
                ret.add(frame);
            }

            // add any windows owned by the current 'frame' object
            getWindows(ret, frame.getOwnedWindows(), finder);
        }
        return ret;
    }

    /**
     * Returns a set of all the Dialogs that are currently visible for all
     * opened, visible windows - including the special case of the shared, hidden
     * frame which is the owner of JDialogs whose owner is not specified.
     *
     * @return The full list of dialogs.
     */
    public List getShowingDialogs() {
        return getShowingDialogs((String) null);
    }

    /**
     * Helper method to obtain a list of the dialogs owned by a given window.
     *
     * @param win    The window to find dialogs for.
     * @return The list of dialogs owned by the given window (including the window if it is a Dialog itself).
     */
    public List getShowingDialogs(Window win) {
        return getShowingDialogs(win, null);
    }

    /**
     * Returns a set of all the Dialogs that are currently visible and the title
     * contains the given titlematch string - including the special case of the
     * shared, hidden frame which is the owner of JDialogs whose owner is not specified.
     *
     * @param titlematch The string to search for in the dialog's title.
     * @return The list of dialogs owned by the given window (including the window if it is a Dialog itself).
     */
    public List getShowingDialogs(String titlematch) {
        Set openWindows = getWindows();
        // We also have to add the shared, hidden frame used by JDialogs which did not
        // have an owner at the beginning
        openWindows.add((new JDialog()).getOwner());
        Window[] owners = new Window[openWindows.size() + 1];
        owners = (Window[]) openWindows.toArray(owners);
        return getShowingDialogs(owners, titlematch);
    }

    /**
     * Returns a set of all the Dialogs that are currently visible and the title
     * contains the given titlematch string.
     *
     * @param win        The window to find dialogs for.
     * @param titlematch The string to search for in the dialog's title.
     * @return The list of dialogs owned by the given window (including the window if it is a Dialog itself).
     */
    public List getShowingDialogs(Window win, String titlematch) {
        return getShowingDialogs(new Window[] { win }, titlematch);
    }

    /**
     * Returns a set of all the Dialogs that are currently visible and the title
     * contains the given titlematch string - including the special case of the
     * shared, hidden frame which is the owner of JDialogs whose owner is not specified.
     *
     * @param owners     The array of windows to find dialogs for.
     * @param titlematch The string to search for in the dialog's title.
     * @return The list of dialogs owned by the given window (including the window if it is a Dialog itself).
     */
    public List getShowingDialogs(Window[] owners, String titlematch) {
        return getShowingDialogs(new ArrayList(), owners, titlematch);
    }

    /**
     * Returns a set of all the Dialogs that are currently visible and the title
     * contains the given titlematch string - including the special case of the
     * shared, hidden frame which is the owner of JDialogs whose owner is not specified.
     *
     * @param ret        The list of already filtered and accepted dialogs.
     * @param owners     The array of windows to find dialogs for.
     * @param titlematch The string to search for in the dialog's title.
     * @return The list of dialogs owned by the given window (including the window if it is a Dialog itself).
     */
    public List getShowingDialogs(List ret, Window[] owners, String titlematch) {
        return getShowingDialogs(ret, owners, new DialogFinder(titlematch));
    }

    /**
     * Returns a set of all the Dialogs that are currently visible and the title
     * contains the given titlematch string - including the special case of the
     * shared, hidden frame which is the owner of JDialogs whose owner is not specified.
     *
     * @param ret        The list of already filtered and accepted dialogs.
     * @param owners     The array of windows to find dialogs for.
     * @param finder     The DialogFinder which is used to filter using a title match
     * @return The list of dialogs owned by the given window (including the window if it is a Dialog itself).
     */
    public List getShowingDialogs(List ret, Window[] owners, Finder finder) {
        if (ret == null) {
            ret = new ArrayList();
        }
        if (owners == null || finder == null) {
            return ret;
        }

        Window[] owned = null;
        for (int i = 0; i < owners.length; i++) {
            if (owners[i] == null) {
                continue;
            }

            if (finder.testComponent(owners[i]) && !ret.contains(owners[i])) {
                ret.add(owners[i]);
            }

            getShowingDialogs(ret, owners[i].getOwnedWindows(), finder);
        }

        return ret;
    }

    /**
     * Finds a JFileChooser being shown by a window.
     *
     * @param win    The window to owning the chooser.
     * @return The chooser that has been found, or null if there are none.
     */
    public JFileChooser getShowingJFileChooser(Window win) {
        List choosers = getShowingJFileChoosers(win);
        return (choosers.isEmpty() ? null : (JFileChooser) choosers.get(0));
    }

    /**
     * Finds a list of all the JFileChoosers being shown by a window.
     *
     * @param win    The window to owning the chooser.
     * @return The list of JFileChoosers.
     */
    public List getShowingJFileChoosers(Window win) {
        List ret = new ArrayList();
        List subComps;
        JFileChooser chooser;

        Window[] owned = win.getOwnedWindows();
        Finder finder = new ComponentFinder(JFileChooser.class);
        for (int i = 0; i < owned.length; i++) {
            if (!(owned[i] instanceof JDialog)) {
                continue;
            }

            subComps = findComponentList(finder, owned[i], new ArrayList(), 0);
            Iterator iter = subComps.iterator();
            while (iter.hasNext()) {
                chooser = (JFileChooser) iter.next();
                if (finder.testComponent(chooser)) {
                    ret.add(chooser);
                }
            }
        }

        return ret;
    }

    /**
     * Finds a component which is an instance of the given class within a given
     * container.
     *
     * @param compCls The class of the component.
     * @param cont    The container that the component will be in.
     * @param index   The index of the component. The first component matching the
     *                criteria will have index 0, the second 1, etc.
     * @return The component that has been found (or null if none found).
     */
    public Component findComponent(Class compCls, Container cont, int index) {
        return findComponent(new ComponentFinder(compCls), cont, index);
    }

    /**
     * Finds a component matching the given criteria within a given container
     *
     * @param name   The component's name.
     * @param cont   The container that the component will be in.
     * @param index  The index of the component. The first component matching the
     *               criteria will have index 0, the second 1, etc.
     * @return The component that has been found (or null if none found).
     */
    public Component findNamedComponent(String name, Container cont, int index) {
        return findNamedComponent(null, name, cont, index);
    }

    /**
     * Finds a component matching the given criteria within a given container.
     *
     * @param aClass The component's class.
     * @param cont   The container that the component will be in.
     * @param index  The index of the component. The first component matching the
     *               criteria will have index 0, the second 1, etc.
     * @return The component that has been found (or null if none found).
     */
    public Component findNamedComponent(Class aClass, Container cont, int index) {
        return findNamedComponent(aClass, null, cont, index);
    }

    /**
     * Finds a component matching the given criteria within a given container.
     *
     * @param aClass The component's class.
     * @param name   The component's name.
     * @param cont   The container that the component will be in.
     * @param index  The index of the component. The first component matching the
     *               criteria will have index 0, the second 1, etc.
     * @return The component that has been found (or null if none found).
     */
    public Component findNamedComponent(Class aClass, String name, Container cont, int index) {
        return findComponent(new NamedComponentFinder(aClass, name), cont, index);
    }

    /**
     * Finds a component matching the given criteria within a given container.
     *
     * @param finder The Finder that is used to test components for a
     *               match.
     * @param cont   The container that the component will be in.
     * @param index  The index of the component. The first component matching the
     *               criteria will have index 0, the second 1, etc.
     * @return The component that has been found (or null if none found).
     */
    public Component findComponent(Finder finder, Container cont, int index) {
        List ret = findComponentList(finder, cont, new ArrayList(), index);
        //for (int i=0;i<ret.size();i++) System.err.println("CompList["+i+"]="+ret.get(i));
        return ((ret.size() > index) ? (Component) ret.get(index) : null);
    }

    /**
     * Method that calls itself repetitively to build up a list of all components
     * in the container instance that is passed in.
     *
     * @param finder  An instance of the finder which implements the testComponent()
     *                method.
     * @param cont    The Container inside which the component is to be found.
     * @param outList The return list.
     * @param index   The index of the component. The first component matching the
     *                criteria will have index 0, the second 1, etc.
     * @return The component that has been found (or null if none found).
     */
    private List findComponentList(Finder finder, Container cont, List outList, int index) {
        if (outList == null) {
            outList = new ArrayList();
        }
        if (cont == null || finder == null) {
            return outList;
        }

        Component children[] = cont.getComponents();

        for (int i = 0; i < children.length; i++) {
            if (finder.testComponent(children[i])) {
                if (!outList.contains(children[i])) {
                    outList.add(children[i]);
                }
                if (outList.size() > index) {
                    return outList;
                }
            } else if (children[i] instanceof Container) {
                findComponentList(finder, (Container) children[i], outList, index);
                if (outList.size() > index) {
                    return outList;
                }
            }
        }

        return outList;
    }

    /**
     * Helper method to fire appropriate events to click(s) a component with a
     * custom wait method.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param testCase The test case that is firing these events.
     * @param comboBox The JComboBox that is being clicked.
     * @param element  The element that is to be selected.
     * @param numberOfClicks
     *                 Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @exception Exception may be thrown.
     */
    public void enterDropDownClickAndLeave(JFCTestCase testCase, JComboBox comboBox, Object element, int numberOfClicks) throws Exception {
        enterDropDownClickAndLeave(testCase, comboBox, element, numberOfClicks, DEFAULTSLEEP);
    }

    /**
     * Helper method to fire appropriate events to click(s) a component with a
     * custom wait method.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param testCase The test case that is firing these events.
     * @param comboBox The JComboBox that is being clicked.
     * @param element  The element that is to be selected.
     * @param numberOfClicks
     *                 Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param awtSleepTime
     *                 The wait time in ms between each event.
     * @exception Exception may be thrown.
     */
    public void enterDropDownClickAndLeave(JFCTestCase testCase, JComboBox comboBox, Object element, int numberOfClicks, long awtSleepTime) throws Exception {
        int elementIndex = -1;
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i) != null && comboBox.getItemAt(i).equals(element)) {
                elementIndex = i;
                break;
            }
        }
        enterClickAndLeave(new JComboBoxMouseEventData(testCase, comboBox, elementIndex, numberOfClicks, awtSleepTime));
    }

    /**
     * Helper method to fire appropriate events to click(s) a component with a
     * custom wait method.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param testCase The test case that is firing these events.
     * @param list     The JList that is being clicked.
     * @param element  The element that is to be selected.
     * @param numberOfClicks
     *                 Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @exception Exception may be thrown.
     */
    public void enterJListClickAndLeave(JFCTestCase testCase, JList list, Object element, int numberOfClicks) throws Exception {
        enterJListClickAndLeave(testCase, list, element, numberOfClicks, DEFAULTSLEEP);
    }

    /**
     * Helper method to fire appropriate events to click(s) a component with a
     * custom wait method.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param testCase The test case that is firing these events.
     * @param list     The JList that is being clicked.
     * @param element  The element that is to be selected.
     * @param numberOfClicks
     *                 Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param awtSleepTime
     *                 The wait time in ms between each event.
     * @exception Exception may be thrown.
     */
    public void enterJListClickAndLeave(JFCTestCase testCase, JList list, Object element, int numberOfClicks, long awtSleepTime) throws Exception {
        int elementIndex = -1;
        for (int i = 0; i < list.getModel().getSize(); i++) {
            if (list.getModel().getElementAt(i) != null && list.getModel().getElementAt(i).equals(element)) {
                elementIndex = i;
                break;
            }
        }
        enterJListClickAndLeave(testCase, list, elementIndex, numberOfClicks, awtSleepTime);
    }

    /**
     * Helper method to fire appropriate events to click(s) a component with a
     * custom wait method.
     *
     * @param testCase The test case that is firing these events.
     * @param list     The JList that is being clicked.
     * @param elementIndex
     *                 The element index (see ListDataModel).
     * @param numberOfClicks
     *                 Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @see #enterClickAndLeave(AbstractMouseEventData)
     * @exception Exception may be thrown.
     */
    public void enterJListClickAndLeave(JFCTestCase testCase, JList list, int elementIndex, int numberOfClicks) throws Exception {
        enterJListClickAndLeave(testCase, list, elementIndex, numberOfClicks, DEFAULTSLEEP);
    }

    /**
     * Helper method to fire appropriate events to click(s) a component with a
     * custom wait method.
     *
     * @param testCase The test case that is firing these events.
     * @param list     The JList that is being clicked.
     * @param elementIndex
     *                 The element index (see ListDataModel).
     * @param numberOfClicks
     *                 Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param awtSleepTime
     *                 The wait time in ms between each event.
     * @see #enterClickAndLeave(AbstractMouseEventData)
     * @exception Exception may be thrown.
     */
    public void enterJListClickAndLeave(JFCTestCase testCase, JList list, int elementIndex, int numberOfClicks, long awtSleepTime) throws Exception {
        enterClickAndLeave(new JListMouseEventData(testCase, list, elementIndex, numberOfClicks, awtSleepTime));
    }

    /**
     * Helper method to fire appropriate events to click(s) on a component.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param evtData The event data container.
     * @exception Exception may be thrown.
     */
    public abstract void enterClickAndLeave(AbstractMouseEventData evtData) throws Exception;

    /**
     * Helper method to fire appropriate events to drag and drop a component with a
     * custom wait method
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param srcEvtData The source  event data container
     * @param dstEvtData The destination event data container
     * @param incr       Amount to increment the coords while "moving" from
     *                   source to destination.
     * @exception Exception may be thrown.
     */
    public abstract void enterDragAndLeave(AbstractMouseEventData srcEvtData, AbstractMouseEventData dstEvtData, int incr) throws Exception;

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method can also differentiate between
     * upper and lower case characters in the specified string.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param testCase The test case that is firing these events.
     * @param comp     The component that is being clicked.
     * @param str      The string that has to be sent to the component.
     * @exception Exception may be thrown.
     */
    public void sendString(JFCTestCase testCase, Component comp, String str) throws Exception {
        sendString(testCase, comp, str, 0);
    }

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method can also differentiate between
     * upper and lower case characters in the specified string.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param testCase  The test case that is firing these events.
     * @param comp      The component that is being clicked.
     * @param str       The string that has to be sent to the component.
     * @param modifiers
     *                  The modifier keys down during the event.
     * @exception Exception may be thrown.
     */
    public void sendString(JFCTestCase testCase, Component comp, String str, int modifiers) throws Exception {
        sendString(testCase, comp, str, modifiers, DEFAULTSLEEP);
    }

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method can also differentiate between
     * upper and lower case characters in the specified string.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param _testCase The test case that is firing these events
     * @param _comp     The component that is being clicked
     * @param _string   The string that has to be sent to the component
     * @param _modifiers
     *                  The modifier keys down during the event
     * @param _awtSleepTime
     *                  The wait time in ms between each event
     * @exception Exception may be thrown.
     */
    public void sendString(JFCTestCase _testCase, Component _comp, String _string, int _modifiers, long _awtSleepTime) throws Exception {
        sendString(new StringEventData(_testCase, _comp, _string, _modifiers, _awtSleepTime));
    }

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method can also differentiate between
     * upper and lower case characters in the specified string.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param evtData  The event data container.
     * @exception Exception may be thrown.
     */
    public abstract void sendString(StringEventData evtData) throws Exception;

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method should be used only to send Action key events.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param testCase The test case that is firing these events.
     * @param comp     The component that is being clicked.
     * @param keyCode  The keyCode of the KeyEvent.
     * @exception Exception may be thrown.
     */
    public void sendKeyAction(JFCTestCase testCase, Component comp, int keyCode) throws Exception {
        sendKeyAction(testCase, comp, keyCode, 0);
    }

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method should be used only to send Action key events.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param testCase  The test case that is firing these events.
     * @param comp      The component that is being clicked.
     * @param keyCode   The keyCode of the KeyEvent.
     * @param modifiers
     *                  The modifier keys down during the event.
     * @exception Exception may be thrown.
     */
    public void sendKeyAction(JFCTestCase testCase, Component comp, int keyCode, int modifiers) throws Exception {
        sendKeyAction(testCase, comp, keyCode, modifiers, DEFAULTSLEEP);
    }

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method should be used only to send Action key events.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param _testCase  The test case that is firing these events
     * @param _comp      The component that is being clicked
     * @param _keyCode   The keyCode of the KeyEvent
     * @param _modifiers
     *                  The modifier keys down during the event
     * @param _awtSleep
     *                  The wait time in ms between each event
     * @exception Exception may be thrown.
     */
    public void sendKeyAction(JFCTestCase _testCase, Component _comp, int _keyCode, int _modifiers, long _awtSleep) throws Exception {
        sendKeyAction(new KeyEventData(_testCase, _comp, _keyCode, _modifiers, _awtSleep));
    }

    /**
     * This method is used to send KeyPressed, KeyTyped
     * and KeyReleased events (in that order) to the specified
     * component. This method should be used only to send Action key events.
     * NOTE: This method will not call 'requestFocus()' on the component - the developer
     * should include it in the test code if needed.
     *
     * @param evtData  The event data container.
     * @exception Exception may be thrown.
     */
    public abstract void sendKeyAction(KeyEventData evtData) throws Exception;
}