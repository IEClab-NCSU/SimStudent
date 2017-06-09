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
		that.ctatdebug("CTATExampleTracerPathComparator --> in compare");

		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p1.isDonePath(): " + p1.isDonePath());
		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p2.isDonePath(): " + p2.isDonePath());

		if((p1.isDonePath() === true) && (p2.isDonePath() === false))
		{
			return -1;
		}
		else if((p2.isDonePath() === true) && (p1.isDonePath() === false)) // done path
		{
			return 1;
		}

 		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p2.isIncorrectPath(): " + p2.isIncorrectPath());
		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p1.isIncorrectPath()(): " + p1.isIncorrectPath());

		if((p2.isIncorrectPath() === true) && (p1.isIncorrectPath() === false))  // not incorrect
		{
			return -1;
		}
		else if((p1.isIncorrectPath() === true) && (p2.isIncorrectPath() === false))
		{
			return 1;
		}

		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p1.getNumberOfPreferredPrefixLinks(): " + p1.getNumberOfPreferredPrefixLinks());
		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p2.getNumberOfPreferredPrefixLinks(): " + p2.getNumberOfPreferredPrefixLinks());

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


		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p1.getNumberOfSuboptimalLinks(): " + p1.getNumberOfSuboptimalLinks());
		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p2.getNumberOfSuboptimalLinks(): " + p2.getNumberOfSuboptimalLinks());


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

		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p1.getNumberOfPreferredLinks(): " + p1.getNumberOfPreferredLinks());
		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p2.getNumberOfPreferredLinks(): " + p2.getNumberOfPreferredLinks());

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

		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p1.getLinks().size: " + i1);
		that.ctatdebug("CTATExampleTracerPathComparator --> in compare p1.getLinks().size: " + i2);

		if(i1 < i2)
		{
			return -1; // shorter path
		}
		else if(i1 > i2)
		{
			return 1;
		}

		that.ctatdebug("CTATExampleTracerPathComparator --> out of comapre, about to call breakByLowerLinkID");

		return that.breakByLowerLinkID(p1,p2);
	};


	/**
	 * @param {CTATExampleTracerPath} i1
	 * @param {CTATExampleTracerPath} i2
	 * @return {integer}
	 */
	this.breakByLowerLinkID = function(i1, i2)
	{
		that.ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID");

		//Jonathan says we need sorted links not links here
		var links1 = i1.getSortedLinks(); //array of CTATExampleTracerLink
		var links2 = i2.getSortedLinks(); //array of CTATExampleTracerLink

		var link1 = null;
		var link2 = null;

		var result = 0; //of type integer
		var i = 0; //index for the loop

		do
		{
			that.ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID in do while");

			if(i >= links1.length) // path 1 is shorter (both end together only if identical)
			{
				that.ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID path 1 is shorter");
				result = -1;
			}
			else if(i >= links2.length) // path 2 is shorter
			{
				that.ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID path 2 is shorter");
				result = 1;
			}
			else // get the links to compare IDs
			{
				that.ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID path are same, compare IDS");
				link1 = links1[i];
				link2 = links2[i];
			}

			i++;//increment

		} while((result === 0) && (link1.getUniqueID() === link2.getUniqueID()));

		if(result === 0)
		{
			that.ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID result is zero: " + link1.getUniqueID());
			that.ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID result is zero: " + link2.getUniqueID());

			result = (link1.getUniqueID() < link2.getUniqueID() ? -1 : 1);
		}

		return result;

		/*for(var i = 0; i < links1.length && i < links2.length; i++)
		{
			link1 = links1[i];
			link2 = links2[i];

			//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link1.getUniqueID(): " + link1.getUniqueID());
			//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link2.getUniqueID(): " + link2.getUniqueID());

			if(link1.getUniqueID() !== link2.getUniqueID())
			{
				break; //break out of the loop
			}
		}*/

		//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link1: " + (link1 === null || typeof(link1) === 'undefined'));
		//ctatdebug("CTATExampleTracerPathComparator --> in breakByLowerLinkID link2: " + (link2 === null || typeof(link2) === 'undefined'));


		//Not testing for null as the list of matched links has to at least contain one link
		/*if(link1 === null || typeof(link1) === 'undefined')
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
		}*/

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