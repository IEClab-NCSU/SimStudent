/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATSerialization/CTATComponentReference.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATComponentReference');
/**
 *
 */
CTATComponentReference = function(aRef,aDiv)
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
