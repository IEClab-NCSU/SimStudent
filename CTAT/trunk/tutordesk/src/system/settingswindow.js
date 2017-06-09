/**
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
	