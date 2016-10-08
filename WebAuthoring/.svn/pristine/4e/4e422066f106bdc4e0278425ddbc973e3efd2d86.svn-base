/* This object represents an CTATExampleTracerPathComparator */

goog.provide('CTATExampleTracerPathComparator');
goog.require('CTATBase');

//goog.require('CTATExampleTracerLink');//
//goog.require('CTATExampleTracerPath');//

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * @constructor
 */
CTATExampleTracerPathComparator = function ()
{
	CTATBase.call(this, "CTATExampleTracerPathComparator","");

	/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


	/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	/**
     * Make the object available to private methods
     */
	var that = this;

	/***************************** PRIVATE METHODS *****************************************************/
	

	/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @param {CTATExampleTracerPath} p1
	 * @param {CTAExampleTracerPath} p2
	 * @return {integer} If and only if x is a better path than y return x<y
	 */
	this.compare = function(p1, p2)
	{
		//ctatdebug("CTATExampleTracerPathComparator --> in compare");

		//ctatdebug("CTATExampleTracerPathComparator --> in compare p1.isDonePath(): " + p1.isDonePath());
		//ctatdebug("CTATExampleTracerPathComparator --> in compare p2.isDonePath(): " + p2.isDonePath());

		if(p1.isDonePath() === true && p2.isDonePath() === false)
		{
			return -1;
		}
		else if(p2.isDonePath() === true && p1.isDonePath() === false) // done path
		{
			return 1;
		}
 
 		//ctatdebug("CTATExampleTracerPathComparator --> in compare p2.isIncorrectPath(): " + p2.isIncorrectPath());
		//ctatdebug("CTATExampleTracerPathComparator --> in compare p1.isIncorrectPath()(): " + p1.isIncorrectPath());

		if(p2.isIncorrectPath() === true && p1.isIncorrectPath() === false)  // not incorrect
		{
			return -1;
		}
		else if(p1.isIncorrectPath() === true && p2.isIncorrectPath() === false)
		{
			return 1;
		}

		//ctatdebug("CTATExampleTracerPathComparator --> in compare p1.getNumberOfPreferredPrefixLinks(): " + p1.getNumberOfPreferredPrefixLinks());
		//ctatdebug("CTATExampleTracerPathComparator --> in compare p2.getNumberOfPreferredPrefixLinks(): " + p2.getNumberOfPreferredPrefixLinks());

		var i1 = p1.getNumberOfPreferredPrefixLinks();
		var i2 = p2.getNumberOfPreferredPrefixLinks();

		if(i1 > i2)
		{
			return -1; // longer initial sequence of preferred links
		}
		else if(i1 < i2)
		{
			return 1;
		}


		//ctatdebug("CTATExampleTracerPathComparator --> in compare p1.getNumberOfSuboptimalLinks(): " + p1.getNumberOfSuboptimalLinks());
		//ctatdebug("CTATExampleTracerPathComparator --> in compare p2.getNumberOfSuboptimalLinks(): " + p2.getNumberOfSuboptimalLinks());


		i1 = p1.getNumberOfSuboptimalLinks();
		i2 = p2.getNumberOfSuboptimalLinks();

		if(i1 < i2)
		{
			return -1;
		}
		else if(i1 > i2) // fewer suboptimal links
		{
			return 1; 
		}

		i1 = p1.getNumberOfPreferredLinks();
		i2 = p2.getNumberOfPreferredLinks();

		if(i1 > i2)
		{
			return -1;  // longer total count of preferred links
		}
		else if(i1 < i2)
		{
			return 1;
		}

		i1 = p1.getLinks().size;
		i2 = p2.getLinks().size;

		//ctatdebug("CTATExampleTracerPathComparator --> in compare i1: " + i1);
		//ctatdebug("CTATExampleTracerPathComparator --> in compare i2: " + i2);

		if(i1 < i2)
		{
			return -1; // shorter path
		}
		else if(i1 > i2)
		{
			return 1;
		}

		//ctatdebug("CTATExampleTracerPathComparator --> out of comapre, about to call breakByLowerLinkID");

		return that.breakByLowerLinkID(p1,p2);
	};


	/**
	 * @param {CTATExampleTracerPath} i1 
	 * @param {CTATExampleTracerPath} i2
	 * @return {integer}
	 */
	this.breakByLowerLinkID = function(i1, i2)
	{
		//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID");

		//Jonathan says we need sorted links not links here
		var links1 = i1.getSortedLinks(); //array of CTATExampleTracerLink
		var links2 = i2.getSortedLinks(); //array of CTATExampleTracerLink

		var link1 = null;
		var link2 = null;

		for(var i = 0; i < links1.length && i < links2.length; i++)
		{
			link1 = links1[i];
			link2 = links2[i];

			//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link1.getUniqueID(): " + link1.getUniqueID());
			//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link2.getUniqueID(): " + link2.getUniqueID());

			if(link1.getUniqueID() !== link2.getUniqueID())
			{
				break; //break out of the loop
			}
		}

		//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link1: " + (link1 === null || typeof(link1) === 'undefined'));
		//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link2: " + (link2 === null || typeof(link2) === 'undefined'));


		//Not testing for null as the list of matched links has to at least contain one link
		if(link1 === null || typeof(link1) === 'undefined')
		{
			return -1;
		}
		else if(link2 === null || typeof(link2) === 'undefined')
		{
			return 1;
		}
		else if(link1.getUniqueID() < link2.getUniqueID())
		{
			//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link1.getUniqueID(): " + link1.getUniqueID());
			//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link2.getUniqueID(): " + link2.getUniqueID());

			return -1;
		}
		else
		{
			//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link1.getUniqueID(): " + link1.getUniqueID());
			//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link2.getUniqueID(): " + link2.getUniqueID());
			return 1;
		}

		//ctatdebug("CTATExampleTracerPathComparator --> out of breakByLowerLinkID");
	};

	/****************************** PUBLIC METHODS ****************************************************/
};

CTATExampleTracerPathComparator.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerPathComparator.prototype.constructor = CTATExampleTracerPathComparator;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerPathComparator;
}