/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATTutorableComponent.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATTutorableComponent');

goog.require('CTATClickableComponent');
/**
 *
 */
CTATTutorableComponent = function(aClassName,
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
									//showFeedback=true; // unused and undeclared
									break;
			case TUTOR_NO_FEEDBACK :
									defaultTutorMe=defaultRecordMe=true;
									//showFeedback=false; // unused and undeclared
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