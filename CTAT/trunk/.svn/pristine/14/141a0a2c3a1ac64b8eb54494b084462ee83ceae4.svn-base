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

var wizardActive=false;

/**
*
*/
function iconBarSetup ()
{
	enableIconClick ();
}

/**
*
*/
function enableIconClick ()
{
	jQuery('#iconMoodle').on('click', showMoodle);
	jQuery('#iconSCORM').on('click', showSCORM);
	jQuery('#iconEdX').on('click', showEdX);
	jQuery('#iconOLI').on('click', showOLI);
	jQuery('#iconTutorShop').on('click', showTutorShop);
	jQuery('#iconXAPI').on('click', showXAPI);
}

/**
*
*/
function disableIconClick ()
{
	jQuery('#iconMoodle').off('click');
	jQuery('#iconSCORM').off('click');
	jQuery('#iconEdX').off('click');
	jQuery('#iconOLI').off('click');
	jQuery('#iconTutorShop').off('click');
	jQuery('#iconXAPI').off('click');
}

/**
*
*/
function showMoodle()
{
	console.log ("showMoodle ()");
	
	if (wizardActive==true)
	{
		return;
	}
	
	disableIconClick ();
	
	document.getElementById ('wizardcontents').src="moodle.html";	
	jQuery("#wizard").toggle();
}

/**
*
*/
function showSCORM()
{
	console.log ("showSCORM ()");
	
	if (wizardActive==true)
	{
		return;
	}	
	
	disableIconClick ();	
	
	document.getElementById ('wizardcontents').src="scorm.php";	
	jQuery("#wizard").toggle();
}

/**
*
*/
function showEdX()
{
	console.log ("showEdX ()");
	
	if (wizardActive==true)
	{
		return;
	}	
	
	disableIconClick ();
	
	document.getElementById ('wizardcontents').src="edx.html";	
	jQuery("#wizard").toggle();
}

/**
*
*/
function showOLI ()
{
	console.log ("showOLI ()");
	
	if (wizardActive==true)
	{
		return;
	}	
	
	disableIconClick ();	
	
	document.getElementById ('wizardcontents').src="oli.html";		
	jQuery("#wizard").toggle();
}

/**
*
*/	
function showTutorShop ()
{
	console.log ("showTutorShop ()");
	
	if (wizardActive==true)
	{
		return;
	}	
	
	disableIconClick ();
	
	document.getElementById ('wizardcontents').src="tutorshop.html";		
	jQuery("#wizard").toggle();
}

/**
*
*/
function showXAPI ()
{
	console.log ("showXAPI ()");
	
	if (wizardActive==true)
	{
		return;
	}	
	
	disableIconClick ();
	
	document.getElementById ('wizardcontents').src="xapi.html";		
	jQuery("#wizard").toggle();
}

/**
*
*/	
/* 
$(document).ready(function() 
{
	console.log ("envwizard js ()");
	
	$("#menu").menu();
		
	iconBarSetup ();
});	
*/

/**
* Since we're now running in various execution environments we need to carefull
* how we start. I've created a function instead of reacting to the document ready
* event so that we can call all these startup pieces from one place. I've moved
* this call to startTutordesk ()
*/
function envSetup ()
{
	console.log ("envSetup ()");
	
	$("#menu").menu();
		
	iconBarSetup ();
}
