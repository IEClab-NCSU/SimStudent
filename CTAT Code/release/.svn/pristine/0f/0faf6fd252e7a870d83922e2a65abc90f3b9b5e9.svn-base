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

goog.provide("CTATAssistments");

goog.require("CTATBase");

var assistmentsTimerVar;

// Your iframe must post multiple messages, as a JSON formatted string, back to the ASSISTments' Tutor.
// All messages begin with the message version, currently AS.1, and an action key.
// The required actions are:
//
// - "loaded"
//    Your iframe sends this message exactly once.
//    Tells the Tutor that the iframe loaded content from your server in a timely manner.
//
// - "heartbeat"
//    Your iframe sends this message once every 10 seconds.
//    Tells the Tutor your page continues to be active.
//
// - "completed"
//    Your iframe sends this message exactly once.
//    Tells the Tutor that the iframe the student has completed the task and returns an associated
//    "score" which must be a value between 0.0 and 1.0. The leading 0 digit is requried (ex: 0.75).

var assistmentsJSONpreface = '{ "version" : "AS.1", "action" : '

// Sends a message from your iframe back to ASSISTments.
// The URL provided _must_ match the URL of the ASSISTments server you are using.
// If not, the problem will fail with an "Invalid Content" error saying:
//    This Problem has inconsistent content.
//    There are no registered URLs found.
// For example, since the URL below says "www.assistments.org", you cannot run this on
// "test1.assistments.org" unless you change the URL below to be test1.
function iframeToParent(message)
{
	window.parent.postMessage(message, "https://test1.assistments.org");
}

// Sends the "loaded" message and starts a timer to periodically send the
// "heartbeat" message.
function iframeLoaded()
{
	iframeToParent(assistmentsJSONpreface + '"loaded" }');

	assistmentsTimerVar = window.setInterval("iframeHeartbeat()", 10000);
}

// Sends the "heartbeat" message
function iframeHeartbeat() 
{
	iframeToParent(assistmentsJSONpreface +  '"heartbeat"}');
}

// Sends the "completed" message and score.
// Stops the "heartbeat" message.
function iframeCompleted() 
{
	var score = document.getElementById("MyScore").value;

	iframeToParent(assistmentsJSONpreface + '"completed", '
    + '"answer" : "<b>Student worked hard on this problem.</b>",'
    + '"score" : ' + score +' }');

	window.clearTimeout(assistmentsTimerVar);
}

