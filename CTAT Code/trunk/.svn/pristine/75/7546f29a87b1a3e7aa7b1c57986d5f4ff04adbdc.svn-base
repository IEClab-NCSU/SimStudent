 /**
 *	@fileoverview a class representing a dialog window used to 
 *	choose the source of an image or audio file linked into the working doc.
 *	The Image can come from an absolute URL, or in the form of a file on the 
 *	user's cloud storage.
 *
 *	Inherits from CTATDialogBase
 */

 /**
 *	@constructor
 *	@param windowId the id of the ctatdialog DOM node in which the dialog lives
 */
var CTATImageSource = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATImageSource", "imagesource", "MODAL", true);
	
	var pointer = this;
	this.selected = null;
	var defaultCbk = function(){console.log('CTATImageSource default callback')};
	var confirmCbk = null;
	var fileChooseCbk = null;
	this.init = function()
	{
		$('#img-src-confirm').on('click', function()
		{
			pointer.confirm();
		});
		
		$('#img-src-cancel').on('click', function()
		{
			pointer.cancel();
		});
		
		$('#'+windowId > '.windowclose').on('click', function()
		{
			pointer.cancel();
		});
				
		$('#img-prompt-text').text('Enter the URL of the image below, or ');
		var gDriveButton = document.createElement('button');
		gDriveButton.setAttribute('id', 'img-src-gdrive');
		$(gDriveButton).text('Choose file from cloud storage');
		$('#img-prompt-text').after(gDriveButton);
		$('#img-src-gdrive').on('click', function()
		{
			silexApp.controller.propertyToolController.pickFile(mode, fileChooseCbk);
			pointer.close();
		});
	};
	
	/**
	*	Close the window and apply the content to the selected node
	*/
	this.confirm = function()
	{
		var url = $('#img-src-url').val();
		confirmCbk(url);
		this.close();
	};
	
	this.cancel = function()
	{
		this.close();
	};
	
	/**
	*	Override CTATDialogBase show function
	*	Displays the window and inits the callbacks
	*	@param cbkOne will be called if an absolute URL is provided
	*	@param cbkTwo will be called if a cloud storage file is used
	*	@param optVal optional value to set in URL input
	*/
	super_show = this.show;
	this.show = function(cbkOne, cbkTwo, optVal, optMode)
	{
		confirmCbk = cbkOne || defaultCbk;
		fileChooseCbk = cbkTwo || defaultCbk;
		$('#img-src-url').val(optVal || '');
		super_show();
	};
	
	/**
	*	Sets mode to select either an image or audio file
	*	@param m the mode, either 'image' or 'audio'
	*/
	this.setMode = function(m)
	{
		console.log('imageSource.setMode('+m+')');
		mode = m;
		if (mode === 'image')
		{
			pointer.setTitle('Choose Image');
		}
		else if (mode === 'audio')
		{
			pointer.setTitle('Choose Audio');
		}
		else if (mode === 'video')
		{
			pointer.setTitle('Choose Video');
		}
	};
	
	this.init();
};

