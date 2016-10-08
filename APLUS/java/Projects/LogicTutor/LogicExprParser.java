package LogicTutor;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;



import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;
import fri.patterns.interpreter.parsergenerator.Lexer;
import fri.patterns.interpreter.parsergenerator.Parser;
import fri.patterns.interpreter.parsergenerator.ParserTables;
import fri.patterns.interpreter.parsergenerator.Token;
import fri.patterns.interpreter.parsergenerator.lexer.LexerBuilder;
import fri.patterns.interpreter.parsergenerator.parsertables.LALRParserTables;
import fri.patterns.interpreter.parsergenerator.semantics.ReflectSemantic;
import fri.patterns.interpreter.parsergenerator.semantics.TreeBuilderSemantic;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;
import fri.patterns.interpreter.parsergenerator.syntax.Syntax;
import fri.patterns.interpreter.parsergenerator.syntax.builder.SyntaxSeparation;



/**

 * Parser for sentential logic expressions. This class is meant to

 * support multi-threading by packing all results of a single parse into

 * {@link LogicExprParser.ParseEvent} instances, which are themselves

 * created by synchronized methods.<p>

 *

 * Use {@link

 * fri.patterns.interpreter.parsergenerator.semantics.ReflectSemantic}

 * as a base class in order to perform expression evaluation.

 */

public class LogicExprParser extends TreeBuilderSemantic

{

	//////////////////////////////////////////////////////////////////////

	//

	// Inner classes and interfaces

	//

	//////////////////////////////////////////////////////////////////////



	/**

	 * Interface defines listeners for parse events.

	 */

	public interface Listener {



		/**

		 * Called when a parse() attempt has been completed.

		 *

		 * @param  evt object containing the results of the parse()

		 */

		public void parseCompleted(ParseEvent evt);

	}



	/**

	 * Public interface for nodes of the syntax tree.

	 */

	public interface Node {  // extends Serializable {



		/**

		 * Pretty-print the subtree at this node.

		 *

		 * @return formatted String with indentation 2 for subtree levels

		 */

		public String formatSubtree();

		

		/**

		 * Return the underlying parser's node.

		 * @return {@link TreeBuilderSemantic.Node}

		 */

		public TreeBuilderSemantic.Node getParserNode();

	}



	/**

	 * Result of parsing a single String. Package this as an EventObject

	 * so can broadcast to listeners as well as return to caller.  Meant

	 * to include all results of a single parse, so that the enclosing

	 * LogicExprParser class can support multi-threading.

	 */

	public static class ParseEvent extends EventObject {

		private String input = "";

		private boolean parsedOk = false;

		private transient Node root = null;

		private String errorMsg = "";

		private Set variableSet = null;

		private String[] subExprArray = null;

		private List subExprList = null;    /// new ArrayList();

		private LogicExprTree.Node[] ExprTreeNode = null;    /// new ArrayList();

		private ParseEvent(Object source, String input, boolean parsedOk,

						   String errorMsg, Node root, Set variableSet, 

						   String[] subExprArray, List subExprList, LogicExprTree.Node[] ExprTreeNode) {

			super(source);

			this.input = input;

			this.parsedOk = parsedOk;

			this.root = root;

			this.errorMsg = errorMsg;

			this.variableSet = variableSet;

			this.subExprArray = subExprArray;

			this.subExprList = subExprList;

			this.ExprTreeNode = ExprTreeNode;

		}



		/**

		 * Return the expression parsed.

		 *

		 * @return {@link #input}

		 */

		public String getInput() {

			return input;

		}



		/**

		 * Tell whether the expression parsed ok or not.

		 *

		 * @return {@link #parseOk}

		 */

		public boolean getResult() {



			return parsedOk;

		}

		

		public boolean CreateMemory() {

			

			//	ExBuildDeftemplate buildtemplate = new ExBuildDeftemplate();

			//	buildtemplate.go();

			//	ExMulti buildfirsttemple = new ExMulti();

			//	buildfirsttemple.go();

			//	Transfer connection = new Transfer();

			//	connection.JavaJess();

				return parsedOk;

			}



