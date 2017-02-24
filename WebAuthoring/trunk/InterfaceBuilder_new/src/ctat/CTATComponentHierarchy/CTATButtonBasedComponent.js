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
function CTATButtonBasedComponent (aClassName,
								   aName,
								   aDescription,
								   aX,
								   aY,
								   aWidth,
								   aHeight)
{
	CTATTutorableComponent.call(this,
					   aClassName, 
					   aName,
					   aDescription,
					   aX,
					   aY,
					   aWidth,
					   aHeight);

	var pointer=this;	
	var buttonText="";
	
	this.debug ("CTATButtonBasedComponent" + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	/**
	 * 
	 * @param aText
	 */
	this.setText=function setText (aText)
	{
		pointer.debug("setText (" + aText + ")");
	
		buttonText=aText;
	
		if (pointer.getComponent()!=null)
			pointer.getComponent().innerHTML=aText;
	};
	
	this.getText=function getText ()
	{
		return (buttonText);
	}

	/**
	 * 
	 * @param e
	 */
	this.processClick=function processClick (e)
	{
		pointer.debug ("processClick ("+e.currentTarget.getAttribute ("id")+" -> "+e.eventPhase+")");
				
		if (pointer.getEnabled()==true)
		{			
			commShell.gradeComponent (pointer);
		}
		else
			pointer.debug ("Component is disabled, not grading");		
    };		
}

CTATButtonBasedComponent.prototype = Object.create(CTATTutorableComponent.prototype);
CTATButtonBasedComponent.prototype.constructor = CTATButtonBasedComponent;

