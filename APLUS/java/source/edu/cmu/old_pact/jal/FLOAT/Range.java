package edu.cmu.old_pact.jal.FLOAT;

/**
 * A range of values within an array, consisting of the array, the index of
 * the first element in the range, and an index one past the range.  The
 * notation is <code>[first, last)</code>, indicating that 
 * <code>array[first]</code> is part of the range but 
 * <code>array[last]</code> is not.  The range <code>[n, n)</code> is a
 * valid range that contains zero elements, while 
 * the range <code>[n, n-1)</code> is invalid.
 *
 * <P>Note that operations on a Range object do not actually change any
 * array elements.  A Range is simply a way of describing a set of values.
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
 */

public final class Range {
  /**
   * Constructs a Range.
   * @param array    Array containing the range
   * @param first    Index of the first element in the range
   * @param last     Index that is one past the last element in the range
   */
  public Range(float[] array, int first, int last)
    {
      super();
      this.array = array;
      this.first = first;
      this.last = last;
    }
 
  /**
   * Constructs a Range that represents an entire array.  Equivalent
   * to <code>Range(array, 0, array.length)</code>.
   * @param array    Array containing the range
   */
  public Range(float[] array)
    {
      this(array, 0, array.length);
    }


  /**
   * Creates a string representation of this Range.
   * @return    A string of the form &quot;<code>[first, last)</code>&quot;.
   */
  public String toString()
    {
      return "[" + first + ", " + last + ")";
    }

  /**
   * Array containing the range.
   */
  public float[] array;

  /**
   * Index of the first element in the range.
   */
  public int first;

  /**
   * Index that is one past the last element in the range.
   */
  public int last;
}
