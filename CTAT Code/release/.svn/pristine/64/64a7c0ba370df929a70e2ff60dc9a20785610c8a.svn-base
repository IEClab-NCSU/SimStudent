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

	We can keep a 'current dialog' pointer since windows like the file
	chooser are always modal and there should not be more than one
	modeless dialog at the same time. If this system ever becomes really
	big and widely used it might have to change. But you know, CTAT, fat
	chance.
*/

var currentDialog=null;

/**
*
*/
var CTATDialogBase = function(anID,aClass,anInstance,aMode,aTitle) 
{
	CTATBase.call (this, aClass, anInstance);
	
	var dialogID=anID;
	var pointer=this;
	var refreshTimer=-1;
	var windowObject=null;
	
	var mode = aMode;
	
	/**
	*
	*/
	this.setRefreshTimer=function setRefreshTimer (aTimerValue)
	{
		refreshTimer=aTimerValue;
	};
	
	this.getRefreshTimer = function()
	{
		return refreshTimer;
	}
	
	/**
	*
	*/
	this.getDialogID=function getDialogID ()
	{
		return dialogID;
	};
	
	/**
	*
	*/
	this.setTitle=function setTitle (aTitle)
	{
		if (aTitle)
		{
			if (dialogID.indexOf ("#") === -1)
			{
				$("#"+dialogID+"-title").text (aTitle);
			}
			else
			{
				$(dialogID+"-title").text (aTitle);				
			}
		}		
	};
	
	/**
	*
	*/
	this.show=function show (aTitle)
	{
		$(dialogID).visible ();

		if (aTitle)
			pointer.setTitle (aTitle);
		
		windowObject=windowManager.addWindow (dialogID, null, mode);
		windowObject.setWindowMode(mode);
		windowManager.centerWindow (dialogID);		
		
		return (windowObject);
	};	
	
	/**
	*
	*/
	this.close=function close ()
	{
		windowManager.closeWindow (dialogID);
		
		if (refreshTimer!=-1)
		{			
			window.clearTimeout (refreshTimer);
			window.clearInterval(refreshTimer);
			refreshTimer=-1;
		}
		
		currentDialog=null;
	};	
		
	currentDialog=this;
	
	pointer.show ();
};

CTATDialogBase.prototype = Object.create(CTATBase.prototype);
CTATDialogBase.prototype.constructor = CTATDialogBase;
;var FileUtils = {
	
	extToTypeMap : {
		'txt' : 'text/plain',
		'html': 'text/html',
		'ed.html':'text/html',
		'css' : 'text/css',
		'js'  : 'text/javascript',
		'png' : 'image',
		'jpg' : 'image',
		'gif' : 'image'
	},

	typeToExtMap : {
		'text/html': '.html',
		'text/plain': '.txt',
		'text/css': '.css',
		'text/javascript': '.js'
	}
};

FileUtils.getExtension = function(filename)
{
	var len = filename.indexOf('.');
	return (len > -1) ? filename.substring(len+1, filename.length) : null;
}

FileUtils.hasExtension = function(filename)
{
	var ext = FileUtils.getExtension(filename);
	return (!!FileUtils.extToTypeMap[ext]);
}

FileUtils.extensionToMimeType = function(ext)
{
	return (FileUtils.extToTypeMap[ext] ? FileUtils.extToTypeMap[ext] : '');
};

FileUtils.mimeTypeToExtension = function(type)
{
	return (FileUtils.typeToExtMap[type] ? FileUtils.typeToExtMap[type] : '.txt');
}

FileUtils.parseDate = function (date, mode)
{
	var regex = FileUtils.dateFormats[mode];
	var m = regex.exec(date);
	var year   = +m[1];
	var month  = +m[2];
	var day    = +m[3];
	var hour   = +m[4];
	var minute = +m[5];
	var second = +m[6];
	if (mode == 'googledrive')
	{
		var msec   = +m[7];
		var tzHour = +m[8];
		var tzMin  = +m[9];
		var tzOffset = new Date().getTimezoneOffset() + tzHour * 60 + tzMin;

		return new Date(year, month - 1, day, hour, minute - tzOffset, second, msec);
	}
	else if (mode == 'dropbox')
	{
		return new Date(year, month - 1, day, hour, minute, second, 0);
	}
};
	
FileUtils.dateFormats = {	
	'dropbox': /(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})Z/,
	'googledrive': /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})\.(\d{3})([+-]\d{2}):(\d{2})$/
};

/**
* Initialize object used to populate jstree pane in fchooser window
*/
FileUtils.prepTreeObject = function prepTreeObject()
{		
	var treeObject =
	{
		'core' : 
		{
			'check_callback': true,
			'multiple' : false,				
			'data' : [
						{
							'id' : 'root',
							'type': 'folder',
							'text' : 'CTAT',
							'state' : { 
										'opened' : true,
										'selected' : true 
									},
							'children' : []
						}
					]
		},
		'types':
		{
			'folder': {
				'valid_children': ['file', 'folder']
			},
			'file': {
				'valid_children': []
			}
		}
	};
	
	return treeObject;
};

FileUtils.convertFileFormat = function(inFile, inFileFormat)
{
	var file = {};
	switch(inFileFormat)
	{
		case 'dropbox':
			file.id = inFile['path_lower']; 
			file.title = inFile['name'];
			file.mimeType = (inFile['.tag'] === 'folder') ? 'folder' : 
					FileUtils.extensionToMimeType(FileUtils.getExtension(inFile['name']));
			file.modifiedTime = inFile['client_modified'];
			file.fileSize = inFile['size'];
		break;
		case 'box':
			file.id = inFile['id'];
			file.title = inFile['name'];
			file.modifiedTime = inFile['modified_at'];
			file.createdTime = inFile['created_at'];
			file.mimeType = (inFile['type'] === 'folder') ? 'folder' : 
					FileUtils.extensionToMimeType(FileUtils.getExtension(inFile['name']));
			file.fileSize = inFile['size'];
	}
	return file;
};

FileUtils.assertName = function(name, parent, cbk)
{
	cloudUtils.getIdFromName(name, parent, function(id)
		{
			if (id)
			{
				var num = /(.*)\(([0-9]*)\)/.exec(name);
				if (num)
				{
					name = num[1]+'('+(parseInt(num[2], 10)+1)+')';
				}
				else
				{					
					name += '('+1+')';
				}
				FileUtils.assertName(name, parent, cbk);
			}
			else
			{
				cbk(name);
			}
		});
};;
/**
*
*/
var RequestQueue = function()
{
	//array of queues, higher indices = higher priority
	var requests = [[]];
	//index of highest priority queue
	var reqIndex = 0;
	var execTimer;
	var thisReq, lastReq, gap; 
	var inFlight = 0;
	this.add = function(request, callback)
	{
		requests[0].push({'request': request, 'callback': callback});
	
		if (!execTimer)
		{
			execTimer = window.setInterval(execute, 10);
			lastReq = 0;
			thisReq = 0;
			gap = 30;
		}
		inFlight++;
	};
	
	function execute()
	{
		//time 'now'
		thisReq += 10;
		
		if (thisReq - lastReq >= gap)
		{
			//(execution block)
			//find highest priority queue with requests in it
			while(requests[reqIndex].length === 0 && reqIndex > 0)
			{
				reqIndex--;
			}
			var thisIndex = reqIndex;
			var queue = requests[reqIndex];
			var toExecute = queue.shift();
			if (toExecute)
			{
				toExecute.request.execute(function(response)
					{
						inFlight--;
						if (response.error && response.error.message.toLowerCase().includes('user rate limit exceeded'))
						{
							//only handle rate limit errors here (double request interval)
							gap *= 2;
							console.log('RequestQueue hit rate limit, increasing gap to '+gap);
							//add request back to next highest priority queue
							if (requests[thisIndex].length === 0)
							{
								console.log('RequestQueue adding back to queue '+thisIndex);
								requests[thisIndex].push(toExecute);
							}
							else
							{
								thisIndex++;
								console.log('RequestQueue adding to next highest queue at index '+thisIndex);
								if (!requests[thisIndex])
								{
									requests[thisIndex] = [];
								}
								if (requests[thisIndex].length == 0)
								{
									//set highest priority queue to this one
									reqIndex = thisIndex;
									console.log('RequestQueue setting reqIndex to '+thisIndex);
								}
								requests[thisIndex].push(toExecute);
							}
							inFlight++;
						}
						else
						{
							//request completed, call callback
							toExecute.callback(response);
							//check if all requests have come back
							if (inFlight === 0)
							{
								console.log('all requests completed, resetting...');
								//clear timer and reset request interval
								window.clearInterval(execTimer);
								execTimer = null;
								gap = 30;
							}
						}
					});
				lastReq = thisReq;
			}
		}
	}
};;/**-----------------------------------------------------------------------------
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

 Class to interact with Google Drive. Since interaction with
 Drive is done through HTTP requests, callback methods must
 be provided to interact with any returned data.
 
 Methods adapted from those given in the Google Drive API tutorials.
 Note: make sure to include google's api's:
 https://apis.google.com/js/client.js
*/

var client_id = '32492251947-k9b5vu0cfl342q9fjhfltikq2pgjpp3r.apps.googleusercontent.com';
var developerKey = 'AIzaSyAaNDuYgDWnCb7Oo1JclQrr_KPeJfHHN1o';  

/**
*
*/
function handleClientLoad() 
{ 
	ctatdebug ("handleClientLoad()");
	window.setTimeout(gapi.auth.init,1);
}

/**
*
*/
function GoogleDrive()
{ 
	CTATBase.call(this, "GoogleDrive","drive");
	
	var pointer=this;
  
	var scopes = ['https://www.googleapis.com/auth/drive'];
	var oathToken;
	var tempFileID=null;
	var tempFileMetadata=null;
	var requestQueue = new RequestQueue();

	//separates metadata from file data
	const boundary = '-------314159265358979323846';
	const delimiter = "\r\n--" + boundary + "\r\n";
	const close_delim = "\r\n--" + boundary + "--";
  
	/**
	*
	*/
	this.getTempFileID=function getTempFileID ()
	{
		return (tempFileID);
	};

	/**
	*
	*/	
	this.getTempFileMetadata=function getTempFileMetadata ()
	{
		return (tempFileMetadata);
	};

	/**
    Initializes the authorization process. Unless this is successful,
    no call to the Drive API will work. On success will load the Drive API, Picker API, and Google+ API.
    
    immediate indicates if a popup window should appear and if reauthorization should be automatic.
    
    callback should take a boolean authorized, indicating if authorization was successful.
	
	Note: to test for a blocked popup use:
	
	var newWin = window.open(url); 
	if(!newWin || newWin.closed || typeof newWin.closed=='undefined') 
	{ 
		//POPUP BLOCKED
	}
	*/
	this.authorize=function authorize(immediate,callback)
	{
		ctatdebug("authorize("+immediate+")");
		//var pointer=this;
		var reauthorize=pointer.reauthorize;
		gapi.client.setApiKey(developerKey);
		
		ctatdebug("Requesting authorization ...");
		
		gapi.auth.authorize
		({
			'client_id': client_id,
			'scope': scopes, 
			'immediate': immediate
		},
		function (authResult)
		{ 
			ctatdebug ("Processing authorization result");
			
			authorized=authResult && !authResult.error;
			oauthToken = authResult.access_token;		  
			ctatdebug("Authorized result: " + authorized + ", with token: " + authResult.access_token);		  
				
			if(authorized)
			{
				ctatdebug("Access token retrieved, requests to API allowed");			  
				gapi.client.load('drive','v2',function()
				{ 
					//load Drive API			 
					ctatdebug("Drive api loaded");			  
					gapi.load('picker',{callback: function()
					{
						//load picker api
						ctatdebug("Picker api loaded");
		  
						gapi.client.load('plus','v1',function()
						{
							ctatdebug("Google+ api loaded");
							callback(authorized);
						});
					}});
				});			
					
				//reauthorizes user after 45 minutes
				if(!immediate)
				{
					window.setTimeout(reauthorize,45*60*1000);
				}
			}
			else
			{
				ctatdebug("Authorization unsuccessful. immediate: " + immediate);
				// try again prompting the user to login this time
				if(immediate)
				{
					pointer.authorize(false,callback);
				}	
			}
		});
	};
  
	/**
	* This method will try to authorize again. It will set immediate to
	* true, meaning no popup should appear and further reauthorization
	* should be automatic.
	*
	* callback takes in a boolean authorized, indicating if reauthorization is successful.
	*/
	this.reauthorize = function reauthorize(callback)
	{
		ctatdebug("reauthorize()");
		
		if(!callback)
		{
			callback=function(a){ctatdebug("Reauthorization: "+a);};
		}
		
		gapi.auth.authorize
		(
			{
				'client_id': client_id, 
				'scope': scopes, 
				'immediate': true
			},
			function(authResult)
			{
				authorized=authResult && !authResult.error;
				oauthToken = authResult.access_token;
				callback(authorized)
			}
		);
	}
  
	/**
	* Disconnects the user by revoking all authorization. Probably not 
	* going to be used but here if we need it.
	*/
	this.disconnect=function disconnect(callback) 
	{
		var revokeUrl = 'https://accounts.google.com/o/oauth2/revoke?token=' +  oauthToken;

		// Perform an asynchronous GET request.
		jQuery.ajax(
		{
			type: 'GET',
			url: revokeUrl,
			async: false,
			contentType: "application/json",
			dataType: 'jsonp',
			success: function(nullResponse) 
			{
				// Do something now that user is disconnected
				// The response is always undefined.
				
				if (callback)
				{
					callback ();
				}
			},
			error: function(e) 
			{
				// Handle the error
				// ctatdebug(e);
				// You could point users to manually disconnect if unsuccessful
				// https://plus.google.com/apps
			}
		});
	}
	
	/**
	* Copy an existing file.
	*
	* @param {String} originFileId ID of the origin file to copy.
	* @param {String} copyTitle Title of the copy.
	*/
	this.copyFile=function copyFile(originFileId, parentID, optCbk) 
	{
		ctatdebug ("copyFile ("+originFileId+","+parentID+")");
		var parents=[{id:parentID}];
		var body = 
		{
			'parents': parents
		};
		var request = gapi.client.drive.files.copy
		({
			'fileId': originFileId,
			'resource': body
		});
		
		requestQueue.add(request, function(resp) 
		{
			console.log('drive.copyFile response: '+JSON.stringify(resp));
			console.log('ID of copy: ' + resp.id);
			if (optCbk)
			{
				optCbk(resp);
			}
		});
	};
	
	this.moveFile = function(fileId, fromId, toId, optCbk)
	{
		var reqCntr = 2;
		//request to delete parent 1
		var delRequest = gapi.client.drive.parents.delete({
			'parentId': fromId,
			'fileId': fileId
		});
		//request to add parent 2
		var body = {'id': toId};
		var addRequest = gapi.client.drive.parents.insert({
			'fileId': fileId,
			'resource': body
		});
		//response handler
		var cbk = function(response)
		{
			reqCntr--;
			if (reqCntr === 0 && optCbk)
			{
				optCbk();
			}
		};
		requestQueue.add(delRequest, cbk);
		requestQueue.add(addRequest, cbk);
	};
	
	/**
	 * Update an existing file's metadata and content.
	 *
	 * @param {String} fileId ID of the file to update.
	 * @param {Object} fileMetadata existing Drive file's metadata.
	 * @param {File} fileData File object to read data from.
	 * @param {Function} callback Callback function to call when the request is complete.
	 */
	this.updateFile=function updateFile(fileId, fileMetadata, fileData, callback) 
	{
		ctatdebug ("updateFile ()");
		
		var doRequest = function(metadata)
		{
			if (metadata)
				fileMetadata = metadata;
			
			const boundary = '-------314159265358979323846';
			const delimiter = "\r\n--" + boundary + "\r\n";
			const close_delim = "\r\n--" + boundary + "--";
		
			var contentType = fileMetadata.mimeType || 'application/octet-stream';
			// Updating the metadata is optional and you can instead use the value from drive.files.get.
			var base64Data = btoa(unescape(encodeURIComponent(fileData))); /*hack to fix "character outside latin1 range" error*/
			var multipartRequestBody =
				delimiter +
				'Content-Type: application/json\r\n\r\n' +
				JSON.stringify(fileMetadata) +
				delimiter +
				'Content-Type: ' + contentType + '\r\n' +
				'Content-Transfer-Encoding: base64\r\n' +
				'\r\n' +
				base64Data +
				close_delim;

			var request = gapi.client.request(
			{
				'path': '/upload/drive/v2/files/' + fileId,
				'method': 'PUT',
				'params': {'uploadType': 'multipart', 'alt': 'json'},
				'headers': 
				{
					'Content-Type': 'multipart/mixed; boundary="' + boundary + '"'
				},
				'body': multipartRequestBody
			});
			
			if (!callback) 
			{
				callback = function(file) 
				{
					pointer.ctatdebug(file)
				};
			}	
			
			requestQueue.add(request, callback);
		};
		
		if (!fileMetadata)
		{
			pointer.getMetadata(fileId, doRequest.bind(this))
		}
		
		else doRequest.bind(this)();
	}
	

	/**
	* Convenience function, not sure why we started with updateFile. Probably a leftover
	* from starting with the official Google examples.
	*/
	this.saveFile=function saveFile(fileName,fileText,fileType,parent,callback) 
	{
		pointer.insertFile (fileName,fileText,fileType,parent,callback);
	};	
	
	/**
	* Insert a file using raw text. Just another option if we don't have a
	* Javascript File object. Parent is a string representing the file id 
	* of the folder containing this file callback should take in the file's 
	* metadata. Information about that class at:
	*
	* 	https://developers.google.com/drive/v2/reference/files
	*
	* Most important field is id, the id of the file, and parents, the object 
	* representing the folders the file resides in.
	*/
	this.insertFile=function insertFile(fileName,fileText,fileType,parent,callback)
	{
		ctatdebug("insertFile()");
    
		parents=[{id:parent}];
		
		//ctatdebug(parents);
		var contentType = fileType || 'text/plain';
		var metadata = 
		{
			'title': fileName,
			'mimeType': contentType,
			'parents': parents
		};
    
		base64Data = btoa(unescape(encodeURIComponent(fileText))); /*hack to fix "character outside latin1 range" error*/
		
		var multipartRequestBody =//entirety of upload request body
			delimiter +
			'Content-Type: application/json\r\n\r\n' +
			JSON.stringify(metadata) +
			delimiter +
			'Content-Type: ' + contentType + '\r\n' +
			'Content-Transfer-Encoding: base64\r\n' +
			'\r\n' +
			base64Data +
			close_delim;
      
		var request = gapi.client.request(
		{
			//not executed yet
			'path': '/upload/drive/v2/files',//url for file insert
			'method': 'POST',
			'params': {
						'uploadType': 'multipart'
						},
			'headers': {
						'Content-Type': 'multipart/mixed; boundary="' + boundary + '"'
						},
			'body': multipartRequestBody
		});
		
		if (!callback) 
		{
			callback = function(file) 
			{
				ctatdebug(file);//should probably save it somewhere
			};
		}

		//reauthorizes first if necessary (as a fallback)
		requestQueue.add(request, function(resp)
		{
			tempFileID=resp.id;
		
			if(!resp.error)
			{
				callback(resp);
			}
			else if(resp.error.code==401)
			{
				reauthorize(function()
				{
					callback(resp);
				});
			}
			else
			{
				console.warn("An error occurred in insertFile: "+resp.error.message);
				console.trace();
			}
		});//where the request is actually sent    
	};
	
	/** Insert a folder. 
	*
	* parent is a string representing the file id of the folder containing this folder
	* callback should take in the folder's metadata. Information about that
	* class at https://developers.google.com/drive/v2/reference/files
	* Most important field is id, the id of the folder, and parents, the object representing the folders the folder resides in.
	*/
	this.insertFolder=function insertFolder(folderName, aParent, callback)
	{
		ctatdebug("insertFolder("+folderName+","+aParent+")");
    
		var parents=[{id:aParent}];
		var metadata = 
		{
			'title': folderName,
			'mimeType': "application/vnd.google-apps.folder",
			'parents': parents
		};
		var request = gapi.client.request(
		{
			//not executed yet
			'path': '/drive/v2/files',//url for file metadata insert
			'method': 'POST',
			'headers': { 'Content-Type': 'application/json'},
			'body': JSON.stringify(metadata)
		})
		if (!callback) 
		{
			callback = function(file) 
			{
				ctatdebug(file);//should probably save it somewhere
			};
		}
		//reauthorizes first if necessary (as a fallback)
		requestQueue.add(request, function(resp)
			{
				if(!resp.error)
				{
					callback(resp);
				}
				else if(resp.error.code==401)
				{
					reauthorize(function()
					{
						callback(resp);
					});
				}
				else
				{
					console.warn("An error occurred in insertFolder: "+resp.error.message);
					console.trace();
				}
			});//where the request is actually sent
	};
		
	this.renameFile = function(fileId, newTitle, cbk)
	{
		var body = {'title': newTitle};
		var request = gapi.client.drive.files.patch({
		  'fileId': fileId,
		  'resource': body
		});
		
		requestQueue.add(request, function()
		{
			cbk();
		});
	};
		
	/**
	 * Retrieve a list of files belonging to a folder.
	 *
	 * @param {String} folderId ID of the folder to retrieve files from.
	 * @param {Function} callback Function to call when the request is complete.
	 *
	 */
	this.retrieveFilesByFolder=function retrieveFilesByFolder(folderId, callback, forced) 
	{
		ctatdebug("retrieveFilesByFolder("+folderId+")");
	
		var retrievePageOfChildren = function(request, result) 
		{
			requestQueue.add(request, function(resp)
				{
					if ((resp==null) || (resp==undefined))
					{
						ctatdebug ("Fatal error, can't call Google client API");
						return;
					}
					else if (resp.error && resp.error.message)
					{
						console.warn('an error occurred in retrieveFilesByFolder: '+resp.error.code+', '+resp.error.message);
						console.trace();
						return;
					}
					
					result = result.concat(resp.items);
					var nextPageToken = resp.nextPageToken;
					if (nextPageToken) 
					{				
						console.log('there is a next page token');
						request = gapi.client.drive.children.list(
						{
							'folderId' : folderId,
							'pageToken': nextPageToken
						});
						
						retrievePageOfChildren(request, result);
					} 
					else 
					{		
						callback(result);
					}
				});
		};
		
		var initialRequest = gapi.client.drive.children.list
		(
			{
				'folderId' : folderId,
				'q' : 'trashed = false'
			}
		);
		
		retrievePageOfChildren(initialRequest, []);
	};

	
	/**
	* Given a file name, get all files with that name. The name doesn't have
	* to be unique, so we need to return an array.
	* Note that this returns a list of file metadata, not the files themselves
	*
	* callback should take in an array of File resource objects
	*/
	this.retrieveFilesByName = function retrieveFilesByName(fileName,parent,callback)
	{
		ctatdebug("retrieveFile()");
		
		var query = "trashed=false and title='"+fileName+"'";
		
		if(parent)
		{
			query += "and '"+parent+"' in parents";
		}
				
		retrieveFilesByQuery(query,callback);
	};

	this.retrieveFolders = function(parent, callback)
	{
		var queryStr = "trashed = false and mimeType = 'application/vnd.google-apps.folder' and '"+parent+"' in parents";
		retrieveFilesByQuery(queryStr, callback);
	}
	
	/**
	* Given a file id, get the metadata for the file. This is unique, so we only
	* need to return a single File Resource object 
	*/
	this.getMetadata=function getMetadata(fileId,callback)
	{
		ctatdebug("getMetadata()");
		var request = gapi.client.drive.files.get({'fileId': fileId});
		//reauthorizes first if necessary (as a fallback)
		requestQueue.add(request, function(resp)
			{
				if(!resp.error)
				{
					callback(resp);
				}	
				else if(resp.error.code==401)
				{
					reauthorize(function()
					{
						callback(resp);
					});
				}else
				{
					console.warn("An error occured in getMetadata: "+resp.error.message);
					console.trace();
				}
			});//where the request is actually sent
	};	
	
	/**
	* Given a file id, create a metadata request for the file. The request
	* is NOT EXECUTED, just returned to the caller.
	*/
	this.getMetadataRequest=function getMetadataRequest(fileId)
	{
		var request = gapi.client.drive.files.get({'fileId': fileId});
		return (request);
	};
	
	/**
	* Downloads a file given the name. This method makes the assumption that
	* that there is only one such file. If there are multiple, it takes
	* the first one queried. If you need to parse through each different
	* file, use the retrieveFiles + downloadFile methods manually.
	*
	* parent is a string representing the file id of the folder containing this file
	* callback should take in the file's text as a string
	*/
	this.downloadFileByName = function downloadFileByName(fileName,parent,callback)
	{
		ctatdebug("getFile("+fileName+","+parent+")");
	
		if(!callback) callback=function(file)
		{
			ctatdebug(file);
		}
    
		var pointer=this;
		
		pointer.retrieveFilesByName(fileName, parent, function(files)
		{
			if(!files[0])
			{
				callback(null);
			}
			else
			{
				tempFileID=files[0].id;
				pointer.downloadFileById(files[0].id,callback);
			}
		});
	};
	
	/**
	* Downloads a file given the unique ID for the file. Gives the file
	* text if successful or null otherwise.
	*
	* callback should take in the file's text as a string
	*/
	this.downloadFileById=function downloadFileById(fileId, callback) 
	{
		//unfortunately no built in function in Google Client Library API does this
		if(!callback)
		{
			callback=function(a){ctatdebug(a);};
			//ctatdebug("callback was null");
		}
		if(!fileId)
		{
			ctatdebug("fileId null");
			return;
		}
		var fun=function(fileMetaData)
		{
			tempFileMetadata=fileMetaData;	
			ctatdebug('downloadFile()');
			//ctatdebug(fileMetaData);
			if (fileMetaData.downloadUrl) 
			{
				//case it's even downloadable
		
				var accessToken = gapi.auth.getToken().access_token;
				var xhr = new XMLHttpRequest();
				xhr.open('GET', fileMetaData.downloadUrl);
				xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
				xhr.onload = function() 
				{
					//function called if download is successful
					callback(xhr.responseText);
				};
				xhr.onerror = function() 
				{
					//function called if download unsuccessful
					callback(null);
				};
				xhr.send();
			} 
			else 
			{
				ctatdebug("No download url");
				callback(null);
			}
		}
		pointer.getMetadata(fileId,fun);
	};
	
	this.downloadBlobById = function(fileId, callback)
	{
		if(!fileId)
		{
			ctatdebug("fileId null");
			return;
		}
		var fun=function(fileMetaData)
		{
			tempFileMetadata=fileMetaData;	
			ctatdebug('downloadFile()');
			if (fileMetaData.downloadUrl) 
			{
				//case it's even downloadable
				var accessToken = gapi.auth.getToken().access_token;
				var xhr = new XMLHttpRequest();
				xhr.open('GET', fileMetaData.downloadUrl);
				xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
				xhr.responseType = 'arraybuffer';
				xhr.onload = function() 
				{
					//function called if download is successful
					callback(xhr.response);
				};
				xhr.onerror = function() 
				{
					//function called if download unsuccessful
					callback(null);
				};
				xhr.send();
			} 
			else 
			{
				ctatdebug("No download url");
				callback(null);
			}
		}
		pointer.getMetadata(fileId,fun);
	};
	
	/**
	*	Given a file ID, moves that file to the trash.
	*/
	this.trashFile = function trashFile(fileID, cbk)
	{
		pointer.ctatdebug("trashFile() id = "+ fileID);
		var request = gapi.client.drive.files.trash({'fileId': fileID});
		requestQueue.add(request, function(resp)
			{
				if (resp.error)
				{
					alert("Error deleting file: "+resp.error.code+": "+resp.error.message);
				}
				else cbk();
			});
	};
	
	this.sendBatch = function(batch, cbk)
	{
		requestQueue.add(batch, cbk);
	}
	
	this.findCTATFolder = function(cbk)
	{
		console.log('findCTATFolder');
		var query = "title = 'CTAT' and trashed = false and 'root' in parents";
		retrieveFilesByQuery(query, function(result)
		{
			var found = false;
			for (var i = 0; i < result.length; i++)
			{
				console.log('checking file '+result[i].title);
				if (result[i].title === 'CTAT')
				{
					console.log('found folder, id = '+result[i].id);
					cbk(result[i].id);
					found = true;
					break;
				}
			}
			
			if (!found)
			{
				console.log('no ctatFolder found, creating...');
				pointer.insertFolder('CTAT', 'root', function(ctatFolder)
					{
						console.log('CTATFolder created, id = '+ctatFolder.id);
						cbk(ctatFolder.id);
					});
			}
		}, true);
	};
	
	this.getParents = function(fileId, cbk)
	{
		pointer.getMetadata(fileId, function(metadata)
			{
				console.log('drive.getParents cbk, parents = '+JSON.stringify(metadata.parents));
				cbk(metadata.parents);
			});	
	}
	
	/** ---Internal---
	 *	Allows file retrieval by arbitrary query
	 *	@param query a string defining the query parameters
	 *	@param callback a callback function to pass the result to
	 *	@returns an array of file objects
	 */
	var retrieveFilesByQuery = function(query, callback)
	{
		console.log('retrieveFilesByQuery( '+query+' )');
		var request = gapi.client.drive.files.list
		(
			{
				'q':query
			}
		);
		
		requestQueue.add(request, function(response)
			{
				if (!response || !response.items)
				{
					console.warn('An error occured contacting Drive in retrieveFilesByQuery: ');
					if (response.error && response.error.message)
					{
						console.warn(response.error.code+', '+response.error.message);
						console.trace();
					}
					callback(null);
				}
				else 
				{
					callback(response.items)
				}
			});
	}
}

