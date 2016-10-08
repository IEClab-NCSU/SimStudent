package edu.cmu.old_pact.jal.CHAR;

/**
 * Interface that represents a function object: a predicate that returns
 *  true or false depending on some binary operation involving the
 *  arguments.  Examples include equality tests and comparison functions.
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
 *  
 * @see Predicate
 * @author Matthew Austern (austern@mti.sgi.com)
 * @author Alexander Stepanov (stepanov@mti.sgi.com)
 *
 *
 */

public interface BinaryPredicate {
  /**
   * Tests whether or not the arguments satisfy some condition.
   * @param x   First argument
   * @param y   Second argument
   * @return    Result of the binary operation.
   */
  abstract public boolean apply(char x, char y);
}