		/**

		 * Return a reference to the root of the syntax tree returned by

		 * {@link tutors.LogicTutor.LogicExprParser#parse(String)

		   LogicExprParser.parse(String)}.

		 *

		 * @return {@link #rootNode}; null if error on parse

		 */

		public Node getRoot() {

			return root;

		}



		/**

		 * Return any error message from the parse action.

		 *

		 * @return {@link #errorMsg}; may be empty or null if parse successful

		 */

		public String getErrorMsg() {

			return errorMsg;

		}



		/**

		 * Return the number of unique variables found in the parsed

		 * expression.

		 *

		 * @return {@link #variableSet}.size()

		 */

		public int getVariableCount() {

			if (variableSet == null)

				return 0;

			else

				return variableSet.size();

		}



		public Set getVariableSet() {

				return variableSet;

		}	

		

        public String[] getVariableList() {

        	Set vSet = getVariableSet();

        	String[] vArray = new String[vSet.size()];

        	vSet.toArray(vArray);

        // 	System.out.println("getVariableList = " + vArray[0] + " " + vArray[1]);

    	    for (int i = 0; i < vSet.size(); ++i)

    	    {

          	System.out.print("vArray[" + i + "]" + vArray[i]);

    	    System.out.print("\n");

    	    }

        return(vArray);

        	

        }

		

        public String[] getSubExprArray() {

        	System.out.println("Get subExpreArray = " + subExprArray);

        return (subExprArray);

        	

        }

        

        public List getSubExprList() {

        	System.out.println("Get subExpreList = " + subExprList);

        return (subExprList);

        	

        }

        

        public LogicExprTree.Node[] getExprTreeNode() {

        	System.out.println("Get ExprTreeNode = " + ExprTreeNode);

        return (ExprTreeNode);

        	

        }

        

		/**

		 * Return an iterator over the variables in the parsed expression.

		 *

		 * @return Iterator on {@link #variableSet}

		 */

		public Iterator variableIterator() {

			if (variableSet != null)

				return variableSet.iterator();

			else {

				return new Iterator() {

					public boolean hasNext() {

						return false;

					}

					public Object next() {

						throw new NoSuchElementException("Empty Iterator");

					}

					public void remove() {

						throw new IllegalStateException("Empty Iterator");

					}

				};

			}

		}

	}



	/**

	 * A node in the syntax tree.

	 */

	public static class NodeImpl implements Node {



		/** Reference to actual node from parser result. */ 

		private TreeBuilderSemantic.Node tbsn = null;



		/**

		 * Constructor sets {@link #tbsn}.

		 * @param node reference to actual node from parser result

		 */

		private NodeImpl( TreeBuilderSemantic.Node node ) {

			tbsn = node;

		}

		

		/**

		 * Access to the underlying parser node.

		 */

		public TreeBuilderSemantic.Node getParserNode() {		

			return tbsn;

		}



		/**

		 * Pretty-print the subtree at this node.

		 * @return formatted String with indentation 2 for subtree levels

		 */

		public String formatSubtree() {

			return tbsn.toString(2);

		}

	}



	private static final String OR = "|";

	private static final String AND = "&";

	private static final String EQUIV = "<->";

	private static final String IMPLY = "->";

	private static final String NOT = "~";



	/**

	 * Grammar for logical expressions.

	 */



	private static String [][] rules = {

		{ "START",   "EXPRESSION" },

		{ "EXPRESSION",   "TERM" },

		{ "EXPRESSION",   "EXPRESSION", "OR", "TERM" },

		{ "EXPRESSION",   "EXPRESSION", "AND", "TERM" },

		{ "TERM",   "FACTOR", },

		{ "TERM",   "TERM", "EQUIV", "FACTOR" },

		{ "TERM",   "TERM", "IMPLY", "FACTOR" },

		{ "FACTOR",   "PRIMARY", },

		{ "FACTOR",   "NOT", "FACTOR" },	// need LALRParserTables instead of SLRParserTables because of this rule

		{ "PRIMARY",   "VARIABLE" },

		{ "PRIMARY",   "'('", "EXPRESSION", "')'" },

		{ Token.TOKEN, "OR" },

		{ Token.TOKEN, "AND" },

		{ Token.TOKEN, "EQUIV" },

		{ Token.TOKEN, "IMPLY" },

		{ Token.TOKEN, "NOT" },

		{ Token.TOKEN, "VARIABLE" },

		{ "OR", "'|'" },

		{ "AND", "'&'" },

		{ "EQUIV", "'<'","'-'","'>'" },

		{ "IMPLY", "'-'","'>'" },

		{ "NOT", "'~'" },

		{ "VARIABLE", "'P'" },

		{ "VARIABLE", "'Q'" },

		{ "VARIABLE", "'R'" },

		{ "VARIABLE", "'S'" },

		{ Token.IGNORED,   "`whitespaces`" },

	};