GoogleDrive.prototype = Object.create (CTATBase.prototype);
GoogleDrive.prototype.constructor = GoogleDrive;;
function Box()
{
	//access function for localStorage where oauth token will be stored
	var tokenStore = function(key, val)
	{ 
		if ( arguments.length > 1 ) 
			(localStorage[key] = val) 
		else
			return localStorage[key]; 
	};
	var errHandler = function(req, optCaller)
	{
		optCaller = optCaller || '(function not given)';
		console.log('an error occurred in '+optCaller+': '+req.statusText);
	};
	var hasTriedReauth = false;
	var pointer = this;
	var appKey = 'ozfbs00f9550cs9d3c4d3uumc8nqpsdy';
	var baseUrl = 'https://api.box.com/2.0/';
	/**
	*	Request an access code to the box api through oauth.
	*	@param cbk function to call on successful authorization
	*	@param errCbk function to call if authorization fails
	*/
	this.authorize = function(cbk, errCbk)
	{
		errCbk = errCbk || errHandler;
		if(tokenStore('__boxoauth') 
		&& tokenStore('__boxoauth') !== 'undefined'
		&& tokenStore('__boxoauth') !== 'false'
		&& tokenStore('__boxoauth') !== 'null')
		
		{
			console.log('we have a token already and it looks like: '+tokenStore('__boxoauth'));
			cbk();
			return;
		}
		var authorizeUrl = 'https://account.box.com/api/oauth2/authorize'
		var redirectUrl = 'https://preview.pact.cs.cmu.edu/tutordesk/editor.html?login=true&mode=box';
		//gen csrf token
		var array = new Uint32Array(1);
		window.crypto.getRandomValues(array);
		var csrf = array[0];
		//build query string
		var queryStr = 'response_type=code';
		queryStr += '&client_id='+encodeURIComponent(appKey);
		queryStr += '&redirect_uri='+encodeURIComponent(redirectUrl);
		queryStr += '&state='+encodeURIComponent(csrf);
		var fullUrl = authorizeUrl+'?'+queryStr;
		//store csrf
		tokenStore('__boxcsrf', csrf);
		//redirect to authorization endpoint
		window.location.replace(fullUrl);
	};
	
	
	/**
	*	Renew our access token using a stored refresh_token
	*	@param {function} cbk a function to call on success
	*	@param {function} errCbk a function to call on failure
	*/
	this.reauthorize = function(cbk, errCbk)
	{
		if (!tokenStore('__boxrefresh')
		||	 tokenStore('__boxrefresh') == 'undefined'
		||	 tokenStore('__boxrefresh') == 'false'
		||	 tokenStore('__boxrefresh') == 'null')
		{
			console.log('we have no refresh token, starting alll the way over...');
			tokenStore('__boxoauth', '');
			pointer.authorize(cbk, errCbk);
		}
		errCbk = errCbk || errHandler;
		var appSecret = 'uIDIvwdQZsWSa5pPXlHGntBSGEqBLcRH';
		var tokenUrl = 'https://api.box.com/oauth2/token';
		var xhr = new XMLHttpRequest();
		xhr.open('POST', tokenUrl, true);
		xhr.setRequestHeader('Content-Type', 'x-www-form-urlencoded');
		var params = 'grant_type=refresh_token';
		params += '&client_id='+encodeURIComponent(appKey);
		params += '&client_secret='+encodeURIComponent(appSecret);
		params += '&refresh_token='+encodeURIComponent(tokenStore('__boxrefresh'));
		
		xhr.onload = function()
		{
			console.log('reauth.onload');
			var respObj = JSON.parse(xhr.response);
			tokenStore('__boxoauth', respObj.access_token);
			tokenStore('__boxrefresh', respObj.refresh_token);
			cbk();
		}
		
		xhr.onerror = function()
		{
			console.log('reauth.onerror, falling back on regular auth process');
			tokenStore('__boxoauth', '');
			pointer.authorize(cbk, errCbk);
		}
		
		xhr.send(params);
	};
	
	/**
	*	Create a new directory on the Box server
	*	@param {string} folderName the name of the folder to createElement
	*	@param {string} aParent the id of the parent directory
	*	@param {function} callback a function to call on success
	*/
	this.insertFolder = function(folderName, aParent, callback)
	{
		var requestBody = '{"name": "' + folderName + '", "parent": {"id": "'+aParent+'"}}';
		var xhr = new XMLHttpRequest();
		xhr.open('POST', baseUrl + 'folders');
		xhr.setRequestHeader('Content-Type', 'x-www-form-urlencoded');
		xhr.setRequestHeader('Authorization', 'Bearer ' + tokenStore('__boxoauth'));
		
		xhr.onload = function()
		{
			var resp = FileUtils.convertFileFormat(JSON.parse(xhr.response), 'box');
			callback(resp);
		}
		
		xhr.onerror = errHandler.bind(this, xhr, 'box.insertFolder');
		
		xhr.send(requestBody);
	};
	
	this.disconnect = function(cbk)
	{
		//TODO
	};
	
	/**
	*	Update contents of an existing file on the Box server
	*	@param {string} fileId the id of the file
	*	@param {Object} fileMetadata N/A
	*	@param {string} fileData the contents of the file
	*	@param {function} callback a function to call on success
	*/
	this.updateFile = function(fileId, fileMetadata, fileData, callback)
	{
		var form = new FormData();
		var fileBlob = new Blob([fileData]);
		form.append('file', fileBlob);
		var url = 'https://upload.box.com/api/2.0/files/' + fileId + '/content';
		var xhr = new XMLHttpRequest();
		xhr.open('POST', url);
		xhr.setRequestHeader('Content-type', 'multipart/form-data');
		xhr.setRequestHeader('Authorization', 'Bearer '+tokenStore('__boxoauth'));
		xhr.onload = function()
		{
			callback(JSON.parse(xhr.response));
		};
		xhr.onerror = errHandler.bind(this, xhr, 'box.updateFile');
		
		xhr.send(form);
	};
	
	/**
	*	Alias to this.insertFile()
	*/
	this.saveFile=function saveFile(fileName,fileText,fileType,parent,callback) 
	{
		pointer.insertFile(fileName,fileText,fileType,parent,callback);
	};
	
	/**
	*	Create a new file on the Box server
	*	@param {string} fileName the name of the file to createElement
	*	@param {string} fileText the contents of the file
	*	@param {string} fileType the mimeType of the file
	*	@param {string} parent the id of the parent directory
	*	@param {function} callback a function to call on success
	*/
	this.insertFile = function(fileName,fileText,fileType,parent,callback)
	{
		fileType = fileType || 'text/plain';
		//make sure name includes extension
		if (!FileUtils.hasExtension(fileName))
			fileName += FileUtils.mimeTypeToExtension(fileType);
		//form to send
		var form = new FormData();
		
		//add file attributes to form
		var fileAttr = '{"name":"'+fileName+'", "parent":{"id":"'+parent+'"}}';
		form.append('attributes', fileAttr);
		
		//add file contents to form
		var fileBlob = new Blob([fileText], {type: fileType});
		form.append('file', fileBlob, fileName);
		
		var url = 'https://upload.box.com/api/2.0/files/content';
		var xhr = new XMLHttpRequest();
		xhr.open('POST', url);
		//xhr.setRequestHeader('Content-Type', 'multipart/form-data');
		xhr.setRequestHeader('Authorization', 'Bearer '+tokenStore('__boxoauth'));
		xhr.onload = function()
		{
			callback(JSON.parse(xhr.response));
		};
		xhr.onerror = errHandler.bind(this, xhr, 'box.insertFile');
		
		xhr.send(form);
	};
	
	/**
	*	Retrieve all files with the given name from the given parent folder
	*	@param {string} fileName the filename to match against
	*	@param {string} parent the id of the folder to look in
	*	@param {function} callback a function to pass the result to
	*/
	this.retrieveFilesByName = function	(fileName,parent,callback)
	{
		var filtered = [];
		pointer.retrieveFilesByFolder(parent, function(result)
			{
				for (var i = 0; i < result.length; i++)
				{
					if (result[i].title === fileName)
					{
						filtered.push(FileUtils.convertFileFormat(result[i], 'box'));
					}
				}
			});
		
		callback(filtered);
	};
	
	/**
	*	Retrieve all files from a given folder
	*	@param {string} id the id of the folder to retrieveCallback
	*	@param {function} cbk a function to pass the result to
	*/
	this.retrieveFilesByFolder = function(id, cbk)
	{
		var url = baseUrl + 'folders/' + id + '/items';
		var xhr = new XMLHttpRequest();
		var result;
		xhr.open('GET', url);
		xhr.setRequestHeader('Authorization', 'Bearer '+tokenStore('__boxoauth'));
		var retrieveCallback = function(data)
		{
			result = data.entries;
			console.log('box.retrieveCallback, got '+data.total_count+' entries');
			for (var i = 0; i < result.length; i++)
			{
				result[i] = FileUtils.convertFileFormat(result[i], 'box');
			}
			cbk(result);
		};
		
		xhr.onload = function()
		{
			retrieveCallback(JSON.parse(xhr.response));
		};
		
		xhr.onerror = function()
		{
			console.log('box.retrieveFilesByFolder.onerror, status = '+xhr.status);
			if ((xhr.status == 401 || xhr.status == 0 ) && !hasTriedReauth)
			{
				console.log('401\'d, attempting reauth');
				hasTriedReauth = true;
				pointer.reauthorize(pointer.retrieveFilesByFolder.bind(pointer, id, cbk));
			}
		};
		
		xhr.send();
	};
	
	/**
	*	Retrieve all folders from a given parent
	*	@param {string} id the id of the parent folder
	*	@param {function} cbk a function to pass the result to
	*/
	this.retrieveFolders = function (id, cbk)
	{
		var query = 'ancestor_folder_ids='+id+'&type=folder';
		retrieveFilesByQuery(query, cbk);
	};
	
	/**
	*	Download a file's contents given that file's name
	*	@param {string} fileName the name of the file to retrieve
	*	@param {string} parent the id of the file's parent directory
	*	@param {function} callback a function to pass the filedata to
	*/
	this.downloadFileByName = function downloadFileByName(fileName,parent,callback)
	{
		var getFile = function(listFilesResult)
		{
			if (listFilesResult.length > 0)
			{
				for (var i = 0; i < listFilesResult.length; i++)
				{
					if (listFilesResult[i].title === fileName)
					{
						pointer.downloadFileById(listFilesResult[i].id, callback);
						break;
					}
				}
			}
			else
			{
				console.warn('file '+fileName+' not found');
			}
		};
		
		pointer.retrieveFilesByFolder(parent, getFile);
	};
	
	/**
	*	Download a file's contents given that file's Box ID
	*	@param {string} fileId the id of the file
	*	@param {function} callback a function to pass the filedata to
	*/
	this.downloadFileById=function downloadFileById(fileId, callback) 
	{
		var url = baseUrl+'files/'+fileId+'/content';
		var hdrs = new Headers(
		{
			'Authorization': 'Bearer '+tokenStore('__boxoauth')
		});
		
		var init = {
			method: 'GET',
			headers: hdrs,
			mode: 'cors',
			redirect: 'manual'
		}
		var req = new Request(url, init);
		
		fetch(req)
		.then(function(response)
			{
				console.log('box.downloadFileById.onload');
				if (!response.ok)
				{
					console.log('response not ok!');
					console.log('status: '+response.status);
					console.log('statusText: '+response.statusText);
					console.log('headers: '+ JSON.stringify(response.headers));
					console.log('redirected: '+response.redirected);
					console.log('type: '+response.type);
					console.log('url: '+response.url);
					console.log('list of keys: ');
					for (var key in response)
					{
						console.log(key);
					}
					response.text().then(function(text)
					{
						console.log('response body: '+text);
					});
				}
				else
				{
					console.log('response ok: '+response);
					callback(response);
				}
			})
		.catch(function(error)
			{
				console.log('fetch error: '+ error);
			});
	};
	
	/*
	this.downloadFileById = function downloadFileById(fileId, callback)
	{
		console.log('box.downloadFileById');
		var url = baseUrl+'/files/'+fileId+'/content';
		var xhr = new XMLHttpRequest();
		xhr.open('GET', url);
		xhr.setRequestHeader('Authorization', 'Bearer '+tokenStore('__boxoauth'))
		xhr.onload = function()
		{
			console.log('box.downloadFileById.onload');
			console.log(xhr.response);
			callback(xhr.response);
		};
		xhr.onerror = function()
		{
			console.log('box.downloadFileById.onerror');
			console.log('response url = '+xhr.responseURL);
		};
		xhr.onreadystatechange = function()
		{
			console.log('onreadystatechange: response url = '+xhr.responseURL);
			console.log('full obj: '+JSON.stringify(xhr));
		}
		
		xhr.send();
	};
	*/
	
	
	/**
	*	Get an access token to the box api, using an authorization code
	*	from a successful call to this.authorize
	*	@param {string} code the access code that will be exchanged for a token
	*	@param {function} cbk a function to call on sucessful retrieval
	*	@param {function} errCbk a function to call on failure
	*/
	this.getToken = function(cbk, errCbk)
	{
		console.log('box.getToken');
		if (tokenStore('__boxoauth') 
		&& tokenStore('__boxoauth') !== 'undefined'
		&& tokenStore('__boxoauth') !== 'false'
		&& tokenStore('__boxoauth') !== 'null')
		{
			console.log('already have a token in browser storage');
			cbk();
			return;
		}
		var appSecret = 'uIDIvwdQZsWSa5pPXlHGntBSGEqBLcRH';
		var tokenUrl = 'https://api.box.com/oauth2/token';
		//get token code and csrf back from href
		var query = window.utils.parseQueryString(window.location.search);
		var code = query['code'];
		var state = query['state'] ? query['state'] : null;
		if (code && state == tokenStore('__boxcsrf'))
		{
			var params = 'grant_type='+encodeURIComponent('authorization_code');
			params += '&client_id='+encodeURIComponent(appKey);
			params += '&client_secret='+encodeURIComponent(appSecret);
			params += '&code='+encodeURIComponent(code);
			
			var xhr = new XMLHttpRequest();
			xhr.open('POST', tokenUrl, true);
			xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xhr.onload = function()
			{
				console.log('getToken.onload');
				var respObj = JSON.parse(xhr.response);
				tokenStore('__boxoauth', respObj.access_token);
				tokenStore('__boxrefresh', respObj.refresh_token);
				cbk();
			};
			
			xhr.onerror = function()
			{
				console.log('getToken.onerror');
				errCbk();
			}
			
			xhr.send(params);			
		}
		else if (!state)
		{
			console.log('state param not in redirect');
		}
		else
		{
			if (state != tokenStore('__boxcsrf'))
				console.log('oauth failed, bad CSRF');
			else
				console.log('oauth failed, no token code');
			errCbk();
		}
	}
	
	/**
	*	---Internal---
	*	Retrieve files from the Box server by query string
	*	@param {string} query the string to match against
	*	@param {function} cbk a function to pass the results to
	*/
	function retrieveFilesByQuery(query, cbk)
	{
		var url = baseUrl + 'search?query='+query;
		var xhr = new XMLHttpRequest();
		xhr.open('GET', url);
		xhr.setRequestHeader('Authorization', 'Bearer '+tokenStore('__boxoauth'));
		
		xhr.onload = function()
		{
			var result = JSON.parse(xhr.response).entries;
			for (var i = 0; i < result.length; i++)
			{
				result[i] = FileUtils.convertFileFormat(result[i], 'box');
			}
			cbk(result);
		};
		
		xhr.onerror = errHandler.bind(this, xhr, 'box.retrieveFilesByQuery');
		
		xhr.send();
	}
	
	window.onerror = function(error, url, line)
	{
		console.log('echoing error :'+error+', '+url+', '+line);
	};
	
	window.addEventListener('error', function(error, url, line)
	{
		console.log('echoing error :'+error+', '+url+', '+line);
	}, true);
};;/**
*	@fileoverview A class that represents a generic context menu displayed on right click
*/

