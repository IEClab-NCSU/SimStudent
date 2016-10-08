/**
 * Copyright 2007 Carnegie Mellon University.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.rmi.server.UID;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pslc.logging.ContextMessage;
import edu.cmu.pslc.logging.OliDiskLogger;
import edu.cmu.pslc.logging.ToolMessage;
import edu.cmu.pslc.logging.TutorMessage;
import edu.cmu.pslc.logging.element.DatasetElement;
import edu.cmu.pslc.logging.element.LevelElement;
import edu.cmu.pslc.logging.element.MetaElement;
import edu.cmu.pslc.logging.element.ProblemElement;
import edu.cmu.pslc.logging.element.SkillElement;

/**
 * @author sewall
 *
 */
public class RumbleBlocksLog2StudentLevelRows {

	/** Format of timestamps. */
	static DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	/** For selecting actions that generate tool_messages. */
	private static final Pattern saiPattern =
			Pattern.compile("^(Add_Flower|Add_Egg|Place|Remove|Block|Start_Balance|Level_End).*");	
	
	/** Value that Excel functions interpret as "not available". */
	static final String NA = "#N/A";
	
	/**
	 * Abstraction for single-line processor.
	 */
	interface LineProcessor {

		/** XML tag name for user identifier. */
		final String USER_ID = "UserID";

		/**
		 * Process a single line of log data.
		 * @param elementText
		 * @param xmlOffset
		 * @return
		 */
		String processElementText(String elementText, int xmlOffset)
				throws Exception;

		/**
		 * Generate final results and clean up.
		 */
		void completeProcessing();
	}

	/**
	 * Splits raw log file into single file per user.
	 */
	class SplitRawLogByUserID implements LineProcessor {

		static final String CMD = "splitFileByUserID";
		
		/** Files for students. */
		private Map<String, PrintWriter> studentFiles = new HashMap<String, PrintWriter>();
		
		/**
		 * Constructor adheres to
		 * {@link RumbleBlocksLog2StudentLevelRows#createProcessor(String, String[])}
		 * @param args command-line arguments (not used)
		 */
		SplitRawLogByUserID(String[] args) {}

		/**
		 * Parse the text and write it to a file named for the &lt;UserID&gt; element. 
		 * @param elementText
		 * @param xmlOffset offset of XML element
		 * @return null on success; else diagnostic description
		 */
		public String processElementText(String elementText, int xmlOffset) {
			final String tag0 = "<UserID>", tag1 = "</UserID>";
			final int tag0Len = tag0.length();

			int s = elementText.indexOf(tag0)+tag0Len;
			int e = elementText.indexOf(tag1);
			if(s < tag0Len || e < s)
				return "missing "+tag0+" or "+tag1;

			String userID = elementText.substring(s, e);
			if(userID.length() < 1)
				return "empty userID";

			PrintWriter writer = studentFiles.get(userID);
			try {
				if(writer == null) {
					File file = new File(userID);
					writer = new PrintWriter(new FileOutputStream(file));
					studentFiles.put(userID, writer);
				}
				writer.println(elementText);
			} catch(IOException ioe) {
				ioe.printStackTrace();
				return "Error writing file for user "+userID;
			}
			return null;  // success
		}

