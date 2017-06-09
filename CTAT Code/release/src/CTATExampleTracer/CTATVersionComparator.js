/* This object represents an CTATVersionComparator */

goog.provide('CTATVersionComparator');
goog.require('CTATBase');

/* LastModify: FranceskaXhakaj 08/21*/

/**
 * For comparing version strings. The String comparator will give undesired results for, e.g.,
 * "2.9.0" vs. "2.11.0", since the "11" sorts before the "9" in alphanumeric character ordering.
 * @constructor
 */
CTATVersionComparator = function()
{
	CTATBase.call(this, "CTATVersionComparator","");

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	/**
     * Make the object available to private methods
     */
	var that = this;

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * Compare each "."-delimited segment of 2 Strings as if the segments contain integers.
	 * @param {string} o1
	 * @param {string} o2
	 * @return {integer}: -1 if o1 is smaller, 1 if o2 is smaller, 0 if they're the same
	 */
	this.compare = function(o1, o2)
	{
		if(o1 === null || typeof(o1) === 'undefined')
		{
			return (o2 === null || typeof(o2) === 'undefined' ? 0 : -1); //nulls sort first
		}
		else if (o2 === null || typeof(o2) === 'undefined')
		{
			return 1;
		}

		var result = 0;

		//This code splits a string using the period as a delimiter
		//Such an example would be in "2.11.7" where we want to get
		//the individual numbers
		var a1 = o1.split("."); //array of strings
		var a2 = o2.split("."); //array of strings

		var i; //integer
		for(i = 0; i < Math.min(a1.length, a2.length); i++)
		{
			try
			{
				var i1 = parseInt(a1[i]);
				var i2 = parseInt(a2[i]);

				//mimicking the compareTo() method in Java
				if (i1 < i2)
				{
					result = -1;
				}
				else if (i1 > i2)
				{
					result = 1;
				}
				else
		    	{
		    		result = 0;
		    	}

				if(0 !== result)
				{
					return result;
				}
			}
			catch(e)
			{
				//mimicking the compareTo() method in Java
				if (a1[i].toString() < a2[i].toString())
				{
					result = -1;
				}
		    	else if (a1[i].toString() > a2[i].toString())
		    	{
		    		result = 1;
		    	}
		    	else
		    	{
		    		result = 0;
		    	}

				if(0 !== result)
				{
					return result;
				}
			}
		}

		if(i < a1.length)
		{
			return 1; // o1 is longer
		}

		if(i < a2.length)
		{
			return -1;  // o1 is shorter
		}

		//mimicking the compareTo() method in Java
		if (o1.toString() < o2.toString())
		{
			return -1;
		}
    	else if (o1.toString() > o2.toString())
    	{
    		return 1;
    	}
    	else
    	{
    		return 0;
    	}
	};

/****************************** STATIC METHODS ****************************************************/


/****************************** PUBLIC METHODS ****************************************************/

};


CTATVersionComparator.prototype = Object.create(CTATBase.prototype);
CTATVersionComparator.prototype.constructor = CTATVersionComparator;

/**
 * @global
 */
CTATVersionComparator.vc = new CTATVersionComparator();

if(typeof module !== 'undefined')
{
module.exports = CTATVersionComparator;
}
