/* This object represents an CTATExampleTracerInterpretationComparator */

goog.provide('CTATExampleTracerInterpretationComparator');
goog.require('CTATBase');
goog.require('CTATMatcherComparator');
goog.require('CTATExampleTracerPath');

//goog.require('CTATExampleTracerInterpretation');//

/* LastModify: sewall 10/31 */

/**
 * Class that used to be part of ExampleTracerTracer.java.
 * We have decided to keep it in a separate file in JavaScript.
 * @constructor
 */
CTATExampleTracerInterpretationComparator = function ()
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	//calling the constructor of the super class
	CTATBase.call(this, "CTATExampleTracerInterpretationComparator","");

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	/**
     * Make the object available to private methods
     */
	var that = this; //for private functions

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @param {CTATExampleTracerInterpretation} i1
	 * @param {CTATExampleTracerInterpretation} i2
	 * @return {integer} i1>i2 iff i1 is a better interpretaion than i2
	 */
	this.compare = function(i1, i2)
	{
		that.ctatdebug("CTATExampleTracerInterpretationComparator --> in compare");

		var i1Type = i1.getType();

		var result = CTATExampleTracerLink.compareLinkTypes(i1Type, i2.getType());

		ctatdebug("CTATExampleTracerInterpretationComparator comparing i1Type: " + i1.getType());
		ctatdebug("CTATExampleTracerInterpretationComparator comparing i2Type: " + i2.getType());
		ctatdebug("CTATExampleTracerInterpretationComparator comparing link types result: " + result);

		if(result !== 0) // for link types, t1<t2 if t1 is better
		{
			ctatdebug("CTATExampleTracerInterpretationComparator result is not equal to 0");
			return (result < 0 ? 1 : -1);
		}

		// if comparing 2 incorrect, look first at the buggy links themselves
		if(CTATExampleTracerLink.BUGGY_ACTION.toString().toUpperCase() === i1Type.toString().toUpperCase())
		{
			ctatdebug("CTATExampleTracerInterpretationComparator checking for buggy actions");
			var w1 = i1.getLastMatchedLink();
			var w2 = i2.getLastMatchedLink();

			var m = CTATMatcherComparator.compare(w1.getMatcher(), w2.getMatcher());

			if(m !== 0)
			{
				return m;  // prefer more specific Matchers
			}
		}

		var p1 = CTATExampleTracerPath.getBestPath(i1.getPaths());
		var p2 = CTATExampleTracerPath.getBestPath(i2.getPaths());

		var bestPair = new Set();

		bestPair.add(p1);
		bestPair.add(p2);

		var best = CTATExampleTracerPath.getBestPath(bestPair);
		ctatdebug("CTATExampleTracerInterpretationComparator found best path: " + (best === p2));

		best.getLinks().forEach(function(el)
		{
			that.ctatdebug("My best link==: " + el.getUniqueID());
		});

		return (best === p2 ? -1 : 1);
	};

	/****************************** PUBLIC METHODS ****************************************************/
};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATExampleTracerInterpretationComparator.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerInterpretationComparator.prototype.constructor = CTATExampleTracerInterpretationComparator;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerInterpretationComparator;
}