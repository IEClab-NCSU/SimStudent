/**
 * Called by edX to initialize the xblock.
 * @param runtime - provided by EdX
 * @param element - provided by EdX
 */
function Initialize_CTATXBlock(runtime, element) {
    
    
    var post = {
	save_problem_state: function(state) {
	    $.ajax({
            type: "POST",
		    url: runtime.handlerUrl(element, 'ctat_save_problem_state'),
		    data: JSON.stringify({state:state}),
		    contentType: "application/json; charset=utf-8",
		    dataType: "json",
            success: function(data) {
                console.log("I am save_problem: ");
                console.log(data);
            }
        });
	},
	report_grade: function(correct_step_count, total_step_count) {
	    $.ajax({
            type: "POST",
		    url: runtime.handlerUrl(element, 'ctat_grade'),
		    data: JSON.stringify({'value': correct_step_count,
					  'max_value': total_step_count}),
		    contentType: "application/json; charset=utf-8",
		    dataType: "json",
            success: function(data) {
                console.log("I am report_grade: ");
                console.log(data);
            }
        });
	},
	log_event: function(aMessage) {
	    msg = JSON.stringify({
		  'event_type': 'ctat_log',
		  'action': 'CTATlogevent',
		  'message': aMessage
        });
        
	    $.ajax({
            type: "POST",
		    url: runtime.handlerUrl(element, 'ctat_log'),
		    data: msg,
		    contentType: "application/json; charset=utf-8",
		    dataType: "json",
            success: function(data) {
                console.log("I am log_event: ");
                console.log(data);
            }
        
        });
	},
	report_skills: function(skills) {
	    $.ajax({
            type: "POST",
		    url: runtime.handlerUrl(element, 'ctat_save_skills'),
		    data: JSON.stringify({'skills': skills}),
		    contentType: "application/json; charset=utf-8",
		    dataType: "json",
            success: function(data) {
                console.log("I am report_skills: ");
                console.log(data);
            }
        });
	}
    };
    
    //------------------testing area--------------------
    // global object count for the first time
    var count = 0;
    
    
    //------------------testing ends--------------------
    
    $('.ctatxblock').on("load", function() {
    count ++;
        
        
	var ctattutor = new URL(this.src);
	// put problem state in config
	this.contentWindow.postMessage(CTATConfig, ctattutor.origin);    
    
	window.addEventListener("message", function(event) {
	    if (event.origin !== ctattutor.origin) {
		console.log("Message not from valid source:", event.origin,
			    "Expected:", ctatttuor.origin);
		return;
	    }
        
        switch (event.data.action) {
	    case "save_problem_state":
        console.log("I am save problem state---------");
        console.log(event.data.input);
        console.log("---------------------------------");
		post.save_problem_state(event.data.input);
		break;
	    case "grade":
        console.log("I am grade---------");
        console.log(event.data.input.value);
        console.log(event.data.input.max);
        console.log("---------------------------------");
		post.report_grade(event.data.input.value, event.data.input.max);
		break;
	    case "log":
        console.log("I am log---------");
        console.log(event.data.input);
        console.log("---------------------------------");
		post.log_event(event.data.input);
		break;
	    case "skills":
        
		post.report_skills(event.data.input);
		break;
	    default:
        console.log("I am default---------");
        console.log(event.data.action);
        console.log("---------------------------------");
		console.log("unrecognized action:", event.data.action);
		break;
	    }
        
	    
	}, false);
       
    });
    

}
