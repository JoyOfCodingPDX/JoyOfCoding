package junit.extensions.jfcunit;

import java.awt.Component;

import javax.swing.AbstractButton;

/**
 * Class for checking if the component being searched for has been found
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class AbstractButtonFinder extends Finder {
    /**
     * The text of the component.
     */
    private String label;

    /**
     * Constructor accepting all arguments needed to filter component.
     *
     * @param str    The desired text of the component.
     */
    public AbstractButtonFinder(String str) {
        label = str;
    }

    /**
     * Method that returns true if the given component matches the search
     * criteria
     *
     * @param comp   The component to test
     * @return true if this component is a match
     */
    public boolean testComponent(Component comp) {
        return (comp != null
               && isValidForProcessing(comp, AbstractButton.class)
               && compareValues(String.class, ((AbstractButton)comp).getText(), label));
    }
}

