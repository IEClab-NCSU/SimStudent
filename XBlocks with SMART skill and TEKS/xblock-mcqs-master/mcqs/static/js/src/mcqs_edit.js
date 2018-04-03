function McqsXBlockInitStudio(runtime, element) {
    
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

        /* when the page on load, get all the data from python backend. */
        section = document.getElementsByClassName("navigation-parent")[0].innerText;
        subsection = document.getElementsByClassName("navigation-parent")[1].innerText;
        unit = $("span[class='title-value']").text();
        nextUnit = $(".is-current").next().find("a").text();
        preUnit = $(".is-current").prev().find("a").text();
        
        
        var getDefaultData = runtime.handlerUrl(element, 'get_default_data');
        $.ajax({
            type: "POST",
            url: getDefaultData,
            data: JSON.stringify({'result': 'getDefaultData'}),
            success: function(data) {
                
                $('#problemTitleForEdit').val(data.display_name);
                $('#problemIdForEdit').val(data.problemId);
                $('#kcForEdit').val(data.kc);
                $('#questionForEdit').val(data.question);

                var choices = "";
                for(var i = 0; i < data.choices.length; i++) {
                    if(i == data.choices.length - 1) {
                        choices += data.choices[i];
                    } else {
                        choices += data.choices[i] + '|';
                    }
                }
                $('#choicesForEdit').val(choices);
                $('#hintForEdit').val(data.hint);
                $('#correctForEdit').val(data.correct_choice);
                $('#imageUrlForEdit').val(data.image_url);
                $('#imageSizeForEdit').val(data.image_size);

                if(data.iso == 1) {
                    $("#isoQuestion").attr('checked', true);
                }
            }
        });
        
        
        
        var updateDisplayName = runtime.handlerUrl(element, 'update_display_name');
        $('#disableHint').on('change', function() {
            if( $('#disableHint').is(':checked') ) {
                $('#hintForEdit').val('');
                $('#hintForEdit').attr('disabled', 'disabled');
                $.ajax({
                    type: "POST",
                    url: updateDisplayName,
                    data: JSON.stringify({'result': 'Multiple Choice without hints'}),
                    success: function(data) {
                        $('#problemTitleForEdit').val(data.display_name);
                    }
                });
            } else {
                $('#hintForEdit').removeAttr('disabled');
                $('#hintForEdit').val(hintMessage);
                $.ajax({
                    type: "POST",
                    url: updateDisplayName,
                    data: JSON.stringify({'result': 'Text Box'}),
                    success: function(data) {
                        $('problemTitleForEdit').val(data.display_name);
                    }
                });
            }
        })


        $("#isoQuestion").on('change', function() {
            if($("#isoQuestion").is(':checked')) {
                $.ajax({
                    type: "POST",
                    url: runtime.handlerUrl(element, 'update_iso_question'),
                    data: JSON.stringify({'iso': 1})
                });
            } else {
                $.ajax({
                    type: "POST",
                    url: runtime.handlerUrl(element, 'update_iso_question'),
                    data: JSON.stringify({'iso': 0})
                });
            }
        });
        
    });
    
    // for cancel button:
    $('.cancel-button', element).click(function() {
        runtime.notify('cancel', {});
    });
    
    // for save button:
    $('.save-button', element).click(function() {
        var problemTitle = $("#problemTitleForEdit").val();
        var problemId = $("#problemIdForEdit").val();
        var question = $("#questionForEdit").val();
        var choices = $("#choicesForEdit").val().split("|");
        var hint = $("#hintForEdit").val();
        var correct = $("#correctForEdit").val();
        var kc = $("#kcForEdit").val();
        var imageUrl = $("#imageUrlForEdit").val();
        var imageSize = $("#imageSizeForEdit").val();
        
        
        var updateUrl = runtime.handlerUrl(element, 'update_question');
        var obj = {
            'problemTitle' : problemTitle,
            'problemId' : problemId,
            'question': question,
            'choices': choices,
            'hint': hint,
            'correct': correct,
            'kc': kc,
            'imageUrl': imageUrl,
            'imageSize': imageSize,
            'section': section,
            'subsection': subsection,
            'unit': unit,
            'preUnit': preUnit,
            'nextUnit': nextUnit
        }
        
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