/* This object represents an CTATExactMatcher */

goog.provide('CTATExactMatcher');
goog.require('CTATSingleMatcher');

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * @param {integer} vector
 * @param {string} value
*/
CTATExactMatcher = function(vector, value)
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	//calling the constructor fo the super class from where we are inheriting
	CTATSingleMatcher.call(this, vector, true);

/**************************** PUBLIC INSTANCE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	/**
     * Make the object available to private methods
     */
	var that = this;

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * For //ctatdebugging
	 * @return {string}
	 */
	this.toString = function()
	{
		return that.getSingle().toString();
	};

	/**
	 * Test against this.getSingle(), obeying this.getCaseInsensitive().
	 * @param {string} s
	 * @return boolean
	 */
	this.matchSingle = function(s)
	{
		var sv = that.getSingle();
		ctatdebug("CTATExactMatcher --> in matchSingle: s = "+s+", singleval = "+sv+", caseInsensitive "+that.getCaseInsensitive()+", "+typeof(that.getCaseInsensitive()));
		if(that.getCaseInsensitive())
		{
			return ((String(s)).toUpperCase() == (String(sv)).toUpperCase());
		}
		else
		{
			return (String(s) == String(sv));
		}
	};

/****************************** CONSTRUCTOR CALLS ****************************************************/

};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATExactMatcher.prototype = Object.create(CTATSingleMatcher.prototype);
CTATExactMatcher.prototype.constructor = CTATExactMatcher;

if(typeof module !== 'undefined')
{
	module.exports = CTATExactMatcher;
}
