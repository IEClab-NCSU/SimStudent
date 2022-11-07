/**
 * 
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.List;

import fri.patterns.interpreter.parsergenerator.Lexer;
import fri.patterns.interpreter.parsergenerator.Parser;
import fri.patterns.interpreter.parsergenerator.ParserTables;
import fri.patterns.interpreter.parsergenerator.Token;
import fri.patterns.interpreter.parsergenerator.lexer.LexerBuilder;
import fri.patterns.interpreter.parsergenerator.parsertables.LALRParserTables;
import fri.patterns.interpreter.parsergenerator.semantics.TreeBuilderSemantic;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;
import fri.patterns.interpreter.parsergenerator.syntax.Syntax;
import fri.patterns.interpreter.parsergenerator.syntax.builder.SyntaxBuilder;
import fri.patterns.interpreter.parsergenerator.syntax.builder.SyntaxSeparation;

/**
 * @author sewall
 *
 */
public class LabelRecognizer extends TreeBuilderSemantic {
	
	private static String [][] rules = {	// arithmetic sample
		{ "LABEL",   "KEYNOUN" },
		{ "LABEL",   "PARTICIPIALPHRASE" },
		{ "PARTICIPIALPHRASE",   "KEYNOUN", "KEYVERB" },
		{ "KEYNOUN", "\"videotapes\"" },
		{ "KEYNOUN", "\"tapes\"" },
		{ "KEYNOUN", "\"videos\"" },
		{ "KEYVERB", "\"rented\"" },
		{ Token.TOKEN, "others" },
		{ "others", "others", "other" },
		{ "others", "other" },
		{ "other", "`char`", Token.BUTNOT, "' '" },
		{ Token.IGNORED, "`whitespaces`" }
		
//		{ Token.IGNORED, "`whitespaces`" }
	};
/*	
	public Object LABEL(Object TERM)	{
		return TERM;	// do not really need this method as ReflectSemantic.fallback() does this
	}
	public Object LABEL(Object LABEL, Object operator, Object TERM)	{
		if (operator.equals("+"))
			return new Double(((Double) LABEL).doubleValue() + ((Double) TERM).doubleValue());
		return new Double(((Double) LABEL).doubleValue() - ((Double) TERM).doubleValue());
	}
	public Object TERM(Object FACTOR)	{
		return FACTOR;	// do not really need this method as ReflectSemantic.fallback() does this
	}
	public Object TERM(Object TERM, Object operator, Object FACTOR)	{
		if (operator.equals("*"))
			return new Double(((Double) TERM).doubleValue() * ((Double) FACTOR).doubleValue());
		return new Double(((Double) TERM).doubleValue() / ((Double) FACTOR).doubleValue());
	}
	public Object FACTOR(Object number)	{
		return Double.valueOf((String) number);
	}
	public Object FACTOR(Object minus, Object FACTOR)	{
		return new Double( - ((Double) FACTOR).doubleValue() );
	}
	public Object FACTOR(Object leftParenthesis, Object LABEL, Object rightParenthesis)	{
		return LABEL;
	}
*/

	/** Verbose output (command-line option). */
	private static boolean verbose = false;

	/**
	 * SYNTAX:
	 *  java fri.patterns.interpreter.parsergenerator.examples.LabelRecognizer [-v] 'video' 'videos rented'
	 */
	public static void main(String [] args)	{
		boolean explainErrors = false;
		String syntaxFile = null;
		int i = 0;
		while (i < args.length && args[i].startsWith("-")) {
			if (args[i].length() != 2)
				continue;
			char opt = args[i].charAt(1);
			switch (opt) {
			case 'f': case 'F':
				syntaxFile = "LabelRecognizer.syntax";
				++i; break; 
			case 'v': case 'V': verbose = true;
				++i; break; 
			case 'e': case 'E': explainErrors = true;
				++i; break; 
			case 'h': case 'H': usageExit(null);
				++i; break; 
			default: usageExit(new IllegalArgumentException("Undefined option \"-"+opt+"\"")); 
			}
		}
		if (args.length <= i)
			usageExit(new IllegalArgumentException("No labels to parse."));
		try {
			Syntax syntax = null;
			if (syntaxFile != null) {
			    Reader syntaxInput =
			    	new InputStreamReader(LabelRecognizer.class.getResourceAsStream(syntaxFile));
			    SyntaxBuilder builder = new SyntaxBuilder(syntaxInput);
			    syntax = builder.getSyntax();				
			} else {
				syntax = new Syntax(rules);
			}
			SyntaxSeparation separation = new SyntaxSeparation(syntax);	// takes away IGNORED
			LexerBuilder builder = new LexerBuilder(separation.getLexerSyntax(), separation.getIgnoredSymbols());
			ParserTables parserTables = new LALRParserTables(separation.getParserSyntax());
			Parser parser = new Parser(parserTables);
			for (; i < args.length; i++) {
				String input = args[i];
				try	{
					ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
					parser.setPrintStream(new PrintStream(baos));
					Lexer lexer = builder.getLexer(input);
					boolean ok = parser.parse(lexer, new LabelRecognizer());
					System.out.printf("%5b <= [%2d] %s\n", ok, i, input);
					if (!ok && explainErrors)
						trace.out(baos.toString());
					if (verbose) {
						Node result = (Node) parser.getResult();
						trace.out(" result:\n"+
							   (result == null ? "null" : result.toString(1)));
					}
				}
				catch (Exception e)	{
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

	/**
	 * Print a usage message and exit.
	 * @param e error to print; null if none
	 * @return never returns
	 */
	private static void usageExit(Exception e) {
		if (e != null)
			System.err.print("Error: "+e+". ");
		System.err.println("Usage:\n"+
				"  java "+LabelRecognizer.class.getName()+" [-h] [-e] [-v] [-f] \"label\" ...\n"+
				"where--\n"+
				"  -h means print this help message;\n"+
				"  -e means explain input errors;\n"+
				"  -v means verbose output;\n"+
				"  -f means reads syntax from an EBNF file (default LabelRecognizer.syntax);\n"+
				"  label is a label to parse.");
		System.exit(2);
	}

	public Object doSemantic(Rule rule, List inputTokens, List ranges)	{
		Object result = super.doSemantic(rule, inputTokens, ranges);
		if (verbose) {
			System.out.printf("LabelRecognizer.doSemantic("+
					"\n        rule %s,"+
					"\n        inputTokens %s,"+
					"\n        ranges %s)\n",
					rule.toString(), inputTokens.toString(), ranges.toString());
		}
		return result;
	}

}
