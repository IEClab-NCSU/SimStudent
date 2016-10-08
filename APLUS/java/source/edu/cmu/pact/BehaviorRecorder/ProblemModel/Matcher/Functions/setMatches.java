package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Returns true if the elements in the first argument are the same as those in the second.
 */
public class setMatches {

    /**
     * <p>Returns true if the semicolon-delimited lists of values given as arguments contain the same
     * values, regardless of the order of elements or duplicates.</p>
     * @param value1 elements in first set, delimited by semicolons (";")
     * @param value2 elements in second set
     * @return true if elements in value1 are the same as those in value2
     */
    public boolean setMatches(String value1, String value2) {
        return setMatches(value1, value2, ";");
    }

    /**
     * <p>Returns true if the lists of values given as arguments contain the same
     * values, regardless of the order of elements or duplicates.</p>
     * @param value1 elements in first set
     * @param value2 elements in second set
     * @param delimiter between set elements (regular expression)
     * @return true if elements in value1 are the same as those in value2
     */
    public boolean setMatches(String value1, String value2, String delim) {
        String[] values1 = value1.split(delim);
        String[] values2 = value2.split(delim);

        List<String> list1 = Arrays.asList(values1);
        List<String> list2 = Arrays.asList(values2);

        Set<String> set1 = new HashSet<String>(list1);
        Set<String> set2 = new HashSet<String>(list2);

        return set1.equals(set2);
    }

   
    /**
     * Test harness. Quote the arguments to avoid trouble with the semicolon delimiter.
     * @param args arg1 & arg 2 are left & right operands
     */
    public static void main(String[] args) {
        setMatches sm = new setMatches();
        System.out.printf("{%s} == {%s} ? %b\n", args[0], args[1], sm.setMatches(args[0], args[1]));
    }
}

