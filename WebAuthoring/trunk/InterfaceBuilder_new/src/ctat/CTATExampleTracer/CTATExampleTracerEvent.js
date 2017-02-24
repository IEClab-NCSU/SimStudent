/* This object represents an CTATExampleTracerEvent */
/* LastModify: FranceskaXhakaj 06/21/14*/

function CTATExampleTracerEvent() 
{

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	var studentSAI; //CTATExampleTracerSAI instance
	var preloadedLinkMatches; //array of ExampleTracerLink only
	var tutorSAI; //of type CTATExampleTracerSAI
	var fromSolver; //boolean type
	var result; //of type string

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

/***************************** PRIVILEDGED METHODS *****************************************************/

	/*
	 * @return objec of type CTATExampleTracerSAI
	 */
	this.getStudentSAI = function ()
	{
		return studentSAI;
	};

	/*
     * Used to set the student SAI
     * @param selection of type array
     * @param action of type array
     * @param input of type array
     * @param actor of type String
     * @return indefined
     */
	this.setStudentSAI = function (selection, action, input, actor)
	{
		studentSAI = new CTATExampleTracerSAI(selection, action, input, actor);
	};

	/*
	 * @return an array of CTATExampleTracerLink
	 */
	this.getPreloadedLinkMatches = function ()
	{
		return preloadedLinkMatches;
	};

	/*
	 * @param sai of type CTATExampleTracerSAI
	 * @return undefined
	 */
	this.setTutorSAI = function(sai)
	{
		//...
	};

	/*
	 * @param numberOfInterpretations of type integer
	 * @return undefined
	 */
	this.setNumberOfInterpretations = function (numberOfInterpretations)
	{
		//...
	};


	/*
	 * @param doneStepFailed of type boolean 
	 * @return undefined
	 */
	this.setDoneStepFailed = function (doneStepFailed)
	{
		//...
	};	

	/*
	 * @param result of type string 
	 * @return undefined
	 */
	this.setResult = function (result)
	{
		//...
	};
	
	/*
	 * @return boolean
	 */
	this.isSolverResult = function ()
	{
		return fromSolver;
	};

	/*
	 * @return string
	 */
	this.getResult = function ()
	{
		return result;
	};


/****************************** PUBLIC METHODS ****************************************************/

}