/**	@constructor
*	@param menuId the ID of the menu node in the DOM
*	@param optionClass the class applied to options in the menu
*/
var rightClickMenu = function(menuId, optionClass)
{
	//CSS classes applied to menu
	const contextMenuClassName = "rightclick-context-menu";
	const contextMenuActive = "rightclick-context-menu--active";
	const contextMenuItemClassName = "rightclick-context-menu-item";
	const contextMenuDisabledItem = "rightclick-context-menu-item-disabled"
	
	//click coordinate vars
	var clickCoords;
	var clickCoordsX;
	var clickCoordsY;
	
	//menu style vars
	var menuWidth;
	var menuHeight;
	var menuPosition;
	var menuPositionX;
	var menuPositionY;

	var windowWidth;
	var windowHeight;
	
	//whether the menu is visible
	var isActive = false;
	
	//the function responding to the 'contextmenu' event
	var eventListener = null;
	
	//the DOM node of the menu
	var menu = document.querySelector("#"+menuId);
	
	//the DOM nodes of the menu options
	var options = document.querySelectorAll("."+optionClass);
	
	//mapping of option values to handler functions
	var optionHandlers = {};
	
	//whether each option is disabled or not
	var disabled = {};
	
	/**
	*	Set up the listener on the 'contextmenu' event
	*	@param target the DOM node that should be listening
	*	@param handler the function that will handle the event
	*/
	this.setHandler = function(target, handler)
	{
		eventListener = handler
		target.addEventListener('contextmenu', eventListener);
		target.addEventListener('click', this.hide);
	};
	
	/**
	*	Set a handler for an individual menu option
	*	@param optionName the value of the option's 'data-option-value' attribute
	*	@param handler the function that will handle the event
	*/
	this.setOptionHandler = function(optionName, handler)
	{
		optionHandlers[optionName] = handler;
	}
	
	/**
	* 	Turns the custom context menu on
	*	@param clickEvent the event that fired the 'contextmenu' event
	*/
	this.show = function(clickEvent) 
	{
		positionMenu(clickEvent);
		if (isActive)
		{
			return;
		} 
		menu.classList.add(contextMenuActive);
		isActive = true;
	}

	/**
	* 	Hide the context menu
	*/
	this.hide = function() 
	{
		if (isActive)
		{
			menu.classList.remove(contextMenuActive);
			isActive = false;
		}
	};

	/**
	*	Disable an option in the menu
	*	@param optionName the value of the option's 'data-option-value' attribute
	*/
	this.disableOption = function(optionName)
	{
		$(menu).find('li[data-option-value="'+optionName+'"]')[0].classList.add(contextMenuDisabledItem);
		disabled[optionName] = true;
	}
	
	/**
	*	Enable an option in the menu
	*	@param optionName the value of the option's 'data-option-value' attribute
	*/
	this.enableOption = function(optionName)
	{
		$(menu).find('li[data-option-value="'+optionName+'"]')[0].classList.remove(contextMenuDisabledItem);
		disabled[optionName] = false;
	}
	
	/**
	*	Fired when a menu option is clicked
	*	@param e the click event
	*/
	var handleOptionClick = function(e)
	{
		var optionText = e.target.getAttribute('data-option-value');
		if (!disabled[optionText])
			optionHandlers[optionText]();
		
		e.preventDefault();
	};
	
	/**
	* Positions the menu properly.
	* 
	* @param {Object} e The event
	*/
	var positionMenu = function positionMenu(e) 
	{
		clickCoords = getPosition(e);
		clickCoordsX = clickCoords.x;
		clickCoordsY = clickCoords.y;

		menuWidth = menu.offsetWidth + 4;
		menuHeight = menu.offsetHeight + 4;

		windowWidth = window.innerWidth;
		windowHeight = window.innerHeight;

		if ( (windowWidth - clickCoordsX) < menuWidth ) 
		{
			menu.style.left = windowWidth - menuWidth + "px";
		} 
		else 
		{
			menu.style.left = clickCoordsX + "px";
		}

		if ((windowHeight - clickCoordsY) < menuHeight)
		{
			menu.style.top = windowHeight - menuHeight + "px";
		} 
		else 
		{
			menu.style.top = clickCoordsY + "px";
		}
	}
  
  /**
   * Gets exact position of event.
   * 
   * @param {Object} e The event passed in
   * @return {Object} Returns the x and y position
   */
    var getPosition = function getPosition(e) 
    {
		var posx = 0;
		var posy = 0;

		if (!e) var e = window.event;
		
		if (e.pageX || e.pageY) {
		  posx = e.pageX;
		  posy = e.pageY;
		} else if (e.clientX || e.clientY) {
		  posx = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
		  posy = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
		}

		return {
		x: posx,
		y: posy
		};
	}
	
	//Add click listeners to menu options
	for (var i = 0; i < options.length; i++)
	{
		options.item(i).addEventListener('click', handleOptionClick);
	}
	
	//add window-wide listener to hide menu on left-click
	window.addEventListener('click', this.hide);
};/**
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
};
/**
*
*/
window.settingsObject=
{
	internal: 	[
					["mode","browser"]
				],
	parameters: [
					["Remember Window Settings","true"],
					["Show Account Chooser on Startup","true"],
					["Logout if idle","true"],
					["Idle time","45min"]
				],	
	windows: []	
};

/**
*
*/
var CTATSettings = function() 
{
	CTATBase.call (this, "CTATSettings", "settings");
	
	var pointer=this;
	var updateTimer=-1;
	var settingsFileID=null;
	var settingsFileMetadata=null;
	
	/**
	*
	*/
	this.getApplicationMode=function getApplicationMode ()
	{
		return (window.settingsObject.internal ['mode']);
	}
	/**
	*
	*/
	this.isDesktop=function isDesktop ()
	{
		if (window.settingsObject.internal ['mode']=='desktop')
		{
			return (true);
		}
		
		return (false);
	}	
	/**
	*
	*/
	this.getSettingsObject=function getSettingsObject ()
	{
		return (window.settingsObject);
	}
	/**
	*
	*/
	this.init=function init ()
	{
		pointer.ctatdebug ("init ()");
		
		//updateTimer=setInterval (pointer.updateSettings,5*1000*60); // Every 5 minutes

		pointer.load ();
	};
	/**
	*
	*/	
	this.updateSettings=function updateSettings ()
	{		
		pointer.ctatdebug ("init ()");
		
	};
	/**
	*
	*/
	this.load=function load ()
	{
		pointer.ctatdebug ("load ("+cloudUtils.getWorkspaceFolder ()+")");		
		
		cloudUtils.openFileByName (".settings", cloudUtils.getWorkspaceFolder (), pointer.settingsLoaded);
	};
	/**
	*
	*/
	this.save=function save ()
	{
		pointer.ctatdebug ("save ()");		
		
		if (settingsFileID==-1)
		{
			cloudUtils.saveFileAs (pointer.saveResult,
								   ".settings",
								   JSON.stringify (window.settingsObject),
								   cloudUtils.getWorkspaceFolder (),
								   "text/plain");
		}
		else
		{
			// Here we use the stored metadata to update a file in the cloude. Theoretically
			// that should be faster than doing a save as. This code still needs to be 
			// properly factored.
			
			cloudUtils.saveFileAs (pointer.saveResult,
								   ".settings",
								   JSON.stringify (window.settingsObject),
								   cloudUtils.getWorkspaceFolder (),
								   "text/plain");			
		}
	};
	/**
	*
	*/
	this.settingsLoaded=function settingsLoaded (data)
	{
		pointer.ctatdebug ("settingsLoaded ()");
		
		/*
		if (data==null)
		{
			pointer.save ();
		}
		else
		{
			settingsFileID=drive.getTempFileID ();
			settingsFileMetadata=drive.getTempFileMetadata ();
	
			console.log (JSON.stringify (data));
			
			window.settingsObject=jQuery.parseJSON(data);
		}
		*/
	};
	/**
	*
	*/
	this.saveResult=function saveResult (result)
	{
		pointer.ctatdebug ("saveResult ("+result+")");
		
	};
};

CTATSettings.prototype = Object.create(CTATBase.prototype);
CTATSettings.prototype.constructor = CTATSettings;
;/**-----------------------------------------------------------------------------
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

*/

/**
 *
 */
var CTATDesktop = function()
{
	CTATBase.call(this, "CTATDesktop","desktopmanager");

	var pointer=this;
	var icons=[];
	
	var margin_x=10;
	var margin_y=10;
	
	var index_x=margin_x;
	var index_y=margin_y;
	
	var iconPadding=4;
	var iconSpacing=48;
	
	var generator=CTATGuid;
	
	var iconSize=32;
	
	/**
	*
	*/
	this.getIcon = function getIcon (anID)
	{
		for (var i=0;i<icons.length;i++)
		{
			var testIcon=icons [i];
			
			if (testIcon.id==anID)
			{
				return (testIcon);
			}
		}
		
		return (null);
	}
	
	/**
	*
	*/
	this.addIcon=function addIcon (anImage,aFunction,aLabel)
	{
		pointer.ctatdebug ("addIcon ()");

		var uuid=CTATGuid.guid();
		
		pointer.ctatdebug ("Adding icon with id: " + uuid);
		
		var iconData=
		{
			'id' : uuid,
			'image' : anImage,
			'callback' : aFunction,
			'label' : aLabel,
			xCell : aCellX,
			yCell : aCellY,			
			x : index_x,
			y : index_y
		};
		
		icons.push (iconData);
		
		$("#desktop").append ('<div id="'+uuid+'" class="desktop_icon" style="position: absolute; top: '+index_y+'px; left: '+index_x+'px;"><div class="desktop_icon_row"><div class="icon"><img src="'+anImage+'" border="0" style="margin: 0px; padding: 2px; width: 32px; height: 32px;"/></div></div><div class="desktop_icon_row"><div class="icon desktop_label">'+aLabel+'</div></div></div>');
				
		index_x+=(iconSize+iconPadding+iconSpacing);
	};
	
	/**
	*
	*/
	this.addIconPlace=function addIconPlace (anImage,aFunction,aLabel,aCellX,aCellY)
	{
		pointer.ctatdebug ("addIconPlace ()");

		var uuid=CTATGuid.guid();
		
		pointer.ctatdebug ("Adding icon with id: " + uuid);
		
		var iconData=
		{
			'id' : uuid,
			'image' : anImage,
			'callback' : aFunction,
			'label' : aLabel,
			xCell : aCellX,
			yCell : aCellY,
			x : (aCellX*(iconSize+iconPadding+iconSpacing)) + margin_x,
			y : (aCellY*(iconSize+iconPadding+iconSpacing)) + margin_y
		};
		
		icons.push (iconData);
		
		$("#desktop").append ('<div id="'+uuid+'" class="desktop_icon" style="position: absolute; top: '+iconData.y+'px; left: '+iconData.x+'px;"><div class="desktop_icon_row"><div class="icon"><img src="'+anImage+'" border="0" style="margin: 0px; padding: 2px; width: 32px; height: 32px;"/></div></div><div class="desktop_icon_row"><div class="icon desktop_label">'+aLabel+'</div></div></div>');
				
		if (iconData.x>=index_x)		
		{
			index_x=iconData.x;
		}
		
		if (iconData.y>=index_y)
		{
			index_y=iconData.y;
		}		
	};	
	
	/**
	*
	*/
	this.loadDesktopIcons = function loadDesktopIcons (aCallback)
	{
		pointer.ctatdebug ("loadDesktopIcons ()");	
		
		var jqxhr = $.getJSON("desktop.json", function(data)
		{
			pointer.ctatdebug ("success: adding icons...");
		  			
			$.each(data, function(k, v)
			{				
				if (v.length>3)
				{
					pointer.addIconPlace (v [0],v [1],v [2],v [3],v [4]);
				}
				else
				{
					pointer.addIcon (v [0],v [1],v [2]);					
				}
			});			

			if(aCallback)
			{
				aCallback ();
			}
			
		}).done(function() 
		{
			pointer.ctatdebug ("done");
		}).fail(function()
		{
			ctatdebug('missing desktop.json file');
			pointer.ctatdebug ("fail");
		}).always(function()
		{
			pointer.ctatdebug ("always");
		});
		 
		// Set another completion function for the request above
		/*
		jqxhr.complete(function() 
		{
			console.log("Complete event handler");
		});
		*/
	};
	
	/**
	*
	*/
	this.continueInit = function continueInit ()
	{
		pointer.ctatdebug ("continueInit ()");
		
		$(".desktop_icon").draggable(
		{	
			containment: 'window',
			cursor: 'default',
			scroll: false,
			grid: [ iconSize, iconSize ]
		});	
		
		$(".desktop_icon").mouseover(function()
		{
			$(this).addClass('desktop_icon_highlighted');
		});

		$(".desktop_icon").mouseout(function()
		{
			$(this).removeClass('desktop_icon_highlighted');
		});

		$(".desktop_icon").dblclick(function processIconDoubleClick ()
		{
			pointer.ctatdebug ("processIconDoubleClick ("+$(this).attr("id")+")");
			
			var targetIconID=$(this).attr("id");
			
			var targetIcon=pointer.getIcon (targetIconID);
			
			if (targetIcon!=null)
			{
				var icnCallback=targetIcon.callback;
				
				pointer.ctatdebug ("typeof icnCallback: " + typeof icnCallback);
				
				if (typeof icnCallback === "string")
				{
					window [icnCallback]();
				}
				else
				{
					icnCallback ();
				}	
			}
			else
			{
				pointer.ctatdebug ("Internal error: unable to find target icon");
			}
		});			
	};
	
	/**
	* http://api.jqueryui.com/draggable/#option-cursor
	*/
	this.init = function init ()
	{	
		pointer.ctatdebug ("init ()");
		
		pointer.loadDesktopIcons (pointer.continueInit);		
	};
};

