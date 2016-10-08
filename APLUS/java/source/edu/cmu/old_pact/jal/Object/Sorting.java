package edu.cmu.old_pact.jal.Object;

/**
 * A class that encapsulates sorting and related algorithms on one
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
 * <P>
 * Many methods require a <em>comparison function</em>, an object of class
 * BinaryPredicate.  If <code>comp</code> is a comparison function, then
 * <code>comp.apply(a,b)</code> should return <code>true</code> if 
 * <code>a</code> is less than <code>b</code>, and <code>false</code>
 * if <code>a</code> is greater than or equal to <code>b</code>.  In 
 * particular, <code>comp</code> must satisfy the requirement that, 
 * for any element <code>a</code>, <code>comp.apply(a,a)</code> is
 * <code>false</code>.
 *
 * <P>
 * Note that an inequality operator defines an equivalence relation:
 * two elements <code>a</code> and <code>b</code> are equivalent if
 * and only if the relations <code>comp.apply(a,b)</code> and
 * <code>comp.apply(b,a)</code> are both <code>false</code>.  Unless
 * explicitly stated otherwise, the algorithms in this class always
 * use this equivalence relation rather than the <code>==</code> 
 * operator or <code>Object.equals()</code>.
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
 * @see Numeric
 * @author Matthew Austern (austern@mti.sgi.com)
 * @author Alexander Stepanov (stepanov@mti.sgi.com)
 */

public final class Sorting
{

  /**
   * Sort a range of elements by a user-supplied comparison function.
   * Uses the quicksort algorithm.  Average 
   * performance goes as <code>N log N</code>; worst-case performance
   * is quadratic, but this case is extremely rare.
   * @param array       Array containing the range.
   * @param first       Beginning of the range.
   * @param last        One past the end of the range.
   * @param comp        Comparison function.  
   * @see Sorting#stable_sort
   * @see Sorting#insertion_sort
   * @see Sorting#partial_sort
   */
  public static void sort(Object[] array, int first, int last,
			  BinaryPredicate comp)
    { 
      if (last - first >= partitionCutoff)
	qsortLoop(array, first, last, comp);
      insertion_sort(array, first, last, comp);
    }




  /**
   * Sort a range of elements by a user-supplied comparison function.
   * Uses the insertion sort algorithm.  This is a quadratic
   * algorithm, but it is useful for sorting small numbers of elements.
   * @param array       Array containing the range.
   * @param first       Beginning of the range.
   * @param last        One past the end of the range.
   * @param comp        Comparison function.  
   * @see Sorting#sort
   */
  public static void insertion_sort(Object[] array, int first, int last,
				    BinaryPredicate comp)
    {
      for (int current = first; ++current < last; /* */ ) { 
	Object tmp = array[current];
	int i = current;
	for (Object tmp1 = array[i - 1];
	     comp.apply(tmp, tmp1); 
	     tmp1 = array[--i - 1] ) {
	  array[i] = tmp1;
	  if (first == i - 1) {
	    --i;
	    break;
	  }
	}
	array[i] = tmp;
      }
    }


  private static int quickPartition(Object[] array, int first, int last,
				    BinaryPredicate comp)
    {
      Object f = array[first];
      Object l = array[last - 1];
      Object pivot = array[first + (last - first) / 2];

      if (comp.apply(pivot,f)) {
	if (comp.apply(f,l))
	  pivot = f;
	else if (comp.apply(pivot,l))
	  pivot = l;
      }
      else if (comp.apply(l,f))
	pivot = f;
      else if (comp.apply(l,pivot))
	pivot = l;

      --first;
      while (true) {
	while (comp.apply(array[++first], pivot))
	  { }

	while (comp.apply(pivot, array[--last]))
	  { }

	if (first >= last)
	  return first;

	Object tmp = array[first];
	array[first] = array[last];
	array[last] = tmp;
      }
    }

  private static final int partitionCutoff = 13;
  private static final int qsort_stacksize = 56;


