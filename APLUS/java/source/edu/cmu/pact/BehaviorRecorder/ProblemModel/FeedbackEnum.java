package edu.cmu.pact.BehaviorRecorder.ProblemModel;

/**
 * A type for different choices of feedback display.
 */
public enum FeedbackEnum {
	SHOW_ALL_FEEDBACK("Show All Feedback"),  // show all feedback as it's sent by the tutor
	DELAY_FEEDBACK("Delay Feedback"),     	 // delay feedback until the Done button is pressed
	HIDE_ALL_FEEDBACK("Hide All Feedback");  // never show feedback

	public static final FeedbackEnum DEFAULT = FeedbackEnum.SHOW_ALL_FEEDBACK;
			
	private String description;
	
	private FeedbackEnum(String description) {
		this.description = description;
	}
	
	/**
	 * Human readable string
	 * @return the description of the enum option
	 */
	public String toString() {
		return description;
	}

	/**
	 * Converts a string to a FeedbackEnum
	 * @param text the string to be converted
	 * @return the description of the enum referent 
	 * 	to the text input, the default one if there's no match
	 */
	public static FeedbackEnum fromString(String text) {
		
		if (text == null)
			return DEFAULT;
		
		for (FeedbackEnum fe : FeedbackEnum.values()) {
	        if (text.equalsIgnoreCase(fe.toString())) {
	          return fe;
	        }
	      }
		
		if (text.equalsIgnoreCase(Boolean.TRUE.toString()))
			return FeedbackEnum.HIDE_ALL_FEEDBACK;
		
		if (text.equalsIgnoreCase(Boolean.FALSE.toString()))
			return FeedbackEnum.SHOW_ALL_FEEDBACK;
		
		return DEFAULT;
	}
}
