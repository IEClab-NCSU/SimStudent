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
	this.retrieveFilesByFolder=function retrieveFilesByFolder(folderId, callback) 
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
		requestQueue.add(batch, cbk, true);
	};
	
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
	
	this.setReqGap = function(g)
	{
		requestQueue.setGap(g);
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
GoogleDrive.prototype.constructor = GoogleDrive;