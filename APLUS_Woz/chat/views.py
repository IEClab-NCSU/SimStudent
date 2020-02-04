# Create your views here.
# chat/views.py
import csv, os
from SimStudent.settings import BASE_DIR
from django.http import HttpResponse
from django.shortcuts import render
from django.utils.safestring import mark_safe
import json
from .models import Session, ProblemBank, QuestionBank, WorkOutProblems, TutorTuteeConversation, MetaHints, ActionLogs, QuestionsUnderTypes, QuizUpdate
from django.core import serializers


def room(request, room_name):
    print(room_name)
    return render(request, 'chat/room.html', {
        'room_name_json': mark_safe(json.dumps(room_name))
    })


def sim_session_intro(request, tutor_name, tutee_name, image_name):
    image_path = "/img/all_avatars/" + image_name + ".png"
    # Check if session exist
    existing_sessions = Session.objects.filter(tutor_name=tutor_name, tutee_name=tutee_name,
                                               image_name=image_name).order_by('-pk').first()
    if existing_sessions:
        session = existing_sessions
    else:
        session = Session.objects.create(tutor_name=tutor_name, tutee_name=tutee_name, image_name=image_name)
    request.session['session_id'] = session.id
    room_name = str(session.id) + "_" + tutor_name + "_" + tutee_name
    request.session['room_name'] = room_name
    return render(request, 'IntroWilliams/mother_williams.html', {
        'room_name_json': mark_safe(json.dumps(room_name)),
        'session_id': session.id,
        'tutee': "no",
        'manager': "no",
        'tutee_name': mark_safe(json.dumps(session.tutee_name)),
        'tutor_name': mark_safe(json.dumps(session.tutor_name)),
        'image_name': image_path,
        'image_name_only': str(image_name),
    })


def sim_session(request, tutor_name, tutee_name, image_name):
    for key in request.session.keys():
        print("lava", key)
    print("tutor_sim_session", request.session['session_id'])
    image_path = "/img/all_avatars/"+image_name+".png"
    # Check if session exist
    existing_sessions = Session.objects.filter(tutor_name=tutor_name, tutee_name=tutee_name, image_name=image_name).order_by('-pk').first()
    if existing_sessions:
        session = existing_sessions
    else:
        session = Session.objects.create(tutor_name=tutor_name, tutee_name=tutee_name, image_name=image_name)
    request.session['session_id'] = session.id
    room_name = str(session.id)+"_"+tutor_name+"_"+tutee_name
    request.session['room_name'] = room_name
    latest_eq_tr_cell = WorkOutProblems.objects.filter(session_id_id=session.id).order_by('-created_at').first()
    print("eq tr cell in views", latest_eq_tr_cell)
    quiz_update = QuizUpdate.objects.filter(session_id_id=session.id).order_by('-created_at').first()
    return render(request, 'chat/room.html', {
        'room_name_json': mark_safe(json.dumps(room_name)),
        'session_id': session.id,
        'tutee': "no",
        'manager': "no",
        'tutee_name': mark_safe(json.dumps(session.tutee_name)),
        'tutor_name': mark_safe(json.dumps(session.tutor_name)),
        'image_name': image_path,
        'latest_eq_tr_cell': latest_eq_tr_cell,
        'quiz_update': quiz_update
    })


