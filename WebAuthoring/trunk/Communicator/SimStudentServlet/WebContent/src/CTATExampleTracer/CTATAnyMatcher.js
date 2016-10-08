/* This object represents a CTATAnyMatcher */

goog.provide('CTATAnyMatcher');
goog.require('CTATMatcher');

/* LastModify: Dhruv Chand 07/31*/

/*
 * @param {integer} vector 
 * @param {string} value 
 */
CTATAnyMatcher = function(vector, value)
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

    //calling the constructor of the object we are inheriting from: CTATMatcher
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
     * @return {string}
     */
    this.toString = function()
    {
        return "this is CTATAnyMatcher.";
    };


    this.matchAction = function(action)
    {
        return true;
    };

    this.matchSelection = function(action)
    {
        return true;
    };

    this.matchInput = function(action)
    {
        return true;
    };

    this.matchSingle = function(str)
    {
        return true;
    };

    this.match = function(selection, action, input)
    {
        return true;
    };

/****************************** PUBLIC METHODS ****************************************************/


/****************************** CONSTRUCTOR CALLS ****************************************************/

};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATAnyMatcher.prototype = Object.create(CTATMatcher.prototype);
CTATAnyMatcher.prototype.constructor = CTATAnyMatcher;

if(typeof module !== 'undefined')
{
    module.exports = CTATAnyMatcher;
}
