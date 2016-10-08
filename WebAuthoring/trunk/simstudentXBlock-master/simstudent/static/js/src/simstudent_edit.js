/* Javascript for simstudentXBlock. */
function simstudentXBlock(runtime, element) {

    function paellaSaved(result) {
        $('.server', element).text();
        $('.video_id', element).text(result.video_id);
        $('.display_name', element).text(result.display_name);
    }

    $(element).find('.cancel-button').bind('click', function() {
        runtime.notify('cancel', {});
    });

    $(element).find('.save-button').bind('click', function() {
        var brd_url = encodeURIComponent($(edit_href).context.value);
        var complete_url = "http://kona.pslc.cs.cmu.edu:8080/WebAuthoring/Communicator/SimStudentServlet/WebContent/tutor.html?BRD="
        + brd_url
        +"&CSS=&INFO=&BRMODE=AuthorTimeTutoring&AUTORUN=on&KEYBOARDGROUP=Disabled&BACKDIR=http%3A%2F%2Fkona.pslc.cs.cmu.edu%3A8080%2FWebAuthoring%2FCommunicator%2FSimStudentServlet%2FWEB-INF%2Fclasses&BACKENTRY=interaction.ModelTracerBackend&PROBLEM=xxx&DATASET=FlashLoggingTest_xxx&LEVEL1=Unit1&TYPE1=unit&USER=qa-test&GENERATED=on&SOURCE=PACT_CTAT_HTML5&USEOLI=false&SLOG=true&LOGTYPE=None&DISKDIR=.&PORT=4000&REMOTEURL=http%3A%2F%2Fkona.pslc.cs.cmu.edu%3A8080%2FSimStudentServlet%2Fserv&SKILLS=&VAR1=xxx_xxx&VAL1=xxx&VAR2=xxx_xxx&VAL2=xxx&VAR3=xxx_xxx&VAL3=xxx&VAR4=xxx_xxx&VAL4=xxx&submit=Launch+HTML5+Tutor";
        var data = {
            'display_name': $(edit_display_name).context.value,
            'href':complete_url
        };

        $('.xblock-editor-error-message', element).html();
        $('.xblock-editor-error-message', element).css('display', 'none');
        var handlerUrl = runtime.handlerUrl(element, 'save_simstudent');
        $.post(handlerUrl, JSON.stringify(data)).done(function(response) {
            if (response.result === 'success') {
                window.location.reload(false);
            } else {
                $('.xblock-editor-error-message', element).html('Error: '+response.message);
                $('.xblock-editor-error-message', element).css('display', 'block');
            }
        });
    });

    $(element).find('#drive-button').bind('click', function() {
        $.getScript("https://apis.google.com/js/api.js?onload=loadPicker");
    });
    
    // The Browser API key obtained from the Google Developers Console.
    // Replace with your own Browser API key, or your own key.
    window.developerKey = 'AIzaSyCpW0K0tbhfvJmMrPw1rsFsyhH6QFJDRA8';

    // The Client ID obtained from the Google Developers Console. Replace with your own Client ID.
    window.clientId = "870410046401-p2s67o18c1npgi8ebbppqsi03237a5bn.apps.googleusercontent.com";

    // Replace with your own App ID. (Its the first number in your Client ID)
    window.appId = "870410046401";

    // Scope to use to access user's Drive items.
    window.scope = ['https://www.googleapis.com/auth/drive'];

    var pickerApiLoaded = false;
    var oauthToken;

    // Use the Google API Loader script to load the google.picker script.
    window.loadPicker = function() {
      gapi.load('auth', {'callback': onAuthApiLoad});
      gapi.load('picker', {'callback': onPickerApiLoad});
    }

    window.onAuthApiLoad = function() {
      window.gapi.auth.authorize(
          {
            'client_id': clientId,
            'scope': scope,
            'immediate': false
          },
          handleAuthResult);
    }

    window.onPickerApiLoad = function() {
      pickerApiLoaded = true;
      createPicker();
    }

    window.handleAuthResult = function(authResult) {
      if (authResult && !authResult.error) {
        oauthToken = authResult.access_token;
        createPicker();
      }
    }

    // Create and render a Picker object for searching images.
    window.createPicker = function() {
      if (pickerApiLoaded && oauthToken) {
        var view = new google.picker.View(google.picker.ViewId.DOCS);
        //view.setMimeTypes("text/plain");
        var picker = new google.picker.PickerBuilder()
            .enableFeature(google.picker.Feature.NAV_HIDDEN)
            .enableFeature(google.picker.Feature.MULTISELECT_ENABLED)
            .setAppId(appId)
            .setOAuthToken(oauthToken)
            .addView(view)
            .addView(new google.picker.DocsUploadView())
            .setDeveloperKey(developerKey)
            .setCallback(pickerCallback)
            .build();
         picker.setVisible(true);
      }
    }

    // A simple callback implementation.
    window.pickerCallback = function(data) {
      if (data.action == google.picker.Action.PICKED) {
        var fileId = data.docs[0].id;
        alert('The user selected: ' + fileId);
        $(edit_href).context.value = "https://drive.google.com/uc?export=view&amp;id="+fileId;
      }
    }
    
    $(function ($) {
        /* Here's where you'd do things on page load. */
    });
}
