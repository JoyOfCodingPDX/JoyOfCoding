package junit.extensions.jfcunit;

/**
 * Class for checking if the component being searched for has been found.
 * This class can be used to check only windows that have titles (Frame/Dialog and their sub-classes)
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public abstract class AbstractWindowFinder extends Finder {
    /**
     * The title of the component.
     */
    private String title;

    /**
     * Constructor accepting all arguments needed to filter component.
     *
     * @param str    The desired title of the component.
     */
    public AbstractWindowFinder(String str) {
        title = str;
    }

    /**
     * This method checks whether the title and the titlematch strings are equal.
     * Note: If the titlematch string is null, we need to return all showing windows,
     * so 'true' is returned.
     *
     * @param winTitle The title of the window being checked
     * @return True if either the filter string is null or if the
     *         filter string is a substring of the window title
     */
    protected final boolean checkIfTitleMatches(String winTitle) {
        return ((title == null)
               || (winTitle != null && winTitle.indexOf(title) != -1));
    }
}

