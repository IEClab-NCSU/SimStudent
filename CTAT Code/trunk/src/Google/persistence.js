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

var running=false;
var drive = new GoogleDrive();
var tutors="";
var titleFound=false;
var SMUAnchor=""; // the Google folder id of where we store and track all the tutor related files
var tempID="";
var tutorsFileID=null;
var tutorsFileMetadata=null;

/**
*
*/
function getTutorEntry (anID)
{
	ctatdebug ("getTutorEntry ("+anID+")");
	
	var i=0;
	
	for (i=0;i<tutors.length;i++)
	{
		if (tutors [i].id===anID)
		{
			return (tutors [i]);
		}
	}
	
	return (null);
}

/**
*
*/
function updateTutors ()
{
	ctatdebug ("updateTutors ()");
	
	//ctatdebug (JSON.stringify (tutors));
	
	drive.updateFile (tutorsFileID,
					  tutorsFileMetadata,
					  JSON.stringify (tutors),
					  processTutorsFileSaved);
}

/**
*
*/
function loadTutorState ()
{
	ctatdebug ("loadTutorState ()");
	
	drive.getFile ("tutors.json",SMUAnchor,processJSONLoaded);
}

/**
*
*/
function processJSONLoaded (aResult)
{
	ctatdebug ("processJSONLoaded ()");
		
	if (aResult===null)
	{
		ctatdebug ("tutors.json not found, creating ...");
	
		drive.insertFile ("tutors.json",JSON.stringify (tutors),"text/plain",SMUAnchor,processJSONLoaded);
		return;
	}
	
	tutorsFileID=drive.getTempFileID ();
	tutorsFileMetadata=drive.getTempFileMetadata ();
	
	ctatdebug ("Set tutors.json fileid to: " + tutorsFileID);
	
	ctatdebug (aResult);
	
	tutors=jQuery.parseJSON(aResult);
	
	running=true;

	$("#loading").dialog('close');

	generateTutorTable ();

	$("#hider").css("display","block");
}

/**
*
*/
function processTutorsFileSaved ()
{
	ctatdebug ("loadTutorState ()");
}
