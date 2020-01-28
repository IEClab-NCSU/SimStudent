from django.conf.urls import url

from . import consumers

websocket_urlpatterns = [
    url(r'^ws/chat/(?P<room_name>[^/]+)/$', consumers.ChatConsumer),
    url(r'^ws/lock_unlock/(?P<room_name>[^/]+)/$', consumers.HighlightConsumer),
    url(r'^ws/all_actions/(?P<room_name>[^/]+)/$', consumers.AllActionsConsumer),
]