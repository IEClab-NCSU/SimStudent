/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cl.utilities.sm.SMParserSettings;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;

/**
 * Substitute for {@link cl.utilities.sm.SymbolManipulator} and
 * {@link edu.cmu.old_pact.cmu.sm.SymbolManiplator}, to handle the cases where
 * we may or may not have the CL libraries. See {@link VersionInformation#includesCL()}.
 */
public class SymbolManipulator {
	
	/**
	 * Access to {@link cl.utilities.sm.SMParserSettings}.
	 */
	public static class Settings {
		public static final Object MIXED_NUMBERS_E_AS_VAR = (VersionInformation.includesCL() ?
				cl.utilities.sm.SMParserSettings.MIXED_NUMBERS_E_AS_VAR : null); 
	}

	private static final String DEFAULT_FUNCTION = "convertMixedToImproper";;
	
	/** The underlying object. */
	private final Object delegate;
	
	/**
	 * Print an optional error message and exit w/ status 2.
	 * @param errMsg can be null; will append period '.'
	 */
	private static void usageExit(String errMsg) {
		if (errMsg != null)
			System.err.printf("%s. ", errMsg);
		System.err.println("Usage:\n"+
				"  java -cp ... "+SymbolManipulator.class.getName()+" [-f function] expr...\n"+
				"where--\n"+
				"  function is the method to call, one of {standardize, patternMatches, "+DEFAULT_FUNCTION+" (default)};\n"+
				"  expr... are expression(s) to pass to function.");
		System.exit(2);
	}

	/**
	 * @param args see {@link #usageExit(String)}
	 */
	public static void main(String[] args) {
		SymbolManipulator sm = new SymbolManipulator();
		String function = DEFAULT_FUNCTION;
		int i = 0;
		if (i < args.length && args[i].startsWith("-")) {
			switch (args[i++].charAt(1)) {
			case 'h': case 'H':
				usageExit(null); break;
			case 'f': case 'F':
				function = args[i++]; break;
			default:
				usageExit("Unknown switch '-"+args[i].charAt(1)+"'"); break;
			}
		}
		if ("convertMixedToImproper".equalsIgnoreCase(function)) {
			System.out.printf("Using SM.%s from class %s, not using delegate directly:\n",
					function, sm.delegate.getClass().getName());
			for (; i < args.length; ++i)
				System.out.printf("  %10s = %10s\n", args[i], sm.convertMixedToImproper(args[i]));
		} else if ("standardize".equalsIgnoreCase(function)) {
			System.out.printf("Using SM.%s from class %s, using delegate directly:\n",
					function, sm.delegate.getClass().getName());
			for (; i < args.length; ++i) {
				try {
					System.out.printf("  %10s = %10s\n", args[i],
							((cl.utilities.sm.SymbolManipulator) sm.delegate).standardize(args[i]));
				} catch (Throwable t) {
					System.out.printf("  %10s = %10s; cause %s\n", args[i], t.toString(), t.getCause());
				}	
			}
		} else if ("patternMatches".equalsIgnoreCase(function)) {
			System.out.printf("Using SM.%s from class %s, using delegate directly:\n",
					function, sm.delegate.getClass().getName());
			for (; i < args.length; i+=2) {
				try {
					System.out.printf("  %10s, %10s: patternMatches %b\n", args[i], args[i+1],
							((cl.utilities.sm.SymbolManipulator) sm.delegate).patternMatches(args[i], args[i+1]));
				} catch (Throwable t) {
					System.out.printf("  %10s = %10s; cause %s\n", args[i], t.toString(), t.getCause());
				}	
			}
		} else
			usageExit("Unsupported function "+function);
	}

	/**
	 * Default constructor.
	 * @deprecated for CL use 
	 * {@link cl.utilities.sm.SymbolManipulator#SymbolManipulator(cl.utilities.sm.SMParserSettings)}
	 */
	public SymbolManipulator() {
		if (VersionInformation.includesCL())
			delegate = new cl.utilities.sm.SymbolManipulator(SMParserSettings.MIXED_NUMBERS_E_AS_VAR);
		else
			delegate = new edu.cmu.old_pact.cmu.sm.SymbolManipulator();
	}
	
	/**
	 * Recommended constructor.
	 * @param parserSettings should be an instance of {@link cl.utilities.sm.SMParserSettings}
	 * See {@link cl.utilities.sm.SymbolManipulator#SymbolManipulator(cl.utilities.sm.SMParserSettings)}
	 */
	SymbolManipulator(Object parserSettings) {
		if (VersionInformation.includesCL())
			delegate = new cl.utilities.sm.SymbolManipulator((cl.utilities.sm.SMParserSettings) parserSettings);
		else
			delegate = new edu.cmu.old_pact.cmu.sm.SymbolManipulator();
	}
	
