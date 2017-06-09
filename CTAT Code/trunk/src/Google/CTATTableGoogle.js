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

 */
 
/**
 *  
 */
var CTATTableGoogle = function(aDescription,aX,aY,aWidth,aHeight)
{		
	CTAT.Component.Base.Tutorable.call(this,
								"CTATTableGoogle", 
								"table",
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
	* Gets the dimensions of an array. The return value is a 1 dimensional
	* array where each entry is the length of the array at that index. Found
	* at: http://stackoverflow.com/questions/10237615/get-size-of-dimensions-in-array
	* For example: 
	*
	*      size ([[1, 1, 1], [1, 1, 1]])
	*
	* would return: 
	*
	*      [2, 3]
	*/
	this.size=function size(ar)
	{
		var row_count = ar.length;
		var row_sizes = [];
		
		for(var i=0;i<row_count;i++)
		{
			row_sizes.push(ar[i].length);
		}
	  
		return [row_count, Math.min.apply(null, row_sizes)];
	};	
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
		
		CTATCommShell.commShell.showFeedback (error.message);
		
		return ("ok");
	};	
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
	*
	*/	
	this.setEnabled=function setEnabled (aValue)
	{
		pointer.ctatdebug ("Does absolutely nada");
	};
	
	/**
	*
	*/
	this.reset=function reset (aValue,aMessage)
	{
		pointer.ctatdebug("reset ()");
		
		pointer.ctatdebug ("Value: " + aValue);
		
		if (aMessage.getSelection ().indexOf (":")!=-1)
		{		
			var json=null;
			
			try
			{
				json=jQuery.parseJSON(aValue);
			}
			catch (e)
			{
				pointer.ctatdebug ("Error parsing JSON: " + e);
				return;
			}
			
			pointer.ctatdebug ("Parsed, determining size ...");
			
			var aSize=pointer.size (json);
			
			pointer.ctatdebug ("Emptying " + aSize [0] + " by " + aSize [1]);
			
			for (var i=0;i<aSize [0];i++)
			{
				for (var j=0;j<aSize [1];j++)
				{
					json [i][j]=" ";
					
					pointer.ctatdebug ("["+i+"]["+j+"] = " + json [i][j]);
				}			
			}
			
			aValue=JSON.stringify(json);
			
			pointer.ctatdebug ("Value is now: " + aValue);
		}
		else
		{
			aValue=" ";
		}
	
		if (aMessage==null)
		{
			return;
		}
		
		if (typeof aMessage == 'undefined')
		{
			return;
		}
		
		pointer.ctatdebug("We have a message, parsing selection ...");
		
		pointer.ctatdebug("Selection: " + aMessage.getSelection ());
		
		if (aMessage.getSelection ().indexOf (".")==-1)
		{
			pointer.ctatdebug("Nothing to do for selection: " + aMessage.getSelection ());
			return;
		}
				
		var processed=nameTranslator.translateFromCTAT (aMessage.getSelection ());
		var splitter=aMessage.getSelection ().split ("."); // take off any table. namespace
		if (splitter.length==2)
		{
			processed=nameTranslator.translateFromCTAT(splitter [1]);
		}
		
		pointer.ctatdebug ("Final range for processing: " + processed);

		addCall (new RPCObject ("setText",processed,aValue));
		
		pointer.ctatdebug ("Google app script call executed");	 	
	};
	
	/**
	*
	*/
	this.resetBasic=function resetBasic (aValue,aMessage)
	{
		pointer.ctatdebug("resetBasic ()");
		
		if (aMessage==null)
		{
			return;
		}
		
		if (typeof aMessage == 'undefined')
		{
			return;
		}		
				
		if (aMessage.getSelection ().indexOf (":")!=-1)
		{		
			var json=null;
			
			try
			{
				json=jQuery.parseJSON(aValue);
			}
			catch (e)
			{
				pointer.ctatdebug ("Error parsing JSON: " + e);
				return;
			}
			
			pointer.ctatdebug ("Parsed, determining size ...");
			
			var aSize=pointer.size (json);
			
			pointer.ctatdebug ("Emptying " + aSize [0] + " by " + aSize [1]);
			
			for (var i=0;i<aSize [0];i++)
			{
				for (var j=0;j<aSize [1];j++)
				{
					json [i][j]=" ";
					
					pointer.ctatdebug ("["+i+"]["+j+"] = " + json [i][j]);
				}			
			}
		}
			
		pointer.ctatdebug("We have a message, parsing selection ...");
		
		pointer.ctatdebug("Selection: " + aMessage.getSelection ());
		
		if (aMessage.getSelection ().indexOf (".")==-1)
		{
			pointer.ctatdebug("Nothing to do for selection: " + aMessage.getSelection ());
			return;
		}
				
		var processed=nameTranslator.translateFromCTAT (aMessage.getSelection ());
		var splitter=aMessage.getSelection ().split ("."); // take off any table. namespace
		if (splitter.length==2)
		{
			processed=nameTranslator.translateFromCTAT(splitter [1]);
		}
		
		pointer.ctatdebug ("Final range for processing: " + processed);
		
		addCall (new RPCObject ("setFontColor",processed,"#ffffff"));
		
		addCall (new RPCObject ("setHintHighlight",processed,"#ffffff"));
				
		addCall (new RPCObject ("setText",processed,""));
		
		pointer.ctatdebug ("Google app script (resetBasic) call executed");		
	};	
	
	/**
	*
	*/
	this.resetInternal=function resetInternal (aSelection)
	{
		pointer.ctatdebug("resetInternal ("+aSelection+")");
		
		var processed=nameTranslator.translateFromCTAT(aSelection);
		
		pointer.ctatdebug ("Final range for processing: " + processed);
		
		var manufactoredJSON=nameTranslator.manufactorJSON (processed,"");		
				
		addCall (new RPCObject ("setFontColor",processed,"#000000"));

		addCall (new RPCObject ("setHintHighlight",processed,"#ffffff"));
				
		addCall (new RPCObject ("setText",processed,manufactoredJSON));
		
		pointer.ctatdebug ("Google app script (resetInternal) call executed");		
	};		
	
	/**
	 *  Ported from AS3
	 *
	 * Does not work correctly for IE because disabled components have their own font color
	 */		
	this.showCorrect=function showCorrect(aMessage)
	{
		pointer.ctatdebug("showCorrect()");
	
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
				
		var processed=nameTranslator.translateFromCTAT (aMessage.getSelection ());
		var splitter=aMessage.getSelection ().split ("."); // take off any table. namespace
		if (splitter.length==2)
		{
			processed=nameTranslator.translateFromCTAT(splitter [1]);
		}
						
		pointer.ctatdebug ("Calling google server side: setFontColor (" + processed + ",#009900)");
		
		addCall (new RPCObject ("showCorrect",processed,"#009900"));
		
		pointer.ctatdebug ("Google app script (showCorrect) call executed");	 
	};
	
	this.removeCorrect = function () {};
	
	/**
	 * Ported from AS3
	 */		
	this.showInCorrect=function showInCorrect(aMessage)
	{
		pointer.ctatdebug("showInCorrect()");
      
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
				
		var processed=nameTranslator.translateFromCTAT (aMessage.getSelection ());
		var splitter=aMessage.getSelection ().split ("."); // take off any table. namespace
		
		if (splitter.length==2)
		{
			processed=nameTranslator.translateFromCTAT(splitter [1]);
		}
						
		pointer.ctatdebug ("Calling google server side: setFontColor (" + processed + ",#ff0000)");

		addCall (new RPCObject ("showIncorrect",processed,"#ff0000"));
		
		pointer.ctatdebug ("Google app script (showInCorrect) call executed");	  
	};	
	
	this.remvoeInCorrect = function () {};
	
	/**
	 * Ported from AS3
	 */
	this.setHintHighlight=function setHintHighlight(newValue,aSomething,aMessage)
	{
		pointer.ctatdebug("setHintHighlight (" + newValue + ")");
		
		if (aMessage==null)
		{
			pointer.ctatdebug("Error: message is null");
			return;
		}
		
		if (typeof aMessage == 'undefined')
		{
			pointer.ctatdebug("Error: message is of undefined type");
			return;
		}
				
		var processed=nameTranslator.translateFromCTAT (aMessage.getSelection ());
		var splitter=aMessage.getSelection ().split ("."); // take off any table. namespace
		if (splitter.length==2)
		{
			processed=nameTranslator.translateFromCTAT(splitter [1]);
		}
						
		if (newValue==true)
		{
			pointer.ctatdebug ("Calling google server side: setHintHighlight (" + processed + ",#ffff00)");
					
			addCall (new RPCObject ("setHintHighlight",processed,"#ffff00"));
		}
		else
		{
			pointer.ctatdebug ("Calling google server side: setHintHighlight (" + processed + ",#ffffff)");
			
			addCall (new RPCObject ("setHintHighlight",processed,"#ffffff"));
		}
		
		pointer.ctatdebug ("Google app script (setHintHighlight) call executed");
	};	
	
	/**
	 * Ported from AS3, can handle things like: C1:C4;[[1],[2],[3],[4]]
	 */
	this.UpdateTextArea=function UpdateTextArea(aValue,aMessage)
	{
		pointer.ctatdebug("UpdateTextArea (" + aValue + " -> "+typeof aMessage.getSelection ()+")");
		
		var splitter=aMessage.getSelection ().split (".");
		
		var prior=splitter [1].toString ();
		
		pointer.ctatdebug ("Cleaned selection: " + prior);
			
		if (prior.indexOf (";")!=1)
		{
			var formatter=prior.split (";");
			
			//pointer.ctatdebug ("Calling google server side (on range): setText (" + formatter [0] + "," + formatter [1] + ")");
			
			addCall (new RPCObject ("setText",formatter,aValue.toString ()));
		}
		else
		{
			var processed=nameTranslator.translateFromCTAT(splitter [1]);
			
			pointer.ctatdebug ("Calling google server side: setText (" + processed + "," + aValue + ")");

			addCall (new RPCObject ("setText",processed,aValue.toString ()));
		}
		
		pointer.ctatdebug ("Google app script (UpdateTextArea) call executed");
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
		
		var processed=nameTranslator.translateFromCTAT (aMessage.getSelection ());
		var splitter=aMessage.getSelection ().split ("."); // take off any table. namespace
		if (splitter.length==2)
		{
			processed=nameTranslator.translateFromCTAT(splitter [1]);
		}
		
		pointer.ctatdebug ("Calling google server side: setFormula (" + processed + "," + aValue + ")");
		
		addCall (new RPCObject ("setFormula",processed,aValue));
		
		addCall (new RPCObject ("setFontColor",processed,"#008000"));
		
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
		
		this.updateSAI();
		this.ctatdebug('processExternalGrading() post updateSAI');
		this.processAction();
		this.ctatdebug('processExternalGrading() post processAction');
		//CTATCommShell.commShell.gradeComponent (pointer);
	};
	this.updateSAI = function() {
		this.setSAI(this.getCurrentSelection (),
				"UpdateTextArea",
				this.getCurrentValue ());

	};
	this.setNotGraded = function(){};
};

CTATTableGoogle.prototype = Object.create(CTAT.Component.Base.Tutorable.prototype);
CTATTableGoogle.prototype.constructor = CTATTableGoogle;