def tutee_sim_session(request, session_id, condition_name):

    all_questions = {}
    if 'aplus' in condition_name:
        aplus_id = QuestionBank.objects.get(question_type="APLUS");
        selected_type_id = aplus_id.id
    else:
        selected_type_id = request.GET.get('question_type')

    if selected_type_id is not None:
        all_questions = QuestionBank.objects.get(pk=selected_type_id).all_questions.all();
    else:
        selected_type_id = -1
    hint_tag = request.GET.get('hint_tag')

    if hint_tag is not None:
        filtered_hints = MetaHints.objects.filter(hints__icontains=hint_tag)
    else:
        filtered_hints = MetaHints.objects.all()

    session = Session.objects.get(pk=session_id)
    room_name = str(session.id)+"_"+session.tutor_name+"_"+session.tutee_name
    # Loading the value of questions
    all_question_types = QuestionBank.objects.all().order_by('tutoring_phase')
    print("data", all_question_types)
    chat_history = TutorTuteeConversation.objects.filter(session_id=int(session_id)).order_by('comment_time')
    dict_chat_history = serializers.serialize("json", chat_history)
    request.session['room_name'] = room_name
    # info of the latest equation content
    latest_eq_tr_cell = WorkOutProblems.objects.filter(session_id_id=session_id).order_by('-created_at').first()
    print("eq tr cell in views", latest_eq_tr_cell)
    quiz_update = QuizUpdate.objects.filter(session_id_id=session_id).order_by('-created_at').first()
    return render(request, 'chat/room.html', {
        'room_name_json': mark_safe(json.dumps(room_name)),
        'session_id': session.id,
        'tutee': "yes",
        'condition_name': condition_name,
        'manager': "no",
        'tutee_name': mark_safe(json.dumps(session.tutee_name)),
        'tutor_name': mark_safe(json.dumps(session.tutor_name)),
        'question_types': all_question_types,
        'all_questions_under_type': all_questions,
        'all_hints': filtered_hints,
        'selected_type_id': selected_type_id,
        'chat_history': mark_safe(json.dumps(dict_chat_history)),
        'hint_tag': hint_tag,
        'latest_eq_tr_cell': latest_eq_tr_cell,
        'quiz_update': quiz_update
    })


def decide_chatroom(request):
    sessions = Session.objects.order_by('-pk').all()[:10]
    if request.method == 'POST':
        condition_name = ""
        is_woz = request.POST.get('Woz', "false")
        is_aplus = request.POST.get('Aplus', "false")
        if "false" in is_woz and "false" in is_aplus:
            condition_name = 'woz' # default condition is woz if none is clicked.
        elif "false" in is_aplus and "false" not in is_woz:
            condition_name = 'woz'
        else:
            condition_name = 'aplus'
        session_id = [key for key in request.POST.keys()][1].split("=")[1]
        session = Session.objects.get(pk=session_id)
        #return tutee_sim_session(request, session_id)
        return render(request, 'session/enter_chatroom_2.html', {
            'session_id': session_id,
            'tutee': "yes",
            'manager': "no",
            'condition_name': condition_name,
            'tutee_name': mark_safe(json.dumps(session.tutee_name)),
            'tutor_name': mark_safe(json.dumps(session.tutor_name)),
        })
    else:
        return render(request, 'session/enter_chatroom.html', {
            'sessions': sessions,
            #'session_id': []
        })


def problem_entered_log(request):
    response = HttpResponse(content_type='text/csv')
    response['Content-Disposition'] = 'attachement; filename="report_all_problem_worked_on.csv"'

    writer = csv.writer(response)
    writer.writerow(['ID', 'SESSION_ID', 'TUTOR_NAME', 'TUTEE_NAME', 'DORMIN_FIELDS', 'DORMIN_CONTENTS', 'CREATED_AT'])

    students = WorkOutProblems.objects.all().values_list('pk', 'session_id__pk', 'session_id__tutor_name',
                                                    'session_id__tutee_name', 'dormin_fields', 'dormin_contents', 'created_at')

    # Note: we convert the students query set to a values_list as the writerow expects a list/tuple
    # students = students.values_list()

    for student in students:
        writer.writerow(student)

    return response


def chat_log(request):
    response = HttpResponse(content_type='text/csv')
    response['Content-Disposition'] = 'attachement; filename="report_all_chat_log.csv"'

    writer = csv.writer(response)
    writer.writerow(['ID', 'SESSION_ID', 'TUTOR_NAME', 'TUTEE_NAME', 'DIALOGUE_BY', 'DIALOGUE_TEXT', 'DIALOGUE_TIME'])

    students = TutorTuteeConversation.objects.all().values_list('pk', 'session_id__pk', 'session_id__tutor_name',
                                                    'session_id__tutee_name', 'comment_by', 'comment_content', 'comment_time')

    # Note: we convert the students query set to a values_list as the writerow expects a list/tuple
    # students = students.values_list()

    for student in students:
        writer.writerow(student)

    return response


