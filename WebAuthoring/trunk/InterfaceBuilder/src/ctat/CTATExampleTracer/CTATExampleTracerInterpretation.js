/* This object represents an CTATExampleTracerInterpretation */
/* LastModify: FranceskaXhakaj 06/20/14*/

/*
 * @param givenValidPaths of type set of ExampleTracerPaths
 */
function CTATExampleTracerInterpretation(givenValidPaths) 
{

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/
	
	var vt; //object of type VariableTable
	var linkIdTraversalCountMap; //object that will represent a Map<Integer, int[]>
	var validPaths; //set of ExampleTracerPath
	var traversedLinks; //array of ExampleTracerLink

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/


/**************************** PUBLIC INSTACE VARIABLES ******************************************************/



/***************************** PRIVILEDGED METHODS *****************************************************/
	/*
	 * @return a VariableTable object
	 */
	this.getVariableTable = function ()
	{
		return vt;
	};

	/*
	 * @param givenVt of type VariableTable
	 */
	this.setVariableTable = function (givenVt)
	{
		vt = givenVt;
	};

	/*
	 * @param target of type ExampleTracerLink
	 * @return integer
	 */
	this.getTraversalCount = function (target)
	{
		//array of integers
		var traversalCount = linkIdTraversalCountMap[target.getUniqueID()]; //the addition of the IDs should be done in the same way

		return (typeof(traversalCount) === 'undefined' || traversalCount === null ? 0 : traversalCount[0]);

	};

	/*
	 * @return a set of ExampleTracerPath
	 */
	this.getPaths = function ()
	{
		return validPaths;
	};

	/*
	 * @ return ExampleTracerInterpretation
	 */
	this.clone = function ()
	{
		var interp = null;

		//...

		return interp;
	};

	/*
	 * @param paths of type set of ExampleTracerPath
	 * @return undefined
	 */
	this.setPaths = function (paths)
	{
		//...
	};

	/*
	 * Adds the link to the path of interpretation
	 * @param link of type ExampleTracerLink
	 * @return undefined
	 */
	this.addLink = function (link)
	{
		//...
	};

	/*
	 * Set the student SAI variables (linkN.selection, linkN.action, etc.) for this link.
	 * Also for correct or suboptimal links, set the selection variable.
	 * @param student_sai of type ExampleTracerSAI
	 * @param replacementInput of type array
	 * @param link of type ExampleTracerLink 
	 * @return undefined
	 */
	this.updateVariableTable = function (student_sai, replacementInput, link)
	{
		//...
	};

/****************************** PUBLIC METHODS ****************************************************/

}