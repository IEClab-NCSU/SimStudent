goog.provide("CTATNoolsTracerUtil");

var CTATNoolsTracerUtil = function()
{
	var types = {};
	var absUrlRegex = new RegExp('^(?:[a-z]+:/)?/', 'i');
	
	this.setTypes = function(flow, typeNames)
	{
		console.log("setTypes()");
		typeNames.forEach((type) => {
			types[type] = flow.getDefined(type);
			if (!types[type])
			{
				console.log("couldn't get "+type);
			}
			else
				console.log("set type "+type);
		});
	}
	
	/**
	*	Get the type of a given fact
	*	@param {Object|String|Number|Boolean} fact the fact
	*	@returns {String|null}
	*/
	this.getFactType = function(fact)
	{
		var type = typeof fact;
		if (type === "object")
		{
			type = null;
			if (fact instanceof Array)
				type = "Array";
			else if (fact instanceof RegExp)
				type = "RegExp";
			else if (fact instanceof Date)
				type = "Date";
			else
			{
				for (t in types)
				{
					if (types.hasOwnProperty(t) && types[t] === fact.constructor)
					{
						type = t
						break;
					}	
				}
			}
		}
		return type;
	};
	
	/**
	*
	*/
	this.getTypeConstructor = function(type)
	{
		return types[type];
	}
	
	/**
	*	Translate a fact declaration from {type: typeString, properties: {key1: val1...}}
	*		to a form that can be asserted in the engine
	*	@param {Object} factData an object formatted like above
	*	@returns {Object} a fact object that can be asserted
	*/
	this.prepFact = function(factData)
	{
		var fact, type = factData.type, properties = factData.properties;
		if (!types[type])
		{
			//get constructor
			types[type] = flow.getDefined(type);
		}
		//construct
		fact = new types[type]();
		//assign slot values
		for (prop in properties)
		{
			if (properties.hasOwnProperty(prop) 
			&&  fact.hasOwnProperty(prop))
			{
				if (typeof properties[prop] === 'object')
					fact[prop] = JSON.parse(JSON.stringify(properties[prop])); //deep copy
				else
					fact[prop] = properties[prop];
			}
		}
		return fact;
	}
	
	this.genDefaultHint = function(sai)
	{
		var hint = "",
			value = sai.input,
			action = sai.action.toLowerCase();
			
		if (action.includes("updatetext"))
		{
			hint = "Enter "+value+" in the highlighted field";
		}
		else if (action.includes('button'))
		{
			hint = "Press the highlighted button";
		}
		
		return hint;
	}
	
	this.relativeToAbsolute = function(rel, base)
	{
		if (this.isAbsoluteURL(rel))
			return rel;
		
		base = base || window.location.href;
		var stack = base.split("/"),
			parts = rel.split("/");
		stack.pop(); // remove current file name (or empty string)
					 // (omit if "base" is the current folder without trailing slash)
		for (var i=0; i<parts.length; i++) {
			if (parts[i] == ".")
				continue;
			if (parts[i] == "..")
				stack.pop();
			else
				stack.push(parts[i]);
		}
		return stack.join("/");
	};
	
	this.isAbsoluteURL = function(url)
	{
		return absUrlRegex.test(url);
	};
	
	var transactionId = 0;
	this.buildStartStateMsg = function(tpaFact) {
		var msgStr = "<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceAction</MessageType>";
		msgStr += "<transaction_id>"+(transactionId++)+"</transaction_id>";
		msgStr += "<Selection><value>"+tpaFact.selection+"</value></Selection>";
		msgStr += "<Action><value>"+tpaFact.action+"</value></Action>";
		msgStr += "<Input><value>"+tpaFact.input+"</value></Input>";
		msgStr += "</properties></message>";
	
		return msgStr;
	};
	
	this.buildStateGraphMsg = function(configObject) {
		var sgMsg = "<message><verb>SendNoteProperty</verb><properties><MessageType>StateGraph</MessageType>";
		for (let attr in configObject) {
			if (configObject.hasOwnProperty(attr)) {
				sgMsg += "<"+attr+">"+configObject[attr]+"</"+attr+">";
			}
		}
		sgMsg += "</properties></message>";
		return sgMsg;
	}
}