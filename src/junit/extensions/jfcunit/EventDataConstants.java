package junit.extensions.jfcunit;

import java.awt.event.MouseEvent;

import javax.swing.SwingConstants;

/**
 * An interface defining the default values of the common attributes used in all EventDataContainer classes.
 * Package level access is enough for this class.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
interface EventDataConstants {
    /**
     * Place the mouse at the center of the component.
     */
    public static final int CENTER = SwingConstants.CENTER;

    /**
     * Place the mouse at the center of the top edge component.
     */
    public static final int NORTH = SwingConstants.NORTH;

    /**
     * Place the mouse at the top right corner of the component.
     */
    public static final int NORTH_EAST = SwingConstants.NORTH_EAST;

    /**
     * Place the mouse at the center of the right edge of the component.
     */
    public static final int EAST = SwingConstants.EAST;

    /**
     * Place the mouse at the bottom right corner of the component.
     */
    public static final int SOUTH_EAST = SwingConstants.SOUTH_EAST;

    /**
     * Place the mouse at the center of the bottom edge of the component.
     */
    public static final int SOUTH = SwingConstants.SOUTH;

    /**
     * Place the mouse at the bottom left corner of the component.
     */
    public static final int SOUTH_WEST = SwingConstants.SOUTH_WEST;

    /**
     * Place the mouse at the center of the left edge of the component.
     */
    public static final int WEST = SwingConstants.WEST;

    /**
     * Place the mouse at the top left corner of the component.
     */
    public static final int NORTH_WEST = SwingConstants.NORTH_WEST;

    /**
     * Place the mouse at a point in the component specified by the user.
     * Skip other SwingConstants incase they are needed in future.
     */
    public static final int CUSTOM = 12;

    /**
     * Place the mouse at a point in the component specified by
     * percentage of width and height. The point argument should
     * contain the percentages. For example: to get  a position
     * in the center the arguments to the event for position and point
     * would be PERCENT, new Point(50,50).
     */
    public static final int PERCENT = 13;

    /**
     * Place the mouse at a point specified by the offset.
     */
    public static final int OFFSET = 14;

    /**
     * String description of the positions.
     */
    public static final String POSITIONSTRINGS[] = {
        "center",
        "north",
        "northeast",
        "east",
        "southeast",
        "south",
        "southwest",
        "west",
        "northwest",
        "",
        "",
        "",
        "custom",
        "percent",
        "offset",

    };


    /**
     * Invalid text offset
     */
    public static final int INVALID_TEXT_OFFSET = -1;

    /**
     * Default value specifying the position of the mouse relative to the component.
     */
    public static final int DEFAULT_POSITION = CENTER;

    /**
     * Default value specifying the number of clicks for the MouseEvents.
     */
    public static final int DEFAULT_NUMBEROFCLICKS = 1;

    /**
     * Default value specifying whether the mouse event being fired
     * would trigger a popup or not.
     */
    public static final boolean DEFAULT_ISPOPUPTRIGGER = false;

    /**
     * Default value specifying the modifier key values that need to be passed onto the MouseEvent.
     */
    public static final int DEFAULT_MOUSE_MODIFIERS = MouseEvent.BUTTON1_MASK;

    /**
     * Default value specifying the modifiers key values that need to be passed by KeyEvents.
     */
    public static final int DEFAULT_KEY_MODIFIERS = 0;

    /**
     * Default value specifying the wait time in ms between each event.
     */
    public static final long DEFAULT_SLEEPTIME = TestHelper.DEFAULTSLEEP;

    /**
     * Default value specifying the hold time in ms before ejecting a event
     * from the event manager.
     */
    public static final long DEFAULT_HOLDTIME = 100L;

}
