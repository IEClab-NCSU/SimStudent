goog.provide("CTATNoolsTracer");

goog.require("CTATNoolsTracerUtil");
goog.require("CTATNoolsSessionManager");

/**
*	@Constructor
*	@param logFunc function to pass log msgs to
*/
var CTATNoolsTracer = function(logFunc)
{	
	var log = logFunc;
	var pointer = this;
	var initialized = false;
	
	//nools entry point
	var flow = null;
	//active nools session
	var session = null; 
	
	//most recent student/tutor SAIs
	var studentSAIs = {},
		ruleChains = {
			selectionMap: {}
		},
		lastStudentSAI = null;
	
	//messages sent on correct/incorrect matches
	var successOrBugMsg = null;
	
	//tracks current chain of rule activations
	var currChain = {
		links: [],
		lengthAtBranchStack: [],
		matchType: null
	};
	
	//whether the rule engine is going forward
	var iAmMovingForward = false;
	
	//utility classes
	var noolsUtil = null;
	var sessionManager = null;
	
	//set during initialization
	var startStateMsgs = [],
		problemConfig = {};
	
	//whether a match was found this cycle and last cycle, respectively
	var lastFoundMatch = true;
	
	//if nools should backtrack
	var inBacktrackMode = false;
	
	//whether we're in the midst of a hint match cycle
	var iAmHintMatching = false,
		lastMatchWasHint = false;
	
	//whether old activations should remain on the agenda
	var pruneOldActivations = false;
	
	/************ Public **************/
	
	/**
	*	Get last success/buggymsg
	*/
	this.getSuccessOrBugMsg = function()
	{
		return successOrBugMsg || "";
	};
	
	/**
	*	Initialize the model, given a source file containing rules and types
	*	@param {String} srcFile a URL to the file containing the model
	*	@param {Function} cbk a function to call on successful compilation of the model
	*	@param {errCbk}	errCbk a function to call on error
	*/
	this.init = function(srcFile, cbk, errCbk)
	{
		log("debug", "noolstracer init w/ source file: "+ srcFile);
		if (!nools)
		{
			log("error", 'Error: nools is not defined');
			errCbk("nools is not defined");
			return;
		}
		
		noolsUtil = new CTATNoolsTracerUtil();
		sessionManager = new CTATNoolsSessionManager();
		var baseUrl = noolsUtil.relativeToAbsolute(srcFile);
		//get problem file
		$.ajax(srcFile, {
			dataType: "text",
			success: (problemData) => {
				//pre-process imports
				_handleImports(problemData, baseUrl, null, function(file) {
					//compile flow
					if (!(_compile(file, srcFile) === true))
					{
						log("error", "CTATNoolsTracer: Error compiling model");
						return;
					}
					//create session
					let sid = pointer.createSession();					
					//do initialization match
					_firstMatch(()=>{
						cbk(sid, startStateMsgs, problemConfig);
					})
				});
			},
			error: (req, err, errThrown) => {
				ctatdebug("Error retrieving rule file: ")
				ctatdebug(err);
				errCbk(err);
			}
		});
	};
	
	/**
	*	Instantiate the model
	*	@param {Array}	initialFacts a list of facts to assert
	*	@return {int | null} id of new session on success, null on fail
	*/
	this.createSession = function(initialFacts)
	{
		var s = sessionManager.createSession(flow);
		if (s)
		{
			session = s.session
			//assert initial facts if any
			initialFacts && pointer.assert(initialFacts);
			//set up event listeners
			pointer.setListener("assert", _onAssert);
			pointer.setListener("retract", _onRetract);
			pointer.setListener("modify", _onModify);
			pointer.setListener("state_save", _onStateSave);
			pointer.setListener("state_restore", _onStateRestore);
			pointer.setListener("backtrack", _onBacktrack);
			pointer.setListener("fire", _onFire);
			pointer.setListener("agenda_insert", _onAgendaInsert);
			pointer.setListener("agenda_retract", _onAgendaRetract);
			pointer.setListener("agenda_empty", _onAgendaEmpty);
			return s.id;
		}
		return null;
	};
	
	/**
	*	Set a particular session as active
	*	@param {String} sId the ID of the session
	*	@returns {boolean} true if set successfully
	*/
	this.setActiveSession = function(sId)
	{
		var s = sessionManager.getSession(sId);
		if (s)
			session = s;
		
		return !!s;
	};
		
	/**
	*	Given an SAI, determine whether it is a correct action by feeding it to nools
	*	@param {Object} sai the SAI
	*	@param {cbk} function to call on complete, should take as args a boolean representing
	*		whether the sai was correct and the tutor-predicted sai
	*	@param {boolean} isHintMatch whether or not this is a hint request
	*/
	this.evaluate = function(sai, cbk, isHintMatch) 
	{
		log("debug", "noolsTracer.evaluate, is"+(isHintMatch?" ":" not ")+"a hint match");
		if (!isHintMatch || (isHintMatch && lastFoundMatch)) {
			var callback;
			_clearPerMatchVars();
			session.prepForMatch();
			_assertSAI(sai);
			if (isHintMatch) {
				iAmHintMatching = true;
				callback = _handleHintMatchResult.bind(pointer, cbk, true)
			} else {
				callback = _handleMatchResult.bind(pointer, cbk);
			}
			_match(callback);
		} else {
			log("debug", "re-using hints from last match");
			_handleHintMatchResult(cbk, false);
		}
	};

	/**
	*	Register an event listener to file on rule engine events
	*	@param {String} e the event to listen for
	*	@param {Function} f function to call on event fire
	*	@param {Number} optId optional ID of session to listen on
	*					defaults to currently active session
	*/
	this.setListener = function(e, f, optId)
	{
		var s = optId ? sessionManager.getSession(optId) : session;
		if (s)
		{
			s.on(e, f);
		}
	};
	
	/**
	*	Get map of all studentSAIs evaluated so far
	*	@returns {Object} object storing SAIs, keyed by selection
	*/
	this.getStudentSAIs = function()
	{
		return studentSAIs;
	};
	
	/**
	*	Get map of all SAIs predicted by the model so far
	*	@returns {Object} object storing SAIs, keyed by selection
	*/
	this.getTutorSAIs = function()
	{
		return ruleChains.selectionMap;
	};
	
	/**
	*	Reset the model to its initial state
	*	@param {function} cbk function to call on complete
	*/
	this.reset = function(cbk)
	{
		pointer.createSession();
		initialized = false;
		studentSAIs = {};
		ruleChains.selectionMap = {};
		ruleChains.firstTutorSelection = null;
		ruleChains.matchChain = null;
		ruleChains.bugMatchChain = null;
		lastStudentSAI = null,
		successOrBugMsg = null;
		lastFoundMatch = true;
		
		_firstMatch(cbk)
	}
	
	/************* Private **************/
	
	/**
	*	Run first (initialization) match on a new session
	*	@param {function} cbk function to call on complete
	*/
	function _firstMatch(cbk) {
		startStateMsgs = [];
		problemConfig = {};
		session.setDoBacktracking(false);
		log("debug", "starting initial match");
		_match(()=>{
			lastFoundMatch = true;
			session.setPruneOldActivations(pruneOldActivations);
			session.setDoBacktracking(inBacktrackMode);
			initialized = true;
			typeof cbk === "function" && cbk();
		});
	}
	
	/**
	*	Set current success message
	*	@param msg the new message
	*/
	function _setSuccessOrBugMsg(msg) 
	{
		msg && (successOrBugMsg = msg);
	}
	
	/**
	*	Search the text of a model file for import statements and import the urls
	*	@param {String} fileData the model file text
	*	@param {String} baseURL the absolute URL of the file
	*	@param {Object|null} imported map of urls to whether they've been imported, prevents duplicate imports
	*	@param {Function} cbk function to pass the assembled file to
	*	@param {Object|undefined} cntr maintains state related to # of requests in flight and stores imported file chunks
	*	@param {Number|undefined} depth the level of recursion of a given call to the function
	*	@param {Number|undefined} width dictates the spot this chunk will be inserted at in the final file
	*/
	function _handleImports(fileData, baseURL, imported, cbk, cntr, depth, width)
	{
		log("debug", "handleImports for "+baseURL);
		imported = imported || {};
		cntr = cntr || {count: 0, chunks: []};
		depth = depth || 0;
		width = width || 0;
		var importRegex = /\bimport\s?\(('|")?([^()'"\s]*)\1?\);/g;
		var match, matches = [];
		//match for all import statements
		while ((match = importRegex.exec(fileData)) != null) {
			let url = noolsUtil.relativeToAbsolute(match[2], baseURL);
			if (!imported[url])
			{
				matches.push(url);
				imported[url] = true;
			}
			else
				log("debug", "skipping duplicate import: "+url);
			fileData = fileData.replace(match[0], "");
			importRegex.lastIndex -= match[0].length;
		}
		//add file data to chunks
		if (!cntr.chunks[depth])
			cntr.chunks[depth] = [];
		cntr.chunks[depth][width] = fileData;
		//inc request counter
		cntr.count += matches.length;
		log("debug", baseURL+" is importing "+matches.length+" files");
		if (matches.length > 0)
		{
			for (let i = 0; i < matches.length; i++)
			{
				let url = matches[i];
				$.ajax(url, {
					dataType: "text",
					success: (data) => {
						_handleImports(data, url, imported, cbk, cntr, depth+1, i);
						cntr.count--;
						if (cntr.count == 0)
						{
							//put it all together
							cbk(_assembleChunks(cntr.chunks));
						}
					},
					error: (req, err, errThrown) => {
						log("error", "Error in _handleImports() : ");
						log("error", err);
					}
				});
			}
		}
		else if (depth == 0)
		{
			//no imports
			cbk(_assembleChunks(cntr.chunks));
		}
	}
	
	/**
	*	Concatenate model file chunks into one file
	*	@param {Array} chunks 2d array of file data chunks
	*	@returns {String} the concatenated nools file
	*/
	function _assembleChunks(chunks)
	{
		var ret = "",
			chunkLvl;
		
		while (chunks.length > 0)
		{
			chunkLvl = chunks.pop();
			while (chunkLvl.length > 0)
			{
				ret += (chunkLvl.shift() + '\n');
			}
		}
		return ret;
	}
	
	/**
	*	Compile the rule model
	*	@param {String} model the DSL model to compile, see https://github.com/C2FO/nools for syntax
	*	@returns {Error | true}
	*/
	function _compile(model, flowName) 
	{
		var error = null;
		var scope = {
			getStudentSAIs: this.getStudentSAIs,
			checkSAI: _checkSAI,
			setSuccessOrBugMsg: _setSuccessOrBugMsg,
			setProblemAttribute: _setProblemAttribute,
			getInitialized: function() {return initialized},
			backtrack: function() {session.backtrack(true)},
			assert: _assertOverride,
			modify: _modifyOverride,
			retract: _retractOverride
		};
		
		try{
			flow = nools.compile(model, {"name": flowName || "CTATFlow", 
										"scope": scope});
		}catch(err) {
			log("error", "Error compiling rule file: "+err);
			initialized = false;
			error = err;
		}
		
		return error ? error : true;
	}
	
	
	/**
	*	Assert a fact in the working memory
	*	@param {Object} fact the fact to assert
	*/
	function _assert(fact) 
	{
		fact && session.assert(fact);
	}
	
	/**
	*	Assert a given student input into working memory
	*	@param {Object} newSAI the student input
	*/
	function _assertSAI(newSAI)
	{
		log("debug", "Assert sai: "+JSON.stringify(newSAI));
		var oldSAI = studentSAIs[newSAI.selection];
			
		if (oldSAI)
		{
			oldSAI.input = newSAI.input;
			session.modify(oldSAI);
		}
		else
		{
			var SAI = flow.getDefined("StudentValues");
			oldSAI = new SAI(newSAI.selection, newSAI.action, newSAI.input);
			session.assert(oldSAI);
			studentSAIs[newSAI.selection] = oldSAI;
		}
		lastStudentSAI = newSAI;
	}
	
	/**
	*	Start the nools match cycle
	*	@param {Function} cbk function to handle the results of the match
	*/
	function _match(cbk)
	{
		session.match().then(cbk, (err) => {
			log("error", "nools encountered an error: "+err);
		});
	}
	
	/**
	*	Process the result of an evaluation cycle
	*	@param {Function} cbk function to pass the result to
	*/
	function _handleMatchResult(cbk)
	{
		lastMatchWasHint = false;
		var result = "no_model",
			selectionMap = ruleChains.selectionMap,
			selectionList,
			chainToRtn;
			
		if (currChain.links.length > 0) { //TODO move to halt
			_storeChain();
		}
		
		if (ruleChains.matchChain) {
			chainToRtn = ruleChains.matchChain;
			result = "correct";
		} else if (ruleChains.bugMatchChain) {
			chainToRtn = ruleChains.bugMatchChain;
			result = "bug";
		} else if (selectionMap[lastStudentSAI.selection]){
			chainToRtn = selectionMap[lastStudentSAI.selection][0];
		} else {
			chainToRtn = selectionMap[ruleChains.firstTutorSelection][0];
		}

		lastFoundMatch = (result === "correct");
		
		cbk(result, chainToRtn);
	}
	
	/**
	*	Process the results of a hint match cycle
	*	@param {function} cbk function to pass results to
	*	@param {bool} wasRealMatch whether we actually ran a match cycle or re-using results from last one
	*/
	function _handleHintMatchResult(cbk, wasRealMatch)
	{
		if (wasRealMatch) {
			iAmHintMatching = false;
			lastMatchWasHint = true;
			lastFoundMatch = false;
		}
		cbk(ruleChains.selectionMap, ruleChains.firstTutorSelection);
	}
	
	/**
	*	Compare two SAIs for equality
	*	@param {Object} predictedSAI the tutorSAI
	*	@param {function} optComparator optional function to do the comparison, should return true if match is valid
	*	@param {boolean} isBuggyStep whether predictedSAI is a buggy step
	*/
	function _checkSAI(predictedSAI, optComparator, isBuggyStep)
	{
		var res = false,
			tSelection = predictedSAI.selection,
			tAction = predictedSAI.action,
			tInput = predictedSAI.input,
			sSelection = lastStudentSAI.selection,
			sAction = lastStudentSAI.action,
			sInput = lastStudentSAI.input;
			
		log("sai_check", 'checkSAI');
		log("sai_check", 'student: '+JSON.stringify(lastStudentSAI));
		log("sai_check", 'tutor  : '+JSON.stringify(predictedSAI));
		currChain.links[currChain.links.length-1].sai = {
			selection: (tSelection && tSelection !== "not_specified") ? tSelection : "",
			action: (tAction && tAction !== "not_specified") ? tAction : "",
			input: (tInput && tInput !== "not_specified") ? tInput : ""
		};
		
		if (!iAmHintMatching) {
			if (optComparator && typeof optComparator === "function") {
				res = optComparator(lastStudentSAI, predictedSAI);
			}
			else {
				let i1 = !isNaN(sInput) ? parseInt(sInput, 10) : sInput, 
					i2 = !isNaN(tInput) ? parseInt(tInput, 10) : tInput;
				res = ((sSelection.toLowerCase() === tSelection.toLowerCase()) || tSelection === "not_specified") && 
					  ((sAction.toLowerCase() === tAction.toLowerCase()) || tAction === "not_specified") && 
					  ((i1 === i2) || i2 === "not_specified");
			}
			log("sai_check", (res ? 'Match' : 'No match')+", "+(isBuggyStep ? "was" : "was not")+" a buggy step");

			if (res) {
				if (isBuggyStep) {
					currChain.matchType = "buggy";
				} else {
					currChain.matchType = "match";
				}
			} else {
				currChain.matchType = null;
			}
		} else {
			log("sai_check", "Skipping check, we're hint matching");
		}
		return res;
	}
	
	/**
	*	Clear messages/sais/hints set by match cycle
	*/
	function _clearPerMatchVars()
	{
		successOrBugMsg = "";
		ruleChains.firstTutorSelection = null;
		ruleChains.selectionMap = {};
		ruleChains.matchChain = null;
		ruleChains.bugMatchChain = null;
		currChain.links = [];
		currChain.lengthAtBranchStack = [];
		currChain.matchType = null;
	}
	
	/**
	*	Set a problem configuration parameter
	*	@param {String} the name of the parameter to set
	*	@param {??} value the value of the parameter
	*/
	function _setProblemAttribute(attrName, value)
	{
		log("debug", "setProblemAttribute ("+attrName+", "+value+")");
		problemConfig[attrName] = value;
		if (attrName === "use_backtracking") {
			inBacktrackMode = (value && value !== "false") ? true : false;
		} else if (attrName === "prune_old_activations") {
			pruneOldActivations = (value && value !== "false") ? true : false;
		}
	}
	
	/**
	*	Called when end of a chain is reached to store data about the chain
	*	Three cases where the current chain should be stored:
	*		-backtrack event when iAmMovingForward == true
	*		-onAgendaEmpty when iAmMovingForward == true
	*		-handleMatchResult when currChain has > 0 links
	*/
	function _storeChain()
	{
		var	links = currChain.links,
			selectionMap = ruleChains.selectionMap,
			link,
			linkSAI,
			chainObj,
			ruleList = [],  //rules activated in chain
			skillList = [], //associated skills
			hintList = [],  //any hints generated
			selection,		//selection property of the tutorSAI
			finalSAI = {	//SAI predicted by the chain
				selection: "",
				action: "",
				input: ""
			};
		//generate rule/skill/lists, predicted sai
		for (let i = 0; i < links.length; i++){
			link = links[i];
			linkSAI = link.sai;
			ruleList.push(link.ruleName);
			skillList = skillList.concat(_getAssociatedSkills(link.ruleName));
			hintList = hintList.concat(link.hints);
			if (linkSAI) {
				linkSAI.selection && (selection = finalSAI.selection = linkSAI.selection);
				linkSAI.action && (finalSAI.action = linkSAI.action);
				linkSAI.input && (finalSAI.input = linkSAI.input);
			}
		}
		//sort hints by precedence first, insertion order second
		hintList.sort(function(a, b) {
			let ret = a.precedence - b.precedence;
			if (ret === 0) {
				ret = a.pos - b.pos;
			}
			return ret;
		});
		if (!selectionMap[selection]) {
			selectionMap[selection] = [];
			//keep track of first branch explored for ease of access later
			if (!ruleChains.firstTutorSelection) {
				ruleChains.firstTutorSelection = selection;
			}
		}
		chainObj = {
			sai: finalSAI,
			rules: ruleList,
			skills: skillList,
			hints: hintList
		};
		selectionMap[selection].push(chainObj);
		switch (currChain.matchType) {
			case "buggy":
			ruleChains.bugMatchChain = chainObj;
			break;
			case "match":
			ruleChains.matchChain = chainObj;
			break;
		}
		log("debug", "stored last chain -- rules: "+ruleList.join()+" ; finalSAI: "+JSON.stringify(finalSAI)+", matchType: "+currChain.matchType);
	}
	
	function _getAssociatedSkills(rule) {
		//TODO
		return [];
	}
	
	/******** Overridden nools WM functions *********/
	
	function _assertOverride(fact)
	{
		if (inBacktrackMode) {
			session.pushUndo("assert", fact);
		}
		return session.assert(fact);
	}
	
	function _retractOverride(fact)
	{
		if (inBacktrackMode) {
			session.pushUndo("retract", fact);
		}
		return session.retract(fact);
	}
	
	function _modifyOverride(fact, property, value)
	{
		if (inBacktrackMode) {
			session.pushUndo("modify", fact, property);
		}
		fact[property] = value;
		return session.modify(fact);
	}
	
	
	/********* Nools Event Listeners ***********/
	
	/**
	*	Called when the model backtracks (end of a chain is reached -or- all branches have been explored)
	*/
	function _onBacktrack(initiatedByModel) 
	{
		if (iAmMovingForward) {
			_storeChain();
		} else {
			currChain.lengthAtBranchStack.pop();
		}
		iAmMovingForward = false;
		log("backtrack", "backtracking, initiated by "+(initiatedByModel ? "model" : "engine"));
	}
	
	/**
	*	Called when a fact is asserted
	*	@param {Object} fact the asserted fact
	*	@param {String} type the type of the fact
	*/
	function _onAssert(fact, type)
	{
		log("assert", type + " fact asserted");
		if (!initialized) {
			if (type === "tpa") {
				log("debug", "got a TPA, creating start state message");
				startStateMsgs.push(noolsUtil.buildStartStateMsg(fact));
			}
		} else if (type === "hint") {
			let lastLink = currChain.links[currChain.links.length-1];
			if (!lastLink.hints) {
				lastLink.hints = [];
			}
			fact.pos = currChain.links.length+(lastLink.hints.length/(lastLink.hints.length+1));
			lastLink.hints.push(fact);
		}
	}
	
	/**
	*	Called when a fact is modified
	*	@param {Object} fact the asserted fact
	*	@param {String} type the type of the fact
	*/
	function _onModify(fact, type)
	{
		log("modify", type+" fact modified");
	}
	
	/**
	*	Called when a fact is retracted
	*	@param {Object} fact the retracted fact
	*	@param {String} type the type of the fact
	*/
	function _onRetract(fact, type)
	{
		log("retract", type+" fact retracted");
	}
	
	/**
	*	Called when the model saves its state (branch pt reached)
	*	@param {Object} state object representing the branch point
	*/
	function _onStateSave(state) 
	{
		log("state_save", "Hit branch point, agenda: "+JSON.stringify(state.agenda));
		var branchLenStack = currChain.lengthAtBranchStack,
			links = currChain.links,
			currentMatchType = currChain.matchType;
			
		branchLenStack.push({numLinks: links.length, matchType: currentMatchType});
	}
	
	/**
	*	Called when the model returns to a previous branch pt
	*	@param {Object} state object representing the branch point
	*/
	function _onStateRestore(state)
	{
		log("state_restore", "State restored, agenda: "+JSON.stringify(state.agenda));
		var branchLenStack = currChain.lengthAtBranchStack,
			branchLenStackEntry = branchLenStack[branchLenStack.length-1];
			links = currChain.links,

		links.splice(branchLenStackEntry.numLinks);
		currChain.matchType = branchLenStackEntry.matchType;
	}
	
	/**
	*	Called when an activation is fired
	*	@param {String} ruleName the name of the rule that generated the activation
	*	@param {Object} factHash the variable bindings of the activation
	*	@param {String} activationId identifier for the activation
	*/
	function _onFire(ruleName, factHash, activationId)
	{
		log("fire", "Firing activation: "+activationId);
		currChain.links.push({ruleName: ruleName, hints: []});
		iAmMovingForward = true;
	}
	
	/**
	*	Called when an activation is added to the agenda
	*	@param {String} id identifier for the activation
	*	@param {boolean} isNew whether or not the activation was generated by the last fire
	*	@param {boolean} skipped true if the activation was not added to the agenda
	*/
	function _onAgendaInsert(id, isNew, skipped) {
		log("agenda_insert", id+" added to agenda, is "+(isNew ? " " : "not ")+"new, was "+(skipped ? " " : "not ")+"skipped");
	}
	
	/**
	*	Called when an activation is removed from the agenda
	*	@param {String} id identifier for the activation
	*/
	function _onAgendaRetract(id) {
		log("agenda_retract", id+" removed from agenda");
	}
	
	/**
	*	Called when no more activations in the agenda and no more states to backtrack to (end of the line)
	*/
	function _onAgendaEmpty() {
		log("agenda_empty", "No more activations in agenda, all branches have been explored");
		if (iAmMovingForward) {
			_storeChain();
		}
	}
}