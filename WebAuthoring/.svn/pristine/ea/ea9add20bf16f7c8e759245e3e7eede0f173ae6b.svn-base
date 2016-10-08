/* This object parses the graph passed to it by CTATET. */

goog.provide('CTATGraphParser');
goog.require('CTATBase');
goog.require('CTATVariableTable');
goog.require('CTATMsgType');
goog.require('CTATAnyMatcher');
goog.require('CTATExactMatcher');
goog.require('CTATExpressionMatcher');
goog.require('CTATRangeMatcher');
goog.require('CTATRegexMatcher');
goog.require('CTATVectorMatcher');
goog.require('CTATExampleTracerNode');
goog.require('CTATExampleTracerLink');
goog.require('CTATExampleTracerGraph');

/* 
 * Last Modified: Dhruv Chand, 6th August, 2014
 */

CTATGraphParser = function()
{
    CTATBase.call(this, "CTATGraphParser", "__undefined__");  

    var exampleTracerTracer;
    var graph;
    var parser = new CTATXML();
    var serializer = new XMLSerializer();
    var errorSAI = null;
    var groupModel;
	var suppressStudentFeedback = CTATMsgType.SHOW_ALL_FEEDBACK;

	this.getSuppressStudentFeedback = function()
	{
		return suppressStudentFeedback;
	};

    this.parseGraph =  function(stateGraph,tracer)
    {
        //useDebugging = true;
        ctatdebug("parseBRD()");
        var nodeNo = 0;
        var vt = new CTATVariableTable();
        graph = new CTATExampleTracerGraph(true, false, vt);
        var rootChildren = parser.getElementChildren(stateGraph);
        
        //Get State Graph attributes
        var isUnordered = parser.getElementAttr(stateGraph, "unordered");
        var caseInsensitive = parser.getElementAttr(stateGraph, "caseInsensitive");
        var lockWidget = parser.getElementAttr(stateGraph, "lockWidget");
        suppressStudentFeedback = parser.getElementAttr(stateGraph, "suppressStudentFeedback");
		if(suppressStudentFeedback === null || suppressStudentFeedback === undefined) suppressStudentFeedback = CTATMsgType.SHOW_ALL_FEEDBACK;
        var highlightRightSelection = parser.getElementAttr(stateGraph, "highlightRightSelection");
        var confirmDone = parser.getElementAttr(stateGraph, "confirmDone");
        

        //Set hint biasing, etc.
        tracer.setHintPolicy(parser.getElementAttr(stateGraph, "hintPolicy"));
		tracer.setHighlightRightSelection(highlightRightSelection != "false");  // default is true
        
        for (var index = 0; index < rootChildren.length; index++)
        {
            if (parser.getElementName(rootChildren[index]) === "startNodeMessages")
            {
                
                var startStateMessages = parser.getElementChildren(rootChildren[index]);
                var serializer = new XMLSerializer();
                for(var m = 0 ; m < startStateMessages.length; m++){
                    if (parser.getElementName(startStateMessages[m]) === "message")
                         startStateMessages[m] = serializer.serializeToString(startStateMessages[m]);
                }
                                   
                //Construct StateGraphMessage
				var msgBuilder = new CTATTutorMessageBuilder();
                if(confirmDone !== "true" && confirmDone !== "false" && suppressStudentFeedback === CTATMsgType.HIDE_ALL_FEEDBACK)
                {
                    confirmDone = "true";  // default true for quizzes, false for tutors
                }
                var stateGraphMsg = msgBuilder.createStateGraphMessage(caseInsensitive,isUnordered,lockWidget,(suppressStudentFeedback === CTATMsgType.HIDE_ALL_FEEDBACK),highlightRightSelection,confirmDone,tracer.getSkillBarVector());
                var nMsgs = startStateMessages.unshift(stateGraphMsg);

                //Insert SendWidgetLock just before last msg
                startStateMessages.splice(nMsgs - 1, 0, msgBuilder.createLockWidgetMsg(lockWidget));

                tracer.sendBundle(startStateMessages);
            }
            if (parser.getElementName(rootChildren[index]) === "node")
            {
                nodeNo++;
                var node = processNode(rootChildren[index]);
                graph.addNode(node);

                if (nodeNo == 1)
                {
                    dbg1 = node;
                    graph.setStartNode(node);
                }
            }
            if (parser.getElementName(rootChildren[index]) === "edge")
            {
                graph.addLink(processEdge(rootChildren[index]));
            }
            if (parser.getElementName(rootChildren[index]) === "EdgesGroups")
            {
                processGroup(rootChildren[index], isUnordered);
            }
        }
        
        graph.redoLinkDepths();
        exampleTracerTracer = graph.getExampleTracer();
        exampleTracerTracer.resetTracer();
        graph.forDebugging();
        ctatdebug("Graph loaded.");
        return [graph,exampleTracerTracer];
    };


    function processEdge(edgeElement)
    {
        ctatdebug("processEdge()");
        var edge = new CTATExampleTracerLink(null, null, null);
        var children = parser.getElementChildren(edgeElement);
        var SelectionMatchers, ActionMatchers, InputMatchers, saiMessage, uniqueID, sourceID;

        for (var index = 0; index < children.length; index++)
        {
            if (parser.getElementName(children[index]) === "sourceID")
            {
                sourceID = parseInt(parser.getNodeTextValue(children[index]));
                edge.setPrevNode(sourceID);
            }
            if (parser.getElementName(children[index]) === "destID")
            {
                edge.setNextNode(parseInt(parser.getNodeTextValue(children[index])));
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
                    if (parser.getElementName(actionLabelChildren[jndex]) === "buggyMessage")
                    {
                        edge.setBuggyMsg(parser.getNodeTextValue(actionLabelChildren[jndex]));
                    }
                    if (parser.getElementName(actionLabelChildren[jndex]) === "successMessage")
                    {
                        edge.setSuccessMsg(parser.getNodeTextValue(actionLabelChildren[jndex]));
                    }
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

                        }

                    }

                }

            }
        }
        //Create a vector matcher here and set default sai.
        var vectorMatcher = new CTATVectorMatcher(SelectionMatchers, ActionMatchers, InputMatchers);
        ctatdebug("Default SAI = > " +saiMessage);
        vectorMatcher.setDefaultSelection(saiMessage.getSelection());
        vectorMatcher.setDefaultAction(saiMessage.getAction());
        vectorMatcher.setDefaultInput(saiMessage.getInput());
        ctatdebug("Default SAI = > " + vectorMatcher.getDefaultSAI());
        edge.setMatcher(vectorMatcher);
		ctatdebug("GraphParser.processEdge() sourceID "+sourceID+", src node "+graph.getNode(sourceID)+", edge "+edge);
        graph.getNode(sourceID).addOutLink(edge);

        return edge;
    }


    function processMatchers(element, vector)
    {
        ctatdebug("processMatchers()");
        var matcherXML = parser.getElementChildren(element);
        var matchers = [];
        for (var index = 0; index < matcherXML.length; index++)
        {
            if (parser.getElementName(matcherXML[index]) === "matcher")
            {
                var matcher;

                var matcherData = parser.getElementChildren(matcherXML[index]);
                for (var j = 0; j < matcherData.length; j++)
                {
                    if (parser.getElementName(matcherData[j]) === "matcherType")
                    {
                        if (parser.getNodeTextValue(matcherData[j]) === "ExactMatcher")
                        {
                            matcher = new CTATExactMatcher(vector, null);
                        }

                        if (parser.getNodeTextValue(matcherData[j]) === "RegexMatcher")
                        {
                            matcher = new CTATRegexMatcher(vector, null);
                        }
                        if (parser.getNodeTextValue(matcherData[j]) === "AnyMatcher")
                        {
                            matcher = new CTATAnyMatcher(vector, null);
                        }
                        if (parser.getNodeTextValue(matcherData[j]) === "RangeMatcher")
                        {
                            matcher = new CTATRangeMatcher(vector, null);
                        }

                        if (parser.getNodeTextValue(matcherData[j]) === "WildcardMatcher")
                        {
                            matcher = new CTATAnyMatcher(vector, null);
                        }
                        if (parser.getNodeTextValue(matcherData[j]) === "ExpressionMatcher")
                        {
                            matcher = new CTATExpressionMatcher(vector, null);
                        }

                    }
                    if (parser.getElementName(matcherData[j]) === "matcherParameter")
                    {
                        var paramName = parser.getElementAttr(matcherData[j], "name");

                        if (paramName === "single")
                        {
                            matcher.setSingle(parser.getNodeTextValue(matcherData[j]));
                        }

                        if (paramName === "min")
                        {
                            matcher.setMin(parser.getNodeTextValue(matcherData[j]));
                        }

                        if (paramName === "max")
                        {
                            matcher.setMax(parser.getNodeTextValue(matcherData[j]));
                        }
                    }
                }
                matchers.push(matcher);
            }
        }
        return matchers;
    }

    function processNode(element)
    {
        ctatdebug("processNode(" + element + ")");
        var children = parser.getElementChildren(element);
        var nodeID;
        for (var index = 0; index < children.length; index++)
        {
            if (parser.getElementName(children[index]) === "uniqueID")
            {
                nodeID = parser.getNodeTextValue(children[index]);
            }
        }
        return new CTATExampleTracerNode(nodeID, new Set());
    }

    function processGroup(groupTag, isUnordered)
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
        groupModel.setGroupOrdered(groupModel.getTopLevelGroup(), isUnordered);//
        var groupElements = parser.getElementChildren(groupTag);

        for (var i = 0; i < groupElements.length; i++)
        {
            if (parser.getElementName(groupElements[i]) == "group")
                readGroupFromXML(groupElements[i], idToEdgeMap);
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
        // if(!isGroupNameValid(name))return;   
        var isOrdered = !Boolean(parser.getElementAttr(element, "unordered"));
        //var isReenterable = parser.getElementAttr(element,"");   
        var subElements = parser.getElementChildren(element);

        for (var i = 0; i < subElements.length; i++)
        {
            if (parser.getElementName(subElements[i]) === "link")
            {
                console.log(subElements[i]);
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
                setOfLinks.forEach(function(link)
                {
                    links.add(link);
                });
            }
        }
        groupModel.addGroup(name, isOrdered, links);
        groupModel.setGroupReenterable(groupModel.getGroupByName(name), true);
        return links;
    }
};

CTATGraphParser.prototype = Object.create(CTATBase.prototype);
CTATGraphParser.prototype.constructor = CTATGraphParser;

if(typeof module !== 'undefined')
{
    module.exports = CTATGraphParser;
}
