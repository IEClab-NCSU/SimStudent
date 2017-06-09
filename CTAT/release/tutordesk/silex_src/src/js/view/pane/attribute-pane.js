
goog.provide('silex.view.pane.AttributePane');
goog.require('silex.view.pane.PaneBase');

/**
*	@constructor
*/
silex.view.pane.AttributePane = function(propertyTool, model, element, controller)
{
	// call super
	goog.base(this, element, model, controller);
	this.propertyTool = propertyTool;
	this.attrList = goog.dom.getElement('attr-list');
	this.attributeEditor = element;
	this.model = model;
	var pointer = this;
};

goog.inherits(silex.view.pane.AttributePane, silex.view.pane.PaneBase);

//the id if the currently selected element
silex.view.pane.AttributePane.prototype.selectedId = null;
//a handle to the propertyTool this pane lives in
silex.view.pane.AttributePane.prototype.propertyTool = null;
//a handle to the div containing all the labels/inputs
silex.view.pane.AttributePane.prototype.attrList = null; 
//the whole section
silex.view.pane.AttributePane.prototype.attributeEditor = null;
//a handle to the main silex model object
silex.view.pane.AttributePane.prototype.model = null;

/**
*	Clear the contents of the attribute pane
*/
silex.view.pane.AttributePane.prototype.clear = function()
{
	while (this.attrList.firstChild)
		this.attrList.removeChild(this.attrList.firstChild);
};

/**
*	Render the pane
*	@param selectedElements an array of currently selected elements
*/
silex.view.pane.AttributePane.prototype.redraw = function(selectedElements)
{	
	//Check whether any input has focus, and if so process that value first
	if (this.focusedElement)
	{
		this.focusedElement.onchange({target: this.focusedElement});
		this.focusedElement = null;
	}
	this.iAmRedrawing = true;
	if (!selectedElements)
		selectedElements = this.model.body.getSelection();

	if (selectedElements.length == 1)
	{
	  var selectedElement = selectedElements[0];
	  if (!(this.selectedId && selectedElement.id == this.selectedId)) //element not already selected
	  {	  
		//empty list contents
		this.clear();
		//get relevant CTAT class
		var allAttr = this.getAttributeList(selectedElement.className);
		if (allAttr.length > 0)
		{
			var attributes = this.buildAttrArray(allAttr);
			var numAttributes = attributes.length;
			var thisAttribute;
			var inputDiv;
			for (var i = 0; i < numAttributes; i++) //for each attribute
			{
				//generate input element
				inputDiv = this.createAttrInput(attributes[i], selectedElement); 
				if (inputDiv)
				{
					//add to list div
					this.attrList.appendChild(inputDiv);
				}
				else console.log("Failed to create input element for attribute "+attributes[i]['name']);
			}
			if (selectedElement.className.includes('CTATComboBox'))
			{
				var existingOptions = $(selectedElement).find('option');
				if (existingOptions && existingOptions.length > 0)
				{
					this.addComboOptionInputs(existingOptions.length,
											  this.model.file.getContentWindow(),
											  selectedElement,
											  existingOptions);
				}
			}
		}
		//update current selection id
		this.selectedId = selectedElement.id;
		
		//populate property tool's classlist
		this.populateClasslist(selectedElement);
	  }
	}
	else if (selectedElements.length > 1)
	{
		//don't show attributes if more than one element selected
		this.clear();
		if (goog.dom.getElementByClass('property-tool-msg', this.attrList) == null)
		{
			var msg = document.createElement('p');
			msg.appendChild(document.createTextNode("To edit attributes, select only one element."));
			goog.dom.classlist.add(msg, 'property-tool-msg');
			this.attrList.appendChild(msg);
		}
	}
	this.iAmRedrawing = false;
};

