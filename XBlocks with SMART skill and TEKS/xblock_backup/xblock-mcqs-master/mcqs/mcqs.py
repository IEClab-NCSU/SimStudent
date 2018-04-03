""" An XBlock for Multiple Choice Questions """

import pkg_resources

from django.template import Template, Context
from xblock.core import XBlock
from xblock.fields import Scope, Integer, String, List, Boolean
from xblock.fragment import Fragment
from xblockutils.studio_editable import StudioEditableXBlockMixin
from xblock_utils import *
import pytz



# from django.db import connection



class McqsXBlock(XBlock, StudioEditableXBlockMixin):
    """
    Multiple Choice Question XBlock
    """
    display_name = String(default='Multiple Choice Question', scope=Scope.content)
    block_name = String(default='Multiple Choice Question')
    # editable_fields = ('title', 'question', 'choices', 'correct_choice', 'hint')

    title = String(
        display_name='Problem title:',
        default='Enter a problem title here',
        scope=Scope.content, help='Problem name'
    )

    problemId = String(
        display_name='default problem id',
        default='Enter a Problem Name here',
        scope=Scope.content, help='Problem Name for DataShop'
    )

    question = String(
        display_name='Question:',
        default='Enter a question here',
        scope=Scope.content, help='Question statement'
    )
    choices = List(
        display_name='Choices',
        default=['Choice_1', 'Choice_2', 'Choice_3'],
        scope=Scope.content, help='Choices for Multiple Choice Question'
    )
    correct_choice = Integer(
        display_name='Correct Choice',
        default=1, scope=Scope.content,
        help='Index of correct choice among given choices. For example if third choice is correct, enter 3'
    )
    hint = String(
        display_name='Hint',
        default='Try hard!|Think again!|The last hint message is the answer for this question.',
        scope=Scope.content,
        help='Hint for the User'
    )
    kc = String(
        display_name='KC ()',
        default='Enter a Skill Name here',
        scope=Scope.content
    )
    image_url = String(
        default="image_url",
        scope=Scope.content
    )
    image_size = String(default="50%", scope=Scope.content)
    iso = Boolean(default=False, scope=Scope.content)
    condition = String(default="", scope=Scope.user_state)
    school = String(default="", scope=Scope.user_state)
    className = String(default="", scope=Scope.user_state)
    dynamicClicked = Boolean(default=False, scope=Scope.user_state)
    user_choice = Integer(scope=Scope.user_state, help='Index of choice selected by User')
    correct = Boolean(default=False, scope=Scope.user_state, help='User selection is correct or not')
    row1 = String(scope=Scope.user_state)
    session_id = String(default="", scope=Scope.user_info)
    # This 'attempt' attribute is for 'attempts' column
    attempts = Integer(default=1, scope=Scope.user_state)
    hint_numbers = Integer(default=0, scope=Scope.user_state)
    # When student refresh the page, countTimes will increase one
    count_times = Integer(default=0, scope=Scope.user_state)
    module_id = Integer(default=0, scope=Scope.user_state)
    xblock_id = String(default="", scope=Scope.user_state)
    student_id = Integer(default=0, scope=Scope.user_state)
    probability = Integer(default=0, scope=Scope.user_state)
    pastel_student_id = String(scope=Scope.user_state)
    hasBeenSent = String(default="false", scope=Scope.user_state)
    setBorderColor = Integer(default=0, scope=Scope.content,
                             help='Help studio view to see if there is any skillname missing')
    hintClicked = Boolean(default=False, scope=Scope.user_state)

    def resource_string(self, path):
        """
        Handy helper for getting resources from our kit.
        """
        data = pkg_resources.resource_string(__name__, path)
        return data.decode("utf8")

    def student_view(self, context=None):
        """ 
        The primary view of the McqsXBlock, shown to students
        when viewing courses.
        """

        if context is None:
            context = {}

        context.update({'self': self})

        html = Template(self.resource_string("static/html/mcqs.html")).render(Context(context))
        frag = Fragment(html)
        frag.add_css(self.resource_string("static/css/mcqs.css"))
        frag.add_javascript(self.resource_string("static/js/src/mcqs.js"))
        frag.initialize_js('McqsXBlockInitView')

        return frag

    def studio_view(self, context=None):
        """
        The primary view of the McqsXBlock, shown to students
        when viewing courses.
        """

        if context is None:
            context = {}

        context.update({'self': self})

        html = Template(self.resource_string("static/html/mcqs_edit.html")).render(Context(context))
        frag = Fragment(html)
        frag.add_javascript(self.resource_string("static/js/src/mcqs_edit.js"))
        frag.initialize_js('McqsXBlockInitStudio')

        return frag

    @XBlock.json_handler
    def get_status_when_refresh(self, data, suffix=''):
        """
        After refreshing the page, we are able to tell the student's previous answer
        """
        return {"countTimes": self.count_times, "correctness": self.correct, "userChoice": self.user_choice, "dynamicClicked": self.dynamicClicked}

    @XBlock.json_handler
    def set_status_when_refresh(self, data, suffix=''):
        """
         After student selecting the answer, this method trigger
        """
        self.count_times += 1
        self.user_choice = int(data.get('userChoice', 0))
        self.hasBeenSent = str(data.get('hasBeenSent'))

    @XBlock.json_handler
    def refresh_session(self, data, suffix=''):
        self.session_id = util_generate_session_id(str(self.runtime.user_id))

    # Get anonymous student id
    @XBlock.json_handler
    def get_student_id(self, data, suffix=''):
        """
         anonymous_student_id format: 5c58c732aba1b9e2e708a00c3b243de7
         1. base on the anony student id, we find the user_id attribute from edxapp.student_anonymoususerid
        """

        self.xblock_id = str(unicode(self.scope_ids.usage_id))
        """
        student_id format: 5
        """
        self.student_id = int(self.runtime.user_id)

        hintCount = int(data.get('hintCount'))
        clicktype = str(data.get('type'))
        pageId = str(data.get('page_id'))
        section = str(data.get('section'))
        subsection = str(data.get('subsection'))
        unit = str(data.get('unit'))

        if clicktype == "isomcqsQuestionClick":
            construct_obj = data.get("construct_obj")
            print "skillname is " + construct_obj['kc']
            print "all iso data from frontend: " + str(data.get('construct_obj'))
            print "hintCount is " + str(hintCount)
            self.insert_user_activities(0, hintCount, clicktype, pageId, construct_obj)
        else:
            self.insert_user_activities(0, hintCount, clicktype, pageId, 0, section, subsection, unit)

        return {'user_id': self.runtime.anonymous_student_id, 'xblock_id': self.xblock_id}

    # This method help us to store the user activities in OpenEdx database:
    def insert_user_activities(self, student_module_id, hintCount, clicktype, pageId, construct_obj, section, subsection, unit):
        # first, collect all the information


        ts = time.time()
        timestamp = datetime.datetime.fromtimestamp(ts, pytz.timezone('US/Central')).strftime('%Y-%m-%d %H:%M:%S')

        outcome = "CORRECT" if str(self.correct) == "True" else "INCORRECT"

        problemId = self.problemId

        question = self.question

        choices = '| '.join(self.choices)

        userChoice = str(self.user_choice)

        skillName = self.kc

        feedback = self.hint.split('|')[hintCount]

        if self.user_choice is None:
            print "self.user_choice is None!!!"
            return

        pastel_id = "None" if self.pastel_student_id is None else str(self.pastel_student_id)

        if self.session_id == '' or self.session_id is None:
            self.session_id = util_generate_session_id(str(self.runtime.user_id))

        if clicktype == 'checkbutton':

            question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                               '"session id":"' + self.session_id + '", ' \
                               '"time zone":"US/Central", ' \
                               '"student response type":"ATTEMPT", ' \
                               '"student response subtype":"N/A", ' \
                               '"tutor response type":"RESULT", ' \
                               '"tutor response subtype":"N/A", ' \
                               '"level":"N/A",' \
                               '"problem name":"' + problemId + '", ' \
                               '"problem view":"' + str(self.attempts) + '", ' \
                               '"step name":"N/A", ' \
                               '"attempt at step":"' + str(self.attempts) + '",' \
                               '"outcome":"'+ outcome + '", ' \
                               '"selection":"mcqs_radio_' + userChoice + '", ' \
                               '"action":"button clicked", ' \
                               '"input":"' + userChoice + '",' \
                               '"feedback text":"N/A", ' \
                               '"feedback classification":"N/A", ' \
                               '"help level":"", ' \
                               '"total number hints":"' + str(len(self.hint.split('|'))) + '", ' \
                               '"condition name":"' + self.condition + '", ' \
                               '"condition type":"N/A", ' \
                               '"kc":"' + skillName + '", ' \
                               '"kc category":"N/A", ' \
                               '"school":"' + self.school + '", ' \
                               '"class":"' + self.className + '", ' \
                               '"cf_course":"' + str(self.scope_ids.usage_id.course_key) + '", ' \
                               '"cf_section":"' + section + '", ' \
                               '"cf_subsection":"' + subsection + '", ' \
                               '"cf_unit":"' + unit + '", ' \
                               '"cf_user_runtime_id":"' + str(self.runtime.user_id) + '", ' \
                               '"cf_student_pastel_id":"' + pastel_id + '",' \
                               '"cf_question": "' + question + '", ' \
                               '"cf_choices": "' + choices + '", ' \
                               '"cf_video_url":"N/A", ' \
                               '"cf_video_position":"N/A", ' \
                               '"cf_page_id":"' + pageId + '", ' \
                               '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                               '"cf_action":"multiple choice get answer",' \
                               '"cf_result":"N/A" }}'

        elif clicktype == 'isomcqsQuestionClick':

            question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                               '"session id":"' + self.session_id + '", ' \
                               '"time zone":"US/Central", ' \
                               '"student response type":"ATTEMPT", ' \
                               '"student response subtype":"N/A", ' \
                               '"tutor response type":"RESULT", ' \
                               '"tutor response subtype":"N/A", ' \
                               '"level":"N/A",' \
                               '"problem name":"' + str(construct_obj['problem_id'].encode("utf8")) + '", ' \
                               '"problem view":"' + str(construct_obj['attempt'].encode("utf8")) + '", ' \
                               '"step name":"N/A", ' \
                               '"attempt at step":"' + str(construct_obj['attempt'].encode("utf8")) + '",' \
                               '"outcome":"'+ outcome + '", ' \
                               '"selection":"mcqs_radio_' + userChoice + '", ' \
                               '"action":"button clicked", ' \
                               '"input":"' + userChoice + '",' \
                               '"feedback text":"N/A", ' \
                               '"feedback classification":"N/A", ' \
                               '"help level":"' + str(hintCount % len(self.hint.split('|')) + 1) + '", ' \
                               '"total number hints":"' + str(len(self.hint.split('|'))) + '", ' \
                               '"condition name":"' + str(construct_obj['condition'].encode("utf8")) + '", ' \
                               '"condition type":"N/A", ' \
                               '"kc":"' + str(construct_obj['kc'].encode("utf8")) + '", ' \
                               '"kc category":"N/A", ' \
                               '"school":"' + self.school + '", ' \
                               '"class":"' + self.className + '", ' \
                               '"cf_course":"' + str(self.scope_ids.usage_id.course_key) + '", ' \
                               '"cf_section":"' + section + '", ' \
                               '"cf_subsection":"' + subsection + '", ' \
                               '"cf_unit":"' + unit + '", ' \
                               '"cf_user_runtime_id":"' + str(self.runtime.user_id) + '", ' \
                               '"cf_student_pastel_id":"' + pastel_id + '",' \
                               '"cf_question": "' + str(construct_obj['cf_question'].encode("utf8")) + '", ' \
                               '"cf_choices": "' + str(construct_obj['cf_choices'].encode("utf8")) + '", ' \
                               '"cf_video_url":"N/A", ' \
                               '"cf_video_position":"N/A", ' \
                               '"cf_page_id":"' + pageId + '", ' \
                               '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                               '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                               '"cf_action":"multiple choice(isomorphic) get answer",' \
                               '"cf_result":"N/A" }}'

        elif clicktype == 'pageLoaded':

            question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                                   '"session id":"' + self.session_id + '", ' \
                                   '"time zone":"US/Central", ' \
                                   '"student response type":"ATTEMPT", ' \
                                   '"student response subtype":"N/A", ' \
                                   '"tutor response type":"RESULT", ' \
                                   '"tutor response subtype":"N/A", ' \
                                   '"level":"N/A",' \
                                   '"problem name":"N/A", ' \
                                   '"problem view":"0", ' \
                                   '"step name":"N/A", ' \
                                   '"attempt at step":"N/A",' \
                                   '"outcome":"UNGRADED", ' \
                                   '"selection":"N/A", ' \
                                   '"action":"N/A", ' \
                                   '"input":"N/A",' \
                                   '"feedback text":"N/A", ' \
                                   '"feedback classification":"N/A", ' \
                                   '"help level":"", ' \
                                   '"total number hints":"", ' \
                                   '"condition name":"' + self.condition + '", ' \
                                   '"condition type":"N/A", ' \
                                   '"kc":"N/A", ' \
                                   '"kc category":"N/A", ' \
                                   '"school":"' + self.school + '", ' \
                                   '"class":"' + self.className + '", ' \
                                   '"cf_course":"' + str(self.scope_ids.usage_id.course_key) + '", ' \
                                   '"cf_section":"' + section + '", ' \
                                   '"cf_subsection":"' + subsection + '", ' \
                                   '"cf_unit":"' + unit + '", ' \
                                   '"cf_user_runtime_id":"' + str(self.runtime.user_id) + '", ' \
                                   '"cf_student_pastel_id":"' + pastel_id + '",' \
                                   '"cf_question": "N/A", ' \
                                   '"cf_choices": "N/A", ' \
                                   '"cf_video_url":"N/A", ' \
                                   '"cf_video_position":"N/A", ' \
                                   '"cf_page_id":"' + pageId + '", ' \
                                   '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                                   '"cf_action":"page loaded",' \
                                   '"cf_result":"N/A" }}'

        elif clicktype == 'unitFilled':
            question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                                   '"session id":"' + self.session_id + '", ' \
                                   '"time zone":"US/Central", ' \
                                   '"student response type":"ATTEMPT", ' \
                                   '"student response subtype":"N/A", ' \
                                   '"tutor response type":"RESULT", ' \
                                   '"tutor response subtype":"N/A", ' \
                                   '"level":"N/A",' \
                                   '"problem name":"N/A", ' \
                                   '"problem view":"0", ' \
                                   '"step name":"N/A", ' \
                                   '"attempt at step":"N/A",' \
                                   '"outcome":"UNGRADED", ' \
                                   '"selection":"N/A", ' \
                                   '"action":"N/A", ' \
                                   '"input":"N/A",' \
                                   '"feedback text":"N/A", ' \
                                   '"feedback classification":"N/A", ' \
                                   '"help level":"", ' \
                                   '"total number hints":"", ' \
                                   '"condition name":"' + self.condition + '", ' \
                                   '"condition type":"N/A", ' \
                                   '"kc":"N/A", ' \
                                   '"kc category":"N/A", ' \
                                   '"school":"' + self.school + '", ' \
                                   '"class":"' + self.className + '", ' \
                                   '"cf_course":"' + str(self.scope_ids.usage_id.course_key) + '", ' \
                                   '"cf_section":"' + section + '", ' \
                                   '"cf_subsection":"' + subsection + '", ' \
                                   '"cf_unit":"' + unit + '", ' \
                                   '"cf_user_runtime_id":"' + str(self.runtime.user_id) + '", ' \
                                   '"cf_student_pastel_id":"' + pastel_id + '",' \
                                   '"cf_question": "N/A", ' \
                                   '"cf_choices": "N/A", ' \
                                   '"cf_video_url":"N/A", ' \
                                   '"cf_video_position":"N/A", ' \
                                   '"cf_page_id":"' + pageId + '", ' \
                                   '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                                   '"cf_action":"unit filled",' \
                                   '"cf_result":"N/A" }}'

        elif clicktype == 'dynamicLinkClicked':
            self.dynamicClicked = True
            question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                                   '"session id":"' + self.session_id + '",' \
                                   '"time zone":"US/Central", ' \
                                   '"student response type":"ATTEMPT", ' \
                                   '"student response subtype":"N/A", ' \
                                   '"tutor response type":"RESULT", ' \
                                   '"tutor response subtype":"N/A", ' \
                                   '"level":"N/A",' \
                                   '"problem name":"N/A", ' \
                                   '"problem view":"0", ' \
                                   '"step name":"N/A", ' \
                                   '"attempt at step":"N/A",' \
                                   '"outcome":"UNGRADED", ' \
                                   '"selection":"N/A", ' \
                                   '"action":"N/A", ' \
                                   '"input":"N/A",' \
                                   '"feedback text":"N/A", ' \
                                   '"feedback classification":"N/A", ' \
                                   '"help level":"", ' \
                                   '"total number hints":"", ' \
                                   '"condition name":"' + self.condition + '", ' \
                                   '"condition type":"N/A", ' \
                                   '"kc":"N/A", ' \
                                   '"kc category":"N/A", ' \
                                   '"school":"' + self.school + '", ' \
                                   '"class":"' + self.className + '", ' \
                                   '"cf_course":"' + str(self.scope_ids.usage_id.course_key) + '", ' \
                                   '"cf_section":"' + section + '", ' \
                                   '"cf_subsection":"' + subsection + '", ' \
                                   '"cf_unit":"' + unit + '", ' \
                                   '"cf_user_runtime_id":"' + str(self.runtime.user_id) + '", ' \
                                   '"cf_student_pastel_id":"' + pastel_id + '",' \
                                   '"cf_question": "N/A", ' \
                                   '"cf_choices": "N/A", ' \
                                   '"cf_video_url":"N/A", ' \
                                   '"cf_video_position":"N/A", ' \
                                   '"cf_page_id":"' + pageId + '", ' \
                                   '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                                   '"cf_action":"dynamic link clicked",' \
                                   '"cf_result":"' + skillName + '" }}'

        elif clicktype == 'menuLinkClicked':
            question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                                   '"session id":"' + self.session_id + '", ' \
                                   '"time zone":"US/Central", ' \
                                   '"student response type":"ATTEMPT", ' \
                                   '"student response subtype":"N/A", ' \
                                   '"tutor response type":"RESULT", ' \
                                   '"tutor response subtype":"N/A", ' \
                                   '"level":"N/A",' \
                                   '"problem name":"N/A", ' \
                                   '"problem view":"0", ' \
                                   '"step name":"N/A", ' \
                                   '"attempt at step":"N/A",' \
                                   '"outcome":"UNGRADED", ' \
                                   '"selection":"N/A", ' \
                                   '"action":"N/A", ' \
                                   '"input":"N/A",' \
                                   '"feedback text":"N/A", ' \
                                   '"feedback classification":"N/A", ' \
                                   '"help level":"", ' \
                                   '"total number hints":"", ' \
                                   '"condition name":"' + self.condition + '", ' \
                                   '"condition type":"N/A", ' \
                                   '"kc":"N/A", ' \
                                   '"kc category":"N/A", ' \
                                   '"school":"' + self.school + '", ' \
                                   '"class":"' + self.className + '", ' \
                                   '"cf_course":"' + str(self.scope_ids.usage_id.course_key) + '", ' \
                                   '"cf_section":"' + section + '", ' \
                                   '"cf_subsection":"' + subsection + '", ' \
                                   '"cf_unit":"' + unit + '", ' \
                                   '"cf_user_runtime_id":"' + str(self.runtime.user_id) + '", ' \
                                   '"cf_student_pastel_id":"' + pastel_id + '",' \
                                   '"cf_question": "N/A", ' \
                                   '"cf_choices": "N/A", ' \
                                   '"cf_video_url":"N/A", ' \
                                   '"cf_video_position":"N/A", ' \
                                   '"cf_page_id":"' + pageId.split("+")[0] + '", ' \
                                   '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                                   '"cf_action":"menu link clicked",' \
                                   '"cf_result":"' + pageId.split("+")[1].strip() + '" }}'

        elif clicktype == 'unitIconClicked':
            question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                                   '"session id":"' + self.session_id + '",' \
                                   '"time zone":"US/Central", ' \
                                   '"student response type":"ATTEMPT", ' \
                                   '"student response subtype":"N/A", ' \
                                   '"tutor response type":"RESULT", ' \
                                   '"tutor response subtype":"N/A", ' \
                                   '"level":"N/A",' \
                                   '"problem name":"N/A", ' \
                                   '"problem view":"0", ' \
                                   '"step name":"N/A", ' \
                                   '"attempt at step":"N/A",' \
                                   '"outcome":"UNGRADED", ' \
                                   '"selection":"N/A", ' \
                                   '"action":"N/A", ' \
                                   '"input":"N/A",' \
                                   '"feedback text":"N/A", ' \
                                   '"feedback classification":"N/A", ' \
                                   '"help level":"", ' \
                                   '"total number hints":"", ' \
                                   '"condition name":"' + self.condition + '", ' \
                                   '"condition type":"N/A", ' \
                                   '"kc":"N/A", ' \
                                   '"kc category":"N/A", ' \
                                   '"school":"' + self.school + '", ' \
                                   '"class":"' + self.className + '", ' \
                                   '"cf_course":"' + str(self.scope_ids.usage_id.course_key) + '", ' \
                                   '"cf_section":"' + section + '", ' \
                                   '"cf_subsection":"' + subsection + '", ' \
                                   '"cf_unit":"' + unit + '", ' \
                                   '"cf_user_runtime_id":"' + str(self.runtime.user_id) + '", ' \
                                   '"cf_student_pastel_id":"' + pastel_id + '",' \
                                   '"cf_question": "N/A", ' \
                                   '"cf_choices": "N/A", ' \
                                   '"cf_video_url":"N/A", ' \
                                   '"cf_video_position":"N/A", ' \
                                   '"cf_page_id":"' + pageId.split("+")[0] + '", ' \
                                   '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                                   '"cf_action":"unit icon clicked",' \
                                   '"cf_result":"' + pageId.split("+")[1].strip() + '" }}'

        elif clicktype == 'pageForwardClicked':
            question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                                   '"session id":"' + self.session_id + '",' \
                                   '"time zone":"US/Central", ' \
                                   '"student response type":"ATTEMPT", ' \
                                   '"student response subtype":"N/A", ' \
                                   '"tutor response type":"RESULT", ' \
                                   '"tutor response subtype":"N/A", ' \
                                   '"level":"N/A",' \
                                   '"problem name":"N/A", ' \
                                   '"problem view":"0", ' \
                                   '"step name":"N/A", ' \
                                   '"attempt at step":"N/A",' \
                                   '"outcome":"UNGRADED", ' \
                                   '"selection":"N/A", ' \
                                   '"action":"N/A", ' \
                                   '"input":"N/A",' \
                                   '"feedback text":"N/A", ' \
                                   '"feedback classification":"N/A", ' \
                                   '"help level":"", ' \
                                   '"total number hints":"", ' \
                                   '"condition name":"' + self.condition + '", ' \
                                   '"condition type":"N/A", ' \
                                   '"kc":"N/A", ' \
                                   '"kc category":"N/A", ' \
                                   '"school":"' + self.school + '", ' \
                                   '"class":"' + self.className + '", ' \
                                   '"cf_course":"' + str(self.scope_ids.usage_id.course_key) + '", ' \
                                   '"cf_section":"' + section + '", ' \
                                   '"cf_subsection":"' + subsection + '", ' \
                                   '"cf_unit":"' + unit + '", ' \
                                   '"cf_user_runtime_id":"' + str(self.runtime.user_id) + '", ' \
                                   '"cf_student_pastel_id":"' + pastel_id + '",' \
                                   '"cf_question": "N/A", ' \
                                   '"cf_choices": "N/A", ' \
                                   '"cf_video_url":"N/A", ' \
                                   '"cf_video_position":"N/A", ' \
                                   '"cf_page_id":"' + pageId + '", ' \
                                   '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                                   '"cf_action":"page forward clicked",' \
                                   '"cf_result":"N/A" }}'

        elif clicktype == 'pageBackwardClicked':
            question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                                   '"session id":"' + self.session_id + '",' \
                                   '"time zone":"US/Central", ' \
                                   '"student response type":"ATTEMPT", ' \
                                   '"student response subtype":"N/A", ' \
                                   '"tutor response type":"RESULT", ' \
                                   '"tutor response subtype":"N/A", ' \
                                   '"level":"N/A",' \
                                   '"problem name":"N/A", ' \
                                   '"problem view":"0", ' \
                                   '"step name":"N/A", ' \
                                   '"attempt at step":"N/A",' \
                                   '"outcome":"UNGRADED", ' \
                                   '"selection":"N/A", ' \
                                   '"action":"N/A", ' \
                                   '"input":"N/A",' \
                                   '"feedback text":"N/A", ' \
                                   '"feedback classification":"N/A", ' \
                                   '"help level":"", ' \
                                   '"total number hints":"", ' \
                                   '"condition name":"' + self.condition + '", ' \
                                   '"condition type":"N/A", ' \
                                   '"kc":"N/A", ' \
                                   '"kc category":"N/A", ' \
                                   '"school":"' + self.school + '", ' \
                                   '"class":"' + self.className + '", ' \
                                   '"cf_course":"' + str(self.scope_ids.usage_id.course_key) + '", ' \
                                   '"cf_section":"' + section + '", ' \
                                   '"cf_subsection":"' + subsection + '", ' \
                                   '"cf_unit":"' + unit + '", ' \
                                   '"cf_user_runtime_id":"' + str(self.runtime.user_id) + '", ' \
                                   '"cf_student_pastel_id":"' + pastel_id + '",' \
                                   '"cf_question": "N/A", ' \
                                   '"cf_choices": "N/A", ' \
                                   '"cf_video_url":"N/A", ' \
                                   '"cf_video_position":"N/A", ' \
                                   '"cf_page_id":"' + pageId + '", ' \
                                   '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                                   '"cf_action":"page backward clicked",' \
                                   '"cf_result":"N/A" }}'

        else:

            question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                                   '"session id":"' + self.session_id + '",' \
                                   '"time zone":"US/Central", ' \
                                   '"student response type":"HINT_REQUEST", ' \
                                   '"student response subtype":"N/A", ' \
                                   '"tutor response type":"HINT_MSG", ' \
                                   '"tutor response subtype":"N/A", ' \
                                   '"level":"N/A",' \
                                   '"problem name":"' + problemId + '", ' \
                                   '"problem view":"' + str(self.attempts) + '", ' \
                                   '"step name":"N/A", ' \
                                   '"attempt at step":"' + str(self.attempts) + '",' \
                                   '"outcome":"HINT", ' \
                                   '"selection":"", ' \
                                   '"action":"", ' \
                                   '"input":"",' \
                                   '"feedback text":"' + feedback + '", ' \
                                   '"feedback classification":"N/A", ' \
                                   '"help level":"' + str(hintCount % len(self.hint.split('|')) + 1) + '", ' \
                                   '"total number hints":"' + str(len(self.hint.split('|'))) + '", ' \
                                   '"condition name":"' + self.condition + '", ' \
                                   '"condition type":"N/A", ' \
                                   '"kc":"' + skillName + '", ' \
                                   '"kc category":"N/A", ' \
                                   '"school":"' + self.school + '", ' \
                                   '"class":"' + self.className + '", ' \
                                   '"cf_course":"' + str(self.scope_ids.usage_id.course_key) + '", ' \
                                   '"cf_section":"' + section + '", ' \
                                   '"cf_subsection":"' + subsection + '", ' \
                                   '"cf_unit":"' + unit + '", ' \
                                   '"cf_user_runtime_id":"' + str(self.runtime.user_id) + '", ' \
                                   '"cf_student_pastel_id":"' + pastel_id + '",' \
                                   '"cf_question": "' + question + '", ' \
                                   '"cf_choices": "' + choices + '", ' \
                                   '"cf_video_url":"N/A", ' \
                                   '"cf_video_position":"N/A", ' \
                                   '"cf_page_id":"' + pageId + '", ' \
                                   '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                                   '"cf_action":"multiple choice get hint",' \
                                   '"cf_result":"N/A" }}'

        # for state column
        state = question_related.encode("utf-8")

        print state

        util_save_user_activity(state, timestamp, str(student_module_id))

    """
        We are not using skill_mapping table anymore!
    """

    @XBlock.json_handler
    def module_skillname_saved(self, data, suffix=''):

        skillname = str(self.kc)
        xblock_id = str(unicode(self.scope_ids.usage_id))
        url = str(data.get('location_id'))
        paragraph_id = str(self.scope_ids.usage_id.block_id).replace("course", "block")
        # if we want to use <jump_to_id> method then we need to know the course_id and paragraph_id. Format: /jump_to_id/location_id#paragraph_id
        # now we are using full url link instead.
        course_id = str(self.scope_ids.usage_id.course_key)
        # full_url = str(data.get('full_url'))[:-1] + "#" + paragraph_id
        # start saving it in the database, target table: skill_mapping
        location_id = course_id + "$" + url + "$" + paragraph_id

        util_save_module_skillname(skillname, xblock_id, location_id)
        return {"skillname": skillname, "xblock_id": xblock_id, "location_id": url, "paragraph_id": paragraph_id,
                "course_id": course_id}

    """
        We are not using skill_mapping table anymore!
    """

    @XBlock.json_handler
    def get_skill_mapping(self, data, suffix=''):
        skillname = str(self.kc)
        xblock_id = str(unicode(self.scope_ids.usage_id))
        url = util_get_skill_mapping(xblock_id, skillname)

        if data.get("getLocation") is False:
            self.dynamicClicked = data.get("getLocation")

        return {'course_id': url[0], "location_id": url[1], "paragraph_id": url[2], "dynamicClicked": self.dynamicClicked}

    @XBlock.json_handler
    def check_answer(self, data, suffix=''):
        """
        Check answer for submitted response
        """
        self.attempts += 1
        response = dict(correct=False, dynamicClicked=False)

        ans = int(data.get('ans', 0))

        # store user response
        self.user_choice = ans

        if ans == self.correct_choice:
            self.correct = True
            response['correct'] = True
            if self.dynamicClicked is True:
                response['dynamicClicked'] = True
        else:
            self.correct = False
            response['correct_choice'] = False
            self.dynamicClicked = False
            response['dynamicClicked'] = False
        return response

    @XBlock.json_handler
    def get_question_name(self, data, suffix=''):
        """
        get question name as a hint id 
        """
        return {'response': self.question}

    @XBlock.json_handler
    def get_border_color(self, data, suffix=''):
        course_id = str(self.scope_ids.usage_id.course_key)
        skillname = self.kc
        problemId = self.problemId

        HtmlSetBorderColor = util_get_border_color(course_id, skillname)
        return {"setBorderColor": HtmlSetBorderColor, "skillname": skillname, "problemId": problemId}

    @XBlock.json_handler
    def update_question(self, data, suffix=''):
        """
        Update all the fields:
        """
        self.display_name = str(data['problemTitle'])
        self.problemId = data['problemId']
        self.question = data['question']
        self.choices = data['choices']
        self.correct_choice = data['correct']
        self.hint = data['hint']
        self.kc = data['kc']
        self.image_url = data['imageUrl']
        self.image_size = data['imageSize']
        # for course information:
        section = data['section']
        subsection = data['subsection']
        unit = data['unit']

        course_id = str(self.scope_ids.usage_id.course_key)
        xblock_id = str(unicode(self.scope_ids.usage_id))
        type_of_xblock = "MultipleChoiceQuestion"
        title = self.display_name
        question = self.question
        choices = '| '.join(self.choices)
        image_url = self.image_url
        correct_answer = self.correct_choice
        hint = self.hint
        problem_name = self.problemId
        skillname = self.kc


        self.setBorderColor = util_update_xblock_for_exporter(xblock_id, course_id, section, subsection, unit,
                                                              type_of_xblock, title, question,
                                                              choices, image_url, correct_answer, hint, problem_name,
                                                              skillname)

        return {'result': 'success'}

    @XBlock.json_handler
    def get_hint(self, data, suffix=''):
        """
        Give hint for the question
        """
        self.hint_numbers += 1
        response = dict(hint=self.hint)
        self.hintClicked = True
        return {'response': self.hint}

    @XBlock.json_handler
    def get_default_data(self, data, suffix=''):
        """
        when mcqs_edit page is on load, get all the default data from here
        """

        return {'display_name': self.display_name, 'title': self.title, 'problemId': self.problemId, 'kc': self.kc,
                'question': self.question, 'choices': self.choices, 'correct_choice': self.correct_choice,
                'hint': self.hint, 'image_url': self.image_url, 'image_size': self.image_size, 'iso': self.iso}

    @XBlock.json_handler
    def get_xblock_id(self, data, suffix=''):
        xblock_obj = self.scope_ids.usage_id
        xblock_id = (str(xblock_obj.course_key) + '+type@' + str(xblock_obj.block_type) + '+block@' + str(
            xblock_obj.block_id)).replace("course", "block")
        return {"xblock_id": xblock_id, "xblock_code": str(xblock_obj.block_id).replace("course", "block"), "skillname": self.kc}

    @XBlock.json_handler
    def update_display_name(self, data, suffix=''):
        self.display_name = str(data['result'])
        return {'display_name': self.display_name}

    @XBlock.json_handler
    def get_studentId_and_skillname(self, data, suffix=''):
        if self.pastel_student_id is None:
            return {"student_id": str(self.runtime.user_id), "skillname": self.kc, "question_id": self.problemId,
                    "correctness": self.correct, "course": str(self.scope_ids.usage_id.course_key)}
        else:
            return {"student_id": str(self.pastel_student_id), "skillname": self.kc, "question_id": self.problemId,
                    "correctness": self.correct, "course": str(self.scope_ids.usage_id.course_key)}

    @XBlock.json_handler
    def delete_xbock(self, data, suffix=''):
        xblock_id = str(data.get("xblock_id"))
        util_delete_xblock_for_exporter(xblock_id)

    @XBlock.json_handler
    def get_hint_length(self, data, suffix=''):

        return {"length": len(self.hint.split('|'))}

    @XBlock.json_handler
    def get_probability(self, data, suffix=''):

        skillname = str(data.get('skillname'))
        student_id = str(data.get('student_id'))

        result = util_get_probability(skillname, student_id)
        if result is not None:
            return {'probability': str(result[5])}
        else:
            print "Probability table doesn't contain the skillname."
            return {'probability': '0'}

    @XBlock.json_handler
    def get_pastel_student_id(self, data, suffix=''):

        user_id = str(self.runtime.user_id)
        self.pastel_student_id, self.school, self.className, self.condition = util_get_pastel_student_id(user_id)

        return {'pastel_student_id': user_id if self.pastel_student_id is None else self.pastel_student_id, 'hasBeenSent': self.hasBeenSent}


    @XBlock.json_handler
    def update_iso_question(self, data, suffix=''):
        value = int(data.get('iso'))
        if value == 1:
            self.iso = True
        else:
            self.iso = False

    @staticmethod
    def workbench_scenarios():
        """
        A canned scenario for display in the workbench.
        """
        return [
            ("McqsXBlock",
             """<mcqs/>
             """),
            ("Multiple McqsXBlock",
             """<vertical_demo>
                <mcqs/>
                <mcqs/>
                <mcqs/>
                </vertical_demo>
             """),
        ]