		/**
		 * Close the streams in {@link #studentFiles}.
		 */
		public void completeProcessing() {
			for (Map.Entry<String, PrintWriter> fileEntry : studentFiles.entrySet()) {
				String userID = fileEntry.getKey();
				try {
					fileEntry.getValue().close();
				} catch(Exception e) {
					System.err.println("Error closing file for user "+userID);
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Parsed level name to split out tier, level.
	 */
	static class TierLevel {
		/** Matches "Tier N, order M". */
		private static final Pattern TierOrder =
				Pattern.compile(" *Tier +([0-9]+) *, *order +([0-9]+) *", Pattern.CASE_INSENSITIVE);
		/** Matches <i>action</i> (K of N), Tier M", where <i>action</i> is Predict, Hypothesis or Explanation. */
		private static final Pattern PredictHypothesisExplanationTier =
				Pattern.compile(" *(Predict|Hypothesis|Explanation) +\\(([^)]+)\\) *, *Tier +([0-9]+) *", Pattern.CASE_INSENSITIVE);;
		final String combinedName;
		final String tier;
		final String level;
		private String inquiryStep;
		TierLevel(String combinedName) {
			this.combinedName = combinedName;
			Matcher m;
			if((m=TierOrder.matcher(combinedName)).matches()) {
				tier = m.group(1);
				level = m.group(2);
			} else if((m=PredictHypothesisExplanationTier.matcher(combinedName)).matches()) {
				tier = m.group(3);
				level = m.group(1)+" "+m.group(2);
				inquiryStep = m.group(1);
			} else {
				String[] parsedName = combinedName.split("_");
				tier = (parsedName.length > 1 ? parsedName[0] : "");
				level = parsedName[parsedName.length-1];
			}
		}
	}
	
	/** Constant for answering incorrectly, losing a level, etc. */
	private static final String FAILURE = "Failure";

	/** Constant for answering correctly, winning a level, etc. */
	private static final String SUCCESS = "Success";

	/** Tell whether a string contains {@value #SUCCESS} or {@value #FAILURE} (case-insensitive). */
	private static final Pattern SuccessOrFailure =
			Pattern.compile(".*("+SUCCESS+"|"+FAILURE+").*", Pattern.CASE_INSENSITIVE);

	/**
	 * Create a matrix with one row per student and one column per level.
	 */
	class StudentLevelRows implements LineProcessor {

		static final String CMD = "levelRows";

		/**
		 * Details about a student's performance on a level in a session.
		 */
		class LevelRow {
			/** Identifies the exercise. */
			final String levelName;
			/** GUID for student session. */
			String sessionID;
			/** Tier (difficulty or other category). */
			String tier;
			/** "Level" or exercise name within tier. */
			String level;
			/** Ordinal number of this trial in the current tier. */
			int trialInTier = 0;
			/** Ordinal number of this trial in the current level. */
			int trialInLevel = 0;
			/** Timestamp at Level_Start. */
			Date startTime;
			/** Timestamp at Level_End. */
			Date endTime;
			/** "Success" or "Failure". Initialized "#N/A" for compatibility with Excel's functions. */
			String success = NA;
			/** Timestamp at Start_Balance, Earthquake_Start, etc. */
			Date simStartTime;
			/** Timestamp at Earthquake_End, etc. */
			Date simStopTime;
			/** A verbal question asked of the student. */
			String question;
			/** Student's response to the question. */
			String answer;
			/** Number of Place actions in this level. */
			int nPlace = 0;
			/** Number of Remove actions in this level. */
			int nRemove = 0;
			/** Beanstalk: number of times student added a flower. */
			public int nAddFlower;
			/** Beanstalk: number of times student asked Chicken to add an egg. */
			public int nAddEgg;
			/** Beanstalk: total number of times help requested (perhaps by system as well as user). */
			public int nHelp = 0;
			/** Beanstalk: number of times help requested by system. */
			public int nUnrequestedHelp = 0;
			/** Beanstalk: number of times student asked Chicken for help. */
			public int nChickenHelp ;
			/** Beanstalk: number of times student asked Crow for help. */
			public int nCrowHelp;
			/** Beanstalk: number of times student asked for help without asking Chicken or Crow. */
			public int nNoSELHelp;
			/** Individual student or game actions. */
			List<SAI> saiList = new LinkedList<SAI>();
			/** Help statements or other narration. */
			StopAddList<String> adviceList = new StopAddList(); 
			
			/** A list with a flag to stop adding. */
			class StopAddList<T> extends LinkedList<T> {
				private boolean mutable = true;
				void setMutable(boolean m) { mutable = m; }
				public boolean add(T s) { if(mutable) super.add(s); return mutable; }
			}
			
			/**
			 * @param levelName value for {@link #levelName}
			 */
			LevelRow(String levelName) {
				TierLevel tl = new TierLevel(levelName);
				this.levelName = levelName;
				this.tier = tl.tier;
				this.level = tl.level;
			}
		}

		/**
		 * All data on a single student.
		 */
		class Student {
			final String userID;
			TierLevel currentTL = new TierLevel("noTier_noLevel");
			int trialInTier = 0;
			int trialInLevel = 0;
			LevelRow currentLevel;
			Set<String> levels = new HashSet<String>();
			List<LevelRow> levelRows = new ArrayList<LevelRow>();
			private String levelSequence;
			private String gameVersion;
			private String platform;
			private String isWebPlayer;
			private String age;
			private String gender = NA;

			Student(String userID) {
				this.userID = userID;
			}

			/**
			 * Parse demographic info from a log entry and fill in the proper fields.
			 * @param input &lt;Input&gt; tag contents
			 */
			public void setDemographics(String input) {
				if(input == null)
					return;
				String[] demoInfo = input.split(";");
				for(String demoAttr : demoInfo) {
					String label;
					if(demoAttr.toLowerCase().startsWith(label="version="))
						gameVersion = demoAttr.substring(label.length());
					else if(demoAttr.toLowerCase().startsWith(label="platform="))
						platform = demoAttr.substring(label.length());
					else if(demoAttr.toLowerCase().startsWith(label="webplayer="))
						isWebPlayer = demoAttr.substring(label.length());
					else if(demoAttr.toLowerCase().startsWith(label="age="))
						age = demoAttr.substring(label.length());
					else if(demoAttr.toLowerCase().startsWith(label="gender="))
						gender = demoAttr.substring(label.length()); 
				}
			}
		}

		/** Command-line prefix for level regexes. */
		private static final String LEVEL_ARG = "--level=";

		/** Regular expressions to test level names for inclusion. */
		List<Pattern> levelPatterns = new LinkedList<Pattern>();

		/** All levels found, excluding those not of interest. */
		Set<String> levels = new LinkedHashSet<String>();

		/** All students' data: key is userID. */
		SortedMap<String, Student> students = new TreeMap<String, Student>();

		/** Parser for XML elements. */
		private SAXBuilder builder = new SAXBuilder(false);

		/**
		 * Arguments passed from the command line: <ul>
		 *   <li>--level=<i>regex</i> : include levels matching this regular expression;</li>
		 * </ul>
		 * @param args command-line arguments
		 */
		StudentLevelRows(String[] args) {
			for(String arg : args) {
				if(arg.toLowerCase().startsWith(LEVEL_ARG))
					levelPatterns.add(Pattern.compile(arg.substring(LEVEL_ARG.length())));
			}
			if(levelPatterns.size() < 1)  // no patterns specified => include all levels
				levelPatterns.add(Pattern.compile(".*"));
		}

		/**
		 * Collect data for each student.
		 * @param elementText
		 * @param xmlOffset
		 * @return diagnostic; null indicates success
		 */
		public String processElementText(String elementText, int xmlOffset)
				throws Exception {
			Element elt = null;
			try {
				elt = createElement(elementText.substring(xmlOffset));
			} catch(Exception e) {
				e.printStackTrace();
				return "error in XML parsing";
			}
			if (debug.contains("line"))
				System.err.printf("%6d: elementText=%s\n", lineNo, elementText);

			String userID = getChildText(USER_ID, elt);
			if (userID == null || userID.length() < 1) {
				String errMsg = "null or empty userID \""+userID+"\"";
				System.err.printf("%6d: %s in element\n%s\n", lineNo, errMsg, elementText);
				return errMsg;
			}
			userID = userID.toLowerCase().trim();
			Student student = students.get(userID);
			if(student == null) {
				student = new Student(userID);
				students.put(userID, student);
			}

			String action = getChildText("Action", elt);
			if(!getSessionInfo(student, action, elt))
				student.currentLevel = null;                  // new session: close old record
			boolean levelWanted = recordLevel(student, action, elt);
			if(levelWanted)
				recordStudentData(student, action, elt);
			return null;
		}

		/**
		 * Retrieve data that appears only once per session, viz.,<ul>
		 *   <li>{@link RumbleBlocksLog2StudentLevelRows.StudentLevelRows.Student#levelSequence levelSequence}
		 *     from Session_SequenceLoaded records</li>
		 *   <li>student demographic info from Session_SystemAndDemographics records</li>
		 * </ul>
		 * @param student object to update
		 * @param action action read from the record
		 * @param elt parsed record
		 * @return true if sessionID in record matches current one in student object
		 */
		private boolean getSessionInfo(Student student, String action, Element elt) {
			String sessionID = getChildText("SessionID", elt);
			boolean result = (sessionID == null || student.currentLevel == null ? false :
					sessionID.equalsIgnoreCase(student.currentLevel.sessionID));
			if(!result) {
				if(student.currentLevel != null && !("Session_Start".equalsIgnoreCase(action)))
					System.err.printf("%6d. Unmatched sessionID %s not on Session_Start record\n", lineNo, sessionID);
			}
			String input = getChildText("Input", elt);
			if("Session_SequenceLoaded".equalsIgnoreCase(action)) {
				student.levelSequence = input;
			} else if("Session_SystemAndDemographics".equalsIgnoreCase(action)) {
				student.setDemographics(input);
			}
			return result;
		}

		/**
		 * Populate the {@link Student#currentLevel} record with data from the 
		 * current log line.
		 * @param student student record to update
		 * @param action Action element already parsed
		 * @param elt entire line, already parsed
		 * @throws ParseException
		 */
		private void recordStudentData(Student student, String action, Element elt)
				throws ParseException {
			dateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));  // logging time is GMT
			Date time = dateFmt.parse(getChildText("Time", elt));
			student.currentLevel.sessionID = getChildText("SessionID", elt);
			String selection = getChildText("Selection", elt);
			String input = getChildText("Input", elt);
			if (debug.contains("fields"))
				System.err.printf("%6d: time %s s %-14s a %-18s, i %-18s\n", lineNo,
						dateFmt.format(time), selection, action, input);
			if("Level_End".equalsIgnoreCase(action)) {
				student.currentLevel.endTime = time;
				String level = input;
				int comma = input.indexOf(',');
				Matcher m = SuccessOrFailure.matcher(input);
				if(comma >= 0 && m.matches()) {
					student.currentLevel.success =
							(m.group(1).equalsIgnoreCase(SUCCESS) ? SUCCESS : FAILURE);
					level = input.substring(0,comma);
				}
				if(!student.currentLevel.levelName.equals(level))
					System.err.printf("line %6d, userID %s: no %s Level_Start before Level_End\n",
							lineNo, student.userID, level);
				else
					student.currentLevel.adviceList.setMutable(false);
			} else if("PausedButton".equalsIgnoreCase(selection)) {
				student.currentLevel.endTime = time;
				if(student.currentLevel.success == NA)
					student.currentLevel.success = "Paused";
			} else if("BackButton".equalsIgnoreCase(selection) ||
					"NextLevelButton".equalsIgnoreCase(selection)) {
				if(student.currentLevel != null) {
					if(student.currentLevel.endTime == null)
						student.currentLevel.endTime = time;
					student.currentLevel = null;   // not currently in any level
				}
			} else if("Level_Start".equalsIgnoreCase(action)) {
				student.currentLevel.startTime = time;
			} else if("Level_Restart".equalsIgnoreCase(action)) {
				student.currentLevel.startTime = time;
			} else if("Start_Balance".equalsIgnoreCase(action)) {
				student.currentLevel.simStartTime = time;
			} else if("Earthquake_Start".equalsIgnoreCase(action)) {
				student.currentLevel.simStartTime = time;
			} else if("Earthquake_End".equalsIgnoreCase(action)) {
				student.currentLevel.simStopTime = time;
			} else if("Comparison_Selection".equalsIgnoreCase(action)) {
				student.currentLevel.success =
						("Stable".equalsIgnoreCase(selection) ? SUCCESS : FAILURE);
			} else if("End_State".equalsIgnoreCase(action)) {
				student.currentLevel.success = input;
			} else if("Final_State".equalsIgnoreCase(action)) {
				student.currentLevel.success = input;
			} else if("Question_Asked".equalsIgnoreCase(action)) {
				student.currentLevel.question = input;
			} else if("Question_Answered".equalsIgnoreCase(action)) {
				student.currentLevel.answer = input;
			} else if("Placed".equalsIgnoreCase(action)) {
				student.currentLevel.nPlace++;
			} else if("Removed".equalsIgnoreCase(action)) {
				student.currentLevel.nRemove++;
			} else if("won".equalsIgnoreCase(action)) {
				student.currentLevel.success = SUCCESS;
				student.currentLevel.simStartTime = time;
			} else if("lost".equalsIgnoreCase(action)) {
				student.currentLevel.success = FAILURE;
				student.currentLevel.simStartTime = time;
			} else if(action != null && action.toLowerCase().endsWith("-done")) {
				student.currentLevel.simStartTime = time;
			} else if("correct".equalsIgnoreCase(action)) {
				student.currentLevel.success = SUCCESS;
				student.currentLevel.answer = input;
			} else if("wrong".equalsIgnoreCase(action)) {
				student.currentLevel.success = FAILURE;
				student.currentLevel.answer = input;
			} else if("Add_Flower".equalsIgnoreCase(action)) {
				student.currentLevel.nAddFlower++;
			} else if("Add_Egg".equalsIgnoreCase(action)) {
				student.currentLevel.nAddEgg++;
			} else if("FailsTriggerHelpMode".equalsIgnoreCase(action)) {
				student.currentLevel.nHelp++;
				student.currentLevel.nUnrequestedHelp++;
			} else if("Clicked".equalsIgnoreCase(action) && "ChickenButton".equalsIgnoreCase(selection)) {
				student.currentLevel.nHelp++;
				student.currentLevel.nChickenHelp++;
			} else if("Clicked".equalsIgnoreCase(action) && "CrowllButton".equalsIgnoreCase(selection)) {
				student.currentLevel.nHelp++;
				student.currentLevel.nCrowHelp++;
			} else if("Clicked".equalsIgnoreCase(action) && "HelpButtonForNoSEL".equalsIgnoreCase(selection)) {
				student.currentLevel.nHelp++;
				student.currentLevel.nNoSELHelp++;
			} else if("Said".equalsIgnoreCase(action)) {
				student.currentLevel.adviceList.add(selection+": "+input);
			} else if("Clicked".equalsIgnoreCase(action) && "RemoveButton".equalsIgnoreCase(selection)) {
				student.currentLevel.nRemove++;
			} else if("Session_SystemAndDemographics".equalsIgnoreCase(action)) {
				student.setDemographics(input);
			}
			Matcher saim = saiPattern.matcher(action);
			if(debug.contains("sai"))
				System.err.println("saiPattern "+saiPattern+" = "+action+" saim.matches() "+saim.matches());
			if (saim.matches())
				student.currentLevel.saiList.add(new SAI(selection, action, input, time));
		}

		/**
		 * Grab the level from Level_Start entries, set {@link Student#currentLevel}.
		 * @param student
		 * @param action
		 * @param elt
		 * @return true if we're interested in data from this record; else false
		 */
		private boolean recordLevel(Student student, String action, Element elt) {
			String levelName = null;
			
			if("Level_Start".equalsIgnoreCase(action) || "Level_Restart".equalsIgnoreCase(action)) {
				String input = getChildText("Input", elt);
				for(Pattern lp : levelPatterns) {
					Matcher m = lp.matcher(input);
					if(m.find()) {
						levelName = input;
						break;
					}
				}
				if(levelName == null) {              // this level not wanted
					student.currentLevel = null;
					return false;
				}
				if(student.currentLevel != null) {
					if(student.currentLevel.endTime == null) {
						if(student.currentLevel.levelName.equals(levelName))
							return true;                 // duplicate Level_Start record
						System.err.printf("line %6d, userID %s: no %s Level_End before %s Level_Start\n",
								lineNo, student.userID, student.currentLevel.levelName, levelName);
					}
				}
				levels.add(levelName);
				student.currentLevel = new LevelRow(levelName);
				TierLevel tl = new TierLevel(levelName);
				if (tl.tier.equalsIgnoreCase(student.currentTL.tier))
					student.currentLevel.trialInTier = ++student.trialInTier;
				else
					student.currentLevel.trialInTier = student.trialInTier = 1;
				if (tl.level.equalsIgnoreCase(student.currentTL.level))
					student.currentLevel.trialInLevel = ++student.trialInLevel;
				else
					student.currentLevel.trialInLevel = student.trialInLevel = 1;
				student.currentTL = tl;
				student.levels.add(levelName);
				student.levelRows.add(student.currentLevel);
				return true;
			}
			
			return student.currentLevel != null;  // not null => record this level's results
		}

		/**
		 * Get the text of a child element.
		 * @param tag
		 * @param elt
		 * @return elt.getChild(tag).getText()
		 */
		private String getChildText(String tag, Element elt) {
			Element childElt = elt.getChild(tag);
			if(debug.contains("xml"))
				System.err.printf("getChildText(%s,<elt>)=>%s\n", tag, xmlo.outputString(childElt));
			String result = (childElt == null ? null : childElt.getText());
			return result;
		}

		/**
		 * Parse a line of text into an XML element.
		 * @param xmlText
		 * @return
		 * @throws Exception
		 */
		private Element createElement(String xmlText) throws Exception {
			Reader strIn = new StringReader(xmlText);
			if(debug.contains("xml")) System.err.printf("createElement(%s)\n", xmlText);
			Document doc = builder.build(strIn);
			return doc.getRootElement();
		}

		/**
		 * Generate student matrix.
		 * @see RumbleBlocksLog2StudentLevelRows.LineProcessor#completeProcessing()
		 */
		public void completeProcessing() {
			dateFmt.setTimeZone(TimeZone.getDefault());  // local time for output
			
			System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\r\n",
				       "session", "user", "tier", "level",
				       "trialInTier", "trialInLevel",
				       "levelStart", "levelEnd", "endState",
				       "ms", "balanceStart", "questionAsked", "questionAnswer", "places", "removes");

			for(Student student : students.values()) {
				String userID = student.userID;
				for(LevelRow lr : student.levelRows) {
					String startTime = (lr.startTime != null ? dateFmt.format(lr.startTime) : "");
					String endTime = (lr.endTime != null ? dateFmt.format(lr.endTime) : "");
					String simStartTime = (lr.simStartTime != null ? dateFmt.format(lr.simStartTime) : "");
					String question = (lr.question != null ? lr.question : "");
					String answer = (lr.answer != null ? lr.answer : "");
					String success = (SUCCESS.equalsIgnoreCase(lr.success) ? "1"
							: (FAILURE.equalsIgnoreCase(lr.success) ? "0" : lr.success) );
					long ms = -1;
					if (simStartTime.length() > 0 && startTime.length() > 0)
						ms = lr.simStartTime.getTime() - lr.startTime.getTime();
					System.out.printf("%s\t%s\t%s\t%s\t%d\t%d\t%s\t%s\t%s\t%d\t%s\t%s\t%s\t%d\t%d\r\n",
							lr.sessionID, userID, lr.tier, lr.level,
							lr.trialInTier, lr.trialInLevel,
							startTime, endTime, success,
							ms, simStartTime, question, answer, lr.nPlace, lr.nRemove);
				}
			}
		}
	}
	
