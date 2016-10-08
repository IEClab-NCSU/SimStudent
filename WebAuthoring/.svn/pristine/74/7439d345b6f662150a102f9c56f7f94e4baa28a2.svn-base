// OTBEditor.js
//author - hardik vasa, kevin
/* Note on internal representation of components:
   zOrderedComponents is the master list of components. Each component in this list is a RaphaelJS object on screen.
   Each component may also have these two attributes: groupingHandleId and ctatComponentIndex, both of which are indices into the respective arrays */

var zOrderedComponents = new Array(); // list of on-canvas components from back to front; SOME ELEMENTS MAY BE NULL if they have been deleted
var selectedComponents = new Array(); // set of components that are currently selected
var selectionRect = null;
var grabBoxes = new Array();
var xAlignmentLines = null; // when moving or resizing, this will hold the vertical lines (constant x) that can be "dynamically aligned" to
var yAlignmentLines = null; // when moving or resizing, this will hold the horizontal lines (constant y) that can be "dynamically aligned" to
var dragOffsetX = 0, dragOffsetY = 0; // for dragging: the offset of the cursor from the top left corner of the icon being dragged
var lastClickedIconId = null; // ID of the toolbox icon that was last clicked on

function drag(ev) {
	ev.dataTransfer.setData("Text", ev.target.id);

	iconRect = document.getElementById(ev.target.id).getBoundingClientRect();
	dragOffsetX = ev.clientX - iconRect.left;
	dragOffsetY = ev.clientY - iconRect.top;
}
function dropOnCanvas(ev) {
	debug('dropOnCanvas('+ev+')');
	ev.preventDefault();
	var data = ev.dataTransfer.getData("Text");
	var PREFIX = 'toolbox_';
	if(data.substring(0, PREFIX.length) === PREFIX) {
		beginUndoableAction();
		clearSelection();

		var c = document.getElementById('mygraphiccontainer');

		var x, y, width=50, height=50; // TODO don't hard code these numbers
		var canvasRect = c.getBoundingClientRect();
		x = ev.clientX - canvasRect.left;
		y = ev.clientY - canvasRect.top;

		if(data == "toolbox_table") {
			addTableComponent(x - dragOffsetX, y - dragOffsetY);toolbox_line
		}
		else if(data == "toolbox_line"){
			var component = RaphaelCanvas.rect(x - dragOffsetX, y - dragOffsetY, width, 1).attr({fill: "rgb(255, 255, 255)"});
			component.drag(compDragMove, compDragStart, compDragEnd);
			component.click(compClick);
			doBookkeepingForNewComponent(component, data);

			snapToGrid(component);
		}
		else if(data == "toolbox_label"){
			var component = RaphaelCanvas.rect(x - dragOffsetX, y - dragOffsetY, width, height).attr({fill: "rgb(255, 255, 255)",stroke:"#ffffff"});
			component.drag(compDragMove, compDragStart, compDragEnd);
			component.click(compClick);
			doBookkeepingForNewComponent(component, data);

			snapToGrid(component);
		}
		else {
			var component = RaphaelCanvas.rect(x - dragOffsetX, y - dragOffsetY, width, height).attr({fill: "rgb(255, 255, 255)"});
			component.drag(compDragMove, compDragStart, compDragEnd);
			component.click(compClick);
			doBookkeepingForNewComponent(component, data);

			snapToGrid(component);
		}
	}
	else {
		debug('Unknown dragged object: ' + data);
	}
}
function allowDrop(ev) {
	ev.preventDefault();
}
function compDragMove(dx, dy) {
	if(selectedComponents !== null && selectedComponents.length > 0)
		return; // If something is selected, this component should not be dragged.

	this.attr({x: this.ox + dx, y: this.oy + dy});
	doDynamicAlignment([this], true);
}
function compDragStart() {
	if(groupingHandles[this.data("groupingHandleId")] !== null) { // If this component is part of a group, don't allow it to be dragged on its own.
		compSelect(this);
		return;
	}

	beginUndoableAction();

	this.ox = this.attr("x");
    this.oy = this.attr("y");
	this.toFront();

	getAlignmentsFor([this]);
}
function compDragEnd() {
	if(selectedComponents !== null && selectedComponents.length > 0)
		return; // If this component is part of a group, don't allow it to be dragged on its own.

	if(!shifted) {
		compSelect(this);
	}

	xAlignmentLines = yAlignmentLines = null;
	clearDynamicAlignmentLines();
	snapToGrid(this);
}
function compClick() {
	if(shifted) {
		addComponentToSelection(this, true);
	}
	else {
		compSelect(this);
	}
}
function selectionDragMove(dx, dy) {
	selectionRect.attr({x: selectionRect.ox + dx, y: selectionRect.oy + dy});

	for(var i = 0; i < grabBoxes.length; i++) {
		grabBoxes[i].attr({x: grabBoxes[i].ox + dx, y: grabBoxes[i].oy + dy});
	}

	for(i = 0; i < selectedComponents.length; i++) {
		selectedComponents[i].attr({x: selectedComponents[i].ox + dx, y: selectedComponents[i].oy + dy});
	}

	doDynamicAlignment(selectedComponents, true);
}
function selectionDragStart() {
	beginUndoableAction();

	selectionRect.ox = selectionRect.attr("x");
	selectionRect.oy = selectionRect.attr("y");

	for(var i = 0; i < grabBoxes.length; i++) {
		grabBoxes[i].ox = grabBoxes[i].attr("x");
		grabBoxes[i].oy = grabBoxes[i].attr("y");
	}

	for(i = 0; i < selectedComponents.length; i++) {
		selectedComponents[i].ox = selectedComponents[i].attr("x");
		selectedComponents[i].oy = selectedComponents[i].attr("y");
	}

	getAlignmentsFor(selectedComponents);
}
function selectionDragEnd() {
	xAlignmentLines = yAlignmentLines = null;
	clearDynamicAlignmentLines();
	snapAllToGrid();
}
var GRABBOX_SIZE = 10;
var SPACE = 5; // pixels of space between a component and the selection bounding box
function compSelect(comp) {
	// clear previous selection
	clearSelection();
	selectedComponents = [ comp ];

	// "selection" is going to surround the selection object, with a small amount of space between the two boundaries
	var x = comp.attr("x")-SPACE;
	var y = comp.attr("y")-SPACE;
	var width = comp.attr("width")+SPACE*2;
	var height = comp.attr("height")+SPACE*2;

	// add selection bounding rectangle
	selectionRect = RaphaelCanvas.rect(x, y, width, height).attr({stroke: "rgb(69, 127, 214)", fill: "rgb(255, 255, 255)", 'fill-opacity': '0'}); // the transparent fill allows it to be draggable
	selectionRect.drag(selectionDragMove, selectionDragStart, selectionDragEnd);

	// add grab boxes
	grabBoxes = [
		RaphaelCanvas.rect(x-GRABBOX_SIZE, y-GRABBOX_SIZE, GRABBOX_SIZE, GRABBOX_SIZE), // upper left corner
		RaphaelCanvas.rect(x+width/2-GRABBOX_SIZE/2, y-GRABBOX_SIZE, GRABBOX_SIZE, GRABBOX_SIZE), // upper middle
		RaphaelCanvas.rect(x+width, y-GRABBOX_SIZE, GRABBOX_SIZE, GRABBOX_SIZE), // upper right corner
		RaphaelCanvas.rect(x+width, y+height/2-GRABBOX_SIZE/2, GRABBOX_SIZE, GRABBOX_SIZE), // right middle
		RaphaelCanvas.rect(x+width, y+height, GRABBOX_SIZE, GRABBOX_SIZE), // bottom right corner
		RaphaelCanvas.rect(x+width/2-GRABBOX_SIZE/2, y+height, GRABBOX_SIZE, GRABBOX_SIZE), // bottom middle
		RaphaelCanvas.rect(x-GRABBOX_SIZE, y+height, GRABBOX_SIZE, GRABBOX_SIZE), // bottom left corner
		RaphaelCanvas.rect(x-GRABBOX_SIZE, y+height/2-GRABBOX_SIZE/2, GRABBOX_SIZE, GRABBOX_SIZE) ]; // left middle
	for(var i = 0; i < grabBoxes.length; i++) {
		grabBoxes[i].attr({fill: "rgb(210, 228, 255)", stroke: "rgb(0, 92, 230)"}); // TODO make a striped pattern on the grab boxes
		grabBoxes[i].drag(grabBoxMove, grabBoxStart, grabBoxEnd);
	}

	// Also select any components that are grouped with a selected component
	expandSelectionForGrouping();

	if(selectedComponents.length > 1) {
		showMultiSelectPanel();
	}
	else if(selectedComponents.length == 1) {
		showSingleSelectPanel();
	}
	else {
		showNoSelectPanel();
	}
}
function rectSelect(rect) {
	debug('rectSelect');
	// clear previous selection
	clearSelection();

	// figure out which components are wholely within the selection rectangle
	selectedComponents = [];
	for(var i = 0; i < zOrderedComponents.length; i++) {
		comp = zOrderedComponents[i];
		if(comp != null) {
			if((comp.attr("x") >= rect.x) && ((comp.attr("x") + comp.attr("width")) <= (rect.x + rect.width)) && (comp.attr("y") >= rect.y) && ((comp.attr("y") + comp.attr("height") <= (rect.y + rect.height)))) {
				selectedComponents.push(comp);
			}
		}
	}

	var x = rect.x;
	var y = rect.y;
	var width = rect.width;
	var height = rect.height;

	// add selection bounding rectangle
	selectionRect = RaphaelCanvas.rect(x, y, width, height).attr({stroke: "#ffffff", fill: "rgb(255, 255, 255)", 'fill-opacity': '0'}); // the transparent fill allows it to be draggable
	selectionRect.drag(selectionDragMove, selectionDragStart, selectionDragEnd);

	// add grab boxes
	grabBoxes = [
		RaphaelCanvas.rect(x-GRABBOX_SIZE, y-GRABBOX_SIZE, GRABBOX_SIZE, GRABBOX_SIZE), // upper left corner
		RaphaelCanvas.rect(x+width/2-GRABBOX_SIZE/2, y-GRABBOX_SIZE, GRABBOX_SIZE, GRABBOX_SIZE), // upper middle
		RaphaelCanvas.rect(x+width, y-GRABBOX_SIZE, GRABBOX_SIZE, GRABBOX_SIZE), // upper right corner
		RaphaelCanvas.rect(x+width, y+height/2-GRABBOX_SIZE/2, GRABBOX_SIZE, GRABBOX_SIZE), // right middle
		RaphaelCanvas.rect(x+width, y+height, GRABBOX_SIZE, GRABBOX_SIZE), // bottom right corner
		RaphaelCanvas.rect(x+width/2-GRABBOX_SIZE/2, y+height, GRABBOX_SIZE, GRABBOX_SIZE), // bottom middle
		RaphaelCanvas.rect(x-GRABBOX_SIZE, y+height, GRABBOX_SIZE, GRABBOX_SIZE), // bottom left corner
		RaphaelCanvas.rect(x-GRABBOX_SIZE, y+height/2-GRABBOX_SIZE/2, GRABBOX_SIZE, GRABBOX_SIZE) ]; // left middle
	for(i = 0; i < grabBoxes.length; i++) {
		grabBoxes[i].attr({fill: "rgb(255,255,255)"}); // TODO make a striped pattern on the grab boxes
		grabBoxes[i].drag(grabBoxMove, grabBoxStart, grabBoxEnd);
	}

	// Also select any components that are grouped with a selected component
	expandSelectionForGrouping();

	if(selectedComponents.length > 1) {
		showMultiSelectPanel();
	}
	else if(selectedComponents.length == 1) {
		showSingleSelectPanel();
	}
	else {
		showNoSelectPanel();
	}
}
function addComponentToSelection(comp, doExpandSelectionForGrouping) { // NOTE: outside code should always set the second parameter to true
	if(selectionRect === null || selectedComponents === null || selectedComponents.length === 0) {
		compSelect(comp);
		return;
	}

	selectedComponents.push(comp);

	// increase size of selection bounding rectangle as necessary to hold the newly selected component
	var newLeft = min(selectionRect.attr("x"), comp.attr("x") - SPACE);
	var newTop = min(selectionRect.attr("y"), comp.attr("y") - SPACE);
	var newRight = max(selectionRect.attr("x") + selectionRect.attr("width"), comp.attr("x") + comp.attr("width") + SPACE);
	var newBottom = max(selectionRect.attr("y") + selectionRect.attr("height"), comp.attr("y") + comp.attr("height") + SPACE);
	selectionRect.attr({x: newLeft, y: newTop, width: newRight - newLeft, height: newBottom - newTop});
	selectionRect.toFront();

	var x = newLeft, y = newTop, width = newRight - newLeft, height = newBottom - newTop;
	// add grab boxes
	grabBoxes[0].attr({x: x-GRABBOX_SIZE, y: y-GRABBOX_SIZE}); // upper left corner
	grabBoxes[1].attr({x: x+width/2-GRABBOX_SIZE/2, y: y-GRABBOX_SIZE}); // upper middle
	grabBoxes[2].attr({x: x+width, y: y-GRABBOX_SIZE}); // upper right corner
	grabBoxes[3].attr({x: x+width, y: y+height/2-GRABBOX_SIZE/2}); // right middle
	grabBoxes[4].attr({x: x+width, y: y+height}); // bottom right corner
	grabBoxes[5].attr({x: x+width/2-GRABBOX_SIZE/2, y: y+height}); // bottom middle
	grabBoxes[6].attr({x: x-GRABBOX_SIZE, y: y+height}); // bottom left corner
	grabBoxes[7].attr({x: x-GRABBOX_SIZE, y: y+height/2-GRABBOX_SIZE/2}); // left middle

	// Also select any components that are grouped with a selected component
	if(doExpandSelectionForGrouping) {
		expandSelectionForGrouping();
	}

	if(selectedComponents.length > 1) {
		showMultiSelectPanel();
	}
	else if(selectedComponents.length == 1) {
		showSingleSelectPanel();
	}
	else {
		showNoSelectPanel();
	}
}
function expandSelectionForGrouping() {
	var copyOfSelectedComponents = selectedComponents.slice(0);
	for(var i = 0; i < copyOfSelectedComponents.length; i++) {
		var selected = copyOfSelectedComponents[i];
		var groupingId = groupingHandles[selected.data("groupingHandleId")];
		if(groupingId !== null) {
			var groupingIdStack = [ groupingId ];
			while(groupingIdStack.length > 0) {
				groupingId = groupingIdStack.pop();
				var components = groupingForest[groupingId].components;
				var subgroupings = groupingForest[groupingId].subgroupings;
				for(var j = 0; j < components.length; j++) {
					addComponentToSelection(components[j], false);
				}
				for(j = 0; j < subgroupings.length; j++) {
					groupingIdStack.push(subgroupings[j]);
				}
			}
		}
	}
}

