package edu.cmu.pact.CommManager;

import edu.cmu.pact.ctat.MessageObject;

public interface CommMessageHandler {

    public boolean getShowWidgetInfo();
	public void sendMessage(MessageObject mo);
    public boolean lockWidget();
	public void handleMessage(MessageObject messageObject);
}
