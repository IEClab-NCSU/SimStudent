/* Javascript for Inline Dropdown XBlock. */
function InlineDropdownXBlockInitView(runtime, element) {
    
    var handlerUrl = runtime.handlerUrl(element, 'student_submit');
    var resetUrl = runtime.handlerUrl(element, 'student_reset');
    var hintUrl = runtime.handlerUrl(element, 'send_hints');
    var idUrl = runtime.handlerUrl(element, 'send_xblock_id');
    var restoreUrl = runtime.handlerUrl(element, 'restore_state');
	var publishUrl = runtime.handlerUrl(element, 'publish_event');

	var $element = $(element);
	
    var check_button = $element.find('.check_button');
    var hint_button = $element.find('hint_button');
    var reset_button = $element.find('.reset_button');

    var problem_progress = $element.find('.problem_progress');
    var question_prompt = $element.find('.question_prompt');
    var feedback_div = $element.find('.feedback');
    var hint_div = $element.find('.hint');
    var hint_button_holder = $element.find('.hint_button_holder');
        
    var hint;
    var hints;
    var hint_counter = 0;
    
    var prompt = question_prompt.html();;
    
    var xblock_id = '';
    	
    $.ajax({
        type: 'POST',
        url: hintUrl,
        data: JSON.stringify({requested: true}),
        success: set_hints
    });

    $.ajax({
        type: 'POST',
        url: idUrl,
        data: JSON.stringify({requested: true}),
        success: set_xblock_id
    });

    function publish_event(data) {
      $.ajax({
          type: "POST",
          url: publishUrl,
          data: JSON.stringify(data)
      });
    }
    
    function pre_submit() {
        problem_progress.text('(Loading...)');
        if (prompt == '') {
        	prompt = question_prompt.html();
        }
    }

	function post_submit(result) {
        problem_progress.text('(' + result.problem_progress + ')');
		show_feedback(result.feedback);
        reset_hint();
        
        // reset the prompt to the original value to remove previous decorations
        question_prompt.html(prompt);
        // restore select values
        restore_selections(result.submissions);
		// add decorations to indicate correctness
        add_decorations(result.correctness, result.selection_order);
        
	}
	
	function restore_selections(selections) {
        $("select").each(function() { 
        	if (this.getAttribute('xblock_id') == xblock_id) {
        		// reset the select value to what the student submitted
        		this.value = selections[this.getAttribute('input')];
        	}
        });        
	}
	
	function add_decorations(correctness, selection_order) {
        $("select").each(function() { 
        	if (this.getAttribute('xblock_id') == xblock_id) {
        		
        		var decoration_number = selection_order[this.getAttribute('input')];
        		        		
        		// add new decoration to the select
        		if (correctness[this.getAttribute('input')] == 'True') {
	        		$('<span class="inline_dropdown feedback_number_correct">(' + decoration_number + ')</span>').insertAfter(this);
	        		$('<span class="fa fa-check status correct"/>').insertAfter(this);
        		} else {
	        		$('<span class="inline_dropdown feedback_number_incorrect">(' + decoration_number + ')</span>').insertAfter(this);
	        		$('<span class="fa fa-times status incorrect"/>').insertAfter(this);
        		}
        	}
        });        
	}
	
	function post_reset(result) {
        problem_progress.text('(' + result.problem_progress + ')');
        reset_prompt();
        reset_hint();
        reset_feedback();
	}

	function set_hints(result) {
		hints = result.hints;
		if (hints.length > 0) {
	        hint_button.css('display','inline');
			hint_button_holder.css('display','inline');
    	}
	}

	function set_xblock_id(result) {
		xblock_id = result.xblock_id;
    	$.ajax({
	        type: 'POST',
    	    url: restoreUrl,
        	data: JSON.stringify({requested: true}),
        	success: restore_state
    	});
	}
	    
	function restore_state(result) {
		if (result.completed == true) {
        	restore_selections(result.selections);
        	add_decorations(result.correctness, result.selection_order);
        	show_feedback(result.current_feedback);
        }
	}
	    
    function reset_prompt() {
        // reset the prompt to the original value to remove previous decorations
        question_prompt.html(prompt);
	}

    function reset_hint() {
    	hint_counter = 0;
    	hint_div.css('display','none');
    }

    function reset_feedback() {
        feedback_div.html();
    	feedback_div.css('display','none');
    }

    function show_hint() {
    	hint = hints[hint_counter];
		hint_div.html(hint);
		hint_div.css('display','block');
		publish_event({
			event_type:'hint_button',
			next_hint_index: hint_counter,
		});
		if (hint_counter == (hints.length - 1)) {
			hint_counter = 0;
		} else {
			hint_counter++;
		}
    }

    function show_feedback(feedback) {
		feedback_div.html(feedback);
		feedback_div.css('display','block');
    }
    
    $('.check_button', element).click(function(eventObject) {
        pre_submit();
        var selections = {};
        var selection_order = {};
        var complete = true;
        var counter = 1;
        $("select").each(function() { 
        	if (this.getAttribute('xblock_id') == xblock_id) {
        		if (this.selectedIndex == 0) {
    	    		complete = false;
    	    		show_feedback('<p class="incorrect">You haven\'t completed the question.</p>');
	        	} 
        		selections[this.getAttribute('input')] = this[this.selectedIndex].text;
        		selection_order[this.getAttribute('input')] = counter;
        		counter++;
        	}
        });
        var data = {
                selections: selections,
                selection_order: selection_order,
            };
        if (complete) {
	        $.ajax({
    	        type: 'POST',
        	    url: handlerUrl,
            	data: JSON.stringify(data),
	            success: post_submit
    	    });
        }
	});

    $('.reset_button', element).click(function(eventObject) {
		var data = {};
        $.ajax({
            type: 'POST',
            url: resetUrl,
            data: JSON.stringify(data),
            success: post_reset
        });
	});
	
    $('.hint_button', element).click(function(eventObject) {
        show_hint();
	});
		
}


