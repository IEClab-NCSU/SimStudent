function TextXBlockInitStudio(runtime, element) {
    
    var section = "";
    var subsection = "";
    var unit = "";
    var nextUnit = "";
    var preUnit = "";
    
    $(function ($) {

        // prevent the page jump to the top whenever the user want to save the xblock.
        if($(".save-button").attr("href") == "#") {
            $(".save-button").removeAttr("href");
            $(".cancel-button").removeAttr("href");
        }

        section = document.getElementsByClassName("navigation-parent")[0].innerText;
        subsection = document.getElementsByClassName("navigation-parent")[1].innerText;
        unit = $("span[class='title-value']").text();
        nextUnit = $(".is-current").next().find("a").text();
        preUnit = $(".is-current").prev().find("a").text();
        
        /* when the page on load, get all the data from python backend. */
        var getDefaultData = runtime.handlerUrl(element, 'get_default_data');
        $.ajax({
            type: "POST",
            url: getDefaultData,
            data: JSON.stringify({'result': 'getDefaultData'}),
            success: function(data) {
                $('#problemTitleForEdit').val(data.display_name);
                $('#problemIdForEdit').val(data.problemId);
                $('#kcForEdit').val(data.kc);
                $('#SMARTkcForEdit').val(data.SMARTkc);
                $('#questionForEdit').val(data.question);
                $('#textTitleForEdit').val(data.text_title);
                $('#textContentForEdit').val(data.text_content);
                $('#textSubTitleForEdit').val(data.text_sub_title);
                $('#imageForEdit').val(data.image_url);
                $('#imageSizeForEdit').val(data.image_size);
                var skillset = data.skillset;
                for(var i = 0; i < skillset.length; i++) {
                    $('#skillset').append("<option value=" + skillset[i][0] + ">" + skillset[i][0] + "</option>");
                }

            }
        });

        $("#skillset").change(function() {
            //console.log($("#skillset option:selected").text());
            var selectedSkill  = $("#skillset option:selected").text();
            console.log(selectedSkill);
            console.log($("#kcForEdit").html());
            if($("#kcForEdit").val() == "") {
                $("#kcForEdit").val(selectedSkill);
            } else {
                $("#kcForEdit").val($("#kcForEdit").val() + "," + selectedSkill);
            }
        });
        
        
    });
    

    
    // for cancel button:
    $('.cancel-button', element).click(function() {
        runtime.notify('cancel', {});
    });
    
    // for save button:
    $('.save-button', element).click(function() {
        var kc = $("#kcForEdit").val();
        var textContent = $("#textContentForEdit").val();
        var textTitle = $("#textTitleForEdit").val();
        var textSubTitle = $("#textSubTitleForEdit").val();
        var image_url = $("#imageForEdit").val();
        var imageSize = $("#imageSizeForEdit").val();
        var obj = {
            'kc': kc,
            'textTitle': textTitle,
            'textContent' : textContent,
            'textSubTitle' : textSubTitle,
            'image_url': image_url,
            'imageSize': imageSize,
            'section': section,
            'subsection': subsection,
            'unit': unit,
            'preUnit': preUnit,
            'nextUnit': nextUnit
        }
       //alert(section + ", " + subsection + ", " + unit);
        var updateUrl = runtime.handlerUrl(element, 'update_question');
        $.ajax({
            type: "POST",
            data: JSON.stringify(obj),
            url: updateUrl,
            success: function(data){
                runtime.notify('save', {state: 'end'});
            }
        });
        
        
    });
                                     

        
}