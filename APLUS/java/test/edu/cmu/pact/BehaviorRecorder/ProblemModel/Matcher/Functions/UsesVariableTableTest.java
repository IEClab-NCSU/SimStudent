package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.net.URL;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageHandlerTest.SinkToolProxy;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * An author function to demonstrate author function access to the Example Tracer's
 * variable table.
 */
public class UsesVariableTableTest extends TestCase implements UsesVariableTable {
	
	/** The variable table. */
	private VariableTable variableTable = null;

	/**
	 * @param variableTable new value for {@link #variableTable}
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.UsesVariableTable#setVariableTable(edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable)
	 */
	public void setVariableTable(VariableTable variableTable) {
		this.variableTable = variableTable;
	}

	/**
	 * Simply retrieve the named variable and try to return it as an integer.
	 * @param var variable to get
	 * @return value converted to integer
	 */
	public Integer UsesVariableTableTest(String var) {
		trace.out("functions", "variableTable size"+
				(variableTable == null ? " null" : " "+variableTable.size()+
						"; val "+variableTable.get(var)) );
		Object val = variableTable.get(var);
		return CTATFunctions.toInteger(val);
	}
	
	public static Test suite() {
        TestSuite suite = new TestSuite(UsesVariableTableTest.class);
        return suite;
    }
	
	private CTAT_Launcher launcher = null;
	private BR_Controller controller = null;
	private SinkToolProxy sink = null;

	/**
	 * @throws java.lang.Exception
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		launcher = new CTAT_Launcher(new String[0]);
		controller = launcher.getFocusedController();
		sink = new SinkToolProxy(controller); 
        controller.setUniversalToolProxy(sink);
	}

	/**
	 * @throws java.lang.Exception
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		controller = null;
		sink = null; 
		launcher = null;
	}

	/**
	 * Test method for {@link edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.UsesVariableTable#setVariableTable(edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel)}.
	 */
	public final void testSetVariableTable() {
		controller.getProblemModel().setUseCommWidgetFlag(false);
		String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/ProblemModel/Matcher/Functions/604_551.brd";
		URL url = Utils.getURL(problemFileLocation, this);
		controller.openBRFromURL(url.toString());
		Vector<String> selection = new Vector<String>(); 
		Vector<String> action = new Vector<String>(); 
		Vector<String> input = new Vector<String>();
		selection.add("table1_C5R4");
		action.add("UpdateTable");
		input.add("5");
		controller.getPseudoTutorMessageHandler().processPseudoTutorInterfaceAction(selection, action, input);
		assertEquals("No. interps after ambiguous step",
				2, controller.getPseudoTutorMessageHandler().getExampleTracer().getResult().getNumberOfInterpretations());
		controller.getPseudoTutorMessageHandler().processPseudoTutorInterfaceAction(selection, action, input);

		// test the author function, which should match on both interpretations
		selection.set(0, "table1_C4R4");
		action.set(0, "UpdateTable");
		input.set(0, "1");
		controller.getPseudoTutorMessageHandler().processPseudoTutorInterfaceAction(selection, action, input);
		assertEquals("No. interps using variable table",
				2, controller.getPseudoTutorMessageHandler().getExampleTracer().getResult().getNumberOfInterpretations());
	}
}
