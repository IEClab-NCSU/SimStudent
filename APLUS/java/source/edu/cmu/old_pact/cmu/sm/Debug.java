package edu.cmu.old_pact.cmu.sm;

import edu.cmu.pact.Utilities.trace;

public class Debug {
	static boolean printDebugging=false;
	
	static void println (String output) {
		if (printDebugging) {
			trace.out(output);
		}
	}
	
	public static void startDebugging() {
		printDebugging = true;
	}
	
	public static void stopDebugging() {
		printDebugging = false;
	}

	public static boolean getDebugging(){
		return printDebugging;
	}
};
