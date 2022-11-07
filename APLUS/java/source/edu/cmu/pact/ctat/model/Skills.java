package edu.cmu.pact.ctat.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.Utilities.trace;

/**
 * This class models a collection of {@link Skill} objects. There are several classes
 * associated with skills: <ul>
 * <li>{@link Skill} models a single skill, holding the current values of the probabilities
 *     {@link Skill#getPKnown()}, etc.; </li>
 * <li>this class, which holds the skills relevant to a single problem in execution;</li>
 * <li>{@link RuleProduction} holds information about a rule or skill reference in a behavior
 *     graph; it maintains opportunity counts;</li>
 * <li>{@link RuleProduction.Catalog} is a collection of {@link RuleProduction} objects, where
 *     the scope of the collection may be a single problem, as in
 *     {@link ProblemModel#getProblemSummary()}, or several problems, as in
 *     {@link BR_Controller#getRuleProductionCatalog()}. </li>
 * <li></li>
 * </ul>
 */
public class Skills implements Serializable 
{
	private static final long serialVersionUID = -7981908596528542627L;

	/** Parameter name for passing an object of this type. */
	public static final String SKILLS = "Skills";

	/** For debug messages. */
	private static XMLOutputter xmlOutputter = new XMLOutputter();
	
	/** for fast retrieval of skills. */
	private HashMap<String, Skill> skillMap = new LinkedHashMap<String, Skill>();
	
	/** Step identifiers to enforce the restriction that no step's skills are updated more than once. */
	private Set<String> updatedStepIDs = new HashSet<String>();

	/** A serial number for update transactions, used to tell which skills have changed. */
	private int transactionNumber = 0;

	/** Tell whether these skills were defined externally (not from a brd): default is false. */
	private boolean externallyDefined = false;

	/** Client version. Can affect protocol used for skill updates. */
	private String version;

	/**
	 * Equivalent to {@link #Skills(List)} with an empty list.
	 */
	public Skills() {
		this(new LinkedList<Skill>());
	}

	/**
	 * Populate from a list of {@link Skill}s.
	 * @param skillList
	 */
	public Skills(List<Skill> skillList)
	{
		for(Skill skill : skillList)
			skillMap.put(skill.getSkillName().toLowerCase(), skill);
	}

	public Skill getSkill(String skillName)
	{
		return skillMap.get(skillName == null ? null : skillName.toLowerCase());
	}
	
	public List<Skill> getAllSkills()
	{
		List<Skill> skillList = new ArrayList<Skill>();
		for(Skill skill : skillMap.values())
			skillList.add(skill);
		return skillList;
	}
	
	/**
	 * @return Number of skills in {@link #skillMap}
	 */
	public int size() {
		return skillMap.size();
	}

	/**
	 * Access for testing.
	 * @param args first argument is file name to parse
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 1)
			return;
		File inputFile = new File(args[0]);
		Skills skills = factory(inputFile);
		System.out.printf("%2s. %-30s %-30s %s\n", "No", "Category", "Name", "pKnown");
		int i = 0;
		for (Skill skill : skills.getAllSkills())
			System.out.printf("%2d. %-30s %-30s %4.2f\n",
					++i, skill.getCategory(), skill.getName(), skill.getPKnown());
		trace.out();
		trace.out(skills.toXMLString());
	}

	/**
	 * Create from an XML file, containing a {@value #SKILLS} element as its root or a top-level child.
	 * @param inputFile with {@link #SKILLS} element
	 * @return Skills populated from file; null if no {@link #SKILLS} element
	 * @throws Exception from i/o or parsing
	 */
	public static Skills factory(File inputFile) throws Exception {
		InputStream is = new BufferedInputStream(new FileInputStream(inputFile));
		SAXBuilder xmlParser = new SAXBuilder();
		Document doc = xmlParser.build(is);
		Element root = doc.getRootElement();
		if (SKILLS.equalsIgnoreCase(root.getName()))
			return factory(doc.getRootElement());
		Element skillsElt = root.getChild(SKILLS);
		if (skillsElt != null)
			return factory(skillsElt);
		return null;
	}
	
	
	/**
	 * Create from an XML string, containing a {@value #SKILLS} element as its root or a top-level child.
	 * @param inputFile with {@link #SKILLS} element
	 * @return Skills populated from file; null if no {@link #SKILLS} element
	 * @throws Exception from i/o or parsing
	 */
	public static Skills factory(String inputString) throws Exception 
	{	
		SAXBuilder xmlParser = new SAXBuilder();
		Document doc = xmlParser.build(new StringReader(inputString));
		Element root = doc.getRootElement();
		
		if (SKILLS.equalsIgnoreCase(root.getName()))
			return factory(doc.getRootElement());
		
		Element skillsElt = root.getChild(SKILLS);
		
		if (skillsElt != null)
			return factory(skillsElt);
		
		return null;
	}	

