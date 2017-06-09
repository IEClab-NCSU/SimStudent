/**
 *	@fileoverview a class which represents a dialog window to be used for multiple choice
 *	question generation.  Inherits from CTATDialogBase
 */

/**
 *	@Constructor
 *	@param windowId the id of the ctatdialog DOM node where this instance will live
 */
var CTATMultChoice = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATMultChoice", "multchoice", "MODAL", true);
	
	var pointer = this;
	this.selected = null;
	this.questionFontSize = 16;
	this.answerFontSize = 16;
	this.numAnswers = 4;
	var defaultCbk = function(questionInfo)
	{
		//set undo checkpoint
		window.silexApp.controller.editMenuController.undoCheckPoint();
		//pass data to silex to build the actual DOM node
		window.silexApp.model.element.createElement('question.multchoice', questionInfo);
	};
	var confirmCbk = null;
	/**
	 *	@Override CTATDialogBase.show()
	 */
	var super_show = this.show;
	this.show = function(selectedElement, cbk)
	{
		super_show();
		this.selected = selectedElement;
		confirmCbk = cbk || defaultCbk;
		if (this.selected)
		{
			//---load old question data in from existing---//
			//dialog box title
			$('#mult-choice-title').text('Edit Question');
			//get question text
			var question = $(selectedElement).find('.mult-choice-question')[0];
			//get answers (and check whether radio buttons or checkboxes)
			var answers = $(selectedElement).find('.CTATRadioButton');
			if (!answers || answers.length == 0)
			{
				answers = $(selectedElement).find('.CTATCheckBox');
				$('#mult-choice-mult-selection').prop('checked', true);
			}
			else
			{				
				$('#mult-choice-mult-selection').prop('checked', false);
			}
			if (answers[0].getAttribute('data-ctat-tutor') == "false")
			{
				$('#mult-choice-grade-immediate').prop('checked', false);
			}
			else
			{
				$('#mult-choice-grade-immediate').prop('checked', true);
			}
			//set question name
			$('#mult-choice-name').val(answers[0].getAttribute('name'));
			//set question text
			$('#mult-choice-question').val(question.textContent);
			//set number of options
			$('#mult-choice-num-options').val(answers.length);
			this.setNumOptions();
			//set option text
			var answerInputs = $('.mult-choice-answer');
			for (var i = 0; i < answerInputs.length; i++)
			{
				if (answers[i])
					$(answerInputs[i]).val(answers[i].getAttribute('data-ctat-label'));
				else
					$(answerInputs[i]).val('');
			}
			//set font sizes
			this.setFontSize(($(question).css('font-size') ? $(question).css('fontSize') : '16'), 'q');
			this.setFontSize(($(answers[0]).css('font-size') ? $(answers[0]).css('fontSize') : '16'), 'a');
			//set font family
			var font = window.silexApp.model.element.getStyle(this.selected, 'fontFamily');
			$('#mult-choice-fontselect').fontSelector('select', [font]);
			//set include submit
			var submit = $(selectedElement).find('.mult-choice-gen-submit-btn');
			$('#mult-choice-include-submit').prop('checked', ((submit && submit.length > 0) ? true : false));
		}
		else
		{
			$('#mult-choice-title').text('New Question');
			this.clear();
		}
	};
	
	/**
	 *	Reset input values for new question
	 */
	this.clear = function()
	{
		$('#mult-choice-name').val('');
		$('#mult-choice-question').val('');
		$('.mult-choice-answer').val('');
		$('#mult-choice-a-font-size').val('');
		$('#mult-choice-q-font-size').val('');
		$('#mult-choice-num-options').val('4');
		this.setNumOptions();
		$('#mult-choice-mult-selection').prop('checked', false);
		$('#mult-choice-grade-immediate').prop('checked', false);
		$('#mult-choice-include-submit').prop('checked', false);
		this.setFontSize('16', 'both');
		this.setFontFamily($('#mult-choice-fontselect').fontSelector('selected'));
	}
	
	/**
	 *	@Override CTATDialogBase.close()
	 */
	var super_close = this.close;
	this.close = function()
	{
		this.resetErrors('ALL');
		super_close();
	}
	
	/**
	 * Set up listeners on all the input elements / buttons
	 */
	this.initEvents = function()
	{
		$('#mult-choice-confirm').on('click', function()
			{
				pointer.confirm();
			});
		$('#mult-choice-cancel').on('click', function()
			{
				pointer.cancel();
			});
		$('#mult-choice-dialog').find('.windowclose').on('click', function()
			{
				pointer.cancel();
			});
		$('#mult-choice-q-font-size').on('change', function(event)
			{
				pointer.setFontSize($(event.target).val(), 'q');
			});
		$('#mult-choice-a-font-size').on('change', function(event)
			{
				pointer.setFontSize($(event.target).val(), 'a');
			});
		$('#mult-choice-name').on('keypress', function() {pointer.resetErrors('NO_NAME');});
		$('#mult-choice-question').on('keypress', function() {pointer.resetErrors('NO_QUESTION');});
		$('.mult-choice-answer').on('keypress', function() {pointer.resetErrors('NO_ANSWER');});
		$('#mult-choice-num-options').on('change', pointer.setNumOptions);
		$('#mult-choice-grade-immediate').on('change', pointer.setGradeImmediate);
		$('#mult-choice-fontselect').fontSelector({
			'hide_fallbacks' : true,
			'initial' : 'Courier New,Courier New,Courier,monospace',
			'selected' : function(style) 
				{ 
					pointer.setFontFamily(style); 
				},
			'opened' : function(style) {  },
			'closed' : function(style) {  },
			'fonts' : [
				'Arial,Arial,Helvetica,sans-serif',
				'Arial Black,Arial Black,Gadget,sans-serif',
				'Comic Sans MS,Comic Sans MS,cursive',
				'Courier New,Courier New,Courier,monospace',
				'Georgia,Georgia,serif',
				'Impact,Charcoal,sans-serif',
				'Lucida Console,Monaco,monospace',
				'Lucida Sans Unicode,Lucida Grande,sans-serif',
				'Palatino Linotype,Book Antiqua,Palatino,serif',
				'Tahoma,Geneva,sans-serif',
				'Times New Roman,Times,serif',
				'Trebuchet MS,Helvetica,sans-serif',
				'Verdana,Geneva,sans-serif',
				'Gill Sans,Geneva,sans-serif'
			]
		});
	};
	
	/**
	 *	Close the window and use the input values to generate a multiple choice question
	 */
	this.confirm = function()
	{
		//extract input data
		var questionName = $('#mult-choice-name').val();
		var questionText = $('#mult-choice-question').val();
		var answerTextArr = [];
		$('.mult-choice-answer').each(function()
			{
				if ($(this).val())
					answerTextArr.push($(this).val());
			});
		var allowMultAnswer = $('#mult-choice-mult-selection').prop('checked');
		var gradeOnInput = $('#mult-choice-grade-immediate').prop('checked');
		var includeSubmit = $('#mult-choice-include-submit').prop('checked');
		//input validation
		if (!questionName)
		{
			this.displayErrMsg('NO_NAME');
			return;
		}
		if (!questionText)
		{
			this.displayErrMsg('NO_QUESTION');
			return;
		}
		if (answerTextArr.length === 0)
		{	
			this.displayErrMsg('NO_ANSWER');
			return; 
		}
		if (!this.validateName(questionName))
		{
			this.displayErrMsg('BAD_ID');
			return; 
		}
			
		var questionInfo = {
			'wrapper': this.selected,
			'name': questionName,
			'question': questionText,
			'answers': answerTextArr,
			'fontFamily': this.fontFamily,
			'qFontSize': this.questionFontSize,
			'aFontSize': this.answerFontSize,
			'includeSubmit': includeSubmit,
			'allowMultAnswer': allowMultAnswer,
			'gradeOnInput':gradeOnInput
		};
		confirmCbk(questionInfo);
		this.close();
	};
	
	this.cancel = function()
	{
		this.close();
	};
	
	this.setFontFamily = function(family)
	{
		pointer.fontFamily = family;
		pointer.applyFontFamily();
	};
	
	this.applyFontFamily = function()
	{
		$('#mult-choice-question').css('font-family', pointer.fontFamily);
		$('.mult-choice-answer').css('font-family', pointer.fontFamily);
	};
	
	this.setFontSize = function(size, qOrA)
	{
		var regex = /\d{1,}(px)?/;
		if (regex.test(size))
		{
			if (!size.includes('px'))
				size+='px';
			switch(qOrA)
			{
				case 'a':
					this.answerFontSize = size;					
					$('#mult-choice-a-font-size').val(size.substring(0, size.length-2));
				break;
				case 'both':
					this.answerFontSize = size;
					$('#mult-choice-a-font-size').val(size.substring(0, size.length-2));
				case 'q':
					this.questionFontSize = size;
					$('#mult-choice-q-font-size').val(size.substring(0, size.length-2));
			}
			pointer.applyFontSize(qOrA);
		}
	};
	
	this.applyFontSize = function(qOrA)
	{
		switch(qOrA)
		{
			case 'a':					
				$('.mult-choice-answer').css('font-size', this.answerFontSize);
			break;
			case 'both':
				$('.mult-choice-answer').css('font-size', this.answerFontSize);
			case 'q':
				$('#mult-choice-question').css('font-size', this.questionFontSize);
		}
	}
	
	this.setNumOptions = function(numOptions)
	{
		if (!numOptions || typeof(numOptions === 'Object'))
			numOptions = document.getElementById('mult-choice-num-options').value;
		var optionList = document.getElementById('mult-choice-answer-list');
		var optionsAdded = false;
		if (!isNaN(numOptions) && parseInt(numOptions) >= 1)
		{ 
			numOptions = parseInt(numOptions)
			if (pointer.numAnswers < numOptions)
			{
				optionsAdded = true;
			}
			while (pointer.numAnswers != numOptions)
			{
				if (pointer.numAnswers < numOptions)
				{
					//add option
					var li = document.createElement('li');
					var textarea = document.createElement('textarea');
					textarea.classList.add('mult-choice-answer');
					textarea.setAttribute('placeholder', 'option ' + (++pointer.numAnswers));
					textarea.setAttribute('rows', '1');
					li.appendChild(textarea);
					optionList.appendChild(li);
				}
				else
				{
					//remove option
					optionList.removeChild($(optionList).find('li').last()[0]);
					pointer.numAnswers--;
				}
			}
			if (optionsAdded)
			{
				pointer.applyFontFamily();
				pointer.applyFontSize('both');
			}
		}
		else
			console.warn('numOptions not valid int');
	};
	
	/**
	 *	Show an error message if user tries to confirm w/ fields missing
	 *	@param errCode a string denoting which field is missing
	 */
	this.displayErrMsg = function(errCode)
	{
		switch(errCode)
		{
			case 'NO_NAME':
				$('#mult-choice-name').val('Please provide a unique ID');
				$('#mult-choice-name').attr('error', 'true');
			break;
			case 'NO_QUESTION':
				$('#mult-choice-question').val('Please fill in this field');
				$('#mult-choice-question').attr('error', 'true');
			break;
			case 'NO_ANSWER':
				$('.mult-choice-answer').attr('error', 'true');
			break;
			case 'BAD_ID':
				alert('Ids must be unique, cannot be "done" or "hint", and cannot contain spaces');
		}
	};
	
	/**
	 *	Unset the 'error' attribute on one or all of the dialog's input fields
	 *	(When the 'error' attribute is set fields turn red)
	 *	@param errCode which input should be set, or "ALL" to reset all of them
	 */
	this.resetErrors = function(errCode)
	{
		switch(errCode)
		{
			case 'NO_ANSWER':
				$('.mult-choice-answer').attr('error', 'false');
			break;
			case 'NO_QUESTION':
				$('#mult-choice-question').attr('error', 'false');
			break;
			case 'NO_NAME':
				$('#mult-choice-name').attr('error', 'false');
			break;
			case 'ALL':
				$('#mult-choice-name').attr('error', 'false');
				$('#mult-choice-question').attr('error', 'false');
				$('.mult-choice-answer').attr('error', 'false');
		}
	};
	
	/**
	 *	Checks provided name against working document to make
	 *		sure it's unique
	 *	@param name the name
	 */
	this.validateName = function(name)
	{
		if (name.includes(' '))
			return false;
		var stage = silexApp.model.file.getContentDocument();
		var el = stage.getElementById(name);
		if (el && el != pointer.selected)
			return false;
		
		return true;
	};
	
	this.setGradeImmediate = function()
	{
		var gradeImmediate = $('#mult-choice-grade-immediate').prop('checked');
		if (gradeImmediate)
		{
			$('#mult-choice-include-submit').prop('checked', false);
			$('#mult-choice-include-submit').prop('disabled', true);
		}
		else
		{
			$('#mult-choice-include-submit').prop('disabled', false);
		}
	};
	
	this.initEvents();
};

