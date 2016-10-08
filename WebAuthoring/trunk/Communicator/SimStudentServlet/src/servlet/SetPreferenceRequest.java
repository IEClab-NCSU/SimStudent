package servlet;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a request which sends the set preferences of the tutoring interface
 * @author Patrick Nguyen
 *
 */
public class SetPreferenceRequest extends RequestMessage{

	private String logServiceUrl;
	private boolean logToRemoteServer;
	private boolean logToDisk;
	private String logToDiskDirectory;
	private String userGuid;
	private String problemName;
	private String questionFile;
	// Added by Shruti for demonstrating a valueTypeChecker input argument to Backend
	private String argument;
	private String className;
	private String schoolName;
	private String instructorName;
	private String sourceId;
	private String sui;
	private String problemStateStatus;
	private List<Skill> skills;
	private String commShellVersion;
	private String backendDirectory;
	private String backendEntryClass;
	private String driveUrl;
	private String driveToken;
	private List<String> wmes;


	public SetPreferenceRequest(){
		setMessageType("SetPreferences");
		skills = new ArrayList<Skill>();
	}

	/**
	 * Gets the log service url
	 * @return logServiceUrl - the url to which we send log messages
	 */
	public String getLogServiceUrl() {
		return logServiceUrl;
	}
	/**
	 * Sets the log service url
	 * @param logServiceUrl - the url to which we send log messages
	 */
	public void setLogServiceUrl(String logServiceUrl) {
		this.logServiceUrl = logServiceUrl;
	}
	/**
	 * Whether or not we are logging to a remote server
	 * @return logToRemoteServer
	 */
	public boolean isLogToRemoteServer() {
		return logToRemoteServer;
	}
	/**
	 * Sets whether or not we are logging to a remote server
	 * @param logToRemoteServer
	 */
	public void setLogToRemoteServer(boolean logToRemoteServer) {
		this.logToRemoteServer = logToRemoteServer;
	}
	/**
	 * Whether or not we are logging to disk
	 * @return logToDisk
	 */
	public boolean isLogToDisk() {
		return logToDisk;
	}
	/**
	 * Sets whether or not we are logging to disk
	 * @param logToDisk
	 */
	public void setLogToDisk(boolean logToDisk) {
		this.logToDisk = logToDisk;
	}
	/**
	 * Gets the directory on disk we are logging to
	 * @return logToDiskDirectory
	 */
	public String getLogToDiskDirectory() {
		return logToDiskDirectory;
	}
	/**
	 * Sets the directory on disk we are logging to
	 * @param logToDiskDirectory
	 */
	public void setLogToDiskDirectory(String logToDiskDirectory) {
		this.logToDiskDirectory = logToDiskDirectory;
	}

	/**
	 * Gets the user guid
	 * @return userGuid
	 */
	public String getUserGuid() {
		return userGuid;
	}

