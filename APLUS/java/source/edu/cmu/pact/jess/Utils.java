/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.Utilities.trace;

import jess.Context;
import jess.JessException;
import jess.JessToken;
import jess.RU;
import jess.ReaderTokenizer;
import jess.Value;
import jess.ValueVector;

/**
 * Utility functions for this package.
 */
public class Utils {

	/** Pattern to find strings that cannot be Jess symbols. */
	private static final Pattern notSymbol =
		Pattern.compile("[~`!@%^&(){}|\\\"':;\"',]|\\s|\\[|\\]");

	/**
	 * Convert a Jess {@link Value} into a Java value. Converts<ul>
	 * <li>all numerics to double,</li>
	 * <li>lists to {@link ArrayList} (recursively),</li>
	 * <li>string and non-numeric symbols to {@link String},</li>
	 * <li>all others as toString().</li>
	 * </ul>
	 * @param jessVal
	 * @param context Jess evaluation context
	 * @return 
	 * @throws JessException
	 */
	public static Object jessValueToJava(Value jessVal, Context context) throws JessException {
		Object result;
		if(jessVal == null)
			result = null;
		else {
			 if(jessVal.isLexeme(context)) {
				 if(jessVal.type() == RU.SYMBOL && jessVal.isNumeric(context))
					result = jessVal.floatValue(context);
				else
					result = jessVal.stringValue(context);
			 }
			 else if(jessVal.isNumeric(context)) {
				 if(jessVal.type() == RU.INTEGER)
					 result = jessVal.intValue(context);
				 else
					 result = jessVal.floatValue(context);					 
			 }
			 else if(jessVal.type() == RU.JAVA_OBJECT)
				 result = jessVal.javaObjectValue(context);
			 else if(jessVal.type() == RU.NONE)
				 result = null;
			 else if(jessVal.type() == RU.LIST) {
				 result = new ArrayList<Object>();
				 ValueVector vv = jessVal.listValue(context);
				 for(int i = 0; i < vv.size(); ++i)
					 ((List<Object>) result).add(jessValueToJava(vv.get(i), context));
			 }
			 else
				 result = jessVal.resolveValue(context).toString();
		}
		return result;
	}

	/**
	 * Escape a value, if necessary.
	 * @param  s String to escape
	 * @return s, with embedded quotes and backslashes escaped; special cases
	 *         "nil" if s is null;
	 *         "" if s is empty;
	 */
	public static String escapeString(String s) {
		return escapeString(s, false);
	}

	/**
	 * Escape a value, if necessary.
	 * @param  s String to escape
	 * @param  coerceSymbolsToStrings if true, convert strings that could be of type
	 *         {@link RU#SYMBOL} to double-quoted {@link RU#STRING} values
	 * @return s, with embedded quotes and backslashes escaped; special cases
	 *         "nil" if s is null;
	 *         "" if s is empty;
	 */
	public static String escapeString(String s, boolean coerceSymbolsToStrings) {
		int ruType = -1;
		try {
			ruType = getJessType(s, null);
		} catch (JessException je) {
			trace.err("exception from getJessType shouldn't happen on null Value[]: "+
					je);
			je.printStackTrace();
			return s;
		}
		switch(ruType) {
		case RU.NONE:
			return "nil";
		case RU.INTEGER:
		case RU.LONG:
		case RU.FLOAT:
		case RU.SYMBOL:
			if(!coerceSymbolsToStrings)
				return s;
			else
				/* fall through */ ;
		case RU.STRING:
		default:
				break;
		}
		if (s.length() <= 0)
			return "\"\"";    /* quoted empty string */
		
		StringBuffer result = new StringBuffer("\"");
		StringTokenizer tkzr = new StringTokenizer(s, "\"\\", true);
		while (tkzr.hasMoreTokens()) {
			String tkn = tkzr.nextToken();
			if (tkn.length() > 1)
				result.append(tkn);
			else {
				switch(tkn.charAt(0)) {
				case '\\':
					result.append("\\\\");
					break;
				case '\"':
					result.append("\\\"");
					break;
				default:
					result.append(tkn);
					break;
				}
			}
		}
		result.append("\"");
		return result.toString();
	}

	/**
	 * Try to determine the type of a string as Jess's parser would.
	 * @param  s String to examine
	 * @param  v single-element array to return converted Value, if not null
	 * @return one of the type constants in {@link RU}; special cases<ul>
	 *          RU.NONE if s is null;
	 *			RU.STRING if s is the empty string
	 * @throws JessException if error from {@link Value} constructor; no
	 *          exception if v argument is null
	 */
	public static int getJessType(String s, Value[] v)
			throws JessException {
		return getJessType(s, v, false);
	}

	/**
	 * Try to determine the type of a string as Jess's parser would.
	 * @param  s String to examine
	 * @param  v single-element array to return converted Value, if not null
	 * @param  coerceSymbolsToStrings if true, always return {@link RU#STRING} for symbols as well as strings
	 * @return one of the type constants in {@link RU}; special cases<ul>
	 *          RU.NONE if s is null;
	 *			RU.STRING if s is the empty string
	 * @throws JessException if error from {@link Value} constructor; no
	 *          exception if v argument is null
	 */
	public static int getJessType(String s, Value[] v, boolean coerceSymbolsToStrings)
			throws JessException {
		if (s == null)
			return RU.NONE;
		if (s.length() <= 0)
			return RU.STRING;
		try {
			int i = Integer.parseInt(s);
			if (v != null)
				v[0] = new Value(i, RU.INTEGER);
			return RU.INTEGER;
		} catch (NumberFormatException nfe) {}
		try {
			long el = Long.parseLong(s);
			if (v != null)
				v[0] = new Value(el, RU.LONG);
			return RU.LONG;
		} catch (NumberFormatException nfe) {}
		try {
			double d = Double.parseDouble(s);
			if (v != null)
				v[0] = new Value(d, RU.FLOAT);
			return RU.FLOAT;
		} catch (NumberFormatException nfe) {}
		if(!coerceSymbolsToStrings) {
			try {
				Matcher m = notSymbol.matcher(s);
				if (!m.find() && countTokens(s) < 2) {
					if (v != null)
						v[0] = new Value(s, RU.SYMBOL);
					return RU.SYMBOL;
				}
			} catch (JessException je) {
				trace.err("WMEEditor.getJessType("+s+") error matching symbol: "+je+
						(je.getCause() == null ? "" : "; cause "+je.getCause()));
				// fall through to STRING
			}
		}
		if (v != null)
			v[0] = new Value(s, RU.STRING);
		return RU.STRING;
	}

	/**
	 * Invoke Jess's parser to count the tokens in a string.
	 * @param s string to analyze
	 * @return count of calls to {@link jess.Tokenizer#nextToken()} before EOF
	 * @throws JessException on error from {@link jess.Tokenizer#nextToken()}
	 */
	static int countTokens(String s) throws JessException {
		ReaderTokenizer tkzr = new ReaderTokenizer(new StringReader(s), true);
		int result = 0;
		for(JessToken tk = tkzr.nextToken(); tk != null && !tk.isEOF(); tk = tkzr.nextToken())
			++result;
		return result;
	}
	
	

}
