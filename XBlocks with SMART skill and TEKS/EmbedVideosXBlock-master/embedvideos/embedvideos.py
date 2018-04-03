"""
EmbedVideos XBlock enables users to post from various providers
with various parameters on a single XBlock.
"""

import pkg_resources

from xblock.core import XBlock
from xblock.fields import Scope, Integer, String, Boolean
from xblock.fragment import Fragment
from xblockutils.studio_editable import StudioEditableXBlockMixin
from jinja2 import Environment, PackageLoader
env = Environment(loader=PackageLoader('embedvideos', 'static/html'))
from xblock_utils import *
import pytz


class EmbedVideosXBlock(XBlock, StudioEditableXBlockMixin):    #pylint disable=R0901, R0904
    """
    The class variables are all the parameters for each video provider.
    Default values are the actual default values of each parameter.
    """


    # YOUTUBE BLOCK #
    # detailed descriptions of the parameters can be found here:
    # https://developers.google.com/youtube/player_parameters
    display_name = String(default='Video', scope=Scope.content)
    block_name = String(default = 'Video XBlock')
    youtube_id = String(default='nLy_jEbuY-U', scope=Scope.content)
    youtube_width = Integer(default=850, scope=Scope.content)
    youtube_height = Integer(default=500, scope=Scope.content)
    attempts = Integer(default=0, scope=Scope.user_state)
    video_watch = Integer(default=1, scope=Scope.user_state)
    session_id = String(default="", scope=Scope.user_info)
    # add seq_number to implement the Writer for debugging
    seq_number = Integer(default=0, scope=Scope.user_info)
    kc = String(display_name='KC ()', default='Enter a Skill Name here', scope=Scope.content)
    problemId = String(display_name='default problem id', default='Enter a Problem Name here', scope=Scope.content, help='Problem Name for DataShop')
    condition = String(default="", scope=Scope.user_state)
    school = String(default="", scope=Scope.user_state)
    className = String(default="", scope=Scope.user_state)
    module_id = Integer(default=0, scope=Scope.user_state)
    xblock_id = String(default="", scope=Scope.user_state)
    student_id = Integer(default=0, scope=Scope.user_state)
    pastel_student_id = String(scope=Scope.user_state)
    playerInfoList = String(default="", scope=Scope.user_state_summary)

    setBorderColor = Integer(default=0, scope=Scope.content, help='Help studio view to see if there is any skillname missing')



    def resource_string(self, path):
        """Handy helper for getting resources from our kit."""
        data = pkg_resources.resource_string(__name__, path)
        return data.decode("utf8")

    # TO-DO: change this view to display your data your own way.
    def student_view(self, context=None):
        """Create fragment and send the appropriate context."""

        context = {
            'youtube_id': self.youtube_id,
            'youtube_width': self.youtube_width,
            'youtube_height': self.youtube_height,
            'display_name': self.display_name,
            'kc': self.kc,
            'problemId': self.problemId
        }

        frag = Fragment()
        template = env.get_template('embedvideos.html')
        frag.add_content(template.render(**context))    #pylint: disable=W0142
        frag.add_css(self.resource_string('static/css/embedvideos.css'))
        frag.add_javascript(self.resource_string("static/js/src/embedvideos.js"))  # pylint: disable=C0301
        frag.add_javascript(self.resource_string("static/js/src/video_event.js"))
        frag.initialize_js('EmbedVideosXBlock')

        return frag


    def studio_view(self, context):
        """Create a fragment used to display the edit view in the Studio."""

        context = {
                'youtube_id': self.youtube_id,
                'youtube_width': self.youtube_width,
                'youtube_height': self.youtube_height,
                'display_name': self.display_name,
                'kc': self.kc,
                'problemId': self.problemId
        }

        frag = Fragment()
        template = env.get_template('embedvideos_edit.html')
        frag.add_content(template.render(**context))    #pylint: disable=W0142
        frag.add_css(self.resource_string("static/css/embedvideos_edit.css"))
        frag.add_javascript(self.resource_string("static/js/src/embedvideos_edit.js")) #pylint: disable=C0301
        frag.initialize_js('EmbedVideosEditXBlock')
        return frag        


    @XBlock.json_handler
    def studio_submit(self, data, suffix=''):   #pylint disable=W0613
        """Called when submitting the form in Studio."""

        attrList = [
            'youtube_id',
            'youtube_width',
            'youtube_height',
            'display_name',
            'kc',
            'section',
            'subsection',
            'unit',
            'problemId'
        ]

        for item in attrList:
            if (data.get(item) != None):
                setattr(self, item, data.get(item))

        return {'result':'success'}

    @XBlock.json_handler
    def refresh_session(self, data, suffix=''):
        self.session_id = util_generate_session_id(str(self.runtime.user_id))
        self.seq_number = 0

    @XBlock.json_handler
    def get_youtube_id(self, data, suffix=''):
        if self.youtube_id not in self.playerInfoList:
            self.playerInfoList += self.youtube_id + ","

        return {"playerInfoList": self.playerInfoList, "youtube_id": self.youtube_id, "youtube_height": self.youtube_height, "youtube_width": self.youtube_width}

    @XBlock.json_handler
    def get_xblock_id(self, data, suffix=''):

        xblock_obj = self.scope_ids.usage_id
        xblock_id = (str(xblock_obj.course_key) + '+type@' + str(xblock_obj.block_type) + '+block@' + str(
            xblock_obj.block_id)).replace("course", "block")
        return {"xblock_id": xblock_id, "xblock_code": str(xblock_obj.block_id).replace("course", "block"), "skillname": self.kc}

    @XBlock.json_handler
    def get_student_id(self, data, suffix=''):
        """
         anonymous_student_id format: 5c58c732aba1b9e2e708a00c3b243de7
         1. base on the anony student id, we find the user_id attribute from edxapp.student_anonymoususerid
        """

        self.pastel_student_id, self.school, self.className, self.condition = util_get_pastel_student_id(str(self.runtime.user_id), str(self.scope_ids.usage_id.course_key))

        self.attempts += 1
        self.xblock_id = str(unicode(self.scope_ids.usage_id))
        self.student_id = int(self.runtime.user_id)  #student_id format: 5

        clicktype = data['status']
        if clicktype == "video end":
            self.video_watch += 1

        current_play_time = str(datetime.timedelta(seconds=float(data['time'])))
        pageId = str(data.get('pageId'))
        section = str(data.get('section'))
        subsection = str(data.get('subsection'))
        unit = str(data.get('unit'))

        # add seq_number to implement Writer for dubugging:
        self.seq_number += 1

        self.insert_user_activities(0, clicktype, current_play_time, pageId, section, subsection, unit)

        return {'user_id': self.runtime.anonymous_student_id, 'xblock_id': self.xblock_id}

        # This method help us to store the user activities in OpenEdx database:
    def insert_user_activities(self, student_module_id, clicktype, current_play_time, pageId, section, subsection, unit):
        """
            display_name = String(default='Video', scope=Scope.content)
            block_name = String(default = 'Video XBlock')
            youtube_id = String(default='nLy_jEbuY-U', scope=Scope.content)
            youtube_width = Integer(default=578, scope=Scope.content)
            youtube_height = Integer(default=325, scope=Scope.content)
            attempts = Integer(default=0, scope=Scope.user_state)
            section = String(display_name="New Section", default="", scope=Scope.content)
            subsection = String(display_name="Sub Section", default="", scope=Scope.content)
            unit = String(display_name="Unit", default="", scope=Scope.content)
        """


        ts = time.time()
        timestamp = datetime.datetime.fromtimestamp(ts, pytz.timezone('US/Central')).strftime('%Y-%m-%d %H:%M:%S.%f')

        if self.session_id == '' or self.session_id is None:
            self.session_id = util_generate_session_id(str(self.runtime.user_id))

        pastel_id = "None" if self.pastel_student_id is None else str(self.pastel_student_id)

        question_related = '{"question_details": {"timestamp":"' + timestamp + '", ' \
                               '"session id":"' + self.session_id + '",' \
                               '"time zone":"US/Central", ' \
                               '"student response type":"ATTEMPT", ' \
                               '"student response subtype":"N/A", ' \
                               '"tutor response type":"RESULT", ' \
                               '"tutor response subtype":"N/A", ' \
                               '"level":"N/A",' \
                               '"problem name":"N/A", ' \
                               '"problem view":"' + str(self.video_watch) + '", ' \
                               '"step name":"N/A", ' \
                               '"attempt at step":"' + str(self.attempts) + '",' \
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
                               '"kc":"' + self.kc + '", ' \
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
                               '"cf_video_url":"https://www.youtube.com/watch?v=' + self.youtube_id + '", ' \
                               '"cf_video_position":"' + current_play_time + '", ' \
                               '"cf_page_id":"' + pageId + '", ' \
                               '"cf_unit_id":"' + str(self.scope_ids.usage_id.block_id).replace("course", "block") + '", ' \
                               '"cf_action":"' + clicktype + '",' \
                               '"cf_result":"' + self.display_name + '", "cf_seq_number":"' + str(self.seq_number) + '"}}'

        # for state column
        state = question_related.encode('utf-8')

        # Mysql database access here:
        print state
        util_save_user_activity(state, timestamp, str(student_module_id), section, subsection, unit, self.display_name)

    @XBlock.json_handler
    def update_question(self, data, suffix=''):
        """
        Update all the fields after 'save' in the studio view:
        var attrList = [
            'youtube_id',
            'youtube_width',
            'youtube_height',
            'display_name',
            'problemId',
            'kc',
            'section',
            'subsection',
            'unit'
        ];
        """

        course_id = str(self.scope_ids.usage_id.course_key)
        xblock_id = str(unicode(self.scope_ids.usage_id))
        type_of_xblock = "VideoXBlock"

        youtubeId = data['youtube_id']
        youtubeWidth = data['youtube_width']
        youtubeHeight = data['youtube_height']
        title = data['display_name']
        problemId = data['problemId']
        skillname = data['kc']
        section = data['section']
        subsection = data['subsection']
        unit = data['unit']

        self.setBorderColor = util_update_xblock_for_exporter(course_id, xblock_id, section, subsection, unit, type_of_xblock, title, skillname, problemId, youtubeId, youtubeWidth, youtubeHeight)

        return {'result': 'success', 'border': self.setBorderColor}

    # for skill mapping -- Link Generator
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


    @XBlock.json_handler
    def get_border_color(self, data, suffix=''):
        course_id = str(self.scope_ids.usage_id.course_key)
        skillname = self.kc

        HtmlSetBorderColor = util_get_border_color(course_id, skillname)
        return {"setBorderColor": HtmlSetBorderColor, "skillname": skillname}

    @XBlock.json_handler
    def delete_xbock(self, data, suffix=''):
        xblock_id = str(data.get("xblock_id"))
        print "Start to delete xblock_id"
        util_delete_xblock_for_exporter(xblock_id)
        print "End with deletion"

    @staticmethod
    def workbench_scenarios():
        """A canned scenario for display in the workbench."""
        return [
            ("EmbedVideosXBlock",
             """<vertical_demo>
                <embedvideos youtube_id="nLy_jEbuY-U"/>
                </vertical_demo>
             """),
        ]