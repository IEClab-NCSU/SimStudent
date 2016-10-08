package edu.cmu.pact.BehaviorRecorder.CTATStartStateEditor;

import edu.cmu.pact.BehaviorRecorder.Dialogs.MergeMassProductionDialogTest;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATSerializable;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CTATSerializableTest extends TestCase {

	public static Test suite() {
		return new TestSuite(CTATSerializableTest.class);
	}
	
	/**
	 * Test for ordering of {@link CTATSerializable.IncludeIn}.
	 */
	public void testIncludeInOrdering() {
		CTATSerializable.IncludeIn[] v = CTATSerializable.IncludeIn.values();
		for(int i = 0; i<v.length; ++i) {
			for(int j=i+1; j<v.length; ++j) {
				boolean result = (v[i].compareTo(v[j]) > 0);
				System.out.printf("%-6s %s %s\n", v[i].toString(),
						result ? "> " : "<=",
						v[j].toString());
				assertFalse(v[i]+".compareTo("+v[j].toString()+") should return -1 or 0 for <=", result);
			}
		}
	}
}
