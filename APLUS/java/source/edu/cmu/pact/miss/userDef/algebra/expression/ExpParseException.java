package edu.cmu.pact.miss.userDef.algebra.expression;
/**
 * An Exception class to represent an exception from parsing an AlgExp expression
 * @author ajzana
 *
 */
public class ExpParseException extends Exception
{
 
    private static final long serialVersionUID = 1L;

    ExpParseException(String s)
    {
        super(s);
    }
}
