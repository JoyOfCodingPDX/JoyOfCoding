package junit.extensions.jfcunit;

import java.awt.Component;

/**
 * Abstract class for defining call back classes to test whether a component
 * that is being searched for has been found.
 * @author Unknown
 */
public abstract class Finder {
    /**
     * This method should not be called inside the .equals() method of the
     * class (1st parameter) that is being checked - otherwise, you will have
     * a cyclical reference.
     *
     * @param objClass The class of the objects being compared
     * @param obj1     First object for comparison
     * @param obj2     Second object for comparison
     * @return boolean whether the two objects have the same values
     *         (ie, the .equals() method returns true)
     */
    protected boolean compareValues(Class objClass, Object obj1, Object obj2) {
        // if the type is different - return false
        if (objClass.isInstance(obj1) != objClass.isInstance(obj2)) {
            return false;
        }

        if ((obj1 != null) && (obj2 != null)) {
            // both are not null - call the .equals() method
            return obj1.equals(obj2);
        } else {
            // if one is null (or) both are null
            return (obj1 == obj2);
        }
    }

    /**
     * This method is used to check that the window object is an instance
     * of the specified class and is also visible.
     *
     * @param comp   The component to be checked
     * @param cls    The type of the component
     * @return true if the component is of the specified type and is visible.
     */
    protected final boolean isValidForProcessing(Component comp, Class cls) {
        return (comp != null && cls != null && cls.isInstance(comp) && comp.isShowing());
    }

    /**
     * Method that returns true if the given component matches the search
     * criteria
     *
     * @param comp   The component to test
     * @return true if this component is a match
     */
    public abstract boolean testComponent(Component comp);
}

