/* This object represents an CTATExampleTracerTracer */

goog.provide('CTATExampleTracerTracer');
goog.require('CTATBase');
goog.require('CTATMsgType');
goog.require('CTATExampleTracerSAI');
goog.require('CTATExactMatcher');
goog.require('CTATExpressionMatcher');
goog.require('CTATVectorMatcher');
goog.require('CTATExampleTracerLink');
goog.require('CTATExampleTracerLinkComparator');
goog.require('CTATExampleTracerEvent');
goog.require('CTATExampleTracerInterpretation');
goog.require('CTATExampleTracerInterpretationComparator');
goog.require('CTATExampleTracerPath');


//goog.require('CTATVariableTable');//
//goog.require('CTATMatcher');//


/* LastModify: FranceskaXhakaj 07/14*/

/**************************** GLOBAL VARIABLES ******************************************************/

/**
 * @global
 */
var CTATExampleTracerTracer_count = 0;

/**
 * Top-level algorithms for the example tracer.
 * @constructor
 * @param {CTATExampleTracerGraph} givenGraph
 * @param {CTATVariableTable} givenVT
 */
CTATExampleTracerTracer = function(givenGraph, givenVT) 
{
    CTATBase.call(this, "CTATExampleTracerTracer", givenGraph);

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

    /**
     * @type {CTATVariableTable}
     */
	var startStateVT = givenVT;

	/**
     * @type {array of CTATExampleTracerInterpretation}
     */
	var interpretations = null;

	/**
     * @type {Map<Integer, CTATExampleTracerLink>}
     */	
	var incorrectActionMatches = null;

	/**
     * @type {array of CTATExampleTracerSAI}
     */		
	var studentSAIs = null;

	/**
     * @type {CTATExampleTracerGraph}
     */	
	var graph = givenGraph;

	/**
     * @type {CTATExampleTracerEvent}
     */	
	var result = null;

	/**
     * @type {boolean}
     */	
	var isDemonstrateMode = false;

	/**
     * @type {integer}
     */	
	var instance = CTATExampleTracerTracer_count++;
	
	/**
	 * Last known best interpretation.
     * @type {CTATExampleTracerInterpretation}
     */
	var bestInterpretation = null;

	//We do not have listeners anymore, we have decided to remove them from our implementation
	//var listeners = new Set(); //set of CTATExampleTracerEventListener
	
	/**
     * Make the object available to private methods
     */
	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

	/** 
	 * Evaluates the given sai and updates the tracer with new interpretations
	 * If there are no link matches or only incorrect-action link matches, then
	 * the state of the tracer remains unchanged.
	 * @private
	 * @param {CTATExampleTracerEvent} result Instance to hold trace results
	 * @param {boolean} isHintTrace
	 * @param {boolean} doUpdate 
	 * @return {boolean} True, if there are any correct/suboptimal action link matches
	 */
	function _evaluate (result, isHintTrace, doUpdate)
	{
		that.ctatdebug("CTATExampleTracerTracer --> _evaluate");
		that.ctatdebug("In private _evaluate");

		var newInterps = new Set(); //Set of CTATExampleTracerInterpretation
		var saiLinkMatches = result.getPreloadedLinkMatches(); //array of CTATExampleTracerLink objects ONLY

		that.ctatdebug("interps # : " + interpretations.length);
		that.ctatdebug("interps: " + interpretations);
		
		interpretations.forEach(function(interpretation) //the best way to iterate through an array
		{
			that.ctatdebug("In evaluate -- entered the interpretations loop");

			if(typeof(saiLinkMatches) === 'undefined' || saiLinkMatches === null) 
			{
				that.ctatdebug("In evaluate -- entered if check for saiLinkMatches");
				saiLinkMatches = findSAIMatchingLinks(result.getStudentSAI(), isHintTrace, interpretation.getVariableTable());
				that.ctatdebug("In evaluate -- exiting if check for saiLinkMatches");          
			}
			that.ctatdebug("Link matches # : " + saiLinkMatches.length);
			saiLinkMatches.forEach(function(link)
			{
				that.ctatdebug("In evaluate -- entered the saiLinkMatches loop + " + link);

				if(interpretation.getTraversalCount(link) >= link.getMaxTraversals())
				{
					that.ctatdebug("In evaluate -- skipped a saiLinkMatches loop \n Skipped due to max traversals" +link.getMaxTraversals() );
					return false; //instead of continue we do return
				}

				var newInterp = interpretation.clone(); //of type CTATExampleTracerInterpretation 

				that.ctatdebug("In evaluate size of getPaths "+ newInterp.getPaths().size);

				newInterp.getPaths().forEach(function(path)
				{
					that.ctatdebug("In evaluate -- looping through the newInterp.getPaths()\n Examining path: " + path);

					if(isPathOK(link, newInterp, path, isDemonstrateMode, result) === false)
					{
						that.ctatdebug("In evaluate -- in if condition, checking if isPathOK");
                                                
						newInterp.getPaths().delete(path); //might not be a great idea, it might have undefined behavior, make sure it is working correctly
						
						that.ctatdebug("In evaluate -- out of if condition, checking if isPathOK\n Tried to delete path.");
					}
				});
               
                that.ctatdebug("In evaluate -- done looping through the newInterp.getPaths()");
                                console.log("newInterp.getPaths().size = " + newInterp.getPaths().size);
				if(newInterp.getPaths().size > 0)
				{
					that.ctatdebug("In evaluate -- if number of paths is greater than 0");

					var m = link.getMatcher();

					that.ctatdebug("In evaluate -- we got the matcher ");

					// CTATExampleTracerSAI accepts only arrays in the constructor. 
					// For this reason, all the calls to matcher that return strings, 
					// need to be converted to arrays first
					var matcherSelection = [];
					matcherSelection.push(m.getSelection());
					var matcherAction = [];
					matcherAction.push(m.getAction());
					var matcherInput = [];
					matcherInput.push(m.getEvaluatedInput());

					var tutorSai = new CTATExampleTracerSAI(matcherSelection, matcherAction, matcherInput, m.getActor());
                    
                    ////console.log("SAI object: ");
                    ////console.log(tutorSai);

                    that.ctatdebug("In evaluate -- TUTORSAI object: " + tutorSai);

					result.setTutorSAI(tutorSai);
				
					that.ctatdebug("In evaluate -- tutorSAI set");
					
					//not working with the solverResult for now

					that.ctatdebug("In evaluate -- calling fixupMatcherForPreloadedLinkMatches");
					fixupMatcherForPreloadedLinkMatches(link, newInterp, result); 
					that.ctatdebug("In evaluate -- out of calling fixupMatcherForPreloadedLinkMatches");

					//not working with solverResult for now

					newInterp.addLink(link);

					that.ctatdebug("In evaluate -- new link added");

					if(doUpdate === true)
					{
						that.ctatdebug("In evaluate -- we are in the doUpdate if condition");
						var replacementInput = replaceInput(link, result.getStudentSAI(), newInterp);

						that.ctatdebug("In evaluate -- updatingVariable table");
						newInterp.updateVariableTable(result.getStudentSAI(), replacementInput, link);
						that.ctatdebug("In evaluate -- done updatingVariable table");
					}

					newInterps.add(newInterp); //adding to a set
					that.ctatdebug("In evaluate -- added new interp");
				}

				that.ctatdebug("In evaluate -- out of saiLinkMatches loop");
			});

		that.ctatdebug("In evaluate -- out of interpretation loops");

		});

		result.setNumberOfInterpretations(newInterps.size);

		that.ctatdebug("In evaluate -- number of interpretations " + newInterps.size + " set");

		if(newInterps.size === 0)
		{
			that.ctatdebug("In evaluate -- if condition no new interpretations");

			if(saiLinkMatches.length !== 0 && saiLinkMatches[0].isDone() === true)
			{
				result.setDoneStepFailed(true);
			}

			result.setResult(CTATExampleTracerLink.NO_MODEL);

			that.ctatdebug("In evaluate -- if condition no new interpretations returning false");

			return false;
		}

		var bestInterp = findBestInterpretation(newInterps);
		that.ctatdebug("In evaluate -- just found bestInterp");
		
		var type = null; //of type string

		if(result.isSolverResult() === true)
		{
			that.ctatdebug("In evaluate -- in if branch of isSolverResult");
			type = (CTATExampleTracerLink.NO_MODEL.toString().toUpperCase() === result.getResult().toString().toUpperCase() ? CTATExampleTracerLink.BUGGY_ACTION.toString() : result.getResult().toString());
		}
		else
		{
			that.ctatdebug("In evaluate -- in else branch of isSolverResult");
			type = bestInterp.getLastMatchedLink().getType(); 	
		}

		if(doUpdate === true)
		{
			that.ctatdebug("In evaluate -- in if Branch doUpdate()");

			studentSAIs.push(result.getStudentSAI());

			if(type.toString() === CTATExampleTracerLink.CORRECT_ACTION.toString() || type.toString() === CTATExampleTracerLink.FIREABLE_BUGGY_ACTION.toString())
			{
				that.ctatdebug("In evaluate -- in first branch doUpdate()");

				newInterps.forEach(function(iter) 
				{
					if(iter.getLastMatchedLink().getType().toString() === CTATExampleTracerLink.BUGGY_ACTION.toString())
					{
						that.ctatdebug("In evaluate -- deleting an incorrect interp");
						newInterps.delete(iter);
					}
				});

				that.ctatdebug("In evaluate -- after iterating over interpretations");

				setInterpretations(newInterps);

				that.ctatdebug("In evaluate -- after setting new interps");

				//the best way I know of to clear a map/object
				for (var i in incorrectActionMatches)
				{
					if(incorrectActionMatches.hasOwnProperty(i))
					{
						delete incorrectActionMatches[i];
					}
				}

				that.ctatdebug("In evaluate -- out of DoUpdate if branch");
			}
			else
			{
				that.ctatdebug("In evaluate -- in else branch of doUpdate");

				result.setNumberOfInterpretations(0);

				//the best way I know of to clear a map/object
				for (var j in incorrectActionMatches)
				{
					if(incorrectActionMatches.hasOwnProperty(j))
					{
						delete incorrectActionMatches[j];
					}
				}

				newInterps.forEach(function(interp)
				{
					incorrectActionMatches[interp.getLastMatchedLink().getUniqueID()] = interp.getLastMatchedLink();
				});
			}

			if(type.toString() === CTATExampleTracerLink.BUGGY_ACTION.toString())
			{
				//use this to remove an element from an array
				//removes the element, and resizes the array
				studentSAIs.splice(studentSAIs.length - 1, 1); 
			}
		}

		var link = bestInterp.getLastMatchedLink();

		if(result.isSolverResult() === false)
		{
			that.ctatdebug("In evaluate inside the solver result if condition + " + type);
			result.setResult(type);
		}

		that.ctatdebug("In evaluate out the solver result if condition");
		result.setReportableLink(link);

		result.setReportableVariableTable(bestInterp.getVariableTable());

		var sai = that.getResult().getStudentSAI();

		//As interpolatedHints,Selection,Action,Input are part of CTATExampleTracerEvent now
		//we need to change the following call
		//link.interpolateHints(bestInterp.getVariableTable(), sai.getSelectionAsString(), sai.getActionAsString(), sai.getInputAsString());

		result.setInterpolateSAI(sai.getSelectionAsString(), sai.getActionAsString(), sai.getInputAsString());
		that.ctatdebug("In evaluate after calling setInterpolatesSAI");
		result.setInterpolatedHints(link.interpolateHints(bestInterp.getVariableTable()));
		that.ctatdebug("In evaluate after calling setInterpolatedHints");

		result.setReportableHints(link.getHints());
		that.ctatdebug("In evaluate --> after setReportableHints");


		//We will not have anymore listeners, this function is not needed
		//that.fireExampleTracerEvent(result);
		that.ctatdebug("In evaluate after fireExampleTracerEvent");
		
		that.ctatdebug("CTATExampleTracerTracer --> out of _evaluate");
		return (type.toString() !== CTATExampleTracerLink.BUGGY_ACTION.toString());
	}

   /**
	* Find all the links with the given sai.
	* @private
	* @param {CTATExampleTracerSAI} sai Student sai
	* @param {boolean} hint Are we finding matching links for a hint trace?
	* @param {CTATVariableTable} vt Interp specific vt, if null refers to problemmodel(best interp)'s vt 
	* @return {array of CTATExampleTracerLink} Links that match
	*/
	function findSAIMatchingLinks (sai, hint, vt)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in findSAIMatchingLinks");

		var matchingLinks = []; //array of CTATExampleTracerLink ONLY!

		graph.getLinks().forEach(function(link)
		{
			if(hint === false)//hint will only be a boolean
			{
				//Note: this function call belongs to CTATExampleTracerEvent now
				
				that.ctatdebug("Calling link.matchesSAI() on link " + link + ", sai " + sai);
				var matched = link.matchesSAI(sai,vt);
				that.ctatdebug("Result from link.matchesSAI() on link " + link + ": " + matched + ", typeof " + typeof(matched));

				if(matched === true) //function not done
				{
					matchingLinks.push(link);
				}

			} //matchesSAI will take an extra parameter, the vt
			else if (link.matchesSAIforHint(sai, that.getResult(), vt) === true)
			{
				matchingLinks.push(link);
			}
		});	

		that.ctatdebug("CTATExampleTracerTracer --> out of findSAIMatchingLinks: #matchingLinks " + matchingLinks.lengtho);

		return matchingLinks;
	}

	/**
	 * @private
	 * @param {CTATExampleTracerLink} newLink The link we're trying to extend the interpretation/path with
	 * @param {CTATExampleTracerInterpretation} interp 
	 * @param {CTATExampleTracerPath} path 
	 * @param {boolean} isDemonstrateMode If we're in demonstrate mode, ignore the path beyond
	 * the deepest traced link and recheck the interpretation fully
	 * @param {CTATExampleTracerEvent} result True if the newLink can be added, false otherwise
	 * @return {boolean}
	 */
	function isPathOK (newLink, interp, path, isDemonstrateMode, result)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in isPathOK");

		var pathLinks = null; // object of type set of CTATExampleTracerLink

		if(isDemonstrateMode === true)
		{
			var allLinks = interp.getMatchedLinks().slice(); //array of CTATExampleTracerLink
			allLinks.push(newLink);
			pathLinks = path.getLinksRestricted(allLinks);
		}
		else
		{
			pathLinks = path.getLinks();
		}

		that.ctatdebug("CTATExampleTracerTracer --> in isPathOK path.getLinks() " + path.getLinks().size);
		that.ctatdebug("CTATExampleTracerTracer --> in isPathOK pathLinks " + pathLinks.size);


		if(isDemonstrateMode === true)
		{
			var traversedLinks = []; //array of CTATExampleTracerLink
			var returnValue = true;

			interp.getMatchedLinks().forEach(function(link)
			{
				//mimic a break in a forEach iteration
				if(returnValue === false)
				{
					return;
				}

				if(graph.observesOrderingConstraints(traversedLinks, link, pathLinks, result) === false || doneStepOK(interp, link, pathLinks) === false)
				{
					that.ctatdebug("CTATExampleTracerTracer --> in isPathOK returning false1");
					returnValue = false;
					return;
				}

				traversedLinks.push(link);
			});

			//mimicking the return that should have happened within the loop above
			if(returnValue === false)
			{
				that.ctatdebug("CTATExampleTracerTracer --> in isPathOK returning false2");
				return false;
			}
		}

		if(newLink.getType().toString() ===  CTATExampleTracerLink.BUGGY_ACTION.toString())
		{
			if(graph.isIncorrectLinkOK(interp.getMatchedLinks(), newLink, pathLinks, interp) === false)
			{
				that.ctatdebug("CTATExampleTracerTracer --> in isPathOK returning false3");
				return false;
			}
		}
		else
		{
			if(graph.observesOrderingConstraints(interp.getMatchedLinks(), newLink, pathLinks, result) === false)
			{
				that.ctatdebug("CTATExampleTracerTracer --> in isPathOK returning false4");
				return false;
			}

			if(doneStepOK(interp, newLink, pathLinks) === false)
			{
				that.ctatdebug("CTATExampleTracerTracer --> in isPathOK returning false5");
				return false;
			}
		}

		that.ctatdebug("CTATExampleTracerTracer --> out of isPathOK returning true");

		return true;
	}

	/**
	 * @private
	 * This method should go to the branch of the if condition that
	 * checks if the matcher is an instance of CTATVectorMatcher
	 * All new graphs contain CTATVectorMatcher, very old ones contain
	 * CTATExactMatchers
	 * @param {CTATExampleTracerLink} link   
	 * @param {CTATExampleTracerInterpretation} interp  
	 * @param {CTATExampleTracerEvent} result   
	 * @return {undefined}
	 */
	function fixupMatcherForPreloadedLinkMatches(link, interp, result)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in fixupMatcherForPreloadedLinkMatches");

		if(result.getPreloadedLinkMatches() === null || typeof(result.getPreloadedLinkMatches()) === 'undefined')
		{
			that.ctatdebug("There are no preloaded link matches.");
			return;
		}

		//check if the current link exists in the array
		//index needs to be a positive number or 0
		if(result.getPreloadedLinkMatches().indexOf(link) < 0)
		{
			that.ctatdebug("link does not exist in preloaded link matches");
			return;
		}

		//we do not have an EdgeData class anymore
		//all the functionality goes to CTATExampleTracerLink
		setInterpolateSAI(link);

		//get the matcher from the link
		var m = link.getMatcher();

		var sai = result.getStudentSAI();

		//some Matchers do not need this many parameters, so eventually they will be ignored
		//for example CTATExactMatcher does not need the variable table parameter
		var mResult = m.match(sai.getSelectionAsArray(), sai.getActionAsArray(), sai.getInputAsArray(), sai.getActor(), interp.getVariableTable());

		var tutorSAI = new CTATExampleTracerSAI(m.getSelection(), m.getAction(), m.getDefaultInput(), m.getActor());

		if(m instanceof CTATVectorMatcher)
		{
			that.ctatdebug("Instance of VectorMatcher, we are in the correct place.");
			var inputMatcher = m.getMatchers(2)[0];//return an array of CTATMatcher, we get the 0th element

			if(inputMatcher instanceof CTATExpressionMatcher)
			{
				that.ctatdebug("Instance of CTATExpressionMatcher.");
				if(inputMatcher.isEqualRelation() === true)
				{
					var evaluatedInput = inputMatcher.getLastResult();
					
					result.setEvaluatedInput(evaluatedInput);
					link.setStudentInput(evaluatedInput);
					inputMatcher.setDefaultInput(evaluatedInput);

					tutorSAI.setInput(inputMatcher.getEvaluatedInput());//the parameter is a string
				}

			}
			else if (inputMatcher instanceof CTATExactMatcher)
			{
				that.ctatdebug("Instance of CTATExactMatcher.");
				link.setStudentInput(m.getInputMatcher());
				tutorSAI.setInput(m.getInputMatcher());
				link.setStudentAction(m.getActionMatcher());
				link.setStudentSelection(m.getSelectionMatcher());
			}
		}
		else if(m instanceof CTATExactMatcher)
		{
			//Jonathan says that all new brd files are constructoed using a VectorMatcher
			//very old brd files still use CTATExactMatcher, so unless we hvae such an old file, we should not pass into this branch
			that.ctatdebug("You should not be here. The matcher should not be a CTATExactMatcher (unless your brd file is very old).");
			/*link.setStudentInput(m.getDefaultInput());
			tutorSAI.setInput(m.getDefaultInput());
			link.setStudentAction(m.getDefaultAction());
			link.setStudentSelection(m.getDefaultSelection());
			*/
		}

		result.setTutorSAI(tutorSAI);

		that.ctatdebug("CTATExampleTracerTracer --> out of fixupMatcherForPreloadedLinkMatches");
	}

	/**
	 * @private
	 * If indicated by replaceInput(), calculate a new value for the student's
	 * input using evaluateReplacement(array, array, array, CTATVariableTable)}.
	 * @param {CTATExampleTracerLink} link Has evaluateReplacement(array, array, array, CTATVaraibleTable)
	 * @param {CTATExampleTracerSAI} sai Student's input data
	 * @param {CTATExampleTracerInterpretation} newInterp Supplied variable table to use
	 * @return {array} Null if no replacement indicated
	 */
	function replaceInput(link, sai, newInterp)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in replaceInput");

		if(link === null || typeof(link) === 'undefined' || link.replaceInput() === false)
		{
			that.ctatdebug("in first if condition");
			return null;
		}

		that.ctatdebug("out of first if condition");

		var replacementInput = link.evaluateReplacement(sai.getSelectionAsArray(), sai.getActionAsArray(), sai.getInputAsArray(), newInterp.getVariableTable());

		that.ctatdebug("CTATExampleTracerTracer --> out of replaceInput");

		return replacementInput;
	}

	/**
	 * @private
	 * @param {Set of CTATExampleTracerInterpretations} iter
	 * @return {CTATExampleTracerInterpretation}
	 */
	function findBestInterpretation(iter)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in findBestInterpretation");

		var comp = new CTATExampleTracerInterpretationComparator();
		var bestInterp = null; //instance of CTATExampleTracerInterpretation

		iter.forEach(function(interp)
		{
			that.ctatdebug("CTATExampleTracerTracer --> in findBestInterpretation iteration");

			if(bestInterp === null || typeof(bestInterp) === 'undefined')
			{
				that.ctatdebug("CTATExampleTracerTracer --> in findBestInterpretation first if condition");

				bestInterp = interp;
			}
			else
			{
				that.ctatdebug("CTATExampleTracerTracer --> in findBestInterpretation else of first if condition");

				var compResult = comp.compare(interp, bestInterp);

				if(compResult > 0)
				{
					that.ctatdebug("CTATExampleTracerTracer --> in findBestInterpretation second if condition");

					bestInterp = interp;
				}
			}
		});

		that.ctatdebug("CTATExampleTracerTracer --> out of findBestInterpretation");

		return bestInterp;
	}

	/**
	 * @private
	 * @param {Set of CTATExampleTracerInterpretation} c 
	 * @return {undefined}
	 */
	function setInterpretations(c)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in setInterpretations");

		interpretations.length = 0; //clear the elements in the array

		if(c.size === 0)
		{
			that.ctatdebug("CTATExampleTracerTracer --> returning from setInterpretations");

			return;
		}

		that.ctatdebug("CTATExampleTracerTracer --> setInterpretations c.size: " + c.size);

		//interpretations = interpretations.concat(c);
		c.forEach(function(el)
		{
			interpretations.push(el);
		});

		bestInterpretation = null;

		that.ctatdebug("CTATExampleTracerTracer --> out of setInterpretations");
	}

	/**
	 * @private
	 * @return {Set of CTATExampleTracerPath}
	 */
	function getAllPaths()
	{
		that.ctatdebug("CTATExampleTracerTracer --> in getAllPaths");

		var result = graph.findAllPaths();
        that.ctatdebug("CTATExampleTracerTracer --> in getAllPaths: #paths " + result.size);

        result.forEach(function(el)
		{
			that.ctatdebug("CTATExampleTracerTracer --> in getAllPaths loopring through paths");
			that.ctatdebug("CTATExampleTracerTracer --> in getAllPaths size of path" + el.getLinks().size);
		});

		return result;
	}

	/**
	 * Tell whether a step is valid with respect to achieving a problem-done state.
	 * Returns true if  the matched link is not a Done step or it's an incorrect action or 
	 * there is one path in the extension with all links visited.
	 * @private
     * @param {CTATExampleTracerInterpretation} interp Check min traversal counts in this interpretation
     * @param {CTATExampleTracerLink} newLink Return false if link is a Done step and it's premature
     * @param {Set of CTATExampleTracerLink} path Check this path
     * @return {boolean}
	 */
	function doneStepOK(interp, newLink, path)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in doneStepOK");

		if(newLink === null || typeof(newLink) === 'undefined' || newLink.isDone() === false)
		{
			that.ctatdebug("doneStepOK in first if condition");
			return true; //newLink is not a Done step
		}

		if(newLink.getType().toString() === CTATExampleTracerLink.BUGGY_ACTION.toString())
		{
			that.ctatdebug("doneStepOK in second if condition");
			return true; //newLink is an incorrect action
		}

		//removed variable visitCount: never used

		that.ctatdebug("doneStepOK before loop");

		//there is no way we can break out of a forEach
		//this way at least we can return the right value
		var returnValue = true;

		path.forEach(function(link)
		{
			if(returnValue === false)
			{
				that.ctatdebug("CTATExampleTracerTracer --> we are breaking from the loop");
				return; //the only way we can mimic a break from a forEach
			}
			
			if(link.getUniqueID() === newLink.getUniqueID())
			{
				that.ctatdebug("CTATExampleTracerTracer --> inside second if condition");
				return; //similar to continue in Java
			}

			var traversalCount = interp.getTraversalCount(link); //of type integer

			//even though we should break the for loop once we go into this if
			//we have to keep going, there is no way we can break that
			if(traversalCount < link.getMinTraversals())
			{
				that.ctatdebug("CTATExampleTracerTracer --> we should return false from the second if condition");
				//return false;

				returnValue = false;
				return;
			}
		});

		if(returnValue === false)
		{
			that.ctatdebug("CTATExampleTracerTracer --> out of doneStepOK returning false");
				
			return false;
		}

		that.ctatdebug("CTATExampleTracerTracer --> out of doneStepOK returning true");

		return true; //there is np path in the extension with all links visited
	}

	/** 
	 * @private
	 * @param {CTATExampleTracerLink} link
	 * @return {undefined}
	 */
	function setInterpolateSAI(link)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in setInterpolateSAI");

		var selection = "";

		if(link.getSelection() !== null && typeof(link.getSelection()) !== 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in setInterpolateSAI first if condition");
			selection = (link.getSelection())[0].toString(); //get the element at the 0th position
		}

		var action = null;

		if(link.getAction() !== null && typeof(link.getAction()) !== 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in setInterpolateSAI second if condition");
			action = (link.getAction())[0].toString();  //get the element at the 0th position
		}

		var input = null;

		if(link.getInput() !== null && typeof(link.getInput()) !== 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in setInterpolateSAI third if condition");
			input = (link.getInput())[0].toString();  //get the element at the 0th position
		}

		//Note: we call it on CTATExampleTracerEvent and not on CTATExampleTracerLink
		result.setInterpolateSAI(selection, action, input);
	}

	/**
	 * Find an unvisited link that would be a valid next step. 
	 * @private
	 * @param {boolean} wantHint Require link to have hints
	 * @param {array of CTATExampleTracerInterpretation} rtnInterp If not null, supply (in element 0) or return the interpretation used
	 * @return {CTATExampleTracerLink} Highest unvisited link that would be a good next step
	 */
	function _getBestNextLink(wantHint, rtnInterp)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in _getBestNextLink");

		var interp = (rtnInterp !== null && typeof(rtnInterp) !== 'undefined' && rtnInterp[0] !== null && typeof(rtnInterp[0]) !== 'undefined' ? rtnInterp[0] : null);
		
		if(interp === null || typeof(interp) === 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in _getBestNextLink first if condition");
			interp = that.getBestInterpretation();
		}	

		if(rtnInterp !== null && typeof(rtnInterp) !== 'undefined')
		{
			rtnInterp[0] = interp;
		}

		that.ctatdebug("CTATExampleTracerTracer --> in _getBestNextLink interp.getPaths(): " + interp.getPaths().size);

		var path = CTATExampleTracerPath.getBestPath(interp.getPaths());

		that.ctatdebug("CTATExampleTracerTracer --> in _getBestNextLink path: " + path);

		var highestLink = getHighestUntraversedLink(interp, path, wantHint);

		that.ctatdebug("CTATExampleTracerTracer --> in _getBestNextLink highestLink: " + highestLink);

		if(highestLink !== null && typeof(highestLink) !== 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in _getBestNextLink returning highestLink");
			return highestLink;
		}

		var lastLink = null;

		//dbg = path;
		that.ctatdebug("CTATExampleTracerTracer --> in _getBestNextLink returning path.getLinks() " + path.getLinks().size);
		that.ctatdebug("CTATExampleTracerTracer --> in _getBestNextLink returning path.getSortedLinks() " + path.getSortedLinks());


		var breakVal = false;
		var val = null;

		//ITERATE OVER LINKS OR SORTEDLINKS?
		path.getSortedLinks().forEach(function(link)
		{
			if(breakVal === true)
			{
				return;
			}

			that.ctatdebug("CTATExampleTracerTracer --> in _getBestNextLink in for loop" + link);
			lastLink = link;

			if((interp.getTraversalCount(link) < link.getMaxTraversals()) && (wantHint === false || that.nonEmptyHints(link, interp.getVariableTable()) > 0) && graph.observesOrderingConstraints(interp.getMatchedLinks(), link, path.getLinks(), result) && doneStepOK(interp, link, path.getLinks()))
			{
				that.ctatdebug("CTATExampleTracerTracer --> in _getBestNextLink in link: " + link);
				val = link;

				breakVal = true;
				return;
				//return link;
			}
		});

		if(breakVal === true)
		{
			return val;
		}
		
		//no last node here

		that.ctatdebug("CTATExampleTracerTracer --> out of _getBestNextLink, returning: " + lastLink);

		return null;
	}

	/**
	 * Find the first (moving from the start state) unvisited link in an interpretation, such
	 * that it would be a preferred next step.
	 * @private
	 * @param {CTATExampleTracerInterpretation} interp Interpretation with set of visited links
	 * @param {CTATExampleTracerPath} path Preferred path within the interp
	 * @param {boolean} wantHint Require link to have hints
	 * @return {null} If no link qualifies
	 */
	function getHighestUntraversedLink(interp, path, wantHint)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in getHighestUntraversedLink");

		var result = new CTATExampleTracerEvent(that, null);

		var lastLinkVisited = true;

		var suggestedLinks = []; //array of CTATExampleTracerLink

		//is this sorted or normal links?
		path.getLinks().forEach(function(link)
		{
			that.ctatdebug("CTATExampleTracerTracer --> in getHighestUntraversedLink in loop");

			if(interp.isTraversed(link) === true || isNoOp(link) === true)
			{
				that.ctatdebug("CTATExampleTracerTracer --> in getHighestUntraversedLink in if condition");

				lastLinkVisited = true;
				return;	
			}

			if(lastLinkVisited === true)
			{
				that.ctatdebug("CTATExampleTracerTracer --> in getHighestUntraversedLink in second if condition");

				var pNode = graph.getNode(link.getPrevNode());
				var outLinks = pNode.getOutLinks();

				outLinks.forEach(function(outLink)
				{
					if(wantHint && that.nonEmptyHints(outLink, interp.getVariableTable()) < 1)
					{
						return;
					}

					if(interp.getTraversalCount(outLink) >= outLink.getMaxTraversals())
					{
						return;
					}

					suggestedLinks.push(outLink);
				});
			}

			lastLinkVisited = (link === null || typeof(link) === 'undefined' ? false : (link.getMinTraversals() <= interp.getTraversalCount(link)));
		});


		//sorting suggestedLinks using the compare function from CTATExampleTracerLinkComaprator
		suggestedLinks.sort(function(a,b)
		{
			return (new CTATExampleTracerLinkComparator(interp, that)).compare(a, b);
		});

		that.ctatdebug("CTATExampleTracerTracer --> in getHighestUntraversedLink before sorting");

		var retVal = false;
		var val = null;

		suggestedLinks.forEach(function(suggestedLink)
		{	
			if(retVal === true)
			{
				return;
			}

			that.ctatdebug("CTATExampleTracerTracer --> in getHighestUntraversedLink after sorting in fro loop");

			if(isPathOK(suggestedLink, interp, path, isDemonstrateMode, result) === true)
			{
				that.ctatdebug("CTATExampleTracerTracer --> in getHighestUntraversedLink after sorting first if");
				val = suggestedLink;
				retVal = true;
				return;
				//return suggestedLink;
			}

			interp.getPaths().forEach(function(otherPath)
			{
							
				if(retVal === true)
				{
					return;
				}

				that.ctatdebug("CTATExampleTracerTracer --> in getHighestUntraversedLink after sorting in inner loop");

				if(path !== otherPath)
				{
					that.ctatdebug("CTATExampleTracerTracer --> in getHighestUntraversedLink after sorting second if");

					if(isPathOK(suggestedLink, interp, otherPath, isDemonstrateMode, result) === true)
					{
						that.ctatdebug("CTATExampleTracerTracer --> in getHighestUntraversedLink after sorting third if");
						val = suggestedLink;
						retVal = true;
						return;
						//return suggestedLink;
					}
				}
			});
		});

		if(retVal === true)
		{
			return val;
		}

		that.ctatdebug("CTATExampleTracerTracer --> out of getHighestUntraversedLink");

		return null;
	}

	/**
	 * Tell whether the given link is a no-op for example tracing. A no-op link
	 * is automatically considered traversed in a path, but generates no transaction or log.
	 * @private
	 * @param {CTATExampleTracerLink} link 
	 * @return {boolean} True if minTraversals and maxTraversals are both zero
	 */
	function isNoOp(link)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in isNoOp");

		if(link === null || typeof(link) === 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in isNoOp first if branch");
			return false;
		}
		else
		{
			that.ctatdebug("CTATExampleTracerTracer --> in isNoOp second if branch");
			return ((link.getMinTraversals() < 1) && (link.getMaxTraversals() < 1));
		}
	}

