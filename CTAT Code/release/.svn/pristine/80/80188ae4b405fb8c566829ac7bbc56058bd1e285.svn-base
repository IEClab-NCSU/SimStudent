var InternetExplorer = navigator.appName.indexOf("Microsoft") != -1;
if (navigator.appName && navigator.appName.indexOf("Microsoft") != -1 && navigator.userAgent.indexOf("Windows") != -1 && navigator.userAgent.indexOf("Windows 3.1") == -1) {
	document.write('<SCRIPT LANGUAGE=VBScript\> \n');
	document.write('on error resume next \n');
	document.write('Sub qwsco_FSCommand(ByVal command, ByVal args)\n');
	document.write('  call qwsco_DoFSCommand(command, args)\n');
	document.write('end sub\n');
	document.write('</SCRIPT\> \n');
}

var objectivesSupported=true;
var interactionsSupported=true;
var rawScoreCandidate="0";

function qwsco_DoFSCommand(command, args){

	//alert("FSCommand "+command+","+args);

	//First parse the arguments which are in escaped format.
	var theOldArgs=args.split("&");
	var theArgs=new Array();
	for (var i = 0; i < theOldArgs.length; i++) {
		if(window.decodeURIComponent){
			theArgs.push(decodeURIComponent(theOldArgs[i]));
		}else{ // use unescape
			theArgs.push(unescape(theOldArgs[i]));
		}
	}

	//Now act on the command
	if(command=="LMSSetObjective"){

		rawScoreCandidate=theArgs[2];

		if(!objectivesSupported){
			return;
		}

		theArgs[1] = removeSpaces(theArgs[1]);

		//Objectives score may only be between 0 and 100
		if(Number(theArgs[2])<0 || Number(theArgs[2])>100){
			theArgs[2]="0";
		}

		var result=doLMSSetValue("cmi.objectives."+theArgs[0]+".id", theArgs[1]);
		if(result=="true"){
			doLMSSetValue("cmi.objectives."+theArgs[0]+".score.raw", theArgs[2]);
		}
		else{
			//has failed for some reason
			var stringCount=doLMSGetValue("cmi.objectives._count");
			if (stringCount==""){
				//No good, objectives are not supported at all.
				objectivesSupported=false;
				return;
			}else{
				//objectives are supported
				var count=Number(stringCount);
				if(Number(theArgs[0])>count){
					//The number is higher than the current count, it should be the same or less - fill in some dummy values
					while(Number(theArgs[0])>count){
						doLMSSetValue("cmi.objectives."+count+".id", "NULL");
						//alert("Filler");
						count++;
					}
					doLMSSetValue("cmi.objectives."+theArgs[0]+".id", theArgs[1]);
					doLMSSetValue("cmi.objectives."+theArgs[0]+".score.raw", theArgs[2]);
				}else{
					//This LMS doesn't allow overwriting current ids. We'll try to add it as a new record instead.
					doLMSSetValue("cmi.objectives."+count+".id", theArgs[1]);
					doLMSSetValue("cmi.objectives."+count+".score.raw", theArgs[2]);
				}
			}
		}
	}
	else if(command=="LMSSetInteraction"){

		rawScoreCandidate=theArgs[2];

		if(!interactionsSupported){
			return;
		}

		theArgs[1] = removeSpaces(theArgs[1]);
		
		var result=doLMSSetValue("cmi.interactions."+theArgs[0]+".id", theArgs[1]);
		if(result=="true"){
			doLMSSetValue("cmi.interactions."+theArgs[0]+".result", theArgs[2]);
			doLMSSetValue("cmi.interactions."+theArgs[0]+".type",   theArgs[3]);
			doLMSSetValue("cmi.interactions."+theArgs[0]+".time",   theArgs[4]);
		}
		else{
			//has failed for some reason
			var stringCount=doLMSGetValue("cmi.interactions._count");
			if (stringCount==""){
				//No good, interactions are not supported at all.
				interactionsSupported=false;
				return;
			}else{
				//objectives are supported
				var count=Number(stringCount);
				if(Number(theArgs[0])>count){
					//The number is higher than the current count, it should be the same or less - fill in some dummy values
					while(Number(theArgs[0])>count){
						doLMSSetValue("cmi.interactions."+count+".id", "NULL");
						count++;
					}
					doLMSSetValue("cmi.interactions."+theArgs[0]+".id", theArgs[1]);
					doLMSSetValue("cmi.interactions."+theArgs[0]+".result", theArgs[2]);
					doLMSSetValue("cmi.interactions."+theArgs[0]+".type",   theArgs[3]);
					doLMSSetValue("cmi.interactions."+theArgs[0]+".time",   theArgs[4]);
				}else{
					//This LMS doesn't allow overwriting current ids. We'll try to add it as a new record instead.
					doLMSSetValue("cmi.interactions."+count+".id", theArgs[1]);
					doLMSSetValue("cmi.interactions."+count+".result", theArgs[2]);
					doLMSSetValue("cmi.interactions."+count+".type",   theArgs[3]);
					doLMSSetValue("cmi.interactions."+count+".time",   theArgs[4]);
				}
			}
		}

	}
	else if(command=="LMSSetValue"){
		doLMSSetValue(theArgs[0], theArgs[1]);
	}
	else if(command=="LMSInitialize"){
		doLMSInitialize();
	}
	else if(command=="LMSFinish"){
		doLMSSetValue("cmi.core.score.raw", rawScoreCandidate);
		doLMSCommit();
		doLMSFinish();
		//dont want to do a commit
		return;
	}
	
	doLMSCommit();
}

function removeSpaces(string) {
	var tstring = "";
	string = '' + string;
	splitstring = string.split(" ");
	for(i = 0; i < splitstring.length; i++)
	tstring += splitstring[i];
	return tstring;
}