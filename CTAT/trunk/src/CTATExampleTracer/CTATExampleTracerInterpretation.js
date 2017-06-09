/* This object represents an CTATExampleTracerInterpretation */

goog.provide('CTATExampleTracerInterpretation');
goog.require('CTATBase');
goog.require('CTATVariableTable');
goog.require('CTATExampleTracerLink');

//goog.require('CTATSAI');//
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
	 * Default or best path (see CTATExampleTracerPath.getBestPath()) to a done state.
	 * @type {CTATExampleTracerPath}
	 */
	var pathToDone = null;

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

/***************************** PRIVILEGED METHODS *****************************************************/

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
	 * Set the variable hints in the variable table. The type is array<string>.
	 * @param array<string> interpolatedHints
	 * @param array<string> defaultHints use this if there are no interpolated hints; no-op if empty or null
	 */  
	this.setLatestHints = function(interpolatedHints, defaultHints)
	{
	    that.ctatdebug("In evaluate after calling setInterpolatedHints");
	    if(!Array.isArray(interpolatedHints) || interpolatedHints.length < 1)
			interpolatedHints = defaultHints;
	    if(!Array.isArray(interpolatedHints) || interpolatedHints.length < 1)
			return;          // do not blank a previous value
	    vt.put("hints", interpolatedHints);
	};

    /**
	 * Get the variable hints from the variable table. The type is array of string.
	 * @return array<string> value of hints variable in vt
	 */
	this.getLatestHints = function()
	{
		return vt.get("hints");
	};

    /**
	 * Set the variable buggy_message in the variable table. The type is string.
	 * @param {string} text new value; if null or empty, will not overwrite prior value
	 */
	this.setLatestBuggyMessage = function(text)
	{
		if(!text || (text = String(text)).length < 1)
			return;
		vt.put("buggy_message", text);
	};

    /**
	 * Set the variable success_message in the variable table. The type is string.
	 * @param {string} text new value; if null or empty, will not overwrite prior value
	 */
	this.setLatestSuccessMessage = function(text)
	{
		if(!text || (text = String(text)).length < 1)
			return;
		vt.put("success_message", text);
	};

    /**
	 * Get the variable buggy_message from the variable table. The type is string.
	 * @return {string} value of buggy_message variable in vt
	 */
	this.getLatestBuggyMessage = function()
	{
		return vt.get("buggy_message");
	};

    /**
	 * Get the variable success_message from the variable table. The type is string.
	 * @return {string} value of success_message variable in vt
	 */
	this.getLatestSuccessMessage = function()
	{
		return vt.get("success_message");
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

		if(pathToDone)
			interp.setPathToDone(new CTATExampleTracerPath(pathToDone.getLinks()));

		ctatdebug("CTATExampleTracerInterpretation --> out of clone");

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
		ctatdebug("CTATExampleTracerInterpretation --> in putInlinkIdTraversalCountMap");
		ctatdebug("CTATExampleTracerInterpretation --> in putInlinkIdTraversalCountMap key: " + key);
		ctatdebug("CTATExampleTracerInterpretation --> in putInlinkIdTraversalCountMap value: " + value);

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
		if(link.isTraversable() && !(pathToDone && pathToDone.getLinks().has(link)))
			pathToDone = null;           // must recalculate

		ctatdebug("CTATExampleTracerInterpretation.addLink("+(link && link.getUniqueID())+") pathToDone "+(pathToDone && pathToDone.size()));

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
	 * @param {CTATExampleTracerPath} newPathToDone
	 */
	this.setPathToDone = function(newPathToDone)
	{
		pathToDone = newPathToDone;
	};

	/**
	 * Find a path to a done state.
	 * @param {CTATExampleTracerGraph} graph
	 * @return {CTATExampleTracerPath} pathToDone, newly calculated if null
	 */
	this.getPathToDone = function(graph)
	{
		if(pathToDone)            // presume any change which invalidates will also null the pointer
			return pathToDone;

		var startNode = graph.getStartNode();
		if(startNode) {
			graph.getDoneStates().forEach(function(ds) {
				if(pathToDone)
				{              // later we could return a path to each done state
					return;
				}
				pathToDone = graph.getBestSubpath(startNode, ds, traversedLinks);
			});
		}
		return pathToDone;
	};

	/**
	 * Count the steps in the pathToDone according to the rules for calculating an interim
	 * score before problem completion.
	 * @param {CTATExampleTracerGraph} graph
	 * @return {integer} sum over all links i of max(minTraversals[i], actualTraversals[i])
	 */
	this.countStepsForScore = function(graph)
	{
		var result = Number.MAX_VALUE;  // default infinity in case no path
		var path = that.getPathToDone(graph);
		var startCountNode = graph.getStudentStartsHereNode() || graph.getStartNode();
		if(startCountNode && path && path.getLinks()) {
			var scnID = startCountNode.getNodeID();
			result = 0;
			path.getSortedLinks().forEach(function(link) {
				if(scnID > 0 && link.getPrevNode() != scnID)  // 
				{
					return;  // start count only when past start-count-node
				}
				scnID = -1;  // don't try to match again
				if(CTATMatcher.isTutorActor(link.getActor(), true))
				{
					return;
				}
				var actualTraversals = that.getTraversalCount(link);
				var minTraversals = link.getMinTraversals();
				if(actualTraversals > minTraversals)
					result += actualTraversals;
				else
					result += minTraversals;
			});
		}
		return result;
	};

	/**
	 * Set the student SAI variables (linkN.selection, linkN.action, etc.) for this link.
	 * Also for correct or suboptimal links, set the selection variable.
	 * @param {CTATSAI} student_sai
	 * @param {array} replacementInput
	 * @param {CTATExampleTracerLink} link
	 * @return {CTATSAI} the given student_sai, with changed input if replacementInput is not null
	 */
	this.updateVariableTable = function (student_sai, replacementInput, link)
	{
		//ctatdebug("CTATExampleTracerInterpretation --> in updateVariableTable");
		var saiToReturn = student_sai;

		var inputStr = student_sai.getInput();
		if(replacementInput !== null && typeof(replacementInput) !== 'undefined')
		{
			//ctatdebug("in updateVariableTable in if condition");
			inputStr = replacementInput;
			saiToReturn = new CTATSAI(student_sai.getSelection(), student_sai.getAction(), inputStr);
		}

		vt.put(nameLink(link, "selection"), student_sai.getSelection());
		vt.put(nameLink(link, "action"), student_sai.getAction());
		var inputName = nameLink(link, "input");
		vt.put(inputName, inputStr);
		vt.put(CTATVariableTable.nameAsString(inputName), CTATVariableTable.valueAsString(inputStr));

		if(CTATExampleTracerLink.CORRECT_ACTION.toString().toUpperCase() === link.getActionType().toString().toUpperCase() || CTATExampleTracerLink.FIREABLE_BUGGY_ACTION.toString().toUpperCase() === link.getActionType().toString().toUpperCase())
		{
			var sel = student_sai.getSelection();
			if(sel !== null && typeof(sel) !== 'undefined' && sel.length > 0)
			{
				vt.put(sel, inputStr);
			}
		}

		//ctatdebug("CTATExampleTracerInterpretation --> out of updateVariableTable");
		return saiToReturn;
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
	 * @param {boolean} countOptionalAsTraversed if true, treat links with minTraversals <= 0 as always traversed
	 * @return {boolean} true as above; false if no entry in linkIdTraversalCountMap
	 */
	this.isTraversed = function(link, countOptionalAsTraversed)
	{
		ctatdebug("CTATExampleTracerInterpretation --> in isTraversed: " + link.getUniqueID());

		var traversalCount = linkIdTraversalCountMap[link.getUniqueID()];

		if(traversalCount === null || typeof(traversalCount) === 'undefined')
		{
			ctatdebug("CTATExampleTracerInterpretation --> in isTraversed in if condition (traversalCount === null): " + (traversalCount === null));
			ctatdebug("CTATExampleTracerInterpretation --> in isTraversed in if condition (typeof(traversalCount) === 'undefined'): " + (typeof(traversalCount) === 'undefined'));

			if(countOptionalAsTraversed === true)
			{
				return (link.getMinTraversals() <= 0);
			}
			else
			{
				return false;
			}
		}

		ctatdebug("CTATExampleTracerInterpretation --> in isTraversed out of if condition traversalCount[0]: " + traversalCount[0]);
		ctatdebug("CTATExampleTracerInterpretation --> in isTraversed out of if condition link.getMinTraversals(): " + link.getMinTraversals());

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
