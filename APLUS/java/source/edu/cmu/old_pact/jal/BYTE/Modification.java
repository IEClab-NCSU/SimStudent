package edu.cmu.old_pact.jal.BYTE;
import java.util.Random;

/**
 * A class that encapsulates mutating sequence algorithms on one
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
 * @see Sorting
 * @see Numeric
 * @author Matthew Austern (austern@mti.sgi.com)
 * @author Alexander Stepanov (stepanov@mti.sgi.com)
 */

public final class Modification
{
  /**
   * Copy elements from one location to another.  There must be
   * enough space in the destination array, and existing elements 
   * will be overwritten.  Note: the source and destination ranges are
   * permitted to be in the same range and are permitted to overlap.
   * @param source      Array from which elements are copied
   * @param destination Array to which elements are copied
   * @param first       Beginning of the range from which elements are copied
   * @param last        One past the end of the range
   * @param to          Beginning of the range to which elements will be
   *                    copied.  
   * @exception         ArrayIndexOutOfBoundsException If the input or
   *                    output range is invalid.
   */
  static public void copy(byte[] source, byte[] destination,
			  int first, int last, int to)
    {
      if (last > first)
	System.arraycopy(source, first, destination, to, last - first);
    }

  /**
   * Performs a pairwise swap of two ranges.  That is: for every index
   * <code>i</code> in the range <code>[first1,last1)</code>, swaps
   * <code>array1[i]</code> and <code>array2[first2 + (i-first1)]</code>.
   * Note: if the two ranges are in the same array, they are not
   * permitted to overlap.                
   * @param array1      Array containing the first range.
   * @param array2      Array containing the second range.
   * @param first1      Beginning of the first range.
   * @param last1       One past the end of the first range
   * @param first2      Beginning of the second range.
   */
  static public void swap_ranges(byte[] array1, byte[] array2,
				 int first1, int last1, int first2)
    {
      while (first1 < last1) {
	byte tmp = array2[first2];
	array2[first2] = array1[first1];
	array1[first1] = tmp;
	++first1;
	++first2;
      }
    }

  /**
   * Performs an operation on every element of a range and assigns the result
   * to elements in another range.  That is: for every index <code>i</code>
   * in the range <code>[first,last)</code>, performs the operation
   * <code>destination[to + (i-first)] = f.apply(source[i])</code>.
   * The destination array must contain
   * sufficient space, and existing elements will be overwritten.
   * @param source      Array containing the elements to be operated on.
   * @param destination Array in which results of the operation will be
   *                    stored.
   * @param first       Beginning of the input range.
   * @param last        One past the end of the input range.
   * @param to          Beginning of the output range.
   * @param f           Operation to perform on elements of the
   *                    input range.
   */
  public static void transform(byte[] source, byte[] destination,
			       int first, int last, int to,
			       UnaryOperator f)
    {
      while (first < last)
	destination[to++] = f.apply(source[first++]);
    }

  /**
   * Performs a binary operation on elements of two ranges, assigning the
   * result to elements of another range.  That is: for every index <code>i</code>
   * in the range <code>[first1,last1)</code>, performs the operation
   * <code>destination[to + (i-first1)] =</code>
   * <code>f.apply(source1[i], source2[first2 + (i-first1)])</code>.
   * The destination array must contain
   * sufficient space, and existing elements will be overwritten.
   * @param source1     Array containing first range of input elements.
   * @param source2     Array containing second range of input elements.
   * @param destination Array in which results of the operation will be
   *                    stored.
   * @param first1      Beginning of the first input range.
   * @param last1       One past the end of the first input range.
   * @param first2      Beginning of the second input range.
   * @param to          Beginning of the output range.
   * @param f           Operation to perform on elements of the
   *                    input range.
   */
  public static void transform(byte[] source1, byte[] source2,
			       byte[] destination,
			       int first1, int last1, int first2, int to,
			       BinaryOperator f)
    {
      while (first1 < last1)
	destination[to++] = f.apply(source1[first1++], source2[first2++]);
    }

  /**
   * Performs in-place substitution on a range of elements.  All elements
   * equal to <code>old_value</code> are replaced by <code>new_value</code>.
   * @param array     Array containing the range.
   * @param first     Beginning of the range.
   * @param last      One past the end of the range.
   * @param old_value Value that will be replaced.
   * @param new_value Value that old_value will be replaced with.
   */
  public static void replace(byte[] array, int first, int last,
			     byte old_value, byte new_value)
    {
      while (first < last) {
	if (array[first] == old_value)
	  array[first] = new_value;
	++first;
      }
    }

