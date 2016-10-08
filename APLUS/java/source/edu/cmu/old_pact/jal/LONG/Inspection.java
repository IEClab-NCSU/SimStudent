package edu.cmu.old_pact.jal.LONG;

/**
 * A class that encapsulates non-mutating sequence algorithms on one
 * and two arrays.  All methods are static and all variables are
 * static and final, so this class has no constructors.
 *
 * <P>
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
 * @see Modification
 * @see Sorting
 * @see Numeric
 * @author Matthew Austern (austern@mti.sgi.com)
 * @author Alexander Stepanov (stepanov@mti.sgi.com)
 */

final public class Inspection     
{
  /**
   * Applies a function to every element of a range.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param f        Function to be applied.
   */
  public static void for_each(long[] array, int first, int last,
			      VoidFunction f)
    {
      while(first < last)
	f.apply(array[first++]);
    }

  /**
   * Finds the first adjacent pair of equal elements in a range.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @return         The first iterator <code>i</code> in the range
   *                 <code>[first, last-1)</code> such that 
   *                 <code>array[i] == array[i+1]</code>.  Returns
   *                 <code>last</code> if no such iterator exists.
   */
  public static int adjacent_find(long[] array, int first, int last)
    {
      int next = first;
      while (++next < last) {
	if (array[first] == array[next])
	  return first;
	else
	  first = next;
      }
      
      return last;
    }

  /**
   * Finds the first adjacent pair of elements in a range that satisfy
   * some condition.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param p        Condition that must be satisfied.
   * @return         The first iterator <code>i</code> in the range
   *                 <code>[first, last-1)</code> such that 
   *                 <code>p.apply(array[i], array[i+1])</code> is
   *                 <code>true</code>.  Returns
   *                 <code>last</code> if no such iterator exists.
   */
  public static int adjacent_find(long[] array, int first, int last,
				  BinaryPredicate p)
    {
      int next = first;
      while (++next < last) {
	if (p.apply(array[first], array[next]))
	  return first;
	else
	  first = next;
      }
      
      return last;
    }


  /** 
   * Finds the first element in a range that is equal to a specified value.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param x        Value to be searched for.
   * @return         Index of first element <code>E</code> such that
   *                 <code>E == x</code>, or <code>last</code> if no such
   *                 element exists.
   */
  public static int find(long[] array, int first, int last, long x)
    {
      while (first < last && !(x == array[first]))
	++first;
      return first;
    }

  /** 
   * Finds the first element in a range that is not equal to a specified value.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param x        Value to be searched for.
   * @return         Index of first element <code>E</code> such that
   *                 <code>E != x</code>, or <code>last</code> if no such
   *                 element exists.
   */
  public static int find_not(long[] array, int first, int last, long x)
    {
      while (first < last && x == array[first])
	++first;
      return first;
    }

  /** 
   * Finds the first element in a range that satisfies a condition.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param p        Condition to search for.
   * @return         Index of first element <code>E</code> such that
   *                 <code>E == x</code>, or <code>last</code> if no such
   *                 element exists.
   */
  public static int find_if(long[] array, int first, int last, 
			    Predicate p)
    {
      while (first < last && !p.apply(array[first]))
	++first;
      return first;
    }

  /** 
   * Finds the first element in a range that does not satisfy some condition.
   * @param array    Array containing the range
   * @param first    Beginning of the range
   * @param last     One past the end of the range
   * @param p        Condition being tested
   * @return         Index of first element <code>E</code> such that 
   *                 <code>p.apply(E)</code> is <code>false</code>, or
   *                 <code>last</code> if no such element exists.
   */
  public static int find_if_not(long[] array, int first, int last, 
			      Predicate p)
    {
      while (first < last && p.apply(array[first]))
	++first;
      return first;
    }