  private static void qsortLoop(Object[] array, int first, int last,
				  BinaryPredicate comp)
    {
      int[] stack = new int[qsort_stacksize];
      int position = 0;
      while (true) {
	int cut = quickPartition(array, first, last, comp);

	if (last - cut < partitionCutoff) {
	  if (cut - first < partitionCutoff) {
	    if (position == 0) 
	      return;
	    last = stack[--position];
	    first = stack[--position];
	  }
	  else
	    last = cut;
	}
	else if (cut - first <  partitionCutoff) { 
	  first = cut; 
	}
	else if (last - cut > cut - first) {
	  stack[position++] = cut;
	  stack[position++] = last;
	  last = cut;
	}
	else {
	  stack[position++] = first;
	  stack[position++] = cut;
	  first = cut;
	} 
      }
    }

  private static final int stableSortCutoff = 9;


  /**
   * Sort a range of elements by a user-supplied comparison function.
   * The sort is stable---that is, the relative order of equal elements
   * is unchanged.  Worst case performance is <code>N (log N)^2</code>.
   * @param array       Array containing the range.
   * @param first       Beginning of the range.
   * @param last        One past the end of the range.
   * @param comp        Comparison function.
   * @see Sorting#sort
   */
  public static void stable_sort(Object[] array, int first, int last,
				 BinaryPredicate comp)
    {
      if (last - first < stableSortCutoff) 
	insertion_sort(array, first, last, comp);
      else {
	int middle = first + (last - first) / 2;
	stable_sort(array, first, middle, comp);
	stable_sort(array, middle, last, comp);
	inplace_merge(array, first, middle, last, comp);
      }
    }


  /**
   * Partially sorts a range by a user-supplied comparison function:  
   * places the first <code>middle-first</code> elements in the range 
   * <code>[first, middle)</code>.  These elements are sorted, the rest
   * are not.  It is not guaranteed that the relative ordering of 
   * unsorted elements is preserved.
   * @param array       Array containing the range.
   * @param first       Beginning of the range.
   * @param middle      Element such that the range
   *                    <code>[first, middle)</code> will be sorted.
   * @param last        One past the end of the range.
   * @param comp        Comparison function.
   * @see Sorting#partial_sort_copy
   * @see Sorting#sort
   */   
  public static void partial_sort(Object[] array,
				  int first, int middle, int last,
				  BinaryPredicate comp)
    {
      make_heap(array, first, middle, comp);
      int current = middle;
      while (current < last) {
	if (comp.apply(array[current], array[first])) {
	  Object tmp = array[current];
	  array[current] = array[first];
	  array[first] = tmp;
	  adjust_heap(array, first, first, middle, comp);
	}
	++current;
      }
      sort_heap(array, first, middle, comp);
    }



  /**
   * Copies the first <code>N</code> sorted elements from one range
   * into another, where <code>N</code> is the length of the smaller of
   * the two ranges.  Sort order is by a user-supplied comparison function.
   * Existing elements in the output range will be overwritten.
   * @param source       Array containing the input range.
   * @param destination  Array containing the output range.
   * @param first        Beginning of the input range.
   * @param last         One past the end of the input range.
   * @param result_first Beginning of the output range.
   * @param result_last  One past the end of the output range.
   * @param comp         Comparison function.
   * @return             <code>result_first + N</code>, where
   *                     <code>N = min(last-first, result_last-result_first)</code>.
   * @see Sorting#partial_sort
   */
  public static int partial_sort_copy(Object[] source, Object[] destination,
				      int first, int last,
				      int result_first, int result_last,
				      BinaryPredicate comp)
    {
      if (result_first == result_last)
	return result_last;

      int len = Math.min(last-first, result_last-result_first);
      Modification.copy(source, destination, first, first + len, result_first);
      result_last = result_first + len;

      make_heap(destination, result_first, result_last, comp);
      for (first += len ; first < last; ++first)
	if (comp.apply(source[first], destination[result_first])) {
	  destination[result_first] = source[first];
	  adjust_heap(destination,
				 result_first, result_first, result_last,
				 comp);
	}
      sort_heap(destination, result_first, result_last, comp);

      return result_last;
    }



