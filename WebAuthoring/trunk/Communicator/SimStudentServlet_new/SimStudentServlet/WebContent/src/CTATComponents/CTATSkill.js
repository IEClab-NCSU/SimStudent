/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATSkill.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATSkill');
/**
 *
 */
CTATSkill = function()
{
	var skillName= "";
	var displayName= "";
	var category= "";
	var modelName= "";
	var level= 0;
	var description= "";
	var touched= false;

	var pGuess= "";
	var pSlip= "";
	var pKnown= "";
	var pLearn= "";

	var label=null;

	/**
	 * Sets the Skill's name.
	 * @param	n	The skill's name.
	 */
	this.setSkillName=function setSkillName(n)
	{
		skillName = n;
	};

	/**
	 * Sets the skill's display name.
	 * @param	n	If a skill does not have a display new it will use its name instead.
	 */
	this.setDisplayName=function setDisplayName(n)
	{
		displayName = n;
	};

	/**
	 * Sets the skill's model
	 * @param	model	The model that the skill belongs to.
	 */
	this.setModelName=function setModelName(model)
	{
		modelName = model;
	}

	/**
	 * Sets the skill's category.
	 * @param	cat	The category that the skill belongs to.
	 */
	this.setCategory=function setCategory(cat)
	{
		category = cat;
	};

	/**
	 * Sets the current profficiency level of the skill.
	 * @param	lvl	The level of the skill.
	 */
	this.setLevel=function setLevel(lvl)
	{
		if (isNaN (lvl)==true)
		{
			ctatdebug ("Error: attempting to set a level to NaN");
			return;
		}

		level = lvl;

		this.setPKnown(String(level));
	};

	/**
	 * Sets a long ofrm description of the skill.
	 * @param	desc	The Skill's long form description.
	 */
	this.setDescription=function setDescription(desc)
	{
		description = desc;
	};

	/**
	 * Returns the skill's name.
	 * @return	The skill's name.
	 */
	this.getSkillName=function getSkillName()
	{
		return skillName;
	};

	/**
	 * Returns the skill's displayName.
	 * <p>If a skill has no display name it will use its given name.</p>
	 * @return	The skill's display name, or its given name if it has none.
	 */
	this.getDisplayName=function getDisplayName()
	{
		//return (hasDisplayName() ?  name : displayName);
		return (displayName);
	};

	/**
	 * Returns whether or not the skill has a displayName.
	 * @return	<code>true</code> if the skill has a display name, <code>false</code> otherwise.
	 */
	this.hasDisplayName=function hasDisplayName()
	{
		return (displayName != "" && displayName != null);
	};

	/**
	 * Returns the name of skill's model.
	 * @return	The skill's model.
	 */
	this.getModelName=function getModelName()
	{
		return modelName;
	};

	/**
	 * Returns whether or not the skill has a category value.
	 * @return	<code>true</code> if the skill has a category, <code>false</code> otherwise.
	 */
	this.hasCategory=function hasCategory()
	{
		return category != "";
	};

	/**
	 * Returns whether or not the skill has a model name.
	 * @return	<code>true</code> if the skill has a model, <code>false</code> otherwise.
	 */
	this.hasModelName=function hasModelName()
	{
		return modelName != "";
	};

	/**
	 * Returns the category of the skill.
	 * @return	The skill's category.
	 */
	this.getCategory=function getCategory()
	{
		return category;
	};

	/**
	 * Returns the current level of the skill.
	 * @return	The skill's level.
	 */
	this.getLevel=function getLevel()
	{
		return level;
	};

	/**
	 * Returns the long form description of the skill.
	 * @return	The skill's description.
	 */
	this.getDescription=function getDescription()
	{
		return description;
	};

	/**
	 * @private
	 * @param	touch
	 */
	this.setTouched=function setTouched(touch)
	{
		touched = touch
	};

	/**
	 * @private
	 * @return
	 */
	this.getTouched=function getTouched()
	{
		return touched;
	};

	/**
	 * Returns and empty string?
	 */
	this.toXMLString=function toXMLString()
	{
		return "";
	};

	/**
	 * Returns an xml string of the skill formatted as it is expected to be in a SetPreferences message.
	 * @return	An XML String of the skill.
	 */
	this.toSetPreferencesXMLString=function toSetPreferencesXMLString()
	{
		var string = '<skill label="' + displayName + '" pSlip="' + pSlip + '" description="' + description;
		string += '" pKnown="' +pKnown + '" category="' + category + '" pLearn="' + pLearn + '" name="' + skillName + '" pGuess="' + pGuess + '" />';

		return string;
	};
	/**
	 *
	 */
	this.setPGuess=function setPGuess(guess)
	{
		pGuess = guess;
	};
	/**
	 *
	 */
	this.getPGuess=function getPGuess()
	{
		return pGuess;
	};
	/**
	 *
	 */
	this.setPSlip=function setPSlip(slip)
	{
		pSlip = slip;
	};
	/**
	 *
	 */
	this.getPSlip=function getPSlip()
	{
		return pSlip;
	};
	/**
	 *
	 */
	this.setPLearn=function setPLearn(learn)
	{
		pLearn = learn;
	};
	/**
	 *
	 */
	this.getPLearn=function getPLearn()
	{
		return pLearn;
	};
	/**
	 *
	 */
	this.setPKnown=function setPKnown(known)
	{
		pKnown = known;
	};
	/**
	 *
	 */
	this.getPKnown=function getPKnown()
	{
		return pKnown;
	};
}