CTATDesktop.prototype = Object.create (CTATBase.prototype);
CTATDesktop.prototype.constructor = CTATDesktop;
;/*
jQuery(function()
{	
	console.log("script js");
	
	var template = '<div class="progressHolder"><div class="progress"></div></div>'; 					
	var dropbox = jQuery('#dropbox'),
		message = jQuery('.message', dropbox);
	
	dropbox.filedrop(
	{
		// The name of the $_FILES entry:
		paramname:'pic',		
		maxfiles: 10,
    	maxfilesize: 2,
		url: 'post_file.php',
		
		uploadFinished:function(i,file,response)
		{
			console.log ("uploadFinished ("+file.name+")");
			
			//$.data(file).addClass('done');
			// response is the JSON object that post_file.php returns
		},
		
    	error: function(err, file) 
		{
			switch(err) 
			{
				case 'BrowserNotSupported':
											showMessage('Your browser does not support HTML5 file uploads!');
											break;
				case 'TooManyFiles':
											alert('Too many files! Please select 5 at most! (configurable)');
											break;
				case 'FileTooLarge':
											alert(file.name+' is too large! Please upload files up to 2mb (configurable).');
											break;
				default:
											break;
			}
		},
		
		// Called before each upload is started
		beforeEach: function(file)
		{
			console.log ("beforeEach ("+file.name+")");
		
			//if(!file.type.match(/^image\//))
			//{
			//	alert('Only images are allowed!');
			//	
			//	// Returning false will cause the
			//	// file to be rejected
			//	return false;
			//}		
		},
		
		uploadStarted:function(i, file, len)
		{
			console.log ("uploadStarted ("+file.name+")");
		
			//createImage(file);
		},
		
		progressUpdated: function(i, file, progress) 
		{
			console.log ("progressUpdated ("+file.name+")");
		
			//$.data(file).find('.progress').width(progress);
		}
    	 
	});
		
	function createImage(file)
	{
		var preview = jQuery(template), 
			image = jQuery('img', preview);
			
		var reader = new FileReader();
		
		image.width = 100;
		image.height = 100;
		
		reader.onload = function(e)
		{			
			// e.target.result holds the DataURL which
			// can be used as a source of the image:
			
			image.attr('src',e.target.result);
		};
		
		// Reading the file as a DataURL. When finished,
		// this will trigger the onload function above:
		reader.readAsDataURL(file);		
		message.hide();
		preview.appendTo(dropbox);
		
		// Associating a preview container
		// with the file, using jQuery's $.data():
		
		$.data(file,preview);
	}

	function showMessage(msg)
	{
		message.html(msg);
	}
});
*/;	//css classes for menus
	const contextMenuClassName = "rightclick-context-menu";
	const contextMenuItemClassName = "rightclick-context-menu-item";
	const contextMenuActive = "rightclick-context-menu--active";

	//click coordinate vars
	var clickCoords;
	var clickCoordsX;
	var clickCoordsY;

	//target of right click
	var lastTarget = null;
	
	//denotes whether last click was on a folder node
	var folderClicked = false;
	
	//denotes whether toCut id is a folder
	var folderCut = false;
	
	//targets of copy and cut commands
	var toCopy=null, toCut=null, destination=null;
	
	//handles on both menus and their items
	var fileMenu, fileMenuItems,
		silexMenu, silexMenuItems;
		
	//handle on currently displayed menu, if any
	var activeMenu = null;
	
	//menu style vars
	var menuWidth;
	var menuHeight;
	var menuPosition;
	var menuPositionX;
	var menuPositionY;

	var windowWidth;
	var windowHeight;
	
	/**
	* Initialise our application's code.
	*/
	function initRightClick() 
	{
		var stageDoc = silexApp.model.file.getContentDocument();
		fileMenu = document.querySelector("#file-rightclick-context-menu");
		fileMenuItems = document.querySelectorAll(".file-rightclick-context-menu-item");
		silexMenu = document.querySelector("#silex-rightclick-context-menu");
		silexMenuItems = document.querySelectorAll(".silex-rightclick-context-menu-item");

		console.log ("initRightClick ()");  
		//on right click over file dialog
		$(document).on('contextmenu', function(e)
		{
			folderClicked = false;
			if ($(e.target).closest(".jqx-grid-cell").length)
			{
				//file click
				destination = window.ctatFileChooser.getCurrentFolderId();
				lastTarget = cloudUtils.getIdFromName(window.ctatFileChooser.selectedFile, destination);
				setOptionsEnabled('files');
				showContextMenu(e, fileMenu);
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
				setOptionsEnabled('files');
				showContextMenu(e, fileMenu);
				e.preventDefault();
			}
			else
			{
				hideContextMenu();
			}
		});
		//on right click over silex stage
		$(stageDoc).on('contextmenu', function(e)
		{
			lastTarget = e.target;
			setOptionsEnabled('silex');
			showContextMenu(e, silexMenu);
			e.preventDefault();
		});
		//init listeners on menu items
		for (var i = 0; i < silexMenuItems.length; i++)
		{
			silexMenuItems.item(i).addEventListener('click', handleSilexMenuItem);
		}
		for (var i = 0; i < fileMenuItems.length; i++)
		{
			fileMenuItems.item(i).addEventListener('click', handleFileMenuItem);
		}
		//listen for clicks outside menu to close italics
		document.addEventListener('click', hideContextMenu);
		stageDoc.addEventListener('click', hideContextMenu);
	}

	/**
	* Turns the custom context menu on.
	*/
	function showContextMenu(clickEvent, menu) 
	{
		console.log ("showContextMenu(), lastTarget = "+lastTarget);
		positionMenu(clickEvent, menu);
		if (menu === activeMenu)
		{
			return;
		} 
		else if (activeMenu)
		{
			//hide other one
			hideContextMenu();
		}
		menu.classList.add(contextMenuActive);
		activeMenu = menu;
	}

	/**
	* Turns the custom context menu off.
	*/
	function hideContextMenu() 
	{
		if (activeMenu)
		{
			activeMenu.classList.remove(contextMenuActive);
			activeMenu = null;
		}
	}

	function handleFileMenuItem(e) 
	{
		console.log('handleFileMenuItem() ');

		var menuText = e.target.getAttribute('data-silex-value');
		switch(menuText)
		{
			case 'cut':
				//record id for later paste
				toCut = lastTarget;
				toCopy = null;
				folderCut = folderClicked;
			break;
			case 'delete':
				window.ctatFileChooser.deleteFile(lastTarget, folderClicked);
			break;
			case 'copy':
				//record id for later paste
				toCopy = lastTarget;
				toCut = null;
			break;
			case 'paste':
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
			break;
			case 'rename':
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
			break;
		}
		e.preventDefault();
	}
	
	function handleSilexMenuItem(e)
	{
		console.log('handleSilexMenuItem ');

		var menuText = e.target.getAttribute('data-silex-value');
		switch (menuText)
		{
			case 'copy':
				console.log('silexContextMenu -- Copy');
				silexApp.controller.editMenuController.copySelection();
			break;
			case 'delete':
				console.log('silexContextMenu -- Delete');
				silexApp.controller.editMenuController.removeSelectedElements();
			break;
			case 'paste':
				console.log('silexContextMenu -- Paste');
				silexApp.controller.editMenuController.pasteSelection();
			break;
		}
		
		e.preventDefault();
	}
	
	function setOptionsEnabled(menuType)
	{
		var option;
		if (menuType == 'files')
		{
			//paste
			if (!toCopy && !toCut)
			{
				$(fileMenu).find('li[data-silex-value="paste"]')[0].classList.add('rightclick-context-menu-item-disabled');
			}
			else
			{
				$(fileMenu).find('li[data-silex-value="paste"]')[0].classList.remove('rightclick-context-menu-item-disabled');
			}
			// copy
			if (folderClicked)
			{
				$(fileMenu).find('li[data-silex-value="copy"]')[0].classList.add('rightclick-context-menu-item-disabled');
			}
			else
			{
				$(fileMenu).find('li[data-silex-value="copy"]')[0].classList.remove('rightclick-context-menu-item-disabled');
			}
			//delete
			if (lastTarget === cloudUtils.getRootFolder())
			{
				$(fileMenu).find('li[data-silex-value="delete"]')[0].classList.add('rightclick-context-menu-item-disabled');
			}
			else
			{
				$(fileMenu).find('li[data-silex-value="delete"]')[0].classList.remove('rightclick-context-menu-item-disabled');
			}
		}
		else
		{
			//paste
			if(!silex.controller.ControllerBase.clipboard 
			|| silex.controller.ControllerBase.clipboard.length === 0)
			{
				$(silexMenu).find('li[data-silex-value="paste"]')[0].classList.add('rightclick-context-menu-item-disabled');
			}
			else
			{
				$(silexMenu).find('li[data-silex-value="paste"]')[0].classList.remove('rightclick-context-menu-item-disabled');
			}
			// copy / delete
		    if (lastTarget.tagName.toLowerCase() === 'body'
			||  lastTarget.getAttribute('data-silex-id') === 'background-initial')
			{
				$(silexMenu).find('li[data-silex-value="copy"]')[0].classList.add('rightclick-context-menu-item-disabled');
				$(silexMenu).find('li[data-silex-value="delete"]')[0].classList.add('rightclick-context-menu-item-disabled');
			}
			else
			{
				$(silexMenu).find('li[data-silex-value="copy"]')[0].classList.remove('rightclick-context-menu-item-disabled');
				$(silexMenu).find('li[data-silex-value="delete"]')[0].classList.remove('rightclick-context-menu-item-disabled');
			}
		}
	}
	
	/**
	* Positions the menu properly.
	* 
	* @param {Object} e The event
	*/
	function positionMenu(e, menu) 
	{
		clickCoords = getPosition(e);
		clickCoordsX = clickCoords.x;
		clickCoordsY = clickCoords.y;

		menuWidth = menu.offsetWidth + 4;
		menuHeight = menu.offsetHeight + 4;

		windowWidth = window.innerWidth;
		windowHeight = window.innerHeight;

		if ( (windowWidth - clickCoordsX) < menuWidth ) 
		{
			menu.style.left = windowWidth - menuWidth + "px";
		} 
		else 
		{
			menu.style.left = clickCoordsX + "px";
		}

		if ((windowHeight - clickCoordsY) < menuHeight)
		{
			menu.style.top = windowHeight - menuHeight + "px";
		} 
		else 
		{
			menu.style.top = clickCoordsY + "px";
		}
	}
  
  /**
   * Gets exact position of event.
   * 
   * @param {Object} e The event passed in
   * @return {Object} Returns the x and y position
   */
    function getPosition(e) 
    {
		var posx = 0;
		var posy = 0;

		if (!e) var e = window.event;
		
		if (e.pageX || e.pageY) {
		  posx = e.pageX;
		  posy = e.pageY;
		} else if (e.clientX || e.clientY) {
		  posx = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
		  posy = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
		}

		return {
		x: posx,
		y: posy
		};
	}
;/**
 * @fileoverview A class that represents the UI part of the cloud file system code
 *
 * @author $Author: $
 * @version $Revision: $
 */
 
//goog.require('CTATBase');

/**
*
*/
var CTATTutorPlayer = function() 
{		
	CTATDialogBase.call (this, "#tutorplayer", "CTATTutorPlayer", "fileui","MODAL");
	
	var pointer=this;
	
	/**
	*
	*/
	this.showOpenDialog = function showOpenDialog ()
	{
		pointer.ctatdebug ("showOpenDialog ()");
				
		pointer.show ();	
	};
	
	function initClose()
	{
		$('#tutorplayer > .ctatwindowclose').on('click', pointer.close);
	}
	
	var super_close = this.close;
	this.close = function()
	{
		$('#tutorplayer > .ctatcontent').html('');
		super_close();
	};
	
	initClose();
};

CTATTutorPlayer.prototype = Object.create(CTATDialogBase.prototype);
CTATTutorPlayer.prototype.constructor = CTATTutorPlayer;
;/**-----------------------------------------------------------------------------
 $Author$
 $Date$
 $HeadURL$
 $Revision$

 -
 License:
 -
 ChangeLog:
 -
 Notes:
  
*/ 

var wizardActive=false;

/**
*
*/
function iconBarSetup ()
{
	enableIconClick ();
}

/**
*
*/
function enableIconClick ()
{
	jQuery('#iconMoodle').on('click', showMoodle);
	jQuery('#iconSCORM').on('click', showSCORM);
	jQuery('#iconEdX').on('click', showEdX);
	jQuery('#iconOLI').on('click', showOLI);
	jQuery('#iconTutorShop').on('click', showTutorShop);
	jQuery('#iconXAPI').on('click', showXAPI);
}

/**
*
*/
function disableIconClick ()
{
	jQuery('#iconMoodle').off('click');
	jQuery('#iconSCORM').off('click');
	jQuery('#iconEdX').off('click');
	jQuery('#iconOLI').off('click');
	jQuery('#iconTutorShop').off('click');
	jQuery('#iconXAPI').off('click');
}

/**
*
*/
function showMoodle()
{
	console.log ("showMoodle ()");
	
	if (wizardActive==true)
	{
		return;
	}
	
	disableIconClick ();
	
	document.getElementById ('wizardcontents').src="moodle.html";	
	jQuery("#wizard").toggle();
}

/**
*
*/
function showSCORM()
{
	console.log ("showSCORM ()");
	
	if (wizardActive==true)
	{
		return;
	}	
	
	disableIconClick ();	
	
	document.getElementById ('wizardcontents').src="scorm.php";	
	jQuery("#wizard").toggle();
}

/**
*
*/
function showEdX()
{
	console.log ("showEdX ()");
	
	if (wizardActive==true)
	{
		return;
	}	
	
	disableIconClick ();
	
	document.getElementById ('wizardcontents').src="edx.html";	
	jQuery("#wizard").toggle();
}

/**
*
*/
function showOLI ()
{
	console.log ("showOLI ()");
	
	if (wizardActive==true)
	{
		return;
	}	
	
	disableIconClick ();	
	
	document.getElementById ('wizardcontents').src="oli.html";		
	jQuery("#wizard").toggle();
}

/**
*
*/	
function showTutorShop ()
{
	console.log ("showTutorShop ()");
	
	if (wizardActive==true)
	{
		return;
	}	
	
	disableIconClick ();
	
	document.getElementById ('wizardcontents').src="tutorshop.html";		
	jQuery("#wizard").toggle();
}

/**
*
*/
function showXAPI ()
{
	console.log ("showXAPI ()");
	
	if (wizardActive==true)
	{
		return;
	}	
	
	disableIconClick ();
	
	document.getElementById ('wizardcontents').src="xapi.html";		
	jQuery("#wizard").toggle();
}

/**
*
*/	
/* 
$(document).ready(function() 
{
	console.log ("envwizard js ()");
	
	$("#menu").menu();
		
	iconBarSetup ();
});	
*/

/**
* Since we're now running in various execution environments we need to carefull
* how we start. I've created a function instead of reacting to the document ready
* event so that we can call all these startup pieces from one place. I've moved
* this call to startTutordesk ()
*/
function envSetup ()
{
	console.log ("envSetup ()");
	
	$("#menu").menu();
		
	iconBarSetup ();
}
;/**-----------------------------------------------------------------------------
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

 view-source:http://jgraph.github.io/mxgraph/javascript/examples/helloworld.html
*/

var rootFolder="root";

// Model variables
var graphContainer=null;
var graphBRD=null;
var graphView=null;
var graphModel=null;
var graphParent=null;
var graphLayout=null;
var graph=null;
var rubberband = null;
var graphShown=false;

// width/height of nodes
var w = 80;
var h = 30;

// Interaction settings

var graphSnap=false;
var graphGrid=true;

var graphNodes=null;
var graphEdges=null;

var isGraphDemo=false;

/**
*
*/
function setContainer (container)
{
	graphContainer=container;
}

/**
*
*/
function view_br ()
{
	console.log ("view_br ()");
	
	if (!mxClient.isBrowserSupported())
	{
		// Displays an error message if the browser is not supported.
		mxUtils.error('Browser is not supported!', 200, false);
		return;
	}
	//init graph
	setContainer(document.getElementById('graphContainer'));
	generateGraph();
	
	if (isGraphDemo==true)
	{
		// Disables the built-in context menu
		mxEvent.disableContextMenu(document.getElementById('graphContainer'));
		// Adds cells to the model in a single step
		graph.getModel().beginUpdate();
		try
		{
			var v1 = graph.insertVertex(graphParent, null, 'Hello,', 20, 20, w, h);
			var v2 = graph.insertVertex(graphParent, null, 'World!', 200, 150, w, h);
			var e1 = graph.insertEdge(graphParent, null, '', v1, v2);
		}
		finally
		{
			// Updates the display
			graph.getModel().endUpdate();
		}
	}
	else
	{
		//$('#graph_menu').menubutton('enable');
		//$('#tutormode').disabled = false;
		loadBRD('https://preview.pact.cs.cmu.edu/tutordesk/tests/testBRDs/balloons.brd');
	}
}

/**
*
*/
function hide_br ()
{
	console.log ("hide_br ()");
	
	clearGraph ();	
	$('#graph_menu').menubutton('disable');
	$('#tutormode').disabled = true;
}

function createBR (Y)
{
	view_br_panel = new Y.Panel(
	{
		srcNode: '#view_br',
		headerContent: 'Behavior Recorder',
		width: 650,
		height: 550,
		centered: true,
		modal: false,
		visible: false,
		zIndex : 1020,
		render: true
	});

	var view_br_resize = new Y.Resize(
	{
		node: '#view_br',
		preserveRatio: true,
		wrap: true,
		handles: 'br'
	});	
	
	view_br_panel.plug(Y.Plugin.Drag, 
	{
		handles: ['.yui3-widget-hd']
	});	
	
	view_br_panel.on('visibleChange',function(o) 
	{
		console.log ("graphShown: " + graphShown);
	
		if (graphShown===true)
		{
			hide_br ();
			graphShown=false;
		}
		else
		{
			graphShown=true;
		}
	});
}

/**
* For example: http://augustus.pslc.cs.cmu.edu/html5/HTML5TestFiles/BasicTests/FinalBRDs/1416.brd
*/
function loadBRD (aURL)
{
	$.get((aURL), function (data)
	{		
		parseBRD (data);
	});	
}

/**
*
*/
function parseBRD (data)
{
	console.log ("parseBRD ()");

	parseToDOM (data);
	buildModel ();
}

/**
*
*/
function parseToDOM (data)
{
	console.log ("parseToDOM ()");
	//Arrays to store nodes and edges
	graphNodes=new Array ();
	graphEdges=new Array ();
	//Object to parse xml data
	var xmlParser=new CTATXML ();
	
	//parse xml data
	xmlDoc=xmlParser.parseXML (data);
	if (xmlDoc===null)
	{
		console.log ("Error parsing xml");
		return;
	}			
	
	console.log ("Root: " + xmlParser.getElementName (xmlDoc));
	
	var rootChildren=xmlParser.getElementChildren (xmlDoc);
	for (var t=0;t<rootChildren.length;t++)
	{
		var entry=rootChildren [t];
		
		//>---------------   Process Node   --------------------
		if (xmlParser.getElementName (entry)=="node")
		{
			//console.log ("Found graph node");
			var newNode=new CTATExampleTracerNodeVisualData ();
			graphNodes.push (newNode);
			var nodeChildren=xmlParser.getElementChildren (entry);
			for (var i=0;i<nodeChildren.length;i++)
			{
				var nodeElement=nodeChildren [i];
				if (xmlParser.getElementName (nodeElement)=="text")
				{
					newNode.setLabel (xmlParser.getNodeTextValue (nodeElement));
				}					
				if (xmlParser.getElementName (nodeElement)=="uniqueID")
				{
					newNode.setID (xmlParser.getNodeTextValue (nodeElement));
				}									
				if (xmlParser.getElementName (nodeElement)=="dimension")
				{
					var dimensionChildren=xmlParser.getElementChildren (nodeElement);
					for (var j=0;j<dimensionChildren.length;j++)
					{
						var coordinate=dimensionChildren [j];
						if (xmlParser.getElementName (coordinate)=="x")
						{
							var xValue=xmlParser.getNodeTextValue (coordinate);
														
							newNode.setX (xValue);
						}
						if (xmlParser.getElementName (coordinate)=="y")
						{
							var yValue=xmlParser.getNodeTextValue (coordinate);
							newNode.setY (yValue);
						}						
					}	
				}
			}	
		}		
		//>---------------    Process Edge   ------------------
		if (xmlParser.getElementName (entry)=="edge")
		{
			console.log ("Found edge node");
			var newEdge=new CTATExampleTracerLinkVisualData ();									
			graphEdges.push (newEdge);
			var edgeChildren=xmlParser.getElementChildren (entry);
			for (var w=0;w<edgeChildren.length;w++)
			{
				var edgeElement=edgeChildren [w];				
				if (xmlParser.getElementName (edgeElement)=="sourceID")
				{
					newEdge.setSource (xmlParser.getNodeTextValue (edgeElement));
				}
				if (xmlParser.getElementName (edgeElement)=="destID")
				{
					newEdge.setDestination (xmlParser.getNodeTextValue (edgeElement));
				}				
			}	
		}
		
		//>---------------------------------------------------------------		
	}	
}


/**
* Creates the graph inside the given container
*/
function generateGraph ()
{
	console.log ("generateGraph ()");
	if (graph===null)
	{
		console.log ("Creating new graph object ...");
		graph = new mxGraph(graphContainer);
		configureGraph (graph);
		graphView=graph.getView ();
		graphModel=graph.getModel();
		graphModel.addListener(mxEvent.CHANGE,propagateGraphEdits);
	
		//var config = mxUtils.load('mxgraph/editors/config/keyhandler-commons.xml').getDocumentElement();
		//var editor = new mxEditor(config);
		
		// Disables basic selection and cell handling
		graph.setEnabled(true);
		graph.setGridEnabled(graphSnap);
		// Gets the default graphParent for inserting new cells. This
		// is normally the first child of the root (ie. layer 0).
		clearGraph ();
		rubberband = new mxRubberband(graph);
		graph.gridSize = 40;
		// Creates a layout algorithm to be used with the graph
		graphLayout = new mxFastOrganicLayout(graph);
		// Moves stuff wider apart than usual
		graphLayout.forceConstant = 80;			
	}
}

/**
*
*/
function buildModel ()
{
	console.log ("buildModel ()");

	if (graph===null)
	{
		console.log ("Error: graph is null, can't build model");
		return;
	}
	// Adds cells to the model in a single step
	graphModel.beginUpdate();
	try
	{
		console.log ("Building ...");
		for (var i=0;i<graphNodes.length;i++)
		{
			var aNode=graphNodes [i];
			var vizReference=graph.insertVertex(graphParent, null, aNode.getLabel (), aNode.getX (), aNode.getY (), w, h);
			aNode.setVizReference (vizReference);
		}
		for (var j=0;j<graphEdges.length;j++)
		{
			var anEdge=graphEdges [j];
			var fromNode=getNode (anEdge.getSource ());
			var toNode=getNode (anEdge.getDestination ());
			if ((fromNode!==null) && (toNode!==null))
			{
				graph.insertEdge(graphParent, null, 'ab', fromNode.getVizReference (), toNode.getVizReference ());
			}
			else
			{
				console.log ("Error: either the from or to node is null for this edge");
			}
		}	
		//graphLayout.execute(graphParent);
	}
	catch (err)
	{
		console.log('Error generating visual graph model: " + err.message');
		setStatus ("Error generating visual graph model: " + err.message);
	}
	finally
	{
		console.log ("Updating display ...");
		graphModel.endUpdate();
	}
}

/**
*
*/
function configureGraph (graph)
{
	// Changes the default vertex style in-place
	var style = graph.getStylesheet().getDefaultVertexStyle();
	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
	style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter ;
	//style[mxConstants.STYLE_GRADIENTCOLOR] = 'white';
	style[mxConstants.STYLE_FILLCOLOR]= 'white';
	style[mxConstants.STYLE_AUTOSIZE] = "true";
	style[mxConstants.STYLE_FONTSIZE] = '10';					
}

/**
*
*/
function propagateGraphEdits (sender, evt)
{
	setStatus ("propagateGraphEdits ()");
	
	var changes = evt.getProperty('edit').changes;
	var nodes = [];
	var codec = new mxCodec();

	for (var i = 0; i < changes.length; i++)
	{
		nodes.push(codec.encode(changes[i]));
	}
	
	// do something with the nodes	
}

/**
*
*/
function enableSnap (snap)
{
	graphSnap = snap;
	if (graphSnap)
	{
		graph.setGridEnabled(true);
	}
	else
	{
		graph.setGridEnabled(false);
	}
}

/**
*
*/
function showGrid ()
{
	if (graphGrid===true)
	{
		graphGrid=false;
	}
	else
	{
		graphGrid=true;
	}
}

/**
*
*/
function clearGraph ()
{
	console.log ("clearGraph ()");
	
	// Clear any existing cells if they exist
	if (graph!==null)
	{
		graph.getModel ().clear ();
		graphParent = graph.getDefaultParent();
	}	
}

/**
*
*/
function getNode (anID)
{
	for (var i=0;i<graphNodes.length;i++)
	{
		var aNode=graphNodes [i];
		if (aNode.getID ()==anID)
		{
			return (aNode);
		}
	}
	
	return (null);
}

;/**-----------------------------------------------------------------------------
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

*/

/**
 *
 */
var CTATWindow = function()
{
	CTATBase.call(this, "CTATWindow");

	var pointer=this;

	var windowID="";	
	var windowState="DEFAULT"; // Options are: DEFAULT, MAXIMIZED, MINIMIZED
	var windowMode="MODELESS"; // Either MODAL or MODELESS

	var storedX=0;
	var storedY=0;
	var storedWidth=0;
	var storedHeight=0;

	/**
	*
	*/
	this.setWindowID=function setWindowID (anID)
	{
		windowID=anID;
	};
	
	/**
	*
	*/
	this.getWindowID=function getWindowID ()
	{
		return (windowID);
	};	
	/**
	*
	*/
	this.setWindowState=function setWindowState (aState)
	{
		windowState=aState;
	};
	
	/**
	*
	*/
	this.getWindowState=function getWindowState ()
	{
		return (windowState);
	};
	
	/**
	*
	*/	
	this.setWindowMode=function setWindowMode (aMode)
	{
		windowMode=aMode;
	};
	
	/**
	*
	*/	
	this.getWindowMode=function getWindowMode ()
	{
		return (windowMode);
	};	
	
	/**
	*
	*/
	this.storeDimensions=function storeDimensions(anX,anY,aWidth,aHeight)
	{
		storedX=anX;
		storedY=anY;
		storedWidth=aWidth;
		storedHeight=aHeight;
	};

	/**
	*
	*/	
	this.getStoredX=function getStoredX ()
	{
		return (storedX);
	};
	
	/**
	*
	*/	
	this.getStoredY=function getStoredY ()
	{
		return (storedY);
	};
	
	/**
	*
	*/	
   	this.getStoredWidth=function getStoredWidth ()
	{
		return (storedWidth);
	};
	
	/**
	*
	*/	
	this.getStoredHeight=function getStoredHeight ()
	{
		return (storedHeight);
	};

	this.setStatus = function(status)
	{
		var statusField;
		if (windowID.indexOf('#') == 0)
			statusField = $(windowID+' > .ctatstatusbar > .status');
		else
			statusField = $('#'+windowID+' > .ctatstatusbar > .status');
		if (statusField)
		{
			statusField.text(status);
		}
		else
			console.warn("CTATWindow couldn't find the statusbar!");
	};
};

