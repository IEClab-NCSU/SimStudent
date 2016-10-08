/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATSerialization/CTATComponentDescription.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATComponentDescription');

goog.require('CTATBase');
goog.require('CTATParameter');
goog.require('CTATStyle');
/**
 *
 */
CTATComponentDescription = function()
{
	CTATBase.call(this, "CTATComponentDescription", "");

	this.type="Unknown";
	this.name="Unknown";
	this.groupName="Unknown"; //For things like radio buttons
	this.x=0;
	this.y=0;
	this.width=0;
	this.height=0;
	this.zIndex=-1;
	this.tabIndex=-1;
	this.styles=new Array ();
	this.params=new Array ();

	var pointer=this;

	this.componentPointer=null;

	/**
	 *
	 */
	this.addStyle=function addStyle (aStyle)
	{
		pointer.ctatdebug ("addStyle ()");

		this.styles.push(aStyle.trim());
	};

	/**
	 *
	 */
	this.setComponentPointer=function setComponentPointer (aPointer)
	{
		this.componentPointer=aPointer;
	};

	/**
	 *
	 */
	this.getComponentPointer=function getComponentPointer ()
	{
		return (this.componentPointer);
	};
}

CTATComponentDescription.prototype = Object.create(CTATBase.prototype);
CTATComponentDescription.prototype.constructor = CTATComponentDescription;