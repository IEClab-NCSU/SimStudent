/**
*	@fileoverview: Each type of ctat component is mapped to an array of
*	attribute objects, which are used to populate the property tool in the
*	editor and manipulate the component's properties.
*	The first element in each component's array is an array of base attribute groups
*		that that component inherits
*
*	The CTATStyleHandlers object maps style attributes to getter/setter functions
*	which can be used to access/modify CTAT component styles by passing them to
*	CTATTutor.callComponentFunction()
*/

goog.provide("CTATComponentAttributes");
var CTATComponentAttributes = {
	/* Component Base Groups */
	'ID' : [
		{
			'name': 'ID',
			'attrName': 'id',
			'inputType': 'text',
			'description': 'The HTML ID attribute of the component',
			'default': '',
			'setterFunction': function(element, value)			
			{
				//check if valid id val
				if (!( value === 'hint' || value === 'done' || value === '' || value.includes(' ')))
				{
					var stageEl = window.silexApp.model.file.getContentDocument();
					var el = stageEl.getElementById(value);
					//check if already taken
					if (!el)
					{
						element.setAttribute('id', value);
						var comps = window.silexApp.model.file.getContentWindow().CTATShellTools.findComponent(value)
						var comp = comps[0];
						console.log('found comp = '+JSON.stringify(comp));
						if (comp && comp.setName)
						{
							value = value.trim();
							comp.setName(value);
						}
						return true;
					}
				}
				return false;
			}
		}
	],
	'Class': [
		{
			'name': 'Classes',
			'attrName': null,
			'inputType': 'datalist',
			'listID': 'property-tool-classlist',
			'description': 'CSS classes applied to this component',
			'default': '',
			'onKeypress': function(e)
			{
				silexApp.view.propertyTool.setLastKeyPressed(e.key);
			},
			'onInput': function(e)
			{
				let value = this.value;
				if (silexApp.view.propertyTool.checkClasslist(value))
				{
					let lastChar = value.charAt(value.length-1);
					let minusOne = value.substring(0, value.length-1);
					let lastPressed = silexApp.view.propertyTool.getLastKeyPressed();
					let lastInput = silexApp.view.propertyTool.getLastInput();
					if (!(lastPressed == lastChar && lastInput == minusOne))
					{
						//means datalist entry was clicked, call setter
						this.onchange(true)
						silexApp.view.propertyTool.setLastInput('');
						silexApp.view.propertyTool.setLastKeyPressed('');
					}
					else
					{
						silexApp.view.propertyTool.setLastInput(value);
					}
				}
				else
				{
					silexApp.view.propertyTool.setLastInput(value);
				}
			},
			'setterFunction': function(element, value, input, wasClicked)
			{
				if (!value || silex.Config.RestrictedClasses[value])
				{
					return;
				}
				let added = silexApp.view.propertyTool.addToClasslist(value, 'remove');
				if (added)
				{
					element.classList.add(value);
				}
				else
				{
					if (element.className.includes(value))
					{
						if (wasClicked === true)
						{
							element.classList.remove(value);
							silexApp.view.propertyTool.setClasslistItemText(value, 'add');
						}
					}
					else
					{
						element.classList.add(value);
						silexApp.view.propertyTool.setClasslistItemText(value, 'remove');
					}
				}		
				input.value = '';
			}
		}
	],
	'Base': [
		{
			'name': 'Enabled',
			'attrName': 'data-ctat-enabled',
			'inputType': 'checkbox',
			'description': 'Whether the component is interactive at run time',
			'default': 'true',
			'setterFunction': null //function(comp, value){comp.setEnabled(value);}
		},
		{
			'name': 'Tutored',
			'attrName': 'data-ctat-tutor',
			'inputType': 'checkbox',
			'description': 'Whether the component is graded on input',
			'default': 'true',
			'setterFunction': function(el, value, input)
			{
				let feedbackInput = document.querySelector('#input-for-show-feedback > input');
				if (!feedbackInput)
				{
					console.warn("couldn't find 'show feedback' input");
					return;
				}
				if (!input.checked)
				{
					feedbackInput.checked = false;
					feedbackInput.setAttribute('disabled', 'true');
					el.setAttribute('data-ctat-show-feedback', 'false');
				}
				else
				{
					feedbackInput.removeAttribute('disabled');
				}
			}
		},
		{
			'name': 'Show Feedback',
			'attrName': 'data-ctat-show-feedback',
			'inputType': 'checkbox',
			'description': 'Whether the component will signify if the input is correct or not',
			'default': 'true'
		},
		{
			'name': 'Show Highlight',
			'attrName': 'data-ctat-show-hint-highlight',
			'inputType': 'checkbox',
			'description': 'Whether hints can be provided for this component',
			'default': 'true'
		},
		{
			'name': 'Disable on Correct',
			'attrName': 'data-ctat-disable-on-correct',
			'inputType': 'checkbox',
			'description': 'Whether the component becomes non-interactive once the correct answer is given',
			'default': 'true'
		}
	],
	'CTATTextBased': [
		{
			'name': 'Font Size:',
			'attrName': 'data-ctat-font-size',
			'inputType': 'numeric',
			'description': 'The size of the text (in px) in the component',
			'default': '16',
			'setterFunction': function(element, value)
				{
					let iframe = window.silexApp.model.file.getContentWindow();
					window.silexApp.model.element.setStyle(element, 'fontSize', value+'px');
				}
		}
	],
	'CTATUnitDisplay': [
		{
			'name': 'Value',
			'attrName': 'data-ctat-value',
			'inputType': 'text',
			'description': 'The value of the fraction represented by the component',
			'default': '1/2+1/4+(0*1/8)',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value){comp.setValue(value);}, value);
				}
		},
		{
			'name': 'Numer Ctrls',
			'attrName': 'data-ctat-ctrl-numerator',
			'inputType': 'text',
			'description': 'A \';\' list of components that control the numerator value',
			'default': '',
			'setterFunction': null
		},
		{
			'name': 'Denom Ctrls',
			'attrName': 'data-ctat-ctrl-denominator',
			'inputType': 'text',
			'description': 'A \';\' list of components that control the denominator value',
			'default': '',
			'setterFunction': null
		},
		{
			'name': 'Partition Ctrls',
			'attrName': 'data-ctat-ctrl-partition',
			'inputType': 'text',
			'description': 'A \';\' separated list of components that control the number of partitions',
			'default': '',
			'setterFunction': null
		}
	],
	'CTATLabeled': [
		{
			'name': 'Label',
			'attrName': 'data-ctat-label',
			'inputType': 'text',
			'description': 'The value of the label displayed on the component',
			'default': "",
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element, 
						function(comp, value){comp.setText(value);}, value);
				}
		}
	],
	'Tabbable': [
		{
			'name': 'Tab Order',
			'attrName': 'data-ctat-tabindex',
			'inputType': 'numeric',
			'description': 'The order in which components can be focused by the tab key (lower = earlier)',
			'default': '',
			'setterFunction': function(element, value)
				{
					silexApp.model.element.setTabOrderLabel(element, value);
				}
		}
	],
	'Grouped': [
		{
			'name': 'Group Name',
			'attrName': 'name',
			'inputType': 'text',
			'description': 'The name of the group of components the component belongs to. Component groups are graded as one entity',
			'default': '',
			'setterFunction': null
		}
	],
	/*End Component Base Groups*/
	'CTATAudioButton': [
		['ID', 'Class', 'Base', 'CTATLabeled', 'Tabbable'],
		{
			'name': 'Audio Src',
			'attrName': 'data-ctat-src',
			'inputType': 'button',
			'description': 'Choose the source of the audio played when the button is clicked',
			'default': '',
			'setterFunction': function(element, value)
				{
					silexApp.controller.propertyToolController.audioElement = element;
					silexApp.view.stage.showFileSourceWindow('audio', 
													function(url)
													{
														silexApp.controller.propertyToolController.setAudioUrl(url);
														silexApp.view.propertyTool.attributePane.updateAttr('data-ctat-src', element);
													},
													function(fileData) {
														this.setBlobAudioUrl(fileData.id, fileData.data, fileData.name);
														silexApp.view.propertyTool.attributePane.updateAttr('data-ctat-src', element);
													}.bind(silexApp.controller.propertyToolController));
													
				}
		},
	],
	'CTATImageButton': [
		['ID', 'Class', 'Base', 'Tabbable'],
		{
			'name': 'Value',
			'attrName': 'value',
			'inputType': 'text',
			'description': 'The value sent by clicking the button',
			'default': '-1'
		},
		{
			'name': 'Default',
			'attrName': 'data-ctat-image-default',
			'inputType': 'button',
			'description': 'The background image for the button',
			'default': '',
			'setterFunction': function(element, value)
			{
				silexApp.view.stage.showFileSourceWindow('image',
														function(url)
														{
															silexApp.controller.propertyToolController.setImgButtonPropertyUrl(element,
																															  url,
																															  'data-ctat-image-default');
															silexApp.view.propertyTool.attributePane.updateAttr('data-ctat-image-default', element);
														},
														function(fileData)
														{
															silexApp.controller.propertyToolController.setImgButtonPropertyBlob(element,
																															   fileData,
																															   'data-ctat-image-default');
															silexApp.view.propertyTool.attributePane.updateAttr('data-ctat-image-default', element);
														});
			}
		},
		{
			'name': 'Hover',
			'attrName': 'data-ctat-image-hover',
			'inputType': 'button',
			'description': 'The image displayed when the button is hovered over',
			'default': '',
			'setterFunction': function(element, value)
			{
				silexApp.view.stage.showFileSourceWindow('image',
														function(url)
														{
															silexApp.controller.propertyToolController.setImgButtonPropertyUrl(element,
																															  url,
																															  'data-ctat-image-hover');
															silexApp.view.propertyTool.attributePane.updateAttr('data-ctat-image-hover', element);
														},
														function(fileData)
														{
															silexApp.controller.propertyToolController.setImgButtonPropertyBlob(element,
																															   fileData,
																															   'data-ctat-image-hover');																
															silexApp.view.propertyTool.attributePane.updateAttr('data-ctat-image-hover', element);
														});
			}
		},
		{
			'name': 'Click',
			'attrName': 'data-ctat-image-clicked',
			'inputType': 'button',
			'description': 'The image displayed when the button is clicked',
			'default': '',
			'setterFunction': function(element, value)
			{
				silexApp.view.stage.showFileSourceWindow('image',
														function(url)
														{
															silexApp.controller.propertyToolController.setImgButtonPropertyUrl(element,
																															  url,
																															  'data-ctat-image-clicked');
															silexApp.view.propertyTool.attributePane.updateAttr('data-ctat-image-clicked', element)
														},
														function(fileData)
														{
															silexApp.controller.propertyToolController.setImgButtonPropertyBlob(element,
																															   fileData,
																															   'data-ctat-image-clicked');
															silexApp.view.propertyTool.attributePane.updateAttr('data-ctat-image-clicked', element);
														});
			}
		},
		{
			'name': 'Disabled',
			'attrName': 'data-ctat-image-disabled',
			'inputType': 'button',
			'description': 'The image displayed when the button is disabled',
			'default': '',
			'setterFunction': function(element, value)
			{
				silexApp.view.stage.showFileSourceWindow('image',
														function(url)
														{
															silexApp.controller.propertyToolController.setImgButtonPropertyUrl(element,
																															  url,
																															  'data-ctat-image-disabled');
															silexApp.view.propertyTool.attributePane.updateAttr('data-ctat-image-disabled', element);
														},
														function(fileData)
														{
															silexApp.controller.propertyToolController.setImgButtonPropertyBlob(element,
																															   fileData,
																															   'data-ctat-image-disabled');
															silexApp.view.propertyTool.attributePane.updateAttr('data-ctat-image-disabled', element);
														});
			}
		}
	],
	'CTATButton': [
		['ID', 'Class', 'Base','CTATLabeled', 'Tabbable'],
		{
			'name': 'Value',
			'attrName': 'value',
			'inputType': 'text',
			'description': 'The value sent by clicking the button',
			'default': '-1'
		}
	],
	'CTATSubmitButton': [
		['ID', 'Class', 'Base', 'CTATLabeled', 'Tabbable'],	
		{
			'name': 'Target ID',
			'attrName': 'data-ctat-target',
			'inputType': 'text',
			'description': 'The component ID or group name for which this component triggers grading',
			'default': ''
		}
	],
	'CTATCheckBox': [
		['ID', 'Class', 'Base','CTATLabeled', 'Grouped', 'Tabbable']
	],
	'CTATRadioButton': [
		['ID', 'Class', 'Base','CTATLabeled', 'Grouped', 'Tabbable'],
	],
	'CTATComboBox': [
		['ID', 'Class', 'Base', 'CTATTextBased', 'Tabbable'],
		{
			'name': 'Number of Options',
			'attrName': null,
			'inputType': 'numeric',
			'description': 'The number of options in the dropdown',
			'default': '',
			'setterFunction': null
		},
	],
	'CTATHintWidget': [
		['ID', 'Class'],
		{
			'name': 'Hints Enabled',
			'attrName': 'data-ctat-hint-enabled',
			'inputType': 'checkbox',
			'description': 'Whether hints are available at run time',
			'default': 'true',
			'setterFunction': function(element, value)
				{
					var hintBtn = element.getElementsByClassName('CTATHintButton')[0];
					var enabled = (!value || value === 'false') ? 'false' : 'true';
					hintBtn.setAttribute('data-ctat-enabled', enabled);
				}
		},
		{
			'name': 'Done Button Enabled',
			'attrName': 'data-ctat-done-enabled',
			'inputType': 'checkbox',
			'description': 'Whether the done button is enabled at run time',
			'default': 'true',
			'setterFunction': function(element, value)
				{
					var doneBtn = element.getElementsByClassName('CTATDoneButton')[0];
					var enabled = (!value || value === 'false') ? 'false' : 'true';
					doneBtn.setAttribute('data-ctat-enabled', enabled);
				}
		},
		{
			'name': 'Done Btn. Tab Order',
			'attrName': 'data-ctat-done-tabindex',
			'inputType': 'numeric',
			'description': 'The index in the tab order of the "Done" button',
			'default': '',
			'setterFunction': function(element, value)
				{
					let doneBtn = element.querySelector('.CTATDoneButton');
					doneBtn.setAttribute('data-ctat-tabindex', value);
					silexApp.model.element.setTabOrderLabel(doneBtn);
				}
		},
		{
			'name': 'Done Btn. Show Highlight',
			'attrName': 'data-ctat-done-highlight',
			'inputType': 'checkbox',
			'description': 'Whether the done button will be highlighted by hints',
			'default': 'true',
			'setterFunction': function(element, value)
				{
					var doneBtn = element.getElementsByClassName('CTATDoneButton')[0];
					var enabled = (!value || value === 'false') ? 'false' : 'true';
					doneBtn.setAttribute('data-ctat-show-hint-highlight', enabled);
				}
		}
	],
	'CTATSkillWindow': [
		['ID', 'Class'],
		{
			'name': 'Mastery Threshold',
			'attrName': 'data-ctat-threshold',
			'inputType': 'text',
			'description': 'How full a skill bar must be before the skill is considered "mastered"',
			'default': '0.95',
			'setterFunction': null
		}
	],
	'CTATFractionBar': [
		['ID', 'Class', 'Base', 'CTATUnitDisplay', 'Tabbable']			
	],
	'CTATNumberLine': [
		['ID', 'Class', 'Base', 'Tabbable'],
		{
			'name': 'Max Value',
			'attrName': 'data-ctat-maximum',
			'inputType': 'numeric',
			'description': 'The maximum value on the number line',
			'default': '3',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value){comp.setMaximum(value);}, value);
				}
		},
		{
			'name': 'Min Value',
			'attrName': 'data-ctat-minimum',
			'inputType': 'numeric',
			'description': 'The minimum value on the number line',
			'default': '0',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value){comp.setMinimum(value);}, value);
				}
		},
		{
			'name': 'Large Increment',
			'attrName': 'data-ctat-large-tick-step',
			'inputType': 'numeric',
			'description': 'The distance covered by each large tick on the numberline',
			'default': '1',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value){comp.set_large_step(value);}, value);
				}
		},
		{
			'name': 'Small Increment',
			'attrName': 'data-ctat-small-tick-step',
			'inputType': 'numeric',
			'description': 'The distance covered by each small tick on the numberline',
			'default': '1/2',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value){comp.set_small_step(value);}, value);
				}
		},
		{
			'name': 'Denominator',
			'attrName': 'data-ctat-denominator',
			'inputType': 'numeric',
			'description': 'Controls the number of denominator ticks',
			'default': '0',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value){comp.set_denominator(value);}, value);
				}
		},
		{
			'name': 'Number of Points',
			'attrName': 'data-ctat-max-points',
			'inputType': 'numeric',
			'description': 'The number of points that can be input on the numberline',
			'default': '1',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value){comp.setMaxPoints(value);}, value);
				}
		},
		{
			'name': 'Point Size',
			'attrName': 'data-ctat-point-size',
			'inputType': 'numeric',
			'description': 'The size of points on the numberline',
			'default': '7',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value){comp.setPointSize(value);}, value);
				}
		},
		{
			'name': 'Snap to Tick Marks',
			'attrName': 'data-ctat-snap',
			'inputType': 'checkbox',
			'description': 'Whether points "snap" to the nearest tick mark',
			'default': 'false',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value){comp.setSnapToTickMark(value);}, value);
				}
		},
		{
			'name': 'Angle',
			'attrName': 'data-ctat-rotation',
			'inputType': 'numeric',
			'description': 'The angle (off the x-axis) the numberline is rotated',
			'default': '0',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value){comp.setOrientation(value);}, value);
				}
		},
		{
			'name': 'Max Val Ctrls',
			'attrName': 'data-ctat-ctrl-max',
			'inputType': 'text',
			'description': 'A ; separated list of components that control the maximum value',
			'default': ''
		},
		{
			'name': 'Min Val Ctrls',
			'attrName': 'data-ctat-ctrl-min',
			'inputType': 'text',
			'description': 'A ; separated list of components that control the minimum value',
			'default': ''
		},
		{
			'name': 'Sm Tick Ctrls',
			'attrName': 'data-ctat-ctrl-small-tick',
			'inputType': 'text',
			'description': 'A ; separated list of components that control the distance covered by small ticks',
			'default': ''
		},
		{
			'name': 'Lg Tick Ctrls',
			'attrName': 'data-ctat-ctrl-large-tick',
			'inputType': 'text',
			'description': 'A ; separated list of components that control the distance covered by large ticks',
			'default': ''
		},
		{
			'name': 'Denom Ctrls',
			'attrName': 'data-ctat-ctrl-denominator',
			'inputType': 'text',
			'description': 'A ; separated list of components that control the distance between denominator tick marks',
			'default': ''
		}
	],
	'CTATTextArea': [
		['ID', 'Class', 'Base', 'CTATTextBased', 'Tabbable'],
		{
			'name': "Tab on Enter",
			'attrName': 'data-ctat-tab-on-enter',
			'inputType': 'checkbox',
			'description': 'Whether the enter key will cause the component to lose focus',
			'default': 'true'
		}
	],
	'CTATTextInput': [
		['ID', 'Class', 'Base', 'CTATTextBased', 'Tabbable'],
		{
			'name': 'Max Length',
			'attrName': 'maxLength',
			'inputType': 'numeric',
			'description': 'The maximum number of characters that can be entered',
			'default': '30',
			'setterFunction': function(element, value)
				{
					window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
						function(comp, value)		
						{
							var input = comp.getDivWrap().getElementsByTagName('input')[0];
							if (input)
								input.setAttribute('maxlength', value);
						}, value);
				}
		},
		{
			'name': "Has Initial Focus",
			'attrName': 'autofocus',
			'inputType': 'checkbox',
			'description': 'Whether the component will have focus when the tutor is loaded',
			'default': 'false',
			'setterFunction': function(element, value)
			{
				window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
					function(comp, value)
					{
						var input = comp.getDivWrap().getElementsByTagName('input')[0];
						if (input)
							input.setAttribute('autofocus', value);
					}, value);
			}
		}
	],
	'CTATTextField': [
		['ID', 'Class', 'Base']
	],
	'CTATPieChart': [
		['ID', 'Class', 'Base', 'CTATUnitDisplay', 'Tabbable'],
		{
			'name': 'Spread Distance',
			'attrName': 'data-ctat-explode',
			'inputType': 'numeric',
			'description': 'The distance between each "piece" in the pie chart',
			'default': '3',
			'setterFunction': function(element, value)
			{	
				window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
					function(comp, value){comp.setExplode(value);}, value);
			}
		},
		{
			'name': 'Shadow Length',
			'attrName': 'data-ctat-shadow-distance',
			'inputType': 'numeric',
			'description': 'The length of the pieces\' shadows',
			'default': '3',
			'setterFunction': function(element, value)
			{
				window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
					function(comp, value){comp.setDropShadowDistance(value);}, value);
			}
		}
	],
	'CTATTable': [
		['ID', 'Class', 'Base', 'Tabbable'],
		{
			'name': 'Row Count',
			'attrName': 'data-ctat-num-rows', 
			'inputType': 'numeric',
			'description': 'The number of rows in the table',
			'default': '2',
			'setterFunction': function(element, value)
			{
				window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
					function(comp, value)
					{
						value = parseInt(value, 10);
						if (value < 1) return;
						var numRows = comp.get_row_count();
						while (numRows != value)
						{
							if (numRows < value)
							{
								comp.addRow();
								numRows++;
							}
							else
							{
								comp.deleteRow();
								numRows--;
							}
						}
					}, value);
				window.silexApp.model.element.resizeTableHeight(element);
			}
		},
		{
			'name': 'Column Count',
			'attrName': 'data-ctat-num-cols',
			'inputType': 'numeric',
			'description': 'The number of columns in the table',
			'default': '2',
			'setterFunction': function(element, value)
			{
				window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
					function(comp, value)
					{
						value = parseInt(value, 10);
						if (value < 1) return;
						var numCols = comp.get_col_count();
						while (numCols != value)
						{
							if (numCols < value)
							{
								comp.addColumn();
								numCols++;
							}
							else
							{
								comp.deleteColumn();
								numCols--;
							}
						}
					}, value);
				window.silexApp.model.element.resizeTableWidth(element);
			}
		},
		{
			'name': 'Has Header',
			'attrName': 'data-ctat-has-header',
			'inputType': 'checkbox',
			'description': 'Determines whether the first row of the table is a header row',
			'default': 'false',
			'setterFunction': function(element, value)
			{
				console.log('has header setter, value = '+value);
				var header = true;
				if (!value || value === 'false')
				{
					header = false;
				}
				window.silexApp.model.file.getContentWindow().CTATTutor.callComponentFunction(element,
					function(comp, value)
					{
						if (!header)
						{
							comp.removeHeader();
						}
						else
						{
							comp.fixHeader();
						}
					});
			}
		}
	],
	'CTATJumble': [
		['ID', 'Class', 'Base', 'Tabbable'],
		{
			
		}
	],
	'CTATDragNDrop': [
		['ID', 'Class', 'Base', 'Grouped', 'Tabbable'],
		{
			'name': 'Max # of Items',
			'attrName': 'data-ctat-max-cardinality',
			'inputType': 'numeric',
			'description': 'The maximum number of items that can be dropped into the component',
			'default': '',
			'setterFunction': null
		}
	],
	'CTATHintButton': [
		[],
		{
			'name': 'Enabled',
			'attrName': 'data-ctat-enabled',
			'inputType': 'checkbox',
			'description': 'Whether the component is interactive at run time',
			'default': 'true',
			'setterFunction': null //function(comp, value){comp.setEnabled(value);}
		}
	],
	'CTATDoneButton': [
		['Tabbable', 'Class'],
		{
			'name': 'Enabled',
			'attrName': 'data-ctat-enabled',
			'inputType': 'checkbox',
			'description': 'Whether the component is interactive at run time',
			'default': 'true',
			'setterFunction': null //function(comp, value){comp.setEnabled(value);}
		},
		{
			'name': 'Show Highlight',
			'attrName': 'data-ctat-show-hint-highlight',
			'inputType': 'checkbox',
			'description': 'Whether hints can be provided for this component',
			'default': 'true'
		}
	],
	'question.multchoice-element': [
		['ID', 'Class']
	],
	'container-element': [
		['ID', 'Class'],
		{
			'name': 'Scroll X',
			'attrName': 'data-silex-scroll-x',
			'inputType': 'checkbox',
			'description': 'Whether content overflowing horizontally will cause the component to scroll',
			'default': 'false',
			'setterFunction': function(element, value, input)
			{
				silexApp.model.element.setScroll(element, 'x', input.checked);
			}
		},
		{
			'name': 'Scroll Y',
			'attrName': 'data-silex-scroll-y',
			'inputType': 'checkbox',
			'description': 'Whether content overflowing vertically will cause the component to scroll',
			'default': 'false',
			'setterFunction': function(element, value, input)
			{
				silexApp.model.element.setScroll(element, 'y', input.checked);
			}
		}
	],
	'image-element': [
		['ID', 'Class']
	]
}