  /**
   * Performs in-place substitution on a range of elements.  Every element
   * <code>E</code> for which <code>p.apply(E)</code> is <code>true</code>
   * are replaced by <code>new_value</code>.
   * @param array     Array containing the range.
   * @param first     Beginning of the range.
   * @param last      One past the end of the range.
   * @param p         Condition for replacement.
   * @param new_value Value to be substituted for replaced elements.
   */
  public static void replace_if(byte[] array, int first, int last,
				Predicate p, byte new_value)
    {
      while (first < last) {
	if (p.apply(array[first]))
	  array[first] = new_value;
	++first;
      }
    }

  /**
   * Performs copying and substitution on a range of elements.  The elements
   * in the input range are copied to an output range, except that 
   * <code>new_value</code> is substituted for any elements that are equal
   * to <code>old_value</code>.
   * The destination array must contain
   * sufficient space, and existing elements will be overwritten.
   * @param source      Array containing the input range.
   * @param destination Array containing the output range.
   * @param first       Beginning of the input range.
   * @param last        One past the end of the input range.
   * @param to          Beginning of the output range.
   * @param old_value   Value to be replaced.
   * @param new_value   Value that old_value will be replaced with.
   */
  public static void replace_copy(byte[] source, byte[] destination,
				  int first, int last, int to,
				  byte old_value, byte new_value)
    {
      while (first < last) {
	byte tmp = source[first++];
	destination[to++] = (tmp == old_value) ? new_value : tmp;
      }
    }

  /**
   * Performs copying and substitution on a range of elements.  The elements
   * in the input range are copied to an output range, except that 
   * <code>new_value</code> is substituted for any elements <code>E</code>
   * that satisfy the condition <code>p.apply(E)</code>.
   * The destination array must contain
   * sufficient space, and existing elements will be overwritten.
   * @param source      Array containing the input range.
   * @param destination Array containing the output range.
   * @param first       Beginning of the input range.
   * @param last        One past the end of the input range.
   * @param to          Beginning of the output range.
   * @param p           Condition for replacement.
   * @param new_value   Value to be substituted for replaced elements.
   */
  public static void replace_copy_if(byte[] source, byte[] destination,
				     int first, int last, int to,
				     Predicate p, byte new_value)
    {
      while (first < last) {
	byte tmp = source[first++];
	destination[to++] = p.apply(tmp) ? new_value : tmp;
      }
    }
  
  /** 
   * Assigns a value to every element in a range.  The array must contain
   * sufficient space, and existing elements will be overwritten.
   * @param array    Array containing the range
   * @param first    Beginning of the range  
   * @param last     One past the end of the range
   * @param x        Value to be assigned to elements in the range
   */
  public static void fill(byte[] array, int first, int last, 
			  byte x)
    {
      while(first < last) 
	array[first++] = x;
    }

  /**
   * Assigns values, produced by a function object that takes no arguments,
   * to each element of a range.  The array must contain
   * sufficient space, and existing elements will be overwritten.
   * @param array    Array containing the range
   * @param first    Beginning of the range  
   * @param last     One past the end of the range
   * @param f        Source of values to be assigned to elements in
   *                 the range.  <code>f.apply()</code> is evaluated
   *                 exactly <code>last-first</code> times.
   */
  public static void generate(byte[] array, int first, int last,
			      Generator f)
    {
      while(first < last) 
	array[first++] = f.apply();
    }

  /**
   * Remove all elements from a range that are equal to a given value.
   * It is not guaranteed that the relative order of remaining elements is
   * unchanged.
   * @param array    Array containing the range 
   * @param first    Beginning of the range 
   * @param last     One past the end of the range
   * @param x        Value to be removed.
   * @return         An index <code>i</code> such that all remaining elements
   *                 are contained in the range <code>[first, i)</code>.
   */
  public static int remove_if(byte[] array, int first, int last, 
			      byte x)
    {
      int oldLast = last;
      --first;
      while (true) {
	while (++first < last && array[first] != x); 
	while (first < --last && array[last] == x); 
	if (first >= last) {
	  return first;
	}
	array[first] = array[last];
      }
    }


