package edu.cmu.old_pact.jal.CHAR;

/**
 * Interface that represents a function object: a function that takes 
 *  a single argument and does not return a value.
 *
 * <P>Copyright &copy; 1996
 * Silicon Graphics, Inc.
 *
 * <BR>Permission to use, copy, modify, distribute and sell this software
 * and its documentation for any purpose is hereby granted without fee,
 * provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear
 * in supporting documentation.  Silicon Graphics makes no
 * representations about the suitability of this software for any
 * purpose.  It is provided &quot;as is&quot; without express or 
 * implied warranty.
 *
 * @see Generator
 * @see UnaryOperator
 * @see BinaryOperator
 * @author Matthew Austern (austern@mti.sgi.com)
 * @author Alexander Stepanov (stepanov@mti.sgi.com)
 */

public interface VoidFunction {
  /**
   * Applies a function to an argument
   * @param x   Value passed to the function.
   */
  abstract public void apply(char x);
}
