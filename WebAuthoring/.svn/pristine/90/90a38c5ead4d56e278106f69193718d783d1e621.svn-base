/**
* Google Sheets Test 1:
*
* tests client side JSON and XML parsing. 
*
* Notes: https://developers.google.com/apps-script/reference/ui/ui-instance
*        https://developers.google.com/apps-script/guides/ui-service
*        https://sites.google.com/site/appsscriptforbusiness/example---how-to
*        https://developers.google.com/apps-script/guides/ui-service?csw=1
*        https://developers.google.com/apps-script/guide_libraries
*
* Sheet methods available for API-ification:
* https://developers.google.com/apps-script/reference/spreadsheet/sheet
*/

/**
*
*/
function ctatdebug (aMessage)
{
	Logger.log(aMessage); 
}

/**
 * Retrieves all the rows in the active spreadsheet that contain data 
 * and logs the values for each row.
 * For more information on using the Spreadsheet API, see
 * https://developers.google.com/apps-script/service_spreadsheet
 */
function readRows() 
{
	var sheet = SpreadsheetApp.getActiveSheet();
	var rows = sheet.getDataRange();
	var numRows = rows.getNumRows();
	var values = rows.getValues();

	for (var i = 0; i <= numRows - 1; i++) 
	{
		var row = values[i];
		Logger.log(row);
	}
};

/**
*
*/
function dumpPrivateCache() 
{ 
	var cache = CacheService.getPrivateCache();
  
	var firstKey = cache.get("firstKey");
	var lastKey = cache.get("lastKey");
  
	ctatdebug("firstKey = " + firstKey);
	ctatdebug("lastKey = " + lastKey);
  
	for (var key = firstKey; key <= lastKey; key++) 
	{
		ctatdebug("key = " + key + " value = " + cache.get(key));
	}
}

/**
*
*/
function createOnEditQueue() 
{  
	var cache = CacheService.getPrivateCache();
  
	//if (cache.get("firstKey") == null) {
    cache.put("firstKey", 0);
    cache.put("lastKey", 0);
	//}
}

/**
*
*/
function enqueueOnEdit(range) 
{
	var cache = CacheService.getPrivateCache();
 
	var lastKey = cache.get("lastKey");
  
	lastKey++;
  
	cache.put(lastKey, range);
	cache.put("lastKey", lastKey);
}

/**
*
*/
function dequeueOnEdit() 
{
	var cache = CacheService.getPrivateCache();
 
	var firstKey = cache.get("firstKey");
	var lastKey = cache.get("lastKey");
  
	ctatdebug("firstKey = " + firstKey);
	ctatdebug("lastKey = " + lastKey);
  
	// something isn't right here
	if ((firstKey < lastKey)) 
	{
		firstKey++;
		var firstValue = cache.get(firstKey);
    
		ctatdebug ("dequeueOnEdit() = " + firstValue);
    
		cache.remove(firstKey);
    
		//firstKey++; 
		cache.put("firstKey", firstKey);
    
		return firstValue;
	}
  
	ctatdebug ("dequeueOnEdit() = null");
  
	return null;
}

/**
*
*/
function dequeueRangeValueOnEdit() 
{
	var result = null;
	var a1Notation = dequeueOnEdit();
  
	if (a1Notation != null) 
	{
		var spreadsheet = SpreadsheetApp.getActiveSpreadsheet();
    
		ctatdebug (a1Notation);
		ctatdebug (spreadsheet.getName());
    
		var range = spreadsheet.getRange(a1Notation);
    
		result = new Object();
		result.a1Notation = a1Notation;
		result.value = range.getValue();
	}
  
	return result;
}

/**
*
*/
function lengthOnEditQueue() 
{
	var cache = CacheService.getPrivateCache();
 
	var firstKey = cache.get("firstKey");
	var lastKey = cache.get("lastKey");
  
	return lastKey - firstKey + 1;
}

/**
*
*/
function onCellEdit(e) 
{  
	ctatdebug ("onCellEdit ()");
  
	var stringToDisplay = e.range.getA1Notation();
  
	var tutorUiApp = UiApp.getActiveApplication();
  
	tutorUiApp.createButton("Creating Button");

	var cellRangeComponent = tutorUiApp.getElementById("cellRange");
	var cellValueComponent = tutorUiApp.getElementById("cellValue");
	var statusComponent = tutorUiApp.getElementById("status");
	var randomNameComponent = tutorUiApp.getElementById("someUnlikelyNamedComponent");
  
	enqueueOnEdit(e.range.getA1Notation());

	return tutorUiApp;
};

