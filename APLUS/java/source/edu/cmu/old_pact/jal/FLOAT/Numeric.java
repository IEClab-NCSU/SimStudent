package edu.cmu.old_pact.jal.FLOAT;

/**
 * A class that encapsulates generalized numeric algorithms on one
 * and two arrays.  All methods are static and all variables are
 * static and final, so this class has no constructors.
 *
 *<P>
 * Most methods operate on a range of elements.  A range is described
 * by the index of its first element and an index that is 
 * <strong>one past</strong> its last element.  So, for example,
 * <code>[n, n+1)</code> is a range that contains one element,
 * <code>[n, n)</code> is a range that contains zero elements,
 * and <code>[n, n-1)</code> is not a valid range.
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
 * @see Inspection
 * @see Modification
 * @see Sorting
 * @author Matthew Austern (austern@mti.sgi.com)
 * @author Alexander Stepanov (stepanov@mti.sgi.com)
 */

public final class Numeric
{
  /**
   * Add all elements in a range.  The result <code>acc</code> is set to 
   * an initial value and, for each index <code>i</code> in the range, 
   * it is set to <code>acc + array[i]</code>.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     Last element of the range.
   * @param init     Initial value of the accumulation.
   * @return         <code>init + array[first] + ... + array[last-1]</code>.
   */
  public static float accumulate(float[] array, int first, int last,
				  float init)
    {
      float acc = init;
      while (first < last)
	acc = (float)(acc + array[first++]);
      return acc;
    }

  /**
   * Generalized accumulation.  The result <code>acc</code> is set to 
   * an initial value and, for each index <code>i</code> in the range, 
   * it is set to <code>op.apply(acc, array[i])</code>.
   *
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     Last element of the range.
   * @param init     Initial value of the accumulation.
   * @param op       Binary operation.
   * @return         The result of the accumulation.
   */
  public static float accumulate(float[] array, int first, int last,
				  float init,
				  BinaryOperator op)
    {
      float acc = init;
      while (first < last)
	acc = op.apply(acc, array[first++]);
      return acc;
    }

  /**
   * Computes the inner product of two ranges.  The result <code>acc</code>
   * is set to an initial value, and, for each index <code>i</code> in
   * the range <code>[first, last)</code>, <code>acc</code> is set to
   * <code>acc + array1[i] * array2[first2 + (i - first1)]</code>.
   * @param array1    Array containing the first range.
   * @param array2    Array containing the second range.
   * @param first1    Beginning of the first range.
   * @param last1     One past the end of the first range.
   * @param first2    Beginning of the second range.
   * @param init      Initial value for the result.
   * @return          The inner product. 
   */
  public static float inner_product(float[] array1, float[] array2,
				     int first1, int last1, int first2,
				     float init)
    {
      float acc = init;
      while (first1 < last1)
	acc = (float) (acc + array1[first1++] * array2[first2++]);
      return acc;
    }
  
  /**
   * Computes the generalized inner product of two ranges.  This is
   * identical to the ordinary inner product, except that addition and
   * multiplication are replaced, respectively, by binary operations
   * <code>op1</code> and <code>op2</code>.  Specifically, the result
   * <code>acc</code> is set to an initial value, and, for each index
   * <code>i</code> in the range <code>[first, last)</code>, 
   * <code>acc</code> is set to 
   * <code>op1.apply(acc, op2.apply(array1[i] * array2[first2 + (i - first1)]))</code>.
   * @param array1    Array containing the first range.
   * @param array2    Array containing the second range.
   * @param first1    Beginning of the first range.
   * @param last1     One past the end of the first range.
   * @param first2    Beginning of the second range.
   * @param init      Initial value for the result.
   * @param op1       Binary operation that plays the role of addition.
   * @param op1       Binary operation that plays the role of multiplication.
   */
  public static float inner_product(float[] array1, float[] array2,
				     int first1, int last1, int first2,
				     float init,
				     BinaryOperator op1, BinaryOperator op2)
    {
      float acc = init;
      while (first1 < last1)
	acc = op1.apply(acc, op2.apply(array1[first1++], array2[first2++]));
      return acc;
    }

