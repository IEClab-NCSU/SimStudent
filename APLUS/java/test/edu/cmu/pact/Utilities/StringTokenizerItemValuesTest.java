package edu.cmu.pact.Utilities;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StringTokenizerItemValuesTest extends TestCase {
	char delim = ',';
	boolean returnDelims = false;
	char predelim = '/';
	
	public void testNoTokens() {
		String str = ",,,,,";
		
		StringTokenizerItemValues tokenizer = 
									new StringTokenizerItemValues(str,
											delim,
											predelim,
											returnDelims);
		
		assertFalse(tokenizer.hasMoreTokens());
	}
	
	public void testFirstTokens() {
		int test_Number = 9;
		String testStrings [] = new String[test_Number];
		String expectedFirstTokenStrs [] = new String[test_Number];
		
		testStrings [0]= "aaaaaaa/,bb, cccccc"; 	// test ',' is a middle char
		expectedFirstTokenStrs[0] = "aaaaaaa,bb";
		
		testStrings [1]= "aaaaaaa/,";				// test ',' is the last char 
		expectedFirstTokenStrs[1] = "aaaaaaa,";
		
		testStrings [2]= "/,";						// test ',' is the only char
		expectedFirstTokenStrs[2] = ",";
		
		testStrings [3]= "a//,/a";					// test both ',' and '/' are in token
		expectedFirstTokenStrs[3] = "a/,/a";
		
		testStrings [4]= ",/";						// test '/' is the last only char
		expectedFirstTokenStrs[4] = "/";
		
		testStrings [5]= "/ ,";						// test "/ " is the only token
		expectedFirstTokenStrs[5] = "/ ";
		
		testStrings [6]= "/ /,";					// test "/ ," is the last token
		expectedFirstTokenStrs[6] = "/ ,";
		
		testStrings [7] = " ,";						// test space " " token
		expectedFirstTokenStrs[7] = " ";
		
		testStrings [8] = "a/,b/,"; 				// test consecutive '/,' combinations
		expectedFirstTokenStrs [8] = "a,b,";
		
		String nextTokenStr;
		
		for (int i=0; i<testStrings.length; i++) {
			StringTokenizerItemValues tokenizer = 
						new StringTokenizerItemValues(testStrings[i],
								delim,
								predelim,
								returnDelims);

			nextTokenStr = (String) tokenizer.nextToken();
			
			trace.out(5, this, "nextTokenStr = |" + nextTokenStr + "|");
			
			assertEquals(nextTokenStr, expectedFirstTokenStrs[i]);
		}
		
		return;
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(StringTokenizerItemValuesTest.class);
		
		return suite;
	}
	
	public static void main(String args[]) { 
        junit.textui.TestRunner.run(suite());
        
        System.exit(0);
	}
}