/**
*
*/
function onCellChange(e) 
{ 
	ctatdebug ("onCellChange ()");
  
	/*
	var stringToDisplay = e.range.getA1Notation();
  
	var tutorUiApp = UiApp.getActiveApplication();
  
	tutorUiApp.createButton("Creating Button");

	var cellRangeComponent = tutorUiApp.getElementById("cellRange");
	var cellValueComponent = tutorUiApp.getElementById("cellValue");
	var statusComponent = tutorUiApp.getElementById("status");
	var randomNameComponent = tutorUiApp.getElementById("someUnlikelyNamedComponent");
  
	enqueueOnEdit(e.range.getA1Notation());
	*/

	return tutorUiApp;
};

/**
* Google Sheet Access
* https://developers.google.com/apps-script/reference/spreadsheet/sheet
* https://developers.google.com/apps-script/reference/spreadsheet/range
*/
function getTargetCell (a1Notation) 
{
	var tutorUiApp = UiApp.getActiveApplication();
    
	var sheet = SpreadsheetApp.getActiveSpreadsheet();
  
	var range = sheet.getRange(a1Notation);
  
	return (range);
}  

/**
* Google Sheet Access
* https://developers.google.com/apps-script/reference/spreadsheet/sheet
* https://developers.google.com/apps-script/reference/spreadsheet/range
*/
function highlightCell (a1Notation, color) 
{
	ctatdebug ("highlightCell ("+a1Notation+","+color+")");

	var targetCell=getTargetCell (a1Notation);
    
	if (targetCell!=null)
	{
		targetCell.setFontColor(color);  
	} 
};

/**
* Google Sheet Access
* https://developers.google.com/apps-script/reference/spreadsheet/sheet
* https://developers.google.com/apps-script/reference/spreadsheet/range
*/
function setText (a1Notation, aValue) 
{
	ctatdebug ("setText ("+a1Notation+","+aValue+")");
  
	var targetCell=getTargetCell (a1Notation);
    
	if (targetCell!=null)
	{
		targetCell.setValue(aValue);  
	} 
};


/**
* Google Sheet Access
* https://developers.google.com/apps-script/reference/spreadsheet/sheet
* https://developers.google.com/apps-script/reference/spreadsheet/range
*/
function setBackground (a1Notation, aColor) 
{
	ctatdebug ("setBackground ("+a1Notation+","+aColor+")");
  
	var targetCell=getTargetCell (a1Notation);
    
	if (targetCell!=null)
	{
		targetCell.setBackground(aColor);
	} 
};

/**
* https://developers.google.com/apps-script/guides/triggers/installable#managing_triggers_programmatically
*/
function deleteTrigger(triggerId) 
{
  ctatdebug ("deleteTrigger("+triggerId+")");
  
	var allTriggers = ScriptApp.getProjectTriggers();
	for (var i = 0; i < allTriggers.length; i++) 
	{
		// If the current trigger is the correct one, delete it.
		if (allTriggers[i].getUniqueId() == triggerId) 
		{
			ScriptApp.deleteTrigger(allTriggers[i]);
			break;
		}
	}
}

/**
* https://developers.google.com/apps-script/guides/triggers/installable#managing_triggers_programmatically
*/
function manageTriggers()
{
	ctatdebug ("manageTriggers()");
  
	var spreadsheet = SpreadsheetApp.getActiveSpreadsheet();  
  
	var allTriggers = ScriptApp.getProjectTriggers();
  
	var clean=false;
  
	ctatdebug ("Clearing old triggers ("+allTriggers.length+")...");
  
	while (clean==false)
	{
		clean=true;
      
        allTriggers = ScriptApp.getProjectTriggers();
    
		for (var i = 0; i < allTriggers.length; i++) 
		{
			ScriptApp.deleteTrigger(allTriggers[i]);
			Utilities.sleep(1000);
			clean=false;
			break;
		}  
	}
  
	ctatdebug ("Install new triggers ...");
  
	// We use this for our basic operations or in other words we
	// get our basic tutoring behavior from this
	ScriptApp.newTrigger("onCellEdit")
		.forSpreadsheet(spreadsheet)
		.onEdit()
		.create();
  
	// For a more subtle understanding of what a user is doing we install
	// a change event, which hopefully triggers when users interact with
	// the menu for example. We need to make sure that this event is not
	// processed when we access the spreadsheet from the outside, otherwise
	// we will end up in an infinite loop
	ScriptApp.newTrigger("onCellChange")
		.forSpreadsheet(spreadsheet)
		.onChange()
		.create();    
}

/**
 * https://developers.google.com/apps-script/reference/base/ui
 * https://developers.google.com/apps-script/reference/html/html-output
 */
function onOpen() 
{  
	//createOnEditQueue();
  
	var htmlOutput = HtmlService.createHtmlOutputFromFile("TutorSidebar.html").setTitle("Google Sheet - Test 9");
 
	SpreadsheetApp.getUi().showSidebar(htmlOutput);
  
	manageTriggers ();
};
