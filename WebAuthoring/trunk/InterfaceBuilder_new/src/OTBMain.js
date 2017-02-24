var modified=false; // Are there unsaved changes in the tutor interface?
var preferenceFile;

var file_new_panel=null;
var edit_preferences_panel=null;
var view_br_panel=null;
var view_js_editor=null;
var help_index_window=null;
var editor=null;
var editorID=0;

var canvasWidth = 700;
var canvasHeight = 475;

/**
 * Call the "use" method, passing in "node-menunav".  This will load the
 * script and CSS for the MenuNav Node Plugin and all of the required
 * dependencies.
 */
YUI_config=
{
	// filter: 'debug',
	// useConsoleOutput: true,
	// base: 'http://10.20.30.7/yui3/'
    base: 'yui3/'
};

/**
*
*/
var RaphaelCanvas;
function initIDE ()
{
	window.onerror = function(errorMsg, url, lineNumber) 
	{	    
		var formatter=new CTATHTMLManager ();
			
		useDebugging=true; // This should always go through
		debug(formatter.htmlEncode (errorMsg) + " in " + url + ", line " + lineNumber);	    
		useDebugging=false;
	};

	$(document).bind('keyup keydown', processGlobalKey );
	$("#mygraphiccontainer").bind ('mousemove', processMouseMove);
	
	debug("Initializing IDE...");
	
	/*
		YUI().use('graphics', function(Y) 
		{
			mygraphic = new Y.Graphic( {render: "#mygraphiccontainer"} );
			debug("Graphic coordinates are "+mygraphic.getXY()); // FIXME why does this return (0,0) rather than the coordinates of the div container?						
		});
						
	*/

	RaphaelCanvas = new Raphael(document.getElementById('mygraphiccontainer'), canvasWidth, canvasHeight);

	YUI().use('slider','console','button','button-group','panel','node-menunav', 'dd-plugin','resize-plugin','tabview', initYUI); 
	
	//buildGrids(10, "#EEEEEE", 50, "tutorbackground");
	showToolbox('content');	

	setStatus ("Not logged in. You must be logged in to access Google Drive.");	
	
	// Tell the parent frame that we're good to go.
	window.parent.appReady ();
}

/**
*
*/
function initYUI (Y) 
{
	debug ('YUI().use');

	// Main menu ...

	$('#graph_menu').menubutton('disable');
	$('#tutormode').disabled = true;
	
	// Basic dialog box
	
	file_new_panel = new Y.Panel(
	{
		srcNode: '#file_new_div',
		headerContent: 'New Interface',
		width: 660,		
		height: 500,		
		centered: true,
		modal: true,
		visible: false,
		zIndex : 1000,
		render: true
	});

	file_new_panel.plug(Y.Plugin.Drag, 
	{
		handles: ['.yui3-widget-hd']
	});	
	
	// Behavior Recorder
	
	edit_preferences_panel = new Y.Panel(
	{
		srcNode: '#edit_preferences',
		headerContent: 'Edit Preferences',
		width: 320,
		height: 300,
		centered: true,
		modal: true,
		visible: false,
		zIndex : 1010,
		render: true
	});

	edit_preferences_panel.plug(Y.Plugin.Drag, 
	{
		handles: ['.yui3-widget-hd']
	});	
			
	// Behavior Recorder

	createBR (Y);
	
	// Javascript Editor
	
	view_js_editor = new Y.Panel(
	{
		srcNode: '#view_js_editor',
		headerContent: 'Javascript Editor',
		width: 320,
		height: 280,
		centered: true,
		modal: false,
		visible: false,
		zIndex : 1030,
		render: true
	});
	
	var view_js_editor_resize = new Y.Resize(
	{
		node: '#view_js_editor',
		preserveRatio: true,
		wrap: true,
		handles: 'br'
	});
	
	view_js_editor.plug(Y.Plugin.Drag, 
	{
		handles: ['.yui3-widget-hd']
	});		
		
	$('#view_js_editor').bind('resize', function()
	{	
		var newWidth=$('#view_js_editor').width();
		var newHeight=$('#view_js_editor').height();
			
		$('#editor').width(newWidth-4);
		$('#editor').height(newHeight-30);
		
		editor.resize();
	});
  
	// New File dialog
  
	dialog = new Y.Panel(
	{
		contentBox : Y.Node.create('<div id="dialog" />'),
		bodyContent: '<div class="message icon-warn">Any changes you have made will be lost. Do you want to continue?</div>',
		width      : 440,
		zIndex     : 6,
		centered   : true,
		modal      : true, // modal behavior
		render     : '.example',
		visible    : false, // make visible explicitly with .show()
		buttons    : {
			footer: [
				{
					name  : 'cancel',
					label : 'Cancel',
					action: 'onCancel'
				},

				{
					name     : 'proceed',
					label    : 'OK',
					action   : 'onOK'
				}
			]
		}
	});
  
	dialog.onCancel = function (e) 
	{
		e.preventDefault();
		this.hide();
        // the callback is not executed, and is
        // callback reference removed, so it won't persist
        this.callback = false;
	};

	dialog.onOK = function (e) 
	{
		e.preventDefault();
		
		this.hide();
		
		// code that executes the user confirmed action goes here
		if(this.callback)
		{
			this.callback();
		}
		
		// callback reference removed, so it won't persist
		this.callback = false;
	};
	
	// Help Index
	
	help_index_window = new Y.Panel(
	{
		srcNode: '#help_index',
		headerContent: 'Help Index',
		width: 400,
		height: 400,
		centered: true,
		modal: false,
		visible: false,
		zIndex : 1040,
		render: true
	});
	
	var help_index_window_resize = new Y.Resize(
	{
		node: '#help_index',
		preserveRatio: true,
		wrap: true,
		handles: 'br'
	});
	
	help_index_window.plug(Y.Plugin.Drag, 
	{
		handles: ['.yui3-widget-hd']
	});
	
	// Property grid
	
	debug ('Initializing property grid ...');
	
    $('#pg').propertygrid(
	{
		url: 'tutordata.json',
		method:'get',
		showGroup:true,
		scrollbarSize:0
    });
	
	// Right tab view panel ...
	
	debug ('Initializing right panel tab view ...');
		
    var tabview = new Y.TabView(
	{
        srcNode: '#rightpanel'
    });

    tabview.render();		
	
	debug ('YUI().use done');
}

