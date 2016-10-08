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
function CTATArgument ()
{		
	var value="Undefined";
	var type="String";
	var format="text";
		
	/**
	*
	*/
	this.setValue=function setValue(aValue) 
	{
		value=aValue;
	};
	/**
	*	
	*/
	this.getValue=function getValue()
	{
		return value;			
	};
	/**
	*
	*/
	this.setType=function setType(aType) 
	{
		type=aType;
	};
	/**
	*	
	*/
	this.getType=function getType() 
	{
		return type;
	};
	/**
	*
	*/
	this.setFormat=function setFormat(aFormat) 
	{
		format=aFormat;
	};
	/**
	*	
	*/
	this.getFormat=function getFormat() 
	{
		return format;
	};
}
