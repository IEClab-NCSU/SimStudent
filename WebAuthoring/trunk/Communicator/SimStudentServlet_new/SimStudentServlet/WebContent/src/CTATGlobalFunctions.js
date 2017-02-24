/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATGlobalFunctions.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATGlobalFunctions');

goog.require('CTATConfig');
goog.require('CTATGlobals');
goog.require('CTATSandboxDriver');

CTATGlobalFunctions = {};
/**
 *
 */
function stringToBoolean (string)
{
	switch(string.toLowerCase())
	{
	case "true": case "yes": case "1": return true;
	case "false": case "no": case "0": case null: return false;
	default: return Boolean(string);
	}
}

/**
 *
 */
function noenter(e)
{
	//ctatdebug ("noenter ()");

	if (CTATConfig.platform=="google")
	{
		return (0);
	}

	e = e || window.event;
	var key = e.keyCode || e.charCode;

	return (key !== 13);
}

/**
 *
 * @param str
 * @returns {Boolean}
 */
function isEmpty(str)
{
	return (!str || 0 === str.length);
}

/**
 *
 */
function isBlank(str)
{
	return (!str || /^\s*$/.test(str));
}

/**
 *
 * @param c
 * @returns
 */
function componentToHex(c)
{
	var hex = c.toString(16);
	return hex.length == 1 ? "0" + hex : hex;
}

/**
 *
 * @param r
 * @param g
 * @param b
 * @returns {String}
 */
function rgbToHex(r, g, b)
{
	return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}

/**
 *
 * @param hex
 * @returns
 */
function hexToRgb(hex)
{
	var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);

	return result ?
			{
		r: parseInt(result[1], 16),
		g: parseInt(result[2], 16),
		b: parseInt(result[3], 16)
			} : null;
}

/**
 *
 * @param filename
 * @param filetype
 */
function loadjscssfile(filename, filetype)
{
	var fileref;
	if (filetype=="js")
	{
		//if filename is a external JavaScript file
		fileref=document.createElement('script');
		fileref.setAttribute("type","text/javascript");
		fileref.setAttribute("src", filename);
	}
	else if (filetype=="css")
	{
		//if filename is an external CSS file
		fileref=document.createElement("link");
		fileref.setAttribute("rel", "stylesheet");
		fileref.setAttribute("type", "text/css");
		fileref.setAttribute("href", filename);
	}

	if (typeof fileref!="undefined")
	{
		getSafeElementsByTagName("head")[0].appendChild(fileref);
	}
}

/**
 *
 */
function parseQueryString ()
{
	ctatdebug ("parseQueryString ()");

	var str = location.search;

	var query = str.charAt(0) == '?' ? str.substring(1) : str;
	var args = {};

	if (query)
	{
		var fields = query.split('&');

		for (var f = 0; f < fields.length; f++)
		{
			var field = fields[f].split('=');
			args[unescape(field[0].replace(/\+/g, ' '))] = unescape(field[1].replace(/\+/g, ' '));
		}
	}

	return args;
}

/**
 *
 */
function parseQueryStringArgs (aQuerySet)
{
	var str = aQuerySet;

	var query = str.charAt(0) == '?' ? str.substring(1) : str;
	var args = {};

	if (query)
	{
		var fields = query.split('&');

		for (var f = 0; f < fields.length; f++)
		{
			var field = fields[f].split('=');
			args[unescape(field[0].replace(/\+/g, ' '))] = unescape(field[1].replace(/\+/g, ' '));
		}
	}

	return args;
}

/**
 *
 */
