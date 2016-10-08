/*
 * $Id: MessageEvent.java 8953 2008-05-09 20:06:39Z sewall $
 */
package edu.cmu.pact.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.TimeZone;

/**
 * Event to describe a message sent or received.
 */
public class MessageEvent extends EventObject {

	/**
	 * True if this message was sent by the event source; false if received.
	 */
	private boolean sent;

	/**
	 * Message type understood by listeners.
	 */
	private String messageType;

	/**
	 * Message data understood by listeners.
	 */
	private String result;

	/**
	 * Time of creation.
	 */
	private Date timeStamp;
	
	/**
	 * The message itself (this event is notifying the listener about).
	 */
	private Object message;

	/** {@link MessageEvent#getMessageType()} value for quitting the connection. */
	public static final String QUIT = "quit";
	
	/** Date format for printing {@link #timeStamp}. */
	private static final DateFormat dateFmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS z");
	
	static {
		dateFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	/**
	 * Create a message event.
	 *
	 * @param  source source object for superclass
	 * @param  sent true if message was sent (by the source); false if received
	 * @param  message the message itself
	 */
	public MessageEvent(Object source,
						boolean sent,
						Object message) {
		this(source, sent, null, null, message);
	}
	
	/**
	 * Create a message event.
	 *
	 * @param  source source object for superclass
	 * @param  sent true if message was sent (by the source); false if received
	 * @param  messageType String message type understood by listeners
	 * @param  message the message itself
	 */
	public MessageEvent(Object source,
						boolean sent,
						String messageType,
						Object message) {
		this(source, sent, messageType, null, message);
	}

	/**
	 * Create a message event.
	 *
	 * @param  source source object for superclass
	 * @param  sent true if message was sent (by the source); false if received
	 * @param  messageType String message type understood by listeners
	 * @param  result String result understood by listeners
	 * @param  message the message itself
	 */
	public MessageEvent(Object source,
						boolean sent,
						String messageType,
						String result,
						Object message) {
		super(source);
		timeStamp = new Date();
		this.sent = sent;
		this.messageType = messageType;
		this.result = result;
		this.message = message;
	}
	
	/**
	 * Print for debugging.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append("[").append(dateFmt.format(timeStamp));
		sb.append(",").append(sent ? "sent" : "rcvd");
		sb.append(",").append(messageType);
		sb.append(":\n ").append(message);
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Return true if this message was sent by the event source;
	 * false if received.
	 *
	 * @return value of {@link #sent}
	 */
	public boolean getSent() {
		return sent;
	}

	/**
	 * Return message type understood by listeners.
	 *
	 * @return value of {@link #messageType}
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * Return message type understood by listeners.
	 *
	 * @return value of {@link #messageType}
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Return the message itself.
	 *
	 * @return value of {@link #message}
	 */
	public Object getMessage() {
		return message;
	}

	/**
	 * @return the {@link #timeStamp}
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Convenience for returning the {@link #message} as a String.
	 * @return null if message null; else message.toString()
	 */
	public String getMessageAsString() {
		if (message == null)
			return null;
		else
			return message.toString();
	}

	/**
	 * Tell whether this is a quit message, indicating a connection termination, e.g.
	 * @return true if {@link #QUIT} == {@link #messageType}
	 */
	public boolean isQuitMsg() {
		return QUIT.equals(messageType);
	}
}