	/**
	 * Class to create context, tool and tutor messages for DataShop. 
	 */
	class LevelRowsToDataShop extends StudentLevelRows {

		/** Command-line switch for this class. */
		public static final String CMD = "DataShop";
        
		/** Time zone in log timestamps. */
		private static final String LOG_TIME_ZONE = "UTC";

		/** Labels for command-line arguments. */
        private static final String CLASSNAME_ARG = "--classname=";
        private static final String SCHOOL_ARG = "--school=";
        private static final String PERIOD_ARG = "--period=";
        private static final String INSTRUCTOR_ARG = "--instructor=";
        private static final String DATASET_ARG = "--dataset=";
        private static final String OUTPUT_ARG = "--output=";

    	/** Class name for context message. */
        private String className;
        
    	/** School name for context message. */
        private String school;
        
    	/** Period name for context message. */
        private String period;
        
    	/** Instructor name for context message. */
        private String instructor;
        
    	/** Dataset name for context message. */
    	private String dataset;
    	
    	/** OLI disk file. Suitable for use with DiskImporter. */
    	private String oliFilename = "datashop.log";

    	/** For writing DataShop messages to disk for input to DiskImporter. */
    	private OliDiskLogger oliDiskLogger;
		
		LevelRowsToDataShop(String[] args) {
			super(args);
			setDatasetParams(args);
			File oliFile = new File(oliFilename);  // setDatasetParams() sets oliFilename
			if(oliFile.exists() && oliFile.length() > 0)
				throw new IllegalArgumentException("Output file "+oliFilename+" exists. Will not overwrite.");
	        oliDiskLogger = OliDiskLogger.create(oliFilename);
		}

