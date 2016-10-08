/* This object represents a CTATRegexMatcher */

goog.provide('CTATRegexMatcher');
goog.require('CTATMatcher');

/* LastModify: Dhruv Chand 07/31*/

/*
 * @param vector of type integer
 * @param value of type string
 */
CTATRegexMatcher = function(vector, value)
{

    /**************************** PUBLIC INSTACE VARIABLES ******************************************************/

    //calling the constructor of the object we are inheriting from: CTATMatcher
    CTATMatcher.call(this, true, vector, true);

    /**************************** PRIVATE INSTACE VARIABLES ******************************************************/

    var singlePattern = null; //of type string
    var that = this; // used to make the object available to the private methods
    var inputPattern;
    var selectionPattern;
    var actionPattern;
    var inputPatternObj;
    var selectionPatternObj;
    var actionPatternObj;
    var singlePatternObj;
    var inputString;
	var singleString;

    /***************************** PRIVATE METHODS *****************************************************/

    /***************************** PRIVILEDGED METHODS *****************************************************/

    /**
     * @return a string
     */
    this.toString = function()
    {
        return singleString; //Workaround. ToString is expected to echo the input back.
    };

    /**
     * @param givenSinglePattern of type string
     * @return undefined
     */
    this.setSinglePattern = function(pattern)
    {
        if ( typeof (pattern) === 'undefined' || pattern === null)
        {
            return;
        }
        that.singlePattern = pattern;
        that.singlePatternObj = new RegExp(pattern);
    };
    this.setSingle = function(pattern)
    {
        that.setSinglePattern(pattern);
    };
    
    /**
     * 
     */
    this.setInputPattern = function(pattern)
    {
        that.inputPattern = pattern;
        that.inputPatternObj = new RegExp(pattern);
    };

    /**
     *
     */
    this.setActionPattern = function(pattern)
    {
        that.actionPattern = pattern;
        that.actionPatternObj = new RegExp(pattern);
    };

    /**
     * 
     */
    this.setSelectionPattern = function(pattern)
    {
        that.selectionPattern = pattern;
        that.selectionPatternObj = new RegExp(pattern);
    };

    this.getActionPattern = function()
    {
        return that.actionPattern;
    };

    this.getSelectionPattern = function()
    {
        return that.selectionPattern;
    };

    this.getInputPattern = function()
    {
        return that.inputPattern;
    };


    this.matchAction = function(action)
    {
        return that.actionPatternObj.test(action[0]);
    };

    this.matchSelection = function(action)
    {
        return that.selectionPatternObj.test(selection[0]);
    };

    this.matchInput = function(action)
    {
        return that.inputPatternObj.test(action[0]);
    };

    this.matchSingle = function(str)
    {
		singleString = str;
        return that.singlePatternObj.test(str);
    };

    this.match = function(selection, action, input)
    {
        return (that.matchInput(input) && that.matchSelection(selection) && that.matchAction(action));
    };

    /****************************** PUBLIC METHODS ****************************************************/


    /****************************** CONSTRUCTOR CALLS ****************************************************/

    //call from the constructor
    this.setSinglePattern(value);
};

CTATRegexMatcher.prototype = Object.create(CTATMatcher.prototype);
CTATRegexMatcher.prototype.constructor = CTATRegexMatcher;

if(typeof module !== 'undefined')
{
    module.exports = CTATRegexMatcher;
}
