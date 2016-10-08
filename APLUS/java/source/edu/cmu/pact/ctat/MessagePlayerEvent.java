/**
 * Copyright 2007 Carnegie Mellon University.
 */
 package edu.cmu.pact.ctat;

import java.beans.PropertyChangeEvent;

public class MessagePlayerEvent extends PropertyChangeEvent {

	private MessageObject messageObject;
	private int totalCount;
	private int sentCount;
	private boolean stopping;

    MessagePlayerEvent(MessagePlayer messagePlayer, MessageObject messageObject) {
        super(messagePlayer, "", null, null);
        this.messageObject = messageObject;
    }

	public MessageObject getDataShopMessageObject()
	{
		return messageObject;
	}

	/**
	 * @return the {@link #totalCount}
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * @return the {@link #sentCount}
	 */
	public int getSentCount() {
		return sentCount;
	}

	/**
	 * @return the {@link #stopping}
	 */
	public boolean isStopping() {
		return stopping;
	}

	/**
	 * @param totalCount new value for {@link #totalCount}
	 */
	void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * @param sentCount new value for {@link #sentCount}
	 */
	void setSentCount(int sentCount) {
		this.sentCount = sentCount;
	}

	/**
	 * @param stopping new value for {@link #stopping}
	 */
	void setStopping(boolean stopping) {
		this.stopping = stopping;
	}
}
