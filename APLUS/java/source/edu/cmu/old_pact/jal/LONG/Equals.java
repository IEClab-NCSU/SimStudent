package edu.cmu.old_pact.jal.LONG;

/**
 * A function object that represents a binary predicate: tests whether two
 * elements are equal.  
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
 * @author Matthew Austern (austern@mti.sgi.com)
 * @author Alexander Stepanov (stepanov@mti.sgi.com)
 *
 *
 */

public class Equals implements BinaryPredicate {
  /**
   * Tests whether or not the arguments are equal.  
   * <code>apply(x, y)</code> is equivalent to
   * <code>x == y</code>.
   * @param x   First argument
   * @param y   Second argument
   * @return    Result of the binary operation.
   */
  public boolean apply(long x, long y)
    {
      return x == y;
    }
}
