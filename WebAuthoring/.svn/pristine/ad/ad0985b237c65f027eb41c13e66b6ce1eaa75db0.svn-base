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
function CTATStyle (name, value)
{
	this.styleName=name;
	this.styleValue=value;
}

/**
 * 
 */
function CTATParameter (name, value)
{
	this.paramName=name;
	this.paramValue=value;
}

/**
 *  
 */
function CTATComponentDescription ()
{
	CTATBase.call(this, "CTATComponentDescription", "");
	
	this.type="Unknown";
	this.name="Unknown";
	this.groupName="Unknown"; //For things like radio buttons
	this.x=0;
	this.y=0;
	this.width=0;
	this.height=0;
	this.styles=new Array ();
	this.params=new Array ();

	this.pointer=this;
	
	this.componentPointer=null;

	/**
	 * 
	 */
	this.addStyle=function addStyle (aStyle)
	{
		pointer.debug ("addStyle ()");
		
		this.styles.push(aStyle);
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