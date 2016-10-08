/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.hcii.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Diagnostic tools not really application-specific.
 *
 */
public class DiagTools {

	private static final String topLevelPkg;
	static {
		String[] thisClassName = (new DiagTools()).getClass().getName().split("\\.");
		topLevelPkg = thisClassName[0]+(thisClassName.length > 1 ? "."+thisClassName[1] : "");
	}
	
	/**
	 * Private constructor to avoid instantiation by other classes.
	 */
	private DiagTools() {}

	/**
	 * Debugging method to list threads. For each thread in the calling thread's ThreadGroup
	 * and descendant groups, dumps thread name and that part of the stack whose callers are
	 * classes in {@link #topLevelPkg}.
	 * @return sorted (by name) list of active threads in {@link Thread#getThreadGroup()}.
	 */
	public static List<String> listThreads() {
		Thread[] activeThreads = new Thread[Thread.activeCount()*5];  // make array plenty big
		int count = Thread.enumerate(activeThreads);
		List<String> tNames = new ArrayList<String>(count);
		for (int i = 0; i < count; ++i) {
			Thread t = activeThreads[i];
			StackTraceElement[] stack = t.getStackTrace();
			StringBuilder sb = new StringBuilder();
			for (int j = 0, nSkipped = 0; j < stack.length-1; ++j) {
				if (stack[j].getClassName().startsWith(topLevelPkg)) {
					if (nSkipped > 0) {
						if (nSkipped > 1) sb.append("\n   ...");      // > 1 since we show one here
						sb.append("\n   ").append(stack[j-1].toString());  // top call outside appl
					}
					sb.append("\n   ").append(stack[j].toString());    // call from inside our appl
					nSkipped = 0;
				} else
					nSkipped++;                   // if lines skipped, show "..." before next frame
			}
			if (sb.length() < 1) {      // found none of our code, so dump whole stack to see state
				for (int j = 0; j < stack.length-1; ++j)
					sb.append("\n   ").append(stack[j].toString());
			}
			tNames.add("\n"+t.toString()+":"+sb.toString());
		}
		Collections.sort(tNames);
		return tNames;
	}

}