/**
*	Helper function which, given an attribute object of a CTAT component,
*	generates an appropriate input element to control that attribute.
*	@param attribute {Object}: stores metadata concerning an attribute,
*		see ctat_component_attributes.js for the format of this object.
*/
silex.view.pane.AttributePane.prototype.createAttrInput = function(attribute, selectedElement)
{
	var li = document.createElement('div');
	var label = document.createTextNode(attribute['name']);
	li.appendChild(label);
	li.setAttribute('id', 'input-for-'+attribute['name'].toLowerCase().replace(/ /g, '-'));
	li.setAttribute('data-silex-attr-name', attribute['attrName']);
	if (attribute['description'])
	{
		li.setAttribute('title', attribute['description']);
	}
	var input = null;
	switch(attribute['inputType'])
	{
		case 'text':
		case 'numeric':
		case 'checkbox':
			var initialVal = attribute['default'];
			//get current value of attribute
			if (attribute['attrName'] 
			&& selectedElement.getAttribute(attribute['attrName']))
			{
				initialVal = selectedElement.getAttribute(attribute['attrName']);
			}
			else if (attribute['name'] === 'Number of Options')
			{
				let options = selectedElement.getAttribute('data-ctat-labels');
				if (options)
				{
					initialVal = options.split(',').length;
				}
			}
			//translate urls from blob src to filename
			if (attribute['attrName'] === 'data-ctat-src')
			{
				if (this.model.file.imgUrlMap[initialVal])
				{
					initialVal = this.model.file.imgUrlMap[initialVal]['name'];
				}
			}			
			//create input
			input = document.createElement('input');
			input.type = attribute['inputType'];
			if (attribute['inputType'] == 'checkbox')
			{
				if (initialVal == 'true')
				{
					input.checked = "yes";
				}
				if (attribute['name'] === 'Show Feedback')
				{
					if (selectedElement.getAttribute('data-ctat-tutor') === 'false')
					{
						input.setAttribute('disabled', 'sho nuff');
					}
				}
			}
			else
			{			
				input.setAttribute('value',initialVal);
			}
		break;
		case 'combobox':
			input = document.createElement('select');
			var options = attribute['comboChoices'];
			for (var j = 0; j < options.length; j++)
			{
				var option = document.createElement('option');
				option.value = options[j];
				option.appendChild(document.createTextNode(option.value));
				input.appendChild(option);
			}
		break;
		case 'button':
			li.removeChild(label);
			label = document.createElement('span');
			label.classList.add('silex-asset-label');
			input = document.createElement('button');
			input.innerHTML = attribute['name'];
			input.classList.add('property-tool-img-btn');
			input.classList.add('property-tool-btn');
			let assetName = selectedElement.getAttribute(attribute['attrName']);
			var title;
			if (assetName && this.model.file.imgUrlMap[assetName])
			{
				assetName = this.model.file.imgUrlMap[assetName]['name'];
				title = assetName;
			}
			else if (assetName && (assetName.includes('http://') || assetName.includes('https://')))
			{
				title = assetName;
				assetName = assetName.split('/').pop().split('?')[0];
			}
			label.textContent = assetName || 'N/A';
			label.title = title || '';
			li.appendChild(label);
		break;
		case 'datalist':
			input = document.createElement('input');
			input.setAttribute('type', 'text');
			input.setAttribute('list', attribute['listID']);
		break;
	}
	
	if (input)
	{
		input.classList.add("input-"+attribute['inputType']);
		if (attribute['disabled'])
		{
			input.setAttribute('disabled', 'yes');
		}
		else
		{
			this.setAttrListener(attribute, input, selectedElement);
		}
		li.appendChild(input);
		return li;
	}
	else return null;
};