  /**
   * Partitions a range of elements into two subranges
   * <code>[first, nth)</code> and <code>[nth, last)</code>.  These
   * satisfy the properties that no element in the first range is greater
   * than any element in the second, and that the element in the
   * position <code>nth</code> is the same as the one that would be
   * in that position if the entire range <code>[first, last)</code>
   * had been sorted.  Sorting is by a user-supplied comparison function.
   * @param array       Array containing the range.
   * @param first       Beginning of the range.
   * @param nth         Location of the partition point.
   * @param last        One past the end of the range.
   * @param comp        Comparison function.
   */
  public static void nth_element(Object[] array,
				 int first, int nth, int last,
				 BinaryPredicate comp)
    {
      while (last - first > 3) {
	int cut = quickPartition(array, first, last, comp);
	if (cut <= nth)
	  first = cut;
	else
	  last = cut;
      }
      
      insertion_sort(array, first, last, comp);
    }



  /**
   * Performs a binary search on an already-sorted range: finds the first
   * position where an element can be inserted without violating the ordering.
   * Sorting is by a user-supplied comparison function.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param x        Element to be searched for.
   * @param comp     Comparison function.
   * @return         The largest index i such that, for every j in the
   *                 range <code>[first, i)</code>, 
   *                 <code>comp.apply(array[j], x)</code> is
   *                 <code>true</code>.
   * @see Sorting#upper_bound
   * @see Sorting#equal_range
   * @see Sorting#binary_search
   */
  public static int lower_bound(Object[] array, int first, int last,
				Object x,
				BinaryPredicate comp)
    {
      int len = last - first;
      while (len > 0) {
	int half = len / 2;
	int middle = first + half;
	if (comp.apply(array[middle], x)) {
	  first = middle + 1;
	  len -= half + 1;
	} else
	  len = half;
      }
      return first;
    } 



  /**
   * Performs a binary search on an already-sorted range: finds the last
   * position where an element can be inserted without violating the ordering.
   * Sorting is by a user-supplied comparison function.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param x        Element to be searched for.
   * @param comp     Comparison function.
   * @return         The largest index i such that, for every j in the
   *                 range <code>[first, i)</code>, 
   *                 <code>comp.apply(x, array[j])</code> is 
   *                 <code>false</code>.
   * @see Sorting#lower_bound
   * @see Sorting#equal_range
   * @see Sorting#binary_search
   */
  public static int upper_bound(Object[] array, int first, int last,
				Object x,
				BinaryPredicate comp)
    {
      int len = last - first;
      while (len > 0) {
	int half = len / 2;
	int middle = first + half;
	if (comp.apply(x, array[middle]))
	  len = half;
	else {
	  first = middle + 1;
	  len -= half + 1;
	}
      }
      return first;
    }


  /**
   * Performs a binary search on an already-sorted range:
   * Finds the largest subrange in the supplied range such that an
   * element can be inserted at any point in that subrange without violating
   * the existing ordering.  
   * Sorting is by a user-supplied comparison function.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param x        Element to be searched for.
   * @param comp     Comparison function
   * @return         An object <code>R</code>of class <code>R</code> such 
   *                 that, for any index <code>i</code> in the range
   *                 <code>[R.first, R.last)</code>, the conditions
   *                 <code>comp.apply(array[i], x)</code> and
   *                 <code>comp.apply(x, array[i])</code> are both false.
   *                 Note that it is possible for the return value to be
   *                 an empty range.
   * @see Sorting#lower_bound
   * @see Sorting#upper_bound
   * @see Sorting#binary_search
   */
  public static Range equal_range(Object[] array, int first, int last,
				  Object x,
				  BinaryPredicate comp)
    {
      int len = last - first;
      while (len > 0) {
	int half = len / 2;
	int middle = first + half;
	if (comp.apply(array[middle], x)) {
	  first = middle + 1;
	  len = len - half + 1;
	}
	else if (comp.apply(x, array[middle]))
	  len = half;
	else {
	  int left  = lower_bound(array, first, middle, x, comp);
	  int right = upper_bound(array, middle + 1, first + len, x, comp);
	  return new Range(array, left, right);
	}
      }

      return new Range(array, first, first); // An empty range.
    }
   


  /**
   * Performs a binary search on an already-sorted range:
   * determines whether the range contains an element equivalent to a
   * certain value.
   * Sorting is by a user-supplied comparison function.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param x        Element to be searched for.
   * @param comp     Comparison function.
   * @return         <code>true</code> if and only if the range contains 
   *                 an element <code>E</code> such that 
   *                 <code>value &lt; E</code> and
   *                 <code>E &lt; value</code> are both 
   *                 <code>false</code>.
   * @see Sorting#lower_bound
   * @see Sorting#upper_bound
   * @see Sorting#equal_range
   */
  public static boolean binary_search(Object[] array, int first, int last, 
				      Object x,
				      BinaryPredicate comp)
    {
      int i = lower_bound(array, first, last, x, comp);
      return i < last && !comp.apply(x, array[i]);
    }



