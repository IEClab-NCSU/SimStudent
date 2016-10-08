/**
 * Copyright 2014 Carnegie Mellon University.
 */

/**
 * An object that bears a message for logging and a reply mechanism. The reply mechanism
 * might be, e.g., a socket or an HTTP response object.
 */
public class MessageReplyPair {
	
	/** Content to log. */
	private final String message;
	
	/** Whether a reply is required. Default is true. */
	private boolean replyRequired = true;

	/** Object, such as HttpResponse object or socket, by which to send the reply. */
	private final Object replyMechanism;

	/**
	 * Set all fields.
	 * @param message
	 * @param replyMechanism
	 */
	public MessageReplyPair(String message, Object replyMechanism) {
		this.message = message;
		this.replyMechanism = replyMechanism;
	}

	/**
	 * @return 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format("[%.70s%s; reply obj %s@%d]",
				message, message.length() > 70 ? "..." : "",
				replyMechanism == null ? null : replyMechanism.getClass().getSimpleName(),
				replyMechanism == null ? 0 : replyMechanism.hashCode());
	}
	
	/**
	 * @return {@link #message}
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Return true if a reply is still required; false if not.
	 * @return {@link #replyRequired}
	 */
	public boolean isReplyRequired() {
		return replyRequired;
	}

	/**
	 * @param replyRequired new value for {@link #replyRequired}
	 */
	public void setReplyRequired(boolean replyRequired) {
		this.replyRequired = replyRequired;
	}

	/**
	 * @return {@link #replyMechanism}
	 */
	public Object getReplyMechanism() {
		return replyMechanism;
	}
}
