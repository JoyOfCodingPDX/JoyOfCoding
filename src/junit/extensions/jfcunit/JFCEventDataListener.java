package junit.extensions.jfcunit;

import java.util.EventListener;

/**
 * Listener interface to recieve AbstractEventData generated from a record session
 * of the JFCEventManager.
 *
 * @author <a href="mailto:vraravam@thoughtworks.com">Vijay Aravamudhan : ThoughtWorks Inc.</a>
 */
public interface JFCEventDataListener extends EventListener {
    /**
     * This method should be implemented by the recording class which will
     * translate the JFCEventData into a serializable event data.
     *
     * @param evtData The AbstractEventData which is to be processed by the listener.
     */
    void handleEvent(AbstractEventData evtData);
}