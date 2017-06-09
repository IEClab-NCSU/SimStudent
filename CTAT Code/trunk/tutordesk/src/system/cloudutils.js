/**-----------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 
 -
 License:
 -
 ChangeLog:
 -
 Notes:

	A class which acts as an intermediary layer between the desktop environment and 
	the various cloud storage APIs.  
*/

//folderID -> [{result: metadata obj}, ...]

/**
*
*/
var CloudUtils = function () 
{    
	CTATBase.call(this, "CloudUtils", "clouddrive");
	
	var pointer=this;
	
    var currentFileId = null;//should be set when user saves or opens
	var workspaceFolder = null;//should be set on load time
	var rootFolder = null;
	var preferenceFileId = null;//should be set on load time
	var mode;	//either 'googledrive', 'box', or 'dropbox'
	var drive;  //the object that will make the actual rest api calls
	var folderCache={isCached: {}};
	
	/**
	*	Return the mode (i.e. cloud service) currently being used
	*/
	this.getMode = function()
	{
		return mode;
	};
	
	/**
	* Initializes the GoogleDrive object used to make API requests
	* @param {string} service the cloud storage service to use
	*		(valid options are 'googledrive', 'dropbox', or 'box')
	* @param {boolean} authorized true if an Oauth access code has already
	*		retrieved.  In that case the code is exchanged for an access token.
	*		If false the user must login/authorize the service to obtain a code.
	*/
	this.initDrive = function initDrive (service, authorized)
	{
		console.log('initializing with '+service);
		mode = service;
		this.retrieveFileCallback = retrieveFileCallbacks[service].bind(this);
		switch(service)
		{
			case 'googledrive':
				drive = new GoogleDrive();
			break;
			case 'dropbox':
				drive = new DropBox();
			break;
			case 'box':
				drive = new Box();
			break;
		}
		processInit[service](authorized);
	};

	/**
	*	I think synonymous w/ getRootFolder
	*/
	this.getWorkspaceFolder=function getWorkspaceFolder ()
	{
		return (workspaceFolder);
	};
	
	/**
	*	Return the ID of the root CTAT directory
	*/
	this.getRootFolder=function getRootFolder ()
	{
		return (rootFolder);
	};

	this.disconnect = function(cbk)
	{
		drive.disconnect(cbk);
	};
	
	/**
	*	Begin authorization process for whichever cloud API
	*/
	this.authorize = function(immediate, callback)
	{
		drive.authorize(immediate, callback);
	};
	
	/**
	*	Renew access to whichever cloud API
	*/
	this.reauthorize = function(callback)
	{
		drive.reauthorize(callback);
	};
	
	/**
	*	Retrieve all folders in a given directory
	*	@param id the id of the directory
	*/
	this.listFolders = function(id, cbk, force)
	{
		ctatdebug('cloudUtils.listFolders()');
		if (folderCache[id] && !force)
		{
			//already in cache, return local results
			ctatdebug('Returning cached result');
			var folders = [];
			var files = folderCache[id];
			for (var fileId in files)
			{
				if (files.hasOwnProperty(fileId)
				&&	files[fileId].mimeType.includes('folder'))
				{
					folders.push(files[fileId]);
				}
			}
			cbk(folders);
		}
		else
		{
			//not in cache, query drive
			drive.retrieveFolders(id, cbk);
		}
	};
	
	/**
	* Retrieve all files in the root folder
	* @param divID {String}: the id of the div element where the filetree lives
	* @param callback {function}: a function to pass the result to
	*/
	this.listFiles = function listFiles (divID, callback)
	{
		pointer.ctatdebug("listFiles("+divID+")");
		
		var cbk = pointer.retrieveFileCallback.bind(this, divID, rootFolder, callback);			
		
		drive.retrieveFilesByFolder(rootFolder, cbk);
	};
	
	/**
	* Retrieve all files from the folder with id anID
	* @param divID {String}: the id of the div element where the filetree lives
	* @param anID {anID}: the id of the folder to retrieve
	* @param forced {boolean}: if true, ignores the cache and fetches from the server
	* @param callback {function}: function to pass the result to
	*/
	this.listFilesByID = function listFilesByID(divID, anID, forced, callback)
	{
		pointer.ctatdebug("listFilesByID("+divID+","+anID+")");
		var result = folderCache[anID];
		if (!result || forced)
		{
			var cbk = pointer.retrieveFileCallback.bind(this, divID, anID, callback);
			drive.retrieveFilesByFolder(anID, cbk, forced);		
		}
		else 
		{
			console.log('not forced update, returning cached result');
			if (callback)
				callback(result);
		}
	};
	
	/**
	 *	retrieve files based on mimeType
	 * 	@param divId the id of the dialog div
	 *	@param type either the full mimeType or a general
	 *		type (i.e. 'image' or 'text')
	 **/
	this.listFilesByType = function listFilesByType(divId, type, anID, callback, forced)
	{
		pointer.ctatdebug("listFilesByType("+divId+","+type+","+anID+")");
		var result = folderCache.isCached[anID] ? folderCache[anID] : null;
		var filter = function(data)
		{
			var filtered = [];
			for (var entry in data)
			{
				if (data.hasOwnProperty(entry))
				{
					if (data[entry].mimeType.includes(type)
					||	data[entry].mimeType.includes('folder'))
					{
						filtered.push(data[entry]);
					}
				}
			}
			callback(filtered);
		}
		if (!result || forced)
		{
			var cbk = pointer.retrieveFileCallback.bind(this, divId, anID, filter);
			drive.retrieveFilesByFolder(anID, cbk, false);
		}
		else
		{
			console.log('not a forced update, returning cached result');
			if (callback)
				filter(result);
		}
	};	

	/**
	*	Insert a new folder in drive
	*	@param aName the name of the folder to create
	*	@param aFolderParent the id of the parent folder
	*	@param callback a function to call on completion
	*/
	this.createFolder = function createFolder (aName,aFolderParent,callback)
	{
		var cbk = function(response)
		{
			pointer.cacheFile(response, aFolderParent);
			folderCache[response.id] = {};
			if (callback && typeof(callback) === 'function')
				callback(response);
		};
		
		drive.insertFolder(aName,aFolderParent,cbk);
	};		
	
	/**
	*	Moves to trash all files selected in tree.
	*	@param {string} fileId id of the file to delete
	*	@param {function} cbk a function to call on success
	*	@param {boolean} isId if true, file parameter stores the file ID
	*		if false, file parameter stores the filename
	*/
	this.trashFile = function(fileId, parentId, cbk)
	{
		pointer.ctatdebug("trashFile( "+fileId+" ) ");
		if (fileId === rootFolder)
		{
			console.warn('The root folder cannot be deleted');
			if (cbk) cbk();
			return;
		}
		cbk = cbk || function() {console.log('file '+fileId+' deleted');};
		if (!fileId)
			return;
		else
		{
			drive.trashFile(fileId, function()
				{
					//update cache
					pointer.removeFromCache(parentId, fileId);
					cbk();
				})
		}
	};

	/**
	*	Given a filename and directory ID, retrieves the file's
	*	ID and uses it to download the file.
	*	@param filename {String}: the name of the file
	*	@param parentID {String}: the ID of the directory in which the file lives
	*	@param callback {function}: a callback function passed along to drive.getFile()
	*		-this callback should accept the file data as an argument
	*/
	this.openFileByName = function openFileByName (aFilename, parentID, callback)
	{
		pointer.ctatdebug("openFileByName( " + aFilename + "," + parentID+") -> " + workspaceFolder);
		
		if (!parentID) 
		{
			parentID = workspaceFolder;
		}
		
		currentFileId = pointer.getIdFromName (aFilename,parentID);
		
		if (!currentFileId)
		{
			return false;
		}
		
		drive.downloadFileById (currentFileId, callback);
		return true;
	};
	
	/**
	*	Get a file's contents by ID
	*	@param fileId the unique ID of the file
	*	@param callback a function to which the file data is passed
	**/
	this.openFileById = function openFileById (fileId, callback)
	{
		console.log('openFileById ( '+fileId+' )');
		drive.downloadFileById(fileId, callback);
	};
	
	/**
	*	Retrieve a file's contents as a blob given the filename
	*	@param filename the canonical name of the file
	*	@param parent the unique ID of the file's parent folder
	*	@param callback a function to which the blob is passed
	*/
	this.openBlobByName = function(filename, parent, callback)
	{
		currentFileId = pointer.getIdFromName (filename, parent);
		
		if (!currentFileId)
		{
			return false;
		}
		
		drive.downloadBlobById (currentFileId, callback);
		return true;
	};
	
	/**
	*	Retrieve a file's contents as a blob given the file ID
	*	@param id the unique ID of the file
	*	@param callback a function to which the blob is passed
	*/
	this.openBlobById = function(id, callback)
	{
		drive.downloadBlobById(id, callback);
	}
	
	/**
	*
	*/
	this.saveFile = function saveFile(filename, toPublish, folderObj, fileObject, cbk)
	{
		pointer.ctatdebug("saveFile("+filename+")");
		var folderId = folderObj.htmlId;
		if (!folderId) folderId = rootFolder;
		var counter = 0;
		var fileText = fileObject.data;	
		var pubFilename = filename+'.html';
		var edFilename = filename+'.ed.html';
		var styleFileName = filename+'-styles.css';
		
		// ---callbacks--- //
		//called per file saved 
		var saveDoneCallback = function(parentFolderId, resp)
		{
			if (resp && resp.error)
				alert("Error saving file: "+resp.error.code+": "+resp.error.message);
			else 
				pointer.cacheFile(resp, parentFolderId);
			
			counter--;
			if (counter === 0)
				allDone();
		}
		//called once all saved
		var allDone = function()
		{
			windowManager.closeWindow('#editor_filepicker');
			var time = new Date();
			var fields = [];
			fields[0] = time.getHours();
			fields[1] = time.getMinutes();
			fields[2] = time.getSeconds();
			for (var i = 0; i < 3; i++)
			{
				if (fields[i].length == 1)
				{
					fields[i] = '0'+fields[i];
				}
			}
			window.silexApp.view.stage.setStatus('File saved at '+fields[0]+':'+fields[1]+':'+fields[2]);
			var path = folderObj.path ? folderObj.path : folderObj.name+'/HTML/'+filename;
			window.silexApp.view.stage.setFilename(path);
			if (cbk)
				cbk();
		};
		//performs the actual save operations
		var saveCallback = function(name, data, parentId, type)
		{
			pointer.ctatdebug ("saveCallback ()");
			counter++;				
			pointer.getIdFromName(name, parentId, function(id)
				{
					if (!id) 
					{
						console.log('file_saveas ('+name+')');
						pointer.saveFileAs(saveDoneCallback.bind(pointer, parentId),
											name,
											data,
											parentId,
											type);
					}	
					else
					{
						console.log("file_save ( "+name+" )");
						drive.updateFile(id,
										null,
										data,
										saveDoneCallback.bind(pointer, parentId));
					}	
				});
		}.bind(this);
		
		//save editor version (.ed.html)
		saveCallback(edFilename, fileText, folderId, 'text/html');
		//save regular version (.html)
		silex.utils.Dom.getCleanFile(fileText, function(fileData)
			{
				//save html
				saveCallback(pubFilename, fileData['htmlString'], folderId, 'text/html');
				//save css
				pointer.getIdFromName('Assets', folderId, function(assetFolderId)
					{
						saveCallback(styleFileName, fileData['cssString'], assetFolderId, 'text/css');
						//copy assets
						var assets = fileData['files'];
						var assetName, found;
						for (var id in assets)
						{
							if (assets.hasOwnProperty(id))
							{
								//if not already in asset folder, copy to there
								assetName = assets[id];
								found = false;
								if (folderCache[assetFolderId])
								{
									console.log('found asset folder in cache');
									var entries = folderCache[assetFolderId];
									for (var entry in entries)
									{
										if (entries.hasOwnProperty(entry))
										{
											if (entries[entry].title === assetName)
											{
												found = true;
												console.log('asset already in asset folder');
												break;
											}
										}
									}
								}
								if (!found)
								{
									pointer.copyFile(id, assetFolderId);		
								}
							}
						}
					});
			}, true);
	};		

	/**
	* save as.  Given a filename, file data, a directory ID, and filetype, saves
	* that file to Drive under the given ID.  If ID is false, null, undefined, etc.
	* the file will be saved to root.
	* @param callback {function}: a callback function
	* @param filename {String}: the name of the file
	* @param filetext {String}: the actual file data
	* @param fileparentID {String}: the ID of the directory in which the file will lives
	* @param filetype {String}: the mimeType of the file
	*/
	this.saveFileAs=function saveFileAs (callback, filename, filetext, fileparentID, filetype)
	{
		pointer.ctatdebug("saveFileAs type = "+filetype);
		
		filetype = filetype || "text/plain";
		
		fileparentID = fileparentID || workspaceFolder;
						
		drive.saveFile(filename,filetext,filetype,fileparentID,function(aFile)
		{
			if (aFile && aFile.id)
			{
				currentFileId=aFile.id;	
			}		
			if(callback)
			{
				callback(aFile);
			}
		});
	};	
	
	/**
	*	Copy a file
	*	@param fromId the ID of the file to copy 
	*	@param toId the ID of the destination folder
	*/
	this.copyFile = function(fromId, toId, optCallback)
	{
		var doCopy = function()
		{
			drive.copyFile(fromId, toId, function(response)
				{
					pointer.cacheFile(response, toId);
					if (optCallback)
						optCallback(response);	
				});
		}
		
		pointer.getParentId(fromId, function(pid)
			{
				var fileObj = pointer.getFolderIdCached(pid, fromId);
				pointer.getIdFromName(fileObj.title, toId, function(id)
					{
						if (id)
						{
							if (confirm('The destination folder already contains a file or folder with the same name.  '+
								'Click OK to overwrite it, or click Cancel to abort the copy'))
							{
								//delete old
								pointer.trashFile(id, toId, doCopy);
							}
							else
							{
								return;
							}
						}
						else
						{
							doCopy();
						}
					});
			});
	};
	
	/**
	*	Move a file
	*	@param fileId the ID of the file to move
	*	@param fromId the current parent of the file
	*	@param toId the ID of the destination folder
	*/
	this.moveFile = function(fileId, fromId, toId, optCallback)
	{
		var fileObj = pointer.getFolderIdCached(fromId, fileId);
		var doMove = function()
		{
			drive.moveFile(fileId, fromId, toId, function(optNewId)
				{
					//re-cache in new parent folder
					if (optNewId)
					{
						//needed b/c dropbox uses file paths for IDs
						fileObj.id = optNewId;
					}
					pointer.cacheFile(fileObj, toId);
					pointer.removeFromCache(fromId, fileId);
					
					if (optCallback)
						optCallback(optNewId);
				});
		};
		
		pointer.getIdFromName(fileObj.title, toId, function(id)
			{
				if (id)
				{
					if (confirm('The destination folder already contains a file or folder with the same name.  '+
								'Click OK to overwrite it, or click Cancel to abort the move'))
					{
						//delete old
						pointer.trashFile(id, toId, doMove);
					}
					else
					{
						return;
					}
				}
				else
				{
					doMove();
				}
			});
	};
	
	/**
	*	Rename an existing file
	*	@param fileId the unique ID of the file
	*	@param newName the new name for the file
	*	@param cbk a function to execute on completion
	*/
	this.renameFile = function(fileId, newName, cbk)
	{
		var fileObj, parentId, oldName;
		pointer.getParentId(fileId, function(pid)
			{
				parentId = pid;
				fileObj = pointer.getFolderIdCached(pid, fileId);
				oldName = fileObj.title;
				var ext = FileUtils.getExtension(oldName);
				if (ext && !FileUtils.hasExtension(newName))
				{
					newName += '.'+ext;
				}
				drive.renameFile(fileId, newName, function(newId)
				{
					//update data in cache
					fileObj.title = newName;
					if (newId)
					{
						fileObj.id = newId;
						pointer.cacheFile(fileObj, parentId);
						pointer.removeFromCache(parentId, fileId);
					}
					cbk();
				});
			});
	}

	/**
	*	Given a filename and parent folder, returns false if another file
	*	exists with the same name in the same folder.
	*	@param {string} filename the name of the file
	*	@param {string} folderId the id of the file's parent folder
	*/  
	this.validateFileName=function validateFileName (filename, folderId)
	{
		return !this.getIdFromName(filename, folderId)
	};
	
	/**
	*	Given a filename, return that file's unique ID
	*	@param filename the name to match against
	*	@param targetFolderID the ID of the folder where the file lives
	*	@param optCallback an optional callback function to pass the ID to
	*/
	this.getIdFromName = function getIdFromName(filename, targetFolderID, optCallback)
	{
		pointer.ctatdebug("getIdFromName (" + filename + "," + targetFolderID + ")");
		var requestFile = function()
			{
				console.log('folder '+targetFolderID + ' is not in cache...');
				if (optCallback)
				{
					drive.retrieveFilesByName(filename, targetFolderID, function(result)
						{
							if (result.length > 0)
							{
								pointer.cacheFile(result[0], targetFolderID);
								optCallback(result[0].id);
							}
							else
							{
								optCallback(null);
							}
						});
				}
			};
		targetFolderID = targetFolderID || workspaceFolder;
		var id = null;
		//check cache first
		if (folderCache[targetFolderID])
		{
			var fileData = pointer.getFolderNamedCached (targetFolderID,filename);
			if ((!fileData || !fileData.id)
			&&	!folderCache['isCached'][targetFolderID])
			{
				requestFile();
			}
			else
			{
				id = fileData ? fileData.id : null;
				if (optCallback) 
					optCallback(id);
				else 
					return id;
			}
		}
		else
		{
			requestFile();
		}
	};
	
	/**
	*	Given a filename and parent folder, return that file's mimeType
	*	@param {string} filename the name of the file
	*	@param {string} targetFolderID the id of the file's parent folder
	*	@param {function} optCallback an optional callback to pass the mimeType to
	*	@returns {?string} returns the mimeType as a string only if no callback is passed
	*/
	this.getTypeFromName = function getTypeFromName(filename, targetFolderID, optCallback)
	{
		targetFolderID = targetFolderID || workspaceFolder;
		var fileData = pointer.getFolderNamedCached(targetFolderID, filename);
		if (!fileData)
		{
			console.warn('file object not found, returning null');
			return null;
		}
		var type = fileData.mimeType;
		if (!type)
		{
			console.warn('File id property not found, returning null');
			type = null;
		}
		console.log('found type = '+type);
		if (optCallback) 
			optCallback(type);
		else 
			return type;
	}
	
	/**
	*	Get the ID of a file's parent folder
	*	@param fileId the Id of the file
	*	@param callback a function to which the parent ID is passed
	*/
	this.getParentId = function(fileId, callback)
	{
		var parentId;
		if (mode === 'googledrive');
		{
			//check cache first
			for (folderId in folderCache)
			{
				if (folderCache.hasOwnProperty(folderId)
				&&	folderCache[folderId][fileId]
				&&	folderId !== 'isCached')
				{
					parentId = folderId;
					break;
				}
			}
		}
		if (!parentId)
		{
			drive.getParents(fileId, function(parents)
				{
					callback(parents[0]);
				});
		}
		else
		{
			callback(parentId);
		}
	}
	
	/**
	 *	Retrieve and cache contents of children folders of a given folder.  Caching one
	 *		level ahead of selected folder makes for no delay when a folder is opened.
	 *	@param folderId the contents of all folders inside this folder will be cached
	 *	@param force if true will retrieve/cache no matter what, otherwise will only
	 *		retrieve and cache if there is not already an entry 
	 */
	this.cacheFolder = function(folderId, force, callback)
	{
		if (force || !folderCache.isCached[folderId])
		{
			console.log('cloudUtils.cacheFolder()');
			var cbk = function(response)
			{
				var counter = response.length;
				var handleResp = function()
				{
					counter--;
					if (counter == 0)
					{
						folderCache.isCached[folderId] = true;
						if (callback && typeof callback === 'function')
						{
							callback();
						}
					}
				}
				for (var i = 0; i < response.length; i++) //for each folder in folderId
				{
					console.log('caching folder '+response[i].id);
					pointer.listFilesByID(null, response[i].id, true, handleResp); //cache that folder
				};
			}

			pointer.listFolders(folderId, cbk, force);
		}
	}
	
	/**
	 *	Manually insert a file into a folder's cache entry.
	 *	@param {object} fileObj an object storing the file's metadata
	 *	@param {string} parentId the id of the file's parent folder
	 */
	this.cacheFile = function(fileObj, parentId)
	{
		console.log('cacheFile(): id = '+fileObj.id+', parent = '+parentId);
		if (!folderCache[parentId])
		{
			folderCache[parentId] = {};
		}
		folderCache[parentId][fileObj.id] = fileObj;
	};

	/**
	*	Given a filename and a folder ID, retrieve that file's metadata object from the cache
	*	@param {string} aFolderID the id of the file's parent folder
	*	@param {string} aName the name of the file
	*	@returns the file object or null if it is not found in the cache
	*/
	this.getFolderNamedCached = function getFolderNamedCached (aFolderID,aName)
	{		
		pointer.ctatdebug ("getFolderNamedCached ("+aFolderID+","+aName+")");
		if (!aFolderID)
		{
			aFolderID=workspaceFolder;
		}
		var entry = folderCache[aFolderID];
		if (entry) 
		{
			for (target in entry)
			{
				if (entry[target].title==aName)
				{
					return (entry[target]);
				}
			}	
		}
		else
		{
			console.log(aFolderID+' not found in cache');
		}
		console.log(aName+' not found in cache');
		return (null);
	};		
	
	/**
	*	Retrieve the cache entry for a given file
	*	@param folderId the ID of the folder in which the file lives
	*	@param fileId the ID of the file
	*/
	this.getFolderIdCached = function(folderId, fileId)
	{
		return folderCache[folderId][fileId];
	};
	
	/**
	*	Remove a given file from the cache
	*	@param folderId the ID of the folder in which the file lives
	*	@param fileId the ID of the file to remove
	*/
	this.removeFromCache = function(folderId, fileId)
	{
		delete folderCache[folderId][fileId];
	};
	
	/**
	*	Retrieve a file from a particular index in a cached folder
	*	@param {string} aFolderID the id of the file's parent folder
	*	@param {integer} anIndex the index of the file w/in the folder
	*	@returns the filename or null if not in the cache
	*/
	this.getFolderItemCached = function getFolderItemCached (aFolderID,anIndex)
	{		
		for (entry in folderCache) 
		{
			if (folderCache.hasOwnProperty(entry) && entry==aFolderID)
			{
				//pointer.ctatdebug ("Found folder!");
				var index=0;
				var foundResults=folderCache [entry];
				for (target in foundResults)
				{
					//console.log (JSON.stringify (target) + " in: " + foundResults);
					if (index==anIndex)
					{
						//pointer.ctatdebug ("Found target item: " + foundResults [target].result.title);
						return (foundResults [target].title);
					}
					index++;
				}
			
			}	
		}

		return (null);
	};

	/**
	*	Return the cache object storing file listings
	*/		
	this.getFolderCache = function getFolderCache()
	{		
		return (folderCache);
	};

	/**
	*	Whether there's an entry for a given folder in the cache
	*/
	this.isCached = function(id)
	{
		return !!folderCache[id];
	};

	/**
	*	Whether cacheFolder has been called on a given folder
	*/
	this.beenCached = function(id)
	{
		return folderCache.isCached[id];
	};
	
	this.setReqGap = function(g)
	{
		drive.setReqGap(g);
	}
	
	/**
	*	Service-specific initialization functions called on log in
	*/
	var processInit = {
		//Google Drive
		'googledrive': function processGoogleInit ()
		{
			ctatdebug ("processGoogleInit ()");
			silexApp.view.stage.setStatus('Authorizing...');
			gapi.auth.init(function()
			{
				//toggleProgressDialog (true);
				pointer.authorize(false,function(authorized)
				{
					ctatdebug ("Authorization:"+authorized);
					if(!authorized)
					{
						silexApp.view.stage.setStatus("Google authorization failed.");
						return;
					}	
					setInterval (refreshAuth,5*1000*60); // Every 5 minutes
					//status bar
					silexApp.view.stage.setStatus('Authorization successful, retrieving files...');
					drive.findCTATFolder(function(folderId)
					{
						rootFolder = folderId;
						workspaceFolder = folderId;
						pointer.listFiles (null, function ()
						{
							ctatdebug ("processGoogleFinished ()");
							settingsManager.init ();
							toggleProgressDialog (false);
							silexApp.view.stage.setStatus('Successfully connected to Google Drive');
							pointer.cacheFolder(folderId);
						});	
					});
				});
			});
		},
		//Dropbox
		'dropbox': function (authorized)
		{
			console.log('dropbox init');
			rootFolder = '';
			workspaceFolder = '';
			var successCbk = function()
			{				
				silexApp.view.stage.setStatus('Authorization successful, retrieving files...');
				pointer.listFiles(null, function(result)
				{
					console.log('processDropboxFinished()');
					silexApp.view.stage.setStatus('Successfully connected to Dropbox');
					pointer.cacheFolder('');
				});
				toggleProgressDialog (false);
			}
			silexApp.view.stage.setStatus('Authorizing...');
			drive.setAuthCompleteHandler(successCbk);
			drive.authorize(successCbk);
		},
		//Box
		'box': function(authorized)
		{
			console.log('initializing box, authorized = '+authorized);
			rootFolder = '0';
			workspaceFolder = '0';
			toggleProgressDialog (true);
			$('#infocontent').append("Please wait, authenticating with your Box account");
			$('#infocontent').append("<br>Cloud driver loaded, logging in ...");
			var successCbk = function()
			{
				console.log('box.successCbk()');
				$('#infocontent').append("<br>Cloud driver loaded, starting ...");
				$('#infocontent').append("<br>Cloud driver loaded, loading initial data ...");
				pointer.listFiles(null, function (data)
					{
						//check for CTAT folder, create if not there
						if (rootFolder == "0")
						{
							$('#infocontent').append("<br>Cloud driver, creating CTAT folder ...");
							pointer.createFolder("CTAT", pointer.getRootFolder (), processCTATFolderCreate);
							return;
						}
						else
						{
							ctatdebug ("We have a root folder, loading preferences ...");
						}
						settingsManager.init ();
						toggleProgressDialog (false);
					});
			};
			var errorCbk = function()
			{
				$('#infocontent').append('<br>Box authorization failed!');
			};
			
			if (!authorized)
			{
				drive.authorize(successCbk);
			}
			else
			{
				drive.getToken(successCbk, errorCbk);
			}
		}
		
	};
	
	/**
	* Every time we list files in a folder, the result will go through
	* one of these methods before it's handed to the original callback.
	* The particular method depends on which cloud storage service is 
	* being used.
	*/
	var retrieveFileCallbacks = {
		
		/** Google Drive
		* @param divID the id of the div our jstree lives in
		* @param aFolderID the id of the folder we retrieved from
		* @param cbk a callback to pass the results to after processing
		* @param result the actual retrieval data
		*/
		'googledrive': function (divID, aFolderID, cbk, result)
		{
			pointer.ctatdebug ("retrieveFileCallback("+divID+","+aFolderID+")");
			
			if (result.length==0)
			{
				folderEmpty(divID, aFolderID, cbk);
				return;
			}
			var batch = gapi.client.newBatch();
			//create a metadata request for every file returned by retrieveAllFiles
			for (var i=0;i<result.length;i++)
			{			
				batch.add (drive.getMetadataRequest (result [i].id));
			}
			$('#infocontent').append("<br>Retrieved drive data, processing ...");
			
			drive.sendBatch(batch, function(data)
			{
				var files = {};				
				for (entry in data) 
				{			
					if (data.hasOwnProperty(entry) && data[entry].result) 
					{
						files[data[entry].result.id] = data[entry].result;
					}	
				}
				folderCache[aFolderID]=files;
				folderCache['isCached'][aFolderID] = true;
				if (cbk) 
				{
					cbk(files);
				}
			});
		},
		
		/** Dropbox
		* @param divID the id of the div our jstree lives in
		* @param aFolderID the id (in this case a path) of the folder we retrieved from
		* @param cbk a callback to pass the results to after processing
		* @param result the actual retrieval data
		*/
		'dropbox': function (divID, aFolderID, cbk, result)
		{
			if (result.length == 0)
			{
				folderEmpty(divID, aFolderID, cbk);
				return;
			}
			//convert to object
			var files = {};
			for (var i = 0; i < result.length; i++)
			{
				files[result[i].id] = result[i];
			}
			folderCache[aFolderID] = files;
			folderCache['isCached'][aFolderID] = true;
			if (cbk) cbk(result);
		},
		
		'box': function(divID, aFolderID, cbk, result)
		{
			console.log('box.retrieveFileCallback');
			if (result.length == 0)
			{
				folderEmpty(divID, aFolderID, cbk);
				return;
			}
			folderCache[aFolderID] = result;
			folderCache['isCached'][aFolderID] = true;
			if ((aFolderID=="0") && (workspaceFolder=="0"))
			{
				pointer.ctatdebug ("We have a request for the root folder, finding and storing id ...");
				for (var entry in result)
				{
					if (result.hasOwnProperty(entry) && result[entry].title)
					{
						var fileObject = result[entry];
						if (fileObject.mimeType.includes("folder")
						&&	fileObject.title 
						&&  fileObject.title=="CTAT")
						{
							pointer.ctatdebug ("We have a root folder and a root folder id ("+fileObject.id+"), storing ...");
							workspaceFolder=fileObject.id;
							rootFolder=fileObject.id;
							//preemptively cache 1st lvl folders
							pointer.cacheFolder(fileObject.id, true);
						}
					}
				}
			}
			if (cbk) cbk(result);
		}
	
	};
	
	/**
	*	Called when a folder's contents are retrieved and the folder
	*	is empty
	*	@param {string} divID the id of the div where the jstree lives
	*	@param {string} aFolderID the id of the retrieved folder
	*	@param {function} cbk a function to pass the folder contents to
	*/
	var folderEmpty = function(divID, aFolderID, cbk)
	{
		folderCache[aFolderID] = {};
		if (divID)
		{
			pointer.ctatdebug ("We've got all the items (0 in this case), displaying ...");
			var driveObject=FileUtils.prepTreeObject ();
			jQuery('#'+divID).jstree (driveObject);
		}
		if (cbk) cbk({});
	};
};


CloudUtils.prototype = Object.create (CTATBase.prototype);
CloudUtils.prototype.constructor = CloudUtils;
