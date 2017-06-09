/**
 *
 */
 
//goog.require('CTATBase');

/**
*
*/
var CTATAccountManager = function() 
{		
	CTATDialogBase.call (this, "#accountwindow", "CTATAccountManager", "account","MODAL");
	
	var pointer=this;
	var msgIsThere = false;
	this.init=function init()
	{
		pointer.ctatdebug ("init ()");
		
		$("#accountselect").on("click",function ()
		{			
			var provider = document.querySelector('input[name = "accountprovider"]:checked').value
			
			pointer.close ();
			
			cloudUtils.initDrive(provider);		
		});		
	};
	
	/**
	*
	*/
	this.showChooser=function showChooser ()
	{
		pointer.ctatdebug ("showChooser ()");

		//pointer.processCTATFolder (currentFolderid);
		
		//refreshTimer=window.setInterval (pointer.fileDialogReload,60000);
	};
	
	pointer.init ();
};

CTATAccountManager.prototype = Object.create(CTATDialogBase.prototype);
CTATAccountManager.prototype.constructor = CTATAccountManager;