  /** 
   * Counts the number of elements in a range that satisfy a condition.
   * @param array    Array containing the range
   * @param first    Beginning of the range
   * @param last     One past the end of the range
   * @param p        Condition being tested
   * @return         Number of elements <code>E</code> such that
   *                  <code>p.apply(E)</code> is <code>true</code>.
   */
  public static int count_if(long[] array, int first, int last, 
			    Predicate p)
    {
      int counter = 0;
      while (first < last) 
	if (p.apply(array[first++])) ++counter;
      return counter;
    }

  /** 
   * Counts the number of elements in a range that do not satisfy a condition.
   * @param array    Array containing the range
   * @param first    Beginning of the range
   * @param last     One past the end of the range
   * @param p        Condition being tested
   * @return         Number of elements <code>E</code> such that
   *                 <code>p.apply(E)</code> is <code>false</code>.
   */
  public static int count_if_not(long[] array, int first, int last, 
				 Predicate p)
    {
      int counter = 0;
      while (first < last) 
	if (!p.apply(array[first++])) ++counter;
      return counter;
    }
  
  /**
   * Finds the first location at which two ranges differ.  
   * Note: the two
   * ranges are permitted to be in the same array and are permitted to
   * overlap.
   * @param array1   Array containing the first range.
   * @param array2   Array containing the first range.
   * @param first1   Beginning of the first range
   * @param last1    One past the end of the first range
   * @param first2   Beginning of the second range
   * @return         The first index <code>i</code> such that
   *                 <code>array1[i] != array2[first2 + (i-first1)]</code>,
   *                 or <code>last1</code> if no such index in the range
   *                 <code>[first1,last1)</code> exists.
   */
  public static int mismatch(long[] array1, long[] array2,
			     int first1, int last1, int first2)
    {
      while(first1 < last1 && array1[first1] == array2[first2]) {
	++first1;
	++first2;
      }
      return first1;
    }

  /**
   * Finds the first location at which two ranges fail to satisfy a condition.
   * Note: the two
   * ranges are permitted to be in the same array and are permitted to
   * overlap.
   * @param array1   Array containing the first range.
   * @param array2   Array containing the first range.
   * @param first1   Beginning of the first range
   * @param last1    One past the end of the first range
   * @param first2   Beginning of the second range
   * @param p        Condition to be tested
   * @return         The first index <code>i</code> such that
   *                 <code>p.apply(array1[i], array2[first2 + (i-first1)])</code>
   *                 is <code>false</code>, or <code>last1</code> if no such 
   *                 index in the range <code>[first1,last1)</code> exists.
   */
  public static int mismatch(long[] array1, long[] array2,
			     int first1, int last1, int first2,
			     BinaryPredicate p)
    {
      while(first1 < last1 && p.apply(array1[first1], array2[first2])) {
	++first1;
	++first2;
      }
      return first1;
    }

  /**
   * Tests whether two ranges are pairwise equal.
   * Note: the two
   * ranges are permitted to be in the same array and are permitted to
   * overlap.
   * @param array1   Array containing the first range.
   * @param array2   Array containing the first range.
   * @param first1   Beginning of the first range
   * @param last1    One past the end of the first range
   * @param first2   Beginning of the second range
   * @return         <code>true</code> if, for every index <code>i</code>
   *                 in the range <code>[first1,last1)</code>, 
   *                 <code>array1[i] == array2[first2 + (i-first1)]</code>,
   *                 otherwise returns <code>false</code>.
   */
  public static boolean equal(long[] array1, long[] array2,
			      int first1, int last1, int first2)
    {
      while (first1 < last1 && array1[first1] == array2[first2]) {
	++first1;
	++first2;
      }

      return first1 >= last1;
    }

