/**
 * @fileoverview Adds basic interaction with the component in the form of
 * handling click events on the component.  This defines a very general click
 * handler that initiates grading.
 *
 * @author $Author: mringenb $
 * @version  $Revision: 23157 $
 */
/**-----------------------------------------------------------------------------
 $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATComponentHierarchy/CTATClickableComponent.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTAT.Component.Base.Clickable');

goog.require('CTAT.Component.Base.Tutorable');
//goog.require('CTATGlobals');
/**
 *
 */
CTAT.Component.Base.Clickable = function(aClassName,
								aName,
								aDescription,
								aX,
								aY,
								aWidth,
								aHeight)
{
	CTAT.Component.Base.Tutorable.call(this,
					  aClassName,
					  aName,
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);

	var pointer=this;

	/**
	 * Implement in child class, make sure to .setInput() before calling this.
	 * @param e
	 */
	this.processClick=function (e)
	{
		//useDebugging=true;

		pointer.ctatdebug ("processClick ("+e.currentTarget.getAttribute ("id")+" -> "+e.eventPhase+")");

		if (pointer.getEnabled()===true)
		{
			pointer.processAction();
		}
		else
			pointer.ctatdebug ("Component is disabled, not grading");

		//useDebugging=false;
    };

    /**
     * Interface Action: setClickable
     */
    this.setClickable = function(clickable) {
    	// TODO: AS3 version also handled adding and removing event listeners.
    	pointer.setEnabled(clickable);
    };
};

CTAT.Component.Base.Clickable.prototype = Object.create(CTAT.Component.Base.Tutorable.prototype);
CTAT.Component.Base.Clickable.prototype.constructor = CTAT.Component.Base.Clickable;
