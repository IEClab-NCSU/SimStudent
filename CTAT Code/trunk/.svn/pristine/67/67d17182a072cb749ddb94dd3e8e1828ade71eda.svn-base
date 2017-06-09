var fio = require('fs');
var DOMParser = require('xmldom').DOMParser;
var XML = require('xmldom');
var doc;
var callback;

function parseXML(data,callback)
{
var doc = new DOMParser().parseFromString(data);
return doc.documentElement;
//callback(doc.documentElement);
}




function getElementName(anElement)
{
return anElement.nodeName;
}

function getElementValue(anElement)
{
return anElement.nodeValue;
}

function getElementChildren (anElement)
{
	return (anElement.childNodes);
}

function getNodeTextValue (aNode)
{
	if (aNode==null)
	{
		console.log("Node argument is null");
		return ("");
	}
	
	if (aNode.childNodes==null)
	{
		console.log("Node does not have any children");
		return ("");
	}	
	
	if (aNode.childNodes.length==0)
	{
		console.log("Node has children size of 0");
		return ("");
	}	
	
	console.log("First do a check to see if it has a 'value' sub element");
	
	var entries=aNode.childNodes;
	for (var t=0;t<entries.length;t++)
	{
		var entry=entries [t];
		
		if ((entry.nodeName=="value") || (entry.nodeName=="Value"))
		{
			if(entry.childNodes.length==1)
			{
				console.log("Data: ("+entry.childNodes[0].nodeName+")" + entry.childNodes[0].nodeValue);
							
				return (entry.childNodes[0].nodeValue);
			}
			else
			{
				console.log("Data: ("+entry.childNodes[1].nodeName+")" + entry.childNodes[1].nodeValue);
				
				return (entry.childNodes[1].nodeValue);					
			}
		}
	}	

	console.log("Bottoming out");
	
	return (aNode.childNodes[0].nodeValue);
};

function xmlToString(xmlData) 
	{	
//		/this.ctatdebug ("xmlToString ()");
		
		if (xmlData==null)
		{
			this.ctatdebug ("Error: xml data is null");
			return (null);
		}
	
		var xmlString = new XML.XMLSerializer().serializeToString(xmlData);
		
		return xmlString;
	};


exports.parseXML = parseXML;
exports.getElementName = getElementName;
exports.getElementValue = getElementValue;
exports.getElementChildren = getElementChildren;
exports.xmlToString = xmlToString;
