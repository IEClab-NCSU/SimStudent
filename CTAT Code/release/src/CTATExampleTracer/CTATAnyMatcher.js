/* This object represents a CTATAnyMatcher */

goog.provide('CTATAnyMatcher');
goog.require('CTATSingleMatcher');

/* LastModify: Dhruv Chand 07/31*/

/*
 * @param {integer} vector
 * @param {string} value
 */
CTATAnyMatcher = function(vector, value)
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

    //calling the constructor of the object we are inheriting from: CTATSingleMatcher
	CTATSingleMatcher.call(this, vector, true);


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
        return "*";
	};

	/**
	 * Test -- always matches.
	 * @param {string} s
	 * @return {boolean} constant true
	 */
	this.matchSingle = function(s)
	{
		return true;
    };

};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATAnyMatcher.prototype = Object.create(CTATSingleMatcher.prototype);
CTATAnyMatcher.prototype.constructor = CTATAnyMatcher;

if(typeof module !== 'undefined')
{
    module.exports = CTATAnyMatcher;
}
