package junit.extensions.jfcunit;

import java.awt.Component;

/**
 * A generic component finder which uses just the type (class) for filtering.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class ComponentFinder extends Finder {
    /**
     * The type of the component.
     */
    private Class compCls = null;

    /**
     * Constructor accepting all arguments needed to filter component.
     *
     * @param cls    The desired type of the component.
     */
    public ComponentFinder(Class cls) {
        compCls = cls;
    }

    /**
     * Method that returns true if the given component matches the search
     * criteria
     *
     * @param comp   The component to test
     * @return true if this component is a match
     */
    public boolean testComponent(Component comp) {
        return (comp != null && isValidForProcessing(comp, compCls));
    }
}