/**
*
*/
function edit_copy()
{
	debug("Edit Copy is not yet implemented");
	// TODO
}

/**
*
*/
function edit_paste()
{
	debug("Edit Paste is not yet implemented");
	// TODO
}

/**
*
*/
function edit_cut()
{
	debug("Edit Cut is not yet implemented");
	// TODO
}

/**
*
*/
function edit_preferences ()
{
	edit_preferences_panel.show ();
}

/**
* Takes a numerical template id (0 to 5 inclusive) and generates a new student interface canvas with that template
*/
function file_generate_new (template_id)
{
	var canvasDiv = '<div id="mygraphiccontainer" class="tutorcontainer"> \
					<canvas id="tutorbackground" style="width: 100%; height: 100%; border:0px;"> \
					Browser does not support canvas \
					</canvas> \
					</div>';
	
	debug("Generating new tutor ...");
	
	switch(template_id) {
	case 0:
		currentFileId=null;
		// TODO zero out the internal representation of the old tutor interface
		document.getElementById("mainPanel").innerHTML = canvasDiv;
		
		$(document).bind('keyup keydown', processGlobalKey );
		$("#mygraphiccontainer").bind ('mousemove', processMouseMove);
		
		buildGrids(10, "#EEEEEE", 50, "tutorbackground");
		
		break;
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
		debug("Template ID "+template_id+" not yet implemented."); // TODO
		break;
	default:
		debug("INVALID TEMPLATE ID");
		break;
	}
	
	file_new_panel.hide ();
}

/**
*
*/
function file_export ()
{
	debug("Export is not yet implemented");
	// TODO
}

/**
*
*/
function file_import ()
{
	debug("Import is not yet implemented");
	// TODO
}

/**
*
*/
function file_logout ()
{
	debug("Logout is not yet implemented");
	// TODO
}

/*
 * Prompt the user that unsaved changes will be lost. If the user says OK, callback will be called. On cancel, nothing happens.
 */
function prompt_discard_changes(callback)
{
  alert("Some changes have not been saved. Please save the changes first to continue!");
  //dialog.bodyContent=
  //'<div class="message icon-warn">Any changes you have made will be lost. Do you want to continue?</div>';
  //dialog.callback=callback;
  //dialog.show();
}

/**
*
*/
function file_new ()
{
	if(modified) {
		var result = prompt_discard_changes(file_new_internal);
	}
	else {
		file_new_internal();
	}
}

function file_new_internal()
{
	//file_new_panel.show();
	clearCanvas();
	clearUndoAndRedo();
	currentFileId = null;
	modified = false;
    
	debug("New tutor interface");
}

/**
*
*/
function file_new_graph ()
{
	$('#contenttabs').tabs('add',
	{
		title:'Behavior Graph',
		content:'Tab Body',
		closable:true,
		tools:[
		{
			iconCls:'icon-popout',
			handler:function()
			{
				alert('refresh');
			}
		}]
	});
}

/**
*
*/
function file_new_js ()
{
	var editorDivID=("editor-"+editorID);
	
	debug ("Creating new js div with id: " + editorDivID);

	$('#contenttabs').tabs('add',
	{
		title:'JavaScript File',
		content:'<div id="'+editorDivID+'"></div>',
		closable:true,
		tools:[
		{
			iconCls:'icon-popout',
			handler:function()
			{
				alert('refresh');
			}
		}]
	});
		
    editor = ace.edit(editorDivID);
    editor.setTheme("ace/theme/theme-pastel_on_dark");
    editor.getSession().setMode("ace/mode/javascript");
	
	editorID++;
}

/**
*
*/
function file_settings ()
{
	debug("Settings is not yet implemented");
	// TODO
}

function edit_undo()
{
	undo();
}

function edit_redo()
{
	redo();
}

function edit_cut()
{
	// cutting is the same as copying then deleting
	copySelection();
	deleteSelection();
}

