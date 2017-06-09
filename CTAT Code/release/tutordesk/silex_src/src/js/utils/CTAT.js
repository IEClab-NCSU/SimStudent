/**
 *	@fileoverview Some utility functions for working with the CTAT library
 */

goog.provide('silex.utils.CTAT');

/**
 *	Get the name of the CTAT class given a divWrap element
 *	@param element the div in which the component lives
 *	@returns null or the CTAT class name (e.g. CTATTextArea)
 */
silex.utils.CTAT.getCTATClassName = function(element)
{
	var retVal = null;
	if (element)
	{
		//get CTAT className
		var regex = /CTAT[A-Za-z]*/;
		var className = element.className.match(regex);
		if (className) retVal = className[0];	
	}
	return retVal;
};

silex.utils.CTAT.splitCTATClassName = function(name)
{
	var splitName = 'CTAT';
	var toSplit = name.substring(4, name.length);
	let startHere = 0;
	for (let i = 1; i < toSplit.length; i++)
	{
		if (toSplit.charAt(i).toUpperCase() === toSplit.charAt(i))
		{
			splitName += ' '+toSplit.substring(startHere, i);
			startHere = i;
		}
	}
	splitName += ' '+toSplit.substring(startHere, toSplit.length);
	return splitName;
};

/**
 *	Generates a CSS selector for a ctat component provided the divWrap
 *	and the canonical name of the style
 *	@param element the divWrap
 *	@param styleName the name of the style in camelCase
 *	@returns an object containing the selector and the real
 *		name of the css style (needed b/c svg comps use 'fill'
 *		instead of 'color')
 */
silex.utils.CTAT.getCTATCSSSelector = function(element, styleName)
{
	var className = silex.utils.CTAT.getCTATClassName(element);
	if (!className)
	{
		return;
	}
	//get style name and tag name
	var styleMap = CTATComponentStyleMappings[className];
	if (!styleMap)
	{
		console.error("updateCTATStyleSheet( ) couldn't find style object mapped to "+className);
	}
	else
	{
		var style;
		var tagName;
		if (styleMap['usesDefault'])
		{
			style = CTATComponentStyleMappings['defaultStyleAttr'][styleName];
			tagName = styleMap['innerTagName'];
		}
		else
		{
			style =   styleMap['styleAttributes'][styleName][0];
			tagName = styleMap['styleAttributes'][styleName][1];
		}
		let selector = '.'+window.silexApp.model.property.getSilexId(element)
		if (tagName) selector += ' > '+tagName;
		
		let retVal = {'selector': selector, 'style': style};
		
		return retVal;
	}
	return null;
};