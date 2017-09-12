/* Javascript for McqsXBlock. */
function McqsXBlockInitView(runtime, element) {

    var hint_array = [];
    var count = 0;
    var xblock_id = "";
    var xblock_code = "";
    // for refresh:
    var countTimes = 0;
    var correctOrNot = false;
    var userChoice = 0;
    var location_id = "";
    var student_id = "";
    var skillname = "";
    var hasBeenSent = false;
    
    $(function ($) {
        var xblockIdUrl = runtime.handlerUrl(element, 'get_xblock_id');
        $.ajax({
            type: "POST",
            url: xblockIdUrl,
            data: JSON.stringify({'getquestion': true}),
            async: false,
            success: function(data) {
                xblock_id = data.xblock_id;
                xblock_code = data.xblock_code;
                $("div[data-id='" + xblock_id + "'] span[id='hint']").attr("id", xblock_code);
                
            }
        }); 
        
        var getStatusWhenRefresh = runtime.handlerUrl(element, "get_status_when_refresh");
        $.ajax({
            type: "POST",
            url: getStatusWhenRefresh,
            data: JSON.stringify({'getData': true}),
            async: false,
            success: function(data) {
                countTimes = data.countTimes;
                correctOrNot = data.correctness;
                userChoice = data.userChoice;
                //==========testing===============
                if(countTimes >= 1 && userChoice > 0) {
                    var selectedCheck = $("div[data-id='" + xblock_id + "'] input[type='radio'][checked]");
                    if(correctOrNot) {

                        selectedCheck.parent().addClass('correct');
                        selectedCheck.parent().append("<i class='tick'>&nbsp;&nbsp;&nbsp;  &#x2713;</i>");
                    } else {

                        selectedCheck.parent().addClass('incorrect');
                        selectedCheck.parent().append("<i class='ballot'>&nbsp;&nbsp;&nbsp; &#10006;</i>");
                    }
                   
                }
                
            }
        });
        //alert("...countTimes: " + countTimes + ", correctOrNot: " + correctOrNot + ", userChoice: " + userChoice);
        
        addBorderColor();
        //Is there any fucking asshole would do something like that? Every time student view the page would trigger the function.
        var locationObject = $('#seq_content').children()[1] == undefined ? undefined : $('#seq_content').children()[1].getAttribute('data-usage-id');
        if(locationObject != undefined) {
            var locationArray = locationObject.split("@");
            $.ajax({
                url: runtime.handlerUrl(element, 'module_skillname_saved'),
                type: "POST",
                data: JSON.stringify({"paragraph_id": xblock_code, "location_id": locationArray[locationArray.length - 1]}),
                success: function(data) {
                    console.log("Skillname has been saved!", data);
                }
            });
        }
        // testing iframe element here...
        
/* 
$( document ).on( "pagecreate", function() {
    // The window width and height are decreased by 30 to take the tolerance of 15 pixels at each side into account
    function scale( width, height, padding, border ) {
        var scrWidth = $( window ).width() - 30,
            scrHeight = $( window ).height() - 30,
            ifrPadding = 2 * padding,
            ifrBorder = 2 * border,
            ifrWidth = width + ifrPadding + ifrBorder,
            ifrHeight = height + ifrPadding + ifrBorder,
            h, w;
        if ( ifrWidth < scrWidth && ifrHeight < scrHeight ) {
            w = ifrWidth;
            h = ifrHeight;
        } else if ( ( ifrWidth / scrWidth ) > ( ifrHeight / scrHeight ) ) {
            w = scrWidth;
            h = ( scrWidth / ifrWidth ) * ifrHeight;
        } else {
            h = scrHeight;
            w = ( scrHeight / ifrHeight ) * ifrWidth;
        }
        return {
            'width': w - ( ifrPadding + ifrBorder ),
            'height': h - ( ifrPadding + ifrBorder )
        };
    };
    $( ".ui-popup iframe" )
        .attr( "width", 0 )
        .attr( "height", "auto" );
    $( "#popupVideo" ).on({
        popupbeforeposition: function() {
            // call our custom function scale() to get the width and height
            var size = scale( 497, 298, 15, 1 ),
                w = size.width,
                h = size.height;
            $( "#popupVideo iframe" )
                .attr( "width", w )
                .attr( "height", h );
        },
        popupafterclose: function() {
            $( "#popupVideo iframe" )
                .attr( "width", 0 )
                .attr( "height", 0 );
        }
    });
});
*/
        
        
        
        /* after we saved all the probability to database, this method is going to trigger the read probability function
        $.ajax({
            url: runtime.handlerUrl(element, 'get_temporary_probability_method'),
            type: "POST",
            data: JSON.stringify({"get_data": true})
        });
        */
        
        
        // This method trigger the get_border_color, if there is no any other XBlock(e.g. Text paragraph XBlock) matches the skill name.
        $.ajax({
            url: runtime.handlerUrl(element, "get_border_color"),
            type: "POST",
            data: JSON.stringify({"getBorderColor": true}),
            success: function(data) {
                console.log("multiple choice color:", data);
                if(data.setBorderColor == 0) {
                    // studio and lms: the structure of the html are not the same, data-usage-id is for studio use only.
                    $("div[data-usage-id='" + xblock_id + "'] div[id='border']").css("border", "2px solid red");
                    
                } else {
                    $("div[data-usage-id='" + xblock_id + "'] div[id='border']").removeAttr("style");
                }
            }
        });
        
        //testing, for deletion function:
        var deleteButtonGroup = document.getElementsByClassName("delete-button");
        var xblockGroup = document.getElementsByClassName("xblock-student_view");
        
        var getXBlockIdFromBroswer=function(arg){  
            return function(){  
                
                console.log("Now I have: " + document.getElementsByClassName("xblock-student_view").length + " xblock on the page.");
                console.log("Now I have: " + document.getElementsByClassName("delete-button").length + " delete button on the page.");
                console.log(arg);
                console.log($("#deleteButton_" + arg).closest("section").children("article").children("div").attr("data-usage-id"));
                var selectedXBlockId = $("#deleteButton_" + arg).closest("section").children("article").children("div").attr("data-usage-id");
                
//                $("div").one("click", ".action-secondary", function(event) {
//	                //alert("Now the 'Cancle' button trigger: " + event.target.className + "#" + event.target.id);	
//                });
                $("div").one("click", ".action-primary", function(event) {
	                //alert("Now the 'Yes' button trigger: " + event.target.className + "#" + event.target.id);
                    $.ajax({
                        url: runtime.handlerUrl(element, "delete_xbock"),
                        type: "POST",
                        data: JSON.stringify({"xblock_id": selectedXBlockId}),
                        anysc: false,
                        success: function(data) {
                            console.log("Deleted sucessfully!");
                        }
                    });
                    for(var i = 0; i < deleteButtonGroup.length; i++) {
                        deleteButtonGroup[i].id = "deleteButton_" + (i);
                        deleteButtonGroup[i].onclick = getXBlockIdFromBroswer(i);
                    }
                });
                
            }  
        };
        for(var i = 0; i < deleteButtonGroup.length; i++) {
            deleteButtonGroup[i].id = "deleteButton_" + (i);
            deleteButtonGroup[i].onclick = getXBlockIdFromBroswer(i);
            
        }
        
        
        // for testing propose only, get student_pastel_id from DB.
        $.ajax({
            url: runtime.handlerUrl(element, 'get_pastel_student_id'),
            type: "POST",
            data: JSON.stringify({"get_pastel_student_id": true}),
            success: function(data){
                //alert(data['hasBeenSent']);
                if(data['hasBeenSent'] == 'false') {
                    $.ajax({
                        url: runtime.handlerUrl(element, "get_studentId_and_skillname"),
                        type: "POST",
                        data: JSON.stringify({"getStudent_id": true}),
                        success: function(data) {

                            var student_id = data['student_id'];
                            var skillname = data['skillname'];

                            $.ajax({
                                url: runtime.handlerUrl(element, 'get_probability'),
                                type: "POST",
                                data: JSON.stringify({"skillname": skillname, "student_id": student_id}),
                                success: function(data) {
                                    if(data != null) {
                                           var probability = data['probability'];

                                           if(parseFloat(probability) > 0.95) {
                                                 $("div[data-id='" + xblock_id + "']").remove();
                                           }
                                    }

                                }
                            });


                        }
                    });
                }
                    
                
            }
        });
        
        
        
    });
    
    
    
    
    
    function addBorderColor() {
        $("label").not($("label input[name='choice']:checked").parent()).hover(function() {
            $(this).addClass("addBorder");
        }, function() {
            $(this).removeClass("addBorder");
        });
    }
    
    function removeBorderColor() {
        $("label").hover(function() {
            $(this).removeClass("addBorder");
        }, function() {
            $(this).removeClass("addBorder");
        });
    }
    /*
    jQuery(window).load(function () {
        //alert($("div[data-id='" + xblock_id + "'] input[name='choice']:checked").val());
        // which means user refresh the page:
        var selectedCheck = $("div[data-id='" + xblock_id + "'] input[name='choice']:checked");
        if(countTimes >= 1 && userChoice > 0) {
            
            //alert(selectedCheck.val());
            if(correctOrNot) {

                selectedCheck.parent().addClass('correct');
                selectedCheck.parent().append("<i class='tick'>&nbsp;&nbsp;&nbsp;  &#x2713;</i>");
            } else {

                selectedCheck.parent().addClass('incorrect');
                selectedCheck.parent().append("<i class='ballot'>&nbsp;&nbsp;&nbsp; &#10006;</i>");
            }
        }
    });
    */
    
    
    
        
    // when submit button has been clicked
    $('#submit', element).on('click', function(e){
        
        /*/ for sound effect application: ============================
        var audio = document.createElement("audio");
        audio.src = "http://www.freesfx.co.uk/rx2/mp3s/5/16952_1461335341.mp3";
        audio.volumn = 0.50;
        audio.autoPlay = false;
        audio.preLoad = true;
        audio.play();
        */
        //before that, check if the user select a choice or not
        checkAnswer();
        removeBorderColor();
        addBorderColor();
        
        
        /* temporary table: temporary_probability (now we don't need it anymore)
        $.ajax({
            url: runtime.handlerUrl(element, 'save_temporary_probability_method'),
            type: "POST",
            data: JSON.stringify({"save_data": true}),
            success: function(data) {
                console.log("probability has been saved!");
            }
        });
        */
        
        
    });
    
    
    
    
    
    // when getHint button has been clicked
    $("#getHint", element).on('click', function(e){
        
        //$("div[data-id='" + xblock_id + "'] span[id='hint']").attr("id", xblock_code);
        if(hint_array.length === 0) {
            getHint();
            saveHint();
        } else {
            count++;
            if(count > hint_array.length - 1) {
                count = 0;
            }
            getHint2();
            saveHint();
            //alert(question);
            //$("#" + question + "hint").text(hint_array[count]);
            //$("#hint").text(hint_array[count]);    
        }
        //alert("hint message should show at the right place.");
        //alert(xblock_id);
        if(!$("div[data-id='" + xblock_id + "'] span[title='Get Hint']").hasClass("help-tip")) {
            $("div[data-id='" + xblock_id + "'] span[title='Get Hint']").addClass("help-tip");
        }
        $("#" + xblock_code).html(hint_array[count]);
        
    });
    
    function saveStudentDataForProbability(correctness) {
        // send this ajax request to my spring boot backend.
        
        $.ajax({
            url: runtime.handlerUrl(element, "get_studentId_and_skillname"),
            type: "POST",
            data: JSON.stringify({"getStudent_id": true}),
            success: function(data) {
                var student_id = data['student_id'];
                var skillname = data['skillname'];
                var question_id = data['question_id'];
                
                $.ajax({
                    url: "http://127.0.0.1:8080/callandsaveBKT",
                    type: "GET",
                    data: "student_id=" + student_id + "&skillname=" + skillname.split(' ').join('_') + "&correctness=" + correctness + "&question_id=" + question_id,
                    dataType: 'jsonp',
                    jsonp: 'callback',
                    //jsonpCallback: "callback",
                    anysc: false,
                    success: function(data) {
                        
                    }
                });
            }
        });
    }
    
    
    
    // check answer button
    function checkAnswer() {
        var selectedCheck = $("div[data-id='" + xblock_id + "'] input[name='choice']:checked");
        //alert(selectedCheck.val());
        // ==========================================================
        var setStatusWhenRefresh = runtime.handlerUrl(element, 'set_status_when_refresh');
        $.ajax({
            type: "POST",
            data: JSON.stringify({'setStatus': true, 'userChoice': selectedCheck.val(), 'hasBeenSent': 'true'}),
            url: setStatusWhenRefresh,
            async: false
        });
        
       
        //var selectedCheck = $("div[data-id='" + xblock_id + "'] input[name='choice']:checked");
        var answerId = selectedCheck.val();
        var userSelected = answerId;
        console.log("user selected: " + answerId);
        
        //alert("user selected: " + answerId);
        $("div[data-id='" + xblock_id + "'] input[name='choice']").not(selectedCheck).removeAttr('checked');
        selectedCheck.attr("checked", "checked");
        // remove class and <i> tag if there are any exist:
        $("div[data-id='" + xblock_id + "'] label").attr('class', '');
        $("div[data-id='" + xblock_id + "'] i").remove(".tick");
        $("div[data-id='" + xblock_id + "'] i").remove(".ballot");
        
        //alert("dada: " + userSelected + ", changed?:" + answerId);
        
        //selectedCheck.parent().addClass('user-choice');
        var checkUrl = runtime.handlerUrl(element, 'check_answer');
        $.ajax({
            type: "POST",
            data: JSON.stringify({'ans': userSelected}),
            url: checkUrl,
            anysc: false,
            success: function(data){
                // mark question as attempted
                //$("#question-block").attr("id", xblock_id + "question-block");
                //$("#question-block", element).addClass('attempted');
                
                
                if(data.correct == true){
                    selectedCheck.parent().addClass('correct');
                    selectedCheck.parent().append("<i class='tick'>&nbsp;&nbsp;&nbsp;  &#x2713;</i>");
                    // save student data for probability  !hasBeenSent
                    if(!hasBeenSent) {
                        saveStudentDataForProbability(1);
                        hasBeenSent = true;
                    }
                    
                    
                }else{
                    // indicate correct and incorrect
                    selectedCheck.parent().addClass('incorrect');
                    selectedCheck.parent().append("<i class='ballot'>&nbsp;&nbsp;&nbsp; &#10006;</i>");
                    // save student data for probability
                    if(!hasBeenSent) {
                        saveStudentDataForProbability(0);
                        hasBeenSent = true;
                    }
                    
                    var hostname = $(location).attr('host') + "/courses/";
                    
                    $.ajax({
                        url: runtime.handlerUrl(element, "get_skill_mapping"),
                        type: "POST",
                        data: JSON.stringify({"getLocation": true}),
                        anysc: false,
                        success: function(data) {
                            
                            var course_id = data['course_id'];
                            var paragraph_id = data['paragraph_id'];
                            var location_id = data['location_id'];
                            
                            if($("div[data-id='" + xblock_id + "'] div[id='navigate_id']").length > 0 || $("div[data-id='" + xblock_id + "'] div[id='navigate_id']").html() != "") {
                                $("div[data-id='" + xblock_id + "'] div[id='navigate_id']").remove();
                            }
                            
                            $("div[data-id='" + xblock_id + "'] div[class='hint-block']").append("<div id='navigate_id'>Please click the <a href='http://" + hostname + course_id + "/jump_to_id/" + location_id + "#" + paragraph_id + "'>link</a> here to review the course content again.");
                            
                        }
                    });
                    // after that, you can start from here.
                    //$("div[data-id='" + xblock_id + "'] div[class='hint-block']").append("<div>Please click the <a href='" + hostname + "'>link</a> here to review the course content again.");
                }
                
                var userUrl = runtime.handlerUrl(element, 'get_student_id');
                
                $.ajax({
                    type: "POST",
                    data: JSON.stringify({'hintCount': count, 'type': 'checkbutton'}),
                    url: userUrl,
                    success: function(data) {
                        console.log("user Id : " + data.user_id);
                        console.log("xblock Id :" + data.xblock_id);
                        if(data.user_id !== null) {
                            // Then start to store all the information to db:
                            var student_id = data.user_id.toString().split(',')[3];

                            // all the answer located in edxapp.courseware_studentmodule

                        }
                    }
                });
            }
        });
         
        
        
        
    }

    
    
    
    // get hint function
    function getHint() {   
        // send Ajax:
        var hintUrl = runtime.handlerUrl(element, 'get_hint');

        $.ajax({
            type: "POST",
            data: JSON.stringify({'getHint': true}),
            url: hintUrl,
            async: false,
            success: function(data){
                hint_array = data.response.split("|");
                
            } 
        });
    }
    
    function getHint2() {
        var hintUrl = runtime.handlerUrl(element, 'get_hint');
        $.ajax({
            type: "POST",
            data: JSON.stringify({'getHint': true}),
            url: hintUrl,
            async: false
        });
        
    }
    
    
    function saveHint() {
        //$("#hint").text(hint_array[count]);
        // start to send ajax request and store the user activities:
        var userUrl = runtime.handlerUrl(element, 'get_student_id');
        $.ajax({
            type: "POST",
            data: JSON.stringify({'hintCount': count, 'type': 'hintbutton'}),
            url: userUrl,
            success: function(data) {
                console.log("User Id : " + data.user);
                console.log("Xblock Id :" + data.xblock_id);
            }
        });
    }
    
    
    
}
