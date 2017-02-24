/**------------------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 
 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
 http://stackoverflow.com/questions/6486307/default-argument-values-in-javascript-functions
 
*/

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
function CTATSAI(aSelection,anAction,anInput,aPrompt) 
{
    CTATBase.call(this, "CTATSAI","sai");

    // we use the instance name as the selection
        
    var action="undefined";        
    var prompt="undefined";
    var inputFlattened="";
    var tools=new CTATStringUtil ();
    var internalArguments=new Array ();

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
            
        for (i=0;i<this.internalArguments.length;i++)
        {
                var arg=internalArguments [i];
            
                if (arg.getType()==="Boolean") 
                {
                    this.debug ("Adding Boolean argument ("+arg.getValue()+") ...");
                    
                    sai_arguments.push(tools.String2Boolean(arg.getValue()));
                } 
                else if (arg.getType() === "Number") 
                {
                    this.debug ("Adding Number argument ("+arg.getValue()+") ...");
                    sai_arguments.push(new Number(arg.getValue()));
                }
                else if (arg.getType() === "String") 
                {
                    if (arg.getValue()==="No_Value") 
                    {
                        this.debug ("Detected default argument ("+arg.getValue()+"), setting contents to null instead");
                        
                        sai_arguments.push(null);
                    } 
                    else 
                    {
                        sai_arguments.push(new String(arg.getValue()));
                    }
                } 
                else 
                {
                    this.debug ("Unrecognized argument type: "+arg.getType()+" in "+this.toSerializedString()+" IGNORING IT!!!");
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
     * 
     */
    this.checkDefaultArgument=function checkDefaultArgument ()
    {
        this.debug ("checkDefaultArgument ()");
            
        if (internalArguments.length===0)
        {
            this.debug ("Adding default argument ...");
            
            var defaultArgument=new CTATArgument ();
            internalArguments.push (defaultArgument);
        }                        
    };
    /**
     * 
     */
    this.setArgument=function setArgument (anIndex,aValue)
    {    
        checkDefaultArgument ();
            
        var tempArgument=internalArguments [anIndex];
        
        tempArgument.value=aValue;
        
        return (tempArgument);
    };    
    /**
     * 
     */
    this.addArgument=function addArgument (aValue,aType,aFormat)
    {
        //this.debug ("addArgument ()");
        
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
        //this.debug ("addExistingArgument ()");
        
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
        this.debug("setInput("+newInput+")");

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
        this.debug ("getInput()");
            
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
        this.debug("setType()");
        
        this.checkDefaultArgument ();
        
        var arg=this.getArgument (0);
        arg.type=aType;
    };
    /**
     *    Returns the primary selection value
     */
    this.getType=function getType() 
    {
        this.debug("getType()");
            
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
        this.debug("setFormat()");
            
        this.checkDefaultArgument ();            
            
        var arg=this.getArgument (0);
        arg.setFormat (aFormat);
    };
    /**
     *    Returns the primary selection value
     */
    this.getFormat=function getFormat() 
    {
        this.debug("getFormat()");
        
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
        this.debug("setSelection("+newInput+")");
        
        this.setName(newInput);
    };
    /**
    *    Returns the primary selection value
    */
    this.getSelection=function getSelection() 
    {
        this.debug("getSelection()");
        
        return (this.getName());
    };
    /**
    *
    */
    this.setAction=function setAction(newInput) 
    {
        this.debug("setAction("+newInput+")");
        
        action = newInput;
    };
    /**
    *    Returns the primary action value
    */
    this.getAction=function getAction() 
    {
        this.debug("getAction()");
        
        return action;
    };
    /**
    *
    */
    this.setPrompt=function setPrompt(newInput) 
    {
        this.debug("setPrompt("+newInput+")");
        
        prompt = newInput;
    };
    /**
    *    Returns the primary action value
    */
    this.getPrompt=function getPrompt() 
    {
        this.debug("getPrompt()");
        
        return prompt;
    };
    /**
     *    
     */
    this.propagate=function propagate (source)        
    {
        this.debug ("propagate ()");
            
        var sourceArguments=source.getArguments ();
            
        for (var i=0;i<sourceArguments.length;i++)
        {
            var fromArg=sourceArguments [i];
            var toArg=internalArguments [i];
                
            if ((fromArg===null) || (toArg===null))
            {
                this.debug ("Internal error: argument lists do not align between received SAI and source SAI");
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
        var parser=new CTATXML ();
        
        var rootNode=parser.parseXML (aStream);
        
        this.fromXML (rootNode);
    };
    /**
    * Provided to the parse method is a single node, which can have any node name.
    * The code below will then search in that node for the selection, action, input
    * fields, etc.    
    */    
    this.fromXML=function fromXML (aNode)
    {
        this.debug ("fromXML ()");
                          
        var parser=new CTATXML ();
        
        internalArguments=new Array ();

        var entries=aNode.childNodes;

        inputFlattened="";

        for (var t=0;t<entries.length;t++)
        {
            var entry=entries [t];
            
            //>-----------------------------------------------------------------------------

            if ((entry.nodeName==="selection") || (entry.nodeName==="Selection"))
            {
                //this.debug ("Parsing selection");
                
                var vals=entry.childNodes;
                    
                var nameMatched=false;
                    
                for (var i=0;i<vals.length;i++)
                {
                    var val=vals [i];
                    
                    if (val.nodeName==="value")
                    {
                        //this.debug ("Parsing value: " + parser.getNodeTextValue (val));
                        
                        nameMatched=true;
                        this.setSelection (parser.getNodeTextValue (val));
                    }                            
                }
                
                if (nameMatched===false)
                {
                    //this.debug ("Parsing value: " + parser.getNodeTextValue (entry));
                    
                    this.setSelection (parser.getNodeTextValue (entry));
                }    
            }
            
            //>-----------------------------------------------------------------------------
            
            if ((entry.nodeName==="action") || (entry.nodeName==="Action"))
            {
                var acts=entry.childNodes;
                    
                var actionMatched=false;
                    
                for (var j=0;j<acts.length;j++)
                {
                    var act=acts [j];
                    
                    if (act.nodeName==="value")
                    {
                        actionMatched=true;
                        this.setAction (parser.getNodeTextValue (act));    
                    }                            
                }

                if (actionMatched===false)
				{
                    this.setAction (parser.getNodeTextValue (entry));
				}	
            }
            
            //>-----------------------------------------------------------------------------
                
            if ((entry.nodeName==="internalArguments") || (entry.nodeName==="value") || (entry.nodeName==="Input"))
            {
                var args=entry.childNodes;
                var newValue=null;
				
                internalArguments=new Array ();
                    
                var formatter=new CTATHTMLManager ();
                var newArgument=new CTATArgument ();                    
                var ind=0;
                    
                internalArguments.push (newArgument);
                    
                for (var k=0;k<args.length;k++)
                {                
                    var argument=args [k];
                    
                    if (argument.nodeName==="value")
                    {
                        if (argument.childNodes !== null)
                        {
                            this.debug ("Parsing SAI input ...");

                            this.debug ("Childnodes: " + argument.childNodes.length);

                            if (argument.childNodes.length===1)
                            {
                                newValue=formatter.htmlDecode (parser.getNodeTextValue(argument));
                                
                                this.debug ("Setting new value to: " + newValue);
                                
                                newArgument.setValue (newValue);
                            }
                            else
                            {
                                if (ind>0)
								{
                                    inputFlattened+=",";
								}	
                                                        
                                newValue=formatter.htmlDecode (parser.getNodeTextValue(argument));
                                
                                this.debug ("Setting new value to: " + newValue);
                                
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
                            newVal=formatter.htmlDecode (parser.getNodeTextValue(argument));
                            
                            this.debug ("Setting new value to: " + newVal);
                            
                            newArgument.setValue (newVal);
                        }
                    }
                }
            }
            
            //>-----------------------------------------------------------------------------
            
            if ((entry.nodeName==="prompt") || (entry.nodeName==="Prompt"))
            {                    
                this.debug ("Parsing prompt ...");
                                
                this.setPrompt (parser.getNodeTextValue (entry));
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
        this.debug ("fromXMLInternal ()");
                          
        var parser=new CTATXML ();
        
        internalArguments=new Array ();

        var entries=aNode.childNodes;

        inputFlattened="";

        for (var t=0;t<entries.length;t++)
        {
            var entry=entries [t];
            
            //>-----------------------------------------------------------------------------

            if ((entry.nodeName==="selection") || (entry.nodeName==="Selection"))
            {
                //this.debug ("Parsing selection");
                
                var vals=entry.childNodes;
                    
                var nameMatched=false;
                    
                for (var i=0;i<vals.length;i++)
                {
                    var val=vals [i];
                    
                    if (val.nodeName==="value")
                    {
                        //this.debug ("Parsing value: " + parser.getNodeTextValue (val));
                        
                        nameMatched=true;
                        this.setSelection (parser.getNodeTextValue (val));
                    }                            
                }
                
                if (nameMatched===false)
                {
                    //this.debug ("Parsing value: " + parser.getNodeTextValue (entry));
                    
                    this.setSelection (parser.getNodeTextValue (entry));
                }    
            }
            
            //>-----------------------------------------------------------------------------
            
            if ((entry.nodeName==="action") || (entry.nodeName==="Action"))
            {
                var acts=entry.childNodes;
                    
                var actionMatched=false;
                    
                for (var j=0;j<acts.length;j++)
                {
                    var act=acts [j];
                    
                    if (act.nodeName==="value")
                    {
                        actionMatched=true;
                        this.setAction (parser.getNodeTextValue (act));    
                    }                            
                }

                if (actionMatched===false)
				{
                    this.setAction (parser.getNodeTextValue (entry));
				}	
            }
            
            //>-----------------------------------------------------------------------------
                
            if ((entry.nodeName==="internalArguments") || (entry.nodeName==="value") || (entry.nodeName==="Input"))
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
                    
                    if (argument.nodeName==="value")
                    {
                        if (argument.childNodes !== null)
                        {
                            this.debug ("Parsing SAI input ...");

                            this.debug ("Childnodes: " + argument.childNodes.length);

                            if (argument.childNodes.length===1)
                            {
                                newValue=formatter.htmlDecode (parser.getNodeTextValue(argument));
                                
                                this.debug ("Setting new value to: " + newValue);
                                
                                newArgument.setValue (newValue);
                            }
                            else
                            {
                                if (ind>0)
								{
                                    inputFlattened+=",";
								}	
                                                        
                                newValue=formatter.htmlDecode (parser.getNodeTextValue(argument));
                                
                                this.debug ("Setting new value to: " + newValue);
                                
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
                            newVal=formatter.htmlDecode (parser.getNodeTextValue(argument));
                            
                            this.debug ("Setting new value to: " + newVal);
                            
                            newArgument.setValue (newVal);
                        }
                    }
                }
            }
            
            //>-----------------------------------------------------------------------------
            
            if ((entry.nodeName==="prompt") || (entry.nodeName==="Prompt"))
            {                    
                this.debug ("Parsing prompt ...");
                                
                this.setPrompt (parser.getNodeTextValue (entry));
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
    
        formatter+="<selection>"+name+"</selection><action>"+action+"</action><input>";
        
        for (var i=0;i<internalArguments.length;i++)
        {
            var arg=internalArguments [i];
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
        
        formatter+="<Selection><value>"+this.getName ()+"</value></Selection><Action><value>"+this.getAction ()+"</value></Action><Input>";
            
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
            formatter+="<value>"+this.getInput()+"</value>";
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
        
        formatter+=getClassOpen ()+"<selection>"+this.getName ()+"</selection><action>"+this.getAction()+"</action><internalArguments>";
        
        for (var i=0;i<internalArguments.length;i++)
        {
            var arg=internalArguments [i];
            formatter+="<value fmt=\"text\" name=\""+arg.getName ()+"\" type=\""+arg.getType ()+"\">"+arg.getValue()+"</value>";
        }
        
        formatter+="</internalArguments>"+getClassClose ();
                
        return (formatter);
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
