function TextboxXBlockInitStudio(runtime, element) {
    
    $(function ($) {
        /* when the page on load, get all the data from python backend. */
        var getDefaultData = runtime.handlerUrl(element, 'get_default_data');
        var hintMessage = "";
        $.ajax({
            type: "POST",
            url: getDefaultData,
            data: JSON.stringify({'result': 'getDefaultData'}),
            success: function(data) {
                
                $('#problemTitleForEdit').val(data.display_name);
                $('#problemIdForEdit').val(data.problemId);
                $('#kcForEdit').val(data.kc);
                $('#questionForEdit').val(data.question);
                $('#answerForEdit').val(data.correct_answer);
                $('#hintForEdit').val(data.hint);
                hintMessage = data.hint;
                $('#correctForEdit').val(data.correct_choice);
                $('#imageUrlForEdit').val(data.image_url);
                $('#imageSizeForEdit').val(data.image_size);
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
                    data: JSON.stringify({'result': 'Text Box without hints'}),
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
        var correct_answer = $("#answerForEdit").val();
        var hint = $("#hintForEdit").val();
        var kc = $("#kcForEdit").val();
        var imageUrl = $('#imageUrlForEdit').val();
        var imageSize = $("#imageSizeForEdit").val();
        
        var updateUrl = runtime.handlerUrl(element, 'update_question');
        var obj = {
            'problemTitle' : problemTitle,
            'problemId' : problemId,
            'question': question,
            'correct_answer': correct_answer,
            'hint': hint,
            'kc': kc,
            'image_url': imageUrl,
            'imageSize': imageSize
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