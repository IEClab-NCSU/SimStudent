/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATTextBasedComponent.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATTextBasedComponent');

goog.require('CTATConfig');
goog.require('CTATGlobals');
goog.require('CTATTutorableComponent');
/**
 *
 */
CTATTextBasedComponent = function(aClassName, aName, aDescription, aX, aY, aWidth, aHeight)
{

	CTATTutorableComponent.call(this,
			aClassName,
			aName,
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var pointer=this;
	var text="";
	var tabOnEnter=true;
	var maxCharacters=255;
	var editable=true;

	/**
	 *
	 */
	this.assignText=function assignText(aText)
	{
		text=aText;
	};

	/**
	 * @function UpdateTextField
	 * An Interface Action for setting the text
	 * @param {string} aText
	 * @see CTATTextBasedComponent.assignText
	 */
	this.UpdateTextField = this.assignText.bind(pointer);
	/**
	 * @function UpdateTextArea
	 * An Interface Action for setting the text
	 * @param {string} aText
	 * @see CTATTextBasedComponent.assignText
	 */
	this.UpdateTextArea = this.assignText.bind(pointer);
	/**
	 *
	 */
	this.setTabOnEnter=function setTabOnEnter(aValue)
	{
		tabOnEnter=aValue;
	};

	/**
	 *
	 */
	this.assignEditable=function assignEditable(aEditable)
	{
		editable=aEditable;
	};

	/**
	 *
	 */
	this.setMaxCharacters=function setMaxCharacters(aMax)
	{
		maxCharacters=aMax;
	};

	/**
	 *
	 */
	this.getText=function getText()
	{
		return (text);
	};

	/**
	 *
	 */
	this.getEditable=function getEditable()
	{
		return (editable);
	};

	/**
	 *
	 */
	this.getTabOnEnter=function getTabOnEnter()
	{
		return (tabOnEnter);
	};

	/**
	 *
	 */
	this.getMaxCharacters=function getMaxCharacters()
	{
		return (maxCharacters);
	};

	/**
	 *
	 */
	function getKey (e)
	{
		var key;

		if (CTATConfig.platform=="google")
		{
			return (0);
		}

		if(window.event)
			key = window.event.keyCode; //IE
		else
			key = e.which; //firefox

		return (key);
	}

	/**
	 *
	 * @param aValue
	 */
	this.setEditable=function setEditable(aValue)
	{
		pointer.assignEditable(aValue);

		if (pointer.getComponent()==null)
			return;

		if (pointer.getEditable()==true)
			pointer.getComponent().contentEditable='true';
		else
			pointer.getComponent().contentEditable='false';

		// set background/font color for disabled text from servlet instead

		// pointer.setFontColor(pointer.getDisabledTextColor());
		// pointer.setBackgroundColor(pointer.getDisabledBGColor());
	};

	/**
	 * Override from CTATCompBase because for text based components
	 * we also have to set them non-editable
	 *
	 * @param aValue
	 */
	this.setEnabled=function setEnabled(aValue)
	{
		pointer.assignEnabled(aValue);

		if (pointer.getComponent()==null)
			return;

		pointer.getComponent().disabled=!aValue;

		this.setEditable (aValue);
	};

	/**
	 *
	 */
	this.processKeypress=function processKeypress (e)
	{
		pointer.ctatdebug ("processKeypress ()");

		var id=e.target.getAttribute ("id");
		pointer.ctatdebug(id);
		var comp=pointer.getComponentFromID (id);

		var textElement=pointer.getComponent();

		if (comp==null)
		{
			pointer.ctatdebug ("Error: component reference is null");
			return;
		}

		pointer.ctatdebug (comp.name + " keydown ("+getKey (e)+" -> "+e.eventPhase+") " + "ID: " + id);

		var currentComponent=id;
		var currentComponentPointer=comp;

		switch (getKey (e))
		{
		// key code for left arrow
		case 37:
			pointer.ctatdebug('left arrow key pressed!');
			break;

			// key code for right arrow
		case 39:
			pointer.ctatdebug('right arrow key pressed!');
			break;

		case 13:
			pointer.ctatdebug('Enter key pressed!');

			if(tabOnEnter==true)
			{
				pointer.ctatdebug ("tabOnEnter==true");

				commShell.focusNextComponent (comp);
			}

			commShell.gradeComponent (comp);
			return (false);

		default:
			pointer.ctatdebug('Key pressed!');

		/*
	       			if(this.getComponent().innerHTML.length>maxCharacters)
	       			{
	       				this.getComponent().innerHTML=this.getComponent().innerHTML.substring(0, maxCharacters);
	       				alert("You have exceeded the maximum characters allowed: " + maxCharacters);
	       			}
		 */
		}
	};
}

CTATTextBasedComponent.prototype = Object.create(CTATTutorableComponent.prototype);
CTATTextBasedComponent.prototype.constructor = CTATTextBasedComponent;