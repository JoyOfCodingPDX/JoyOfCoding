package junit.extensions.jfcunit.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

/**
 * Tree node for the ComponentBrowser application.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class ComponentNode implements TreeNode {
    /** Parent node */
    private ComponentNode mParent;

    /** Component */
    private Component mComponent;

    /**
     * Default Constructor.
     */
    public ComponentNode() {
        mComponent = null;
    }

    /**
     * Construtor
     * @param parent node of parent.
     * @param comp Component to be held by the node.
     */
    public ComponentNode(ComponentNode parent, Component comp) {
        mParent = parent;
        mComponent = comp;
    }

    /**
     * Get the component held by this node of the tree.
     * @return Component
     */
    public Component getComponent() {
        return mComponent;
    }

    /**
     * Get the children of this node.
     * @return Enumeration of the children of type ComponentNode.
     */
    public Enumeration children() {
        Vector kids = new Vector();
        int num = getChildCount();
        for (int i = 0; i < num; i++) {
            kids.add(getChildAt(i));
        }
        return kids.elements();
    }

    /**
     * Can the node support children.
     * @return true if the node Component is a Container.
     */
    public boolean getAllowsChildren() {
        return mComponent == null || mComponent instanceof Container;
    }

    /**
     * Get the specified child.
     *
     * @param childIndex The index of the child node to be returned.
     * @return TreeNode  The child node in the tree.
     */
    public TreeNode getChildAt(int childIndex) {
        Component child;
        if (mComponent == null) {
            child = Frame.getFrames()[childIndex];
        } else if (mComponent instanceof Container) {
            Container container = (Container) mComponent;
            int cnt = container.getComponentCount();
            if (childIndex >= cnt && container instanceof Window) {
                Window[] windows = ((Window) container).getOwnedWindows();
                child = windows[childIndex - cnt];
            } else {
                child = container.getComponent(childIndex);
            }
        } else {
            throw new RuntimeException("no child with index " + childIndex);
        }
        return new ComponentNode(this, child);
    }

    /**
     * Get the number of children.
     * @return int Number of children.
     */
    public int getChildCount() {
        int num = 0;
        if (mComponent == null) {
            num = Frame.getFrames().length;
        } else if (mComponent instanceof Container) {
            Container container = (Container) mComponent;
            num = container.getComponentCount();
            if (container instanceof Window) {
                num += ((Window) container).getOwnedWindows().length;
            }
        }
        return num;
    }

    /**
     * Get the index of the node given.
     * @param node TreeNode for which the index should be given.
     * @return int Returns the index of the node or -1 if the
     *             node is not found.
     */
    public int getIndex(TreeNode node) {
        int index = -1;
        int num = getChildCount();
        for (int i = 0; i < num; i++) {
            if (node.equals(getChildAt(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Get the parent node.
     * @return TreeNode Reference to parent.
     */
    public TreeNode getParent() {
        return mParent;
    }

    /**
     * Is the node a leaf.
     *
     * @return boolean True if the node has no children.
     */
    public boolean isLeaf() {
        return !getAllowsChildren() || getChildCount() == 0;
    }

    /**
     * The hashCode of this node.
     * @return int hashCode of the referenced Component or 1
     *             if no component is referenced (ex. Top of Tree).
     */
    public int hashCode() {
        return ((mComponent == null) ? 1 : mComponent.hashCode());
    }

    /**
     * Check equality.
     *
     * @param other Object to be compared.
     * @return boolean true if the objects are equal.
     */
    public boolean equals(Object other) {
        return ((other instanceof ComponentNode) && (mComponent == ((ComponentNode) other).mComponent));
    }

    /**
     * Generate a description of the object.
     *
     * @return String description of this object.
     */
    public String toString() {
        if (mComponent == null) {
            return "All Frames";
        }
        // mComponent is not null
        return ((mComponent.getName() == null) ? (mComponent.getClass().getName() + " : " + mComponent.hashCode()) : mComponent.getName());
    }
}
