/**
 *
 */

/**
*
*/
var CTATFileManager = function(aMode) 
{
	var wId="#drivewindow";
	
	CTATDialogBase.call (this, wId, "CTATFileManager", "fileui","MODELESS");
	
	var pointer=this;
	var currentFolderid="root";
	var mode="NONE";
	var htmlInitialized=false;
	var fControls=new CTATFileControls ();
	
	/**
	*
	*/
	this.initHTML=function initHTML ()
	{
		pointer.ctatdebug ("initHTML ()");
		
		if (htmlInitialized==true)
		{
			return;
		}
		
		$("#explReload").on ("click",function ()
		{
			console.log ("click ()");
		});
		
		$("#explNew").on ("click",function ()
		{
			console.log ("click ()");
			
			var windowObject=windowManager.addWindow ("#newfolderdialog");
			if (windowObject!=null)
			{
				windowObject.setWindowMode ("MODAL");
			}
			
			windowManager.centerWindow ("newfolderdialog");			
		});

		$("#explTrash").on ("click",function ()
		{
			console.log ("click ()");
		});		
				
		$(wId+"-close").on("click",function ()
		{
			pointer.close ();
		});					
				
		$('#explorergripper').drags();
		
		$("#googledrive").bind('select_node.jstree', function (evt, selectionData) 
		{		
			console.log ("select_node.jstree");
			
			var selectedNodes=$("#googledrive").jstree().get_selected(true);
									
			var r=[];
						
			for(aNode in selectedNodes) 
			{
				console.log ("Adding selected node: " + selectedNodes [aNode].text + ")");
			
				cloudUtils.getIdFromName (selectedNodes [aNode].text,"root",pointer.processFolderSelect)
				
				return;
			}
		});		
				
		htmlInitialized=true;
	};
	
	/**
	*
	*/
	this.processFolderSelect=function processFolderSelect (data)
	{
		pointer.ctatdebug ("processFolderSelect ()");
		
		console.log (JSON.stringify (data));
	};
	
	/**
	*
	*/
	this.showExplorer=function showExplorer ()
	{
		pointer.ctatdebug ("showExplorer ()");
				
		windowManager.addWindow ("#drivewindow",false);
	
		$('#drivelayout').layout();
				
		mode="OPEN";
				
		currentFolderid=cloudUtils.getRootFolder ();
		
		pointer.ctatdebug ("Root id: " + currentFolderid);
		
		var fCache=cloudUtils.getFolderCache ();
		
		if (fCache ["root"]!=null)
		{
			fControls.displayFileTree ("googledrive",fCache ["root"]);
		}
		else
		{
			pointer.ctatdebug ("Internal error fCache [root] is null!");
		}
	};
	
	pointer.initHTML ();
	
	var super_close = this.close;
	
	/**
	*
	*/
	this.close = function()
	{
		pointer.ctatdebug ("close ()");	
		
		//$("#googledrivedetailstt").hide();
	
		super_close();
	};	
};

CTATFileManager.prototype = Object.create(CTATDialogBase.prototype);
CTATFileManager.prototype.constructor = CTATFileManager;
