//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/NewStringTokenizer.java

package edu.cmu.old_pact.cmu.toolagent;

import java.util.Enumeration;
import java.util.NoSuchElementException;


public class NewStringTokenizer implements Enumeration {
    private int currentPosition;
    private int maxPosition;
    private int delimLength;
    private String str;
    private String delimiters;
    private boolean retTokens;

   
   	public NewStringTokenizer(String str, String delim, boolean returnTokens) {
		currentPosition = 0;
		this.str = str;
		maxPosition = str.length();
		delimiters = delim;
		delimLength = delim.length();
		retTokens = returnTokens;
		currentPosition = str.indexOf(delimiters);
   	}

   	public NewStringTokenizer(String str, String delim) {
		this(str, delim, false);
   	}

    private void skipDelimiters() {
		while (!retTokens &&
			(currentPosition < maxPosition)){
	    	currentPosition = currentPosition + delimLength+1;
		}
    }

    public boolean hasMoreTokens() {
		skipDelimiters();
		return (currentPosition < maxPosition);
    }

    public String nextToken(String delim) {
		delimiters = delim;
		return nextToken();
    }

    public String nextToken() {
		skipDelimiters();

		if (currentPosition >= maxPosition) {
	    	throw new NoSuchElementException();
		}

		int start = currentPosition;
		currentPosition = str.indexOf(delimiters, start+delimLength);
		if(currentPosition == -1 )
			currentPosition = maxPosition;

		return str.substring(start, currentPosition);
    }

    public boolean hasMoreElements() {
		return hasMoreTokens();
    }

    public Object nextElement() {
		return nextToken();
    }

    public int countTokens() {
		int count = 0;
		int currpos = currentPosition;

		while (currpos < maxPosition) {
	   		while (!retTokens &&
	   			(currpos < maxPosition) &&
		   		(str.indexOf(delimiters, currpos) >= 0)) {
				currpos = currpos + delimLength;
	    	}

	    	if (currpos >= maxPosition) {
				break;
	    	}

	    	int start = currpos;
	    	while ((currpos < maxPosition) && 
		   	(str.indexOf(delimiters, currpos) < 0)) {
				currpos = currpos + delimLength;
	    	}
	    	if (retTokens && 
	    		(start == currpos) &&
				(str.indexOf(delimiters, currpos) >= 0)) {
				currpos = currpos + delimLength;
	    	}
	    	count++;

		}
		return count;
    }
}
