/* This object represents a CTATRangeMatcher */

goog.provide('CTATRangeMatcher');
goog.require('CTATExampleTracerException');
goog.require('CTATSingleMatcher');

/* Author: Dhruv Chand 07/31 */

/**
 * @param vector (of type integer)
 * @param value (of type string)
 */
CTATRangeMatcher = function(vector, value)
{
    //calling the constructor fo the super class from where we are inheriting
    CTATSingleMatcher.call(this, vector, true);

    /**************************** PUBLIC INSTACE VARIABLES ******************************************************/

    /**************************** PRIVATE INSTACE VARIABLES ******************************************************/

    var that = this; // used to make the object available to the private methods

	var minimum = 0;   // lower end of the interval

	var maximum = 0;   // upper end of the interval

    /***************************** PRIVATE METHODS *****************************************************/


    /***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @return {string} of form "[ min , max ]"
	 */
    this.toString = function()
    {
        return "[ "+minimum+" , "+maximum+" ]";
    };

    this.setMinimum = function(minVal)
    {
        try
        {
            minimum = parseFloat(minVal);
        }
        catch (e)
        {
            throw new CTATExampleTracerException("Number format exception while parsing minimum value \""+minVal+"\": "+e);
        }

    };

    this.setMaximum = function(maxVal)
    {
        try
        {
            maximum = parseFloat(maxVal);
        }
        catch (e)
        {
            throw new CTATExampleTracerException("Number format exception while parsing maximum value \""+maxVal+"\": "+e);
        }

    };

	/**
	 * @param {string} s convert to number and test
	 * @return {boolean} true if number result is in interval [minimum, maximum]
	 */
    this.matchSingle = function(s)
    {
        var answer;
        try
        {
           answer = parseFloat(s);
        }
        catch (e)
        {
            throw new CTATExampleTracerException("Number format exception while parsing .");
        }

        if (minimum <= answer && answer <= maximum)
            return true;
        else
            return false;
    };

/****************************** PUBLIC METHODS ****************************************************/
};

//we are inheriting from the CTATSingleMatcher object
CTATRangeMatcher.prototype = Object.create(CTATSingleMatcher.prototype);
CTATRangeMatcher.prototype.constructor = CTATRangeMatcher;

/**
 * @param {string} expectedValue the expected value to match
 * @param {string} paramName optional name of the parameter to set
 */
CTATRangeMatcher.prototype.setParameter = function(expectedValue, paramName)
{
	switch((paramName ? paramName : "").toString().trim().toLowerCase())
	{
	case "minimum":
		this.setMinimum(expectedValue);
		return;
	case "maximum":
		this.setMaximum(expectedValue);
		return;
	default:
		console.log("CTATRangeMatcher.setParameter() undefined parameter name \""+paramName+"\"");
		this.setMinimum(expectedValue);
		this.setMaximum(expectedValue);
		return;
	}
};

/**
 * @param selection (of type array)
 * @param action (of type array)
 * @param input (of type array)
 * @param actor (of type string)
 * @return boolean
 */
CTATRangeMatcher.prototype.match = function(selection, action, input, actor)
{
	return this.matchSingle(selection, action, input, actor);
};

if(typeof module !== 'undefined')
{
    module.exports = CTATRangeMatcher;
}
