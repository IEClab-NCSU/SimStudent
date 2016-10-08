/**
 * Copyright 2007-2014 Carnegie Mellon University.
 */
package edu.cmu.pact.Utilities;

import java.util.Comparator;

/**
 * For comparing version strings. The String comparator will give undesired results for, e.g.,
 * "2.9.0" vs. "2.11.0", since the "11" sorts before the "9" in alphanumeric character ordering.
 */
public class VersionComparator implements Comparator<String> {

	/**
	 * Compare each "."-delimited segment of 2 Strings as if the segments contain integers.
	 * @param o1
	 * @param o2
	 * @return -1 if o1 is smaller, 1 if o2 is smaller, 0 if they're the same
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(String o1, String o2) {
		if(o1 == null)
			return(o2 == null ? 0 : -1);  // nulls sort first   
		else if(o2 == null)
			return 1;

		int result = 0;
		String[] a1 = o1.split("\\."), a2 = o2.split("\\.");
		int i;
		for(i = 0; i < Math.min(a1.length, a2.length); ++i) {
			try {
				Integer i1 = Integer.valueOf(a1[i]), i2 = Integer.valueOf(a2[i]);
				if(0 != (result = i1.compareTo(i2)))
					return result;
			} catch(NumberFormatException nfe) {
				trace.err("Version parse error at element "+i+" in \""+o1+"\" or \""+o2+"\": "+nfe);
				if(0 != (result = a1[i].compareTo(a2[i])))
					return result;
			}
		}
		if(i < a1.length) return 1;    // o1 is longer
		if(i < a2.length) return -1;   // o1 is shorter
		return o1.compareTo(o2);       // last-gasp try
	}

	/** Prevent other instantiations: need only one of these. */
	private VersionComparator() {}

	/** Single {@link Skill.VersionComparator} instance for {@link #versionToSkillBarDelimiter(String)}. */
	public static VersionComparator vc = new VersionComparator();

	/**
	 * Test harness for {@link #versionToSkillBarDelimiter(String)}. Exit code is number of mismatches.
	 * @param args versions
	 */
	public static void main(String[] args) {
		if(args.length < 1 || args[0].toLowerCase().startsWith("-h"))
			usageExit(null);
		if(args.length < 2)
			usageExit("At least 2 arguments are required.");
		String v0 = null, vi;
		int result = 0;
		for(int i = 0; i < args.length; ++i) {
			if((vi = args[i]).equalsIgnoreCase("null"))
				vi = null;
			if(i == 0)
				v0 = vi;
			else {
				int c = VersionComparator.vc.compare(v0, vi);
				if(c != 0)
					result++;
				System.out.printf("%12s %c %s\n", v0, (c == 0 ? '=' : (c < 0 ? '<' : '>')), vi);
			}
		}
		System.exit(result);
	}

	/**
	 * Print an usage message. Never returns: calls {@link System#exit(int)} with exit code 2.
	 * @param errMsg if not null, an error diagnostic to precede the usage message 
	 */
	private static void usageExit(String errMsg) {
		System.err.printf("%sUsage: java -cp ... %s v0 v1 ...\n"+
				"where--\n"+
				"  v0 is a string like 2.11.0 against which all others will be compared;\n"+
				"  v1 ... are the other comparison strings.\n"+
				"Enter null to compare a null value. Exit code is number of mismatches.\n",
				(errMsg == null ? "" : errMsg+" "),
				VersionComparator.class.getName());
		System.exit(2);
	}
}
