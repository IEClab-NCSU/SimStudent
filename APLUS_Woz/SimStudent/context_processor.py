from django.shortcuts import get_object_or_404
from django.utils.safestring import mark_safe
import json
from chat.models import Session, TutorTuteeConversation
from django.core import serializers

from chat.models import Session


def tutor_tutee_session_info(request):
    #print("session context",request.session['room_name'])
    if 'room_name' in request.session:

        #session_id = request.session['session_id']
        session_id = request.session['room_name'].split("_")[0]
        #print("In the context pro with session id", session_id)
        room_name = request.session['room_name']
        #print("inside context", session_id)
        session = get_object_or_404(Session, pk=int(session_id))
        chat_history = TutorTuteeConversation.objects.filter(session_id=int(session_id)).order_by('comment_time')
        dict_chat_history = serializers.serialize("json", chat_history)
        print("dict chat history inside context", dict_chat_history)
        return {
            "session_info": session,
            "chat_history": mark_safe(json.dumps(dict_chat_history)),
            "room_name": room_name,
        }
    else:
        print("In the context pro without session id")
        return {
            "session_info": [],
            "chat_history": [],
            "room_name": "",
        }