  /**
   * Merges two sorted ranges into a third range, which will be sorted.
   * Elements in the first input range will precede equal elements in the 
   * second.
   * There must be
   * enough space in the destination array, and existing elements 
   * will be overwritten.
   * Sorting is by a user-supplied comparison function.
   * Note: the destination range is not permitted to overlap either of 
   * the two input ranges.
   * @param source1     Array containing the first input range.
   * @param source2     Array containing the second input range.
   * @param dest        Array containing the output range.
   * @param first1      Beginning of the first input range.
   * @param last1       One past the end of the first input range.
   * @param first2      Beginning of the second input range.
   * @param last2       One past the end of the second input range.
   * @param to          Beginning of the output range.
   * @param comp        Comparison function
   * @return            One past the end of the output range, that is,
   *                    <code>to + (last1-first1) + (last2-first2)</code>.
   * @see Sorting#inplace_merge
   */
  public static int merge(Object[] source1, Object[] source2, Object[] dest,
			  int first1, int last1, int first2, int last2,
			  int to,
			  BinaryPredicate comp)
    {
      while (first1 < last1 && first2 < last2)
	if (comp.apply(source2[first2], source1[first1]))
	  dest[to++] = source2[first2++];
	else
	  dest[to++] = source1[first1++];
      
      Modification.copy(source1, dest, first1, last1, to);
      Modification.copy(source2, dest, first2, last2, to);
      return to + (last1 - first1) + (last2 - first2);
    }

    

  /**
   * Transforms two consecutive sorted ranges into a single sorted 
   * range.  The initial ranges are <code>[first, middle)</code>
   * and <code>[middle, last)</code>, and the resulting range is
   * <code>[first, last)</code>.  
   * Elements in the first input range will precede equal elements in the 
   * second.
   * Sorting is by a user-supplied comparison function.
   * @param array    Array containing the ranges.
   * @param first    Beginning of the first range.
   * @param middle   One past the end of the first range, and beginning
   *                 of the second.
   * @param last     One past the end of the second range.
   * @param comp     Comparison function.
   * @see Sorting#merge
   */
  public static void inplace_merge(Object[] array, 
				   int first, int middle, int last,
				   BinaryPredicate comp)
    {

      if (first >= middle || middle >= last)
	return;

      if (last - first == 2) {
	if (comp.apply(array[middle], array[first])) {
	  Object tmp = array[first];
	  array[first] = array[middle];
	  array[middle] = tmp;
	}
	return;
      }

      int firstCut;
      int secondCut;

      if (middle - first > last - middle) {
	firstCut = first + (middle - first) / 2;
	secondCut = lower_bound(array, middle, last, array[firstCut], comp);
      }
      else {
	secondCut = middle + (last - middle) / 2;
	firstCut = upper_bound(array, first, middle, array[secondCut], comp);
      }

      Modification.rotate(array, firstCut, middle, secondCut);
      middle = firstCut + (secondCut - middle);

      inplace_merge(array, first, firstCut, middle, comp);
      inplace_merge(array, middle, secondCut, last, comp);
    }



  /**
   * Tests whether the first range is a superset of the second; both ranges
   * must be sorted.
   * Sorting is by a user-supplied comparison function.
   * @param array1   Array containing the first range.
   * @param array2   Array containing the second range.
   * @param first1   Beginning of the first range.
   * @param last1    One past the end of the first range.
   * @param first2   Beginning of the second range.
   * @param last2    One past the end of the second range.
   * @param comp     Comparison function.
   * @return         <code>true</code> if and only if, for every element in
   *                 the range <code>[first2,last2)</code>, the range
   *                 <code>[first1,last1)</code> contains an equivalent 
   *                 element.
   * @see Sorting#set_union
   * @see Sorting#set_intersection
   * @see Sorting#set_difference
   * @see Sorting#set_symmetric_difference
   */
  public static boolean includes(Object[] array1, Object[] array2,
				 int first1, int last1, int first2, int last2,
				 BinaryPredicate comp)
    {
      while (first1 < last1 && first2 < last2) {
	if (comp.apply(array2[first2], array1[first1]))
	  return false;
	else if (comp.apply(array1[first1], array2[first2]))
	  ++first1;
	else {
	  ++first1;
	  ++first2;
	}
      }

      return first2 == last2;	    
    }


