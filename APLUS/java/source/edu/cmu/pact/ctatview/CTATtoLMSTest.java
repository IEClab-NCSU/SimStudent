package edu.cmu.pact.ctatview;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Since this does not mutate any files or objects within CTAT, 
 * this test is just an example of how it should be used
 * and as a debuggint tool
 * @author ko
 */
public class CTATtoLMSTest extends TestCase {

	private String filepath = ""; //give a directory to where you want to test
	private String platform = "/launchFlashTutor.jsp";
	private String studentInterface = "Division5.swf";
	
	public static Test suite()
	{
		TestSuite suite = new TestSuite();
		suite.addTest(new CTATtoLMSTest("testCreateExcel"));
		//suite.addTest(new CTATtoLMSTest("testGetDepth"));
		return suite;
	}

	/*
	 * public void testGetDepth() {
		File testDir = new File("test");
		CTATtoLMS ctl = new CTATtoLMS(testDir, platform, studentInterface, "yyy", false);
		trace.out("skills", "testGetDepth(test)="+ctl.getDepth(testDir));
	}*/
	
	public void testCreateExcel()
	{
		String[] args = new String[5];
		args[0] = filepath;
		args[1] = platform;
		args[2] = studentInterface;
		args[3] = "yyyy";
		args[4] = "yyy"; //debugging option
		CTATtoLMS.run(args, false);
	}
	
	
	public CTATtoLMSTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
