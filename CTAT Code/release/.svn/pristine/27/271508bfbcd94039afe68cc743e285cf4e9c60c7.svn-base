/* This object represents an ExampleTracerInterpretation */
/* LastModify: FranceskaXhakaj 06/20/14*/

/*
 * @param givenValidPaths of type set of ExampleTracerPaths
 */
function ExampleTracerInterpretation(givenValidPaths) 
{

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/
	

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/
	
	var vt; //object of type VariableTable
	var linkIdTraversalCountMap; //object that will represent a Map
	var validPaths; //set of ExampleTracerPath
	var traversedLinks; //aray of ExampleTracerLink

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

	function _getVariableTable()
	{
		return vt;
	}

	/*
	 * @param vt of type VariableTable
	 */
	function _setVariableTable(givenVt)
	{
		vt = givenVt;
	}

	/*
	 * @param target of type ExampleTracerLink
	 * @return integer
	 */
	function _getTraversalCount(target)
	{
		//array of integers
		var traversalCount = linkIdTraversalCountMap[target.getUniqueID()]; //the addition of the IDs should be done in the same way

		return (typeof(traversalCount) === 'undefined' || traversalCount === null ? 0 : traversalCount[0];

	}

	/*
	 * @return a set of ExampleTracerPath
	 */
	function _getPaths()
	{
		return validPaths;
	}

	/*
	 * @ return ExampleTracerInterpretation
	 */
	function _clone()
	{
		ExampleTracerInterpretation interp;

		//...

		return interp;
		
	}

	/*
	 * @param paths of type set of ExampleTracerPath
	 * @return undefined
	 */
	function _setPaths(paths)
	{
		//...
	}

	/*
	 * Adds the link to the path of interpretation
	 * @param link of type ExampleTracerLink
	 * @return undefined
	 */
	function _addLink(link)
	{
		//...
	}

	/*
	 * Set the student SAI variables (linkN.selection, linkN.action, etc.) for this link.
	 * Also for correct or suboptimal links, set the selection variable.
	 * @param student_sai of type ExampleTracerSAI
	 * @param replacementInput of type array
	 * @param link of type ExampleTracerLink 
	 * @return undefined
	 */
	function _updateVariableTable(student_sai, replacementInput, link)
	{

	}

/***************************** PRIVILEDGED METHODS *****************************************************/
	
	this.getVariableTable = function ()
	{
		return _getVariableTable();
	};

	this.setVariableTable = function (givenVt)
	{
		return _setVariableTable(givenVt);
	};

	this.getTraversalCount = function (target)
	{
		return _getTraversalCount(target);
	};

	this.getPaths = function ()
	{
		return _getPaths();
	};

	this.clone = function ()
	{
		return _clone();
	};

	this.setPaths = function (paths)
	{
		return _setPaths(paths);
	};

	this.addLink = function (link)
	{
		return _addLink(link);
	};

	this.updateVariableTable = function (student_sai, replacementInput, link)
	{
		return _updateVariableTable(student_sai, replacementInput, link);
	};

/****************************** link METHODS ****************************************************/

}

module.exports = ExampleTracerInterpretation;