package junit.extensions.jfcunit;

import java.awt.Component;

/**
 * This is a 'breakaway' from the ComponentFinder in which the component to be
 * tested is assumed to have a name and so the name passed in is compared to the comp.name
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public class NamedComponentFinder extends Finder {
    /**
     * The type of the component.
     */
    private Class compCls = null;

    /**
     * The name of the component.
     */
    private String name = null;

    /**
     * Constructor accepting all arguments needed to filter component.
     *
     * @param cls    The desired type of the component.
     * @param str    The desired name of the component.
     */
    public NamedComponentFinder(Class cls, String str) {
        compCls = cls;
        name = str;
    }

    /**
     * Method that returns true if the given component matches the search
     * criteria.
     *
     * @param comp   The component to test.
     * @return true if this component is a match.
     */
    public boolean testComponent(Component comp) {
        return (comp != null
               && (compCls == null || isValidForProcessing(comp, compCls))
               && compareValues(String.class, comp.getName(), name));
    }
}

