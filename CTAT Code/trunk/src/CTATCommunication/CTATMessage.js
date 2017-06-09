/**-----------------------------------------------------------------------------
 $Author: sewall $
 $Date: 2017-02-09 14:03:34 -0600 (週四, 09 二月 2017) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATCommunication/CTATMessage.js $
 $Revision: 24583 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */

goog.provide('CTATMessage');
goog.require('CTATBase');
goog.require('CTATGlobalFunctions');
goog.require('CTATGuid');
goog.require('CTATSAI');
goog.require('CTATSkillSet');
goog.require('CTATXML');
goog.require('CTATJSON');

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
CTATMessage = function(aMessage)
{
	CTATBase.call(this, "CTATMessage","message");

	/**
	 * Create a unique string suitable for a getTransactionId() value.
	 * @return CTATGuid.guid()
	 */
	CTATMessage.makeTransactionId = function()
	{
		return CTATGuid.guid();
	};

	// Keep in mind that if the parser is JSON then this object is of type CTATJSONObject
	var messageObj=aMessage;
	var messageProperties=null;
	var messageParser=null;

	if (aMessage == undefined)
	{
		messageObj=null;
	}

	if (CTATConfig.parserType=="xml")
	{
		messageParser=new CTATXML ();
	}
	else
	{
		messageParser=new CTATJSON ();
	}

	if (messageObj) // There might not be one if it's a fabricated message, for example prev and next button assoc rules
	{
		this.ctatdebug ("messageParser "+messageParser+", messageObj "+messageObj);

		if(messageObj.children)
		{
			this.ctatdebug (messageObj.children);
		}
	}

	var gradeResult="ungraded";
	var isLogMessage=false;
	var hassai=false;
	var rawMessage="";
	var messageSkills=new CTATSkillSet ();

	var messageType="";
	var transactionID="";
	var toolSelection="";

	var successMsg="";
	var buggyMsg="";
	var highlightmessage="";

	var url;

	var sai=null; // CTATSAI
	var studentSAI=null; // CTATSAI

	var customFields=null;  // from AssociatedRules msgs

	/**
	 * See lockTransactionId(String), setTransactionId(String)
	 * Originally belongs to MessageObject.java
	 * @type boolean
	 */
	var isTransactionIdLocked = false;

	/**
	*
	*/
	this.setURL=function setURL (aURL)
	{
		url=aURL;
	};

	/**
	*
	*/
	this.getURL=function getURL ()
	{
		return (url);
	};

	/**
	*
	*/
	this.assignSAI=function assignSAI (anSAI)
	{
		hassai=true;
		sai=anSAI;
	};

	/**
	*
	*/
	this.setTransactionID=function setTransactionID (anID)
	{
		transactionID=anID;
	};
	/**
	*
	*/
	this.getTransactionID=function getTransactionID ()
	{
		return (transactionID);
	};
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
		return (messageObj);
	};
	/**
	*
	*/
	this.getSkillsObject=function getSkillsObject ()
	{
		return (messageSkills);
	};
	/**
	* @return {object} whose property names and values are the custom_field names and values
	*/
	this.getCustomFields=function getCustomFields ()
	{
		return (customFields);
	};
	/**
	*
	*/
	this.getSuccessMessage=function getSuccessMessage ()
	{
		return (successMsg);
	};
	/**
	*
	*/
	this.getBuggyMsg=function getBuggyMsg ()
	{
		return (buggyMsg);
	};
	/**
	*
	*/
	this.getHighlightMsg=function getHighlightMsg ()
	{
		return (highlightmessage);
	};
	/**
	 *
	 */
	this.parse=function parse ()
	{
		this.ctatdebug ("parse ()");

		if (messageObj===null)
		{
			return;
		}

		this.ctatdebug ("messageParser: " + messageParser);
		this.ctatdebug ("Root name: " + messageParser.getElementName (messageObj));

		if (messageParser.getElementName(messageObj)=="tool_message")
		{
			this.ctatdebug ("Detected tool message");
			messageType="tool_message";
		}
		else
		{
			if (messageParser.getElementName (messageObj) != "message")
			{
				this.ctatdebug ("Detected log message");

				isLogMessage = true;
				messageType = messageParser.getElementName (messageObj);
			}
			else
			{
				this.ctatdebug ("Detected regular message");

				isLogMessage = false;

				this.parseMessageType ();
			}
		}

		//useDebugging=true;
		this.parseTransactionID();
		//useDebugging=false;

		//useDebugging=true;
		this.parseSAI();
		//useDebugging=false;

		var tList=messageParser.getElementChildren (messageObj);

		for (var t=0;t<tList.length;t++)
		{
			var entry=tList [t];

			if (messageParser.getElementName (entry)=="properties")
			{
				ctatdebug ("Found a 'properties' element ... ");

				messageProperties=messageParser.getElementChildren (entry);
			}
		}

		this.ctatdebug ("Message " + messageType + ", with transaction id: " + transactionID);
	};
	/**
	 * Depending on format SAI is located in different places
	 * @internal TODO - need to extend this to parse potential ComplexSAIs
	 */
	this.parseSAI=function parseSAI()
	{
		this.ctatdebug ("parseSAI ()");

		var selection="";
		var action="";
		var input="";
		var prompt="";
		var selectionElts=[], actionElts=[], inputElts=[];

		if (messageType=="tool_message")
		{
			this.ctatdebug ("Parsing s, a and i");

			var tList=messageParser.getElementChildren (messageObj);

			for (var t=0;t<tList.length;t++)
			{
				var entry=tList [t];

				if (messageParser.getElementName (entry)=="tool_message")
				{
					var aList=messageParser.getElementChildren (entry);

					for (var w=0;w<aList.length;w++)
					{
						var test=aList [w];

						if (messageParser.getElementName (test)=="event_descriptor")
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
			//toolSelection = messageObj.properties.tool_selection;

			var tList=messageObj.childNodes;

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
							toolSelection=messageParser.getNodeTextValue (test);
					}
				}
			}
		}
		*/

		if(!isLogMessage)
		{
			this.ctatdebug ("!isLogMessage ...");

			/*
			selection = messageObj.properties.Selection.value;
			action = messageObj.properties.Action.value;
			input = messageObj.properties.Input.value;
			prompt = messageObj.properties.prompt;

			if (hasProperty("StudentSelection"))
			{
				this.ctatdebug ("hasProperty(StudentSelection) ...");

				var studentSelection = messageObj.properties.StudentSelection.value;
				var studentAction= messageObj.properties.StudentAction.value;
				var studentInput = messageObj.properties.StudentInput.value;
				studentSAI = new CTATSAI (studentSelection, studentAction, studentInput,prompt);
			}
			*/

			var lList=messageParser.getElementChildren (messageObj);

			for (var l=0;l<lList.length;l++)
			{
				var lentry=lList [l];

				if (messageParser.getElementName (lentry)=="properties")
				{
					var laList=messageParser.getElementChildren (lentry);

					for (var lw=0;lw<laList.length;lw++)
					{
						var ltest=laList [lw];
						var eltName=messageParser.getElementName (ltest);

						this.ctatdebug ("Nodename: " + eltName);

						switch(eltName)
						{
						case "URL":
							url=messageParser.getNodeTextValue (ltest);
							break;

						case "Skills":
							//console.log ("Found skills info in message, parsing ...");
							messageSkills.parseByValue (ltest);
							break;

						case "SuccessMsg":
							successMsg=selection=messageParser.getNodeTextValue (ltest);
							break;

						case "BuggyMsg":
							buggyMsg=messageParser.getNodeTextValue (ltest);
							break;

						case "Selection":
							selection=messageParser.getNodeTextValue (ltest);
							selectionElts.push(ltest);
							break;

						case "Action":
							action=messageParser.getNodeTextValue (ltest);
							actionElts.push(ltest);
							break;

						case "Input":
							input=messageParser.getNodeTextValue (ltest);
							inputElts.push(ltest);
							break;

						case "prompt":
							prompt=messageParser.getNodeTextValue (ltest);
							break;

						case "HighlightMsgText":
							highlightmessage=messageParser.getNodeTextValue (ltest);
							break;

						case "custom_fields":
							customFields=parseCustomFields (ltest);
							break;
						}
					}
				}
			}

			this.ctatdebug ("SAI: " + selection + "," + action + "," + input);
		}
		else
		{
			this.ctatdebug ("isLogMessage ...");

			/*
			selection = messageObj.event_descriptor.selection;
			action = messageObj.event_descriptor.action;
			input = messageObj.event_descriptor.input;
			prompt = messageObj.event_descriptor.prompt;
			*/

			var dList=messageParser.getElementChildren (messageObj);

			for (var d=0;d<dList.length;d++)
			{
				var dentry=dList [d];

				if (messageParser.getElementName (dentry)=="event_descriptor")
				{
					var daList=messageParser.getElementChildren (dentry);

					for (var dw=0;dw<daList.length;dw++)
					{
						var dtest=daList [dw];

						if (messageParser.getElementName (dtest)=="selection")
						{
							selection=messageParser.getNodeTextValue (dtest);
							selectionElts.push(dtest);
						}

						if (messageParser.getElementName (dtest)=="action")
						{
							action=messageParser.getNodeTextValue (dtest);
							actionElts.push(dtest);
						}

						if (messageParser.getElementName (dtest)=="input")
						{
							input=messageParser.getNodeTextValue (dtest);
							inputElts.push(dtest);
						}

						if (messageParser.getElementName (dtest)=="prompt")
						{
							prompt=messageParser.getNodeTextValue (dtest);
						}
					}
				}
			}

			this.ctatdebug ("SAI: " + selection + "," + action + "," + input);
		}

		if ((selection !== "") && (selection !== null))
		{
			//this.ctatdebug ("Creating new SAI object with ("+selection+","+action+","+input+","+prompt+"), activating new parsing ...");

			sai=new CTATSAI (selection,action,input,prompt);

			sai.setArrayFromElements(selectionElts);
			sai.setArrayFromElements(actionElts);
			sai.setArrayFromElements(inputElts);

			//sai=new CTATSAI ();
			//sai.fromXMLInternal (messageObj.properties);

			/*
			var tList=messageObj.childNodes;

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
			this.ctatdebug ("No SAI found");
			hassai = false;
		}

		this.ctatdebug ("parseSAI () done, hassai: " + hassai + ", ("+selection+","+action+","+input+","+prompt+")");
	};

	/**
	 * Depending on format TransactionID is located in different places.
	 */
	this.parseTransactionID=function parseTransactionID()
	{
		this.ctatdebug ("parseTransactionID()");

		if (messageType=="tool_message")
		{
			//transactionID = messageObj.tool_message.semantic_event.@transaction_id;

			//this.ctatdebug ("Transaction ID: " + transactionID);

			return;
		}

		if (!isLogMessage)
		{
			var tList=messageParser.getElementChildren (messageObj);

			for (var t=0;t<tList.length;t++)
			{
				var entry=tList [t];

				if (messageParser.getElementName (entry)=="properties")
				{
					//this.ctatdebug ("Found 'properties' marker, going one level deeper ...");

					var aList=messageParser.getElementChildren (entry);

					for (var w=0;w<aList.length;w++)
					{
						var test=aList [w];

						var nameCheck=messageParser.getElementName (test);

						//this.ctatdebug ("Name check: " + nameCheck);

						if (nameCheck=="transaction_id")
						{
							transactionID=messageParser.getNodeTextValue (test);
						}
					}
				}
			}
		}
		else
		{
			/*
			if (messageObj.semantic_event != null)
			{
				//transactionID = messageObj.semantic_event.@transaction_id;
			}
			*/
		}

		this.ctatdebug ("parseTransactionID() done, id: " + transactionID);
	};

	/**
	 * Parse the custom_fields element into an object whose properties are the field names.
	 * @param {Object} cfParentElt top-level element from message
	 * @return {Object} with 1 property for each custom_field name element
	 */
	function parseCustomFields(cfParentElt)
	{
		var result = {};
		var cfElts = messageParser.getElementChildren (cfParentElt);
		this.ctatdebug ("CTATMessage.parseCustomFields("+messageParser.stringify(cfParentElt)+"): cfElts.length "+cfElts.length);
		for(var i = 0; i < cfElts.length; ++i)
		{
			var cfElt = cfElts[i];
			if(messageParser.getElementName (cfElt) != "custom_field")
			{
				//console.log ("CTATMessage.parseCustomFields(): unexpected element name "+messageParser.getElementName(cfElt)+" at custom_fields["+i+"]");
				continue;
			}
			var name = null, value = null;
			var cfEltChildren = messageParser.getElementChildren (cfElt);
			for(var j = 0, nf = 0; j < cfEltChildren.length && nf < 2; ++j)
			{
				var cfEltChild = cfEltChildren[j];
				switch(messageParser.getElementName (cfEltChild))
				{
				case "name":
					name = messageParser.getNodeTextValue (cfEltChild);
					nf++;
					break;
				case "value":
					value = messageParser.getNodeTextValue (cfEltChild);
					nf++;
					break;
				default:
					//console.log ("CTATMessage.parseCustomFields(): unexpected element name "+messageParser.getElementName(cfEltChild)+" within custom_field["+i+"]");
					break;
				}
			}
			if(!name)
			{
				continue;
			}
			result[name] = (value ? value : "");
			this.ctatdebug ("CTATMessage.parseCustomFields(): result["+name+"]="+result[name]+";");
		}
		return result;
	};


	/**
	 *
	 */
	this.parseMessageType=function parseMessageType ()
	{
		this.ctatdebug ("parseMessageType()");

		var tList=messageParser.getElementChildren (messageObj); // This means it starts at the root again

		//ctatdebug ("Message type length: " + tList.length);

		for (var t=0;t<tList.length;t++)
		{
			var entry=tList [t];

			//this.ctatdebug ("check name: " + messageParser.getElementName (entry));

			if (messageParser.getElementName (entry)=="properties")
			{
				var aList=messageParser.getElementChildren (entry);

				//this.ctatdebug ("BINGO: " + aList.length);

				for (var w=0;w<aList.length;w++)
				{
					var test=aList [w];

					//this.ctatdebug ("Pre: BINGO 2: " + test.name);

					if (messageParser.getElementName (test)=="MessageType")
					{
						//this.ctatdebug ("BINGO 2");

						messageType=messageParser.getNodeTextValue (test);
					}
				}
			}
		}

		this.ctatdebug ("parseMessageType() -> " + messageType);
	};

	/**
	 * Returns the MessageType element of the message.
	 * <p>This is by far the most commonly used property of any CTATMessage as it governs determines the format of the message
	 * and provides what other fields can be expected to be available. A class will exist at some point that contains static
	 * variables for the possible values of this field.</p>
	 * @see CTATMessageType
	 * @return	The type of the CTATMessage.
	 */
	this.getMessageType = function()
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
	 * Returns the array of all Selection values of this message's SAI, if is has an SAI.
	 * @return {Array<string>} result of getSelectionArray() from this message's SAI, if present; else an empty array
	 */
	this.getSelectionArray=function getSelectionArray()
	{
		return (hassai ? sai.getSelectionArray() : []);
	};

	/**
	 * Returns the array all of Action values of this message's SAI, if is has an SAI.
	 * @return {Array<string>} result of getActionArray() from this message's SAI, if present; else an empty array
	 */
	this.getActionArray=function getActionArray()
	{
		return (hassai ? sai.getActionArray() : []);
	};

	/**
	 * Returns the array all of Input values of this message's SAI, if is has an SAI.
	 * @return {Array<string>} result of getInputArray() from this message's SAI, if present; else an empty array
	 */
	this.getInputArray=function getInputArray()
	{
		return (hassai ? sai.getInputArray() : []);
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
		return messageObj;
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
        if (messageObj.xml)
            return messageObj.xml;
        else if (XMLSerializer)
        {
            var xml_serializer = new XMLSerializer();
            return xml_serializer.serializeToString(messageObj);
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
		{
			return "";
		}

		return (this.getProperty ("Indicator"));
	};

	/**
	 *
	 */
	this.getIndicatorSub=function getIndicatorSub ()
	{
		if (messageType != "AssociatedRules")
		{
			return "";
		}

		return (this.getProperty ("IndicatorSub"));
	};

	/**
	 *
	 */
	this.setProperty=function setProperty(property,value)
	{
		this.ctatdebug ("setProperty ("+property+","+value+")");

		if (messageProperties!==null)
		{
			for (var w=0;w<messageProperties.length;w++)
			{
				var test=messageProperties [w];

				if (messageParser.getElementName (test)==property)
				{
					// return (messageParser.getNodeTextValue (test));

					// messageObj.properties [property]=value;
				}
			}
		}
		else
		{
			this.ctatdebug ("Internal error: no messageProperties object available");
		}
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
		if (isLogMessage)
		{
			return "";
		}
		else
		{
			//return messageObj.properties[property];

			if (messageProperties!==null)
			{
				for (var w=0;w<messageProperties.length;w++)
				{
					var test=messageProperties [w];

					if (messageParser.getElementName (test)==property)
					{
						return (messageParser.getNodeTextValue (test));
					}
				}
			}
			else
			{
				this.ctatdebug ("Internal error: no messageProperties object available");
			}
		}

		return ("");
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
		//listProperties (messageObj);

		//return (!isLogMessage && messageObj.properties[property] != null && messageObj.properties[property] != null);

		var prop=this.getProperty (property);

		return (!isLogMessage && (prop!==""));
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
		var trans=this.getProperty ("end_of_transaction");

		if (trans=="true")
		{
			return (true);
		}

		return (false);

		//return hasProperty("end_of_transaction") && messageObj.properties.end_of_transaction == "true";
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
		return studentSAI !== null;
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
	transactionID=CTATMessage.makeTransactionId();

	// Now, on with the parsing!!

	this.parse ();

/*********************** ORIGINALLY BELONGS TO MESSAGEOBJECT IN JAVA ************************************/
/* LastModify: FranceskaXhakaj 11/14*/

/**
	 * Initialize the root, verb and properties elements from scratch.
	 * @param {String} messageType
	 * @return undefined
	 */
	this.init = function(givMessageType)
	{
		//will not have text, root or verbs
		//makeProperties(null); // removed as it is not defined anywhere
		this.setMessageType(givMessageType);
	};

	/**
	 * @param {array of Strings} selection will copy before saving
	 * @return undefined
	 */
	this.setSelection = function (selection)
	{
		if (sai)
		{
			sai.setSelection (selection);
		}

		this.setProperty('SELECTION', selection);
	};

	/**
	 * @param {array of Strings} selection will copy before saving
	 * @return undefined
	 */
	this.setAction = function (action)
	{
		if (sai)
		{
			sai.setAction (action);
		}

		this.setProperty('ACTION', action);
	};

	/**
	 * @param {array of Strings} selection will copy before saving
	 * @return undefined
	 */
	this.setInput = function (input)
	{
		if (sai)
		{
			sai.setInput (input);
		}

		this.setProperty('INPUT', input);
	};

	/**
	 * Semantic event identifier of linked event. This call sets isTransactionIdLocked to
	 * prevent setTransactionId(String) from changing the value later.
	 * @param {String} id new value for getTransactionId()
	 * @return undefined
	 */
	this.lockTransactionId = function(id)
	{
		if(id === null || typeof(id) === 'undefined' || id.length < 1)
		{
			throw new CTATExampleTracerException("lockTranactionId() argument "+id+" must be a valid id");
		}

		this.setProperty(CTATMessage.TRANSACTION_ID_TAG,id);
		isTransactionIdLocked = true;
	};

    this.setMessageType = function(givMessageType)
    {
    	this.setProperty(CTATMessage.MESSAGE_TYPE, givMessageType);

    	messageType = givMessageType;

    	//not using text
    };

	/**
	 * Semantic event identifier of linked event. No-op if isTransactionIdLocked is true.
	 * @param {String} id new value for getTransactionId()
	 */
    this.setTransactionId = function(id)
    {
    	if(isTransactionIdLocked)
    	{
    		return;
    	}

    	if(id === null || typeof(id) === 'undefined' || id.length < 1)
    	{
    		id = CTATMessage.makeTransactionId();
    	}

    	this.setProperty(CTATMessage.TRANSACTION_ID_TAG, id);
    };

/****************************** STATIC METHODS ****************************************************/

	/**
	 * Create with default verb DEFAULT_VERB and given CTATMessageType.
	 * @param {String} givMessageType
	 * @param {String} verb
	 * @return {CTATMessageObject} new instance
	 */
	CTATMessage.create = function(givMessageType, verb) // Does not work, unused.
	{
		var result = new CTATMessage();

		result.init(givMessageType);

		//not working with verbs

		return result;
	};

};

/****************************** CONSTANTS ****************************************************/

/** properties child tag. Message type used for dispatching.
 * type String
 */
Object.defineProperty(CTATMessage, "MESSAGE_TYPE", {enumerable: false, configurable: false, writable: false, value: "MessageType"});

/** Transaction identifier links messages in a common tutoring request-response action.
 * type String
 */
Object.defineProperty(CTATMessage, "TRANSACTION_ID_TAG", {enumerable: false, configurable: false, writable: false, value: "transaction_id"});

CTATMessage.prototype = Object.create(CTATBase.prototype);
CTATMessage.prototype.constructor = CTATMessage;

if(typeof module !== 'undefined')
{
    module.exports = CTATMessage;
}
