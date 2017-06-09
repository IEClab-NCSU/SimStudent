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
  //client id is currently on my account, probably should move to a cmu one
  
  /*
  var CLIENT_ID = '759564077334-59ihbnunrnc5md28rt4qqd5421slkbll.apps.googleusercontent.com';
  var developerKey = 'AIzaSyBUOv0HElL_deW7rs5fGLsTegYP5F2g4wU';
  */
  
  /*
  var CLIENT_ID = '174954919694-kcpallmft9gusojavrdt498r7g48fnm1.apps.googleusercontent.com';
  var developerKey = 'AIzaSyD3kTaGnMqYQRk_3hMJVKVTZ9-DWVQ6ulE';
  */
  
  var CLIENT_ID = '174954919694-kcpallmft9gusojavrdt498r7g48fnm1.apps.googleusercontent.com';
  var developerKey = 'AIzaSyD3kTaGnMqYQRk_3hMJVKVTZ9-DWVQ6ulE';  
  
  var SCOPES = ['https://www.googleapis.com/auth/drive',
				'https://www.googleapis.com/auth/drive.appfolder',
				'https://www.googleapis.com/auth/drive.install',
				'https://www.googleapis.com/auth/drive.scripts',
				'https://www.googleapis.com/auth/plus.me'];
  var oathToken;
  var tempFileID=null;
  var tempFileMetadata=null;
  
  //separates metadata from file data
  const boundary = '-------314159265358979323846';
  const delimiter = "\r\n--" + boundary + "\r\n";
  const close_delim = "\r\n--" + boundary + "--";
    
  this.getTempFileID=function getTempFileID ()
  {
	return (tempFileID);
  };
  
  this.getTempFileMetadata=function getTempFileMetadata ()
  {
	return (tempFileMetadata);
  };

  /*
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

    var pointer=this;
    var reauthorize=this.reauthorize;
    gapi.client.setApiKey(developerKey);
    gapi.auth.authorize
	(
        {
			'client_id': CLIENT_ID,
			'scope': SCOPES, 
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
  
	/*
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
				'client_id': CLIENT_ID, 
				'scope': SCOPES, 
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
  
	/*
	* Disconnects the user by revoking all authorization. Probably not 
	* going to be used but here if we need it.
	*/
	this.disconnect=function disconnectUser() 
	{
		var revokeUrl = 'https://accounts.google.com/o/oauth2/revoke?token=' +  oauthToken;

		// Perform an asynchronous GET request.
		$.ajax(
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
	this.copyFile=function copyFile(originFileId, copyTitle, launchFunction, parentID) 
	{
		ctatdebug ("copyFile ("+originFileId+","+copyTitle+")");
	
		var parents=[{id:parentID}];
		
		var body = 
		{
			'title': copyTitle,
			'parents': parents
		};
		
		var request = gapi.client.drive.files.copy
		({
			'fileId': originFileId,
			'resource': body
		});
		
		request.execute(function(resp) 
		{
			console.log('Copy ID: ' + resp.id +", lauching ...");
			
			launchFunction (resp.id);
		});
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
	
		const boundary = '-------314159265358979323846';
		const delimiter = "\r\n--" + boundary + "\r\n";
		const close_delim = "\r\n--" + boundary + "--";
			
		var contentType = 'application/octet-stream';
		// Updating the metadata is optional and you can instead use the value from drive.files.get.
		var base64Data = btoa(fileData);
		var multipartRequestBody =
			delimiter +
			'Content-Type: application/json\r\n\r\n' +
			fileData +
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
				console.log(file)
			};
		}
			
		request.execute(callback);
	};
	
	/* 
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
    
		base64Data = btoa(fileText);
		
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
		request.execute(function(resp)
		{
			tempFileID=resp.id;
		
			if(!resp.error)
			{
				//ctatdebug("callback is also: "+callback);
				//callback(resp);
				callback (fileText);
				
				//ctatdebug("after is: "+callback);
			}
			else if(resp.error.code==401)
			{
				reauthorize(function()
				{
					//callback(resp);
					callback (fileText);
				});
			}
			else
			{
				alert("An error occured: "+resp.error.message);
			}
		});//where the request is actually sent    
	};
	
	/* Insert a folder. 
	*
	* parent is a string representing the file id of the folder containing this folder
	* callback should take in the folder's metadata. Information about that
	* class at https://developers.google.com/drive/v2/reference/files
	* Most important field is id, the id of the folder, and parents, the object representing the folders the folder resides in.
	*/
	this.insertFolder=function insertFolder(folderName, parent, callback)
	{
		ctatdebug("insertFolder()");
    
		var parents=[{id:parent}];
		
		var metadata = 
		{
			'title': folderName,
			'mimeType': "application/vnd.google-apps.folder"//,
			//'parents': parents
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
		request.execute(function(resp)
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
				alert("An error occured: "+resp.error.message);
			}
		});//where the request is actually sent
	};
		
	/**
	 * Retrieve a list of files belonging to a folder.
	 *
	 * @param {String} folderId ID of the folder to retrieve files from.
	 * @param {Function} callback Function to call when the request is complete.
	 *
	 */
	this.retrieveAllFilesInFolder=function retrieveAllFilesInFolder(folderId, callback) 
	{
		ctatdebug("retrieveAllFilesInFolder("+folderId+")");
	
		var retrievePageOfChildren = function(request, result) 
		{
			request.execute(function(resp) 
			{
				if ((resp==null) || (resp==undefined))
				{
					ctatdebug ("Fatal error, can't call Google client API");
					return;
				}
			
				//console.log (introspect ("resp",resp.items,"   ",5));
			
				result = result.concat(resp.items);
				
				var nextPageToken = resp.nextPageToken;
				
				if (nextPageToken) 
				{
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
		}
		
		var initialRequest = gapi.client.drive.children.list
		(
			{
				'folderId' : folderId,
				'q' : 'trashed = false'
			}
		);
			
		retrievePageOfChildren(initialRequest, []);
	};
	
	/*
	* Given a file name, get all files with that name. The name doesn't have
	* to be unique, so we need to return an array.
	* Note that this returns a list of file metadata, not the files themselves
	*
	* callback should take in an array of File resource objects
	*/
	this.retrieveFile=function retrieveFile(fileName,parent,callback)
	{
		ctatdebug("retrieveFile()");
		
		var query = "trashed=false and title='"+fileName+"'";
		
		if(parent)
		{
			query += "and '"+parent+"' in parents";
		}
		
		//ctatdebug(query);
		this.retrieveFiles(query,callback);
	};	
	
	/*
	* Gets all files that match the query string. It gives an array of the files' metadata, not the file text itself
	* NOTE: this includes files in the trash.
	* 
	* callback should take in a an array of File objects.
	* For information about the query string, see
	* https://developers.google.com/drive/web/search-parameters
	*/
	this.retrieveFiles=function retrieveFiles(queryString,callback)
	{
		ctatdebug("retrieveFiles("+queryString+")");

		if(!callback)
		{
			callback=function(a){ctatdebug(a);};
		}	
	  
		//this function will iterate through all pages and add items to result array
		var retrievePageOfFiles = function(request, result) 
		{
			request.execute(function(resp) 
			{
				result = result.concat(resp.items);//adding items on this page
				var nextPageToken = resp.nextPageToken;
				
				if (nextPageToken) 
				{
					//case there exists a next page
					request = gapi.client.drive.files.list(
					{
						'pageToken': nextPageToken,
						'q': queryString//our query
					});
					retrievePageOfFiles(request, result);//recursion into next page
				} 
				else 
				{
					callback(result);//no more pages, so do callback
				}
			});
		}
		var initialRequest = gapi.client.drive.files.list(
		{
			'q': queryString//our query
		});
		
		retrievePageOfFiles(initialRequest, []);//initial state is obviously empty array
	};
  
	/*
	* Gets all files except those in the trash, cause that's ridiculous.
	* Note that this returns a list of file metadata, not the files themselves
	* 
	* callback should take in an array of File resource objects
	*/
	this.retrieveAllFiles=function retrieveAllFiles(callback) 
	{
		ctatdebug("retrieveAllFiles()");
		retrieveFiles('trashed=false',callback);
	};	
	
	/*
	* Given a file id, get the metadata for the file. This is unique, so we only
	* need to return a single File Resource object 
	*/
	this.getMetadata=function getMetadata(fileId,callback)
	{
		ctatdebug("getMetadata()");
		
		var request = gapi.client.drive.files.get({'fileId': fileId});
		
		//reauthorizes first if necessary (as a fallback)
		
		request.execute(function(resp)
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
				alert("An error occured: "+resp.error.message);
			}
		});//where the request is actually sent
	};	
	
	/*
	* Downloads a file given the fileId for the file. Gives the file
	* text if successful or null otherwise.
	*
	* callback should take in the file's text as a string
	*/
	this.downloadFile=function downloadFile(fileId, callback) 
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
    
		this.getMetadata(fileId,fun);
	};
  
	/*
	* Gets a file given the name. This method makes the assumption that
	* that there is only one such file. If there are multiple, it takes
	* the first one queried. If you need to parse through each different
	* file, use the retrieveFiles + downloadFile methods manually.
	*
	* parent is a string representing the file id of the folder containing this file
	* callback should take in the file's text as a string
	*/
	this.getFile = function getFile(fileName,parent,callback)
	{
		ctatdebug("getFile("+fileName+","+parent+")");
	
		if(!callback) callback=function(file)
		{
			ctatdebug(file);
		}
    
		var pointer=this;
		
		this.retrieveFile(fileName,parent,function(files)
		{
			if(!files[0])
			{
				callback(null);
			}
			else
			{
				tempFileID=files[0].id;
				pointer.downloadFile(files[0].id,callback);
			}
		});
	}	
}
