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
function CTATTutorableComponent (aClassName,
								 aName,
					 			 aDescription,
					 			 aX,
					 			 aY,
					 			 aWidth,
					 			 aHeight)
{	
	CTATClickableComponent.call(this,
								aClassName, 
								aName,
								aDescription,
								aX,
								aY,
								aWidth,
								aHeight);

	var TUTOR="Tutor";
	var TUTOR_NO_FEEDBACK="Tutor but no visual feedback";
	var DO_NOT_TUTOR="Do not tutor";
	
	var defaultTutorMe=true;
	var defaultRecordMe=true;

	var _tutorComponent="Tutor";
	
	this.setTutorComponent=function setTutorComponent (theValue)
	{
		_tutorComponent=theValue;
		
		switch (_tutorComponent) 
		{
			case TUTOR :
									defaultTutorMe=defaultRecordMe=true;
									showFeedback=true;
									break;
			case TUTOR_NO_FEEDBACK :
									defaultTutorMe=defaultRecordMe=true;
									showFeedback=false;
									break;
			case DO_NOT_TUTOR :
									defaultTutorMe=false;
									defaultRecordMe=true;
									break;
		}
	};
	
	this.getTutorComponent=function getTutorComponent ()
	{
		return (_tutorComponent);
	};						
}

CTATTutorableComponent.prototype = Object.create(CTATClickableComponent.prototype);
CTATTutorableComponent.prototype.constructor = CTATTutorableComponent;