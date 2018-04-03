/* Javascript for flashXBlock. */
function flashXBlockInitEdit(runtime, element) {

    $(element).find('.action-cancel').bind('click', function() {
        runtime.notify('cancel', {});
    });

    $(element).find('.action-save').bind('click', function() {
        var data = {
            'display_name': $('#flash_edit_display_name').val(),
            'url': $('#flash_edit_url').val(),
            'allow_download': $('#flash_edit_allow_download').val(),
            'source_text': $('#flash_edit_source_text').val(),
            'source_url': $('#flash_edit_source_url').val()
        };
        
        runtime.notify('save', {state: 'start'});
        
        var handlerUrl = runtime.handlerUrl(element, 'save_flash');
        $.post(handlerUrl, JSON.stringify(data)).done(function(response) {
            if (response.result === 'success') {
                runtime.notify('save', {state: 'end'});
                // Reload the whole page :
                // window.location.reload(false);
            } else {
                runtime.notify('error', {msg: response.message})
            }
        });
    });
}