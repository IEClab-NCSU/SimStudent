/* This object represents a CTATWildcardMatcher */

goog.provide('CTATWildcardMatcher');
goog.require('CTATRegexMatcher');

//goog.require('CTATMatcher'); //

/* LastModify: Dhruv Chand 07/31*/

/**
 * @param vector (of type integer)
 * @param value (of type string)
 */
CTATWildcardMatcher = function(vector, value)
{
    //calling the constructor of the super class from where we are inheriting
    CTATRegexMatcher.call(this, vector,value);

    /**************************** PUBLIC INSTACE VARIABLES ******************************************************/

    /**************************** PRIVATE INSTACE VARIABLES ******************************************************/

    var that = this; // used to make the object available to the private methods
    var simpleSinglePattern = null;

    /***************************** PRIVATE METHODS *****************************************************/


    /***************************** PRIVILEDGED METHODS *****************************************************/



    this.toString = function()
    {
        return simpleSinglePattern;
    };

    this.setSingle = function(pattern)
    {
        that.simpleSinglePattern = pattern;
        that.setSinglePattern.call(that,convertToFullRegex(pattern));
    };

	var questionMark = new RegExp("\\?", "g");

    function convertToFullRegex(pattern)
    {
        if(pattern == null || typeof(pattern) === "undefined")
            return null;
        var str = new String(pattern);  // make it really a string
		var qmRE = new RegExp("\\?", "g");
		var qmStr = str.replace(qmRE, ".");
		var asterRE = new RegExp("\\*", "g");
		var asterStr = qmStr.replace(asterRE, ".*");
		var negateCharSetRE = new RegExp("\\[\\!", "g");
		var result = asterStr.replace(negateCharSetRE, "[^")
		that.ctatdebug("CTATWildcardMatcher.convertToFullRegex("+pattern+") after qm: "+qmStr+"; after aster: "+asterStr+"; after negate: "+result+";");
		return result;
    }

/****************************** CONSTRUCTOR CALLS ****************************************************/
//constructor calls
this.setSingle(value);
this.simpleSinglePattern = value;
/****************************** PUBLIC METHODS ****************************************************/
};

//we are inheriting from the CTATRegexMatcher object
CTATWildcardMatcher.prototype = Object.create(CTATRegexMatcher.prototype);
CTATWildcardMatcher.prototype.constructor = CTATWildcardMatcher;

if(typeof module !== 'undefined')
{
    module.exports = CTATWildcardMatcher;
}
