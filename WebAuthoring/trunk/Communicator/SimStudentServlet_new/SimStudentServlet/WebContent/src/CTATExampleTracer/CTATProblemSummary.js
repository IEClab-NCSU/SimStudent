/* This object represents an CTATProblemSummary */

goog.provide('CTATProblemSummary');
goog.require('CTATBase');
goog.require('CTATExampleTracerException');

//goog.require('CTATSkills');//

/* LastModify: FranceskaXhakaj 11/2014*/

/**
 * @param {String} givenProblemName
 * @param {CTATSkills} givenSkills
 * @param {boolean} givenCountOnlyLastResults
 * @throws IllegalArgumentException if problemName null or empty
 */
CTATProblemSummary = function(givenProblemName, givenSkills, givenCountOnlyLastResults) 
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	CTATBase.call(this, "CTATProblemSummary","");

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	if(givenProblemName ===  null || typeof(givenProblemName) === 'undefined' || givenProblemName.length < 1)
	{
		throw new CTATExampleTracerException("problemName null or empty");
	}

	/** 
	 * Name of the problem. 
	 * @type {string}
	 */
	var problemName =  givenProblemName; 
	
    /** 
	 * Skills from the problem, or updated to the problem. 
	 * @type {CTATSkills}
	 */
	var skills = givenSkills;

	/** 
	 * Whether getUniqueCorrect(), getUniqueHints() and 
	 * getUniqueErrors() count only the last result recorded for each step.
	 * @type {boolean}
	 */
	var countOnlyLastResults = givenCountOnlyLastResults;

	/** 
	 * Time spent on this problem, in milliseconds. 
	 * @type {integer}
	 */
	var timeElapsed = 0;

	/** 
	 * Timestamp for start of problem-solution time interval. 
	 * @type {Date}
	 */
	var startTime = new Date();

	/** 
	 * Keeps track of results for each step performed. 
	 * @type {Map<String, CTATStep>}
	 */
	var stepMap = {};

	/**
	 * Number of steps on which the student requested a hint. Since CTAT2193, this matches
	 * unique hints in the CL sense. I.e., not how many times a specific hint was asked for but
	 * instead how many different hints were asked for. If a student erred on a step, then
	 * requested a hint, we previously wouldn't increment this field but, like CL, now we do.
	 * @type {integer}
	 */
	var uniqueHints = 0;

	/**
	 * Number of steps on which the first student attempt was a correct answer. The student may
	 * have requested a hint but did not commit an error on this step.
	 * @type {integer}
	 */
	var uniqueCorrect = 0;

	/**
	 * Number of steps on which the first student action was a correct answer. The student
	 * performed these steps without requesting a hint or committing an error.
	 * @type {integer}
	 */
	var uniqueCorrectUnassisted = 0;

	/**
	 * Number of steps on which the student committed an error. The student may have requested
	 * a hint. 
	 * @type {integer}
	 */
	var uniqueErrors = 0;

	/** 
	 * Whether the student has finished the problem. Values in CTATMsgType CompletionValue.
	 * @type {String}
	 */
	var completionStatus = CTATMsgType.CompletionValue[0]; // 0: incomplete


	/** 
	 * Total number of first hints requested during the problem. 
	 * @type {integer}
	 */
	var hints = 0;

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * Getter for skills and values of the problem.
	 * Depending on when its called, could have different fields filled in.
	 * @return {CTATSkills} skills 
	 */
	this.getSkills = function()
	{
		return skills;
	};

	/**
	 * Replace the skill information for this summary.
	 * @param {CTATSkills} givenSkills
	 * @return {undefined}
	 */
	this.setSkills = function(givenSkills)
	{
		skills = givenSkills;
	};

	/**
	 * Increment timeElapsed by the number of milliseconds elapsed since
	 * startTime. We increment instead of simply setting in case user wants
	 * to call startTimer()- stopTimer() more than once.
	 * @return {Date} current date (stop time)
	 */
	this.stopTimer = function()
	{
		var stopTime = new Date();

		timeElapsed += stopTime.getTime() - startTime.getTime();

		return stopTime;
	};

	/**
	 * Stop the timer and restart it, to record the timeElapsed so far.
	 * @return {integer} revised timeElapsed 
	 */
	this.restartTimer = function()
	{
		startTime = that.stopTimer();

		return timeElapsed;
	};

	/**
	 * Record that the student requested a hint on the given step. 
	 * So far, this method does not count student requests to show 2nd or further hints on the step. 
	 * @param {string} stepID a unique ID for the step whose hint was delivered
	 * @return {undefined}
	 */
	this.addHint = function(stepID)
	{
		var trial = stepMap[stepID]; //of type CTATStep

		if(trial !== null && typeof(trial) !== 'undefined')
		{
			if(trial.getNFirstHints() < 1)
			{
				++uniqueHints; // increment if no hinting on this step already
			}

			trial.incrementFirstHints();
		}
		else
		{
			trial = new CTATStep(stepID, CTATProblemSummary.StepResult[2]); //2 : HINT
			stepMap[stepID] = trial;
			uniqueHints++;
		}

		hints++;

		trial.setLastResult(CTATProblemSummary.StepResult[2]); //2 : HINT
	};

	/**
	 * Record that the tutor evaluated the student's attempt on the given step as incorrect.
	 * @param {String} stepID a unique ID for the step to which the error was charged
	 * @return {undefined}
	 */
	this.addError = function(stepID)
	{
		var trial = stepMap[stepID]; //of type CTATStep

		if(trial !== null && typeof(trial) !== 'undefined')
		{
			if(trial.getNCorrect() < 1 && trial.getNErrors() < 1)
			{
				++uniqueErrors; // increment if no attempt on this step already
			}

			trial.incrementErrors();
		}
		else
		{
			trial = new CTATStep(stepID, CTATProblemSummary.StepResult[1]); //1 : INCORRECT
			stepMap[stepID] = trial;
			uniqueErrors++;
		}

		errors++;

		trial.setLastResult(CTATProblemSummary.StepResult[1]); //1 : INCORRECT
	};

	/**
	 * Record that the tutor evaluated the student's attempt on the given step as correct.
	 * @param {String} stepID a unique ID for the step
	 * @return {undefined}
	 */
	this.addCorrect = function(stepID)
	{
		var trial = stepMap[stepID]; //of type CTATStep

		if(trial !== null && typeof(trial) !== 'undefined')
		{
			if(trial.getNCorrect() < 1 && trial.getNErrors() < 1)
			{
				++uniqueCorrect; // increment if no attempt on this step already
			
				if(trial.getNFirstHints() < 1)
				{
					uniqueCorrectUnassisted++; // can't happen? priorTrial would not exist
				}
			}

			trial.incrementNCorrect();
		}
		else
		{
			trial = new CTATStep(stepID, CTATProblemSummary.StepResult[3]); //3 : CORRECT
			stepMap[stepID] = trial;
			uniqueCorrect++;
			uniqueCorrectUnassisted++;
		}

		correct++;

		trial.setLastResult(CTATProblemSummary.StepResult[3]); // : CORRECT
	};

	/**
	 * @param {string} givCompletionStatus new value for completionStatus
	 * @param {boolean} canRevert true if allowed to change CompletionValue complete to
	 * CompletionValue incomplete
	 * @return {undefined}
	 */
	this.setCompletionStatus = function(givCompletionStatus, canRevert)
	{
		//1 : complete
		if(CTATMsgType.CompletionValue[1] === completionStatus)
		{
			if(canRevert === false)
			{
				return; // no-op if already complete and can't go back
			}
		}

		completionStatus = givCompletionStatus;
	};


/****************************** PUBLIC METHODS ****************************************************/


/****************************** CONSTRUCTOR CALLS ****************************************************/

};


/****************************** CONSTANTS ****************************************************/

	/** 
	 * Local value for the possible evaluations of student action with respect to a step.
	 * 0 : UNTRIED - no result for this step
	 * 1 : INCORRECT - a wrong answer for this step
	 * 2 : HINT - a hint request for this step
	 * 3 : CORRECT - a right answer for this step
	 * @param {array of strings}
	 */
	Object.defineProperty(CTATProblemSummary, "StepResult", {enumerable: false, configurable: false, writable: false, value: ["UNTRIED", "INCORRECT", "HINT", "CORRECT"]});

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATProblemSummary.prototype = Object.create(CTATBase.prototype);
CTATProblemSummary.prototype.constructor = CTATProblemSummary;

if(typeof module !== 'undefined')
{
	module.exports = CTATProblemSummary;
}
 