var noSelectPanelText = null; // null iff the single-select panel is currently being shown; otherwise its text will be saved here
function showMultiSelectPanel() {
	if(noSelectPanelText === null) {
		noSelectPanelText = document.getElementById("rightpanel").innerHTML;
	}
	document.getElementById("rightpanel").innerHTML =
			'<div>'+
			'<p align="center">Grouping</p>'+
			'<p align="center"><input type="button" value="Group" onclick="groupSelected();">'+
			'<input type="button" value="Ungroup" onclick="ungroupSelected();"></p>'+
			'<hr/>'+
			'<p align="center">Alignment</p>'+
			'<p align="center"><input type="button" value="Left" onclick="alignSelectedLeft();">'+
			'<input type="button" value="Center Horz." onclick="alignSelectedCenterHorz();">'+
			'<input type="button" value="Right" onclick="alignSelectedRight();"></p>'+
			'<p align="center"><input type="button" value="Top" onclick="alignSelectedTop();">'+
			'<input type="button" value="Center Vert." onclick="alignSelectedCenterVert();">'+
			'<input type="button" value="Bottom" onclick="alignSelectedBottom();"></p>'+
			'<hr/>'+
			'<p align="center">Distribute Spacing</p>'+
			'<p align="center"><input type="button" value="Dist. Horz." onclick="distributeSelectedHorizontally();">'+
			'<input type="button" value="Dist. Vert." onclick="distributeSelectedVertically();"></p>'+
			'</div>';
}
function showSingleSelectPanel() {
	var currentName, currentCaption;
	if(!selectedComponents || selectedComponents.length != 1) {
		return; // not exactly one component is selected, so showing this panel would be inappropriate
	}
	currentName = ctatComponents[selectedComponents[0].data("ctatComponentIndex")].getName();
	currentCaption = ctatComponents[selectedComponents[0].data("ctatComponentIndex")].getText();

	if(noSelectPanelText === null) {
		noSelectPanelText = document.getElementById("rightpanel").innerHTML;
	}

	// If this is a button, there needs to be some button-specific options (change to Done button, change to Hint button)
	var doneButtonCheckbox;
	var className = ctatComponents[selectedComponents[0].data("ctatComponentIndex")].getClassName();
	if(className == "CTATButton") {
		doneButtonCheckbox = '<hr/><p align="center"><input type="checkbox" id="makeDoneButton" onchange="toggleDoneButton();">Done button<br/><input type="checkbox" id="makeHintButton" onchange="toggleHintButton();">Hint button</p>';
	}
	else if(className == "CTATDoneButton") {
		doneButtonCheckbox = '<hr/><p align="center"><input type="checkbox" id="makeDoneButton" checked="true" onchange="toggleDoneButton();">Done button<br/><input type="checkbox" id="makeHintButton" onchange="toggleHintButton();">Hint button</p>';
	}
	else if(className == "CTATHintButton") {
		doneButtonCheckbox = '<hr/><p align="center"><input type="checkbox" id="makeDoneButton" onchange="toggleDoneButton();">Done button<br/><input type="checkbox" id="makeHintButton" checked="true" onchange="toggleHintButton();">Hint button</p>';
	}
	else {
		doneButtonCheckbox = ""; // not a button, so there shouldn't be an option to turn it into a done button
	}

	// If this is a drop-down box, the user needs to be able to modify the choices
	var captionHtml;
	if(className == "CTATComboBox") {
		captionHtml = getCaptionHtmlForDropdownBox(selectedComponents[0]);
	}
	else {
		captionHtml = '<p align="center"><input type="text" id="newCaption" value="'+currentCaption+'"></p>'+
			'<p align="center"><input type="button" value="Change Caption" onclick="changeSelectedComponentCaption(document.getElementById(\'newCaption\').value);"></p>';
	}

	document.getElementById("rightpanel").innerHTML =
			'<div>'+
			'<p align="center">Component Name</p>'+
			'<p align="center"><input type="text" id="newName" value="'+currentName+'"></p>'+
			'<p align="center"><input type="button" value="Change Name" onclick="changeSelectedComponentName(document.getElementById(\'newName\').value);"></p>'+
			'<hr/>'+
			'<p align="center">Caption</p>'+
			captionHtml+
			doneButtonCheckbox+
			'</div>';
}
function showNoSelectPanel() {
	if(noSelectPanelText !== null) {
		document.getElementById("rightpanel").innerHTML = noSelectPanelText;
		noSelectPanelText = null;
	}
}

// called by showSingleSelectPanel() when the selected component is a dropdown box. This function returns the specialized HTML that presents the items in the dropdown and allows their modification
function getCaptionHtmlForDropdownBox(component) {
	var ctatcompindex = component.data("ctatComponentIndex");
	var ctatcomp = ctatComponents[ctatcompindex];

	// TODO the rest of this function
	var htmlcomp = ctatcomp.getHTMLComponent();
	var str = '<p align="center">';//"This is a dropdown box. Its name is " + ctatcomp.getName() + ". Its " + htmlcomp.children.length + " entries are ";
	for(var i = 0; i < htmlcomp.children.length; i++) {
		str = str + htmlcomp.children[i].getAttribute("value") + "<br>";
	}
	str = str + ".";

	str = str + '<input type="button" value="Add" onclick="temp_add('+ctatcompindex+');">';
	str = str + '</p>';

	return str;
}
function temp_add(ctatcompindex) { // TODO
	var ctatcomp = ctatComponents[ctatcompindex];
	var text = window.prompt("What option would you like to add?");
	if(text) {
		ctatcomp.addItem(text);
		showSingleSelectPanel();
	}
}

// changes the name of the selected CTAT component. Returns true for success, false for failure.
function changeSelectedComponentName(newName) {
	var success = false;
	if(selectedComponents && selectedComponents.length == 1) {
		if(nameIsUnique(newName)) {
			beginUndoableAction();
			ctatComponents[selectedComponents[0].data("ctatComponentIndex")].setName(newName);
			success = true;
		}
		showSingleSelectPanel(); // refresh the properties panel to show the change or lack of change
	}

	modified = modified || success;

	return success;
}
// changes the caption of the selected CTAT component. Returns true for success, false for failure.
function changeSelectedComponentCaption(newCaption) {
	var success = false;
	if(selectedComponents && selectedComponents.length == 1) {
		beginUndoableAction();
		var ctatComponent = ctatComponents[selectedComponents[0].data("ctatComponentIndex")];
		ctatComponent.setText(newCaption);
		success = true;
		showSingleSelectPanel(); // refresh the properties panel to show the change or lack of change
	}

	modified = modified || success;

	return success;
}

