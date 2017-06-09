/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATSerialization/CTATComponentReference.js $
 $Revision: 23157 $

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
};

CTATComponentReference.components = {};
CTATComponentReference.list = function() {
	for (var comp in CTATComponentReference.components) {
		var ref = CTATComponentReference.components[comp];
		ctatdebug("Component: "+ref.getElement().getName()+", with div: "+ref.getDiv().id);
	}
};
CTATComponentReference.getComponentFromID = function(anId) {
	var components = CTATComponentReference.components;
	if (components.hasOwnProperty(anId)) {
		var ref = components[anId];
		if (!ref.getElement()) {
			return null;
		}
		return ref.getElement();
	}
	return null;
};

CTATComponentReference.add = function(aComponent,aDiv)
{
	var ref = new CTATComponentReference(aComponent,aDiv);
	CTATComponentReference.components[ref.getDiv().getAttribute("id")] = ref;

	return ref;
};