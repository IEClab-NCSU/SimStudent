/* Class to interact with Google Drive. Since interaction with
 * Drive is done through HTTP requests, callback methods must
 * be provided to interact with any returned data.
 * 
 * Methods adapted from those given in the Google Drive API tutorials.
 * 
 *
 * Note: make sure to include google's api's:
 * https://apis.google.com/js/client.js
 */
function GoogleDrive()
{
  //client id is currently on my account, probably should move to a cmu one
  var CLIENT_ID = '162342217693-jpu5ooi1s85j4v63mke962gfe5gf14ac.apps.googleusercontent.com';
  var developerKey = 'AIzaSyCpk0YuF5Dl1ucUj-MKOpSFjfvdXMrKaGw';
  var SCOPES = ['https://www.googleapis.com/auth/drive',
                'https://www.googleapis.com/auth/plus.me'];
  var oathToken;
  
  //separates metadata from file data
  const boundary = '-------314159265358979323846';
  const delimiter = "\r\n--" + boundary + "\r\n";
  const close_delim = "\r\n--" + boundary + "--";
  /*
    Initializes the authorization process. Unless this is successful,
    no call to the Drive API will work. On success will load the Drive API, Picker API, and Google+ API.
    
    immediate indicates if a popup window should appear and if reauthorization should be automatic.
    
    callback should take a boolean authorized, indicating if authorization was successful.
  */
  this.authorize=function authorize(immediate,callback)
  {
    debug("authorize("+immediate+")");

    var pointer=this;
    var reauthorize=this.reauthorize;
    gapi.client.setApiKey(developerKey);
    gapi.auth.authorize(
        {'client_id': CLIENT_ID, 'scope': SCOPES, 'immediate': immediate},
        function(authResult){ 
          authorized=authResult && !authResult.error;
          oauthToken = authResult.access_token;
          //debug(authResult);
          if(authorized)//access token retrieved, requests to API allowed
          {
        	  debug("Authorized.");
        	  gapi.client.load('drive','v2',function(){ //load Drive API
              debug("Drive api loaded");
              gapi.load('picker',{callback: function(){//load picker api
                debug("Picker api loaded");
                gapi.client.load('plus','v1',function(){
                  debug("Google+ api loaded");
                  callback(authorized);
                });
              }});
            });
            //reauthorizes user after 45 minutes
            if(!immediate){
              window.setTimeout(reauthorize,45*60*1000);
            }
          }
          else{
              debug("Authorization unsuccessful.");
              if(immediate)//try again prompting the user to login this time
                pointer.authorize(false,callback);
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
  this.reauthorize = function reauthorize(callback){
    debug("reauthorize()");
    if(!callback){
      callback=function(a){debug("Reauthorization: "+a);};
    }
    gapi.auth.authorize(
      {'client_id': CLIENT_ID, 'scope': SCOPES, 'immediate': true},
      function(authResult){
        authorized=authResult && !authResult.error;
        oauthToken = authResult.access_token;
        callback(authorized)
    });
  }
  
  /*
   * Disconnects the user by revoking all authorization. Probably not 
   * going to be used but here if we need it.
   */
  this.disconnect=function disconnectUser() {
  var revokeUrl = 'https://accounts.google.com/o/oauth2/revoke?token=' +
      oauthToken;

  // Perform an asynchronous GET request.
  $.ajax({
    type: 'GET',
    url: revokeUrl,
    async: false,
    contentType: "application/json",
    dataType: 'jsonp',
    success: function(nullResponse) {
      // Do something now that user is disconnected
      // The response is always undefined.
    },
    error: function(e) {
      // Handle the error
      // debug(e);
      // You could point users to manually disconnect if unsuccessful
      // https://plus.google.com/apps
    }
  });
}
  
  
  /* Insert a file using raw text. Just another option if we don't have a
   * Javascript File object. 
   *
   * parent is a string representing the file id of the folder containing this file
   * callback should take in the file's metadata. Information about that
   * class at https://developers.google.com/drive/v2/reference/files
   * Most important field is id, the id of the file, and parents, the object representing the folders the file resides in.
  */
  this.insertFile=function insertFile(fileName,fileText,fileType,parent,callback)
  {
    debug("insertFile()");
    
    parents=[{id:parent}];
    //debug(parents);
    var contentType = fileType || 'text/plain';
    var metadata = {
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
      
    var request = gapi.client.request({//not executed yet
    'path': '/upload/drive/v2/files',//url for file insert
    'method': 'POST',
    'params': {'uploadType': 'multipart'},
    'headers': {
      'Content-Type': 'multipart/mixed; boundary="' + boundary + '"'
    },
    'body': multipartRequestBody});
    
    if (!callback) {
      callback = function(file) {
        debug(file);//should probably save it somewhere
      };
    }
    
    //reauthorizes first if necessary (as a fallback)
    request.execute(function(resp){
      if(!resp.error){
        //debug("callback is also: "+callback);
        callback(resp);
        //debug("after is: "+callback);
      }else if(resp.error.code==401){
        reauthorize(function(){
          callback(resp);
        });
      }else{
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
		debug("insertFolder()");
    
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



  
  /*
   * Replace a file already on Drive, provided the raw text for the file
   *
   * callback should take in the file's metadata. Information about that
   * class at https://developers.google.com/drive/v2/reference/files
   * Most important field is id, the id of the file, and parents, the object representing the folders the file resides in.
   */
  this.updateFile=function updateFile(fileId, fileText, callback) 
  {
    var fun = function(fileMetadata){
      debug("updateFileFromText()");
        var contentType = 'text/plain';
        var base64Data = btoa(fileText);
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

        var request = gapi.client.request({//not executed yet
            'path': '/upload/drive/v2/files/' + fileMetadata.id,//url for file update
            'method': 'PUT',
            'params': {'uploadType': 'multipart', 'alt': 'json'},
            'headers': {
              'Content-Type': 'multipart/mixed; boundary="' + boundary + '"'
            },
            'body': multipartRequestBody});
        if (!callback) {
          callback = function(file) {
            debug(file);
          };
        }
        request.execute(callback);
      }
      
      this.getMetadata(fileId,fun);
  };
  
  /*
   * Downloads a file given the fileId for the file. Gives the file
   * text if successful or null otherwise.
   *
   * callback should take in the file's text as a string
   */
  this.downloadFile=function downloadFile(fileId, callback) {
	//unfortunately no built in function in Google Client Library API does this
    if(!callback){
      callback=function(a){debug(a);};
      //debug("callback was null");
    }
    if(!fileId){
      debug("fileId null");
      return;
    }
      
    var fun=function(fileMetaData)
	{
      debug('downloadFile()');
	  
      //debug(fileMetaData);
	  
      if (fileMetaData.downloadUrl) 
	  {
		//case it's even downloadable
		
        var accessToken = gapi.auth.getToken().access_token;
        var xhr = new XMLHttpRequest();
        xhr.open('GET', fileMetaData.downloadUrl);
        xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
        
        
          
        xhr.onload = function() {//function called if download is successful
          callback(xhr.responseText);
        };
        xhr.onerror = function() {//function called if download unsuccessful
          callback(null);
        };
        xhr.send();
      } else {
        debug("No download url");
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
		debug("getFile("+fileName+")");
	
		if(!callback) callback=function(file){debug(file);}
    
		var pointer = this;
		
		this.retrieveFile(fileName,parent,function(files)
		{
			if(!files[0])
			{
				callback(null);
			}
			else
			{
				pointer.downloadFile(files[0].id,callback);
			}
		});
   }
  
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
    debug("retrieveFiles("+queryString+")");
	
    if(!callback)
      callback=function(a){debug(a);};
      
    //this function will iterate through all pages and add items to result array
    var retrievePageOfFiles = function(request, result) {
      request.execute(function(resp) {
        result = result.concat(resp.items);//adding items on this page
        var nextPageToken = resp.nextPageToken;
        if (nextPageToken) {//case there exists a next page
          request = gapi.client.drive.files.list({
            'pageToken': nextPageToken,
            'q': queryString//our query
          });
          retrievePageOfFiles(request, result);//recursion into next page
        } else {
          callback(result);//no more pages, so do callback
        }
      });
    }
    var initialRequest = gapi.client.drive.files.list({
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
  this.retrieveAllFiles=function retrieveAllFiles(callback) {
    debug("retrieveAllFiles()");
    retrieveFiles('trashed=false',callback);
  };
  /*
   * Given a file name, get all files with that name. The name doesn't have
   * to be unique, so we need to return an array.
   * Note that this returns a list of file metadata, not the files themselves
   *
   * callback should take in an array of File resource objects
   */
  this.retrieveFile=function retrieveFile(fileName,parent,callback){
    debug("retrieveFile()");
    var query = "trashed=false and title='"+fileName+"'";
    if(parent){
      query += "and '"+parent+"' in parents";
    }
    //debug(query);
    this.retrieveFiles(query,callback);
  };
  /*
   * Given a file id, get the metadata for the file. This is unique, so we only
   * need to return a single File Resource object 
   */
  this.getMetadata=function getMetadata(fileId,callback){
    debug("getMetadata()");
    var request = gapi.client.drive.files.get({'fileId': fileId});
     //reauthorizes first if necessary (as a fallback)
    request.execute(function(resp){
      if(!resp.error){
        callback(resp);
      }else if(resp.error.code==401){
        reauthorize(function(){
          callback(resp);
        });
      }else{
        alert("An error occured: "+resp.error.message);
      }
    });//where the request is actually sent
  };
  
  /*
     Displays a menu of folders so that the user can choose a folder.
     Set ownedByMe to true if you don't want to display folders shared with me.
     Callback takes String representing fileId of the chosen folder.
  */
  this.pickFolder=function pickFolder(ownedByMe,callback){
    var fun = function(data){
      if (data[google.picker.Response.ACTION] == google.picker.Action.PICKED) {
          var doc = data.docs[0].id;
          callback(doc);
      }else{
        callback(null);
      }
    };
  
    var view = new google.picker.DocsView (google.picker.ViewId.FOLDERS);
    view.setIncludeFolders(true);
    view.setSelectFolderEnabled(true);
    view.setOwnedByMe(ownedByMe);
    var picker = new google.picker.PickerBuilder().
        addView(view).
        setOAuthToken(oauthToken).
        //setDeveloperKey(developerKey).
        setCallback(fun).
        build();
    picker.setVisible(true);
  };
  
  /*
     parent: string id of parent
     ownedByMe: boolean representing if it should show only docs owned by me (not shared with me)
     Callback takes String representing fileId
  */
  this.pickFile=function pickFile(parent,ownedByMe,callback){
    debug("pickFile()");
    var fun = function(data){
      if (data[google.picker.Response.ACTION] == google.picker.Action.PICKED) {
          var doc = data.docs[0].id;
          callback(doc);
      }else{
        callback(null);
      }
    };
    var view = new google.picker.DocsView (google.picker.ViewId.DOCS);
    view.setIncludeFolders(true);
    view.setOwnedByMe(ownedByMe);
    view.setParent(parent);
    var picker = new google.picker.PickerBuilder().
        addView(view).
        setOAuthToken(oauthToken).
        //setDeveloperKey(developerKey).
        setCallback(fun).
        build();
    picker.setVisible(true);        
  }
}
/* Portions of this page are modifications based on work created and shared by Google and used according to terms described in the Creative Commons 3.0 Attribution License. */