/* This object represents an ExampleTracerEvent */
/* LastModify: FranceskaXhakaj 06/20/14*/

var ExampleTracerSAI = require('./ExampleTracerSAI');

function ExampleTracerEvent() 
{

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	var studentSAI; //ExampleTracerSAI instance
	var preloadedLinkMatches; //array of ExampleTracerLink only
	var tutorSAI; //of type ExampleTracerSAI
	var fromSolver; //boolean type
	var result; //of type string

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

	function _getStudentSAI ()
	{
		return studentSAI;
	}

    /*
     * Used to set the student SAI
     * @param selection of type array
     * @param action of type array
     * @param input of type array
     * @param actor of type String
     */
	function _setStudentSAI(selection, action, input, actor)
	{
		studentSAI = new ExampleTracerSAI(selection, action, input, actor);
	}

	/*
	 * @return an array of ExampleTracerLink
	 */
	function _getPreloadedLinkMatches()
	{
		return preloadedLinkMatches;

	}

	/*
	 * @param sai of type ExampleTracerSAI
	 * @return undefined
	 */
	function _setTutorSAI(sai)
	{
		//...
	}

	/*
	 * @param numberOfInterpretations of type integer
	 * @return undefined
	 */
	function _setNumberOfInterpretations(numberOfInterpretations)
	{
		//...
	}

	/*
	 * @param doneStepFailed of type boolean 
	 * @return undefined
	 */
	function _setDoneStepFailed(doneStepFailed)
	{
		//...
	}

	/*
	 * @param result of type string 
	 * @return undefined
	 */
	function _setResult(result)
	{
		//...
	}

	/*
	 * @return boolean
	 */
	function _isSolverResult()
	{
		return fromSolver;
	}

	/*
	 * @return string
	 */
	function _getResult()
	{
		return result;
	}


/***************************** PRIVILEDGED METHODS *****************************************************/

	this.getStudentSAI = function ()
	{
		return _getStudentSAI();
	};

	this.setStudentSAI = function (selection, action, input, actor)
	{
		return _setStudentSAI(selection, action, input, actor);
	};

	this.getPreloadedLinkMatches = function ()
	{
		return _getPreloadedLinkMatches();
	};

	this.setNumberOfInterpretations = function (numberOfInterpretations)
	{
		return _setNumberOfInterpretations(numberOfInterpretations);
	};

	this.setDoneStepFailed = function (doneStepFailed)
	{
		return _setDoneStepFailed(doneStepFailed);
	};	


	this.setResult = function (result)
	{
		return _setResult(result);
	};
	
	this.isSolverResult = function ()
	{
		return _isSolverResult();
	};

	this.getResult = function ()
	{
		return _getResult();
	};


/****************************** PUBLIC METHODS ****************************************************/

}

module.exports = ExampleTracerEvent;