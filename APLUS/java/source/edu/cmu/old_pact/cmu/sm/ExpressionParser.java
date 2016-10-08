//ExpressionParser is an interface to a parser for expressions
//Such parsers start with a string in some format and create an Expression
//The second version of the parse() method takes a list of strings that
//should be treated as variables during the parse; unlisted symbols that
//would normally parse as variables are instead parsed as constant variables
//(which means, among other things, that they aren't "seen" by queries like
//"variable side expression".  Currently the parser only supports
//single-character variables.

package edu.cmu.old_pact.cmu.sm;

public interface ExpressionParser {
	public void init();
	public Expression parse(String inputExpression) throws ParseException;
        public Expression parse(String inputExpression,String[] vars) throws ParseException;
}
