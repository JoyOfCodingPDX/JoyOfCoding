package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.Point;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Data container class that holds all the data necessary for JFCUnit to fire mouse events.
 * This class is specific to events on a JTree.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class JTreeMouseEventData extends AbstractMouseEventData {
    /**
     * The JTree on which to trigger the event.
     */
    private JTree tree;

    /**
     * The path of the specific node on which to trigger the event.
     */
    private TreePath treePath;

    /**
     * The String value of the specific node on which to trigger the event.
     */
    private String nodeValue;

    /**
     * Constructor
     */
    public JTreeMouseEventData() {
        super();
        setValid(false);
    }

    /**
     * Constructor
     *
     * @param _testCase The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree     The component on which to trigger the event.
     * @param _treePath The path of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                  Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     */
    public JTreeMouseEventData(JFCTestCase _testCase, JTree _tree, TreePath _treePath, int _numberOfClicks) {
        this(_testCase, _tree, _treePath, _numberOfClicks, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _nodeValue The String value of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     */
    public JTreeMouseEventData(JFCTestCase _testCase, JTree _tree, String _nodeValue, int _numberOfClicks) {
        this(_testCase, _tree, _nodeValue, _numberOfClicks, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _treePath  The path of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTreeMouseEventData(JFCTestCase _testCase, JTree _tree, TreePath _treePath, int _numberOfClicks, long _sleepTime) {
        this(_testCase, _tree, _treePath, _numberOfClicks, DEFAULT_ISPOPUPTRIGGER, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _nodeValue The String value of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTreeMouseEventData(JFCTestCase _testCase, JTree _tree, String _nodeValue, int _numberOfClicks, long _sleepTime) {
        this(_testCase, _tree, _nodeValue, _numberOfClicks, DEFAULT_ISPOPUPTRIGGER, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _treePath  The path of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     */
    public JTreeMouseEventData(JFCTestCase _testCase, JTree _tree, TreePath _treePath, int _numberOfClicks, boolean _isPopupTrigger) {
        this(_testCase, _tree, _treePath, _numberOfClicks, _isPopupTrigger, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _nodeValue The String value of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     */
    public JTreeMouseEventData(JFCTestCase _testCase, JTree _tree, String _nodeValue, int _numberOfClicks, boolean _isPopupTrigger) {
        this(_testCase, _tree, _nodeValue, _numberOfClicks, _isPopupTrigger, DEFAULT_SLEEPTIME);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _treePath  The path of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTreeMouseEventData(JFCTestCase _testCase, JTree _tree, TreePath _treePath, int _numberOfClicks, boolean _isPopupTrigger, long _sleepTime) {
        this(_testCase, _tree, _treePath, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _nodeValue The String value of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTreeMouseEventData(JFCTestCase _testCase, JTree _tree, String _nodeValue, int _numberOfClicks, boolean _isPopupTrigger, long _sleepTime) {
        this(_testCase, _tree, _nodeValue, _numberOfClicks, DEFAULT_MOUSE_MODIFIERS, _isPopupTrigger, _sleepTime);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _treePath  The path of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTreeMouseEventData(JFCTestCase _testCase, JTree _tree, TreePath _treePath, int _numberOfClicks, int _modifiers, boolean _isPopupTrigger, long _sleepTime) {
        this(_testCase, _tree, _treePath, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, DEFAULT_POSITION, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _nodeValue The String value of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     */
    public JTreeMouseEventData(JFCTestCase _testCase, JTree _tree, String _nodeValue, int _numberOfClicks, int _modifiers, boolean _isPopupTrigger, long _sleepTime) {
        this(_testCase, _tree, _nodeValue, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, DEFAULT_POSITION, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _treePath  The path of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _position  The relative mouse position within the cell.
     */
    public JTreeMouseEventData(
        JFCTestCase _testCase,
        JTree _tree,
        TreePath _treePath,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        int _position) {
        this(_testCase, _tree, _treePath, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, _position, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _nodeValue The String value of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _position  The relative mouse position within the cell.
     */
    public JTreeMouseEventData(
        JFCTestCase _testCase,
        JTree _tree,
        String _nodeValue,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        int _position) {
        this(_testCase, _tree, _nodeValue, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, _position, null);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _treePath  The path of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _referencePoint
     *                   The custom mouse position within the cell.
     */
    public JTreeMouseEventData(
        JFCTestCase _testCase,
        JTree _tree,
        TreePath _treePath,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        Point _referencePoint) {
        this(_testCase, _tree, _treePath, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, CUSTOM, _referencePoint);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _nodeValue The String value of the specific node on which to trigger the event.
     * @param _numberOfClicks
     *                   Number of clicks in the MouseEvent (1 for single-click, 2 for double clicks)
     * @param _modifiers The modifier key values that need to be passed onto the event.
     * @param _isPopupTrigger
     *                   boolean specifying whether this event will show a popup.
     * @param _sleepTime
     *                   The wait time in ms between each event.
     * @param _referencePoint
     *                        The CUSTOM mouse position within the cell.
     */
    public JTreeMouseEventData(
        JFCTestCase _testCase,
        JTree _tree,
        String _nodeValue,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        Point _referencePoint) {
        this(_testCase, _tree, _nodeValue, _numberOfClicks, _modifiers, _isPopupTrigger, _sleepTime, CUSTOM, _referencePoint);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _treePath  The path of the specific node on which to trigger the event.
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
    public JTreeMouseEventData(
        JFCTestCase _testCase,
        JTree _tree,
        TreePath _treePath,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        int _position,
        Point _referencePoint) {
        setTestCase(_testCase);
        setSource(_tree);
        setNumberOfClicks(_numberOfClicks);
        setModifiers(_modifiers);
        setPopupTrigger(_isPopupTrigger);
        setTreePath(_treePath);
        setSleepTime(_sleepTime);
        setPosition(_position);
        setReferencePoint(_referencePoint);
        setValid(true);
    }

    /**
     * Constructor
     *
     * @param _testCase  The JFCTestCase on whose thread 'awtSleep()' has to be invoked.
     * @param _tree      The component on which to trigger the event.
     * @param _nodeValue The String value of the specific node on which to trigger the event.
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
    public JTreeMouseEventData(
        JFCTestCase _testCase,
        JTree _tree,
        String _nodeValue,
        int _numberOfClicks,
        int _modifiers,
        boolean _isPopupTrigger,
        long _sleepTime,
        int _position,
        Point _referencePoint) {
        setTestCase(_testCase);
        setSource(_tree);
        setNumberOfClicks(_numberOfClicks);
        setModifiers(_modifiers);
        setPopupTrigger(_isPopupTrigger);
        setNodeValue(_nodeValue);
        setSleepTime(_sleepTime);
        setPosition(_position);
        setReferencePoint(_referencePoint);
        setValid(true);
    }

    /**
     * Set the attribute value
     *
     * @param _tree  The new value of the attribute
     */
    public void setSource(JTree _tree) {
        tree = _tree;
    }

    /**
     * Get the attribute value
     *
     * @return JTree    The value of the attribute
     */
    public JTree getSource() {
        return tree;
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
     * @param _treePath The new value of the attribute
     */
    public void setTreePath(TreePath _treePath) {
        treePath = _treePath;
    }

    /**
     * Get the attribute value
     *
     * @return TreePath    The value of the attribute
     */
    public TreePath getTreePath() {
        return treePath;
    }

    /**
     * Set the attribute value
     *
     * @param _nodeValue The new value of the attribute
     */
    public void setNodeValue(String _nodeValue) {
        nodeValue = _nodeValue;
    }

    /**
     * Get the attribute value
     *
     * @return String    The value of the attribute
     */
    public String getNodeValue() {
        return nodeValue;
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

        tree.updateUI();
        JFCTestCase testCase = getTestCase();
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }

        TreePath path = getTreePath();
        if (path == null) {
            DefaultMutableTreeNode node = getNode(tree, getNodeValue());
            path = new TreePath(node.getPath());
        }

        if (path == null) {
            return false;
        }

        tree.setSelectionPath(path);
        if (testCase != null) {
            testCase.awtSleep(getSleepTime());
        }

        Point p = calculatePoint(tree.getPathBounds(path));
        Point screen = tree.getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);
        return true;
    }

    /**
     * Finds the node in the specified tree by comparing
     * with the value passed in.
     *
     * @param tree    The tree containing the node
     * @param userObj The value of the node (usually the node label)
     * @return The node whose value matched the value passed in
     */
    private DefaultMutableTreeNode getNode(JTree tree, String userObj) {
        if (userObj == null) {
            return null;
        }

        TreeModel model = tree.getModel();

        // if the userObj is the same as the userObject of the root, just return root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        if (checkNodesUserObject(root, userObj)) {
            return root;
        }

        List nodeList = findNodeList(root, new ArrayList());
        DefaultMutableTreeNode node = null;
        Iterator iter = nodeList.iterator();
        while (iter.hasNext()) {
            node = (DefaultMutableTreeNode) iter.next();
            if (checkNodesUserObject(node, userObj)) {
                return node;
            }
        }

        // nothing matched - return null
        return null;
    }

    /**
     * Calls itself recursively to build a list of all
     * the child nodes in the parent
     *
     * @param parent  Node whose children have to be added to the list
     * @param outList List of all the nodes in the tree
     * @return List of all nodes
     */
    private List findNodeList(DefaultMutableTreeNode parent, List outList) {
        DefaultMutableTreeNode node;

        for (int idx = 0; idx < parent.getChildCount(); idx++) {
            node = (DefaultMutableTreeNode) parent.getChildAt(idx);
            outList.add(node);
            findNodeList(node, outList);
        }

        return outList;
    }

    /**
     * Checks to see if the node is not null and its
     * userObject is equal to the value passed in
     *
     * @param node    Node
     * @param userObj Expected value
     * @return true if the values are the same
     */
    private boolean checkNodesUserObject(DefaultMutableTreeNode node, String userObj) {
        return (node != null && node.getUserObject() != null && node.getUserObject().toString().equals(userObj.toString()));
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
        JTree source = (JTree)me.getSource();
        setSource(source);
        setModifiers(me.getModifiers());
        setNumberOfClicks(me.getClickCount());
        setPopupTrigger(me.isPopupTrigger());
        Point p = new Point(me.getX(), me.getY());
        Point screen = source.getLocationOnScreen();
        screen.translate(p.x, p.y);
        setLocationOnScreen(screen);

        int row = ((JTree)source).getRowForLocation(me.getX(), me.getY());
        TreePath path = ((JTree)source).getPathForRow(row);
        setTreePath(path);

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
        if (!(ae.getSource() instanceof JTree)
            || !super.canConsume(ae)
            || !sameSource(ae)) {
            return false;
        }
        if (isValid()) {
            int row = ((JTree)ae.getSource()).getRowForLocation(((MouseEvent)ae).getX(), ((MouseEvent)ae).getY());
            TreePath path = ((JTree)ae.getSource()).getPathForRow(row);
            if (!path.equals(getTreePath())) {
                return false;
            }
        }
        return true;
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
        buf.append(" nodeValue: " + getNodeValue());
        buf.append(" treePath: " + getTreePath());
        return buf.toString();
    }

}