  /**
   * Constructs a union of two already-sorted ranges.  That is, 
   * the output range will be a sorted range containing every element from
   * either of the two input ranges.  If an element in the second range
   * is equivalent to one in the first, the one in the first range is
   * copied.  
   * There must be
   * enough space in the destination array, and existing elements 
   * will be overwritten.
   * Sorting is by a user-provided comparison function.
   * Note: the destination range is not permitted to overlap either of 
   * the two input ranges.
   * @param source1     Array containing the first input range.
   * @param source2     Array containing the second input range.
   * @param destination Array containing the output range.
   * @param first1      Beginning of the first input range.
   * @param last1       One past the end of the first input range.
   * @param first2      Beginning of the second input range.
   * @param last2       One past the end of the second input range.
   * @param to          Beginning of the output range.
   * @param comp        Comparison function
   * @return            One past the end of the output range.
   * @see Sorting#includes
   * @see Sorting#set_intersection
   * @see Sorting#set_difference
   * @see Sorting#set_symmetric_difference
   */ 
  public static int set_union(Object[] source1, Object[] source2,
			      Object[] destination,
			      int first1, int last1, int first2, int last2,
			      int to,
			      BinaryPredicate comp)
    {
      while (first1 < last1 && first2 < last2) {
	if (comp.apply(source1[first1], source2[first2]))
	  destination[to++] = source1[first1++];
	else if (comp.apply(source2[first2], source1[first1]))
	  destination[to++] = source2[first2++];
	else {
	  destination[to++] = source1[first1++];
	  first2++;
	}
      }

      Modification.copy(source1, destination, first1, last1, to);
      Modification.copy(source2, destination, first2, last2, to);
      return to + (last1 - first1) + (last2 - first2);
    }


  /**
   * Constructs an intersection of two already-sorted ranges.  That is, 
   * the output range will be a sorted range containing every element from
   * the first range such that an equivelent element exists in the
   * second range.  
   * There must be
   * enough space in the destination array, and existing elements 
   * will be overwritten.
   * Sorting is by a user-provided comparison function.
   * Note: the destination range is not permitted to overlap either of 
   * the two input ranges.
   * @param source1     Array containing the first input range.
   * @param source2     Array containing the second input range.
   * @param destination Array containing the output range.
   * @param first1      Beginning of the first input range.
   * @param last1       One past the end of the first input range.
   * @param first2      Beginning of the second input range.
   * @param last2       One past the end of the second input range.
   * @param to          Beginning of the output range.
   * @param comp        Comparison function
   * @return            One past the end of the output range.
   * @see Sorting#includes
   * @see Sorting#set_union
   * @see Sorting#set_difference
   * @see Sorting#set_symmetric_difference
   */ 
  public static int set_intersection(Object[] source1, Object[] source2,
				     Object[] destination,
				     int first1, int last1,
				     int first2, int last2,
				     int to,
				     BinaryPredicate comp)
    {
      while (first1 < last1 && first2 < last2) {
	if (comp.apply(source1[first1], source2[first2]))
	  ++first1;
	else if (comp.apply(source2[first2], source1[first1]))
	  ++first2;
	else {
	  destination[to++] = source1[first1++];
	  first2++;
	}
      }

      return to;
    }