function edit_copy()
{
	copySelection();
}

function edit_paste()
{
	paste();
}

/**
*
*/
function help_about ()
{
	debug("Help About is not yet implemented");
	// TODO
}

/**
*
*/
function view_js ()
{
	view_js_editor.show ();
	
    editor = ace.edit("editor");
    editor.setTheme("ace/theme/theme-pastel_on_dark");
    editor.getSession().setMode("ace/mode/javascript");
}

function help_index ()
{
	help_index_window.show ();
}

/**
*
*/
function toggleFullScreen()
{
    if ((document.fullScreenElement && document.fullScreenElement !== null) || (!document.mozFullScreen && !document.webkitIsFullScreen)) 
	{
		if (document.documentElement.requestFullScreen)
		{  
			document.documentElement.requestFullScreen();  
		}
		else if (document.documentElement.mozRequestFullScreen)
		{  
			document.documentElement.mozRequestFullScreen();  
		}
		else if (document.documentElement.webkitRequestFullScreen)
		{  
			document.documentElement.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);  
		}  
	}
	else
	{  
		if (document.cancelFullScreen)
		{  
			document.cancelFullScreen();  
		}
		else if (document.mozCancelFullScreen)
		{  
			document.mozCancelFullScreen();  
		}else if (document.webkitCancelFullScreen)
		{  
			document.webkitCancelFullScreen();  
		}  
	}  
}

/**
 * Display the toolbox on the left side of the screen. Parameter should be "content" or "layout" to select which tab to be shown; default is "content".
 */
function showToolbox(which) {
	if(which == 'layout') { // show layout toolbox
		debug("Layout toolbox not yet implemented");
		// TODO
	}
	else { // show content toolbox
		var toolboxHTML = 
			'<table style="padding:15px;"><tr><td>' +
			'<img id="toolbox_button" src="css/images/toolbox/button.png" draggable="true" ondragstart="drag(event)" onclick="clickOnIcon(event)" />' + '</td></tr><tr><td>' +
			'<img id="toolbox_radiobutton" src="css/images/toolbox/radiobutton.png" draggable="true" ondragstart="drag(event)" onclick="clickOnIcon(event)" />' + '</td></tr><tr><td>' +
			'<img id="toolbox_label" src="css/images/toolbox/label.png" draggable="true" ondragstart="drag(event)" onclick="clickOnIcon(event)" />' + '</td></tr><tr><td>' +
			'<img id="toolbox_textbox" src="css/images/toolbox/textbox.png" draggable="true" ondragstart="drag(event)" onclick="clickOnIcon(event)" />' + '</td></tr><tr><td>' +
			'<img id="toolbox_dropdown" src="css/images/toolbox/dropdown.png" draggable="true" ondragstart="drag(event)" onclick="clickOnIcon(event)" />' + '</td></tr><tr><td>' +
			'<img id="toolbox_table" src="css/images/toolbox/table.png" draggable="true" ondragstart="drag(event)" onclick="clickOnIcon(event)" />' + '</td></tr><tr><td>' +
			'<img id="toolbox_line" src="css/images/toolbox/line.png" draggable="true" ondragstart="drag(event)" onclick="clickOnIcon(event)" />' + '</td></tr><tr><td>' +
			'<img id="toolbox_image" src="css/images/toolbox/image.png" draggable="true" ondragstart="drag(event)" onclick="clickOnIcon(event)" />' + '</td></tr><tr><td>' +
			'<img id="toolbox_hintwindow" src="css/images/toolbox/hintwindow.png" draggable="true" ondragstart="drag(event)" onclick="clickOnIcon(event)" />' +
			'</td></tr></table>';
		
		document.getElementById('toolsPanel').innerHTML = toolboxHTML;
	}
}


function run(){
  //need to encode url properly by replacing & with &amp;
  var replace = function(str){
      for(var i = 0; i < str.length; i++){
        if(str.charAt(i) == '&'){
          str = str.substring(0,i) + "&amp;"+str.substring(i+1);
        }
      }
      return str;
    };

  //function called after getting download url and access token
  var sendSetPreferences = function(url,token){
    var message = "<message><properties><MessageType>SetPreferences</MessageType>";
    message += "<drive_url>"+url+"</drive_url>";
    message += "<drive_token>"+token+"</drive_token>";
    message += "</properties></message>";
    useDebugging=true;
    debug(replace(message));
    useDebugging=false;
    
    
    var xhr = new XMLHttpRequest();
    var servletUrl = "serv";//"http://localhost:9999/SimStudentServlet/serv"
    xhr.open("POST",servletUrl);
    xhr.onload = function(){
      debug("Set Preferences request sent.");
    };
    xhr.onerror = function(){
      debug("Unable to send Set Preferences.");
    };
    xhr.send(replace(message));
  };

  //get file metadata since that contains the download url
  //also get access token
  drive.retrieveFile("1416.brd",rootFolder,function(files){
    if(!files[0]){
      useDebugging("No file found");
    }else{
      var url = files[0].downloadUrl;
      var token = gapi.auth.getToken().access_token;
      sendSetPreferences(url,token);
    }
  });
}