CTATWindow.prototype = Object.create (CTATBase.prototype);
CTATWindow.prototype.constructor = CTATWindow;
;/**
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
		
		/*
		$(wId+"-close").on("click",function ()
		{
			pointer.ctatdebug ("Close on click");	
			
			pointer.close ();
		});		
		*/
		
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
	;/**
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
	
	this.initTypeSelect = function()
	{
		$('#fdialogfiletype').on('change', function()
		{
			var filetype = $('#fdialogfiletype').val();
			console.log('filechoooser.onTypeChange(), type = '+filetype);
			pointer.fileDialogReload(null, filetype);
		});
	};
	
	this.getCurrentFolderId = function()
	{
		return currentFolderid;
	}
	
	this.setFileType = function(type)
	{
		$('#fdialogfiletype').val(type);
	};
	
	this.getFileType = function()
	{
		return fileFilter;
	}
	
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
	 */
	var super_show = this.show;
	this.show = function(mode, optData)
	{
		pointer.ctatdebug('show( '+mode+' )');
		$(wId).attr('data-fchooser-mode', mode);
		
		$('#fname_input').val('');
		$('#fname_input').removeAttr('disabled');
		$('#fdialogfiletype').removeAttr('disabled');

		if (!$(wId).data('initialized'))
			pointer.init();

		switch(mode)
		{
			case 'OPEN_SILEX':
				fileFilter = 'text/html';
				pointer.setTitle('Open');
				pointer.setFileType('html');
			break;
			case 'OPENIMG':
				fileFilter = 'image';
				pointer.setTitle('Select Image');
				pointer.setFileType('image');
			break;
			case 'OPENAUDIO':
				fileFilter = 'audio';
				pointer.setTitle('Select Audio File');
				pointer.setFileType('audio');
			break;
			case 'OPENSTYLESHEET':
				fileFilter = 'text/css';
				pointer.setTitle('Select Stylesheet');
				pointer.setFileType('css');
			break;
			case 'OPENSCRIPT':
				fileFilter = 'javascript';
				pointer.setTitle('Select JS File');
				pointer.setFileType('js');
			break;
			case 'SAVE_SILEX':
				fileFilter = 'text/html';
				pointer.setTitle('Save File');
				pointer.setFileType('html');
				if (optData && typeof(optData) === 'string')
				{
					$('#fname_input').val(optData);
				}
			break;
			case 'PUBLISH':
				fileFilter = 'text/html';
				pointer.setFileType('html');
				pointer.setTitle('Publish');
				if (lastPublishedName)
				{
					$('#fname_input').val(lastPublishedName);
				}
				loadLastPkg();
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
			//pointer.setRefreshTimer (window.setInterval (pointer.fileDialogReload,60000));
		}
		//call super
		super_show();
	};
	
	/**
	*	Delete the currently selected file or folder
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
	*
	*/
	this.fileDialogHome = function fileDialogHome(e)
	{
		pointer.ctatdebug ("fileDialogHome ()");
		
	};
	
	/**
	*	Display new folder dialog 
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
		var mode = $(wId).attr('data-fchooser-mode');
		
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
					else
					{
						window.silexApp.view.stage.setStatus('Loading file...');
						//Open the selected file in the editor
						if (!cloudUtils.openFileByName (targetFiles [0],currentFolderid,function (response)
							{
								//clear url (asset) map
								window.silexApp.model.file.resetUrlMap();
								window.silexApp.model.file.setHtml(response, function()
								{
									//reset undo history
									window.silexApp.controller.stageController.undoReset();
									window.silexApp.view.stage.setScrollX(30);
									
									//init dblclick listeners on text blocks and mult choice questions
									window.silexApp.model.file.initTextEditListeners();
									window.silexApp.model.file.initQuestionEditListeners(window.silexApp.view.stage);
									
									//populate group, script, and stylesheet menus
									window.silexApp.view.menu.populateMenus();
									window.silexApp.view.stage.setStatus('');
									window.silexApp.view.stage.setFilename(targetFiles[0]);
									window.silexApp.model.file.setMeta('parent-id', currentFolderid);
								});
							}))
						{
							window.silexApp.view.stage.setStatus('File not found :(');
						}
					}
					pointer.close ();
				break;
				case "OPENIMG":
					//Open the selected image in the editor
					silexApp.view.stage.setStatus('Loading '+targetFiles[0]+'...');
					var fileId = cloudUtils.getIdFromName(targetFiles[0], currentFolderid);
					cloudUtils.openBlobById(fileId, function(resp)
					{
						window.silexApp.controller.propertyToolController.setBlobImgUrl(fileId, resp, targetFiles[0]);
					});
					pointer.close ();
				break;
				case "OPENAUDIO":
					silexApp.view.stage.setStatus('Loading '+targetFiles[0]+'...');
					var audioId = cloudUtils.getIdFromName(targetFiles[0], currentFolderid);
					cloudUtils.openBlobById(audioId, function(resp)
						{
							window.silexApp.controller.propertyToolController.setBlobAudioUrl(audioId, resp, targetFiles[0]);
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
				case "SAVE_SILEX":
					this.publishInterface(targetFiles[0], currentFolderid, false);
				break;
				case "PUBLISH":
					this.publishInterface(targetFiles[0], currentFolderid, false);
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
	
	this.createPackage = function(packageName, cbk)
	{
		var pendingRequests = 4;
		var resp = {};
		cbk = cbk || function() {silexApp.view.stage.setStatus('package '+packageName+' created')};
		var assetCreated = function(response)
		{
			pendingRequests--;
			resp['assetId'] = response.id;
			if (pendingRequests == 0)
			{
				cbk(resp);
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
				cbk(resp);
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
			var valid = validatePkg(pkgName);
			
			if (valid)
			{
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
			else
			{
				alert('The selected package folder is not formatted properly.'+
					  ' A package folder must contain a subdirectory called "HTML",'+
					  ' and the HTML subdirectory must contain a subdirectory called "Assets"');
			}
		}
	};
/*	
	this.saveInterface = function(filename, pkgName, doCreateNew)
	{
		var saveFunc = function(pkgInfo)
		{
			window.silexApp.view.stage.setStatus('Saving...');
			var htmlDir = pkgInfo['htmlId'];
			var pkgDir = pkgInfo['pkgId'];
			//set interface-id meta tag
			window.silexApp.model.file.setMeta('interface-id', filename);
			//set parent-id meta tag
			window.silexApp.model.file.setMeta('parent-id', htmlDir);
			//set rest of file data
			var fileObj = {
				type: 'text/html',
				data: window.silexApp.model.file.getHtml()
			};
			if (FileUtils.getExtension(filename) !== 'ed.html')
				filename += '.ed.html';
			cloudUtils.saveFile(
					filename,
					false, 
					htmlDir, 
					fileObj, 
					function() {
						silex.controller.ControllerBase.lastSaveUndoIdx = silex.controller.ControllerBase.undoHistory.length - 1;
						silexApp.view.contextMenu.redraw();
					}
			);
		};
		
		pointer.prepPackage(pkgName, doCreateNew, false, saveFunc);
	}
*/	
	/**
	*	Publish the interface open in the editor.
	*	@param interfaceName the name of the interface file
	*	@param pkgName the name of the package the interface belongs to
	*	@param doCreateNew if true will create a new package, if false will
	*		try to save to an existing one.
	*/
	this.publishInterface = function(interfaceName, pkgName, doCreateNew)
	{
		var pkgDir, htmlDir;
		
		var saveFunc = function(pkgInfo)
		{
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
			window.silexApp.model.file.setMeta('parent-id', htmlDir);
			//set rest of file data
			var fileObj = {
				type: 'text/html',
				data: window.silexApp.model.file.getHtml()
			};
			cloudUtils.saveFile(interfaceName,
				false, 
				htmlDir, 
				fileObj, 
				function() {
					silex.controller.ControllerBase.lastSaveUndoIdx = silex.controller.ControllerBase.undoHistory.length - 1;
					silexApp.view.contextMenu.redraw();
					currentFolderid = cloudUtils.getRootFolder();
					pointer.processCTATFolder (currentFolderid, '', true);
			});
			lastPackageId = pkgDir;
			lastPublishedName = interfaceName;
		};
		
		pointer.prepPackage(pkgName, doCreateNew, true, saveFunc);
	};
	
	
	this.downloadZip = function(folderId, folderName)
	{
		console.log('fChooser.downloadZip( '+folderId+' )');
		var valid = validatePkg(folderId);
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
	};
	
	function validatePkg(folderId)
	{
		if (folderId)
		{
			var htmlDirId = cloudUtils.getIdFromName('HTML', folderId);
			if (htmlDirId)
			{
				var assetDir = cloudUtils.getIdFromName('Assets', htmlDirId);
				if (!assetDir)
					return false;
				
				return true;
			}
			return false;
		}
		return false;
	}
	
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
CTATFileChooser.prototype.constructor = CTATFileChooser;;/**
*
*/
var CTATFileControls = function() 
{
	CTATBase.call (this, "CTATFileControls", "fcontrols");
	
	var pointer=this;
	var checkRoot=false;
	var driveObject = null;
	var initialized = false;
	
	/**
	*	Populate the jstree and jqxGrid panes in the dialog window
	*	based on file listing retrieved from drive
	*	@param divID the id of the DOM element the fchooser dialog lives in
	*	@param data an object containing the file data with which to populate the dialog
	*/
	this.displayFileTree=function displayFileTree (divID, data)
	{
		pointer.ctatdebug ("displayFileTree ("+divID+")");
		
		//init jstree object
		driveObject=FileUtils.prepTreeObject ();
		if ((divID==undefined) || (divID==null))
		{
			console.warn("divID is undefined or null");
			divID = "gdrive"; //default to googledrive window
		}
		var fileElement=null; //will store file data for jstree		
		var vizlist=[];		//will store file data for jqxGrid
		for (entry in data) 
		{
			if (data.hasOwnProperty(entry)) 
			{
				var fileObject=data [entry];						
				if (fileObject)
				{
					if (!pointer.checkExclusion(fileObject.title))
					{
						if (fileObject.mimeType.includes("folder"))
						{
							//folders go in jstree
							var fileElement = this.buildJstreeObj(fileObject);
							driveObject.core.data [0].children.push (fileElement);	
						}
						else
						{
							//files go in jqxGrid
							var fileArray= this.buildJqxgridObj(fileObject);							
							vizlist.push(fileArray);
						}
					}
				}					
				//else console.log ("Error obtaining file object!");
			}
		}
		
		ctatdebug ("We've got all the items, displaying ...");

		$('#'+divID).jstree (driveObject);			
		this.fillJqxGrid('#'+divID+"detailstt", vizlist);
		
		initialized = true;
	};
	
	/**
	 *	Updates contents of one particular folder in the fchooser dialog
	 *	(called when a folder is clicked to load that folder's contents)
	 *	@param divId the id of the DOM element where the fchooser dialog lives
	 *	@param dirId the id of the folder that has been selected
	 *	@param newData the contents of the folder
	 */
	this.updateFileTree = function(divId, dirId, newData)
	{
		console.log('updateFileTree( )');
		if (!initialized)
		{
			console.log('not init\'d yet, calling display instead');
			pointer.displayFileTree(divId, newData);
			return;
		}
		if (dirId == cloudUtils.getRootFolder())
			dirId = 'root';
		
		console.log('updating dir w/ id = '+dirId);
		
		var vizlist = []; //will store data for jqxGrid
		if (dirId)
		{
			//check for deleted folders
			var nodeJSON = $('#'+divId).jstree('get_json', dirId);
			var node = eval(nodeJSON);
			for (var i = 0; i < node.children.length; i++)
			{
				if (node.children[i] && !newData[node.children[i].id])
				{
					ctatdebug('deleting node: '+node.children[i].id);
					$('#'+divId).jstree('delete_node', node.children[i]);
				}
			}
			
			//update jstree data
			for (entry in newData)
			{
				if (newData.hasOwnProperty(entry))
				{
					var file = newData[entry];
					var newNode;
					if (!pointer.checkExclusion(file.title))
					{
						if (file.mimeType.includes("folder"))
						{
							//folders go in jstree
							//need to check if already there first
							if (!($('#'+divId).jstree('get_node', file.id)))
							{
								ctatdebug('adding node: '+file.id);
								newNode = this.buildJstreeObj(file);
								$('#'+divId).jstree('create_node', dirId, newNode, 'last');
							}
						}
						else
						{
							//files go in jqxGrid
							newNode = this.buildJqxgridObj(file);
							vizlist.push(newNode);
						}
					}
				}
			}
			//set updated dir to open
			$('#'+divId).jstree('open_node', dirId);
			//redraw jqxGrid
			this.fillJqxGrid('#'+divId+"detailstt", vizlist);
		}
		else
		{
			console.log("ERROR, couldn't find jstree node for dir "+dirId);
		}
	};
	
	/** ---Internal---
	 *	Populates jqxGrid pane with file data
	 *	@param listid the id of the DOM node where the jqxGrid lives
	 *	@param srcData an object representing the contents of the grid
	 */
	this.fillJqxGrid = function(listid, srcData)
	{
		var source =
		{
			localdata: srcData,
			datafields: [
				{ name: 'icon', type: 'string', map: '0'},
				{ name: 'name', type: 'string', map: '1' },
				{ name: 'created', type: 'string', map: '2' },
				{ name: 'modified', type: 'string', map: '3' },
				{ name: 'size', type: 'string', map: '4' }
			],
			datatype: "array"
		};

		var dataAdapter = new $.jqx.dataAdapter(source);
   
		$(listid).jqxGrid(
		{             
			width: '100%',
			height: '100%',
			source: dataAdapter,
			columnsresize: true,
			sortable: true,
			columns: 
			[
				{ text: ' ', datafield: 'icon', width: 24 },
				{ text: 'File Name', datafield: 'name', width: 150 },
				{ text: 'Created', datafield: 'created', width: 100 },
				{ text: 'Modified', datafield: 'modified', width: 100 },
				{ text: 'Size', datafield: 'size', width: 100 }
			]
		});
	};
	
	/** ---Internal---
	 *	Given a drive file object, builds a corresponding jstree node
	 *	@param fileObject the drive file object
	 */
	this.buildJstreeObj = function(fileObject)
	{	
		return {
			text: fileObject.title,
			id: fileObject.id,
			children: [],
			type: 'folder',
			valid_children: 'folder'
		};
	};
	
	/** ---Internal---
	 *	Given a drive file object, builds a corresponding jqxgrid node
	 *	@param fileObject the drive file object
	 */
	this.buildJqxgridObj = function(fileObject)
	{
		var jsDateCreated='--';
		if (fileObject.createdTime)
		{
			jsDateCreated=FileUtils.parseDate(fileObject.createdTime, cloudUtils.getMode());
		}
		
		var jsDateModified='--';
		if (fileObject.modifiedTime)
		{
			jsDateModified=FileUtils.parseDate(fileObject.modifiedTime, cloudUtils.getMode());
		}
		
		var fileArray =	[
			('<img src="'+pointer.getExtensionImage (fileObject.title)+'" />'),							
			fileObject.title,
			jsDateCreated.toString (),
			jsDateModified.toString (),
			(fileObject.fileSize/1000)+"K"
		];
		
		return fileArray;
	};
	
	/** ---Internal---
	*	Given a filename, return the proper icon to display in jstree
	*	@param aFilename the name of the file
	*/
	this.getExtensionImage=function getExtensionImage (aFilename)
	{
		//pointer.ctatdebug ("getExtensionImage ()");
		var ext = FileUtils.getExtension(aFilename);
		var src;
		switch(ext)
		{
			case 'ed.html':
				src = "css/jstree/ed-html.png";
			break;
			case 'html':
				src = "css/jstree/html.png";
			break;
			case 'css':
				src = "css/jstree/css.png"
			break;
			case 'brd':
				src = "css/jstree/brd.gif";
			break;
			case 'jpg':
			case 'png':
			case 'gif':
				src = "css/jstree/image.png";
			break;
			case 'mp3':
				src = "css/jstree/audio.png";
			break;
			default:
				src = 'css/jstree/file.png';
		}
		
		return src;
	};
	
	/** ---Internal---
	*	Determine whether or not a file should be displayed
	*	@param aFilename the name of the file
	*	@returns true if the file should NOT be displayed
	*/
	this.checkExclusion=function checkExclusion (aFilename)
	{
		if (aFilename==null)
		{
			return (true);
		}
		
		if (aFilename==".settings")
		{
			return (true);
		}
		
		return (false);
	};
};

CTATFileControls.prototype = Object.create(CTATBase.prototype);
CTATFileControls.prototype.constructor = CTATFileControls;
;(function($) {
    $.fn.invisible = function() {
        return this.each(function() {
            $(this).css("visibility", "hidden");
        });
    };
    $.fn.visible = function() {
        return this.each(function() {
            $(this).css("visibility", "visible");
        });
    };
}(jQuery));
; /**
 *	@fileoverview a class representing a dialog window used to 
 *	edit the content of a text block.  Inherits from CTATDialogBase
 */

 /**
 *	@constructor
 *	@param windowId the id of the ctatdialog DOM node in which the dialog lives
 */
var CTATTextEdit = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATTextEdit", "textedit", "MODAL", true);
	
	var pointer = this;
	this.fontWeight = 'normal';
	this.fontStyle = 'normal';
	this.selected = null;
	this.fontSize = 16;
	
	this.initEvents = function()
	{
		$('#text-editor-confirm').on('click', function()
		{
			pointer.confirm();
		});
		
		$('#text-editor-cancel').on('click', function()
		{
			pointer.cancel();
		});
		
		$('#text-editor-font-size').on('change', function(event)
		{
			pointer.setFontSize($(event.target).val());
		});
		
		$('#text-edit-fontselect').fontSelector({
			'hide_fallbacks' : true,
			'initial' : 'Courier New,Courier New,Courier,monospace',
			'selected' : function(style) 
				{ 
					pointer.setFontFamily(style); 
				},
			'opened' : function(style) { console.log('font selector opened'); },
			'closed' : function(style) { console.log('font selector closed'); },
			'fonts' : [
				'Arial,Arial,Helvetica,sans-serif',
				'Arial Black,Arial Black,Gadget,sans-serif',
				'Comic Sans MS,Comic Sans MS,cursive',
				'Courier New,Courier New,Courier,monospace',
				'Georgia,Georgia,serif',
				'Impact,Charcoal,sans-serif',
				'Lucida Console,Monaco,monospace',
				'Lucida Sans Unicode,Lucida Grande,sans-serif',
				'Palatino Linotype,Book Antiqua,Palatino,serif',
				'Tahoma,Geneva,sans-serif',
				'Times New Roman,Times,serif',
				'Trebuchet MS,Helvetica,sans-serif',
				'Verdana,Geneva,sans-serif',
				'Gill Sans,Geneva,sans-serif'
			]
		});
		
		$('#text-editor-bold').on('click', pointer.toggleBold);
		
		$('#text-editor-italic').on('click', pointer.toggleItalic);
		
		$('#text-editor-italic > .windowclose').on('click', pointer.close);
	}
	
	/**
	*	Close the window and apply the content to the selected node
	*/
	this.confirm = function()
	{
		var text = $('#text-editor-input').val();
		//apply font size and style
		window.silexApp.model.element.setStyle(pointer.selected, 'fontSize', pointer.fontSize);
		window.silexApp.model.element.setStyle(pointer.selected, 'fontFamily', pointer.fontFamily);
		window.silexApp.model.element.setStyle(pointer.selected, 'fontWeight', pointer.fontWeight);
		window.silexApp.model.element.setStyle(pointer.selected, 'fontStyle', pointer.fontStyle);
		var contentNode = $(pointer.selected).find('.silex-element-content')[0];
		$(contentNode).text(text);
		pointer.close();
	};
	
	this.cancel = function()
	{
		pointer.close();
	};
	
	this.toggleBold = function()
	{
		console.log('toggleBold');
		if (pointer.fontWeight === 'bold')
		{
			pointer.setFontWeight('normal');
		}
		else
		{
			pointer.setFontWeight('bold');
		}
	};
	
	this.toggleItalic = function()
	{
		console.log('toggleItalic');
		if (pointer.fontStyle === 'italic')
		{
			pointer.setFontStyle('normal');
		}
		else
		{
			pointer.setFontStyle('italic');
		}
	};
	
	/**
	 *	@Override CTATDialogBase.show()
	 *	@param selectedElement the currently selected DOM node
	 */
	var super_show = this.show;
	this.show = function(selectedElement)
	{
		super_show();
		//fill dialog inputs in w/ values from selected node
		pointer.selected = selectedElement;
		
		//text content
		var contentNode = $(pointer.selected).find('.silex-element-content')[0];
		$('#text-editor-input').val($(contentNode).text());
		
		//font size
		var sizeStr = window.silexApp.model.element.getStyle(selectedElement, 'font-size') || '16px';
		sizeStr = sizeStr.replace('px', '');
		this.setFontSize(sizeStr);
		$('#text-editor-font-size').val(sizeStr);
		
		//font family
		pointer.fontFamily = window.silexApp.model.element.getStyle(selectedElement, 'font-family') ||
			'Arial, Helvetica, sans-serif';
		console.log('font family = '+pointer.fontFamily);
		$('#text-edit-fontselect').fontSelector('select', pointer.fontFamily);
		
		//bold
		this.setFontWeight(window.silexApp.model.element.getStyle(selectedElement, 'font-weight'));
		
		//italic
		this.setFontStyle(window.silexApp.model.element.getStyle(selectedElement, 'font-style'));
	};
	
	this.setFontSize = function(size)
	{
		var regex = /\d{1,}/;
		if (regex.test(size))
		{
			pointer.fontSize = size+'px';
			$('#text-editor-input').css('font-size', pointer.fontSize);
		}
	}
	
	this.setFontFamily = function(family)
	{
		pointer.fontFamily = family;
		$('#text-editor-input').css('font-family', pointer.fontFamily);
	};
	
	this.setFontWeight = function(weight)
	{
		pointer.fontWeight = (weight === 'normal' || weight === 'bold') ? weight : 'normal';
		$('#text-editor-input').css('font-weight', pointer.fontWeight);
	};
	
	this.setFontStyle = function(style)
	{
		pointer.fontStyle = (style === 'normal' || style === 'italic') ? style : 'normal';
		$('#text-editor-input').css('font-style', pointer.fontStyle);	
	};
	
	this.initEvents();
	
};