	private static String [][] newrules = {

		{ "START",   "EXPRESSION" },

		{ "EXPRESSION",   "EXPRESSION", "EQUIV", "EXPRESSION" },

		{ "EXPRESSION",   "EXPRESSION", "IMPLY", "EXPRESSION" },

		{ "EXPRESSION",   "TERM" },

		{ "EXPRESSION",   "EXPRESSION", "OR", "TERM" },

		{ "EXPRESSION",   "EXPRESSION", "AND", "TERM" },

		{ "TERM",   "FACTOR", },

		{ "FACTOR",   "PRIMARY", },

		{ "FACTOR",   "NOT", "FACTOR" },	// need LALRParserTables instead of SLRParserTables because of this rule

		{ "PRIMARY",   "VARIABLE" },

		{ "PRIMARY",   "'('", "EXPRESSION", "')'" },

		{ Token.TOKEN, "OR" },

		{ Token.TOKEN, "AND" },

		{ Token.TOKEN, "EQUIV" },

		{ Token.TOKEN, "IMPLY" },

		{ Token.TOKEN, "NOT" },

		{ Token.TOKEN, "VARIABLE" },

		{ "OR", "'|'" },

		{ "AND", "'&'" },

		{ "EQUIV", "'<'","'-'","'>'" },

		{ "IMPLY", "'-'","'>'" },

		{ "NOT", "'~'" },

		{ "VARIABLE", "'P'" },

		{ "VARIABLE", "'Q'" },

		{ "VARIABLE", "'R'" },

		{ "VARIABLE", "'S'" },

		{ Token.IGNORED,   "`whitespaces`" },

	};



	/**

	 * Parser built from {@link #rules}.

	 */

	private Parser parser = null;

	

	/**

	 * Lexical analyzer builder built from {@link #rules}.

	 */

	private LexerBuilder builder = null;



	/**

	 * Set of variables found.

	 */

	private Set variableSet = null;



private String[] subExprArray = null;

private List subExprList = null;

private LogicExprTree.Node[] ExprTreeNode = null;

    /**

	 * Result from latest call to {@link #parse(String)}.

	 */

	private ParseEvent lastParseResult = null;



	/**

	 * The current set of LogicExprParser.Listener instances. 

	 */

	private List listeners = new LinkedList();



	/**

	 * Static reference to last instance created.

	 */

	private static volatile LogicExprParser singleInstance = null;



	/**

	 * Constructor generates lexer, parser from {@link #rules}.

	 * Sets {@link #singleInstance}.

	 */

	private LogicExprParser() {

		try	{

			/*

			 * Change the lines down to "parser =" to the following to create

			 * a parser that will be serialized to

			 * $HOME/.friware/parsers/LogicExprParserParser.ser and reused if

			 * available:

			 * 

			 * parser = new SerializedParser().get(rules, "LogicExprParser");

			 */

			SyntaxSeparation separation =

				new SyntaxSeparation(new Syntax(rules)); // takes away IGNORED

			builder =

				new LexerBuilder(separation.getLexerSyntax(),

								 separation.getIgnoredSymbols());

			ParserTables parserTables =

				new LALRParserTables(separation.getParserSyntax());

			parser = new Parser(parserTables);

		}

		catch (Exception e)	{

			e.printStackTrace();

			System.exit(2);

		}

	}