  /**
   * Tests whether two ranges satisfiy a condition pairwise.
   * Note: the two
   * ranges are permitted to be in the same array and are permitted to
   * overlap.
   * @param array1   Array containing the first range.
   * @param array2   Array containing the first range.
   * @param first1   Beginning of the first range.
   * @param last1    One past the end of the first range.
   * @param first2   Beginning of the second range.
   * @param p        Condition to be tested
   * @return         <code>true</code> if, for every index <code>i</code>
   *                 in the range <code>[first1,last1)</code>, 
   *                 <code>p.apply(array1[i], array2[first2 + (i-first1)])</code>
   *                 is <code>true</code>, otherwise returns <code>false</code>.
   */
  public static boolean equal(long[] array1, long[] array2,
			      int first1, int last1, int first2,
			      BinaryPredicate p)
    {
      while (first1 < last1 && p.apply(array1[first1], array2[first2])) {
	++first1;
	++first2;
      }

      return first1 >= last1;
    }

  /**
   * Searches, within one range, for a sequence of elements equal
   * to the elements in a second range.
   * 
   * Note: the two
   * ranges are permitted to be in the same array and are permitted to
   * overlap.
   * Note: the worst-case performance of this algorithm is quadratic.
   * @param array1   Array containing the first range.
   * @param array2   Array containing the first range.
   * @param first1   Beginning of the first range.
   * @param last1    One past the end of the first range.
   * @param first2   Beginning of the second range.
   * @param last2    One past the end of the second range.
   * @return         The first index in the range
   *                 <code> [first1, last1-len) </code> such that
   *                 for every non-negative <code>i&lt;len</code> 
   *                 (where <code> len = last2-first2</code>), the
   *                 condition 
   *                 <code>array1[first1+n] == array2[first1+n]</code>
   *                 is satisfied, or <code>last1</code> if no such
   *                 index exists.
   */
  public static int search(long[] array1, long[] array2,
			       int first1, int last1,
			       int first2, int last2)
    {
      int len1 = last1 - first1;
      int len2 = last2 - first2;
      int cur1 = first1;
      int cur2 = first2;

      if (len1 < len2)
	return last1;

      while (cur2 < last2) {
	if (array1[cur1++] != array2[cur2++]) {
	  if (len1 == len2)
	    return last1;
	  else {
	    cur1 = ++first1;
	    cur2 = first2;
	    --len1;
	  }
	}
      }
      
      return (cur2 == last2) ? first1 : last1;
    }

  /**
   * Searches, within one range, for a sequence of elements that match
   * the elements in a second range.  Matching is defined as satisfying
   * a BinaryPredicate passed as an argument.
   * 
   * Note: the two
   * ranges are permitted to be in the same array and are permitted to
   * overlap.
   * Note: the worst-case performance of this algorithm is quadratic.
   * @param array1   Array containing the first range.
   * @param array2   Array containing the first range.
   * @param first1   Beginning of the first range.
   * @param last1    One past the end of the first range.
   * @param first2   Beginning of the second range.
   * @param last2    One past the end of the second range.
   * @param p        Condition to be tested pairwise.
   * @return         The first index in the range
   *                 <code>[first1, last1-len)</code> such that
   *                 for every non-negative <code>i&lt;len</code> 
   *                 (where <code>len = last2-first2</code>), the
   *                 condition 
   *                 <code>p.apply(array1[first1+n],array2[first1+n])</code>
   *                 is satisfied, or <code>last1</code> if no such
   *                 index exists.
   */
  public static int search(long[] array1, long[] array2,
			       int first1, int last1,
			       int first2, int last2,
			       BinaryPredicate p)
    {
      int len1 = last1 - first1;
      int len2 = last2 - first2;
      int cur1 = first1;
      int cur2 = first2;

      if (len1 < len2)
	return last1;

      while (cur2 < last2) {
	if (!p.apply(array1[cur1++], array2[cur2++])) {
	  if (len1 == len2)
	    return last1;
	  else {
	    cur1 = ++first1;
	    cur2 = first2;
	    --len1;
	  }
	}
      }
      
      return (cur2 == last2) ? first1 : last1;
    }

  /* We don't need a constructor. */
  private Inspection() {}

}

