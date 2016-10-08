/* This object represents an CTATExampleTracerInterpretation */

goog.provide('CTATExampleTracerInterpretation');
goog.require('CTATBase');
goog.require('CTATVariableTable');
goog.require('CTATExampleTracerLink');

//goog.require('CTATExampleTracerSAI');//
//goog.require('CTATExampleTracerPath');//

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * @param {set of CTATExampleTracerPaths} givenValidPaths
 */
CTATExampleTracerInterpretation = function(givenValidPaths) 
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	CTATBase.call(this, "CTATExampleTracerInterpretation","");
	
/**************************** PRIVATE INSTACE VARIABLES ******************************************************/
	
	/**
	 * Links traversed, in the order traversed--including duplicates if traversed more than once.
	 * @type {array of CTATExampleTracerLink} 
	 */
	var traversedLinks = [];

	/**
	 * A map <linkID, traversalCount> to support fast implementations of isVisited(int)
	 * Object that will represent a Map<Integer, int[]>
	 * @type {map <linkID, traversalCount> } 
	 */
	var linkIdTraversalCountMap = {};

	/**
	 * @type {CTATVariableTable} 
	 */
	var vt = new CTATVariableTable();

	/**
	 * Paths valid with the traversedLinks so far.
	 * @type {set of CTATExampleTracerPath} 
	 */	
	var validPaths = new Set();
	givenValidPaths.forEach(function(path)
	{
		validPaths.add(path); //adding the given paths to the validPaths
	});

	/**
	 * The type of the least-good link in traversedLinks. See getType().
	 * @type {string} 
	 */
	var worstLinkType = CTATExampleTracerLink.CORRECT_ACTION;

	/**
     * Make the object available to private methods
     */
	var that = this;

/***************************** PRIVATE METHODS *****************************************************/

	/** 
	 * Generate the variable name for a link variable.
	 * @param {CTATExampleTracerLink} link
	 * @param {string} s - uffix ("selection", "action", e.g.)
	 * @return {string} "link" + linkID + "." + s
	 */
	function nameLink(link, s)
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in nameLink");

		return "link" + link.getUniqueID() + "." + s; //tested and works
	}

