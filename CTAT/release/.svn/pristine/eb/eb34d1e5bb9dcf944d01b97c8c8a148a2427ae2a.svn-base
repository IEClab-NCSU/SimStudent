/**
 * @fileoverview 
 *
 * @author $Author: $
 * @version $Revision: $
 */
 
//goog.require('CTATBase');

/**
*
*/
var CTATFileEditor = function() 
{		
	var wId="#scripteditor";
	
	CTATDialogBase.call (this, wId, "CTATFileEditor", "fileeditor","MODELESS");
	
	var pointer=this;
	var htmlInitialized=false;
	var contentNodeId = null;
	var filePicker = null;
	var editor = null;
	var currentMimeType = 'plaintext';
	/**
	*
	*/
	this.init = function init (contentId)
	{
		pointer.ctatdebug ("init ()");
		if (htmlInitialized==true)
		{
			return;
		}		
		contentNodeId = contentId || 'editor';
		editor = ace.edit(contentNodeId);
		editor.setTheme("ace/theme/monokai");
		editor.getSession().setMode("ace/mode/"+currentMimeType);		
		htmlInitialized=true;		
	};
	
	this.newFile = function()
	{
		editor.setValue('');
	}
	
	this.pickFile = function()
	{
		if (!filePicker)
		{
			if (!window.ctatFileChooser)
				window.ctatFileChooser = new CTATFileChooser();
			
			filePicker = window.ctatFileChooser;
		}
		filePicker.show('OPEN_TEXT_EDIT');
	};
	
	this.openFile = function(data)
	{
		editor.setValue(data);
	};
	
	this.save = function ()
	{
		if (!filePicker)
		{
			if (!window.ctatFileChooser)
				window.ctatFileChooser = new CTATFileChooser();
			
			filePicker = window.ctatFileChooser;
		}
		filePicker.show('SAVE_TEXT_EDIT');
	};
	
	this.getFileString = function()
	{
		var text = editor.getValue();
		return text; 
	};

	this.setMimeType = function(type)
	{
		type = type.toLowerCase();
		if (type.includes('javascript'))
			currentMimeType = 'javascript';
		else if (type.includes('html'))
			currentMimeType = 'html';
		else if (type.includes('css'))
			currentMimeType = 'css';
		else
			currentMimeType = 'plaintext';
		console.log('setting editor mode to '+currentMimeType);
		editor.getSession().setMode("ace/mode/"+currentMimeType);
	}
	
	this.getMimeType = function()
	{
		return currentMimeType;
	}
};


CTATFileEditor.prototype = Object.create(CTATDialogBase.prototype);
CTATFileEditor.prototype.constructor = CTATFileEditor;