	/**
	 * Create from a single XML element, containing one or more Skill child elements.
	 * @param elt
	 * @return
	 * @throws Exception
	 */
	public static Skills factory(Element elt) throws Exception {
		List<Element> childElts = null;
		if (trace.getDebugCode("skills"))
			trace.out("skills", "Skills.factory("+xmlOutputter.outputString(elt)+")");

		if (Skill.ELEMENT_NAME.equalsIgnoreCase(elt.getName())) {
			childElts = new ArrayList<Element>();
			childElts.add(elt);
		} else {
			childElts = elt.getChildren(Skill.ELEMENT_NAME);
			if (childElts == null || childElts.isEmpty())
				childElts = elt.getChildren(Skill.ELEMENT_NAME.toLowerCase());
		}
		return factory(childElts);
	}

	/**
	 * Create from a list of Skill Elements.
	 * @param skillElts
	 * @return
	 * @throws Exception
	 */
	public static Skills factory(List skillElts) throws Exception {
		List<Skill> skillList = new ArrayList<Skill>();
		for (Object child : skillElts) {
			if (!(child instanceof Element))
				continue;
			try {
				Skill skill = Skill.factory((Element) child);
				if (skill != null)
					skillList.add(skill);
			} catch (Exception e) {
				trace.err("Error parsing skill: "+e+"; cause "+e.getCause()+
						"\n  skill: "+xmlOutputter.outputString((Element) child));				
			}
		}
		if (trace.getDebugCode("skills")) trace.out("skills", "Skills.factory skillList: "+skillList);
		return new Skills(skillList);
	}

	/**
	 * @return contents as an XML string {@value #SKILLS}
	 */
	public String toXMLString() {
		Element skillsElt = toXMLElement();
		return Skill.xmlOutputter.outputString(skillsElt);
	}

	/**
	 * @return contents as an XML {@value #SKILLS} element
	 */
	public Element toXMLElement() {
		Element skillsElt = new Element(SKILLS);
		for(Skill sk : getAllSkills())
			skillsElt.addContent(sk.toXMLElement());
		return skillsElt;
	}
	
	/**
	 * Return a vector suitable for displaying or updating a skill bar. For format,
	 * see {@link Skill#getSkillBarString()}. Retrieves only those skills whose 
	 * {@link Skill#getTransactionNumber()} matches our {@link #transactionNumber}.
	 * @return string in format "[*]<i>skillName</i>=<i>pKnown</i>=<i>mastery</i>"
	 */
	public Vector<String> getSkillBarVector() {
		return getSkillBarVector(false);
	}

	/**
	 * Return a vector suitable for displaying or updating a skill bar. For format,
	 * see {@link Skill#getSkillBarString()}. Retrieves only those skills whose 
	 * {@link Skill#getTransactionNumber()} matches our {@link #transactionNumber}.
	 * @param includeLabels if true, append "=<i>label</i>" to the strings
	 * @return string in format "[*]<i>skillName</i>=<i>pKnown</i>=<i>mastery</i>"
	 */
	public Vector<String> getSkillBarVector(boolean includeLabels) {
		return getSkillBarVector(includeLabels, false);
	}

