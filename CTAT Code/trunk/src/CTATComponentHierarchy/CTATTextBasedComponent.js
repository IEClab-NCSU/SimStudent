/**-----------------------------------------------------------------------------
 $Author: mdb91 $
 $Date: 2017-03-16 16:02:46 -0500 (週四, 16 三月 2017) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATTextBasedComponent.js $
 $Revision: 24659 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:
	Derive from CTAT.Component.Base.Tutorable instead of CTAT.Component.Base.Clickable
	because the text components do not use the click event.
 */
goog.provide('CTATTextBasedComponent');

goog.require('CTATConfig');
//goog.require('CTATGlobals');
goog.require('CTATGlobalFunctions');
goog.require('CTAT.Component.Base.Tutorable');
/**
 *
 */
CTATTextBasedComponent = function(aClassName, aName, aDescription, aX, aY, aWidth, aHeight) {
	CTAT.Component.Base.Tutorable.call(this,
			aClassName,
			aName,
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var pointer=this;
	var text="";
	var textColor = '#000000';
	var textSize = 16;
	var tabOnEnter=true;
	var maxCharacters=255;
	var editable=true;
	this.setAction('UpdateTextField');
	this.backgrade = true;

	this.setFontColor = function(aColor)
	{
		textColor = aColor;
		$(pointer.getComponent()).css('color', aColor);
	}
	
	this.getFontColor = function()
	{
		return textColor;
	}
	
	this.setFontSize = function(aSize)
	{
		if (!aSize.includes('px'))
			aSize+='px';
		textSize = aSize;
		$(pointer.getComponent()).css('font-size', aSize);
	}
	
	this.getFontSize = function()
	{
		return textSize;
	}
	
	/**
	 *
	 */
	this.assignText=function assignText(aText)
	{
		text=aText;
		this.setInput(aText);
	};

	/**
	 * @function UpdateTextField
	 * An Interface Action for setting the text
	 * @param {string} aText
	 * @see CTATTextBasedComponent.assignText
	 */
	this.UpdateTextField = function(aText) {
		this.setText(aText);
	};
	/**
	 * @function UpdateTextArea
	 * An Interface Action for setting the text
	 * @param {string} aText
	 * @see CTATTextBasedComponent.assignText
	 */
	this.UpdateTextArea = this.UpdateTextField;
	/**
	 *
	 */
	this.setTabOnEnter=function setTabOnEnter(aValue)
	{
		tabOnEnter=CTATGlobalFunctions.toBoolean(aValue);
	};
	this.setStyleHandler('TabOnEnter',this.setTabOnEnter);
	this.data_ctat_handlers['tab-on-enter'] = this.setTabOnEnter;

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
	this.setStyleHandler('MaxCharacters',this.setMaxCharacters);

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
		pointer.assignEditable(CTATGlobalFunctions.toBoolean(aValue));

		if (pointer.getComponent()===null)
			return;

		if (pointer.getEditable()===true)
			pointer.getComponent().contentEditable='true';
		else
			pointer.getComponent().contentEditable='false';
	};
	this.setStyleHandler('Enabled',this.setEditable);

	/**
	 * Override from CTATCompBase because for text based components
	 * we also have to set them non-editable
	 *
	 * @param aValue
	 */
	this.setEnabled=function setEnabled(aValue)
	{
		pointer.assignEnabled(aValue);

		if (pointer.getComponent()===null)
			return;

		pointer.getComponent().disabled=!aValue;

		this.setEditable (aValue);
	};
	var super_processAction = this.processAction.bind(this);
	this.processAction = function(force_grade, force_record) 
	{
		pointer.ctatdebug ("processAction ()");
	
		this.updateSAI();
		
		if (!CTATGlobalFunctions.isBlank(this.getValue()))
		{
			super_processAction(force_grade,force_record);
		}
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

		//var textElement=pointer.getComponent();

		if (comp===null)
		{
			pointer.ctatdebug ("Error: component reference is null");
			return;
		}

		pointer.ctatdebug (comp.getName() + " keydown ("+getKey (e)+" -> "+e.eventPhase+") " + "ID: " + id);

		//var currentComponent=id;
		//var currentComponentPointer=comp;

		//console.log('keypress:',e.which);
		switch (e.which)
		{
		// key code for left arrow
		case 37:
			pointer.ctatdebug('left arrow key pressed!');
			break;

			// key code for right arrow
		case 39:
			pointer.ctatdebug('right arrow key pressed!');
			break;

		case 13: // enter
			//console.log('Enter key pressed',tabOnEnter);
			if (tabOnEnter) {
				pointer.component.blur();
				CTATGlobals.Tab.Focus = null; // prevents backgrading
				pointer.processAction();
				return false;
			} else {
				return true;
			}
			break;
		case 0: // tab
			//pointer.ctatdebug('Tab key pressed!');
			pointer.component.blur();

			CTATGlobals.Tab.Focus = null;
			pointer.processAction();
			break;
		default:
			pointer.ctatdebug('Key pressed! "'+e.which+'"');
			//pointer.setNotGraded();
		}
	};

	this.updateSAI = function()
	{
		pointer.ctatdebug ("updateSAI ()");

		this.setInput(this.getValue());

		var testSAI=this.getSAI ();

		pointer.ctatdebug ("SAI: " + testSAI.toTSxmlString ());
	};
	
	
};

CTATTextBasedComponent.prototype = Object.create(CTAT.Component.Base.Tutorable.prototype);
CTATTextBasedComponent.prototype.constructor = CTATTextBasedComponent;
