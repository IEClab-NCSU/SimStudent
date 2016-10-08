/* This object represents an CTATExampleTracerSkill */

goog.provide('CTATExampleTracerSkill');
goog.require('CTATBase');
goog.require('CTATMsgType');
goog.require('CTATVersionComparator');

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * All fields but skillName and pknown may be null when passing back to CL Server
 * @param {string} givenSkillName
 * @param {float} p_guess
 * @param {float} p_known
 * @param {float} p_slip
 * @param {float} p_learn
 */
CTATExampleTracerSkill = function(givenSkillName, p_guess, p_known, p_slip, p_learn) 
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	//calling the constructor of the object we are inheriting from: CTATBase
    CTATBase.call(this, "CTATExampleTracerSkill", givenSkillName);

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	/** 
	 * Name of the skill.
	 * @type {string}
	 */
	var skillName = givenSkillName; 

	/** 
	 * Float value for p_guess.
	 * @type {float}
	 */
	var pGuess = p_guess;

	/** 
	 * Float value for p_known.
	 * @type {float}
	 */
	var pKnown = p_known;

	/** 
	 * Float value for p_learn.
	 * @type {float}
	 */
	var pLearn = p_learn;

	/** 
	 * Float value for p_slip.
	 * @type {float}
	 */
	var pSlip = p_slip;

	/** 
	 * A serial number identifying the last transaction calling updatePKnown(String, Integer).
	 * @type {integer}
	 */
	var transactionNumber = 0;

	/** 
	 * Number of chances encountered to demonstrate this skill.
	 * @type {integer}
	 */
	var opportunityCount = 0; //of type integer

	/** 
	 * Separator between fields in a message meant to update a single skill in the skillometer.
	 * @type {string}
	 */
	var skillBarDelimiter = CTATExampleTracerSkill.SKILL_BAR_DELIMITER_v2_10;

	/** 
	 * Float value for p_guess.
	 * @type {float}
	 */
	var masteryThreshold = CTATExampleTracerSkill.DEFAULT_MASTERY_THRESHOLD;

	/** 
	 * Label for skillometer.
	 * @type {string}
	 */
	var label = null;

	/** 
	 * Long description.
	 * @type {string}
	 */
	var description = null;
	
    /**
     * Make the object available to private methods
     */
	var that = this;

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @return {string}
	 */
	this.getSkillName = function()
	{
		return skillName;
	};

	/**
	 * @param {integer} givenTransactionNumber
	 * @return {undefined}
	 */
	this.setTransactionNumber = function(givenTransactionNumber)
	{
		transactionNumber = givenTransactionNumber;
	};

	/**
	 * This method updates the pKnown based on preset skill values.
	 * @param {string} status one of: CORRECT, INCORRECT, HINT.
	 * @return {float} revised pKnown 
	 */
	this.updatePKnown = function(status)
	{
		pKnown = CTATExampleTracerSkill.updatePKnownStatic(status, pGuess, pKnown, pSlip, pLearn);

		return pKnown;
	};

	/**
	 * Alter the  opportunityCount by adding the given delta.
	 * @param {integer} delta
	 * @return {integer} revised opportunityCount 
	 */
	this.changeOpportunityCount = function(delta)
	{
		opportunityCount += delta;

		return opportunityCount;
	};

	/**
	 * @return {integer} the transactionNumber
	 */
	this.getTransactionNumber = function()
	{
		return transactionNumber;
	};

	/**
	 * Return a single string suitable for displaying a skill bar. Format:
	 * skillName = pKnown = {1|0}", where the trailing 1 or 0
	 * indicates that masteryThreshold has been reached (1) or not (0).
	 * @param {boolean} includeLabels if true, append "`<i>label</i>" to the strings
	 * @return {string} in format "[*]<i>skillName</i>`<i>pKnown</i>`<i>mastery</i>"
	 */
	this.getSkillBarString = function(includeLabels)
	{
		var sb = this.getSkillName();
		sb = sb + this.getSkillBarDelimiter() + pKnown;
		sb = sb + this.getSkillBarDelimiter() + (this.hasReachedMastery() ? '1' : '0');

		if(includeLabels === true)
		{
			sb = sb + this.getSkillBarDelimiter() + this.getLabel();
		}

		return sb.toString();
 	};

	/**
	 * Separator between fields in a message meant to update a single skill in the skillometer.
	 * @return {string} the skillBarDelimiter
	 */
 	this.getSkillBarDelimiter = function()
 	{
 		return skillBarDelimiter;
 	};

	/**
	 * @return {boolean} true if pKnown is at least masteryThreshold
	 */
 	this.hasReachedMastery = function()
 	{
 		if(pKnown === null || typeof(pKnown) === 'undefined')
 		{
 			return false;
 		}
 		else
 		{
 			return (pKnown >= masteryThreshold);
 		}
 	};

 	/**
 	 * @return {string}
 	 */
 	this.getLabel = function()
 	{
 		if(label === null || typeof(label) === 'undefined' || label.length < 1)
 		{
 			return CTATExampleTracerSkill.getName(skillName);
 		}
 		else
 		{
 			return label;
 		}
 	};


	/**
	 * Handle version-specific differences in the protocol used to transmit skill updates.
	 * Used to achieve backward compatibility with older clients.
	 * @param {string} givenVersion argument for CTATExampleTracerSkill.versionToSkillBarDelimiter(String)
	 * ignored (no-op) if null or empty
	 * @return {undefined}
	 */
 	this.setVersion = function(givenVersion)
 	{
 		if(givenVersion !== null && typeof(givenVersion) !== 'undefined' && givenVersion.length > 0) // don't change on null or empty version
 		{
 			skillBarDelimiter = CTATExampleTracerSkill.versionToSkillBarDelimiter(givenVersion);
 		}
 	};

	/**
	 * @param {string} givenLabel new value for label
	 * @return {undefined}
	 */
 	this.setLabel = function(givenLabel)
 	{
 		label = givenLabel;
 	};

	/**
	 * @param {string} givenDescription new value for description
	 * @return {undefined}
	 */
 	this.setDescription = function(givenDescription)
 	{
 		description = givenDescription;
 	};