  /**
   * Constructs the set difference of two already-sorted ranges.  That is, 
   * the output range will be a sorted range containing every element from
   * the first range such that an equivelent element does not exist in the
   * second range.  
   * There must be
   * enough space in the destination array, and existing elements 
   * will be overwritten.
   * Sorting is by a user-supplied comparison function.
   * Note: the destination range is not permitted to overlap either of 
   * the two input ranges.
   * @param source1     Array containing the first input range.
   * @param source2     Array containing the second input range.
   * @param destination Array containing the output range.
   * @param first1      Beginning of the first input range.
   * @param last1       One past the end of the first input range.
   * @param first2      Beginning of the second input range.
   * @param last2       One past the end of the second input range.
   * @param to          Beginning of the output range.
   * @param comp        Comparison function. 
   * @return            One past the end of the output range.
   * @see Sorting#includes
   * @see Sorting#set_union
   * @see Sorting#set_intersection
   * @see Sorting#set_symmetric_difference
   */ 
  public static int set_difference(Object[] source1, Object[] source2,
				   Object[] destination,
				   int first1, int last1,
				   int first2, int last2,
				   int to,
				   BinaryPredicate comp)
    {
      while (first1 < last1 && first2 < last2) {
	if (comp.apply(source1[first1], source2[first2]))
	  destination[to++] = source1[first1++];
	else if (comp.apply(source2[first2], source1[first1]))
	  ++first2;
	else {
	  ++first1;
	  ++first2;
	}
      }

      Modification.copy(source1, destination, first1, last1, to);
      return to + (last1 - first1);
    }
  

  /**
   * Constructs the set symmetric difference of two already-sorted ranges.  
   * That is,  the output range will be a sorted range containing every
   * element from the first range such that an equivelent element does not 
   * exist in the second range, and every element in the second such that an
   * equivalent element does not exist in the first.
   * There must be
   * enough space in the destination array, and existing elements 
   * will be overwritten.
   * Sorting is by a user-supplied comparison function.
   * Note: the destination range is not permitted to overlap either of 
   * the two input ranges.
   * @param source1     Array containing the first input range.
   * @param source2     Array containing the second input range.
   * @param destination Array containing the output range.
   * @param first1      Beginning of the first input range.
   * @param last1       One past the end of the first input range.
   * @param first2      Beginning of the second input range.
   * @param last2       One past the end of the second input range.
   * @param to          Beginning of the output range.
   * @param comp        Comparison function.
   * @return            One past the end of the output range.
   * @see Sorting#includes
   * @see Sorting#set_union
   * @see Sorting#set_intersection
   * @see Sorting#set_difference
   */ 
  public static int set_symmetric_difference(Object[] source1,
					     Object[] source2,
					     Object[] destination,
					     int first1, int last1,
					     int first2, int last2,
					     int to,
					     BinaryPredicate comp)
    {
      while (first1 < last1 && first2 < last2) {
	if (comp.apply(source1[first1], source2[first2]))
	  destination[to++] = source1[first1++];
	else if (comp.apply(source2[first2], source1[first1]))
	  destination[to++] = source2[first2++];
	else {
	  ++first1;
	  ++first2;
	}
      }

      Modification.copy(source1, destination, first1, last1, to);
      Modification.copy(source2, destination, first2, last2, to);
      return to + (last1 - first1) + (last2 - first2);
    }



  /**
   * Adds an element to a heap.  The range <code>[first, last-1)</code>
   * must be a valid heap, and the element to be added must be in
   * <code>array[last-1]</code>.  
   * The heap is ordered by a user-supplied comparison function.
   * @param array    Array containing the heap.
   * @param first    Beginning of the heap.
   * @param last     Index such that <code>[first, last-1)</code> is a
   *                 valid heap and such that <code>array[last-1]</code> 
   *                 contains the element to be added to the heap. 
   * @param comp     Comparison function.
   * @see Sorting#make_heap
   */
  public static void push_heap(Object[] array, int first, int last,
			       BinaryPredicate comp)
    {
      if (last - first < 2) return;
      Object tmp = array[--last];
      int parent = first + ((last - first) - 1) / 2;
      while (last > first && comp.apply(array[parent], tmp)) {
	array[last] = array[parent];
	last = parent;
	parent = first + ((last - first) - 1) / 2;
      }
      array[last] = tmp;
    }