;/**
 *	@fileoverview a class which represents a dialog window to be used for multiple choice
 *	question generation.  Inherits from CTATDialogBase
 */

/**
 *	@Constructor
 *	@param windowId the id of the ctatdialog DOM node where this instance will live
 */
var CTATMultChoice = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATMultChoice", "multchoice", "MODAL", true);
	
	var pointer = this;
	this.selected = null;
	this.questionFontSize = 16;
	this.answerFontSize = 16;
	this.numAnswers = 4;
	
	/**
	 *	@Override CTATDialogBase.show()
	 */
	var super_show = this.show;
	this.show = function(selectedElement)
	{
		super_show();
		this.selected = selectedElement;
		if (this.selected)
		{
			//---load old question data in from existing---//
			//dialog box title
			$('#mult-choice-title').text('Edit Question');
			//get question text
			var question = $(selectedElement).find('.mult-choice-question')[0];
			//get answers (and check whether radio buttons or checkboxes)
			var answers = $(selectedElement).find('.CTATRadioButton');
			if (!answers || answers.length == 0)
			{
				answers = $(selectedElement).find('.CTATCheckBox');
				$('#mult-choice-mult-selection').prop('checked', true);
			}
			else
			{				
				$('#mult-choice-mult-selection').prop('checked', false);
			}
			if (answers[0].getAttribute('data-ctat-tutor') == "false")
			{
				$('#mult-choice-grade-immediate').prop('checked', false);
			}
			else
			{
				$('#mult-choice-grade-immediate').prop('checked', true);
			}
			//set question name
			$('#mult-choice-name').val(answers[0].getAttribute('name'));
			//set question text
			$('#mult-choice-question').val(question.textContent);
			//set number of options
			$('#mult-choice-num-options').val(answers.length);
			this.setNumOptions();
			//set option text
			var answerInputs = $('.mult-choice-answer');
			for (var i = 0; i < answerInputs.length; i++)
			{
				if (answers[i])
					$(answerInputs[i]).val(answers[i].getAttribute('data-ctat-label'));
				else
					$(answerInputs[i]).val('');
			}
			//set font sizes
			this.setFontSize(($(question).css('font-size') ? $(question).css('fontSize') : '16'), 'q');
			this.setFontSize(($(answers[0]).css('font-size') ? $(answers[0]).css('fontSize') : '16'), 'a');
			//set font family
			var font = window.silexApp.model.element.getStyle(this.selected, 'fontFamily');
			$('#mult-choice-fontselect').fontSelector('select', [font]);
		}
		else
		{
			$('#mult-choice-title').text('New Question');
			this.clear();
		}
	};
	
	/**
	 *	Reset input values for new question
	 */
	this.clear = function()
	{
		$('#mult-choice-name').val('');
		$('#mult-choice-question').val('');
		$('.mult-choice-answer').val('');
		$('#mult-choice-a-font-size').val('');
		$('#mult-choice-q-font-size').val('');
		$('#mult-choice-num-options').val('4');
		this.setNumOptions();
		$('#mult-choice-mult-selection').prop('checked', false);
		$('#mult-choice-grade-immediate').prop('checked', false);
		$('#mult-choice-include-submit').prop('checked', false);
		this.setFontSize('16', 'both');
		this.setFontFamily($('#mult-choice-fontselect').fontSelector('selected'));
	}
	
	/**
	 *	@Override CTATDialogBase.close()
	 */
	var super_close = this.close;
	this.close = function()
	{
		this.resetErrors('ALL');
		super_close();
	}
	
	/**
	 * Set up listeners on all the input elements / buttons
	 */
	this.initEvents = function()
	{
		$('#mult-choice-confirm').on('click', function()
			{
				pointer.confirm();
			});
		$('#mult-choice-cancel').on('click', function()
			{
				pointer.cancel();
			});
		$('#mult-choice-dialog').find('.windowclose').on('click', function()
			{
				pointer.cancel();
			});
		$('#mult-choice-q-font-size').on('change', function(event)
			{
				pointer.setFontSize($(event.target).val(), 'q');
			});
		$('#mult-choice-a-font-size').on('change', function(event)
			{
				pointer.setFontSize($(event.target).val(), 'a');
			});
		$('#mult-choice-name').on('keypress', function() {pointer.resetErrors('NO_NAME');});
		$('#mult-choice-question').on('keypress', function() {pointer.resetErrors('NO_QUESTION');});
		$('.mult-choice-answer').on('keypress', function() {pointer.resetErrors('NO_ANSWER');});
		$('#mult-choice-num-options').on('change', pointer.setNumOptions);
		$('#mult-choice-grade-immediate').on('change', pointer.setGradeImmediate);
		$('#mult-choice-fontselect').fontSelector({
			'hide_fallbacks' : true,
			'initial' : 'Courier New,Courier New,Courier,monospace',
			'selected' : function(style) 
				{ 
					pointer.setFontFamily(style); 
				},
			'opened' : function(style) {  },
			'closed' : function(style) {  },
			'fonts' : [
				'Arial,Arial,Helvetica,sans-serif',
				'Arial Black,Arial Black,Gadget,sans-serif',
				'Comic Sans MS,Comic Sans MS,cursive',
				'Courier New,Courier New,Courier,monospace',
				'Georgia,Georgia,serif',
				'Impact,Charcoal,sans-serif',
				'Lucida Console,Monaco,monospace',
				'Lucida Sans Unicode,Lucida Grande,sans-serif',
				'Palatino Linotype,Book Antiqua,Palatino,serif',
				'Tahoma,Geneva,sans-serif',
				'Times New Roman,Times,serif',
				'Trebuchet MS,Helvetica,sans-serif',
				'Verdana,Geneva,sans-serif',
				'Gill Sans,Geneva,sans-serif'
			]
		});
	};
	
	/**
	 *	Close the window and use the input values to generate a multiple choice question
	 */
	this.confirm = function()
	{
		//extract input data
		var questionName = $('#mult-choice-name').val();
		var questionText = $('#mult-choice-question').val();
		var answerTextArr = [];
		$('.mult-choice-answer').each(function()
			{
				if ($(this).val())
					answerTextArr.push($(this).val());
			});
		var allowMultAnswer = $('#mult-choice-mult-selection').prop('checked');
		var gradeOnInput = $('#mult-choice-grade-immediate').prop('checked');
		var includeSubmit = $('#mult-choice-include-submit').prop('checked');
		//input validation
		if (!questionName)
		{
			this.displayErrMsg('NO_NAME');
			return;
		}
		if (!questionText)
		{
			this.displayErrMsg('NO_QUESTION');
			return;
		}
		if (answerTextArr.length === 0)
		{	
			this.displayErrMsg('NO_ANSWER');
			return; 
		}
		if (!this.validateName(questionName))
		{
			this.displayErrMsg('BAD_ID');
			return; 
		}
			
		var questionInfo = {
			'wrapper': this.selected,
			'name': questionName,
			'question': questionText,
			'answers': answerTextArr,
			'fontFamily': this.fontFamily,
			'qFontSize': this.questionFontSize,
			'aFontSize': this.answerFontSize,
			'includeSubmit': includeSubmit,
			'allowMultAnswer': allowMultAnswer,
			'gradeOnInput':gradeOnInput
		};
		//set undo checkpoint
		window.silexApp.controller.editMenuController.undoCheckPoint();
		//pass data to silex to build the actual DOM node
		window.silexApp.model.element.createElement('question.multchoice', questionInfo);
		this.close();
	};
	
	this.cancel = function()
	{
		this.close();
	};
	
	this.setFontFamily = function(family)
	{
		pointer.fontFamily = family;
		pointer.applyFontFamily();
	};
	
	this.applyFontFamily = function()
	{
		$('#mult-choice-question').css('font-family', pointer.fontFamily);
		$('.mult-choice-answer').css('font-family', pointer.fontFamily);
	};
	
	this.setFontSize = function(size, qOrA)
	{
		var regex = /\d{1,}(px)?/;
		if (regex.test(size))
		{
			if (!size.includes('px'))
				size+='px';
			switch(qOrA)
			{
				case 'a':
					this.answerFontSize = size;					
					$('#mult-choice-a-font-size').val(size.substring(0, size.length-2));
				break;
				case 'both':
					this.answerFontSize = size;
					$('#mult-choice-a-font-size').val(size.substring(0, size.length-2));
				case 'q':
					this.questionFontSize = size;
					$('#mult-choice-q-font-size').val(size.substring(0, size.length-2));
			}
			pointer.applyFontSize(qOrA);
		}
	};
	
	this.applyFontSize = function(qOrA)
	{
		switch(qOrA)
		{
			case 'a':					
				$('.mult-choice-answer').css('font-size', this.answerFontSize);
			break;
			case 'both':
				$('.mult-choice-answer').css('font-size', this.answerFontSize);
			case 'q':
				$('#mult-choice-question').css('font-size', this.questionFontSize);
		}
	}
	
	this.setNumOptions = function(numOptions)
	{
		if (!numOptions || typeof(numOptions === 'Object'))
			numOptions = document.getElementById('mult-choice-num-options').value;
		var optionList = document.getElementById('mult-choice-answer-list');
		var optionsAdded = false;
		if (!isNaN(numOptions) && parseInt(numOptions) >= 1)
		{ 
			numOptions = parseInt(numOptions)
			if (pointer.numAnswers < numOptions)
			{
				optionsAdded = true;
			}
			while (pointer.numAnswers != numOptions)
			{
				if (pointer.numAnswers < numOptions)
				{
					//add option
					var li = document.createElement('li');
					var textarea = document.createElement('textarea');
					textarea.classList.add('mult-choice-answer');
					textarea.setAttribute('placeholder', 'option ' + (++pointer.numAnswers));
					textarea.setAttribute('rows', '1');
					li.appendChild(textarea);
					optionList.appendChild(li);
				}
				else
				{
					//remove option
					optionList.removeChild($(optionList).find('li').last()[0]);
					pointer.numAnswers--;
				}
			}
			if (optionsAdded)
			{
				pointer.applyFontFamily();
				pointer.applyFontSize('both');
			}
		}
		else
			console.warn('numOptions not valid int');
	};
	
	/**
	 *	Show an error message if user tries to confirm w/ fields missing
	 *	@param errCode a string denoting which field is missing
	 */
	this.displayErrMsg = function(errCode)
	{
		switch(errCode)
		{
			case 'NO_NAME':
				$('#mult-choice-name').val('Please provide a unique ID');
				$('#mult-choice-name').attr('error', 'true');
			break;
			case 'NO_QUESTION':
				$('#mult-choice-question').val('Please fill in this field');
				$('#mult-choice-question').attr('error', 'true');
			break;
			case 'NO_ANSWER':
				$('.mult-choice-answer').attr('error', 'true');
			break;
			case 'BAD_ID':
				alert('Ids must be unique, cannot be "done" or "hint", and cannot contain spaces');
		}
	};
	
	/**
	 *	Unset the 'error' attribute on one or all of the dialog's input fields
	 *	(When the 'error' attribute is set fields turn red)
	 *	@param errCode which input should be set, or "ALL" to reset all of them
	 */
	this.resetErrors = function(errCode)
	{
		switch(errCode)
		{
			case 'NO_ANSWER':
				$('.mult-choice-answer').attr('error', 'false');
			break;
			case 'NO_QUESTION':
				$('#mult-choice-question').attr('error', 'false');
			break;
			case 'NO_NAME':
				$('#mult-choice-name').attr('error', 'false');
			break;
			case 'ALL':
				$('#mult-choice-name').attr('error', 'false');
				$('#mult-choice-question').attr('error', 'false');
				$('.mult-choice-answer').attr('error', 'false');
		}
	};
	
	/**
	 *	Checks provided name against working document to make
	 *		sure it's unique
	 *	@param name the name
	 */
	this.validateName = function(name)
	{
		if (name.includes(' '))
			return false;
		var stage = silexApp.model.file.getContentDocument();
		var el = stage.getElementById(name);
		if (el && el != pointer.selected)
			return false;
		
		return true;
	};
	
	this.setGradeImmediate = function()
	{
		var gradeImmediate = $('#mult-choice-grade-immediate').prop('checked');
		if (gradeImmediate)
		{
			$('#mult-choice-include-submit').prop('checked', false);
			$('#mult-choice-include-submit').prop('disabled', true);
		}
		else
		{
			$('#mult-choice-include-submit').prop('disabled', false);
		}
	};
	
	this.initEvents();
};

; /**
 *	@fileoverview a class representing a dialog window used to 
 *	choose the source of an image linked into the working doc.
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
			switch(mode)
			{
				case 'image':
					window.silexApp.controller.propertyToolController.pickFile('IMG');
				break;
				case 'audio':
					window.silexApp.controller.propertyToolController.pickFile('AUDIO');
				break;
				default:
					console.warn('invalid mode set in imagesource.js');
				break;
			}
			pointer.close();
		});
	};
	
	/**
	*	Close the window and apply the content to the selected node
	*/
	this.confirm = function()
	{
		var url = $('#img-src-url').val();
		switch(mode)
		{
			case 'image':
				window.silexApp.controller.propertyToolController.setImgUrl(url);
			break;
			case 'audio':
				window.silexApp.controller.propertyToolController.setAudioUrl(url);
			break;
		}
		
		this.close();
	};
	
	this.cancel = function()
	{
		this.close();
	};
	
	super_show = this.show;
	this.show = function()
	{
		$('#img-src-url').val('');
		super_show();
	}
	
	this.setMode = function(m)
	{
		mode = m;
		if (mode === 'image')
		{
			pointer.setTitle('Choose Image');
		}
		else if (mode === 'audio')
		{
			pointer.setTitle('Choose Audio');
		}
	}
	
	this.init();
};

;
var CTATCreatePackage = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATCreatePackage", "createpackage", "MODAL", true);
	
	var pointer = this;
	var saveCbk = function(){console.log('default save callback')};
	var mode = null;
	
	this.setSaveCallback = function(callback)
	{
		saveCbk = callback;
	};
	
	this.initEvents = function()
	{
		$('#create-package-confirm').on('click', pointer.confirm);
		$('#create-package-cancel').on('click', pointer.cancel);
		$('#create-package-savetoexisting').on('click', pointer.saveToExisting);
		$(windowId + '>'+'.windowclose').on('click', pointer.cancel);
	};
	
	this.saveToExisting = function()
	{
		if (mode === 'publish')
		{
			window.ctatFileChooser.show('PUBLISH');
		}
		else if (mode === 'save')
		{
			window.ctatFileChooser.show('SAVE_SILEX');
		}
	}
	
	this.confirm = function()
	{
		var pkgName = $('#create-package-pkgname').val();
		var interfaceName = $('#create-package-interfacename').val();
		var root = cloudUtils.getRootFolder();
		
		var doIt = function(nameToUse)
		{		
			var goAhead = true;
			if (nameToUse !== pkgName)
			{
				goAhead = confirm("A package already exists with the name you provided.  Your new package will be named "+nameToUse);
			}
			if (goAhead)
			{
				if (mode === 'publish')
				{
					window.ctatFileChooser.publishInterface(interfaceName, nameToUse, true);					
				}
				else if (mode === 'save')
				{
					window.ctatFileChooser.publishInterface(interfaceName, nameToUse, true);
				}
				pointer.close();
			}
		}
		
		FileUtils.assertName(pkgName, root, doIt);
	};
	
	this.cancel = function()
	{
		pointer.close();
	};
	
	var super_show = this.show;
	this.show = function(thisMode)
	{
		mode = thisMode;
		$('#create-package-pkgname').val('');
		$('#create-package-interfacename').val('');
		var lastPkg = window.ctatFileChooser.getLastPackage();
		if (lastPkg && !cloudUtils.beenCached(lastPkg))
		{
			cloudUtils.cacheFolder(lastPkg, false, null);			
		}
		super_show();
	}
};
	;/**
 *	@fileoverview a class which represents a dialog window to be used for creating
 *	component groups in the HTML editor.  Inherits from CTATDialogBase
 */

/**
 *	@Constructor
 *	@param windowId the id of the ctatdialog DOM node where this instance will live
 */
var CTATGroupDialog = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATGroupDialog", "groupdialog", "", true);
	
	var pointer = this;
	var compList = document.getElementById('group-dialog-member-list');
	var ids = [];
	var confirmBtn = document.getElementById('group-dialog-confirm');
	var deleteBtn = document.getElementById('group-dialog-delete');
	var cancelBtn = document.getElementById('group-dialog-cancel');
	var addBtn = document.getElementById('group-dialog-add-member');
	var idField = document.getElementById('group-dialog-id-field');
	var nameField = document.getElementById('group-dialog-name');
	var mode = null;
	var oldName = null;
	
/////// ---- PUBLIC METHODS ---- ///////
	
	/**
	 *	@Override CTATDialogBase.show
	 */
	var super_show = this.show;
	this.show = function(m, selectedElements, groupName)
	{
		super_show();
		
		clear();
		mode = m;
		this.selected = selectedElements;
		if (mode === 'create')
		{	
			$('#group-dialog-title').text('New Group');
			//create li for each of selectedElements that is a CTAT component
			for (var i = 0; i < selectedElements.length; i++)
			{
				if (selectedElements[i].className.includes('CTAT'))
					addToList(selectedElements[i].getAttribute('id'));
			}
			oldName = null;
			deleteBtn.setAttribute('disabled','true');
		}
		else
		{
			$('#group-dialog-title').text('Edit Group');
			nameField.value = groupName;
			for (var i = 0; i < selectedElements.length; i++)
			{
				addToList(selectedElements[i]);
			}
			oldName = groupName;
			deleteBtn.removeAttribute('disabled');
		}
	};
	
	
	/**
	 *	@Override CTATDialogBase.close()
	 */
	var super_close = this.close;
	this.close = function()
	{
		super_close();
	}
	
	/**
	 *	Close the window and use the input values to generate a ctatgroupingcomponent
	 */
	this.confirm = function()
	{
		var groupName = nameField.value;
		if (groupName) 
		{
			if (validateName(groupName) || mode === 'edit')
			{
				if (ids.length > 0)
				{
					var badIds = [];
					//validate ids
					for (var i = 0; i < ids.length; i++)
					{
						if (!silexApp.model.file.getContentDocument().getElementById(ids[i]))
						{
							badIds.push(ids[i]);
						}
					}
					if (badIds.length > 0)
					{
						alert('The following components do not exist: '+badIds.join());
					}
					else //if we get here, group will be created
					{
						var componentListStr = ids.join();
						console.log('comp list string: '+componentListStr);
						if (oldName)
						{
							editGroup(groupName, componentListStr);
						}
						else
						{
							createGroup(groupName, componentListStr);	
						}
						pointer.close();
					}
				}
			}
			else
			{
				displayErrMsg('BAD_NAME');
			}
		}
		else
		{
			displayErrMsg('NO_NAME');
		}
	};
	
	
	this.cancel = function()
	{
		pointer.close();
	};

