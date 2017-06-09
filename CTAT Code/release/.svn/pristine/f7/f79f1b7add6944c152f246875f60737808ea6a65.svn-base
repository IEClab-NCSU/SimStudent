/**	@fileoverview this class exposes the dropbox file api to CloudUtils; all calls
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
}