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
function CTATClickableComponent(aClassName,
								aName,
								aDescription,
								aX,
								aY,
								aWidth,
								aHeight)
{
	CTATCompBase.call(this,
					  aClassName,
					  aName,
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);
					
	var pointer=this;
								
	/**
	 * Implement in child class
	 * @param e
	 */
	this.processClick=function processClick (e)
	{
		pointer.debug ("processClick ("+e.currentTarget.getAttribute ("id")+" -> "+e.eventPhase+")");
				
		if (pointer.getEnabled()==true)
		{		
			if ((pointer.getClassName ()=="CTATTextArea") || (pointer.getClassName ()=="CTATTextInput") || (pointer.getClassName ()=="CTATTextField"))
			{
				pointer.debug ("Info: click detected on a text based component, we should grade this type exclusively through backgrading");
			}	
			else
				commShell.gradeComponent (pointer);
		}
		else
			pointer.debug ("Component is disabled, not grading");		
    };     
}

CTATClickableComponent.prototype = Object.create(CTATCompBase.prototype);
CTATClickableComponent.prototype.constructor = CTATClickableComponent;
