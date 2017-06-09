
/**
*
*/
window.settingsObject=
{
	internal: 	[
					["mode","browser"]
				],
	parameters: [
					["Remember Window Settings","true"],
					["Show Account Chooser on Startup","true"],
					["Logout if idle","true"],
					["Idle time","45min"]
				],	
	windows: []	
};

/**
*
*/
var CTATSettings = function() 
{
	CTATBase.call (this, "CTATSettings", "settings");
	
	var pointer=this;
	var updateTimer=-1;
	var settingsFileID=null;
	var settingsFileMetadata=null;
	
	/**
	*
	*/
	this.getApplicationMode=function getApplicationMode ()
	{
		return (window.settingsObject.internal ['mode']);
	}
	/**
	*
	*/
	this.isDesktop=function isDesktop ()
	{
		if (window.settingsObject.internal ['mode']=='desktop')
		{
			return (true);
		}
		
		return (false);
	}	
	/**
	*
	*/
	this.getSettingsObject=function getSettingsObject ()
	{
		return (window.settingsObject);
	}
	/**
	*
	*/
	this.init=function init ()
	{
		pointer.ctatdebug ("init ()");
		
		//updateTimer=setInterval (pointer.updateSettings,5*1000*60); // Every 5 minutes

		pointer.load ();
	};
	/**
	*
	*/	
	this.updateSettings=function updateSettings ()
	{		
		pointer.ctatdebug ("init ()");
		
	};
	/**
	*
	*/
	this.load=function load ()
	{
		pointer.ctatdebug ("load ("+cloudUtils.getWorkspaceFolder ()+")");		
		
		cloudUtils.openFileByName (".settings", cloudUtils.getWorkspaceFolder (), pointer.settingsLoaded);
	};
	/**
	*
	*/
	this.save=function save ()
	{
		pointer.ctatdebug ("save ()");		
		
		if (settingsFileID==-1)
		{
			cloudUtils.saveFileAs (pointer.saveResult,
								   ".settings",
								   JSON.stringify (window.settingsObject),
								   cloudUtils.getWorkspaceFolder (),
								   "text/plain");
		}
		else
		{
			// Here we use the stored metadata to update a file in the cloude. Theoretically
			// that should be faster than doing a save as. This code still needs to be 
			// properly factored.
			
			cloudUtils.saveFileAs (pointer.saveResult,
								   ".settings",
								   JSON.stringify (window.settingsObject),
								   cloudUtils.getWorkspaceFolder (),
								   "text/plain");			
		}
	};
	/**
	*
	*/
	this.settingsLoaded=function settingsLoaded (data)
	{
		pointer.ctatdebug ("settingsLoaded ()");
		
		/*
		if (data==null)
		{
			pointer.save ();
		}
		else
		{
			settingsFileID=drive.getTempFileID ();
			settingsFileMetadata=drive.getTempFileMetadata ();
	
			console.log (JSON.stringify (data));
			
			window.settingsObject=jQuery.parseJSON(data);
		}
		*/
	};
	/**
	*
	*/
	this.saveResult=function saveResult (result)
	{
		pointer.ctatdebug ("saveResult ("+result+")");
		
	};
};

CTATSettings.prototype = Object.create(CTATBase.prototype);
CTATSettings.prototype.constructor = CTATSettings;
