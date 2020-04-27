package edu.cmu.pact.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.oli.log.client.DiskLogger;
import edu.cmu.pact.Log.DataShopSampleSplitter.DataShopSampleSplitter;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.trace;


public class LogFormatUtils {

	/** Exact match for OLI's XML prologue. */
	private static final String OLI_PROLOGUE = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";

	public static final String DEFAULT_ENCODING = "UTF-8";

	/** Regular expression to match XML prologues. */
	private static final String XML_PROLOGUE_REGEX =
		"<\\?xml version=\"1\\.0\" encoding=\"[-A-Z0-9][-A-Z0-9]*\"\\?>";
	
	/** For matching {@link #XML_PROLOGUE_REGEX}. */
	private static final Pattern XML_PROLOGUE_REGEX_PATTERN =
		Pattern.compile(XML_PROLOGUE_REGEX, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
	
	/** Builder for reuse by {@link #validXML(String, int, int)}. */
	private static final SAXBuilder saxBuilder = new SAXBuilder();

// Why do all the log calls use 5 digits for milliseconds? Only the
// last 3 digits matter. At any rate, these first couple of formats
// aim to fix that.
	private static final SimpleDateFormat yyyyMMddHHmmssSSSSSFormat =
		new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSSS");
	private static final SimpleDateFormat yyyyMMddHHmmssSSSFormat =
		new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	
	/** Regular expression for matching XML decimal entity expressions. */
	private static final Pattern numericEntityFmt = Pattern.compile("&#([0-9][0-9]*);");
	
	/** Regular expression for matching XML hexadecimal entity expressions. */
	private static final Pattern hexNumericEntityFmt = Pattern.compile("&#[xX]([0-9A-Fa-f][0-9A-Fa-f]*);");
	
	/**
	 * Predefined XML entities. From http://www.w3schools.com/xml/xml_cdata.asp.
	 * Odd array elements are the characters, even elements are the escape
	 * sequences. E.g. "&lt;" "&amp;", etc.
	 */
	private static final String[] predefinedEntities = {
		"<",	"&lt;",   // < less than
		">",	"&gt;",   // > greater than
		"&",	"&amp;",  // & ampersand
		"'",	"&apos;", // ' apostrophe
		"\"",	"&quot;"  // " quotation mark
	};
	
	/**
	 * Entries in {@link #predefinedEntities} other than ampersand. For a
	 * {@link StringTokenizer}.
	 */
	private static final String nonAmpersandEntities;
	static {
		StringBuffer escapeUs = new StringBuffer();
		for (int i = 0; i < predefinedEntities.length; i+=2) {
			if (!("&".equals(predefinedEntities[i])))
				escapeUs.append(predefinedEntities[i]);
		}
		nonAmpersandEntities = escapeUs.toString();
	}
	
	/**
	 * A class to record and format skills.
	 */
	static class Skills {
		private StringBuffer sb = null;
		private int nSkills = 0;
		Skills(List skillElts) {
			if (skillElts == null)
				return;
			for (Iterator it = skillElts.iterator(); it.hasNext(); ++nSkills)
				add((Element) it.next());
		}
		void add(Element skillElt) {
			if (sb == null)
				sb = new StringBuffer();
			else
				sb.append("\n");
			String s = null;
			Element category = skillElt.getChild("category");
			if (category != null && (s = category.getTextNormalize()).length() > 0)
				sb.append(s).append(" ");
			Element name = skillElt.getChild("name");
			if (name != null && (s = name.getTextNormalize()).length() > 0)
				sb.append(s);
			s = skillElt.getAttributeValue("buggy");
			if (s != null)
				sb.append(" buggy=").append(s);
			s = skillElt.getAttributeValue("probability");
			if (s != null)
				sb.append(" probability=").append(s);
		}
		public String toString() {
			return (sb == null ? "" : sb.toString());
		}
	}
	private Skills skills;

	/** For reading & writing XML files from Input & OutputStreams. */
	private static Charset charset = Charset.forName(DEFAULT_ENCODING);

	/** Output encoding. See command-line options in {@link #usageExit(int)}, {@link #main(String[])}. */
	private static String encoding = DEFAULT_ENCODING;

	/**
	 * @param args
	 *            args[0] = ctat log file args[1] = Slogger (Firefox extension)
	 *            log file args[2] = merged and unescaped output file args[3] =
	 *            output file for human reading args[4] = location for
	 *            logformatprefs.xml
	 * @see #usageExit(int)
	 */
	public static void main(String[] args)
	{
		trace.out("authorLog", "user.dir is "+System.getProperty("user.dir"));
		int i = 0;
		String encoding = DEFAULT_ENCODING;
		if (args.length > i && "-encoding".equals(args[i])) {
			encoding = (args.length > i+1 ? args[++i] : DEFAULT_ENCODING);
			setEncoding(encoding);
		}
		if (args.length < i+1)
			usageExit(1);
		if ("-readElements".equals(args[i])) {
			try {
		        Element[] getRoot = new Element[1];
				List<Element> elts = readLogFile(args[++i], getRoot);
				write("<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\r\n", System.out);
				write("<root>\r\n", System.out);
				XMLOutputter xmlo = new XMLOutputter(Format.getPrettyFormat().setIndent("  ")
						.setEncoding(encoding).setOmitEncoding(false)
						.setOmitDeclaration(false).setLineSeparator("\r\n"));
				for (Element elt: elts){
					write("\r\n", System.out);
					xmlo.output(elt, System.out);
					write("\r\n", System.out);
				}					
				write("\r\n</root>\r\n", System.out);
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else if ("-unescape".equals(args[i])) {
			for (++i; i < args.length; ++i) {
				try {
					File convertedFile = parseConvertUnescape(new File(args[i]));
					BufferedReader rdr =
							new BufferedReader(new InputStreamReader(new FileInputStream(convertedFile), encoding));
					for (String s = rdr.readLine(); s != null; s = rdr.readLine()) {
						write(s, System.out);
						write("\r\n", System.out);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
			System.exit(0);
		} else if ("-decode".equals(args[i])) {
			for (++i; i < args.length; ++i) {
				try {
					File f = new File(args[i]);
					BufferedReader rdr =
							new BufferedReader(new InputStreamReader(new FileInputStream(f), encoding));
					for (String s = rdr.readLine(); s != null; s = rdr.readLine()) {
						s = unescape(s);
						write(s, System.out);
						write("\r\n", System.out);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
			System.exit(0);
		} else if ("-addCustomFieldMilliSecond".equals(args[i])) {
			try {
		        Element[] getRoot = new Element[1];
				List<Element> elts = readLogFile(args[++i], getRoot);
				OutputStream os = new FileOutputStream(new File(args[++i])); 
				write("<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\r\n", os);
				write("<tutor_related_message_sequence\r\n" + 
						  "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\r\n" +
						  "  xsi:noNamespaceSchemaLocation=\"http://learnlab.web.cmu.edu/dtd/tutor_message_v4.xsd\"\r\n" +
						  "  version_number=\"4\">\r\n", os);

				XMLOutputter xmlo = new XMLOutputter(Format.getPrettyFormat().setIndent("  ")
						.setEncoding(encoding).setOmitEncoding(true)
						.setOmitDeclaration(true).setLineSeparator("\r\n"));
				for (Element elt: elts){
					write("\r\n", os);
					Element newElt = formatForMilliSecond (elt);
					if (newElt != null)
						xmlo.output(newElt, os);
					os.flush();
				}					
				write("\r\n</tutor_related_message_sequence>\r\n", os);
				os.flush();
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
				usageExit(1);
			}
		}
		if (args.length < 4)
			usageExit(1);
		try
		{
			File log = new File(args[0]);
			File preferences;
			if (args.length < 5)
				preferences = new File("logformatprefs.xml");
			else
				preferences = new File(args[4]);

			File tempLog = convertLogFile(log);

			mergeLogs(tempLog, new File(args[1]),
					new File(args[2]));
			formatForHumanReading(new File(args[2]),
				new File(args[3]),
				preferences);
		} catch (Exception e)
		{
			e.printStackTrace();
			usageExit(2);
		}
	}

	/**
	 * Print a help message explaining the command-line arguments and exit the
	 * Java VM.
	 * 
	 * @param exitStatus
	 *            argument to {@link System#exit(int)}
	 * @return never returns: calls {@link System#exit(int)}
	 */
	private static int usageExit(int exitStatus) {
		System.err.println("\n"+
				"Correct usage: java LogFormatUtils c s m hr [p]\n"+
				"           or: java LogFormatUtils [-encoding e] -unescape o...\n"+
				"           or: java LogFormatUtils [-encoding e] -decode u...\n"+
				"           or: java LogFormatUtils [-encoding e] -readElements c...\n"+
				"           or: java LogFormatUtils [-encoding e] -addCustomFieldMilliSecond i o\n"+				
				"where\n"+
				"   c = ctat log file(s) (use dummy name if none),\n"+
				"   i = ctat log file\n"+
				"   o = OLI  DISK log file(s)\n"+
				"   u = file(s) encoded with URL encoding (\"%2C\" for comma, e.g.)\n"+
				"   e = output character encoding (\"ISO-8859-1\", default \"UTF-8\")\n"+
				"   s = Slogger (Firefox extension) log file (use dummy name if none),\n"+
				"   m = merged and unescaped output file,\n"+
				"   hr = output file for human reading,\n"+
				"   p = preferences file (default logformatprefs.xml)");
		System.exit(exitStatus);
		return exitStatus; // not reached
	}

/*
 * doc = SAXBuilder.parse(file) root = doc.getRoot() for (logActElt:
 * root.getChildren()) if logActElt.name != "log_action", skip if
 * (trmsElt=logAct.getChild("tutor_related_message_sequence") == null), skip
 * msgElt = trmsElt.getChild(0) // get first child if msgElt.name not in
 * {context_message, tool_message, tutor_message}, skip insert a new <meta>
 * element as the first child of the msgElt: <meta>
 * <user_guid>logActElt.getAttributeValue("user_guid")</user_guid>
 * <session_id>logActElt.getAttributeValue("session_id")</session_id>
 * <date_time>logActElt.getAttributeValue("date_time")</date_time>
 * <timezone>logActElt.getAttributeValue("timezone")</timezone> </meta> copy
 * the rest of the context_message, tool_message or tutor_message insert a new
 * <custom_field> element as the last child of the msgElt: <custom_field>
 * <name>X_event_time</name> //where X is "context" "tool" or "tutor" Date d =
 * logActElt.getAttributeValue("date_time") manipulate Date d as below (previous
 * msg) <value>2008-03-13 14:04:52.223 UTC</value> </custom_field> output the
 * changed msgElt
 */

	static String tzDateFormat(String time, String timeZone) {
	//	DateFormat dfin = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSSS z");
		DateFormat dfin = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSSS");	
		DateFormat dfout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		
		try {
			dfout.setTimeZone(TimeZone.getTimeZone(timeZone));
	//		Date d = dfin.parse(time + " " + timeZone);
			Date d = dfin.parse(time);		
//			System.err.println("New Format = " + dfout.format(d).toString());
			return dfout.format(d).toString();
		} catch (ParseException pe) {
			
		}
		return time;
	}
	
	static String utcDateFormat(String time, String timeZone) {
		TimeZone tzout = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		DateFormat dfout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
		dfout.setTimeZone(tzout);
		
		try {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			Date d = df.parse(time);
			
//			System.err.println("New Format = " + df.format(d).toString());
			return dfout.format(d).toString();
		} catch (ParseException pe) {
			
		}
		return time;
	}
	static Element formatForMilliSecond (Element logActionElem)
	{
		if (!logActionElem.getName().equals("log_action"))
			return null; // logActionElem;
		
		Element trmsElt = logActionElem.getChild("tutor_related_message_sequence");
		if (trmsElt == null) return logActionElem;

		
		Element tm = null;
		
		if (
			((tm = trmsElt.getChild("context_message")) == null) &&
			((tm = trmsElt.getChild("tutor_message")) == null)  &&
			((tm = trmsElt.getChild("tool_message")) == null))
					return null; // logActionElem;
		else {

			Element metaElement = new Element("meta");

			metaElement.addContent(new Element("user_id"));
			metaElement.getChild("user_id").
					addContent(logActionElem.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));

			metaElement.addContent(new Element(Logger.SESSION_ID_PROPERTY));
			metaElement.getChild(Logger.SESSION_ID_PROPERTY).
					addContent(logActionElem.getAttributeValue(Logger.SESSION_ID_PROPERTY));

			metaElement.addContent(new Element("time"));
			metaElement.getChild("time").addContent(
					tzDateFormat(logActionElem.getAttributeValue("date_time"), logActionElem.getAttributeValue("timezone")));
			

					

			metaElement.addContent(new Element("time_zone"));
			metaElement.getChild("time_zone").addContent(logActionElem.getAttributeValue("timezone"));


			
			
			Element custom_field_Element = new Element("custom_field");
			
			custom_field_Element.addContent(new Element("name"));
			
//			custom_field_Element.getChild("name").addContent(tm.getName());
			if (tm.getName().equals("tool_message")) custom_field_Element.getChild("name").addContent("tool_event_time");
			else if (tm.getName().equals("tutor_message")) custom_field_Element.getChild("name").addContent("tutor_event_time");
			else if (tm.getName().equals("context_message")) custom_field_Element.getChild("name").addContent("context_event_time");
			
			custom_field_Element.addContent(new Element("value"));
			custom_field_Element.getChild("value").addContent(utcDateFormat(logActionElem.getAttributeValue("date_time"), logActionElem.getAttributeValue("timezone")));

			List removeElement = tm.removeContent();
			
			tm.addContent(metaElement);
			tm.addContent(removeElement);
			tm.addContent(custom_field_Element);
		
		}
			
			

//		return logActionElem;  
		return tm;
	
	}
		
	




	
	/**
	 * Formats an XML log so that it can be easily read by a human using Excel.
	 * 
	 * @param inFile
	 *            Well-formed XML log
	 * @param outFile
	 *            XML file, formatted for easy reading. Doesn't include all data
	 *            that is in inFile.
	 * @param prefs
	 *            XML file containing preferences for which columns are visible
	 * @throws IOException
	 * @throws JDOMException
	 */
	static void formatForHumanReading (File inFile, File outFile, File prefs)
		throws JDOMException, IOException
	{
		SAXBuilder saxb = new SAXBuilder();
		Document doc = saxb.build(inFile);
		List allLogActions = doc.getRootElement().getChildren();
		// doc.getRootElement is just the artificially-created <root>

		Document doc2 = saxb.build(prefs);
		Element allPrefs = doc2.getRootElement();

		String currentWindow = "";

		FileWriter fw = new FileWriter(outFile);
		fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		fw.write("<root>\r\n");
		XMLOutputter xmlo = new XMLOutputter(Format.getPrettyFormat().setIndent("  ")
				.setOmitEncoding(false).setOmitDeclaration(false).setLineSeparator("\r\n"));

	    Date lastDate = null;
		Iterator iter = allLogActions.iterator();
		while (iter.hasNext())
		{
			Element logActionElem = (Element) iter.next();

			// we only want log_actions. They should be mostly log_actions
			// anyway.
			// omit the log_session_start because it's echoed by a corresponding
			// log_action
			if (!logActionElem.getName().equals("log_action"))
				continue;



			String actionId = logActionElem.getAttributeValue("action_id");
			String action = null;
			String argument = null;
			String result = null;
			String cycle = null;
			Skills skills = null;

			if (actionId.equals("tool_message"))
			{
				// if there's a tool_message, the next message is a
				// tutor_message,
				// with the same information (plus more).
				continue;
			}

			if (actionId.equals("tutor_message"))
			{
				// tutor_messages caused by the behavior recorder are really
				// behavior
				// recorder actions in disguise.
				if (logActionElem.getAttributeValue("source_id").equals("PACT_CTAT")) {
					if (currentWindow.equals("Student Interface"))
						logActionElem.setAttribute("source_id", "STUDENT_INTERFACE");
				} else
					continue;   // sewall 10/3/06 skip any old WPI tutor
								// messages?
			}

			// the plain old "message"s are not important for this log
			else if (actionId.equals("message"))
				continue;
			// ATTEMPTs and RESULTs are echoed by more informative logs
			else if (actionId.equals("ATTEMPT") || actionId.equals("RESULT"))
				continue;
			else if (actionId.equals("START_TUTOR"))
			{
				action = "START_TUTOR";
			}
			// don't log this, because TEST_MODEL_ALL_STEPS_RESULT will be
			// logged too.
			else if (actionId.equals("author_action_message") &&
					(logActionElem.getChild("tutor_related_message_sequence")
					.getChild("author_action_message").getChildText("action_type")
					.equals("TEST_MODEL_ALL_STEPS")))
				continue;

			if (action == null)
				action = findActionName(logActionElem);
			if (argument == null)
				argument = findArgumentName(logActionElem);
			if (result == null)
				result = findResultName(logActionElem);
			if (cycle == null)
				cycle = findCycleName(logActionElem, action);
			if (skills == null)
				skills = findSkills(logActionElem);

			String fullDate = null;
			Date dateInXmlFile = null;
			try{
			dateInXmlFile = yyyyMMddHHmmssSSSSSFormat.parse(logActionElem
					.getAttributeValue("date_time"));
			} catch(ParseException pe) {pe.printStackTrace();}
			fullDate = yyyyMMddHHmmssSSSFormat.format(dateInXmlFile);
			logActionElem.setAttribute("date_time", fullDate);

			// reorder attributes so date_time is next to time_elapsed
			Attribute dateTimeAttr = logActionElem.getAttribute("date_time");
			logActionElem.removeAttribute(dateTimeAttr);
			logActionElem.setAttribute(dateTimeAttr);

			String timeElapsed = getTimeElapsed(lastDate, dateInXmlFile);
			logActionElem.setAttribute("time_elapsed", timeElapsed);
			lastDate = dateInXmlFile;


			// set the current window (usually Docked Window or Student
			// Interface)
			if (action.equals("FOCUS_GAINED"))
				currentWindow = argument;
			else if (action.equals("FOCUS_LOST") && currentWindow.equals(argument))
				currentWindow = "";

			// remove all children and text, then add the children back one by
			// one
			logActionElem.removeContent();

			logActionElem.addContent(new Element("action"));
			logActionElem.getChild("action").addContent(new Text(action));

			logActionElem.addContent(new Element("argument"));
			logActionElem.getChild("argument").addContent(new Text(argument));

			logActionElem.addContent(new Element("result"));
			logActionElem.getChild("result").addContent(new Text(result));

			logActionElem.addContent(new Element("cycle"));
			logActionElem.getChild("cycle").addContent(new Text(cycle));

			logActionElem.addContent(new Element("skills"));
			logActionElem.getChild("skills").addContent(new Text(skills.toString()));

			// hide whichever columns are designated to hide in the prefs file
			hideColumns(logActionElem, prefs);

			// if the preferences are set to hide focus entries, hide them
			if ((action.equals("SHOW") || action.equals("HIDE")
					|| action.equals("FOCUS_GAINED") || action.equals("FOCUS_LOST"))
					&&
					!allPrefs.getChildText("show_focus_entries").equals("true"))
				continue;

			xmlo.output(logActionElem, fw);
			fw.write("\r\n");
		}
		fw.write("</root>\r\n");
		fw.close();

	}

	private static Skills findSkills(Element logActionElem) {
		Element trmsElt = logActionElem.getChild("tutor_related_message_sequence");
		if (null != trmsElt) {
			Element tmElt = trmsElt.getChild("tutor_message");
			if (null != tmElt) {
				List skillElts = tmElt.getChildren("skill");
				return new Skills(skillElts);
			}
		}
		return new Skills(null);
	}

	private static String findCycleName(Element logActionElem, String action)
	{
		if (action == null)
			return "";

		String sourceId = logActionElem.getAttributeValue("source_id");
		if (sourceId.equals(AuthorActionLog.EXTERNAL_EDITOR))
			return "edit";
		else if (sourceId.equals(AuthorActionLog.WHY_NOT_WINDOW))
			return "debug";
		else if (sourceId.equals(AuthorActionLog.CONFLICT_TREE))
			return "debug";
		else if (sourceId.equals(AuthorActionLog.WORKING_MEMORY_EDITOR))
		{
			return "debug";
		}
		else if (sourceId.equals(AuthorActionLog.BEHAVIOR_RECORDER))
		{
			if (action.equals("TEST_MODEL_ALL_STEPS_RESULT"))
				return "test";
			else if (action.equals("TEST_MODEL_1_STEP_RESULT"))
				return "test";
		}
		else if (sourceId.equals(AuthorActionLog.STUDENT_INTERFACE))
			return "test";
		return "";
	}


	private static void hideColumns(Element logActionElem, File prefs) throws JDOMException, IOException
	{
		// it's a little inefficient to generate all the columns and then hide
		// them, but
		// it's simpler, and this method doesn't need to be particularly
		// efficient.
		SAXBuilder s = new SAXBuilder();
		Document d = s.build(prefs);
		Element allPrefs = d.getRootElement().getChild("columns");

		List allAttrs = logActionElem.getAttributes();
		for(int i = allAttrs.size() - 1; i >= 0; i--)
		{
			Attribute attr = (Attribute) allAttrs.get(i);

			if (! allPrefs.getChildText(attr.getName()).trim().equals("true"))
				logActionElem.removeAttribute(attr);
		}
	}

	private static String findArgumentName(Element logActionElem)
	{
		String actionId = logActionElem.getAttributeValue("action_id");
		if (actionId == null)
			return "";
		else if (actionId.equals("author_action_message"))
		{
			Element authorActionMessageElem =
				logActionElem.getChild("tutor_related_message_sequence")
				.getChild("author_action_message");
			return (authorActionMessageElem.getChildText("argument"));
		}
		else if (actionId.equals("curriculum_message"))
		{
			Element curriculumElem =
				logActionElem.getChild("tutor_related_message_sequence")
				.getChild("curriculum_message");
			return (curriculumElem.getChildText("course_name") + ":"
					+ curriculumElem.getChildText("unit_name") + ":"
					+ curriculumElem.getChildText("section_name") + ":"
					+ curriculumElem.getChildText("problem_name"));
		}
		else if (logActionElem.getAttributeValue("action_id").equals("program_action_message"))
		{
			Element programActionMessageElem =
				logActionElem.getChild("tutor_related_message_sequence")
				.getChild("program_action_message");
			return programActionMessageElem.getChildText("argument");
		}
		else if (actionId.equals("tutor_message")
				&& isTutorResult(logActionElem))
		{
			Element eventDescriptorElem =
				logActionElem.getChild("tutor_related_message_sequence")
				.getChild("tutor_message").getChild("event_descriptor");
			if (eventDescriptorElem != null)
				return ("Input: " + eventDescriptorElem.getChildText("input"));
			else
				return "";
		}
		else
			return "";
	}

	private static String findResultName(Element logActionElem)
	{
		String actionId = logActionElem.getAttributeValue("action_id");
		if (actionId == null)
			return "";
		else if (actionId.equals("author_action_message"))
		{
			Element authorActionMessageElem =
				logActionElem.getChild("tutor_related_message_sequence")
				.getChild("author_action_message");
			return (authorActionMessageElem.getChildText("result") + "" +
				authorActionMessageElem.getChildText("result_details"));
		}
		else if (logActionElem.getAttributeValue("action_id").equals("program_action_message"))
		{
			Element programActionMessageElem =
				logActionElem.getChild("tutor_related_message_sequence")
				.getChild("program_action_message");
			return programActionMessageElem.getChildText("result") +
				programActionMessageElem.getChildText("result_details");
		}
		else if (actionId.equals("tutor_message")
				&& logActionElem.getChild("tutor_related_message_sequence")
				.getChild("tutor_message").getChild("semantic_event")
				.getAttributeValue("name").equals("RESULT"))
		{
			Element tutorMessageElem = logActionElem
				.getChild("tutor_related_message_sequence").getChild("tutor_message");

			return (tutorMessageElem.getChildText("action_evaluation"));
		}
		return "";
	}


	private static String findActionName(Element logActionElem)
	{
		if (logActionElem.getAttributeValue("action_id") == null)
			return "";
		else if (logActionElem.getAttributeValue("action_id").equals("author_action_message"))
		{
			Element authorActionMessageElem =
				logActionElem.getChild("tutor_related_message_sequence")
				.getChild("author_action_message");
			return authorActionMessageElem.getChildText("action_type");
		}
		else if (logActionElem.getAttributeValue("action_id").equals("program_action_message"))
		{
			Element programActionMessageElem =
				logActionElem.getChild("tutor_related_message_sequence")
				.getChild("program_action_message");
			return programActionMessageElem.getChildText("action_type");
		}
		else if (logActionElem.getAttributeValue("action_id").equals("tutor_message")
				&& isTutorResult(logActionElem))
		{
			Element eventDescriptorElem =
				logActionElem.getChild("tutor_related_message_sequence")
				.getChild("tutor_message").getChild("event_descriptor");
			if (eventDescriptorElem != null)
				return ("Selection: " + eventDescriptorElem.getChildText("selection")
						+ "Action: " + eventDescriptorElem.getChildText("action"));
		}
		return "";
	}

	/**
	 * Tell whether a log_action element is a tutor_message RESULT.
	 * 
	 * @param logActionElem
	 * @return true if has a tutor_message element whose semantic_event's name
	 *         is RESULT
	 */
	private static boolean isTutorResult(Element logActionElem)
	{
		Element trms = logActionElem.getChild("tutor_related_message_sequence");
		if (trms == null)
			return false;
		Element tm = trms.getChild("tutor_message");
		if (tm == null)
			return false;
		Element se = tm.getChild("semantic_event");
		if (se == null)
			return false;
		trace.out("log", "semantic_event transaction_id "+se.getAttributeValue("transaction_id"));
		String name = se.getAttributeValue("name");
		return "RESULT".equals(name);
	}

	/**
	 * Reformats a file of logger output so that it is valid XML. (modifies
	 * file)
	 * 
	 * @param f
	 *            the file to be modified
	 */
	public static void makeValidXML (File f) throws IOException
	{
		makeValidXML(f, f);
	}
	/**
	 * Reformats a file of logger output so that it is valid XML. (modifies
	 * file). Reads file in ISO-8859-1 encoding, matching OLI's top-level XML encoding.
	 * Writes file in current {@link #getEncoding()} charset.	
	 * @param inf the input file to be modified
	 * @param outf the output file to be modified
	 */
	public static void makeValidXML(File inf, File outf) throws IOException
	{		
		FileInputStream is = new FileInputStream(inf);
		InputStreamReader isr = new InputStreamReader(is, "ISO-8859-1");
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		while (br.ready()) {
			sb.append(br.readLine());  // might be just one line
		}
		br.close();
		isr.close();
		is.close();

		String[] eltTexts = docTextsToElementTexts(sb.toString());//FIXME this is returning size 0

		if (trace.getDebugCode("log"))
			trace.out("log", "file "+inf.getName()+": nMsgs "+eltTexts.length);
		String s0 = (eltTexts.length > 0 ? eltTexts[0] : "");

		OutputStream os = new FileOutputStream(outf);
		if (s0.length() < 1) {  // return empty file
			trace.err("Found no valid XML in file "+inf.getCanonicalPath());//FIXME now that I add the xml header early, this is never true
			write("", os);
		} else {
			//write("<?xml version=\"1.0\" encoding=\""+getEncoding()+"\"?>", os);
			boolean needsRoot = (s0.startsWith("<log_action") || s0.startsWith("<log_session"));
			if (needsRoot)
				write("<root>", os); // there's no top-level node, so it's not valid xml
			for (String s: eltTexts)
				write(s, os);
			if (needsRoot)
				write("</root>", os);
		}
		os.close();
	}

	/**
	 * Split a given string, containing a series of concatenated documents, into an
	 * array of valid element texts.
	 * @param s
	 * @return
	 */
	private static String[] docTextsToElementTexts(String s) {
		Matcher m = XML_PROLOGUE_REGEX_PATTERN.matcher(s);
		if (!m.find()) {
			
			trace.err("Found no instances of XML prolog pattern "+XML_PROLOGUE_REGEX_PATTERN.toString()+
					"\nSplitting to try returning an array of individual XML documents");
			
			//positive lookbehind (?<=) which will split on every zero-length string that is preceded by a ">"
			String[] result = s.split("(?<=>)");
			
			if(trace.getDebugCode("logconsole"))
			for(int i = 0; i < result.length; i++){
				trace.out("logconsole", result[i]);
			}
			
			return result;
		}

		List<String> result = new ArrayList<String>();
		int b0 = m.start();
		int b1 = 0;
		
		if (b0 > 0) {                   // prologue pattern not found at start of s			
			String s0 = s.substring(0, b0).trim();
			if (s0.length() > 0) {  // non-whitespace before initial prologue
				trace.err("Warning: data (length ) found before first XML prologue: "+
						(s0.length() > 30 ? s0.substring(0, 30)+"..." : s0));
				if (validXML(s, 0, b0))
					result.add(s0);
			}
		}

		do {
			int e = m.end();
			b1 = (m.find() ? m.start() : s.length());
			if (validXML(s, b0, b1))
				result.add(s.substring(e, b1));
			b0 = b1;
		} while (b1 < s.length());
		
		int n = result.size();
		trace.out("log", "docTextsToElementTexts found "+n+" valid documents");
		return result.toArray(new String[n]);
	}

	/**
	 * Check whether a substring is valid XML.
	 * @param s source string
	 * @param b starting index in s
	 * @param e ending index in s (index of 1st char following substring to check) 
	 * @return true if valid
	 */
	private static boolean validXML(String s, int b, int e) {
		try {
			String fragment = s.substring(b, e);
			StringReader rdr = new StringReader(fragment);
			Document d = saxBuilder.build(rdr);
			return true;
		} catch (JDOMException je) {
			trace.err("invalid XML between chars "+b+" & "+e+": "+je);
			return false;
		} catch (IOException ioe) {
			trace.err("i/o error between chars "+b+" & "+e+": "+ioe);
			return false;
		}
	}

	// from w3c at http://www.w3.org/International/unescape.java
	public static String unescape(String s) {
		if (true) {
			try {
				return URLDecoder.decode(s, "UTF-8");
			} catch (UnsupportedEncodingException uee) {
				trace.err("should not happen on UTF-8:" + uee);
				return s;
			}
		}
		StringBuffer sbuf = new StringBuffer();
		int l = s.length();
		int ch = -1;
		int b, sumb = 0;
		for (int i = 0, more = -1; i < l; i++) {
			/* Get next byte b from URL segment s */
			switch (ch = s.charAt(i)) {
			case '%':
				ch = s.charAt(++i);
				int hb = (Character.isDigit((char) ch) ? ch - '0'
						: 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
				ch = s.charAt(++i);
				int lb = (Character.isDigit((char) ch) ? ch - '0'
						: 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
				b = (hb << 4) | lb;
				break;
			case '+':
				b = ' ';
				break;
			default:
				b = ch;
			}
			/* Decode byte b as UTF-8, sumb collects incomplete chars */
			if ((b & 0xc0) == 0x80) { // 10xxxxxx (continuation byte)
				sumb = (sumb << 6) | (b & 0x3f); // Add 6 bits to sumb
				if (--more == 0)
					sbuf.append((char) sumb); // Add char to sbuf
			} else if ((b & 0x80) == 0x00) { // 0xxxxxxx (yields 7 bits)
				sbuf.append((char) b); // Store in sbuf
			} else if ((b & 0xe0) == 0xc0) { // 110xxxxx (yields 5 bits)
				sumb = b & 0x1f;
				more = 1; // Expect 1 more byte
			} else if ((b & 0xf0) == 0xe0) { // 1110xxxx (yields 4 bits)
				sumb = b & 0x0f;
				more = 2; // Expect 2 more bytes
			} else if ((b & 0xf8) == 0xf0) { // 11110xxx (yields 3 bits)
				sumb = b & 0x07;
				more = 3; // Expect 3 more bytes
			} else if ((b & 0xfc) == 0xf8) { // 111110xx (yields 2 bits)
				sumb = b & 0x03;
				more = 4; // Expect 4 more bytes
			} else /* if ((b & 0xfe) == 0xfc) */{ // 1111110x (yields 1 bit)
				sumb = b & 0x01;
				more = 5; // Expect 5 more bytes
			}
			/* We don't test if the UTF-8 encoding is well-formed */
		}
		return sbuf.toString();
	}
	
	/**
	 * Escape characters that are illegal in XML attribute values.
	 * 
	 * @param s
	 *            String to edit
	 * @return edited String
	 */
	public static String escape(String s) {
		if (s == null || s.length() < 1)
			return s;
		s = fixAmpersands(s);  // deal w/ the & chars first

		// now for the chars other than &
		StringTokenizer tkzr = new StringTokenizer(s, nonAmpersandEntities, true); 
		StringBuffer sb = new StringBuffer();
		while (tkzr.hasMoreTokens()) {
			String tkn = tkzr.nextToken();
			if (tkn.length() == 1) {
				for (int i = 0; i < predefinedEntities.length; i+=2) {
					if (predefinedEntities[i].equals(tkn)) {
						tkn = predefinedEntities[i+1];
						break;
					}
				}
			}
			sb.append(tkn);
		}
		return sb.toString();
	}

	/**
	 * Fix ampersands that need to be in XML escape sequences but aren't.
	 * 
	 * @param s
	 *            String to edit
	 * @return edited String
	 */
	public static String fixAmpersands(String s) {
		int a0 = s.indexOf('&');
		if (a0 < 0)
			return s;
		StringBuffer sb = new StringBuffer(s.substring(0, a0));
		for (int a = a0, a1 = s.indexOf('&', a+1); a >= 0; a = a1, a1 = s.indexOf('&', a+1)) {
			String seg = (a1 < 0 ? s.substring(a) : s.substring(a, a1));
			String escSeq = startingEscapeSequence(seg);
			if (escSeq == null)
				sb.append("&amp;").append(seg.substring(1));
			else
				sb.append(escSeq).append(seg.substring(escSeq.length()));
		}
		return sb.toString();
	}
	
	/**
	 * If the given string starts with an XML escape sequence, then return that
	 * sequence. Uses
	 * 
	 * @param s
	 * @return the leading escape sequence, including the '&' and ';'; null if s
	 *         does not begin with an escape sequence
	 */
	public static String startingEscapeSequence(String s) {
		if (s == null || s.length() < 2)
			return null;
		int semicolonPos = s.indexOf(';'); 
		if (s.charAt(0) != '&' || semicolonPos < 1)
			return null;
		String escSeq = s.substring(0, semicolonPos+1);
		String unescChar = unescapeEntity(escSeq);
		return unescChar == null ? null : escSeq;
	}
	
	/**
	 * Unescape all XML entities in given string.
	 * @param s string to translate
	 * @return translated string
	 */
	public static String unescapeString(String s) {
		if (s == null)
			return null;
		int a0 = s.indexOf('&');
		if (a0 < 0)
			return s;
		StringBuffer sb = new StringBuffer(s.substring(0, a0));
		for (int a = a0, a1 = s.indexOf('&', a+1); a >= 0; a = a1, a1 = s.indexOf('&', a+1)) {
			String seg = (a1 < 0 ? s.substring(a) : s.substring(a, a1));
			String escSeq = startingEscapeSequence(seg);
			if (escSeq == null)
				sb.append("&").append(seg.substring(1));
			else {
				sb.append(unescapeEntity(escSeq));
				sb.append(seg.substring(escSeq.length(), seg.length()));
			}
		}
		return sb.toString();
		
	}

	/**
	 * Tell whether the given string is an XML escape sequence.
	 * 
	 * @param seq
	 * @return converted sequence; null if not an escape sequence
	 */
	public static String unescapeEntity(String seq) {
		if (seq == null || seq.length() < 2)
			return null;
		int semicolonPos = seq.indexOf(';'); 
		if (seq.charAt(0) != '&' || semicolonPos < 1)
			return null;
		Matcher m = numericEntityFmt.matcher(seq);
		if (m.matches())
			return String.valueOf((char) (Integer.parseInt(m.group(1))));
		m = hexNumericEntityFmt.matcher(seq);
		if (m.matches())
			return String.valueOf((char) (Integer.parseInt(m.group(1), 16)));
		for (int i = 1; i < predefinedEntities.length; i+=2) {
			if (predefinedEntities[i].equals(seq))
				return predefinedEntities[i-1];
		}
		return null;
	}

	/**
	 * unescapes all inner xml entries in a file (modifies file)
	 * 
	 * @param f
	 *            file to be unescaped
	 * @throws IOException
	 */
	public static void unescapeAll(File f) throws IOException
	{
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		StringBuffer sb = new StringBuffer();
		while (br.ready())
		{
			sb.append(br.readLine());
		}
		String s = sb.toString();
		s = unescapeAll(s, true);
		FileWriter fw = new FileWriter(f);
		fw.write(s);
		fw.close();

		br.close();
		fr.close();
	}

	/**
	 * Call {@link #unescape(String)} from the URLEncoding, {@link #fixAmpersands(String)} and
	 * remove XML prologues matching {@link #XML_PROLOGUE_REGEX}. 
	 * @param s String to modify
	 * @param escAmperands if true, call {@link #fixAmpersands(String)} after unescaping
	 * @return modified string
	 */
	static String unescapeAll(String s, boolean escAmperands) {
		if (s == null)
			return s;
		s = unescape(s);
		if (escAmperands)
			s = fixAmpersands(s);
		s = s.replaceAll(XML_PROLOGUE_REGEX, "");
		return s;
	}

	/**
	 * Given two dates formatted as they are in the xml log file, find the time
	 * between them
	 * 
	 * @param oldTime
	 * @param newTime
	 * @return The time elapsed between the two, formatted as HH:mm:ss.SSS
	 */
	static String getTimeElapsed(Date oldTime, Date newTime)
	{
		Date d = null;
		String timeElapsed = "";
		long msSinceLastLog = 0;

		SimpleDateFormat SSSSSInput = new SimpleDateFormat("SSSSS");
		SimpleDateFormat HHmmssSSSOutput = new SimpleDateFormat("HH:mm:ss.SSS");

		if (oldTime == null)
			oldTime = newTime;
		msSinceLastLog = newTime.getTime() - oldTime.getTime();
		try{
			d = SSSSSInput.parse((new Long(msSinceLastLog).toString()));
			timeElapsed = HHmmssSSSOutput.format(d);
		}catch (ParseException pe) {pe.printStackTrace();}
		return timeElapsed;
	}


	/**
	 * Given two properly formatted xml logs of author actions, create a log of
	 * all actions recorded in either log.
	 * 
	 * @param log1
	 *            The first of the two logs to be merged
	 * @param log2
	 *            The second log to be merged
	 * @param outFile
	 *            The destination for the merged log
	 * @throws IOException
	 * @throws JDOMException
	 *             Thrown if either file isn't well-formed XML
	 * @throws ParseException
	 */
	public static void mergeLogs(File log1, File log2, File outFile) throws JDOMException, IOException, ParseException
	{
		FileOutputStream fos = new FileOutputStream(outFile);
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", fos);
		write("<root>", fos);

		XMLOutputter xmlout = new XMLOutputter();
		xmlout.getFormat().setEncoding("UTF-8");

		SAXBuilder saxb = new SAXBuilder();

		// if either file doesn't exist, make it a blank file so that the merge
		// will still work properly.
		Document log1doc = (log1.exists()? saxb.build(log1): new Document(new Element("root")));
		Document log2doc = (log2.exists()? saxb.build(log2): new Document(new Element("root")));

		List log1nodes = log1doc.getRootElement().getChildren();
		List log2nodes = log2doc.getRootElement().getChildren();

		// merge the lists; index i for debugging
		for (int i = 0; (!log1nodes.isEmpty()) && (!log2nodes.isEmpty()); ++i)
		{
			Element e1 = (Element) log1nodes.get(0);
			Element e2 = (Element) log2nodes.get(0);
			String dateStr1 = e1.getAttributeValue("date_time");
			String dateStr2 = e2.getAttributeValue("date_time");
			if (dateStr1 == null) {
				log1nodes.remove(0);
				continue;
			}
			if (dateStr2 == null) {
				log2nodes.remove(0);
				continue;
			}
			Date date1 = yyyyMMddHHmmssSSSFormat.parse(dateStr1);
			Date date2 = yyyyMMddHHmmssSSSFormat.parse(dateStr2);
			if (date1.before(date2))
				xmlout.output((Element)(log1nodes.remove(0)), fos);
			else
				xmlout.output((Element)(log2nodes.remove(0)), fos);
		}
		while (!log1nodes.isEmpty())
			xmlout.output((Element) log1nodes.remove(0), fos);
		while (!log2nodes.isEmpty())
			xmlout.output((Element) log2nodes.remove(0), fos);

		write("</root>", fos);
		fos.close();
	}

	/**
	 * Convert an OLI log file recorded by {@link DiskLogger} to well-formed,
	 * unescaped XML. Does not alter original file.
	 * before altering.
	 * 
	 * @param inf
	 *            input file
	 * @return copy of inf, converted
	 * @throws IOException
	 */
	public static File convertLogFile(File inf) throws IOException {
		File infTemp = inf;
		
		if (trace.getDebugCode("log"))
			trace.out("log", "convertLogFile file name: " + infTemp);
		
		try {
			// Check if the file is a tab-delimited sample from the Data Shop
			if( DataShopSampleSplitter.isTabDelimited( inf ) )
			{
				infTemp = File.createTempFile(inf.getName(), ".tmp");
				infTemp.deleteOnExit();
				DataShopSampleSplitter.convertTabDelimitedToXML( inf, infTemp );
			}
			return parseConvertUnescape(infTemp);
		} catch (Exception e) {
			String errMsg = "Error converting log file "+inf.getName();
			trace.err(errMsg+": "+e);
			IOException ioe2 = new IOException(errMsg);
			ioe2.initCause(e);
			throw ioe2;
		}
	}
	
	/**
	 * Read a file recorded by {@link DiskLogger}.
	 * @param inFileName name of file to read
	 * @param returnRoot if not null, return root in the first element of this array
	 * @return top-level elements from the file
	 * @throws FileNotFoundException if can't resolve or read inFileName
	 * @throws Exception on error from {@link #parseLog(File, boolean)}
	 */
	public static List readLogFile(String inFileName, Element[] returnRoot)
			throws Exception {
		return readLogFile(inFileName, true, returnRoot);
	}
	
	/**
	 * Read a file recorded by {@link DiskLogger}.
	 * @param inFileName name of file to read
	 * @param convert if true, will call {@link #convertLogFile(File)} to remove XML escaping 
	 * @param returnRoot if not null, return root in the first element of this array
	 * @return top-level elements from the file
	 * @throws FileNotFoundException if can't resolve or read inFileName
	 * @throws Exception on error from {@link #parseLog(File, boolean)}
	 */
	public static List<Element> readLogFile(String inFileName, boolean convert, Element[] returnRoot)
			throws Exception {
		File inf = (inFileName == null ? null : new File(inFileName));
		if (inf == null || !inf.isFile()) {
			Exception e = new FileNotFoundException("cannot read file \""+inFileName+"\"");
			trace.err(e.toString());
			throw e;
		}
		Document doc = LogFormatUtils.parseLog(inf, convert);
		
		if(trace.getDebugCode("log")) {
			int i = 0;
			for(Object elt : doc.getRootElement().getChildren("log_action")) {
				Element e = (Element) elt;
				System.out.printf("%s[%3d] has %d children\n", e.getName(), i++, e.getChildren().size());
			}
		}
		
		Element root = doc.getRootElement();
		List<Element> logEntries = (List<Element>) root.getChildren(); //Returns list of log_Actions
		if (returnRoot != null && returnRoot.length > 0)
			returnRoot[0] = root;
		
		trace.out("log", "readLogFile: logEntries is "+logEntries);
		trace.out("log", "readLogFile: logEntries.size() "+logEntries.size());
		trace.out("log", "doc = "+(new XMLOutputter().outputString(doc)));

		return logEntries;
	}

	/**
	 * Parse a log file. If {@link #convertLog} is true, first calls
	 * {@link convertLogFile}
	 * 
	 * @param inf
	 * @param convertLog
	 *            whether to convert the file from {@link
	 * @return document node parsed from XML; null if error occurs
	 * @throws Exception
	 *             on error from {@link #convertLogFile(File)},
	 *             {@link SAXBuilder#build(File)}
	 */
	public static Document parseLog(File inf, boolean convertLog)
			throws Exception {
		File infTemp;
		infTemp = (convertLog ? convertLogFile(inf) : inf);

		if (trace.getDebugCode("log")) trace.out("log", "parseLog pretty printing " + convertLog);
		
		Document doc;
		try {
			SAXBuilder saxb = new SAXBuilder();
			doc = saxb.build(infTemp);
			if (infTemp != inf)
				infTemp.delete();
			return doc;
		} catch (JDOMException je) {
			Exception e = new Exception("Error parsing log file "+infTemp.getName()+": "+je+
					(je.getCause() == null ? "" : "; cause: "+je.getCause().toString()), je);
			trace.err(e.toString());
			throw e;
		} catch (IOException ioe) {
			Exception e = new Exception("Error parsing log file "+infTemp.getName()+": "+ioe, ioe);
			trace.err(e.toString());
			throw e;
		}
	}

	/**
	 * Parse a log file. This method tries to undo the escaping and other conversions in
	 * {@link DiskLogger} in reverse order.
	 * @param inf
	 * @return reference to temp file with changed child elements; null if error occurs
	 * @throws Exception
	 *             on error from {@link #makeValidXML(File, File)},
	 *             {@link SAXBuilder#build(File)}
	 */
	public static File parseConvertUnescape(File inf)
			throws Exception {
		File infTemp = File.createTempFile(inf.getName(), ".tmp");
		infTemp.deleteOnExit();
		Document doc;
		try {
			makeValidXML(inf, infTemp);
			SAXBuilder saxb = new SAXBuilder();
			doc = saxb.build(infTemp);
		} catch (JDOMException je) {
			Exception e = new Exception("Error parsing log file "+infTemp.getName()+": "+je+
					(je.getCause() == null ? "" : ";\n cause: "+je.getCause().toString()), je);
			trace.err(e.toString());
			throw e;
		} catch (IOException ioe) {
			Exception e = new Exception("Error reading log file "+inf.getName()+": "+ioe+
					(ioe.getCause() == null ? "" : ";\n cause: "+ioe.getCause().toString()), ioe);
			trace.err(e.toString());
			throw e;
		}
		
		StringWriter sw = new StringWriter();
		sw.write("<?xml version=\"1.0\" encoding=\""+getEncoding()+"\"?>\n");
		sw.write("<root>\n");
		XMLOutputter xmlo = new XMLOutputter(Format.getPrettyFormat().setIndent("  ")
				.setEncoding(getEncoding()).setOmitEncoding(true)
				.setOmitDeclaration(true).setLineSeparator("\r\n"));
		Element root = doc.getRootElement();
		List children = root.getChildren();
		Iterator childrenIt = children.iterator();
		for (int i = 1; childrenIt.hasNext(); ++i) {
			Element child = (Element) childrenIt.next();
			String escText = child.getTextTrim();
			String text = unescapeAll(escText, false);
			child.setText("");
			StringBuffer tag = new StringBuffer(xmlo.outputString(child));
			tag.deleteCharAt(tag.lastIndexOf("/"));
			sw.append(tag);
			sw.append(text);
			sw.append("</").append(child.getName()).append(">\n");
		}
		sw.write("</root>\n");
		sw.close();

		File outfTemp = File.createTempFile(inf.getName(), ".tmp2");
		outfTemp.deleteOnExit();
		OutputStream os = new FileOutputStream(outfTemp);
		write(sw.toString(), os);
		os.close();
		return outfTemp;
	}

	/**
	 * @return the {@link #encoding}
	 */
	public static String getEncoding() {
		return encoding;
	}

	/**
	 * Sets both {@link #encoding} and {@link #charset}. Package-private visibility for unit testing.
	 * @param encoding new value for {@link #encoding}; sets {@value #DEFAULT_ENCODING} if null  
	 */
	static void setEncoding(String encoding) {
		if (encoding == null)
			encoding = DEFAULT_ENCODING;
		else
			encoding = encoding.toUpperCase();
		if (encoding.equals(LogFormatUtils.encoding))
			return;
		LogFormatUtils.encoding = encoding;
		charset = Charset.forName(LogFormatUtils.encoding);
	}

	/**
	 * Write a string in the {@link #charset} encoding.
	 * @param s
	 * @param os
	 * @throws IOException
	 */
	public static void write(String s, OutputStream os)
			throws IOException {
		ByteBuffer bytes = charset.encode(s);
		os.write(bytes.array(), 0, bytes.limit());
	}
}
