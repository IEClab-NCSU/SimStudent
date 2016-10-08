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
var stringInput = "";

function CTATMobileTutorHandler()
{
	CTATBase.call(this, "CTATMobileTutorHandler","mobiletutorhandler");
	
	this.debug ("CTATMobileTutorHandler ()");
	this.debug ("Edit moved sI");
		
	/**
	 * 
	 * @param aComponent One of 'landscape' or 'portrait'
	 */
	this.processOrientationChange=function processOrientationChange (orientation)
	{
		this.debug ("processOrientationChange ("+orientation+")");
		
	}	
	
	/**
	 * The argument will be a component with a className of either one of:
	 * CTATTextInput, CTATTextField, CTATTextArea
	 * 
	 * @param aComponent
	 */
	this.processTextFocus=function processTextFocus (x,y,width,height)
	{
		this.debug ("processTextFocus ("+x+","+y+","+width+","+height+")");

		stringInput = "";
		
		this.manipulateKeyboard();

		//this.setText ("Hello: " + Math.random());
	}	
	
	/**
	 * Called by any outside code to push a new string into the currently
	 * selected text input component
	 */
	this.setText=function setText (aString)
	{
		this.debug ("setText ("+aString+")");
		
		if (selectedTextInput!=null)
		{
			this.debug ("Attempting to call HTML5 method on text object ...");
			
			selectedTextInput.setText (aString);
		}
		else
		{	
			this.debug ("Attempting to call AS3 method ...");
			
			var swfObject=document.getElementById("QA_Test");
			this.debug(swfObject);
			
			if (swfObject!=null)
			{
				//this.debugObject (swfObject);
												
				try
				{
					stringInput = stringInput + aString;
					this.debug (stringInput);
					swfObject.processExternalKeyboard(stringInput);

					this.debug ("Successfully called AS3 method");
				}
				catch(err)
				{
					this.debug ("Error description: " + err.message);
				}					
			}
			else
				this.debug ("Error: unable to obtain reference to swf object");										
		}

	}
	
	/**
	*
	*/
	this.manipulateKeyboard=function manipulateKeyboard()
	{
		
		this.debug ("manipulateKeyboard()");
		document.getElementById("keyboardUI").style.visibility="visible";
		
		
	};
}

CTATMobileTutorHandler.prototype = Object.create(CTATBase.prototype);
CTATMobileTutorHandler.prototype.constructor = CTATMobileTutorHandler;