  /**
   * Remove all elements from a range that satisfy a specified condition.
   * It is not guaranteed that the relative order of remaining elements is
   * unchanged.
   * @param array    Array containing the range 
   * @param first    Beginning of the range 
   * @param last     One past the end of the range
   * @param p        Condition being tested
   * @return         An index <code>i</code> such that all remaining elements
   *                 are contained in the range <code>[first, i)</code>.
   */
  public static int remove_if(byte[] array, int first, int last, 
			      Predicate p)
    {
      int oldLast = last;
      --first;
      while (true) {
	while (++first < last && !p.apply(array[first])); 
	while (first < --last && p.apply(array[last])); 
	if (first >= last) {
	  return first;
	}
	array[first] = array[last];
      }
    }

  /**
   * Remove all elements from a range that are equal to a given value.
   * It is guaranteed that the relative order of remaining elements is
   * unchanged.
   * @param array    Array containing the range. 
   * @param first    Beginning of the range. 
   * @param last     One past the end of the range.
   * @param x        Value to be removed.
   * @return         An index <code>i</code> such that all remaining elements
   *                 are contained in the range <code>[first, i)</code>.
   */
  public static int stable_remove(byte[] array, int first, int last, 
				  byte x)
    {
      first = Inspection.find(array, first, last, x);
      int next = Inspection.find_not(array, first, last, x);
      while (next < last) {
	array[first++] = array[next];
	next = Inspection.find_not(array, ++next, last, x);
      }
      return first;
    }

  /**
   * Remove all elements from a range that satisfy a specified condition.
   * It is guaranteed that the relative order of remaining elements is
   * unchanged.
   * @param array    Array containing the range 
   * @param first    Beginning of the range 
   * @param last     One past the end of the range
   * @param p        Condition being tested
   * @return         An index <code>i</code> such that all remaining elements
   *                 are contained in the range <code>[first, i)</code>.
   */
  public static int stable_remove_if(byte[] array, int first, int last, 
				     Predicate p)
    {
      first = Inspection.find_if(array, first, last, p);
      int next = Inspection.find_if_not(array, first, last, p);
      while (next < last) {
	array[first++] = array[next];
	next = Inspection.find_if_not(array, ++next, last, p);
      }
      return first;
    }

  /**
   * Copies all of the elements in a range except for those that are
   * equal to a given value.  It is guaranteed that the relative order of 
   * elements that are copied is the same as in the input range.
   * The output array must contain
   * sufficient space, and existing elements will be overwritten.
   * @param source      Array containing the input range. 
   * @param destination Array containing the output range. 
   * @param first       Beginning of the input range 
   * @param last        One past the end of the input range
   * @param to          Beginning of the output range.
   * @param value       Value to be removed.
   * @return            An index i such that the resulting output range
   *                    is <code>[to, i)</code>.
   */
  static public int remove_copy(byte[] source, byte[] destination,
				int first, int last, int to,
				byte value)
    {
      while (first < last) {
	byte tmp = source[first++];
	if (tmp != value)
	  destination[to++] = tmp;
      }
      return to;
    }

  /**
   * Copies all of the elements in a range except for those that satisfy
   * a given condition.  It is guaranteed that the relative order of 
   * elements that are copied is the same as in the input range.
   * The output array must contain
   * sufficient space, and existing elements will be overwritten.
   * @param source      Array containing the input range. 
   * @param destination Array containing the output range. 
   * @param first       Beginning of the input range 
   * @param last        One past the end of the input range
   * @param to          Beginning of the output range.
   * @param p           Condition for removal.
   * @return            An index i such that the resulting output range
   *                    is <code>[to, i)</code>.
   */
  static public int remove_copy_if(byte[] source, byte[] destination,
				   int first, int last, int to,
				   Predicate p)
    {
      while (first < last) {
	byte tmp = source[first++];
	if (!p.apply(tmp))
	  destination[to++] = tmp;
      }
      return to;
    }


  /**
   * Eliminates all but the first element of every consecutive group
   * of equal elements.  The relative order of remaining elements is
   * guaranteed to be unchanged.
   * @param array       Array containing the range
   * @param first       Beginning of the input range 
   * @param last        One past the end of the input range
   * @return            An index i such that the resulting output range
   *                    is <code>[first, i)</code>.
   */
  public static int unique(byte[] array, int first, int last)
    {
      first = Inspection.adjacent_find(array, first, last);
      return unique_copy(array, array, first, last, first);
    }

