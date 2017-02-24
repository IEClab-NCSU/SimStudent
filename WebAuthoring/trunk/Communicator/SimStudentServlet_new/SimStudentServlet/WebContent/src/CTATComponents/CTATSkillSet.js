/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATSkillSet.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

The AssociatedRules msg looks like this; the delimiters in the <Skills> values are backquotes:

<?xml version="1.0" encoding="UTF-8"?>
<message>
 <verb>SendNoteProperty</verb>
 <properties>
  <MessageType>AssociatedRules</MessageType>
  <Indicator>Correct</Indicator>
  <Selection><value>firstDenConv</value></Selection>
  <Action><value>UpdateTextArea</value></Action>
  <Input><value>6</value></Input>
  <StudentSelection><value>firstDenConv</value></StudentSelection>
  <StudentAction><value>UpdateTextArea</value></StudentAction>
  <StudentInput><value>6</value></StudentInput>
  <Actor>Student</Actor>
  <Rules>
    <value>determine-lcd fraction-addition</value>
    <value>multiply-denominators fraction-addition</value>
  </Rules>
  <Skills>
    <value>multiply-denominators fraction-addition`0.50609756`0</value>
    <value>determine-lcd fraction-addition`0.50609756`0</value>
  </Skills>
  <skillBarDelimiter>`</skillBarDelimiter>
  <StepID>1</StepID>
  <tool_selection>firstDenConv</tool_selection>
  <transaction_id>445F8E49FAF09641</transaction_id>
  <LogAsResult>true</LogAsResult>
  <end_of_transaction>true</end_of_transaction>
 </properties>
</message>

 */
goog.provide('CTATSkillSet');

goog.require('CTATBase');
goog.require('CTATGlobals');
goog.require('CTATSkill');
goog.require('CTATXML');
/**
 *
 */
CTATSkillSet = function()
{
	CTATBase.call(this, "CTATSkillSet","skills");

	this.skillSet=new Array ();
	var pointer=this;

	/**
	*
	*/
	this.fromXMLString=function fromXMLString (aSkills)
	{
		this.ctatdebug("fromXMLString ()");

		this.ctatdebug ("Skills string: " + aSkills);

		if (aSkills==null)
		{
			this.ctatdebug ("Warning: skill object is null");
			return;
		}

		if (aSkills=="")
		{
			this.ctatdebug ("Info: empty skill string provided, bump");
			return;
		}

		//useDebugging=true;

		//this.ctatdebug ("Raw: " + aSkills);

		var decoded=decodeURIComponent(aSkills.replace(/\+/g,  " "));

		//this.ctatdebug ("Decoded: " + decoded);

		var valuePattern= new RegExp ("/<value>.+<\/value>");

		if (valuePattern.exec(decoded)!=null)
		{
			//fromXMLData (null,decoded);
			this.parseByValue (decoded);
		}
		else
		{
			var parser=new CTATXML ();
			var root=parser.parseXML (decoded);

			this.parseByAttributes(root,decoded);
		}

		//useDebugging=false;

		this.ctatdebug("fromXMLString () done");
	};

	/**
	 * Parses an XML String into a SkillSet.
	 * <p>This method is written to support both of the possible configurations of a skill based XML String.
	 * One is of the form &#60;Skills&#62;&#60;value&#62;<i>name</i> <i>category</i>`<i>level</i>`<i>mastery</i>`<i>DisplayName</i>&#60;/value&#62;&#60;/Skills&#62;.
	 * The other is of the form &#60;Skills&#62;&#60;skill name="<i>name</i> <i>category</i>" level="<i>level</i>" mastery="<i>mastery</i>" description="<i>description</i>" label="displayName"/&#62;&#60;/Skills&#62;
	 * There is capability to deal with different capitalization of "value" and "Skill" but the two presented are the prefered formating.</p>
	 * @param	xml	An XMLList containing skills of either of the accepted formats.
	 */
	this.fromXMLData=function fromXMLData (xml,raw)
	{
		this.ctatdebug("fromXMLData ()");

		// this is a way better parsing system than what we used before.

		var valuePattern= new RegExp ("/<value>.+<\/value>");

		if (valuePattern.exec(raw)!=null)
		{
			this.parseByValue(raw);
		}
		else
		{
			this.parseByAttributes(xml);
		}

		this.ctatdebug("fromXMLData () done");
	};
	/**
	 *
	 */
	this.parseByValue=function parseByValue(aSkills)
	{
		//this.ctatdebug("parseByValue("+aSkills.nodeName+" -> " + aSkills.childNodes.length + ")");
		this.ctatdebug("parseByValue()");

		if (aSkills==null)
		{
			pointer.ctatdebug ("Error: aSkills is null");
			return;
		}

		var slist=aSkills.childNodes;

		var parser=new CTATXML ();

		for (var k=0;k<slist.length;k++)
		{
			var testSkill=slist [k];

			var skillString=parser.getNodeTextValue (testSkill);

			var aSkill = skillString.split("`");

			var pair=aSkill[0].split(" ");

			//I have seen cases where skills don't come with a display name, hence the first condition.
			if (aSkill.length==4)
				this.addSkill(pair[0], aSkill[1], aSkill[2], aSkill[3], aSkill[3], pair[1]);
			else
				this.addSkill(pair[0], aSkill[1], aSkill[2], aSkill[3], aSkill[4], pair[1]);
		}
	};

	/**
	 *
	 */
	this.parseDOM=function parseDOM(anElement)
	{
		this.ctatdebug("parseDOM()");

		var parser=new CTATXML ();

		var aList=parser.getElementChildren (anElement);

		for (var k=0;k<aList.length;k++)
		{
			var testSkill=aList [k];

			var skillString=parser.getNodeTextValue (testSkill);

			var aSkill = skillString.split("`");

			var pair=aSkill[0].split(" ");

			//I have seen cases where skills don't come with a display name, hence the first condition.
			if (aSkill.length==4)
				this.addSkill(pair[0], aSkill[1], aSkill[2], aSkill[3], aSkill[3], pair[1]);
			else
				this.addSkill(pair[0], aSkill[1], aSkill[2], aSkill[3], aSkill[4], pair[1]);
		}
	};

	/**
	 * <skill opportunitycount="2" name="determine-lcd" label="determine-lcd" category="main" pknown="0.25"/>
	 */
	this.parseByAttributes=function parseByAttributes(aSkills)
	{
		this.ctatdebug("parseByAttributes()");

		if (aSkills==null)
		{
			pointer.ctatdebug ("Error: aSkills is null");
			return;
		}

		var x=aSkills.childNodes;

		if (x==null)
		{
			this.ctatdebug ("Error: list of skill xml elements is null");
			return;
		}

		for (var i=0;i<x.length;i++)
		{
			var elem=x [i];

			if (elem.nodeName=="Skill")
			{
				this.ctatdebug ("Parsing node ("+i+"): " + elem.nodeName + " -> " + elem.nodeValue)

				this.addSkill (elem.attributes.getNamedItem("name").value,
							   elem.attributes.getNamedItem("pKnown").value,
							   .95,
							   elem.attributes.getNamedItem("label").value,
							   elem.attributes.getNamedItem("label").value,
							   elem.attributes.getNamedItem("category").value,
							   elem.attributes.getNamedItem("pGuess").value,
							   elem.attributes.getNamedItem("pLearn").value,
							   elem.attributes.getNamedItem("pSlip").value);
			}
		}
	};
	/**
	 * Returns the current list of skills within the set.
	 * @return	The current set of skills.
	 */
	this.getSkillSet=function getSkillSet()
	{
		return this.skillSet;
	};

	/**
	 * Returns the number of skills in the collection.
	 */
	this.getSize=function getSize()
	{
		return this.skillSet.length;
	};
	/**
	 * Adds a CTATSkill to the SkillSet.
	 * <p>If a skill by the same name already exists within the set then the values will be updated with those provided.</p>
	 * @param	aName			The name of the skill
	 * @param	aLevel			The level of the skill
	 * @param	aMastery		The master level for the skill
	 * @param	aDescription	The long form description of the skill
	 * @param	aDisplayName	The display name of the skill
	 * @param	aCategory		The category of the skill
	 * @param	pGuess			The pGuess value of the skill
	 * @param	pLearn			The pLearn value of the skill
	 * @param	pSlip			The pSlip value of the skill
	 * @return	The CTATSkill that was added.
	 */
	this.addSkill=function addSkill(aName,
							 		aLevel,
							 		aMastery,
							 		aDescription,
							 		aDisplayName,
							 		aCategory,
							 		pGuess,
							 		pLearn,
							 		pSlip)
	{
		this.ctatdebug("addSkill() name = " + aName + " level = " +aLevel + " mastery = " +aMastery + " aDescription = " + aDescription
				+ " adisplayName = " + aDisplayName + " aCategory = " + aCategory + " pguess= " + pGuess +" plearn = " + pLearn
				+ " pslip = " + pSlip);

		//var newSkill=new CTATSkill ();

		var newSkill=this.setSkillLevel (aName,aLevel,aMastery);

		this.ctatdebug ("Configuring " + newSkill.getDisplayName ());

		if(aDescription!="" && aDescription!=null)
			newSkill.setDescription(aDescription);

		if(aDisplayName!="" && aDisplayName!=null)
			newSkill.setDisplayName(aDisplayName);

		if(aCategory!="" && aCategory!=null)
			newSkill.setCategory(aCategory);

		if(pGuess != "")
		{
			newSkill.setPGuess(pGuess);
			newSkill.setPLearn(pLearn);
			newSkill.setPSlip(pSlip);
		}

		return newSkill;
	};

	/**
	 * Searched the list for a skill of the given name and updates its level.
	 * <p>If not skill by that name exists it will create a new Skill</p>
	 * @param	aName		The name of skill
	 * @param	aLevel		The level of the skill
	 * @param	aMastery	The mastery level of the skill
	 * @return	The updated CTATSkill, or a new one if it did not exist.
	 */
	this.setSkillLevel=function setSkillLevel(aName, aLevel, aMastery)
	{
		this.ctatdebug("setSkillLevel ("+aName+")");

		var skill=this.getSkill (aName);

		if (skill==null)
		{
			this.ctatdebug ("Skill not found, creating new one ...");

			skill = new CTATSkill ();
			skill.setSkillName(aName);

			if (aMastery == 1)
				skill.setLevel(1);
			else
				skill.setLevel(aLevel);

			this.skillSet.push(skill);
		}
		else
		{
			this.ctatdebug ("Skill found, adjusting ...");

			if (aMastery == 1)
				skill.setLevel(1);
			else
				skill.setLevel(aLevel);
		}

		skill.setTouched(true);

		return skill;
	};

	/**
	 * Returns a skill by name
	 * @param	aName	The name of a skill
	 * @return	The CTATSkill in the CTATSkillSet with the desired name
	 */
	this.getSkill=function getSkill(aName)
	{
		this.ctatdebug ("getSkill ("+aName+") -> " + this.skillSet.length);

		if (aName == null || aName == "")
		{
			return null;
		}

		for (var i=0;i<this.skillSet.length;i++)
		{
			var skill=this.skillSet [i];

			if (skill.getSkillName ()==aName)
			{
				this.ctatdebug ("Returning: " + i);
				return (skill);
			}
		}

		return null;
	};
	/**
	 * Returns the skillLevel of a given skill.
	 * <p>If the skill does not exist in the SkillSet it will return <code>-Infinity</code>.</p>
	 * @param	aName	The name of a skill
	 * @return	The level of the Skill, or <code>-Infinity</code> if the skill is not in the set.
	 */
	this.getSkillLevel=function getSkillLevel (aName)
	{
		if (aName == null || aName == "")
			return -1;

		for (var skill in this.skillSet)
		{
			if (skill.getSkillName ()==aName)
				return (skill.getLevel ());
		}

		return -1;
	};

	/**
	 * Returns all of the skills that have been changed since the last time untouchSkills was called.
	 * @return	Returns a subset of skills that were changed since the last untouchSkills was called.
	 */
	this.getTouched=function getTouched()
	{
		this.ctatdebug ("getTouched ()");

		var touchedList=new Array ();

		for (var i=0;i<this.skillSet.length;i++)
		{
			var skill=this.skillSet [i];

			if (skill.getTouched ()==true)
			{
				this.ctatdebug ("Adding touched skill: " + skill.getSkillName());

				touchedList.push (skill);
			}
		}

		return (touchedList);
	};

	/**
	 * Reset the touched status of all the skills in the set.
	 */
	this.untouchSkills=function untouchSkills()
	{
		this.ctatdebug ("untouchSkills ()");

		for (var i=0;i<this.skillSet.length;i++)
		{
			var skill=this.skillSet [i];

			skill.setTouched (false);
		}
	};

	/**
	 * Returns an XMLString of the entire skillset in the format expected by a SetPreferences message
	 * @return	An XMLString of the entire skillSet.
	 */
	this.toSetPreferencesXMLString=function toSetPreferencesXMLString()
	{
		var message="<skills>";

		for (var i=0;i<this.skillSet.length;i++)
		{
			var skill=this.skillSet [i];

			message += skill.toSetPreferencesXMLString();
		}

		message += "</skills>";

		return message;
	};
}

CTATSkillSet.prototype = Object.create(CTATBase.prototype);
CTATSkillSet.prototype.constructor = CTATSkillSet;

if(typeof module !== 'undefined')
{
    module.exports = CTATSkillSet;
}
