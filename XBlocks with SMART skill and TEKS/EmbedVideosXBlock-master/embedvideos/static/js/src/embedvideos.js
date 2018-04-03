/* Javascript for EmbedVideosXBlock. */


function EmbedVideosXBlock(runtime, element) {



    $(function ($) {

        var xblock_id = "";
        var xblock_code = "";
        var skillname = "";
        var youtubeId = "";
        var width = "";
        var height = "";
        var pageId = "";
        var playerInfoList = [];
        var section = "";
        var subsection = "";
        var unit = "";
        var editButton = $(".action-button-text").length;

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

                $("div[data-usage-id='" + xblock_id + "']").attr("id", xblock_code);

                $("#" + xblock_code).parent().prev().find(".xblock-display-name").append("&nbsp; &nbsp;<span style='color: red'><b> Skill Name: " + skillname + "</b></span>");
            }
        });

        if(editButton == 0) {
            var info = $(".path").text().split(">");
            section = info[0].trim();
            subsection = info[1].trim();
            unit = info[2].trim();

        }

        $.ajax({
            url: runtime.handlerUrl(element, "get_youtube_id"),
            type: "POST",
            data: JSON.stringify({"get_youtube_id": true}),
            async: false,
            success: function(data) {
                youtubeId = data["youtube_id"];
                width = data["youtube_width"];
                height = data["youtube_height"];
            }
        });


        /*
            !!!!!!  -- Link generator
            Disable the module_skillname_saved method if we want to use it in the study!
            Once we refresh the student view this method will trigger and save the current location to the table: module_skillname.
            !!!!!!
        */

        var locationObject = $("button.nav-item.active").attr("data-id") == undefined ? undefined : $("button.nav-item.active").attr("data-id");
        if(locationObject != undefined) {
            var locationArray = locationObject.split("@");
            pageId = locationArray[locationArray.length - 1];
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

        var player = {
            playVideo: function(container, videoId) {
                if (typeof(YT) == 'undefined' || typeof(YT.Player) == 'undefined') {
                    window.onYouTubePlayerAPIReady = function() {
                        player.loadPlayer(container, videoId);
                    };
                    $.getScript('//www.youtube.com/player_api');
                } else {
                    player.loadPlayer(container, videoId);
                }
            },
            loadPlayer: function(container, videoId) {
                player = new YT.Player(container,{
                    height: height,
                    width: width,
                    videoId: videoId,
                    events: {
                        onReady: onPlayerReady,
                        onStateChange: function(event) {
                            switch(event.data) {
                                 case(0):
                                    console.log("ended", player.getCurrentTime());
                                    get_student_id_handler("video end", player.getCurrentTime());
                                 break;
                                 case(1):
                                    console.log("playing", player.getCurrentTime());
                                    get_student_id_handler("video playing", player.getCurrentTime());
                                 break;
                                 case(2):
                                    console.log("paused", player.getCurrentTime());
                                    get_student_id_handler("video paused", player.getCurrentTime());
                                 break;
                                 case(3):
                                    console.log("loading", player.getCurrentTime());
                                    get_student_id_handler("video loaded", player.getCurrentTime());
                                 break;
                            }
                        }
                    }
                });
            }
        };

        var videos = document.getElementsByClassName("video_xblock");
        var containerId = $(videos[0]).attr("id");

        player.playVideo(containerId, youtubeId);


        // 4. The API will call this function when the video player is ready.
        function onPlayerReady(event) {

            //event.target.playVideo();
            return false;
        }

        function onPlayerStateChange(event) {
            console.log(event);
            switch(event.data) {
    //             case(-1):
    //                console.log("unstarted", player.getCurrentTime());
    //                get_student_id_handler("video unstarted", player.getCurrentTime());
    //             break;
                 case(0):
                    console.log("ended", player.getCurrentTime());
                    get_student_id_handler("video end", player.getCurrentTime());
                 break;
                 case(1):
                    console.log("playing", player.getCurrentTime());
                    get_student_id_handler("video playing", player.getCurrentTime());
                 break;
                 case(2):
                    console.log("paused", player.getCurrentTime());
                    get_student_id_handler("video paused", player.getCurrentTime());
                 break;
                 case(3):
                    console.log("loading", player.getCurrentTime());
                    get_student_id_handler("video loaded", player.getCurrentTime());
                 break;
    //             case(5):
    //                console.log("video cued");
    //                get_student_id_handler("video cued", player.getCurrentTime());
    //             break;
            }
        }





        // This method trigger the get_border_color, if there is no any other XBlock(e.g. Textbox or multiple choices XBlock) matches the skill name.
        var editButton = $(".action-button-text").length;
        if(editButton != 0) {
            $.ajax({
                url: runtime.handlerUrl(element, 'get_border_color'),
                type: "POST",
                data: JSON.stringify({"getBorderColor": true}),
                success: function(data) {
                    if(data.setBorderColor == 0) {
                        // studio and lms: the structure of the html are not the same, data-usage-id is for studio use only.
                        $("div[data-usage-id='" + xblock_id + "'] div[id='border']").css("border", "2px solid red");
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
                        async: false,
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


        function get_student_id_handler(status, time) {
            var userUrl = runtime.handlerUrl(element, 'get_student_id');
            $.ajax({
                type: "POST",
                data: JSON.stringify({'status': status, 'time': time, 'pageId': pageId, "section": section, "subsection": subsection, "unit": unit}),
                url: userUrl,
                success: function() {}
            });
        }


    });


}



