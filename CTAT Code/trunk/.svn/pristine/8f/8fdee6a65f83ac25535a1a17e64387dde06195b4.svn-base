/**
 * @fileoverview A class that represents the UI part of the cloud file system code
 *	Extends the CTATDialogBase class
 *
 * @author $Author: $
 * @version $Revision: $
 */
 
//goog.require('CTATBase');

/**
*	@constructor
*/
var CTATFileChooser = function() 
{		
	var wId="#editor_filepicker2";
	CTATDialogBase.call (this, wId, "CTATFileChooser", "fileui", "MODAL", true);
	var pointer = this;
	//id of folder that's currently open
	var currentFolderid=cloudUtils.getWorkspaceFolder ();
	//id of last package created/published to
	var lastPackageId = null;
	//name of last published interface
	var lastPublishedName = null;
	//object that handles the rendering of the file tree display
	this.fControls = new CTATFileControls ();
	//specifies what type of files will be displayed
	var fileFilter='text/html';
	//dialog that comes up when creating a new folder
	var fNewFolderDialog = null;
	//true if a folder is selected, false if a file is selected
	var folderSelected = false;
	//id or name of selected file
	this.selectedFile = null;
	//context menu
	var contextMenu = null;
	var defaultCbk = function(){console.log('file chooser default callback');};
	var confirmCbk = null;
	
	/**
	*	Set up listeners for all the dialog buttons
	*/
	this.init = function init ()
	{
		pointer.ctatdebug ("init()");
		
		//just in case
		if ($(wId).data('initialized') === 'true')
		{
			return;
		}		
		$("#fdialogreload").on("click",function ()
		{
			pointer.fileDialogReload (null);
		});
		$("#fdialognewfolder").on("click",function ()
		{
			pointer.fileDialogNewFolder (null, null, 'New Folder', 'Enter a name for the new folder below');
		});
		$("#fdialogdelete").on("click", function()
		{
			pointer.deleteFile();
		});
		$("#fdialogconfirm").on("click",function ()
		{
			pointer.confirmFileDialog (null);
		});
		$("#fdialogcancel").on("click",function ()
		{
			pointer.close ();
		});		
		$("#closeicon").on("click",function ()
		{
			pointer.close ();
		});		
		$('#choosergripper').drags();
		
		//init jqxGrid events
		this.initJqxGrid();
		//init jstree events
		this.initJstree();
		//init file type combobox
		this.initTypeSelect();
		//init context menu
		contextMenu = new fileDialogRightClickMenu();
		
		$(wId).data('initialized', 'true');		
	};
	
	/**
	*	Set up event listener on filetype dropdown
	*/
	this.initTypeSelect = function()
	{
		$('#fdialogfiletype').on('change', function()
		{
			var filetype = $('#fdialogfiletype').val();
			console.log('filechoooser.onTypeChange(), type = '+filetype);
			pointer.fileDialogReload(null, filetype);
		});
	};
	
	/**
	*	Returns ID of folder currently selected
	*/
	this.getCurrentFolderId = function()
	{
		return currentFolderid;
	}
	
	/**
	*	Set filetype dropdown selection
	*	@param type the filetype to set to
	*/
	this.setFileType = function(type)
	{
		$('#fdialogfiletype').val(type);
	};
	
	/**
	*	Returns current filetype used to filter listing
	*/
	this.getFileType = function()
	{
		return fileFilter;
	}
	
	/**
	*	Returns ID of last package folder created/saved to
	*/
	this.getLastPackage = function()
	{
		return lastPackageId;
	}
	
	/**
	*	Set up listeners for file list click events
	*/
	this.initJqxGrid = function()
	{
		//Event fired when a file is clicked
		$("#gdrivedetailstt").on('rowclick', function (event) 
		{			
			var args = event.args;
			// row's bound index.
			var boundIndex = args.rowindex;
			// row's visible index.
			var visibleIndex = args.visibleindex;
			// right click.
			var rightclick = args.rightclick;
			// original event.
			ctatdebug ("rowclick ("+boundIndex+","+visibleIndex+")");
			var targetFolderItem = $('#gdrivedetailstt').jqxGrid('getcellvalue', boundIndex, "name");
			if (targetFolderItem)
			{
				$("#fname_input").val (targetFolderItem);
				pointer.selectedFile = targetFolderItem;
				folderSelected = false;
			}
		});

		//Event fired when a file is doubleclicked
		//Same behavior as clicking "ok" when a file is selected
		$("#gdrivedetailstt").on('dblclick', function (event)
		{
			console.log('jqxGrid double click');
			pointer.confirmFileDialog();
		});
		
		// sort jqxgrid on load
		$("#gdrivedetailstt").bind('bindingcomplete', function()
		{
			$("#gdrivedetailstt").jqxGrid('sortby', 'name', 'asc');
		});
	};
	
	/**
	*	Set up listeners for folder list click events
	*/
	this.initJstree = function()
	{
		$("#gdrive").on('select_node.jstree', function (evt, selectionData) 
		{			
			console.log('select_node.jstree');
			var selectedNodes=$("#gdrive").jstree().get_selected(true);
			var r=[];
			for(aNode in selectedNodes) 
			{
				r.push(selectedNodes [aNode].text);
			}
			if (selectedNodes.length == 1)
			{
				console.log('opening folder '+selectedNodes[0].id);
				var dirId = (selectedNodes[0].id === 'root' || selectedNodes[0].id === '') ? 
					cloudUtils.getRootFolder() : selectedNodes[0].id;
				if (dirId || dirId === '')
				{
					currentFolderid = dirId;
					pointer.selectedFile = dirId;
					folderSelected = true;
					pointer.processCTATFolder(dirId, fileFilter, true);
					cloudUtils.cacheFolder(dirId, false); //cache next level of dir
				}
				else console.log("couldn't find dir ID!");
			}
		});
		
		$("#gdrive").on("dblclick.jstree", function (e, data) 
		{
			pointer.ctatdebug ("onDoubleClick ()");

			var selectedNodes=$("#gdrive").jstree().get_selected(true);
			var r=[];
			for(aNode in selectedNodes) 
			{
				//console.log ("Adding selected node: " + JSON.stringify (aNode));
				r.push(selectedNodes [aNode].text);
			}
		});
	};
	
	/**
	 *	@Override CTATDialogBase.show()
	 *	@param mode the mode the dialog has been called with (open, save, etc)
	 *	@param optData any additional data that might be needed based on mode
	 *	@param optCbk optional callback to use as confirm callback
	 */
	var super_show = this.show;
	this.show = function(mode, optData, optCbk)
	{
		pointer.ctatdebug('show( '+mode+' )');
		$(wId).attr('data-fchooser-mode', mode);
		
		$('#fname_input').val('');
		$('#fname_input').removeAttr('disabled');
		$('#fdialogfiletype').removeAttr('disabled');

		if (!$(wId).data('initialized'))
			pointer.init();

		confirmCbk = optCbk || defaultCbk;
		switch(mode)
		{
			//Opening an .ed.html file in the editor
			case 'OPEN_SILEX':
				fileFilter = 'text/html';
				pointer.setTitle('Open');
				pointer.setFileType('html');
			break;
			//Opening an image file in the editor
			case 'OPENIMG':
				fileFilter = 'image';
				pointer.setTitle('Select Image');
				pointer.setFileType('image');
			break;
			//Opening an audio file in the editor
			case 'OPENAUDIO':
				fileFilter = 'audio';
				pointer.setTitle('Select Audio File');
				pointer.setFileType('audio');
			break;
			//Opening a video file
			case 'OPENVIDEO':
				fileFilter = 'video';
				pointer.setTitle('Select Video');
				pointer.setFileType('video');
			break;
			//Opening a stylesheet in the editor
			case 'OPENSTYLESHEET':
				fileFilter = 'text/css';
				pointer.setTitle('Select Stylesheet');
				pointer.setFileType('css');
			break;
			//Opening a JS file in the editor
			case 'OPENSCRIPT':
				fileFilter = 'javascript';
				pointer.setTitle('Select JS File');
				pointer.setFileType('js');
			break;
			case 'DOWNLOAD':
				fileFilter = '';
				pointer.setFileType('all');
				pointer.setTitle('Download Package');
				$('#fname_input').attr('disabled', 'true');
				$('#fdialogfiletype').attr('disabled', 'true');
				loadLastPkg();
			break;
			case 'DISPLAY':
				fileFilter = '';
				pointer.setFileType('all');
				pointer.setTitle('Google Drive');
		}
		if (!currentFolderid) 
		{
			currentFolderid = cloudUtils.getWorkspaceFolder ();
		}
		
		pointer.processCTATFolder (currentFolderid, fileFilter, true);
		
		if (pointer.getRefreshTimer() === -1)
		{
			pointer.setRefreshTimer (window.setInterval (pointer.fileDialogReload,60000));
		}
		//call super
		super_show();
	};
	
	/**
	*	Delete the currently selected file or folder
	*	@param optId optional ID to delete, if undefined selected file/folder will be deleted
	*	@param optIsFolder optional boolean specifying whether file to delete is a folder
	*/
	this.deleteFile = function(optId, optIsFolder)
	{
		var id, parentId, selected;
		var cbk = function()
		{
			if (folderSelected || optIsFolder)
			{
				//deleting folder
				currentFolderid = parentId;
				var currentFolderNode;
				if (currentFolderid === cloudUtils.getRootFolder())
					currentFolderNode = $('#gdrive').jstree('get_node', 'root', true);
				else
					currentFolderNode = $('#gdrive').jstree('get_node', currentFolderid, true);
				
				//set parent as selected
				$('#gdrive').jstree('select_node', currentFolderNode, true);
				if (selected)
					$('#gdrive').jstree('deselect_node', selected, true);
			}
			$("#fname_input").val ('');
			pointer.processCTATFolder(currentFolderid, fileFilter, true);
		};
		
		if (!optId) //delete selected node in tree
		{
			if (!folderSelected)
			{
				id = cloudUtils.getIdFromName(pointer.selectedFile, currentFolderid);
				parentId = currentFolderid;
			}
			else
			{
				id = pointer.selectedFile;
				selected = $('#gdrive').jstree('get_node', id, true);
				parentId = $('#gdrive').jstree('get_parent', selected);
				if (parentId === 'root')
				{
					parentId = cloudUtils.getRootFolder();
				}
			}
			cloudUtils.trashFile(id, parentId, cbk);
		}
		else //delete whatever id passed in
		{
			id = optId;
			cloudUtils.getParentId(id, function(pid)
				{
					parentId = pid;
					cloudUtils.trashFile(id, pid, cbk);
				});
		}
	};
	
	/**
	*	Display new folder dialog 
	*	@param e click event
	*	@param optCbk optional callback to call on success
	*	@param optTitle optional title for new folder window
	*	@param optTxt optional description text for new folder window
	*/
	this.fileDialogNewFolder = function fileDialogNewFolder(e, optCbk, optTitle, optTxt)
	{
		pointer.ctatdebug ("fileDialogNewFolder ()");
		if (!fNewFolderDialog)
		{
			fNewFolderDialog = new CTATNewFolderDialog(pointer.processNewFolder);
			fNewFolderDialog.init ();
		}
		optTitle = optTitle || 'Create Folder';
		fNewFolderDialog.show(optCbk, optTitle, optTxt);
	};

	/**
	*	Callback called when 'confirm' clicked on new folder dialog
	*	Handles the actual folder creation
	*	@param aFolderName the name of the folder to create
	*/
	this.processNewFolder = function processNewFolder(aFolderName)
	{
		pointer.ctatdebug ("processNewFolder ("+aFolderName+")");
		
		cloudUtils.createFolder (aFolderName,currentFolderid,pointer.createNewFolderCallback)
	};
	
	/**
	*	Callback called when a new folder has been created
	*	Reloads the dialog to reflect the new addition
	*/
	this.createNewFolderCallback = function createNewFolderCallback ()
	{
		pointer.ctatdebug ("createNewFolderCallback ()");
				
		pointer.fileDialogReload ();
	};
	
	/**
	*	Reload the contents of the file dialog
	*	Called either on a timeout or by clicking the refresh button
	*/
	this.fileDialogReload=function fileDialogReload (e, opt_filetype)
	{
		pointer.ctatdebug ("fileDialogReload (), currentfolderId="+currentFolderid);	
		
		var cbk = pointer.processListingResult.bind(pointer, true, currentFolderid);
		
		if (opt_filetype)
		{
			fileFilter = (opt_filetype === 'all') ? '' : opt_filetype;
		}
		if (fileFilter)
		{
			cloudUtils.listFilesByType("gdrive", fileFilter, currentFolderid, cbk, true);
		}
		else
		{
			cloudUtils.listFilesByID("gdrive",currentFolderid, true, cbk);
		}
		cloudUtils.cacheFolder(currentFolderid, true);
	};
	
	/**
	*	Called when the 'ok' button clicked in the fchooser dialog
	*	Takes action on the selected file based on what mode fchooser
	*		was opened in		
	*/
	this.confirmFileDialog = function confirmFileDialog()
	{
		pointer.ctatdebug ("confirmFileDialog ()");
		var mode = $(wId).attr('data-fchooser-mode');
		var selectedDir = $("#gdrive").jstree().get_selected(true);
		selectedDir = selectedDir[0].text;
		var targetTemp=$("#fname_input").val ();
		var targetFiles=targetTemp.split (",");
		var mimeType;
		if (targetFiles.length === 1)
		{
			if (targetFiles[0].indexOf('.') > 0)
				mimeType = targetFiles[0].substring(targetFiles[0].indexOf('.'), targetFiles[0].length);
		}
		if (targetFiles[0])
		{
			switch(mode)
			{
				case "OPEN_SILEX":
					if (FileUtils.getExtension(targetFiles[0]) !== 'ed.html')
					{
						alert('Only files created with the editor are supported');
						return;
					}
					window.silexApp.view.stage.setStatus('Loading file...');
					var fileId = cloudUtils.getIdFromName(targetFiles[0], currentFolderid); 
					//Open the selected file in the editor
					cloudUtils.openFileById (fileId,function (response)
						{
							//clear url (asset) map
							window.silexApp.model.file.resetUrlMap();
							window.silexApp.model.file.setHtml(response, function()
							{
								var pkgJSON = "{\"id\":\""+currentFolderid+"\", \"path\":\""+getPath()+"\"}";
								//reset undo history
								window.silexApp.controller.stageController.undoReset();
								window.silexApp.view.stage.setScrollX(30);
								
								//populate group, script, and stylesheet menus
								window.silexApp.view.menu.populateMenus();
								window.silexApp.view.stage.setStatus('');
								window.silexApp.view.stage.setFilename(getPath());
								window.silexApp.model.file.setMeta('parent-data', pkgJSON);
							});
						})
					pointer.close ();
				break;
				case "OPENIMG":
				case "OPENAUDIO":
				case "OPENVIDEO":
					//Open the selected image in the editor
					silexApp.view.stage.setStatus('Loading '+targetFiles[0]+'...');
					var fileId = cloudUtils.getIdFromName(targetFiles[0], currentFolderid);
					var path = getPath(fileId);
					cloudUtils.openBlobById(fileId, function(resp)
					{
						confirmCbk({id: fileId, data: resp, name: targetFiles[0], path: path});
					});
					pointer.close ();
				break;
				case "OPENSTYLESHEET":
					silexApp.view.stage.setStatus('Loading '+targetFiles[0]+'...');
					cloudUtils.getIdFromName(targetFiles[0], currentFolderid, function(id)
						{
							cloudUtils.openFileById(id, function(response)
								{
									silexApp.controller.insertMenuController.addAsset(targetFiles[0], response, id, 'stylesheet');
								});
						});
					pointer.close ();
				break;
				case "OPENSCRIPT":
					silexApp.view.stage.setStatus('Loading '+targetFiles[0]+'...');
					cloudUtils.getIdFromName(targetFiles[0], currentFolderid, function(id)
						{
							cloudUtils.openFileById(id, function(response)
								{
									silexApp.controller.insertMenuController.addAsset(targetFiles[0], response, id, 'script');
								});
						});
					pointer.close ();
				break;
				case "DOWNLOAD":
					this.downloadZip(currentFolderid, selectedDir);
				break;
				case "DISPLAY":
					//generic mode for file browsing, do nothing
					pointer.close ();
			}
		}
		else if (mode === 'DOWNLOAD')
		{
			this.downloadZip(currentFolderid, selectedDir);
		}
		else if (mode !== 'DISPLAY')
		{
			alert('Please enter a file name');
		}
		else
		{
			pointer.close();
		}
	};
	
	///////////////////////////////////////////////////////////////////
	// Internal methods
	///////////////////////////////////////////////////////////////////
	
	/**
	*	@param aRootFolderID the id of the folder to retrieve
	*	@param opt_filetype optional full mimeType or generic type string to use
	*		as a search filter.  If not provided will process entire folder
	*/
	this.processCTATFolder=function processCTATFolder (aRootFolderID, opt_filetype, isUpdate)
	{
		pointer.ctatdebug ("processCTATFolder ("+aRootFolderID+","+opt_filetype+")");
		
		if (opt_filetype)
			cloudUtils.listFilesByType ('gdrive', 
										opt_filetype,
										aRootFolderID,
										pointer.processListingResult.bind(pointer, isUpdate, aRootFolderID),
										false);
		else
			cloudUtils.listFilesByID ("gdrive",
									  aRootFolderID,
									  false,
									  pointer.processListingResult.bind(pointer, isUpdate, aRootFolderID));
	};
	
	/**
	*	Create a set of folders following the CTAT package structure
	*	@param packageName the name of the top level folder of the package
	*	@param cbk a function to which an object containing the new folder IDs are passed
	*/
	this.createPackage = function(packageName, cbk)
	{
		var pendingRequests = 4;
		var resp = {};
		var done = function() 
			{
				silexApp.view.stage.setStatus('package '+packageName+' created')
				if (cbk && typeof(cbk) === 'function')
				{
					cbk(resp);
				}
			};
		var assetCreated = function(response)
		{
			pendingRequests--;
			resp['assetId'] = response.id;
			if (pendingRequests == 0)
			{
				done();
			}
		}
		
		var htmlCreated = function(response)
		{
			pendingRequests--;
			//HTML dir created, create 'Assets' dir
			silexApp.view.stage.setStatus('Creating package folders...');
			resp['htmlId'] = response.id;
			cloudUtils.createFolder('Assets', response.id, assetCreated);
		};
		
		var brdCreated = function(response)
		{
			pendingRequests--;
			resp['brdId'] = response.id;
			if (pendingRequests == 0)
			{
				done();
			}
		};
		
		var pkgCreated = function(response)
		{
			pendingRequests--;
			//package dir created, create 'HTML' and 'FinalBRDs' dirs
			silexApp.view.stage.setStatus('Creating package folders..');
			resp['pkgId'] = response.id;
			cloudUtils.createFolder('HTML', response.id, htmlCreated);
			cloudUtils.createFolder('FinalBRDs', response.id, brdCreated);
		};
		
		//Create package dir
		silexApp.view.stage.setStatus('Creating package folders.');
		cloudUtils.createFolder(packageName, cloudUtils.getRootFolder(), pkgCreated);
	};
	
	/**
	*	Get package data to be used for a save
	*	@param pkgName if doCreateNew this is a name for the new package, else the ID of an existing package
	*	@param doCreateNew whether or not to create a new package
	*	@param cacheAssetDir whether or not the 'Assets' directory should be preemptively cached
	*	@param cbk a function to which the package data will be passed
	*/
	this.prepPackage = function(pkgName, doCreateNew, cacheAssetDir, cbk)
	{
		if (doCreateNew)
		{
			pointer.createPackage(pkgName, cbk);
		}
		else
		{
			var pkgInfo = {};
			pkgInfo['pkgId'] = pkgName;
			//validate proper dir structure
			pkgInfo['htmlId'] = cloudUtils.getIdFromName('HTML', pkgName);
			if (cacheAssetDir)
			{
				cloudUtils.getIdFromName('Assets', pkgInfo['htmlId'], function(id)
				{
					//check if asset directory cached already
					if (cloudUtils.isCached(id))
					{	
						cbk(pkgInfo);
					}
					else
					{
						//pre-cache assets folder
						cloudUtils.listFilesByID(null, id, true, cbk.bind(pointer, pkgInfo));
					}
				});
			}
			else
			{
				cbk(pkgInfo);
			}
			//close publish dialog
			window.silexApp.controller.fileMenuController.publishDialog.close();
			pointer.close();
		}
	};

	/**
	*	Publish the interface open in the editor.
	*	@param interfaceName the name of the interface file
	*	@param pkgName the name of the package the interface belongs to
	*	@param doCreateNew if true will create a new package, if false will
	*		try to save to an existing one.
	*/
	this.publishInterface = function(interfaceName, pkg, cbk)
	{
		var pkgDir, htmlDir;
		cbk = cbk || function()
			{
				silex.controller.ControllerBase.lastSaveUndoIdx = silex.controller.ControllerBase.undoHistory.length - 1;
				silexApp.view.contextMenu.redraw();
			};
		//cbk to do the actual save
		var saveFunc = function(pkgInfo)
		{
			pkgInfo.name = pkg.name;
			silexApp.view.stage.setStatus('exporting files...');
			var len = interfaceName.indexOf('.html');
			if (len > -1)
			{
				interfaceName = interfaceName.substring(0, len);
				len = interfaceName.indexOf('.ed');
				if (len > -1)
				{
					interfaceName = interfaceName.substring(0, len);
				}
			}
			pkgDir = pkgInfo['pkgId'];
			htmlDir = pkgInfo['htmlId'];
			//set interface-id meta tag
			window.silexApp.model.file.setMeta('interface-id', interfaceName);
			//set parent-id meta tag
			var pkgPath = '/'+pkgInfo.name+'/HTML/'+interfaceName; 
			window.silexApp.model.file.setMeta('parent-data', "{\"id\":\""+htmlDir+"\", \"path\":\""+pkgPath+"\"}");
			//set rest of file data
			var fileObj = {
				type: 'text/html',
				data: window.silexApp.model.file.getHtml()
			};
			cloudUtils.saveFile(interfaceName,
				false, 
				pkgInfo, 
				fileObj, 
				function() {
					currentFolderid = cloudUtils.getRootFolder();
					pointer.processCTATFolder (currentFolderid, '', true);
					cbk();
			});
			lastPackageId = pkgDir;
			lastPublishedName = interfaceName;
		};
		
		//Create package or get associated IDs if saving to existing
		pointer.prepPackage(pkg.id, false, true, saveFunc);
	};
	
	/**
	*	Create a zip package from a package and download it to HD
	*	@param folderId the ID of the package's top level directory
	*	@param folderName the name to save the package as
	*/
	this.downloadZip = function(folderId, folderName)
	{
		console.log('fChooser.downloadZip( '+folderId+' )');
		//Make sure folder has proper package structure
		validatePkg(folderId, function(valid)
		{
			if (!valid)
			{
				alert('The selected package folder is not formatted properly.'+
						  ' A package folder must contain a subdirectory called "HTML",'+
						  ' and the HTML subdirectory must contain a subdirectory called "Assets"');
				return;
			}
			silexApp.view.stage.setStatus('zipping package.');
			//highest level in zip folder
			var zip = new JSZip();
			//queue of folders to retrieve and zip
			var retrieveFolderQueue = [];
			//queue of files to retrieve and zip
			var retrieveFileQueue = [];
			//counters that determine when all requests have completed
			var waitingForFolders = 1, waitingForFiles;
			
			//retrieves files in <dirId> and adds them to appropriate queue
			function zipDir(dirId, parentDir)
			{
				cloudUtils.listFilesByID('gdrive', dirId, false, function(response)
					{
						//decrement folder counter
						waitingForFolders--;
						for (var file in response)
						{
							if (response.hasOwnProperty(file))
							{
								//if folder, add entry to parent in zip
								if (response[file].mimeType.includes('folder'))
								{
									var thisDirObj = parentDir.folder(response[file].title);
									retrieveFolderQueue.push(
										{
											id: response[file].id,
											dirObj: thisDirObj
										});
										
									waitingForFolders++;
								}
								else //if file
								{
									retrieveFileQueue.push(
										{
											id: response[file].id,
											title: response[file].title,
											type: response[file].mimeType,
											parent: parentDir
										});
								}
							}
						}
						if (waitingForFolders == 0) 
						{
							//all list folder requests have come back
							silexApp.view.stage.setStatus('zipping package..');
							//start retrieving files
							getFiles();
						}
					});
			}
			
			function getFiles()
			{
				waitingForFiles = retrieveFileQueue.length;
				var thisFile; 
				//download all files in queue
				while (retrieveFileQueue.length > 0)
				{
					thisFile = retrieveFileQueue.pop();
					if (thisFile)
					{
						console.log('downloading file '+thisFile.title);
						zipFile(thisFile);
					}
				}
			}
			
			function zipFile(fileObj)
			{
				var doZip = function(fileData)
				{
					//add data to zip object
					fileObj.parent.file(fileObj.title, fileData);
					waitingForFiles--;
					if (waitingForFiles == 0) //all file download requests have come back
					{
						silexApp.view.stage.setStatus('zipping package...');
						zip.generateAsync({type: 'blob'}).then(function(pkg)
							{
								download(pkg, folderName+'.zip', 'application/zip');
								silexApp.view.stage.setStatus('Package Downloaded');
							});
					}
				}
				if (fileObj.type.includes('image'))
				{
					cloudUtils.openBlobById(fileObj.id, doZip)
				}
				else
				{
					cloudUtils.openFileById(fileObj.id, doZip);
				}
			}
			
			//create main package folder in zip
			var pkgDir = zip.folder(folderName);
			zipDir(folderId, pkgDir);
			
			//list files in all folders in queue
			var thisDir;
			while (retrieveFolderQueue.length > 0)
			{
				thisDir = retrieveFolderQueue.pop();
				zipDir(thisDir.id, thisDir.dirObj);
			}
			
			//close file dialog
			pointer.close();
		});
	};
	
	/**
	*	Returns the path to the given file
	*	@param fileId the id of the file
	*/
	function getPath()
	{
		console.log('getPath( )');
		var pathStr = '';
		var dirId = currentFolderid;
		var node;
		if (!folderSelected)
		{
			pathStr += pointer.selectedFile;
		}
		do {
			node = $("#gdrive").jstree().get_node(dirId);
			node && (pathStr = (node.text + '/') + pathStr);
			dirId = $("#gdrive").jstree().get_parent(node.id)
		}while(dirId && dirId !== 'root');
		
		return pathStr;
	}
	
	/**
	*	Tests whether a given folder has proper CTAT package structure
	*	@param folderId the ID of the folder to validate
	*	@param cbk a function to which to pass the result (boolean)
	*/
	function validatePkg(folderId, cbk)
	{
		if (folderId)
		{
			cloudUtils.getIdFromName('HTML', folderId, function(htmlDirId)
			{
				if (htmlDirId)
				{
					cloudUtils.getIdFromName('Assets', htmlDirId, function(assetDir)
					{
						if (!assetDir)
							cbk(false);
						else
							cbk(true);
					});
				}
				else
					cbk(false);
			});
		}
		else 
			cbk(false);
	}
	this.validatePkg = validatePkg;
	
	/**
	*	Mark the jsTree node associated with the last created/opened package as selected
	*/
	function loadLastPkg()
	{
		if (lastPackageId && currentFolderid !== lastPackageId)
		{
			console.log('pre-loading package w/ id '+lastPackageId);
			var selectedArr = $("#gdrive").jstree().get_selected(true);
			var selected = selectedArr[0];
			currentFolderid = lastPackageId;
			if (selected.id !== lastPackageId)
			{
				var pkgFolderNode = $('#gdrive').jstree('get_node', lastPackageId, true);
				$('#gdrive').jstree('select_node', pkgFolderNode, true);
				$('#gdrive').jstree('deselect_node', selected, true);
			}
		}
	}
	
	//########################### Public Event Handlers ##############################

	/**
	*
	*/
	this.processListingResult=function processListingResult (isUpdate, folderId, data)
	{
		if (!folderId && folderId !== '')
			folderId = currentFolderid;
		
		pointer.ctatdebug ("processListingResult ( "+folderId+" )");
		if (!isUpdate)
			pointer.fControls.displayFileTree ("gdrive", data);
		else
			pointer.fControls.updateFileTree("gdrive", folderId, data);
	};
};


CTATFileChooser.prototype = Object.create(CTATDialogBase.prototype);
CTATFileChooser.prototype.constructor = CTATFileChooser;