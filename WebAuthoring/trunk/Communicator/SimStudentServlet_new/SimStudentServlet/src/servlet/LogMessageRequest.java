package servlet;
/**
 * Class to wrap up a log message request
 * @author Patrick Nguyen
 *
 */
public class LogMessageRequest extends RequestMessage {
	/** Content to log. */
	private final String message;
	
	public LogMessageRequest(String message){
		this.message=message;
	}
	
	/**
	 * @return {@link #message}
	 */
	public String getMessage() {
		return message;
	}

}
