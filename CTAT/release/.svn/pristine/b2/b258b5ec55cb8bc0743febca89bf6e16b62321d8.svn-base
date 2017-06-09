/* This object represents an CTATGroupIterator */
/* LastModify: FranceskaXhakaj 07/14*/

/**
 * @constructor
 * @param {CTATLinkGroup} group 
 */
function CTATGroupIterator(group) 
{
	
/**************************** PUBLIC INSTANCE VARIABLES ******************************************************/


/**************************** PRIVATE INSTANCE VARIABLES ******************************************************/

	/**
	 * Represents the current group
     * @type CTATLinkGroupType
     */
	var currentGroup = group;
	
	/**
	 * Set of all subgroups of the current group
     * @type set of CTATLinkGroup
     */
	var iteratorIterator = (group !== null && typeof(group) !== 'undefined' ? group.getSubgroups() : null);
	
	/**
	 * Group iterator for the subgroups
     * @type CTATGroupIterator
     */
	var currentSubIterator = null;

	/**
	 * Counter that serves as a reference to track the subgroup that we are currently on
     * @type integer
     */
	var count = 0;

	//ctatdebug("CTATGroupIterator --> " + iteratorIterator === null);
	//ctatdebug("CTATGroupIterator --> " + iteratorIterator.size);

	//ctatdebug("================================================");
	//ctatdebug("CTATGroupIterator --> creating" + currentGroup.getName());
	//ctatdebug("CTATGroupIterator --> " + iteratorIterator.size);
	//ctatdebug("================================================");

	if(iteratorIterator !== null && typeof(iteratorIterator) !== 'undefined' && iteratorIterator.size > 0)
	{
		//ctatdebug("================================================");
		//ctatdebug("CTATGroupIterator --> creating" + currentGroup.getName());
		//ctatdebug("CTATGroupIterator --> " + iteratorIterator.size);

		var gotIt = false;
		var val = null;

		//get the first subgroup in the set
		iteratorIterator.forEach(function(el)
		{
			if(gotIt === false)
			{
				val = el;
				gotIt = true;
				return;
			}

			return;
		});

		currentSubIterator = new CTATGroupIterator(val);
		count++; //we are one step forward in iterating over the subgroups

		//ctatdebug("CTATGroupIterator --> " + currentSubIterator);
	}

	/**
     * Make the object available to private methods
     */
	var that = this;

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/
	
	/**
	 * @return {boolean} True if the current group is not null or undefined, false otherwise
	 */
	this.hasNext = function()
	{
		return (currentGroup !== null && typeof(currentGroup) !== 'undefined');
	};

	/**
	 * @return {CTATLinkGroup} The next group to iterate over
	 */
	this.next = function ()
	{
		if(currentGroup === null || typeof(currentGroup) === 'undefined')
		{
			throw new CTATExampleTracerException("NoSuchElementException");
		}

		var temp = currentGroup;

		//ctatdebug("CTATGroupIterator --> in NEXT: " + currentGroup.getName());

		if(currentSubIterator === null || typeof(currentSubIterator) === 'undefined')
		{
			//ctatdebug("CTATGroupIterator --> in next first if " + currentGroup.getName());
			currentGroup = null;
		}
		else if(currentSubIterator.hasNext() === true)
		{
			//ctatdebug("CTATGroupIterator --> in next second if");
			currentGroup = currentSubIterator.next();
			//ctatdebug("CTATGroupIterator --> in next second if currentGroup: " + currentGroup.getName());

		}
		else if(iteratorIterator !== null && typeof(iteratorIterator) !== 'undefined' && iteratorIterator.size > count)
		{
			//ctatdebug("CTATGroupIterator --> in next third if : " + iteratorIterator.size);
			//ctatdebug("CTATGroupIterator --> in next third if : " + currentGroup.getName());

			//as we do not have pointers, refernces or iterators
			//when we need a specific element of the set
			//we have to iterate over all the set to find it
			//the follwoing piece of code is implemented to mimic the Java function next() on an iterator
			//in order to find the subgroup we need to iterate over next, we keep
			//the count and use a subcount to find the appropriate one

			var val = null;
			var subCount = 1;

			iteratorIterator.forEach(function(el)
			{
				//ctatdebug("CTATGroupIterator --> in next subCount: " + subCount);
				//ctatdebug("CTATGroupIterator --> in next subCount: " + (count + 1));

				if(subCount === (count + 1))
				{
					val = el;
				}
				subCount++;
				return;
			});

			//ctatdebug("CTATGroupIterator --> in next val is null: " + (val === null));
			currentSubIterator = new CTATGroupIterator(val);
			count++; //one step forward, to the next subgroup we need to iterate over

			currentGroup = currentSubIterator.next();
		}
		else
		{
			//ctatdebug("CTATGroupIterator --> in next fourth if");
			currentGroup = null;
		}

		//ctatdebug("CTATGroupIterator --> in next temp: " + temp.getName());
		return temp;
	};

/****************************** PUBLIC METHODS ****************************************************/
}

CTATGroupIterator.prototype = Object.create(CTATBase.prototype);
CTATGroupIterator.prototype.constructor = CTATGroupIterator;

if(typeof module !== 'undefined')
{
	module.exports = CTATGroupIterator;
}