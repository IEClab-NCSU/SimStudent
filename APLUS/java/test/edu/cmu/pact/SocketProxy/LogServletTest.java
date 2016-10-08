/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.SocketProxy;

import edu.cmu.pact.BehaviorRecorder.Dialogs.DialogUtilitiesTest;
import edu.cmu.pact.Utilities.trace;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sewall
 *
 */
public class LogServletTest extends TestCase {

    public static Test suite() {
        return new TestSuite(LogServletTest.class);
    }
	
	protected void setUp() throws Exception {
		trace.addDebugCode("logservice");
		super.setUp();
	}

	public void testExtractLogRecord() {
		class ExtractLogRecordTest {
			String msg;
			String result;
			boolean isHTTP;
			ExtractLogRecordTest(String msg, String result, boolean isHTTP) {
				this.msg = msg;
				this.result = result;
				this.isHTTP = isHTTP;
			}
		};
		ExtractLogRecordTest[] tests = {  
				new ExtractLogRecordTest("<?xml version=\"1.0\"?><log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						"<?xml version=\"1.0\"?><log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						false),
				new ExtractLogRecordTest("POST /log/server HTTP/1.0\r\n"+
						"Content-length: 239\r\n"+
						"\r\n"+
						"<?xml version=\"1.0\"?>\r\n<log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						"<?xml version=\"1.0\"?>\r\n<log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						true),
				new ExtractLogRecordTest("<?xml version=\"1.0\"?>/>\r\n<log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						null,  // extra "/>" after prologue
						false),
				new ExtractLogRecordTest("<?xml version=\"1.0\"?>\r\n<log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						"<?xml version=\"1.0\"?>\r\n<log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						false),
				new ExtractLogRecordTest("<log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						"<log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						false),
				new ExtractLogRecordTest("POST /log/server HTTP/1.0\r\n"+
						"Content-length: 239\r\n"+
						"\r\n"+
						"<log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						"<log_session_start timezone=\"America/New_York\" date_time=\"2013/03/14 15:19:50.220\" auth_token=\"\" session_id=\"S2436bc899ab277d3a01354e39af4531b\" user_guid=\"atest0\" class_id=\"\" treatment_id=\"\" assignment_id=\"\" info_type=\"tutor_message.dtd\"/>",
						true)
		};
		boolean[] isHTTP = new boolean[1];
		
		for(int i = 0; i < tests.length; ++i) {
			ExtractLogRecordTest test = tests[i];
			String result = LogServlet.extractLogRecord(test.msg, isHTTP);
			if(test.result == null)
				assertNull("["+i+"] Result not null but should have been", result);
			else {
				assertEquals("["+i+"] Results fail to match", test.result, result);
				assertEquals("["+i+"] wrong isHTTP flag", test.isHTTP, isHTTP[0]);
			}
		}
	}
}
