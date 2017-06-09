/**-----------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $HeadURL$ 
 $Revision$ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
 
var GASQueue=new Queue ();
var GASCalling=false;
var GASNotifyOnEmpty=false;
var GASQueueProgressDiv="";
var GASQueueProgressLabel="";

/**
*
*/
function addCall (anRPCObject)
{
	console.log ("addCall ("+anRPCObject.getName ()+")");

	GASQueue.enqueue (anRPCObject);
	
	checkQueue ();
}

/**
*
*/
function checkQueue ()
{
	console.log ("checkQueue ()");
	
	if (GASCalling==true)
	{
		console.log ("GASCalling==true, bump");
		return;
	}

	var length = GASQueue.getLength();
	
	console.log ("Queue size: " + length);
			
	if (length>0)
	{	
		updateIndicator ("Processing " + GASQueue.getLength() + " cells, please wait");
	
		// Remember that after calling this the item is no longer on the queue
		var aCall = GASQueue.dequeue(); 
		
		console.log ("Matching: "+aCall.getName ()+" ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
		
		if (aCall.getName ()=="setFontColor")
		{
			console.log ("Calling: setFontColor ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
		
			GASCalling=true;
		
			google.script.run.withSuccessHandler(onGASSuccess).
							  withFailureHandler(onGASFailure).setFontColor (aCall.getTarget (),aCall.getArgument ());		
		}
		
		if (aCall.getName ()=="setFontColors")
		{
			console.log ("Calling: setFontColors ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
			
			GASCalling=true;
		
			google.script.run.withSuccessHandler(onGASSuccess).
							  withFailureHandler(onGASFailure).setFontColors (aCall.getTarget (),aCall.getArgument ());		
		}		
		
		if (aCall.getName ()=="showCorrect")
		{
			console.log ("Calling: showCorrect ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
			
			GASCalling=true;
		
			google.script.run.withSuccessHandler(onGASSuccess).
							  withFailureHandler(onGASFailure).showCorrect (aCall.getTarget (),aCall.getArgument ());		
		}

		if (aCall.getName ()=="showIncorrect")
		{
			console.log ("Calling: showIncorrect ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
			
			GASCalling=true;
		
			google.script.run.withSuccessHandler(onGASSuccess).
							  withFailureHandler(onGASFailure).showIncorrect (aCall.getTarget (),aCall.getArgument ());		
		}
		
		if (aCall.getName ()=="setText")
		{
			console.log ("Calling: setText ...");
		
			GASCalling=true;
		
			if (aCall.getTarget ().indexOf(":")==-1)
			{
				console.log ("Calling: setText ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
			
				google.script.run.withSuccessHandler(onGASSuccess).
								  withFailureHandler(onGASFailure).setText (aCall.getTarget (),aCall.getArgument ());		
			}
			else
			{
				console.log ("Calling: setText ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
			
				google.script.run.withSuccessHandler(onGASSuccess).
								  withFailureHandler(onGASFailure).setTexts (aCall.getTarget (),aCall.getArgument ());					
			}
		}

		if (aCall.getName ()=="setTexts")
		{
			console.log ("Calling: setTexts ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
			
			GASCalling=true;
		
			google.script.run.withSuccessHandler(onGASSuccess).
							  withFailureHandler(onGASFailure).setTexts (aCall.getTarget (),aCall.getArgument ());		
		}		
		
		if (aCall.getName ()=="setFormula")
		{
			console.log ("Calling: setFormula ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
			
			GASCalling=true;
		
			google.script.run.withSuccessHandler(onGASSuccess).
							  withFailureHandler(onGASFailure).setFormula (aCall.getTarget (),aCall.getArgument ());		
		}

		if (aCall.getName ()=="setFormulas")
		{
			console.log ("Calling: setFormulas ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
			
			GASCalling=true;
		
			google.script.run.withSuccessHandler(onGASSuccess).
							  withFailureHandler(onGASFailure).setFormulas (aCall.getTarget (),aCall.getArgument ());		
		}	
		
		if (aCall.getName ()=="setBackground")
		{
			console.log ("Calling: setBackground ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
			
			GASCalling=true;
		
			google.script.run.withSuccessHandler(onGASSuccess).
							  withFailureHandler(onGASFailure).setBackground (aCall.getTarget (),aCall.getArgument ());		
		}
		
		if (aCall.getName ()=="setHintHighlight")
		{
			console.log ("Calling: setHintHighlight ("+aCall.getTarget ()+","+aCall.getArgument ()+")");
			
			GASCalling=true;
		
			google.script.run.withSuccessHandler(onGASSuccess).
							  withFailureHandler(onGASFailure).setHintHighlight (aCall.getTarget (),aCall.getArgument ());		
		}		
		
		if (aCall.getName ()=="resetOnEditQueue")
		{
			console.log ("Calling: resetOnEditQueue ()");
			
			GASCalling=true;
		
			google.script.run.withSuccessHandler(onGASSuccess).
							  withFailureHandler(onGASFailure).resetOnEditQueue();
							  
			GASNotifyOnEmpty=true;
		}		
		
		if (GASCalling==false)
		{
			console.log ("Potential problem, unmatched GAS call!");
		}
	}
	else
	{
		console.log ("GAS RPC queue empty, good");
		
		hideIndicator ();
		
		if (GASNotifyOnEmpty==true)
		{
			console.log ("The commshell requested a notification when the queue is empty, calling ...");
		
			if (useWorkedExample==false)
			{
				CTATCommShell.commShell.showFeedback ("The tutor is ready, you can now freely interact with the spreadsheet");
			}
			
			GASNotifyOnEmpty=false; // make sure we don't call it again
		}
	}
}

/**
* Google specific function
*/
function onGASFailure(error) 
{
	console.log ("onGASFailure ("+error.message+")");
	
	GASCalling=false;
	
	checkQueue ();
}

/**
*
*/
function onGASSuccess(editedRange) 
{
	console.log ("onGASSuccess ()");
	
	GASCalling=false;

	// First let the code process the actual result

	//onEditSuccess (editedRange);
	
	// Then see if something queued up another call ...
	
	checkQueue ();
}

/**
*
*/
function hideIndicator ()
{
	if (GASQueueProgressDiv!="")
	{
		var GASIndicator=document.getElementById(GASQueueProgressDiv);
		
		if (GASIndicator!=null)
		{
			GASIndicator.style.display="none";
		}
	}	
}

/**
*
*/
function showIndicator ()
{
	if (GASQueueProgressDiv!="")
	{
		var GASIndicator=document.getElementById(GASQueueProgressDiv);
		
		if (GASIndicator!=null)
		{
			GASIndicator.style.display="block";
		}		
	}	
}

/**
*
*/
function updateIndicator (aMessage)
{
	showIndicator (); // Just in case
			
	if (GASQueueProgressLabel!="")
	{		
		var GASLabel=document.getElementById(GASQueueProgressLabel);
	
		if (GASLabel!=null)
		{
			GASLabel.innerHTML=aMessage;
		}
	}	
}