/**
*	Adds an event listener to an input element in the attributes section
*	@param attribute: an object representing the attribute the input is associated
*		with.  see ctat_component_attributes.js for the structure of these objects
*	@param input the input element
*	@param selectedElement the element currently selected on the stage.
*/
silex.view.pane.AttributePane.prototype.setAttrListener = function(attribute, input, selectedElement)
{
	var iFrameWindow = this.model.file.getContentWindow();
	var pointer = this;
	if (attribute.inputType == 'button')
	{
		input.onclick = function(e)
		{
			attribute['setterFunction'](selectedElement);
		};
	}
	else if (attribute.inputType == 'datalist')
	{
		input.onchange = function(wasClicked)
			{
				if (pointer.iAmRedrawing) return;
				pointer.propertyTool.controller.propertyToolController.undoCheckPoint();
				pointer.propertyTool.controller.propertyToolController.view.contextMenu.redraw();
				//call setter w/ value
				attribute['setterFunction'](selectedElement, input.value, input, wasClicked)
			};
		if (attribute['onKeypress'])
		{
			input.onkeypress = attribute['onKeypress'];
		}
		if (attribute['onInput'])
		{
			input.oninput = attribute['onInput'].bind(input);
		}
		input.addEventListener('focus', function(e)
			{
				pointer.focusedElement = e.target;
			});
	}
	else
	{
		input.onchange = function(e)
			{
				if (pointer.iAmRedrawing) return;
				pointer.propertyTool.controller.propertyToolController.undoCheckPoint();
				pointer.propertyTool.controller.propertyToolController.view.contextMenu.redraw();
				if (pointer.focusedElement == e.target)
				{
					pointer.focusedElement = null;
				}
				var attrToUpdate = attribute['attrName']; //the component attribute to set
				var setter = attribute['setterFunction']; //the function to call to apply the change
				var newVal;
				if (attribute.inputType === 'checkbox')
					newVal = input.checked ? 'true' : 'false';
				else
					newVal = input.value;
				var noSet = false;
				if (setter)
				{
					var retVal = setter(selectedElement, newVal, input);
					if (attribute['name'] === 'ID')
					{					
						if (!retVal)
						{
							pointer.showBadIdMsg(true);
							noSet = true;
						}
						else //id successfully changed
						{
							pointer.showBadIdMsg(false);
							if (selectedElement.className.includes('multchoice-element'))
							{
								pointer.updateQuestionComponentNames(selectedElement, newVal);
							}
						}
					}
				}
				if (attrToUpdate && !noSet)
				{
					if (attribute['inputType'] == 'text' || attribute['inputType'] == 'numeric')
					{
						if ((attribute['inputType'] == 'numeric' && !validateNumericStr(newVal))) 
							return; //if input is NaN 
						selectedElement.setAttribute(attrToUpdate, newVal); 
					}
					else if (attribute['inputType'] == 'checkbox')
					{						
						selectedElement.setAttribute(attrToUpdate, newVal);				
					}
				}
				if (attribute['name'] === 'Number of Options')
				{
					pointer.addComboOptionInputs(newVal, iFrameWindow, selectedElement, selectedElement.getElementsByTagName('option'));
				}
			};
		//Callback to track which input has focus
		input.addEventListener('focus', function(e)
			{
				console.log('focus');
				pointer.focusedElement = e.target;
			});
	}
};

/**
*	Adds inputs corresponding to combobox options to the attribute pane of the property editor.
*	Called from redraw() and setAttrListener()
*	@param numOptions the number of inputs to add (size of the combobox)
*	@param stageFrame the iFrame window object, needed to call functions on the stage's
*		instance of CTATTutor
*	@param comboElement the div containing the combobox component
*	@param values an optional array of <option> nodes whose values will pre-populate the inputs
*/
silex.view.pane.AttributePane.prototype.addComboOptionInputs = function(numOptionsStr, stageFrame, comboElement, values)
{
	var input; //the input DOM node
	var label; //label for the DOM node
	var inputDiv; //wrapper for the input
	var pointer = this;
	
	let numOptions = parseInt(numOptionsStr);
	if (!numOptions) 
		return; //if NaN
	
	//Array of inputs that are already in the pane
	var existingOptions = this.attrList.getElementsByClassName('combo-option');
	var numExistingOptions = existingOptions ? existingOptions.length : 0;
	//callback that will be passed to CTATTutor.callComponentFunction()
	var setComboOptions = function(component, value)
	{
		component.setLabels(value);
	}
	//Callback to track which input has focus
	var onFocus = function(e)
	{
		console.log('focus');
		pointer.focusedElement = e.target;
	};
	//Callback for option inputs value change event
	//builds a string from all inputs and passes it to setComboOptions()
	var onChange = function(e)
	{
		if (!pointer.focusedElement)
		{
			return;
		}
		let options = pointer.attrList.getElementsByClassName('combo-option');
		//reset focused element property
		if (e && pointer.focusedElement == e.target)
		{
			pointer.focusedElement = null;
		}
		//Concatenation of all options
		var optionString = "";
		var optionArrLen = options.length;
		//build option string
		for (var j = 0; j < optionArrLen; j++)
		{
			var thisOption = options[j].value;
			if (!thisOption) thisOption = '';
			optionString += thisOption;
			if (j < numOptions-1)
			{
				optionString += ',';
			}
		}
		if (stageFrame)
		{
			stageFrame.CTATTutor.callComponentFunction(comboElement, setComboOptions, optionString);
		}
		comboElement.setAttribute('data-ctat-labels', optionString);
	};

	if (numOptions > numExistingOptions) //need to add options
	{
		for (var i = numExistingOptions; i < numOptions; i++)
		{
			//create DOM node
			inputDiv = document.createElement('div');
			inputDiv.setAttribute('id', 'input-for-combo-option-'+(i+1));
			label = document.createTextNode('Option '+(i+1)+':');
			inputDiv.appendChild(label);
			input = document.createElement('input');
			input.setAttribute('type', 'text');
			input.classList.add('combo-option');
			input.classList.add('input-text');
			//add listeners
			input.onchange = onChange;
			input.addEventListener('focus', onFocus);
			//if preexisting value, set that too
			if (values && values[i])
			{
				input.setAttribute('value', values[i].getAttribute('value'));
			}
			inputDiv.appendChild(input);
			this.attrList.appendChild(inputDiv);
		}
		if (numOptions > values.length) //if more inputs than existing <option> tags
		{
			//add blank tags
			stageFrame.CTATTutor.callComponentFunction(comboElement, function(component, value)
				{
					component.addBlank(value);
				}, numOptions - values.length);
		}
	}
	else if (numOptions < numExistingOptions) //need to remove options
	{
		while (numOptions < numExistingOptions)
		{
			this.attrList.removeChild(this.attrList.lastChild);
			numExistingOptions--;
		}
		
		onChange(); //call to update <option> tags on selected element
	}
};

