/**
 *	@fileoverview A class representing styles applied to elements
 *	by the editor.  
 */

goog.provide("SilexStyleSheet");

/**
 *	@Constructor
 *	@param id the id of the <style> tag
 *	@param doc the document node of the working file
 */
SilexStyleSheet = function(id, doc)
{
	this.id = id;
	this.selectors = {};
	this.document = doc;
	this.keys = [];
}

//The id attribute of the <style> tag corresponding to this sheet 
SilexStyleSheet.prototype.id = null;
//A hashmap of css selectors to arrays of (stylename, value) pairs
SilexStyleSheet.prototype.selectors = null;
//An array of keys to this.selectors
SilexStyleSheet.prototype.keys = null;
//The document node this sheet applies to
SilexStyleSheet.prototype.document = null;

/**
 *	Add a style to the 'sheet'
 *	@param selector the css selector
 *	@param styleName the name of the css style
 *	@param styleVal the value for the style
 */
SilexStyleSheet.prototype.setStyle = function(selector, styleName, styleVal)
{
	selector = removeSpaces(selector);
	styleName = normalizeCase(styleName);
	if (!this.selectors[selector])
	{
		this.selectors[selector] = [];
		this.keys.push(selector);
	}
	
	var newStyle = {
		'name': styleName,
		'value': styleVal
	};
	
	let styles = this.selectors[selector];
	var idx = -1;
	for (let i = 0; i < styles.length; i++)
	{
		if (styles[i]['name'] === styleName)
		{
			idx = i;
			break;
		}
	}
	if (idx < 0)
		styles.push(newStyle);
	else
		styles[idx] = newStyle;
};

/**
 *	Retrieve a style value
 *	@param selector the css selector
 *	@param styleName the name of the css style
 *	@returns the value of the style or null
 */
SilexStyleSheet.prototype.getStyle = function(selector, styleName)
{
	selector = removeSpaces(selector);
	styleName = normalizeCase(styleName);
	var value = null;
	if (this.selectors[selector])
	{
		let styles = this.selectors[selector];
		for (let i = 0; i < styles.length; i++)
		{
			if (styles[i]['name'] === styleName)
			{
				value = styles[i]['value'];
				break;
			}
		}	
	}
	return value;
};

/**
 *	Retrieve array of all (style, value) pairs for a certain selector
 */
SilexStyleSheet.prototype.getAllStyles = function(selector)
{
	selector = removeSpaces(selector);
	return this.selectors[selector];
}

/**
 *	Remove a selector from the 'sheet'
 *	@param selector the css selector
 */
SilexStyleSheet.prototype.removeSelector = function(selector)
{
	console.log('removing '+selector+' from ctatStyleSheet');
	selector = removeSpaces(selector);
	if (selector)
	{
		delete this.selectors[selector];
		this.keys = this.keys.splice(this.keys.indexOf(selector), 1);
	}
};

/**
 *	Remove a selector by substring w/in that selector
 *	Will remove all selectors w/ that substring
 *	@param substring the substring
 */ 
SilexStyleSheet.prototype.removeSelectorBySubstring = function(substring)
{
	console.log('removeSelectorBySubstring( '+substring+' )');
	for (let i = 0; i < this.keys.length; i++)
	{
		if (this.keys[i].includes(substring))
		{
			this.removeSelector(this.keys[i]);
		}
	}
};

/**
 *	Read in the contents of a <style> node to this object
 */
SilexStyleSheet.prototype.readFromDom = function()
{
	console.log('SilexStyleSheet.readFromDom( )');
	var sheet = null;
	var sheetNode;
	//find the corresponding stylesheet
	for (let i = 0; i < this.document.styleSheets.length; i++)
	{
		sheetNode = this.document.styleSheets[i].ownerNode;
		if (sheetNode && sheetNode.getAttribute('id') === this.id)
		{
			sheet = this.document.styleSheets[i];
			break;
		}
	}
	if (!sheet)
	{
		console.log('readFromDom( ) couldn\'t find the stylesheet!');
		return;
	}
	
	var rules = sheet.cssRules;
	for (let i = 0; i < rules.length; i++)
	{
		let rule = rules[i];
		let selector = rule.selectorText;
		let decBlock = rule.style.cssText;
		let attributes = decBlock.split(';');
		for (let j = 0; j < attributes.length; j++)
		{
			let attribute = attributes[j].split(':');
			let name = attribute[0].trim();
			let value = attribute[1];
			if (value) value = value.trim();
			if (name && value)
			{
				this.setStyle(selector, name, value);
			}
		}
	}
	
	this.document.head.removeChild(sheetNode);
};

/**
 *	Write the contents of this class out to a <style> node
 *	@param doc the document node to write the styles to
 */
SilexStyleSheet.prototype.writeToDom = function(doc)
{
	console.log('writeToDom()');
	var styleStr = '';
	for (let selector in this.selectors)
	{
		console.log("selector = "+selector);
		if (this.selectors[selector])
		{
			styleStr += selector + ' { \n';
			let styles = this.selectors[selector];
			for (let i = 0; i < styles.length; i++)
			{
				styleStr += styles[i].name + ': ' + styles[i].value + ';\n';
			}
			styleStr+='}\n';
		}
	}
	var styleTag = document.createElement('style');
	styleTag.setAttribute('id', this.id);
	doc.head.appendChild(styleTag);
	styleTag.innerHTML = styleStr;
};

SilexStyleSheet.prototype.selectorChanged = function(oldId, newSelector)
{
	let selectors = getSelectorsBySubstring.call(this, oldId);
	for (let i = 0; i < selectors.length; i++)
	{
		let style = this.selectors[selectors[i]];
		this.selectors[newSelector] = style;
		this.removeSelector(selectors[i]);
	}
};

/**
 *	Helper function - convert a camelCase string into
 * 	lower-case-hyphenated
 */
function normalizeCase(styleName)
{
	if (styleName)
	{
		var regex = /[A-Z]/g;
		var idx = styleName.search(regex);
		styleName = styleName.toLowerCase();
		if (idx > 0)
		{
			var first = styleName.substring(0, idx);
			var last = styleName.substring(idx);
			styleName = first+'-'+last;
		}
		return styleName;
	}
}

function getSelectorsBySubstring(substring)
{
	var selectors = [];
	for (let i = 0; i < this.keys.length; i++)
	{
		if (this.keys[i].includes(substring))
		{
			selectors.push(this.keys[i]);
		}
	}
	
	return selectors;
}

/**
 *	Helper function - remove all spaces from a string
 */
function removeSpaces(str)
{
	var pieces = str.split(' ');
	var whole = '';
	for (let i = 0; i < pieces.length; i++)
	{
		whole += pieces[i];
	}
	return whole;
}