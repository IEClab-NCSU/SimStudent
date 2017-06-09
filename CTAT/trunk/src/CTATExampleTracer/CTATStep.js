/* This object represents a CTATStep */

/* LastModify: Franceska Xhakaj 11/2014*/

goog.provide('CTATStep');

goog.require('CTATBase');

/**
 * Explicitly initialize required fields.
 * @param {String} givenID unique identifier for this step
 * @param {String} givenResult whether the first action on this step was a hint, error or correct
 */
CTATStep = function(givenID, givenResult)
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

    CTATBase.call(this, "CTATStep", givenID);

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

    /**
     * Unique identifier for this step within this problem.
     * @type {String}
     */
    var id = givenID;

    /**
     * Result of student's first action on this step.
     * @type {String}
     */
    var result = givenResult;

    /**
     * Number of top-level hint requests for this step.
     * @type {integer}
     */
    var nFirstHints = 0;

    /**
     * The last recorded result for this step, used for scoring with feedback suppressed.
     * @type {String}
     */
    var lastResult = null;

    /**
     * Number of correct responses on this step. In most cases this will be no more than 1.
     * @param {integer}
     */
    var nCorrect = 0;

    /**
     * Number of errors charged to this step
     * @param {integer}
     */
    var nErrors = 0;

    /**
     * Make the object available to private methods
     */
    var that = this;

	that.ctatdebug("entering CTATStep("+givenID+", "+givenResult+") constructor");

/***************************** PRIVATE METHODS *****************************************************/

/***************************** PRIVILEDGED METHODS *****************************************************/

    /**
     * Add 1 to the number of top-level hint requests.
     * @return {undefined}
     */
    this.incrementFirstHints = function()
    {
        nFirstHints++;
    };

   /**
    * Add 1 to the number of correct attempts.
    * @return {undefined}
    */
    this.incrementNCorrect = function()
    {
        nCorrect++;
    };

   /**
    * Add 1 to the number of errors.
    * @return {undefined}
    */
    this.incrementErrors = function()
    {
        nErrors++;
    };

   /**
    * @param {String} givenResult new value for lastResult
    */
    this.setLastResult = function(givenResult)
    {
        lastResult = givenResult;
    };

    /**
     * @return {String} lastResult
     */
    this.getLastResult = function()
    {
        return lastResult;
    };

    /**
     * @return {String} result
     */
    this.getResult = function()
    {
        return result;
    };

    /**
     * @return {integer} the nCorrect
     */
    this.getNCorrect = function()
    {
        return nCorrect;
    };

    /**
     * @return {integer} the nErrors
     */
    this.getNErrors = function()
    {
        return nErrors;
    };

    /**
     * @return {integer} the nFirstHints
     */

    this.getNFirstHints = function()
    {
        return nFirstHints;
    };


/****************************** PUBLIC METHODS ****************************************************/


/****************************** CONSTRUCTOR CALLS ****************************************************/

    /**
     * CTATStep.StepResult[ ]
     * 1 : INCORRECT
     * 2 : HINT
     * 3 : CORRECT
     */
	that.ctatdebug("in CTATStep("+givenID+", "+givenResult+") constructor");
    switch (givenResult)
    {
        case CTATStep.StepResult[3]:
            that.incrementNCorrect();
            break;
        case CTATStep.StepResult[1]:
            that.incrementErrors();
            break;
        case CTATStep.StepResult[2]:
            that.incrementFirstHints();
            break;
    }
	that.ctatdebug("exiting CTATStep constructor");
};

/****************************** CONSTANTS ****************************************************/

/**
 * Local value for the possible evaluations of student action with respect to a step.
 * 0 : UNTRIED - no result for this step
 * 1 : INCORRECT - a wrong answer for this step
 * 2 : HINT - a hint request for this step
 * 3 : CORRECT - a right answer for this step
 * @var {array of strings}
 */
Object.defineProperty(CTATStep, "StepResult", {enumerable: false, configurable: false, writable: false, value: ["UNTRIED", "INCORRECT", "HINT", "CORRECT"]});

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATStep.prototype = Object.create(CTATBase.prototype);
CTATStep.prototype.constructor = CTATStep;

if(typeof module !== 'undefined')
{
    module.exports = CTATStep;
}