  /**
   * Computes the partial sums of elements in an input range and assigns 
   * them to elements in an output range.
   * <code>dest[to]</code> = <code>source[first]</code>,
   * <code>dest[to+1]</code> = <code>source[first] + source[first+1]</code>,
   * etc. 
   * There must be
   * enough space in the destination array, and existing elements 
   * will be overwritten.  
   * @param source    Array containing the input range.
   * @param dest      Array containing the output range.
   * @param first     Beginning of the input range.
   * @param last      One past the end of the input range.
   * @param to        Beginning of the output range.
   * @return          One past the end of the output range, that is,
   *                  <code>to + (last - first)</code>.
   */
  public static int partial_sum(float[] source, float[] dest,
				int first, int last, int to)
    {
      if (first < last) {
	dest[to] = source[first];
	float value = dest[to];
	while (++first < last) {
	  value = (float) (value + source[first]);
	  dest[++to] = value;
	}
	return to + 1;
      }
      else
	return to;
    }
				

  /**
   * Computes the generalized partial sums of elements in an input range and 
   * assigns them to elements in an output range.  Generalized partial sums
   * are identical to partial sums except that addition is replaced by an
   * arbitrary binary operation <code>op</code>.
   * <code>dest[to]</code> = <code>source[first]</code>,
   * <code>dest[to+1]</code> = <code>op.apply(source[first], source[first+1])</code>,
   * etc. 
   * There must be
   * enough space in the destination array, and existing elements 
   * will be overwritten.  
   * @param source    Array containing the input range.
   * @param dest      Array containing the output range.
   * @param first     Beginning of the input range.
   * @param last      One past the end of the input range.
   * @param to        Beginning of the output range.
   * @param op        Binary operation that plays the role of addition.
   * @return          One past the end of the output range, that is,
   *                  <code>to + (last - first)</code>.
   */
  public static int partial_sum(float[] source, float[] dest,
				int first, int last, int to,
				BinaryOperator op)
    {
      if (first < last) {
	dest[to] = source[first];
	float value = dest[to];
	while (++first < last) {
	  value = op.apply(value, source[first]);
	  dest[++to] = value;
	}
	return to + 1;
      }
      else
	return to;
    }

  /**
   * Computes the difference between each pair of adjacent elements in
   * the input range and assigns them to an output range.  Assigns
   * <code>dest[to] = source[first]</code>,
   * <code>dest[to+1] = source[first+1] - source[first]</code>,...,
   * <code>dest[to + (last-first)] = source[last-1] - source[last-2]</code>.
   * There must be
   * enough space in the destination array, and existing elements 
   * will be overwritten.  
   * @param source    Array containing the input range.
   * @param dest      Array containing the output range.
   * @param first     Beginning of the input range.
   * @param last      One past the end of the input range.
   * @param to        Beginning of the output range.
   * @return          One past the end of the output range, that is,
   *                  <code>to + (last - first)</code>.
   */
  public static int adjacent_difference(float[] source, float[] dest,
					int first, int last, int to)
    {
      if (first < last) {
	dest[to] = source[first];

	float prev_value = source[first];
	while (++first < last) {
	  float cur_value = source[first];
	  dest[++to] = (float) (cur_value - prev_value);
	  prev_value = cur_value;
	}

	return to + 1;
      }
      else
	return to;
    }
	
  /**
   * Computes a binary operation <code>op</code> for each pair of adjacent elements in
   * the input range and assigns the results to an output range.  Assigns
   * <code>dest[to] = source[first]</code>,
   * <code>dest[to+1] = op.apply(source[first+1], source[first])</code>,...,
   * <code>dest[to + (last-first)] = op.apply(source[last-1], source[last-2])</code>.
   * There must be
   * enough space in the destination array, and existing elements 
   * will be overwritten.  
   * @param source    Array containing the input range.
   * @param dest      Array containing the output range.
   * @param first     Beginning of the input range.
   * @param last      One past the end of the input range.
   * @param to        Beginning of the output range.
   * @param op        Binary operation.
   * @return          One past the end of the output range, that is,
   *                  <code>to + (last - first)</code>.
   */
  public static int adjacent_difference(float[] source, float[] dest,
					int first, int last, int to,
					BinaryOperator op)
    {
      if (first < last) {
	dest[to] = source[first];

	float prev_value = source[first];
	while (++first < last) {
	  float cur_value = source[first];
	  dest[++to] = op.apply(cur_value, prev_value);
	  prev_value = cur_value;
	}

	return to + 1;
      }
      else
	return to;
    }



  /* We don't need a constructor. */
  private Numeric() {}
 
}
