package edu.cmu.pact.ctat.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.Utilities.VersionComparator;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.ctat.XMLSupport;

/**
 * 
 */
public class Skill implements XMLSupport.Client, Serializable  
{
	private static final long serialVersionUID = -2045143690658256687L;

	/** Value for {@link #SKILL_BAR_DELIMITER} from version 2.11 on. */
	private static final String SKILL_BAR_DELIMITER_v2_11 = "`";
	
	/** Value for {@link #SKILL_BAR_DELIMITER} before version 2.11. */
	private static final String SKILL_BAR_DELIMITER_v2_10 = "=";
	
	/** Separator between fields in a message meant to update a single skill in the skillometer. */
	public static final String SKILL_BAR_DELIMITER = SKILL_BAR_DELIMITER_v2_10;

	/** Default level of {@link #pKnown} that represents mastery of a skill. */
	public static float DEFAULT_MASTERY_THRESHOLD = 0.95F;
	
	/** Default value for {@link #pGuess}. */
	public static float DEFAULT_P_GUESS = 0.2F;

	/** Default value for {@link #pKnown}. */
	public static float DEFAULT_P_KNOWN = 0.3F;

	/** Default value for {@link #pSlip}. */
	public static float DEFAULT_P_SLIP = 0.3F;

	/** Default value for {@link #pLearn}. */
	public static float DEFAULT_P_LEARN = 0.15F;
	
	/** XML tag name for element for this class. */
	public static final String ELEMENT_NAME = "Skill";

