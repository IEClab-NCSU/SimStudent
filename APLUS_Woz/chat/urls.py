from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'tutoring/(?P<tutor_name>[^/]+)/(?P<tutee_name>[^/]+)/(?P<image_name>[\d]+)/$', views.sim_session, name='room'),
    url(r'(?P<tutor_name>[^/]+)/(?P<tutee_name>[^/]+)/(?P<image_name>[\d]+)/$', views.sim_session_intro, name='room'),
    url(r'(?P<session_id>[\d]+)/$', views.tutee_sim_session, name='tutee_room'),
    url(r'(?P<room_name>[^/]+)/$', views.room, name='room'),
    #url(r'^$', views.index, name='index'),
]