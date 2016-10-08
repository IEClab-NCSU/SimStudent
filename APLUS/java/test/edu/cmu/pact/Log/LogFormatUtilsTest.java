package edu.cmu.pact.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.Utilities.Logger;

public class LogFormatUtilsTest extends TestCase {

	private static final String PARSE_CONVERT_UNESCAPE_LOG = "test/edu/cmu/pact/Log/parseConvertUnescape.txt";
	private static final String PARSE_CONVERT_UNESCAPE_ERROR_LOG = "test/edu/cmu/pact/Log/parseConvertUnescapeError.txt";
	private static final String ENCODING_TAG = "%_ENCODING_TAG_%";

	public LogFormatUtilsTest()
	{
		super();
	}
	
	public LogFormatUtilsTest(String arg0)
	{
		super(arg0);
	}

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Expected output for {@link #testParseConvertUnescape()}. Note that not all characters
	 * in here are ASCII, so the encoding scheme matters. See the text of the <tutor_advice>
	 * around the string "WARUM".
	 */
 	private static final String PARSE_CONVERT_UNESCAPE_LOG_OUTPUT =
 		"<?xml version=\"1.0\" encoding=\""+ENCODING_TAG+"\"?>\n"+
 		"<root>\n"+
 		"<log_session_start info_type=\"tutor_message.dtd\" assignment_id=\"\" treatment_id=\"\" class_id=\"\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" date_time=\"2008/01/12 09:18:37\" timezone=\"UTC\" ></log_session_start>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:19:19\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"02C5CF0D-C29C-841B-E945-434DD6057242\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>note1</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>-12</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:19:25\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"73D3278B-BE02-6013-74AF-CF546D396261\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line2left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>10</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:19:25\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"73D3278B-BE02-6013-74AF-CF546D396261\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line2left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>10</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>CORRECT</action_evaluation>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:19:31\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"20111AB7-06FD-85FE-AC21-AC774DDC38FA\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line2right</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>15y</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:19:31\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"20111AB7-06FD-85FE-AC21-AC774DDC38FA\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line2right</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>15y</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>CORRECT</action_evaluation>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:19:38\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"241D1A11-590A-3F44-A274-4D263FB657CB\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>note2</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>/15</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:19:54\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"05F50E45-2ED5-B968-B908-478E50E5AEB1\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>note2</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>/15</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:19:59\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"001277A9-CD39-CC6E-4FD4-4AE09CDFD8F5\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:20:00\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"001277A9-CD39-CC6E-4FD4-4AE09CDFD8F5\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:20:05\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"2D72EF0B-044D-8B25-16B1-CE6EEC3B1308\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>1</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:20:06\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"2D72EF0B-044D-8B25-16B1-CE6EEC3B1308\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:20:28\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"07CB52FF-AB04-67E9-73D0-E406F331F79A\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>note2</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>/15</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:21:31\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"0D1DAA91-54D5-1C33-729D-159927D1715D\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>note2</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>/15</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:21:52\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"026F507B-EEF2-51FB-69EE-AAC7EBCB280B\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>1</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:21:52\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"026F507B-EEF2-51FB-69EE-AAC7EBCB280B\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:21:54\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"07C32D7D-B404-1FE8-9146-D4E9ABFB794F\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:21:54\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"07C32D7D-B404-1FE8-9146-D4E9ABFB794F\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:21:59\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"01283ABD-736D-BC70-34EC-45371142394C\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:21:59\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"01283ABD-736D-BC70-34EC-45371142394C\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:22:02\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"0BFAE4F7-E92D-6321-24F9-EA4DDE5CA1AE\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>4</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:22:02\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"0BFAE4F7-E92D-6321-24F9-EA4DDE5CA1AE\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:22:05\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"0357DD3B-FB50-D8CF-0C4B-D0A73EB71413\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>5</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:22:05\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"0357DD3B-FB50-D8CF-0C4B-D0A73EB71413\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:22:06\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"0039F805-A3FE-2C66-8345-DD14825A8B7B\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>6</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:22:07\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"0039F805-A3FE-2C66-8345-DD14825A8B7B\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:22:09\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"58280091-C61D-C5ED-A834-51E211C87A44\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>7</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:22:09\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"58280091-C61D-C5ED-A834-51E211C87A44\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:22:10\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"79732A17-9E0B-52A5-6323-A1E2483A418A\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>8</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:22:10\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"79732A17-9E0B-52A5-6323-A1E2483A418A\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:22:12\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"08E817C5-577E-6C3A-5CEE-E8180FB46221\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:22:12\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"08E817C5-577E-6C3A-5CEE-E8180FB46221\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>9</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:22:59\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"679A9C43-EE02-870B-B47A-7DA983EA55C8\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>note2</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>/15</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:23:00\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"01D37C5F-0772-279F-2886-AF842CE237B2\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>15</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:23:00\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"01D37C5F-0772-279F-2886-AF842CE237B2\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>INCORRECT</action_evaluation>\n"+
 		"\t\t<tutor_advice>XXxXXberlegt Euch gemeinsam: WARUM ist dieser LXXxXXsungsschritt nicht richtig?</tutor_advice>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:24:54\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"037998C5-5737-7950-8E0C-1017606E5CA1\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>10/15</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:24:54\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"037998C5-5737-7950-8E0C-1017606E5CA1\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>10/15</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>CORRECT</action_evaluation>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:25:09\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"08D350E3-0669-B626-454C-D2AC258D01B6\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3right</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>y</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:25:09\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"08D350E3-0669-B626-454C-D2AC258D01B6\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line3right</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>y</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>CORRECT</action_evaluation>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:25:13\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"0E404485-B096-25C3-3E9C-EC677523F91F\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line4left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:25:13\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"0E404485-B096-25C3-3E9C-EC677523F91F\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line4left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>2/3</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>CORRECT</action_evaluation>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:25:16\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"08BB93F7-C0DB-C74A-6FCA-B166841DCBDB\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line4right</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>y</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TUTOR_ACTION RESULT\" date_time=\"2008/01/12 09:25:16\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tutor_message context_message_id=\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"08BB93F7-C0DB-C74A-6FCA-B166841DCBDB\" name=\"RESULT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line4right</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>y</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t\t<action_evaluation>CORRECT</action_evaluation>\n"+
 		"\t</tutor_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"CONTEXT_ACTION LOAD_TUTOR\" date_time=\"2008/01/12 09:25:24\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<context_message context_message_id=\"09A2D4A5-2C46-F231-A468-3843A7944DE8\" name=\"LOAD_TUTOR\">\n"+
 		"\t\t<class>\n"+
 		"\t\t\t<school>some School</school>\n"+
 		"\t\t</class>\n"+
 		"\t\t<dataset>\n"+
 		"\t\t\t<name>Freiburg Algebra Studie</name>\n"+
 		"\t\t\t<level type=\"\">\n"+
 		"\t\t\t\t<name>All Problems</name>\n"+
 		"\t\t\t\t<problem>\n"+
 		"\t\t\t\t\t<name>procedural3_v4</name>\n"+
 		"\t\t\t\t</problem>\n"+
 		"\t\t\t</level>\n"+
 		"\t\t</dataset>\n"+
 		"\t\t<condition>\n"+
 		"\t\t\t<name>cooperative</name>\n"+
 		"\t\t\t<type>learning_situation</type>\n"+
 		"\t\t\t<desc>Cooperative situation</desc>\n"+
 		"\t\t</condition>\n"+
 		"\t\t<condition>\n"+
 		"\t\t\t<name>procedural</name>\n"+
 		"\t\t\t<type>problem_type</type>\n"+
 		"\t\t\t<desc>Procedural problems</desc>\n"+
 		"\t\t</condition>\n"+
 		"\t</context_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"</root>\n";

	/**
	 * Expected output for {@link #testParseConvertUnescape()}. Note that not all characters
	 * in here are ASCII, so the encoding scheme matters. 
	 */
 	private static final String PARSE_CONVERT_UNESCAPE_ERROR_LOG_OUTPUT =
 		"<?xml version=\"1.0\" encoding=\""+ENCODING_TAG+"\"?>\n"+
 		"<root>\n"+
 		"<log_session_start info_type=\"tutor_message.dtd\" assignment_id=\"\" treatment_id=\"\" class_id=\"\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" date_time=\"2008/01/12 09:18:37\" timezone=\"UTC\" ></log_session_start>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:19:25\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"73D3278B-BE02-6013-74AF-CF546D396261\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line2left</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>10</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"<log_action action_id=\"TOOL_ACTION ATTEMPT\" date_time=\"2008/01/12 09:19:31\" user_guid=\"pi2829\" session_id=\"S_20080112085038_4574379\" auth_token=\"dummy\" timezone=\"UTC\" container_id=\"\" source_id=\"FLASH_PSEUDO_TUTOR\" external_object_id=\"\" info_type=\"tutor_message.dtd\" >\n"+
 		"<tutor_related_message_sequence version_number=\"4\">\n"+
 		"\t<tool_message context_message_id =\"0401CC61-C796-8AE1-DDC9-E0BC9C7D9E96\">\n"+
 		"\t\t<semantic_event transaction_id=\"20111AB7-06FD-85FE-AC21-AC774DDC38FA\" name=\"ATTEMPT\" />\n"+
 		"\t\t<event_descriptor>\n"+
 		"\t\t\t<selection>line2right</selection>\n"+
 		"\t\t\t<action>UpdateTextField</action>\n"+
 		"\t\t\t<input>15y</input>\n"+
 		"\t\t</event_descriptor>\n"+
 		"\t</tool_message>\n"+
 		"</tutor_related_message_sequence>\n"+
 		"</log_action>\n"+
 		"</root>\n";

	/**
	 * Test method for {@link LogFormatUtils#parseConvertUnescape(File)}. 
	 */
	public void testParseConvertUnescape() {
		testParseConvertUnescape(PARSE_CONVERT_UNESCAPE_LOG, PARSE_CONVERT_UNESCAPE_LOG_OUTPUT, LogFormatUtils.DEFAULT_ENCODING);
		testParseConvertUnescape(PARSE_CONVERT_UNESCAPE_LOG, PARSE_CONVERT_UNESCAPE_LOG_OUTPUT, "ISO-8859-1");
		testParseConvertUnescape(PARSE_CONVERT_UNESCAPE_LOG, PARSE_CONVERT_UNESCAPE_LOG_OUTPUT, "UTF-8");
		testParseConvertUnescape(PARSE_CONVERT_UNESCAPE_ERROR_LOG, PARSE_CONVERT_UNESCAPE_ERROR_LOG_OUTPUT, "ISO-8859-1");
	}

	/**
	 * Test method for {@link LogFormatUtils#parseConvertUnescape(File)}.
	 * @param encoding 
	 */
	private void testParseConvertUnescape(String file, String expectedContent, String encoding) {
		try {
			LogFormatUtils.setEncoding(encoding);
			File convertedFile =
				LogFormatUtils.parseConvertUnescape(new File(file));
			FileInputStream is = new FileInputStream(convertedFile);
			Reader rdr = new BufferedReader(new InputStreamReader(is, encoding));
			StringBuffer sb = new StringBuffer();
			for (int c = rdr.read(); c >= 0; c = rdr.read())
				sb.append((char) c);
			assertEquals("content of "+file,
					expectedContent.replace(ENCODING_TAG, encoding),
					sb.toString());
		} catch (Exception e) {
			fail("Error on file "+file+" in "+encoding+": "+e+
					(e.getCause() == null ? "" : "\n cause: "+e.getCause()));
		}
	}
	
	/**
	 * Test method for {@link LogFormatUtils#escape(String)}.
	 */
	public void testEscape() {
		String s;
		s = "none"; assertEquals(s, s, LogFormatUtils.escape(s));
		s = "one&embedded"; assertEquals(s, s.replaceAll("&", "&amp;"), LogFormatUtils.escape(s));
		s = "&leading one"; assertEquals(s, s.replaceAll("&", "&amp;"), LogFormatUtils.escape(s));
		s = "trailing one&"; assertEquals(s, s.replaceAll("&", "&amp;"), LogFormatUtils.escape(s));
		s = "two&em&bedded"; assertEquals(s, s.replaceAll("&", "&amp;"), LogFormatUtils.escape(s));
		s = "ESCembedded&lt;lessthan"; assertEquals(s, s, LogFormatUtils.escape(s));
		s = "ESC&gt;leadingGreaterThan"; assertEquals(s, s, LogFormatUtils.escape(s));
		s = "ESCtrailingAmpersand&amp;"; assertEquals(s, s, LogFormatUtils.escape(s));
		s = "ESCconsecutive&quot;&apos;escapes"; assertEquals(s, s, LogFormatUtils.escape(s));
		s = "ESCtab&#09;embedded"; assertEquals(s, s, LogFormatUtils.escape(s));
		s = "embedded<lessthan"; assertEquals(s, s.replaceAll("<", "&lt;"), LogFormatUtils.escape(s));
		s = ">leadingGreaterThan"; assertEquals(s, s.replaceAll(">", "&gt;"), LogFormatUtils.escape(s));
		s = "trailingAmpersand&"; assertEquals(s, s.replaceAll("&", "&amp;"), LogFormatUtils.escape(s));
		s = "consecutive\"\'escapes"; assertEquals(s, s.replaceAll("\"", "&quot;").replaceAll("\'", "&apos;"),
				LogFormatUtils.escape(s));
		s = "tab\tembedded"; assertEquals(s, s, LogFormatUtils.escape(s));
	}
	
	/**
	 * Test method for {@link LogFormatUtils#unescapeEntity(String)}.
	 */
	public void testUnescapeEntity() {
		String s;
		s = "none"; assertNull(s, LogFormatUtils.unescapeEntity(s));
		s = "&amp;"; assertEquals(s, "&", LogFormatUtils.unescapeEntity(s));
		s = "&lt;"; assertEquals(s, "<", LogFormatUtils.unescapeEntity(s));
		s = "&gt;"; assertEquals(s, ">", LogFormatUtils.unescapeEntity(s));
		s = "&apos;"; assertEquals(s, "'", LogFormatUtils.unescapeEntity(s));
		s = "&quot;"; assertEquals(s, "\"", LogFormatUtils.unescapeEntity(s));
		s = "&#9;"; assertEquals(s, "\t", LogFormatUtils.unescapeEntity(s));
		s = "&#09;"; assertEquals(s, "\t", LogFormatUtils.unescapeEntity(s));
		s = "&#x9;"; assertEquals(s, "\t", LogFormatUtils.unescapeEntity(s));
		s = "&#xA;"; assertEquals(s, "\n", LogFormatUtils.unescapeEntity(s));
		s = "&#x0A;"; assertEquals(s, "\n", LogFormatUtils.unescapeEntity(s));
		s = "&#xA"; assertNull(s, LogFormatUtils.unescapeEntity(s));
		s = "&#A;"; assertNull(s, LogFormatUtils.unescapeEntity(s));
		s = "&xA;"; assertNull(s, LogFormatUtils.unescapeEntity(s));
		s = "#xA;"; assertNull(s, LogFormatUtils.unescapeEntity(s));
	}
	
	/**
	 * Test method for {@link LogFormatUtils#unescapeString(String)}.
	 */
	public void testUnescapeString() {
		String s;
		s = "none"; assertEquals(s, s, LogFormatUtils.unescapeString(s));
		s = "&amp;"; assertEquals(s, "&", LogFormatUtils.unescapeString(s));
		s = "&lt;"; assertEquals(s, "<", LogFormatUtils.unescapeString(s));
		s = "&gt;"; assertEquals(s, ">", LogFormatUtils.unescapeString(s));
		s = "&lt;element&gt;"; assertEquals(s, "<element>", LogFormatUtils.unescapeString(s));
		s = "&apos;"; assertEquals(s, "'", LogFormatUtils.unescapeString(s));
		s = "single&apos;quote"; assertEquals(s, "single'quote", LogFormatUtils.unescapeString(s));
		s = "&quot;"; assertEquals(s, "\"", LogFormatUtils.unescapeString(s));
		s = "&#9;"; assertEquals(s, "\t", LogFormatUtils.unescapeString(s));
		s = "&#09;tab"; assertEquals(s, "\ttab", LogFormatUtils.unescapeString(s));
		s = "tab&#x9;"; assertEquals(s, "tab\t", LogFormatUtils.unescapeString(s));
		s = "&#xA;newline"; assertEquals(s, "\nnewline", LogFormatUtils.unescapeString(s));
		s = "newline&#x0A;"; assertEquals(s, "newline\n", LogFormatUtils.unescapeString(s));
		s = "&#xA"; assertEquals(s, s, LogFormatUtils.unescapeString(s));
		s = "&#A;"; assertEquals(s, s, LogFormatUtils.unescapeString(s));
		s = "&xA;"; assertEquals(s, s, LogFormatUtils.unescapeString(s));
		s = "#xA;"; assertEquals(s, s, LogFormatUtils.unescapeString(s));
	}
	
	/**
	 * Test method for 'edu.cmu.pact.Log.LogFormatUtils.fixAmpersands(String)'
	 */
	public void testFixAmpersands()
	{
		String s;
		s = "none"; assertEquals(s, LogFormatUtils.fixAmpersands(s));
		s = "one&embedded"; assertEquals(s.replaceAll("&", "&amp;"), LogFormatUtils.fixAmpersands(s));
		s = "&leading one"; assertEquals(s.replaceAll("&", "&amp;"), LogFormatUtils.fixAmpersands(s));
		s = "trailing one&"; assertEquals(s.replaceAll("&", "&amp;"), LogFormatUtils.fixAmpersands(s));
		s = "two&em&bedded"; assertEquals(s.replaceAll("&", "&amp;"), LogFormatUtils.fixAmpersands(s));
		s = "embedded&lt;lessthan"; assertEquals(s, LogFormatUtils.fixAmpersands(s));
		s = "&gt;leadingGreaterThan"; assertEquals(s, LogFormatUtils.fixAmpersands(s));
		s = "trailingAmpersand&amp;"; assertEquals(s, LogFormatUtils.fixAmpersands(s));
		s = "consecutive&quot;&apos;escapes"; assertEquals(s, LogFormatUtils.fixAmpersands(s));
		s = "tab&#09;embedded"; assertEquals(s, LogFormatUtils.fixAmpersands(s));
	}

	/**
	 * Test method for 'edu.cmu.pact.Log.LogFormatUtils.formatForHumanReading(File, File, File)'
	 */
	public void testFormatForHumanReading() throws IOException, JDOMException
	{
		String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" +
				"<log_action auth_token=\"\" session_id=\"L-21d344d6:10c5e9b8186:-7fff\" user_guid=\"Dan Tasse\" date_time=\"2006/07/11 13:21:36.00447\" timezone=\"US/Eastern\" action_id=\"author_action_message\" source_id=\"DOCKING_WINDOW\" external_object_id=\"\" info_type=\"author_message.dtd\"><tutor_related_message_sequence version_number=\"2\"><author_action_message><action_type>HIDE</action_type><argument>Simulated Student Console</argument><result/><result_details/></author_action_message></tutor_related_message_sequence></log_action>" +
				"<log_action auth_token=\"\" session_id=\"L-21d344d6:10c5e9b8186:-7fff\" user_guid=\"Dan Tasse\" date_time=\"2006/07/11 13:21:36.00537\" timezone=\"US/Eastern\" action_id=\"author_action_message\" source_id=\"DOCKING_WINDOW\" external_object_id=\"\" info_type=\"author_message.dtd\"><tutor_related_message_sequence version_number=\"2\"><author_action_message><action_type>SHOW</action_type><argument>Behavior Recorder</argument><result/><result_details/></author_action_message></tutor_related_message_sequence></log_action>" +
				"<log_action auth_token=\"\" session_id=\"L-21d344d6:10c5e9b8186:-7fff\" user_guid=\"Dan Tasse\" date_time=\"2006/07/11 13:22:04.00377\" timezone=\"US/Eastern\" action_id=\"author_action_message\" source_id=\"BEHAVIOR_RECORDER\" external_object_id=\"\" info_type=\"author_message.dtd\"><tutor_related_message_sequence version_number=\"2\"><author_action_message><action_type>GO_TO_STATE</action_type><argument>state1</argument><result/><result_details/></author_action_message></tutor_related_message_sequence></log_action>" +
				"<log_action auth_token=\"\" session_id=\"L-21d344d6:10c5e9b8186:-7fff\" user_guid=\"Dan Tasse\" date_time=\"2006/07/11 13:22:04.00467\" timezone=\"US/Eastern\" action_id=\"tutor_message\" source_id=\"PACT_CTAT\" external_object_id=\"\" info_type=\"tutor_message.dtd\"><tutor_related_message_sequence version_number=\"2\"><tutor_message attempt_id=\"L-21d344d6:10c5e9b8186:-7fe3\"><problem_name>C:\\Pact-CVS-Tree\\AuthoringTools\\java\\Projects\\ProblemsOrganizer\\Examples\\Arithmetic\\Addition\\startcopy.brd</problem_name><semantic_event id=\"M-21d344d6:10c5e9b8186:-7fd3\" name=\"RESULT\" trigger=\"DATA\"/><event_descriptor event_id=\"M-21d344d6:10c5e9b8186:-7fd3\"><action>UpdateTable</action><selection>table1_C6R4</selection><input>5</input></event_descriptor><action_evaluation>CORRECT</action_evaluation></tutor_message></tutor_related_message_sequence></log_action></root>";
		File inFile = new File("testFormatForHumanReadingInput.xml");
		FileWriter fw = new FileWriter(inFile);
		fw.write(input);
		fw.close();

		String prefs = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><preferences><columns><auth_token>false</auth_token><session_id>false</session_id><user_guid>true</user_guid>" +
				"<timezone>false</timezone><action_id>true</action_id><source_id>true</source_id><external_object_id>false</external_object_id><info_type>false</info_type>" +
				"<date_time>true</date_time><time_elapsed>true</time_elapsed><current_window>true</current_window><action>true</action><argument>true</argument><result>true</result></columns>" +
				"<show_focus_entries>true</show_focus_entries></preferences>";

		File prefsFile = new File("testHumanReadingPrefs.xml");
		fw = new FileWriter(prefsFile);
		fw.write(prefs);
		fw.close();
		
		File outFile = new File("testFormatForHumanReadingOutput.xml");
		
		LogFormatUtils.formatForHumanReading(inFile, outFile, prefsFile);
		
		SAXBuilder saxb = new SAXBuilder();
		Document doc = saxb.build(outFile);
		
		List items = doc.getRootElement().getChildren();
		
		Element e0 = (Element) items.get(0);
		assertEquals("author_action_message", e0.getAttributeValue("action_id"));
		assertEquals("DOCKING_WINDOW", e0.getAttributeValue("source_id"));
		assertEquals("2006/07/11 13:21:36.447", e0.getAttributeValue("date_time"));
		assertEquals("00:00:00.000", e0.getAttributeValue("time_elapsed"));
		assertEquals("Dan Tasse", e0.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));
		assertEquals("HIDE", e0.getChildText("action"));
		assertEquals("Simulated Student Console", e0.getChildText("argument"));
		assertEquals("", e0.getChildText("result"));
		assertEquals(null, e0.getAttributeValue("timezone"));
		assertEquals(null, e0.getAttributeValue("auth_token"));
		assertEquals(null, e0.getAttributeValue(Logger.SESSION_ID_PROPERTY));
		assertEquals(null, e0.getAttributeValue("external_object_id"));
		assertEquals(null, e0.getAttributeValue("info_type"));
		
		Element e1 = (Element) items.get(1);
		assertEquals("author_action_message", e1.getAttributeValue("action_id"));
		assertEquals("DOCKING_WINDOW", e1.getAttributeValue("source_id"));
		assertEquals("2006/07/11 13:21:36.537", e1.getAttributeValue("date_time"));
		assertEquals("00:00:00.090", e1.getAttributeValue("time_elapsed"));
		assertEquals("Dan Tasse", e1.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));
		assertEquals("SHOW", e1.getChildText("action"));
		assertEquals("Behavior Recorder", e1.getChildText("argument"));
		assertEquals("", e1.getChildText("result"));
		assertEquals(null, e1.getAttributeValue("timezone"));
		assertEquals(null, e1.getAttributeValue("auth_token"));
		assertEquals(null, e1.getAttributeValue(Logger.SESSION_ID_PROPERTY));
		assertEquals(null, e1.getAttributeValue("external_object_id"));
		assertEquals(null, e1.getAttributeValue("info_type"));

