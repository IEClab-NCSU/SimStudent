/**
 * Called by edX to initialize the xblock.
 * @param runtime - provided by EdX
 * @param element - provided by EdX
 */
function Initialize_CTATXBlock(runtime, element) {
    var post = {
	save_problem_state: function(state) {
	    $.ajax({type: "POST",
		    url: runtime.handlerUrl(element, 'ctat_save_problem_state'),
		    data: JSON.stringify({state:state}),
		    contentType: "application/json; charset=utf-8",
		    dataType: "json"});
	},
	report_grade: function(correct_step_count, total_step_count) {
	    $.ajax({type: "POST",
		    url: runtime.handlerUrl(element, 'ctat_grade'),
		    data: JSON.stringify({'value': correct_step_count,
					  'max_value': total_step_count}),
		    contentType: "application/json; charset=utf-8",
		    dataType: "json"});
	},
	log_event: function(aMessage) {
	    msg = JSON.stringify({
		'event_type': 'ctat_log',
		'action': 'CTATlogevent',
		'message': aMessage});
	    $.ajax({
            type: "POST",
		    url: runtime.handlerUrl(element, 'ctat_log'),
		    data: msg,
		    contentType: "application/json; charset=utf-8",
		    dataType: "json",
		    success: function(data1){
                
                
                if(data1['result']==='success'){
                    
                    
                    $.ajax({
                        async: false,
                        crossDomain: true,
                        //url: "http://10.202.210.147:8080/SimStudentServlet/serv",
                        url: "http://127.0.0.1:8080/SimStudentServlet/serv",
                        method: "POST",
                        dataType: "text",
                        data: "<?xml version=\"1.0\" encoding=\"UTF-8\"?><message><verb>NotePropertySet</verb><properties><MessageType>InterfaceAction</MessageType><transaction_id>ce84270a-f527-0988-c717-3c817ed4c127</transaction_id><Selection><value>"+data1['selection']+"</value></Selection><Action><value>"+data1['action']+"</value></Action><Input><value>"+data1['input']+"</value></Input><session_id>d62e0111-a494-4d5c-1bbc-3ec5b1bd2088</session_id></properties></message>",
                        success: function(data) {
                            
                            // ======================Logging function start from here===============
                            //alert(data);
                            if( data1['selection'] != undefined && data1['action'] != undefined && data1['input'] != undefined ) {
                                var correctOrNot = data.split(",")[0];
                                var kc = data.split(",")[1];

                                $.ajax({
                                    type: 'POST',
                                    url: runtime.handlerUrl(element, 'get_student_id'),
                                    data: JSON.stringify({'type': 'checkbutton', 'hintCount': 0, 'correctness': correctOrNot, 'kc': kc, 'selection': data1['selection'], 'action': data1['action'], 'input': data1['input']}),
                                    success: function(data) {
                                        console.log("User Id : " + data.user);
                                        console.log("Xblock Id :" + data.xblock_id);
                                    }

                                });
                            }
                    //======================Logging function ends here===================
                    // ********
                        
                        }
                    }); 
                    
                   

                }
                
		    }
        });
	},
	report_skills: function(skills) {
	    $.ajax({type: "POST",
		    url: runtime.handlerUrl(element, 'ctat_save_skills'),
		    data: JSON.stringify({'skills': skills}),
		    contentType: "application/json; charset=utf-8",
		    dataType: "json"});
	}
    };
    $('.ctatxblock').on("load", function() {
	var ctattutor = new URL(this.src);
	// put problem state in config
	this.contentWindow.postMessage(CTATConfig, ctattutor.origin);

	window.addEventListener("message", function(event) {
	    if (event.origin !== ctattutor.origin) {
		console.log("Message not from valid source:", event.origin,
			    "Expected:", ctattutor.origin);
        console.log("I am event data: " + event.data);    
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
    });
}
