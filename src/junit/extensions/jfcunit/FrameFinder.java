package junit.extensions.jfcunit;

import java.awt.Component;
import java.awt.Frame;

/**
 * Class for checking if the component being searched for has been found
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class FrameFinder extends AbstractWindowFinder {
    /**
     * Constructor accepting all arguments needed to filter component.
     *
     * @param str    The desired title of the component.
     */
    public FrameFinder(String str) {
        super(str);
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
               && isValidForProcessing(comp, Frame.class)
               && checkIfTitleMatches(((Frame)comp).getTitle()));
    }
}

