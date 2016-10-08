/**------------------------------------------------------------------------------------
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
function CTATFlashVars ()
{
	CTATBase.call (this, "CTATFlashVars", "flashvars");
	
	var pointer=this;
	var raw=null;
	
	/**
	 * 
	 * @param aData
	 */
	this.assignRawFlashVars=function assignRawFlashVars (aData)
	{
		this.raw=aData;
	};
	
	/**
	 * 
	 */
	this.getRawFlashVars=function getRawFlashVars ()
	{
		return (this.raw);
	};
	
	/**
	 * <b>[Required]</b> The local time zone name. 
	 * <p>Datashop prefers the use of tz database time zones, such as one from the "TZ" column in this <a href="http://en.wikipedia.org/wiki/List_of_zoneinfo_time_zones">List of zoneinfo time zones</a>. 
	 * Three-letter time zone abbreviations such as "EST" and "PST" are still valid, but are deprecated. If the field is not assigned
	 * the default value of "America/New_York" will be used.</p>
	 */
	this.setTimeZone=function setTimeZone (zone) 
	{
		var getDebugger = new CTATBase("CTATFlashVars", "");

		getDebugger.debug ("setTimeZone ("+zone+")");		
		
		var timeZone="";
	
		if ((zone==null) || (zone=="") || (zone==undefined)) 
		{
		    var tz = jstz.determine(); // Determines the time zone of the browser client
		    			
		    getDebugger.debug ("Assigning detected timezone: " + tz.name ());
		    
			timeZone = tz.name(); // Returns the name of the time zone eg "Europe/Berlin"
		}
		else 
		{
			if (zone.length > 50)
				zone = zone.substr(0, 50);
			
			if (zone.length == 3 || zone.length == 4)
			{
				getDebugger.debug("3 and 4 letter time zone abbreviations are deprecated. See list of tz database zone names for better options");
			}	
			
			timeZone = zone;
		}
		
		if (this.raw!=null)
		{
			this.raw ['timezone']=timeZone;
		}	
	};
	
	/**
	 * 
	 * @returns
	 */
	this.getTimeZone=function getTimeZone() 
	{
		if (this.raw!=null)
		{
			return (this.raw ['timezone']);
		}
		
		return "";
	};
}	

CTATFlashVars.prototype = Object.create(CTATBase.prototype);
CTATFlashVars.prototype.constructor = CTATFlashVars;
