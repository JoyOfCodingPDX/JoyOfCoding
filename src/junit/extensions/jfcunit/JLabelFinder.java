package junit.extensions.jfcunit;

import java.awt.Component;

import javax.swing.JLabel;

/**
 * Class for checking if the component being searched for has been found
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class JLabelFinder extends Finder {
    /**
     * The text of the component.
     */
    private String label;

    /**
     * Constructor accepting all arguments needed to filter component.
     *
     * @param str    The desired text of the component.
     */
    public JLabelFinder(String str) {
        label = str;
    }

    /**
     * Method that returns true if the given component matches the search
     * criteria
     *
     * @param comp   The component to test
     * @return true if this component is a match
     */
    public boolean testComponent(final Component comp) {
        return (comp != null
               && isValidForProcessing(comp, JLabel.class)
               && compareValues(String.class, ((JLabel)comp).getText(), label));
    }
}