/////// ---- PRIVATE METHODS ---- ///////
	
	/**
	 *	Reset input values for new question
	 */
	function clear()
	{
		nameField.value = '';
		idField.value = '';
		while (compList.firstChild)
		{
			compList.removeChild(compList.firstChild);
		}
		ids = [];
	};
	
	/**
	 *	Show an error message if user tries to confirm w/ fields missing
	 *	@param errCode a string denoting which field is missing
	 */
	function displayErrMsg(errCode)
	{
		switch(errCode)
		{
			case 'ID_IN_GROUP':
				idField.value = 'A component with that ID is already in the group';
				idField.setAttribute('error', 'true');
			break;
			case 'NO_NAME':
				nameField.value = 'Enter an ID for the group';
				nameField.setAttribute('error', 'true');
			break;
			case 'BAD_NAME':
				nameField.value = 'IDs must be unique, and cannot contain spaces';
				nameField.setAttribute('error', 'true');
			break;
		}
	};
	
	/**
	 *	Unset the 'error' attribute on one or all of the dialog's input fields
	 *	(When the 'error' attribute is set fields turn red)
	 *	@param errCode which input should be set, or "ALL" to reset all of them
	 */
	function resetErrors(errCode)
	{
		console.log('resetErrs');
		switch(errCode)
		{
			case 'ID_IN_GROUP':
				idField.removeAttribute('error');
			break;
			case 'NO_NAME':
			case 'BAD_NAME':
				nameField.removeAttribute('error');
		}
	};
	
	function validateId(id)
	{
		//check if already in group
		return !(ids.includes(id));
	};
	
	/**
	 *	Checks provided name against working document to make
	 *		sure it's unique
	 *	@param name the name
	 */
	function validateName(name)
	{
		if (name.includes(' '))
			return false;
		var stage = silexApp.model.file.getContentDocument();
		var el = stage.getElementById(name);
		if (el)
			return false;
		
		return true;
	};
	
	function addToList(id)
	{
		var li = document.createElement('li');
		li.setAttribute('id', 'group-member-'+id);
		var span = document.createElement('span');
		var removeBtn = document.createElement('button');
		li.appendChild(span);
		span.appendChild(document.createTextNode(id));
		removeBtn.appendChild(document.createTextNode('-'));
		span.appendChild(removeBtn);
		
		removeBtn.addEventListener('click', function(e)
			{
				compList.removeChild(li);
				ids.splice(ids.indexOf(id), 1);
			});
			
		compList.appendChild(li);
		ids.push(id);
	};
	
	function editGroup(newName, componentList)
	{
		var stageDoc = silexApp.model.file.getContentDocument();
		var oldGroup = stageDoc.getElementById(oldName);
		oldGroup.setAttribute('data-ctat-componentlist', componentList);
		if (oldName !== newName)
		{
			//rename groupingcomponent
			oldGroup.setAttribute('id', newName);
			//rename menu option
			silexApp.view.menu.setGroupName(oldName, newName);
		}
	}
	
	function createGroup(groupName, componentList)
	{
		//create ctatgroupingcomponent
		window.silexApp.model.element.createElement('group', 
		{'id': groupName,
		 'componentList': componentList}
		);
		//add group menu option
		silexApp.view.menu.addGroup(groupName);
	}
	
	function deleteGroup()
	{
		if (oldName)
		{
			var stageDoc = silexApp.model.file.getContentDocument();
			var oldGroup = stageDoc.getElementById(oldName);
			oldGroup.parentElement.removeChild(oldGroup);
			silexApp.view.menu.removeGroup(oldName);
			pointer.close()
		}
		else
		{
			console.warn('no group selected');
		}
	}
	
	/**
	 * Set up listeners on all the input elements / buttons
	 */
	function initEvents()
	{
		//close icon
		$(windowId+' > .windowclose').on('click', pointer.close);
		//confirm
		confirmBtn.addEventListener('click', pointer.confirm);
		//delete group
		deleteBtn.addEventListener('click', deleteGroup);
		//cancel
		cancelBtn.addEventListener('click', pointer.close);
		//add component
		addBtn.addEventListener('click', function()
			{
				var toAdd = idField.value;
				if (toAdd && validateId(toAdd))
				{
					addToList(toAdd);
					idField.value = '';
				}
				else if (toAdd)
				{
					displayErrMsg('ID_IN_GROUP');
				}
			});
		
		//idField 
		idField.addEventListener('input', function()
			{
				resetErrors('ID_IN_GROUP');
			});
		
		//nameField
		nameField.addEventListener('input', function()
			{
				resetErrors('NO_NAME');
			});
	};
	
	initEvents();
};

;/**
 *
 */
 
//goog.require('CTATBase');

/**
*
*/
var CTATAccountManager = function() 
{		
	CTATDialogBase.call (this, "#accountwindow", "CTATAccountManager", "account","MODAL");
	
	var pointer=this;
	var msgIsThere = false;
	this.init=function init()
	{
		pointer.ctatdebug ("init ()");
		
		$("#accountselect").on("click",function ()
		{			
			var provider = document.querySelector('input[name = "accountprovider"]:checked').value
			
			pointer.close ();
			
			cloudUtils.initDrive(provider);		
		});		
	};
	
	/**
	*
	*/
	this.showChooser=function showChooser ()
	{
		pointer.ctatdebug ("showChooser ()");

		//pointer.processCTATFolder (currentFolderid);
		
		//refreshTimer=window.setInterval (pointer.fileDialogReload,60000);
	};
	
	pointer.init ();
};

