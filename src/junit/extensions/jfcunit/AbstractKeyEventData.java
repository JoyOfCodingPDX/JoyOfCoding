package junit.extensions.jfcunit;

import java.awt.event.KeyEvent;
import java.awt.AWTEvent;
import java.awt.Component;


/**
 * Abstract data container class that holds most of the data necessary for JFCUnit to fire mouse events.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public abstract class AbstractKeyEventData extends AbstractEventData {
    /**
     * Get the key code for a character.
     *
     * @param c The character to be coded.
     * @return int key code for the character passed in.
     */
    public int getCode(char c) {
        return ((Character.isLowerCase(c)) ? (int) Character.toUpperCase(c) : (int) c);
    }

    /**
     * Check if the keyCode is a typed key.
     * @param keyCode key code to be checked.
     * @return true if the keyCode is a typed key.
     */
    public static final boolean isTypedChar(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_ACCEPT :
            case KeyEvent.VK_ADD :
            case KeyEvent.VK_AGAIN :
            case KeyEvent.VK_ALL_CANDIDATES :
            case KeyEvent.VK_ALPHANUMERIC :
            case KeyEvent.VK_ALT :
            case KeyEvent.VK_ALT_GRAPH :
            case KeyEvent.VK_CANCEL :
            case KeyEvent.VK_CAPS_LOCK :
            case KeyEvent.VK_CLEAR :
            case KeyEvent.VK_CODE_INPUT :
            case KeyEvent.VK_COMPOSE :
            case KeyEvent.VK_CONTROL :
            case KeyEvent.VK_CONVERT :
            case KeyEvent.VK_COPY :
            case KeyEvent.VK_CUT :
            case KeyEvent.VK_DELETE :
            case KeyEvent.VK_DOWN :
            case KeyEvent.VK_END :
            case KeyEvent.VK_F1 :
            case KeyEvent.VK_F10 :
            case KeyEvent.VK_F11 :
            case KeyEvent.VK_F12 :
            case KeyEvent.VK_F13 :
            case KeyEvent.VK_F14 :
            case KeyEvent.VK_F15 :
            case KeyEvent.VK_F16 :
            case KeyEvent.VK_F17 :
            case KeyEvent.VK_F18 :
            case KeyEvent.VK_F19 :
            case KeyEvent.VK_F2 :
            case KeyEvent.VK_F20 :
            case KeyEvent.VK_F21 :
            case KeyEvent.VK_F22 :
            case KeyEvent.VK_F23 :
            case KeyEvent.VK_F24 :
            case KeyEvent.VK_F3 :
            case KeyEvent.VK_F4 :
            case KeyEvent.VK_F5 :
            case KeyEvent.VK_F6 :
            case KeyEvent.VK_F7 :
            case KeyEvent.VK_F8 :
            case KeyEvent.VK_F9 :
            case KeyEvent.VK_FINAL :
            case KeyEvent.VK_FIND :
            case KeyEvent.VK_FULL_WIDTH :
            case KeyEvent.VK_HALF_WIDTH :
            case KeyEvent.VK_HELP :
            case KeyEvent.VK_HIRAGANA :
            case KeyEvent.VK_HOME :
            case KeyEvent.VK_INSERT :
            case KeyEvent.VK_JAPANESE_HIRAGANA :
            case KeyEvent.VK_JAPANESE_KATAKANA :
            case KeyEvent.VK_JAPANESE_ROMAN :
            case KeyEvent.VK_KANA :
            case KeyEvent.VK_KANJI :
            case KeyEvent.VK_KATAKANA :
            case KeyEvent.VK_KP_DOWN :
                /*Commented out since this is present only from jdk 1.3
                case KeyEvent.VK_KANA_LOCK:
                */
            case KeyEvent.VK_KP_LEFT :
            case KeyEvent.VK_KP_RIGHT :
            case KeyEvent.VK_KP_UP :
            case KeyEvent.VK_LEFT :
            case KeyEvent.VK_META :
            case KeyEvent.VK_MODECHANGE :
            case KeyEvent.VK_NONCONVERT :
            case KeyEvent.VK_NUM_LOCK :
            case KeyEvent.VK_PAGE_DOWN :
            case KeyEvent.VK_PAGE_UP :
            case KeyEvent.VK_PASTE :
            case KeyEvent.VK_PAUSE :
            case KeyEvent.VK_PREVIOUS_CANDIDATE :
            case KeyEvent.VK_PRINTSCREEN :
            case KeyEvent.VK_PROPS :
            case KeyEvent.VK_ROMAN_CHARACTERS :
            case KeyEvent.VK_RIGHT :
            case KeyEvent.VK_SCROLL_LOCK :
            case KeyEvent.VK_SHIFT :
            case KeyEvent.VK_STOP :
            case KeyEvent.VK_UNDEFINED :
            case KeyEvent.VK_UNDO :
                /*Commented out since this is present only from jdk 1.3
                case KeyEvent.VK_INPUT_METHOD_ON_OFF:
                */
            case KeyEvent.VK_UP :
                return false;
        }
        // all others return true
        return true;
    }

    /**
     * Get the attribute value
     * @param ae Event to be processed.
     * @return boolean    The value of the attribute
     */
    public boolean consume(AWTEvent ae) {
        if (!(ae instanceof KeyEvent)) {
            return false;
        }

        KeyEvent ke = (KeyEvent)ae;
        int id = ke.getID();
        if ((id == KeyEvent.KEY_TYPED) ||
            (id == KeyEvent.KEY_RELEASED) ||
            (isMetaChar(ke.getKeyCode())) ||
            ae.getSource().equals(getRoot())) {
            // Ignore the event.
            return true;
        }
        return false;

    }

    /**
     * Check if this event can consume the AWTEvent.
     *
     * @param ae Event to be consumed.
     * @return boolean true if the event can be consumed.
     */
    public boolean canConsume(AWTEvent ae) {
        if (!isValid()) {
            return true;
        }

        return (ae instanceof KeyEvent)
            && getRoot((Component)ae.getSource()).equals(getRoot());

    }
}
