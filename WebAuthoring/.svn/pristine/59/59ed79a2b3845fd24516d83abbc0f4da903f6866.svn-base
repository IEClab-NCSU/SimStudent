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
 * 
 */
function CTATXML () 
{	
	CTATBase.call(this, "CTATXML","xml");
	
	/**
	 * https://developers.google.com/apps-script/articles/XML_tutorial
	 * @returns
	 */
	this.parseXML=function parseXML (aMessage)
	{
		this.debug ("parseXML ()");
		
		this.debug ("message: " + aMessage);
		
		var document = XmlService.parse(xml);
		
		if (document==null)
		{
			this.debug ("Error parsing message");
			return (null);
		}

		var root = document.getRootElement();
				
		return (root);
	};
	/**
	*
	*/
	this.getElementName=function getElementName (anElement)
	{
		return (anElement.getName ());
	};
	/**
	*
	*/
	this.getElementValue=function getElementValue (anElement)
	{
		return (anElement.getValue ());
	};	
	/**
	*
	*/
	this.getElementChildren=function getElementChildren (anElement)
	{
		return (anElement.getChildren ());
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
		if (aNode==null)
		{
			this.debug ("Node argument is null");
			return ("");
		}
		
		if (aNode.getChildren()==null)
		{
			this.debug ("Node does not have any children");
			return ("");
		}	
		
		if (aNode.getChildren().length==0)
		{
			this.debug ("Node has children size of 0");
			return ("");
		}	
		
		this.debug ("First do a check to see if it has a 'value' sub element");
		
		var entries=aNode.getChildren();

		for (var t=0;t<entries.length;t++)
		{
			var entry=entries [t];
			
			if ((entry.getName ()=="value") || (entry.getName ()=="Value"))
			{
				if(entry.getChildren().length==1)
				{
					//this.debug ("Data: ("+entry.childNodes[0].nodeName+")" + entry.childNodes[0].nodeValue);
								
					return (entry.getChildren()[0].getValue ());
				}
				else
				{
					//this.debug ("Data: ("+entry.childNodes[1].nodeName+")" + entry.childNodes[1].nodeValue);
					
					return (entry.getChildren()[1].getValue ());
				}
			}
		}	
		
		this.debug ("Bottoming out ...");
		
		return (aNode.getChildren()[0].getValue ());
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
	
        var output = XmlService.getPrettyFormat().format(xmlData);
      
		return (output);
	};
}	

CTATXML.prototype = Object.create(CTATBase.prototype);
CTATXML.prototype.constructor = CTATXML;
