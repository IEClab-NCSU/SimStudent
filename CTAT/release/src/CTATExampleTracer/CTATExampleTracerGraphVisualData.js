/** For graphical representation. */

goog.provide('CTATExampleTracerGraphVisualData');
goog.require('CTATBase');

//goog.require('CTATExampleTracerGraph');//

/**
 * @constructor
 * @augments CTATBase
 */
CTATExampleTracerGraphVisualData = function ()
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	//calling the constructor of the super class
	CTATBase.call(this, "CTATExampleTracerGraphVisualData","visualdata");

/**************************** PUBLIC INSTANCE VARIABLES ******************************************************/

/**************************** PRIVATE INSTANCE VARIABLES ******************************************************/

};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATExampleTracerGraphVisualData.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerGraphVisualData.prototype.constructor = CTATExampleTracerGraphVisualData;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerGraphVisualData;
}