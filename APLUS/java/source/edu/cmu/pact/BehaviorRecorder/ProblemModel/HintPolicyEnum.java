package edu.cmu.pact.BehaviorRecorder.ProblemModel;

/**
 * A type for different choices of hint policy.
 */
public enum HintPolicyEnum {
	HINTS_UNBIASED("Always Follow Best Path"),
	HINTS_BIASED_BY_CURRENT_SELECTION_ONLY("Bias Hints by Current Selection Only"),
	HINTS_BIASED_BY_PRIOR_ERROR_ONLY("Bias Hints by Prior Error Only"),
	HINTS_BIASED_BY_ALL("Use Both Kinds of Bias");
	
	public static final HintPolicyEnum DEFAULT = HintPolicyEnum.HINTS_BIASED_BY_ALL;
	
	private String description;
	
	private HintPolicyEnum(String description) {
		this.description = description;
	}
	
	public boolean isBiasedByCurrentSelection() {
		return (this.equals(HintPolicyEnum.HINTS_BIASED_BY_ALL)
				|| this.equals(HintPolicyEnum.HINTS_BIASED_BY_CURRENT_SELECTION_ONLY));
	}
	
	public boolean isBiasedByPriorError() {
		return (this.equals(HintPolicyEnum.HINTS_BIASED_BY_ALL) 
				|| this.equals(HintPolicyEnum.HINTS_BIASED_BY_PRIOR_ERROR_ONLY));
	}
	
	/**
	 * Human readable string
	 * @return the description of the enum
	 */
	public String toString() {
		return description;
	}
	
	/**
	 * Converts a string to a HintPolicyEnum
	 * @param text the string to be converted
	 * @return the description of the enum referent 
	 * 	to the text input or the default one if there's no match
	 */
	public static HintPolicyEnum fromString(String text) {
		return fromString(text, false);
	}
	
	/**
	 * Converts a string to a HintPolicyEnum.
	 * @param text the string to be converted
	 * @param strict if true, require a match
	 * @return the {@link #description} of the enum referent to the text input or, 
	 * 	       if there's no match, if strict, return null; else return the default one
	 */
	public static HintPolicyEnum fromString(String text, boolean strict) {
		
		if (text == null)
			return (strict ? null : DEFAULT);
		
		for (HintPolicyEnum hbe : HintPolicyEnum.values()) {
	        if (text.equalsIgnoreCase(hbe.toString())) {
	          return hbe;
	        }
	      }
		return (strict ? null : DEFAULT);
	}

	/**
	 * Just dump the {@link #values()}.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		HintPolicyEnum[] values = values();
		for(int i = 0; i < values.length; ++i)
			System.out.printf(" %d. %s\n", i, values[i]);
	}
}
