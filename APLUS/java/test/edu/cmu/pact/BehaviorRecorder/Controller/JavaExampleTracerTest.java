/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

/**
 * @author sewall
 *
 */
public class JavaExampleTracerTest extends PseudoTutorMessageHandlerTest {
	
	protected static CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);
	static {
		controller = launcher.getFocusedController();
	}
	
	public JavaExampleTracerTest(String name) {
		super(name);
	}
}