	/** String format for printing, transmitting. */
	static XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat().setIndent(" ")
			.setEncoding("UTF-8").setOmitEncoding(true)
			.setOmitDeclaration(true).setLineSeparator("\n"));

	/** Attribute name for {@link #skillName}. */
	private static final String NAME = "name";

	/** Attribute name for {@link #getCategory()}. */
	private static final String CATEGORY = "category";
	
	/**Name of the skill */
	private final String skillName;
	
	/**Float value for p_guess */
	private float masteryThreshold = DEFAULT_MASTERY_THRESHOLD;
	
	/**Float value for p_guess */
	private Float pGuess;
	
	/**Float value for p_known */
	private Float pKnown;
	
	/**Float value for p_slip */
	private Float pSlip;
	
	/**Float value for p_learn */
	private Float pLearn;

	/** Long description. */
	private String description;

	/** Label for skillometer. */
	private String label;

	/** A serial number identifying the last transaction calling {@link #updatePKnown(String, Integer)}. */
	private int transactionNumber = 0;

	/** Separator between fields in a message meant to update a single skill in the skillometer. */	
	private String skillBarDelimiter = SKILL_BAR_DELIMITER_v2_10;

	/** Number of chances encountered to demonstrate this skill. */
	private int opportunityCount = 0;
	
	/**
	 * Separator between fields in a message meant to update a single skill in the skillometer.
	 * @return the {@link #skillBarDelimiter}
	 */
	public String getSkillBarDelimiter() {
		return skillBarDelimiter;
	}

	/**Constant for a correct problem status */
	public static final String CORRECT = "correct";

	/**Constant for an incorrect problem status */
	public static final String INCORRECT = "incorrect";
	
	/**Constant for a student asking for hint */
	public static final String HINT = "hint";

	//all fields but name and pknown may be null when passing back to CL Server
	public Skill(String skillName, Float p_guess, Float p_known, Float p_slip, Float p_learn)
	{
		this.skillName = skillName;
		this.pGuess = p_guess;
		this.pKnown = p_known;
		this.pLearn = p_learn;
		this.pSlip = p_slip;
	}

	/**
	 * Constructor uses all default values.
	 * @param skillName
	 */
	public Skill(String skillName) {
		this(skillName, DEFAULT_P_GUESS, DEFAULT_P_KNOWN, DEFAULT_P_SLIP, DEFAULT_P_LEARN);
	}
	
	/**
	 * Constructor sets {@link #pKnown}. 
	 * @param skillName
	 * @param p_known
	 */
	public Skill(String skillName, Float p_known) {
		this(skillName, DEFAULT_P_GUESS, p_known, DEFAULT_P_SLIP, DEFAULT_P_LEARN);
	}
	
	/**
	 * Updates the pknown using algorithm found in older code
	 * This method can be called from anywhere returning the updated pknown value, however the value is not stored internally to this skill object 
	 */
	public static Float updatePKnown(String status, Float p_guess, Float p_known, Float p_slip, Float p_learn)
	{
		double knewIt = 0;
		if(status.equalsIgnoreCase(CORRECT))
		{
			double guessedIt = p_guess * (1.0 - p_known);
			double knewAndPerformed = p_known * (1.0 - p_slip);
			knewIt = knewAndPerformed / (knewAndPerformed + guessedIt);
		}
		else if(status.equalsIgnoreCase(INCORRECT) || status.equalsIgnoreCase(HINT))
		{
			double choked = p_known * p_slip;
			double dontKnowDontGuess = (1.0 - p_known) * (1.0 - p_guess);
			knewIt =  choked / (choked + dontKnowDontGuess);
		}
		else
		{
			//error, unknown status
		}
		return new Float(knewIt + p_learn*(1.0 - knewIt)); //no point in setting pknown to it ...
	}
	
	/**
	 * This method updates the {@link #pKnown} based on preset skill values.
	 * @param status one of {@link #CORRECT}, {@link #INCORRECT}, {@link #HINT}.
	 * @return revised {@link #pKnown}
	 */
	public Float updatePKnown(String status)
	{
		return pKnown = updatePKnown(status, pGuess, pKnown, pSlip, pLearn);
	}
	
	//clone method for copies from an interpretation
	public Skill clone()
	{
		return new Skill(new String(skillName), new Float(pGuess), new Float(pKnown), new Float(pSlip), new Float(pLearn));
	}
	
	//getters
	public String getSkillName()
	{
		return skillName;
	}
	
	/**
	 * The category is also known as the skill set name or the production rule set name.
	 * It is that portion of the {@link #skillName} following the 1st embedded space.
	 * @return
	 */
	public String getCategory()	{
		return getCategory(skillName);
	}
	
	/**
	 * The category is also known as the skill set name or the production rule set name.
	 * It is that portion of the {@link #skillName} following the 1st embedded space.
	 * @param skillName
	 * @return
	 */
	public static String getCategory(String skillName) {
		int spPos = skillName.indexOf(' ');
		if (spPos < 0)
			return "";
		else
			return skillName.substring(spPos+1);
	}
	
	/**
	 * The simple name is also known as the skill name or the rule name.
	 * It is that portion of the {@link #skillName} preceding the 1st embedded space.
	 * @param skillName
	 * @return
	 */
	public String getName() {
		return getName(skillName);
	}
	
	/**
	 * The simple name is also known as the skill name or the rule name.
	 * It is that portion of the {@link #skillName} preceding the 1st embedded space.
	 * @param skillName
	 * @return
	 */
	public static String getName(String skillName)
	{
		int spPos = skillName.indexOf(' ');
		if (spPos < 0)
			return skillName;
		else
			return skillName.substring(0, spPos);
	}
	
	public String getDescription()
	{
		if (description == null || description.length() < 1)
			return skillName;
		else
			return description;
	}
	
	public String getLabel()
	{
		if (label == null || label.length() < 1)
			return getName();
		else
			return label;
	}
	
	public Float getPGuess()
	{
		return pGuess;
	}

	public Float getPLearn()
	{
		return pLearn;
	}
	
	public Float getPSlip()
	{
		return pSlip;
	}
	
	public Float getPKnown()
	{
		return pKnown;
	}

	/**
	 * Parse an XML Element to create an instance of this class.
	 * @param elt
	 * @return new instance
	 * @throws Exception from introspection methods
	 */
	public static Skill factory(Element elt) throws Exception {
		String skillName = elt.getAttributeValue(NAME);
		if (skillName == null || skillName.length() < 1)
			throw new IllegalArgumentException("Bad XML for skill: required attribute "+NAME+" missing");
		String category = elt.getAttributeValue(CATEGORY);
		if (category != null && category.length() > 0)
			skillName = skillName+' '+category;
		Skill sk = new Skill(skillName);
		xmlSupport.getAttributes(sk, elt);
		return sk;
	}

	/**
	 * @return contents as an XML {@value #ELEMENT_NAME} element
	 */
	public Element toXMLElement() {
		Element skElt = new Element(ELEMENT_NAME);
		skElt.setAttribute(NAME, getName());
		skElt.setAttribute(CATEGORY, getCategory());
		xmlSupport.setAttributes(this, skElt);
		return skElt;
	}

	/**
	 * @return contents as an XML {@value #ELEMENT_NAME} string
	 */
	public String toXMLString() {
		Element elt = toXMLElement();
		return xmlOutputter.outputString(elt);
	}

	/**
	 * @return "[<i>skillName</i>, pK=<i>pKnown</i>]" 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		sb.append(getSkillName()).append(", pK=").append(getPKnown()).append("]");
		return sb.toString();
	}

	/**
	 * Decides whether a property is to be saved in {@link #ATTR_FIELDS} by generic code.
	 * Other properties, such as key values, may be saved in specialized code;
	 * cf. {@link #NAME}.
	 * @param fName the field name
	 * @return true if the property is to be saved 
	 */
	public boolean isAttrProperty(String fName) {
		fName = fName.toLowerCase();
		return fName.startsWith("p")
		|| fName.equals("label")
		|| fName.equals("description")
		|| fName.equals("opportunitycount");
	}
	
	/**
	 * @param guess new value for {@link #pGuess}; uses {@link #DEFAULT_P_GUESS} on error
	 */
	public void setPGuess(String guess) {
		try {
			pGuess = Float.valueOf(guess);
		} catch (NumberFormatException nfe) {
			trace.err("Error converting p-guess \""+guess+"\" to float: "+nfe+
					";\n  using default "+DEFAULT_P_GUESS);
		}
	}

	/**
	 * @param known new value for {@link #pKnown}; uses {@link #DEFAULT_P_KNOWN} on error
	 */
	public void setPKnown(String known) {
		try {
			pKnown = Float.valueOf(known);
		} catch (NumberFormatException nfe) {
			trace.err("Error converting p-known \""+known+"\" to float: "+nfe+
					";\n  using default "+DEFAULT_P_KNOWN);
		}
	}

	/**
	 * @param slip new value for {@link #pSlip}; uses {@link #DEFAULT_P_SLIP} on error
	 */
	public void setPSlip(String slip) {
		try {
			pSlip = Float.valueOf(slip);
		} catch (NumberFormatException nfe) {
			trace.err("Error converting p-slip \""+slip+"\" to float: "+nfe+
					";\n  using default "+DEFAULT_P_SLIP);
		}
	}

	/**
	 * @param learn new value for {@link #pLearn}; uses {@link #DEFAULT_P_LEARN} on error
	 */
	public void setPLearn(String learn) {
		try {
			pLearn = Float.valueOf(learn);
		} catch (NumberFormatException nfe) {
			trace.err("Error converting p-learn \""+learn+"\" to float: "+nfe+
					";\n  using default "+DEFAULT_P_LEARN);
		}
	}
	
	/**
	 * @param description new value for {@link #description}
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @param label new value for {@link #label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Alter the {@link #opportunityCount} by adding the given delta.
	 * @param delta
	 * @return revised {@link #opportunityCount}
	 */
	public int changeOpportunityCount(int delta) {
		opportunityCount += delta;
		return opportunityCount;
	}

	/**
	 * @return the {@link #opportunityCount}
	 */
	public int getOpportunityCount() {
		return opportunityCount;
	}

	/**
	 * @param opportunityCount new value for {@link #opportunityCount}
	 */
	public void setOpportunityCount(String opportunityCount) {
		this.opportunityCount = Integer.valueOf(opportunityCount);
	}

	/**
	 * @return {@link #xmlSupport}
	 * @see edu.cmu.pact.ctat.XMLSupport.Client#getXMLSupport()
	 */
	public XMLSupport getXMLSupport() {
		return xmlSupport;
	}

	public void setXMLSupport(XMLSupport newXMLSupport) {
		xmlSupport = newXMLSupport;
	}
	
	/** Set of fields that need to be saved in XML representations. */
	private static XMLSupport xmlSupport = null;

	/**
	 * @return the {@link #masteryThreshold}
	 */
	public float getMasteryThreshold() {
		return masteryThreshold;
	}

	/**
	 * @param masteryThreshold new value for {@link #masteryThreshold}
	 */
	public void setMasteryThreshold(float masteryThreshold) {
		this.masteryThreshold = masteryThreshold;
	}
	
	/**
	 * Return a single string suitable for displaying a skill bar. Format:
	 * "{@link #skillName}={@link #pKnown}={1|0}", where the trailing 1 or 0
	 * indicates that {@link #masteryThreshold} has been reached (1) or not (0).
	 * @return string in format "[*]<i>skillName</i>=<i>pKnown</i>=<i>mastery</i>"
	 */
	public String getSkillBarString() {
		return getSkillBarString(false);
	}

	/**
	 * Parsed content of a skill bar. See {@link Skill#getSkillBarString()}.
	 */
	public static class SkillBar implements Serializable  
	{
		private static final long serialVersionUID = -539319473342965130L;
		
		private String category;
		private String name;
		private Float pKnown;
		private Boolean mastery;
		private String label;
		private SkillBar(String category, String name, Float pKnown, Boolean mastery, String label) {
			this.category = category;
			this.name = name;
			this.pKnown = pKnown;
			this.mastery = mastery;
			this.label = label;
		}
		public String getCategory() { return category; }
		public String getName() { return name; }
		public Float getPKnown() { return pKnown; }
		public Boolean getMastery() { return mastery; }
		public String getLabel() { return label; }
	}

	/**
	 * Return a single string suitable for displaying a skill bar. Format:
	 * "{@link #skillName}={@link #pKnown}={1|0}", where the trailing 1 or 0
	 * indicates that {@link #masteryThreshold} has been reached (1) or not (0).
	 * @param includeLabels if true, append "`<i>label</i>" to the strings
	 * @return parse results in {@link Skill.SkillBar}
	 */
	public static SkillBar parseSkillBarString(String skillBarString) {
		return parseSkillBarString(skillBarString, SKILL_BAR_DELIMITER);
	}

	/**
	 * Return a single string suitable for displaying a skill bar. Format:
	 * "{@link #skillName}={@link #pKnown}={1|0}", where the trailing 1 or 0
	 * indicates that {@link #masteryThreshold} has been reached (1) or not (0).
	 * @param includeLabels if true, append "`<i>label</i>" to the strings
	 * @param delimiter character to insert between fields
	 * @return parse results in {@link Skill.SkillBar}
	 */
	public static SkillBar parseSkillBarString(String skillBarString, String delimiter) {
		String category = null;
		String name = null;
		Float pKnown = null;
		Boolean mastery = null;
		String label = null;
		if (skillBarString != null) {
			String sbDelimiter = (delimiter == null || delimiter.length() < 1 ?
					SKILL_BAR_DELIMITER : delimiter);
			String[] fields = skillBarString.split(sbDelimiter);
			for (int i = 0; i < fields.length; ++i) {  // don't assume all fields present
				switch(i) {
				case 0: 
					category = getCategory(fields[i]); name = getName(fields[i]); break; 
				case 1:
					try { pKnown = Float.valueOf(fields[i]); }
					catch (NumberFormatException nfe) { trace.err("bad pKnown: "+nfe); } break;
				case 2:
					mastery = new Boolean('1' == fields[i].charAt(0)); break;
				case 3:
					label = fields[i]; break;
				}
			}
		}
		return new SkillBar(category, name, pKnown, mastery, label);
	}

	/**
	 * Return a single string suitable for displaying a skill bar. Format:
	 * "{@link #skillName}={@link #pKnown}={1|0}", where the trailing 1 or 0
	 * indicates that {@link #masteryThreshold} has been reached (1) or not (0).
	 * @param includeLabels if true, append "`<i>label</i>" to the strings
	 * @return string in format "[*]<i>skillName</i>`<i>pKnown</i>`<i>mastery</i>"
	 */
	public String getSkillBarString(boolean includeLabels) {
		StringBuffer sb = new StringBuffer(getSkillName());
		sb.append(getSkillBarDelimiter()).append(pKnown);
		sb.append(getSkillBarDelimiter()).append(hasReachedMastery() ? '1' : '0');
		if (includeLabels)
			sb.append(getSkillBarDelimiter()).append(getLabel());
		if(trace.getDebugCode("skill")) trace.out("skill",sb.toString());
		return sb.toString();
	}

	/**
	 * @return true if {@link #pKnown} is at least {@link #masteryThreshold}
	 */
	public boolean hasReachedMastery() {
		if (pKnown == null)
			return false;
		else
			return pKnown.floatValue() >= masteryThreshold;
	}

	/**
	 * @return the {@link #transactionNumber}
	 */
	public int getTransactionNumber() {
		return transactionNumber;
	}

	/**
	 * @param transactionNumber new value for {@link #transactionNumber}
	 */
	void setTransactionNumber(int transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	/**
	 * Convert a string of the form given by {@link #getSkillBarString()} into a Skill,
	 * using the delimiter {@link Skill#SKILL_BAR_DELIMITER}.
	 * @param skillBarStr string of the format above
	 * @return Skill with data parsed from the string
	 */
	public static Skill skillBarToSkill(String skillBarStr) {
		return skillBarToSkill(skillBarStr, SKILL_BAR_DELIMITER);
	}

	/**
	 * Convert a string of the form given by {@link #getSkillBarString()} into a Skill.
	 * @param skillBarStr string of the format above
	 * @param delimiter
	 * @return Skill with data parsed from the string
	 */
	public static Skill skillBarToSkill(String skillBarStr, String delimiter) {
		String[] sbPieces = skillBarStr.split(delimiter);
		Skill result = new Skill(sbPieces[0]);
		if (sbPieces.length < 2)
			return result;
		try {
			result.setPKnown(sbPieces[1]);
		} catch (NumberFormatException nfe) {
			trace.err("bad pKnown in skill bar \""+skillBarStr+"\": using default");
		}
		if (sbPieces.length > 3)
			result.setLabel(sbPieces[3]);
		return result;
	}

	/**
	 * Create a step identifier by concatenating selection and action.  Omits null and empty
	 * list elements. Also omits artifacts of hint requests.
	 * @param selection
	 * @param action
	 * @return string with each element of the inputs; empty string if both null or empty
	 */
	public static String makeStepID(Vector selection, Vector action) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (Vector v = selection; i++ < 2; v = action) {
			if (v == null || v.size() < 1)
				continue;
			boolean vStarted = false;
			for (Object o : v) {
				if (o == null)
					continue;
				String s = o.toString();
				if (s.length() < 1)
					continue;
				if (v == selection && ("hint".equalsIgnoreCase(s) || "help".equalsIgnoreCase(s)))
					continue;
				if (v == action && HintMessagesManagerForClient.PREVIOUS_FOCUS.equalsIgnoreCase(s))
					continue;
				sb.append(vStarted ? "," : "[").append(s);
				vStarted = true;
			}
			if (vStarted)
				sb.append("]");  // vEnded
		}
		return sb.toString();
	}

	/**
	 * Handle version-specific differences in the protocol used to transmit skill updates.
	 * Used to achieve backward compatibility with older clients.
	 * @param version argument for {@link #versionToSkillBarDelimiter(String)};
	 *                ignored (no-op) if null or empty
	 */
	public void setVersion(String version) {
		if (version != null && version.length() > 0)    // don't change on null or empty version 
			skillBarDelimiter = versionToSkillBarDelimiter(version);
	}
	
	/**
	 * Calculate the proper skill bar string delimiter for the given version.
	 * @param version
	 * @return
	 */
	public static String versionToSkillBarDelimiter(String version) {
		if (version == null)
			return SKILL_BAR_DELIMITER_v2_10;
		if (VersionComparator.vc.compare(version, "2.11") >= 0)
			return SKILL_BAR_DELIMITER_v2_11;
		else
			return SKILL_BAR_DELIMITER_v2_10;
	}

	/**
	 * Test harness for {@link #versionToSkillBarDelimiter(String)}
	 * @param args versions
	 */
	public static void main(String[] args) {
		for (String v : args)
			System.out.printf("versionToSkillBarDelimiter(%s) -> %s\n",
					v, versionToSkillBarDelimiter(v) );
		String v = null;
		System.out.printf("versionToSkillBarDelimiter(%s) -> %s\n",
				v, versionToSkillBarDelimiter(v) );

	}
	
	/**
	 * Instance initializer to populate {@link #xmlSupport}. Be sure this block 
	 * remains at the end of the class.
	 */
	{ xmlSupport = XMLSupport.initialize(this); }
}
