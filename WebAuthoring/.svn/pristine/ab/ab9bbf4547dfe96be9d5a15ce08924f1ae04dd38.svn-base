/**-----------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2014-10-22 10:41:01 -0400 (Wed, 22 Oct 2014) $ 
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATTable.js $ 
 $Revision: 21448 $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:

	http://quocity.com/colresizable/

 */
 
goog.provide('CTATTableGoogle');

goog.require('CTATCompBase');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATNameTranslator');

/**
 *  
 */
CTATTableGoogle = function(aDescription,aX,aY,aWidth,aHeight)
{		
	CTATTutorableComponent.call(this,
								"CTATTableGoogle", 
								"__undefined__",
								aDescription,
								aX,
								aY,
								aWidth,
								aHeight);

	var pointer=this;
	pointer.isTabIndexable=false;
	var table=null;
	var nrRows=2;
	var nrColumns=2;
	var nameCheck="";
	var headerHeight=25;
	//var translator=new CTATNameTranslator ();
	
	var currentSelection="";
	var currentValue="";
	
	/**
	*
	*/
	this.getCurrentSelection=function getCurrentSelection ()
	{
		return (currentSelection);
	};
	/**
	*
	*/
	this.getCurrentValue=function getCurrentValue ()
	{
		return (currentValue);
	};
	/**
	*
	*/
	this.init=function init() 
	{
		pointer.ctatdebug("init (" + pointer.getName() + ")");
		
		this.setIsAbstractComponent (true);
	};
	/**
	 * 
	 */
	this.configFromDescription=function configFromDescription ()
	{
		pointer.ctatdebug ("configFromDescription ()");
		
	};
	/**
	 * 
	 */
	this.processSerialization=function processSerialization()
	{	
		pointer.ctatdebug ("processSerialization()");

	};	

	/**
	* Google specific function
	*/
	this.onProcessFailure=function onProcessFailure(error) 
	{
		pointer.ctatdebug ("onProcessFailure ("+error.message+")");
		
		return ("ok");
	}	
	/**
	* Sheet methods available for API-ification:
	* https://developers.google.com/apps-script/reference/spreadsheet/sheet
	*/
	this.onProcessSuccess=function onProcessSuccess(editedRange) 
	{
		pointer.ctatdebug ("onProcessSuccess ()");
		
		return (editedRange);
	};	
	
	/**
	* A dummy method with a dummy argument to make sure we have something
	* to call in case of a buggy message where we're really only interested
	* in the feedback message
	*/
	this.externalDummyMethod=function externalDummyMethod (aDummyBoolean)
	{
		pointer.ctatdebug ("externalDummyMethod ()");
	};
	
	/**
	 *  Ported from AS3
	 *
	 * Does not work correctly for IE because disabled components have their own font color
	 */		
	this.showCorrect=function showCorrect(aMessage)
	{
		pointer.ctatdebug("showCorrect("+correctColor+")");
	
		if (aMessage==null)
		{
			return;
		}
		
		if (typeof aMessage == 'undefined')
		{
			return;
		}
		
		if (aMessage.getSelection ().indexOf (".")==-1)
		{
			pointer.ctatdebug("Nothing to do for selection: " + aMessage.getSelection ());
			return;
		}
				
		var splitter=aMessage.getSelection ().split (".");
		var processed=nameTranslator.translateFromCTAT(splitter [1]);
						
		pointer.ctatdebug ("Calling google server side: setFontColor (" + processed + ",#00ff00)");
		
		try
		{
			google.script.run.withSuccessHandler(pointer.onProcessSuccess).withFailureHandler(pointer.onProcessFailure).setFontColor(processed,"#00ff00");				
		}
		catch (err)
		{
			pointer.ctatdebug ("Google App Script call failure: " + err.message);
		}			

		pointer.ctatdebug ("Google app script call executed");	 
	};
	/**
	 * Ported from AS3
	 */		
	this.showInCorrect=function showInCorrect(aMessage)
	{
		pointer.ctatdebug("showInCorrect("+incorrectColor+")");
      
		if (aMessage==null)
		{
			return;
		}
		
		if (typeof aMessage == 'undefined')
		{
			return;
		}
		
		if (aMessage.getSelection ().indexOf (".")==-1)
		{
			pointer.ctatdebug("Nothing to do for selection: " + aMessage.getSelection ());
			return;
		}		
				
		var splitter=aMessage.getSelection ().split (".");
		var processed=nameTranslator.translateFromCTAT(splitter [1]);
						
		pointer.ctatdebug ("Calling google server side: setFontColor (" + processed + ",#ff0000)");
		
		try
		{
			google.script.run.withSuccessHandler(pointer.onProcessSuccess).withFailureHandler(pointer.onProcessFailure).setFontColor(processed,"#ff0000");				
		}
		catch (err)
		{
			pointer.ctatdebug ("Google App Script call failure: " + err.message);
		}			
		
		pointer.ctatdebug ("Google app script call executed");	  
	};	
	/**
	 * Ported from AS3
	 */
	this.setHintHighlight=function setHintHighlight(newValue,complex,aMessage)
	{
		pointer.ctatdebug("setHintHighlight (" + newValue + ")");
		
		if (aMessage==null)
		{
			return;
		}
		
		if (typeof aMessage == 'undefined')
		{
			return;
		}
				
		var splitter=aMessage.getSelection ().split (".");
		var processed=nameTranslator.translateFromCTAT(splitter [1]);
						
		if (newValue==true)
		{
			pointer.ctatdebug ("Calling google server side: setHintHighlight (" + processed + ",#ffff00)");
		
			try
			{
				google.script.run.withSuccessHandler(pointer.onProcessSuccess).withFailureHandler(pointer.onProcessFailure).setHintHighlight(processed,"#ffff00");				
			}
			catch (err)
			{
				pointer.ctatdebug ("Google App Script call failure: " + err.message);
			}				
		}
		else
		{
			pointer.ctatdebug ("Calling google server side: setHintHighlight (" + processed + ",#ffffff)");

			try
			{
				google.script.run.withSuccessHandler(pointer.onProcessSuccess).withFailureHandler(pointer.onProcessFailure).setHintHighlight(processed,"#ffffff");
			}
			catch (err)
			{
				pointer.ctatdebug ("Google App Script call failure: " + err.message);
			}				
		}
		
		pointer.ctatdebug ("Google app script call executed");
	};	
	
	/**
	 * Ported from AS3
	 */
	this.UpdateTextArea=function UpdateTextArea(aValue,aMessage)
	{
		pointer.ctatdebug("UpdateTextArea (" + aValue + " -> "+aMessage.getSelection ()+")");
		
		var splitter=aMessage.getSelection ().split (".");
		
		pointer.ctatdebug ("Cleaned selection: " + splitter [1]);
		
		var processed=nameTranslator.translateFromCTAT(splitter [1]);
		
		pointer.ctatdebug ("Calling google server side: setText (" + processed + "," + aValue + ")");
		
		try
		{
			google.script.run.withSuccessHandler(pointer.onProcessSuccess).withFailureHandler(pointer.onProcessFailure).setText(processed,aValue.toString ());
			//google.script.run.withSuccessHandler(pointer.onProcessSuccess).withFailureHandler(pointer.onProcessFailure).setText(processed,JSON.stringify(aValue));
			//google.script.run.withSuccessHandler(pointer.onProcessSuccess).withFailureHandler(pointer.onProcessFailure).setText(processed,"test");
		}
		catch (err)
		{
			pointer.ctatdebug ("Google App Script call failure: " + err.message);
		}
		
		pointer.ctatdebug ("UpdateTextArea () done");
	};	

	/**
	 * 
	 */
	this.setText = function setText(aText,aMessage) 
	{
	    pointer.ctatdebug("setText (" + aText + ")");
	    
		this.UpdateTextArea (aText,aMessage);				
	};
	
	/**
	 * 
	 */
	this.setFormula = function setFormula(aValue,aMessage) 
	{
		pointer.ctatdebug("setFormula (" + aValue + ")");
		
		var splitter=aMessage.getSelection ().split (".");
		var processed=nameTranslator.translateFromCTAT(splitter [1]);
		
		pointer.ctatdebug ("Calling google server side: setFormula (" + processed + "," + aValue + ")");
		
		try
		{
			google.script.run.withSuccessHandler(pointer.onProcessSuccess).withFailureHandler(pointer.onProcessFailure).setFormula(processed,aValue);
		}
		catch (err)
		{
			pointer.ctatdebug ("Google App Script call failure: " + err.message);
		}

		try
		{
			google.script.run.withSuccessHandler(pointer.onProcessSuccess).withFailureHandler(pointer.onProcessFailure).setFontColor(processed,"#008000");
		}
		catch (err)
		{
			pointer.ctatdebug ("Google App Script call failure: " + err.message);
		}			
		
		pointer.ctatdebug ("Google app script call executed");			
	};	
		
	/**
	*
	*/
	this.UpdateTextField=function UpdateTextField (aText,aMessage)
	{
		pointer.ctatdebug("UpdateTextField (" + aText + ")");
	
		this.UpdateTextArea (aText,aMessage);
	};
	/**
	*
	*/
	this.processExternalGrading=function processExternalGrading (a1Notation,aContent)
	{
		pointer.ctatdebug("processExternalGrading (" + a1Notation + "," + aContent + ")");
		
		var mapped=nameTranslator.translateToCTAT (a1Notation);
		
		pointer.ctatdebug("Cell name mapped from: " + a1Notation + " to: " + mapped);
		
		// Turn this into a full namespace CTAT can work with. We might have to use the
		// name translator here to get a proper name in CTAT form
		currentSelection=(pointer.getName() + "." + mapped); 
		
		// Currently we don't transform the value, so this depends on what the Google
		// spreadsheet gives us
		currentValue=aContent;
		
		commShell.gradeComponent (pointer);
	};
}

CTATTableGoogle.prototype = Object.create(CTATTutorableComponent.prototype);
CTATTableGoogle.prototype.constructor = CTATTableGoogle;
