/* This object represents an CTATExactMatcher */

goog.provide('CTATExactMatcher');
goog.require('CTATMatcher');

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * @param {integer} vector
 * @param {string} value
*/
CTATExactMatcher = function(vector, value)
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	//calling the constructor fo the super class from where we are inheriting
	CTATMatcher.call(this, true, vector, true); 

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	/**
     * Make the object available to private methods
     */
	var that = this;

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @param {array} selection
	 * @param {array} action
	 * @param {array} input
	 * @param {string} actor
	 * @return {boolean}
	 */
	this.match = function(selection, action, input, actor)
	{
		//ctatdebug("CTATExactMatcher --> in match");

		//we need to do this check because of function overloading in Matcher.java
		//one of the match function declarations calls matchForHint
		//this happens when there is no input variable
		if(input === null || typeof(input) === 'undefined')
		{
			return that.matchForHint(selection, action, actor);
		}

		//To make up for the other match() call in ExactMatcher.java
		if(actor === null || typeof(actor) === 'undefined')
		{
			actor = that.getActor();
		}

		var comparisonValue = false;

		var actualInput = input[0];
		var actualSelection = selection[0];
		var actualAction = action[0];

		var expectedInput = that.getInput()[0];
		var expectedSelection = that.getSelection()[0];
		var expectedAction = that.getAction()[0];

		if(that.getCaseInsensitive())
		{
			comparisonValue = (expectedInput.toString().toUpperCase() === actualInput.toString().toUpperCase() && expectedSelection.toString().toUpperCase() === actualSelection.toString().toUpperCase() && expectedAction.toString().toUpperCase() === actualAction.toString().toUpperCase());
		}
		else
		{
			comparisonValue = (expectedInput.toString() === actualInput.toString() && expectedSelection.toString() === actualSelection.toString() && expectedAction.toString() === actualAction.toString());
		}

		comparisonValue = comparisonValue && that.matchActor(actor);

		//ctatdebug("CTATExactMatcher --> out of match");
		return comparisonValue;
	};

	/**
	 * @param {array} selection
	 * @param {array} action
	 * @param {string} actor
	 * @return {boolean}
	 */
	this.matchForHint = function(selection, action, actor)
	{
		//ctatdebug("CTATExactMatcher --> in matchForHint");

		if(that.matchActor(actor) === false)
		{
			return false;
		}

		if(selection === null || typeof(selection) === 'undefined')
		{
			return false;
		}

		if(selection[0] === null || typeof(selection[0]) === 'undefined')
		{
			return false;
		}

		var actualSelection = selection[0].toString();
		var expectedSelection = that.getSelection().toString();
		var caseInsensitve = that.getCaseInsensitive();
		var matchesSelection = (caseInsensitve) ? (expectedSelection.toString() === actualSelection.toString()) : (expectedSelection.toString().toUpperCase() === actualSelection.toString().toUpperCase());
	
		if(matchesSelection === false)
		{
			return false;
		}

		var matchesAction = true;

		if(action !== null && typeof(action) !== 'undefined' &&  action[0] !== null && typeof(action[0]) !== 'undefined')
		{
			var actualAction = action[0].toString();
			var expectedAction = that.getAction().toString();

			matchesAction = (caseInsensitve) ? (expectedAction.toString() === actualAction.toString()) : (expectedAction.toString().toUpperCase() === actualAction.toString().toUpperCase());
	
		}

		//ctatdebug("CTATExactMatcher --> out of matchForHint");
		return matchesAction;
	};

	/**
	 * For //ctatdebugging
	 * @return {string}
	 */
	this.toString = function()
	{
		//ctatdebug("CTATExactMatcher --> in toString");

		if(that.getSingleBool() === true)
		{
			//ctatdebug("CTATExactMatcher --> in toString if branch");

			return that.getSingleStr();
		}
		else
		{
			//ctatdebug("CTATExactMatcher --> in toString else branch");
			return CTATMatcher.prototype.toString.call(this); //calling toString() fo super class
		}
	};

/****************************** CONSTRUCTOR CALLS ****************************************************/
	
	//constructor calls
	this.setSingle(value);	
};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATExactMatcher.prototype = Object.create(CTATMatcher.prototype);
CTATExactMatcher.prototype.constructor = CTATExactMatcher;

if(typeof module !== 'undefined')
{
	module.exports = CTATExactMatcher;
}
     