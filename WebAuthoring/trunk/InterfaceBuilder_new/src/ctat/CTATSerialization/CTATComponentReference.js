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
function CTATComponentReference (aRef,aDiv)
{		
	var compReference=aRef;
	var div=aDiv;
	
	/** 
	 * @param aComponent
	 */
	this.setElement=function setElement (aComponent)
	{
		compReference=aComponent;
	};
	/**
	 * 
	 * @returns
	 */
	this.getElement=function getElement ()
	{
		return (compReference);
	};
	/**
	 * 
	 * @param aDiv
	 */
	this.setDiv=function setDiv (aDiv)
	{
		div=aDiv;
	};
	/**
	 * 
	 * @returns
	 */
	this.getDiv=function getDiv ()
	{
		return (div);
	};
}
