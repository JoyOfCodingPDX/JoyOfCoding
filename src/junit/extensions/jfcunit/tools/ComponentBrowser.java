package junit.extensions.jfcunit.tools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;

/**
 * Component Browser utility.
 * Displays the components of a Java application in a component
 * tree. When objects are selected, there properties are displayed
 * in the property table.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class ComponentBrowser extends JFrame implements ActionListener,
    TreeSelectionListener, WindowListener {
    /** Refresh Button */
    private JButton refreshButton;

    /** Component Tree */
    private JTree componentTree;

    /** Property Table Model */
    private DefaultTableModel propTableModel;

    /**
     * Default constructor
     */
    public ComponentBrowser() {
        setTitle("ComponentBrowser");
        equip(getContentPane());
        setSize(400, 300);
        pack();
        setVisible(true);
        addWindowListener(this);
    }

    /**
     * Just another dumb main method.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new ComponentBrowser();
    }

    /**
     * Method to create required widgets/components and populate
     * the content pane with them.
     *
     * @param pane   The content pane to which the created
     *               objects have to be added into.
     */
    private void equip(Container pane) {
        pane.setSize(400, 300);
        pane.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                createLeftPanel(), createRightPanel()),
                 BorderLayout.CENTER);
    }

    /**
     * Method to create a default button to reload the JTree
     *
     * @return A button to reload the data
     */
    private JButton createReloadButton() {
        JButton button = new JButton("Reload");
        button.addActionListener(this);
        return button;
    }

    /**
     * Method to create the left pane consisting of the
     * reload button and a JTree
     *
     * @return A JPanel for the left side of the main frame
     */
    private JPanel createLeftPanel() {
        componentTree = new JTree(new ComponentNode());
        componentTree.addTreeSelectionListener(this);

        JPanel leftPane = new JPanel(new BorderLayout());
        leftPane.setSize(100, 300);
        leftPane.setPreferredSize(leftPane.getSize());
        leftPane.add(new JScrollPane(componentTree), BorderLayout.CENTER);
        refreshButton = createReloadButton();
        leftPane.add(refreshButton, BorderLayout.SOUTH);
        return leftPane;
    }

    /**
     * Method to create the right pane consisting of the JTable
     *
     * @return A JPanel for the right side of the main frame
     */
    private JScrollPane createRightPanel() {
        Vector colNames = new Vector(2);
        colNames.add("Prop name");
        colNames.add("Value");
        propTableModel = new DefaultTableModel(colNames, 0);
        JTable propTable = new JTable(propTableModel);
        propTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane rightPane = new JScrollPane(propTable);
        rightPane.setSize(new Dimension(300, 300));
        rightPane.setPreferredSize(rightPane.getSize());
        rightPane.setColumnHeaderView(propTable.getTableHeader());
        return rightPane;
    }

    /**
     * Utility method showing whether a component
     * node has been selected or not.
     *
     * @return boolean True if a component is selected in the tree.
     */
    public boolean isComponentSelected() {
        return ((ComponentNode) componentTree
                   .getLastSelectedPathComponent() != null);
    }

    /**
     * The method which does the actual work of "refreshing"
     * the right side of the main frame with the data for the
     * component selected on the JTree
     *
     * @param comp   The selected component
     */
    private void componentSelected(Component comp) {
        // The setNumRows() method is being used to be
        // compatible with JDK 1.2.2 should be replaced
        // with setRowCount() instead.
        propTableModel.setNumRows(0);      //kludge to remove all rows

        if (comp == null) {
            return;
        }

        Class superClass = comp.getClass();
        Vector processed = new Vector();
        while (Component.class.isAssignableFrom(superClass)) {
            Method[] methods = superClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if (isAccessibleGetterMethod(methods[i])) {
                    addProperty(comp, methods[i], processed);
                }
            }
            superClass = superClass.getSuperclass();
        }

        // TODO: might be a good idea to also sort the elements
        //       in 'propTableModel'

        propTableModel.fireTableDataChanged();
    }

    /**
     * Method to check if the method specified on the class
     * is a "getter" for an attribute of that class
     *
     * @param method The method to be tested
     * @return true if the method is a "getter"
     */
    private boolean isAccessibleGetterMethod(Method method) {
        return (method.getName().startsWith("get")
                || method.getName().startsWith("is"));
    }

    /**
     * Method to "extract" the property name from the method name
     * following the convention specified for a bean
     *
     * @param methodName The name of the method
     * @return The name of the attribute
     */
    private String getPropertyName(String methodName) {
        String propName = null;
        if (methodName.startsWith("get")) {
            propName = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            propName = methodName.substring(2);
        }
        return propName.substring(0, 1).toLowerCase() + propName.substring(1);
    }

    /**
     * Method to create and add rows to the table model to show
     * the various attributes of the current selected component
     *
     * @param comp      The component selected on the JTree
     * @param method    The method associated with an attribute
     * @param processed The list of already processed methods - to take care of
     *                  methods in the super classes.
     */
    private void addProperty(Component comp, Method method, Vector processed) {
        String name = method.getName();
        if (processed.contains(name)) {
            return;
        }

        processed.addElement(name);

        try {
            Class[] paramTypes = method.getParameterTypes();
            // any getter method which accepts an argument
            // is not a property getter
            if (paramTypes.length == 0) {
                Vector data = new Vector(2);
                data.addElement(getPropertyName(method.getName()));
                data.addElement(method.invoke(comp, new Class[0]));
                propTableModel.addRow(data);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
        } catch (IllegalAccessException e) {
        }
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e dispatched action event.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshButton) {
            ((DefaultTreeModel) componentTree.getModel()).reload();
        }
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(TreeSelectionEvent e) {
        if (e.getSource() == componentTree) {
            if (isComponentSelected()) {
                ComponentNode node = (ComponentNode) componentTree
                                        .getLastSelectedPathComponent();
                componentSelected(node.getComponent());
            }
        }
    }

    /**
     * Invoked the first time a window is made visible.
     *
     * @param e dispatched window event.
     */
    public void windowOpened(WindowEvent e) {
    }

    /**
     * Invoked when the user attempts to close the window
     * from the window's system menu.  If the program does not
     * explicitly hide or dispose the window while processing
     * this event, the window close operation will be cancelled.
     *
     * @param e dispatched window event.
     */
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    /**
     * Invoked when a window has been closed as the result
     * of calling dispose on the window.
     *
     * @param e dispatched window event.
     */
    public void windowClosed(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a normal to a
     * minimized state. For many platforms, a minimized window
     * is displayed as the icon specified in the window's
     * iconImage property.
     *
     * @param e dispatched window event.
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized
     * to a normal state.
     *
     * @param e dispatched window event.
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Invoked when the window is set to be the user's
     * active window, which means the window (or one of its
     * subcomponents) will receive keyboard events.
     *
     * @param e dispatched window event.
     */
    public void windowActivated(WindowEvent e) {
    }

    /**
     * Invoked when a window is no longer the user's active
     * window, which means that keyboard events will no longer
     * be delivered to the window or its subcomponents.
     *
     * @param e dispatched window event.
     */
    public void windowDeactivated(WindowEvent e) {
    }
}