/***************************** PRIVILEDGED METHODS *****************************************************/
	
	/**
	 * @return {CTATVariableTable}
	 */
	this.getVariableTable = function ()
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in getVariableTable");

		return vt;
	};

	/**
	 * @param {CTATariableTable} givenVt 
	 * @return {undefined}
	 */
	this.setVariableTable = function (givenVt)
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in setVariableTable "+ (givenVt === undefined || givenVt === null));
	
		vt = givenVt;

		//ctatdebug("CTATExampleTracerInterpretation --> out of setVariableTable");
	};

	/**
	 * @param {CTATExampleTracerLink} target
	 * @return {integer}
	 */
	this.getTraversalCount = function (target)
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in getTraversalCount");

		//traversal count: array of integers
		//linkIdTraversalCountMap is not an array: its an object and that is how we access the proeprty
		var traversalCount = linkIdTraversalCountMap[target.getUniqueID()]; //the addition of the IDs should be done in the same way

		return (typeof(traversalCount) === 'undefined' || traversalCount === null ? 0 : traversalCount[0]);
	};

	/**
	 * @return {set of CTATExampleTracerPath}
	 */
	this.getPaths = function ()
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in getPaths");

		//ctatdebug("getPaths "+ (validPaths === undefined || validPaths === null));

		return validPaths;
	};

	/**
	 * @return {CTATExampleTracerInterpretation}
	 */
	this.clone = function ()
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in clone");

		var interp = new CTATExampleTracerInterpretation(validPaths);

		interp.addTraversedLinks(traversedLinks);

		for(var entry in linkIdTraversalCountMap)
		{
			if(linkIdTraversalCountMap.hasOwnProperty(entry))
			{
				interp.putInlinkIdTraversalCountMap(entry, linkIdTraversalCountMap[entry].slice());
			}
		}

		interp.setWorstLinkType(worstLinkType);

		//ctatdebug(" after setWorstLinkType");

		//ctatdebug("VT: " + (vt === undefined || vt === null));
		////console.log( vt);

		var vtCopy = vt.clone(); //returns a CTATVariableTableObject

		interp.setVariableTable(vtCopy);

		//ctatdebug("CTATExampleTracerInterpretation --> out of clone");

		return interp;
	};

	/**
	 * Setter for private property. The method takes an array
	 * and concatenates it at the end of the current array
	 * Used to mimic the addAll function for ArrayLists in Java
	 * @param {array of CTATExampleTracerLink} givenTraversedLinks
	 * @return {undefined}
	 */
	this.addTraversedLinks = function (givenTraversedLinks)
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in addTraversedLinks");

		traversedLinks = traversedLinks.concat(givenTraversedLinks);
	};

	/**
	 * Method that subsitutes for the put() method in Maps in Java
	 * @param {integer} key
	 * @param {array of integers} value
	 * @return {undefined}
	 */
	this.putInlinkIdTraversalCountMap = function (key, value)
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in putInlinkIdTraversalCountMap");

		linkIdTraversalCountMap[key] = value;
	};

	/** 
	 * Setter for private properties
	 * @param {string} givenWorstLinkType
	 * @return {undefined}
	 */
	this.setWorstLinkType = function(givenWorstLinkType)
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in setWorstLinkType");

		worstLinkType = givenWorstLinkType;
	};

	/**
	 * Adds the link to the path of interpretation
	 * @param {CTATExampleTracerLink} link
	 * @return {undefined}
	 */
	this.addLink = function (link)
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in addLink");

		traversedLinks.push(link);

		var matcher = link.getMatcher();

		var increment = (matcher === null || typeof(matcher) === 'undefined' ? 1 : matcher.getTraversalIncrement());

		var traversalCount = linkIdTraversalCountMap[link.getUniqueID()];
	
		if(traversalCount !== null && typeof(traversalCount) !== 'undefined')
		{
			traversalCount[0] += increment;
		}
		else
		{
			//to me this is more understandable
			traversalCount = [];
			traversalCount.push(increment);

			linkIdTraversalCountMap[link.getUniqueID()] = traversalCount;
		}

		var linkType = link.getType();

		if(CTATExampleTracerLink.compareLinkTypes(worstLinkType, linkType) < 0)
		{
			worstLinkType = linkType;
		}

		//ctatdebug("CTATExampleTracerInterpretation --> out of addLink");
	};

	/**
	 * Set the student SAI variables (linkN.selection, linkN.action, etc.) for this link.
	 * Also for correct or suboptimal links, set the selection variable.
	 * @param {CTATExampleTracerSAI} student_sai
	 * @param {array} replacementInput
	 * @param {CTATExampleTracerLink} link 
	 * @return {undefined}
	 */
	this.updateVariableTable = function (student_sai, replacementInput, link)
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in updateVariableTable");

		var inputStr = student_sai.getInputAsString();
		if(replacementInput !== null && typeof(replacementInput) !== 'undefined' && replacementInput.length > 0)
		{
			//ctatdebug("in updateVariableTable in if condition");
			inputStr = replacementInput[0];
		}

		vt.put(nameLink(link, "selection"), student_sai.getSelectionAsString());
		vt.put(nameLink(link, "action"), student_sai.getActionAsString());
		vt.put(nameLink(link, "input"), inputStr);

		if(CTATExampleTracerLink.CORRECT_ACTION.toString().toUpperCase() === link.getActionType().toString().toUpperCase() || CTATExampleTracerLink.FIREABLE_BUGGY_ACTION.toString().toUpperCase() === link.getActionType().toString().toUpperCase())
		{
			var sel = student_sai.getSelectionAsString();
			if(sel !== null && typeof(sel) !== 'undefined' && sel.length > 0)
			{
				vt.put(sel, inputStr);
			}
		}

		//ctatdebug("CTATExampleTracerInterpretation --> out of updateVariableTable");
	};

	/**
	 * @return {CTATExampleTracerLink}
	 */
	this.getLastMatchedLink = function()
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in getLastMatchedLink");

		if(traversedLinks.length === 0)
		{
			return null;
		}

		//ctatdebug("CTATExampleTracerInterpretation --> out of getLastMatchedLink");

		return traversedLinks[traversedLinks.length - 1]; //return the last one

	};

	/**
	 * @return {array of CTATExampleTracerLink}
	 */
	this.getMatchedLinks = function ()
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in getMatchedLinks");
		return traversedLinks;
	};

	/**
	 * A characterization of the whole interpretation by its least desirable link, one of
	 * CTATExampleTracerLink.BUGGY_ACTION (worst) 
	 * CTATExampleTracerLink.FIREABLE_BUGGY_ACTION 
	 * CTATExampleTracerLink.CORRECT_ACTION (best) 
	 * @return {string} worstLinkType
	 */
	this.getType = function ()
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in getType");
		return worstLinkType;
	};

	/**
	 * Checks whether the given link has already been traversed or not. A link is traversed
	 * with respect to this interpretation if its count in linkIdTraversalCountMap
	 * is at least getMinTraversals(). 
	 * @param {CTATExampleTracerLink} link
	 * @return {boolean} true as above; false if no entry in linkIdTraversalCountMap
	 */
	this.isTraversed = function(link)
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in isTraversed");

		var traversalCount = linkIdTraversalCountMap[link.getUniqueID()];

		if(traversalCount === null || typeof(traversalCount) === 'undefined')
		{
			//ctatdebug("CTATExampleTracerInterpretation --> in isTraversed in if condition");
			return false;
		}

		//ctatdebug("CTATExampleTracerInterpretation --> in isTraversed out of if condition");

		return (traversalCount[0] >= link.getMinTraversals());
	};

	/**
	 * @return {string}
	 */
	this.toString = function()
	{
		var s = "{";

		traversedLinks.forEach(function(link)
		{
			s += link.getUniqueID() + ", ";
		});

		//take away the last ", "
		if(s.substring(s.length - 2, s.length).toString() === ", ".toString())
		{
			s = s.substring(0, s.length - 2); //removing last ", "
		}

		s += " (" + (validPaths === null || typeof(validPaths) === 'undefined' ? -1 : validPaths.size) + " paths)";
		s += " var tbl " + vt;
		s += "}";

		return s;
	};

/****************************** PUBLIC METHODS ****************************************************/

};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATExampleTracerInterpretation.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerInterpretation.prototype.constructor = CTATExampleTracerInterpretation;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerInterpretation;
}  