  /**
   * Eliminates all but the first element of every consecutive group
   * of equivalent elements, where equivalence is determined by a
   * supplied predicate.
   * The relative order of remaining elements is
   * guaranteed to be unchanged.
   * @param array       Array containing the range
   * @param first       Beginning of the input range 
   * @param last        One past the end of the input range
   * @param p           Predicate used to determine equivalence.
   * @return            An index i such that the resulting output range
   *                    is <code>[first, i)</code>.
   */
  public static int unique(byte[] array, int first, int last,
			   BinaryPredicate p)
    {
      first = Inspection.adjacent_find(array, first, last, p);
      return unique_copy(array, array, first, last, first, p);
    }

  /**
   * Copies elements from an input range to an output range, except that
   * only the first element is copied from every consecutive group of 
   * equal elements.
   * The relative order of elements that are copied is
   * guaranteed to be the same as in the input range.
   * The output array must contain
   * sufficient space, and existing elements will be overwritten.
   * @param source      Array containing the input range.
   * @param destination Array containing the output range.
   * @param first       Beginning of the input range. 
   * @param last        One past the end of the input range.
   * @param to          Beginning of the output range.
   * @return            An index i such that the resulting output range
   *                    is <code>[to, i)</code>.
   */
  public static int unique_copy(byte[] source, byte[] destination,
				int first, int last, int to)
    {
      if (first >= last)
	return to;
      else
	destination[to] = source[first];

      while (++first < last) {
	if (destination[to] != source[first])
	  destination[++to] = source[first];
      }

      return to + 1;	
    }

  /**
   * Copies elements from an input range to an output range, except that
   * only the first element is copied from every consecutive group of 
   * equivalent elements; equivalence is determined by a
   * supplied predicate.
   * The relative order of elements that are copied is
   * guaranteed to be the same as in the input range.
   * The output array must contain
   * sufficient space, and existing elements will be overwritten.
   * @param source      Array containing the input range.
   * @param destination Array containing the output range.
   * @param first       Beginning of the input range. 
   * @param last        One past the end of the input range.
   * @param to          Beginning of the output range.
   * @param p           Predicate used to determine equivalence.
   * @return            An index i such that the resulting output range
   *                    is <code>[to, i)</code>.
   */
  public static int unique_copy(byte[] source, byte[] destination,
				int first, int last, int to,
				BinaryPredicate p)
    {
      if (first >= last)
	return to;
      else
	destination[to] = source[first];

      while (++first < last) {
	if (!p.apply(destination[to], source[first]))
	  destination[++to] = source[first];
      }

      return to + 1;	
    }

  /** 
   * Reverses a sequence of elements.
   * @param array      Array containing the sequence
   * @param first      Beginning of the range
   * @param last       One past the end of the range
   * @exception        ArrayIndexOutOfBoundsException If the range
   *                   is invalid.
   */
  static public void reverse(byte[] array, int first, int last)
    {
      while (first < --last) {
	byte tmp = array[first];
	array[first++] = array[last];
	array[last] = tmp;
      }
    }

  public static void reverse_copy(byte[] array, int first, int last, int to)
    {
      while (last > first)
	array[to++] = array[--last];
    }

  /**
   * Creates a copy of an input range consisting of that range in
   * reverse order; equivalent to copy followed by reverse, but faster.
   * There must be enough space in the array, and existing elements will
   * be overwritten.  Note: if <code>source</code> and
   * <code>destination</code> are the same array, the input and output
   * ranges are <strong>not</strong> permitted to overlap.
   * @param source      Array containing the input range.
   * @param destination Array containing the output range.
   * @param first       Beginning of the input range    
   * @param last        One past the end of the input range
   * @param to          First element of the output range
   */
  public static void reverse_copy(byte[] source, byte[] destination,
				  int first, int last, int to)
    {
      while (last > first)
	destination[to++] = source[--last];
    }

  /**
   * Rotate a range in place: <code>array[middle]</code> is put in
   * <code>array[first]</code>, <code>array[middle+1]</code> is put in
   * <code>array[first+1]</code>, etc.  Generally, the element in position
   * <code>i</code> is put into position 
   * <code>(i + (last-middle)) % (last-first)</code>.
   * @param array    Array containing the range
   * @param first    Beginning of the range
   * @param middle   Index of the element that will be put in
   *                 <code>array[first]</code>
   * @param last     One past the end of the range
   */
  public static void rotate(byte[] array, int first, int middle, 
				 int last)
    {
      if (middle != first && middle != last) {
	reverse(array, first, middle);
	reverse(array, middle, last);
	reverse(array, first, last);
      }
    }

