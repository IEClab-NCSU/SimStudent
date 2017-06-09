/**
*	@fileoverview A class representing a context menu displayed on right click in the file dialog
*/

/**	@constructor
*
*/
var fileDialogRightClickMenu = function()
{
	//call super
	rightClickMenu.call(this, "file-rightclick-context-menu", "file-rightclick-context-menu-item");
	
	//whether last right click was on a folder
	var folderClicked=false;
	
	//whether 'cut' was chosen on a folder
	var folderCut = false;
	
	//file ID of last right click
	var lastTarget = null;
	
	//new parent ID for a pasted file
	var destination = null;
	
	//file IDs registered by 'cut' and 'copy' options 
	var toCut=null, toCopy=null;
	var pointer = this;
	
	pointer.setHandler(document, function(e)
	{
		folderClicked = false;
		if ($(e.target).closest(".jqx-grid-cell").length)
		{
			//file click
			destination = window.ctatFileChooser.getCurrentFolderId();
			lastTarget = cloudUtils.getIdFromName(window.ctatFileChooser.selectedFile, destination);
			setEnabled();
			pointer.show(e);
			e.preventDefault();
		}
		else if ($(e.target).closest(".jstree-anchor").length)
		{
			//folder click
			folderClicked = true;
			var anchor = $(e.target).closest(".jstree-anchor")[0];
			var len = anchor.id.indexOf('_anchor');
			lastTarget = anchor.id.substring(0, len);
			if (lastTarget === 'root')
			{
				lastTarget = cloudUtils.getRootFolder();
			}
			destination = lastTarget
			setEnabled();
			pointer.show(e);
			e.preventDefault();
		}
		else
		{
			pointer.hide();
		}
	});
	
	/**
	*	Enable or disable menu options based on event context
	*/
	var setEnabled = function()
	{
		//paste
		if (!toCopy && !toCut)
		{
			pointer.disableOption('paste');
		}
		else
		{
			pointer.enableOption('paste');
		}
		// copy
		if (folderClicked)
		{
			pointer.disableOption('copy');
		}
		else
		{
			pointer.enableOption('copy');
		}
		//delete
		if (lastTarget === cloudUtils.getRootFolder())
		{
			pointer.disableOption('delete');
		}
		else
		{
			pointer.enableOption('delete');
		}
	}
	
	/**
	*	Called when 'cut' option is clicked
	*/
	var handleCut = function()
	{
		//record id for later paste
		toCut = lastTarget;
		toCopy = null;
		folderCut = folderClicked;
	}
	
	/**
	*	Called when 'copy' option is clicked
	*/
	var handleCopy = function()
	{
		//record id for later paste
		toCopy = lastTarget;
		toCut = null;
	}
	
	/**
	*	Called when 'paste' option is clicked
	*/
	var handlePaste = function()
	{
		var refresh = function(toRefresh)
		{
			ctatFileChooser.processCTATFolder(toRefresh, ctatFileChooser.getFileType(), true);
		};
		if (toCopy)
		{
			cloudUtils.copyFile(toCopy,
							    destination,
							    function()
									{
										refresh(destination);
									});
		}
		else if (toCut)
		{
			cloudUtils.getParentId(toCut, function(parentId)
				{	
					cloudUtils.moveFile(toCut, 
										parentId,
										destination,
										function()
										{
											if (folderCut)
											{
												refresh(parentId);
												refresh(destination);
											}
											else
											{
												refresh(ctatFileChooser.getCurrentFolderId());
											}
											toCut = null;
										});
				});
		}
	};
	
	/**
	*	Called when 'rename' option is clicked
	*/
	var handleRename = function()
	{
		var refresh = function()
		{
			var openFolder = ctatFileChooser.getCurrentFolderId();
			ctatFileChooser.processCTATFolder(openFolder, ctatFileChooser.getFileType(), true);
		};
		var doRename = function(newName)
		{
			cloudUtils.renameFile(lastTarget, 
								  newName,
								  refresh);
		};
		ctatFileChooser.fileDialogNewFolder(null, doRename, 'Rename File');
	};
	
	/**
	*	Called when 'delete' option is clicked
	*/
	var handleDelete = function()
	{
		window.ctatFileChooser.deleteFile(lastTarget, folderClicked);
	}
	
	//set up option -> handler mappings
	pointer.setOptionHandler('cut', handleCut);
	pointer.setOptionHandler('copy', handleCopy);
	pointer.setOptionHandler('delete', handleDelete);
	pointer.setOptionHandler('rename', handleRename);
	pointer.setOptionHandler('paste', handlePaste);
}