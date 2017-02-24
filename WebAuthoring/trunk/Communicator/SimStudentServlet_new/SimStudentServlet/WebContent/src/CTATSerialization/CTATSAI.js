/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATSerialization/CTATSAI.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 http://stackoverflow.com/questions/6486307/default-argument-values-in-javascript-functions

 */
goog.provide('CTATSAI');

goog.require('CTATArgument');
goog.require('CTATBase');
goog.require('CTATHTMLManager');
goog.require('CTATStringUtil');
goog.require('CTATXML');
/**
 *    This is a relatively new class for CTAT and only exists in AS3 and HTML5. It's meant to
 *    become the base unit of transaction for methods related to SAI's, instead of passing
 *    around 3 Strings to methods you can pass a single SAI object and extract relevant data
 *    from it. Also, every component will have an instance of SAI that can be used as a way of
 *    recording state as well as sending new actions.
 *
 *    There is currently no support in the hierarchy for individual elements of the SAI to have
 *    the type and id values used in the dataShop specs for the selection, action, and input
 *    fields of evet_descriptors.
 *
 *    see: http://pslcdatashop.web.cmu.edu/dtd/guide/tool_message.html#element.event_descriptor
 */
CTATSAI = function(aSelection,anAction,anInput,aPrompt)
{
    CTATBase.call(this, "CTATSAI","sai");

    // we use the instance name as the selection

    var action="undefined";
    var prompt="undefined";
    var inputFlattened="";
    var tools=new CTATStringUtil ();
    var internalArguments=new Array ();
    var addedSelections = new Array ();
    var addedActions = new Array ();

    var messageParser;
	if (CTATConfig.parserType=="xml")
	{
		messageParser=new CTATXML ();
	}
	else
	{
		messageParser=new CTATJSON ();
	}

    /**
     *
     */
    this.getArguments=function getArguments ()
    {
        return (internalArguments);
    };
    /**
     * Processes the sai_arguments so that an array of the sai_arguments cast into
     * the appropriate type is returned.
     * @return an array of the sai_arguments cast into their appropriate type.
     *
     */
    this.getArgumentsTyped=function getArgumentsTyped()
    {
        var i=0;
        var sai_arguments = new Array();

        for (i=0;i<internalArguments.length;i++)
        {
                var arg=internalArguments [i];

                if (arg.getType()==="Boolean")
                {
                    this.ctatdebug ("Adding Boolean argument ("+arg.getValue()+") ...");

                    sai_arguments.push(tools.String2Boolean(arg.getValue()));
                }
                else if (arg.getType() === "Number")
                {
                    this.ctatdebug ("Adding Number argument ("+arg.getValue()+") ...");
                    sai_arguments.push(new Number(arg.getValue()));
                }
                else if (arg.getType() === "String")
                {
                    if (arg.getValue()==="No_Value")
                    {
                        this.ctatdebug ("Detected default argument ("+arg.getValue()+"), setting contents to null instead");

                        sai_arguments.push(null);
                    }
                    else
                    {
                        sai_arguments.push(new String(arg.getValue()));
                    }
                }
                else
                {
                    this.ctatdebug ("Unrecognized argument type: "+arg.getType()+" in "+this.toSerializedString()+" IGNORING IT!!!");
                }
        }

        return sai_arguments;
    };
    /**
     *
     */
    this.getArgument=function getArgument (anIndex)
    {
        var arg=internalArguments [anIndex];

        return (arg);
    };
    /**
     * Append a selection to addedSelections[].
     * @param s new selection to add
     */
    this.addSelection = function addSelection (s)
    {
        addedSelections.push(s);
    };
    /**
     * Append an action to addedActions[].
     * @param a new action to add
     */
    this.addAction = function addAction (a)
    {
        addedActions.push(a);
    };
    /**
     *
     */
    this.checkDefaultArgument=function checkDefaultArgument ()
    {
        this.ctatdebug ("checkDefaultArgument ()");

        if (internalArguments.length===0)
        {
            this.ctatdebug ("Adding default argument ...");

            var defaultArgument=new CTATArgument ();
            internalArguments.push (defaultArgument);
        }
    };
    /**
     *
     */
    this.setArgument=function setArgument (anIndex,aValue)
    {
        this.checkDefaultArgument ();

        var tempArgument=internalArguments [anIndex];

        tempArgument.value=aValue;

        return (tempArgument);
    };
    /**
     *
     */
    this.addArgument=function addArgument (aValue,aType,aFormat)
    {
        //this.ctatdebug ("addArgument ()");

        var tempArgument=new CTATArgument ();
        tempArgument.value=aValue;
        tempArgument.type=aType;
        tempArgument.format=aFormat;
        internalArguments.push(tempArgument);

        return (tempArgument);
    };
    /**
     *
     */
    this.addExistingArgument=function addExistingArgument (anArgument)
    {
        //this.ctatdebug ("addExistingArgument ()");

        internalArguments.push(anArgument);

        return (anArgument);
    };
    /**
    *    Changes the current selection, action, and input values
    */
    this.setSAI=function setSAI (newSelection,
                                 newAction,
                                 newInput,
                                 aType,
                                 aPrompt)
    {
        this.setName(newSelection);
        action=newAction;
        prompt=aPrompt;

        this.setInput(newInput);
        this.setType(aType);
    };
    /**
    *    Changes the current input value
    */
    this.setInput=function setInput(newInput)
    {
        this.ctatdebug("setInput("+newInput+")");

        this.checkDefaultArgument ();

        var arg=this.getArgument (0);

        var parser=new CTATHTMLManager ();

        arg.setValue(parser.htmlDecode (newInput));
    };
    /**
    *    Returns the primary selection value
    */
    this.getInput=function getInput()
    {
        this.ctatdebug ("getInput()");

        if (internalArguments.length===0)
		{
            return ("");
		}

        var arg=this.getArgument (0);

        return arg.getValue();
    };
    /**
    *
    */
    this.setType=function setType(aType)
    {
        this.ctatdebug("setType()");

        this.checkDefaultArgument ();

        var arg=this.getArgument (0);
        arg.type=aType;
    };
    /**
     *    Returns the primary selection value
     */
    this.getType=function getType()
    {
        this.ctatdebug("getType()");

        if (internalArguments.length===0)
		{
            return ("");
		}

        var arg=this.getArgument (0);
        return arg.type;
    };
    /**
     *
     */
    this.setFormat=function setFormat(aFormat)
    {
        this.ctatdebug("setFormat()");

        this.checkDefaultArgument ();

        var arg=this.getArgument (0);
        arg.setFormat (aFormat);
    };
    /**
     *    Returns the primary selection value
     */
    this.getFormat=function getFormat()
    {
        this.ctatdebug("getFormat()");

        if (internalArguments.length===0)
		{
            return ("");
		}

        var arg=this.getArgument (0);

        return arg.getFormat ();
    };
    /**
    *    Changes the current input value
    */
    this.setSelection=function setSelection(newInput)
    {
        this.ctatdebug("setSelection("+newInput+")");

        this.setName(newInput);
    };
    /**
    *    Returns the primary selection value
    */
    this.getSelection=function getSelection()
    {
        this.ctatdebug("getSelection()");

        return (this.getName());
    };
    /**
    *
    */
    this.setAction=function setAction(newInput)
    {
        this.ctatdebug("setAction("+newInput+")");

        action = newInput;
    };
    /**
    *    Returns the primary action value
    */
    this.getAction=function getAction()
    {
        this.ctatdebug("getAction()");

        return action;
    };
    /**
    *
    */
    this.setPrompt=function setPrompt(newInput)
    {
        this.ctatdebug("setPrompt("+newInput+")");

        prompt = newInput;
    };
    /**
    *    Returns the primary action value
    */
    this.getPrompt=function getPrompt()
    {
        this.ctatdebug("getPrompt()");

        return prompt;
    };
    /**
     *
     */
    this.propagate=function propagate (source)
    {
        this.ctatdebug ("propagate ()");

        var sourceArguments=source.getArguments ();

        for (var i=0;i<sourceArguments.length;i++)
        {
            var fromArg=sourceArguments [i];
            var toArg=internalArguments [i];

            if ((fromArg===null) || (toArg===null))
            {
                this.ctatdebug ("Internal error: argument lists do not align between received SAI and source SAI");
                return;
            }

            toArg.setValue(fromArg.getValue ());
        }
    };
    /**
    *
    */
    this.fromString=function fromString (aStream)
    {
        //var rootNode=messageParser.parseXML (aStream);

        //this.fromXML (rootNode);

		var messageRoot=messageParser.parse (aStream);

		this.fromXML (messageRoot);
    };
    /**
    * Provided to the parse method is a single node, which can have any node name.
    * The code below will then search in that node for the selection, action, input
    * fields, etc.
    */
    this.fromXML=function fromXML (aNode)
    {
        this.ctatdebug ("fromXML ()");

        internalArguments=new Array ();

        // var entries=aNode.childNodes;
		var entries=messageParser.getElementChildren (aNode);

        inputFlattened="";

        for (var t=0;t<entries.length;t++)
        {
            var entry=entries [t];

            //>-----------------------------------------------------------------------------

            //if ((entry.nodeName==="selection") || (entry.nodeName==="Selection"))
			if ((messageParser.getElementName (entry)=="selection") || (messageParser.getElementName (entry)=="Selection"))
            {
                //this.ctatdebug ("Parsing selection");

                //var vals=entry.childNodes;
				var vals=messageParser.getElementChildren (entry);

                var nameMatched=false;

                for (var i=0;i<vals.length;i++)
                {
                    var val=vals [i];

                    //if (val.nodeName==="value")
					if (messageParser.getElementName (val)=="value")
                    {
                        //this.ctatdebug ("Parsing value: " + messageParser.getNodeTextValue (val));

                        nameMatched=true;
                        this.setSelection (messageParser.getNodeTextValue (val));
                    }
                }

                if (nameMatched===false)
                {
                    //this.ctatdebug ("Parsing value: " + messageParser.getNodeTextValue (entry));

                    this.setSelection (messageParser.getNodeTextValue (entry));
                }
            }

            //>-----------------------------------------------------------------------------

            //if ((entry.nodeName==="action") || (entry.nodeName==="Action"))
			if ((messageParser.getElementName (entry)=="action") || (messageParser.getElementName (entry)=="Action"))
            {
                //var acts=entry.childNodes;
				var acts=messageParser.getElementChildren (entry);

                var actionMatched=false;

                for (var j=0;j<acts.length;j++)
                {
                    var act=acts [j];

                    //if (act.nodeName==="value")
					if (messageParser.getElementName (act)=="value")
                    {
                        actionMatched=true;
                        this.setAction (messageParser.getNodeTextValue (act));
                    }
                }

                if (actionMatched===false)
				{
                    this.setAction (messageParser.getNodeTextValue (entry));
				}
            }

            //>-----------------------------------------------------------------------------

            //if ((entry.nodeName==="internalArguments") || (entry.nodeName==="value") || (entry.nodeName==="Input"))
			if ((messageParser.getElementName (entry)=="internalArguments") || (messageParser.getElementName (entry)=="value") || (messageParser.getElementName (entry)=="Input"))
            {
                //var args=entry.childNodes;
				var args=messageParser.getElementChildren (entry);
                var newValue=null;

                internalArguments=new Array ();

                var formatter=new CTATHTMLManager ();
                var newArgument=new CTATArgument ();
                var ind=0;

                internalArguments.push (newArgument);

                for (var k=0;k<args.length;k++)
                {
                    var argument=args [k];

                    //if (argument.nodeName==="value")
					if (messageParser.getElementName (argument)=="value")
                    {
						var argNodes=messageParser.getElementChildren (argument);

                        if (argNodes !== null)
                        {
                            this.ctatdebug ("Parsing SAI input ...");

                            this.ctatdebug ("Childnodes: " + argNodes.length);

                            if (argNodes.length===1)
                            {
                                newValue=formatter.htmlDecode (messageParser.getNodeTextValue(argument));

                                this.ctatdebug ("Setting new value to: " + newValue);

                                newArgument.setValue (newValue);
                            }
                            else
                            {
                                if (ind>0)
								{
                                    inputFlattened+=",";
								}

                                newValue=formatter.htmlDecode (messageParser.getNodeTextValue(argument));

                                this.ctatdebug ("Setting new value to: " + newValue);

                                newArgument.setValue (newValue);

                                // If available ...

                                newArgument.setName (argument.attributes.getNamedItem("name").value);
                                newArgument.setType (argument.attributes.getNamedItem("type").value);
                                newArgument.setFormat (argument.attributes.getNamedItem("format").value);

                                inputFlattened+=newValue;

                                ind++;
                            }
                        }
                        else
                        {
                            var newVal=formatter.htmlDecode (messageParser.getNodeTextValue(argument));

                            this.ctatdebug ("Setting new value to: " + newVal);

                            newArgument.setValue (newVal);
                        }
                    }
                }
            }

            //>-----------------------------------------------------------------------------

            //if ((entry.nodeName==="prompt") || (entry.nodeName==="Prompt"))
			if ((messageParser.getElementName (entry)=="prompt") || (messageParser.getElementName (entry)=="Prompt"))
            {
                this.ctatdebug ("Parsing prompt ...");

                this.setPrompt (messageParser.getNodeTextValue (entry));
            }

            //>-----------------------------------------------------------------------------
        }

        this.checkDefaultArgument ();
    };
    /**
    * Provided to the parse method is a single node, which can have any node name.
    * The code below will then search in that node for the selection, action, input
    * fields, etc.
    */
    this.fromXMLInternal=function fromXMLInternal (aNode)
    {
        this.ctatdebug ("fromXMLInternal ()");

        var parser=new CTATXML ();

        internalArguments=new Array ();

        //var entries=aNode.childNodes;

		var entries=messageParser.getElementChildren (aNode);

        inputFlattened="";

        for (var t=0;t<entries.length;t++)
        {
            var entry=entries [t];

            //>-----------------------------------------------------------------------------

            //if ((entry.nodeName==="selection") || (entry.nodeName==="Selection"))
			if ((messageParser.getElementName (entry)=="selection") || (messageParser.getElementName (entry)=="Selection"))
            {
                //this.ctatdebug ("Parsing selection");

                //var vals=entry.childNodes;
				var vals=messageParser.getElementChildren (entry);

                var nameMatched=false;

                for (var i=0;i<vals.length;i++)
                {
                    var val=vals [i];

                    //if (val.nodeName==="value")
					if (messageParser.getElementName (val)=="value")
                    {
                        //this.ctatdebug ("Parsing value: " + messageParser.getNodeTextValue (val));

                        nameMatched=true;
                        this.setSelection (messageParser.getNodeTextValue (val));
                    }
                }

                if (nameMatched===false)
                {
                    //this.ctatdebug ("Parsing value: " + messageParser.getNodeTextValue (entry));

                    this.setSelection (messageParser.getNodeTextValue (entry));
                }
            }

            //>-----------------------------------------------------------------------------

            //if ((entry.nodeName==="action") || (entry.nodeName==="Action"))
			if ((messageParser.getElementName (entry)=="action") || (messageParser.getElementName (entry)=="Action"))
            {
                var acts=entry.childNodes;

                var actionMatched=false;

                for (var j=0;j<acts.length;j++)
                {
                    var act=acts [j];

                    //if (act.nodeName==="value")
					if (messageParser.getElementName (act)=="value")
                    {
                        actionMatched=true;
                        this.setAction (messageParser.getNodeTextValue (act));
                    }
                }

                if (actionMatched===false)
				{
                    this.setAction (messageParser.getNodeTextValue (entry));
				}
            }

            //>-----------------------------------------------------------------------------

            //if ((entry.nodeName==="internalArguments") || (entry.nodeName==="value") || (entry.nodeName==="Input"))
			if ((messageParser.getElementName (entry)=="internalArguments") || (messageParser.getElementName (entry)=="Input"))
            {
                var args=entry.childNodes;

                internalArguments=new Array ();

                var formatter=new CTATHTMLManager ();
                var newArgument=new CTATArgument ();
                var ind=0;
				var newValue=null;

                internalArguments.push (newArgument);

                for (var k=0;k<args.length;k++)
                {
                    var argument=args [k];

                    //if (argument.nodeName==="value")
					if (messageParser.getElementName (argument)=="value")
                    {
						var args=messageParser.getElementChildren (argument);

                        if (args !== null)
                        {
                            this.ctatdebug ("Parsing SAI input ...");

                            this.ctatdebug ("Childnodes: " + args.length);

                            if (args.length===1)
                            {
                                newValue=formatter.htmlDecode (messageParser.getNodeTextValue(argument));

                                this.ctatdebug ("Setting new value to: " + newValue);

                                newArgument.setValue (newValue);
                            }
                            else
                            {
                                if (ind>0)
								{
                                    inputFlattened+=",";
								}

                                newValue=formatter.htmlDecode (messageParser.getNodeTextValue(argument));

                                this.ctatdebug ("Setting new value to: " + newValue);

                                newArgument.setValue (newValue);

                                // If available ...

								/*
                                newArgument.setName (argument.attributes.getNamedItem("name").value);
                                newArgument.setType (argument.attributes.getNamedItem("type").value);
                                newArgument.setFormat (argument.attributes.getNamedItem("format").value);
								*/

                                newArgument.setName (messageParser.getElementAttr("name"));
                                newArgument.setType (messageParser.getElementAttr("type"));
                                newArgument.setFormat (messageParser.getElementAttr("format"));


                                inputFlattened+=newValue;

                                ind++;
                            }
                        }
                        else
                        {
                            var newVal=formatter.htmlDecode (messageParser.getNodeTextValue(argument));

                            this.ctatdebug ("Setting new value to: " + newVal);

                            newArgument.setValue (newVal);
                        }
                    }
                }
            }

            //>-----------------------------------------------------------------------------

            //if ((entry.nodeName==="prompt") || (entry.nodeName==="Prompt"))
			if ((messageParser.getElementName (entry)=="prompt") || (messageParser.getElementName (entry)=="Prompt"))
            {
                this.ctatdebug ("Parsing prompt ...");

                this.setPrompt (messageParser.getNodeTextValue (entry));
            }

            //>-----------------------------------------------------------------------------
        }

        this.checkDefaultArgument ();
    };
    /**
    *    Returns the primary SAI in the XML String format used by datashop specs.
    *     NOTE: None of the fmt, name, or type fields should be here it will confuse datashop.
    */
    this.toXMLString=function toXMLString(logMessageFormat)
    {
        if (logMessageFormat)
		{
            return this.toLSxmlString();
		}
        else
		{
            return this.toTSxmlString();
		}
    };
    /**
    *
    */
    this.toLSxmlString=function toLSxmlString()
    {
        var formatter="";

        formatter+="<selection>"+this.getName ();

        // formatter+="<selection>"+this.getSelection();

        for (var i=0;i<addedSelections.length;i++)
        {
            formatter+="</selection><selection>"+addedSelections[i];
        }
        formatter+="</selection><action>"+this.getAction();
        for (var i=0;i<addedActions.length;i++)
        {
            formatter+="</action><action>"+addedActions[i];
        }
        formatter+="</action><input>";
        for (var i=0;i<internalArguments.length;i++)
        {
            var arg=internalArguments [i];
            console.log("SS: CTATSAI.toLSxmlString");
            formatter+="<![CDATA["+arg.getValue()+"]]>";
        }

        formatter+="</input>";

        return (formatter);
    };
    /**
    *    Returns the primary SAI in XML string form, should be good for simple components
    */
    this.toTSxmlString=function toTSxmlString()
    {
        var formatter="";

        formatter+="<Selection><value>"+this.getName ();
        for (var i=0;i<addedSelections.length;i++)
        {
            formatter+="</value><value>"+addedSelections[i];
        }
        formatter+="</value></Selection><Action><value>"+this.getAction ();
        for (var i=0;i<addedActions.length;i++)
        {
            formatter+="</value><value>"+addedActions[i];
        }
        formatter+="</value></Action><Input>";

        if (internalArguments.length>1)
        {
            for (var i=0;i<internalArguments.length;i++)
            {
                var arg=internalArguments [i];
                
                formatter+="<value fmt=\"text\" name=\""+arg.getName ()+"\" type=\""+arg.getType ()+"\"><![CDATA["+arg.getValue()+"]]></value>";
            }
        }
        else
		{
        	//Modified by Shruti - added CDATA
            formatter+="<value><![CDATA["+this.getInput()+"]]></value>";
		}

        formatter+="</Input>";

        return (formatter);
    };
    /**
    *    Returns the primary SAI in XML string form, should be good for simple components
    */
    this.toSerializedString=function toSerializedString()
    {
        var formatter="";

        //formatter+=getClassOpen ()+"<selection>"+this.getName ()+"</selection><action>"+this.getAction()+"</action><internalArguments>";
		formatter+="<selection>"+this.getSelection ()+"</selection><action>"+this.getAction()+"</action><internalArguments>";

        for (var i=0;i<internalArguments.length;i++)
        {
            var arg=internalArguments [i];
            //Modified by Shruti - added CDATA
            formatter+="<value fmt=\"text\" name=\""+arg.getName ()+"\" type=\""+arg.getType ()+"\"><![CDATA["+arg.getValue()+"]]></value>";
        }

        //formatter+="</internalArguments>"+getClassClose ();
		formatter+="</internalArguments>";

        return (formatter);
    };

	this.toString  = function toString()
	{
		return "Selection: " + this.getSelection() +
			   " Action: "  + this.getAction() +
			   " Input: " + this.getInput();
	};

    if ((aSelection!==null) && (aSelection!==""))
	{
        this.setSelection(aSelection);
	}

    if (aSelection!==null)
    {
        this.setSAI(aSelection,anAction,anInput,"String",aPrompt);
	}
}

CTATSAI.prototype = Object.create(CTATBase.prototype);
CTATSAI.prototype.constructor = CTATSAI;

if(typeof module !== 'undefined')
{
    module.exports = CTATSAI;
}
