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
 
*/

/**
 *  http://api.jquery.com/jQuery.parseXML/
 */
function CTATMessageHandler ()
{
	CTATBase.call(this, "CTATMessageHandler", "messagehandler");

	var messageHandler=null;
	var pointer = this;
	
	var startStateMessages=new Array ();
	var inStartState=false;

	var xmlParser=new CTATXML ();
	
	/**
	 * 
	 */
	this.assignHandler=function assignHandler (aHandler)
	{
		pointer.debug ("assignHandler ()");
	
		messageHandler=aHandler;
	};
	/**
	 * 
	 */
	this.getInStartState=function getInStartState ()
	{
		return (inStartState);
	};
	/**
	 * http://api.jquery.com/category/traversing/
	 * http://www.w3schools.com/dom/dom_nodes_traverse.asp
	 */
	this.processMessage=function processMessage (aMessage)
	{	
		pointer.debug ("processMessage ("+lastMessage+")");
				
		var xmlDoc=null;
								
		/*
		if (lastMessage===true)
		{
			commShell.nextProblem (aMessage);
		}
		*/
				
		//var begin=getTimeStamp ();
		
		pointer.debug ("Attempting parse ...");

		xmlDoc=xmlParser.parseXML (aMessage);

		if (xmlDoc==null)
		{
			pointer.debug ("Error parsing xml");
			return;
		}		
		
		//var end=getTimeStamp ();
				
		//pointer.debug ("XML Parsing took: " + (end-begin) + " milliseconds");
		
		//begin=getTimeStamp ();
		
		if (xmlDoc===null)
		{
			pointer.debug ("Aborted XML parsing");
			return;
		}
		
		this.parseElement (xmlDoc);
		
		//end=getTimeStamp ();
		
		//pointer.debug ("Start state processing took: " + (end-begin) + " milliseconds");
	};
	/**
	 * 
	 */
	this.parseElement=function parseElement (anElement)
	{
		pointer.debug ("parseElement ("+xmlParser.getElementName (anElement)+")");
		
		if ((xmlParser.getElementName (anElement)=="CTATResponseMessages") || (xmlParser.getElementName (anElement)=="StartStateMessages") || (xmlParser.getElementName (anElement)=="MessageBundle"))
			this.processMessageBundle (anElement);
		
		if (xmlParser.getElementName (anElement)=="message")
			this.processSingleMessage (anElement);
	};
	/**
	 * 
	 */
	this.processMessageBundle=function processMessageBundle (anElement)
	{
		pointer.debug ("processMessageBundle ("+xmlParser.getElementName (anElement)+")");
	
		//x=anElement.childNodes;
		x=xmlParser.getElementChildren (anElement);
		
		for (var i=0;i<x.length;i++)
		{
			this.parseElement (x [i]);
		}		
	};
	/**
	 * 
	 */
	 
	 var count=0;
	 
	this.processSingleMessage=function processSingleMessage (anElement)
	{
		pointer.debug ("processSingleMessage ("+xmlParser.getElementName (anElement)+")");
		
		var aMessage=new CTATMessage (anElement);
				
		var x=xmlParser.getElementChildren (anElement);
		
		pointer.debug ("Generated CTATMessage, now doing regular processing ...");
		
		for (var i=0;i<x.length;i++)
		{
			var tempElement=x [i];
			
			if (xmlParser.getElementName (tempElement)=='properties')
			{
				pointer.debug ("Parsing properties ...");
				
				var messageProperties=xmlParser.getElementChildren (tempElement);
				
				for (var t=0;t<messageProperties.length;t++)
				{
					var propNode=messageProperties [t];
															
					if (xmlParser.getElementName (propNode)=="MessageType")
					{
						var nodeValue=xmlParser.getNodeTextValue (propNode);
						
						pointer.debug ("MessageType: " + nodeValue);
						
						if (nodeValue=="StateGraph")
						{	
							//useDebugging=true;
							
							this.processStateGraph (tempElement);
							
							//useDebugging=false;
						}
					
						if (nodeValue=="StartProblem")
						{
							inStartState=true;
							
							this.processStartProblem (messageProperties);
						}	
					
						if (nodeValue=="InterfaceIdentification")
						{
							this.processInterfaceIdentification (messageProperties);
						}	
					
						if (nodeValue=="InterfaceDescription")
							this.processInterfaceDescription (messageProperties);			
					
						if (nodeValue=="SendWidgetLock")
						{
							pointer.debug ("Found: SendWidgetLock");
							
						}	
					
						if (nodeValue=="StartStateEnd")
						{
							pointer.debug ("Found: StartStateEnd");
							
							this.displayComponentList ();
							
							if (messageHandler!=null)
							{
								messageHandler.processStartState ();
							}	

							this.processStartStateActions ();
							
							inStartState=false;
							
							//removeScrim ();
						}
						
						
						if (nodeValue=="CorrectAction")
						{
							pointer.debug ("Found: CorrectAction");

							messageHandler.processCorrectAction (aMessage);
						}
						
						if (nodeValue=="InCorrectAction")
						{
							pointer.debug ("Found: InCorrectAction");

							messageHandler.processInCorrectAction (aMessage);
						}
						
						if (nodeValue=="HighlightMsg")
						{
							pointer.debug ("Found: HighlightMsg");

							messageHandler.processHighlightMsg (aMessage);
						}
						
						if (nodeValue=="UnHighlightMsg")
						{
							pointer.debug ("Found: UnHighlightMsg");
							
							messageHandler.processUnHighlightMsg (aMessage);
						}
						
						if (nodeValue=="AssociatedRules")
						{
							pointer.debug ("Found: AssociatedRules ("+messageProperties.length+")");		
							
							var advice="";
							var actor="";
							var indicator="";
							var stepID="";
							var logAsResult="";
							var toolSelection="";
							
							for (var k=0;k<messageProperties.length;k++)
							{
								var testNode=messageProperties [k];
								
								//pointer.debug ("testNode:" + testNode.nodeName);
								
								if (xmlParser.getElementName (testNode)=="TutorAdvice")
								{
									advice=xmlParser.getNodeTextValue (testNode);
								}
								
								if (xmlParser.getElementName (testNode)=="Actor")
								{
									actor=xmlParser.getNodeTextValue (testNode);
								}
								
								if (xmlParser.getElementName (testNode)=="Indicator")
								{
									indicator=xmlParser.getNodeTextValue (testNode);
								}
																
								if (xmlParser.getElementName (testNode)=="StepID")
								{
									stepID=xmlParser.getNodeTextValue (testNode);
								}
								
								if (xmlParser.getElementName (testNode)=="LogAsResult")
								{
									logAsResult=xmlParser.getNodeTextValue (testNode);
								}
								
								if (xmlParser.getElementName (testNode)=="tool_selection")
								{
									toolSelection=xmlParser.getNodeTextValue (testNode);
								}
								
								if (xmlParser.getElementName (testNode)=="Skills")
								{
									pointer.debug ("Processing skills ...");
									
									if (skillSet==null)
									{	
										pointer.debug ("Interesting, there isn't a skillSet object yet. Creating ...");
										skillSet=new CTATSkillSet ();
									}

									skillSet.parseByValue(testNode);									 
								}								
							}
							
							messageHandler.processAssociatedRules (aMessage,indicator,advice);
						}
						
						if (nodeValue=="BuggyMessage")
						{
							pointer.debug ("Found: BuggyMessage");
							
							messageHandler.processBuggyMessage (aMessage);
						}
						
						if (nodeValue=="SuccessMessage")
						{
							pointer.debug ("Found: SuccessMessage");
							
							messageHandler.processSuccessMessage (aMessage);
						}
						
						if (nodeValue=="InterfaceAction")
						{
							pointer.debug ("Found: InterfaceAction");
							
							if (inStartState==true)
							{
								startStateMessages.push (aMessage);
							}
							else
							{
								messageHandler.processInterfaceAction (aMessage);
							}
						}
						
						if (nodeValue=="InterfaceIdentification")
						{
							pointer.debug ("Found: InterfaceIdentification");							
							
							//messageHandler.processInterfaceIdentification (aMessage);
						}
						
						if (nodeValue=="AuthorModeChange")
						{
							pointer.debug ("Found: AuthorModeChange");
							
							//messageHandler.processAuthorModeChange (aMessage);
							
							messageHandler.globalReset ();							
						}
						
						if (nodeValue=="ResetAction")
						{
							pointer.debug ("Found: ResetAction");
							
							// For now not implemented
							
							messageHandler.globalReset ();
						}
												
						if (nodeValue=="ShowHintsMessage")
						{
							pointer.debug ("Found: ShowHintsMessage");
							
							var hintComplete=false;
							var hintArray=new Array ();
							
							// Extract all the hints from XML and store them in a temporary list
							
							for (var k=0;k<messageProperties.length;k++)
							{
								var propNode=messageProperties [k];
								
								if (xmlParser.getElementName (propNode)=="HintsMessage")
								{
									var aList=xmlParser.getElementChildren (propNode);
									
									for (var w=0;w<aList.length;w++)
									{
										var hintNode=aList [w];
										
										if (xmlParser.getElementName (hintNode)=="value")
										{
											hintArray.push (xmlParser.getNodeTextValue (hintNode));
										
											hintComplete=true;
										}	
									}	
								}
							}	
							
							// Call the commshell to handle the message together with the list of hints
							
							if (hintComplete==true)
							{
								messageHandler.processHintResponse (aMessage,hintArray);
							}
							else
								pointer.debug ("Error: incomplete hint message received");
							
							// All done
						}
						
						if (nodeValue=="ConfirmDone")
						{
							pointer.debug ("Found: ConfirmDone");
							
							messageHandler.processConfirmDone (aMessage);
						}
						
						if (nodeValue=="VersionInfo")
						{
							pointer.debug ("Found: VersionInfo");
							
							messageHandler.processVersionInfo (messageProperties);
						}
						
						if (nodeValue=="TutoringServiceAlert")
						{
							pointer.debug ("Found: TutoringServiceAlert");
							
							messageHandler.processTutoringServiceAlert (messageProperties);
						}
						
						if (nodeValue=="TutoringServiceError")
						{
							pointer.debug ("Found: TutoringServiceError");
																					
							messageHandler.processTutoringServiceError (messageProperties);
						}
						
						if (nodeValue=="ProblemSummaryResponse")
						{
							pointer.debug ("Found: ProblemSummaryResponse");
							
							messageHandler.processProblemSummaryResponse (aMessage);
						}
						
						if (nodeValue=="ProblemRestoreEnd")
						{
							pointer.debug ("Found: ProblemRestoreEnd");
							
							messageHandler.processProblemRestoreEnd (aMessage);
						}
					}	
				}
			}
		}				
	};
	/**
	 * <message>
	 * <verb>SendNoteProperty</verb>
	 * <properties>
	 *     <MessageType>StateGraph</MessageType>
	 *     <caseInsensitive>true</caseInsensitive>
	 *     <unordered>true</unordered>
	 *     <lockWidget>true</lockWidget>
	 *     <suppressStudentFeedback>false</suppressStudentFeedback>
	 *     <highlightRightSelection>true</highlightRightSelection>
	 *     <confirmDone>false</confirmDone>
	 *     <Skills>
	 *       <value>right right=0.3=0=Right Branch</value>
	 *     </Skills>
	 *   </properties>
	 * </message>
	 */
	this.processStateGraph=function processStateGraph (aPropertyList)
	{
		pointer.debug ("processStateGraph ()");
		
		if ((aPropertyList==undefined) || (aPropertyList==null))
		{
			pointer.debug ("Error: state graph property list is undefined");
			return;
		}
		
		pointer.debug ("Processing node: " + aPropertyList.nodeName);
				
		if (xmlParser.getElementChildren (aPropertyList)==null)
		{
			pointer.debug ("Error: state graph property list is undefined");
			return;
		}		
				
		var messageProperties=xmlParser.getElementChildren (aPropertyList);
		
		for (var t=0;t<messageProperties.length;t++)
		{
			var propNode=messageProperties [t];
									
			pointer.debug ("State graph attribute: " + xmlParser.getElementName (propNode));
			
			if (xmlParser.getElementName (propNode)=="caseInsensitive")
			{
				if (xmlParser.getNodeTextValue (propNode)=='false')
					caseInsensitive=false;
				else
					caseInsensitive=true;
			}
			
			if (xmlParser.getElementName (propNode)=="unordered")
			{
				if (xmlParser.getNodeTextValue (propNode)=='false')
					unordered=false;
				else
					unordered=true;				
			}
			
			if (xmlParser.getElementName (propNode)=="lockWidget")
			{
				if (xmlParser.getNodeTextValue (propNode)=='false')
					lockWidget=false;
				else
					lockWidget=true;				
			}
			
			if (xmlParser.getElementName (propNode)=="suppressStudentFeedback")
			{
				if (xmlParser.getNodeTextValue (propNode)=='false')
					suppressStudentFeedback=false;
				else
					suppressStudentFeedback=true;				
			}
			
			if (xmlParser.getElementName (propNode)=="highlightRightSelection")
			{
				if (xmlParser.getNodeTextValue (propNode)=='false')
					highlightRightSelection=false;
				else
					highlightRightSelection=true;				
			}
			
			if (xmlParser.getElementName (propNode)=="confirmDone")
			{
				pointer.debug ("Confirm done: " + xmlParser.getNodeTextValue (propNode));
				
				if (xmlParser.getNodeTextValue (propNode)=='false')
					confirmDone=false;
				else
					confirmDone=true;				
			}
		    
			if (xmlParser.getElementName (propNode)=="Skills")
			{
				pointer.debug ("Processing skills ...");
				
				if (skillSet==null)
					skillSet=new CTATSkillSet ();

				skillSet.parseByValue(propNode);
				
				messageHandler.updateSkillWindow ();
			}
		}
	};
	/**
	 * 
	 */
	this.processStartProblem=function processStartProblem (aPropertyList)
	{
		//pointer.debug ("processStartProblem ()");
		
	};
	/**
	 * 
	 */
	this.processInterfaceIdentification=function processInterfaceIdentification (aPropertyList)
	{
		//pointer.debug ("processInterfaceIdentification ()");
		
	};
	/**
	 * 
	 */
	this.processInterfaceDescription=function processInterfaceDescription (aPropertyList)
	{
		pointer.debug ("processInterfaceDescription ("+aPropertyList.length+")");
				
		var aType="Unknown";
		var aName="Unknown";
				
		for (var i=0;i<aPropertyList.length;i++)
		{
			var tempElement=aPropertyList [i];
						
			if (xmlParser.getElementName (tempElement)=="WidgetType")
			{
				pointer.debug ("Widget type: " + xmlParser.getNodeTextValue (tempElement));
				
				aType=xmlParser.getNodeTextValue (tempElement);
			}
			
			if (xmlParser.getElementName (tempElement)=="CommName")
			{
				pointer.debug ("Instance name: " + xmlParser.getNodeTextValue (tempElement));
				
				aName=xmlParser.getNodeTextValue (tempElement);
			}
			
			if (xmlParser.getElementName (tempElement)=="serialized")
			{
				pointer.debug ("De-serializing component ...");
				
				this.deserializeComponent (aType,aName,tempElement.childNodes[0]);
			}
			
			if (xmlParser.getElementName (tempElement)=="interface")
			{
				pointer.debug ("Storing interface for post start-state reconstruction ...");
				
				interfaceElement=tempElement;
			}
			
			if (xmlParser.getElementName (tempElement)=="script")
			{
				pointer.debug ("Storing and loading main javascript code as defined by the BRD ...");
				
				scriptElement=xmlParser.getNodeTextValue (tempElement);
			}			
		}	
	};
	/**
	 *
	 */
	this.deserializeComponent=function deserializeComponent (aType,aName,aComponentElement)
	{
		pointer.debug ("deserializeComponent ("+aType+","+aName+")");
		
		// quick test to see if we have new style components 
		
		if (aComponentElement.attributes.getNamedItem("x")==null)
		{
			pointer.debug ("Warning: this component does not have x,y information. Probably an older component");
			return;
		}
		
		var x=aComponentElement.attributes.getNamedItem("x").value;
		var y=aComponentElement.attributes.getNamedItem("y").value;
		var width=aComponentElement.attributes.getNamedItem("width").value;
		var height=aComponentElement.attributes.getNamedItem("height").value;
		
		//debug ("x: " + x + ", y: " + y + ", width: " + width + ", height: " + height);
		
		var compEntry=new CTATComponentDescription ();
		compEntry.type=aType;
		compEntry.name=aName;
		compEntry.x=Math.floor (x);
		compEntry.y=Math.floor (y);
		compEntry.width=Math.floor (width);
		compEntry.height=Math.floor (height);
		
		components.push (compEntry);
		
		var serializedProps=aComponentElement.childNodes;
		
		for (var i=0;i<serializedProps.length;i++)
		{
			var tempElement=serializedProps [i];
			
			if (tempElement.nodeName=="Parameters")
			{
				this.debug ("Processing component parameters ...");
				
				var paramProps=tempElement.childNodes [0].childNodes;
				
				for (var t=0;t<paramProps.length;t++)
				{
					var paramProperty=paramProps [t];
				
					if (paramProperty.nodeName=="CTATComponentParameter")
					{
						this.debug ("Processing parameter property ...");
						
						var aParam=new CTATParameter();
						
						var paramValues=paramProperty.childNodes;
						
						for (var j=0;j<paramValues.length;j++)
						{
							var paramElement=paramValues [j];
							
							if (paramElement.nodeName=="name")
							{
								this.debug ("Found parameter name: " + xmlParser.getNodeTextValue (paramElement));
								aParam.paramName=xmlParser.getNodeTextValue (paramElement);								
							}
							
							if (paramElement.nodeName=="value")
							{	
								this.debug ("Found parameter value: " + xmlParser.getNodeTextValue (paramElement));

								aParam.paramValue=xmlParser.getNodeTextValue (paramElement);
								
								if(aParam.paramName=="group")
								{
									compEntry.groupName=aParam.paramValue;
								}
							}	
						}
							
						this.debug ("Parameter name: " + aParam.paramName + ", value: " + aParam.paramValue);
						compEntry.params.push(aParam);
					}
				}
			}
			
			if (tempElement.nodeName=="Styles")
			{
				this.debug ("Processing component styles ...");
				
				var stylesProps=tempElement.childNodes [0].childNodes;
				
				for (var t=0;t<stylesProps.length;t++)
				{
					var styleProperty=stylesProps [t];
					
					if (styleProperty.nodeName=="CTATStyleProperty")
					{
						this.debug ("Processing style property ...");
						
						//alert(xml_to_string(styleProperty));
						
						var aStyle=new CTATStyle ();
						
						var styleValues=styleProperty.childNodes;
						
						for (var j=0;j<styleValues.length;j++)
						{
							var styleElement=styleValues [j];
							
							//alert(xml_to_string(styleElement));
							
							if (styleElement.nodeName=="name")
							{
								this.debug ("Found style name: " + xmlParser.getNodeTextValue (styleElement));
								aStyle.styleName=xmlParser.getNodeTextValue (styleElement);								
							}
							
							if (styleElement.nodeName=="value")
							{	
								this.debug ("Found style value: " + xmlParser.getNodeTextValue (styleElement));
								
								if((aStyle.styleName != "DisplayHTMLText") || (aStyle.styleName != "ProcessHTMLInHints"))
								{
									aStyle.styleValue=xmlParser.getNodeTextValue (styleElement);
								}								
								else
								{
									var encoder=new CTATHTMLManager();
									aStyle.styleValue=encoder.htmlEncode(styleElement);
								}
							}	
						}
							
						this.debug ("Style name: " + aStyle.styleName + ", value: " + aStyle.styleValue);
						
						compEntry.styles.push(aStyle);				
					}
				}	
			}			
		}			
	};
	/**
	 * 
	 */
	this.displayComponentList=function displayComponentList ()
	{
		pointer.debug ("displayComponentList ("+components.length+")");
		
		for (var i=0;i<components.length;i++)
		{
			var aDesc=components [i];
			
			pointer.debug ("Component (Desc): " + aDesc.name + ", type: " + aDesc.type+", x: " + aDesc.x + ", y: " + aDesc.y + ", width: " + aDesc.width + ", height: " + aDesc.height);
			
			var component=aDesc.getComponentPointer ();
			
			if (component!=null)
			{
				pointer.debug ("Component (Pointer): " + component.name + ", type: " + component.type+", x: " + component.x + ", y: " + component.y + ", width: " + component.width + ", height: " + component.height);
			}	
		}
	};
	/**
	 * 
	 * @param xml_node
	 * @returns
	 */
    function xml_to_string(xml_node)
    {
        if (xml_node.xml)
            return xml_node.xml;
        else if (XMLSerializer)
        {
            var xml_serializer = new XMLSerializer();
            return xml_serializer.serializeToString(xml_node);
        }
        else
        {
            alert("ERROR: Extremely old browser");
            return "";
        }
    }	
    /**
     * 
     */
    this.processStartStateActions=function processStartStateActions ()
    {    	
    	pointer.debug ("processStartStateActions ()");
    	
		for (var i=0;i<startStateMessages.length;i++)
		{
			var aMessage=startStateMessages [i];
			messageHandler.processInterfaceAction (aMessage);						
		}	    	
				
		if (scriptElement!="")
		{
			try
			{
				eval (scriptElement);
			}
			catch (err)
			{
				alert ("Error executing script from BRD: " + err.message);
			}
		}		
    };
}

CTATMessageHandler.prototype = Object.create(CTATBase.prototype);
CTATMessageHandler.prototype.constructor = CTATMessageHandler;