silex.view.pane.AttributePane.prototype.initGroupingComponent = function(element)
{
	//list of member comps
	//those listed as an attr
	var memberList = element.getAttribute('data-ctat-componentlist');
	if (memberList)
		memberList = memberList.split(',');
	else
		memberList = [];
	//those that are children
	for (let i = 0; i < element.childNodes.length; i++)
	{
		if (silex.utils.CTAT.getCTATClassName(element.childNodes[i]))
			memberList.push(element.childNodes[i].getAttribute('id'));
	}
	var listElement = document.createElement('ul');
	for (let i = 0; i < memberList.length; i++)
	{
		let listItem = document.createElement('li');
		listItem.appendChild(document.createTextNode(memberList[i]));
		let delButton = document.createElement('button');
		delButton.setAttribute('data-silex-target', memberList[i]);
		delButton.textContent = '-';
		delButton.addEventListener('click', onGroupingComponentDelete.bind(this, listItem, memberList[i]));
		listItem.appendChild(delButton);
	}
	//add component button
	var addBtn = document.createElement('button');
	addBtn.textContent = 'Add';
	var addTxt = document.createElement('input');
	addTxt.setAttribute('type', 'text');
	addTxt.setAttribute('placeholder', 'element-ID');
	addTxt.setAttribute('id', 'grouping-component-add');
}

function onGroupingComponentDelete(listItem, id)
{
	var selected = goog.dom.getElement(pointer.selectedId, this.model.file.getContentDocument());
	if (goog.dom.getElement(id, selected))
	{
		error();
	}
};

/**
 * Shows or hides a message about constraints on ID values for
 * CTAT components.
 * @param toShow {boolean} true if the message should be added
		false if the message should be removed
 */
silex.view.pane.AttributePane.prototype.showBadIdMsg = function(toShow)
{	
	var msg = goog.dom.getElement('bad-id-msg', this.attrList);
	if (!msg && toShow)
	{
		if (toShow)
		{
			var msg = document.createElement('p');
			msg.appendChild(document.createTextNode('IDs must be unique, cannot be "done" or "hint", and cannot contain spaces'));
			msg.setAttribute('id', 'bad-id-msg');
			msg.setAttribute('style', 'color: black;');
			var idInput = goog.dom.getElement('input-for-id');
			this.attrList.insertBefore(msg, idInput.nextSibling);
		}
	}
	else if (msg && !toShow)
	{
		this.attrList.removeChild(msg);
	}
}

function validateNumericStr(str)
{
	var regex = /[0-9]{1,}/
	return regex.test(str);
}

/**
 * Given an array from the CTATComponentAttributes object, sorts
 * it so that certain attributes are always in the same place
 * @param attributes {array} the array of attribute objects
 */
