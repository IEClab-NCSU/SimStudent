/**-----------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $HeadURL$ 
 $Revision$ 

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
function RPCObject (aName,aTarget,anArgument)
{
	ctatdebug ("RPCObject ("+aName+","+aTarget+","+anArgument+")");

	var name=aName;
	var target=aTarget;
	var argument=anArgument;
	
	/**
	*
	*/
	this.getName=function getName()
	{
		return (name);
	};
	
	/**
	*
	*/
	this.getTarget=function getTarget()
	{
		return (target);
	};

	/**
	*
	*/
	this.getArgument=function getArgument()
	{
		return (argument);
	};	
}
