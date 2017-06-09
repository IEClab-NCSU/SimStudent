/* This object represents an ExampleTracerTracer */
/* LastModify: FranceskaXhakaj 06/20/14*/

var ExampleTracerEvent = require('./ExampleTracerEvent');
var ExampleTracerGraph = require('./ExampleTracerGraph');

function ExampleTracerTracer(givenGraph) 
{

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	var interpretations; //This array will keep ExampleTracerInterpretation objects ONLY
	var graph; //ExampleTracerGraph instance
	ExampleTracerTracer.count++; //we have removed the instance variable
	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

	/* 
	 * Evaluates the given sai and updates the tracer with new interpretations
	 * If there are no link matches or only incorrect-action link matches, then
	 * the state of the tracer remains unchanged.
	 * @param result of type ExampleTracerEvent 
	 * @param isHintTrace of type boolean 
	 * @param doUpdate of type boolean 
	 * @return boolean
	 * @visibility: private, can be accessed through priviledged method ....
	 */
	function _evaluate (result, isHintTrace, doUpdate)
	{
		var newInterps = new Set(); //Set of ExampleTracerInterpretation
		var saiLinkMatches = result.getPreloadedLinkMatches(); //array of ExampleTracerLink objects ONLY

		for(var i = 0; i < interpretations.length; i++) //this is the best way to iterate through an array
		{
			if(typeof(saiLinkMatches) === 'undefined' || saiLinkMatches === null) 
			{
				saiLinkMatches = _findSAIMatchingLinks(result.getStudentSAI(), isHintTrace, interpretations[i].getVariableTable);
			}

			for(var j = 0; j < saiLinkMatches.length; j++)
			{
				if(interpretations[i].getTraversalCount(saiLinkMatches[j]) >= saiLinkMatches[j].getMaxTraversals())
				{
					continue;
				}

				ExampleTracerInterpretation newInterp = interpretations[i].clone();

				var allPaths = newInterp.getPaths(); //will return a set of ExampleTracerPath

				//not sure if this is going to work, my first time trying without being able to test
				for (let path of allPaths)
				{
					if(_isPathOK(saiLinkMatches[j], newInterp, path, isDemonstrateMode, result) === false)
					{
						allPaths.delete(path);
					}
				}

				//might need to do this: NOT sure qthout testing 
				newInterp.setPaths(allPaths);

				if(newInterp.getPaths().size() > 0)
				{
					var m = saiLinkMatches[j].getMatcher();

					// ExampleTracerSAI accepts only arrays in the constructor. 
					// For this reason, all the calls to matcher that return strings, 
					// need to be converted to arrays first
					var matcherSelection = [];
					matcherSelection.push(m.getSelection())
					var matcherAction = [];
					matcherAction.push(m.getAction());
					var matcherInput = [];
					matcherInput.push(m.getEvaluatedInput());

					result.setTutorSAI(new ExampleTracerSAI(matcherSelection, matcherAction, matcherInput, m.getActor()));
				
					//not working with the solverResult for now


					_fixupMatcherForPreloadedLinkMatches(saiLinkMatches[j], newInterp, result); 
            		
            		//not working with solverresult for now

            		newInterp.addLink(saiLinkMatches[j]);

            		if(doUpdate === true)
            		{
            			var replacementInput = replaceInput(saiLinkMatches[j], result.getStudentSAI(), newInterp);
            			newInterp.updateVariableTable(result.getStudentSAI(), replacementInput, saiLinkMatches[j]);
            		}
            		newInterps.add(newInterp);
				}
			}
		}

		result.setNumberOfInterpretations(newInterps.size);

		if(newInterps.size === 0)
		{
			if(saiLinkMatches.length !== 0 && saiLinkMatches[0].isDone() === true)
			{
				result.setDoneStepFailed(true);
			}

			result.setResult(NULL_MODEL);

			return false;
		}

		var bestInterp = getBestInterpretation(newInterps);
		String type = null;

		if(result.isSolverResult() === true)
		{
			type = (NULL_MODEL === result.getResult().toUpperCase() ? INCORRECT_ACTION : result.getResult())
		}
		else
		{
			type = bestInterp.getLastMatchedLink().getType(); 
			
		}

		return true;
	}


	/**
	 * Find all the links with the given sai.
	 * @param sai student's sai
	 * @param hint are we finding matching links for a hint trace?
	 * @param vt -interp specific vt, if null refers to problemmodel(best interp)'s vt 
	 * @return links that match...
	 */

	 /*
	  * Find all the links with the given sai.
	  * @param sai of type ExampleTracerSAI
	  * @param hint of type boolean
	  * @param vt of type VariableTable
	  * @return array of ExampleTracerLink objct ONLY
	  */
	function _findSAIMatchingLinks (sai, hint, vt)
	{
		var matchingLinks = []; //array of ExampleTracerLink ONLY!

		var graphLinks = graph.getLinks();

		for(var i = 0; i < graphLinks.size; i++)
		{
			if(hint === false)//hint will only be a boolean
			{
				if(graphLinks[i].matchesSAI(sai,vt)) //this function not done Q
				{
					matchingLinks.push(graphLinks[i]);
				}
			}
			//leaving out for now
			/*else if (graphLinks[i].matchesSAIforHint(sai, getResult())) //?
			{
				matchingLinks.push(graphLinks[i])
			}*/

		}	

		return matchingLinks;

	}

	/*
	 * @param newLink of type ExampleTracerLink
	 * @param interp of type ExampleTracerInterpretation
	 * @param path of type ExampleTracerPath
	 * @param isDemonstrateMode of type boolean 
	 * @param result fo type ExampleTracerEvent
	 * @return boolean
	 */
	function _isPathOK (newLink, interp, path, isDemonstrateMode, result)
	{

	}

	/*
	 * @param link of type ExampleTracerLink 
	 * @param interp of type ExampleTracerInterpretation
	 * @param result of type ExampleTracerEvent 
	 * @return undefined
	 */
	function _fixupMatcherForPreloadedLinkMatches(link, interp,	result)
	{
		//...
	}

	/*
	 * @param link of ExampleTracerLink 
	 * @param sai of type ExampleTracerSAI 
	 * @param newInterp of type ExampleTracerInterpretation 
	 * @return array of ??
	 */
	function _replaceInput(link, sai, newInterp)
	{
		//...

		var replacementInput = [];

		//...

		return replacementInput;

	}


	/*
	 * @param iter of type Set of ExampleTracerInterpretations
	 * @return an ExampleTracerInterpretation object
	 */
	function _getBestInterpretation(iter)
	{
		var bestInterp = null;

		return bestInterp;

	}

/***************************** PRIVILEDGED METHODS *****************************************************/

	this.evaluate = function(result, isHintTrace, doUpdate) 
	{
		return _evaluate(result, isHintTrace, doUpdate);
	};

/****************************** PUBLIC METHODS ****************************************************/

}

ExampleTracerTracer.count = 0;

/************************************************************************************************************/

var et = new ExampleTracerTracer(new ExampleTracerGraph);
var result = new ExampleTracerEvent();

result.setStudentSAI([true,false], [1,2,3], ["tell", "me"], "the tutor");

console.log(et.evaluate(result, true, false));