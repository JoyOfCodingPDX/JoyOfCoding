package junit.extensions.jfcunit;

import java.awt.Component;
import javax.swing.JMenuItem;

/**
 * A generic component finder which uses just the type (class) for filtering.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class JMenuItemFinder extends Finder {
    /**
     * The text of the component.
     */
    private String text = null;

    /**
     * Constructor accepting all arguments needed to filter component.
     *
     * @param str    The desired text of the component.
     */
    public JMenuItemFinder(String str) {
        text = str;
    }

    /**
     * Method that returns true if the given component matches the search
     * criteria
     *
     * @param comp   The component to test
     * @return true if this component is a match
     */
    public boolean testComponent(Component comp) {
        // since menuItem might not be visible, we cannot use isValidForProcessing()
        return (comp != null
               && comp instanceof JMenuItem
               && compareValues(String.class, ((JMenuItem)comp).getText(), text));
    }
}

