/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-06-28 15:07:11 -0500 (週二, 28 六月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATComponents/CTATSkillSet.js $
 $Revision: 23782 $

 -
 License:
 -
 ChangeLog:
 -
 Notes: TODO: this should probably made into an object.

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

	this.internalSkillSet=[];
	var pointer=this;

	/**
	*
	*/
	this.fromXMLString=function fromXMLString (aSkills)
	{
		this.ctatdebug("fromXMLString ()");

		this.ctatdebug ("Skills string: " + aSkills);

		if (aSkills===null)
		{
			this.ctatdebug ("Warning: skill object is null");
			return;
		}

		if (aSkills==="")
		{
			this.ctatdebug ("Info: empty skill string provided, bump");
			return;
		}

		if (!aSkills)
		{
			this.ctatdebug ("Warning: skill object is undefined or otherwise empty.");
			return;
		}

		//useDebugging=true;

		this.ctatdebug ("CTATSkillSet.fromXMLString() Raw:     " + aSkills);

		var decoded=decodeURIComponent(aSkills.replace(/\+/g,  " "));

		this.ctatdebug ("CTATSkillSet.fromXMLString() Decoded: " + decoded);

		var valuePattern= new RegExp ("/<value>.+<\/value>");

		if (valuePattern.exec(decoded)!==null)
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

		if (valuePattern.exec(raw)!==null)
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
		this.ctatdebug("parseByValue()");

		if (!aSkills)
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
	 * <skill opportunitycount="2" name="determine-lcd" label="determine-lcd" category="main" pknown="0.25" history="11"/>
	 */
	this.parseByAttributes=function parseByAttributes(aSkills)
	{
		this.ctatdebug("parseByAttributes()");

		if (!aSkills)
		{
			pointer.ctatdebug ("Error: aSkills is null");
			return;
		}

		var x=aSkills.childNodes;

		if (!x)
		{
			this.ctatdebug ("Error: list of skill xml elements is null");
			return;
		}

		for (var i=0;i<x.length;i++)
		{
			var elem=x [i];

			if (elem.nodeName=="Skill" || elem.nodeName=="skill")
			{
				this.ctatdebug ("Parsing node ("+i+"): " + elem.nodeName + " -> " + elem.nodeValue);

				var nm=  elem.attributes.getNamedItem("name");     nm = (nm ? nm.value : "");
				if(!nm)
				{
					continue;  // no skill without a name
				}
				var pK =   elem.attributes.getNamedItem("pKnown");      pK =   (pK   ? pK.value   : "");
				var desc = elem.attributes.getNamedItem("description"); desc = (desc ? desc.value : "");
				var lbl =  elem.attributes.getNamedItem("label");       lbl =  (lbl  ? lbl.value  : "");
				var cat =  elem.attributes.getNamedItem("category");    cat =  (cat  ? cat.value  : "");
				var pG =   elem.attributes.getNamedItem("pGuess");      pG =   (pG   ? pG.value   : "");
				var pL =   elem.attributes.getNamedItem("pLearn");      pL =   (pL   ? pL.value   : "");
				var pS =   elem.attributes.getNamedItem("pSlip");       pS =   (pS   ? pS.value   : "");
				var hist = elem.attributes.getNamedItem("history");     hist = (hist ? hist.value : "");

				this.addSkill (nm, pK, 0.95, desc, lbl, cat, pG, pL, pS, hist);
			}
		}
	};
	/**
	 * Returns the current list of skills within the set.
	 * @return	The current set of skills.
	 */
	this.getSkillSet=function getSkillSet()
	{
		return this.internalSkillSet;
	};

	/**
	 * Returns the number of skills in the collection.
	 */
	this.getSize=function getSize()
	{
		return this.internalSkillSet.length;
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
							 		pSlip,
									aHistory)
	{
		this.ctatdebug("addSkill() name = " + aName + " level = " +aLevel +
				" mastery = " +aMastery + " aDescription = " + aDescription	+
				" adisplayName = " + aDisplayName + " aCategory = " + aCategory +
				" pguess= " + pGuess +" plearn = " + pLearn	+ " pslip = " + pSlip + " history = " + aHistory);

		//var newSkill=new CTATSkill ();

		var newSkill=this.setSkillLevel (aName,aLevel,aMastery);

		this.ctatdebug ("Configuring " + newSkill.getDisplayName ());

		if(aDescription)
			newSkill.setDescription(aDescription);

		if(aDisplayName)
			newSkill.setDisplayName(aDisplayName);

		if(aCategory)
			newSkill.setCategory(aCategory);

		if(pGuess !== "")
		{
			newSkill.setPGuess(pGuess);
			newSkill.setPLearn(pLearn);
			newSkill.setPSlip(pSlip);
		}

		if (aHistory!=="")
		{
			newSkill.setSkillHistory (aHistory);
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
		this.ctatdebug("setSkillLevel ("+aName+","+aLevel+","+aMastery+")");

		var skill=this.getSkill (aName);

		if (skill===null)
		{
			this.ctatdebug ("Skill not found, creating new one ...");

			skill = new CTATSkill ();
			skill.setSkillName(aName);

			//if (aMastery == 1)
			//{
			//	skill.setLevel(1);
			//}
			//else
			//{
				skill.setLevel(aLevel);
			//}

			this.internalSkillSet.push(skill);
		}
		else
		{
			this.ctatdebug ("Skill found, adjusting ...");

			//if (aMastery == 1)
			//{
				skill.setLevel(1);
			//}
			//else
			//{
				skill.setLevel(aLevel);
			//}
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
		this.ctatdebug ("getSkill ("+aName+") -> " + this.internalSkillSet.length);

		if (!aName)
		{
			return null;
		}

		for (var i=0;i<this.internalSkillSet.length;i++)
		{
			var skill=this.internalSkillSet [i];

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
		if (!aName)
			return -1;

		for (var skill in this.internalSkillSet)
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

		var touchedList=[];

		for (var i=0;i<this.internalSkillSet.length;i++)
		{
			var skill=this.internalSkillSet [i];

			if (skill.getTouched ()===true)
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

		for (var i=0;i<this.internalSkillSet.length;i++)
		{
			var skill=this.internalSkillSet [i];

			skill.setTouched (false);
		}
	};

	/**
	 * Returns an XMLString of the entire skillset in the format expected by a SetPreferences message
	 * @return	An XMLString of the entire internalSkillSet.
	 */
	this.toSetPreferencesXMLString=function toSetPreferencesXMLString()
	{
		this.ctatdebug ("toSetPreferencesXMLString ()");

		var message="<skills>";

		for (var i=0;i<this.internalSkillSet.length;i++)
		{
			var skill=this.internalSkillSet [i];

			message += skill.toSetPreferencesXMLString();
		}

		message += "</skills>";

		return message;
	};

	/**
	*
	*/
	this.toLogString=function toLogString ()
	{
		this.ctatdebug ("toLogString ("+this.internalSkillSet.length+")");

		var skillString = "";

		for (var i=0;i<this.internalSkillSet.length;i++)
		{
			var skill=this.internalSkillSet [i];

			skillString += "<skill probability=\""+skill.getLevel()+"\"><name>"+skill.getSkillName()+"</name>";

			if (skill.hasCategory())
			{
				skillString +='<category>' + skill.getCategory() + '</category>';
			}

			if (skill.hasModelName())
			{
			 	skillString += '<model_name>' + skill.getModelName() + '</model_name>';
			}

			skillString += '</skill>';
		}

		return (skillString);
	};
};

CTATSkillSet.prototype = Object.create(CTATBase.prototype);
CTATSkillSet.prototype.constructor = CTATSkillSet;

CTATSkillSet.skills = null;

if(typeof module !== 'undefined')
{
    module.exports = CTATSkillSet;
}
