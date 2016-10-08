var currentFileId;//should be set when user saves or opens
var rootFolder;//should be set on load time
var preferenceFileId;//should be set on load time

/**
*
*/
function file_open(callback1)
{
  debug("file_open");
  var callback = function(fileId){//callback after user chooses file
    debug("callback");
    if(!fileId) {
      debug("no fileId");
      return;
    }
	clearCanvas();
    drive.downloadFile(fileId,callback1);
    currentFileId=fileId;
  }
  
  var fun = function(){//function to execute if authorized
    debug("fun");
    drive.pickFile(rootFolder,true,callback);
  }
   
  if(modified) {
    prompt_discard_changes(function(){
	  executeWhenAuthorized(fun);
	});
  }
  else {
    executeWhenAuthorized(fun);
  }
}

/**
*
*/
function file_save(callback){
  debug("file_save");
  var fun = function(){
    if(!currentFileId){//not (recorded as being) saved on drive
      file_saveas();
    }else{
      var update = getSerializedInterfaceDescription();
      drive.updateFile(currentFileId,update,callback);
	  modified = false;
    } 
  }
  
  executeWhenAuthorized(fun);
}

/**
* save as
*/
function file_saveas(callback){
  debug("file_saveas");
  var save=function(name,text,parentId){
    if(parentId){
      drive.insertFile(name,text,null,parentId,function(file){
        currentFileId=file.id;
        callback();
      });
	  modified = false;
    }
    else{
      drive.insertFile(name,text,null,rootFolder,function(file){
        currentFileId=file.id;
        if(callback){
          callback();
        }
      });
	  modified = false;
    }
  }

  var fun = function(){
	var name = window.prompt("Please enter a name for the file", "interface.brd");
	if(name == null) { // cancel
		return;
	}
    var text= getSerializedInterfaceDescription();
    /*some stuff checking if user wants to save in different folder */
    var useRootFolder=true;//value depends on above
    
    if(useRootFolder){//user doesn't opt to change folders
      save(name,text,null);
    }else{//user opts to change folders
      drive.pickFolder(true,function(parentId){
        debug("id: "+parentId);
        save(name,text,parentId);
      });
    }
  }
  
  
  executeWhenAuthorized(fun);
}

function file_save_jess(callback) {
  var save=function(name,text,parentId){
    if(parentId){
      drive.insertFile(name,text,null,parentId,function(file){
        callback();
      });
    }
    else{
      drive.insertFile(name,text,null,rootFolder,function(file){
        if(callback){
          callback();
        }
      });
    }
  }

  var fun = function(){
	var factsName = window.prompt("Please enter a name for the Jess facts file", "interface.wme");
    var factsText = getJessFacts();
	
	var typesName = window.prompt("Please enter a name for the WME types file", "wmeTypes.clp");
    var typesText = getWMETypes();
	
    /*some stuff checking if user wants to save in different folder */
    var useRootFolder=true;//value depends on above
    
    if(useRootFolder){//user doesn't opt to change folders
      if(factsName !== null) save(factsName,factsText,null);
	  if(typesName !== null) save(typesName,typesText,null);
    }else{//user opts to change folders
      drive.pickFolder(true,function(parentId){
        debug("id: "+parentId);
        if(factsName !== null) save(factsName,factsText,parentId);
		if(typesName !== null) save(typesName,typesText,parentId);
      });
    }
  }
  
  
  executeWhenAuthorized(fun);
}

/* Just a dummy method to use for testing random methods. Would
   take the place of file_saveas when needed. */
function file_saveas1(){
  //debug(gapi.auth.getToken());
  drive.disconnect();
  //drive.getFile("preferences.json",rootFolder,function(text){
  //  debug(text);
  //});
}

/*
 * This method was supposed to check for authorization, but that
 * seems impossible at the moment, so we have to assume authorization.
 * Leaving this method here in case we figure it out.
 */
function executeWhenAuthorized(fun){
  /*if(!drive.authorized){
    drive.reauthorize(fun);
  }else{
    fun();
  }*/
  fun();
}