  /** 
   * Fixes a heap that is slightly invalid.  If the range
   * <code>[first, last)</code> is a valid heap except for the element
   * <code>array[position]</code>, rearrange elements so that it is
   * a valid heap again.
   * The heap is ordered by a user-supplied comparison function.
   * @param array    Array containing the heap.
   * @param first    Beginning of the heap.
   * @param position Index of the incorrectly positioned element.
   * @param last     One past the end of the heap.
   * @param comp     Comparison function.
   * @see Sorting#make_heap
   */
  private static void adjust_heap(Object[] array,
				    int first, int position, int last,
				    BinaryPredicate comp)
    {
      Object tmp = array[position];
      int len = last - first;
      int holeIndex = position - first;
      int secondChild = 2 * holeIndex + 2;
      while (secondChild < len) {
	if (comp.apply(array[first + secondChild],
		       array[first + (secondChild - 1)]))
	  --secondChild;
	array[first + holeIndex] = array[first + secondChild];
	holeIndex = secondChild++;
	secondChild *= 2;
      }
      if (secondChild-- == len) {
	array[first + holeIndex] = array[first + secondChild];
	holeIndex = secondChild;
      }

      int parent = (holeIndex - 1) / 2;
      int topIndex = position - first;
      
      while (holeIndex != topIndex && comp.apply(array[first + parent], tmp)) {
	array[first + holeIndex] = array[first + parent];
	holeIndex =  parent;
	parent = (holeIndex - 1) / 2;
      }
      array[first + holeIndex] = tmp;
    }


  /**  
   * Removes the largest element from a heap.  If the range 
   * <code>[first, last)</code> is a valid heap, then remove
   * <code>array[first]</code> (the largest element) from the heap,
   * rearrange elements such that <code>[first, last-1)</code> is
   * a valid heap, and place the removed element in <code>array[last]</code>.
   * The heap is ordered by a user-defined comparison function.
   * @param array    Array containing the heap.
   * @param first    Beginning of the heap.
   * @param last     One past the end of the heap.
   * @param comp     Comparison function.
   * @see Sorting#make_heap
   */
  public static void pop_heap(Object[] array, int first, int last,
			      BinaryPredicate comp)
    {
      if (last - first < 2) return;
      Object tmp = array[--last];
      array[last] = array[first];
      array[first] = tmp;
      adjust_heap(array, first, first, last, comp);
    }


  /**
   * Turns the range <code>[first, last)</code> into a heap.  A heap has
   * the properties that <code>array[first]</code> is the largest element,
   * and that it is possible to add a new element, or to remove 
   * <code>array[first]</code>, efficiently.
   * The heap is ordered by a user-defined comparison function.
   * @param array    Array containing the range that is to be made a heap.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param comp     Comparison function.
   * @see Sorting#push_heap
   * @see Sorting#pop_heap
   * @see Sorting#sort_heap
   */
  public static void make_heap(Object[] array, int first, int last,
			       BinaryPredicate comp)
    {
      if (last - first < 2) return;
      int parent = (last - first - 2) / 2;

      do 
	adjust_heap(array, first, first + parent, last, comp);
      while (parent-- != 0);
    }



  /**
   * Turns a heap into a sorted range; this operation is
   * <code>O(N log N)</code>.  Note that <code>make_heap</code>
   * followed by <code>sort_heap</code> is the heap sort algorithm.
   * Ordering is by a user-supplied comparision function.
   * @param array    Array containing the heap that is to be made a sorted
   *                 range.
   * @param first    Beginning of the heap.
   * @param last     One past the end of the range.
   * @param comp     Comparison function.
   * @see Sorting#make_heap
   */
  public static void sort_heap(Object[] array, int first, int last,
			       BinaryPredicate comp)
    {
      while (last - first > 1) {
	Object tmp = array[--last];
	array[last] = array[first];
	array[first] = tmp;
	adjust_heap(array, first, first, last, comp);
      }
    }




  /**
   * Finds the largest element in a range.
   * Ordering is by a user-supplied comparison function.
   * @param array    Array containing the range. 
   * @param first    Beginning of the range. 
   * @param last     End of the range. 
   * @param comp     Comparison function.
   * @return         The smallest index <code>i</code> such that every element
   *                 in the range is less than or equivalent to 
   *                 <code>array[i]</code>.  Returns <code>last</code>
   *                 if the range is empty.
   * @see Sorting#min_element                    
   */
  public static int max_element(Object[] array, int first, int last,
				BinaryPredicate comp)
    {
      if (first >= last) return last;

      int result = first;

      while (++first < last) 
	if (comp.apply(array[result], array[first]))
	  result = first;

      return result;
    }