		/**
		 * Set {@link #dataset}, {@link #oliFilename}, {@link #className}, {@link #school},
		 * {@link #period}, {@link #instructor} from command-line arguments.
		 * @param args command-line arguments
		 */
		public void setDatasetParams(String[] args) {
			for(String arg : args) {
				if(arg.toLowerCase().startsWith(DATASET_ARG))
					dataset = (arg.substring(DATASET_ARG.length()));
				else if(arg.toLowerCase().startsWith(OUTPUT_ARG))
					oliFilename = (arg.substring(OUTPUT_ARG.length()));
				else if(arg.toLowerCase().startsWith(CLASSNAME_ARG))
					className = (arg.substring(CLASSNAME_ARG.length()));
				else if(arg.toLowerCase().startsWith(SCHOOL_ARG))
					school = (arg.substring(SCHOOL_ARG.length()));
				else if(arg.toLowerCase().startsWith(PERIOD_ARG))
					period = (arg.substring(PERIOD_ARG.length()));
				else if(arg.toLowerCase().startsWith(INSTRUCTOR_ARG))
					instructor = (arg.substring(INSTRUCTOR_ARG.length()));
			}
			if (dataset == null)
				throw new IllegalArgumentException("Must set a dataset name.");
		}

		/**
		 * Create and write a context message, tool message and tutor message for each row.
		 * @see RumbleBlocksLog2StudentLevelRows.StudentLevelRows#completeProcessing()
		 */
		public void completeProcessing() {

			for(Student student : students.values()) {
				String userID = student.userID;
				String lastSessionID = "not a real session id";

				for(LevelRow lr : student.levelRows) {
					String startTime = (lr.startTime != null ? dateFmt.format(lr.startTime) : "");
					String endTime = (lr.endTime != null ? dateFmt.format(lr.endTime) : "");
					String simStartTime = (lr.simStartTime != null ? dateFmt.format(lr.simStartTime) : "");
					String question = (lr.question != null ? lr.question : "");
					String answer = (lr.answer != null ? lr.answer : "");

					if(!lastSessionID.equalsIgnoreCase(lr.sessionID)) {
						oliDiskLogger.logSession(userID, lr.sessionID);					
						lastSessionID = lr.sessionID;
					}
					MetaElement meta = new MetaElement(userID, lr.sessionID, startTime, LOG_TIME_ZONE);
					ContextMessage cm = ContextMessage.create("START", meta);  // "START" recommended by DS 
					cm.setContextMessageId(makeContextMessageId());
					if (className != null)
						cm.setClassName(className);
					if (school != null)
						cm.setSchool(school);
					if (period != null)
						cm.setPeriod(period);
					if (instructor != null)
						cm.addInstructor(instructor);
					ProblemElement pe = new ProblemElement(lr.levelName);
					LevelElement tle = new LevelElement("tier", lr.tier, pe);
					cm.setDataset(new DatasetElement(dataset, tle));
					oliDiskLogger.log(cm);

					ToolMessage lastToolMsg = null;
					SAI levelEndSAI = null;
					for (SAI sai : lr.saiList) {
						if(sai.action == null) {
							System.err.printf("null action in SAI %s for userID \"%s\"\n", sai, userID);
							continue;
						}
						meta.setTime(dateFmt.format(sai.time));
						if("Level_End".equalsIgnoreCase(sai.action))
							levelEndSAI = sai;
						else {
							ToolMessage tlm = ToolMessage.create(cm);
							if(sai.action.toLowerCase().endsWith("_at_start")
									|| "Start_Balance".equalsIgnoreCase(sai.action))
								tlm.setAsAttempt("tutor-performed");
							else
								tlm.setAsAttempt();
							tlm.addSai(sai.selection, sai.action, sai.input);
							oliDiskLogger.log(tlm);
							lastToolMsg = tlm;         // save for tutor message
						}
					}
					if(lastToolMsg != null && levelEndSAI != null) {
						TutorMessage trm = TutorMessage.create(lastToolMsg);
						if(SUCCESS.equalsIgnoreCase(lr.success) || NA.equalsIgnoreCase(lr.success))
							trm.setAsCorrectAttemptResponse();
						else
							trm.setAsIncorrectAttemptResponse();
						trm.addSai(levelEndSAI.selection, levelEndSAI.action, levelEndSAI.input);
						trm.addSkill(new SkillElement(lr.tier, dataset));
						trm.addTutorAdvice(lr.adviceList.toString());
						oliDiskLogger.log(trm);
					}					
				}
			}
		}

