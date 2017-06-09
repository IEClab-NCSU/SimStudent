/* This object parses the graph passed to it by CTATET. */

goog.provide('CTATGraphParser');
goog.require('CTATBase');
goog.require('CTATMessage');
goog.require('CTATVariableTable');
goog.require('CTATMsgType');
goog.require('CTATMatcher');
goog.require('CTATAnyMatcher');
goog.require('CTATExactMatcher');
goog.require('CTATExpressionMatcher');
goog.require('CTATRangeMatcher');
goog.require('CTATRegexMatcher');
goog.require('CTATWildcardMatcher');
goog.require('CTATVectorMatcher');
goog.require('CTATExampleTracerNode');
goog.require('CTATExampleTracerLink');
goog.require('CTATExampleTracerGraph');
goog.require('CTATExampleTracerSkill');

/*
 * Last Modified: Dhruv Chand, 6th August, 2014
 */

CTATGraphParser = function()
{
    CTATBase.call(this, "CTATGraphParser", "__undefined__");

	var that = this;
    var exampleTracerTracer;
    var graph;
    var parser = new CTATXML();
	var stateGraphMsg = null;
	var startStateMsgs = [];
    var serializer = new XMLSerializer();
    var errorSAI = null;
    var groupModel;
    var caseInsensitive = true;  // default
	var lockWidget = true;
	var graphSkills = [];

	this.getLockWidget = function()
	{
		return lockWidget;
	};

    this.parseGraph =  function(stateGraph,tracer)
    {
        //useDebugging = true;
        ctatdebug("parseBRD()");
        var nodeCount = 0;
		var studentStartStateNodeName = parser.getElementAttr(stateGraph, "startStateNodeName");

        //Get State Graph attributes
        var isUnordered = (parser.getElementAttr(stateGraph, "unordered") === "true");
        caseInsensitive = ((String(parser.getElementAttr(stateGraph, "caseInsensitive"))).toLowerCase() != "false");
        lockWidget = parser.getElementAttr(stateGraph, "lockWidget");

        var vt = new CTATVariableTable();
        graph = new CTATExampleTracerGraph(isUnordered, false, vt);
        var rootChildren = parser.getElementChildren(stateGraph);

		var feedbackPolicy = parser.getElementAttr(stateGraph, "suppressStudentFeedback");
		if(feedbackPolicy === null || feedbackPolicy === undefined) feedbackPolicy = CTATMsgType.SHOW_ALL_FEEDBACK;
        graph.setFeedbackPolicy(feedbackPolicy);
		tracer.setFeedbackSuppressed(feedbackPolicy); // set true/false

        var highlightRightSelection = parser.getElementAttr(stateGraph, "highlightRightSelection");
		tracer.setHighlightRightSelection(highlightRightSelection != "false");  // default is true

        var tracerConfirmDone = parser.getElementAttr(stateGraph, "confirmDone");
        if (tracerConfirmDone !== "true" && tracerConfirmDone !== "false")
        {
        	if (graph.getFeedbackPolicy() === CTATMsgType.HIDE_ALL_FEEDBACK)
        		// default true for suppress feedback, false for delay feedback, quizzes, and tutors
        		tracerConfirmDone = "true";
        	else
        		tracerConfirmDone = "false";
        }

        //Set hint biasing, etc.
        tracer.setHintPolicy(parser.getElementAttr(stateGraph, "hintPolicy"));
		tracer.setOutOfOrderMessage(parser.getElementAttr(stateGraph, "outOfOrderMessage"));

		var edgesGroupsElt = null;
        for (var index = 0; index < rootChildren.length; index++)
        {
            switch(parser.getElementName(rootChildren[index]))
            {
			case "startNodeMessages":
				startStateMsgs = parser.getElementChildren(rootChildren[index]);
				break;

			case "node":
                nodeCount++;
                var node = processNode(rootChildren[index]);
                graph.addNode(node);
                if (nodeCount == 1)
				{
                    graph.setStartNode(node);
					graph.setStudentStartsHereNode(node);  // initialize to origin node, maybe change later
				}
				if(node.getNodeName() == studentStartStateNodeName)
					graph.setStudentStartsHereNode(node);
				break;

			case "edge":
                graph.addLink(processEdge(rootChildren[index]));
				break;

			case "EdgesGroups":
				edgesGroupsElt = rootChildren[index];   // ensure all edges read before groups
				break;

			case "productionRule":
				processSkill(rootChildren[index]);
				break;
			}
        }
        if(edgesGroupsElt)
		{
			var isOrdered = parser.getElementAttr(edgesGroupsElt,"ordered");
			if(isOrdered === null || typeof(isordered) == "undefined")
            {
                isOrdered = !isUnordered; //@Fran this line of code can cause a bunch of trouble if the values are not boolean!!
            }

            ctatdebug("parseGraph() value of isOrdered = " + isOrdered);
            ctatdebug("parseGraph() value of isUnordered = " + isUnordered);
            ctatdebug("parseGraph() typeof isOrdered: " + (typeof(isOrdered)));
            ctatdebug("parseGraph()typeof isUnordered: " + (typeof(isUnordered)));
			//processGroup(edgesGroupsElt, (isOrdered === "true")); //@Fran this line is wrong because isOrdered is a boolean, not a string
            processGroup(edgesGroupsElt, isOrdered);
		}
		var skillBarVector = tracer.getSkillBarVector(false, true);
        if (!skillBarVector || skillBarVector.length < 1)
        {
        	ctatdebug("No existing skills. Add skills from BRD.");
        	tracer.addGraphSkills(graphSkills);
			skillBarVector = tracer.getSkillBarVector();
        }
		//Construct StateGraphMessage
		var msgBuilder = new CTATTutorMessageBuilder();
		stateGraphMsg = msgBuilder.createStateGraphMessage(caseInsensitive, isUnordered, lockWidget, (tracer.isSourceFlash() ? graph.exitOnIncorrectDone() : tracer.isFeedbackSuppressed()), highlightRightSelection, tracerConfirmDone, skillBarVector);

        graph.redoLinkDepths();
        exampleTracerTracer = graph.getExampleTracer();
        exampleTracerTracer.resetTracer();
        graph.forDebugging();
        ctatdebug("Graph loaded.");
        return {gr: graph, tracer: exampleTracerTracer, ssm: startStateMsgs, sgMsg: stateGraphMsg};
    };


    function processSkill(element)
    {
        ctatdebug("processSkill()");
    	var name = "", category = "";
    	var label = null, description = null;
        var elements = parser.getElementChildren(element);
        for (var index = 0; index < elements.length; index++)
        {
			var eltName = parser.getElementName(elements[index]);
			if (eltName == "ruleName")
			{
				name = parser.getNodeTextValue(elements[index]);
				ctatdebug("ruleName="+name);
			}
			if (eltName == "productionSet")
			{
				category = parser.getNodeTextValue(elements[index]);
				ctatdebug("productionSet="+category);
			}
			if (eltName == "label")
			{
				label = parser.getNodeTextValue(elements[index]);
				ctatdebug("label="+label);
			}
			if (eltName == "description")
			{
				description = parser.getNodeTextValue(elements[index]);
				ctatdebug("description="+description);
			}
        }
		if(!name)
		{
			return;
		}
		var skill = new CTATExampleTracerSkill(category, name, CTATExampleTracerSkill.DEFAULT_P_GUESS,
				CTATExampleTracerSkill.DEFAULT_P_KNOWN, CTATExampleTracerSkill.DEFAULT_P_SLIP,
				CTATExampleTracerSkill.DEFAULT_P_LEARN, CTATExampleTracerSkill.DEFAULT_HISTORY);
		if (label != null)
		{
			skill.setLabel(label);
		}
		else
		{
			skill.setLabel(name);
		}
		if (description != null)
		{
			skill.setDescription(description);
		}
		else
		{
			skill.setDescription(name + (category ? " "+category : "") );
		}
		graphSkills.push(skill);
    }

    function processEdge(edgeElement)
    {
        ctatdebug("processEdge()");
        var edge = new CTATExampleTracerLink(null, null, null);
        var children = parser.getElementChildren(edgeElement);
        var SelectionMatchers, ActionMatchers, InputMatchers, saiMessage, uniqueID, sourceID=-1, destID=-1;
		var actor = CTATMsgType.DEFAULT_ACTOR;
		var linkTriggered = false;

        for (var index = 0; index < children.length; index++)
        {
            if (parser.getElementName(children[index]) === "sourceID")
            {
                sourceID = parseInt(parser.getNodeTextValue(children[index]));
                edge.setPrevNode(sourceID);
            }
            if (parser.getElementName(children[index]) === "destID")
            {
				destID = parseInt(parser.getNodeTextValue(children[index]));
                edge.setNextNode(destID);
            }
            if (parser.getElementName(children[index]) === "rule")
            {
				var ruleChildren = parser.getElementChildren(children[index]);
				for(var rcIndex = 0; rcIndex<ruleChildren.length; rcIndex++)
				{
					if (parser.getElementName(ruleChildren[rcIndex]) === "text")
					{
                                               edge.addSkillName(parser.getNodeTextValue(ruleChildren[rcIndex]).toString());
					}
				}
            }
            if (parser.getElementName(children[index]) === "actionLabel")
            {
                edge.setIsPreferredLink(parser.getElementAttr(children[index], "preferPathMark") != "false");

                edge.setMinTraversalsStr(parser.getElementAttr(children[index], "minTraversals"));
                edge.setMaxTraversalsStr(parser.getElementAttr(children[index], "maxTraversals"));

                var actionLabelChildren = parser.getElementChildren(children[index]);

                for (var jndex = 0; jndex < actionLabelChildren.length; jndex++)
                {
                    if (parser.getElementName(actionLabelChildren[jndex]) === "uniqueID")
                    {
                        uniqueID = parser.getNodeTextValue(actionLabelChildren[jndex]);
                        edge.setUniqueID(parseInt(parser.getNodeTextValue(actionLabelChildren[jndex])));
                    }
                    if (parser.getElementName(actionLabelChildren[jndex]) === "message")
                    {
                        saiMessage = new CTATMessage(actionLabelChildren[jndex]);
                    }
                    if (parser.getElementName(actionLabelChildren[jndex]) === "actionType")
                    {
                        edge.setActionType(parser.getNodeTextValue(actionLabelChildren[jndex]));
                    }
                    if (parser.getElementName(actionLabelChildren[jndex]) === "buggyMessage")
                    {
                        edge.setBuggyMsg(parser.getNodeTextValue(actionLabelChildren[jndex]));
                    }
                    if (parser.getElementName(actionLabelChildren[jndex]) === "successMessage")
                    {
                        edge.setSuccessMsg(parser.getNodeTextValue(actionLabelChildren[jndex]));
                    }
                    if (parser.getElementName(actionLabelChildren[jndex]) === "hintMessage")
                    {
                        edge.addHint(parser.getNodeTextValue(actionLabelChildren[jndex]));
                    }

                    //To parse new style 'matchers' tags
                    if (parser.getElementName(actionLabelChildren[jndex]) === "matchers")
                    {

                        var memberMatchers = parser.getElementChildren(actionLabelChildren[jndex]);
                        for (var kndex = 0; kndex < memberMatchers.length; kndex++)
                        {
                            if (parser.getElementName(memberMatchers[kndex]) === "Selection")
                            {
                                SelectionMatchers = processMatchers(memberMatchers[kndex], 0);
                            }

                            if (parser.getElementName(memberMatchers[kndex]) === "Action")
                            {
                                ActionMatchers = processMatchers(memberMatchers[kndex], 1);
                            }

                            if (parser.getElementName(memberMatchers[kndex]) === "Input")
                            {
                                InputMatchers = processMatchers(memberMatchers[kndex], 2);
                            }

                            if (parser.getElementName(memberMatchers[kndex]) === "Actor")
                            {
								actor = parser.getNodeTextValue(memberMatchers[kndex]);
                                linkTriggered = parser.getElementAttr(memberMatchers[kndex], "linkTriggered");
								if(!linkTriggered || linkTriggered.toLowerCase().trim() == "false")
									linkTriggered = false;
								else
									linkTriggered = true;
                            }
                        }
                    }

                    //To parse old style 'matcher' tags
                    if (parser.getElementName(actionLabelChildren[jndex]) === "matcher")
                    {
                        var legacyMatchers = processMatcher(actionLabelChildren[jndex], saiMessage);
                        SelectionMatchers = legacyMatchers[0];
                        ActionMatchers = legacyMatchers[1];
                        InputMatchers = legacyMatchers[2];
                        actor = legacyMatchers[3];  // not actually a matcher
                    }
                }
            }
        }
		if(graph.getStartNode() && destID == graph.getStartNode().getNodeID())
			console.log("***WARNING*** edge "+uniqueID+" destination is graph startNode "+graph.getStartNode());

        //Create a vector matcher here.
        var vectorMatcher = new CTATVectorMatcher(SelectionMatchers, ActionMatchers, InputMatchers, actor);
		vectorMatcher.setDefaultSAI(saiMessage.getSAI());
		vectorMatcher.setCaseInsensitive(caseInsensitive);
		vectorMatcher.setLinkTriggered(linkTriggered);
        edge.setMatcher(vectorMatcher);

		ctatdebug("GraphParser.processEdge() "+sourceID+"->"+destID+", edge "+edge);
        graph.getNode(sourceID).addOutLink(edge);

		graph.checkForDoneState(edge);

        return edge;
    }


    function processMatchers(element, vector)
    {
        that.ctatdebug("processMatchers()");
        var matcherXML = parser.getElementChildren(element);
        var matchers = [];
        for (var index = 0; index < matcherXML.length; index++)
        {
            if (parser.getElementName(matcherXML[index]) === "matcher")
            {
                var matcher = null;
				var replacementFormula = parser.getElementAttr(matcherXML[index], "replacementFormula");

                var matcherData = parser.getElementChildren(matcherXML[index]);
                for (var j = 0; j < matcherData.length; j++)
                {
                    if (parser.getElementName(matcherData[j]) === "matcherType")
                    {
						var matcherType = parser.getNodeTextValue(matcherData[j]);
						that.ctatdebug("CTATGraphParser.processMatchers() matcherType["+j+"] = "+matcherType);
						switch(matcherType)
						{
							case "ExactMatcher":
								matcher = new CTATExactMatcher(vector, null);
								break;
							case "RegexMatcher":
								matcher = new CTATRegexMatcher(vector, null);
								break;
							case "AnyMatcher":
								matcher = new CTATAnyMatcher(vector, null);
								break;
							case "RangeMatcher":
								matcher = new CTATRangeMatcher(vector, null);
								break;
							case "WildcardMatcher":
								matcher = new CTATWildcardMatcher(vector, null);
								break;
							case "ExpressionMatcher":
								matcher = new CTATExpressionMatcher(vector, null);
								break;
							default:
								console.log("CTATGraphParser.processMatchers() unknown matcherType["+j+"] = "+matcherType+"; using CTATExactMatcher");
								matcher = new CTATExactMatcher(vector, null);
						}
                    }
                    if (parser.getElementName(matcherData[j]) === "matcherParameter")
                    {
                        var paramName = parser.getElementAttr(matcherData[j], "name");
                        if (paramName === "single")
                            matcher.setParameter(parser.getNodeTextValue(matcherData[j]));
                        else if (paramName === "minimum")
                            matcher.setParameter(parser.getNodeTextValue(matcherData[j]), "minimum");
                        else if (paramName === "maximum")
                            matcher.setParameter(parser.getNodeTextValue(matcherData[j]), "maximum");
                        else
                            matcher.setParameter(matcherData[j], paramName, parser);
                    }
                }
				if(matcher)
				{
					matcher.setReplacementFormula(replacementFormula);
				}
                matchers.push(matcher);
            }
        }
        return matchers;
    }

    /**
     * This function parses old style matchers, enclosed in 'matcher' tags
     * @param {object<Element>} element matcher element
     * @param {object<MessageObject>} saiMessage message that demonstrated this link; optional
     */
    function processMatcher(element, saiMessage)
    {
        var replacementFormula = parser.getElementAttr(element, "replacementFormula");
        var matcherXML = parser.getElementChildren(element);
        var selectionMatcher, actionMatcher, inputMatcher, matcher, paramName, paramText;
        var actor = CTATMsgType.DEFAULT_ACTOR;

        selectionMatcher = new CTATExactMatcher(CTATMatcher.SELECTION, null);
        actionMatcher = new CTATExactMatcher(CTATMatcher.ACTION, null);

        that.ctatdebug("processMatcher(): nChildren "+matcherXML.length+", saiMessage "+saiMessage);
        for (var index = 0; index < matcherXML.length; index++)
        {
            if (parser.getElementName(matcherXML[index]) === "matcherType")
            {
                var mType = parser.getNodeTextValue(matcherXML[index]);
                if (mType === "ExactMatcher")
                {
                    matcher = CTATExactMatcher;
                }
                if (mType === "RegexMatcher")
                {
                    matcher = CTATRegexMatcher;
                }
                if (mType === "AnyMatcher")
                {
                    matcher =  CTATAnyMatcher;
                }
                if (mType === "RangeMatcher")
                {
                    matcher = CTATRangeMatcher;
                }
                if (mType === "WildcardMatcher")
                {
                    matcher = CTATWildcardMatcher;
                }
                if (mType === "ExpressionMatcher")
                {
                    matcher = CTATExpressionMatcher;
                }
                that.ctatdebug("processMatcher(): child "+index+", eltText "+mType+", matcher "+matcher);
            }

            if (parser.getElementName(matcherXML[index]) === "matcherParameter")
            {
                paramName = parser.getElementAttr(matcherXML[index], "name");
                paramText = parser.getNodeTextValue(matcherXML[index]);

                if(paramName == "selection")
                {
                   selectionMatcher.setParameter(paramText);
                }
                if(paramName == "action")
                {
                    actionMatcher.setParameter(paramText);
                }
                if(paramName == "input")
                {
                    inputMatcher = (inputMatcher ? inputMatcher : new matcher(CTATMatcher.INPUT, null));
                    inputMatcher.setParameter(paramText);
                }
                if(paramName == "actor")
                {
                    actor = paramText;
                }
                if(paramName == "minimum")  // for RangeMatcher
                {
                    inputMatcher = (inputMatcher ? inputMatcher : new matcher(CTATMatcher.INPUT, null));
                    inputMatcher.setParameter(paramText, "minimum");
                }
                if(paramName == "maximum")  // for RangeMatcher
                {
                    inputMatcher = (inputMatcher ? inputMatcher : new matcher(CTATMatcher.INPUT, null));
                    inputMatcher.setParameter(paramText, "maximum");
                }
                if(paramName == "InputExpression")  // for ExpressionMatcher
                {
                    inputMatcher = (inputMatcher ? inputMatcher : new matcher(CTATMatcher.INPUT, null));
                    inputMatcher.setParameter(matcherXML[index], paramName, parser);
                }
                if(paramName == "relation")         // for ExpressionMatcher
                {
                    inputMatcher = (inputMatcher ? inputMatcher : new matcher(CTATMatcher.INPUT, null));
                    inputMatcher.setParameter(matcherXML[index], paramName, parser);
                }
                that.ctatdebug("processMatcher(): child "+index+", eltAttrName "+paramName+", inputMatcher "+inputMatcher);
            }
        }
        if(!inputMatcher)  // can occur with old-style AnyMatcher
        {
            inputMatcher = (matcher ? new matcher(CTATMatcher.INPUT, null) : new CTATExactMatcher(CTATMatcher.INPUT, null));
            inputMatcher.setParameter(paramText ? paramText : "");
	    if((typeof(saiMessage) != "undefined") && (typeof(saiMessage.getSAI) == "function"))
	    {
		var sai=saiMessage.getSAI();
                selectionMatcher.setSingle(sai.getSelection());
                actionMatcher.setSingle(sai.getAction());
                inputMatcher.setSingle(sai.getInput());
	    }
        }
        inputMatcher.setReplacementFormula(replacementFormula);
        return [[selectionMatcher],[actionMatcher],[inputMatcher], actor];
    }

    function processNode(element)
    {
        ctatdebug("processNode(" + element + ")");
        var children = parser.getElementChildren(element);
        var nodeID = -1, nodeName = "";
        for (var index = 0; index < children.length; index++)
        {
			var childName = parser.getElementName(children[index]);
            switch(childName)
            {
				case "uniqueID":
	                nodeID = parseInt(parser.getNodeTextValue(children[index]));
					break;
				case "text":
	                nodeName = parser.getNodeTextValue(children[index]);
					break;
            }
        }
        var node = new CTATExampleTracerNode(nodeID, new Set());
		if(nodeName !== "")
		{
			node.setNodeName(nodeName);
		}
		return node;
    }

    function processGroup(groupTag, isOrdered)
    {
        ctatdebug("processGroup()");
        groupModel = graph.getGroupModel();

        /* For legacy BRD files
         if(parser.getElementAttr(element,"unordered") == null)
         {
         var groupElements = parser.getElementChildren(element);
         for(var i = 0; i<groupElements.length;i++)
         {
         ///legacyReadFromXML
         }
         return;
         }
         */

        var idToEdgeMap = groupModel.createIdToLinkMap();
        groupModel.setGroupOrdered(groupModel.getTopLevelGroup(), isOrdered);//

        ctatdebug("processGroup() name of group: " + groupModel.getGroupName(groupModel.getTopLevelGroup()));
        ctatdebug("processGroup() group isOrdered?: " + groupModel.isGroupOrdered(groupModel.getTopLevelGroup()));
        ctatdebug("processGroup() order of group: isOrdered= " + isOrdered);
        ctatdebug("processGroup() order of group: isOrdered typeof= " + typeof(isOrdered));

        var groupElements = parser.getElementChildren(groupTag);

        for (var i = 0; i < groupElements.length; i++)
        {
			var childTagName = parser.getElementName(groupElements[i]);
            switch(childTagName)
			{
				case "group":
	                readGroupFromXML(groupElements[i], idToEdgeMap);
					break;
				case "outOfOrderMessage":
					groupModel.getTopLevelGroup().setDefaultBuggyMsg(parser.getNodeTextValue(groupElements[i]));
					break;
			}
        }

    }

    function readGroupFromXML(element, idToEdgeMap)
    {
        var links = new Set();
        if (parser.getElementName(element) !== "group")
        {
            return links;
        }

        var name = parser.getElementAttr(element, "name");
        ctatdebug("readGroupFromXML group name: " + name);

        // if(!isGroupNameValid(name))return;
        var isOrdered = (parser.getElementAttr(element, "ordered") === "true");
		var isReenterable = (parser.getElementAttr(element, "reenterable") === "true");

        ctatdebug("readGroupFromXML group isOrdered typeof: " + typeof(isOrdered));
        ctatdebug("readGroupFromXML group isOrdered: " + isOrdered);

		var addLinkToSet = function(linkInGroup) {
			links.add(linkInGroup);
		};

        var subElements = parser.getElementChildren(element);

        for (var i = 0; i < subElements.length; i++)
        {
            if (parser.getElementName(subElements[i]) === "link")
            {
                //console.log(subElements[i]);
                var edgeID = Number(parser.getElementAttr(subElements[i], "id"));
                var link = idToEdgeMap[edgeID];
                if (link !== null || link !== undefined)
                {
                    ctatdebug("Added link ID : " + edgeID);
                    links.add(link);
                }
            }
            if (parser.getElementName(subElements[i]) === "group")
            {
                ctatdebug("encountered a subgroup");
                var setOfLinks = readGroupFromXML(subElements[i], idToEdgeMap);
				setOfLinks.forEach(addLinkToSet);
            }
        }
        groupModel.addGroup(name, isOrdered, links);
        groupModel.setGroupReenterable(groupModel.getGroupByName(name), isReenterable);
        return links;
    }
};

CTATGraphParser.prototype = Object.create(CTATBase.prototype);
CTATGraphParser.prototype.constructor = CTATGraphParser;

if(typeof module !== 'undefined')
{
    module.exports = CTATGraphParser;
}
