/**
 * @fileoverview Handles maintaining the style attribute used in the component's
 * tag.
 * @author $Author: mdb91 $
 * @version  $Revision: 24659 $
 */
/*-----------------------------------------------------------------------------
 $Date: 2017-03-16 16:02:46 -0500 (週四, 16 三月 2017) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/Style.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

*/

goog.provide('CTAT.Component.Base.Style');

goog.require('CTATCompBase');
goog.require('CTATCSS');

// This should be a mixin
/**
 * @param aClassName
 * @param aName
 * @param aX
 * @param aY
 * @param aWidth
 * @param aHeight
 */
CTAT.Component.Base.Style = function (aClassName,aName,aX,aY,aWidth,aHeight) {
	CTATCompBase.call(this,aClassName,aName,aX,aY,aWidth,aHeight);

	var componentStyle = new CTATCSS();
	/*componentStyle.addSelector(":focus");
	componentStyle.addSelectorAttribute(":focus", "outline", 0);
	componentStyle.addCSSAttribute("left", 0+"px");
	componentStyle.addCSSAttribute("top", 0+"px");
	componentStyle.addCSSAttribute("padding","0px");
	componentStyle.addCSSAttribute("position", "absolute");
	componentStyle.addCSSAttribute("outline", "none");*/

	/*this.setCSSStyle = function(aStyle,aValue) {
		componentStyle.modifyCSSAttribute(aStyle, aValue);
	};
	this.getCSSStyle = function() { return componentStyle; }; // unused */
	/**
	 * Wrapper functions for the CSS stuff - so user doesn't need to keep using the getter
	 */
	this.addCSSAttribute=function addCSSAttribute(attrib, val)
	{
		componentStyle.addCSSAttribute(attrib, val);
		this.render ();
	};
	this.removeCSSAttribute = function(attrib) {
		componentStyle.removeCSSAttribute(attrib);
		this.render();
	};

	/**
	 *
	 */
	/*this.clearCSS=function clearCSS ()
	{
		componentStyle.clearCSS();
		this.render ();
	};*/

	/**
	 * Add the given style string to the style attribute.
	 * @param {String} str	the string to remove
	 */
	this.addStringCSS=function addStringCSS(str)
	{
		componentStyle.addStringCSS(str);
		this.render ();
	};
	/**
	 * Remove a given style string from the style attribute.
	 * @param {String} str	the string to remove.
	 */
	this.removeStringCSS=function(str) {
		componentStyle.removeStringCSS(str);
		this.render();
	};
	/**
	 *
	 */
	/*this.addSelector=function addSelector(sel)
	{
		componentStyle.addSelector(sel);
	};*/

	/**
	 *
	 */
	/*this.addSelectorAttribute=function addSelectorAttribute(sel, attrib, val)
	{
		componentStyle.addSelectorAttribute(sel, attrib, val);
	};*/

	/**
	 *
	 */
	this.modifyCSSAttribute=function modifyCSSAttribute(attrib, val)
	{
		componentStyle.modifyCSSAttribute(attrib, val);
		this.render ();
	};

	/**
	 *
	 */
	this.getCSS=function getCSS()
	{
		return componentStyle.toCSSString();
	};

	this.render = function () {
		if (this.isAbstractComponent()===true) 
		{
			this.ctatdebug ("Component is an abstract component, bump");
			return;
		}
		if (this.getInitialized()) {
			var component = this.getComponent();
			if (component) {
				//component.setAttribute('style', componentStyle.toCSSString());
				this.ctatdebug(componentStyle.toCSSString());
			} else {
				this.ctatdebug ("Internal error, html component not available for rendering");
			}
		}
	};
	/**
	 *
	 */
	this.setStyleAll=function setStyleAll (aStyle,aValue)
	{
		if (this.getSubCanvas())
		{
			this.getSubCanvas().setAttribute(aStyle,aValue);
		}

		this.modifyCSSAttribute(aStyle,aValue);
	};

};

CTAT.Component.Base.Style.prototype = Object.create(CTATCompBase.prototype);
CTAT.Component.Base.Style.prototype.constructor = CTAT.Component.Base.Style;