		public String makeContextMessageId() {
			return "C" + (new UID()).toString();
		}
				
	}
	
	/**
	 * Class to create a level-row for Rumble Block logs.
	 */
	class BeanstalkLevelRows extends StudentLevelRows {

		static final String CMD = "beanstalkLevelRows"; 

		/**
		 * Arguments passed from the command line: see superclass.
		 * @param args command-line arguments
		 */
		BeanstalkLevelRows(String[] args) {
			super(args);
		}

		/**
		 * Generate list of level rows.
		 * @see RumbleBlocksLog2StudentLevelRows.LineProcessor#completeProcessing()
		 */
		public void completeProcessing() {
			dateFmt.setTimeZone(TimeZone.getDefault());  // local time for output
			
			System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\r\n",
				       "session", "user", "SEL", "Inquiry",
				       "age", "gender",
				       "tier", "level", "trialInTier", "trialInLevel",
				       "levelStart", "levelEnd", "endState",
				       "ms", "balanceStart",
				       "addFlowers", "addEggs", "removes",
				       "helpsRequested", "helpsGiven",
				       "helpContents");

			for(Student student : students.values()) {
				String userID = student.userID;
				String condition = (student.levelSequence == null ? NA : student.levelSequence.toUpperCase());
				int sel = ((condition.contains("SEL") && !condition.contains("NOSEL")) ? 1 : 0);
				int inquiry = ((condition.contains("INQUIRY") && !condition.contains("NOINQUIRY")) ? 1 : 0);
				for(LevelRow lr : student.levelRows) {
					String startTime = (lr.startTime != null ? dateFmt.format(lr.startTime) : NA);
					String endTime = (lr.endTime != null ? dateFmt.format(lr.endTime) : NA);
					String simStartTime = (lr.simStartTime != null ? dateFmt.format(lr.simStartTime) : NA);
					String success = lr.success;
					if(SuccessOrFailure.matcher(lr.success).matches())  // convert success=>1, failure=>0
							success = (SUCCESS.equalsIgnoreCase(lr.success) ? "1" : "0");
					String ms = NA;
					if (lr.simStartTime != null && lr.startTime != null)
						ms = Long.toString(lr.simStartTime.getTime() - lr.startTime.getTime());
					System.out.printf("%s\t%s\t%d\t%d\t%s\t%s\t%s\t%s\t%d\t%d\t%s\t%s\t%s\t%s\t%s\t%d\t%d\t%d\t%d\t%d\t%s\r\n",
							lr.sessionID, userID, sel, inquiry,
							student.age, genderAsInt(student.gender),
							lr.tier, lr.level, lr.trialInTier, lr.trialInLevel,
							startTime, endTime, success,
							ms, simStartTime, lr.nAddFlower, lr.nAddEgg, lr.nRemove,
							lr.nChickenHelp+lr.nCrowHelp+lr.nNoSELHelp, lr.nHelp,
							lr.adviceList.toString());
				}
			}
		}
	}
	