	/**

	 * Global reference to latest instance created. Creates singleton and

	 * sets {@link #singleInstance} if null.

	 *

	 * @return {@link #singleInstance}

	 */

	public static LogicExprParser instance() {

		if (singleInstance == null) {

			synchronized(LogicExprParser.class) {

				if (singleInstance == null)

					singleInstance = new LogicExprParser();

			}

		}

		return singleInstance;

	}



	/**

	 * Parse a given string. Sets {@link #lastParseResult}.

	 *

	 * @param  input String to parse

	 * @return instance of ParseEvent with output from this parse

	 */

	public synchronized ParseEvent parse(String input) {

		/*

		 * For SerializedParser (see comment in constructor), try this:

		 *

		 * boolean ok = parser.parse(input, this);

		 * System.out.println("Parse return "+ok+", result: " +

		 *                    parser.getResult());

		 */

		ByteArrayOutputStream errStrm = null;

		PrintStream errPrtStrm = null;

		try {

			errStrm = new ByteArrayOutputStream();

			errPrtStrm = new PrintStream(errStrm);

			parser.setPrintStream(errPrtStrm);

			variableSet = new LinkedHashSet();

			Lexer lexer = builder.getLexer(input);

			boolean parsedOk = parser.parse(lexer, this);

			

			errPrtStrm.flush();

			String errMsg = errStrm.toString();

			if (parsedOk) {

				NodeImpl root =

					new NodeImpl((TreeBuilderSemantic.Node) parser.getResult());





				

				

				//		LogicExprParser parser = new LogicExprParser();

				//		ParseEvent pe = parser.parse(input);

						LogicExprTree tree = new LogicExprTree(root.getParserNode());

				//		subExprArray = new String[(tree.getSubExprArray()).length];

				//		List temp = tree.getSubExprList();

						subExprArray = tree.getSubExprArray();

						System.out.println("Save subExpreArray = " + subExprArray);

						subExprList = tree.getSubExprList();

						ExprTreeNode = tree.getExprTreeNode();

						lastParseResult = new ParseEvent(this, input, parsedOk,

								errMsg, root, variableSet, subExprArray, subExprList, ExprTreeNode);		

			} else {

				lastParseResult = new ParseEvent(this, input, parsedOk,

												 errMsg, null, null, null, null,null);

			}

		} catch (IOException ioe) {

			lastParseResult = new ParseEvent(this, input, false,

											 ioe.toString(), null, null, null, null,null);

		} finally {

			if (errPrtStrm != null)

				errPrtStrm.close();

			variableSet = null;

		}

		fireParseEvent(lastParseResult);

		return lastParseResult;

	}



	/**

	 * Get result from latest call to {@link #parse(String)}.

	 *

	 * @return {@link #lastParseResult}; null if no call to parse() yet

	 */

	public ParseEvent getLastParseResult() {

		return lastParseResult;

	}

	

	/**

	 * For stand-alone testing.

	 * @param args single command-line argument is expression to parse

	 */

	public static void main(String[] args) {

		if (args.length < 1) {

			System.err.println("Usage: "+

					LogicExprParser.class.getName()+" \"exprToParse\"");

			System.exit(2);

		}

		String expr = args[0].toUpperCase();

		LogicExprParser parser = new LogicExprParser();

		ParseEvent pe = parser.parse(expr);

		if (!pe.getResult()) {

			System.out.println("Error parsing expression \""+expr+"\":\n  "+

					pe.getErrorMsg());

			return;

		}

		System.out.println("Parsing expression \""+expr+"\":"+

				pe.getRoot().formatSubtree());

		LogicExprTree tree = new LogicExprTree(pe.getRoot().getParserNode());

		System.out.println("LogicExprTree.toString(): "+tree.getRoot().toString());

		System.out.println("LogicExprTree.toString(): "+tree.getRoot().toString(true));

		System.out.println("LogicExprTree is: "+tree.toString());

		System.out.println("LogicExprTree.getVariableTable() is: "+tree.getVariableTable());

		System.out.println("LogicExprTree.getVariableSet is: "+pe.getVariableSet());

		System.out.println("LogicExprTree.getVariableSet is: "+pe.getVariableList());

		System.out.println("LogicExprTree.getsubExprArray() is: "+tree.getSubExprArray());

		System.out.println("LogicExprTree.getSubExprList() is: "+tree.getSubExprList());		

	//	Transfer connection = new Transfer();

	//	connection.JavaJess();

		

	//	ExBuildDeftemplate buildtemplate = new ExBuildDeftemplate();

	//	buildtemplate.go();

		

//		 List subExprList = tree.getSubExprList();

//		for (int j = 0; j < subExprList.size(); ++j) {

//		    System.out.print(""+(j+1)+" ");

//		    String[] le = ((LogicExprTree.Node) subExprList.get(j)).getLogicEvaluation();

//		    for (int i = 0; i < le.length; ++i)

//			System.out.print(" "+le[i]);

//		    System.out.print("\n");

//		}

	}



