/* Javascript for McqsXBlock. */

// global variable for all XBlock in the same page?

function TextXBlockInitView(runtime, element) {




    var hint_array = [];
    var count = 0;
    var xblock_id = "";
    var xblock_code = "";
    // for refresh:
    var countTimes = 0;
    var correctOrNot = false;
    var userChoice = 0;
    var page_id = "";
    var skillname = "";
    var section = "";
    var subsection = "";
    var unit = "";
    var editButton = $(".action-button-text").length;

    $(function ($) {
        var xblockIdUrl = runtime.handlerUrl(element, 'get_xblock_id');
        $.ajax({
            type: "POST",
            url: xblockIdUrl,
            data: JSON.stringify({'getquestion': true}),
            anysc: false,
            success: function(data) {
                xblock_id = data.xblock_id;
                xblock_code = data.xblock_code;
                skillname = data.skillname;
                $("div[data-usage-id='" + xblock_id + "']").attr("id", xblock_code);
                /*
                    For showing the skill name on the top of the XBlock on teacher view
                */
                $("#" + xblock_code).parent().prev().find(".xblock-display-name").append("&nbsp; &nbsp;<span style='color: red'><b> Skill Name: " + skillname + "</b></span>");
            }
        });

        if(editButton == 0) {
            var info = $(".path").text().split(">");
            section = info[0].trim();
            subsection = info[1].trim();
            unit = info[2].trim();

        }

        
        // For skill name testing start from here: trigger a Ajax call to store the skill name in Database. Target table: skill_mapping

        /*
            !!!!!!
            Disable the module_skillname_saved method if we want to use it in the study!
            Once we refresh the student view this method will trigger and save the current location to the table: module_skillname.
            !!!!!!
        */

        var locationObject = $("button.nav-item.active").attr("data-id") == undefined ? undefined : $("button.nav-item.active").attr("data-id");
        if(locationObject != undefined) {
            var locationArray = locationObject.split("@");
            page_id = locationArray[locationArray.length - 1];
            $.ajax({
                url: runtime.handlerUrl(element, 'module_skillname_saved'),
                type: "POST",
                anysc: false,
                data: JSON.stringify({"paragraph_id": xblock_code, "location_id": locationArray[locationArray.length - 1]}),
                success: function(data) {
                    console.log("Skillname has been saved!", data);
                }
            });
        }

        /*
            When the page is loaded, we want to save the current page is loaded by student;
            Catch the moment when the page is loaded. It only active in the student view.
        */


        if(editButton == 0) {// student view

            if(typeof(page_loaded) == "undefined") {
                page_loaded = false;
            } else {
                if(!page_loaded) {
                    page_loaded = true;
                    // send ajax
                    var userUrl = runtime.handlerUrl(element, 'get_student_id');
                    $.ajax({
                        type: "POST",
                        data: JSON.stringify({'hintCount': count, 'type': 'pageLoaded', 'page_id': page_id, "section": section, "subsection": subsection, "unit": unit}),
                        url: userUrl,
                        async: false
                    });
                    $.ajax({
                        type: "POST",
                        data: JSON.stringify({'hintCount': count, 'type': 'unitFilled', 'page_id': page_id, "section": section, "subsection": subsection, "unit": unit}),
                        url: userUrl
                    });

                    $(".accordion-nav").click(function() {


                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'menuLinkClicked', 'page_id': page_id + "+" + this.getElementsByTagName('p')[0].innerHTML, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl
                        });
                    });

                    $("ul[class='dropdown-menu'] li[class='item']").click(function() {
                        var refreshSessionUrl = runtime.handlerUrl(element, 'refresh_session');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': ""}),
                            url: refreshSessionUrl
                        });
                    });

                }
            }



            if(typeof(unit_loaded) == "undefined") {
                unit_loaded = false;

            } else {
                if(!unit_loaded) {
                    unit_loaded = true;


                    $(".nav-item").click(function() {

                        var unitPath = $(this).attr("data-path").split(">");
                        var unitName = unitPath[unitPath.length - 1];

                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'unitIconClicked', 'page_id': page_id + "+" + unitName, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl
                        });
                    });

                    // for page forward button and backward button:
                    $(document).on("click", ".button-next", function() {

                        // send ajax
                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'pageForwardClicked', 'page_id': page_id, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl
                        });

                    });

                    $(document).on("click", ".button-previous", function() {

                        // send ajax
                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'pageBackwardClicked', 'page_id': page_id, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl
                        });

                    });

                }

            }
        }



        
        // This method trigger the get_border_color, if there is no any other XBlock(e.g. Text paragraph XBlock) matches the skill name.

        if(editButton != 0) {
            $.ajax({
                url: runtime.handlerUrl(element, "get_border_color"),
                type: "POST",
                data: JSON.stringify({"getBorderColor": true}),
                success: function(data) {
                    console.log("text color: ", data);

                    // $(".action-button-text").length = 0 which means student view
                    if(data.setBorderColor == 0) {
                        // studio and lms: the structure of the html are not the same, data-usage-id is for studio use only.
                        if(data.skillname != "@") {
                            $("div[data-usage-id='" + xblock_id + "'] div[id='border']").css("border", "2px solid red");
                        }
                    } else {
                        $("div[data-usage-id='" + xblock_id + "'] div[id='border']").removeAttr("style");
                    }
                    if(data.skillname == "") {
                        $("div[data-usage-id='" + xblock_id + "'] div[id='border']").css("border", "2px solid green");
                    }
                }
            });
        }

        

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

                $("div").one("click", ".action-primary", function(event) {
	                //alert("Now the 'Yes' button trigger: " + event.target.className + "#" + event.target.id + ", xblock_id: " + selectedXBlockId);
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

    });
    
    
    
    
    function getDefaultData() {
        var getDefaultData = runtime.handlerUrl(element, "get_default_data");
        $.ajax({
            type: "POST",
            url: getDefaultData,
            data: JSON.stringify({'getDefualtData': true}),
            success: function(data) {
                //alert(data.text_content);
                $("div[data-id='" + xblock_id + "'] p[id='text-content']").val(data.text_content);
            }
        });
    }
    
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




    // when submit button has been clicked
    $('#submit', element).on('click', function(e){

        // for sound effect application: ============================
        var audio = document.createElement("audio");
        audio.src = "http://www.freesfx.co.uk/rx2/mp3s/5/16952_1461335341.mp3";
        audio.volumn = 0.50;
        audio.autoPlay = false;
        audio.preLoad = true;
        audio.play();
        //send ajax request to get the question name
        checkAnswer();
        removeBorderColor();
        addBorderColor();
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
            saveHint();
            //alert(question);
            //$("#" + question + "hint").text(hint_array[count]);
            //$("#hint").text(hint_array[count]);
        }
        //alert("hint message should show at the right place.");
        //alert(xblock_id);
        $("#" + xblock_code).text(hint_array[count]);

    });
    
    // check answer button
    function checkAnswer() {
        var selectedCheck = $("div[data-id='" + xblock_id + "'] input[name='choice']:checked");
        //alert(selectedCheck.val());
        // ==========================================================
        var setStatusWhenRefresh = runtime.handlerUrl(element, 'set_status_when_refresh');
        $.ajax({
            type: "POST",
            data: JSON.stringify({'setStatus': true, 'userChoice': selectedCheck.val()}),
            url: setStatusWhenRefresh,
            success: function(data) {
                // start to play the sound effect:

            }
        });


        var selectedCheck = $("div[data-id='" + xblock_id + "'] input[name='choice']:checked");
        var answerId = selectedCheck.val();
        var userSelected = answerId;

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
            success: function(data){
                // mark question as attempted
                //$("#question-block").attr("id", xblock_id + "question-block");
                //$("#question-block", element).addClass('attempted');


                if(data.correct == true){
                    selectedCheck.parent().addClass('correct');
                    selectedCheck.parent().append("<i class='tick'>&nbsp;&nbsp;&nbsp;  &#x2713;</i>");
                }else{
                    // indicate correct and incorrect
                    selectedCheck.parent().addClass('incorrect');
                    selectedCheck.parent().append("<i class='ballot'>&nbsp;&nbsp;&nbsp; &#10006;</i>");
                }

                var userUrl = runtime.handlerUrl(element, 'get_student_id');
                $.ajax({
                    type: "POST",
                    data: JSON.stringify({'hintCount': count, 'type': 'checkbutton', "section": section, "subsection": subsection, "unit": unit}),
                    url: userUrl,
                    success: function(data) {
                        console.log("User Id : " + data.user);
                        console.log("Xblock Id :" + data.xblock_id);
                        if(data.user !== null) {
                            // Then start to store all the information to db:
                            var student_id = data.user.toString().split(',')[3];

                        }
                    }
                });
            }
        });

        //================Testing the anonynous user id=================

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

    function saveHint() {
        //$("#hint").text(hint_array[count]);
        // start to send ajax request and store the user activities:
        var userUrl = runtime.handlerUrl(element, 'get_student_id');
        $.ajax({
            type: "POST",
            data: JSON.stringify({'hintCount': count, 'type': 'hintbutton', "section": section, "subsection": subsection, "unit": unit}),
            url: userUrl,
            success: function(data) {
                console.log("User Id : " + data.user);
                console.log("Xblock Id :" + data.xblock_id);
            }
        });
    }
}
