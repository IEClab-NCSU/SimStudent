/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATCommunication/CTATMessageHandler.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATMessageHandler');

goog.require('CTATBase');
goog.require('CTATComponentDescription');
goog.require('CTATConfig');
goog.require('CTATGlobals');
goog.require('CTATHTMLManager');
goog.require('CTATJSON');
goog.require('CTATMessage');
goog.require('CTATParameter');
goog.require('CTATSkillSet');
goog.require('CTATStyle');
goog.require('CTATXML');
goog.require('CTATCompBase')

/**
 *
 */
CTATMessageHandler = function()
{
	CTATBase.call(this, "CTATMessageHandler", "messagehandler");

	var messageHandler=null;
	var pointer = this;

	var startStateMessages=new Array ();
	var inStartState=false;

	var messageParser=null;
	var startstatedelay=1000;
	//var startstatedelaytimer=-1;

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
	this.assignHandler=function assignHandler (aHandler)
	{
		pointer.ctatdebug ("assignHandler ()");

		messageHandler=aHandler;
	};
	/**
	 *
	 */
	this.reset=function reset ()
	{
		pointer.ctatdebug ("reset ()");

		startStateMessages=new Array ();
	}
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
	var disableComms=false;

	this.processMessage=function processMessage (aMessage)
	{
		pointer.ctatdebug ("processMessage ("+lastMessage+")");

		if (disableComms==true)
		{
			return;
		}

		if (CTATConfig.parserType=="json")
		{
			if (aMessage.indexOf ("<?xml")!=-1)
			{
				alert ("Error: CTAT is configured to parse JSON but it received an XML message, disabling comm capabilities ...");
				disableComms=true;
				return;
			}
		}

		if (CTATConfig.parserType=="xml")
		{
			if (aMessage.indexOf ("{")==0)
			{
				alert ("Error: CTAT is configured to parse XML but it received a JSON message, disabling comm capabilities ...");
				disableComms=true;
				return;
			}
		}

		var docRoot=null;

		pointer.ctatdebug ("Attempting parse ...");

		docRoot=messageParser.parse (aMessage);

		if (docRoot==null)
		{
			pointer.ctatdebug ("Error parsing message");
			return;
		}

		//useDebugging=true;

		this.parseElement (docRoot);

		//useDebugging=false;
	};
	/**
	 *
	 */
	this.parseElement=function parseElement (anElement)
	{
		var aName=messageParser.getElementName (anElement); // slight optimization

		pointer.ctatdebug ("parseElement ("+aName+")");

		if ((aName=="CTATResponseMessages") || (aName=="StartStateMessages") || (aName=="MessageBundle"))
		{
			this.processMessageBundle (anElement);
		}

		if (aName=="message")
		{
			this.processSingleMessage (anElement);
		}
	};
	/**
	 *
	 */
	this.processMessageBundle=function processMessageBundle (anElement)
	{
		var aName=messageParser.getElementName (anElement); // slight optimization

		pointer.ctatdebug ("processMessageBundle ("+aName+")");

		var list=messageParser.getElementChildren (anElement);

		if (list==null)
		{
			pointer.ctatdebug ("Error parsing bundle");
			return;
		}

		ctatdebug ("Processing " + list.length + " objects in bundle");

		for (var i=0;i<list.length;i++)
		{
			this.parseElement (list [i]);

			/*
			if (i==2)
			{
				return;
			}
			*/
		}
	};
	/**
	 *
	 */
	this.processSingleMessage=function processSingleMessage (anElement)
	{
//		useDebugging = true

		pointer.ctatdebug ("processSingleMessage ("+messageParser.getElementName (anElement)+")");

		var aMessage=new CTATMessage (anElement);

		var x=messageParser.getElementChildren (anElement);

		pointer.ctatdebug ("Generated CTATMessage, now doing regular processing ...");

		for (var i=0;i<x.length;i++)
		{
			var tempElement=x [i];

			if (messageParser.getElementName (tempElement)=='properties')
			{
				pointer.ctatdebug ("Parsing properties ...");

				var messageProperties=messageParser.getElementChildren (tempElement);

				for (var t=0;t<messageProperties.length;t++)
				{
					var propNode=messageProperties [t];

					if (messageParser.getElementName (propNode)=="MessageType")
					{
						var nodeValue=messageParser.getNodeTextValue (propNode);

						//useDebugging=true;
						pointer.ctatdebug ("MessageType: " + nodeValue);
						//useDebugging=false;

						if (nodeValue=="StateGraph")
						{
							this.processStateGraph (tempElement);
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
						{
							//useDebugging=true;
							this.processInterfaceDescription (messageProperties);
							//useDebugging=false;
						}

						if (nodeValue=="SendWidgetLock")
						{
							pointer.ctatdebug ("Found: SendWidgetLock");

						}

						if (nodeValue=="StartStateEnd")
						{
							//useDebugging=true;

							pointer.ctatdebug ("Found: StartStateEnd");

							this.displayComponentList ();

							if (messageHandler!=null)
							{
								messageHandler.processStartState ();
							}
							else
							{
								this.ctatdebug ("Error: no message handler object available to process start state");
							}

							//useDebugging=true;
							this.processStartStateActions ();
							//useDebugging=false;

							inStartState=false;

							//removeScrim ();
						}


						if (nodeValue=="CorrectAction")
						{
							//useDebugging=true;

							pointer.ctatdebug ("Found: CorrectAction");

							if (this.getInStartState ()==true)
							{
								startStateMessages.push (aMessage);
							}
							else
							{
								messageHandler.processCorrectAction (aMessage);
							}

							//useDebugging=false;
						}

						if (nodeValue=="InCorrectAction")
						{
							//useDebugging=true;

							pointer.ctatdebug ("Found: InCorrectAction");

							if (inStartState==true)
							{
								startStateMessages.push (aMessage);
							}
							else
							{
								messageHandler.processInCorrectAction (aMessage);
							}

							//useDebugging=false;
						}

						if (nodeValue=="HighlightMsg")
						{
							pointer.ctatdebug ("Found: HighlightMsg");

							messageHandler.processHighlightMsg (aMessage);
						}

						if (nodeValue=="UnHighlightMsg")
						{
							pointer.ctatdebug ("Found: UnHighlightMsg");

							messageHandler.processUnHighlightMsg (aMessage);
						}

						if (nodeValue=="AssociatedRules")
						{
							pointer.ctatdebug ("Found: AssociatedRules ("+messageProperties.length+")");

							var advice="";
							var actor="";
							var indicator="";
							var stepID="";
							var logAsResult="";
							var toolSelection="";

							for (var k=0;k<messageProperties.length;k++)
							{
								var testNode=messageProperties [k];

								//pointer.ctatdebug ("testNode:" + testNode.nodeName);

								if (messageParser.getElementName (testNode)=="TutorAdvice")
								{
									advice=messageParser.getNodeTextValue (testNode);
								}

								if (messageParser.getElementName (testNode)=="Actor")
								{
									actor=messageParser.getNodeTextValue (testNode);
								}

								if (messageParser.getElementName (testNode)=="Indicator")
								{
									indicator=messageParser.getNodeTextValue (testNode);
								}

								if (messageParser.getElementName (testNode)=="StepID")
								{
									stepID=messageParser.getNodeTextValue (testNode);
								}

								if (messageParser.getElementName (testNode)=="LogAsResult")
								{
									logAsResult=messageParser.getNodeTextValue (testNode);
								}

								if (messageParser.getElementName (testNode)=="tool_selection")
								{
									toolSelection=messageParser.getNodeTextValue (testNode);
								}

								if (messageParser.getElementName (testNode)=="Skills")
								{
									pointer.ctatdebug ("Processing skills ...");

									if (skillSet==null)
									{
										pointer.ctatdebug ("Interesting, there isn't a skillSet object yet. Creating ...");
										skillSet=new CTATSkillSet ();
									}

									skillSet.parseByValue(testNode);
								}
							}

							messageHandler.processAssociatedRules (aMessage,indicator,advice);
						}

						if (nodeValue=="BuggyMessage")
						{
							pointer.ctatdebug ("Found: BuggyMessage");

							messageHandler.processBuggyMessage (aMessage);
						}

						if (nodeValue=="SuccessMessage")
						{
							pointer.ctatdebug ("Found: SuccessMessage");

							messageHandler.processSuccessMessage (aMessage);
						}

						if (nodeValue=="InterfaceAction")
						{
							pointer.ctatdebug ("Found: InterfaceAction");
							if (inStartState==true)
							{
								pointer.ctatdebug ("inStartState==true => storing for later playback ...");

								startStateMessages.push (aMessage);
							}
							else
							{
								messageHandler.processInterfaceAction (aMessage);
							}
						}

						if (nodeValue=="InterfaceIdentification")
						{
							pointer.ctatdebug ("Found: InterfaceIdentification");

							//messageHandler.processInterfaceIdentification (aMessage);
						}

						if (nodeValue=="AuthorModeChange")
						{
							pointer.ctatdebug ("Found: AuthorModeChange");

							//messageHandler.processAuthorModeChange (aMessage);

							messageHandler.globalReset ();
						}

						if (nodeValue=="ResetAction")
						{
							pointer.ctatdebug ("Found: ResetAction");

							// For now not implemented

							messageHandler.globalReset ();
						}

						if (nodeValue=="ShowHintsMessage")
						{
							pointer.ctatdebug ("Found: ShowHintsMessage");

							var hintComplete=false;
							var hintArray=new Array ();

							// Extract all the hints from the message and store them in a temporary list

							for (var k=0;k<messageProperties.length;k++)
							{
								var propNode=messageProperties [k];

								if (messageParser.getElementName (propNode)=="HintsMessage")
								{
									var aList=messageParser.getElementChildren (propNode);

									for (var w=0;w<aList.length;w++)
									{
										var hintNode=aList [w];

										if (messageParser.getElementName (hintNode)=="value")
										{
											hintArray.push (messageParser.getNodeTextValue (hintNode));

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
								pointer.ctatdebug ("Error: incomplete hint message received");

							// All done
						}

						if (nodeValue=="ConfirmDone")
						{
							pointer.ctatdebug ("Found: ConfirmDone");

							messageHandler.processConfirmDone (aMessage);
						}

						if (nodeValue=="VersionInfo")
						{
							pointer.ctatdebug ("Found: VersionInfo");

							messageHandler.processVersionInfo (messageProperties);
						}

						if (nodeValue=="TutoringServiceAlert")
						{
							pointer.ctatdebug ("Found: TutoringServiceAlert");

							messageHandler.processTutoringServiceAlert (messageProperties);
						}

						if (nodeValue=="TutoringServiceError")
						{
							pointer.ctatdebug ("Found: TutoringServiceError");

							messageHandler.processTutoringServiceError (messageProperties);
						}

						if (nodeValue=="ProblemSummaryResponse")
						{
							pointer.ctatdebug ("Found: ProblemSummaryResponse");

							messageHandler.processProblemSummaryResponse (aMessage);
						}

						if (nodeValue=="ProblemRestoreEnd")
						{
							pointer.ctatdebug ("Found: ProblemRestoreEnd");

							messageHandler.processProblemRestoreEnd (aMessage);
						}
						if(nodeValue=="InterfaceAttribute")
						{
							pointer.ctatdebug("Found: InterfaceAttribute");

							//get the component's name from xml
							var name;
							for(var a = 0; a < messageProperties.length; a++){
								if(messageParser.getElementName (messageProperties[a]) == "component"){
									name = messageParser.getNodeTextValue (messageProperties[a]);
									// useDebugging=true;
									pointer.ctatdebug("Found component: "+name);
									// useDebugging=false;
								}
							}

							//get a pointer to the actual component object
							var compPointer = new CTATShellTools().findComponent(name)[0];
							if(compPointer == null){
								pointer.ctatdebug(name + " is not found");
								return;
							}

							//modify the component according to the xml
							for(var a = 0; a < messageProperties.length; a++){
								var currNode = messageProperties[a];
								var tag = messageParser.getElementName(currNode);
								if(tag == "MessageType" || tag == "component"){
									continue;
								}

								var val = messageParser.getNodeTextValue(currNode);
								// useDebugging=true;
								pointer.ctatdebug(tag + "="+val);
								// useDebugging=false;
								if(tag == "background_color"){//string
									compPointer.setBackgroundColor(val);
								}else if(tag == "border_color"){
									compPointer.setBorderColor(val);
								}else if(tag == "border_style"){
									compPointer.setBorderStyle(val);
								}else if(tag == "border_width"){
									compPointer.setBorderWidth(val);
								}else if(tag == "enabled"){
									compPointer.setEnabled(val == "true");
								}else if(tag == "font_color"){
									compPointer.setFontColor(val);
								}else if(tag == "font_size"){
									compPointer.setFontSize(val);
								}else if(tag == "height"){
									compPointer.setHeight(parseInt(val));
								}else if(tag == "hint_highlight"){
									compPointer.setHintHighlight(val == "true");
								}else if(tag == "text"){
									compPointer.setText(val);
								}else if(tag == "width"){
									compPointer.setWidth(parseInt(val));
								}else if(tag == "x_coor"){
									compPointer.setX(parseInt(val));
								}else if(tag == "y_coor"){
									compPointer.setY(parseInt(val));
								}
							}
						}
					}
				}
			}
		}
//		useDebugging = false
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
		pointer.ctatdebug ("processStateGraph ()");

		if ((aPropertyList==undefined) || (aPropertyList==null))
		{
			pointer.ctatdebug ("Error: state graph property list is undefined");
			return;
		}

		pointer.ctatdebug ("Processing node: " + aPropertyList.nodeName);

		if (messageParser.getElementChildren (aPropertyList)==null)
		{
			pointer.ctatdebug ("Error: state graph property list is undefined");
			return;
		}

		var messageProperties=messageParser.getElementChildren (aPropertyList);

		for (var t=0;t<messageProperties.length;t++)
		{
			var propNode=messageProperties [t];

			pointer.ctatdebug ("State graph attribute: " + messageParser.getElementName (propNode));

			if (messageParser.getElementName (propNode)=="caseInsensitive")
			{
				if (messageParser.getNodeTextValue (propNode)=='false')
					caseInsensitive=false;
				else
					caseInsensitive=true;
			}

			if (messageParser.getElementName (propNode)=="unordered")
			{
				if (messageParser.getNodeTextValue (propNode)=='false')
					unordered=false;
				else
					unordered=true;
			}

			if (messageParser.getElementName (propNode)=="lockWidget")
			{
				if (messageParser.getNodeTextValue (propNode)=='false')
					lockWidget=false;
				else
					lockWidget=true;
			}

			if (messageParser.getElementName (propNode)=="suppressStudentFeedback")
			{
				if (messageParser.getNodeTextValue (propNode)=='false')
					suppressStudentFeedback=false;
				else
					suppressStudentFeedback=true;
			}

			if (messageParser.getElementName (propNode)=="highlightRightSelection")
			{
				if (messageParser.getNodeTextValue (propNode)=='false')
					highlightRightSelection=false;
				else
					highlightRightSelection=true;
			}

			if (messageParser.getElementName (propNode)=="confirmDone")
			{
				pointer.ctatdebug ("Confirm done: " + messageParser.getNodeTextValue (propNode));

				if (messageParser.getNodeTextValue (propNode)=='false')
					confirmDone=false;
				else
					confirmDone=true;
			}

			if (messageParser.getElementName (propNode)=="Skills")
			{
				pointer.ctatdebug ("Processing skills ...");

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
		//pointer.ctatdebug ("processStartProblem ()");

	};
	/**
	 *
	 */
	this.processInterfaceIdentification=function processInterfaceIdentification (aPropertyList)
	{
		//pointer.ctatdebug ("processInterfaceIdentification ()");

	};
	/**
	 *
	 */
	this.processInterfaceDescription=function processInterfaceDescription (aPropertyList)
	{
		pointer.ctatdebug ("processInterfaceDescription ("+aPropertyList.length+")");

		var aType="Unknown";
		var aName="Unknown";
		var targetSerializationElement=null;

		for (var i=0;i<aPropertyList.length;i++)
		{
			var tempElement=aPropertyList [i];

			pointer.ctatdebug ("Inspecting element: " + messageParser.getElementName (tempElement));

			if (messageParser.getElementName (tempElement)=="WidgetType")
			{
				pointer.ctatdebug ("Widget type: " + messageParser.getNodeTextValue (tempElement));

				aType=messageParser.getNodeTextValue (tempElement);
			}

			if (messageParser.getElementName (tempElement)=="CommName")
			{
				pointer.ctatdebug ("Instance name: " + messageParser.getNodeTextValue (tempElement));

				aName=messageParser.getNodeTextValue (tempElement);
			}

			if (messageParser.getElementName (tempElement)=="serialized")
			{
				pointer.ctatdebug ("Prepareing to de-serialize component ...");

				var compTest=messageParser.getElementChildren (tempElement);

				for (var j=0;j<compTest.length;j++)
				{
					var serElement=compTest [j];

					if (messageParser.getElementName (serElement).indexOf ("CTAT")!=-1) // Safety check
					{
						targetSerializationElement=serElement;
					}
				}
			}

			if (messageParser.getElementName (tempElement)=="interface")
			{
				pointer.ctatdebug ("Storing interface for post start-state reconstruction ...");

				interfaceElement=tempElement;
			}

			if (messageParser.getElementName (tempElement)=="script")
			{
				pointer.ctatdebug ("Storing and loading main javascript code as defined by the BRD ...");

				scriptElement=messageParser.getNodeTextValue (tempElement);
			}
		}

		if (targetSerializationElement!=null)
		{
			//useDebugging=true;
			this.deserializeComponent (aType,aName,serElement);
			//useDebugging=false;
		}
		else
		{
			pointer.ctatdebug ("Error: unable to find CTAT serialization point of attachement");
		}

		pointer.ctatdebug ("processInterfaceDescription () done");
	};
	/**
	 *
	 */
	this.deserializeComponent=function deserializeComponent (aType,aName,aComponentElement)
	{
		pointer.ctatdebug ("deserializeComponent ("+aType+","+aName+")");

		if (messageParser.getElementAttr (aComponentElement,"x")==null)
		{
			pointer.ctatdebug ("Warning: this component does not have x,y information. Probably an older component");
			return;
		}

		//messageParser.listElementAttr (aComponentElement);

		var x=messageParser.getElementAttr (aComponentElement,"x");
		var y=messageParser.getElementAttr (aComponentElement,"y");
		var width=messageParser.getElementAttr (aComponentElement,"width");
		var height=messageParser.getElementAttr (aComponentElement,"height");
		var tabIndex=messageParser.getElementAttr (aComponentElement,"tabIndex");
		var zIndex=messageParser.getElementAttr (aComponentElement,"zIndex");

		if (tabIndex==null)
		{
			tabIndex=-1;
		}

		if (zIndex==null)
		{
			zIndex=0;
		}

		var compEntry=new CTATComponentDescription ();
		compEntry.type=aType;
		compEntry.name=aName;
		compEntry.x=Math.floor (x);
		compEntry.y=Math.floor (y);
		compEntry.tabIndex=tabIndex;
		compEntry.zIndex=zIndex;
		compEntry.width=Math.floor (width);
		compEntry.height=Math.floor (height);

		components.push (compEntry);

		//var serializedProps=aComponentElement.childNodes;
		var serializedProps=messageParser.getElementChildren (aComponentElement);

		for (var i=0;i<serializedProps.length;i++)
		{
			var tempElement=serializedProps [i];

			//>------------------------------------------------------------------------

			//if (tempElement.nodeName=="SAIs")
			if (messageParser.getElementName (tempElement)=="SAIs")
			{
				// We currently don't do anything with this yet
			}

			//>------------------------------------------------------------------------

			//if (tempElement.nodeName=="Parameters")
			if (messageParser.getElementName (tempElement)=="Parameters")
			{
				this.ctatdebug ("Processing component parameters ...");

				//var paramProps=tempElement.childNodes [0].childNodes;
				var tempProps=messageParser.getElementChildren (tempElement);

				var tempProp=tempProps [0];

				var paramProps=messageParser.getElementChildren (tempProp);

				for (var t=0;t<paramProps.length;t++)
				{
					var paramProperty=paramProps [t];

					//if (paramProperty.nodeName=="CTATComponentParameter")
					if (messageParser.getElementName (paramProperty)=="CTATComponentParameter")
					{
						this.ctatdebug ("Processing parameter property (CTATComponentParameter) ...");

						var aParam=new CTATParameter();

						//var paramValues=paramProperty.childNodes;

						var paramValues=messageParser.getElementChildren (paramProperty);

						for (var j=0;j<paramValues.length;j++)
						{
							var paramElement=paramValues [j];

							//if (paramElement.nodeName=="name")
							if (messageParser.getElementName (paramElement)=="name")
							{
								this.ctatdebug ("Found parameter name: " + messageParser.getNodeTextValue (paramElement));
								aParam.paramName=messageParser.getNodeTextValue (paramElement);
							}

							//if (paramElement.nodeName=="value")
							if (messageParser.getElementName (paramElement)=="value")
							{
								this.ctatdebug ("Found parameter value: " + messageParser.getNodeTextValue (paramElement));

								aParam.paramValue=messageParser.getNodeTextValue (paramElement).trim();

								if(aParam.paramName=="group")
								{
									compEntry.groupName=aParam.paramValue;
								}
							}
						}

						this.ctatdebug ("Parameter name: " + aParam.paramName + ", value: " + aParam.paramValue);
						compEntry.params.push(aParam);
					}
				}
			}

			//>------------------------------------------------------------------------

			//if (tempElement.nodeName=="Styles")
			if (messageParser.getElementName (tempElement)=="Styles")
			{
				this.ctatdebug ("Processing component styles ...");

				//var stylesProps=tempElement.childNodes [0].childNodes;

				var tempProps=messageParser.getElementChildren (tempElement);

				var tempProp=tempProps [0];

				var stylesProps=messageParser.getElementChildren (tempProp);

				for (var t=0;t<stylesProps.length;t++)
				{
					var styleProperty=stylesProps [t];

					//if (styleProperty.nodeName=="CTATStyleProperty")
					if (messageParser.getElementName (styleProperty)=="CTATStyleProperty")
					{
						this.ctatdebug ("Processing style property (CTATStyleProperty) ...");

						//alert(xml_to_string(styleProperty));

						var aStyle=new CTATStyle ();

						//var styleValues=styleProperty.childNodes;

						var styleValues=messageParser.getElementChildren (styleProperty);

						for (var j=0;j<styleValues.length;j++)
						{
							var styleElement=styleValues [j];

							//alert(xml_to_string(styleElement));

							//if (styleElement.nodeName=="name")
							if (messageParser.getElementName (styleElement)=="name")
							{
								//this.ctatdebug ("Found style name: " + messageParser.getNodeTextValue (styleElement));
								aStyle.styleName=messageParser.getNodeTextValue (styleElement);
							}

							//if (styleElement.nodeName=="value")
							if (messageParser.getElementName (styleElement)=="value")
							{
								//this.ctatdebug ("Found style value: " + messageParser.getNodeTextValue (styleElement));

								aStyle.styleValue=messageParser.getNodeTextValue (styleElement).trim();

								/* code was as follows, but && was ||, so "if(... || ...)" was effectively "if(true)"

								if((aStyle.styleName != "DisplayHTMLText") && (aStyle.styleName != "ProcessHTMLInHints"))
								{
									aStyle.styleValue=messageParser.getNodeTextValue (styleElement).trim();
								}
								else
								{
									//this.ctatdebug ("Running a straight trim on incoming value ("+styleElement+") ...");

									var encoder=new CTATHTMLManager();
									aStyle.styleValue=encoder.htmlEncode(styleElement).trim();
								}
								*/
							}
						}

						this.ctatdebug ("Style (name: " + aStyle.styleName + ", value: " + aStyle.styleValue + ")");

						compEntry.styles.push(aStyle);
					}
				}
			}

			//>------------------------------------------------------------------------
		}

		pointer.ctatdebug ("deserializeComponent ("+aType+","+aName+") done");
	};
	/**
	 *
	 */
	this.displayComponentList=function displayComponentList ()
	{
		pointer.ctatdebug ("displayComponentList ("+components.length+")");

		for (var i=0;i<components.length;i++)
		{
			var aDesc=components [i];

			pointer.ctatdebug ("Component (Desc): " + aDesc.name + ", type: " + aDesc.type+", x: " + aDesc.x + ", y: " + aDesc.y + ", width: " + aDesc.width + ", height: " + aDesc.height);

			var component=aDesc.getComponentPointer ();

			if (component!=null)
			{
				pointer.ctatdebug ("Component (Pointer): " + component.name + ", type: " + component.type+", x: " + component.x + ", y: " + component.y + ", width: " + component.width + ", height: " + component.height);
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
		{
            return xml_node.xml;
		}
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
    	pointer.ctatdebug ("processStartStateActions ("+startStateMessages.length+")");

		for (var i=0;i<startStateMessages.length;i++)
		{
			var aMessage=startStateMessages [i];

			pointer.ctatdebug ("Processing startstate message type: " + aMessage.getMessageType());

			if (aMessage.getMessageType()=="InterfaceAction")
			{
				messageHandler.processInterfaceAction (aMessage);
			}

			if (aMessage.getMessageType()=="CorrectAction")
			{
				messageHandler.processCorrectAction (aMessage);
			}

			if (aMessage.getMessageType()=="InCorrectAction")
			{
				messageHandler.processInCorrectAction (aMessage);
			}

			this.sleep (startstatedelay);
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

	/**
	*
	*/
	this.sleep=function sleep(milliseconds)
	{
		pointer.ctatdebug ("sleep ("+milliseconds+")");

		return; // let's not do that for now

		var start = new Date().getTime();
		for (var i = 0; i < 1e7; i++)
		{
			if ((new Date().getTime() - start) > milliseconds)
			{
				break;
			}
		}
	};
}

CTATMessageHandler.prototype = Object.create(CTATBase.prototype);
CTATMessageHandler.prototype.constructor = CTATMessageHandler;
