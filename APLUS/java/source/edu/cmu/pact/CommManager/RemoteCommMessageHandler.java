package edu.cmu.pact.CommManager;

import java.util.concurrent.ConcurrentLinkedQueue;

import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.BehaviorRecorder.Controller.HintMessagesManagerImpl;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

public class RemoteCommMessageHandler implements CommMessageHandler {    

    private HintMessagesManager _mgr;
    private HintWindowInterface _panel;
    private ConcurrentLinkedQueue<MessageObject> _msgQueue;
    private RemoteTutor _remoteTutor;

    public RemoteCommMessageHandler(HintMessagesManager manager) {
        _mgr = manager;
    }

    public RemoteCommMessageHandler() {
        this(new HintMessagesManagerImpl(null));
    }

    public void setRemoteTutor(RemoteTutor remoteTutor) {
        _remoteTutor = remoteTutor;
    }

    private RemoteTutor remoteTutor() {
        if (_remoteTutor==null) {
            _remoteTutor = new RemoteTutor() {
                    public String getLatestFocus() { return "table1_C5R1"; }
                };
        }
        return _remoteTutor;
    }

    public void setHintInterface(HintWindowInterface panel) {
        _panel = panel;
    }
    
    public HintMessagesManager messagesManager() {
        return _mgr;
    }
    
    public boolean getShowWidgetInfo() {
        return false;
    }

    private ConcurrentLinkedQueue<MessageObject> msgQueue() {
        if (_msgQueue==null)
            _msgQueue = new ConcurrentLinkedQueue<MessageObject>();
        return _msgQueue;
    }

    public boolean hasMessage() {
        return !msgQueue().isEmpty();
    }

    public String nextMessage() {
        if (!hasMessage())
            return null;

        MessageObject mo = msgQueue().remove();

        try {
            trace.out("sp", "selection: " + mo.getProperty("Selection"));
            trace.out("sp", "mo before: " + mo);
            trace.out("sp", "mo before: " + mo.toXML());
            if (mo.matchProperty("Selection", "hint")) {
                String latestFocus = remoteTutor().getLatestFocus();
                trace.out("sp", "latestFocus = " + latestFocus);
                if (latestFocus!=null) {
                    mo.addPropertyElement("Action", "PreviousFocus");
                    mo.addPropertyElement("Selection", latestFocus);
                }
                trace.out("sp", "mo after: " + mo);
                trace.out("sp", "mo after: " + mo.toXML());
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        // return new OLIMessageObject(mo, false, new Logger(null)).toXML();
        return mo.toXML();
    }
    
    private boolean isHintRelated(MessageObject mo) {
        return MsgType.hasTextFeedback(mo);
    }
	public void sendMessage(MessageObject mo) {
        if (trace.getDebugCode("sp"))
        	trace.out("sp", getClass().getName() + ".sendCommMessage(" + mo + ")");
        msgQueue().add(mo);
    }

	public void handleCommMessage(MessageObject messageObject) {
    }
    
    public boolean lockWidget() {
        return false;
    }

	public void handleMessage(MessageObject messageObject) {
        sendMessage(messageObject);
	}
}

interface RemoteTutor {
    public String getLatestFocus();
}