	/**
	 * Return a vector suitable for displaying or updating a skill bar. For format,
	 * see {@link Skill#getSkillBarString()}. 
	 * @param includeLabels if true, append "=<i>label</i>" to the strings
	 * @param includeAll if true, include all skills in {@link #skillMap}; else only skills whose
	 *        {@link Skill#getTransactionNumber()} matches our {@link #transactionNumber}
	 * @return string in format "[*]<i>skillName</i>=<i>pKnown</i>=<i>mastery</i>"
	 */
	public Vector<String> getSkillBarVector(boolean includeLabels, boolean includeAll) {
		Vector<String> result = new Vector<String>();
		for (Skill skill : skillMap.values()) {
			if (includeAll || skill.getTransactionNumber() == transactionNumber)
				result.add(skill.getSkillBarString(includeLabels));
		}
		return result;
	}

	/**
	 * Revise the given skill's p-known as a result of the given transaction result.
	 * Maintains {@link #updatedStepIDs}: updates skill only if stepID is new. Also
	 * (sewall 2011/03/06) removes stepID if correct, so that the selection and action
	 * could be entered anew when, e.g., minTraversals > 1 or UI component is reused. 
	 * @param transactionResult one of
	 *        {@link Skill#CORRECT()}, {@link Skill#INCORRECT}, {@link Skill#HINT}
	 * @param skillName no-op if this skill not found by {@link #getSkill(String)}
	 * @param stepID identifier for this step, to ensure no step is updated more than once
	 * @return the skill, if modified 
	 */
	public Skill updateSkill(String transactionResult, String skillName, String stepID) {
		Skill result = null;
		boolean newStep = false;
		Skill skill = getSkill(skillName);
		if (skill != null) {                                       // not tracing this skill
			skill.setTransactionNumber(transactionNumber);
			String key = stepID+" "+skillName;     // sewall 2011/03/07: track each skill separately
			newStep = updatedStepIDs.add(key);
			if (newStep) {                                          // update only if step ID is new
				skill.updatePKnown(transactionResult);
				skill.changeOpportunityCount(1);
			}
			if (Skill.CORRECT.equalsIgnoreCase(transactionResult)) // on correct, forget this change
				updatedStepIDs.remove(key);                        // so it could be credited anew 
			result = skill;
		}
		if (trace.getDebugCode("skills"))
			trace.out("skills", "updateSkill["+skillName+"]=>"+skill+" "+
					(newStep ? "new" : "not new")+": "+transactionResult);
		return result;
	}

	/**
	 * Add the given skill unless one with the same name already appears in {@link #skillMap}.
	 * @param skill
	 */
	public void add(Skill skill) {
		if (skill == null || skill.getSkillName() == null)
			return;
		if (skillMap.get(skill.getSkillName().toLowerCase()) == null) {
			skill.setVersion(version);
			if(trace.getDebugCode("skill")) trace.out("skill","Skills.add("+skill+") setting version to: "+version);
			skillMap.put(skill.getSkillName().toLowerCase(), skill);
		}
	}

	/**
	 * Set a new serial number for update transactions. The serial number is used to tell
	 * which skills have changed. Increments {@link #transactionNumber}.
	 */
	public void startTransaction() {
		++transactionNumber;
	}

	/**
	 * @return the {@link #transactionNumber}
	 */
	public int getTransactionNumber() {
		return transactionNumber;
	}

	/**
	 * Tell whether this set of skills was defined from a .brd or externally.
	 * @return {@link #externallyDefined}
	 */
	public boolean isExternallyDefined() {
		return externallyDefined;
	}

	/**
	 * Set whether this set of skills was defined from a .brd or externally.
	 * @param new value for {@link #externallyDefined}
	 */
	public void setExternallyDefined(boolean externallyDefined) {
		this.externallyDefined = externallyDefined;
	}

	/**
	 * Handle version-specific differences in the protocol used to transmit skill updates.
	 * Used to achieve backward compatibility with older clients.
	 * @param version
	 */
	public void setVersion(String version) {
		if(trace.getDebugCode("skills"))
			trace.out("skills", "Skills.setVersion("+version+"): version was "+this.version);
		this.version = version;
		for (Skill sk : skillMap.values())
			sk.setVersion(version);
	}

	/** 
	 * @return result of {@link Skill#versionToSkillBarDelimiter(String)} with argument {@link #version}
	 */
	public String getSkillBarDelimiter() {
		String result = Skill.versionToSkillBarDelimiter(version);
		if(trace.getDebugCode("skills"))
			trace.out("skills", "Skills.getSkillBarDelimiter() result "+result);
		return result;
	}
}