def export_all_action_log(request):
    print("exporting")
    response = HttpResponse(content_type='text/csv')
    response['Content-Disposition'] = 'attachement; filename="report_all_actions.csv"'

    writer = csv.writer(response)
    writer.writerow(['ID', 'SESSION_ID','TUTOR_NAME','TUTEE_NAME', 'CF_ACTION', 'ACTION_TEXT', 'CREATED_AT', 'SELECTION_TUTOR', 'ACTION_TUTOR', 'INPUT_TUTOR', 'SELECTION_TUTEE', 'ACTION_TUTEE', 'INPUT_TUTEE', 'NEW_PROBLEM','SELECTION_META', 'ACTION_META', 'INPUT_META', 'HINT_REQUESTED', 'HINT_GIVEN', 'DIALOGUE_FROM_TUTEE', 'DIALOGUE_FROM_TUTOR', 'CURRENT_EQUATION_STATE', 'IS_TUTOR_CORRECT'])

    students = ActionLogs.objects.all().values_list('pk', 'session_id__pk','session_id__tutor_name', 'session_id__tutee_name', 'cf_action','actions_text', 'created_at', 'selection_tutor', 'action_tutor', 'input_tutor', 'selection_tutee', 'action_tutee', 'input_tutee', 'new_problem_entered', 'selection_meta', 'action_meta', 'input_meta', 'hint_requested', 'hint_given', 'dialogue_from_tutee', 'dialogue_from_tutor', 'current_equation_state', 'is_correct_step' ).order_by('-pk')

    # Note: we convert the students query set to a values_list as the writerow expects a list/tuple
    #students = students.values_list()

    for student in students:
        writer.writerow(student)

    return response


def session_manager(request):
    print("here manager")
    return render(request, 'session/session_manager_room.html', {
        'manager': "yes",
        'tutee': "no"
    })


def problem_bank(request, is_tutee):
    print(is_tutee)
    order_by = request.GET.get('order_by')
    print(order_by)
    if order_by is None:
        all_examples = ProblemBank.objects.all()
    else:
        all_examples = ProblemBank.objects.order_by(order_by)
    return render(request, 'ProblemBank/problem_examples.html', {
        'tutee': is_tutee,
        'examples': all_examples
    })


def unit_overview(request, is_tutee):

    return render(request, 'ProblemBank/curriculum.html', {
        'tutee': is_tutee,
    })


def intro_video(request, is_tutee):

    return render(request, 'ProblemBank/introVideo.html', {
        'tutee': is_tutee,
    })


def home(request):
    return render(request, 'session/home.html', {})


def log(request):
    return render(request, 'log/all_logs.html', {})


def load_problem_bank():

    file_path = os.path.join(BASE_DIR, 'problem_bank.csv')
    with open(file_path, mode='r', encoding='utf-8-sig') as f:
        reader = csv.reader(f)
        for row in reader:
            p = ProblemBank(problem=row[0], problem_type=int(row[1]), problem_rating=int(row[2]))
            p.save()


def load_question_bank():

    file_path = os.path.join(BASE_DIR, 'question_bank.csv')
    with open(file_path, mode='r', encoding="ISO-8859-1") as f:
        reader = csv.reader(f)
        flag = 1
        for row in reader:
            if flag == 1:
                # Skipping the header
                flag = 2
            else:
                tutoring_phase = -1;
                if "pre" in row[0].lower():
                    tutoring_phase = 1
                elif "during" in row[0].lower():
                    tutoring_phase = 2
                elif "post" in row[0].lower():
                    tutoring_phase = 3
                print("row APLUS", row[1])
                one_type = QuestionBank(tutoring_phase=tutoring_phase, question_type=row[1])
                one_type.save()
                all_questions = (row[2]).split(";")
                for q in all_questions:
                    q1 = q.strip()
                    if len(q1) != 0:
                        #print("after space shot ", q1)
                        question = QuestionsUnderTypes(questions=q1)
                        question.save()
                        one_type.all_questions.add(question)


def load_meta_hints():
    file_path = os.path.join(BASE_DIR, 'meta_hints_meta_cognitive.csv')
    with open(file_path, mode='r', encoding="utf-8") as f:
        reader = csv.reader(f)
        flag = 1
        for row in reader:
            if flag == 1:
                # Skipping the header
                flag = 2
            else:
                print(row[0])
                one_hint = MetaHints(hints=row[0],tags=row[1])
                one_hint.save()



#load_problem_bank()
#load_question_bank()
#load_meta_hints()

def load_data(request):
    #load_problem_bank()
    #load_question_bank()
    #load_meta_hints()
    return render(request, 'log/load_data.html', {})