CTATAccountManager.prototype = Object.create(CTATDialogBase.prototype);
CTATAccountManager.prototype.constructor = CTATAccountManager;
;/**-----------------------------------------------------------------------------
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
	this.listFolders = function(id, cbk)
	{
		ctatdebug('cloudUtils.listFolders()');
		if (folderCache[id])
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
		var result = folderCache[anID];
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
	**/
	this.openFileById = function openFileById (fileId, callback)
	{
		console.log('openFileById ( '+fileId+' )');
		drive.downloadFileById(fileId, callback);
	};
	
	/**
	*	Retrieve a file's contents as a blob given the filename
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
	*/
	this.openBlobById = function(id, callback)
	{
		drive.downloadBlobById(id, callback);
	}
	
	/**
	*	Save file to google drive. Checks whether file already exists 
	*	and calls either file_save or file_saveas accordingly
	*	@param filename the name of the file
	*	@param toPublish if true the file should be cleaned up first
	*	@param folderId the id of the file's parent folder
	*	@param fileObject object storing the file data and mimeType
	*	@param cbk function to call on success
	*/
	this.saveFile = function saveFile(filename, toPublish, folderId, fileObject, cbk)
	{
		pointer.ctatdebug("saveFile("+filename+")");
		
		if (!folderId) folderId = rootFolder;
		var counter = 0;
		var fileType = fileObject.type;
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
			window.silexApp.view.stage.setFilename(filename);
			if (cbk)
				cbk();
		};
		//performs the actual save operations
		var saveCallback = function(name, data, parentId)
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
											fileType);
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
		saveCallback(edFilename, fileText, folderId);
		//save regular version (.html)
		silex.utils.Dom.getCleanFile(fileText, function(fileData)
			{
				//save html
				saveCallback(pubFilename, fileData['htmlString'], folderId);
				//save css
				fileType = 'text/css';
				pointer.getIdFromName('Assets', folderId, function(assetFolderId)
					{
						saveCallback(styleFileName, fileData['cssString'], assetFolderId);
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
		pointer.ctatdebug("saveFileAs");
		
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
	
		targetFolderID = targetFolderID || workspaceFolder;
		var id = null;
		//check cache first
		if (folderCache[targetFolderID])
		{
			var fileData = pointer.getFolderNamedCached (targetFolderID,filename);
			if (fileData && fileData.id)
			{
				id = fileData.id
			}
			else
			{
				console.warn('File id not found, returning null');
			}			

			if (optCallback) 
				optCallback(id);
			else 
				return id;
		}
		else
		{
			console.log(targetFolderID + ' is not in cache...');
			if (optCallback)
			{
				drive.retrieveFilesByName(filename, targetFolderID, function(result)
					{
						if (result.length > 0)
						{
							optCallback(result[0].id);
						}
						else
						{
							optCallback(null);
						}
					});
			}
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
				for (var i = 0; i < response.length; i++) //for each folder in folderId
				{
					console.log('caching folder '+response[i].id);
					pointer.listFilesByID(null, response[i].id, true, function()
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
						}); //cache that folder
				};
			}

			pointer.listFolders(folderId, cbk);
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
			console.warn('cacheFile( ): parent folder w/ id '+parentId+' not in cache!');
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

		return (null);
	};		
	
	this.getFolderIdCached = function(folderId, fileId)
	{
		return folderCache[folderId][fileId];
	};
	
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
	
	/**
	*	Service-specific initialization functions called on log in
	*/
	var processInit = {
		
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
						pointer.listFiles (null, function (data)
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
				folderCache [aFolderID]=files;	
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
;/**-----------------------------------------------------------------------------
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
*/

/**
*
*/
function typeOf(obj) 
{
  var type = typeof obj;
  return type === "object" && !obj ? "null" : type;
}

/**
*
*/
function exists (obj, name, type) 
{
  type = type || "function";
  return (obj ? this.typeOf(obj[name]) : "null") === type;
}

/**
 Introspects an object.

 @param name the object name.
 @param obj the object to introspect.
 @param indent the indentation (optional, defaults to "").
 @param levels the introspection nesting level (defaults to 1).
 @returns a plain text analysis of the object.
*/
function introspect(name, obj, indent, levels) 
{
	//console.log ("introspect ()");

	indent = indent || "";
  
	if (typeOf(levels) !== "number") 
	{
		levels = 1;
	}	
  
	var objType = typeOf(obj);
  
	var result = [indent, name, " ", objType, " :"].join('');
  
	if (objType === "object") 
	{
		if (levels > 0) 
		{
			indent = [indent, "  "].join('');
			
			var prop = null;
			
			for (prop in obj) 
			{
				if (obj.hasOwnProperty(prop)) 
				{
					var propString = introspect(prop, obj[prop], indent, levels - 1);
					result = [result, "\n", propString].join('');
				}	
			}
			
			return result;
		}
		else 
		{
			return [result, " ..."].join('');
		}
	}
	else if (objType === "null") 
	{
		return [result, " null"].join('');
	}
  
	return [result, " ", obj].join('');
}

;/**	@fileoverview this class exposes the dropbox file api to CloudUtils; all calls
*	should be made through the cloudUtils global object rather than calling on this
*	class directly.
*
*	File operations performed by this class are restricted the folder Dropbox/apps/CTAT 
**/

(function(window){
  window.utils = {
    parseQueryString: function(str) 
	{
      var ret = Object.create(null);

      if (typeof str !== 'string') {
        return ret;
      }

      str = str.trim().replace(/^(\?|#|&)/, '');

      if (!str) {
        return ret;
      }

      str.split('&').forEach(function (param) 
	  {
        var parts = param.replace(/\+/g, ' ').split('=');
        // Firefox (pre 40) decodes `%3D` to `=`
        // https://github.com/sindresorhus/query-string/pull/37
        var key = parts.shift();
        var val = parts.length > 0 ? parts.join('=') : undefined;

        key = decodeURIComponent(key);

        // missing `=` should be `null`:
        // http://w3.org/TR/2012/WD-url-20120524/#collect-url-parameters
        val = val === undefined ? null : decodeURIComponent(val);

        if (ret[key] === undefined) 
		{
          ret[key] = val;
        } 
		else if (Array.isArray(ret[key])) 
		{
          ret[key].push(val);
        } 
		else 
		{
          ret[key] = [ret[key], val];
        }
      });

      return ret;
    }
  };
})(window);

function DropBox()
{
	var pointer = this;
	var appKey = '84p1opowc65doa1';
	var dbAuthDone = function() { console.log('default db auth handler...'); };
	var authPoll;
	
	this.setAuthCompleteHandler = function(handler)
	{
		dbAuthDone = handler;
	};
	
	/**
	*	Request access permission (OAuth) to Dropbox 
	*/
	this.authorize = function(cbk)
	{
		var origin;
		if (window.location.protocol === 'https:')
			origin = window.location.href.substring(8, window.location.href.length);
		else
			origin = window.location.href.substring(7, window.location.href.length);
		var tokens = origin.split('/');
		if (tokens[tokens.length-1].includes('.'))
			tokens.pop();
		origin = tokens.join('/');
		
		//start polling for set cookie
		authPoll = window.setInterval(function()
			{
				if (document.cookie.includes('__dbauthcomplete=true'))
				{
					var match = /__dbauthtoken=([^;]*)(;|$)/.exec(document.cookie);
					if (match && match[1] !== 'null')
					{
						localStorage['__dbat'] = match[1];
						document.cookie = '__dbauthcomplete=false';
						document.cookie = '__dbauthtoken=null';
						window.clearInterval(authPoll);
						dbAuthDone();
					}
				}
			}, 50);
			
		dropbox.authenticate({'client_id': appKey, 'redirect_uri':'https://'+origin+'/db-auth-handler.html'}, cbk);
	};
	
	this.reauthorize = function()
	{
		
	};
	
	this.disconnect = function(cbk)
	{
		console.log('dropbox.disconnect()');
		cbk();
	};
	
	this.updateFile = function(fileId, fileMetadata, fileData, callback)
	{
		console.log('dropbox.updateFile('+fileId+')');
		if (!FileUtils.hasExtension(fileId))
			fileId += FileUtils.mimeTypeToExtension(fileMetadata.mimeType);
		
		dropbox('files/upload', {'path': fileId, 'mode': 'overwrite'}, fileData, function(response)
			{
				var result = FileUtils.convertFileFormat(response, 'dropbox');
				callback(result);
			});
	};

	this.saveFile=function saveFile(fileName,fileText,fileType,parent,callback) 
	{
		pointer.insertFile (fileName,fileText,fileType,parent,callback);
	};
	
	this.insertFile = function(fileName,fileText,fileType,parent,callback)
	{
		console.log('dropbox.insertFile()');
		var path = (parent.charAt(parent.length - 1) === '/') ? parent : parent+'/';
		path += fileName;
		if (!FileUtils.hasExtension(fileName))
			path += FileUtils.mimeTypeToExtension(fileType);
		
		dropbox('files/upload', {'path': path, 'mode': 'add', 'autorename':true}, fileText, function(response)
			{
				var result = FileUtils.convertFileFormat(response, 'dropbox');
				callback(result);
			});
	};
	
	this.trashFile = function(id, cbk)
	{
		console.log('dropbox.trashFile()');
		dropbox('files/delete', {'path': id}, function(resp)
			{
				if (resp.error)
				{
					console.warn('There was an error deleting the file');
				}
				else cbk();
			});
	};
	
	this.copyFile = function(fromId, toId, optCbk)
	{
		var tokens = fromId.split('/');
		var filename = tokens[tokens.length-1];
		toId += '/'+filename;
		var reqObj = {'from_path': fromId, 'to_path': toId};
		dropbox('files/copy', reqObj, function(resp)
			{
				if (resp.error)
				{
					alert('error copying files: '+JSON.stringify(resp.error));
				}
				if (optCbk)
				{
					var result = FileUtils.convertFileFormat(resp, 'dropbox');
					optCbk(result);
				}
			});
	};
	
	this.moveFile = function(fileId, fromId, toId, optCbk)
	{
		var tokens = fileId.split('/');
		var filename = tokens[tokens.length-1];
		toId += '/'+filename;
		var reqObj = {'from_path': fileId, 'to_path': toId, 'autorename': true};
		dropbox('files/move', reqObj, function(resp)
			{
				if (resp.error)
				{
					alert('error moving file: '+JSON.stringify(resp.error));
				}
				if (optCbk)
				{
					optCbk(toId);
				}
			});
	};
	
	this.renameFile = function(fileId, newName, cbk)
	{
		var newFileId;
		var path = fileId.split('/');
		path.pop();
		path = path.join('/');
		newFileId = path+'/'+newName;
		var reqObj = {'from_path': fileId, 'to_path': newFileId};
		dropbox('files/move', reqObj, function(resp)
			{
				if (resp.error)
				{
					alert('error naming file: '+JSON.stringify(resp.error));
				}
				if (cbk)
				{
					cbk(newFileId);
				}
			});
	};
	
	this.insertFolder = function(folderName, aParent, callback)
	{
		console.log('dropbox.insertFolder()');
		var path = (aParent.charAt(aParent.length - 1) === '/') ? aParent : aParent+'/';
		path += folderName;
		var cbk = function(result)
		{
			console.log('dropbox.insertFolder result = '+JSON.stringify(result));
			result = FileUtils.convertFileFormat(result, 'dropbox');
			result.mimeType = 'folder';
			console.log('result after format conversion: '+JSON.stringify(result));
			callback(result);
		}
		dropbox('files/create_folder', {'path': path}, cbk);
	};
	
	this.retrieveFilesByName = function	(fileName,parent,callback)
	{
		console.log('dropbox.retrieveFilesByName()');
		var filter = function(result)
		{
			var file;
			var matches = [];
			for (var i = 0; i < result.length; i++)
			{
				if (result[i]['title'] == fileName)
				{
					matches.push(result[i]);
				}
			}
			console.log('dropbox.retrieveFilesByName returning '+JSON.stringify(matches));
			callback(matches);
		};
		
		pointer.retrieveFilesByFolder(parent, filter);
	};
	
	this.retrieveFilesByFolder = function(path, cbk)
	{
		console.log('dropbox.retrieveFilesByFolder( '+path+' )');
		var data = {entries: []};
		var callback = function(result)
		{
			data.entries = data.entries.concat(result['entries']);
			if (result['has_more'])
			{
				dropbox('files/list_folder/continue', {'cursor':result['cursor'] }, callback)
			}
			else
			{
				var formatted = convertFileFormat(data.entries);
				cbk(formatted);
			}
		};
		
		dropbox('files/list_folder', {'path': path}, callback);
	};
	
	this.retrieveFolders = function (path, cbk)
	{
		console.log('dropbox.retrieveFolders()');
		var filter = function(result)
		{
			var formatted = [];
			for (var i = 0; i < result.length; i++)
			{
				if (result[i]['mimeType'].includes('folder'))
				{
					formatted.push(result[i]);
				}
			}
			cbk(formatted);
		};
		
		pointer.retrieveFilesByFolder(path, filter);
	};
	
	this.downloadFileByName = function downloadFileByName(fileName,parent,callback)
	{
		ctatdebug("dropbox.downloadFileByName("+fileName+","+parent+")");
	
		if(!callback) callback=function(file)
		{
			ctatdebug(file);
		}
    
		var pointer=this;
		
		pointer.retrieveFilesByName(fileName, parent, function(files)
		{
			if(!files[0])
			{
				callback(null);
			}
			else
			{
				tempFileID=files[0].id;
				pointer.downloadFileById(files[0].id,callback);
			}
		});
	}
	
	this.downloadFileById=function downloadFileById(fileId, callback) 
	{
		console.log('dropbox.downloadFileById()');
		if (!fileId)
		{
			console.error('no file ID provided');
		}
		
		readBlob = function(response)
		{
			var args = [].slice.call(arguments);
			console.log('args.length = '+args.length);
			for (var i = 0; i < args.length; i++)
			{
				console.log('arg '+i+': '+args[i]);
			}
			var fReader = new FileReader();
			fReader.onload = function()
				{
					callback(fReader.result);
				};
			fReader.readAsText(args[1]);	
		};
		
		dropbox('files/download', {'path': fileId}, readBlob);
	};
	
	this.downloadBlobById = function (fileId, callback)
	{
		console.log('dropbox.downloadBlobById()');
		if (!fileId)
		{
			console.error('no fileID');
		}
		dropbox('files/download', {'path': fileId, responseType: 'arraybuffer'}, function(parsedResp, rawResp, request)
			{
				callback(rawResp);
			});
	};
	
	
	this.getParents = function(fileId, cbk)
	{
		var dirs = fileId.split('/');
		dirs.pop();
		dirs = dirs.join('/');
		cbk([dirs]);
	};
	
	function convertFileFormat(files)
	{
		var formatted = [];
		for (var i = 0; i < files.length; i++)
		{
			formatted.push(FileUtils.convertFileFormat(files[i], 'dropbox'));
		}
		return formatted;
	}
};/**-----------------------------------------------------------------------------
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

*/

/**
*
*/
var CTATWindowManager = function(anID,aClass,anInstance,aMode,aTitle) 
{
	CTATBase.call (this, "CTATWindowManager", "wmanager");

	var activeWindow="";
	var windowStack=[];
	var pointer=this;
	
	/**
	*
	*/
	this.updateBlocker = function updateBlocker (aMessage)
	{
		//pointer.ctatdebug ("updateBlocker ()");
		
		document.getElementById ("infocontent").innerHTML+=aMessage;
	};

	/**
	*
	*/
	this.centerWindow = function centerWindow(anID) 
	{
		//pointer.ctatdebug ("centerWindow ("+anID+")");
		
		$(anID).css('left', jQuery(window).width()/2 - jQuery(anID).width()/2);
		$(anID).css('top', jQuery(window).height()/2 - jQuery(anID).height()/2);
	};

	/**
	*
	*/
	this.findWindow = function findWindow (anID)
	{
		//pointer.ctatdebug ("findWindow ("+anID+")");

		for (var i=0;i<windowStack.length;i++)
		{
			var windowObject=windowStack [i];
			
			if (windowObject.getWindowID ()==anID)
			{
				//pointer.ctatdebug ("Found existing window object, returning ...");
				return (windowObject);
			}
		}
		
		return (null);
	};

	/**
	*
	*/
	this.removeWindow = function removeWindow (anID)
	{
		//pointer.ctatdebug ("removeWindow ("+anID+")");
		
		for (var i=0;i<windowStack.length;i++)
		{
			var windowObject=windowStack [i];
			//pointer.ctatdebug('checking window '+windowObject.getWindowID());
			
			if (windowObject.getWindowID ()==anID)
			{
				//pointer.ctatdebug ("Found existing window object, removing from stack ...");

				windowStack.splice (i,1);
				
				return;
			}
		}	
	};

	/**
	*
	*/
	this.addWindow = function addWindow (anID,aCenter,mode)
	{
		//pointer.ctatdebug ("addWindow ("+anID+","+aCenter+")");

		var windowObject=pointer.findWindow (anID);

		if (windowObject!=null)
		{
			//pointer.ctatdebug ("we're all set, we need to push the window on the top though");
		}
		else
		{	
			//pointer.ctatdebug ("No such window known, registering ...");
		
			windowObject=new CTATWindow ();
			windowObject.setWindowID (anID);
			windowStack.push (windowObject);
					
			//if (!jQuery(anID).draggable('instance')) // Temporarily removed these lines since they clash with certain versions of jQuery we're using in student desk
			//{
				jQuery(anID).draggable(
				{	
					handle: 'h4',
					cancel: '.ctatcontent',
					containment: 'window',
					scroll: false		
				});
			//}

			//if (!jQuery(anID).resizable('instance')) // Temporarily removed these lines since they clash with certain versions of jQuery we're using in student desk
			//{
				jQuery(anID).resizable(
				{
					handles: 'n, e, s, w, ne, se, sw, nw',
					containment: 'body',
					minHeight:150,
					minWidth: 120,
					maxHeight: jQuery(window).height(),
					maxWidth: jQuery(window).width()
				});		
			//}
			
			//>-----------------------------------------------------------------
		
			var titleDiv=jQuery(anID).find('.ctattitle');
			
			titleDiv.click (function processTitleClick ()
			{
				pointer.selectWindow ("#"+jQuery(this).parent().attr ("id"));
			});	
			
			//>-----------------------------------------------------------------	
		
			var closeButton=jQuery(anID).find('.ctatwindowclose');
		
			if (closeButton)
			{
				console.log ("We have a close button, attaching click event ...");
			
				closeButton.click (function processCloseClick ()
				{
					var targetWindowID=('#'+jQuery(this).parent().attr ("id"));
				
					console.log ("processCloseClick ("+targetWindowID+")");

					var targetWindow=pointer.findWindow (targetWindowID);
					
					if (targetWindow!=null)
					{
						pointer.closeWindow (targetWindowID);
					}
					else
					{
						console.log ("Internal error: target window now found in window stack!");
					}
				});
			}	
			
			//>-----------------------------------------------------------------
		
			var minmaxButton=jQuery(anID).find('.ctatwindowmaximize');
			
			if (minmaxButton)
			{
				console.log ("We have a minmaxButton button, attaching click event ...");
			
				minmaxButton.click (function processMaximizeClick ()
				{
					var targetWindowID=('#'+jQuery(this).parent().attr ("id"));
				
					console.log ("processMaximizeClick ("+targetWindowID+")");

					var targetWindow=pointer.findWindow (targetWindowID);
					
					if (targetWindow!=null)
					{
						if (targetWindow.getWindowState ()=="DEFAULT")
						{
							targetWindow.setWindowState ("MAXIMIZED");
							targetWindow.storeDimensions (jQuery(anID).css('left'), jQuery(anID).css('top'), jQuery(anID).css('width'), jQuery(anID).css('height'));
							pointer.maximizeWindow (anID);
							jQuery(anID).draggable('disable');
						}
						else
						{
							if (targetWindow.getWindowState ()=="MAXIMIZED")
							{
								pointer.restoreWindow (anID,
													   targetWindow.getStoredX (),
													   targetWindow.getStoredY (),
												  	   targetWindow.getStoredWidth (),
													   targetWindow.getStoredHeight ());
								targetWindow.setWindowState ("DEFAULT");
								jQuery(anID).draggable('enable');
							}
						}	
					}
					else
					{
						console.log ("Internal error: target window now found in window stack!");
					}
				});
			}			

			//>-----------------------------------------------------------------
		}
		
		jQuery(anID).visible();
		
		if (aCenter === true)
		{
			//pointer.ctatdebug('centering window...');
			pointer.centerWindow (anID);
		}
		
		pointer.selectWindow (anID, mode);
		
		activeWindow=anID;
		
		return (windowObject);
	};

	/**
	*
	*/
	this.maximizeWindow = function maximizeWindow (anID)
	{
		//pointer.ctatdebug ("maximizeWindow ()");

		jQuery(anID).css('left', "5px");
		jQuery(anID).css('top', "5px");	
		jQuery(anID).css('width', (jQuery("#sizeReference").width()-10)+"px");
		jQuery(anID).css('height', (jQuery("#sizeReference").height()-60)+"px");
	};

	/**
	*
	*/
	this.restoreWindow = function restoreWindow (anID,anX,anY,aWidth,aHeight)
	{
		//pointer.ctatdebug ("restoreWindow ("+anID + "," + anX + "," + anY + "," + aWidth + "," + aHeight+")");

		jQuery(anID).css('left', anX);
		jQuery(anID).css('top', anY);
		jQuery(anID).css('width', aWidth);
		jQuery(anID).css('height', aHeight);	
	};

	/**
	*
	*/
	this.closeWindow = function closeWindow (anID)
	{
		//pointer.ctatdebug ("closeWindow ("+anID+")");
		
		var windowObject = null;
		var windex;
		var nextWindow = null;
		
		//get ref to window object and index in stack
		for (windex=0; windex < windowStack.length; windex++)
		{
			windowObject=windowStack [windex];
			if (windowObject.getWindowID () == anID)
			{
				break;
			}
			windowObject = null;
		}

		if (windowObject!=null)
		{
			//turn off blocker
			if (windowObject.getWindowMode ()=="MODAL")
			{
				toggleBlocker (false);
			}
			
			//get ref to next window in stack 
			if (windex == 0)
			{
				if (windowStack[1])
				{
					nextWindow = windowStack[1];
				}
			}
			else if (windowStack[windex-1])
			{
				nextWindow = windowStack[windex-1];
			}
			
			//remove window from stack
			windowStack.splice (windex,1);
		}
		
		//hide window
		jQuery(anID).invisible();
		
		//hide blocker
		toggleBlocker(false);
		
		//select next in stack, if there
		if (nextWindow)
		{
			//pointer.ctatdebug('selecting next window in stack: '+nextWindow.getWindowID());
			pointer.selectWindow(nextWindow.getWindowID(), nextWindow.getWindowMode());
		}
	};

	/**
	*
	*/
	this.deselectAll = function deselectAll ()
	{
		//pointer.ctatdebug ("deselectAll ("+windowStack.length+")");
		
		for (var i=0;i<windowStack.length;i++)
		{
			var windowObject=windowStack [i];
			
			//pointer.ctatdebug ("Deselecting: " + windowObject.getWindowID () + " ...");
			
			/*
			$(windowObject.getWindowID ()).find("h4").toggleClass ("ctattitle",true);
			$(windowObject.getWindowID ()).find("h4").toggleClass ("ctattitleselected",false);
			*/
			
			jQuery(windowObject.getWindowID ()).css('zIndex', ((i+1)*100));
			
			jQuery(windowObject.getWindowID ()).css('background-color',"rgba(160,160,160,0.7)"); 
		}
	};

	/**
	*
	*/
	this.selectWindow = function selectWindow (anID, mode)
	{
		//pointer.ctatdebug ("selectWindow ("+anID+")");
		
		pointer.deselectAll ();
		
		jQuery(anID).css('background-color',"rgba(200,216,224,0.7)"); 
		jQuery(anID).css('zIndex', ((windowStack.length+1)*100)+1001);
		
		if (mode === "MODAL")
		{
			toggleBlocker(true);
		}
	};
};

CTATWindowManager.prototype = Object.create(CTATBase.prototype);
CTATWindowManager.prototype.constructor = CTATWindowManager;
;
var flexEnabled=true;

/**
*
*/
$.fn.drags = function (opt) 
{
	var dragging=false;
	
	ctatdebug ("Trying to assign draggable functionality to gripper ...");
	
	if (flexEnabled==false)
	{
		ctatdebug ("Flex layout resizing currently disabled");
		return;
	}
	
	opt = $.extend(
	{
		handle: '',
		cursor: 'ew-resize',
		min: 10
	},opt);
	
	if (opt.handle === '') 
	{
		var $el = this;
	}
	else 
	{
		var $el = this.find(opt.handle);
	}
	
	// Store the prior cursor, jus tin case
	var priorCursor = $('body').css('cursor');

	return $el.css('cursor', opt.cursor).on('mousedown', function (e) 
	{
		//ctatdebug ("mousedown ()");
		
		priorCursor = $('body').css('cursor');
	
		$('body').css('cursor', opt.cursor);
	
		if (opt.handle === '') 
		{
			var $drag = $(this).addClass('draggable');
		} 
		else 
		{
			var $drag = $(this).addClass('active-handle').parent().addClass('draggable');
		}
	
		var z_idx = $drag.css('z-index');
		var	drg_h = $drag.outerHeight();
		var drg_w = $drag.outerWidth();
		var pos_y = $drag.offset().top + drg_h - e.pageY;
		var	pos_x = $drag.offset().left + drg_w - e.pageX;
		
		dragging=true;
		
		$drag.css('z-index', 1000).parents().on('mousemove', function (e) 
		{
			//ctatdebug ("mousemove ()");
			
			if (dragging==false)
			{
				//ctatdebug ("Not dragging");
				return;
			}
		
			var prev = $('.draggable').prev();
			var next = $('.draggable').next();
			var total = prev.outerWidth() + next.outerWidth();
			
			//ctatdebug('l: ' + prev.outerWidth() + ', r:' + next.outerWidth());
			
			var leftPercentage = (e.pageX - prev.offset().left + (pos_x - drg_w / 2)) / total;
			var rightPercentage = 1 - leftPercentage;

			if (leftPercentage * 100 < opt.min || rightPercentage * 100 < opt.min) 
			{
				return;
			}
			
			//ctatdebug('l: ' + leftPercentage + ', r:' + rightPercentage);
		
			prev.css('flex', leftPercentage.toString());
			next.css('flex', rightPercentage.toString());
			
			$(document).on('mouseup', function () 
			{
				//ctatdebug ("mouseup ()");
				
				$('body').css('cursor', priorCursor);
				$('.draggable').removeClass('draggable').css('z-index', z_idx);
				
				dragging=false;
			});
		});
		
		e.preventDefault();
	});
};
;/**
*
*/
var CTATSettingsDialog = function() 
{		
	CTATDialogBase.call (this, "#settingswindow", "CTATSettingsDialog", "settingswindow","MODAL");
	
	var pointer=this;
	var htmlInitialized=false;
	
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
		
		//pointer.show ();
		
		htmlInitialized=true;		
	}

	/**
	*
	*/
	this.showSettings = function showSettings ()
	{
		pointer.ctatdebug ("showSettings ()");
		
		pointer.init();
		
		var source =
		{
			localdata: window.settingsObject.parameters,
			datafields: [
						{ name: 'setting', type: 'string', map: '0'},
						{ name: 'value', type: 'string', map: '1' }
						],					   
			datatype: "array"
		};
		
		var dataAdapter = new $.jqx.dataAdapter(source, 
		{
			loadComplete: function (data) { },
			loadError: function (xhr, status, error) { }      
		});
		
		$("#settingsgrid").jqxGrid(
		{
			width: '100%',
			height: '100%',
			source: dataAdapter,
			columnsresize: true,
			sortable: true,
			columns: [
			  { text: 'Setting', datafield: 'setting', width: 200 },
			  { text: 'Value', datafield: 'value', width: 100 }
			]
		});		
	};

	var super_close = this.close;
	
	/**
	*
	*/
	this.close = function()
	{
		pointer.ctatdebug ("close ()");	
	
		super_close();
	};
};

CTATSettingsDialog.prototype = Object.create(CTATDialogBase.prototype);
CTATSettingsDialog.prototype.constructor = CTATSettingsDialog;
	;/* jQuery grayscale plugin converts color images to grayscale using canvas element
   requires jQuery (tested only with 1.5.2) and canvas compatible browser and IE
   (c) Josef Richter 2011 \ Christopher Hill 2011
   https://github.com/josefrichter/jquery-grayscale
   licensed under MIT license
   (see http://www.opensource.org/licenses/mit-license)
*/

(function( $ ){

$.fn.grayscale = function() {

return this.each(function(){

	var $this = $(this);

	$this.one('load', function(){

	if($.browser.msie){

		$this.css({
			filter:'progid:DXImageTransform.Microsoft.BasicImage(grayScale=1)',
			MsFilter:'progid:DXImageTransform.Microsoft.BasicImage(grayscale=1)'
			});

	}

	else{

		var canvas = document.createElement('canvas');
		var ctx = canvas.getContext('2d');

		var imgObj = new Image();
		imgObj.src = $this.attr('src');
		canvas.width = imgObj.width;
		canvas.height = imgObj.height;
		ctx.drawImage(this, 0, 0);

		var imgPixels = ctx.getImageData(0, 0, canvas.width, canvas.height);

			for(var y = 0; y < imgPixels.height; y++){
				for(var x = 0; x < imgPixels.width; x++){
					var i = (y * 4) * imgPixels.width + x * 4;
					var avg = (imgPixels.data[i + 0] +  imgPixels.data[i + 1] + imgPixels.data[i + 2]) / 3;

					imgPixels.data[i + 0] = avg;
					imgPixels.data[i + 1] = avg;
					imgPixels.data[i + 2] = avg;
				}
			}

				ctx.putImageData(imgPixels, 0, 0, 0, 0, imgPixels.width, imgPixels.height);
					$this.attr('src',canvas.toDataURL());
		}
				});

		});

  };
})( jQuery );
;/**
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
;/**
*
*/

// Create managers ... 
var cloudUtils =new CloudUtils ();
var desktop = new CTATDesktop ();
var settingsManager = new CTATSettings ();
var windowManager = new CTATWindowManager ();
var sManager = null;
// OLI managers, will be moved to a different location once we are more
// able to handle dynamic extension and usage of the environment
var cManager = null;
var oManager = null;

// Create global check variables, we should find a way to remove this from
// the global scope
var retrievalSize=0;
var retrievalCounter=0;
var tutordeskInitialized=false;
	
/**
*
*/
function showSolidBlocker ()
{
	console.log ("showSolidBlocker()");
	
	$("#blocker").removeClass ("blocker");
	$("#blocker").addClass ("solidblocker");
}	
	
/**
*
*/
function toggleBlocker (shown)
{
	console.log ("toggleBlocker("+shown+")");

	if (shown==false)
	{		
		$("#blocker").removeClass ("solidblocker");
		$("#blocker").css('display', 'none');
	}
	else
	{	
		$("#blocker").addClass ("blocker");
		$("#blocker").css('display', 'block');
	}
}

/**
*
*/
function toggleProgressDialog (shown)
{
	console.log ("toggleProgressDialog("+shown+")");

	toggleBlocker (shown);

	if (shown==false)
	{		
		$("#loading").invisible();
	}
	else
	{
		$("#loading").visible();
		windowManager.centerWindow ("#loading");
	}
}

/**
*
*/
function goFullscreen ()
{
	console.log ("goFullscreen()");
	
	if 
	(
		document.fullscreenElement ||
		document.webkitFullscreenElement ||
		document.mozFullScreenElement ||
		document.msFullscreenElement
	) 
	{
		if (document.exitFullscreen) 
		{
			document.exitFullscreen();
		}
		else if (document.mozCancelFullScreen) 
		{
			document.mozCancelFullScreen();
		} 
		else if (document.webkitExitFullscreen) 
		{
			document.webkitExitFullscreen();
		}
		else if (document.msExitFullscreen) 
		{
			document.msExitFullscreen();
		}
	} 
	else 
	{
		element = $(document.body).get(0);
		
		if (element.requestFullscreen) 
		{
			element.requestFullscreen();
		} 
		else if (element.mozRequestFullScreen) 
		{
			element.mozRequestFullScreen();
		} 
		else if (element.webkitRequestFullscreen) 
		{
			element.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
		} 
		else if (element.msRequestFullscreen) 
		{
			element.msRequestFullscreen();
		}
	}
}

/**
*
*/
function setStatus (anAuthorized,aMessage)
{
	var statDiv=document.getElementById ("status");

	if (statDiv)
	{
		if (anAuthorized==false)
		{
			statDiv.innerHTML=aMessage;
		}
		else
		{
			statDiv.innerHTML=aMessage;
		}
	}	
}	

/**
*
*/
function closeWizard ()
{
	console.log ("closeWizard ()");
	
	jQuery("#wizard").toggle();
	enableIconClick ()
	wizardActive=false;	
}

/**
*
*/
function processHelpIcon ()
{
	console.log ("processHelpIcon ()");
	
	window.open("http://ctat.pact.cs.cmu.edu");
}

/**
*
*/
function showAccount ()
{
	console.log ("showAccount ()");
	
	windowManager.addWindow ("#accountwindow");
	
	var mManager=new CTATAccountManager ();
	mManager.showChooser ();
}

/**
*
*/
function logOff ()
{
	console.log ("logOff ()");
	
	setStatus (false,"logging out ...");
	
	showSolidBlocker ();
	
	cloudUtils.disconnect(processLogOff);
}

/**
*
*/
function processLogOff ()
{
	console.log ("processLogOff ()");
	
	showAccount ();
}

/**
*
*/
function refreshAuth ()
{
	console.log ("refreshAuth ()");
	
	cloudUtils.reauthorize (refreshAuthResult);
}

/**
*
*/
function refreshAuthResult ()
{
	console.log ("refreshAuthResult ()");
}
	
/**
*
*/
$.fn.center = function () 
{
	this.css("left", ( $(window).width() - this.width() ) / 2+$(window).scrollLeft() + "px");
	return this;
}

/**
*
*/
function showSettings ()
{
	console.log ("showSettings ()");
	
	sManager = new CTATSettingsDialog ();
	sManager.showSettings ();
}

/**
*
*/	
function showFiles ()
{
	console.log ("showFiles()");
	
	if (!window.ctatFileChooser)
		window.ctatFileChooser = new CTATFileChooser();

	window.ctatFileChooser.show('DISPLAY');
}

/**
*
*/
function showDataShop ()
{
	console.log ("showDataShop ()");
		
	windowManager.addWindow ("#datashop",true);
}

/**
*
*/	
function startEditor ()
{
	console.log ("startEditor()");
		
	window.fEditor=new CTATFileEditor ();
	window.fEditor.init ();
}

/**
*
*/	
function showCTAT (callback)
{
	console.log ("showCTAT ()");	
	window.silexEditor = windowManager.addWindow ("#ctatwindow",true);
	if(!window.silexApp)
	{
		console.log("first time launching; init silex app");
	}	
	if (typeof callback === 'function')
		callback();
}

/**
*
*/
function initSilex()
{
	console.log('initSilex()');
	window.silexApp = new silex.App();
}	

/**
*
*/	
function showCTATAuthoring ()
{
	console.log ("showCTATAuthoring ()");
			
	windowManager.addWindow ("#ctateditor",true);
		
	//jQuery('#ctatauthoringlayout').layout();
	
	var $tabs=jQuery('#cc').tabs(
	{
		'fit': true,
		'overflow': 'auto'
	})

	var $tabs=jQuery('#tt').tabs(
	{
		'fit': true,
		'overflow': 'auto'
	})

	var $tabs=jQuery('#xx').tabs(
	{
		'fit': true,
		'overflow': 'auto'
	})	
	
	view_br ();
}

/**
*
*/
function systemsCheck ()
{
	console.log ("systemsCheck ()");
	
	if (!window.jQuery) 
	{	
		return (false);
	}	
	
	return (true);
}

/**
*
*/
function startTutordesk (aMode)
{
	console.log ("startTutordesk ()");

	useDebugging=true; // enable CTAT debugging	
	
	if (tutordeskInitialized==true)
	{
		console.log ("Tutordesk already initialized, bump");
		return;
	}
	
	if (systemsCheck ()==false)
	{
		console.log ("This system can't run tutordesk, bump");
		return;
	}
	
	var sData=settingsManager.getSettingsObject ();
	
	console.log ("Testing to see if the default application mode needs to be adjusted: " + settingsManager.getApplicationMode ());
	
	if (aMode)
	{
		sData.internal['mode']=aMode;
		
		console.log ("We're being started in a specific mode: " + aMode);
		
		if (aMode=="desktop")
		{
			console.log ("Starting in desktop mode ...");
			
			sData.internal['mode']=='browser';
		}
	}
	else
	{
		console.log ("We're not being started in a specific mode, assuming browser based execution.");
		sData.internal['mode']=='desktop';
	}
	
	console.log ("Executable application mode: " + settingsManager.getApplicationMode ());
	
	envSetup ();
		
	$(window).resize(function() 
	{
		$('#dashboard').center();
	});			
			
	$('#dashboard').center();
	
	
	$('#ctateditorcontent').resize(function()
	{
		console.log ("authoring tool resizio ...");
	});

	if (settingsManager.isDesktop ()==false)
	{		
		setStatus (false,"logging in ...");
	}
	else
	{
		$('#status').hide ();
	}

	desktop.init ();	
	
	initSilex();
	
	//initRightClick ();
	
	if (settingsManager.isDesktop ()==false)
	{
		var query = window.utils.parseQueryString(window.location.search);
		if (!query['login'])
		{
			showAccount ();
		}
		else
		{
			cloudUtils.initDrive(query['mode'], true);
			toggleBlocker(false);
		}
	}	
	else
	{
		toggleBlocker(false);
	}
	
	if (window.desktopCallback)
	{
		desktopCallback ();
	}
	
	tutordeskInitialized=true;
}

/**
 *
 */
if (window.jQuery) 
{
	$(window).on('load', function() 
	{
		if (window.studentDeskMode)
		{
			if (studentDeskMode==true)
			{
				startTutordesk ('desktop');
				return;
			}
		}
		
		startTutordesk ('browser');
	});
}
else
{
	alert ("Error: JQuery not available, can't execute $(window).load()");
}

/**
*
*/
function processCTATFolderCreate ()
{	
	ctatdebug ("processCTATFolderCreate ()");
	
	settingsManager.init ();
	
	toggleProgressDialog (false);
}
