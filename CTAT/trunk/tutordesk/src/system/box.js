
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
};