/***************************** PRIVILEDGED METHODS *****************************************************/


	/**
	 * Return the results of the last trace.
	 * @return {CTATExampleTracerEvent} Null if no trace
	 */
	this.getResult = function ()
	{
		that.ctatdebug("CTATExampleTracerTracer --> in getResult");
		return result;
	};

	//NOTE: We decided to remove listeners totally, therefore this function is not needed anymore
	//The listeners container will be removed too
	/*
	 * @param e of type CTATExampleTracerEvent
	 * @return undefined
	 */
	/*this.fireExampleTracerEvent = function (e)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in fireExampleTracerEvent");

		listeners.forEach(function(listener)
		{
			listener.ExampleTracerEventOccurred(e);
		});

		that.ctatdebug("CTATExampleTracerTracer --> out of fireExampleTracerEvent");
	};*/

	/**
	 * @return {undefined}
	 */
	this.resetTracer = function ()
	{
		that.ctatdebug("CTATExampleTracerTracer --> in resetTracer entering");

		if(interpretations === null || typeof(interpretations) === 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in resetTracer making interpretations array");
			interpretations = []; // array of CTATExampleTracerInterpretation
		}
		else
		{
			that.ctatdebug("CTATExampleTracerTracer --> in resetTracer clearing interpretations array");
			interpretations.length = 0; //clearing array
		}

		var tempInterp = new CTATExampleTracerInterpretation(getAllPaths());
		that.ctatdebug("CTATExampleTracerTracer tempInterp--> in resetTracer tempInterp " + tempInterp);
			
		if(startStateVT !== null && typeof(startStateVT) !== 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in resetTracer startStateVT if condition");
			tempInterp.setVariableTable(startStateVT);
		}

		interpretations.push(tempInterp);
		bestInterpretation = tempInterp;

		if(incorrectActionMatches === null || typeof(incorrectActionMatches) === 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in resetTracer incorrectActionMatches if condition");
			incorrectActionMatches = {}; //Map of integer - CTATExampleTracerLink
		}
		else
		{
			that.ctatdebug("CTATExampleTracerTracer --> in resetTracer else branch of incorrectActionMatches if condition");

			//clear map
			for (var property in incorrectActionMatches)
			{
				if(incorrectActionMatches.hasOwnProperty(property))
				{
					delete incorrectActionMatches[property];
				}
				
			} 
		}

		if(studentSAIs === null || typeof(studentSAIs) === 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in resetTracer last if condition");
			studentSAIs = []; //array of CTATExampleTracerSAI
		}
		else
		{
			that.ctatdebug("CTATExampleTracerTracer --> in resetTracer else branch of last if condition");
			studentSAIs.length = 0; //clear array
		}

		that.ctatdebug("CTATExampleTracerTracer --> in resetTracer before iterating over all the links in the graphs");
		
		graph.getLinks().forEach(function(link) 
		{
			link.getMatcher().resetMatcher();//note changed name of last function
		});

		that.ctatdebug("CTATExampleTracerTracer --> out of resetTracer");
	};

	/**
	 * Trace an attempt against the graph.
	 * @param {integer} linkID  Preselected link, used for tutor-performed actions
	 * @param {CTATExampleTracerSAI} sai Student SAI
	 * @return {boolean} Result of the private _evaluate
	 */
	this.evaluate = function(linkID, sai)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in public evaluate");

		result = new CTATExampleTracerEvent(that);

		result.setStudentSAI(sai);

		if(linkID > 0)
		{
			that.ctatdebug("CTATExampleTracerTracer --> in public evaluate if condition");

			var link = graph.getLinkByID(linkID);

			result.addPreloadedLinkMatch(link);
		}

		that.ctatdebug("CTATExampleTracerTracer --> out of public evaluate calling private _evaluate");
		return _evaluate(result, false, true);
	};

	/**
	 * Return the number of nonempty hints on an link.
	 * @param {CTATExampleTracerLink} link 
	 * @param {CTATVariableTable} vt For evaluating formulas in hints
	 * @return {integer} Number of non-empty hints, evaluated dynamically
	 */
	this.nonEmptyHints = function(link, vt)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in nonEmptyHints");

		if(link === null || typeof(link) === 'undefined')
		{
			return 0;
		}

		var hints = link.getAllNonEmptyHints();

		that.ctatdebug("CTATExampleTracerTracer --> in nonEmptyHints: link.getAllNonEmptyHints() " + link.getAllNonEmptyHints().length);

		if(hints === null || typeof(hints) === 'undefined' || hints.length < 1)
		{
			return 0;
		}

		if(that.getResult() === null || typeof(that.getResult()) === 'undefined')
		{
			return hints.length;
		}

		var sai = that.getResult().getStudentSAI();

		//extra calls because interpolatedHints is not in CTATExampleTracerLink anymore but in CTATExampleTracerEvent
		//same goes for interpolatedSelection/Action/Input
		result.setInterpolateSAI(sai.getSelectionAsString(), sai.getActionAsString(), sai.getInputAsString());
		result.setInterpolatedHints(link.interpolateHints(vt));

		hints = link.getHints();

		that.ctatdebug("CTATExampleTracerTracer --> returning from nonEmptyHints link.getHints()" + link.getHints().length);

		return hints.length;
	};

	/**
	 * @param {CTATExampleTracerEvent} givenResult 
	 * @return {CTATExampleTracerLink}
	 */
	this.traceForHint = function(givenResult)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in traceForHint");

		var evalRet = _evaluate(givenResult, true, false);

		that.ctatdebug("CTATExampleTracerTracer --> in traceForHint return from evaluate " + evalRet);
		that.ctatdebug("CTATExampleTracerTracer --> in traceForHint result from evaluate " + givenResult.getResult().toString());
		that.ctatdebug("CTATExampleTracerTracer --> in traceForHint link   from evaluate " + givenResult.getReportableLink());

		if(givenResult.getResult().toString() === CTATExampleTracerLink.CORRECT_ACTION.toString())
		{
			that.ctatdebug("CTATExampleTracerTracer --> in traceForHint in if condition");
			var hintLink = givenResult.getReportableLink();
			givenResult.setTutorSAI(new CTATExampleTracerSAI(hintLink.getSelection(), hintLink.getAction(), hintLink.getInput(), "student"));
			return hintLink; //of type CTATExampleTracerLink
		}

		that.ctatdebug("CTATExampleTracerTracer --> out of traceForHint, returning null");
		return null;
	};

	/**
	 * Attempt to find a link using the given sai or, if that fails,
	 * using getBestNextLink().
	 * This method resembles a hint request, but accepts the first selection
	 * element instead of pulling the previous focus from an ordinary hint
	 * request.
	 * @param {CTATExampleTracerEvent} givenResult With CTATExampleTracerEvent.getStudentSAI() set
	 * @return {CTATExampleTracerLink} CORRECT link that best matches selection, or result of 
	 * getBestNextLink(); can return null
	 */
	this.matchForHint = function(givenResult)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in matchForHint");

		var selection = (givenResult.getStudentSAI() === null || typeof(givenResult.getStudentSAI()) === 'undefined' ? null : givenResult.getStudentSAI().getSelectionAsArray()); //of type array

		if(selection !== null && typeof(selection) !== 'undefined' && selection.length > 0)
		{
			that.ctatdebug("CTATExampleTracerTracer --> in matchForHint first if");

			givenResult.setWantReportableHints(true); //to collect hints in result
			
			var link = that.traceForHint(result); //of type CTATExampleTracerLink

			if(link !== null && typeof(link) !== 'undefined' && givenResult.getReportableHints().length > 0)
			{
				that.ctatdebug("CTATExampleTracerTracer --> in matchForHint second if");
				givenResult.setInterpolatedHints(givenResult.getReportableHints());
				return link;
			}
		}

		that.ctatdebug("CTATExampleTracerTracer --> out of matchForHint");

		return that.getBestNextLink(true, result); // no selection => nothing to match
	};

	/**
	 * Attempt to find a link using the given sai or, if that fails,
	 * using getBestNextLink().
	 * This method resembles a hint request, but accepts the first selection
	 * element instead of pulling the previous focus from an ordinary hint
	 * request.
	 * @param {boolean} wantHint If true, return null if the link chosen has no hints
	 * @param {CTATExampleTracerEvent} givenResult
	 * @return {CTATExampleTracerLink} CORRECT link that best matches selection, or result of 
	 * getBestNextLink(); can return null
	 */
	this.getBestNextLink = function(wantHint, givenResult)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in getBestNextLink");

		var interp = [];

		var etLink = _getBestNextLink(wantHint, interp);

		if(etLink === null || typeof(etLink) === 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in getBestNextLink returning null");
			//there is no next step
			return null;
		}

		if(etLink.getType().toString() === CTATExampleTracerLink.BUGGY_ACTION.toString())
		{
			that.ctatdebug("CTATExampleTracerTracer --> in getBestNextLink return null no correct suboptimal step");
			// means there's no correct or suboptimal next step
			return null;
		}
		else
		{
			that.ctatdebug("CTATExampleTracerTracer --> in getBestNextLink else branch");

			var m = etLink.getMatcher();

			that.ctatdebug("CTATExampleTracerTracer --> in getBestNextLink got matcher");

			//not doing solverMatcher for now

			that.ctatdebug("CTATExampleTracerTracer --> in getBestNextLink got matcher " + interp[0]);

			givenResult.setInterpolatedHints(etLink.interpolateHints(interp[0].getVariableTable()));
			
			that.ctatdebug("CTATExampleTracerTracer --> in getBestNextLink setInterpolatedHints");

			givenResult.setReportableLink(etLink);

			givenResult.setTutorSAI(new CTATExampleTracerSAI(etLink.getSelection(), etLink.getAction(), etLink.getInput(), "student"));
			that.ctatdebug("CTATExampleTracerTracer --> in getBestNextLink return etLink");

			return etLink;
		}
	};

	/**
	 * Top-level method to process a hint request.
	 * @param {array} givenSelection 
	 * @param {array} givenAction
	 * @param {array} givenInput
	 * @param {string} givenActor 
	 * @param {array of CTATExampleTracerEvent} rtnResult If not null, put result here
	 * @param {boolean} allowHintBias
	 * @return {CTATExampleTracerLink}
	 */
	this.doHint = function(givenSelection, givenAction, givenInput, givenActor, rtnResult, allowHintBias)
	{
		that.ctatdebug("CTATExampleTracerTracer --> in doHint");

		var link = null; //of type CTATExampleTracerLink
		var previousFocus = null; //of type array
		var previousAction = null; //of type array

		console.log("---" + givenAction[1] == CTATMsgType.PREVIOUS_FOCUS);
		that.ctatdebug("-----");
		that.ctatdebug((givenAction !== null) && (typeof(givenAction) !== 'undefined'));
		that.ctatdebug((givenAction.length > 1) && (givenAction[1].toString() === CTATMsgType.PREVIOUS_FOCUS.toString()));
		that.ctatdebug((givenSelection !== null) && (typeof(givenSelection) !== 'undefined'));
		that.ctatdebug((givenSelection.length > 1) && (givenSelection[1] !== null) && (typeof(givenSelection[1]) !== 'undefined'));
		that.ctatdebug((givenSelection[1].toString() !== "null".toString()));

		if(allowHintBias && (givenAction !== null) && (typeof(givenAction) !== 'undefined') && (givenAction.length > 1) && (givenAction[1].toString() === CTATMsgType.PREVIOUS_FOCUS.toString()) && (givenSelection !== null) && (typeof(givenSelection) !== 'undefined') && (givenSelection.length > 1) && (givenSelection[1] !== null) && (typeof(givenSelection[1]) !== 'undefined') && (givenSelection[1].toString() !== "null".toString()))
		{
			that.ctatdebug("CTATExampleTracerTracer --> in doHint first if condition");
			previousFocus = []; //array
			previousFocus.push(givenSelection[1]);

			if(givenAction.length > 2)
			{
				that.ctatdebug("CTATExampleTracerTracer --> in doHint inner if condition");

				previousAction = []; //of type array

				for(var i = 2; i < givenAction.length; i++)
				{
					that.ctatdebug("CTATExampleTracerTracer --> in doHint for loop " + i);
					previousAction.push(givenAction[i]);
				}
			}
		}

		that.ctatdebug("CTATExampleTracerTracer --> in doHint out of if condition");

		result = new CTATExampleTracerEvent(that, new CTATExampleTracerSAI(previousFocus, previousAction, null, givenActor));
		
		that.ctatdebug("CTATExampleTracerTracer --> in doHint result: " + (result === null || result === undefined));

		link = that.matchForHint(result);

		if(rtnResult !== null && typeof(rtnResult) !== 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in doHint last if condition");
			rtnResult[0] = result;
		}

		that.ctatdebug("CTATExampleTracerTracer --> returning from doHint: " + link);

		return link;
	};

	/**
	 * @return {CTATExampleTracerInterpretation}
	 */
	this.getBestInterpretation = function()
	{
		that.ctatdebug("CTATExampleTracerTracer --> in getBestInterpretation");

		if(bestInterpretation === null || typeof(bestInterpretation) === 'undefined')
		{
			that.ctatdebug("CTATExampleTracerTracer --> in getBestInterpretation in if condition");
			bestInterpretation = findBestInterpretation(that.getInterpretationsInternal());
		}

		that.ctatdebug("CTATExampleTracerTracer --> out of getBestInterpretation " + (bestInterpretation === null || bestInterpretation === undefined));

		return bestInterpretation;
	};

	/**
	 * Internal accessor
	 * @return {array of CTATExampleTracerInterpretation} Returns the current interpretations in the tracer
	 */
	this.getInterpretationsInternal = function()
	{
		that.ctatdebug("CTATExampleTracerTracer --> in getInterpretationsInternal");

		return interpretations;
	};

	/**
	 * @return {string}
	 */
	this.toString = function()
	{
		var s = "TracerState\n";

        if(interpretations === null)
        {
            s += "(no interpretations)\n";
        }
        else
        {
			interpretations.forEach(function(interp)
			{
				s += interp.toString() + "\n";
			});
        }
		return s;
	};

    /**
     * If the given message has selection and input elements, then bind variables in the
     * CTATVariableTable for as many selection, input pairs as you have.
     * @param {CTATMessage} msg
     * @return {undefined}
     */
	this.addInterfaceVariables = function(msg)
	{
		/*var v = msg. getProperty("Selection");

		if(selection === null || typeof(selection) === 'undefined' || input === null || typeof(input) === 'undefined')
		{
			return;
		}

		var nVars = ;

		for(var i = 0; i < nVars; i++)
		{
			var s = selection[i];
		}*/

		var sel = msg.getSelection();
  		var inp = msg.getInput();
  		startStateVT.put(sel, inp);

  		if(interpretations === null || typeof(interpretations) === 'undefined')
  		{
  			return;
  		}

  		interpretations.forEach(function(interp)
  		{
  			var currVT = interp.getVariableTable();

  			if(vt === null || typeof(vt) === 'undefined')
  			{
  				return; //instead of continue
  			}

  			vt.put(key, value);
  		});
	};

	/**
	  * @return {CTATExampleTracerNode}
	  */
	this.findCurrentState = function()
	{
		var reportableInterpretation = that.getBestInterpretation(); //of type CTATExampleTracerInterpretation
		var currBestPath = CTATExampleTracerPath.getBestPath(reportableInterpretation.getPaths()); //of type CTATExampleTracerPath
		var highestLink = CTATExampleTracerPath.getShallowestLink(currBestPath.getLinks()); //of type CTATExampleTracerLink

		var currNode = highestLink.getPrevNode();

		var returnValue = false;

		//Correct over suboptimal, so first check for correct
		currNode.getOutLinks().forEach(function(link){

			if(returnValue === true)
			{
				return;
			}

			if(link.getMinTraversals() > 0 && CTATExampleTracerLink.CORRECT_ACTION.toString().toUpperCase() === link.getActionType().toString().toUpperCase())
			{
				returnValue = true;
				return;
			}
		});

		if(returnValue === true)
		{
			return currNode;
		}

		//Correct over suboptimal, check for suboptimal
		returnValue = false;

		currNode.getOutLinks().forEach(function(link){

			if(returnValue === true)
			{
				return;
			}

			if(link.getMinTraversals() > 0 && CTATExampleTracerLink.BUGGY_ACTION.toString().toUpperCase() === link.getActionType().toString().toUpperCase())
			{
				returnValue = true;
				return;
			}
		});

		if(returnValue === true)
		{
			return currNode;
		}
		else
		{
			return null;
		}
	};

/****************************** PUBLIC METHODS ****************************************************/

/*************************** PART OF CONSTRUCTOR ************************************/

	//Used to be part of the constructor being called from here
	//We had a problem with the startStateNode so we decided to remove this from here 
	//and call it outside whenever we are creating the graph from the brd.
	//this.resetTracer(); 
};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATExampleTracerTracer.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerTracer.prototype.constructor = CTATExampleTracerTracer;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerTracer;
}