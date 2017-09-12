function TextXBlockInitStudio(runtime, element) {
    
    $(function ($) {
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
                $('#questionForEdit').val(data.question);
                $('#textTitleForEdit').val(data.text_title);
                $('#textContentForEdit').val(data.text_content);
                $('#textSubTitleForEdit').val(data.text_sub_title);
                $('#imageForEdit').val(data.image_url);
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
        
        var obj = {
            'kc': kc,
            'textTitle': textTitle,
            'textContent' : textContent,
            'textSubTitle' : textSubTitle,
            'image_url': image_url
        }
       
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