function tutorPrep (aVars)
{
	ctatdebug ("tutorPrep ()");

	var args = [];

	if (CTATConfig.platform=="ctat")
	{
		args=parseQueryString ();
	}

	var generated=false;

	for (var arg in args)
	{
		switch(arg)
		{
		case "GENERATED":
			if (args [arg]=="on")
			{
				var generator=new CTATGuid ();
				aVars ['session_id']=generator.guid();
				generated=true;
			}
			break;
		case "BRD":
			aVars ['question_file']=args[arg];
			break;
			// Added by Shruti for demonstrating a valueTypeChecker input argument to Backend
		case "ARG":
			console.log("Argument: "+args[arg]);
			aVars ['Argument']=args[arg];
			break;
		case "BRMODE":
			aVars ['BehaviorRecorderMode']=args[arg];
			break;
		case "PROBLEM":
			aVars ['problem_name']=args[arg];
			break;
		case "DATASET":
			aVars ['dataset_name']=args[arg];
			break;
		case "LEVEL1":
			aVars ['dataset_level_name1']=args[arg];
			break;
		case "TYPE1":
			aVars ['dataset_level_type1']=args[arg];
			break;
		case "USER":
			aVars ['user_guid']=args[arg];
			break;
	    case "BACKDIR":
            aVars["back_dir"]=args[arg];
            break;

	    case "BACKENTRY":
            aVars["back_entry"]=args[arg];
             break;
		case "SESSION":
			if (generated===false)
			{
				aVars ['session_id']=args[arg];
			}
			break;
		case "SOURCE":
			aVars ['source_id']=args[arg];
			break;
		case "LOGTYPE":
			aVars ['Logging']=args[arg];
			break;
		case "PORT":
			aVars ['remoteSocketPort']=args[arg];
			break;
		case "REMOTEURL":
			aVars ['remoteSocketURL']=args[arg];
			break;
		case "DISKDIR":
			aVars ['log_to_disk_directory']=args[arg];
			break;
		case "USEOLI":
			aVars ['DeliverUsingOLI']=args[arg];
			break;
		case "URL":
			aVars ['log_service_url']=args[arg];
			break;
		case "CONNECTION":
			aVars ['connection']=args[arg];
			break;
		case "SUI":
			aVars ['sui']=args[arg];
			break;
		case "VAR1":
			aVars ['var1']=args[arg];
			break;
		case "VAL1":
			aVars ['val1']=args[arg];
			break;
		case "VAR2":
			aVars ['var2']=args[arg];
			break;
		case "VAL2":
			aVars ['val2']=args[arg];
			break;
		case "VAR3":
			aVars ['var3']=args[arg];
			break;
		case "VAL3":
			aVars ['val3']=args[arg];
			break;
		case "VAR4":
			aVars ['var4']=args[arg];
			break;
		case "VAL4":
			aVars ['val4']=args[arg];
			break;

		case "KEYBOARDGROUP":
			if (args[arg]=='Disabled')
			{
				aVars ['keyboard']='disabled';
			}

			if (args[arg]=='Auto')
			{
				aVars ['keyboard']='auto';
			}

			if (args[arg]=='On')
			{
				aVars ['keyboard']='on';
			}

			break;

			/*
			case "CSS":
								if (args[arg]!="")
								{
									// For some reason this doesn't work

									//loadjscssfile(args[arg], "css") // dynamically load and add this .css file
								}
								break;
			 */
		}
	}

	return (aVars);
}

//function raiseInitialScrim(aMessage) // unused
//{
//var mainCanvas=getSafeElementById("main-canvas");
//mainCanvas.setAttribute('style', "background-color: rgba(0,0,0,0.2);");

//ctx=mainCanvas.getContext("2d");

//var scrimGraphics=new CTATGraphicsTools(ctx);

//scrimGraphics.setLineColor("dark grey");
//scrimGraphics.setFillColor("white");

//var x=mainCanvas.width * 1/8;
//var y=mainCanvas.height * 3/8;

//var w=mainCanvas.width * 6/8;
//var h=mainCanvas.height * 3/8;

//scrimGraphics.drawRoundedRectFilled(x, y, w, h, 5);

//scrimGraphics.setLineColor("black");
//scrimGraphics.drawText(x, y, aMessage);
//}

//function clearInitialScrim()
//{
//var mainCanvas=getSafeElementById("main-canvas");
//mainCanvas.setAttribute('style', "background-color: rgb(255,255,255);");
//}

/**
 *
 */
/*
$(window).bind('orientationchange', function(event)
{
	if (event.orientation==undefined)
	{
		ctatdebug ("Warning: the orientation object is undefined, probably an old browser, compensating ...");
		setOrientation (window.orientation);

	}
	else
	{
		ctatdebug ("orientationchange ("+event.orientation+")");
		setOrientation (event.orientation);
	}
});
 */

/**
 *
 * @param anOrientation
 */
