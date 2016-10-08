/* This object represents an CTATExampleTracerTracer */
/* LastModify: FranceskaXhakaj 06/21/14*/

function CTATExampleTracerTracer(givenGraph) 
{

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	var interpretations; //This array will keep CTATExampleTracerInterpretation objects ONLY
	var graph; //CTATExampleTracerGraph instance
	CTATExampleTracerTracer.count++; //we have removed the instance variable

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

	/* 
	 * Evaluates the given sai and updates the tracer with new interpretations
	 * If there are no link matches or only incorrect-action link matches, then
	 * the state of the tracer remains unchanged.
	 * @param result of type CTATExampleTracerEvent 
	 * @param isHintTrace of type boolean 
	 * @param doUpdate of type boolean 
	 * @return boolean
	 * @visibility: private, can be accessed through priviledged method ....
	 */
	function evaluate (result, isHintTrace, doUpdate)
	{
		var newInterps = new Set(); //Set of CTATExampleTracerInterpretation
		var saiLinkMatches = result.getPreloadedLinkMatches(); //array of CTATExampleTracerLink objects ONLY

		for(var i = 0; i < interpretations.length; i++) //this is the best way to iterate through an array
		{
			if(typeof(saiLinkMatches) === 'undefined' || saiLinkMatches === null) 
			{
				saiLinkMatches = findSAIMatchingLinks(result.getStudentSAI(), isHintTrace, interpretations[i].getVariableTable);
			}

			for(var j = 0; j < saiLinkMatches.length; j++)
			{
				if(interpretations[i].getTraversalCount(saiLinkMatches[j]) >= saiLinkMatches[j].getMaxTraversals())
				{
					continue;
				}

				var newInterp = interpretations[i].clone(); //of type CTATExampleTracerInterpretation 

				var allPaths = newInterp.getPaths(); //will return a set of CTATExampleTracerPath

				//not sure if this is going to work, my first time trying without being able to test
				for (let path of allPaths)
				{
					if(isPathOK(saiLinkMatches[j], newInterp, path, isDemonstrateMode, result) === false)
					{
						allPaths.delete(path);
					}
				}

				//might need to do this: NOT sure qthout testing 
				newInterp.setPaths(allPaths);

				if(newInterp.getPaths().size() > 0)
				{
					var m = saiLinkMatches[j].getMatcher();

					// CTATExampleTracerSAI accepts only arrays in the constructor. 
					// For this reason, all the calls to matcher that return strings, 
					// need to be converted to arrays first
					var matcherSelection = [];
					matcherSelection.push(m.getSelection());
					var matcherAction = [];
					matcherAction.push(m.getAction());
					var matcherInput = [];
					matcherInput.push(m.getEvaluatedInput());

					result.setTutorSAI(new CTATExampleTracerSAI(matcherSelection, matcherAction, matcherInput, m.getActor()));
				
					//not working with the solverResult for now


					fixupMatcherForPreloadedLinkMatches(saiLinkMatches[j], newInterp, result); 
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
		var type = null; //of type string

		if(result.isSolverResult() === true)
		{
			type = (NULL_MODEL === result.getResult().toUpperCase() ? INCORRECT_ACTION : result.getResult());
		}
		else
		{
			type = bestInterp.getLastMatchedLink().getType(); 
			
		}

		return true;
	}

	/*
	* Find all the links with the given sai.
	* @param sai of type CTATExampleTracerSAI
	* @param hint of type boolean
	* @param vt of type VariableTable
	* @return array of CTATExampleTracerLink objct ONLY
	*/
	function findSAIMatchingLinks (sai, hint, vt)
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
	 * @param newLink of type CTATExampleTracerLink
	 * @param interp of type CTATExampleTracerInterpretation
	 * @param path of type CTATExampleTracerPath
	 * @param isDemonstrateMode of type boolean 
	 * @param result fo type CTATExampleTracerEvent
	 * @return boolean
	 */
	function isPathOK (newLink, interp, path, isDemonstrateMode, result)
	{
		//...
	}

	/*
	 * @param link of type CTATExampleTracerLink 
	 * @param interp of type CTATExampleTracerInterpretation
	 * @param result of type CTATExampleTracerEvent 
	 * @return undefined
	 */
	function fixupMatcherForPreloadedLinkMatches(link, interp,	result)
	{
		//...
	}

	/*
	 * @param link of CTATExampleTracerLink 
	 * @param sai of type CTATExampleTracerSAI 
	 * @param newInterp of type CTATExampleTracerInterpretation 
	 * @return array of ??
	 */
	function replaceInput(link, sai, newInterp)
	{
		//...

		var replacementInput = [];

		//...

		return replacementInput;

	}


	/*
	 * @param iter of type Set of CTATExampleTracerInterpretations
	 * @return an CTATExampleTracerInterpretation object
	 */
	function getBestInterpretation(iter)
	{
		var bestInterp = null;

		return bestInterp;

	}

/***************************** PRIVILEDGED METHODS *****************************************************/


/****************************** PUBLIC METHODS ****************************************************/

}

CTATExampleTracerTracer.count = 0;
