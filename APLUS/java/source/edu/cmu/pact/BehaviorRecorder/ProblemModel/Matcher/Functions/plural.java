package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.HashSet;

/**
 * Under control of a given count, return the singular or plural form of a word.
 */
public class plural {
	
	private static char[] Consonants = {
		'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z'
	};
	
	private static final HashSet<Character> consonantSet = new HashSet<Character>();
	static {
		for (char c : Consonants)
			consonantSet.add(new Character(c));
	}
	
	/**
	 * Tell whether a letter at a given position in a word is a consonant.
	 * @param word
	 * @param index position
	 * @return true if 
	 */
	private static boolean isConsonant(String word, int index) {
		if (word == null || word.length() <= index)
			return false;
		char c = Character.toLowerCase(word.charAt(index));
		return consonantSet.contains(new Character(c));
	}
	
	/**
	 * <p>Under control of a given count, return the singular or plural form of a word.
	 * If the given count is greater than one, make the given word plural using default rules.</p>
	 * @param count number to test 
	 * @param word singular form of word to modify
	 * @return word if count < 2; else plural form of word
	 */
	public String plural(int count, String word) {
		if (word == null || word.length() < 1)
			return word;
		if (count < 2)
			return word;
		String wordL = word.toLowerCase();
		if (wordL.endsWith("s"))
			return plural(count, word, "", "es");
		if (wordL.endsWith("y") && isConsonant(wordL, wordL.length()-2))
			return plural(count, word, "y", "ies");
		return word+"s";
	}

	/**
	 * <p>Under control of a given count, return the singular or plural form of a word.
	 * If the given count is greater than one, make the given word plural by removing
	 * a given suffix and appending a different one.</p>
	 * @param count number to test 
	 * @param word singular form of word to modify
	 * @param suffixToStrip
	 * @param suffixToAdd new suffix to add
	 * @return word if count < 2; else plural form of word
	 */
	public String plural(int count, String word, String suffixToStrip, String suffixToAdd) {
		if (word == null || word.length() < 1)
			return word;
		if (count < 2)
			return word;
		String wordL = word.toLowerCase();
		String toStripL = (suffixToStrip == null ? "" : suffixToStrip).toLowerCase();
		if (suffixToAdd == null)
			suffixToAdd = "";
		StringBuffer result = new StringBuffer(word);
		int len = result.length();
		if (Character.isUpperCase(result.charAt(len-1)))
			suffixToAdd = suffixToAdd.toUpperCase();
		int index = wordL.lastIndexOf(toStripL);
		if (index > 0) {
			result.replace(index, len, suffixToAdd);
		} else
			result.append(suffixToAdd);
		return result.toString();
	}

    /**
     * Print a usage message and exit.
     * @param intro optional prefix to message
     */
    public static void usageExit(String intro) {
    	if (intro != null && intro.length() > 0)
    		System.err.printf("%s. ", intro);
		System.err.println("Usage:\n"+
				"  java -cp classpath "+plural.class.getName()+" count word [toStrip toAdd]\n"+
				"where--\n"+
				"  count   should be an integer showing the number;\n"+
				"  word    is the singular form of the word to modify;\n"+
				"  toStrip is the suffix to delete;\n"+
				"  toAdd   is the plural suffix to append.\n");
		System.exit(2);
    }
    
	/**
	 * Test harness for command-line use. E.g.
	 *     plural 4 oddity
	 *     plural 2 phenomenon on a
	 * @param args see {@link #usageExit(String)}
	 */
	public static void main(String[] args) {
		int count = 0;
		if (args.length < 2)
			usageExit("Missing argument(s)");  // never returns
		try {
			count = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			usageExit("First argument '"+args[0]+"' must be an integer");
		}
		String word = args[1];
		plural p = new plural();
		if (args.length < 3)
			System.out.printf("%2d %-15s => %s\n", count, word, p.plural(count, word));
		else {
			String toStrip = args[2];
			String toAdd = (args.length < 4 ? null : args[3]);
			System.out.printf("%2d %-15s => %s\n", count, word, p.plural(count, word, toStrip, toAdd));
		}
	}

}