function setOrientation(anOrientation)
{
	ctatdebug ("setOrientation ("+anOrientation+")");

	switch (anOrientation)
	{
	case -90:
	case 90:
		orientation='landscape';

		/*
					var orImg=getSafeElementById("pageor");
					if (orImg!=null)
					{
						orImg.src="/skindata/landscape.png";
					}
					else
						alert ("Can't find orientation image object");
		 */

		if (mobileAPI)
		{
			mobileAPI.processOrientationChange (orientation);
		}

		break;
	case 180:
	default:
		orientation='portrait';

	/*
					var orImg=getSafeElementById("pageor");
					if (orImg!=null)
					{
						orImg.src="/skindata/portrait.png";
					}
					else
						alert ("Can't find orientation image object");
	 */

	if (mobileAPI)
	{
		mobileAPI.processOrientationChange (orientation);
	}

	break;
	}
}

/**
 * In order to provide support to older browsers, here is an implementation of the Object.create
 * method. Source: @link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create
 */
if (!Object.hasOwnProperty('create')) {
	Object.create = (function() {
		function F(){}

		return function(o)
		{
			if (arguments.length != 1)
			{
				throw new Error('Object.create implementation only accepts one parameter.');
			}

			F.prototype = o;
			return new F();
		};
	})();
}

function centerTutor(tutorWidth, tutorHeight)
{
	var td=getSafeElementById("table element");

	var leftMargin=-1*(tutorWidth/2);
	var topMargin=-1*(tutorHeight/2);

	td.setAttribute("style", "position: absolute; top: 50%; left: 50%; margin: "+topMargin+"px 0 0 "+leftMargin+"px;");
}
/**
 *
 */
function thisMovie(movieName)
{
	if (navigator.appName.indexOf("Microsoft") != -1)
	{
		return window[movieName];
	}
	else
	{
		return document[movieName];
	}
}

/**
 * Introspects an object.
 * http://www.syger.it/Tutorials/JavaScriptIntrospector.html
 *
 * @param name the object name.
 * @param obj the object to introspect.
 * @param indent the indentation (optional, defaults to "").
 * @param levels the introspection nesting level (defaults to 1).
 * @returns a plain text analysis of the object.
 */
function introspect (name, obj, indent, levels)
{
	indent = indent || "";

	if (this.typeOf(levels) !== "number") levels = 1;
	var objType = this.typeOf(obj);
	var result = [indent, name, " ", objType, " :"].join('');

	if (objType === "object")
	{
		if (levels > 0)
		{
			indent = [indent, "  "].join('');

			for (var prop in obj)
			{
				var property = this.introspect(prop, obj[prop], indent, levels - 1);
				result = [result, "\n", property].join('');
			}

			return result;
		}
		else
		{
			return [result, " ..."].join('');
		}
	}
	else if (objType === "null")
	{
		return [result, " null"].join('');
	}

	return [result, " ", obj].join('');
}

/**
 *
 */
function findPointOfAttachment (anInstance)
{
	ctatdebug ("findPointOfAttachment ("+anInstance+")");

	for (var t=0;t<movieclips.length;t++)
	{
		var aMovieClip=movieclips [t];

		ctatdebug ("Examining: " + aMovieClip.getName () + "...");

		if (aMovieClip.isRegistered (anInstance)==true)
		{
			return (aMovieClip);
		}
	}

	return (null);
}

/**
 * Important!! This function returns an object of type CTATMovieClip. This only
 * works if Flash MovieClips have been properly extracted previously in the
 * rendering process. So that means that findMovieClip should be called ideally
 * in the tutor's postprocess method, otherwise this function might fail
 */
function findMovieClip (anInstance)
{
	var formatted=anInstance.trim (); // Just in case

	ctatdebug ("findMovieClip ("+formatted.trim ()+")");

	for (var t=0;t<movieclips.length;t++)
	{
		var aMovieClip=movieclips [t];

		ctatdebug ("Examining: " + aMovieClip.getName () + "...");

		if (aMovieClip.getName ()==formatted)
		{
			return (aMovieClip);
		}
	}

	return (null);
}

/**
 *
 */
function findComponentByName (anInstance)
{
	ctatdebug ("findComponentByName ("+anInstance+")");

	for (var i=0;i<componentReferences.length;i++)
	{
		var ref=componentReferences [i];

		if (ref.getName ()==anInstance)
			return (ref);
	}

	return (null);
}

