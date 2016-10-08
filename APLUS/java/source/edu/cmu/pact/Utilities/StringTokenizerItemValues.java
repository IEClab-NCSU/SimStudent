package edu.cmu.pact.Utilities;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

//import edu.cmu.pact.Utilities.trace;


	/**
	 * StringTokenizerItemValues is a partial extension of StringTokenizer.
	 * It supports the delemeter as a regular char in the string text through
	 * the combination of pre_delimeter and delimeter.
	 *
	 * @author zzhang
	 */


public class StringTokenizerItemValues {
	// this class is only a partial extension of StringTokenizer
	private StringTokenizer stringTokenizer;
	
	// delemeter as char instead of as String
	private char delim;
	
	// pre_delimeter as char
	private char predelim;
	
	// parsing string
	private String str;
	
	public StringTokenizerItemValues (String str, char delim, char predelim) {
		this (str, delim, predelim, false);
	}
	
	public StringTokenizerItemValues (String str, char delim, char predelim, boolean returnDelims) {
		
		stringTokenizer = new StringTokenizer(str, "" + delim, returnDelims);
		
		this.delim = delim;
		this.predelim = predelim;
		this.str = str;
	}
	
	/**
	 * Tells if there are more tokens.
	 *
	 * @return true if the next call of nextToken() will succeed
	 */
	public boolean hasMoreTokens() {
		return stringTokenizer.hasMoreTokens();
	}
	
	/**
	 * Returns the nextToken of the string.
	 *
	 * @return the next token with respect to the delim characters without prefixed predelim
	 * @throws NoSuchElementException if there are no more tokens
	 */
	 public String nextToken() throws NoSuchElementException {
		 String currentToken = (String) stringTokenizer.nextToken();
		 StringBuffer nextTokenBuffer = new StringBuffer(currentToken);
		 
		 boolean flag = isDelimInToken(currentToken);
		 
		 while (flag && stringTokenizer.hasMoreTokens()) {
			 // delim is a regular char in the token and replace predelim with delim
			 nextTokenBuffer.deleteCharAt(nextTokenBuffer.length() - 1);	 
			 nextTokenBuffer.append(delim);
			 
			 // continue to process the next stringTokenizer's token
			 currentToken = (String) stringTokenizer.nextToken();
			 nextTokenBuffer.append(currentToken);
			 
			 flag = isDelimInToken(currentToken);
		 }
		 
		 //	delim is a regular char in the token and replace predelim with delim
		 if (flag) {
			 nextTokenBuffer.deleteCharAt(nextTokenBuffer.length() - 1);
			 nextTokenBuffer.append(delim);
		 }
	
		 return nextTokenBuffer.toString();
	 }
	
	/**
	 * Tells if delim is in the tokens.
	 *
	 * @return true 
	 * 		if predelim is the currentToken's last char and either
	 * 		1. currentToken is not the last token
	 * 		2. or currentToken is the last one and delim is str's last char.
	 */
		
	private boolean isDelimInToken (String currentToken) {
		int tokenLength = currentToken.length();
		
		// empty string
		if (tokenLength == 0)
			return false;
		
		// predelim is the currentToken's last char
		if (currentToken.lastIndexOf(predelim) == tokenLength - 1) {
			// not the last token, so this token is really parsed by delim
			if (stringTokenizer.hasMoreTokens())
				return true;
			// the last token, the str last char is delim
			else if (str.lastIndexOf(delim) == str.length() - 1)
				return true;
		}
		
		return false;
	}
	
	/**
	 * This does the same as hasMoreTokens. This is the
	 * <code>Enumeration</code> interface method.
	 *
	 * @return true, if the next call of nextElement() will succeed
	 * @see #hasMoreTokens()
	 */
	public boolean hasMoreElements() {
		return hasMoreTokens();
	}

	/**
	 * This does the same as nextTokens. This is the
	 * <code>Enumeration</code> interface method.
	 *
	 * @return the next token with respect to the current delimiter characters
	 * @throws NoSuchElementException if there are no more tokens
	 * @see #nextToken()
	 */
	public Object nextElement() throws NoSuchElementException {
		return nextToken();
	}
}
