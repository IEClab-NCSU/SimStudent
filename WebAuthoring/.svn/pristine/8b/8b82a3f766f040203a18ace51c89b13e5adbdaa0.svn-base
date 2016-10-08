/**
 * @author $Author: mringenb $
 * @version  $Revision: 21689 $
 */
/**-----------------------------------------------------------------------------
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATClickableComponent.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATClickableComponent');

goog.require('CTATCompBase');
goog.require('CTAT.Component.Hierarchy.SAIHandler');
//goog.require('CTATGlobals');
/**
 *
 */
CTATClickableComponent = function(aClassName,
								aName,
								aDescription,
								aX,
								aY,
								aWidth,
								aHeight)
{
	CTAT.Component.Hierarchy.SAIHandler.call(this,
	//CTATCompBase.call(this,
					  aClassName,
					  aName,
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);

	var pointer=this;

	/**
	 * Implement in child class
	 * @param e
	 */
	this.processClick=function (e)
	{
		//useDebugging=true;

		pointer.ctatdebug ("processClick ("+e.currentTarget.getAttribute ("id")+" -> "+e.eventPhase+")");

		if (pointer.getEnabled()===true)
		{
			if ((pointer.getClassName ()=="CTATTextArea") || (pointer.getClassName ()=="CTATTextInput") || (pointer.getClassName ()=="CTATTextField"))
			{
				pointer.ctatdebug ("Info: click detected on a text based component, we should grade this type exclusively through backgrading");

				//oldComponentFocus=pointer;
			}
			else
			{
				pointer.grade();
			}
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
    
    /**
     * Double click processing
     */
    this.processDblClick=function  q(e)
    {
      pointer.ctatdebug ("processDblClick()....");
      //pointer.ctatdebug ("processDblClick ("+e.currentTarget.getAttribute ("id")+" -> "+e.eventPhase+")");
      if(pointer.getEnabled() &&
        (pointer.getClassName ()=="CTATTextArea") || (pointer.getClassName ()=="CTATTextInput") || (pointer.getClassName ()=="CTATTextField"))
      {
        var print = function(text){console.log(text);pointer.debug(text);};
        useDebugging=true;
        pointer.ctatdebug ("Double click has been called");
        useDebugging=false;
        var tsMessage = new CTATSAI(pointer.getName(),"DoubleClick",-1);
        pointer.ctatdebug ("tMessage " + tsMessage);
        commShell.processComponentAction(tsMessage);
        console.log(pointer.getText());
      }
    }  
    
    
};

CTATClickableComponent.prototype = Object.create(CTAT.Component.Hierarchy.SAIHandler.prototype);
//CTATClickableComponent.prototype = Object.create(CTATCompBase.prototype);
CTATClickableComponent.prototype.constructor = CTATClickableComponent;