	/**
	 * Access to {@link cl.utilities.sm.SymbolManipulator#canSimplify(String)}
	 * @param expString an expression
	 * @return true if expression can be made simpler
	 * @throws Exception
	 */
	public boolean canSimplify(String expString) throws Exception {
		try {
			if (VersionInformation.includesCL())
				return ((cl.utilities.sm.SymbolManipulator) delegate).canSimplify(expString);
			else
				return ((edu.cmu.old_pact.cmu.sm.SymbolManipulator) delegate).canSimplify(expString);
		} catch (Throwable t) {
			throw new Exception(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}
	
	/**
	 * Access to {@link cl.utilities.sm.SymbolManipulator#algebraicEqual(String, String)}
	 * @param expr0 first equation
	 * @param expr1 second equation
	 * @return true if expression can be made simpler
	 * @throws Exception
	 */
	public boolean algebraicEqual(String expr0, String expr1) throws Exception {
		try {
			if (VersionInformation.includesCL())
				return ((cl.utilities.sm.SymbolManipulator) delegate).algebraicEqual(expr0, expr1);
			else
				return ((edu.cmu.old_pact.cmu.sm.SymbolManipulator) delegate).algebraicEqual(expr0, expr1);
		} catch (Throwable t) {
			throw new Exception(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}
	
	/**
	 * Access to {@link cl.utilities.sm.SymbolManipulator#algebraicMatches(String, String)}
	 * @param eq0 first equation
	 * @param eq1 second equation
	 * @return true if expression can be made simpler
	 * @throws Exception
	 */
	public boolean algebraicMatches(String eq0, String eq1) throws Exception {
		try {
			if (VersionInformation.includesCL())
				return ((cl.utilities.sm.SymbolManipulator) delegate).algebraicMatches(eq0, eq1);
			else
				return ((edu.cmu.old_pact.cmu.sm.SymbolManipulator) delegate).algebraicMatches(eq0, eq1);
		} catch (Throwable t) {
			throw new Exception(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}
	
	/**
	 * Access to {@link cl.utilities.sm.SymbolManipulator#algebraicMatches(String, String)}
	 * @param expr0 expression to test against elements of exprList
	 * @param exprList 1 or more other expressions, as Strings
	 * @return 0 if exprList[0] matches at least one of the other exprList[] elements;
	 * 			1 if it matches none of them, 2 if error
	 * @throws Exception
	 */
	public int expressionMatches(String expr0, List<String> exprList) throws Exception {
		try {
			if (VersionInformation.includesCL()) {
				cl.utilities.sm.SymbolManipulator sm = (cl.utilities.sm.SymbolManipulator) delegate;
				cl.utilities.sm.Expression expression0 = sm.parseCE(expr0);
				for (String expr1: exprList) {
					boolean result = expression0.algebraicEqual(sm.parseCE(expr1));
	                if (trace.getDebugCode("si"))
	                	trace.out("si", "\""+expr0+"\""+(result ? "matches" : "doesn't match")+"\""+expr1+"\"");
					if (result)
						return 0;       // matches exprList[i]
				}
	            return 1;               // mismatch on all
			} else {
				edu.cmu.old_pact.cmu.sm.SymbolManipulator sm = (edu.cmu.old_pact.cmu.sm.SymbolManipulator) delegate;
				edu.cmu.old_pact.cmu.sm.Expression expression0 = sm.parse(expr0);
				for (String expr1: exprList) {
					boolean result = expression0.algebraicEqual(sm.parse(expr1));
	                if (trace.getDebugCode("si")) trace.out("si", "\"" + expression0 + "\"" + (result ? "matches" : "doesn't match") + "\"" + expr1 + "\"");
					if (result)
						return 0;       // matches exprList[i]
				}
	            return 1;               // mismatch on all
			}
		} catch (Throwable t) {
			throw new Exception(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}

	/**
	 * <p>Evaluate the given expression as a desk calculator would.</p> Uses {@link cl.utilities.sm.Expression#compute()}.
	 * @param expr arithmetic expression to compute. 
	 * @return calculated value
	 * @throws RuntimeException
	 */
	public static double calc(String expr) throws RuntimeException {
		try {
			if (VersionInformation.includesCL()) {
				cl.utilities.sm.SymbolManipulator sm = 
					new cl.utilities.sm.SymbolManipulator(cl.utilities.sm.SMParserSettings.MIXED_NUMBERS_E_AS_CONST); 
				cl.utilities.sm.Expression expression = sm.parseCE(expr);
				double d = expression.compute();
				return d;
			} else {
				edu.cmu.old_pact.cmu.sm.SymbolManipulator sm = new edu.cmu.old_pact.cmu.sm.SymbolManipulator();
				String result = sm.simplify(expr);
				try {
					double d = Double.parseDouble(result);
					return d;
				} catch (NumberFormatException nfe) {
					throw new ClassCastException("does not simplify to number: "+nfe);
				}
			}
		} catch (Throwable t) {
			trace.err("calc: error on expression \""+expr+"\": "+t);
			throw new RuntimeException(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}

	/**
	 * Access to {@link cl.utilities.sm.SymbolManipulator#parseCE(String)} or equivalent.
	 * @param expr0 expression to parse
	 * @return object containing instance of {@link cl.utilities.sm.Expression} or equivalent
	 * @throws Exception
	 */
	public Expression parseCE(String expr0) throws Exception {
		try {
			if (VersionInformation.includesCL())
				return new Expression(((cl.utilities.sm.SymbolManipulator) delegate).parseCE(expr0));
			else
				return new Expression(((edu.cmu.old_pact.cmu.sm.SymbolManipulator) delegate).parse(expr0));
		} catch (Throwable t) {
			throw new Exception(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}

	/**
	 * Access to {@link cl.utilities.sm.SymbolManipulator#parseCE(String)} or equivalent.
	 * @param expr0 expression to simplify
	 * @return result from delegate's method
	 * @throws Exception
	 */
	public String simplify(String expr) throws Exception {
		try {
			if (VersionInformation.includesCL())
				return ((cl.utilities.sm.SymbolManipulator) delegate).simplify(expr);
			else
				return ((edu.cmu.old_pact.cmu.sm.SymbolManipulator) delegate).simplify(expr);
		} catch (Throwable t) {
			throw new Exception(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}

	/**
	 * Access to {@link cl.utilities.sm.SymbolManipulator#parseCE(String)} or equivalent.
	 * @param expr expression to simplify
	 * @return result from delegate's method
	 * @throws Exception
	 */
	public String simplifyMixed(String expr) throws Exception {
		try {
			if (VersionInformation.includesCL())
				return ((cl.utilities.sm.SymbolManipulator) delegate).convertMixedToImproper(expr);
			else
				return convertMixedToImproper(expr);
		} catch (Throwable t) {
			throw new Exception(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}

	/**
	 * Access to {@link cl.utilities.sm.SymbolManipulator#convertMixedToImproper(String)} or rough
	 * local equivalent {@link #convertMixedToImproperInternal(String)} if CL code is unavailable.
	 * @param expr mixed number constant
	 * @return expr unchanged if not a fraction or mixed number; else single fraction, possibly improper
	 */
	public String convertMixedToImproper(String expr) {
		try {
			if (VersionInformation.includesCL()) {
				cl.utilities.sm.SymbolManipulator sm = (cl.utilities.sm.SymbolManipulator) delegate;
				return sm.convertMixedToImproper(expr);
			}
			else
				return this.convertMixedToImproperInternal(expr);
		} catch (Throwable t) {
			throw new RuntimeException(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}
	
	/** Simple fraction pattern for {@link #convertMixedToImproperInternal(String)}. */
	private static final Pattern FractionPattern = Pattern.compile("^([-+]?)\\s*([0-9]+)/([0-9]+)");
	
	/** Mixed whole number-fraction pattern for {@link #convertMixedToImproperInternal(String)}. */
	private static final Pattern MixedNumberPattern = Pattern.compile("^([-+]?)\\s*([0-9]+)\\s+([0-9]+)/([0-9]+)");

	/**
	 * Substitute for {@link cl.utilities.sm.SymbolManipulator#convertMixedToImproper(String)}
	 * when CL code is not available. Assumes whole number, numerator and denominator are integer
	 * constants.
	 * @param expr
	 * @return expr unchanged if not a fraction or mixed number; else a fraction expression,
	 *         possibly improper
	 */
	private String convertMixedToImproperInternal(String expr) {
		if (expr == null)
			return null;
		String texpr = expr.trim();
		boolean mfMatched = false, mmMatched = false;
		try {
			Matcher mf = FractionPattern.matcher(texpr);
			mfMatched = mf.find();
			Matcher mm = MixedNumberPattern.matcher(texpr);
			mmMatched = mm.find();   // must call m.find() before trying m.group(i)
			trace.outNT("si", "mfMatched "+mfMatched+", mmMatched "+mmMatched);
			if (mfMatched)           // no whole number present
				return texpr;
			if (!mmMatched)          // some non-digit present?
				return expr;
			long[] n = new long[mm.groupCount()-1];
			for (int i = 0; i < n.length; ++i)
				n[i] = Long.valueOf(mm.group(i+2));
			StringBuffer result = new StringBuffer();
			if ("-".equals(mm.group(1)))
				result.append('-');
			long newNumerator = (n[0] * n[2]) + n[1];  // (whole*denominator)+numerator
			result.append(newNumerator).append('/').append(n[2]);
			return result.toString();
		} catch (Exception e) {
			trace.errStack("convertMixedToImproper: matched "+mmMatched+"; match fails on "+texpr, e);
			return null;
		}
	}
}
