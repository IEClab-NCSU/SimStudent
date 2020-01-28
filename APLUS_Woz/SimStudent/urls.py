"""SimStudent URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from django.conf.urls import include, url
from chat import views
import csv, os
from chat.models import ProblemBank, QuestionBank, QuestionsUnderTypes, MetaHints
from SimStudent.settings import BASE_DIR

urlpatterns = [
    url(r'^chat/', include('chat.urls')),
    url(r'^decide_chatroom/', views.decide_chatroom, name='decide_room'),
    url(r'^tutee_login/', views.decide_chatroom, name='decide_room'),
    url(r'^sim_manager/', views.session_manager, name='session_manager'),
    url(r'^problem_bank/(?P<is_tutee>[^/]+)/$', views.problem_bank, name='problem_bank'),
    url(r'^unit_overview/(?P<is_tutee>[^/]+)/$', views.unit_overview, name='unit_overview'),
    url(r'^intro_video/(?P<is_tutee>[^/]+)/$', views.intro_video, name='intro_video'),
    # url(r'^problem_bank/', ExampleListView.as_view()), ## django-tables2
    url(r'^sim_session/', include('chat.urls')),
    url(r'^export/all_action_logs/$', views.export_all_action_log, name='export_data'),
    url(r'^export/chat_logs/$', views.chat_log, name='export_data'),
    url(r'^export/problem_entered_logs/$', views.problem_entered_log, name='export_data'),
    url(r'^export/logs/$', views.log, name='export_data'),
    url(r'^load_pre_data/$', views.load_data, name='load_data'),
    path('admin/', admin.site.urls),
    url(r'^$', views.home, name='home'),

]


