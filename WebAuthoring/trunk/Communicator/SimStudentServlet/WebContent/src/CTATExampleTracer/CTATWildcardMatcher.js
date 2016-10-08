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
    CTATRegexMatcher.call(vector,value);

    /**************************** PUBLIC INSTACE VARIABLES ******************************************************/

    /**************************** PRIVATE INSTACE VARIABLES ******************************************************/

    var that = this; // used to make the object available to the private methods
    var simpleSinglePattern;
    /***************************** PRIVATE METHODS *****************************************************/


    /***************************** PRIVILEDGED METHODS *****************************************************/



    this.toString = function()
    {
        return "This is a WildCard Matcher";
    };

    this.setSinglePattern = function(pattern)
    {
        that.simpleSinglePattern = pattern;
        that.prototype.setSinglePattern.call(that,converToFullRegex(pattern));
    };

    function converToFullRegex(inputPattern)
    {
        if(inputPattern === null || typeof(inputPattern) === undefined)
            return null;
        inputPattern =  inputPattern.replace(new RegExp("\\."), "\\."); //copying a comment from java - "What is this supposed to do?" 
        inputPattern =  inputPattern.replace(new RegExp("\\*","g"), "\\.\\*");
        inputPattern = inputPattern.replace("\\.\\.\\*", "\\.\\*");
        
        return inputPattern;
    }

/****************************** CONSTRUCTOR CALLS ****************************************************/
//constructor calls
this.setSingle(value);
this.simpleSinglePattern = value;
/****************************** PUBLIC METHODS ****************************************************/
};

//we are inheriting from the CTATMatcher object
CTATWildcardMatcher.prototype = Object.create(CTATRegexMatcher.prototype);
CTATWildcardMatcher.prototype.constructor = CTATWildcardMatcher;

if(typeof module !== 'undefined')
{
    module.exports = CTATWildcardMatcher;
}