/**
*	Map of CTAT components to style names and selectors
*	CTATComponentStyleMappings[<CTATClassName>]['styleAttributes'][<styleName>][0] = CSS style name for that style 
*	CTATComponentStyleMappings[<CTATClassName>]['styleAttributes'][<styleName>][1] = selector suffix that will be appended to element ID
*/
CTATComponentStyleMappings = {
	
	'defaultStyleAttr': {
		'margin': 'margin',
			'marginTop': 'marginTop',
			'marginBottom': 'marginBottom',
			'marginRight': 'marginRight',
			'marginLeft': 'marginLeft',
		'padding': 'padding',
			'paddingTop': 'paddingTop',
			'paddingBottom': 'paddingBottom',
			'paddingRight': 'paddingRight',
			'paddingLeft': 'paddingLeft',
		'fontSize': 'font-size',
		'fontFamily': 'font-family',
		'fontWeight': 'font-weight',
		'fontStyle': 'font-style',
		'textAlign': 'text-align',
		'color': 'color',
		'backgroundColor': 'background-color',
		'backgroundImage': 'background-image',
		'backgroundSize': 'background-size',
		'backgroundRepeat':'background-repeat',
		'backgroundPosition':'background-position',
		'borderWidth': 'border-width',
			'borderTopWidth': 'border-top-width',
			'borderBottomWidth': 'border-bottom-width',
			'borderRightWidth': 'border-right-width',
			'borderLeftWidth': 'border-left-width',
		'borderColor': 'border-color',
			'borderTopColor': 'border-top-color',
			'borderLeftColor': 'border-left-color',
			'borderRightColor': 'border-right-color',
			'borderBottomColor': 'border-bottom-color',
		'borderStyle': 'border-style',
			'borderTopStyle': 'border-top-style',
			'borderBottomStyle': 'border-bottom-style',
			'borderRightStyle': 'border-right-style',
			'borderLeftStyle': 'border-left-style',
		'borderRadius': 'border-radius',
			'borderTopLeftRadius': 'border-top-left-radius',
			'borderTopRightRadius': 'border-top-right-radius',
			'borderBottomLeftRadius': 'border-bottom-left-radius',
			'borderBottomRightRadius': 'border-bottom-right-radius'
	},	
	'CTATTextInput': {
		'innerTagName': 'input',
		'usesDefault': false,
		'styleAttributes': {
			'margin': ['margin', ''],
				'marginTop': ['marginTop', ''],
				'marginBottom': ['marginBottom', ''],
				'marginRight': ['marginRight', ''],
				'marginLeft': ['marginLeft', ''],
			'padding': ['padding', 'input'],
				'paddingTop': ['paddingTop', 'input'],
				'paddingBottom': ['paddingBottom', 'input'],
				'paddingRight': ['paddingRight', 'input'],
				'paddingLeft': ['paddingLeft', 'input'],
			'fontSize': ['font-size', ''],
			'fontFamily': ['font-family', ''],
			'fontWeight': ['font-weight', ''],
			'fontStyle': ['font-style', ''],
			'textAlign': ['text-align', 'input'],
			'color': ['color', 'input'],
			'backgroundColor': ['background-color', ''],
			'backgroundImage': ['background-image', 'input'],
			'backgroundSize': ['background-size', 'input'],
			'backgroundRepeat': ['background-repeat', 'input'],
			'backgroundPosition':['background-position', 'input'],
			'borderWidth': ['border-width', 'input'],
				'borderTopWidth': ['border-top-width', 'input'],
				'borderBottomWidth': ['border-bottom-width', 'input'],
				'borderRightWidth': ['border-right-width', 'input'],
				'borderLeftWidth': ['border-left-width', 'input'],
			'borderColor': ['border-color', 'input'],
				'borderTopColor': ['border-top-color', 'input'],
				'borderLeftColor': ['border-left-color', 'input'],
				'borderRightColor': ['border-right-color', 'input'],
				'borderBottomColor': ['border-bottom-color', 'input'],
			'borderStyle': ['border-style', 'input'],
				'borderTopStyle': ['border-top-style', 'input'],
				'borderBottomStyle': ['border-bottom-style', 'input'],
				'borderRightStyle': ['border-right-style', 'input'],
				'borderLeftStyle': ['border-left-style', 'input'],
			'borderRadius': ['border-radius', 'input'],
				'borderTopLeftRadius': ['border-top-left-radius', 'input'],
				'borderTopRightRadius': ['border-top-right-radius', 'input'],
				'borderBottomLeftRadius': ['border-bottom-left-radius', 'input'],
				'borderBottomRightRadius': ['border-bottom-right-radius', 'input']
		}
	},
	'CTATTextArea': {
		'innerTagName': 'textarea',
		'usesDefault': true
	},
	'CTATTextField': {
		'innerTagName': '',
		'usesDefault': true
	},
	'CTATButton': {
		'innerTagName': 'button',
		'usesDefault': true
	},
	'CTATAudioButton': {
		'innerTagName': 'button',
		'usesDefault': true
	},
	'CTATImageButton': {
		'innerTagName': '',
		'usesDefault': true
	},
	'CTATSubmitButton': {
		'innerTagName': 'button',
		'usesDefault': true
	},
	'CTATTable': {
		'innerTagName': 'div > div > textarea',
		'usesDefault': false,
		'styleAttributes': {
			'margin': ['margin', ''],
				'marginTop': ['marginTop', ''],
				'marginBottom': ['marginBottom', ''],
				'marginRight': ['marginRight', ''],
				'marginLeft': ['marginLeft', ''],
			'padding': ['padding', ''],
				'paddingTop': ['paddingTop', ''],
				'paddingBottom': ['paddingBottom', ''],
				'paddingRight': ['paddingRight', ''],
				'paddingLeft': ['paddingLeft', ''],
			'textAlign': ['text-align', 'div > div > textarea'],
			'fontSize': ['font-size', 'div > div > textarea'],
			'fontFamily': ['font-family', 'div > div > textarea'],
			'fontWeight': ['font-weight', 'div > div > textarea'],
			'fontStyle': ['font-style', 'div > div > textarea'],
			'color': ['color', 'div > div > textarea'],
			'backgroundColor': ['background-color', 'div > div > textarea'],
			'backgroundImage': ['background-image', 'div > div > textarea'],
			'borderColor': ['border-color', 'div > div'],
				'borderTopColor': ['border-top-color', 'div > div'],
				'borderLeftColor': ['border-left-color', 'div > div'],
				'borderRightColor': ['border-right-color', 'div > div'],
				'borderBottomColor': ['border-bottom-color', 'div > div'],
			'borderStyle': ['border-style', 'div > div'],
				'borderTopStyle': ['border-top-style', 'div > div'],
				'borderBottomStyle': ['border-bottom-style', 'div > div'],
				'borderRightStyle': ['border-right-style', 'div > div'],
				'borderLeftStyle': ['border-left-style', 'div > div'],
			'borderWidth': ['border-width', 'div > div'],
				'borderTopWidth': ['border-top-width', 'div > div'],
				'borderBottomWidth': ['border-bottom-width', 'div > div'],
				'borderRightWidth': ['border-right-width', 'div > div'],
				'borderLeftWidth': ['border-left-width', 'div > div'],
			'borderRadius': ['border-radius', 'div > div'],
				'borderTopLeftRadius': ['border-top-left-radius', 'div > div'],
				'borderTopRightRadius': ['border-top-right-radius', 'div > div'],
				'borderBottomLeftRadius': ['border-bottom-left-radius', 'div > div'],
				'borderBottomRightRadius': ['border-bottom-right-radius', 'div > div']
		}
	},
	'CTATCheckBox': {
		'innerTagName': 'label',
		'usesDefault': true
	},
	'CTATRadioButton': {
		'innerTagName': 'label',
		'usesDefault': true
	},
	'CTATFractionBar': {
		'innerTagName': 'rect',
		'usesDefault': false,
		'styleAttributes': {
			'margin': ['margin', ''],
				'marginTop': ['marginTop', ''],
				'marginBottom': ['marginBottom', ''],
				'marginRight': ['marginRight', ''],
				'marginLeft': ['marginLeft', ''],
			'padding': ['padding', ''],
				'paddingTop': ['paddingTop', ''],
				'paddingBottom': ['paddingBottom', ''],
				'paddingRight': ['paddingRight', ''],
				'paddingLeft': ['paddingLeft', ''],		
			'textAlign': ['text-align', ''],
			'fontSize': ['font-size', ''],
			'fontFamily': ['font-family', ''],
			'fontWeight': ['font-weight', ''],
			'fontStyle': ['font-style', ''],
			'color': ['fill', 'svg > g > rect'],
			'backgroundColor': ['background-color', 'svg'],
			'backgroundImage': ['background-image', 'svg'],
			'borderColor': ['border-color', 'svg'],
				'borderTopColor': ['border-top-color', 'svg'],
				'borderLeftColor': ['border-left-color', 'svg'],
				'borderRightColor': ['border-right-color', 'svg'],
				'borderBottomColor': ['border-bottom-color', 'svg'],
			'borderStyle': ['border-style', 'svg'],
				'borderTopStyle': ['border-top-style', 'svg'],
				'borderBottomStyle': ['border-bottom-style', 'svg'],
				'borderRightStyle': ['border-right-style', 'svg'],
				'borderLeftStyle': ['border-left-style', 'svg'],
			'borderWidth': ['border-width', 'svg'],
				'borderTopWidth': ['border-top-width', 'svg'],
				'borderBottomWidth': ['border-bottom-width', 'svg'],
				'borderRightWidth': ['border-right-width', 'svg'],
				'borderLeftWidth': ['border-left-width', 'svg'],
			'borderRadius': ['border-radius', 'svg'],
				'borderTopLeftRadius': ['border-top-left-radius', 'svg'],
				'borderTopRightRadius': ['border-top-right-radius', 'svg'],
				'borderBottomLeftRadius': ['border-bottom-left-radius', 'svg'],
				'borderBottomRightRadius': ['border-bottom-right-radius', 'svg']
		}
	},
	'CTATPieChart': {
		'innerTagName': 'path',
		'usesDefault': false,
		'styleAttributes': {
			'margin': ['margin', ''],
				'marginTop': ['marginTop', ''],
				'marginBottom': ['marginBottom', ''],
				'marginRight': ['marginRight', ''],
				'marginLeft': ['marginLeft', ''],
			'padding': ['padding', ''],
				'paddingTop': ['paddingTop', ''],
				'paddingBottom': ['paddingBottom', ''],
				'paddingRight': ['paddingRight', ''],
				'paddingLeft': ['paddingLeft', ''],
			'textAlign': ['text-align', ''],
			'fontSize': ['font-size', ''],
			'fontFamily': ['font-family', ''],
			'fontWeight': ['font-weight', ''],
			'fontStyle': ['font-style', ''],
			'color': ['fill', 'svg > g > path'],
			'backgroundColor': ['background-color', 'svg'],
			'backgroundImage': ['background-image', 'svg'],
			'borderColor': ['border-color', 'svg'],
				'borderTopColor': ['border-top-color', 'svg'],
				'borderLeftColor': ['border-left-color', 'svg'],
				'borderRightColor': ['border-right-color', 'svg'],
				'borderBottomColor': ['border-bottom-color', 'svg'],
			'borderStyle': ['border-style', 'svg'],
				'borderTopStyle': ['border-top-style', 'svg'],
				'borderBottomStyle': ['border-bottom-style', 'svg'],
				'borderRightStyle': ['border-right-style', 'svg'],
				'borderLeftStyle': ['border-left-style', 'svg'],
			'borderWidth': ['border-width', 'svg'],
				'borderTopWidth': ['border-top-width', 'svg'],
				'borderBottomWidth': ['border-bottom-width', 'svg'],
				'borderRightWidth': ['border-right-width', 'svg'],
				'borderLeftWidth': ['border-left-width', 'svg'],
			'borderRadius': ['border-radius', 'svg'],
				'borderTopLeftRadius': ['border-top-left-radius', 'svg'],
				'borderTopRightRadius': ['border-top-right-radius', 'svg'],
				'borderBottomLeftRadius': ['border-bottom-left-radius', 'svg'],
				'borderBottomRightRadius': ['border-bottom-right-radius', 'svg']
		}
	},
	'CTATNumberLine': {
		'innerTagName': 'g',
		'usesDefault': false,
		'styleAttributes': {
			'margin': ['margin', ''],
				'marginTop': ['marginTop', ''],
				'marginBottom': ['marginBottom', ''],
				'marginRight': ['marginRight', ''],
				'marginLeft': ['marginLeft', ''],
			'padding': ['padding', ''],
				'paddingTop': ['paddingTop', ''],
				'paddingBottom': ['paddingBottom', ''],
				'paddingRight': ['paddingRight', ''],
				'paddingLeft': ['paddingLeft', ''],
			'textAlign': ['text-align', ''],
			'fontSize': ['font-size', ''],
			'fontFamily': ['font-family', ''],
			'fontWeight': ['font-weight', ''],
			'fontStyle': ['font-style', ''],
			'color': ['stroke', 'svg > g'],
			'backgroundColor': ['background-color', 'svg'],
			'backgroundImage': ['background-image', 'svg'],
			'borderColor': ['border-color', 'svg'],
				'borderTopColor': ['border-top-color', 'svg'],
				'borderLeftColor': ['border-left-color', 'svg'],
				'borderRightColor': ['border-right-color', 'svg'],
				'borderBottomColor': ['border-bottom-color', 'svg'],
			'borderStyle': ['border-style', 'svg'],
				'borderTopStyle': ['border-top-style', 'svg'],
				'borderBottomStyle': ['border-bottom-style', 'svg'],
				'borderRightStyle': ['border-right-style', 'svg'],
				'borderLeftStyle': ['border-left-style', 'svg'],
			'borderWidth': ['border-width', 'svg'],
				'borderTopWidth': ['border-top-width', 'svg'],
				'borderBottomWidth': ['border-bottom-width', 'svg'],
				'borderRightWidth': ['border-right-width', 'svg'],
				'borderLeftWidth': ['border-left-width', 'svg'],
			'borderRadius': ['border-radius', 'svg'],
				'borderTopLeftRadius': ['border-top-left-radius', 'svg'],
				'borderTopRightRadius': ['border-top-right-radius', 'svg'],
				'borderBottomLeftRadius': ['border-bottom-left-radius', 'svg'],
				'borderBottomRightRadius': ['border-bottom-right-radius', 'svg']
		}
	},
	'CTATComboBox': {
		'innerTagName': 'select',
		'usesDefault': true
	},
	'CTATDragNDrop': {
		'innerTagName': '',
		'usesDefault': true
	},
	'CTATJumble': {
		'innerTagName': '',
		'usesDefault': true
	},
	'CTATHintWidget': {
		'innerTagName': 'table',
		'usesDefault': true
	},
	'CTATSkillWindow': {
		'innerTagName': "",
		'usesDefault': true
	},
	'CTATGroupingComponent': {
		'innerTagName':'',
		'usesDefault':true
	},
	'CTATHintButton': {
		'innerTagName': 'button',
		'usesDefault': true
	},
	'CTATDoneButton': {
		'innerTagName': 'button',
		'usesDefault': true
	},
	'CTATHintWindow': {
		'innerTagName': '',
		'usesDefault': true
	}
};
