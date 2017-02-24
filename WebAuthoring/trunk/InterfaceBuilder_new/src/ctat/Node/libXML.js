var fio = require('fs');
var DOMParser = require('xmldom').DOMParser;
var doc;
var callback;


function parseXMLfromFile(filename,fn)
{
fio.readFile('test.brd','utf8', parseXML);
callback = fn;
}



function parseXML(err,data,callback)
{
var doc = new DOMParser().parseFromString(data);
callback(doc.documentElement);
}


function processXML(doc)
{
this.abc = "Hello world";
this.XMLDOC = doc.documentElement;
callback(this);
}



var getElementName = function getElementName(anElement)
{
return anElement.nodeName;
}

var getElementValue=function getElementValue (anElement)
{
return anElement.nodeValue;
}

var getElementChildren=function getElementChildren (anElement)
{
	return (anElement.childNodes);
}

var getNodeTextValue=function getNodeTextValue (aNode)
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

var xmlToString=function xmlToString(xmlData) 
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


exports.parseXML = parseXML;
exports.getElementName = getElementName;
exports.getElementValue = getElementValue;
exports.getElementChildren = getElementChildren;
exports.xmlToString = xmlToString;
