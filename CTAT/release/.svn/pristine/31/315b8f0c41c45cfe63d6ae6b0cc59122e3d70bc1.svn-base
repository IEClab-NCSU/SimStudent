/**
*
*/
var CTATNewFolderDialog = function(aConfirmCallback) 
{		
	var wId="#newfolderdialog";

	CTATDialogBase.call (this, wId, "CTATNewFolderDialog", "fileui","MODAL", true);
	
	var pointer=this;
	var htmlInitialized=false;
	var confirmCallback=aConfirmCallback;
	var optionalCallback = null;
	/**
	*
	*/
	this.init = function init ()
	{
		pointer.ctatdebug ("init ()");
		
		if (htmlInitialized==true)
		{
			return;
		}		
		
		$(wId+"-confirm").on("click", pointer.confirm);
		
		$(wId+"-cancel").on("click",function ()
		{
			pointer.ctatdebug ("Cancel on click");	
			
			pointer.close ();
		});		
		
		htmlInitialized=true;		
	};
	
	var super_show = this.show;
	this.show = function(optCbk, optTitle, optTxt)
	{
		optionalCallback = optCbk || null;
		$("#ctatfoldername").val('');
		$('#newfolderdialog-txtfield').text(optTxt || '');
		if (optTitle === 'New Package')
			$('#newfolderdialog-inputlabel').text('Package Name: ');
		else
			$('#newfolderdialog-inputlabel').text('Folder Name: ');
		
		super_show(optTitle);
	};
	
	this.confirm = function()
	{
		pointer.ctatdebug ("Confirm on click");	
		var inputVal = $("#ctatfoldername").val();
		var cbk = optionalCallback || confirmCallback;
		FileUtils.assertName(inputVal, window.ctatFileChooser.getCurrentFolderId(), function(nameToUse)
			{
				var goAhead = true;
				if (nameToUse !== inputVal)
				{
					goAhead = confirm('A folder already exists with the name you provided.  '+nameToUse+' will be used instead');
				}
				if (goAhead)
				{
					cbk(nameToUse);
					pointer.close ();
				}
			});
	};
};

CTATNewFolderDialog.prototype = Object.create(CTATDialogBase.prototype);
CTATNewFolderDialog.prototype.constructor = CTATNewFolderDialog;
	