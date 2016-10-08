package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import edu.cmu.hcii.runcc.MemorySerializedParser;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import edu.cmu.pact.Utilities.trace;
import fri.patterns.interpreter.parsergenerator.Parser;
import fri.patterns.interpreter.parsergenerator.Token;
import fri.patterns.interpreter.parsergenerator.semantics.TreeBuilderSemantic;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;

public class CTATExpressionSemantic extends TreeBuilderSemantic {
    private static final String [][] rules = {
		{ "EXPRESSION", "TERM" },
		
        { "EXPRESSION", "EXPRESSION", "'+'", "TERM" },
        { "EXPRESSION", "EXPRESSION", "'-'", "TERM" },  
        
        { "TERM", "TERM", "'*'", "NEGOPERAND" },
        { "TERM", "TERM", "'/'", "NEGOPERAND" },
        { "TERM", "TERM", "EXPOPERAND"},
        { "TERM", "NEGOPERAND"},		
     
        { "NEGOPERAND", "'-'", "EXPOPERAND"},
        { "NEGOPERAND", "EXPOPERAND"},
       
        { "EXPOPERAND", "OPERAND", "'^'", "NEGOPERAND"},
        { "EXPOPERAND", "OPERAND"},
        
		{ "OPERAND", "'('", "EXPRESSION", "')'" },
        { "OPERAND", "VARREF"},
        { "OPERAND", "NUMBER"},
        
        { "NUMBER", "`number`"},
        { "VARREF", "`letter`"},
        
        
        { Token.IGNORED, "`whitespaces`" },
    };

    /** Default error message for a successful parse. */
    private String _validMsg = "";
    
   // private VariableTable _variableTable;
    ExpressionTreeProperties properties;
    
    public Object doSemantic(Rule rule, List inputTokens, List ranges)	{
    	if (trace.getDebugCode("functions"))
			trace.out("functions", "DoSemantic _" + rule.getNonterminal() + "_");
    	
    	if(rule.getNonterminal().equals("EXPRESSION")){
    		return new ExpressionSyntaxNode(rule, inputTokens, ranges, properties);
    	}else if(rule.getNonterminal().equals("TERM")){
    		return TermSyntaxNode.create(rule, inputTokens, ranges, properties);
    	}else if(rule.getNonterminal().equals("NEGOPERAND")){
    		return new NegationSyntaxNode(rule, inputTokens, ranges, properties);
    	}else if(rule.getNonterminal().equals("EXPOPERAND")){
    		return new ExponentSyntaxNode(rule, inputTokens, ranges, properties);
    	}else if(rule.getNonterminal().equals("OPERAND")){
    		return new OperandSyntaxNode(rule, inputTokens, ranges, properties);
    	}else if(rule.getNonterminal().equals("NUMBER")){
    		return new NumberSyntaxNode(rule, inputTokens, ranges, properties);
    	}else if(rule.getNonterminal().equals("VARREF")){
    		return new VarRefSyntaxNode(rule, inputTokens, ranges, properties);
    	}else{
    		trace.err("Error unmatched rule in CTATExpressionSemantic");
    		return new Node(rule, inputTokens, ranges);
    	}
	}
  
    /**
     * Constructor accepting an externally-supplied {@link Parser}. 
     * @param variableTable
     * @param problemModel
     * @param parser value for {@link #_parser}
     */
    public CTATExpressionSemantic(ExpressionTreeProperties properties, Parser parser) {
    	this.properties = properties;
        _parser = parser;
        if (_parser != null)
        	_parser.setPrintStream(new PrintStream(parserStream()));
    }
    
    /** A buffer to save the serialized parser. */
    private ByteArrayOutputStream _parserStream;
    
    /**
     * @return {@link #_parserStream}; creates the stream if it doesn't already exist.
     */
    private ByteArrayOutputStream parserStream() {
        if (_parserStream==null)
            _parserStream = new ByteArrayOutputStream();
        return _parserStream;
    }

    /**
     * Retrieve an error message from the parse.
     * @return content from {@link #parserStream()}
     */
    public String errorString() {
        String err = parserStream().toString();
        if (err==null || err.length()==0)
            err = _validMsg;
        return err;
    }

    /** Saved copy of the parser. Could this be static? Don't know whether thread-safe. */
    private Parser _parser;

    /**
     * Generate the parser if not yet generated and save a reference in {@link #_parser}.
     * @return {@link #_parser}
     * @throws Exception
     */
    private Parser parser() throws Exception {
    	long begin = System.currentTimeMillis();
    	if (trace.getDebugCode("functions"))
    		trace.out("functions", "before parser constr: _parser "+_parser);
        if (_parser == null) {
            _parser = new MemorySerializedParser().get(getRules(), "CTATExpressionSemantic");
            _parser.setPrintStream(new PrintStream(parserStream()));
        	if (trace.getDebugCode("functions"))
        		trace.out("functions", " after parser constr: took (ms) "+
        				(System.currentTimeMillis()-begin));
        }
        return _parser;
    }
    
    /**
     * Call {@link Parser#parse(Object, fri.patterns.interpreter.parsergenerator.Semantic)},
     * passing the given expression to be parsed.
     * @param expression
     * @return result from called method
     * @throws Exception
     */
    public Boolean evaluate(String expression) throws Exception {
    	boolean ok = parser().parse(expression, this);
    	if(trace.getDebugCode("functions"))
    		trace.out("functions", "parse(\"+expression+\") => "+ok+": "+errorString());
    	return ok;
    }

    /**
     * @return {@link #rules}, the grammar source
     */
	public static String[][] getRules() {
		return rules;
	}
	
	/**
	 * Print a parse tree.
	 * @return result from {@link Node#toString(int)}.
	 */
	public String generateExpressionTreeToString(){
		String returnMe = "";
		try
		{
			Node root = (Node)parser().getResult();
			returnMe = root.toString(2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			trace.err(e.getMessage());
		}
		return returnMe;
	}

	/**
	 * @return the tree generated
	 */
	public ExpressionTreeNode generateExpressionTree()
	{
		try {
			Node root = (Node)parser().getResult();
			return ((SyntaxTreeNode)root).generateExpressionTree();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//root.get
		return null;
	}
}