	/**

	 * Called by the parser at every REDUCE step.

	 *

	 * @param  rule Rule that was recognized

	 * @param  parseResult (not described)

	 * @param  resultRanges all line ranges for parseResults elements

	 */

	public Object doSemantic(Rule rule, List parseResult, List resultRanges) {

		Object result = super.doSemantic(rule, parseResult, resultRanges);

		int rightSize = rule.rightSize();

		boolean isVariable =

			( "PRIMARY".equals(rule.getNonterminal()) &&

			  rightSize == 1 &&

			  "`VARIABLE`".equals(rule.getRightSymbol(0)) );

		String variable = (isVariable ? parseResult.get(0).toString() : null);

		if (variable != null)

			variableSet.add(variable);

		trace.out("ti", "doSemantic(): rightSize()=" + rightSize +

				  ", nonterminal=" + rule.getNonterminal() +

				  ", rightSymbol(0)=" +

				  (rightSize>0 ? rule.getRightSymbol(0) : "(null)") +

				  ", parseResult(0)=" +

				  (rightSize>0 ? parseResult.get(0) : "(null)") +

				  ", resultRanges(0)=" +

				  (rightSize>0 ? resultRanges.get(0) : "(null)") +

				  ", isVariable=" + isVariable + ": " + variable);

		return result;

	}

	

	/**

	 * Rule-specific semantic for the rule whose left-hand side matches

	 * this method's name and whose right-hand side matches its argument list.

	 * Used when this class derives from {@link ReflectSemantic}.

	 *

	 * @return Boolean whose value is this subexpression's value

	 */

	public Object EXPRESSION(Object TERM)	{

		Boolean result = (Boolean) TERM; //ReflectSemantic.fallback() does this

		System.out.println("result: EXPRESSION(Object TERM)=" + result.booleanValue());

		return result;

	}

	

	/**

	 * Rule-specific semantic for the rule whose left-hand side matches

	 * this method's name and whose right-hand side matches its argument list.

	 * Used when this class derives from {@link ReflectSemantic}.

	 *

	 * @return Boolean whose value is this subexpression's value

	 */

	public Object EXPRESSION(Object EXPRESSION, Object operator, Object TERM)	{

		Boolean result = null;

		if (OR.equals(operator))

			result = (Boolean) new Boolean(((Boolean) EXPRESSION).booleanValue() || ((Boolean) TERM).booleanValue());

		else

			result = new Boolean(((Boolean) EXPRESSION).booleanValue() && ((Boolean) TERM).booleanValue());

		System.out.println("result: EXPRESSION(Object EXPRESSION, " + operator + ", Object TERM)=" + result.booleanValue());

		return result;

	}

	



	/**

	 * Rule-specific semantic for the rule whose left-hand side matches

	 * this method's name and whose right-hand side matches its argument list.

	 * Used when this class derives from {@link ReflectSemantic}.

	 *

	 * @return Boolean whose value is this subexpression's value

	 */

	public Object TERM(Object FACTOR)	{

		Boolean result = (Boolean) FACTOR; //ReflectSemantic.fallback() does this

		System.out.println("result: TERM(Object FACTOR)=" + result.booleanValue());

		return result;

	}

	

