/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.util.regex.Matcher;

import jess.Defrule;
import jess.PrettyPrinter;
import edu.cmu.pact.Utilities.trace;

class RulePrinter extends PrettyPrinter{

	/** Text of the rule. */
	private String ruleText = "";

	/** Pattern removes names of local variables created by pattern ? and $? fields. */
	static final String BLANK_VAR = "\\?_\\w*_\\w*";
	
	/** Pattern matches (declare ...) statements in rules. */
	private static final String DECLARE_PATTERN = "\\s*\\(declare(\\s*\\([^)]+\\))(\\))?";
	
	/** Pattern matches individual declarations withing (declare ...) tatements in rules. */
	private static final String DECLARE_MORE_PATTERN = "^(\\s*\\([^)]+\\))(\\))?";
	
	/** Compiled recognizer for DECLARE_PATTERN. */
	private static java.util.regex.Pattern declarePattern = null;
	
	/** Compiled recognizer for DECLARE_MORE_PATTERN. */
	private static java.util.regex.Pattern declareMorePattern = null;
			
	/**
	 * Constructor sets {@link #ruleText}.
	 * @param rule
	 */
	public RulePrinter(Defrule rule){
		super(rule);
		ruleText = super.toString();
	}
	
    /**
     * Convert a 0-based index to a line number.
     * @param  i increment this by one to get a natural number
     * @return result from {@link #patternIndexToLineNo(int, String)}
     */
	String patternIndexToLineNo(int i) {
		return patternIndexToLineNo(i, ruleText);
	}		

	/**
	 * Convert a 0-based index to a line number.
	 * @param  i increment this by one to get a natural number
	 * @param  text string to scan
	 * @return "line 1", "line 22", etc.; empty string if negative
	 */
	static String patternIndexToLineNo(int i, String text) {
		if (i < 0)
			return "";
		if (trace.getDebugCode("mt")) trace.outNT("mt", "patternIndexToLineNo text:\n"+text);
		if (declarePattern == null)
			declarePattern = java.util.regex.Pattern.compile(DECLARE_PATTERN);
		Matcher m = declarePattern.matcher(text);
		boolean declareFound = m.find();
		if (declareFound) {
			int declareCount = 1;
			String declare = m.group(1);
			if (trace.getDebugCode("mt")) trace.outNT("mt", "patternIndexToLineNo declare["+declareCount+
					"]="+declare);
			if (m.group(2) == null) {
				if (declareMorePattern == null)
					declareMorePattern =
						java.util.regex.Pattern.compile(DECLARE_MORE_PATTERN,
								java.util.regex.Pattern.MULTILINE);
				int end = m.end();
				if (trace.getDebugCode("mt")) trace.outNT("mt", "text.substring("+end+")="+text.substring(end));
				m = declareMorePattern.matcher(text.substring(m.end()));
				while (declareFound == m.find()) {
					declare = m.group(1);
					if (declare != null)
						++declareCount;
					if (trace.getDebugCode("mt")) trace.outNT("mt", "patternIndexToLineNo declare["+declareCount+
							"]="+declare);
					if (m.group(2) != null)
						break;
				} 
			}
			i += declareCount;
		}
		++i;            // convert index to natural number
		StringBuffer result = new StringBuffer("line ");
		return result.append(String.valueOf(i)).toString();
	}

	/**
	 * Generate line numbers for all lines but the first, then double-space
	 * the rule text.
	 * @return pretty-printed rule text, modified as above
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String[] tkns = ruleText.split("\n");
		int i = 0;
		StringBuffer sb = new StringBuffer(i < tkns.length ? tkns[i++] : "");
		while (i < tkns.length) {
			sb.append("\n\n");
			if (i < 10)           // this good enough for 100-line rules
				sb.append(" ");
			sb.append(String.valueOf(i)).append(tkns[i++]);
		}
		ruleText = sb.toString();
		return ruleText.replaceAll(RulePrinter.BLANK_VAR, "?");
	}
}
