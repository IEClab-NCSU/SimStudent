/* This object represents an CTATProblemSummary */

goog.provide('CTATProblemSummary');
goog.require('CTATBase');
goog.require('CTATExampleTracerException');
goog.require('CTATStep');

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

	/** Whether toXML() should include counts and skills in its serialized output. Default true. */
	var showCounts = true;

	/** Number of required student steps from start state to done state. */
	var requiredSteps = Number.MAX_VALUE;  // initialize bigger than any problem

	/**
	 * Name of the problem.
	 * @type {string}
	 */
	var problemName =  givenProblemName;

    /**
	 * Skills from the problem, or updated to the problem.
	 * @type {CTATSkills}
	 */
	var pSummarySkills = givenSkills;

	/**
	 * Whether getUniqueCorrect(), getUniqueHints() and
	 * getUniqueErrors() count only the last result recorded for each step.
	 * @type {boolean}
	 */
	var countOnlyLastResults = givenCountOnlyLastResults;

	/**
	 * Initial number of different steps on which the student requested a hint but never erred.
	 * This is only an initial value, not incremented as steps are added. See getHintsOnly().
	 * @type {integer}
	 */
	var initialHintsOnly = 0;

	/**
	 * Initial number of different steps on which the student erred but never requested a hint.
	 * This is only an initial value, not incremented as steps are added. See getErrorsOnly().
	 * @type {integer}
	 */
	var initialErrorsOnly = 0;

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
	 * Total number of correct responses returned during the problem.
	 * @type {integer}
	 */
	var correct = 0;

	/**
	 * Total number of first hints requested during the problem.
	 * @type {integer}
	 */
	var hints = 0;

	/**
	 * Total number of errors charged during the problem.
	 * @type {integer}
	 */
	var errors = 0;

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

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @return {boolean} countOnlyLastResults
	 */
	this.getCountOnlyLastResults = function()
	{
		return countOnlyLastResults;
	};

	/**
	 * @param {boolean} b new value for countOnlyLastResults
	 */
	this.setCountOnlyLastResults = function(b)
	{
		countOnlyLastResults = b;
	};

	/**
	 * @return {integer} Time spent on this problem so far, in milliseconds.
	 */
	this.getTimeElapsed = function()
	{
		return timeElapsed;
	};

	/**
	 * If {@link #getCountOnlyLastResults()} is false, return {@link #uniqueErrors},
	 * the number of unique steps with errors in this problem. Otherwise return
	 * the count of steps in {@link #stepMap} whose {@link Step#getLastResult()} is
	 * {@value StepResult#INCORRECT}.
	 * @return {number} uniqueErrors or count from stepMap
	 */
	this.getUniqueErrors = function()
	{
		if (!that.getCountOnlyLastResults())
		{
			return uniqueErrors;
		}
		var n = 0;
		for(var s in stepMap)
		{
			if(stepMap[s].getLastResult() == CTATStep.StepResult[1])   //1 INCORRECT
			{
				 n++;
			}
		}
		return n;
	};

	/**
	 * If {@link #getCountOnlyLastResults()} is false, return {@link #uniqueCorrect},
	 * the number of steps attempted without error for this problem. Otherwise return
	 * the count of steps in {@link #stepMap} whose {@link Step#getLastResult()} is
	 * {@value StepResult#CORRECT}.
	 * @return {number} uniqueCorrect or count from stepMap
	 */
	this.getUniqueCorrect = function()
	{
		if (!that.getCountOnlyLastResults())
		{
			return uniqueCorrect;
		}
		var n = 0;
		for(var s in stepMap)
		{
			if(stepMap[s].getLastResult() == CTATStep.StepResult[3])  //3 CORRECT
			{
				n++;
			}
		}
		return n;
	};

	/**
	 * @return {integer} Number of different steps on which the student never requested a hint nor erred.
	 */
	this.getUniqueCorrectUnassisted = function()
	{
		return uniqueCorrectUnassisted;
	};

	/**
	 * @return {integer} Number of different steps on which the student requested a hint but never erred.
	 */
	this.getHintsOnly = function()
	{
		var s = initialHintsOnly;
		for(var stepID in stepMap)
		{
			if (CTATStep.StepResult[2] == stepMap[stepID].getResult())  //2 : HINT
			{
				if (stepMap[stepID].getNErrors() < 1)
					++s;
			}
		}
		return s;
	};

	/**
	 * @return {integer} Number of different steps on which the student erred but never requested a hint.
	 */
	this.getErrorsOnly = function()
	{
		var s = initialErrorsOnly;
		for(var stepID in stepMap)
		{
			if (CTATStep.StepResult[1] == stepMap[stepID].getResult())  //1 : INCORRECT
			{
				if (stepMap[stepID].getNFirstHints() < 1)
					++s;
			}
		}
		return s;
	};

	/**
	 * @return {integer} uniqueSteps + stepMap.size()
	 */
	this.getUniqueSteps = function()
	{
		var result = 0;
		for(var stepID in stepMap)
		{
			result++;
		}
		return result;
	};

	/**
	 * @return {integer} number of required student steps from start to done
	 */
	this.getRequiredSteps = function()
	{
		return (requiredSteps == Number.MAX_VALUE ? 0 : requiredSteps);
	};

	/**
	 * @param {integer} nSteps new number of required student steps from start to done
	 */
	this.setRequiredSteps = function(nSteps)
	{
		requiredSteps = nSteps;
	};

	/**
	 * @return {integer} number of times a student step was graded as "correct"
	 */
	this.getCorrect = function()
	{
		return correct;
	};

	/**
	 * Format the ProblemSummary in XML for, e.g., responding to a ProblemSummaryRequest.
	 * @param {boolean} escape if true, escape XML markup characters
	 * @return {string} XML element ProblemSummary
	 */
	this.toXML = function(escape)
	{
		var attrs = ''; var children = '';
		attrs += ' ProblemName="'             + problemName             + '"';
        attrs += ' CompletionStatus="'        + completionStatus        + '"';
		if(showCounts)
		{
			attrs += ' Correct="'                 + correct                 + '"';
			attrs += ' UniqueCorrect="'           + that.getUniqueCorrect() + '"';
			attrs += ' UniqueCorrectUnassisted="' + uniqueCorrectUnassisted + '"';
			attrs += ' Hints="'                   + hints                   + '"';
			attrs += ' UniqueHints="'             + uniqueHints             + '"';
			attrs += ' HintsOnly="'               + that.getHintsOnly()     + '"';
			attrs += ' Errors="'                  + errors                  + '"';
			attrs += ' UniqueErrors="'            + that.getUniqueErrors()  + '"';
			attrs += ' ErrorsOnly="'              + that.getErrorsOnly()    + '"';
			attrs += ' UniqueSteps="'             + that.getUniqueSteps()   + '"';
			attrs += ' RequiredSteps="'           + that.getRequiredSteps() + '"';
		}
		attrs += ' TimeElapsed="'             + timeElapsed             + '"';

		if(showCounts)
		{
			children = (that.getSkills() ? that.getSkills().toXML(escape) : "");
		}
		if(escape)
		{
			return '&lt;ProblemSummary' + attrs + '&gt;' + children + '&lt;/ProblemSummary&gt;';
		}
		else
		{
			return    '<ProblemSummary' + attrs + '>'    + children +    '</ProblemSummary>';
		}
	};

	/**
	 * Getter for pSummarySkills and values of the problem.
	 * Depending on when its called, could have different fields filled in.
	 * @return {CTATSkills} pSummarySkills
	 */
	this.getSkills = function()
	{
		return pSummarySkills;
	};

	/**
	 * Replace the skill information for this summary.
	 * @param {CTATSkills} givenSkills
	 * @return {undefined}
	 */
	this.setSkills = function(givenSkills)
	{
		pSummarySkills = givenSkills;
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
		that.ctatdebug("addHint("+stepID+")");

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
			trial = new CTATStep(stepID, CTATStep.StepResult[2]); //2 : HINT
			stepMap[stepID] = trial;
			uniqueHints++;
		}

		hints++;

		trial.setLastResult(CTATStep.StepResult[2]); //2 : HINT
		that.ctatdebug("exiting addHint() hint count "+hints+", trial "+trial);
	};

	/**
	 * Record that the tutor evaluated the student's attempt on the given step as incorrect.
	 * @param {String} stepID a unique ID for the step to which the error was charged
	 * @return {undefined}
	 */
	this.addError = function(stepID)
	{
		that.ctatdebug("addError("+stepID+")");

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
			trial = new CTATStep(stepID, CTATStep.StepResult[1]); //1 : INCORRECT
			stepMap[stepID] = trial;
			uniqueErrors++;
		}

		errors++;

		trial.setLastResult(CTATStep.StepResult[1]); //1 : INCORRECT
		that.ctatdebug("exiting addError() error count "+errors+", trial "+trial);
	};

	/**
	 * Record that the tutor evaluated the student's attempt on the given step as correct.
	 * @param {String} stepID a unique ID for the step
	 * @return {undefined}
	 */
	this.addCorrect = function(stepID)
	{
		var trial = stepMap[stepID]; //of type CTATStep
		that.ctatdebug("entering addCorrect("+stepID+") trial "+trial);

		if(trial)
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
			that.ctatdebug("to call CTATStep("+stepID+", StepResult "+CTATStep.StepResult[3]);
			trial = new CTATStep(stepID, CTATStep.StepResult[3]); //3 : CORRECT
			stepMap[stepID] = trial;
			uniqueCorrect++;
			uniqueCorrectUnassisted++;
		}

		correct++;

		trial.setLastResult(CTATStep.StepResult[3]); // : CORRECT
		that.ctatdebug("exiting addCorrect() correct count "+correct+", trial "+trial);
	};

	this.setShowCounts = function(show)
	{
		showCounts = show;
	};

	/**
	 * @param {string} givenCompletionStatus new value for completionStatus
	 * @param {boolean} canRevert true if allowed to change CompletionValue complete to
	 * CompletionValue incomplete
	 * @return {undefined}
	 */
	this.setCompletionStatus = function(givenCompletionStatus, canRevert)
	{
		that.ctatdebug("setCompletionStatus(" + givenCompletionStatus + ", " + canRevert + ")");

		//1 : complete
		if(CTATMsgType.CompletionValue[1] == completionStatus)
		{
			if(!canRevert)
			{
				return; // no-op if already complete and can't go back
			}
		}

		completionStatus = givenCompletionStatus;
	};

	/**
	 * @return completionStatus
	 */
	this.getCompletionStatus = function()
	{
		return completionStatus;
	};


/****************************** PUBLIC METHODS ****************************************************/


/****************************** CONSTRUCTOR CALLS ****************************************************/

};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATProblemSummary.prototype = Object.create(CTATBase.prototype);
CTATProblemSummary.prototype.constructor = CTATProblemSummary;

if(typeof module !== 'undefined')
{
	module.exports = CTATProblemSummary;
}
