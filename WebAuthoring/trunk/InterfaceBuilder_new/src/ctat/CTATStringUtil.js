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
function CTATStringUtil ()
{	
	/**
	* Apparently AS3 expects a direct conversion between a string and a boolean to only
	* happen when the string is '0' or '1' for every other case we have to test manually.
	* So this method pretty much catches all varieties.
	*/	
	this.String2Boolean=function String2Boolean (aString)
	{
		switch(aString) 
		{     
			case "1":     
			case "true":     
			case "yes":     
			case "TRUE":     
			case "YES":
			case "Yes":					
						return true;     
			case "0":     
			case "false":     
			case "no":
			case "FALSE":     
			case "NO":
			case "No":
						return false;						
		}
	
		return (true);		
	}
	/**
	*	
	*/
	this.replaceString=function replaceString (thisStr,searchStr,replaceStr) 
	{	
		return (thisStr.replace(searchStr,replaceStr));
	};	
}
