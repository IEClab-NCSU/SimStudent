/**-----------------------------------------------------------------------------
 $Author: sewall $
 $Date: 2016-09-07 10:23:18 -0500 (週三, 07 九月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTAT/CTATJSON.js $
 $Revision: 24129 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATJSON');
goog.require('CTATBase');

function CTATJSONObject (aName,aVal)
{
	this.name = aName || "";
	this.value = aVal || null;
}

var transformArray=[];

/**
 *
 */
CTATJSON = function()
{
	CTATBase.call(this, "CTATJSON","json");

	var JSONObject=null;

	var recursion=5;
	var recursionCounter=0;

	/**
	*
	*/
	this.isJSONObject=function isJSONObject (anObject)
	{
		if (anObject.ctat)
		{
			return (true);
		}

		return (false);
	};

	/**
	 *
	 * @returns
	 */
	this.parse=function parse (aMessage)
	{
		return (this.parseJSON (aMessage));
	};

	/**
	 *
	 * @returns
	 */
	this.parseJSON=function parseJSON (aMessage)
	{
		this.ctatdebug ("parseJSON ()");

		JSONObject=null;

		if ((typeof aMessage) == "string")
		{
			this.ctatdebug ("Parsing JSON as a string ... ");

			try
			{
				JSONObject=JSON.parse(aMessage);
			}
			catch (err)
			{
				this.ctatdebug ("Error parsing JSON message: " + err.message);
				JSONObject=null;
				return (null);
			}

			this.ctatdebug ("Successfully parsed JSON string");
		}

		this.ctatdebug ("JSON string has already been parsed, assigning as an object ...");

		for (var rootElement in JSONObject)
		{
			//if (JSONObject.hasOwnProperty(rootElement))
			//{
				this.ctatdebug ("Creating internal JSON object with name: " + rootElement);

				var rootObject=new CTATJSONObject ();
				rootObject.name=rootElement;
				rootObject.value=JSONObject [rootElement];

				return (rootObject);
			//}
		}

		return (null);
	};
    /**
     * Keep in mind that the input to this methods is or should be
	 * of the type CTATJSONObject
     */
	this.getElementName=function getElementName (anElement)
	{
		if (typeof (anElement)!="object")
		{
			this.ctatdebug ("Internal error, the provided element is not of type CTATJSONObject, instead we found: " + typeof (anElement));
			return (null);
		}

		//this.ctatdebug ("getElementName ("+anElement.name+")");

		return (anElement.name);
	};
    /**
     * Keep in mind that the input to this methods is or should be
	 * of the type CTATJSONObject
     */
	this.getElementValue=function getElementValue (anElement)
	{
		if (anElement==null)
		{
			this.ctatdebug ("Error: anElement is null");
			return (null);
		}

		if (typeof (anElement)!="object")
		{
			this.ctatdebug ("Internal error, the provided element is not of type CTATJSONObject, instead we found: " + typeof (anElement));
			return (null);
		}

		//this.ctatdebug ("getElementValue ("+anElement.name+")");

		return (anElement.value);
	};
	/**
	* Best example to see how we need to map XML handling to JSON handling can be
	* seen when we have to parse a start state bundle. We will receive JSON that
	* looks like this:
	*
	*	{ "StartStateMessages":
	*		{ "message": [
	*
	* The CTATMessageHandler class will get the object representing the root of the
	* JSON object and associate it with the StartStateMessages field. This is already
	* where our interpretation of JSON is different that the usual parsing. The
	* message handler then asks for all children in "StartStateMessages", which
	* effectively means it wants a pointer to [ given the root {
	*/
	this.getElementChildren=function getElementChildren (anElement)
	{
		if (anElement==null)
		{
			this.ctatdebug ("Error: anElement is null");
			return (null);
		}

		if (typeof (anElement)!="object")
		{
			this.ctatdebug ("Internal error, the provided element is not of type CTATJSONObject, instead we found: " + typeof (anElement));
			return (null);
		}

		//this.ctatdebug ("getElementChildren ("+anElement.name+", typeof ("+typeof (anElement.value)+"))");

		//this.walkDOM (anElement.value);

		var transf=null;
		var list=anElement.value;
		var listTransformer=[];

		//this.ctatdebug ("Going into: " + anElement.name);

		for (var test in list)
		{
			//this.ctatdebug ("Check: " + test);

			//if (list.hasOwnProperty(test))
			//{
				var target=list [test];

				if (typeof (target)=="string")
				{
					//this.ctatdebug ("Creating string,string CTATJSONObject for this element -> " + test + " , " + target);

					transf=new CTATJSONObject ();
					transf.name=test;
					transf.value=target;
					listTransformer.push (transf);
				}
				else
				{
					//this.ctatdebug ("Assigning non string object: " + test + "("+target.length+")");

					if ((target [0]==null) || (target [0]==undefined))
					{
						//this.ctatdebug ("Assigning target as is ...");

						transf=new CTATJSONObject ();
						transf.name=test;
						transf.value=target;
						listTransformer.push (transf);
					}
					else
					{
						//this.ctatdebug ("Creating list of " + target.length + " elements, each with name: " + test);

						for (var sub in target)
						{
							transf=new CTATJSONObject ();
							transf.name=test;
							transf.value=target [sub];
							listTransformer.push (transf);
						}
					}


				}
			//}
		}

		return (listTransformer);
	};
    /**
     * Keep in mind that the input to this methods is or should be
	 * of the type CTATJSONObject
     */
	this.getNodeTextValue=function getNodeTextValue (aNode)
	{
		//this.ctatdebug ("getNodeTextValue ("+aNode.name+" -> "+typeof (aNode.value)+")");

		if (typeof (aNode.value)!="string")
		{
			//this.ctatdebug ("The type of the value object in aNode is not a string, going one deeper!");

			var sub=null;

			for (sub in aNode.value)
			{
				//if (aNode.value.hasOwnProperty(sub))
				//{
					//this.ctatdebug ("Returning value " + aNode.value [sub] + " for key: " + sub);
					if (sub=="content")
					{
						//this.ctatdebug ("Found content marker, returning value: "+aNode.value [sub]+", with type: "+typeof (aNode.value [sub])+"...");
						return (aNode.value [sub].toString());
					}
				//}
			}

			// The content wasn't encoded using the convention "content": "....", so let's see if
			// perphaps this is a special case where we are parsing an SAI in which case there
			// will be a "value" : "something" element

			for (sub in aNode.value)
			{
				//if (aNode.value.hasOwnProperty(sub))
				//{
					//this.ctatdebug ("Returning value " + aNode.value [sub] + " for key: " + sub);
					if (sub=="value")
					{
						//this.ctatdebug ("Found value marker, returning value: "+aNode.value [sub]+", with type: "+typeof (aNode.value [sub])+"...");
						return (aNode.value [sub].toString());
					}
				//}
			}
		}

		return (aNode.value); // All we can do really
	};

    /**
     * Keep in mind that the input to this methods is or should be
	 * of the type CTATJSONObject
     */
    this.getElementAttr = function getElementAttr(anElement, attr)
    {
		var attrList=anElement.value;

		for (var test in attrList)
		{
			// this.ctatdebug ("Check: " + test);

			//if (attrList.hasOwnProperty(test))
			//{
				if (test==attr)
				{
					// this.ctatdebug ("Attribute "+attr+" found, returning: " + attrList [test]);
					return (attrList [test]);
				}
			//}
		}

		return (null);
    };

	/**
	*
	*/
	this.isArray=function isArray(what)
	{
		return Object.prototype.toString.call(what) === '[object Array]';
	};
	/**
	*
	*/
	this.walkDOM=function walkDOM (obj)
	{
		this.ctatdebug ("walkDOM () >>>>>>>>>>>>>>>>>>");

		recursionCounter=0;

		this.walk (obj);

		this.ctatdebug ("walkDOM () <<<<<<<<<<<<<<<<<<");
	};
	/**
	*
	*/
	this.walk=function walk (obj)
	{
		recursionCounter++;

		if (recursionCounter>recursion)
		{
			return;
		}

		var i=0;

		for (var key in obj)
		{
			//if (obj.hasOwnProperty(key))
			//{
				var val = obj[key];

				this.ctatdebug ("("+i+") typeof (key): " + typeof(key) + " -> typeof(value): " + typeof (val) + "("+val.length+")");

				if (typeof(val)!="string")
				{
					this.ctatdebug ("walk (" + key + ")");

					this.walk(val);
				}
				else
				{
					this.ctatdebug ("walk (" + key + ") : " + val);
				}
			//}

			i++;
		}
	};
	/**
	* @param {object} element to print
	* @return {string} readable version of argument
	*/
	this.stringify=function (anObject)
	{
		return this.toJSONString (anObject);
	};
	/**
	*
	*/
	this.toJSONString=function toJSONString (anObject)
	{
		return (JSON.stringify(anObject, null, 2)); // indentation level = 2
	};
	/**
	*
	*/
	this.syntaxHighlight=function syntaxHighlight(json)
	{
		if (typeof json != 'string')
		{
			json = JSON.stringify(json, undefined, 2);
		}

		json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
		return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match)
		{
			var cls = 'number';
			if (/^"/.test(match)) {
				if (/:$/.test(match)) {
					cls = 'key';
				} else {
					cls = 'string';
				}
			} else if (/true|false/.test(match)) {
				cls = 'boolean';
			} else if (/null/.test(match)) {
				cls = 'null';
			}
			return '<span class="' + cls + '">' + match + '</span>';
		});
	};
};

CTATJSON.prototype = Object.create(CTATBase.prototype);
CTATJSON.prototype.constructor = CTATJSON;

if(typeof module !== 'undefined')
{
	module.exports = CTATJSON;
}