	/**

	 * Rule-specific semantic for the rule whose left-hand side matches

	 * this method's name and whose right-hand side matches its argument list.

	 * Used when this class derives from {@link ReflectSemantic}.

	 *

	 * @return Boolean whose value is this subexpression's value

	 */

	public Object TERM(Object TERM, Object operator, Object FACTOR)	{

		Boolean result = null;

		if (IMPLY.equals(operator))

			result = new Boolean(!((Boolean) TERM).booleanValue() || ((Boolean) FACTOR).booleanValue());

		else

			result = new Boolean((((Boolean) TERM).booleanValue() &&

								  ((Boolean) FACTOR).booleanValue()) ||

								 (! ((Boolean) TERM).booleanValue() &&

								  ! ((Boolean) FACTOR).booleanValue()));

		System.out.println("trace: TERM(Object TERM, " + operator + ", Object FACTOR)=" + result.booleanValue());

		return result;

	}

	

	/**

	 * Rule-specific semantic for the rule whose left-hand side matches

	 * this method's name and whose right-hand side matches its argument list.

	 * Used when this class derives from {@link ReflectSemantic}.

	 *

	 * @return Boolean whose value is this subexpression's value

	 */

	public Object FACTOR(Object PRIMARY)	{

		Boolean result = (Boolean) PRIMARY;

		System.out.println("result: FACTOR(Object PRIMARY)=" + result.booleanValue());

		return result;

	}

	

	/**

	 * Rule-specific semantic for the rule whose left-hand side matches

	 * this method's name and whose right-hand side matches its argument list.

	 * Used when this class derives from {@link ReflectSemantic}.

	 *

	 * @return Boolean whose value is this subexpression's value

	 */

	public Object FACTOR(Object not, Object FACTOR)	{

		Boolean result = (Boolean) new Boolean( ! ((Boolean) FACTOR).booleanValue() );

		System.out.println("result: FACTOR(Object not, Object FACTOR)=" + result.booleanValue());

		return result;

	}

	

	/**

	 * Rule-specific semantic for the rule whose left-hand side matches

	 * this method's name and whose right-hand side matches its argument list.

	 * Used when this class derives from {@link ReflectSemantic}.

	 *

	 * @return Boolean whose value is this subexpression's value

	 */

	public Object PRIMARY(Object leftParenthesis, Object EXPRESSION, Object rightParenthesis)	{

		Boolean result = (Boolean) EXPRESSION;

		System.out.println("result: PRIMARY(Object leftParenthesis, Object EXPRESSION, Object rightParenthesis)=" + result.booleanValue());

		return result;

	}

	

	/**

	 * Rule-specific semantic for the rule whose left-hand side matches

	 * this method's name and whose right-hand side matches its argument list.

	 * Used when this class derives from {@link ReflectSemantic}.

	 *

	 * @return Boolean whose value is this subexpression's value

	 */

	public Object PRIMARY(Object identifier) {

		if (identifier == null) {

			System.err.println("**PRIMARY(identifier) argument null");

			return new Boolean(false);

		}

		String v = identifier.toString();

		System.out.println("trace: PRIMARY("+v+")");

		return new Boolean(v.equalsIgnoreCase("P") || v.equalsIgnoreCase("R"));

	}



	/**

	 * Add an event listener. Ensures that listener is not added more than

	 * once.

	 *

	 * @param  listener to add

	 */

	public void addParseEventListener(LogicExprParser.Listener listener) {

		listeners.remove(listener);

		listeners.add(listener);

	}



	/**

	 * Remove an event listener.

	 *

	 * @param  listener to remove

	 */

	public void removeParseEventListener(LogicExprParser.Listener listener) {

		listeners.remove(listener);

	}



	/**

	 * Notify all listeners of a {@link ParseEvent}. Uses ListIterator

	 * to avoid trouble if a listener calls {@link #removeParseEventListener}

	 * from its {@link ParseEventListener#parseEventOccurred} method.

	 *

	 * @param  parseEvent event that occurred

	 */

	public void fireParseEvent(ParseEvent parseEvent) {

		for (ListIterator it = listeners.listIterator(); it.hasNext(); )

			((LogicExprParser.Listener) it.next()).parseCompleted(parseEvent);

	}

}

