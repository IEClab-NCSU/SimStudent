var shifted=false;
var alted=false;
var ctrlled=false;
var deled=false;

/**
*
*/
function setStatus (aMessage)
{
	document.getElementById('StatusItem').innerHTML=aMessage;
}

/**
*
*/
function processMouseMove (event)
{
	var localX=event.pageX-$('#mygraphiccontainer').position().left;
	var localY=event.pageY-$('#mygraphiccontainer').position().top;

	//document.getElementById('MouseLabel').innerHTML=("x: " + localX + ", y: " + localY);
}

/**
*
*/
function processGlobalKey (e)
{
	shifted = e.shiftKey;
	alted=e.altKey;
	ctrlled=e.ctrlKey;
	
	if(e.type == "keydown" && e.keyCode == 46) { // 46 means Del key
		deleteSelection();
	}
	
	if(e.type == "keydown" && e.keyCode == 67 && ctrlled) { // 67 means C; this is for Ctrl+C
		edit_copy();
	}
	
	if(e.type == "keydown" && e.keyCode == 86 && ctrlled) { // 86 means V; this is for Ctrl+V
		edit_paste();
	}
	
	if(e.type == "keydown" && e.keyCode == 88 && ctrlled) { // 88 means X; this is for Ctrl+X
		edit_cut();
	}
	
	if(e.type == "keydown" && e.keyCode == 90 && ctrlled) { // 90 means Z; this is for Ctrl+Z
		edit_undo();
	}
	
	if(e.type == "keydown" && e.keyCode == 89 && ctrlled) { // 89 means Y; this is for Ctrl+Y
		edit_redo();
	}
	
	var shiftDiv;
	if (shifted===true)
	{
		shiftDiv=document.getElementById('SHIFTLabel');
		//shiftDiv.style.color="#ff0000";
	}
	else
	{
		shiftDiv=document.getElementById('SHIFTLabel');
		//shiftDiv.style.color="#444444";	
	}
	
	if (alted===true)
	{
		shiftDiv=document.getElementById('ALTLabel');
		//shiftDiv.style.color="#ff0000";
	}
	else
	{
		shiftDiv=document.getElementById('ALTLabel');
		//shiftDiv.style.color="#444444";	
	}

	if (ctrlled===true)
	{
		shiftDiv=document.getElementById('CTRLLabel');
		//shiftDiv.style.color="#ff0000";
	}
	else
	{
		shiftDiv=document.getElementById('CTRLLabel');
		//shiftDiv.style.color="#444444";	
	}	
}

/**
*
*/
function clearConsole ()
{
	console=document.getElementById("customconsole");
	
	console.innerHTML="";
}
