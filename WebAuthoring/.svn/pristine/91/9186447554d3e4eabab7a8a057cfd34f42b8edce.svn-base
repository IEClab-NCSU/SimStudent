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
var swfObjName="SWF";
var mode="disabled";

function CTATMobileTutorHandler(aName,aMode)
{
	CTATBase.call(this, "CTATMobileTutorHandler","mobiletutorhandler");
	
	this.debug ("CTATMobileTutorHandler ()");
	this.debug ("Edit moved sI");
	
	var swfObjName=aName;
	var mode=aMode;
	var onDevice=false;
	
	if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) 
	{
		onDevice=true;
	}
	
	/**
	*
	*/	
	this.error=function error (aMessage)
	{
		this.debug (aMessage);
		
		alert (aMessage);
	}
		
	/**
	 * 
	 * @param aComponent One of 'landscape' or 'portrait'
	 */
	this.processOrientationChange=function processOrientationChange (orientation)
	{
		this.debug ("processOrientationChange ("+orientation+")");
		
	}	
	
	/**
	*
	*/
	this.getEnabled=function getEnabled ()
	{
		if (mode=="disabled")
		{
			return (false);
		}

		if ((mode=="auto") && (onDevice==false))
		{
			return (false);
		}		
		
		return (true);
	}
	
	/**
	 * The argument will be a component with a className of either one of:
	 * CTATTextInput, CTATTextField, CTATTextArea
	 * 
	 * @param aComponent
	 */
	this.processTextFocus=function processTextFocus (x,y,width,height,componentText)
	{
		this.debug ("processTextFocus ("+x+","+y+","+width+","+height+","+componentText+")");

		if (mode=="disabled")
		{
			return;
		}

		if ((mode=="auto") && (onDevice==false))
		{
			return;
		}
		
		stringInput = "";
		
		this.manipulateKeyboard();

		//this.setText ("Hello: " + Math.random());
	}	
	
	/**
	*
	*/
	function removeFocus ()
	{
		this.debug ("removeFocus ()");
		
	}	
	
	/**
	*
	*/
	this.processEnter=function processEnter ()
	{
		this.debug ("processEnter ()");
		
		if (mode=="disabled")
		{
			return;
		}
		
		if ((mode=="auto") && (onDevice==false))
		{
			return;
		}		
		
		var swfObject=getSafeElementById (swfObjName);			
			
		if (swfObject!=null)
		{												
			try
			{
				swfObject.processExternalEnter ();

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
	
	/**
	 * Called by any outside code to push a new string into the currently
	 * selected text input component
	 */
	this.setText=function setText (aString)
	{	
		this.debug ("setText ("+aString+")");
		
		if (mode=="disabled")
		{
			return;
		}
		
		if ((mode=="auto") && (onDevice==false))
		{
			return;
		}		
		
		if (selectedTextInput!=null)
		{
			this.debug ("Attempting to call HTML5 method on text object ...");
			
			var previousString=selectedTextInput.getText ();
			
			selectedTextInput.setText (previousString+aString);
		}
		else
		{	
			this.debug ("Attempting to call AS3 method ...");
			
			var swfObject=getSafeElementById(swfObjName);
			
			if (swfObject!=null)
			{												
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
		
		if (mode=="disabled")
		{
			return;
		}
		
		if ((mode=="auto") && (onDevice==false))
		{
			return;
		}		
		
		this.hideKeyboard ();
		
		getSafeElementById("keyboardUI").style.visibility="visible";				
	};
	/**
	*
	*/
	this.hideKeyboard=function hideKeyboard () 
	{
		document.activeElement.blur();
		getSafeElementById("input").blur();
		//$("input").blur();
	};
	/**
	*
	*/	
	this.hideCustomKeyboard=function hideCustomKeyboard ()
	{
		getSafeElementById("keyboardUI").style.visibility="visible";
	}
}

CTATMobileTutorHandler.prototype = Object.create(CTATBase.prototype);
CTATMobileTutorHandler.prototype.constructor = CTATMobileTutorHandler;

/**
*
*/
function processTextFocus (x,y,width,height,componentText)
{
	mobileAPI.processTextFocus (x,y,width,height,componentText);
}
/**
*
*/
function processFocusOut ()
{
	closeK ();
}
