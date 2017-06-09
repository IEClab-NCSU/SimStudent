/* This object represents an CTATSkills */

goog.provide('CTATSkills');
goog.require('CTATBase');
goog.require('CTATExampleTracerSkill');

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * @param skillList of type array of CTATExampleTracerSkill
 */
CTATSkills = function(skillList)
{
	CTATBase.call(this, "CTATSkills", (skillList ? String(skillList.length) : ""));

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	//for fast retrieval of skills
	var skillMap = {}; //map of String --> CTATExampleTracerSkill

	//A serial number for update transactions, used to tell which skills have changed
	var transactionNumber = 0; //of type integer

	skillList.forEach(function(skill)
	{
		skillMap[skill.getSkillName().toLowerCase()] = skill;
	});

	//Step identifiers to enforce the restriction that no step's skills are updated more than once
	var updatedStepIDs = new Set(); //set of strings

	//Tell whether these skills were defined externally (not from a brd): default is false.
	var externallyDefined = false; //of type boolean

	//Client version. Can affect protocol used for skill updates.
	var version = null; //of type string

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * Format the Skills in XML for, e.g., responding to a ProblemSummaryRequest. For pretty-printing,
	 * pass whitespace as "\n  " to follow each child element with a newline and indent 2 spaces, e.g.
	 * @param {boolean} escape if true, substitute &amp; entities for &lt, &gt;
	 * @param {string} whitespace insert this whitespace between elements (optional)
	 * @return {string} XML element
	 */
	this.toXML = function(escape,whitespace)
	{
		if(!whitespace)
		{
			whitespace = "";  // be sure it's a string
		}
		var sb = (escape ? "&lt;Skills&gt;" : "<Skills>");
		var sbLength0 = sb.length;

		for(var sk in skillMap)
		{
			if(skillMap.hasOwnProperty(sk) && skillMap[sk])
			{
				sb += (whitespace+skillMap[sk].toXML(escape));
			}
		}
		sb += (whitespace && (sb.length > sbLength0) ? "\n" : "");
		sb += (escape ? "&lt;/Skills&gt;" : "</Skills>");
		return sb;
	};

	/**
	 * Revise the given skill's p-known as a result of the given transaction result.
	 * Maintains updatedStepIDs: updates skill only if stepID is new. Also
	 * removes stepID if correct, so that the selection and action
	 * could be entered anew when, e.g., minTraversals > 1 or UI component is reused.
	 * @param transactionResult (of type string): one of CTATExampleTracerSkill.CORRECT, CTATExampleTracerSkill.INCORRECT, CTATExampleTracerSkill.HINT
	 * @param skillName (of type string) no-op if this skill not found by getSkill(String)
	 * @param stepID (of type string) identifier for this step, to ensure no step is updated more than once
	 * @return CTATExampleTracerSkill: the skill, if modified
	 */
	this.updateSkill = function(transactionResult, skillName, stepID)
	{
		var result = null; //of type CTATExampleTracerSkill
		var skill = that.getSkill(skillName);

		if(skill !== null && typeof(skill) !== 'undefined') // // not tracing this skill
		{
			skill.setTransactionNumber(transactionNumber);
			var key = stepID + " " + skillName; //track each skill separately
			if(!updatedStepIDs.has(key))
			{                                     // update only if step ID is new
				updatedStepIDs.add(key);
				skill.updatePKnown(transactionResult);
				skill.updateHistory(transactionResult);
				skill.changeOpportunityCount(1);
			}

			if(CTATExampleTracerSkill.CORRECT.toString().toUpperCase() === transactionResult.toString().toUpperCase()) // on correct, forget this change, so it could be credited anew
			{
				updatedStepIDs.delete(key);
			}

			result = skill;
		}

		return result;
	};

	/**
	 * Set a new serial number for update transactions. The serial number is used to tell
	 * which skills have changed. Increments {@link #transactionNumber}.
	 */
	this.startTransaction = function()
    {
		++transactionNumber;
	};

	/**
	 * @param skillName (of type String)
	 * @return CTATExampleTracerSkill
	 */
	this.getSkill = function(skillName)
	{
		var toGet = (skillName === null || typeof(skillName) === 'undefined' ? null : skillName.toLowerCase());
		var sk = skillMap[toGet];
		that.ctatdebug("CTATSkills.getSkill("+skillName+") returns "+sk);
		return sk;
	};

	/**
	 * Return an array suitable for displaying or updating a skill bar. For format,
	 * see CTATExampleTracerSkill.getSkillBarString(). Retrieves only those skills whose
	 * CTATExampleTracerSkill.getTransactionNumber() matches our transactionNumber.
	 * @return string in format "[*]<i>skillName</i>=<i>pKnown</i>=<i>mastery</i>"
	 * @param includeLabels of type boolean: if true, append "=<i>label</i>" to the strings
	 * @param includeAll of type boolean: if true, include all skills in skillMap; else only skills whose
	 * CTATExampleTracerSkill.getTransactionNumber() matches our transactionNumber.
	 * @return array of strings in format "[*]<i>skillName</i>=<i>pKnown</i>=<i>mastery</i>"
	 */
	this.getSkillBarVector = function(includeLabels, includeAll)
	{
		var result = []; //array of strings

		for(var skill in skillMap)
		{
			if(skillMap.hasOwnProperty(skill) === true)
			{
				if(includeAll === true || skillMap[skill].getTransactionNumber() === transactionNumber)
				{
					result.push(skillMap[skill].getSkillBarString(includeLabels));
				}
			}
		}
		return result;
	};

	/**
	 * Set whether this set of skills was defined from a .brd or externally.
	 * @param givenExternallyDefined of type boolean: new value for externallyDefined
	 * @return undefined
	 */
	this.setExternallyDefined = function(givenExternallyDefined)
	{
		externallyDefined = givenExternallyDefined;
	};

	/**
	 * Handle version-specific differences in the protocol used to transmit skill updates.
	 * Used to achieve backward compatibility with older clients.
	 * @param givenVersion of type string:
	 * @return undefined
	 */
	this.setVersion = function(givenVersion)
	{
		version = givenVersion;

		for(var sk in skillMap)
		{
			if(skillMap.hasOwnProperty(sk) === true)
			{
				skillMap[sk].setVersion(givenVersion);
			}
		}
	};


/****************************** PUBLIC METHODS ****************************************************/

};

CTATSkills.prototype = Object.create(CTATBase.prototype);
CTATSkills.prototype.constructor = CTATSkills;

if(typeof module !== 'undefined')
{
	module.exports = CTATSkills;
}

