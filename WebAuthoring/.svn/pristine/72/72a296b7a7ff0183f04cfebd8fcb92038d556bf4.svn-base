/* This object represents an CTATGroupIterator */


goog.provide('CTATGroupIterator');
goog.require('CTATBase');
goog.require('CTATExampleTracerException');

//goog.require('CTATLinkGroup');//

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * @constructor
 * @param {CTATLinkGroup} group 
 */
CTATGroupIterator = function(group) 
{

	CTATBase.call(this, "CTATGroupIterator", group);
    console.log("CTATGroupIterator(" + group + ") begin constructor");
	
/**************************** PUBLIC INSTANCE VARIABLES ******************************************************/


/**************************** PRIVATE INSTANCE VARIABLES ******************************************************/

	/**
	 * Represents the current group
     * @type {CTATLinkGroup}
     */
	var currentGroup = group;
	
	/**
	 * Iterator with all the elements of the set of all subgroups of the current group
     * @type {Iterator}
     */
	var subgroups = group.getSubgroups();
    console.log("CTATGroupIterator() in constructor group.getSubgroups() " + subgroups + ", typeof " + typeof(subgroups) + ", values " + subgroups.values);

	var iteratorIterator = subgroups.values();
    console.log("CTATGroupIterator() in constructor iteratorIterator " + iteratorIterator + ", typeof " + typeof(iteratorIterator));

	/**
	 * Group iterator for the subgroups
     * @type {CTATGroupIterator}
     */
	var currentSubIterator = null;
	var entry = iteratorIterator.next();
	console.log("CTATGroupIterator() in constructor entry " + entry + ", typeof " + typeof(entry));
    if( !entry.done )
    {
        currentSubIterator = entry.value;
    }

	/**
     * Make the object available to private methods
     */
	var that = this;

    console.log("CTATGroupIterator() end constructor iteratorIterator " + iteratorIterator + ", typeof(iteratorIterator) " + typeof(iteratorIterator));

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

		that.ctatdebug("CTATGroupIterator --> in NEXT: " + currentGroup.getName());

		if(currentSubIterator === null || typeof(currentSubIterator) === 'undefined')
		{
			that.ctatdebug("CTATGroupIterator --> in next first if " + currentGroup.getName());
			currentGroup = null;
		}
		else if(currentSubIterator.hasNext() === true)
		{
			that.ctatdebug("CTATGroupIterator --> in next second if");
			currentGroup = currentSubIterator.next().value;
			that.ctatdebug("CTATGroupIterator --> in next second if currentGroup: " + currentGroup.getName());
		}
		else
		{
			var entry = iteratorIterator.next();
			if(!entry.done)
			{
				that.ctatdebug("CTATGroupIterator --> in next third if; entry " + entry);
				currentSubIterator = new CTATGroupIterator(entry.value);
				currentGroup = currentSubIterator.next().value;
			}
			else
			{
				that.ctatdebug("CTATGroupIterator --> in next fourth if");
				currentGroup = null;
			}
		}
		that.ctatdebug("CTATGroupIterator --> in next temp: " + temp.getName());
		return temp;
	};

/****************************** PUBLIC METHODS ****************************************************/
};

CTATGroupIterator.prototype = Object.create(CTATBase.prototype);
CTATGroupIterator.prototype.constructor = CTATGroupIterator;

if(typeof module !== 'undefined')
{
	module.exports = CTATGroupIterator;
}