	/**
	 * Class to create a level-row for Rumble Block logs.
	 */
	class RumbleBlocksLevelRows extends StudentLevelRows {

		static final String CMD = "rbLevelRows"; 
		
		/** Command-line option to supply the test day. */
		private static final String TEST_DAY_ARG = "--testday=";
		
		private String testDay = "";

		/**
		 * Arguments passed from the command line: <ul>
		 *   <li>--testday=<i>day</i> : fixed value for the \"day\" column;</li>
		 * </ul>
		 * @param args command-line arguments
		 */
		RumbleBlocksLevelRows(String[] args) {
			super(args);
			for(String arg : args) {
				if(arg.toLowerCase().startsWith(TEST_DAY_ARG))
					testDay = arg.substring(TEST_DAY_ARG.length());
			}
		}

		/**
		 * Generate list of level rows.
		 * @see RumbleBlocksLog2StudentLevelRows.LineProcessor#completeProcessing()
		 */
		public void completeProcessing() {
			dateFmt.setTimeZone(TimeZone.getDefault());  // local time for output
			
			System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\r\n",
				       "session", "user", "grade", "level",
				       "day",
				       "levelStart", "levelEnd", "endState",
				       "seconds", "earthquakeStart", "earthquakeEnd");

			for(Student student : students.values()) {
				String userID = student.userID;
				for(LevelRow lr : student.levelRows) {
					String startTime = (lr.startTime != null ? dateFmt.format(lr.startTime) : "");
					String endTime = (lr.endTime != null ? dateFmt.format(lr.endTime) : "");
					String success = (lr.success != null ? lr.success : "");
					String simStartTime = (lr.simStartTime != null ? dateFmt.format(lr.simStartTime) : "");
					String simStopTime = (lr.simStopTime != null ? dateFmt.format(lr.simStopTime) : "");
					long secs = -1;
					if (simStartTime.length() > 0 && startTime.length() > 0)
						secs = Math.round((lr.simStartTime.getTime()-lr.startTime.getTime())/1000);
					System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%d\t%s\t%s\r\n",
							lr.sessionID, userID, userIDtoGrade(userID), lr.levelName,
							testDay,
							startTime, endTime, success,
							secs, simStartTime, simStopTime);
				}
			}
		}
		
		/**
		 * Derive a student's grade level from the 2nd character of his or her userID. 
		 * @param userID
		 * @return grade level, with "0" for kindergarten, "1" for 1st grade, ...
		 */
		private String userIDtoGrade(String userID) {
			if(userID.length()<2) {
				System.err.printf("cannot derive grade from userID \"%s\"\n", userID);
				return "";
			}
			char ch = Character.toLowerCase(userID.charAt(1));
			if(ch == 'k')
				return "0";
			else
				return Character.toString(ch);
		}
	}
	
	/** For holding a single selection-action-input. */
	class SAI {
		String selection;
		String action;
		String input;
		Date time;
		public SAI(String selection, String action, String input, Date time) {
			this.selection = selection;
			this.action = action;
			this.input = input;
			this.time = time;
		}
		public String toString() {
			return "["+selection+","+action+","+input+","+dateFmt.format(time)+"]";
		}
	}
	
	/** Prefix to a processor name on the command line. */
	static final String CMD_ARG = "--cmd=";
	
	/** Prefix to a processor name on the command line. */
	static final String DEBUG_ARG = "--debug=";
	
	/** global line no. */
	static int lineNo = 0;
	
	/** Debug codes. */
	static Set<String> debug = new HashSet<String>();
	
	/** For XML debugging. */
	static final XMLOutputter xmlo = new XMLOutputter(Format.getPrettyFormat().setIndent("  ")
			.setEncoding("UTF-8").setOmitEncoding(true)
			.setOmitDeclaration(true).setLineSeparator("\r\n"));

	/**
	 * Never returns (calls {@link System#exit(int)}). Prints help message to stderr.  
	 * @param msg diagnostic to print before usage
	 */
	private static void usageExit(String msg) {
		String className = RumbleBlocksLog2StudentLevelRows.class.getSimpleName();
		if(msg != null && msg.length() > 0)
			System.err.print(className+": "+msg+" ");
		System.err.println("Usage (reads stdin):\n"+
			"  java -cp ... "+className+" [debug] cmd [args] < input\n"+
			"where--\n"+
			"  debug is "+DEBUG_ARG+"tag,... : tags are xml, cmd, etc. for particular traces;\n"+
			"  cmd is one of\n"+
			"    "+CMD_ARG+SplitRawLogByUserID.CMD+" : splits raw log into 1 file per user;\n"+
			"    "+CMD_ARG+BeanstalkLevelRows.CMD+" : generate 1 row per student per Dec 2012 Beanstalk level;\n"+
			"    "+CMD_ARG+StudentLevelRows.CMD+" : generate 1 row per student per May 2012 Beanstalk level;\n"+
			"    "+CMD_ARG+RumbleBlocksLevelRows.CMD+" : generate 1 row per student per RumbleBlocks level;\n"+
			"    "+CMD_ARG+LevelRowsToDataShop.CMD+" : generate a DataShop log;\n"+
			"  input is the log data: reads stdin.\n"+
			"For commands "+StudentLevelRows.CMD+" and "+RumbleBlocksLevelRows.CMD+", optional arguments are:\n"+
			"  "+RumbleBlocksLevelRows.TEST_DAY_ARG+"day : day is test day 1, 2, etc.;\n"+
			"  "+StudentLevelRows.LEVEL_ARG+"regex : regex is a regular expression matching the game levels to include;\n"+
			"    (this argument can be used repeatedly); if absent, include all levels.\n"+
			"For "+LevelRowsToDataShop.CMD+", additional arguments are:\n"+
			"  "+LevelRowsToDataShop.DATASET_ARG+"dataset name for DataShop;\n"+
			"  "+LevelRowsToDataShop.OUTPUT_ARG+"output file for DiskImporter.\n"+
			"For example:\n"+
			"  java -cp ... "+className+" "+CMD_ARG+SplitRawLogByUserID.CMD+";\n"+
			"  java -cp ... "+className+" "+CMD_ARG+RumbleBlocksLevelRows.CMD+" "+RumbleBlocksLevelRows.TEST_DAY_ARG+"2 "+
			StudentLevelRows.LEVEL_ARG+"\"mini.*\" "+StudentLevelRows.LEVEL_ARG+"\".*noCheck\";\n"
			); 
		System.exit(2);
	}

	/**
	 * Convert males to "1", females to "0". Otherwise return {@value #NA}.
	 * @param gender "M[ale]" or "F[emale]"
	 * @return "1", "0", or {@value #NA}
	 */
	public static String genderAsInt(String gender) {
		if(gender == null)
			return NA;
		gender = gender.toUpperCase().trim();
		if(gender.startsWith("M"))
			return "1";
		else if(gender.startsWith("F"))
			return "0";
		else
			return NA;
	}

	/**
	 * Reads stdin. Argument "--cmd=XXX" determines processing:
	 * @param args: see {@link #usageExit(String)}
	 */
	public static void main(String[] args) {
		RumbleBlocksLog2StudentLevelRows rb = new RumbleBlocksLog2StudentLevelRows(); 
		LineProcessor processor = null;
		for(String arg : args) {
			if (arg.toLowerCase().startsWith("-h") || arg.toLowerCase().startsWith("--h")) {
				usageExit("Help message.");                     // never returns
			} else if (arg.toLowerCase().startsWith(DEBUG_ARG)) {
				for(String tag : arg.substring(DEBUG_ARG.length()).split(","))
					debug.add(tag.toLowerCase());
			} else if(arg.toLowerCase().startsWith(CMD_ARG)) {
				processor = rb.createProcessor(arg.substring(CMD_ARG.length()), args);
			}
		}
		if(processor == null)
			usageExit("Missing or undefined \""+CMD_ARG+"\" argument: cannot create processor.");
		
		BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
		String line;
		try {
			while(null != (line = rdr.readLine())) {
				lineNo++;
				int tabIndex = line.indexOf('\t');  // -1 if no tab => '<' at start of line
				if(line.startsWith("<", tabIndex+1)) {
					String diag = processor.processElementText(line, tabIndex+1);
					if(diag == null)
						continue;
					else
						System.err.printf("Line %6d skipped (%s): %s\n", lineNo, diag, line);
				} else
					System.err.printf("Line %6d skipped (no '<'): %-40s\n", lineNo, line);
			}
		} catch(IOException ioe) {
			System.err.println("I/O error reading stdin at line "+lineNo);
			ioe.printStackTrace();
		} catch(Exception e) {
			System.err.println("I/O error reading stdin at line "+lineNo);
			e.printStackTrace();
		}

		processor.completeProcessing();
	}

	/**
	 * Instantiate a {@link LineProcessor} and pass its constructor the command line.
	 * @param cmd processor choice: must be among <ui>
	 *        <li>{@value SplitRawLogByUserID#CMD}</li>
	 *        <li>{@value StudentLevelRows#CMD}</li>
	 *        </ul>
	 * @param args all command-line arguments
	 * @return processor for lines in file
	 */
	private LineProcessor createProcessor(String cmd, String[] args) {
		if (debug.contains("cmd")) System.err.printf("createProcessor cmd=%s\n", cmd);		
		
		if(SplitRawLogByUserID.CMD.toLowerCase().startsWith(cmd.toLowerCase()))
			return new SplitRawLogByUserID(args); 
		if(BeanstalkLevelRows.CMD.toLowerCase().startsWith(cmd.toLowerCase()))
			return new BeanstalkLevelRows(args);
		if(StudentLevelRows.CMD.toLowerCase().startsWith(cmd.toLowerCase()))
			return new StudentLevelRows(args);
		if(RumbleBlocksLevelRows.CMD.toLowerCase().startsWith(cmd.toLowerCase()))
			return new RumbleBlocksLevelRows(args);
		if(LevelRowsToDataShop.CMD.toLowerCase().startsWith(cmd.toLowerCase()))
			return new LevelRowsToDataShop(args);
		return null;
	}
}