  /**
   * Creates a copy of an input range consisting of a rotation of that
   * range.  Specifically: for each i, <code>first + i</code> is assigned to
   * <code>to + (i + (last-middle)) % (last-first)</code>.  
   * There must be enough space in the output array, and existing elements 
   * will be overwritten.  Note: if <code>source</code> and
   * <code>destination</code> are the same array, the input and output
   * ranges are <strong>not</strong> permitted to overlap.
   * @param source   Array containing the input range.
   * @param destination Array containing the output range.
   * @param first    Beginning of the input range    
   * @param middle   Element that is mapped to <code>to</code>.
   * @param last     One past the end of the input range
   * @param to       First element of the output range
   */
  public static void rotate_copy(byte[] source, byte[] destination,
				 int first, int middle, int last, int to)

    {
      copy(source, destination, middle, last, to);
      copy(source, destination, first, middle, to + (last - middle));
    }

  /** 
   * Shuffles elements in a range, with uniform distribution.  
   * @param array     Array containing the range to be shuffled
   * @param first     Beginning of the range
   * @param last      One past the end of the range
   * @param RNG       Object of class <code>java.util.Random</code>,
   *                  used to supply random numbers.
   */
  public static void random_shuffle(byte[] array, int first, int last,
				    Random RNG)
    {
      for (int i = first + 1; i < last; ++i) {
	int randomPlace =  Math.abs(RNG.nextInt()) % 
	  ((i - first) + 1);
	byte tmp = array[randomPlace];
	array[randomPlace] = array[i];
	array[i] = tmp;
      }
    }

  private static Random default_RNG = new Random();

  /** 
   * Shuffles elements in a range, with uniform distribution.  
   * Uses a default random number generator.
   * @param array     Array containing the range to be shuffled
   * @param first     Beginning of the range
   * @param last      One past the end of the range
   */
  public static void random_shuffle(byte[] array, int first, int last)
    {
      random_shuffle(array, first, last, default_RNG);
    }

  /** 
   * Rearranges elements in a range such that all elements that satisfy 
   * a condition are placed before all elements that do not satisfy it.
   * @param array    Array containing the range
   * @param first    Beginning of the range
   * @param last     One past the end of the range                
   * @param p        Condition being tested
   * @return         An index <code>a</code> such that for all
   *                 <code>first <= i < a</code>,
   *                 <code>p.apply(array[i])</code> is <code>true</code>
   *                 and such that for all
   *                 <code>a <= i < last</code>, 
   *                 <code>p.apply(array[i])</code> is <code>false</code>.
   * @see   Predicate         
   */
  public static int partition(byte[] array, int first, int last, 
			      Predicate p)
    {
      --first;
      while (true) {
	while (++first < last && p.apply(array[first]));
	while (first < --last && !p.apply(array[last])); 
	if (first >= last) return first;
	byte tmp = array[first];
	array[first] = array[last];
	array[last] = tmp;
      }
    }

  /**
   * Rearranges elements in a range such that all elements that satisfy 
   * a condition are placed before all elements that do not satisfy it.
   * It is guaranteed that the relative ordering within each group is
   * unchanged.
   * @param array    Array containing the range
   * @param first    Beginning of the range
   * @param last     One past the end of the range                
   * @param p        Condition being tested
   * @return         An index <code>a</code> such that for all
   *                 <code>first <= i < a</code>,
   *                 <code>p.apply(array[i])</code> is <code>true</code>
   *                 and such that for all
   *                 <code>a <= i < last</code>, 
   *                 <code>p.apply(array[i])</code> is <code>false</code>.
   * @see   Predicate         
   */
  public static int stable_partition(byte[] array, int first, 
				     int last, Predicate p)
    {
      if (first + 1 < last) {
	int middle = first + (last - first) / 2;
	int firstCut = stable_partition(array, first, middle, p);
	int secondCut = stable_partition(array, middle, last, p);
	rotate(array, firstCut, middle, secondCut);
	return firstCut + (secondCut - middle);
      }
      if (first >= last || !p.apply(array[first])) 
	return first;
      else
	return last;
    }

  /* We don't need a constructor. */
  private Modification() {}
}
