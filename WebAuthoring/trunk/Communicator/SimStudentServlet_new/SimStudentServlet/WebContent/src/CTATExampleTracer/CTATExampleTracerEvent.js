/* This object represents an CTATExampleTracerEvent */

goog.provide('CTATExampleTracerEvent');
goog.require('CTATBase');
goog.require('CTATExampleTracerLink');

/* LastModify: sewall 10/31 */

/**
 * Accepts a prebuilt student SAI.
 * @constructor
 * @param {object} givenSource NOTE: not used anywhere, but we decided to keep it for future use
 * @param {CTATExampleTracerSAI} givenStudentSAI 
 */
CTATExampleTracerEvent = function(givenSource, givenStudentSAI) 
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	//calling the constructor of the super class
    CTATBase.call(this, "CTATExampleTracerEvent", (givenStudentSAI ? givenStudentSAI.toString() : "null" ));

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	/**
	 * The transaction identifier
	 * @type {string} 
	 */
	var transactionID = "";

    /**
     * True if the step matched a done step but the path includes undone steps.
     * @type {boolean}
     */
	var doneStepFailed = false;  //type 

    /**
     * The result of the trace attempt, one of NULL_MODEL.
     * @type {string}
     */
	var result = CTATExampleTracerLink.NO_MODEL; 

    /**
     * Student's selection, action, input.
     * @type {CTATExampleTracerSAI}
     */
	var studentSAI = null;

    /**
     * Tutor's selection, action, input.
     * @type {CTATExampleTracerSAI}
     */
	var tutorSAI = null;

    /**
     * The link whose type, message or skills should be reported to the student.
     * @type {CTATExampleTracerLink}
     */
	var reportableLink = null;

    /**
     * True if the attempt violated ordering constraints.
     * @type {boolean}
     */
	var outOfOrder = null; 

    /**
     * Link matches chosen ahead of time, as with tutor-performed actions.
     * @type {array of CTATExampleTracerLink}
     */
	var preloadedLinkMatches = null;
	
    /**
     * Tutor input after formula evaluation.
     * @type {array}
     */
	var evaluatedInput = null;

    /**
     * The variable settings from the reportable interpretation.
     * @type {CTATVariableTable}
     */
	var reportableVariableTable = null;

    /**
     * The current number of viable CTATExampleTracerInterpretations.
     * Value -1 if unset.
     * @type {integer}
     */
	var numberOfInterpretations = -1;
	
    /**
     * Whether this result was from the solver.
     * @type {boolean}
     */
	var fromSolver = false;
	
    /**
     * Hint texts found on the reportable link, evaluated by reportableVariableTable.
     * @type {array of strings}
     */
	var reportableHints = null;

    /**
     * Whether the current trace action needs hint texts.
     * @type {boolean}
     */
	var wantReportableHints = false;

    /**
     * Moved from CTATExampleTracerLink to here.
     * @type {string}
     */
	var interpolateSelection = null;

    /**
     * Moved from CTATExampleTracerLink to here.
     * @type {string}
     */	
	var interpolateAction = null;

    /**
     * Moved from CTATExampleTracerLink to here.
     * @type {string}
     */
	var interpolateInput = null;

    /**
     * Moved from CTATExampleTracerLink to here.
     * @type {array of strings}
     */
	var interpolatedHints = null;

	/**
     * Make the object available to private methods
     */
	var that = this;

	/**
     * Shows whether the evaluate function returns true or false.
     * @type {boolean}
     */
	var indicator = null;
