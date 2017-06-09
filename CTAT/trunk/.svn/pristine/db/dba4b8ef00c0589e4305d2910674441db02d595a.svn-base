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

	We can keep a 'current dialog' pointer since windows like the file
	chooser are always modal and there should not be more than one
	modeless dialog at the same time. If this system ever becomes really
	big and widely used it might have to change. But you know, CTAT, fat
	chance.
*/

var currentDialog=null;

/**
*
*/
var CTATDialogBase = function(anID,aClass,anInstance,aMode,aTitle) 
{
	CTATBase.call (this, aClass, anInstance);
	
	var dialogID=anID;
	var pointer=this;
	var refreshTimer=-1;
	var windowObject=null;
	
	var mode = aMode;
	
	/**
	*
	*/
	this.setRefreshTimer=function setRefreshTimer (aTimerValue)
	{
		refreshTimer=aTimerValue;
	};
	
	this.getRefreshTimer = function()
	{
		return refreshTimer;
	}
	
	/**
	*
	*/
	this.getDialogID=function getDialogID ()
	{
		return dialogID;
	};
	
	/**
	*
	*/
	this.setTitle=function setTitle (aTitle)
	{
		if (aTitle)
		{
			if (dialogID.indexOf ("#") === -1)
			{
				$("#"+dialogID+"-title").text (aTitle);
			}
			else
			{
				$(dialogID+"-title").text (aTitle);				
			}
		}		
	};
	
	/**
	*
	*/
	this.show=function show (aTitle)
	{
		$(dialogID).visible ();

		if (aTitle)
			pointer.setTitle (aTitle);
		
		windowObject=windowManager.addWindow (dialogID, null, mode);
		windowObject.setWindowMode(mode);
		windowManager.centerWindow (dialogID);		
		
		return (windowObject);
	};	
	
	/**
	*
	*/
	this.close=function close ()
	{
		windowManager.closeWindow (dialogID);
		
		if (refreshTimer!=-1)
		{			
			window.clearTimeout (refreshTimer);
			window.clearInterval(refreshTimer);
			refreshTimer=-1;
		}
		
		currentDialog=null;
	};	
		
	currentDialog=this;
	
	pointer.show ();
};

CTATDialogBase.prototype = Object.create(CTATBase.prototype);
CTATDialogBase.prototype.constructor = CTATDialogBase;