/****************************** STATIC METHODS ****************************************************/

	/**
	 * Create a step identifier by concatenating selection and action.  Omits null and empty
	 * list elements. Also omits artifacts of hint requests.
	 * @param {array} selection
	 * @param {array} action 
	 * @return {string} with each element of the inputs; empty string if both null or empty
	 */
	CTATExampleTracerSkill.makeStepID = function(selection, action)
	{
		var sb = ""; //string
		var i = 0; //integer

		for(var v = selection; i++ < 2; v = action)
		{
			if(v === null || typeof(v) === 'undefined' || v.length < 1)
			{
				continue;
			}

			var vStarted = false; //boolean

			for(var j = 0; j < v.length; j++)
			{
				if(v[j] === null || typeof(v[j]) === 'undefined')
				{
					continue;
				}

				var s = v[j].toString();

				if(s.length < 1)
				{
					continue;
				}

				if(v === selection && ("hint".toString().toUpperCase() === s.toString().toUpperCase() || "help".toString().toUpperCase() === s.toString().toUpperCase()))
				{
					continue;
				}

				if(v === action && CTATMsgType.PREVIOUS_FOCUS.toString().toUpperCase() === s.toString().toUpperCase())
				{
					continue;
				}

				var res = (vStarted ? "," : "[");
				sb = sb + res + s;
				vStarted = true;
			}


			if(vStarted === true)
			{
				sb = sb + "]"; //vEnded
			}
		}

		return sb.toString();
	};

	/**
	 * Updates the pknown using algorithm found in older code
	 * This method can be called from anywhere returning the updated pknown value, however the value is not stored internally to this skill object
	 * All operations are rounded to 2 decimal places using the toFIxed() method (this method returns a string but we convert to number
	 * using the "+" sign in the beginning of an operation)
	 * @param {string} givenStatus 
	 * @param {float} givenP_guess 
	 * @param {float} givenP_known 
	 * @param {float} givenP_slip 
	 * @param {float} givenP_learn 
	 * @return {float} rounded to 2 decimal places
	 */
	CTATExampleTracerSkill.updatePKnownStatic = function(givenStatus, givenP_guess, givenP_known, givenP_slip, givenP_learn)
	{
		var knewIt = 0;

		if(givenStatus.toString().toUpperCase() === CTATExampleTracerSkill.CORRECT.toString().toUpperCase())
		{
			var guessedIt = +(givenP_guess * (1.0 - givenP_known)).toFixed(2); //rounded to 2 decimal places
			var knewAndPerformed = +(givenP_known * (1.0 - givenP_slip)).toFixed(2); //rounded to 2 decimal places

			knewIt = +(knewAndPerformed/(knewAndPerformed + guessedIt)).toFixed(2); //rounded to 2 decimal places
		}
		else if((givenStatus.toString().toUpperCase() === CTATExampleTracerSkill.INCORRECT.toString().toUpperCase()) || (givenStatus.toString().toUpperCase() === CTATExampleTracerSkill.HINT.toString().toUpperCase()))
		{
			var choked = +(givenP_known * givenP_slip).toFixed(2); //rounded to 2 decimal places
			var dontKnowDontGuess = +((1.0 - givenP_known) * (1.0 - givenP_guess)).toFixed(2); //rounded to 2 decimal places
			knewIt = +(choked/(choked + dontKnowDontGuess)).toFixed(2); //rounded to 2 decimal places
		}
		else
		{
			//error, unknown status
		}

		//rounded to 2 decimal places
		return +((knewIt + givenP_learn * (1.0 - knewIt))).toFixed(2); //no point in setting pknown to it ...
	};

	/**
	 * The simple name is also known as the skill name or the rule name.
	 * It is that portion of the skillName preceding the 1st embedded space.
	 * @param {string} givenSkillName
	 * @return {string}
	 */
	CTATExampleTracerSkill.getName = function(givenSkillName)
	{
		var spPos = givenSkillName.indexOf(' ');

		if(spPos < 0)
		{
			return givenSkillName;
		}
		else
		{
			return givenSkillName.substring(0, spPos);
		}
	};

	/**
	 * Calculate the proper skill bar string delimiter for the given version.
	 * @param {string} givenVersion
	 * @return {string}
	 */
	CTATExampleTracerSkill.versionToSkillBarDelimiter = function(givenVersion)
	{
		if(givenVersion === null || typeof(givenVersion) === 'undefined')
		{
			return CTATExampleTracerSkill.SKILL_BAR_DELIMITER_v2_10;
		}

		if(CTATVersionComparator.vc.compare(givenVersion, "2.11") >= 0)
		{
			return CTATExampleTracerSkill.SKILL_BAR_DELIMITER_v2_11;
		}
		else
		{
			return CTATExampleTracerSkill.SKILL_BAR_DELIMITER_v2_10;
		}
	};

