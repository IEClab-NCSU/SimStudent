package edu.cmu.old_pact.cmu.solver.ruleset;


/*DblStringTest treats both of the strings passed to the constructor
  as a properties, while StringTest only treats the first of its two
  strings as a property*/

public class DblStringTest extends ComparisonTest {
	public DblStringTest(String[] property,String[] comp){
		super(property,comp);
	}

	public DblStringTest(String property,String comp) {
		super(new String[] {property},new String[] {comp});
	}

	public DblStringTest(String[] one,String two){
		super(one,new String[] {two});
	}

	public DblStringTest(String one,String[] two){
		super(new String[] {one},two);
	}

	boolean compare(String s1,String s2){
		return s1.equalsIgnoreCase(s2);
	}

	public String toString(){
		return "[DBLSTRING: " + ofString(first) + ", " + ofString(second) + "]";
	}
}
