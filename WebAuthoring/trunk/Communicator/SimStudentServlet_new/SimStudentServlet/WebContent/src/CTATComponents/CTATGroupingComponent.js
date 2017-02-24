/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATGroupingComponent.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATGroupingComponent');

goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATTutorableComponent');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATGroupingComponent = function(aDescription,aX,aY,aWidth,aHeight)
{
	CTATTutorableComponent.call(this,
					 			"CTATGroupingComponent",
					  			"__undefined__",
					  			aDescription,
					  			aX,
					  			aY,
					  			aWidth,
					  			aHeight);

	var pointer=this;
	pointer.isTabIndexable=false;
	var targetClip=null;

	/**
	*
	*/
	this.init=function init()
	{
	    pointer.ctatdebug("init (" + pointer.getName() + ")");

	    pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());

		pointer.setInitialized (true);

	    //currentZIndex++;
	    //currentIDIndex++;
	};

	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.ctatdebug ("processSerialization()");
	};

	/**
	*
	*/
	this.postProcess=function postProcess ()
	{
		pointer.ctatdebug ("postProcess ()");

		// Process component specific pre-defined styles ...

		for(var j=0;j<this.parameters.length;j++)
		{
			var aParam=this.parameters [j];

			pointer.ctatdebug ("Checking parameter name: " + aParam.paramName);

			if (aParam.paramName=="TargetMovieClip")
			{
				pointer.ctatdebug ("Loading sub element: " + aParam.paramValue.trim () + " ...");

				var aClip=findMovieClip (aParam.paramValue);

				if (aClip!=null)
				{
					pointer.ctatdebug ("Found target movieclip, storing for future reference ...");

					targetClip=aClip;
				}
			}
		}
	};

	/**
	 * Temporarily disables the component until tempLock(false) is called.
	 * This is for temporarily locking a component with the assumption that the flash
	 * tutor will automatically unlock it. This is to be used when user input is not
	 * wanted for a temporary amount of time (i.e. component already being graded, error
	 * scrim is up, tutor is disconnected). See permlock for action-driven locking.
	 * @param bool to lock or unlock
	 * @return true if perm or temp locked.
	 */
	this.setTempLock=function setTempLock(bool)
	{
		pointer.ctatdebug ("setTemplock ("+bool+")");

		if (targetClip!=null)
		{

		}
		else
			pointer.ctatdebug ("Error: no target MovieClip assigned to this grouping component");

		return (true);
	};

	/**
	 * Is a component disabled or not...
	 * @return is comp disabled
	 */
	//this.isLocked=function isLocked() // unused and isXXXLocked are not declared.
	//{
	//	return (isPermLocked || isTempLocked);
	//};

	/**
	 * Overwrite from CTATCompBase
	 */
	this.move=function move(anX,anY)
	{
		pointer.ctatdebug ("move ("+anX+","+anY+")");

		if (targetClip!=null)
		{
			//targetClip ['move'] (anX,anY);
			targetClip.getDivWrapper ().style.left = anX;
			targetClip.getDivWrapper ().style.top = anY;
		}
		else
			pointer.ctatdebug ("Error: no target MovieClip assigned to this grouping component");
	};

	/**
	 * Overwrite from CTATCompBase, see below
	 */
	this.size=function size(w,h)
	{
		this.setSize (w,h);
	};

	/**
	 * Overwrite from CTATCompBase
	 */
	this.setSize=function setSize(w,h)
	{
		pointer.ctatdebug ("setSize ("+w+","+h+")");

		if (targetClip!=null)
		{
			//targetClip ['setSize'] (w,h);

			targetClip.getDivWrapper ().style.width = (w+'px');
			targetClip.getDivWrapper ().style.height = (h+'px');
		}
		else
			pointer.ctatdebug ("Error: no target MovieClip assigned to this grouping component");
	};

	/**
	 * Overwrite from CTATCompBase
	 */
	this.SetVisible=function SetVisible (aValue)
	{
		var bool=stringToBoolean (aValue);

		pointer.ctatdebug ("SetVisible ("+bool+")","graphics");

		if (targetClip!=null)
		{
			if (bool==true)
			{
				//pointer.ctatdebug ("targetClip.getDivWrapper ().style.display = 'block';");

				targetClip.getDivWrapper ().style.display = 'block';
			}
			else
			{
				//pointer.ctatdebug ("targetClip.getDivWrapper ().style.display = 'none';");

				targetClip.getDivWrapper ().style.display = 'none';
			}
		}
		else
			pointer.ctatdebug ("Error: no target MovieClip assigned to this grouping component");

		pointer.ctatdebug ("SetVisible () Done");
	};

	/**
	 * Overwrite from CTATCompBase
	 */
	this.setVisible=function setVisible (aValue)
	{
		pointer.ctatdebug ("setVisible ("+aValue+")","graphics");

		if (targetClip!=null)
		{
			if (aValue==true)
			{
				targetClip.getDivWrapper ().style.display = 'block';
			}
			else
			{
				targetClip.getDivWrapper ().style.display = 'none';
			}
		}
		else
			pointer.ctatdebug ("Error: no target MovieClip assigned to this grouping component");
	};

	this.configFromDescription ();
}

CTATGroupingComponent.prototype = Object.create(CTATTutorableComponent.prototype);
CTATGroupingComponent.prototype.constructor = CTATGroupingComponent;

CTAT.ComponentRegistry.addComponentType('CTATComponentContainerReference',CTATGroupingComponent);