	/**
	 * Sets the user guid
	 * @param userGuid
	 */
	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
	}

	/**
	 * Gets the problem name
	 * @return problemName
	 */
	public String getProblemName() {
		return problemName;
	}

	/**
	 * Sets the problem name
	 * @param problemName
	 */
	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}

	/**
	 * Gets the question file
	 * @return question file
	 */
	public String getQuestionFile() {
		return questionFile;
	}
	/**
	 * Sets the question file
	 * @param questionFile
	 */
	public void setQuestionFile(String questionFile) {
		this.questionFile = questionFile;
	}
	/**
	 * Gets the class name
	 * @return className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * Sets the class name
	 * @param className
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * Gets the school name
	 * @return school name
	 */
	public String getSchoolName() {
		return schoolName;
	}
	/**
	 * Sets the school name
	 * @param schoolName
	 */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	/**
	 * Gets the instructor name
	 * @return instructorName
	 */
	public String getInstructorName() {
		return instructorName;
	}
	/**
	 * Sets the instructor name
	 * @param instructorName
	 */
	public void setInstructorName(String instructorName) {
		this.instructorName = instructorName;
	}
	/**
	 * Gets the source ID
	 * @return sourceID
	 */
	public String getSourceId() {
		return sourceId;
	}
	/**
	 * Sets the source ID
	 * @param sourceID
	 */
	public void setSourceId(String sourceID) {
		this.sourceId = sourceID;
	}
	/**
	 * Gets the SUI
	 * @return sui
	 */
	public String getSui() {
		return sui;
	}
	/**
	 * Sets the SUI
	 * @param sui
	 */
	public void setSui(String sui) {
		this.sui = sui;
	}
	/**
	 * Gets the problem state status
	 * @return problemStateStatus
	 */
	public String getProblemStateStatus() {
		return problemStateStatus;
	}
	/**
	 * Sets the problem state status
	 * @param problemStateStatus
	 */
	public void setProblemStateStatus(String problemStateStatus) {
		this.problemStateStatus = problemStateStatus;
	}
	/**
	 * Gets the skills
	 * @return skills
	 */
	public List<Skill> getSkills() {
		return skills;
	}
	/**
	 * Sets the skills
	 * @param skills
	 */
	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}
	/**
	 * Adds {@code skill} to the list of skills
	 * @param skill
	 */
	public void addSkill(Skill skill){
		skills.add(skill);
	}
	/**
	 * Gets the Comm Shell version
	 * @return commShellVersion
	 */
	public String getCommShellVersion() {
		return commShellVersion;
	}
	/**
	 * Sets the Comm Shell version
	 * @param commShellVersion
	 */
	public void setCommShellVersion(String commShellVersion) {
		this.commShellVersion = commShellVersion;
	}

	public String getBackendDirectory() {
		return backendDirectory;
	}

	public void setBackendDirectory(String backendDirectory) {
		this.backendDirectory = backendDirectory;
	}

	public String getBackendEntryClass() {
		return backendEntryClass;
	}

	public void setBackendEntryClass(String backendEntryClass) {
		this.backendEntryClass = backendEntryClass;
	}

	public String getDriveUrl(){
		return driveUrl;
	}

	public void setDriveUrl(String driveUrl){
		this.driveUrl = driveUrl;
	}

	public String getDriveToken(){
		return driveToken;
	}

	public void setDriveToken(String driveToken){
		this.driveToken = driveToken;
	}

	public List<String> getWmes() {
		return wmes;
	}

	public void setWmes(List<String> wmes) {
		this.wmes = wmes;
	}

	/**
	 * Gets the additional arguments passed to the communication servlet
	 * Usually of the form "-traceOn -folder informallogic -ssTypeChecker informallogic.MyFeaturePredicate.valueTypeChecker"
	 * @return
	 */
	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	/**
	 * Class representing a single skill
	 * @author Patrick Nguyen
	 *
	 */
	public static class Skill
	{
		private String label;
		private String pSlip;
		private String description;
		private String pKnown;
		private String category;
		private String pLearn;
		private String name;
		private String pGuess;

		/**
		 * Gets the label
		 * @return label
		 */
		public String getLabel() {
			return label;
		}
		/**
		 * Sets the label
		 * @param label
		 */
		public void setLabel(String label) {
			this.label = label;
		}
		/**
		 * Gets the pSlip
		 * @return pSlip
		 */
		public String getpSlip() {
			return pSlip;
		}
		/**
		 * Sets the pSlip
		 * @param pSlip
		 */
		public void setpSlip(String pSlip) {
			this.pSlip = pSlip;
		}
		/**
		 * Gets the description
		 * @return description
		 */
		public String getDescription() {
			return description;
		}
		/**
		 * Sets the description
		 * @param description
		 */
		public void setDescription(String description) {
			this.description = description;
		}
		/**
		 * Gets the pKnown
		 * @return pKnown
		 */
		public String getpKnown() {
			return pKnown;
		}
		/**
		 * Sets the pKnown
		 * @param pKnown
		 */
		public void setpKnown(String pKnown) {
			this.pKnown = pKnown;
		}
		/**
		 * Gets the category
		 * @return category
		 */
		public String getCategory() {
			return category;
		}
		/**
		 * Sets the category
		 * @param category
		 */
		public void setCategory(String category) {
			this.category = category;
		}
		/**
		 * Gets the pLearn
		 * @return pLearn
		 */
		public String getpLearn() {
			return pLearn;
		}
		/**
		 * Sets the pLearn
		 * @param pLearn
		 */
		public void setpLearn(String pLearn) {
			this.pLearn = pLearn;
		}
		/**
		 * Gets the name
		 * @return name
		 */
		public String getName() {
			return name;
		}
		/**
		 * Sets the name
		 * @param name
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * Gets the pGuess
		 * @return pGuess
		 */
		public String getpGuess() {
			return pGuess;
		}
		/**
		 *  Sets the pGuess
		 * @param pGuess
		 */
		public void setpGuess(String pGuess) {
			this.pGuess = pGuess;
		}
	}


}
