/**-----------------------------------------------------------------------------
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

useDebugging=true;

var is_chrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;	
var toProcess=-1;
var toProcessCounter=0;

/**
*
*/
function setStatus (anAuthorized,aMessage)
{
	var statDiv=document.getElementById ("status");

	if (anAuthorized==false)
	{
		statDiv.innerHTML='<font color="#ffff00">' + aMessage+'</font>';
	}
	else
	{
		statDiv.innerHTML=aMessage;
	}
}

/**
* https://developers.google.com/api-client-library/javascript/features/authentication#popup
*/
function init ()
{	
	ctatdebug ("init ()");

	setStatus (false,"logging in ...");
	$("#loading").dialog('open').html('Please wait, initializing learner management system ...<br><center><img src="spinner_pdp.gif"></center>');
	
	$.ajax(
	{
		url: 'tutors.json',
		dataType: 'json', //json data type
		success: initContinue,
		error: processTutorError
	});	
	
	//startCookieRPC ();
}

/**
*
*/
function processTutorError (jqXHR, textStatus, errorThrown)
{
	ctatdebug ("processTutorError ("+textStatus+")");
	
	ctatdebug (introspect ("resp",jqXHR,"   ",5));
	
	ctatdebug (introspect ("resp",errorThrown,"   ",5));
}

/**
*
*/
function initContinue (data)
{
	ctatdebug ("initContinue ()");
	
	tutors=data;

	gapi.auth.init(processGoogleInit);
}

/**
*
*/
function processGoogleInit ()
{
	ctatdebug ("processGoogleInit ()");

	drive.authorize(false,function(authorized)
	{
		ctatdebug ("Authorization:"+authorized);
		
		if(!authorized)
		{
			setStatus (false,"Google authorization failed. Please login and authorize.");

			return;
		}
		
		// https://developers.google.com/+/web/api/rest/latest/people/get
		
		//status bar
		var request = gapi.client.plus.people.get
		({
			'userId' : 'me'
		});
	  
		request.execute(function(me)
		{
			if (me.displayName!="")
			{
				setStatus(true,"Logged in as: "+me.displayName);
			}
			else			
			{
				setStatus(true,"Successfully logged in");			
			}

			drive.retrieveAllFilesInFolder ("root",processRootList);
		});
	});	
}

/**
*
*/
function processRootList (result)
{
	ctatdebug ("processRootList ("+result.length+")");
	
	toProcess=result.length;
			
	for (var i=0;i<result.length;i++)
	{		
		drive.getMetadata (result [i].id,processRootEntry);
	}
}

/**
*
*/
function processRootEntry (meta)
{
	//ctatdebug ("processRootEntry ("+meta.title+")");
	
	if (meta.title=="SMUTutors")
	{
		titleFound=true;
		SMUAnchor=meta.id;

		ctatdebug ("Root identified: " + meta.id);
	}

	toProcessCounter++;

	if (toProcessCounter>=toProcess)
	{
		if (titleFound==false)
		{
			//ctatdebug ("Project folder not found, creating ...");

			drive.insertFolder ("SMUTutors",null,processSMUFolderCreated);
		}
		else
		{
			ctatdebug ("Project folder found, we're up and running");
			
			loadTutorState ();
		}
	}

	//ctatdebug (introspect ("resp",meta,"   ",5));
	//ctatdebug ("Process counter: " + toProcessCounter + ", running: " + running + ", titleFound: " + titleFound);
}

/**
*
*/
function processSMUFolderCreated (anEntry)
{
	ctatdebug ("processSMUFolderCreated ()");
	
	SMUAnchor=anEntry.id;
	
	ctatdebug ("Root identified: " + anEntry.id);	
	
	loadTutorState ();
}

/**
*
*/
function executeTutor (aSheetID)
{
	ctatdebug ("executeTutor ("+aSheetID+")");
	
	tempID=aSheetID;
	
	$("#loading").dialog('open').html('<p>Please wait, retrieving files ...</p><br><center><img src="spinner_pdp.gif">');
	
	var anEntry=getTutorEntry (tempID);
	
	if (anEntry==null)
	{
		alert ("Internal error: ID of template Google document not found");
		return;
	}
	
	if (anEntry.targetid=="")
	{	
		drive.copyFile (aSheetID,anEntry.title,launchSheet,SMUAnchor);
	}
	else
	{
		launchSheet	(anEntry.targetid);
	}
}

/**
*
*/
function launchSheet (aSheetID)
{
	ctatdebug ("launchSheet ("+aSheetID+")");
	
	window.open("https://drive.google.com/open?id="+aSheetID+"&authuser=0");
	$('#loading').html("<p>Result Complete...</p>");
	$("#loading").dialog('close');
	
	ctatdebug ("Setting image with id: id-" + tempID);
	
	$('#id-'+tempID).attr('src','status-active.png');
	
	var anEntry=getTutorEntry (tempID);
	
	anEntry.status="active";
	anEntry.targetid=aSheetID;

	updateTutors (); // Write updates back to Google drive
}

/**
*
*/
function generateTutorTable ()
{	
	ctatdebug ("generateTutorTable ()");
	
	var formatted="";
	
	for (var i=0;i<tutors.length;i++)
	{
		var aTutor=tutors [i];
	
		if (aTutor.status=="pending")
		{
			formatted+='<tr><td width="150"><a href="#" onClick="executeTutor(\''+tutors [i].id+'\')">'+tutors [i].title+'</a></td><td>'+tutors [i].description+'</td><td><a href="#" onClick="executeTutor(\''+tutors [i].id+'\')"><img id="id-'+tutors [i].id+'" src="status-pending.png" width="100"></a></td></tr>';
		}
		
		if (aTutor.status=="active")
		{
			formatted+='<tr><td width="150"><a href="#" onClick="executeTutor(\''+tutors [i].id+'\')">'+tutors [i].title+'</a></td><td>'+tutors [i].description+'</td><td><a href="#" onClick="executeTutor(\''+tutors [i].id+'\')"><img id="id-'+tutors [i].id+'" src="status-active.png" width="100"></a></td></tr>';
		}

		if (aTutor.status=="finished")
		{
			formatted+='<tr><td width="150"><a href="#" onClick="executeTutor(\''+tutors [i].id+'\')">'+tutors [i].title+'</a></td><td>'+tutors [i].description+'</td><td><a href="#" onClick="executeTutor(\''+tutors [i].id+'\')"><img id="id-'+tutors [i].id+'" src="status-finished.png" width="100"></a></td></tr>';
		}
	}

	formatted+='<tr><td width="150"><a href="#" onClick="window.open(\'https://www.surveymonkey.com/r/RMW5NTG\');">Survey</a></td><td>Upon completion of the four problems above, please take a few moments to complete this survey.  Your feedback is valuable, and will help us identify areas for improvement of the tutors.  Thank you.</td></tr>';
	
	$('#tutortable tr:last').after(formatted);
}

/**
*
*/
function processGoogleTutorDone (aSession)
{	
	ctatdebug ("processGoogleTutorDone ("+aSession+")");	
}