/** Changes the currently selected component from a normal button to a done button or vice versa. */
function toggleDoneButton() {
	if(selectedComponents == null || selectedComponents.length != 1) {
		return;
	}

	beginUndoableAction();
	var comp = selectedComponents[0];

	if(document.getElementById("makeDoneButton").checked) {
		// first, remove the existing CTAT component
		var ctatComp = ctatComponents[comp.data("ctatComponentIndex")];
		var name = "done";
		var div = ctatComp.getDivWrap();
		div.parentNode.removeChild(div);
		ctatComponents[comp.data("ctatComponentIndex")] = null;

		// then create a done button and put that in the old component's place
		var dummyDescription=new Object ();
		dummyDescription.x=comp.attr("x") + getCanvasOffsetX();
		dummyDescription.y=comp.attr("y") + getCanvasOffsetY();
		dummyDescription.width=comp.attr("width");
		dummyDescription.height=comp.attr("height");
		dummyDescription.type="CTATDoneButton";
		var done=new CTATImageButton (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		done.setName("done");
		done.setClassName ("CTATDoneButton");
		done.assignImages (doneDefault,doneHover,doneClick,doneDisabled);
		done.assignImages ("src/images/done_button.png",
								   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Hover.png",
								   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Click.png",
								   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Disabled.png");
		addComponent (done);
		ctatComponents[comp.data("ctatComponentIndex")] = done;
	}
	else {
		// first, remove the existing CTAT component
		ctatComp = ctatComponents[comp.data("ctatComponentIndex")];
		name = getUniqueName("button");
		div = ctatComp.getDivWrap();
		div.parentNode.removeChild(div);
		ctatComponents[comp.data("ctatComponentIndex")] = null;

		// then create a button and put that in the old component's place
		dummyDescription=new Object ();
		dummyDescription.name=name;
		dummyDescription.type="CTATButton";
		dummyDescription.styles = [{styleName: "labelText", styleValue: ""}];
		dummyDescription.x=comp.attr("x") + getCanvasOffsetX();
		dummyDescription.y=comp.attr("y") + getCanvasOffsetY();
		dummyDescription.width=comp.attr("width");
		dummyDescription.height=comp.attr("height");
		dummyDescription.type="CTATButton";
		var btn=new CTATButton (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		btn.setName(name);
		btn.setClassName("CTATButton");
		btn.setText("");
		addComponent (btn);
		ctatComponents[comp.data("ctatComponentIndex")] = btn;
	}

	modified = true;
	showSingleSelectPanel();
}
/** Changes the currently selected component from a normal button to a hint button or vice versa. */
function toggleHintButton() {
	if(selectedComponents == null || selectedComponents.length != 1) {
		return;
	}

	beginUndoableAction();
	var comp = selectedComponents[0];

	if(document.getElementById("makeHintButton").checked) {
		// first, remove the existing CTAT component
		var ctatComp = ctatComponents[comp.data("ctatComponentIndex")];
		var name = "done";
		var div = ctatComp.getDivWrap();
		div.parentNode.removeChild(div);
		ctatComponents[comp.data("ctatComponentIndex")] = null;

		// then create a hint button and put that in the old component's place
		var dummyDescription=new Object ();
		dummyDescription.x=comp.attr("x") + getCanvasOffsetX();
		dummyDescription.y=comp.attr("y") + getCanvasOffsetY();
		dummyDescription.width=comp.attr("width");
		dummyDescription.height=comp.attr("height");
		dummyDescription.type="CTATHintButton";
		var hint=new CTATImageButton (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		hint.setName ("hint");
		hint.setClassName ("CTATHintButton");
		//hint.assignImages (hintDefault,hintHover,hintClick,hintDisabled);

		hint.assignImages ("src/images/hint_button.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Hover.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Click.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Disabled.png");

		addComponent (hint);
		ctatComponents[comp.data("ctatComponentIndex")] = hint;
	}
	else {
		// first, remove the existing CTAT component
		ctatComp = ctatComponents[comp.data("ctatComponentIndex")];
		name = getUniqueName("button");
		div = ctatComp.getDivWrap();
		div.parentNode.removeChild(div);
		ctatComponents[comp.data("ctatComponentIndex")] = null;

		// then create a button and put that in the old component's place
		dummyDescription=new Object ();
		dummyDescription.name=name;
		dummyDescription.type="CTATButton";
		dummyDescription.styles = [{styleName: "labelText", styleValue: ""}];
		dummyDescription.x=comp.attr("x") + getCanvasOffsetX();
		dummyDescription.y=comp.attr("y") + getCanvasOffsetY();
		dummyDescription.width=comp.attr("width");
		dummyDescription.height=comp.attr("height");
		dummyDescription.type="CTATButton";
		var btn=new CTATButton (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		btn.setName(name);
		btn.setClassName("CTATButton");
		btn.setText("");
		addComponent (btn);
		ctatComponents[comp.data("ctatComponentIndex")] = btn;
	}

	modified = true;
	showSingleSelectPanel();
}

function nameIsUnique(name) {
	var isUnique = true;
	for(var i = 0; i < ctatComponents.length; i++) {
		if(ctatComponents[i] === null) continue;

		if(name == ctatComponents[i].getName()) {
			isUnique = false;
			break;
		}
	}
	if(isUnique) {
		for(var i = 0; i < groupingForest.length; i++) {
			if(groupingForest[i] === null || groupingForest[i].ungrouped) continue;

			if(name == groupingForest[i].name) {
				isUnique = false;
				break;
			}
		}
	}
	return isUnique;
}
function getUniqueName(prefix) {
	// get all the existing names, of both components and groupings, that start with the prefix
	var namesWithPrefix = [];
	for(var i = 0; i < ctatComponents.length; i++) {
		if(ctatComponents[i] === null) continue;

		var thisName = ctatComponents[i].getName();
		if(thisName.substring(0, prefix.length) === prefix) {
			namesWithPrefix.push(thisName);
		}
	}
	for(var i = 0; i < groupingForest.length; i++) {
		if(groupingForest[i] === null || groupingForest[i].ungrouped) continue;

		var thisName = groupingForest[i].name;
		if(thisName.substring(0, prefix.length) === prefix) {
			namesWithPrefix.push(thisName);
		}
	}

	// find a unique name by trying prefix1, prefix2, etc.
	var i = 1;
	var name;
	for(var i = 1; ; i++) {
		name = prefix + "" + i;
		var isUnique = true;
		for(var j = 0; j < namesWithPrefix.length; j++) {
			if(name == namesWithPrefix[j]) {
				isUnique = false;
				break;
			}
		}
		if(isUnique) {
			return name;
		}
	}
}

var draggingGrabBoxIndex = null;
function grabBoxMove(dx, dy) {
	// depending on which grab box is being dragged, do these things: resize selection box, move grab boxes, resize selected components.
	switch(draggingGrabBoxIndex) {
	case 0: // upper left corner
		var upperSide = min(selectionRect.oTop + dy, selectionRect.oBottom - 1); // keep upper side at least 1 pixel above lower side
		var leftSide = min(selectionRect.oLeft + dx, selectionRect.oRight - 1); // keep left side at least 1 pixel left of right side
		var new_dx = leftSide - selectionRect.oLeft;
		var new_dy = upperSide - selectionRect.oTop;

		selectionRect.attr({x: leftSide, y: upperSide, width: selectionRect.oWidth-new_dx, height: selectionRect.oHeight-new_dy});

		grabBoxes[0].attr({x: leftSide-GRABBOX_SIZE, y: upperSide-GRABBOX_SIZE});
		grabBoxes[1].attr({x: leftSide+selectionRect.attr("width")/2-GRABBOX_SIZE/2, y: upperSide-GRABBOX_SIZE});
		grabBoxes[2].attr({y: upperSide-GRABBOX_SIZE});
		grabBoxes[3].attr({y: upperSide+selectionRect.attr("height")/2-GRABBOX_SIZE/2});
		grabBoxes[4].attr({});
		grabBoxes[5].attr({x: leftSide+selectionRect.attr("width")/2-GRABBOX_SIZE/2});
		grabBoxes[6].attr({x: leftSide-GRABBOX_SIZE});
		grabBoxes[7].attr({x: leftSide-GRABBOX_SIZE, y: upperSide+selectionRect.attr("height")/2-GRABBOX_SIZE/2});

		for(var i = 0; i < selectedComponents.length; i++) {
			var comp = selectedComponents[i];

			var propX = selectionRect.attr("width") / selectionRect.oWidth; // proportion by which the x size has been increased (a negative dx means an increase in width)
			var propY = selectionRect.attr("height") / selectionRect.oHeight; // proportion by which the y size has been increased (a negative dy means an increase in height)

			var newX = leftSide + propX * comp.oLeftRelative;
			var newY = upperSide + propY * comp.oTopRelative;
			var newWidth = propX * comp.oWidth;
			var newHeight = propY * comp.oHeight;
			comp.attr({x: newX, y: newY, width: newWidth, height: newHeight});
		}

		break;
	case 1: // upper side
		upperSide = min(selectionRect.oTop + dy, selectionRect.oBottom - 1); // keep upper side at least 1 pixel above lower side
		new_dy = upperSide - selectionRect.oTop;

		selectionRect.attr({y: upperSide, height: selectionRect.oHeight-new_dy});

		grabBoxes[0].attr({y: upperSide-GRABBOX_SIZE});
		grabBoxes[1].attr({y: upperSide-GRABBOX_SIZE});
		grabBoxes[2].attr({y: upperSide-GRABBOX_SIZE});
		grabBoxes[3].attr({y: upperSide+selectionRect.attr("height")/2-GRABBOX_SIZE/2});
		grabBoxes[4].attr({});
		grabBoxes[5].attr({});
		grabBoxes[6].attr({});
		grabBoxes[7].attr({y: upperSide+selectionRect.attr("height")/2-GRABBOX_SIZE/2});

		for(i = 0; i < selectedComponents.length; i++) {
			comp = selectedComponents[i];

			propY = selectionRect.attr("height") / selectionRect.oHeight; // proportion by which the y size has been increased (a negative dy means an increase in height)

			newY = upperSide + propY * comp.oTopRelative;
			newHeight = propY * comp.oHeight;
			comp.attr({y: newY, height: newHeight});
		}

		break;
	case 2: // upper right corner
		upperSide = min(selectionRect.oTop + dy, selectionRect.oBottom - 1); // keep upper side at least 1 pixel above lower side
		var rightSide = max(selectionRect.oRight + dx, selectionRect.oLeft + 1); // keep right side at least 1 pixel right of left side
		new_dx = rightSide - selectionRect.oRight;
		new_dy = upperSide - selectionRect.oTop;

		selectionRect.attr({y: upperSide, width: selectionRect.oWidth+new_dx, height: selectionRect.oHeight-new_dy});

		grabBoxes[0].attr({y: upperSide-GRABBOX_SIZE});
		grabBoxes[1].attr({x: rightSide-selectionRect.attr("width")/2-GRABBOX_SIZE/2, y: upperSide-GRABBOX_SIZE});
		grabBoxes[2].attr({x: rightSide, y: upperSide-GRABBOX_SIZE});
		grabBoxes[3].attr({x: rightSide, y: upperSide+selectionRect.attr("height")/2-GRABBOX_SIZE/2});
		grabBoxes[4].attr({x: rightSide});
		grabBoxes[5].attr({x: rightSide-selectionRect.attr("width")/2-GRABBOX_SIZE/2});
		grabBoxes[6].attr({});
		grabBoxes[7].attr({y: upperSide+selectionRect.attr("height")/2-GRABBOX_SIZE/2});

		for(i = 0; i < selectedComponents.length; i++) {
			comp = selectedComponents[i];

			propX = selectionRect.attr("width") / selectionRect.oWidth; // proportion by which the x size has been increased (a positive dx means an increase in width)
			propY = selectionRect.attr("height") / selectionRect.oHeight; // proportion by which the y size has been increased (a negative dy means an increase in height)

			newX = selectionRect.attr("x") + propX * comp.oLeftRelative;
			newY = upperSide + propY * comp.oTopRelative;
			newWidth = propX * comp.oWidth;
			newHeight = propY * comp.oHeight;
			comp.attr({x: newX, y: newY, width: newWidth, height: newHeight});
		}

		break;
	case 3: // right side
		rightSide = max(selectionRect.oRight + dx, selectionRect.oLeft + 1); // keep right side at least 1 pixel right of left side
		new_dx = rightSide - selectionRect.oRight;

		selectionRect.attr({width: selectionRect.oWidth+new_dx});

		grabBoxes[0].attr({});
		grabBoxes[1].attr({x: rightSide-selectionRect.attr("width")/2-GRABBOX_SIZE/2});
		grabBoxes[2].attr({x: rightSide});
		grabBoxes[3].attr({x: rightSide});
		grabBoxes[4].attr({x: rightSide});
		grabBoxes[5].attr({x: rightSide-selectionRect.attr("width")/2-GRABBOX_SIZE/2});
		grabBoxes[6].attr({});
		grabBoxes[7].attr({});

		for(i = 0; i < selectedComponents.length; i++) {
			comp = selectedComponents[i];

			propX = selectionRect.attr("width") / selectionRect.oWidth; // proportion by which the x size has been increased (a positive dx means an increase in width)

			newX = selectionRect.attr("x") + propX * comp.oLeftRelative;
			newWidth = propX * comp.oWidth;
			comp.attr({x: newX, width: newWidth});
		}

		break;
	case 4: // lower right corner
		var lowerSide = max(selectionRect.oBottom + dy, selectionRect.oTop + 1); // keep lower side at least 1 pixel below upper side
		rightSide = max(selectionRect.oRight + dx, selectionRect.oLeft + 1); // keep right side at least 1 pixel right of left side
		new_dx = rightSide - selectionRect.oRight;
		new_dy = lowerSide - selectionRect.oBottom;

		selectionRect.attr({width: selectionRect.oWidth+new_dx, height: selectionRect.oHeight+new_dy});

		grabBoxes[0].attr({});
		grabBoxes[1].attr({x: rightSide-selectionRect.attr("width")/2-GRABBOX_SIZE/2});
		grabBoxes[2].attr({x: rightSide});
		grabBoxes[3].attr({x: rightSide, y: lowerSide-selectionRect.attr("height")/2-GRABBOX_SIZE/2});
		grabBoxes[4].attr({x: rightSide, y: lowerSide});
		grabBoxes[5].attr({x: rightSide-selectionRect.attr("width")/2-GRABBOX_SIZE/2, y: lowerSide});
		grabBoxes[6].attr({y: lowerSide});
		grabBoxes[7].attr({y: lowerSide-selectionRect.attr("height")/2-GRABBOX_SIZE/2});

		for(i = 0; i < selectedComponents.length; i++) {
			comp = selectedComponents[i];

			propX = selectionRect.attr("width") / selectionRect.oWidth; // proportion by which the x size has been increased (a positive dx means an increase in width)
			propY = selectionRect.attr("height") / selectionRect.oHeight; // proportion by which the y size has been increased (a positive dy means an increase in height)

			newX = selectionRect.attr("x") + propX * comp.oLeftRelative;
			newY = selectionRect.attr("y") + propY * comp.oTopRelative;
			newWidth = propX * comp.oWidth;
			newHeight = propY * comp.oHeight;
			comp.attr({x: newX, y: newY, width: newWidth, height: newHeight});
		}

		break;
	case 5: // lower side
		lowerSide = max(selectionRect.oBottom + dy, selectionRect.oTop + 1); // keep lower side at least 1 pixel below upper side
		new_dy = lowerSide - selectionRect.oBottom;

		selectionRect.attr({height: selectionRect.oHeight+new_dy});

		grabBoxes[0].attr({});
		grabBoxes[1].attr({});
		grabBoxes[2].attr({});
		grabBoxes[3].attr({y: lowerSide-selectionRect.attr("height")/2-GRABBOX_SIZE/2});
		grabBoxes[4].attr({y: lowerSide});
		grabBoxes[5].attr({y: lowerSide});
		grabBoxes[6].attr({y: lowerSide});
		grabBoxes[7].attr({y: lowerSide-selectionRect.attr("height")/2-GRABBOX_SIZE/2});

		for(i = 0; i < selectedComponents.length; i++) {
			comp = selectedComponents[i];

			propY = selectionRect.attr("height") / selectionRect.oHeight; // proportion by which the y size has been increased (a positive dy means an increase in height)

			newY = selectionRect.attr("y") + propY * comp.oTopRelative;
			newHeight = propY * comp.oHeight;
			comp.attr({y: newY, height: newHeight});
		}

		break;
	case 6: // lower left corner
		lowerSide = max(selectionRect.oBottom + dy, selectionRect.oTop + 1); // keep lower side at least 1 pixel below upper side
		leftSide = min(selectionRect.oLeft + dx, selectionRect.oRight - 1); // keep left side at least 1 pixel left of right side
		new_dx = leftSide - selectionRect.oLeft;
		new_dy = lowerSide - selectionRect.oBottom;

		selectionRect.attr({x: leftSide, width: selectionRect.oWidth-new_dx, height: selectionRect.oHeight+new_dy});

		grabBoxes[0].attr({x: leftSide-GRABBOX_SIZE});
		grabBoxes[1].attr({x: leftSide+selectionRect.attr("width")/2-GRABBOX_SIZE/2});
		grabBoxes[2].attr({});
		grabBoxes[3].attr({y: lowerSide-selectionRect.attr("height")/2-GRABBOX_SIZE/2});
		grabBoxes[4].attr({y: lowerSide});
		grabBoxes[5].attr({x: leftSide+selectionRect.attr("width")/2-GRABBOX_SIZE/2, y: lowerSide});
		grabBoxes[6].attr({x: leftSide-GRABBOX_SIZE, y: lowerSide});
		grabBoxes[7].attr({x: leftSide-GRABBOX_SIZE, y: lowerSide-selectionRect.attr("height")/2-GRABBOX_SIZE/2});

		for(i = 0; i < selectedComponents.length; i++) {
			comp = selectedComponents[i];

			propX = selectionRect.attr("width") / selectionRect.oWidth; // proportion by which the x size has been increased (a negative dx means an increase in width)
			propY = selectionRect.attr("height") / selectionRect.oHeight; // proportion by which the y size has been increased (a positive dy means an increase in height)

			newX = leftSide + propX * comp.oLeftRelative;
			newY = selectionRect.attr("y") + propY * comp.oTopRelative;
			newWidth = propX * comp.oWidth;
			newHeight = propY * comp.oHeight;
			comp.attr({x: newX, y: newY, width: newWidth, height: newHeight});
		}

		break;
	case 7: // left side
		leftSide = min(selectionRect.oLeft + dx, selectionRect.oRight - 1); // keep left side at least 1 pixel left of right side
		new_dx = leftSide - selectionRect.oLeft;

		selectionRect.attr({x: leftSide, width: selectionRect.oWidth-new_dx});

		grabBoxes[0].attr({x: leftSide-GRABBOX_SIZE});
		grabBoxes[1].attr({x: leftSide+selectionRect.attr("width")/2-GRABBOX_SIZE/2});
		grabBoxes[2].attr({});
		grabBoxes[3].attr({});
		grabBoxes[4].attr({});
		grabBoxes[5].attr({x: leftSide+selectionRect.attr("width")/2-GRABBOX_SIZE/2});
		grabBoxes[6].attr({x: leftSide-GRABBOX_SIZE});
		grabBoxes[7].attr({x: leftSide-GRABBOX_SIZE});

		for(i = 0; i < selectedComponents.length; i++) {
			comp = selectedComponents[i];

			propX = selectionRect.attr("width") / selectionRect.oWidth; // proportion by which the x size has been increased (a negative dx means an increase in width)

			newX = leftSide + propX * comp.oLeftRelative;
			newWidth = propX * comp.oWidth;
			comp.attr({x: newX, width: newWidth});
		}

		break;
	default:
		debug("Unknown grab box id: "+draggingGrabBoxIndex);
		break;
	}
	doDynamicAlignment(selectedComponents, true);
}
function grabBoxStart() {
	beginUndoableAction();

	this.ox = this.attr("x");
	this.oy = this.attr("y");

	selectionRect.oLeft = selectionRect.attr("x");
	selectionRect.oTop = selectionRect.attr("y");
	selectionRect.oRight = selectionRect.attr("x") + selectionRect.attr("width");
	selectionRect.oBottom = selectionRect.attr("y") + selectionRect.attr("height");
	selectionRect.oWidth = selectionRect.attr("width");
	selectionRect.oHeight = selectionRect.attr("height");

	// for each selected component, remember its left, top, right, bottom (for moving/resizing purposes)
	for(var i = 0; i < selectedComponents.length; i++) {
		comp = selectedComponents[i];
		comp.oLeft = comp.attr("x");
		comp.oTop = comp.attr("y");
		comp.oRight = comp.attr("x") + comp.attr("width");
		comp.oBottom = comp.attr("y") + comp.attr("height");
		comp.oWidth = comp.attr("width");
		comp.oHeight = comp.attr("height");
		comp.oLeftRelative = comp.attr("x") - selectionRect.attr("x"); // left coordinate, relative to selection bounding box
		comp.oTopRelative = comp.attr("y") - selectionRect.attr("y"); // top coordinate, relative to selection bounding box
	}

	for(i = 0; i < grabBoxes.length; i++) {
		if(grabBoxes[i] === this) {
			draggingGrabBoxIndex = i;
			break;
		}
	}

	getAlignmentsFor(selectedComponents);
}
function grabBoxEnd() {
	draggingGrabBoxIndex = null;
	xAlignmentLines = yAlignmentLines = null;
	clearDynamicAlignmentLines();
	snapAllToGrid();
}
function clearSelection() {
	debug('clearSelection()');
	selectedComponents = [] ;

	if(selectionRect) {
		selectionRect.remove();
		selectionRect = null;
	}

	if(grabBoxes) {
		for(var i = 0; i < grabBoxes.length; i++) {
			grabBoxes[i].remove();
		}
		grabBoxes = [];
	}

	showNoSelectPanel();
}

/** Delete all selected components. NOT the same as clearSelection, which just deselects stuff */
function deleteSelection() {
	beginUndoableAction();
	for(var i = 0; i < selectedComponents.length; i++) {
		var comp = selectedComponents[i];

		// Find the index of this component in the master list
		var found = false;
		for(var j = 0; j < zOrderedComponents.length; j++) {
			if(zOrderedComponents[j] === comp) {
				found = true;
				break;
			}
		}

		// Delete the component with that index
		if(found) {
			deleteComponentByIndex(j);
		}
	}
	clearSelection();
}
function deleteComponentByIndex(index) {
	var comp = zOrderedComponents[index];
	if(comp !== null) {
		zOrderedComponents[index] = null;
		if(comp.data("ctatComponentIndex")) {
			var ctatComp = ctatComponents[comp.data("ctatComponentIndex")];
			var div = ctatComp.getDivWrap();
			div.parentNode.removeChild(div);
			ctatComponents[comp.data("ctatComponentIndex")] = null;
		}
		comp.remove();
		modified = true;
	}
}

function clearCanvas() {
	beginUndoableAction();
	clearSelection();

	// delete the components one by one
	for(var i = 0; i < zOrderedComponents.length; i++) {
		deleteComponentByIndex(i);
	}

	// delete all the "grouping boxes"
	for(var i = 0; i < groupingForest.length; i++) {
		var box = groupingForest[i].box;
		if(box) {
			box.remove();
		}
	}

	// shrink the arrays to avoid wasting space
	zOrderedComponents = [];
	groupingHandles = [];
	groupingForest = [];
}

var clipboard = null;

/** Copy selected components to an internal clipboard */
function copySelection() {
	if(selectedComponents.length == 0) {
		return;
	}

	clipboard = {};
	var indices = [];
	for(var i = 0; i < selectedComponents.length; i++) {
		for(var j = 0; j < zOrderedComponents.length; j++) {
			if(selectedComponents[i] === zOrderedComponents[j]) {
				indices.push(j);
				break;
			}
		}
	}
	indices.sort(function(a, b){return a-b;}); // sort indices ascending because we want to preserve the relative z-ordering of the copied components

	clipboard.zOrderedCtatComponents = [];
	for(var i = 0; i < indices.length; i++) {
		var thisCtatComponent = getCtatComponentInfo(ctatComponents[zOrderedComponents[indices[i]].data("ctatComponentIndex")]);
		thisCtatComponent.x = (thisCtatComponent.x - selectionRect.attr("x")); // x and y are relative to the selection bounding box, because we don't want to paste them overtop of where they already are
		thisCtatComponent.y = (thisCtatComponent.y - selectionRect.attr("y"));
		clipboard.zOrderedCtatComponents.push(thisCtatComponent);
		debug("copying " + JSON.stringify(thisCtatComponent));
	}

	clipboard.groupings = getGroupingsAmong(indices);
}
/** Paste from internal clipboard */
function paste() {
	if(clipboard) {
		beginUndoableAction();

		// add components from clipboard to
		var componentsToAdd = clipboard.zOrderedCtatComponents;
		for(var i = 0; i < componentsToAdd.length; i++) {
			var thisComponentDescription = componentsToAdd[i];
			addNewComponentInternal(thisComponentDescription);
		}

		// handle groupings
		for(var i = 0; i < clipboard.groupings.length; i++) {
			var groupingTree = clipboard.groupings[i];
			groupAccordingToTree(groupingTree, zOrderedComponents.slice(-(clipboard.zOrderedCtatComponents.length)));
		}
		clearSelection();
	}

	snapAllToGrid();

	modified = true;
}

function groupAccordingToTree(groupingTreeOfIndices, componentArray) {
	var componentsToGroup = [];
	for(var i = 0; i < groupingTreeOfIndices.length; i++) {
		var thisElement = groupingTreeOfIndices[i];
		if(thisElement.constructor == Array) {
			var returned = groupAccordingToTree(thisElement, componentArray);
			for(var j = 0; j < returned.length; j++) {
				componentsToGroup.push(returned[j]);
			}
		}
		else {
			componentsToGroup.push(componentArray[thisElement]);
		}
	}

	groupCertainComponents(componentsToGroup);

	return componentsToGroup;
}

/** Get a simple object containig information about the given CTAT component.
    This is similar to serialization, but it doesn't turn it into a string. */
function getCtatComponentInfo(thisComponent) {
	if(thisComponent.tablecell) {
		return thisComponent; // a table cell is a fake CTAT component. It has all the fields that should be returned by this function.
	}

	var infoObj = new Object();

	infoObj.widgetType = thisComponent.getClassName();
	infoObj.commName = thisComponent.getName();
	if(infoObj.widgetType == "CTATCommShell") {
		infoObj.x = 0;
		infoObj.y = 0;
		infoObj.width = canvasWidth;
		infoObj.height = canvasHeight;
		infoObj.zIndex = 0;
		infoObj.caption = null;
	}
	else {
		infoObj.x = thisComponent.getX() - getCanvasOffsetX();
		infoObj.y = thisComponent.getY() - getCanvasOffsetY();
		infoObj.width = thisComponent.getWidth();
		infoObj.height = thisComponent.getHeight();
		infoObj.zIndex = thisComponent.getCanvasZIndex();
		infoObj.caption = thisComponent.getText();
	}

	return infoObj;
}

/** Get a representation of the groupings that have been defined for a set of components.
    The argument should be an array of indices into zOrderedComponents. */
function getGroupingsAmong(indices) {
	var groupings = [];
	var flattenedGroupings = []; // list of the flattened versions of each grouping tree in groupings

	var groupingHandleIds = [];
	var actualComponents = [];
	for(var i = 0; i < indices.length; i++) {
		groupingHandleIds[i] = zOrderedComponents[indices[i]].data("groupingHandleId");
		actualComponents[i] = zOrderedComponents[indices[i]];
		zOrderedComponents[indices[i]].data({"getGroupingsAmongID": i}); // an ID used specifically for this method call
	}

	// for each grouping handle, get everything that the handle groups together, except if something in this group is NOT in the list of indices (parameter)
	for(i = 0; i < groupingHandleIds.length; i++) {
		if(groupingHandles[groupingHandleIds[i]] != null) {
			var groupingTree = getGroupingTreeByGroupingForestIndex(groupingHandles[groupingHandleIds[i]]);
			var flattenedTree = flattenArrayOfArrays(groupingTree);

			var acceptable = true;
			for(var j = 0; j < flattenedTree.length; j++) {
				var found = false;
				for(var k = 0; k < actualComponents.length; k++) {
					if(flattenedTree[j] === actualComponents[k]) {
						found = true;
						break;
					}
				}
				if(!found) {
					acceptable = false;
					break;
				}
			}
			if(acceptable) {
				groupings.push(groupingTree);
				flattenedGroupings.push(flattenedTree);
			}
		} else { // groupingHandles has a null entry for this grouping handle id; it's not grouped
			// Don't do this because there should be no such thing as a grouping of just one component (because that component needs to be free to be a handle for new groupings)
			//groupings.push([actualComponents[i]]);
			//flattenedGroupings.push([actualComponents[i]]);
		}
	}

	// copy the grouping trees, leaving out duplicates. A "duplicate" occurs when one of the components in a tree is equal to a component in another tree.
	// In such a case, choose the larger tree and throw out the smaller.
	var finalGroupings = [];
	var finalFlattened = [];
	for(i = 0; i < groupings.length; i++) {
		var shouldAppend = true;
		var overwriteIndex = -1;

		duplicateCheckingLoop:
		for(j = 0; j < finalFlattened.length; j++) {
			for(k = 0; k < finalFlattened[j].length; k++) {
				for(var m = 0; m < flattenedGroupings[i].length; m++) { // I don't like using l so I use m instead
					if(finalFlattened[j][k] === flattenedGroupings[i][m]) {
						// found a duplicate
						shouldAppend = false;
						if(flattenedGroupings[i].length > finalFlattened[j].length) { // if this (flattened) tree has more elements than that one (i.e. that one's a subtree)
							overwriteIndex = j; // overwrite the subtree
						} // else, don't overwrite or append at all (this tree is a subtree of something we already have)
						break duplicateCheckingLoop;
					}
				}
			}
		}

		if(shouldAppend) {
			finalGroupings.push(groupings[i]);
			finalFlattened.push(flattenedGroupings[i]);
		}
		if(overwriteIndex != -1) {
			finalGroupings[overwriteIndex] = groupings[i];
			finalFlattened[overwriteIndex] = flattenedGroupings[i];
		}
	}

	finalGroupings = getGroupingsAmong_helper(finalGroupings); // convert from a list of trees of components to a list of trees of "ID" numbers
	return finalGroupings;
}

/** There's no reason to call this method except from within getGroupingsAmong(). It replaces each component in the array by its getGroupingsAmongID */
function getGroupingsAmong_helper(arr) {
	var retval = [];
	for(var i = 0; i < arr.length; i++) {
		if(arr[i].constructor == Array) {
			retval[i] = getGroupingsAmong_helper(arr[i]); // recursively
		}
		else {
			retval[i] = arr[i].data("getGroupingsAmongID");
		}
	}
	return retval;
}

function getGroupingTreeByGroupingForestIndex(groupingForestIndex) {
	var tree = [];
	var groupingForestElement = groupingForest[groupingForestIndex];

	for(var i = 0; i < groupingForestElement.components.length; i++) {
		tree.push(groupingForestElement.components[i]);
	}
	for(i = 0; i < groupingForestElement.subgroupings.length; i++) {
		tree.push(getGroupingTreeByGroupingForestIndex(groupingForestElement.subgroupings[i]));
	}

	return tree;
}

function clickOnCanvas(ev) {
	if(ignoreNextClick) {
		ignoreNextClick = false;
		return;
	}

	if(ev.target.nodeName == 'svg') {
		clearSelection();

		if(lastClickedIconId !== null && lastClickedIconId !== '') {
			// copied from dropOnCanvas() and modified slightly:
			var PREFIX = 'toolbox_';
			if(lastClickedIconId.substring(0, PREFIX.length) === PREFIX) {
				beginUndoableAction();
				clearSelection();

				var c = document.getElementById('mygraphiccontainer');

				var x, y, width=50, height=50; // TODO don't hard code these numbers
				var canvasRect = c.getBoundingClientRect();
				x = ev.clientX - canvasRect.left;
				y = ev.clientY - canvasRect.top;

				if(lastClickedIconId == "toolbox_table") {
					addTableComponent(x, y);
				}
				else {
					var component = RaphaelCanvas.rect(x, y, width, height).attr({fill: "rgb(255, 255, 255)"});
					component.drag(compDragMove, compDragStart, compDragEnd);
					component.click(compClick);
					doBookkeepingForNewComponent(component, lastClickedIconId);

					snapToGrid(component);
				}
			}
			else {
				debug('Unknown object to add to canvas: ' + data);
			}
		}
	}
	lastClickedIconId = null;
}

/** Handle click on a toolbar icon */
function clickOnIcon(ev) {
	lastClickedIconId = ev.target.id;
	debug('click; lastClickedIconId is ' + lastClickedIconId); // TODO
}

var mouseDownLocation = null; // relative to client area
var ignoreNextClick = false;
function mouseDownOnCanvas(ev) {
	if(ev.target.nodeName == 'svg') {
		mouseDownLocation = {x: ev.clientX, y: ev.clientY};
	}
	else {
		mouseDownLocation = null;
	}
}
function mouseOutOfCanvas(ev) {
	//mouseDownLocation = null;
}
var MOUSE_MOVE_TOLERANCE = 2;
function mouseUpOnCanvas(ev) {
	if(ev.target.nodeName == 'svg' && mouseDownLocation !== null) {
		var x, y, width=Math.abs(ev.clientX - mouseDownLocation.x), height=Math.abs(ev.clientY - mouseDownLocation.y);
		var c = document.getElementById('mygraphiccontainer');
		var canvasRect = c.getBoundingClientRect();
		x = min(ev.clientX, mouseDownLocation.x) - canvasRect.left;
		y = min(ev.clientY, mouseDownLocation.y) - canvasRect.top;

		if(width <= MOUSE_MOVE_TOLERANCE && height <= MOUSE_MOVE_TOLERANCE) {
			mouseDownLocation = null;
			return;
		}

		if(lastClickedIconId !== null && lastClickedIconId !== '') {
			// copied from dropOnCanvas() and modified slightly: // TODO consolidate these instances of copied code into a single function
			var PREFIX = 'toolbox_';
			if(lastClickedIconId.substring(0, PREFIX.length) === PREFIX) {
				beginUndoableAction();
				clearSelection();

				if(lastClickedIconId == "toolbox_table") {
					addTableComponent(x, y);
				}
				else {
					var component = RaphaelCanvas.rect(x, y, width, height).attr({fill: "rgb(255, 255, 255)"});
					component.drag(compDragMove, compDragStart, compDragEnd);
					component.click(compClick);
					doBookkeepingForNewComponent(component, lastClickedIconId);

					snapToGrid(component);
					ignoreNextClick = true;
				}
			}
			else {
				debug('Unknown object to add to canvas: ' + data);
			}
		}
		else {
			rectSelect({x: x, y: y, width: width, height: height});
			ignoreNextClick = true;
		}
	}
	lastClickedIconId = null;
	mouseDownLocation = null;
}

/** Create a table component, which is really just a bunch of text boxes grouped */
function addTableComponent(x, y) {
	var cols = window.prompt("How many columns?");
	cols = cols > 0 ? cols : 3;

	var rows = window.prompt("How many rows?");
	rows = rows > 0 ? rows : 4;

	// pick a name for the table and its component columns and cells
	var tablename = null;
	var num = 1;
	while(!tablename) {
		tablename = "table" + num;
		if(!nameIsUnique(tablename)) {
			tablename = null;
			continue;
		}

		for(var c = 0; c < cols; c++) {
			var colname = tablename + "_Column" + (c+1);
			if(!nameIsUnique(colname)) {
				tablename = null;
				continue;
			}

			for(var r = 0; r < rows; r++) {
				var cellname = tablename + "_C" + (c+1) + "R" + (r+1);
				if(!nameIsUnique(cellname)) {
					tablename = null;
					continue;
				}
			}
		}
	}

	// create the cells and group them
	var cellWidth = 50, cellHeight = 50; // TODO don't hard code these values
	var allCells = [];
	var origX = x, origY = y;
	for(var c = 0; c < cols; c++) {
		var columnCells = [];

		y = origY;
		for(var r = 0; r < rows; r++) {
			var component = RaphaelCanvas.rect(x, y, cellWidth, cellHeight).attr({fill: "rgb(255, 255, 255)"});
			component.drag(compDragMove, compDragStart, compDragEnd);
			component.click(compClick);
			doBookkeepingForNewComponent(component, "tablecell", tablename + "_C" + (c+1) + "R" + (r+1));

			columnCells.push(component);
			allCells.push(component);

			snapToGrid(component);

			y += cellHeight;
		}

		groupCertainComponents(columnCells, tablename + "_Column" + (c+1));

		x += cellWidth;
	}
	groupCertainComponentsAsTable(allCells, tablename);
}

/** Find the x and y alignment lines for every component NOT in the given array, and set xAlignmentLines and yAlignmentLines accordingly (in ascending sort) */
function getAlignmentsFor(componentArray) {
	xAlignmentLines = new Array();
	yAlignmentLines = new Array();

	for(var i = 0; i < zOrderedComponents.length; i++) {
		comp = zOrderedComponents[i];
		if(comp == null) {
			continue;
		}
		var inArray = false;
		for(var j = 0; j < componentArray.length; j++) {
			if(comp === componentArray[j]) {
				inArray = true;
				break;
			}
		}
		if(!inArray) {
			xAlignmentLines.push(comp.attr("x"));
			xAlignmentLines.push(comp.attr("x")+comp.attr("width")/2);
			xAlignmentLines.push(comp.attr("x")+comp.attr("width"));

			yAlignmentLines.push(comp.attr("y"));
			yAlignmentLines.push(comp.attr("y")+comp.attr("height")/2);
			yAlignmentLines.push(comp.attr("y")+comp.attr("height"));
		}
	}

	xAlignmentLines.sort(function(a, b){return a-b;}); // the comparator function is necessary because otherwise JS would sort the numbers alphabetically
	yAlignmentLines.sort(function(a, b){return a-b;});
}

var dynamicAlignmentLines = new Array();
var DYNAMIC_ALIGNMENT_TOLERANCE = 3; // number of pixels within which to "snap to" dynamic alignment
/** Moves the specified components for dynamic alignment, if necessary, according to xAlignmentLines and yAlignmentLines.
    Each component will be moved by the same amount in the same direction.
    drawLines is a boolean for whether the alignment lines should be drawn. */
function doDynamicAlignment(componentArray, drawLines) {
	clearDynamicAlignmentLines();

	if(componentArray.length === 0) {
		return;
	}

	var smallest_x_difference = Number.POSITIVE_INFINITY;
	var smallest_x_info = null;
	var smallest_y_difference = Number.POSITIVE_INFINITY;
	var smallest_y_info = null;

	for(var i = 0; i < componentArray.length; i++) {
		var comp = componentArray[i];

		var closest_left_x = findClosest(xAlignmentLines, comp.attr("x"));
		var closest_mid_x = findClosest(xAlignmentLines, comp.attr("x") + comp.attr("width") / 2);
		var closest_right_x = findClosest(xAlignmentLines, comp.attr("x") + comp.attr("width"));

		var closest_top_y = findClosest(yAlignmentLines, comp.attr("y"));
		var closest_mid_y = findClosest(yAlignmentLines, comp.attr("y") + comp.attr("height") / 2);
		var closest_bottom_y = findClosest(yAlignmentLines, comp.attr("y") + comp.attr("height"));

		if(Math.abs((closest_mid_x) - (comp.attr("x") + comp.attr("width") / 2)) < smallest_x_difference) {
			smallest_x_difference = Math.abs((closest_mid_x) - (comp.attr("x") + comp.attr("width") / 2));
			smallest_x_info = {component: comp, lmr: 'm', delta: (closest_mid_x) - (comp.attr("x") + comp.attr("width") / 2)};
		}
		if(Math.abs((closest_left_x) - (comp.attr("x"))) < smallest_x_difference) {
			smallest_x_difference = Math.abs((closest_left_x) - (comp.attr("x")));
			smallest_x_info = {component: comp, lmr: 'l', delta: (closest_left_x) - (comp.attr("x"))}; // lmr: left, middle, right
		}
		if(Math.abs((closest_right_x) - (comp.attr("x") + comp.attr("width"))) < smallest_x_difference) {
			smallest_x_difference = Math.abs((closest_right_x) - (comp.attr("x") + comp.attr("width")));
			smallest_x_info = {component: comp, lmr: 'r', delta: (closest_right_x) - (comp.attr("x") + comp.attr("width"))};
		}

		if(Math.abs((closest_mid_y) - (comp.attr("y") + comp.attr("height") / 2)) < smallest_y_difference) {
			smallest_y_difference = Math.abs((closest_mid_y) - (comp.attr("y") + comp.attr("height") / 2));
			smallest_y_info = {component: comp, tmb: 'm', delta: (closest_mid_y) - (comp.attr("y") + comp.attr("height") / 2)};
		}
		if(Math.abs((closest_top_y) - (comp.attr("y"))) < smallest_y_difference) {
			smallest_y_difference = Math.abs((closest_top_y) - (comp.attr("y")));
			smallest_y_info = {component: comp, tmb: 't', delta: (closest_top_y) - (comp.attr("y"))}; // tmp: top, middle, bottom
		}
		if(Math.abs((closest_bottom_y) - (comp.attr("y") + comp.attr("height"))) < smallest_y_difference) {
			smallest_y_difference = Math.abs((closest_bottom_y) - (comp.attr("y") + comp.attr("height")));
			smallest_y_info = {component: comp, tmb: 'b', delta: (closest_bottom_y) - (comp.attr("y") + comp.attr("height"))};
		}
	}

	// move the components by the calculated delta if it is within the threshold
	if(smallest_x_difference <= DYNAMIC_ALIGNMENT_TOLERANCE) {
		for(i = 0; i < componentArray.length; i++) {
			comp = componentArray[i];
			comp.attr({x: comp.attr("x") + smallest_x_info.delta});
		}
		if(drawLines) {
			var x;
			switch(smallest_x_info.lmr) {
			case 'l':
				x = smallest_x_info.component.attr("x");
				break;
			case 'm':
				x = smallest_x_info.component.attr("x") + smallest_x_info.component.attr("width") / 2;
				break;
			case 'r':
				x = smallest_x_info.component.attr("x") + smallest_x_info.component.attr("width");
				break;
			default:
				debug("Dynamic Alignment Error: lmr not one of 'l', 'm', 'r'.");
				break;
			}
			dynamicAlignmentLines.push(RaphaelCanvas.path(["M", x, 0, "L", x, canvasHeight]));
		}
	}
	if(smallest_y_difference <= DYNAMIC_ALIGNMENT_TOLERANCE) {
		for(i = 0; i < componentArray.length; i++) {
			comp = componentArray[i];
			comp.attr({y: comp.attr("y") + smallest_y_info.delta});
		}
		if(drawLines) {
			var y;
			switch(smallest_y_info.tmb) {
			case 't':
				y = smallest_y_info.component.attr("y");
				break;
			case 'm':
				y = smallest_y_info.component.attr("y") + smallest_y_info.component.attr("height") / 2;
				break;
			case 'b':
				y = smallest_y_info.component.attr("y") + smallest_y_info.component.attr("height");
				break;
			default:
				debug("Dynamic Alignment Error: tmb not one of 't', 'm', 'b'.");
				break;
			}
			dynamicAlignmentLines.push(RaphaelCanvas.path(["M", 0, y, "L", canvasWidth, y]));
		}
	}
}
function clearDynamicAlignmentLines() {
	for(var i = 0; i < dynamicAlignmentLines.length; i++) {
		dynamicAlignmentLines[i].remove();
	}
	dynamicAlignmentLines = new Array();
}

/** Snap all components to grid. Currently that means to the nearest whole number of pixels, and then doing dynamic alignment */
function snapAllToGrid() {
	for(var i = 0; i < zOrderedComponents.length; i++) {
		var comp = zOrderedComponents[i];
		if(comp != null) {
			snapToGrid(comp);
		}
	}
}
/** Snap the specified component to grid. Currently that means to the nearest whole number of pixels, and then doing dynamic alignment */
function snapToGrid(comp) {
	var new_x = Math.round(comp.attr("x"));
	var new_y = Math.round(comp.attr("y"));
	var new_width = Math.round(comp.attr("width"));
	var new_height = Math.round(comp.attr("height"));

	comp.attr({x: new_x, y: new_y, width: new_width, height: new_height});

	doDynamicAlignment(comp, false);

	// Position the CTAT component within the box
	var ctatcomp = ctatComponents[comp.data('ctatComponentIndex')];
	if(!ctatcomp.tablecell) {
		ctatcomp.setX(comp.attr('x') + getCanvasOffsetX());
		ctatcomp.setY(comp.attr('y') + getCanvasOffsetY());
		ctatcomp.setWidth(comp.attr('width'))
		ctatcomp.setHeight(comp.attr('height'));
	}

	redrawGroupingBoxes();

	modified = true;
}

// Note: Groupings can be nested hierarchically (must be properly nested).
// Each grouping has one or more "handle" components (when the handle is selected, the grouping is selected) and may contain subgroupings.
var groupingHandles = new Array(); // map from "grouping handle id" to the group that that component is a handle for (expressed as an index into groupingForest)
var groupingForest = new Array(); // set of grouping trees // TODO is this really the best name for this? It's not really a set of disjoint trees, more a set of subtrees
// Note on groupingForest: each element of groupingForest is an object with a name and two arrays, named components and subgroupings.
// Components is the list of single components (handles) in the grouping. Each component is represented by its actual RaphaelJS rectangle.
// Subgroupings is a list of indices into groupingForest.
// Each element of groupingForest may also have a "box" field, which is a bounding Raphael rectangle for the grouped contents, and possibly an "ungrouped" boolean, meaning these components are no longer grouped, and possibly a "table" boolean that means it is a table.
function groupSelected() {
	if(selectedComponents === null || selectedComponents.length <= 1) {
		return; // nothing to group
	}

	// remove duplicates from the selectedComponents array. This is necessary to make the next part work.
	var newSelectedComponents = [];
	for(var i = 0, j = 0; i < selectedComponents.length; i++) {
		var alreadySeen = false;
		for(var k = 0; k < newSelectedComponents.length; k++) {
			if(selectedComponents[i] === newSelectedComponents[k]) {
				alreadySeen = true;
				break;
			}
		}
		if(!alreadySeen) {
			newSelectedComponents[j++] = selectedComponents[i];
		}
	}
	selectedComponents = newSelectedComponents;

	// If all selected components are already in a common grouping, then just prompt to change the name of that grouping.
	var groupings = []; // indices correspond to groupingForest
	var thisGroupingId = -1; // if the common grouping is found, this variable will hold its index
	for(var i = 0; i < groupingForest.length; i++) {
		// find what's in this grouping (pseudo-recursively using a stack)
		var stack = [];
		stack.push(i);
		while(stack.length > 0) {
			var index = stack.pop();
			if(groupings[index] === undefined) { // if this grouping hasn't been calculated yet
				var needToDoFirst = []; // groupings that will need to be calculated (recursively) before this one can
				for(var j = 0; j < groupingForest[index].subgroupings.length; j++) {
					if(groupings[groupingForest[index].subgroupings[j]] === undefined) { // if this grouping hasn't been calculated yet
						needToDoFirst.push(groupingForest[index].subgroupings[j]);
					}
				}
				if(needToDoFirst.length > 0) {
					stack.push(index);
					for(var j = 0; j < needToDoFirst.length; j++) {
						stack.push(needToDoFirst[j]);
					}
				}
				else { // good to go; nothing needs to be done first
					var newArray = [];
					for(var j = 0; j < groupingForest[index].components.length; j++) {
						newArray[j] = groupingForest[index].components[j];
					}
					for(var j = 0; j < groupingForest[index].subgroupings.length; j++) {
						newArray.push(groupings[groupingForest[index].subgroupings[j]]);
					}
					groupings[index] = flattenArrayOfArrays(newArray);
				}
			}
		}
		// see if this grouping contains all and only the selected components
		if(!groupingForest[i].ungrouped) { // if this is still a valid grouping...
			var match = false;
			if(groupings[i].length == selectedComponents.length) {
				match = true;
				for(var j = 0; j < groupings[i].length; j++) {
					var found = false;
					for(var k = 0; k < selectedComponents.length; k++) {
						if(groupings[i][j] === selectedComponents[k]) {
							found = true;
							break;
						}
					}
					if(!found) {
						match = false;
						break;
					}
				}
			}
			if(match) {
				thisGroupingId = i;
				break;
			}
		}
	}
	if(thisGroupingId != -1) { // a common grouping for the selected components has been found
		// prompt to change name
		var groupingName;
		do {
			groupingName = window.prompt("This grouping already exists. You may change its name if you like.", groupingForest[thisGroupingId].name);
		} while(!nameIsUnique(groupingName));
		if(groupingName == null) { // cancel was pressed
			return;
		}
		// change name
		beginUndoableAction();
		groupingForest[thisGroupingId].name = groupingName;
		return;
	}

	// get the name for this grouping. Must be unique.
	var groupingName;
	do {
		groupingName = window.prompt("Please enter a name for this grouping", getUniqueName("grouping"));
	} while(!nameIsUnique(groupingName));
	if(groupingName == null) { // cancel was pressed
		return;
	}

	// do the actual grouping
	beginUndoableAction();
	groupCertainComponents(selectedComponents, groupingName);
}
function ungroupSelected() {
	if(selectedComponents === null || selectedComponents.length == 0) {
		return;
	}

	beginUndoableAction();

	// for each selected component, mark its grouping as "ungrouped" and remove its status as a "handle".
	for(var i = 0; i < selectedComponents.length; i++) {
		var handleId = selectedComponents[i].data("groupingHandleId");
		if(groupingHandles[handleId] !== undefined && groupingForest[groupingHandles[handleId]] !== undefined) { // avoid "undefined" errors
			groupingForest[groupingHandles[handleId]].ungrouped = true;
		}
		groupingHandles[handleId] = null;
	}

	redrawGroupingBoxes();
}
function groupCertainComponents(chosenComponents, groupingName) {
	if(chosenComponents == null || chosenComponents.length == 0) {
		return;
	}

	if(!groupingName || !nameIsUnique(groupingName)) {
		groupingName = getUniqueName("grouping");
	}

	// find the "grouping handle id" of each chosen component
	var chosenHandles = new Array();
	var nullGroupings = new Array();
	var nongroupedChosenComponents = new Array();
	for(var i = 0; i < chosenComponents.length; i++) {
		var handleId = chosenComponents[i].data("groupingHandleId");
		chosenHandles.push(handleId);
		if(groupingHandles[handleId] === null) {
			nullGroupings.push(handleId); // this handle id is not actually a handle for any grouping
			nongroupedChosenComponents.push(chosenComponents[i]); // this component was previously ungrouped (before the new grouping which is happening)
		}
	}

	// if each of the chosen components is already a handle for a grouping, there's no point in making a new grouping (because there is no free handle)
	if(nullGroupings.length === 0) {
		return;
	}

	// The new grouping will be composed of all the "nullGroupings" (free handles) plus the highest parent of each item that is already grouped.
	// Find the highest parent of each already-grouped item.
	var groupsToGroup = new Array(); // set of grouping IDs that will be grouped in the new grouping
	for(i = 0; i < chosenHandles.length; i++) {
		var thisHandleId = chosenHandles[i];
		var thisGroupingId = groupingHandles[thisHandleId];

		if(thisGroupingId !== null) {

			// find the highest parent of thisGroupingId, and set thisGroupingId to it
			var foundParent;
			do {
				foundParent = false;
				for(var j = 0; j < groupingForest.length; j++) {
					var grouping = groupingForest[j];
					for(var k = 0; k < grouping.subgroupings.length; k++) {
						if(grouping.subgroupings[k] == thisGroupingId) {
							foundParent = true;
							thisGroupingId = j;
							break;
						}
					}
					if(foundParent) {
						break;
					}
				}
			} while(foundParent);

			// add thisGroupingId to the set of grouping IDs to group in the new grouping
			var isDuplicate = false;
			for(j = 0; j < groupsToGroup.length; j++) {
				if(groupsToGroup[j] == thisGroupingId) {
					isDuplicate = true;
					break;
				}
			}
			if(!isDuplicate) {
				groupsToGroup.push(thisGroupingId);
			}
		}
	}

	// Create the new "grouping" and add it to groupingForest
	var newGrouping = {name: groupingName, components: nongroupedChosenComponents, subgroupings: groupsToGroup};
	groupingForest.push(newGrouping);

	// Set each of the "handles" (previously ungrouped components) of the new grouping to point to the new grouping
	for(i = 0; i < nullGroupings.length; i++) {
		groupingHandles[nullGroupings[i]] = groupingForest.length - 1; // groupingForest.length - 1 points to the newest grouping
	}

	redrawGroupingBoxes();
}

/** Columns should have already been grouped. This groups all the columns into a table */
function groupCertainComponentsAsTable(cells, tablename) {
	// find the columns that are formed out of these cells
	var columnSet = [];
	for(var i = 0; i < cells.length; i++) {
		var column = groupingHandles[cells[i].data("groupingHandleId")];
		var found = false;
		for(var j = 0; j < columnSet.length; j++) {
			if(columnSet[j] == column) {
				found = true;
				break;
			}
		}
		if(!found) {
			columnSet.push(column);
		}
	}

	// create the grouping
	groupingForest.push({name: tablename, components: [], subgroupings: columnSet, table: true});

	// Set all the cells as handles for the entire table
	for(var i = 0; i < cells.length; i++) {
		groupingHandles[cells[i].data("groupingHandleId")] = groupingForest.length - 1;
	}
}

/** (re)compute and (re)draw the boxes which outline each grouping */
function redrawGroupingBoxes() {
	// delete all existing grouping boxes
	for(var i = 0; i < groupingForest.length; i++) {
		if(groupingForest[i].box) {
			groupingForest[i].box.remove();
			groupingForest[i].box = null;
		}
	}

	// create new grouping boxes. This may take several iterations, because groupings can be nested.
	var done = false;
	while(!done) {
		done = true;
		loopgroupings: for(var i = 0; i < groupingForest.length; i++) {
			if(!groupingForest[i].box) {
				// find the contents of the grouping
				var contents = [];
				for(var j = 0; j < groupingForest[i].components.length; j++) {
					contents.push(groupingForest[i].components[j]);
				}
				for(var j = 0; j < groupingForest[i].subgroupings.length; j++) {
					if(groupingForest[groupingForest[i].subgroupings[j]].box) {
						contents.push(groupingForest[groupingForest[i].subgroupings[j]].box);
					}
					else {
						done = false;
						continue loopgroupings; // this specific grouping's box cannot be computed yet; go to the next
					}
				}

				// find the bounding box for these contents
				var top, left, bottom, right;
				top = contents[0].attr("y");
				left = contents[0].attr("x");
				bottom = contents[0].attr("y") + contents[0].attr("height");
				right = contents[0].attr("x") + contents[0].attr("width");
				for(var j = 1; j < contents.length; j++) {
					top = min(top, contents[j].attr("y"));
					left = min(left, contents[j].attr("x"));
					bottom = max(bottom, contents[j].attr("y") + contents[j].attr("height"));
					right = max(right, contents[j].attr("x") + contents[j].attr("width"));
				}

				// draw the box (either in light gray, or, if this grouping actually no longer exists (i.e. it has been ungrouped), transparent)
				var gapsize = 3;
				var opacity = groupingForest[i].ungrouped ? "0.0" : "0.3";
				groupingForest[i].box = RaphaelCanvas.rect(left - gapsize, top - gapsize, right - left + 2*gapsize, bottom - top + 2*gapsize).attr({"stroke-opacity": opacity, "stroke-dasharray": "-"});
				groupingForest[i].box.toBack();
				if(!groupingForest[i].ungrouped) { // if the group still exists, let it be clicked on (this allows user to change the grouping's name)
					groupingForest[i].box.groupingForestIndex = i;
					groupingForest[i].box.click(groupingClick);
				}
			}
		}
	}
}

/** When the bounding box for a grouping is clicked, allow the user to change the grouping's name */
function groupingClick() {
	// get the name for this grouping. Must be unique.
	var groupingName;
	do {
		groupingName = window.prompt("Please enter a name for this grouping", groupingForest[this.groupingForestIndex].name);
	} while(!nameIsUnique(groupingName) && groupingName != groupingForest[this.groupingForestIndex].name);
	if(groupingName == null) { // cancel was pressed
		return;
	}

	groupingForest[this.groupingForestIndex].name = groupingName;
}

function distributeSelectedHorizontally() {
	if(selectedComponents === null || selectedComponents.length <= 1) {
		return false; // nothing to distribute
	}

	beginUndoableAction();
	var listOfComponents = [];

	// for each selected component, record its current left position and width
	for(var i = 0; i < selectedComponents.length; i++) {
		listOfComponents.push({comp: selectedComponents[i], left: selectedComponents[i].attr("x"), width: selectedComponents[i].attr("width")});
	}

	// order the components by left position.
	listOfComponents.sort(function(a,b){return a.left - b.left;});

	// calculate the amount of space that there should be between components
	var selectionRectWidth = selectionRect.attr("width");
	var totalWidthOfComponents = 0;
	for(i = 0; i < listOfComponents.length; i++) {
		totalWidthOfComponents += listOfComponents[i].width;
	}
	if(totalWidthOfComponents > selectionRectWidth) {
		return false;
	}
	var paddingWidth = (selectionRectWidth - totalWidthOfComponents) / (listOfComponents.length + 1);

	// Position them at the correct position
	xPos = selectionRect.attr("x");
	for(i = 0; i < listOfComponents.length; i++) {
		xPos += paddingWidth;

		listOfComponents[i].comp.attr({"x": xPos});
		ctatComponents[listOfComponents[i].comp.data("ctatComponentIndex")].setX(xPos + getCanvasOffsetX());

		xPos += listOfComponents[i].width;
	}

	modified = true;
}
function distributeSelectedVertically() {
	if(selectedComponents === null || selectedComponents.length <= 1) {
		return false; // nothing to distribute
	}

	beginUndoableAction();
	var listOfComponents = [];

	// for each selected component, record its current top position and height
	for(var i = 0; i < selectedComponents.length; i++) {
		listOfComponents.push({comp: selectedComponents[i], top: selectedComponents[i].attr("y"), height: selectedComponents[i].attr("height")});
	}

	// order the components by top position.
	listOfComponents.sort(function(a,b){return a.top - b.top;});

	// calculate the amount of space that there should be between components
	var selectionRectHeight = selectionRect.attr("height");
	var totalHeightOfComponents = 0;
	for(i = 0; i < listOfComponents.length; i++) {
		totalHeightOfComponents += listOfComponents[i].height;
	}
	if(totalHeightOfComponents > selectionRectHeight) {
		return false;
	}
	var paddingHeight = (selectionRectHeight - totalHeightOfComponents) / (listOfComponents.length + 1);

	// Position them at the correct position
	yPos = selectionRect.attr("y");
	for(i = 0; i < listOfComponents.length; i++) {
		yPos += paddingHeight;

		listOfComponents[i].comp.attr({"y": yPos});
		ctatComponents[listOfComponents[i].comp.data("ctatComponentIndex")].setY(yPos + getCanvasOffsetY());

		yPos += listOfComponents[i].height;
	}

	modified = true;
}

function alignSelectedLeft() {
	if(selectedComponents === null || selectedComponents.length <= 1) {
		return false; // nothing to align
	}

	beginUndoableAction();
	var leftmost = null;
	for(var i = 0; i < selectedComponents.length; i++) {
		var thisComp = selectedComponents[i];
		if(leftmost === null || thisComp.attr("x") < leftmost) {
			leftmost = thisComp.attr("x");
		}
	}

	for(var i = 0; i < selectedComponents.length; i++) {
		thisComp = selectedComponents[i];
		thisComp.attr({"x": leftmost});
		ctatComponents[thisComp.data("ctatComponentIndex")].setX(leftmost + getCanvasOffsetX());
	}
	modified = true;
}
function alignSelectedRight() {
	if(selectedComponents === null || selectedComponents.length <= 1) {
		return false; // nothing to align
	}

	beginUndoableAction();
	var rightmost = null;
	for(var i = 0; i < selectedComponents.length; i++) {
		var thisComp = selectedComponents[i];
		var thisRight = thisComp.attr("x") + thisComp.attr("width");
		if(rightmost === null || rightmost < thisRight) {
			rightmost = thisRight;
		}
	}

	for(var i = 0; i < selectedComponents.length; i++) {
		thisComp = selectedComponents[i];
		var xPos = rightmost - thisComp.attr("width");
		thisComp.attr({"x": xPos});
		ctatComponents[thisComp.data("ctatComponentIndex")].setX(xPos + getCanvasOffsetX());
	}
	modified = true;
}
function alignSelectedCenterHorz() {
	if(selectedComponents === null || selectedComponents.length <= 1) {
		return false; // nothing to align
	}

	beginUndoableAction();
	var leftmost = null;
	var rightmost = null;
	for(var i = 0; i < selectedComponents.length; i++) {
		var thisComp = selectedComponents[i];
		var thisLeft = thisComp.attr("x");
		var thisRight = thisComp.attr("x") + thisComp.attr("width");
		if(leftmost === null || thisLeft < leftmost) {
			leftmost = thisLeft
		}
		if(rightmost === null || rightmost < thisRight) {
			rightmost = thisRight;
		}
	}

	var center = (leftmost + rightmost) / 2;
	for(var i = 0; i < selectedComponents.length; i++) {
		thisComp = selectedComponents[i];
		var xPos = center - thisComp.attr("width") / 2;
		thisComp.attr({"x": xPos});
		ctatComponents[thisComp.data("ctatComponentIndex")].setX(xPos + getCanvasOffsetX());
	}
	modified = true;
}
function alignSelectedTop() {
	if(selectedComponents === null || selectedComponents.length <= 1) {
		return false; // nothing to align
	}

	beginUndoableAction();
	var topmost = null;
	for(var i = 0; i < selectedComponents.length; i++) {
		var thisComp = selectedComponents[i];
		if(topmost === null || thisComp.attr("y") < topmost) {
			topmost = thisComp.attr("y");
		}
	}

	for(var i = 0; i < selectedComponents.length; i++) {
		thisComp = selectedComponents[i];
		thisComp.attr({"y": topmost});
		ctatComponents[thisComp.data("ctatComponentIndex")].setY(topmost + getCanvasOffsetY());
	}
	modified = true;
}
function alignSelectedBottom() {
	if(selectedComponents === null || selectedComponents.length <= 1) {
		return false; // nothing to align
	}

	beginUndoableAction();
	var bottommost = null;
	for(var i = 0; i < selectedComponents.length; i++) {
		var thisComp = selectedComponents[i];
		var thisBottom = thisComp.attr("y") + thisComp.attr("height");
		if(bottommost === null || bottommost < thisBottom) {
			bottommost = thisBottom;
		}
	}

	for(var i = 0; i < selectedComponents.length; i++) {
		thisComp = selectedComponents[i];
		var yPos = bottommost - thisComp.attr("height");
		thisComp.attr({"y": yPos});
		ctatComponents[thisComp.data("ctatComponentIndex")].setY(yPos + getCanvasOffsetY());
	}
	modified = true;
}
function alignSelectedCenterVert() {
	if(selectedComponents === null || selectedComponents.length <= 1) {
		return false; // nothing to align
	}

	beginUndoableAction();
	var topmost = null;
	var bottommost = null;
	for(var i = 0; i < selectedComponents.length; i++) {
		var thisComp = selectedComponents[i];
		var thisTop = thisComp.attr("y");
		var thisBottom = thisComp.attr("y") + thisComp.attr("height");
		if(topmost === null || thisTop < topmost) {
			topmost = thisTop;
		}
		if(bottommost === null || bottommost < thisBottom) {
			bottommost = thisBottom;
		}
	}

	var center = (topmost + bottommost) / 2;
	for(var i = 0; i < selectedComponents.length; i++) {
		thisComp = selectedComponents[i];
		var yPos = center - thisComp.attr("height") / 2;
		thisComp.attr({"y": yPos});
		ctatComponents[thisComp.data("ctatComponentIndex")].setY(yPos + getCanvasOffsetY());
	}
	modified = true;
}

var ctatComponents = []; // NOTE that some contents may be null
var loadedCommShell = false;

/** This function should be called every time a new component is added to the canvas. commName and caption are optional. */
function doBookkeepingForNewComponent(component, typeName, commName, caption) {
	zOrderedComponents.push(component);
	groupingHandles.push(null);
	component.data({"groupingHandleId" : groupingHandles.length - 1});

	// check provided name for uniqueness; if not unique, use default
	if(commName) {
		if(!nameIsUnique(commName)) {
			commName = null;
		}
	}

	if(!caption) {
		caption = "";
	}

	var internalFlashVars=tutorPrep ({});
	if (internalFlashVars ["session_id"]=="none")
		internalFlashVars ["session_id"]=("qa-test_"+guid());
	tutorRunning=true;

	skillSet=new CTATSkillSet ();
	flashVars=new CTATFlashVars ();
	flashVars.assignRawFlashVars({});

	if (platform=="ctat")
	{
		window.onerror = function(errorMsg, url, lineNumber)
		{
			var formatter=new CTATHTMLManager ();

			useDebugging=true; // This should always go through
			debug(formatter.htmlEncode (errorMsg) + " in " + url + ", line " + lineNumber);
			useDebugging=false;
		};

		var canvasname = 'main-canvas';
		ctatcanvas = document.getElementById(canvasname);

		if (ctatcanvas==null)
		{
			alert ("Internal error: HTML5 canvas is null");
			return;
		}

		ctx = ctatcanvas.getContext('2d');

		if (ctx==null)
		{
			alert ("Internal error: HTML5 canvas context is null");
			return;
		}
	}

	if(!loadedCommShell) {
		commShell=new CTATCommShell ();
		commShell.init (this);
		commShell.setName("theShell");
		ctatComponents.push(commShell);
		loadedCommShell = true;
	}

	if (platform=="ctat")
	{
		// Create our own version of the sidebar

		aCanvasWidth=canvasWidth;
		aCanvasHeight=canvasHeight;

		debug ("Canvas: " +canvasWidth + "," + canvasHeight);

		//createSidebarInterface (canvasWidth,canvasHeight);
		debug ("createSidebarInterface ("+aCanvasWidth+","+aCanvasHeight+")");

	var tutorWidth=aCanvasWidth-4; // assume a 2 pixel margin
	var tutorHeight=aCanvasHeight-4; // assume a 2 pixel margin

	var dummyDescription=new Object ();

	dummyDescription.x=component.attr("x") + getCanvasOffsetX();
	dummyDescription.y=component.attr("y") + getCanvasOffsetY();
	dummyDescription.width=50;
	dummyDescription.height=50;

	var name;
	switch(typeName) {
	case "toolbox_button":
		name = commName ? commName : getUniqueName("button");
		dummyDescription.name=name;
		dummyDescription.type="CTATButton";
		dummyDescription.styles = [{styleName: "labelText", styleValue: caption}];

		var done=new CTATButton (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		
		done.setName(name);
		
		done.setClassName("CTATButton");
		done.setText(caption);

		addComponent (done);
		component.data({"ctatComponentIndex" : ctatComponents.length});
		ctatComponents.push(done);

		break;
	case "toolbox_donebutton":
		name = commName ? commName : "done";
		dummyDescription.name=name;
		dummyDescription.type="CTATDoneButton";

		var done=new CTATImageButton (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		done.setName(name);
		done.setClassName ("CTATDoneButton");
		done.assignImages (doneDefault,doneHover,doneClick,doneDisabled);
		done.assignImages ("http://wiki.coolearth.com/images/7/71/Qc.netweightcontent.done.button.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Hover.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Click.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Disabled.png");

		addComponent (done);
		component.data({"ctatComponentIndex" : ctatComponents.length});
		ctatComponents.push(done);

		break;
	case "toolbox_hintbutton":
		name = commName ? commName : "hint";
		dummyDescription.name=name;
		dummyDescription.type="CTATHintButton";

		var hint=new CTATImageButton (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		hint.setName(name);
		hint.setClassName ("CTATHintButton");
		hint.assignImages ("src/images/hint_button.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Hover.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Click.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Disabled.png");

		addComponent (hint);
		component.data({"ctatComponentIndex" : ctatComponents.length});
		ctatComponents.push(hint);

		break;
	case "toolbox_radiobutton":
		name = commName ? commName : getUniqueName("radiobutton");
		dummyDescription.name=name;
		dummyDescription.type="CTATRadioButton";
		dummyDescription.styles = [{styleName: "labelText", styleValue: caption}];

		var rdb=new CTATRadioButton (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		rdb.setName(name);
		rdb.setText(caption);
		rdb.setClassName ("CTATRadioButton");

		addComponent (rdb);
		component.data({"ctatComponentIndex" : ctatComponents.length});
		ctatComponents.push(rdb);

		break;
	case "toolbox_label":
		
		name = commName ? commName : getUniqueName("label");
		dummyDescription.name=name;
		dummyDescription.type="CTATButton";
		dummyDescription.styles = [{styleName: "labelText", styleValue: caption}];

		var done=new CTATButton (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		//var txt=new CTATTextField (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		
		done.setName(name);
		
		done.setClassName("CTATButton");
		done.setText(caption);

		addComponent (done);
		component.data({"ctatComponentIndex" : ctatComponents.length});
		ctatComponents.push(done);
		
		break;
		
		//This will show the label without the border (as required) but does this label does not load on the server for now
		/*
                name = commName ? commName : getUniqueName("label");
		dummyDescription.name=name;
		dummyDescription.type="CTATTextField";
		dummyDescription.styles = [];

		var lbl=new CTATTextField (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		lbl.setName(name);
		lbl.setText(caption);
		lbl.setClassName ("CTATTextField");

		addComponent (lbl);
		component.data({"ctatComponentIndex" : ctatComponents.length});
		ctatComponents.push(lbl);

		break;
		*/
		
	case "toolbox_textbox":
		name = commName ? commName : getUniqueName("textbox");
		dummyDescription.name=name;
		dummyDescription.type="CTATTextInput";

		var txt=new CTATTextInput (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		txt.setName(name);
		txt.setText(caption);
		txt.setClassName ("CTATTextInput");

		addComponent (txt);
		component.data({"ctatComponentIndex" : ctatComponents.length});
		ctatComponents.push(txt);

		break;
	case "toolbox_dropdown":
		name = commName ? commName : getUniqueName("dropdown");
		dummyDescription.name=name;
		dummyDescription.type="CTATComboBox";

		var combo=new CTATComboBox (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		combo.setName(name);
		combo.setText(caption);
		combo.setClassName ("CTATComboBox");

		addComponent (combo);
		component.data({"ctatComponentIndex" : ctatComponents.length});
		ctatComponents.push(combo);

		break;
	case "toolbox_line":
		name = commName ? commName : getUniqueName("line");
		dummyDescription.name=name;
		dummyDescription.type="CTATButton";
		dummyDescription.styles = [{styleName: "labelText", styleValue: caption}];

		var done=new CTATButton (dummyDescription,dummyDescription.x,dummyDescription.y,dummyDescription.width,dummyDescription.height);
		
		done.setName(name);
		
		done.setClassName("CTATButton");
		done.setText(caption);

		addComponent (done);
		component.data({"ctatComponentIndex" : ctatComponents.length});
		ctatComponents.push(done);

		break;
	case "toolbox_image":
		alert("Can't do images yet"); // TODO
		break;
	case "toolbox_hintwindow":
		alert("Can't do hint windows yet"); // TODO
		break;
	case "tablecell": // To speed up the creation of tables, no actual CTAT component is placed in a table cell. It's just left empty.
		component.data({"ctatComponentIndex" : ctatComponents.length});
		ctatComponents.push({tablecell: true,
								widgetType: "CTATTextInput", // table cells should be rendered as a text box
								commName: commName,
								x: dummyDescription.x,
								y: dummyDescription.y,
								width: dummyDescription.width,
								height: dummyDescription.height,
								zIndex: 1, // TODO this number is just arbitrary now
								caption: null,
								getName: function() { return this.commName; }});
		break;
	default:
		alert("Error: Unknown component type added to canvas. Type is "+typeName);
		break;
	}
	}
	else
	{
		// do nothing
	}

	// make all mouse events pass through the CTAT component and end up in the Raphael rect
	if(!(ctatComponents[ctatComponents.length - 1].tablecell)) {
		ctatComponents[ctatComponents.length - 1].getDivWrap().style.pointerEvents = "none";
	}

	modified = true;
}

function getSerializedInterfaceDescription() {
	var serialized = '<?xml version="1.0" standalone="yes"?><stateGraph><startNodeMessages><message><verb>SendNoteProperty</verb><properties><MessageType>StartProblem</MessageType><ProblemName>start</ProblemName></properties></message>';

	for(var i = 0; i < ctatComponents.length; i++) {
		var thisComponent = ctatComponents[i];
		if(thisComponent == null) {
			continue;
		}

		serialized = serialized + serializeSingleCtatComponent(thisComponent);
	}

	serialized = serialized + '<message><verb>SendNoteProperty</verb><properties><MessageType>StartStateEnd</MessageType></properties></message></startNodeMessages></stateGraph>';

	

        var textTitle = '<SAIs></SAIs><Parameters><selection><CTATComponentParameter><name>ShowHintHighlight</name><value fmt="text" name="Show Hint Highlight" type="Boolean" includein="full">true</value></CTATComponentParameter><CTATComponentParameter><name>DisableOnCorrect</name><value fmt="text" name="Disable On Correct" type="Boolean" includein="full">true</value></CTATComponentParameter><CTATComponentParameter><name>tutorComponent</name><value fmt="text" name="Tutor Component" type="String" includein="full">Tutor</value></CTATComponentParameter></selection></Parameters><Styles><!--none--></Styles>'
        var result = serialized.replace(textTitle, '');

        return result;
}

function serializeSingleCtatComponent(thisComponent) {
	var serialized = "";

	var infoObj = getCtatComponentInfo(thisComponent);

	var includeJess = (infoObj.widgetType == "CTATTextInput" || infoObj.widgetType == "CTATTextArea"); // boolean for whether this BRD should include a Jess (WME) template and instance

	serialized = serialized + '<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceDescription</MessageType><WidgetType>';
	serialized = serialized + infoObj.widgetType;
	serialized = serialized + '</WidgetType><CommName>';
	serialized = serialized + infoObj.commName;
	serialized = serialized + '</CommName><UpdateEachCycle>false</UpdateEachCycle><jessDeftemplates>';
	if(includeJess) {
		serialized = serialized + '<value>(deftemplate textField (slot name) (slot value))</value>';
	}
	serialized = serialized + '</jessDeftemplates><jessInstances>';
	if(includeJess) {
		serialized = serialized + '<value>(assert (textField (name ' + infoObj.commName + ')))</value>';
	}
	serialized = serialized + '</jessInstances><serialized><';
	serialized = serialized + infoObj.widgetType;
	serialized = serialized + ' name="';
	serialized = serialized + infoObj.commName;
	serialized = serialized + '" x="';
	serialized = serialized + infoObj.x;
	serialized = serialized + '" y="';
	serialized = serialized + infoObj.y;
	serialized = serialized + '" width="';
	serialized = serialized + infoObj.width;
	serialized = serialized + '" height="';
	serialized = serialized + infoObj.height;
	serialized = serialized + '" scaleX="1" scaleY="1" originalWidth="';
	serialized = serialized + infoObj.width;
	serialized = serialized + '" originalHeight="';
	serialized = serialized + infoObj.height;
	serialized = serialized + '" zIndex="';
	serialized = serialized + infoObj.zIndex;
	serialized = serialized + '" tabIndex="-1"><SAIs></SAIs><Parameters><selection><CTATComponentParameter><name>ShowHintHighlight</name><value fmt="text" name="Show Hint Highlight" type="Boolean" includein="full">true</value></CTATComponentParameter><CTATComponentParameter><name>DisableOnCorrect</name><value fmt="text" name="Disable On Correct" type="Boolean" includein="full">true</value></CTATComponentParameter><CTATComponentParameter><name>tutorComponent</name><value fmt="text" name="Tutor Component" type="String" includein="full">Tutor</value></CTATComponentParameter></selection></Parameters><Styles>';
	if(infoObj.caption !== null) {
		serialized = serialized + '<selection><CTATStyleProperty><name>labelText</name><value fmt="txt" name="Label Text" type="String" includein="sparse">'+infoObj.caption+'</value></CTATStyleProperty></selection>';
	} else {
		serialized = serialized + '<!--none-->';
	}
	serialized = serialized + '</Styles></';
	serialized = serialized + infoObj.widgetType;
	serialized = serialized + '></serialized></properties></message>';

	return serialized;
}

/** Produces and returns the text for wmeTypes.clp */
function getWMETypes() {
	return '(deftemplate MAIN::problem (slot name) (multislot items))\n(deftemplate MAIN::textField (slot name) (slot value))\n(deftemplate MAIN::grouping (slot name) (multislot items))\n(provide wmeTypes)\n';
}

/** Produces and returns the text for the *.wme file */
function getJessFacts() {
	var opening = '(require* wmeTypes "wmeTypes.clp")';

	var factAssertions = '';
	var slotAssignments = '';
	var i;
	for(i = 0; i < ctatComponents.length; i++) {
		var thisCtatComponent = ctatComponents[i];
		if(thisCtatComponent == null) {
			continue;
		}

		var infoObj = getCtatComponentInfo(thisCtatComponent);
		if(infoObj.widgetType == "CTATTextInput" || infoObj.widgetType == "CTATTextArea" || infoObj.widgetType == "CTATComboBox") { // if this component should have a Jess fact
			factAssertions = factAssertions + '(bind ?var' + i + ' (assert(MAIN::textField (name ' + infoObj.commName + '))))\n';
			slotAssignments = slotAssignments + '(modify ?var' + i + ' (name ' + infoObj.commName + ') (value ' + (infoObj.caption ? infoObj.caption : 'nil') + '))\n';
		}
	}
	var offset = i;
	for(var j = 0; j < groupingForest.length; j++) {
		var thisGrouping = groupingForest[j];
		if(thisGrouping == null) {
			continue;
		}

		factAssertions = factAssertions + '(bind ?var' + (j+offset) + ' (assert(MAIN::grouping (name ' + thisGrouping.name + '))))\n';

		slotAssignments = slotAssignments + '(modify ?var' + (j+offset) + ' (name ' + thisGrouping.name + ') (items';
		for(var k = 0; k < thisGrouping.components.length; k++) {
			var thisComponent = thisGrouping.components[k];
			if(thisComponent == null) {
				continue;
			}
			var thisCtatComponentIndex = thisComponent.data("ctatComponentIndex");
			var infoObj = getCtatComponentInfo(ctatComponents[thisCtatComponentIndex]);
			if(infoObj.widgetType == "CTATTextInput" || infoObj.widgetType == "CTATTextArea" || infoObj.widgetType == "CTATComboBox") { // if this component has a Jess fact
				slotAssignments = slotAssignments + ' ?var' + thisCtatComponentIndex;
			}
		}
		for(var k = 0; k < thisGrouping.subgroupings.length; k++) {
			slotAssignments = slotAssignments + ' ?var' + (thisGrouping.subgroupings[k]+offset);
		}
		slotAssignments = slotAssignments + '))\n';
	}

	// TODO problem fact

	return opening + '\n' + factAssertions + '\n' + slotAssignments + '\n';
}

function loadInterfaceDescription(desc) {
	var parser=new DOMParser();
	var xmlDoc=parser.parseFromString(desc,"text/xml");

	var messages = xmlDoc.getElementsByTagName("message");
	for(var i = 0; i < messages.length; i++) {
		var msgElement = messages[i];
		var msgtypeElement = msgElement.getElementsByTagName("MessageType")[0];
		if(msgtypeElement.childNodes[0].nodeValue == "InterfaceDescription") {
			var widgettypeElement = msgElement.getElementsByTagName("WidgetType")[0];
			var widgetType = widgettypeElement.childNodes[0].nodeValue;
			var commName = msgElement.getElementsByTagName("CommName")[0].childNodes[0].nodeValue;
			var attributes = msgElement.getElementsByTagName("serialized")[0].getElementsByTagName(widgetType)[0].attributes;

			var x = attributes.getNamedItem("x").nodeValue;
			var y = attributes.getNamedItem("y").nodeValue;
			var width = attributes.getNamedItem("width").nodeValue;
			var height = attributes.getNamedItem("height").nodeValue;
			var caption = null;

			//
	    var stylesElement = msgElement.getElementsByTagName("serialized")[0].getElementsByTagName("Styles")[0];
	    if(stylesElement != null) {
		console.log("Styles is there");
		var selectionElements = stylesElement.getElementsByTagName("selection");
		if(selectionElements !== null && selectionElements.length >= 1) {
			var selectionElement = selectionElements[0];
			var stylePropertyElements = selectionElement.getElementsByTagName("CTATStyleProperty");
			for(var j = 0; j < stylePropertyElements.length; j++) {
				var stylePropertyElement = stylePropertyElements[j];
				if(stylePropertyElement.getElementsByTagName("name")[0].childNodes[0].nodeValue == "labelText") {
					var childNodes = stylePropertyElement.getElementsByTagName("value")[0].childNodes;
					if(childNodes && childNodes.length > 0) {
						caption = childNodes[0].nodeValue;
					}
				}
			}
		}
	}else{
		console.log("Styles is not there");
		continue;
	}
			//

			if(widgetType == "CTATCommShell") {
				continue; // TODO
			}

			debug(widgetType);
			debug(commName);
			debug(x);
			debug(y);
			debug(width);
			debug(height);
			debug(caption);

			addNewComponentInternal({widgetType: widgetType, commName: commName, x: x, y: y, width: width, height: height, caption: caption});
		}
	}
	modified = false;
	clearUndoAndRedo();
}

var canvasOffsetX;
var canvasOffsetY;
function getCanvasOffsetX() {
	if(!canvasOffsetX) {
		canvasOffsetX = document.getElementById("mygraphiccontainer").getBoundingClientRect().left - document.getElementById("contenttabs").getBoundingClientRect().left;
	}

	return canvasOffsetX;
}
function getCanvasOffsetY() {
	if(!canvasOffsetY) {
		canvasOffsetY = document.getElementById("mygraphiccontainer").getBoundingClientRect().top - document.getElementById("contenttabs").getBoundingClientRect().top;
	}
	return canvasOffsetY;
}

/** Method for internal use. Takes a CTAT component description and creates that CTAT component and the accompanying Raphael rectangle, and does bookkeeping */
function addNewComponentInternal(ctatComponentInfo) {
	var component = RaphaelCanvas.rect(ctatComponentInfo.x, ctatComponentInfo.y, ctatComponentInfo.width, ctatComponentInfo.height).attr({fill: "rgb(255, 255, 255)"});
	component.drag(compDragMove, compDragStart, compDragEnd);
	component.click(compClick);

	var type;
	switch(ctatComponentInfo.widgetType) {
	case "CTATButton": case "CTATImageButton": type = "toolbox_button"; break;
	case "CTATDoneButton": type = "toolbox_donebutton"; break;
	case "CTATHintButton": type = "toolbox_hintbutton"; break;
	case "CTATRadioButton": type = "toolbox_radiobutton"; break;
	case "CTATTextField": type = "toolbox_label"; break;
	case "CTATTextInput": type = "toolbox_textbox"; break;
	case "CTATComboBox": type = "toolbox_dropdown"; break;
	default: alert("widgetType "+widgetType);
	}
	doBookkeepingForNewComponent(component, type, ctatComponentInfo.commName, ctatComponentInfo.caption);

	snapToGrid(component);
}

var undoStack = [];
var redoStack = [];
var UNDO_STACK_MAX = 10;

/** This function should be called before each action that can be "undone". It saves the current state of the canvas and adds it to the undo stack */
function beginUndoableAction() {
	redoStack = [];
	var currentState = getCurrentState();

	undoStack.push(currentState);
	if(undoStack.length > UNDO_STACK_MAX) {
		undoStack.shift();
	}
}
function undo() {
	if(undoStack.length > 0) {
		var currentState = getCurrentState();
		redoStack.push(currentState);

		var newState = undoStack.pop();
		setCurrentState(newState);
	}
}
function redo() {
	if(redoStack.length > 0) {
		var currentState = getCurrentState();
		undoStack.push(currentState);

		var newState = redoStack.pop();
		setCurrentState(newState);
	}
}
function clearUndoAndRedo() {
	undoStack = [];
	redoStack = [];
}
/** Save state, to be stored in the Undo/Redo stack.
 *  Returns a state object with three fields:
 *  zOrderedCtatComponents: a z-ordered (back-to-front) array of CTAT component info objects which come from getCtatComponentInfo()
 *  zOrderedGroupingNames: an array of the names of the grouping for which each component is a handle, or null if the component is not a handle for a grouping
 *  groupings: a list of trees of indices into zOrderedCtatComponents (and zOrderedGroupingNames) which describes the groupings, as returned from getGroupingsAmong()
 */
function getCurrentState() {
	var currentState = {};

	var indices = [];
	for(var i = 0; i < zOrderedComponents.length; i++) {
		if(zOrderedComponents[i] !== null) {
			indices.push(i);
		}
	}

	currentState.zOrderedCtatComponents = [];
	currentState.zOrderedGroupingNames = [];
	for(i = 0; i < indices.length; i++) {
		currentState.zOrderedCtatComponents.push(getCtatComponentInfo(ctatComponents[zOrderedComponents[indices[i]].data("ctatComponentIndex")]));

		var thisGroupingName = null;
		var thisGroupingHandleId = zOrderedComponents[indices[i]].data("groupingHandleId");
		if(groupingHandles[thisGroupingHandleId] !== null) {
			thisGroupingName = groupingForest[groupingHandles[thisGroupingHandleId]].name;
		}
		currentState.zOrderedGroupingNames.push(thisGroupingName);
	}

	currentState.groupings = getGroupingsAmong(indices);

	return currentState;
}
function setCurrentState(state) {
	// first clear the canvas. Don't actually call clearCanvas() because that would count as an undoable action.
	for(var i = 0; i < zOrderedComponents.length; i++) {
		if(zOrderedComponents[i] !== null) {
			deleteComponentByIndex(i);
		}
	}
	for(var i = 0; i < groupingForest.length; i++) {
		var box = groupingForest[i].box;
		if(box) {
			box.remove();
		}
	}
	zOrderedComponents = [];
	groupingHandles = [];
	groupingForest = [];

	// add the specified components to the canvas
	var componentsToAdd = state.zOrderedCtatComponents;
	for(var i = 0; i < componentsToAdd.length; i++) {
		var thisComponentDescription = componentsToAdd[i];
		addNewComponentInternal(thisComponentDescription);
	}

	// handle groupings
	for(var i = 0; i < state.groupings.length; i++) {
		var groupingTree = state.groupings[i];
		groupAccordingToTree(groupingTree, zOrderedComponents);
	}
	clearSelection();

	// replace the default names of the groupings with their user-defined names
	var groupingNames = state.zOrderedGroupingNames;
	for(var i = 0; i < groupingNames.length; i++) {
		if(groupingNames[i] !== null) {
			groupingForest[groupingHandles[zOrderedComponents[i].data("groupingHandleId")]].name = groupingNames[i];
		}
	}

	snapAllToGrid();
}

function flattenArrayOfArrays(a, r){
    if(!r){ r = []}
    for(var i=0; i<a.length; i++){
        if(a[i].constructor == Array){
            flattenArrayOfArrays(a[i], r);
        }else{
            r.push(a[i]);
        }
    }
    return r;
}

function min(a, b) {
	return a < b ? a : b;
}
function max(a, b) {
	return a > b ? a : b;
}
/** find and return the element of arr that is closest in value to val. Assumes that arr has already been sorted in ascending order. */
function findClosest(arr, val) {
	if(val <= arr[0]) {
		return arr[0];
	}
	if(val >= arr[arr.length - 1]) {
		return arr[arr.length - 1];
	}

	var a = 0;
	var b = arr.length;
	while(a+1 < b) {
		var c = Math.round((b - a) / 2) + a;
		if(arr[c] < val) {
			a = c;
		}
		else if(arr[c] > val) {
			b = c;
		}
		else {
			return val; // arr[c] == val
		}
	}

	if(val == arr[a] || val == arr[b]) {
		return val;
	}
	else {
		var a_diff = Math.abs(val - arr[a]);
		var b_diff = Math.abs(val - arr[b]);
		if(a_diff < b_diff) {
			return arr[a];
		}
		else {
			return arr[b];
		}
	}

}