/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATButtonBasedComponent.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATButtonBasedComponent');

goog.require('CTATGlobals');
goog.require('CTATTutorableComponent');
/**
 *
 */
CTATButtonBasedComponent = function(aClassName,
		aName,
		aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTATTutorableComponent.call(this,
			aClassName,
			aName,
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	this.setDefaultWidth (60);
	this.setDefaultHeight (30);

	var pointer=this;
	var buttonText="";

	this.ctatdebug ("CTATButtonBasedComponent" + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.setActionInput("ButtonPressed","-1");

	/**
	 *
	 * @param aText
	 */
	this.setText=function setText (aText)
	{
		pointer.ctatdebug("setText (" + aText + ")");

		buttonText=aText;

		if (pointer.getComponent()!=null)
		{
			pointer.getComponent().innerHTML=aText;
		}
	};

	/**
	 *
	 */
	this.getText=function getText ()
	{
		return (buttonText);
	}

	/**
	 *
	 * @param e
	 */
	this.processClick=function processClick (e)
	{
		//useDebugging=true;

		pointer.ctatdebug ("processClick ("+e.currentTarget.getAttribute ("id")+" -> "+e.eventPhase+")");

		if (pointer.getEnabled()==true)
		{
			if (oldComponentFocus!=null)
			{
				if ((oldComponentFocus.getClassName ()=="CTATTextArea") || (oldComponentFocus.getClassName ()=="CTATTextInput") || (oldComponentFocus.getClassName ()=="CTATTextField"))
				{
					//if (oldComponentFocus.getName ()=="done")
					//{
					commShell.gradeComponent (oldComponentFocus);
					//}
				}
				else
				{
					pointer.ctatdebug ("Info: oldComponentFocus==null");
				}
			}

			pointer.grade();
		}
		else
			pointer.ctatdebug ("Component is disabled, not grading");

		//useDebugging=true;
	};

	/**
	 *
	 */
	this.showCorrect = function(aMessage)
	{
		pointer.ctatdebug ("showCorrect ()");

		pointer.setHintHighlight(false);
		var str = globalCorrectString;
		pointer.addStringCSS(str);

		if(pointer.getDisableOnCorrect() == 'true')
		{
			pointer.setEnabled(false);
		}
	}

	/**
	 *
	 */
	this.showInCorrect = function(aMessage)
	{
		pointer.ctatdebug ("showInCorrect ()");

		var str = globalInCorrectString;
		pointer.addStringCSS(str);
	}

	/**
	 * InterfaceAction for pressing a button.
	 */
	this.ButtonPressed = function () {
		// make sure valid Action exists.
		return;
	};
};

CTATButtonBasedComponent.prototype = Object.create(CTATTutorableComponent.prototype);
CTATButtonBasedComponent.prototype.constructor = CTATButtonBasedComponent;