		Element e2 = (Element) items.get(2);
		assertEquals("author_action_message", e2.getAttributeValue("action_id"));
		assertEquals("BEHAVIOR_RECORDER", e2.getAttributeValue("source_id"));
		assertEquals("2006/07/11 13:22:04.377", e2.getAttributeValue("date_time"));
		assertEquals("00:00:27.840", e2.getAttributeValue("time_elapsed"));
		assertEquals("Dan Tasse", e2.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));
		assertEquals("GO_TO_STATE", e2.getChildText("action"));
		assertEquals("state1", e2.getChildText("argument"));
		assertEquals("", e2.getChildText("result"));
		assertEquals(null, e2.getAttributeValue("timezone"));
		assertEquals(null, e2.getAttributeValue("auth_token"));
		assertEquals(null, e2.getAttributeValue(Logger.SESSION_ID_PROPERTY));
		assertEquals(null, e2.getAttributeValue("external_object_id"));
		assertEquals(null, e2.getAttributeValue("info_type"));

		Element e3 = (Element) items.get(3);
		assertEquals("tutor_message", e3.getAttributeValue("action_id"));
		assertEquals("PACT_CTAT", e3.getAttributeValue("source_id"));
		assertEquals("2006/07/11 13:22:04.467", e3.getAttributeValue("date_time"));
		assertEquals("00:00:00.090", e3.getAttributeValue("time_elapsed"));
		assertEquals("Dan Tasse", e3.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));
		assertEquals("Selection: table1_C6R4Action: UpdateTable", e3.getChildText("action"));
		assertEquals("Input: 5", e3.getChildText("argument"));
		assertEquals("CORRECT", e3.getChildText("result"));
		assertEquals(null, e3.getAttributeValue("timezone"));
		assertEquals(null, e3.getAttributeValue("auth_token"));
		assertEquals(null, e3.getAttributeValue(Logger.SESSION_ID_PROPERTY));
		assertEquals(null, e3.getAttributeValue("external_object_id"));
		assertEquals(null, e3.getAttributeValue("info_type"));
	
		//try it again, but hide the focus entries
		//the only thing that changes is the <show_focus_entries> text
		prefs = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><preferences><columns><auth_token>false</auth_token><session_id>false</session_id><user_guid>true</user_guid>" +
		"<timezone>false</timezone><action_id>true</action_id><source_id>true</source_id><external_object_id>false</external_object_id><info_type>false</info_type>" +
		"<date_time>true</date_time><time_elapsed>true</time_elapsed><current_window>true</current_window><action>true</action><argument>true</argument><result>true</result></columns>" +
		"<show_focus_entries>false</show_focus_entries></preferences>";
		prefsFile = new File("testHumanReadingPrefs.xml");
		fw = new FileWriter(prefsFile);
		fw.write(prefs);
		fw.close();
		
		LogFormatUtils.formatForHumanReading(inFile, outFile, prefsFile);
		doc = saxb.build(outFile);
		items = doc.getRootElement().getChildren();
		assertEquals(items.size(), 2);
		
		e0 = (Element) items.get(0);
		assertEquals(e0.getChildText("action"), "GO_TO_STATE");
		e1 = (Element) items.get(1);
		assertEquals(e1.getChildText("action"), "Selection: table1_C6R4Action: UpdateTable");
		
		inFile.delete();
		outFile.delete();
		prefsFile.delete();
	}

	/*
	 * Test method for 'edu.cmu.pact.Log.LogFormatUtils.makeValidXML(File)'
	 */
	public void testMakeValidXML() throws IOException
	{
		String s =
		"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><log_action auth_token=\"\" " +
		"session_id=\"L-af34f04%3A10c5e09cb9b%3A-7fff\" user_guid=\"Dan+Tasse\" date_" +
		"time=\"2006/07/11 10:42:56.00498\" timezone=\"US/Eastern\" action_id=\"message" +
		"\" source_id=\"PACT_CTAT\" external_object_id=\"\" info_type=\"tutor_message." +
		"dtd\">%3C%3Fxml+version%3D%221.0%22+encoding%3D%22UTF-8%22%3F%3E%0A%3C" +
		"tutor_related_message_sequence+version_number%3D%222%22%3E%0A+%3Cmessage+" +
		"attempt_id%3D%22L-af34f04%3A10c5e09cb9b%3A-8000%22%3E%0A++%3Cproperty+name" +
		"%3D%22verb%22%3ESendNoteProperty%3C%2Fproperty%3E%0A++%3Cproperty+name%3D%22" +
		"MessageType%22%3ESendWidgetLock%3C%2Fproperty%3E%0A++%3Cproperty+name%3D%22" +
		"WidgetLockFlag%22%3Etrue%3C%2Fproperty%3E%0A+%3C%2Fmessage%3E%0A%3C%2F" +
		"tutor_related_message_sequence%3E%0A%0A</log_action><?xml version=\"1.0\" " +
		"encoding=\"UTF-8\"?><log_action auth_token=\"\" session_id=\"" +
		"L-af34f04%3A10c5e09cb9b%3A-7fff\" user_guid=\"Dan+Tasse\" date_time=\"" +
		"2006/07/11 10:43:04.00730\" timezone=\"US/Eastern\" action_id=\"" +
		"author_action_message\" source_id=\"DOCKING_WINDOW\" external_object_id=\"\" " +
		"info_type=\"author_message.dtd\">%3C%3Fxml+version%3D%221.0%22+encoding%3D%22" +
		"UTF-8%22%3F%3E%0A%3Ctutor_related_message_sequence+version_number%3D%222%22%3E%" +
		"0A+%3Cauthor_action_message%3E%0A++%3Caction_type%3EHIDE%3C%2Faction_type%3E%0A" +
		"++%3Cargument%3ESimulated+Student+Console%3C%2Fargument%3E%0A++%3Cresult+%2F%3E" +
		"%0A++%3Cresult_details+%2F%3E%0A+%3C%2Fauthor_action_message%3E%0A%3C%2F" +
		"tutor_related_message_sequence%3E%0A%0A</log_action>";
		File f = new File("testMakeValidXML.xml");
		FileWriter fw = new FileWriter(f);
		fw.write(s);
		fw.close();
		
		LogFormatUtils.setEncoding("UTF-8");
		LogFormatUtils.makeValidXML(f);
		
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		StringBuffer sb = new StringBuffer();
		while (br.ready())
			sb.append(br.readLine());
		String s2 = sb.toString();
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><log_action auth_token=\"\" session_id=\"L-af34f04%3A10c5e09cb9b%3A-7fff\" user_guid=\"Dan+Tasse\" date_time=\"2006/07/11 10:42:56.00498\" timezone=\"US/Eastern\" action_id=\"message\" source_id=\"PACT_CTAT\" external_object_id=\"\" info_type=\"tutor_message.dtd\">%3C%3Fxml+version%3D%221.0%22+encoding%3D%22UTF-8%22%3F%3E%0A%3Ctutor_related_message_sequence+version_number%3D%222%22%3E%0A+%3Cmessage+attempt_id%3D%22L-af34f04%3A10c5e09cb9b%3A-8000%22%3E%0A++%3Cproperty+name%3D%22verb%22%3ESendNoteProperty%3C%2Fproperty%3E%0A++%3Cproperty+name%3D%22MessageType%22%3ESendWidgetLock%3C%2Fproperty%3E%0A++%3Cproperty+name%3D%22WidgetLockFlag%22%3Etrue%3C%2Fproperty%3E%0A+%3C%2Fmessage%3E%0A%3C%2Ftutor_related_message_sequence%3E%0A%0A</log_action><log_action auth_token=\"\" session_id=\"L-af34f04%3A10c5e09cb9b%3A-7fff\" user_guid=\"Dan+Tasse\" date_time=\"2006/07/11 10:43:04.00730\" timezone=\"US/Eastern\" action_id=\"author_action_message\" source_id=\"DOCKING_WINDOW\" external_object_id=\"\" info_type=\"author_message.dtd\">%3C%3Fxml+version%3D%221.0%22+encoding%3D%22UTF-8%22%3F%3E%0A%3Ctutor_related_message_sequence+version_number%3D%222%22%3E%0A+%3Cauthor_action_message%3E%0A++%3Caction_type%3EHIDE%3C%2Faction_type%3E%0A++%3Cargument%3ESimulated+Student+Console%3C%2Fargument%3E%0A++%3Cresult+%2F%3E%0A++%3Cresult_details+%2F%3E%0A+%3C%2Fauthor_action_message%3E%0A%3C%2Ftutor_related_message_sequence%3E%0A%0A</log_action></root>",
				s2);
		br.close();
		fr.close();
		f.delete();
	}

	/*
	 * Test method for 'edu.cmu.pact.Log.LogFormatUtils.unescape(String)'
	 */
	public void testUnescape()
	{
		String s = "%3C%3Fxml+version%3D%221.0%22+encoding%3D%22UTF-8%22%3F%3E%0A%3Ctutor_related_message_sequence+version_number%3D%222%22%3E%0A+%3Cmessage+attempt_id%3D%22L-af34f04%3A10c5e09cb9b%3A-8000%22%3E%0A++%3Cproperty+name%3D%22verb%22%3ESendNoteProperty%3C%2Fproperty%3E%0A++%3Cproperty+name%3D%22MessageType%22%3ESendWidgetLock%3C%2Fproperty%3E%0A++%3Cproperty+name%3D%22WidgetLockFlag%22%3Etrue%3C%2Fproperty%3E%0A+%3C%2Fmessage%3E%0A%3C%2Ftutor_related_message_sequence%3E%0A%0A";
		s = LogFormatUtils.unescape(s);
		assertEquals(s, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tutor_related_message_sequence version_number=\"2\">\n <message attempt_id=\"L-af34f04:10c5e09cb9b:-8000\">\n  <property name=\"verb\">SendNoteProperty</property>\n  <property name=\"MessageType\">SendWidgetLock</property>\n  <property name=\"WidgetLockFlag\">true</property>\n </message>\n</tutor_related_message_sequence>\n\n");
	}

	/*
	 * Test method for 'edu.cmu.pact.Log.LogFormatUtils.unescapeAll(File)'
	 */
	public void testUnescapeAll() throws IOException
	{
		String s = "%3C%3Fxml+version%3D%221.0%22+encoding%3D%22UTF-8%22%3F%3E%0A%3C" +
				"tutor_related_message_sequence+version_number%3D%222%22%3E%0A+%3C" +
				"message+attempt_id%3D%22L-af34f04%3A10c5e09cb9b%3A-8000%22%3E%0A++" +
				"%3Cproperty+name%3D%22verb%22%3ESendNoteProperty%3C%2Fproperty%3E%0A" +
				"++%3Cproperty+name%3D%22MessageType%22%3ESendWidgetLock%3C%2Fproperty" +
				"%3E%0A++%3Cproperty+name%3D%22WidgetLockFlag%22%3Etrue%3C%2Fproperty" +
				"%3E%0A+%3C%2Fmessage%3E%0A%3C%2Ftutor_related_message_sequence%3E%0A%0A";
		File f = new File("testUnescape.log");
		FileWriter fw = new FileWriter(f);
		fw.write(s);
		fw.close();
		
		LogFormatUtils.unescapeAll(f);
		
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		StringBuffer sb = new StringBuffer();
		while (br.ready())
			sb.append(br.readLine() + "\n");
		
		String s2 = sb.toString();
		assertEquals(s2, "\n<tutor_related_message_sequence version_number=\"2\">\n" +
				" <message attempt_id=\"L-af34f04:10c5e09cb9b:-8000\">\n  <property" +
				" name=\"verb\">SendNoteProperty</property>\n  <property name=\"" +
				"MessageType\">SendWidgetLock</property>\n  <property name=\"" +
				"WidgetLockFlag\">true</property>\n </message>\n" +
				"</tutor_related_message_sequence>\n\n");
		
		br.close();
		fr.close();
		f.delete();
	}

	/*
	 * Test method for 'edu.cmu.pact.Log.LogFormatUtils.getTimeElapsed(Date, Date)'
	 */
	public void testGetTimeElapsed()
	{
		SimpleDateFormat HHmmssSSSOutput = new SimpleDateFormat("HH:mm:ss.SSS");
		Date d1 = new Date(2000);
		Date d2 = new Date(3000);
		String s = LogFormatUtils.getTimeElapsed(d1, d2);
		Date d3 = new Date(1000);
		String s2 = HHmmssSSSOutput.format(d3);
		assertEquals(s.substring(2), s2.substring(2));
		//substrings because, for some reason, new Date(1000) makes a date that
		//is 19:00 on December 31, 1969 instead of 0:00 on January 1, 1970, so
		//it comes out as 19:00:01.000 instead of 00:00:01.000
	}

	/*
	 * Test method for 'edu.cmu.pact.Log.LogFormatUtils.mergeLogs(File, File, File)'
	 */
	public void testMergeLogs() throws IOException
	{
		Element root1 = new Element("root");
		Element child1 = new Element("child1");
		child1.setAttribute("date_time", "2006/07/11 11:08:01.000");
		Element child3 = new Element("child3");
		child3.setAttribute("date_time", "2006/07/11 11:08:03.000");
		Element child5 = new Element("child5");
		child5.setAttribute("date_time", "2006/07/11 11:08:05.000");
		root1.addContent(child1);
		root1.addContent(child3);
		root1.addContent(child5);
				
		Element root2 = new Element("root");
		Element child2 = new Element("child2");
		child2.setAttribute("date_time", "2006/07/11 11:08:02.000");
		Element child4 = new Element("child4");
		child4.setAttribute("date_time", "2006/07/11 11:08:04.000");
		root2.addContent(child2);
		root2.addContent(child4);
		
		XMLOutputter xmlo = new XMLOutputter();
		File f1 = new File("testRoot1.xml");
		FileWriter fw1 = new FileWriter(f1);
		xmlo.output(root1, fw1);
		fw1.close();
		File f2 = new File("testRoot2.xml");
		FileWriter fw2 = new FileWriter(f2);
		xmlo.output(root2, fw2);
		fw2.close();
		
		File f3 = new File("testMerged.xml");
		try{
		LogFormatUtils.mergeLogs(f1, f2, f3);
		} catch (JDOMException je) {fail("JDOM exception");}
		catch (ParseException pe) {fail("Parse exception");}
		
		SAXBuilder saxb = new SAXBuilder();
		Document doc = null;
		try{
		doc = saxb.build(f3);
		} catch (JDOMException je) {fail("JDOM exception");}
		List allChildren = doc.getRootElement().getChildren();
		for(int i = 0; i < allChildren.size(); i++)
		{
			Element currentChild = (Element) allChildren.get(i);
			assertEquals("child" + (i+1), currentChild.getName());
			assertEquals("2006/07/11 11:08:0" + (i+1) + ".000", 
					currentChild.getAttributeValue("date_time"));
		}
		f1.delete();
		f2.delete();
		f3.delete();
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(LogFormatUtilsTest.class);
		return suite;
	}
}
