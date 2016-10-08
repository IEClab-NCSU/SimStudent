package edu.cmu.old_pact.cmu.sm;

public class Debug {
	static boolean printDebugging=false;
	
	static void println (String output) {
		if (printDebugging) {
			System.out.println(output);
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