  /**
   * Finds the smallest element in a range.
   * Ordering is by a user-supplied comparison function.
   * @param array    Array containing the range 
   * @param first    Beginning of the range 
   * @param last     End of the range 
   * @param comp     Comparison function.
   * @return         The smallest index <code>i</code> such that every element
   *                 in the range is greater than or equivalent to 
   *                 <code>array[i]</code>.  Returns <code>last</code>
   *                 if the range is empty.
   * @see Sorting#max_element                                        
   */
  public static int min_element(Object[] array, int first, int last,
				BinaryPredicate comp)
    {
      if (first >= last) return last;

      int result = first;

      while (++first < last) 
	if (comp.apply(array[first], array[result]))
	  result = first;

      return result;
    }




  /**
   * Performs a lexicographical (element-by-element) comparison of two ranges.
   * Ordering of individual elements is by a user-supplied comparison function.
   * @param array1    Array containing the first range.
   * @param array2    Array containing the second range.
   * @param first1    Beginning of the first range.
   * @param last1     One past the end of the first range.
   * @param first2    Beginning of the second range.
   * @param last2     One past the end of the second range.
   * @param comp      Comparison function.
   * @return          <code>true</code> if the sequence of elements in the
   *                  range <code>[first1, last1)</code> is lexicographically
   *                  less than that in <code>[first1, last1)</code>,
   *                  otherwise <code>false</code>.
   */
  public static boolean lexicographical_compare(Object[] array1,
						Object[] array2,
						int first1, int last1,
						int first2, int last2,
						BinaryPredicate comp)
    {
      while (first1 < last1 && first2 < last2) 
	if (comp.apply(array1[first1], array2[first2]))
	  return true;
	else if (comp.apply(array2[first2++], array1[first1++]))
	  return false;

      return first1 == last1 && first2 != last2;
    }


 /** 
   * Transforms a range of elements into the next permutation of those
   * elements, where the <em>next</em> permutation is defined by 
   * a lexicographical ordering of the set of all permutations.
   * If no such permutation exists, transforms the range into the 
   * smallest permutation.
   * Ordering of individual elements is by a user-supplied comparison
   * function.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param comp     Comparison function
   * @return         <code>true</code> if a next permutation exists,
   *                 <code>false</code> if the range is already the largest
   *                 permutation.
   * @see            Sorting#lexicographical_compare
   * @see            Sorting#prev_permutation
   */
  public static boolean next_permutation(Object[] array, int first, int last,
					 BinaryPredicate comp)
    {
      if (last - first < 2)
	return false;

      int i  = last - 1;
      while(true) {
	int ii = i--;
	if (comp.apply(array[i], array[ii])) {
	  int j = last;
	  while (!comp.apply(array[i], array[--j]))
	    { }
	  Object tmp = array[i];
	  array[i] = array[j];
	  array[j] = tmp;
	  Modification.reverse(array, ii, last);
	  return true;
	}
	if (i == first) {
	  Modification.reverse(array, first, last);
	  return false;
	}
      }
    }


 /** 
   * Transforms a range of elements into the previous permutation of those
   * elements, where the <em>previous</em> permutation is defined by 
   * a lexicographical ordering of the set of all permutations.
   * If no such permutation exists, transforms the range into the 
   * largest permutation.
   * Ordering of individual elements is by a user-supplied comparison
   * function.
   * @param array    Array containing the range.
   * @param first    Beginning of the range.
   * @param last     One past the end of the range.
   * @param comp     Comparison function
   * @return         <code>true</code> if a previous permutation exists,
   *                 <code>false</code> if the range is already the smallest
   *                 permutation.
   * @see            Sorting#lexicographical_compare
   * @see            Sorting#next_permutation
   */
  public static boolean prev_permutation(Object[] array, int first, int last,
					 BinaryPredicate comp)
    {
      if (last - first < 2)
	return false;

      int i  = last - 1;
      while(true) {
	int ii = i--;
	if (comp.apply(array[ii], array[i])) {
	  int j = last;
	  while (!comp.apply(array[--j], array[i]))
	    { }
	  Object tmp = array[i];
	  array[i] = array[j];
	  array[j] = tmp;
	  Modification.reverse(array, ii, last);
	  return true;
	}
	if (i == first) {
	  Modification.reverse(array, first, last);
	  return false;
	}
      }
    }


  /* We don't need a constructor. */
  private Sorting() {}
}
