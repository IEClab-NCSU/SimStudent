from channels.generic.websocket import WebsocketConsumer
import json
import numpy as np
from asgiref.sync import async_to_sync
from .models import Session, TutorMetaTutorConversation, TutorTuteeConversation, WorkOutProblems, ActionLogs, QuestionBank, QuizUpdate


class ChatConsumer(WebsocketConsumer):

    def fetch_messages(self, data):
        #print('fetch', data['session'])
        messages = TutorTuteeConversation.last_10_messages()
        content = {
            'messages': self.messages_to_json(messages)
        }
        self.send_message(content)
        pass

    def new_messages(self, data):
        if "NB:TYPING" in data['message']:
            session = Session.objects.get(pk=int(data['session']))
            if "yes" in data['is_tutee']:
                typing = session.tutee_name
            else:
                typing = session.tutor_name
            content = {
                'command': 'new_messages',
                'message': "NB:TYPING",
                'session': data['session'],
                'typing': "yes",
                'whose_typing': typing,
                'is_tutee_msg': data['is_tutee']
            }
            return self.send_chat_message(content)
        else:
            print('new_messgae', data['is_tutee'], data['message'])
            # CF_ACTION: "data['is_tutee'] entered chat dialogue"
            current_equation_state = WorkOutProblems.objects.filter(session_id_id=int(data['session'])).order_by(
                '-pk').first()
            if current_equation_state is not None:
                current_eq_state = current_equation_state.dormin_contents
            else:
                current_eq_state = "[]"
            if data['is_tutee'] in "no":
                message_from = "Tutor"
            else:
                message_from = "Tutee"
            cf_action = message_from + " has entered chat dialogue "
            if "Tutee" in message_from:
                log = ActionLogs(session_id_id=int(data['session']),
                             actions_text= data['message'], cf_action=cf_action, selection_tutee="[chat dialogue]", dialogue_from_tutee=data['message'], current_equation_state=current_eq_state)
            else:
                log = ActionLogs(session_id_id=int(data['session']),
                                 actions_text=data['message'], cf_action=cf_action, selection_tutor="[chat dialogue]", dialogue_from_tutor=data['message'], current_equation_state=current_eq_state)
            log.save()
            session = Session.objects.get(pk=data['session'])
            if data['meta'] in "no":

                if data['is_tutee'] in "no":
                    comment_by = session.tutor_name
                else:
                    comment_by = session.tutee_name
                message = TutorTuteeConversation.objects.create(session_id=session, comment_content=data['message'],comment_by=comment_by)
            else:

                if data['is_tutee'] in "no":
                    comment_by = session.tutor_name
                else:
                    comment_by = "Mr.Williams"
                message = TutorMetaTutorConversation.objects.create(session_id=session, comment_content=data['message'],comment_by=comment_by)

            content = {
                'command': 'new_messages',
                'message': self.message_to_json(message),
                'session': self.session_to_json(session),
                'typing': "no"
            }
            return self.send_chat_message(content)

    commands = {
        'fetch_messages': fetch_messages,
        'new_messages': new_messages
    }

    def messages_to_json(self, messages):
        result = []
        for message in messages:
            result.append(self.message_to_json(message))
        return result

    def message_to_json(self, message):
        return {
            'id': message.id,
            'comment_by': message.comment_by,
            'comment_content': message.comment_content,
            'comment_time': str(message.comment_time)
        }

    def session_to_json(self, message):
        return {
            'id': message.id,
            'tutor_name': message.tutor_name,
            'tutee_name': message.tutee_name,
        }

    def connect(self):
        self.room_name = self.scope['url_route']['kwargs']['room_name']
        self.room_group_name = 'chat_%s' % self.room_name
        async_to_sync(self.channel_layer.group_add)(
            self.room_group_name,
            self.channel_name
        )
        self.accept()

    def disconnect(self, close_code):
        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_name,
            self.channel_name
        )

    def receive(self, text_data):
        data = json.loads(text_data)
        self.commands[data['command']](self, data)

    def send_chat_message(self, message):
        async_to_sync(self.channel_layer.group_send)(
            self.room_group_name,
            {
                'type': 'chat_message',
                'message': message
            }
        )

    def send_message(self, message):
        self.send(text_data=json.dumps(message))

    def chat_message(self, event):
        message = event['message']
        self.send(text_data=json.dumps(message))


