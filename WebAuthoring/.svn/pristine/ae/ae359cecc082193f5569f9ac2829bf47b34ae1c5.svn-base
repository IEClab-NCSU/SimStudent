/* This object represents an CTATExampleTracerGraph */

goog.provide('CTATExampleTracerGraph');
goog.require('CTATBase');
goog.require('CTATMsgType');
goog.require('CTATExampleTracerLink');
goog.require('CTATExampleTracerPath');
goog.require('CTATDefaultGroupModel');
goog.require('CTATLinkGroup');
goog.require('CTATExampleTracerTracer');

//goog.require('CTATExampleTracerNode');//
//goog.require('CTATGroupModel');//
//goog.require('CTATExampleTracerInterpretation');//
//goog.require('CTATExampleTracerGraphVisualData');//


/* LastModify: sewall 2014/10/31 */

/**
 * @param {boolean} isUnordered true if the top-level group should be unordered, false if ordered
 * @param {boolean} youStartYouFinish
 */
CTATExampleTracerGraph = function(isUnordered, youStartYouFinish, givenVT)
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

    //calling the constructor of the super class
    CTATBase.call(this, "CTATExampleTracerGraph", "visualdata"); //Martin's code

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

    /** 
     * The tracer of the current graph.
     * @type {CTATExampleTracerTracer}
     */
    var exampleTracerTracer = null;

    /** 
     * Container for the nodes in the graph.
     * @type {array of CTATExampleTracerNode}
     */
    var nodes = null;

    /** 
     * Container for the links in the graph.
     * @type {array of CTATExampleTracerLink}
     */
    var links = null;

    /** 
     * Map of <nodeID, node> for the nodes currently in the graph.
     * @type {map of integer and CTATExampleTracerNode}
     */
    var nodeMap = null;

    /** 
     * The start state node read from the brd file.
     * @type {CTATExampleTracerNode}
     */
    var startNode = null;

    /** 
     * Martin's code.
     * @type {CTATExampleTracerGraphVisualData}
     */
    var visuals = null; 

    /** 
     * @type {CTATGroupModel}
     */
    var groupModel = null;

    /** 
     * Whether to display feedback to the student.
     * True means that hints and correct/incorrect indications should
     * not be passed to the student interface.
     * @type {CTATMsgType}
     */
    var suppressStudentFeedback = CTATMsgType.DEFAULT; 

    /**
     * Make the object available to private methods
     */
    var that = this;

    /***************************** PRIVATE METHODS *****************************************************/

    /** 
     * Constructor workhorse. Create all fixed data structures.
     * @param {boolean} isOrdered true if the top-level group should be ordered, false if unordered
     * @param {boolean} youStartYouFinish
     * @return {undefined}
     */
    function initGraph(isOrdered, youStartYouFinish)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in initGraph ("+isOrdered+ " , " + youStartYouFinish);

        links = []; //array of CTATExampleTracerLink
        nodes = []; //array of CTATExampleTracerNode
        nodeMap = {}; //map of integer to CTATExampleTracerNode

        if (groupModel === null || typeof (groupModel) === 'undefined')
        {
            groupModel = new CTATDefaultGroupModel();
        }

        groupModel.clear();
        groupModel.setDefaultReenterable(!youStartYouFinish);
        groupModel.setGroupOrdered(groupModel.getTopLevelGroup(), isOrdered);
        groupModel.setGroupName(groupModel.getTopLevelGroup(), CTATExampleTracerGraph.TOP_LEVEL);

        //MAJOR IMPORTANCE: USE "that" BECAUSE WE ARE IN A PRIVATE FUNCTION
        //givenVt is coming from the main constructor parameters
        exampleTracerTracer = new CTATExampleTracerTracer(that, givenVT);

        //we have decided to remove this method
        /*if(exampleTracerTracer !== null && typeof(exampleTracerTracer) !== 'undefined')
         {
         exampleTracerTracer.fireExampleTracerEvent(new CTATExampleTracerTracerChangedEvent(that, exampleTracerTracer, newTracer));
         }*/

        that.ctatdebug("CTATExampleTracerGraph --> out of initGraph");
    }

    /**
     * @param {CTATExampleTracerLink} visitedLinks
     * @param {CTATExampleTracerLink} newLink
     * @param {CTATExampleTracerLink} path
     * @return {boolean}
     */
    function isOrderOK(visitedLinks, newLink, path)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in isOrderOK");

        var groups = findGroupsOfLink(newLink); //returns array of CTATLinkGroup

        for (var i = 0; i < groups.length; i++)
        {
            var currentGroup = groups[i];

            if (groupModel.isGroupOrdered(currentGroup) === true)
            {
                if (i !== (groups.length - 1))
                {
                    if (checkOrderedGroup(currentGroup, getFirstLinkOnPath(groups[i + 1], path), visitedLinks, path) === false)
                    {
                        return false;
                    }
                }
                else if (checkOrderedGroup(currentGroup, newLink, visitedLinks, path) === false)
                {
                    return false;
                }
            }
        }

        that.ctatdebug("CTATExampleTracerGraph --> out of isOrderOK");

        return true;
    }

    /**
     * @param {CTATExampleTracerLink} traversedLinks
     * @param {CTATExampleTracerLink} newLink
     * @param {CTATExampleTracerLink} path
     * @return {boolean}
     */
    function isReenteringOK(traversedLinks, newLink, path)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in isReenteringOK");

        var groups = findGroupsOfLink(newLink); //array of CTATLinkGroup

        var entered = true;

        for (var i = 0; i < groups.length; i++)
        {
            if (entered === true && groupModel.isGroupReenterable(groups[i]) === false)
            {
                entered = false;

                for (var j = 0; j < traversedLinks.length; j++)
                {
                    if (groupModel.isLinkInGroup(groups[i], traversedLinks[j]) === true)
                    {
                        entered = true;
                    }
                    else if (entered === true)
                    {
                        return false;
                    }
                }
            }
            else if (entered === false)
            {
                break;
            }
        }

        if (traversedLinks.length !== 0)
        {
            var exitedGroups = findGroupsOfLink(traversedLinks[traversedLinks.length - 1]);

            that.ctatdebug("CTATExampleTracerGraph --> in isReentrableOK: exitedGroups: " + (exitedGroups === null));
            that.ctatdebug("CTATExampleTracerGraph --> in isReentrableOK: exitedGroups: " + (typeof(exitedGroups) === 'undefined'));
            that.ctatdebug("CTATExampleTracerGraph --> in isReentrableOK: exitedGroups: " + traversedLinks[traversedLinks.length - 1].getUniqueID());
            console.log(exitedGroups);

            groups.forEach(function(group)
            {
                //exitedGroups.delete(group);

                exitedGroups.splice(exitedGroups.indexOf(group), 1);
            });

            /*for(k = 0; k < exitedGroups.length; k++)
             {
             if(groupModel.isGroupReenterable(exitedGroups[k]) === true)
             {
             exitedGroups.splice(groups[k], 1);//removes the current element from the array
             }
             }*/

            exitedGroups.forEach(function(group)
            {
                if (groupModel.isGroupReenterable(group) === true)
                {
                    //exitedGroups.delete(group);
                    exitedGroups.splice(exitedGroups.indexOf(group), 1);
                }
            });

            var retVal = true;

            exitedGroups.forEach(function(group)
            {
                if (retVal === false)
                {
                    return;
                }

                path.forEach(function(link)
                {
                    if (retVal === false)
                    {
                        return;
                    }

                    if (getTraversalCount(traversedLinks, link) < link.getMinTraversals() && groupModel.isLinkInGroup(group, link) === true)
                    {
                        retVal = false;
                        return;
                    }
                });

            });

            if (retVal === false)
            {
                return false;
            }
        }

        that.ctatdebug("CTATExampleTracerGraph --> out of isReenteringOK");

        return true;
    }

    /**
     * Returns all the groups containing the given link in the nested order.
     * @param {CTATExampleTracerLink} link
     * @return {array of CTATLinkGroup}
     */
    function findGroupsOfLink(link)
    {
        that.ctatdebug("in findGroupsOfLink(link " + link + "): typeof(groupModel) " + typeof(groupModel));

        var groupsSet = groupModel.getGroupsContainingLink(link);

        that.ctatdebug("in findGroupsOfLink(link " + link + "): groupsSet.size " + groupModel);

        var groups = []; //array of CTATLinkGroup

        groupsSet.forEach(function(el)
        {
            groups.push(el);
        });

        groups.sort(function(arg0, arg1)
        {
            return (groupModel.getGroupLinkCount(arg1) - groupModel.getGroupLinkCount(arg0));
        });

        return groups;
    }

    /**
     * @param {CTATLinkGroup} orderedParent
     * @param {CTATExampleTracerLink} link
     * @param {array of CTATExampleTracerLink} traversedLink
     * @param {array of CTATExampleTracerLink} path
     * @return {boolean}
     */
    function checkOrderedGroup(orderedParent, link, traversedLinks, path)
    {
        var prevPos = -1; //of type integer
        var prev = null; //of type object
        var nextPos = Number.MAX_VALUE; //of type integer
        var next = null; //of type object

        groupModel.getGroupSubgroups(orderedParent).forEach(function(group)
        {
            if (groupModel.isLinkInGroup(group, link) === false && groupModel.getGroupLinkCount(group) !== 0)
            {
                var temp = getFirstLinkOnPath(group, path);

                if (temp !== null && typeof (temp) !== 'undefined')
                {
                    var groupPos = temp.getDepth();

                    if (groupPos < link.getDepth() && groupPos > prevPos && (isGroupOptional(group) === false || isGroupStarted(group, traversedLinks) === true))
                    {
                        prevPos = groupPos;
                        prev = group;
                    }

                    if (groupPos > link.getDepth() && groupPos < nextPos && (isGroupOptional(group) === false || isGroupStarted(group, traversedLinks) === true))
                    {
                        nextPos = groupPos;
                        next = group;
                    }
                }
            }
        });

        //only those on path

        groupModel.getUniqueLinks(orderedParent).forEach(function(parentLink)
        {
            if (path.has(parentLink) === true)
            {
                var linkPos = parentLink.getDepth(); //of type integer
                if (linkPos < link.getDepth() && linkPos > prevPos && (parentLink.getMinTraversals() > 0 || traversedLinks.indexOf(parentLink) > -1))
                {
                    prevPos = linkPos;
                    prev = parentLink;
                }

                if (linkPos > link.getDepth() && linkPos < nextPos && (parentLink.getMinTraversals() > 0 || traversedLinks.indexOf(parentLink) > -1))
                {
                    nextPos = linkPos;
                    next = parentLink;
                }
            }
        });

        if (prev !== null && typeof (prev) !== 'undefined')
        {
            if (prev instanceof CTATExampleTracerLink)
            {
                if (getTraversalCount(traversedLinks, prev) < prev.getMinTraversals())
                {
                    return false;
                }
            }
            else if (prev instanceof CTATLinkGroup)
            {
                if (isGroupFinished(prev, traversedLinks, path) === false)
                {
                    return false;
                }
            }
        }

        if (next !== null && typeof (next) !== 'undefined')
        {
            if (next instanceof CTATExampleTracerLink && traversedLinks.indexOf(next) > -1)
            {
                return false;
            }
            else if (next instanceof CTATLinkGroup)
            {
                if (isGroupStarted(next, traversedLinks) === true)
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @param {CTATLinkGroup} group
     * @param {set of CTATExampleTracerLink} path
     * @return {CTATExampleTracerLink}
     */
    function getFirstLinkOnPath(group, path)
    {
        var minDepth = Number.MAX_VALUE;

        var firstLink = null; //of type CTATExampleTracerLink

        groupModel.getGroupLinks(group).forEach(function(link)
        {
            if (link.getDepth() < minDepth && path.has(link) === true)
            {
                minDepth = link.getDepth();
                firstLink = link;
            }
        });

        return firstLink;
    }

    /**
     * @param {array of CTATExampleTracerLink} traversedLink
     * @param {CTATExampleTracerLink} target
     * @return {integer}
     */
    function getTraversalCount(traversedLinks, target)
    {
        var traversals = 0; //of type integer

        for (var i = 0; i < traversedLinks.length; i++)
        {
            if (target === traversedLinks[i])
            {
                traversals++;
            }
        }

        return traversals;
    }

    /**
     * Nonsensical function written to satisfy test-cases :)
     * It tests for the edge case where:
     * Given: the path-sibling of the inc-link belongs to the same 
     * unordered group as the inc-link's smallest group.
     * Returns True if:
     * All the links along the path to the group have been maxed out
     * and all the links belonging in the group and the path have been maxed out.
     * Returns false if:
     * any of the links on the path leading up to the group
     * or in the group have not been maxed out.
     * Note: assumes that a group is contiguous on a path.
     * Note: Doesn't take into account whether the inclink is actually reachable
     * Basically we only test, if the path/group is maxed, regardless of whether
     * we traversed the links needed to reach incLink.
     * @param {CTATLinkGroup} smallestGroup
     * @param {Set of CTATExampleTracerLink} path
     * @param {CTATExampleTracerInterpretation} interp 
     * @return {boolean}
     */
    function isGroupMaxedOnPath(smallestGroup, path, interp)
    {
        var orderedPath = new CTATExampleTracerPath(path); //of type CTATExampleTracerPath
        var inGroup = false; //of type boolean

        var retVal = true;
        var retVal2 = false;

        orderedPath.getSortedLinks().forEach(function(path_link)
        {
            if (retVal === false)
            {
                return;
            }

            if (retVal2 === true)
            {
                return;
            }

            if (inGroup === false)
            {
                if (groupModel.isLinkInGroup(smallestGroup, path_link) === true)
                {
                    inGroup = true;
                }
            }
            else
            {
                if (groupModel.isLinkInGroup(smallestGroup, path_link) === false)
                {
                    retVal2 = true;
                    return;
                }
            }

            if (interp.getTraversalCount(path_link) < path_link.getMaxTraversals())
            {
                retVal = false;
                return;
            }
        });

        if (retVal === false)
        {
            return false;
        }

        if (retVal2 === true)
        {
            return true;
        }

        return true;
    }

    /**
     * @param {CTATLinkGroup} group 
     * @return {boolean}
     */
    function isGroupOptional(group)
    {
        var retVal = true;

        groupModel.getGroupLinks(group).forEach(function(link)
        {
            if (retVal === false)
            {
                return;
            }

            if (link.getMinTraversals() !== 0)
            {
                retVal = false;
                return;
            }
        });

        if (retVal === false)
        {
            return false;
        }

        return true;
    }

    /**
     * @param {CTATLinkGroup} group
     * @param {array of CTATExampleTracerLink} traversedLinks
     * @return {boolean}
     */
    function isGroupStarted(group, traversedLinks)
    {
        for (var i = 0; i < traversedLinks.length; i++)
        {
            if (groupModel.isLinkInGroup(group, traversedLinks[i]) === true)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @param {CTATLinkGroup} group
     * @param {array of CTATExampleTracerLink} traversedLinks 
     * @param {set of CTATExampleTracerLink} path
     * @return {boolean}
     */
    function isGroupFinished(group, traversedLinks, path)
    {
        var returnVal = true;

        groupModel.getGroupLinks(group).forEach(function(link)
        {
            if (returnVal === false)
            {
                return;
            }

            if (path.has(link) === true)
            {
                if (getTraversalCount(traversedLinks, link) < link.getMinTraversals())
                {
                    returnVal = false;
                    return;
                }
            }
        });

        if (returnVal === false)
        {
            return false;
        }

        return true;
    }

    /**
     * Clear all CTATExampleTracerNode inLinks and recalculate them.
     * @return {undefined}
     */
    function buildInLinks()
    {
        for (var i = 0; i < nodes.length; i++)
        {
            nodes[i].clearInLinks();
        }

        var startNode = that.getStartNode();

        if (startNode === null || typeof (startNode) === 'undefined' || startNode.getOutLinks() === null || typeof (startNode.getOutLinks()) === 'undefined')
        {
            return;
        }

        i = 0;
        startNode.getOutLinks().forEach(function(el)
        {
            updateInLinkSubGraph(el, i++);
        });
    }

    /**
     * Set the CTATExampleTracerNode inLinks in the subgraph starting
     * at the given link's destination node.
     * @param {CTATExampleTracerLink} link 
     * @return {undefined}
     */
    function updateInLinkSubGraph(link, idx)
    {
        ctatdebug("updateInLinkSubGraph(" + link + ", " + idx + ")");

        var dest = that.getNode(link.getNextNode());

        if (dest === null || typeof (dest) === 'undefined')
        {
            return;
        }

        dest.addInLink(link);

        var i = 0;

        dest.getOutLinks().forEach(function(el){
            updateInLinkSubGraph(el, i++);
        });
    }

    /**
     * @param {CTATExampleTracerLink} link
     * @return {undefined}
     */
    function _redoLinkDepths(link)
    {
        var prevNode = that.getNode(link.getPrevNode());

        if (prevNode === null || typeof (prevNode) === 'undefined')
        {
            return;
        }

        var max = -1;

        prevNode.getInLinks().forEach(function(prevLink)
        {
            if (prevLink.getDepth() === -1)
            {
                _redoLinkDepths(prevLink);
            }

            if (prevLink.getDepth() > max)
            {
                max = prevLink.getDepth();
            }
        });

        link.setDepth(max + 1);
    }


    /***************************** PRIVILEDGED METHODS *****************************************************/

    /**
     * @return {array of CTATExampleTracerLink} returns an array containing the links of the graph
     */
    this.getLinks = function()
    {
        that.ctatdebug("CTATExampleTracerGraph --> in getLinks");

        return links;
    };

    /**
     * Adds the given link to the graph
     * @param {CTATExampleTracerLink} link
     * @param {CTATLinkGroup} groupToAddTo
     * @return {undefined}
     */
    this.addLink = function(link, groupToAddTo)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in addLink");

        links.push(link);

        if (groupToAddTo === null || typeof (groupToAddTo) === 'undefined')
        {
            groupModel.addLinkToGroup(groupModel.getTopLevelGroup(), link);
        }
        else
        {
            groupModel.addLinkToGroup(groupToAddTo, link);
        }

        that.ctatdebug("CTATExampleTracerGraph --> out of addLink");
    };

    /**
     * Adds the node to the graph
     * @param {CTATExampleTracerNode} node
     * @return {undefined}
     */
    this.addNode = function(node)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in addNode");

        nodes.push(node);
        nodeMap[node.getNodeID()] = node;

        that.ctatdebug("CTATExampleTracerGraph --> out of addNode");
    };


    /**
     * @return {set of CTATExampleTracerPath} returns a list containing all the paths in the given graph
     */
    this.findAllPaths = function()
    {
        ctatdebug("CTATExampleTracerGraph --> in findAllPaths");
        ctatdebug("CTATExampleTracerGraph --> in findAllPaths " + that.getStartNode().getNodeID());

        return that.findPathsFromNode(that.getStartNode());
    };

    /** 
     * Checks whether the extension observes the ordering constraints or not
     * @param {array of CTATExampleTracerLink} traversedLinks links already matched
     * @param {CTATExampleTracerLink} newLink candidate link for checking
     * @param {set of CTATExampleTracerLink} path check ordering with respect to this path
     * @param {CTATxampleTracerEvent} result
     * @return {boolean} returns false or true depending on whether it observes 
     * ordering Constraints or not 
     */
    this.observesOrderingConstraints = function(traversedLinks, newLink, path, result)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in observesOrderingConstraints");

        if (path === null || typeof (path) === 'undefined' || path.size === 0 || newLink === null || typeof (newLink) === 'undefined')
        {
            that.ctatdebug("CTATExampleTracerGraph --> in observesOrderingConstraints first if");

            if (result !== null && typeof (result) !== 'undefined')
            {
                that.ctatdebug("CTATExampleTracerGraph --> in observesOrderingConstraints setting out of order");
                result.setOutOfOrder(false);
            }

            that.ctatdebug("CTATExampleTracerGraph --> returning first true observesOrderingConstraints");
            return true;
        }

        if ((path.has(newLink) === true) && (isOrderOK(traversedLinks, newLink, path) === true) && (isReenteringOK(traversedLinks, newLink, path) === true))
        {
            that.ctatdebug("CTATExampleTracerGraph --> in observesOrderingConstraints second if");

            if (result !== null && typeof (result) !== 'undefined')
            {
                that.ctatdebug("CTATExampleTracerGraph --> in observesOrderingConstraints setting out of order");
                result.setOutOfOrder(false);
            }

            that.ctatdebug("CTATExampleTracerGraph --> returning second true observesOrderingConstraints");
            return true;
        }

        if (result !== null && typeof (result) !== 'undefined')
        {
            result.setOutOfOrder(true);
        }

        that.ctatdebug("CTATExampleTracerGraph --> out of observesOrderingConstraints");

        return false;
    };

    /**
     * Tries to find a traversable sibling on the path, ignoring max traversals.
     * If none, return false.
     * @param {array of CTATExampleTracerLink} traversedLinks
     * @param {CTATExampleTracerLink} incLink the incorrect action link to check
     * @param {set of CTATExampleTracerLink} path path in which to find a correct action link
     * @param {CTATExampleTracerInterpretation} interp 
     * @return {boolean} true if finds a sibling (see above)
     */
    this.isIncorrectLinkOK = function(traversedLinks, incLink, path, interp)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in isIncorrectLinkOK");

        var siblingOnPath = false; //of type boolean

        var sibLink = null; //of type CTATExampleTracerLink

        var outLinks = that.getNode(incLink.getPrevNode()).getOutLinks();

        var returnValue = true;
        var isBreak = false;

        outLinks.forEach(function(link)
        {
            if (isBreak === true)
            {
                return;
            }

            if (returnValue === false)
            {
                return;
            }

            if (link.getType().toString() === CTATExampleTracerLink.BUGGY_ACTION.toString())
            {
                return; //similar to continue in java
            }

            if (path.has(link) === true)
            {
                siblingOnPath = true;

                var siblingOrderOk = isOrderOK(traversedLinks, link, path);

                if (siblingOrderOk === false)
                {
                    returnValue = false; //we cannot break forEach loops
                    return;
                }

                sibLink = link;

                isBreak = true; //we cannot break forEach loops
                return;
            }
        });

        //we should have returned in the forEach but that is not possible, so we return right after if we need to 
        if (returnValue === false)
        {
            return false;
        }

        if (sibLink === null || typeof (sibLink) === 'undefined')//it is fine if we just check for null, already set above
        {
            return false;
        }

        if (siblingOnPath === false)
        {
            that.ctatdebug("isIncorrectLinkOK(): no siblings of link in path");
            return false;
        }

        //Leave groups out for now

        //Testing whether the path/group to which incLink would belong to is maxed or not:
        //not-maxed meaning that there is either a member of the group that isn't maxed along 
        //the path or that an ancestor along the path to the group isn't maxed.
        if (siblingOnPath === true)
        {
            var incSmallestGroup = that.getSmallestContainingGroup(incLink);

            if ((groupModel.isLinkInGroup(incSmallestGroup, sibLink) === true) && (groupModel.isGroupOrdered(incSmallestGroup) === false))
            {
                if (isGroupMaxedOnPath(incSmallestGroup, path, interp) === true)
                {
                    return false;
                }
            }
        }

        that.ctatdebug("CTATExampleTracerGraph --> out of isIncorrectLinkOK");

        return true;
    };

    /**
     * Find a node in nodeMap given its identifier.
     * @param {integer} nodeID
     * @return {CTATExampleTracerNode} instance from map; null if not found
     */
    this.getNode = function(nodeID)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in getNode(" + nodeID + ") returning " + nodeMap[nodeID]);
        return nodeMap[nodeID];
    };

    /**
     * Finds the paths starting from a given node
     * @param {CTATExampleTracerNode} node
     * @return {set of CTATExampleTracerPath}
     */
    this.findPathsFromNode = function(node)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in findPathsFromNode outlinks " + node.getOutLinks().size);

        var paths = new Set();

        //if no node, then create an empty path
        if (node === null || typeof (node) === 'undefined' || node.getOutLinks().size === 0)
        {
            //even though the constructor of CTATExampleTracerPath takes one parameter
            //if we call it with no parameters, the parameter will be set to undefined
            //this case is covered by the constructor function of the CTATExampleTracerPath
            paths.add(new CTATExampleTracerPath(null));
            return paths;
        }

        that.ctatdebug("CTATExampleTracerGraph --> before forEach ");

        //for each of the outliks from the current node
        node.getOutLinks().forEach(function(outLink)
        {
            that.ctatdebug("CTATExampleTracerGraph --> in forEach " + outLink.getType().toString());
            that.ctatdebug("CTATExampleTracerGraph --> in forEach " + CTATExampleTracerLink.BUGGY_ACTION.toString());
        
            //if it is not an INCORRECT_ACTION link type
            if (outLink.getType().toString() !== CTATExampleTracerLink.BUGGY_ACTION.toString())
            {
                //go to the next node of the current outlink
                var childPaths = that.findPathsFromNode(that.getNode(outLink.getNextNode()));
                that.ctatdebug("findPathsFromNode childPaths.size " + childPaths.size);

                childPaths.forEach(function(childPath)
                {
                    that.ctatdebug("findPathsFromNode childPath.size " + childPath.size + ", outLink " + outLink);
                    childPath.addLink(outLink);
                    //in JS we cannot use addAll to add the elements of a set to another set
                    //not to create another iteration, we add it right here
                    paths.add(childPath);
                });
            }
        }, node);

        //if no paths create an empty one
        if (paths.size === 0)
        {
            paths.add(new CTATExampleTracerPath(null));
        }

        that.ctatdebug("CTATExampleTracerGraph --> out of findPathsFromNode " + paths.size);
        return paths;
    };

    /**
     * Method to get the start node of the graph; the node represents the start node in the brd.
     * We do not rely on the fact that the node might have an ID of 1.
     * Instead we keep a variable of CTATExampleTracerNode type and we save the start state in
     * this variable when we start reading the brd file and creating the graph.
     * @return {CTATExampleTracerNode} the start node of the current graph
     */
    this.getStartNode = function()
    {
        that.ctatdebug("CTATExampleTracerGraph --> in getStartNode() returning " + startNode);
        return startNode;
    };


    /**
     * Return the link object for a given link id.
     * @param {integer} linkID
     * @return {CTATExampleTracerLink} link object from links, null if none matches 
     *
     */
    this.getLinkByID = function(linkID)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in getLinkByID");

        for (var i = 0; i < links.length; i++)
        {
            //this is how we get the uniqueID for each link
            if (links[i].getUniqueID() === linkID)
            {
                return links[i];
            }
        }

        //I though using a normal for loop is easier than a forEach for other coders to read
        /*var foundLink = null;
         links.forEach(function(link)
         {
         if(foundLink !== null && typeof(foundLink) !== 'undefined')
         {
         return; //we cannot break out of a forEach so we return and save the value
         }
         
         if(link.getUniqueID() === linkID)
         {	
         foundLink = link;
         }
         });
         return foundLink;*/

        that.ctatdebug("CTATExampleTracerGraph --> out of getLinkByID");

        return null;
    };

    /**
     * Method created to set the start node of the graph.
     * We do not rely on the ID of the start node being 1,
     * but we set it from the brd directly, when the graph
     * is initially created.
     * @param {CTATExampleTracerNode} node
     */
    this.setStartNode = function(node)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in setStartNode");
        startNode = node;
        that.ctatdebug("CTATExampleTracerGraph --> out of setStartNode");
    };

    /**
     * Getter for the tracer of the current graph
     * @return {CTATExampleTracerTracer}
     */
    this.getExampleTracer = function()
    {
        that.ctatdebug("CTATExampleTracerGraph --> in getExampleTracer() returning " + exampleTracerTracer);
        return exampleTracerTracer;
    };

    /**
     * Method used to print and debug information from the graph
     * while we are creating it from the brd file.
     * @return {undefined}
     */
    this.forDebugging = function()
    {
        that.ctatdebug("CTATExampleTracerGraph --> in forDebugging");

        that.ctatdebug("Nodes in the graph -- ids");

        //print all the nodes (their IDs) of the graph
        nodes.forEach(function(i)
        {
            that.ctatdebug(i.getNodeID());
        });

        that.ctatdebug("Links in the graph -- ids, from, to");

        //print all the links of the graph including: linkID, prevNode ID and nextNode ID
        links.forEach(function(link)
        {
            that.ctatdebug(link);
        });
    };

    /**
     * @param {CTATExampleTracerLink} link
     * @return {CTATLinkGroup}
     */
    this.getSmallestContainingGroup = function(link)
    {
        return groupModel.getLowestLevelGroupOfLink(link);
    };

    /**
     * A link's depth is defined as follows.  A link's depth is
     * equal to the maximum of all preceding link's depths plus
     * 1.  A given link's preceding links are the set of links whose
     * next node is equal to the given link's previous node.  If
     * a link has no preciding links, it's depth is 0. 
     * @return {undefined}
     */
    this.redoLinkDepths = function()
    {
        buildInLinks();

        that.getLinks().forEach(function(link)
        {
            link.setDepth(-1);
        });

        that.getLinks().forEach(function(link)
        {
            if (link.getDepth() === -1)
            {
                _redoLinkDepths(link);
            }
        });
    };

    /**
     * @return {CTATGroupModel}
     */
    this.getGroupModel = function()
    {
        return groupModel;
    };


    /******************************MARTIN'S METHODS ****************************************************/

    /**
     *
     */
    this.setVisualData = function(aData)
    {
        that.ctatdebug("CTATExampleTracerGraph --> in setVisualData");
        visuals = aData;
    };

    /**
     *
     */
    this.getVisualData = function()
    {
        that.ctatdebug("CTATExampleTracerGraph --> in getVisualData() returning " + visuals);
        return (visuals);
    };


/****************************** METHOD CALLS FROM CONSTRUCTOR ****************************************************/

    //method that starts constructing the graph
    initGraph(!isUnordered, youStartYouFinish);
};


/**************************** CONSTANTS ******************************************************/

    /**
     * @param {string} TOP_LEVEL
     */
    Object.defineProperty(CTATExampleTracerGraph, "TOP_LEVEL", {enumerable: false, configurable: false, writable: false, value: "Top Level"});


/**************************** SETTING UP INHERITANCE ******************************************************/

CTATExampleTracerGraph.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerGraph.prototype.constructor = CTATExampleTracerGraph;

if (typeof module !== 'undefined')
{
    module.exports = CTATExampleTracerGraph;
}