var currentFileId;//should be set when user saves or opens
var rootFolder;//should be set on load time
var preferenceFileId;//should be set on load time

/**
*
*/
function file_open(callback1)
{
  ctatdebug("file_open");
  var callback = function(fileId){//callback after user chooses file
    ctatdebug("callback");
    if(!fileId) {
      ctatdebug("no fileId");
      return;
    }
    drive.downloadFile(fileId,callback1);
    currentFileId=fileId;
  }
  
  var fun = function(){//function to execute if authorized
    ctatdebug("fun");
    drive.pickFile(rootFolder,true,callback);
  }
    
  executeWhenAuthorized(fun);
}

/**
*
*/
function file_save(callback){
  ctatdebug("file_save");
  var fun = function(){
    if(!currentFileId){//not (recorded as being) saved on drive
      file_saveas();
    }else{
      var update = "some update to the file";
      drive.updateFile(currentFileId,update,callback);
    } 
  }
  
  executeWhenAuthorized(fun);
}

/**
* save as
*/
function file_saveas(callback){
  ctatdebug("file_saveas");
  var save=function(name,text,parentId){
    if(parentId){
      drive.insertFile(name,text,null,parentId,function(file){
        currentFileId=file.id;
        callback();
      });
    }
    else{
      drive.insertFile(name,text,null,rootFolder,function(file){
        currentFileId=file.id;
        if(callback){
          callback();
        }
      });
    }
  }

  var fun = function(){
    var name="some_name.txt";
    var text="some text";
    /*some stuff checking if user wants to save in different folder */
    var useRootFolder=true;//value depends on above
    
    if(useRootFolder){//user doesn't opt to change folders
      save(name,text,null);
    }else{//user opts to change folders
      drive.pickFolder(true,function(parentId){
        ctatdebug("id: "+parentId);
        save(name,text,parentId);
      });
    }
  }
  
  
  executeWhenAuthorized(fun);
}

/* Just a dummy method to use for testing random methods. Would
   take the place of file_saveas when needed. */
function file_saveas1(){
  //ctatdebug(gapi.auth.getToken());
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