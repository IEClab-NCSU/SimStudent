
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
    var hasBeenSent = false;
    var page_id = "";
    var skillname = "";
    //var nextUnit = "";
    //var previousUnit = "";
    var editButton = $(".action-button-text").length;
    var unitElements = $(".nav-item");
    var currentUnitPosition = 0;
    var enable_dynamic = true;
    var enable_hiding = true;


  
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
                skillname = data.skillname;

                $("div[data-usage-id='" + xblock_id + "'] span[id='hint']").attr("id", xblock_code);
                $("div[data-usage-id='" + xblock_id + "'] input[id='submit']").attr("mode-id", "submit" + xblock_code);
                $("div[data-usage-id='" + xblock_id + "'] input[id='getHint']").attr("mode-id", "getHint" + xblock_code);
                $("div[data-usage-id='" + xblock_id + "']").attr("id", "1" + xblock_code);
                $("#1" + xblock_code).parent().prev().find(".xblock-display-name").append("&nbsp; &nbsp;<span style='color: red'><b> Skill Name: " + skillname + "</b></span>");
            }
        });

        for(var i = 0; i < unitElements.length; i++) {
            if($(unitElements[i]).attr("data-page-title") == $($(".course_info")[0]).text().split("+")[2].trim()) {
                currentUnitPosition = i;
            }
        }

        // make sure it only works for the student view:
        if(editButton == 0) {

            var getConditionControl = runtime.handlerUrl(element, "get_pastel_student_id");
            $.ajax({
                url: runtime.handlerUrl(element, 'get_pastel_student_id'),
                type: "POST",
                async: false,
                data: JSON.stringify({"get_pastel_student_id": true}),
                success: function(data){
                    enable_dynamic = data.enable_dynamic == "1" ? true: false;
                    enable_hiding = data.enable_hiding == "1" ? true: false;

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

                    var courseInfo = $($(".course_info")[0]).text().split("+");
                    var section = courseInfo[0].trim();
                    var subsection = courseInfo[1].trim();
                    var unit = courseInfo[2].trim();

                    if(countTimes >= 1 && userChoice > 0) {
                        var selectedCheck = $("div[data-usage-id='" + xblock_id + "'] input[type='radio'][checked]");

                        if(correctOrNot) {

                            selectedCheck.parent().addClass('correct');
                            selectedCheck.parent().append("<i class='tick'>&nbsp;&nbsp;&nbsp;  &#x2713; &nbsp;&nbsp;&nbsp;Correct</i>");


                            // add "enable_dynamic" to control the condition
                            if(enable_dynamic) {

                                if(!data.dynamicClicked && countTimes != 1) {
                                    var hostname = $(location).attr('host') + "/courses/";

                                    $.ajax({
                                        url: runtime.handlerUrl(element, "get_skill_mapping"),
                                        type: "POST",
                                        data: JSON.stringify({"getLocation": true}),
                                        success: function(data) {

                                            var course_id = data['course_id'];
                                            var paragraph_id = data['paragraph_id'];
                                            var location_id = data['location_id'];

                                            if($("div[data-usage-id='" + xblock_id + "'] h5[id='navigate_id']").length > 0 || $("div[data-usage-id='" + xblock_id + "'] h5[id='navigate_id']").html() != "") {
                                                $("div[data-usage-id='" + xblock_id + "'] h5[id='navigate_id']").remove();
                                            }

                                            $("div[data-usage-id='" + xblock_id + "'] div[class='hint-block']").append("<h5 id='navigate_id' ><mark>Click this <a id='dlink' href='https://" + hostname + course_id + "/jump_to_id/" + location_id + "#" + paragraph_id + "'>link</a> to review the course content and examples on solving this question.</mark></h5>");
                                            $("div[data-usage-id='" + xblock_id + "'] div[class='hint-block'] a[id='dlink']").click(function() {

                                                var userUrl = runtime.handlerUrl(element, 'get_student_id');
                                                $.ajax({
                                                    type: "POST",
                                                    data: JSON.stringify({'hintCount': count, 'type': 'dynamicLinkClicked', 'page_id': page_id + "|" + "https://" + hostname + course_id + "/jump_to_id/" + location_id + "#" + paragraph_id, "section": section, "subsection": subsection, "unit": unit}),
                                                    url: userUrl
                                                });
                                            });
                                        }
                                    });
                                }
                            }


                        } else {

                            selectedCheck.parent().addClass('incorrect');
                            selectedCheck.parent().append("<i class='ballot'>&nbsp;&nbsp;&nbsp; &#10006; &nbsp;&nbsp;&nbsp;Incorrect, please try it again.</i>");

                            // add "enable_dynamic" to control the condition
                            if(enable_dynamic) {
                                var hostname = $(location).attr('host') + "/courses/";

                                $.ajax({
                                    url: runtime.handlerUrl(element, "get_skill_mapping"),
                                    type: "POST",
                                    data: JSON.stringify({"getLocation": true}),
                                    success: function(data) {

                                        var course_id = data['course_id'];
                                        var paragraph_id = data['paragraph_id'];
                                        var location_id = data['location_id'];

                                        if($("div[data-usage-id='" + xblock_id + "'] h5[id='navigate_id']").length > 0 || $("div[data-usage-id='" + xblock_id + "'] h5[id='navigate_id']").html() != "") {
                                            $("div[data-usage-id='" + xblock_id + "'] h5[id='navigate_id']").remove();
                                        }

                                        $("div[data-usage-id='" + xblock_id + "'] div[class='hint-block']").append("<h5 id='navigate_id' ><mark>Click this <a id='dlink' href='https://" + hostname + course_id + "/jump_to_id/" + location_id + "#" + paragraph_id + "'>link</a> to review the course content and examples on solving this question.</mark></h5>");
                                        $("div[data-usage-id='" + xblock_id + "'] div[class='hint-block'] a[id='dlink']").click(function() {

                                            var userUrl = runtime.handlerUrl(element, 'get_student_id');
                                            $.ajax({
                                                type: "POST",
                                                data: JSON.stringify({'hintCount': count, 'type': 'dynamicLinkClicked', 'page_id': page_id + "|" + "https://" + hostname + course_id + "/jump_to_id/" + location_id + "#" + paragraph_id, "section": section, "subsection": subsection, "unit": unit}),
                                                url: userUrl
                                            });
                                        });
                                    }
                                });
                            }


                        }


                    }

                }
            });

        }


        
        addBorderColor();

        /*
            !!!!!!
            Disable the module_skillname_saved method if we want to use it in the study!
            Once we refresh the student view this method will trigger and save the current location to the table: module_skillname.
            !!!!!!
        */

        //var locationObject = $('#seq_content').children()[1] == undefined ? undefined : $('#seq_content').children()[1].getAttribute('data-usage-id');
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
            Catch the moment when the page is loaded.
        */
        if(editButton == 0) {
            var courseInfo = $($(".course_info")[0]).text().split("+");
            var section = courseInfo[0].trim();
            var subsection = courseInfo[1].trim();
            var unit = courseInfo[2].trim();


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

//                    // detect if the browser window is closing:
//                    window.addEventListener("beforeunload", function (e) {
//                        var confirmationMessage = "Do Not close the browser like that!!!";
//
//                        (e || window.event).returnValue = confirmationMessage; //Gecko + IE
//                        return confirmationMessage;                            //Webkit, Safari, Chrome
//                    });




                    $(".accordion-nav").on("click.menu_link", function() {

                        var courseInfo = $($(".course_info")[0]).text().split("+");
                        var section = courseInfo[0].trim();
                        var subsection = courseInfo[1].trim();
                        var unit = courseInfo[2].trim();

                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'menuLinkClicked', 'page_id': page_id + "+" + this.getElementsByTagName('p')[0].innerHTML, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl
                        });
                    });

                    $("ul[class='dropdown-menu'] li[class='item']").click(function() {
                        var courseInfo = $($(".course_info")[0]).text().split("+");
                        var section = courseInfo[0].trim();
                        var subsection = courseInfo[1].trim();
                        var unit = courseInfo[2].trim();

                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, "type": "logoutClicked", "page_id": page_id, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl,
                            async: false
                        });

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


                    $(".nav-item").on("click.unit_icon", function() {

                        var courseInfo = $($(".course_info")[0]).text().split("+");
                        var section = courseInfo[0].trim();
                        var subsection = courseInfo[1].trim();
                        var unit = courseInfo[2].trim();

                        var unitName = $(this).attr("data-page-title");

                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'unitIconClicked', 'page_id': page_id + "+" + unitName, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl
                        });
                    });

                    // for page forward button and backward button:
                    $(".button-next").on("click.page_forward", function() {

                        var courseInfo = $($(".course_info")[0]).text().split("+");
                        var section = courseInfo[0].trim();
                        var subsection = courseInfo[1].trim();
                        var unit = courseInfo[2].trim();

                        var position = 0;
                        for(var i = 0; i < unitElements.length; i++) {
                            if($(unitElements[i]).attr("data-page-title") == unit) {
                                position = i;
                            }
                        }

                        var preUnit = $(unitElements[position - 1]).attr("data-page-title");

                        var nextUnit = $(unitElements[position + 1]).attr("data-page-title");

                        if(nextUnit == undefined && (currentUnitPosition == unitElements.length - 1)) {
                            preUnit = unit;
                            unit = "next unit";
                        }

                        // send ajax
                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'pageForwardClicked', 'page_id': page_id + "|" + preUnit, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl,
                            async: false
                        });

                    });

                    $(".button-previous").on("click.page_backward", function() {

                        var courseInfo = $($(".course_info")[0]).text().split("+");
                        var section = courseInfo[0].trim();
                        var subsection = courseInfo[1].trim();
                        var unit = courseInfo[2].trim();

                        var position = 0;
                        for(var i = 0; i < unitElements.length; i++) {
                            if($(unitElements[i]).attr("data-page-title") == unit) {
                                position = i;
                            }
                        }
                        var nextUnit = $(unitElements[position + 1]).attr("data-page-title");

                        var preUnit = $(unitElements[position - 1]).attr("data-page-title");

                        if(preUnit == undefined && currentUnitPosition == 0) {
                            nextUnit = unit;
                            unit = "previous unit";
                        }

                        // send ajax
                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'pageBackwardClicked', 'page_id': page_id + "|" + nextUnit, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl,
                            async: false
                        });

                    });

                }

            }

             // when .path change, update the click event???
            $(".path").bind('DOMNodeInserted', function(e) {

                  // define if this unit is at the end of the subsection or is the first unit of the subsection

                $(".accordion-nav").off(".menu_link").on("click.menu_link", function() {

                        var courseInfo = $($(".course_info")[0]).text().split("+");
                        var section = courseInfo[0].trim();
                        var subsection = courseInfo[1].trim();
                        var unit = courseInfo[2].trim();

                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'menuLinkClicked', 'page_id': page_id + "+" + this.getElementsByTagName('p')[0].innerHTML, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl
                        });
                });


                $(".button-next").off(".page_forward").on("click.page_forward", function() {

                        var courseInfo = $($(".course_info")[0]).text().split("+");
                        var section = courseInfo[0].trim();
                        var subsection = courseInfo[1].trim();
                        var unit = courseInfo[2].trim();

                        var position = 0;
                        for(var i = 0; i < unitElements.length; i++) {
                            if($(unitElements[i]).attr("data-page-title") == unit) {
                                position = i;
                            }
                        }
                        var preUnit = $(unitElements[position - 1]).attr("data-page-title");

                        var nextUnit = $(unitElements[position + 1]).attr("data-page-title");

                        if(nextUnit == undefined && (currentUnitPosition == unitElements.length - 1)) {
                            preUnit = unit;
                            unit = "next unit";
                        }



                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'pageForwardClicked', 'page_id': page_id + "|" + preUnit, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl,
                            async: false
                        });

                    });

                $(".button-previous").off(".page_backward").on("click.page_backward", function() {
                        console.log("page-backward event has been triggered.");
                        var courseInfo = $($(".course_info")[0]).text().split("+");
                        var section = courseInfo[0].trim();
                        var subsection = courseInfo[1].trim();
                        var unit = courseInfo[2].trim();

                        var position = 0;
                        for(var i = 0; i < unitElements.length; i++) {
                            if($(unitElements[i]).attr("data-page-title") == unit) {
                                position = i;
                            }
                        }

                        var nextUnit = $(unitElements[position + 1]).attr("data-page-title");

                        var preUnit = $(unitElements[position - 1]).attr("data-page-title");

                        if(preUnit == undefined && currentUnitPosition == 0) {
                            nextUnit = unit;
                            unit = "previous unit";
                        }

                        var userUrl = runtime.handlerUrl(element, 'get_student_id');
                        $.ajax({
                            type: "POST",
                            data: JSON.stringify({'hintCount': count, 'type': 'pageBackwardClicked', 'page_id': page_id + "|" + nextUnit, "section": section, "subsection": subsection, "unit": unit}),
                            url: userUrl,
                            async: false
                        });
                });
            });


        }



        // when submit button has been clicked
        //$(document).on("click", "#submit", function(event) {
        $(document).on("click", "input[mode-id='submit" + xblock_code + "']", function(event, type_of_xblock, construct_obj, hintCount, attempt) {
        //$('#submit', element).on('click', function(e){
            event.stopPropagation();
            //$("#button1").trigger("click", [arg1, arg2]);

            if(type_of_xblock == "iso") {
                //$("input[mode-id='submit" + xblock_code + "']").attr({"user-choice": $("input[mode-id='submit" + xblock_code + "']").siblings("#choices").find("input[name='choice']:checked").attr("value"), "attempt": parseInt($("input[mode-id='submit" + xblock_code + "']").attr("attempt")) + 1});
                console.log(type_of_xblock, construct_obj);

                // start write codes here, save iso type of question to database.
                var userUrl = runtime.handlerUrl(element, 'get_student_id');
                $.ajax({
                    type: "POST",
                    data: JSON.stringify({'hintCount': $("input[mode-id='submit" + xblock_code + "']").attr("hintCount"),
                                          'type': 'isomcqsQuestionClick',
                                          'page_id': page_id,
                                          'attempt': $("input[mode-id='submit" + xblock_code + "']").attr("attempt"),
                                          'construct_obj': construct_obj
                                         }),
                    url: userUrl
                });

            } else {
                //before that, check if the user select a choice or not
                checkAnswer();
                removeBorderColor();
                addBorderColor();
            }


            /*/ for sound effect application: ============================
            var audio = document.createElement("audio");
            audio.src = "http://www.freesfx.co.uk/rx2/mp3s/5/16952_1461335341.mp3";
            audio.volumn = 0.50;
            audio.autoPlay = false;
            audio.preLoad = true;
            audio.play();
            */


        });



        // when getHint button has been clicked
        //$(document).on("click", "#getHint",function(event) {
        //$(document).on("click", "input[mode-id='getHint" + xblock_code + "']", function(event) {
            //event.stopPropagation();
        $("input[mode-id='submit" + xblock_code + "']").attr("hintCount", parseInt($("input[mode-id='submit" + xblock_code + "']").attr("hintCount")) + 1);
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
            if(!$("div[data-usage-id='" + xblock_id + "'] span[title='Get Hint']").hasClass("help-tip")) {
                $("div[data-usage-id='" + xblock_id + "'] span[title='Get Hint']").addClass("help-tip");
            }
            $("#" + xblock_code).html(hint_array[count]);
            //for hint length
            //$("#getHint").val("Hint(" + (hint_array.length - count - 1) + ")");
            $("input[mode-id='getHint" + xblock_code + "']").val("More Hints");

        });



        
        // This method trigger the get_border_color, if there is no any other XBlock(e.g. Text paragraph XBlock) matches the skill name.

        if(editButton != 0) {
            $.ajax({
                url: runtime.handlerUrl(element, "get_border_color"),
                type: "POST",
                data: JSON.stringify({"getBorderColor": true}),
                success: function(data) {

                    if(data.setBorderColor == 0) {
                        // studio and lms: the structure of the html are not the same, data-usage-id is for studio use only.
                        $("div[data-usage-id='" + xblock_id + "'] div[id='border']").css("border", "2px solid red");

                    } else {
                        $("div[data-usage-id='" + xblock_id + "'] div[id='border']").removeAttr("style");
                    }

                    if(data.skillname == "" || data.problemId == "" || data.problemId == "Enter a Problem Name here") {
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
        

        //This method would be called if we want to make ALP works. Appear or Disappear
        if(editButton == 0 && enable_hiding) { // student view
            pastel_dis_show();
        }

        // change hint button add a number of hint at the end of the 'Hint'  --> 'Hint()'

        //getHintLength();
        
    });

    function getHintLength() {
        $.ajax({
            url: runtime.handlerUrl(element, 'get_hint_length'),
            type: 'POST',
            data: JSON.stringify({"getHintLength": true}),
            success: function(data) {
                $("div[data-usage-id='" + xblock_id + "'] input[id='getHint']").val('Hint(' + data['length'] + ')');
            }
        });
    }

    
    function pastel_dis_show() {


        // for testing propose only, get student_pastel_id from DB.
        $.ajax({
            url: runtime.handlerUrl(element, 'get_pastel_student_id'),
            type: "POST",
            data: JSON.stringify({"get_pastel_student_id": true}),
            success: function(data){

                if(data['hasBeenSent'] == 'false') {
                    $.ajax({
                        url: runtime.handlerUrl(element, "get_studentId_and_skillname"),
                        type: "POST",
                        data: JSON.stringify({"getStudent_id": true}),
                        success: function(data) {

                            var student_id = data['student_id'];
                            var skillname = data['skillname'];

                            $.ajax({
                                url: "https://kona.education.tamu.edu:2401/ALP/getLValue",
                                //url: "http://localhost:8080/ALP/getLValue",
                                type: "GET",
                                data: "student_id=" + student_id + "&skillname=" + skillname.split(' ').join('_'),
                                dataType: 'jsonp',
                                jsonp: 'callback',
                                anysc: false,
                                success: function(data) {
                                    if(data != null) {
                                           var probability = data['probability'];
                                           console.log("data from ALP: ", data);
                                           if(parseFloat(probability) > 0.95) {
                                                 var courseInfo = $($(".course_info")[0]).text().split("+");
                                                 var section = courseInfo[0].trim();
                                                 var subsection = courseInfo[1].trim();
                                                 var unit = courseInfo[2].trim();
                                                 var userUrl = runtime.handlerUrl(element, 'get_student_id');
                                                 $.ajax({
                                                    type: "POST",
                                                    data: JSON.stringify({'hintCount': count, 'type': 'problemHidden', 'page_id': page_id + "|" + probability, "section": section, "subsection": subsection, "unit": unit}),
                                                    url: userUrl,
                                                    success: function() {
                                                        $("div[data-usage-id='" + xblock_id + "']").remove();
                                                    }
                                                 });



                                           }
                                    }

                                }
                            });



                        }
                    });
                }


            }
        });
    }

    
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
                var course = data['course'];
                console.log("Course id: ", course);
                $.ajax({
                    url: "https://kona.education.tamu.edu:2401/ALP/callandsaveBKT",
                    //url: "http://localhost:8080/ALP/callandsaveBKT",
                    type: "GET",
                    data: "student_id=" + student_id + "&skillname=" + skillname.split(' ').join('_') + "&correctness=" + correctness + "&question_id=" + question_id + "&course=" + course,
                    dataType: 'jsonp',
                    jsonp: 'callback',
                    anysc: false,
                    success: function(data) {
                        //alert("ALP" + data['probability']);
                    }
                });
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
        

        var answerId = selectedCheck.val();
        var userSelected = answerId;
        console.log("user selected: " + answerId + ", xblock_code: " + xblock_code);

        $("div[data-id='" + xblock_id + "'] input[name='choice']").not(selectedCheck).removeAttr('checked');
        selectedCheck.attr("checked", "checked");
        // remove class and <i> tag if there are any exist:
        $("div[data-id='" + xblock_id + "'] label").attr('class', '');
        $("div[data-id='" + xblock_id + "'] i").remove(".tick");
        $("div[data-id='" + xblock_id + "'] i").remove(".ballot");

        
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

                var courseInfo = $($(".course_info")[0]).text().split("+");
                var section = courseInfo[0].trim();
                var subsection = courseInfo[1].trim();
                var unit = courseInfo[2].trim();

                if(data.correct == true){
                    selectedCheck.parent().addClass('correct');
                    selectedCheck.parent().append("<i class='tick'>&nbsp;&nbsp;&nbsp;  &#x2713; &nbsp;&nbsp;&nbsp;Correct</i>");

                    if(!hasBeenSent && editButton == 0) {
                        if(enable_hiding) {
                            saveStudentDataForProbability(1);
                        }

                        hasBeenSent = true;
                    }

                    // we make dynamic link disappear only if the student click the link.

                    if(data.dynamicClicked) {
                        $("div[data-usage-id='" + xblock_id + "'] h5[id='navigate_id']").remove();
                    }
                    
                }else{
                    // indicate correct and incorrect
                    selectedCheck.parent().addClass('incorrect');
                    selectedCheck.parent().append("<i class='ballot'>&nbsp;&nbsp;&nbsp; &#10006; &nbsp;&nbsp;&nbsp; Incorrect, please try it again.</i>");
                    // save student data for probability
                    if(!hasBeenSent && editButton == 0) {
                        if(enable_hiding) {
                            saveStudentDataForProbability(0);
                        }
                        hasBeenSent = true;
                    }

                    if(enable_dynamic) {
                        var hostname = $(location).attr('host') + "/courses/";

                        $.ajax({
                            url: runtime.handlerUrl(element, "get_skill_mapping"),
                            type: "POST",
                            data: JSON.stringify({"getLocation": false}),
                            anysc: false,
                            success: function(data) {

                                var course_id = data['course_id'];
                                var paragraph_id = data['paragraph_id'];
                                var location_id = data['location_id'];

                                if($("div[data-usage-id='" + xblock_id + "'] h5[id='navigate_id']").length > 0 || $("div[data-usage-id='" + xblock_id + "'] h5[id='navigate_id']").html() != "") {
                                    $("div[data-usage-id='" + xblock_id + "'] h5[id='navigate_id']").remove();
                                }

                                $("div[data-usage-id='" + xblock_id + "'] div[class='hint-block']").append("<h5 id='navigate_id' ><mark>Click this <a id='dlink' href='https://" + hostname + course_id + "/jump_to_id/" + location_id + "#" + paragraph_id + "'>link</a> to review the course content and examples on solving this question.</mark></h5>");
                                $("div[data-usage-id='" + xblock_id + "'] div[class='hint-block'] a[id='dlink']").click(function() {

                                    var userUrl = runtime.handlerUrl(element, 'get_student_id');
                                    $.ajax({
                                        type: "POST",
                                        data: JSON.stringify({'hintCount': count, 'type': 'dynamicLinkClicked', 'page_id': page_id + "|" + "https://" + hostname + course_id + "/jump_to_id/" + location_id + "#" + paragraph_id, "section": section, "subsection": subsection, "unit": unit}),
                                        url: userUrl
                                    });
                                });
                            }
                        });
                    }

                    // after that, you can start from here.
                    //$("div[data-id='" + xblock_id + "'] div[class='hint-block']").append("<div>Please click the <a href='" + hostname + "'>link</a> here to review the course content again.");
                }


                
                var userUrl = runtime.handlerUrl(element, 'get_student_id');
                
                $.ajax({
                    type: "POST",
                    data: JSON.stringify({'hintCount': count, 'type': 'checkbutton', 'page_id': page_id, "section": section, "subsection": subsection, "unit": unit}),
                    url: userUrl,
                    success: function() {

                    }
                });
            }
        });
         
        
        
        
    }

    
    
    
    // get hint function
    function getHint() {   
        // send Ajax:
        var hintUrl = runtime.handlerUrl(element, 'get_hint_text');

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
        var courseInfo = $($(".course_info")[0]).text().split("+");
        var section = courseInfo[0].trim();
        var subsection = courseInfo[1].trim();
        var unit = courseInfo[2].trim();

        var hintUrl = runtime.handlerUrl(element, 'get_hint');

        $.ajax({
            type: "POST",
            data: JSON.stringify({'getHint': true}),
            url: hintUrl,
            async: false
        });

        var userUrl = runtime.handlerUrl(element, 'get_student_id');
        $.ajax({
            type: "POST",
            data: JSON.stringify({'hintCount': count, 'type': 'hintbutton', 'page_id': page_id, "section": section, "subsection": subsection, "unit": unit}),
            url: userUrl
        });
    }
    


}