/****************************** PUBLIC METHODS ****************************************************/

};

/****************************** CONSTANTS ****************************************************/

    /**
     * Constant for a student asking for hint.
     * @param {string} HINT
     */
    Object.defineProperty(CTATExampleTracerSkill, "HINT", {enumerable: false, configurable: false, writable: false, value: "hint"});

    /**
     * @param {string} CORRECT
     */
    Object.defineProperty(CTATExampleTracerSkill, "CORRECT", {enumerable: false, configurable: false, writable: false, value: "correct"});
    
    /**
     * @param {string} INCORRECT
     */
    Object.defineProperty(CTATExampleTracerSkill, "INCORRECT", {enumerable: false, configurable: false, writable: false, value: "incorrect"});
    
    /**
     * @param {string} SKILL_BAR_DELIMITER_v2_10
     */
    Object.defineProperty(CTATExampleTracerSkill, "SKILL_BAR_DELIMITER_v2_10", {enumerable: false, configurable: false, writable: false, value: "="});
    
    /**
     * Value for SKILL_BAR_DELIMITER from version 2.11 on.
     * @param {string} SKILL_BAR_DELIMITER_v2_11
     */
    Object.defineProperty(CTATExampleTracerSkill, "SKILL_BAR_DELIMITER_v2_11", {enumerable: false, configurable: false, writable: false, value: "`"});
    
    /**
     * Default level of pKnown that represents mastery of a skill.
     * @param {float} DEFAULT_MASTERY_THRESHOLD
     */
    Object.defineProperty(CTATExampleTracerSkill, "DEFAULT_MASTERY_THRESHOLD", {enumerable: false, configurable: false, writable: false, value: 0.95});
    
    /**
     * Default value for pGuess.
     * @param {float} DEFAULT_P_GUESS
     */
    Object.defineProperty(CTATExampleTracerSkill, "DEFAULT_P_GUESS", {enumerable: false, configurable: false, writable: false, value: 0.2});
    
    /**
     * Default value for pKnown.
     * @param {float} DEFAULT_P_KNOWN
     */
    Object.defineProperty(CTATExampleTracerSkill, "DEFAULT_P_KNOWN", {enumerable: false, configurable: false, writable: false, value: 0.3});
    
    /**
     * Default value for pSlip.
     * @param {float} DEFAULT_P_SLIP
     */
    Object.defineProperty(CTATExampleTracerSkill, "DEFAULT_P_SLIP", {enumerable: false, configurable: false, writable: false, value: 0.3});
 
     /**
     * Default value for pLearn.
     * @param {float} DEFAULT_P_LEARN
     */
    Object.defineProperty(CTATExampleTracerSkill, "DEFAULT_P_LEARN", {enumerable: false, configurable: false, writable: false, value: 0.15});

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATExampleTracerSkill.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerSkill.prototype.constructor = CTATExampleTracerSkill;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerSkill;
}
 
