/**
 * Copyright 2010 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.Arrays;
import java.util.regex.Pattern;

import edu.cmu.pact.Utilities.trace;
import fri.patterns.interpreter.parsergenerator.Lexer;
import fri.patterns.interpreter.parsergenerator.Token;
import fri.patterns.interpreter.parsergenerator.lexer.LexerBuilder;
import fri.patterns.interpreter.parsergenerator.semantics.TreeBuilderSemantic;
import fri.patterns.interpreter.parsergenerator.syntax.Syntax;
import fri.patterns.interpreter.parsergenerator.syntax.builder.SyntaxSeparation;

/**
 * A function to tell whether the constants in an expression conform to one or more patterns.
 * This can be used, e.g., to verify that all the numeric values in an equation are either
 * integers or dollar amounts ("d.dd").
 */
public class constantsConform extends TreeBuilderSemantic {
	
	/**
	 * <p>Tell whether each constant in an expression conforms to at least of a given set of patterns.</p>
	 * This can be used, e.g., to verify that all the numeric values in an equation are either
	 * integers or dollar amounts ("d.dd")
	 * @param expr arithmetic expression with numbers
	 * @param strPatterns regular expressions (to be compiled): each number should match one of these
	 * @return 0 if each number matches some pattern; 1 if some number matches no pattern; 2 if error 
	 */
	public boolean constantsConform(String expr, String... strPatterns) {
		int result = exec(expr, strPatterns);
		return (result == 0);
	}

	/**
	 * Test harness. Calls {@link System#exit(int)} with 0 if at least one match.
	 * @param args 1st arg is expression to test, second and others are patterns
	 */
	public static void main(String[] args) throws Throwable {
		if (args.length < 1 || args[0].contains("h")) {
			System.err.println("Usage:\n"
					+"  constantsConform expr pattern ...\n"
					+"where--\n"
					+"  expr is the expression to scan;\n"
					+"  pattern ... is one or more regular expressions");
		}
		
		constantsConform cc = new constantsConform();
		String expr = args[0];
		String[] patternArgs = Arrays.copyOfRange(args, 1, args.length);
		int result = cc.exec(expr, patternArgs);
		System.out.printf("%d = constantsConform(%s, %s)\n", result, expr, Arrays.toString(patternArgs));
		System.exit(result);
	}
	
	/** The patterns to test the constant expressions. */
	private Pattern[] patterns = null;

	/**
	 * Check that all numbers in an expression conform to at least one of a set of patterns.
	 * @param expr arithmetic expression with numbers
	 * @param strPatterns regular expressions (to be compiled): each number should match one of these
	 * @return 0 if each number matches some pattern; 1 if some number matches no pattern; 2 if error 
	 */
	int exec(String expr, String... strPatterns) {
		Pattern[] patterns = new Pattern[strPatterns.length];
		int i = 0;
		try {
			for (String s : strPatterns) 
				patterns[i++] = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
			return exec(expr, patterns);
		} catch (Exception e) {
			trace.err("Error compiling pattern["+i+"] \"+strPatterns[i]+\" in constantsConform("+expr+"): " + e +
                    (e.getCause() == null ? "" : "; cause " + e.getCause().toString()));
            e.printStackTrace(System.err);
			return 2;
		}
	}

	/**
	 * Check that all numbers in an expression conform to at least one of a set of patterns.
	 * @param expr arithmetic expression with numbers
	 * @param patterns compiled regular expressions: each number should match one of these
	 * @return 0 if each number matches some pattern; 1 if some number matches no pattern; 2 if error 
	 */
	int exec(String expr, Pattern... patterns) {
		if (expr == null || expr.length() < 1)
			return 2;
		try	{
		    SyntaxSeparation separation = new SyntaxSeparation(new Syntax(rules));   // takes off TOKEN and IGNORED
		    LexerBuilder builder = new LexerBuilder(separation.getLexerSyntax(), separation.getIgnoredSymbols());
		    Lexer lexer = builder.getLexer();
		    lexer.setTerminals(separation.getTokenSymbols());
		    lexer.setInput(expr);
		    Token token;
		    int result = 0;
		    do    {
		        token = lexer.getNextToken(null);    // null: no hints what is expected
		        if (token.symbol == null)
		            lexer.dump(System.err);
		        else if (!numberConforms(token, patterns))
		        	result = 1;
		    }
		    while (result == 0 && token.symbol != null && !Token.isEpsilon(token));
		    if (result == 0 && !Token.isEpsilon(token))
		    	result = 2;                       // failed to scan to end of input
			if (trace.getDebugCode("functions")) trace.out("functions", "scan returns "+result+", last token symbol "+token.symbol+
					", text "+token.text+", eof "+Token.isEpsilon(token));
			return result;
		} catch (Exception e)	{
			trace.err("Error from constantsConform \"" + expr + "\"" + e +
                    (e.getCause() == null ? "" : "; cause " + e.getCause().toString()));
            e.printStackTrace(System.err);
			return 2;
		}
	}

	/** Lexer rules to token arithmetic expressions. */
	private static String [][] rules = {	// arithmetic sample
		{ Token.TOKEN, "OPERATOR" },
		{ Token.TOKEN, "`number`" },
		{ Token.TOKEN, "`letter`" },
		{ "OPERATOR", "'+'" },
		{ "OPERATOR", "'-'" },
		{ "OPERATOR", "'*'" },
		{ "OPERATOR", "'/'" },
		{ "OPERATOR", "'^'" },
		{ "OPERATOR", "'('" },
		{ "OPERATOR", "')'" },
		{ "OPERATOR", "'='" },
		{ "OPERATOR", "'<'" },
		{ "OPERATOR", "'>'" },
		{ Token.IGNORED,   "`whitespaces`" }
	};

	/**
	 * Test whether a number conforms to the given patterns.
	 * @param t token to examine
	 * @param patterns compiled regular expressions to test
	 * @return true if token is not a number or the number conforms
	 */
	public boolean numberConforms(Token t, Pattern[] patterns) {
		boolean result = true;
		int i = 0;
		if ("`number`".equals(t.symbol)) {
			if (t.text != null) {
				String number = t.text.toString();
				for (Pattern pattern : patterns) {
					if (pattern.matcher(number).matches())
						break;
					++i;
				}
			}
		}
		if (trace.getDebugCode("functions")) trace.outNT("functions", "constantsConform.numberConforms() symbol "+t.symbol+", text "+t.text+
				"\n     matches pattern "+(i >= patterns.length ? "(none)" : patterns[i].toString()));

		if (i >= patterns.length)                            // this number found no match
			result = false;
		return result;
    }
}
