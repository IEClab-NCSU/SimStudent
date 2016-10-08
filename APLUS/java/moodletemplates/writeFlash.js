/****************************************************************
 * Copyright (c) 2005, Peter Elst & Alexander McCabe,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the 
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the following 
 * disclaimer. 
 * 
 * 2. Redistributions in binary form must reproduce the above 
 * copyright notice, this list of conditions and the following 
 * disclaimer in the documentation and/or other materials 
 * provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or 
 * promote products derived from this software without specific 
 * prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ****************************************************************/

/****************************************************************
 * writeFlash Version 1.02
 * USAGE
 * 
 * Example
 * 	writeFlash("thumb.swf","100%","100%",{menu:"false",align:"middle"},{flashvar1:"my value",flashvar2:"my value2"});
 * 
 * Function signature
 * 	writeFlash(swf,width,height,params,pairs)
 * 
 * swf - the name of the Flash file to include
 * Examples
 * 	"myflash.swf"
 * 	"flashfiles/banner.swf"
 * 	"http://www.domain.com/myflash.swf"
 *
 * width / height - these can be pixel values or percentages
 * Examples
 * 	"600"
 *	"50%"
 *
 * Params - any number of parameters can be added as name/value pairs
 * Examples
 * 	{menu:"false",align:"middle"}
 * 	{version:"7,0,21,0",align:"middle"}
 * List of Possible parameters
 * 	align - left, middle, right
 *	menu - true, false
 *      version - made up of 4 comma separated values
 *	quality - low, autolow, autohigh, high, best
 * 	wmode - transparent, opaque, window
 *      base - URL of assets
 *      bgcolor - RGB, eg #FFFFFF
 *      align - L,R,T,B
 *      salign - L,R,T,B,TL,TR,BL,BR
 *      scale - showall, noborder, exactfit
 *      play - true/false
 *      loop - true/false
 *	swliveconnect - true/false
 * 	AllowScriptAccess - always/never/samedomain
 *	name - set this for an identifying name - useful for fscommand
 *
 * pairs - flashvars should be added as name/value pairs
 * Examples
 * 	{flashvar1:"my value",flashvar2:"my value2"}
 * 	{eyecolor:"green", haircolor:"brown"}
 ****************************************************************/

var DEFAULTBACKGROUNDCOLOR="#FFFFFF";
var DEFAULTFLASHVERSION="6,0,47,0";
var DEFAULTQUALITY="high";
var DEFAULTALIGNMENT="left";
var DEFAULTMENU="true";
var DEFAULTNAME="flash";

function writeFlash(swf,width,height,params,pairs) {
	
	//parse flashvars pairs
	var flashvars="";
	var writeAmp=false;
	for(var i in pairs){
		if (writeAmp){flashvars=flashvars+"&";}else{writeAmp=true;}
	
		if(window.encodeURIComponent){
			//use encode if available - it is better for unicode characters
			flashvars=flashvars+i+"="+encodeURIComponent(pairs[i]);
		}else{ 
			// use escape
	  		flashvars=flashvars+i+"="+escape(pairs[i]);
		}

	}
	
	if(!params){
		params = new Object();
	}
	if(!params.version) {
		params.version=DEFAULTFLASHVERSION;
	}
	if(!params.align){
		params.align=DEFAULTALIGNMENT;
	}
	if(!params.bgcolor){
		params.bgcolor=DEFAULTBACKGROUNDCOLOR;
	}
	if(!params.quality){
		params.quality=DEFAULTQUALITY;
	}
	if(!params.menu){
		params.menu=DEFAULTMENU;
	}
	if(!params.name){
		params.name=DEFAULTNAME;
	}
	if(!params.flashvars){
		params.flashvars=flashvars;
	}
	

	//if target is version 5 or less flashvars must be added to .swf string
	if(parseInt(params.version.substring(0,1))<6){
		swf=swf+"?"+params.flashvars;
		params.flashvars="";
	}
	
	// parse parameters
	var objectParams = "";
	var embedParams = "";
	for(var i in params) {
		if(i!="version" && i!="align" && i!="name"){
			objectParams += "<PARAM NAME="+i+" VALUE=\""+params[i]+"\">\n";
			embedParams += i+"=\""+params[i]+"\" ";
		}
	}


	// write the object
	document.write("<OBJECT classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" ");
	document.write("codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version="+params.version+"\"");
	document.write("WIDTH=\""+width+"\" HEIGHT=\""+height+"\" id=\""+params.name+"\" ALIGN=\""+params.align+"\">\n");
	document.write("<PARAM NAME=movie VALUE=\""+swf+"\">\n"); 
	document.write(objectParams);
	document.write("<EMBED src=\""+swf+"\" WIDTH=\""+width+"\" HEIGHT=\""+height+"\" name=\""+params.name+"\" ALIGN=\""+params.align+"\" " );
	document.write(embedParams);
	document.write(" TYPE=\"application/x-shockwave-flash\" PLUGINSPAGE=\"http://www.macromedia.com/go/getflashplayer\"></EMBED>");
	document.write("</OBJECT>");
}
