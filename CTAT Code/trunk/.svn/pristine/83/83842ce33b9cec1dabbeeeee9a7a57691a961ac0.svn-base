/**-----------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 
 -
 License:
 -
 ChangeLog:
 -
 Notes:

*/

/**
 *
 */
var CTATWindow = function()
{
	CTATBase.call(this, "CTATWindow");

	var pointer=this;

	var windowID="";	
	var windowState="DEFAULT"; // Options are: DEFAULT, MAXIMIZED, MINIMIZED
	var windowMode="MODELESS"; // Either MODAL or MODELESS

	var storedX=0;
	var storedY=0;
	var storedWidth=0;
	var storedHeight=0;

	/**
	*
	*/
	this.setWindowID=function setWindowID (anID)
	{
		windowID=anID;
	};
	
	/**
	*
	*/
	this.getWindowID=function getWindowID ()
	{
		return (windowID);
	};	
	/**
	*
	*/
	this.setWindowState=function setWindowState (aState)
	{
		windowState=aState;
	};
	
	/**
	*
	*/
	this.getWindowState=function getWindowState ()
	{
		return (windowState);
	};
	
	/**
	*
	*/	
	this.setWindowMode=function setWindowMode (aMode)
	{
		windowMode=aMode;
	};
	
	/**
	*
	*/	
	this.getWindowMode=function getWindowMode ()
	{
		return (windowMode);
	};	
	
	/**
	*
	*/
	this.storeDimensions=function storeDimensions(anX,anY,aWidth,aHeight)
	{
		storedX=anX;
		storedY=anY;
		storedWidth=aWidth;
		storedHeight=aHeight;
	};

	/**
	*
	*/	
	this.getStoredX=function getStoredX ()
	{
		return (storedX);
	};
	
	/**
	*
	*/	
	this.getStoredY=function getStoredY ()
	{
		return (storedY);
	};
	
	/**
	*
	*/	
   	this.getStoredWidth=function getStoredWidth ()
	{
		return (storedWidth);
	};
	
	/**
	*
	*/	
	this.getStoredHeight=function getStoredHeight ()
	{
		return (storedHeight);
	};

	this.setStatus = function(status)
	{
		var statusField;
		if (windowID.indexOf('#') == 0)
			statusField = $(windowID+' > .ctatstatusbar > .status');
		else
			statusField = $('#'+windowID+' > .ctatstatusbar > .status');
		if (statusField)
		{
			statusField.text(status);
		}
		else
			console.warn("CTATWindow couldn't find the statusbar!");
	};
};

CTATWindow.prototype = Object.create (CTATBase.prototype);
CTATWindow.prototype.constructor = CTATWindow;