class HighlightConsumer(WebsocketConsumer):

    def new_highlights(self, data):
        print('new_highlights', data['is_tutee'], data['message'])
        current_equation_state = WorkOutProblems.objects.filter(session_id_id=int(data['session'])).order_by(
            '-pk').first()
        if current_equation_state is not None:
            current_eq_state = current_equation_state.dormin_contents
        else:
            current_eq_state = "[]"
        # I am enabling cells is data['message']
        cf_action = "Tutee enabled cells for tutor input"
        log = ActionLogs(session_id_id=int(data['session']),
                         actions_text=data['message'], cf_action=cf_action, selection_tutee=data['message'], action_tutee=cf_action, input_tutee="selected box checked", current_equation_state=current_eq_state)
        log.save()
        content = {
            'command': 'new_highlights',
            'message': {
                'type': 'new_highlights',
                'message_content': data['message']
            }
        }
        return self.send_highlight_message(content)

    def enable_unlock(self, data):
        print('enable_unlock', data['is_tutee'], data['message'])
        current_equation_state = WorkOutProblems.objects.filter(session_id_id=int(data['session'])).order_by(
            '-pk').first()
        if current_equation_state is not None:
            current_eq_state = current_equation_state.dormin_contents
        else:
            current_eq_state = "[]"
        # I am enabling cells is data['message']
        cf_action = "Tutor enabled Unlock button for tutee"
        log = ActionLogs(session_id_id=int(data['session']),
                         actions_text=data['message'], cf_action=cf_action, selection_tutee=data['message'],
                         action_tutee=cf_action, input_tutee="selected box checked",
                         current_equation_state=current_eq_state)
        log.save()
        content = {
            'command': 'enable_unlock',
            'message': {
                'type': 'enable_unlock',
                'message_content': data['message']
            }
        }
        return self.send_highlight_message(content)

    def new_eqTrContent(self, data):
        content = {
            'command': 'new_eqTrContent',
            'message': {
                'type': 'new_eqTrContent',
                'message_content': data['message']
            }

        }
        return self.send_highlight_message(content)

    def new_quizContent(self, data):
        print('new_quizContent', data['is_tutee'], data['message'])
        # I am updating tutee's quiz progress and the current quiz progress is in data['message']
        current_equation_state = WorkOutProblems.objects.filter(session_id_id=int(data['session'])).order_by(
            '-pk').first()
        if current_equation_state is not None:
            current_eq_state = current_equation_state.dormin_contents
        else:
            current_eq_state = "[]"

        quiz_update = QuizUpdate(session_id_id=int(data['session']), update_contents= data['message'])
        quiz_update.save()
        cf_action = "Tutee's QUIZ panel updated"
        log = ActionLogs(session_id_id=int(data['session']),
                         actions_text=data['message'], cf_action=cf_action, selection_tutee="['one-quiz-input', 'two-quiz-input', 'var-quiz-input', 'overall-quiz-input']", action_tutee=cf_action, input_tutee=data['message'], current_equation_state=current_eq_state)
        log.save()
        content = {
            'command': 'new_quizContent',
            'message': {
                'type': 'new_quizContent',
                'message_content': data['message']
            }

        }
        return self.send_highlight_message(content)

    def new_HintContent(self, data):
        print('new_HintContent', data['is_tutee'], data['message'])
        # I am searching for hints using hint tags. hint tag can be found in data['message']
        current_equation_state = WorkOutProblems.objects.filter(session_id_id=int(data['session'])).order_by(
            '-pk').first()
        if current_equation_state is not None:
            current_eq_state = current_equation_state.dormin_contents
        else:
            current_eq_state = "[]"
        cf_action = "Meta Tutor is searching for HINT"
        log = ActionLogs(session_id_id=int(data['session']),
                         actions_text=data['message'], cf_action=cf_action, selection_meta="['search-hint-tag']", action_meta=cf_action, input_meta=data['message'], current_equation_state=current_eq_state)
        log.save()
        content = {
            'command': 'new_HintContent',
            'message': {
                'type': 'new_HintContent',
                'message_content': data['message']
            }

        }
        return self.send_highlight_message(content)

    def new_freeze(self, data):
        content = {
            'command': 'new_freeze',
            'message': {
                'type': 'new_freeze',
                'message_content': data['message']
            }

        }
        return self.send_highlight_message(content)

    commands = {
        'new_highlights': new_highlights,
        'enable_unlock': enable_unlock,
        'new_eqTrContent': new_eqTrContent,
        'new_quizContent': new_quizContent,
        'new_HintContent': new_HintContent,
        'new_freeze': new_freeze,
    }

    def connect(self):
        self.room_name = self.scope['url_route']['kwargs']['room_name']
        self.room_group_name = 'highlight_%s' % self.room_name
        async_to_sync(self.channel_layer.group_add)(
            self.room_group_name,
            self.channel_name
        )
        self.accept()

    def disconnect(self, close_code):
        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_name,
            self.channel_name
        )

    def receive(self, text_data):
        data = json.loads(text_data)
        self.commands[data['command']](self, data)

    def send_highlight_message(self, message):
        async_to_sync(self.channel_layer.group_send)(
            self.room_group_name,
            {
                'type': 'highlight_message',
                'message': message,
            }
        )

    def send_message(self, message):
        self.send(text_data=json.dumps(message))

    def highlight_message(self, event):
        message = event['message']
        self.send(text_data=json.dumps(message))


