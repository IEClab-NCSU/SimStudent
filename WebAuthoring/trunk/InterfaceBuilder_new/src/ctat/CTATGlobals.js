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

var tutorRunning=false;

var useDebugging=true;
var useDebuggingBasic=false;
var debugPointer=null;
var globalCommDisabled=false;

var customconsole=null;

var lastMessage=false;

var version="3.2.0";
var contextGUID="";
var orientation="portrait";
var ctatcanvas=null;
var ctx =null;	
var lineCounter=0;
var canvasWidth=600;
var canvasHeight=500;
var windowPadding=4;
var currentZIndex=2;
var currentIDIndex=1;
var currentComponentPointer=null;
var movieclips=new Array ();
var components=new Array();
var componentReferences=new Array ();
var feedbackComponents=new Array ();
var oldComponentFocus=null;
var globalDebugger=null;

// Global component settings ...

var correctColor="#00cc00";
var incorrectColor="#ff0000";
var highlightColor="#ffff00";
var hintColor="#ffff00";

var canvasCalibrate=5;

// Support class instances and global data structures ...
 
var scriptElement="";
var flashVars=null;
var commShell=null; // Nice to have but don't rely on it too much!
var mobileAPI=null; // Pointer to a CTATMobileTutorHandler object
var skillSet=null;
var aVars=null;
var interfaceElement=null; // Pointer to the DOM element holding the serialized low level interface
var commLibrary=null;
var commLMSService=null;
var commMessageBuilder=null;
var commMessageHandler=null;
var commLogMessageBuilder=null;
var commLoggingLibrary=null;
var selectedTextInput=null;
var nameTranslator=null;

// Specific hint support

var hints=new Array ();
var hintIndex=0;

// StateGraph variables

var caseInsensitive=true;
var unordered=true;
var lockWidget=true;
var suppressStudentFeedback=false;
var highlightRightSelection=true;
var confirmDone=false;

// Constants and visual defaults ...

var globalBackgroundColor="#ffffff";
var globalBorderColor="#cccccc";	
var globalFontColor="#000000";
var globalFontFamily="Verdana";
var globalFontSize=9;
var globalShowBorder=false;
var globalAlign='left';	
var globalBorderString="1px solid";
var globalGlowString="-webkit-box-shadow: 0px 0px 15px 5px rgba(255, 255, 190, 1.0); -moz-box-shadow: 0px 0px 15px 5px rgba(255, 255, 190, 1.0); box-shadow: 0px 0px 15px 5px rgba(255, 255, 190, 1.0); ";

var dialogDiv=null;

var incompatibleBrowserMessage="Your browser does not support CTAT. Please update or replace your browser.";
