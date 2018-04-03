""" An XBlock for Text """

import pkg_resources
from django.template import Template, Context

from xblock.core import XBlock
from xblock.fields import Scope, Integer, String, List, Boolean
from xblock.fragment import Fragment
from xblock.validation import ValidationMessage
from xblockutils.studio_editable import StudioEditableXBlockMixin
from xblock_utils import *
import pytz


class TextXBlock(XBlock, StudioEditableXBlockMixin):
    """
    Multiple Choice Question XBlock
    """
    display_name = String(default='Text')
    block_name = String(default='Text')
    
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
    text_content = String(
        display_name='text_content',
        default='Text Content',
        scope=Scope.content
    )
    text_title = String(
        display_name='text_title',
        default='Text Title',
        scope=Scope.content
    )
    text_sub_title = String(
        display_name='text_sub_title',
        default='Text sub title',
        scope=Scope.content
    )
    image_url = String(
        default='',
        scope=Scope.content
    )
    condition = String(default="", scope=Scope.user_state)
    school = String(default="", scope=Scope.user_state)
    className = String(default="", scope=Scope.user_state)
    pastel_student_id = String(scope=Scope.user_state)
    module_id = Integer(default=0, scope=Scope.user_state)
    xblock_id = String(default="", scope=Scope.user_state)
    student_id = Integer(default=0, scope=Scope.user_state)
    session_id = String(default="", scope=Scope.user_info)
    image_size = String(default="50%", scope=Scope.content)
    row1 = String(scope=Scope.user_state)
    #This 'attempt' attribute is for 'attempts' column
    attempts = Integer(default=0, scope=Scope.user_state)
    hint_numbers = Integer(default=0, scope=Scope.user_state)
    # When student refresh the page, countTimes will increase one
    count_times = Integer(default=0, scope=Scope.user_state)
    location_id = String(default="", scope=Scope.content)
    setBorderColor = Integer(default=0, scope=Scope.content, help='Help studio view to see if there is any skillname missing')
    
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

        html = Template(self.resource_string("static/html/text.html")).render(Context(context))
        frag = Fragment(html)
        frag.add_css(self.resource_string("static/css/text.css"))
        frag.add_javascript(self.resource_string("static/js/src/text.js"))
        frag.initialize_js('TextXBlockInitView')

        return frag

    def studio_view(self, context=None):
        """
        The primary view of the simstudentXBlock, shown to students
        when viewing courses.
         """
        if context is None:
            context = {}

        context.update({'self': self})
        
        html = Template(self.resource_string("static/html/text_edit.html")).render(Context(context))
        frag = Fragment(html)
        frag.add_javascript(self.resource_string("static/js/src/text_edit.js"))
        frag.initialize_js('TextXBlockInitStudio')

        return frag

    #for skill mapping -- Link Generator
    @XBlock.json_handler
    def module_skillname_saved(self, data, suffix=''):
        
        
        skillname = str(self.kc)
        xblock_id = str(unicode(self.scope_ids.usage_id))
        url = str(data.get('location_id'))
        paragraph_id = str(self.scope_ids.usage_id.block_id).replace("course", "block")
        # if we want to use <jump_to_id> method then we need to know the course_id and paragraph_id. Format: /jump_to_id/location_id#paragraph_id
        # now we are using full url link instead.
        course_id = str(self.scope_ids.usage_id.course_key)
        #full_url = str(data.get('full_url'))[:-1] + "#" + paragraph_id
        #start saving it in the database, target table: skill_mapping
        location_id = course_id + "$" + url + "$" + paragraph_id

        util_save_module_skillname(skillname, xblock_id, location_id)

        return {"skillname": skillname, "xblock_id": xblock_id, "location_id": url, "paragraph_id": paragraph_id, "course_id": course_id}

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

        self.insert_user_activities(0, hintCount, clicktype, pageId, section, subsection, unit)

        return {'user_id': self.runtime.anonymous_student_id, 'xblock_id': self.xblock_id}

        # This method help us to store the user activities in OpenEdx database:
    def insert_user_activities(self, student_module_id, hintCount, clicktype, pageId, section, subsection, unit):
        # first, collect all the information

        ts = time.time()
        timestamp = datetime.datetime.fromtimestamp(ts, pytz.timezone('US/Central')).strftime('%Y-%m-%d %H:%M:%S')

        self.pastel_student_id, self.school, self.className, self.condition = util_get_pastel_student_id(str(self.runtime.user_id))


        pastel_id = "None" if self.pastel_student_id is None else str(self.pastel_student_id)

        if self.session_id == '' or self.session_id is None:
            self.session_id = util_generate_session_id(str(self.runtime.user_id))

        if clicktype == 'pageLoaded':

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
                               '"cf_action":"page loaded",' \
                               '"cf_result":"N/A" }}'

        elif clicktype == 'menuLinkClicked':
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

        # for state column

        state = question_related.encode('utf-8')
        print state

        util_save_user_activity(state, timestamp, str(student_module_id))


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

        HtmlSetBorderColor = util_get_border_color(course_id, skillname)
        return {"setBorderColor": HtmlSetBorderColor, "skillname": skillname}
    
    @XBlock.json_handler
    def update_question(self, data, suffix=''):
        """
        Update all the fields:
        """    
        self.kc=data['kc']
        self.text_content=data['textContent']
        self.text_title=data['textTitle']
        self.text_sub_title=data['textSubTitle']
        self.image_url = data['image_url']
        self.image_size=data['imageSize']
        section = data['section']
        subsection = data['subsection']
        unit = data['unit']

        xblock_id = str(unicode(self.scope_ids.usage_id))
        course_id = str(self.scope_ids.usage_id.course_key)

        type_of_xblock = "TextParagraph"
        title = data['textTitle']
        sub_title = data['textSubTitle']
        text = data['textContent']
        image_url = str(self.image_url)
        skillname = str(self.kc)


        util_update_xblock_for_exporter(xblock_id, course_id, section, subsection, unit, type_of_xblock, title, sub_title, text, image_url, skillname)
        
        return {'result': 'success'}

    
    @XBlock.json_handler
    def get_default_data(self, data, suffix=''):
        """
        when mcqs_edit page is on load, get all the default data from here
        """
        skillset = util_get_skillset(str(self.scope_ids.usage_id.course_key))
        return {'display_name': self.display_name, 'title': self.title,  'kc': self.kc, 'text_title': self.text_title, 'text_content': self.text_content, 'text_sub_title': self.text_sub_title, 'image_url': self.image_url, 'image_size': self.image_size, "skillset": skillset}
    
    
    @XBlock.json_handler
    def delete_xbock(self, data, suffix=''):
        xblock_id = str(data.get("xblock_id"))
        print "Start to delete xblock_id"
        util_delete_xblock_for_exporter(xblock_id)
        print "End with deletion"
    
    @XBlock.json_handler
    def get_xblock_id(self, data, suffix=''):
        xblock_obj = self.scope_ids.usage_id
        xblock_id= (str(xblock_obj.course_key) + '+type@' + str(xblock_obj.block_type) + '+block@' + str(xblock_obj.block_id)).replace("course", "block")
        return {"xblock_id": xblock_id, "xblock_code": str(xblock_obj.block_id).replace("course", "block"), "skillname": self.kc }
    
    @staticmethod
    def workbench_scenarios():
        """
        A canned scenario for display in the workbench.
        """
        return [
            ("McqsXBlock",
             """<text/>
             """),
            ("Multiple McqsXBlock",
             """<vertical_demo>
                <text/>
                <text/>
                <text/>
                </vertical_demo>
             """),
        ]
