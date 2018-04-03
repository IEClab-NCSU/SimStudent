function EmbedVideosEditXBlock(runtime, element) {

    // prevent the page jump to the top whenever the user want to save the xblock.
    if($(".save-button").attr("href") == "#") {
        $(".save-button").removeAttr("href");
        $(".cancel-button").removeAttr("href");
    }

    var section = "";
    var subsection = "";
    var unit = "";

    $(function($) {
        section = document.getElementsByClassName("navigation-parent")[0].innerText;
        subsection = document.getElementsByClassName("navigation-parent")[1].innerText;
        unit = $("span[class='title-value']").text();
    });



    $(element).find('.save-button').bind('click', function() {
        var handlerUrl = runtime.handlerUrl(element, 'studio_submit');

        /* List of attributes that are inputs */
        var attrList = [
            'youtube_id',
            'youtube_width',
            'youtube_height',
            'display_name',
            'problemId',
            'kc',
            'section',
            'subsection',
            'unit'
        ];



        var data = {};

        /* Save values from inputs to the data dict */
        attrList.forEach(function(item) {
            var value;
            if(item == "youtube_id") {
                var youtube = $(element).find('input[name=' + item + ']').val().split("=");
                value = youtube[youtube.length - 1];
            } else if (item == "section"){
                value = section;
            } else if (item == "subsection") {
                value = subsection;
            } else if (item == "unit") {
                value = unit;
            } else {
                value = $(element).find('input[name=' + item + ']').val();
            }
            /* If the user leaves blank would cause errors in Django,
               this way they are left to the default values
            */
            if (value != 'None') {
                data[item] = value;
            }
        });


        runtime.notify('save', {
            state: 'start'
        });
        $.post(handlerUrl, JSON.stringify(data)).done(function(response) {
            runtime.notify('save', {
                state: 'end'
            });
        });

        // send data to update question:
        $.ajax({
            type: "POST",
            data: JSON.stringify(data),
            url: runtime.handlerUrl(element, "update_question"),
            success: function(data){
                console.log("video border color: ", data['border']);
            }
        });


    });

    $(element).find('.cancel-button').bind('click', function() {
        runtime.notify('cancel', {});
    });
}