/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @return {CTATExampleTracerSAI}
	 */
	this.getStudentSAI = function ()
	{
		that.ctatdebug("CTATExampleTracerEvent --> in getStudentSAI");
		return studentSAI;
	};

	/**
     * Set the student trace elements.
     * @param {CTATExampleTracerSAI} sai
     * @return {undefined}
     */
	this.setStudentSAI = function (sai)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setStudentSAI");

		//we need to make this check becuase otherwise if sai is null or undefined we might break
		if(sai !== null && typeof(sai) !== 'undefined') //this check makes up for the constructor parameter check
		{
			studentSAI = sai.clone();
		}
		
		that.ctatdebug("CTATExampleTracerEvent --> out of setStudentSAI");
	};

	/**
	 * @return {array of CTATExampleTracerLink}
	 */
	this.getPreloadedLinkMatches = function ()
	{
		that.ctatdebug("CTATExampleTracerEvent --> in getPreloadedLinkMatches");

		return preloadedLinkMatches;
	};

	/**
	 * Set the tutor's model tracing elements.
	 * @param {CTATExampleTracerSAI} sai 
	 * @return {undefined}
	 */
	this.setTutorSAI = function(sai)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setTutorSAI");

		//we need to make this check becuase otherwise if sai is null or undefined we might break
		if(sai !== null && typeof(sai) !== 'undefined') //this check makes up for the constructor parameter check
		{
			tutorSAI = sai.clone();
		}
		
		that.ctatdebug("CTATExampleTracerEvent --> out of setTutorSAI");
	};

	/**
	 * @param {integer} givenNumberOfInterpretations 
	 * @return {undefined}
	 */
	this.setNumberOfInterpretations = function (givenNumberOfInterpretations)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setNumberOfInterpretations( " + givenNumberOfInterpretations + " )");
		numberOfInterpretations = givenNumberOfInterpretations;
	};

	/**
	 * @return {string} givenTransactionID
	 */
	this.getTransactionID = function ()
	{
		that.ctatdebug("CTATExampleTracerEvent --> in getTransactionID() returning " + transactionID);
		return transactionID;
	};	

	/**
	 * @param {string} givenTransactionID
	 * @return {undefined}
	 */
	this.setTransactionID = function (givenTransactionID)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setTransactionID( " + givenTransactionID + " )");
		transactionID = givenTransactionID;
	};	

	/**
	 * @param {boolean} givenDoneStepFailed
	 * @return {undefined}
	 */
	this.setDoneStepFailed = function (givenDoneStepFailed)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setDoneStepFailed( " + givenDoneStepFailed + " )");
		doneStepFailed = givenDoneStepFailed;
	};	

	/**
	 * @param {string} givenResult  
	 * @return {undefined}
	 */
	this.setResult = function (givenResult)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setResult( " + givenResult + " )");
		result = givenResult;
	};
	
	/**
	 * @return {boolean}
	 */
	this.isSolverResult = function ()
	{
		that.ctatdebug("CTATExampleTracerEvent --> in isSolverResult");
		return fromSolver;
	};

	/**
	 * @return {string}
	 */
	this.getResult = function ()
	{
		that.ctatdebug("CTATExampleTracerEvent --> in getResult() returning " + result);
		return result;
	};

	/**
	 * @param {CTATExampleTracerLink} givenReportableLink 
	 * @return {undefined}
	 */
	this.setReportableLink = function (givenReportableLink)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setReportableLink(" + givenReportableLink + ")");
		reportableLink = givenReportableLink;

	};

	/**
	 * This event's variable table should be a clone of the best interpretation's variable table.
	 * @param {CTATVaribleTable} bestInterpVT 
	 * @return {undefined}
	 */
	this.setReportableVariableTable = function (bestInterpVT)
	{
		//ctatdebug("CTATExampleTracerEvent --> in setReportableVariableTable");
		reportableVariableTable = bestInterpVT.clone();
		//ctatdebug("CTATExampleTracerEvent --> out of setReportableVariableTable");
	};

	/**
	 * @param {array of strings} hints
	 * @return {undefined}
	 */
	this.setReportableHints = function (hints)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setReportableHints(" + hints + ")");
		reportableHints = hints;
	};

	/**
	 * @return {boolean}
	 */
	this.getWantReportableHints = function()
	{
		that.ctatdebug("CTATExampleTracerEvent --> in getWantReportableHints() returning " + wantReportableHints);
		return wantReportableHints;
	};

	/**
	 * @param {boolean} givenIsOutOfOrder 
	 * @retun {undefined}
	 */
	this.setOutOfOrder = function(givenIsOutOfOrder)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setOutOfOrder(" + givenIsOutOfOrder + ")");
		outOfOrder = givenIsOutOfOrder;
	};

	/**
	 * @return {boolean} outOfOrder
	 */
	this.isOutOfOrder = function()
	{
		that.ctatdebug("CTATExampleTracerEvent --> in getOutOfOrder() returning " + outOfOrder);
		return outOfOrder;
	};

	/**
	 * Set evaluatedInput
	 * @param {string} givenEvaluatedInput 
	 * @return {undefined}
	 */
	this.setEvaluatedInput = function(givenEvaluatedInput)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setEvaluatedInput(" + givenEvaluatedInput + ")");

		if(evaluatedInput === null || typeof(evaluatedInput) === 'undefined')
		{
			evaluatedInput = []; //this is how we declare arrays
		}

		evaluatedInput.push(givenEvaluatedInput); //evaluatedInput is an array
	};

	/**
	 * Preselect a CTATExampleTracerLinkMatch to bypass matching. Used with
	 * tutor-performed actions.
	 * @param {CTATExampleTracerLink} link Link to add
	 * @return {undefined}
	 */
	this.addPreloadedLinkMatch = function(link)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in addPreloadedLinkMatch(" + link + ")");

		if(preloadedLinkMatches === null || typeof(preloadedLinkMatches) === 'undefined')
		{
			preloadedLinkMatches = []; //array of CTATExampleTracerLink
		}

		preloadedLinkMatches.push(link);//preloadedLinkMatches is an array

	};

	/**
	 * Method now belongs to CTATExampleTracerEvent (originially belonged to EdgeData.java)
	 * @param {string} s
	 * @param {string} a
	 * @param {string} i
	 * @return {undefined}
	 */
	this.setInterpolateSAI = function(s, a, i)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setInterpolateSAI(selection " + s + " action " + a + " input " + i + ")");
		interpolateSelection = s;
        interpolateAction = a;
        interpolateInput = i;
	};

	/**
	 * Method that belongs to CTATExampleTracerEvent now, because interpolatedHints container
	 * resides in this class
	 * Set the givenInterpolatedHints to the given value
	 * @param {array of strings} givenInterpolatedHints With hints
	 */
	this.setInterpolatedHints = function(givenInterpolatedHints)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setInterpolatedHints(" + givenInterpolatedHints + ")");

		//reset the interpolated hints
		if(interpolatedHints !== null && typeof(interpolatedHints) !== 'undefined')
		{
			interpolatedHints.length = 0;
		}
		
		//set the new ones
		if(givenInterpolatedHints !== null && typeof(givenInterpolatedHints) !== 'undefined')
		{
			interpolatedHints = givenInterpolatedHints.slice(0);
		}
		
		that.ctatdebug("CTATExampleTracerEvent --> out of setInterpolatedHints");
	};

	/**
	 * @param {boolean} givenWantReportableHints 
	 */
	this.setWantReportableHints = function(givenWantReportableHints)
	{
		that.ctatdebug("CTATExampleTracerEvent --> in setWantReportableHints(" + givenWantReportableHints + ")");
		wantReportableHints = givenWantReportableHints;
	};

	/**
	 * @return {array of strings}
	 */
	this.getReportableHints = function()
	{
		that.ctatdebug("CTATExampleTracerEvent --> in getReportableHints() returning " + reportableHints);
		return reportableHints;
	};

	/**
	 * @return {CTATExampleTracerLink}
	 */
	this.getReportableLink = function()
	{
		that.ctatdebug("CTATExampleTracerEvent --> in getReportableLink() returning " + reportableLink);
		return reportableLink;
	};

	/**
	 * @return {array}
	 */
	this.getTutorSelection = function ()
	{
		return (tutorSAI === null || typeof(tutorSAI) === 'undefined' ? null : tutorSAI.getSelectionAsArray());
	};

	/**
	 * @return {array}
	 */
	this.getTutorAction = function ()
	{
		return (tutorSAI === null || typeof(tutorSAI) === 'undefined' ? null : tutorSAI.getActionAsArray());
	};

	/**
	 * @return {array} Or null
	 */
	this.getTutorInput = function()
	{
		if(evaluatedInput !== null && typeof(evaluatedInput) !== 'undefined')
		{
			return that.getEvaluatedInputAsArray();
		}
		else if (tutorSAI !== null && typeof(tutorSAI) !== 'undefined')
		{
			return tutorSAI.getInputAsArray();
		}
		else
		{
			return null;
		}
	};

	/**
	 * @return {array}
	 */
	this.getEvaluatedInputAsArray = function()
	{
		return evaluatedInput;
	};

	/**
	 * For debugging
	 * @return {string}
	 */
	this.toString = function()
	{
		var sb = "[";
		sb += that.getResult();

		if(that.getStudentSAI() !== null && typeof(that.getStudentSAI()) !== 'undefined')
		{
			sb += ", StudentSAI " + that.getStudentSAI();
		}

		if(that.getReportableLink() !== null && typeof(that.getReportableLink()) !== 'undefined')
		{
			sb += ", reportableLink " + that.getReportableLink(); //we are not keeping the edges and the action labels anymore
		}

		if(that.isDoneStepFailed() === true)
		{
			sb += ", doneStepFailed";
		}

		if(that.getTutorSAI() !== null && typeof(that.getTutorSAI()) !== 'undefined')
		{
			sb += ", TutorSAI " + that.getTutorSAI();
		}

		sb += "]";

		return sb;
	};

	/**
	 * @return {boolean} doneStepFailed
	 */
	this.isDoneStepFailed = function()
	{
		return doneStepFailed;
	};

	/*
	 * @return {CTATExampleTracerSAI}
	 */
	this.getTutorSAI = function()
	{
		return tutorSAI;
	};

/****************************** CONSTRUCTOR CALLS ****************************************************/

	//method called by the constructor
	this.setStudentSAI(givenStudentSAI);

/****************************** PUBLIC METHODS ****************************************************/

};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATExampleTracerEvent.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerEvent.prototype.constructor = CTATExampleTracerEvent;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerEvent;
} 