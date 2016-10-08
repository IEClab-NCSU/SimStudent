/**------------------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

/**
 * CTATMessage is used as an accessible representation of CTAT's message format.
 * <p>CTATMessage is mainly used to parse and manipulate messages that the interface 
 * receives from the TutoringService but it also maintains functionality to work with 
 * the DataShop log message format as well. The provided accessors do <i>not</i> represent 
 * an exhaustive list of all the possible fields a message can have, merely the most 
 * commonly used ones. Any field that is not given a direct accessor can be retrieved 
 * by working directly with the underlying <code>XML</code> object, though this would 
 * require a prior knowledge of the message's format, which is not very well documented 
 * at this time.</p>
 */
function CTATMessage (aMessage) 
{	
	CTATBase.call(this, "CTATMessage","message");
	
	var gradeResult="ungraded";
	var isLogMessage=false;
	var hassai=false;
	var rawMessage="";
	var xmlObj=null;
	
	if (aMessage instanceof String)
	{
		rawMessage=aMessage;
	}
	else
	{
		xmlObj=aMessage;
	}
	
	var messageType="";	
	var transactionID="";
	var toolSelection="";
	
	var sai=null; // CTATSAI
	var studentSAI=null; // CTATSAI
	
	var xmlParser=new CTATXML ();

	/**
	*
	*/
	this.setGradeResult=function setGradeResult (aResult)
	{
		gradeResult=aResult;
	};
	/**
	*
	*/
	this.getGradeResult=function getGradeResult() 
	{
		return gradeResult;
	};	
	/**
	*
	*/
	this.getXMLObject=function getXMLObject ()
	{
		return (xmlObj);
	};
	/**
	*
	*/
	this.parseMessage=function parseMessage (aMessage)
	{
		isLogMessage=false;
		hassai=false;
		rawMessage="";
		xmlObj=null;	
	
		if (aMessage instanceof String)
		{
			rawMessage=aMessage;
		}
		else
		{
			xmlObj=aMessage;
		}
	
		this.parse ();
	}
	/**
	 * 
	 */
	this.parse=function parse ()
	{
		//useDebugging=true;
		
		this.debug ("parse ()");
				
		if (xmlObj==null)
		{
			xmlObj=xmlParser.parseXML (rawMessage);
		}
		else
			this.debug ("Message already pre-parsed, walking the DOM ...");
		
		this.debug ("Root name: " + xmlParser.getElementName (xmlObj));
		
		if (xmlParser.getElementName(xmlObj)=="tool_message")
		{
			this.debug ("Detected tool message");
			messageType="tool_message";
		}
		else
		{
			if (xmlParser.getElementName (xmlObj) != "message") 
			{
				isLogMessage = true;
				messageType = xmlParser.getElementName (xmlObj);
			}
			else 
			{
				isLogMessage = false;
			
				this.parseMessageType ();
			}
		}
				
		this.parseTransactionID();
							
		this.parseSAI();
		
		this.debug ("Message " + messageType + ", with transaction id: " + transactionID);
		
		//useDebugging=false;
	};
	/**
	 * Depending on format SAI is located in different places
	 * @internal TODO - need to extend this to parse potential ComplexSAIs
	 */
	this.parseSAI=function parseSAI() 
	{
		this.debug ("parseSAI ()");
		
		var selection="";
		var action="";
		var input="";
		var prompt="";
					
		if (messageType=="tool_message")
		{
			this.debug ("Parsing s, a and i");

			var tList=xmlParser.getElementChildren (xmlObj);
			
			for (var t=0;t<tList.length;t++)
			{
				var entry=tList [t];
				
				if (xmlParser.getElementName (entry)=="tool_message")
				{
					var aList=xmlParser.getElementChildren (entry);
					
					for (var w=0;w<aList.length;w++)
					{
						var test=aList [w];
						
						if (xmlParser.getElementName (test)=="event_descriptor")
						{							
							sai=new CTATSAI ();
							sai.fromXMLInternal (test);							
						}	
					}	
				}
			}			
				
			hassai = true;				
				
			return;
		}
		
		/*
		if (hasProperty("tool_selection"))
		{
			//toolSelection = xmlObj.properties.tool_selection;
			
			var tList=xmlObj.childNodes;
			
			for (var t=0;t<tList.length;t++)
			{
				var entry=tList [t];
				
				if (entry.nodeName=="properties")
				{
					var aList=entry.childNodes;
					
					for (var w=0;w<aList.length;w++)
					{
						var test=aList [w];
						
						if (test.nodeName=="tool_selection")
							toolSelection=xmlParser.getNodeTextValue (test);
					}	
				}
			}						
		}
		*/
			
		if(!isLogMessage) 
		{
			this.debug ("!isLogMessage ...");
				
			/*
			selection = xmlObj.properties.Selection.value;
			action = xmlObj.properties.Action.value;
			input = xmlObj.properties.Input.value;
			prompt = xmlObj.properties.prompt;
				
			if (hasProperty("StudentSelection")) 
			{
				this.debug ("hasProperty(StudentSelection) ...");
				
				var studentSelection = xmlObj.properties.StudentSelection.value;
				var studentAction= xmlObj.properties.StudentAction.value;
				var studentInput = xmlObj.properties.StudentInput.value;
				studentSAI = new CTATSAI (studentSelection, studentAction, studentInput,prompt);
			}
			*/				
			
			var tList=xmlParser.getElementChildren (xmlObj);
			
			for (var t=0;t<tList.length;t++)
			{
				var entry=tList [t];
				
				if (xmlParser.getElementName (entry)=="properties")
				{
					var aList=xmlParser.getElementChildren (entry);
					
					for (var w=0;w<aList.length;w++)
					{
						var test=aList [w];
						
						this.debug ("Nodename: " + xmlParser.getElementName (test));
						
						if (xmlParser.getElementName (test)=="Selection")
						{
							selection=xmlParser.getNodeTextValue (test);
						}
						
						if (xmlParser.getElementName (test)=="Action")
						{
							action=xmlParser.getNodeTextValue (test);
						}
						
						if (xmlParser.getElementName (test)=="Input")
						{
							input=xmlParser.getNodeTextValue (test);
						}
						
						if (xmlParser.getElementName (test)=="prompt")
						{
							prompt=xmlParser.getNodeTextValue (test);
						}						
					}	
				}
			}								
		}
		else 
		{
			this.debug ("isLogMessage ...");
				
			/*
			selection = xmlObj.event_descriptor.selection;
			action = xmlObj.event_descriptor.action;
			input = xmlObj.event_descriptor.input;
			prompt = xmlObj.event_descriptor.prompt;
			*/

			var tList=xmlParser.getElementChildren (xmlObj);
			
			for (var t=0;t<tList.length;t++)
			{
				var entry=tList [t];
				
				if (xmlParser.getElementName (entry)=="event_descriptor")
				{
					var aList=xmlParser.getElementChildren (entry);
					
					for (var w=0;w<aList.length;w++)
					{
						var test=aList [w];
						
						if (xmlParser.getElementName (test)=="selection")
						{
							selection=xmlParser.getNodeTextValue (test);
						}
						
						if (xmlParser.getElementName (test)=="action")
						{
							action=xmlParser.getNodeTextValue (test);
						}
						
						if (xmlParser.getElementName (test)=="input")
						{
							input=xmlParser.getNodeTextValue (test);
						}
						
						if (xmlParser.getElementName (test)=="prompt")
						{
							prompt=xmlParser.getNodeTextValue (test);
						}						
					}	
				}
			}									
		}
			
		if ((selection != "") && (selection != null)) 
		{
			this.debug ("Creating new SAI object with ("+selection+","+action+","+input+","+prompt+"), activating new parsing ...");
			
			sai=new CTATSAI (selection,action,input,prompt);

			//sai=new CTATSAI ();
			//sai.fromXMLInternal (xmlObj.properties);
			
			/*
			var tList=xmlObj.childNodes;
			
			for (var t=0;t<tList.length;t++)
			{
				var entry=tList [t];
				
				if (entry.nodeName=="properties")
				{
					sai=new CTATSAI ();
					sai.fromXMLInternal (entry);	
				}
			}
			*/						
			
			hassai = true;
		}
		else
		{
			this.debug ("No SAI found");
			hassai = false;
		}
				
		this.debug ("parseSAI () done");
	};
		
	/**
	 * Depending on format TransactionID is located in different places.
	 */
	this.parseTransactionID=function parseTransactionID() 
	{
		this.debug ("parseTransactionID()");
			
		if (messageType=="tool_message")
		{				
			//transactionID = xmlObj.tool_message.semantic_event.@transaction_id;		
				
			this.debug ("Transaction ID: " + transactionID);
				
			return;
		}
			
		if (!isLogMessage)
		{				
			var tList=xmlParser.getElementChildren (xmlObj);
			
			for (var t=0;t<tList.length;t++)
			{
				var entry=tList [t];
				
				if (xmlParser.getElementName (entry)=="properties")
				{
					var aList=xmlParser.getElementChildren (entry);
					
					for (var w=0;w<aList.length;w++)
					{
						var test=aList [w];
						
						if (test.nodeName=="transaction_id")
							transactionID=xmlParser.getNodeTextValue (test);
					}	
				}
			}			
		}	
		else
		{	
			/*
			if (xmlObj.semantic_event != null)
			{
				//transactionID = xmlObj.semantic_event.@transaction_id;
			}
			*/			
		}	
	};
	
	/**
	 * 
	 */
	this.parseMessageType=function parseMessageType ()
	{
		this.debug ("parseMessageType()");
		
		var tList=xmlParser.getElementChildren (xmlObj);
		
		for (var t=0;t<tList.length;t++)
		{
			var entry=tList [t];
			
			if (xmlParser.getElementName (entry)=="properties")
			{
				var aList=xmlParser.getElementChildren (entry);
				
				for (var w=0;w<aList.length;w++)
				{
					var test=aList [w];
					
					if (xmlParser.getElementName (test)=="MessageType")
					{
						messageType=xmlParser.getNodeTextValue (test);
					}	
				}	
			}
		}
	};
		
	/**
	 * Returns the MessageType element of the message.
	 * <p>This is by far the most commonly used property of any CTATMessage as it governs determines the format of the message
	 * and provides what other fields can be expected to be available. A class will exist at some point that contains static
	 * variables for the possible values of this field.</p>
	 * @see CTATMessageType
	 * @return	The type of the CTATMessage.
	 */
	this.getMessageType=function getMessageType() 
	{
		return messageType;
	};
		
	/**
	 * Returns the Transaction ID of the message.
	 * <p>Most messages will have a Transaction ID, usually a 16 character GUID. A message sent to the Tutoring Service that has a
	 * transaction ID will recieve a response with an identical ID. The primary use of the ID is for this pairing but it can also 
	 * be used to uniquely identify messages.</p>
	 * @return	The transaction ID of the message.
	 */
	this.getTransactionID=function getTransactionID() 
	{
		return transactionID;
	};
		
	/**
	 * Returns the SAI of the message.
	 * <p>An SAI tripple is used to describe every action within CTAT. The SAI is the central important feature in most message types.
	 * Despite this, not all messages have an SAI element. For messages types that do not have an SAI, this method will return an
	 * SAI whose values are empty strings.</p>
	 * @return	The SAI of the message, or an empty SAI if the message doesn't have one.
	 */
	this.getSAI=function getSAI() 
	{
		return (hassai ? sai : new CTATSAI());
	};
		
	/**
	 * Returns the Selction value of the messages SAI if it has one.
	 * @return	The Selection String of the message's SAI, if it has one, empty String otherwise.
	 */
	this.getSelection=function getSelection() 
	{
		return (hassai ? sai.getSelection() : "");
	};
		
	/**
	 * Returns the Action value of the messages SAI if it has one.
	 * @return	The Action String of the message's SAI, if it has one, empty String otherwise.
	 */
	this.getAction=function getAction() 
	{
		return (hassai ? sai.getAction() : "");
	};
		
	/**
	 * Returns the Input value of the messages SAI if it has one.
	 * @return	The Input String of the message's SAI, if it has one, empty String otherwise.
	 */
	this.getInput=function getInput() 
	{
		return (hassai ? sai.getInput() : "");
	};
	
	/**
	 * Returns the internal <code>XML</code> object used to represent the message.
	 * <p>If you are need a message field that has no simple accessor, i.e. it is further nested <code>XML</code> that cannot be 
	 * reached through the <code>getProperty()</code> method, or the message is a log message, you can access any field
	 * not otherwise provided for by retrieveing the <code>XML</code> object, which the CTATMessage uses internally to store
	 * message content. This will require prior knowledge of the message's xml formatting, which is provided to the best of our
	 * ability in the CTATMessageType class documentation.</p>
	 * @see getProperty()
	 * @return	The internal XML object used to store the message.
	 */
	this.getXML=function getXML() 
	{
		return xmlObj;
	};
		
	/**
	 * Returns the XMLString representation of the message.
	 * <p>The XMLString of the message is equivalent to the String that was passed over the XML socket to or from the tutoring
	 * service. There is an option for whether the string should be pretty printed or not. This setting is behaviourally
	 * equivalent to XML.prettyPrinting</p>
	 * @param	prettyPrinting A boolean for whether you want the string returned to be pretty printed or not.
	 * @return	The XMLString representation of the message.
	 */
	this.getXMLString=function getXMLString(pretty) 
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
	};
		
	/**
	 * Returns the Indicator field of an AssociatedRules Message.
	 * <p>AssociatedRules messages are a common response message type, which carry an extra Indicator field to function like a
	 * MessageSubType. Due to their commodity a direct accessor to this field was implemented. If the message is not an AssociatedRules
	 * type this method will return an empty String.</p>
	 * <p><b>NOTE:</b> It is currently planned for the AssociatedRules message type to become a central response message type. This field
	 * would then absorb the functionality of the current <code>CORRECT</code>, <code>INCORRECT</code>, <code>HIGHLIGHT</code>, 
	 * <code>UNHIGHLIGHT</code>, and <code>SHOW_HINTS</code> message types.</p>
	 * @return The Indicator type of an <code>ASSOCIATED_RULES</code> message, if the message is not <code>ASSOCIATED_RULES</code>, empty string.
	 */
	this.getIndicator=function getIndicator() 
	{
		if (messageType != "AssociatedRules")
			return "";
		else
			return xmlObj.properties.Indicator;
	}
		
	/**
	 * 
	 */
	this.getIndicatorSub=function getIndicatorSub () 
	{
		if (messageType != "AssociatedRules")
			return "";
		else
		{
			if (hasProperty ("IndicatorSub")==true)
				return xmlObj.properties.IndicatorSub;
		}
			
		return "";
	};

	/**
	 *
	 */
	this.setProperty=function setProperty(property,value)
	{
		xmlObj.properties [property]=value;
	};
		
	/**
	 * Returns a given property of a Tutoring Service message.
	 * <p>All messages between the interface the and Tutoring Service contain a list of properties. The accessor methods 
	 * provided in this class are for the most commonly used properties, however, if a message contains a property that is not included 
	 * explicitly it can be accessed here.</p>
	 * <p><b>NOTE:</b> This method is only valid for Tutoring Service messages, if called on a log message it will return an empty string.
	 * If you need to access a field of a log message that is not included use the <code>getXML()</code> method instead</p>
	 * @see		getXML()
	 * @param	property	The name of a property, case-sensative.
	 * @return	The value of the given property.
	 */
	this.getProperty=function getProperty(property) 
	{
		//xmlObj.properties
		if (isLogMessage || !hasProperty(property))
			return "";
		else
			return xmlObj.properties[property];
	};

	/**
	 * Returns whether or not the CTATMessage contains a given property.
	 * <p>Subject to the same message type contraints in getProperty. Checks whether or not the message containts a given property.
	 * If called on a logMessage it will always return <code>false</code>.</p>
	 * <p><b>NOTE:</b> Calling <code>message.hasProperty("SAI")</code> will always return <code>false</code> because messages
	 * never contain a field explicitly named "SAI"</p>
	 * @see 	hasSAI()
	 * @param	property	The name of a property, case-sensative.
	 * @return	<code>true</code> if the message contains the property, <code>false</code> otherwise.
	 */
	this.hasProperty=function hasProperty(property) 
	{
		return (!isLogMessage && xmlObj.properties[property] != null && xmlObj.properties[property] != null);
	};
		
	/**
	 * Returns whether or not the CTATMessage contains an SAI element.
	 * <p>Because not all messages necessarily contain SAI elements this method is provided to check for their presence.</p>
	 * @return	<code>true</code> if the message contains an SAI, <code>false</code> otherwise.
	 */
	this.hasSAI=function hasSAI() 
	{
		return hassai;
	};
		
	/**
	 * Returns whether or not the current message is the end of a two way transaction.
	 * <p>Messages sent to the tutoring service are paired with their response messages. It is also possible that
	 * more than one response is sent for every message. In such a case it can be necessary to check if a particular message
	 * is at the end of a transaction.</p>
	 * @return	<code>true</code> if the message is the end of a transaction, <code>false</code> otherwise.
	 */
	this.isEndOfTransaction=function isEndOfTransaction() 
	{
		return hasProperty("end_of_transaction") && xmlObj.properties.end_of_transaction == "true";
	};

	/**
	 * Returns whether or not the message is in the logging format.
	 * @return <code>true</code> if the message is a log message, <code>false</code> otherwise.
	 */
	this.isLogMessageType=function isLogMessageType() 
	{
		return isLogMessage;
	};

	/**
	 * Returns whether or not the message has student SAI fields.
	 * @return <code>true</code> if the message has a student SAI, <code>false</code> otherwise.
	 */
	this.hasStudentSAI=function hasStudentSAI() 
	{
		return studentSAI != null;
	};

	/**
	 * Returns the selection of the student SAI.
	 * @return
	 */
	this.getStudentSelection=function getStudentSelection() 
	{
		return studentSAI.getSelection();
	};
		
	/**
	 * Returns the action of the student SAI.
	 * @return
	 */
	this.getStudentAction=function getStudentAction() 
	{
		return studentSAI.getAction();
	};
		
	/**
	 * Returns the input of the student SAI
	 * @return
	 */
	this.getStudentInput=function getStudentInput() 
	{
		return studentSAI.getInput();
	};
		
	/**
	 * Returns the full student SAI.
	 * @return
	 */
	this.getStudentSAI=function getStudentSAI() 
	{
		return studentSAI;
	};
		
	/**
	 * 
	 */
	this.getToolSelection=function getToolSelection()
	{
		return toolSelection;
	};		
	
	// Let's fix a transaction id if one doesn't exist
	
	var generator=new CTATGuid ();
	
	transactionID=generator.guid();
	
	// Now, on with the parsing!!
	
	this.parse ();
}

CTATMessage.prototype = Object.create(CTATBase.prototype);
CTATMessage.prototype.constructor = CTATMessage;