silex.view.pane.AttributePane.prototype.buildAttrArray = function(attributes)
{
	var baseGroups = attributes[0];
	var attrArray = [];
	var checkboxInputs = [];
	var numericInputs = [];
	var textInputs = [];
	var comboInputs = [];
	for (let i = 0; i < baseGroups.length; i++)
	{
		//id, then base (enabled, graded, etc.) then everything else
		if (baseGroups[i] === 'ID')
		{
			attrArray = CTATComponentAttributes['ID'].concat(attrArray);
		}
		else if (baseGroups[i] === 'Base')
		{
			attrArray = attrArray.concat(CTATComponentAttributes['Base']);
		}
		else
		{
		//	attributes = attributes.concat(CTATComponentAttributes[baseGroups[i]]);
			attributes = CTATComponentAttributes[baseGroups[i]].concat(attributes);
		}
	}
	for (let i = 0; i < attributes.length; i++)
	{
		if (attributes[i]['inputType'])
		{
			if (attributes[i]['inputType'] === 'checkbox')
				checkboxInputs.push(attributes[i]);
			else if (attributes[i]['inputType'] === 'numeric')
				numericInputs.push(attributes[i]);
			else if (attributes[i]['inputType'] === 'text')
				textInputs.push(attributes[i]);
			else
				comboInputs.push(attributes[i]);
		}
	}
	attrArray = attrArray.concat(checkboxInputs);
	attrArray = attrArray.concat(numericInputs);
	attrArray = attrArray.concat(textInputs);
	attrArray = attrArray.concat(comboInputs);
	return attrArray;
};

silex.view.pane.AttributePane.prototype.updateQuestionComponentNames = function(questionEl, newName)
{
	var comps = $(questionEl).find('.CTATComponent');
	for (let i = 0; i < comps.length; i++)
	{
		comps[i].setAttribute('name', newName);
		comps[i].setAttribute('id', newName+'-option-'+i);
	}
};

silex.view.pane.AttributePane.prototype.populateClasslist = function(element)
{
	let classes = element.className.split(' ');
	this.propertyTool.resetClasslist();
	for (let i = 0; i < classes.length; i++)
	{
		if (!(silex.Config.RestrictedClasses[classes[i]] || classes[i].includes('silex-id')))
		{
			let added = this.propertyTool.addToClasslist(classes[i], 'remove');
			if (!added)
			{
				this.propertyTool.setClasslistItemText(classes[i], 'remove');
			}
		}
	}
};

silex.view.pane.AttributePane.prototype.getAttributeList = function(className)
{
	var classArray = className.split(' ');
	var numClasses = classArray.length;
	var thisClass;
	var allAttr = [];
	for (var i = 0; i < numClasses; i++)
	{
		thisClass = classArray[i];
		if (CTATComponentAttributes.hasOwnProperty(thisClass))
		{
			if (thisClass.includes("CTAT"))
			{
				allAttr = CTATComponentAttributes[thisClass].concat(allAttr);
			}
			else
			{
				allAttr = allAttr.concat(CTATComponentAttributes[thisClass]);
			}
		}
	}
	
	return allAttr;
}

silex.view.pane.AttributePane.prototype.updateAttr = function(attribute, optEl)
{
	if (!optEl)
	{
		optEl = this.model.body.getSelection();
		if (optEl.length !== 1) return;
		
		optEl = optEl[0];
	}
	let val = optEl.getAttribute(attribute);
	var title;
	if (val && this.model.file.imgUrlMap[val])
	{
		val = this.model.file.imgUrlMap[val]['name'];
		title = val;
	}
	else if (val && (val.includes('http://') || val.includes('https://')))
	{
		title = val;
		val = val.split('/').pop().split('?')[0];
	}
	
	let attrDiv = this.element.querySelector('div[data-silex-attr-name="'+attribute+'"]');
	let valElement = $(attrDiv).find('.silex-asset-label')[0];
	if (valElement)
	{
		valElement.textContent = val;
		valElement.title = title;
	}
	else
	{
		valElement = $(attrDiv).find('input');
		if (valElement)
		{
			if (valElement.type === 'checkbox')
			{
				if (!val || val === 'false')
					valElement.checked = false;
				else
					valElement.checked = true;
			}
			else
				valElement.value = val;
		}
	}
}