/**
 *
 */
function findComponentByClass (anInstance)
{
	ctatdebug ("findComponentByClass ("+anInstance+")");

	var results=[];

	for (var i=0; i<componentReferences.length; i++)
	{
		var ref=componentReferences [i];

		if (ref.getClassName ()==anInstance)
		{
			results.add (ref);
		}
	}

	return (results);
}
/**
 *
 */
function findComponent (aName)
{
	for (var j=0;j<components.length;j++)
	{
		var subRef=components [j];

		var subComponent=subRef.getComponentPointer ();

		if (subComponent)
		{
			if (subComponent.getName()==aName)
			{
				//ctatdebug ("Found sub component, returning ...");
				return (subComponent);
			}
		}
	}

	return (null);
}

/**
 *
 */
function listComponentReferences ()
{
	ctatdebug ("listComponentReferences ("+componentReferences.length+")");

	for (var i=0;i<componentReferences.length;i++)
	{
		var ref=componentReferences [i];

		ctatdebug ("Component: " + ref.getElement ().getName () + ", with div: " + ref.getDiv ().id);
	}
}

/**
 *
 */
function colName(n,toUpperCase)
{
	ctatdebug ("colName ("+n+")");

	var s = "";

	while(n >= 0)
	{
		s = String.fromCharCode(n % 26 + 97) + s;
		n = Math.floor(n / 26) - 1;
	}

	if (toUpperCase==true)
	{
		s.toUpperCase ();
	}

	return s;
}

/**
 *
 */
/*
function selectText(containerid)
{
	if (document.selection)
	{
		var range = document.body.createTextRange();
		range.moveToElementText(document.getElementById(containerid));
		range.select();
	}
	else if (window.getSelection)
	{
		var range = document.createRange();
		range.selectNode(document.getElementById(containerid));
		window.getSelection().addRange(range);
	}
}
 */

function selectText(target)
{
	//document.getElementById(target).focus();
	//document.getElementById(target).select();
}

function urldecode(str)
{
	return decodeURIComponent((str+'').replace(/\+/g, '%20'));
}
//removes all elements in array equal to element, equality decided by function comp
function arrayRemove(array,element,comp){
	var a;
	for(var i = 0; i < array.length; i++){
		if(comp(array[i],element)){
			a = array[i];
			array.splice(i,1);
			i--;
		}
	}
	return a;
}
//finds first element in array such that fun(element) returns true
function arrayFind(array, fun){
	for(var i = 0; i < array.length; i++){
		if(fun(array[i])){
			return array[i];
		}
	}
}

function detectIE() {
	var ua = window.navigator.userAgent;
	var msie = ua.indexOf('MSIE ');
	var trident = ua.indexOf('Trident/');

	if (msie > 0) {
		// IE 10 or older => return version number
		return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
	}

	if (trident > 0) {
		// IE 11 (or newer) => return version number
		var rv = ua.indexOf('rv:');
		return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
	}

	// other browser
	return false;
}

function listProperties (anObject)
{
	ctatdebug ("listProperties ()");

	for(var propertyName in anObject)
	{
		//ctatdebug ("["+propertyName+"]: " + anObject[propertyName]);
		ctatdebug ("["+propertyName+"]");
	}
}

function formatColor(aColor){
	var c = aColor;
	while(c.length < 6)
		c = '0' + c;
		return '#'+c;
}

CTATGlobalFunctions={};

CTATGlobalFunctions.Gensym = function() {
	this.make_gensym = function() {
		var prefix = '';
		var index = 0;
		return {
			set_prefix: function(p) {
				prefix = String(p);
			},
			set_index: function(i) {
				index = i;
			},
			gensym: function() {
				var result = prefix + index;
				index += 1;
				return result;
			}
		};
	};

	var z_index = this.make_gensym();
	z_index.set_index(2);
	var id_index = this.make_gensym();
	id_index.set_index(1);
	id_index.set_prefix('ctatdiv');
	return {
		z_index: z_index.gensym,
		div_id: id_index.gensym
	};
};
CTATGlobalFunctions.gensym = CTATGlobalFunctions.Gensym();
