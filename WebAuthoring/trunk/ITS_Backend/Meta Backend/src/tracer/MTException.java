package tracer;

import jess.JessException;

/**
 * Custom exception used by our userfunctions. Provides a way for jess to provide
 * our tracer with feedback on the activated production rules.
 * 
 * BUGGYMATCH: Student sai matched a buggy production rule
 * BUGGYFAIL: Student sai failed to match a buggy production rule
 * HINT: Found a hint for an activated production rule
 * NOHINT: No hints available
 * FAIL: Student sai differs from tutor sai
 * AUTO: Auto throw exception turned on
 *  
 * @author Alex Xiao
 *
 */

public class MTException extends JessException {
	
	private String message;

	private MTExceptionType type;
	
	public enum MTExceptionType {
		BUGGYMATCH, BUGGYFAIL, HINT, FAIL, NOHINT, AUTO, OTHER
	}
	
	public MTException(String routine, String message, String data) {
        super(routine, message, data);
    }
	
	public MTException(MTExceptionType type, String message) {
		super("", message, "");
		this.setType(type);
		this.message = message;
	}

	public MTExceptionType getType() {
		return type;
	}

	public void setType(MTExceptionType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
