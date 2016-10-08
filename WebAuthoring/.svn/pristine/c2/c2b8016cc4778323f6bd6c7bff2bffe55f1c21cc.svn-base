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
 
 http://stackoverflow.com/questions/22512097/parsexml-returns-invalid-xml-in-google-apps-script
 
 ------------------------------------------------------------------------------------
*/

/**
 * 
 */
function CTATXML () 
{	
	CTATBase.call(this, "CTATXML","xml");
	
	/**
	 * 
	 * @returns
	 */
	this.parseXML=function parseXML (aMessage)
	{
		this.debug ("parseXML ()");
		
		//this.debug ("message: " + aMessage);
				
		var xmlDoc=null;
						
		try
		{
			xmlDoc = $.parseXML(aMessage);
		}
		catch (err)
		{
			if (xmlDoc!=null)
			{
				this.debug ("JQuery could not process the provided XML: " + err.message + " ("+xmlDoc.parseError.errorCode+") ("+xmlDoc.parseError.reason + ") (" + xmlDoc.parseError.line + ")");
			}
			else
			{
				this.debug ("JQuery could not process the provided XML (xmlDoc==null): " + err.message);
			}			
			
			return (null);		
		}
		
		this.debug ("Parsing complete, checking and converting ...");
		
		if (xmlDoc==null)
		{
			this.debug ("Unspecified error parsing xml message. xmlDoc is null");
			
			return (null);
		}
				 
		$xml=$(xmlDoc);
		
		this.debug ("parseXML () done");
				
		return (xmlDoc.documentElement);
	};	
	/**
	*
	*/
	this.getElementName=function getElementName (anElement)
	{
		return (anElement.nodeName);
	};
	/**
	*
	*/
	this.getElementValue=function getElementValue (anElement)
	{
		return (anElement.nodeValue);
	};	
	/**
	*
	*/
	this.getElementChildren=function getElementChildren (anElement)
	{
		return (anElement.childNodes);
	};	
	/**
	 * This method can handle the following cases:
	 * 
	 *	<Action>UpdateTextField</Action> 
	 * 
	 *	<Action>
	 *		<value>UpdateTextField</value>
	 *	</Action>
	 */
	this.getNodeTextValue=function getNodeTextValue (aNode)
	{
		//this.debug ("getNodeTextValue ()");
	
		if (aNode==null)
		{
			//this.debug ("Node argument is null");
			return ("");
		}
		
		if (aNode.childNodes==null)
		{
			//this.debug ("Node does not have any children");
			return (aNode.nodeValue);
		}	
		
		if (aNode.childNodes.length==0)
		{
			//this.debug ("Node has children size of 0");
			return ("");
		}	
		
		//this.debug ("First do a check to see if it has a 'value' sub element");
		
		var entries=aNode.childNodes;

		for (var t=0;t<entries.length;t++)
		{
			var entry=entries [t];
			
			if ((entry.nodeName=="value") || (entry.nodeName=="Value"))
			{
				if(entry.childNodes.length==1)
				{
					//this.debug ("Data: ("+entry.childNodes[0].nodeName+")" + entry.childNodes[0].nodeValue);
								
					return (entry.childNodes[0].nodeValue);
				}
				else
				{
					//this.debug ("Data: ("+entry.childNodes[1].nodeName+")" + entry.childNodes[1].nodeValue);
					
					return (entry.childNodes[1].nodeValue);					
				}
			}
		}	
		
		//this.debug ("Bottoming out ...");
		
		return (aNode.childNodes[0].nodeValue);
	};
	
	/**
	*
	*/
	this.xmlToString=function xmlToString(xmlData) 
	{	
		this.debug ("xmlToString ()");
		
		if (xmlData==null)
		{
			this.debug ("Error: xml data is null");
			return (null);
		}
	
		var xmlString=null;
		
		//IE
		if (window.ActiveXObject)
		{
			xmlString = xmlData.xml;
		}
		// code for Mozilla, Firefox, Opera, etc.
		else
		{
			xmlString = (new XMLSerializer()).serializeToString(xmlData);
		}
		return xmlString;
	};
}	

CTATXML.prototype = Object.create(CTATBase.prototype);
CTATXML.prototype.constructor = CTATXML;
