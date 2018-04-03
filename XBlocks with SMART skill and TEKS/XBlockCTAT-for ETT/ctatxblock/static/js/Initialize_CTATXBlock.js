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
		    dataType: "json",
            async: false,
            success: function(data) {
                console.log("save_problem ends");
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
            async: false,
            success: function(data) {
                console.log("report_grade ends");
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
            async: false,
            success: function(data) {
                console.log("log_event ends");
            },
            error: function (jqXHR, exception) {
                var msg = '';
                if (jqXHR.status === 0) {
                    msg = 'Not connect.\n Verify Network.';
                } else if (jqXHR.status == 404) {
                    msg = 'Requested page not found. [404]';
                } else if (jqXHR.status == 500) {
                    msg = 'Internal Server Error [500].';
                } else if (exception === 'parsererror') {
                    msg = 'Requested JSON parse failed.';
                } else if (exception === 'timeout') {
                    msg = 'Time out error.';
                } else if (exception === 'abort') {
                    msg = 'Ajax request aborted.';
                } else {
                    msg = 'Uncaught Error.\n' + jqXHR.responseText;
                }
                console.log("Error message: " + msg);
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
            async: false,
            success: function(data) {
                console.log("report_skills ends");
                console.log(data);
            }
        });
	}
    };
    
    
        
    
    
    // get the id attribute from the xblock backend. In order to separate the different tutors on the same page. 
    
    
    // +++++++++++++++++++++testing++++++++++++++++++++++
    
    var xblock_code = "";
    var xblock_id = "";
    
    $.ajax({
        type: "POST",
        url: runtime.handlerUrl(element, 'get_xblock_id'),
        data: JSON.stringify({'getXblock': true}),
        dataType: "json",
        async: false,
        success: function(data) {
            xblock_code = data['xblock_code'];
            xblock_id = data['xblock_id'];
            $('#ctat-iframe').attr('id', data['xblock_code']);
        }
    });
        
    /*    
    var iframe = $('#tutor_id' + xblock_code);
    console.log("tutor_id" + xblock_code);
    console.log("iframe: ", iframe);    
    iframe.on("load", function() {
        initIFrame();
    });
        
    
    
    
    
    //+++++++++++++++++++++++++++++++++++++++++++++++++++
    
    
    
    
    
    function initIFrame() {
            // get it's own src from xblock backend:
            var src = "";
            $.ajax({
                url: runtime.handlerUrl(element, "get_src"),
                type: "POST",
                data: JSON.stringify({'getSRC': true}),
                dataType: "json",
                async: false,
                success: function(data) {
                    src = data['src'];
                }
            });
            
            
            var ctattutor = new URL(src);
        
            // put problem state in config
            var iframe = document.getElementById("tutor_id" + xblock_code);
            console.log("iframe.contentWindow: ", iframe);
            iframe.contentWindow.postMessage(CTATConfig, ctattutor.origin);
            
            window.addEventListener("message", function(event) {
                //alert("ctattutor.origin: " + ctattutor.origin);
                console.log('receiveMessageFromIframePage', event);
                //event.stopPropagation();
                
                if (event.origin !== ctattutor.origin) {
                    alert("Message not from valid source, I have been returned!");
                    return;
                }
                switch (event.data.action) {
                    case "save_problem_state":
                        post.save_problem_state(event.data.input);
                        break;
                    case "grade":
                        post.report_grade(event.data.input.value, event.data.input.max);
                        break;
                    case "log":
                        post.log_event(event.data.input);
                        break;
                    case "skills":
                        post.report_skills(event.data.input);
                        break;
                    default:
                        console.log("unrecognized action:", event.data.action);
                        break;
                }
            }, false);
        
            
    }
    
    $.ajax({
        type: "POST",
        url: runtime.handlerUrl(element, 'get_xblock_id'),
        data: JSON.stringify({'getXblock': true}),
        dataType: "json",
        async: false,
        success: function(data) {
            xblock_code = data['xblock_code'];
            xblock_id = data['xblock_id'];
            $('#ctat-iframe').attr('id', data['xblock_code']);
        }
    });
    
    
    var CTATConfig = {}; // define the CTATConfig object by its own but not global
        $.ajax({
            url: runtime.handlerUrl(element, "get_ctatconfig"),
            type: "POST",
            data: JSON.stringify({'getConfigObject': true}),
            dataType: "json",
            async: false,
            success: function(data) {
                //meta
                CTATConfig['session_id'] = "xblockctat_" + data['guid'];
                CTATConfig['user_guid'] = data['student_id'];
                CTATConfig['tutor_id'] = data['tutor_id'];
                
                //class
                CTATConfig['class_name'] = data['course'];
                CTATConfig['school_name'] = data['org'];
                CTATConfig['period_name'] = data['run'];
                CTATConfig['class_description'] = "Edx class";
                
                //dataset
                CTATConfig['dataset_name'] = data['course_key'];
                CTATConfig['problem_name'] = data['problem_name'];
                CTATConfig['dataset_level_name1'] = data['block_type'];
                CTATConfig['dataset_level_type1'] = "Unit";
                CTATConfig['problem_context']=data['tutor_html'] + ":" + data['question_file']
                
                //runtime
                CTATConfig['connection']="javascript";
                CTATConfig['logging'] = "ClientToService";// assume
                CTATConfig['log_service_url'] = "";
                
                CTATConfig['question_file'] = data['question_file'];
                CTATConfig['tutoring_service_communication'] = "javascript";
                CTATConfig['saveandrestore'] = data['saved_state'];
                CTATConfig['skills'] = data['skills'];
                CTATConfig['problem_state_status'] = data['completed']!=="False"?'complete':data['saved_state']!==""?'incomplete':'empty';
                
                // after that, try catch block:
                try {
                    var custom_params = JSON.parse(data['custom']);
                    CTATConfig = Object.assign(CTATConfig, custom_params);
                } catch(err) {
                    console.log('Invalid JSON in custom parameters: ' + err);
                }
                
            }
        });
    
    
    */
    $('#tutor_id' + xblock_code).on("load", function() {
        
        $.ajax({
            url: runtime.handlerUrl(element, 'export_course_content'),
            type: "POST",
            data: JSON.stringify({"export_data": true})
        });
        
	    var ctattutor = new URL(this.src);
	    // put problem state in config
	    //this.contentWindow.postMessage(CTATConfig, ctattutor.origin);
        var iframe = document.getElementById("tutor_id" + xblock_code);
        
        iframe.contentWindow.postMessage(CTATConfig, ctattutor.origin);
        
        //if we have more than one ctat xblock on the same page, this eventlistener is going to override them all.
        
        //first, we check if the window object already have event 'message':
        var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
        
        var eventer = window[eventMethod];
        var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";
        
        eventer(messageEvent, function(event) {
            
            //run function//
            console.log('receiveMessageFromIframePage-V2', event);
            if (event.origin !== ctattutor.origin) {
                console.log("Message not from valid source:", event.origin,
                    "Expected:", ctattutor.origin);
                return;
            }
            switch (event.data.action) {
                case "save_problem_state":
                    post.save_problem_state(event.data.input);
                break;
                case "grade":
                    post.report_grade(event.data.input.value, event.data.input.max);
                break;
                case "log":
                    post.log_event(event.data.input);
                break;
                case "skills":
                    post.report_skills(event.data.input);
                break;
                default:
                    console.log("unrecognized action:", event.data.action);
                break;
            }
            //event.stopImmediatePropagation();
        },false);
        
        /*
        window.addEventListener("message", function(event) {
            console.log('receiveMessageFromIframePage-V2', event);
            if (event.origin !== ctattutor.origin) {
                console.log("Message not from valid source:", event.origin,
                    "Expected:", ctatttuor.origin);
                return;
            }
            switch (event.data.action) {
                case "save_problem_state":
                    post.save_problem_state(event.data.input);
                break;
                case "grade":
                    post.report_grade(event.data.input.value, event.data.input.max);
                break;
                case "log":
                    post.log_event(event.data.input);
                break;
                case "skills":
                    post.report_skills(event.data.input);
                break;
                default:
                    console.log("unrecognized action:", event.data.action);
                break;
            }
        }, false);
        */
    });   
    
    
}