class AllActionsConsumer(WebsocketConsumer):

    def all_actions(self, data):
        print('all_actions', data['is_tutee'], data['message'])
        current_equation_state = WorkOutProblems.objects.filter(session_id_id=int(data['session'])).order_by('-pk').first()
        if current_equation_state is not None:
            current_eq_state = current_equation_state.dormin_contents
        else:
            current_eq_state = "[]"
        # This part has all the tutor clicked messages in data['message'] but if I am sending some hint to tutor then data['message'] contains Hint%% <<hints>>
        if "yes" in data['hint_request']:
            log = ActionLogs(session_id_id=int(data['session']), actions_text=data['message'],
                             cf_action=data['message'], current_equation_state=current_eq_state,
                             hint_requested=data['message'], selection_tutor=data['message'], action_tutor=data['message'], input_tutor=data['message'])
            log.save()
            content = {
                'command': 'all_actions',
                'message': {
                    'type': 'all_actions',
                    'message_content': data['message']
                }
            }
            return self.send_all_actions_message(content)
        if "yes" in data['correctness_update']:
            grade_result = ""
            if "0" in data['message']:
                grade_result = "Tutor's step is Incorrect"
            else:
                grade_result = "Tutor's step is Correct"
            log = ActionLogs(session_id_id=int(data['session']), actions_text=grade_result,
                             cf_action="Step stated by tutor graded", current_equation_state=current_eq_state, is_correct_step=data['message'])
            log.save()
            content = {
                'command': 'all_actions',
                'message': {
                    'type': 'all_actions',
                    'message_content': data['message']
                }
            }
            return self.send_all_actions_message(content)
        elif "Hint%%" in data['message']:
            log = ActionLogs(session_id_id=int(data['session']), actions_text=data['message'],
                             cf_action=data['message'], current_equation_state=current_eq_state,hint_given=data['message'])
            log.save()
            content = {
                'command': 'all_actions',
                'message': {
                    'type': 'all_actions',
                    'message_content': data['message']
                }
            }
            return self.send_all_actions_message(content)
        elif "NB:LOG_ONLY_PROBLEMS" in data['message']:
            # CF_ACTION: "Problem Entered by tutor"
            print("inside problem log", data['content_eq_tr'])
            log = WorkOutProblems(session_id_id=int(data['session']), dormin_fields=data['eq_tr_id'], dormin_contents=data['content_eq_tr'])
            log.save()
            new_problem = np.array(data['content_eq_tr'])
            all_id = np.array(data['eq_tr_id'])
            pos = int(data['edited_eq_tr_id_pos'])
            selection = all_id[pos]
            input = new_problem[pos]
            action = "Tutor entered value in cell " + selection
            print("position of problem entered", pos)
            if pos == 0 or pos == 1:
                #new_problem = np.array(data['content_eq_tr'])
                problem_entered = new_problem[0]+"="+new_problem[1]
                log = ActionLogs(session_id_id=int(data['session']), actions_text=data['content_eq_tr'],
                                 cf_action="Problem Entered by tutor", new_problem_entered=problem_entered, selection_tutor=selection, action_tutor=action, input_tutor=input, current_equation_state=data['content_eq_tr'])
                log.save()
            else:
                log = ActionLogs(session_id_id=int(data['session']), actions_text=data['content_eq_tr'], cf_action="Problem Entered by tutor", selection_tutor=selection, action_tutor=action, input_tutor=input, current_equation_state=data['content_eq_tr'])
                log.save()
        elif "NB:LOG_ONLY_QTYPE" in data['message']:
            # CF_ACTION: "Tutee selected question type"
            qtype = data['qtype']
            text_qtype = QuestionBank.objects.get(pk=int(qtype))
            log = ActionLogs(session_id_id=int(data['session']),
                             actions_text=text_qtype.question_type, cf_action="Tutee has decided to ask question type", selection_tutee="[ques type dropdown]", action_tutee="Tutee has decided to ask question type", input_tutee=text_qtype.question_type, current_equation_state=current_eq_state)
            log.save()
        elif "NB:LOG_ONLY_QUES" in data['message']:
            # CF_ACTION: "Tutee selected specific question"
            ques = data['ques']
            log = ActionLogs(session_id_id=int(data['session']),
                             actions_text=ques, cf_action= "Tutee has decided to ask specific question from a selected type", selection_tutee="[specific question dropdown]", action_tutee="Tutee has decided to ask specific question from a selected type", input_tutee=ques, current_equation_state=current_eq_state)
            log.save()
        else:
            log = ActionLogs(session_id_id=int(data['session']), actions_text=data['message'], cf_action=data['message'], current_equation_state=current_eq_state, action_tutor=data['message'], input_tutor=data['message'], selection_tutor=data['message'])
            log.save()
            content = {
                'command': 'all_actions',
                'message': {
                    'type': 'all_actions',
                    'message_content': data['message']
                }
            }
            return self.send_all_actions_message(content)

    commands = {
        'all_actions': all_actions,
    }

    def connect(self):
        self.room_name = self.scope['url_route']['kwargs']['room_name']
        self.room_group_name = 'all_actions_%s' % self.room_name
        async_to_sync(self.channel_layer.group_add)(
            self.room_group_name,
            self.channel_name
        )
        self.accept()

    def disconnect(self, close_code):
        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_name,
            self.channel_name
        )

    def receive(self, text_data):
        data = json.loads(text_data)
        self.commands[data['command']](self, data)

    def send_all_actions_message(self, message):
        async_to_sync(self.channel_layer.group_send)(
            self.room_group_name,
            {
                'type': 'all_actions_message',
                'message': message,
            }
        )

    def send_message(self, message):
        self.send(text_data=json.dumps(message))

    def all_actions_message(self, event):
        message = event['message']
        self.send(text_data=json.dumps(message))
