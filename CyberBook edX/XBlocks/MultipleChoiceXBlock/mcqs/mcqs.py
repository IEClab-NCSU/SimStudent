""" An XBlock for Multiple Choice Questions """

import pkg_resources

from django.template import Template, Context
from opaque_keys.edx.keys import CourseKey, UsageKey
from opaque_keys import InvalidKeyError
from xblock.core import XBlock
from xblock.fields import Scope, Integer, String, List, Boolean
from xblock.fragment import Fragment
from xblock.validation import ValidationMessage
from xblockutils.studio_editable import StudioEditableXBlockMixin
#for mysql db connection
import MySQLdb
import datetime
import time


class McqsXBlock(XBlock, StudioEditableXBlockMixin):
    """
    Multiple Choice Question XBlock
    """
    display_name = String(default='Multiple Choice Question', scope=Scope.content)
    block_name = String(default='Multiple Choice Question')
    #editable_fields = ('title', 'question', 'choices', 'correct_choice', 'hint')
    
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
        default='Enter a Problem Name here',
        scope=Scope.content
    )
    image_url = String(
        default="image_url",
        scope=Scope.content
    )
    user_choice = Integer(scope=Scope.user_state, help='Index of choice selected by User')
    correct = Boolean(default=False, scope=Scope.user_state, help='User selection is correct or not')
    row1 = String(scope=Scope.user_state)
    #This 'attempt' attribute is for 'attempts' column
    attempts = Integer(default=0, scope=Scope.user_state)
    hint_numbers = Integer(default=0, scope=Scope.user_state)
    # When student refresh the page, countTimes will increase one
    count_times = Integer(default=0, scope=Scope.user_state)
    module_id = Integer(default=0, scope=Scope.user_state)
    xblock_id = String(default="", scope=Scope.user_state)
    student_id = Integer(default=0, scope=Scope.user_state)
    
    
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
        #frag.add_css(self.resource_string("static/css/font-awesome/css/font-awesome.min.css"))
        frag.add_javascript(self.resource_string("static/js/src/mcqs.js"))
        frag.initialize_js('McqsXBlockInitView')
        """
        frag = Fragment()
        frag.add_content(render_template("static/html/mcqs.html",{'self': self, 'context': context}))
        frag.add_css(self.resource_string("static/css/mcqs.css"))
        frag.add_javascript(self.resource_string("static/js/src/mcqs.js"))
        frag.initialize_js('McqsXBlockInitView')
        """
        return frag

    def studio_view(self, context=None):
        """
        The primary view of the simstudentXBlock, shown to students
        when viewing courses.
         """
        if context is None:
            context = {}

        context.update({'self': self})
        
        html = Template(self.resource_string("static/html/mcqs_edit.html")).render(Context(context))
        frag = Fragment(html)
        frag.add_javascript(self.resource_string("static/js/src/mcqs_edit.js"))
        frag.initialize_js('McqsXBlockInitStudio')
        """
        frag = Fragment()
        frag.add_content(render_template("static/html/mcqs_edit.html",{'self': self, 'context': context}))
        frag.add_javascript(self.resource_string("static/js/src/mcqs_edit.js"))
        frag.initialize_js('McqsXBlockInitStudio')
        """
        return frag
    
    def validate_field_data(self, validation, data):
        """
        Perform validation on Studio submitted data
        """
        if not data.question.strip():
            validation.add(ValidationMessage(ValidationMessage.ERROR, u"Question is required."))

        # there must be two choices to choose from
        if not data.choices or len(data.choices) < 2:
            validation.add(ValidationMessage(ValidationMessage.ERROR, u"Please enter at least two choices"))

        if data.correct_choice not in range(1, len(data.choices) + 1):
            validation.add(ValidationMessage(
                ValidationMessage.ERROR,
                u"Correct choice must be from 1 to {}".format(len(data.choices))
            ))

        
    @XBlock.json_handler
    def get_status_when_refresh(self, data, suffix=''):
        return {"countTimes": self.count_times, "correctness": self.correct, "userChoice": self.user_choice}
    
    @XBlock.json_handler
    def set_status_when_refresh(self, data, suffix=''):
        self.count_times += 1
        self.user_choice = int(data.get('userChoice', 0))
    
    
    #Get anonymous student id
    @XBlock.json_handler
    def get_student_id(self, data, suffix=''):
        # created, state, grade, max_grade, id, student_module_id
        #5c58c732aba1b9e2e708a00c3b243de7
        
        #1. base on the anony student id, we find the user_id attribute from edxapp.student_anonymoususerid
        
        print str(data.get('type'))
        self.xblock_id = str(unicode(self.scope_ids.usage_id))
        self.student_id = int(self.runtime.user_id)
        print "I am scope_ids: "
        print self.scope_ids
        ts = time.time()
        timestamp = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
        hintCount = int(data.get('hintCount'))

        if self.xblock_id == "" or self.student_id == 0:
            print "I am getting the xblock_id and student_id from database..."
            try:
                db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp")
                db.autocommit(True)
                cursor = db.cursor()
                sql = """select * from edxapp.student_anonymoususerid where anonymous_user_id='""" + self.runtime.anonymous_student_id + """'"""
                cursor.execute(sql)
                row = cursor.fetchone()

                #2. base on the student_id and xblock id we can find the student_module_id
                # table: edxapp.courseware_studentmodule
                self.student_id = int(row[3])
                print "student_id: ", str(row[3])

                #self.scopr_ids.usage_id: {'block_type': u'mcqs', 'block_id': u'3b74606022e047539c790c57a056c61d', 'course_key': CourseLocator(u'University1', u'Ts1', u'2014_T1', None, None)}

                xblock_obj = self.scope_ids.usage_id
                print "I am xblock_obj: "
                print xblock_obj
                xblock_id= (str(xblock_obj.course_key) + '+type@' + str(xblock_obj.block_type) + '+block@' + str(xblock_obj.block_id)).replace("course", "block")
                self.xblock_id = str(xblock_id)
                print "I am xblock_id:"
                print str(xblock_id)

                #print-result:
                # block-v1:University1+Ts1+2014_T1+type@mcqs+block@0947a6a6dff34756ad4d130753cfdd60
                # block-v1:University1+Ts1+2014_T1+type@mcqs+block@0947a6a6dff34756ad4d130753cfdd60
            except:
                import traceback
                traceback.print_exc()
                db.rollback()
                print "Database has beeen rollback!!!"
            finally:
                cursor.close()
                db.close()
                time.sleep(1)
                # invoke the get_module_id method

        self.get_module_id(self.student_id, self.xblock_id, hintCount, str(data.get('type')))
        time.sleep(0.5)
        return {'user_id': self.runtime.anonymous_student_id, 'xblock_id': self.xblock_id}

        # This method help us to find module_id for our XBlock
    def get_module_id(self, student_id, xblock_id, hintCount, clicktype):
        print '*' + str(student_id) + '*'
        print '*' + xblock_id + '*'
        print '*' + clicktype + '*'
        if self.module_id == 0:
            try:
                db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp")

                cursor = db.cursor()
                sql = """select * from edxapp.courseware_studentmodule where student_id=""" + str(student_id) + """ and module_id='""" + str(xblock_id) + """'"""
                cursor.execute(sql)
                student_module_id = cursor.fetchone()
                self.module_id = int(student_module_id[0])
                print "module_id is: ", str(self.module_id)

            except:
                import traceback
                traceback.print_exc()
                db.rollback()
                print "Database has been rollback!!!"
            finally:
                cursor.close()
                db.close()

        self.insert_user_activities(self.module_id, hintCount, clicktype)

        # This method help us to store the user activities in OpenEdx database:
    def insert_user_activities(self, student_module_id, hintCount, clicktype):
        # first, collect all the information

        hintMessage = self.hint

        correctAnswer = str(self.correct)
        message = self.hint.split('|')[hintCount]

        correct_map = '"correct_map":{"mcqs_id":{"hint":"' + hintMessage + '", "hintmode": "click", "correctness":"' + correctAnswer + '", "msg": "' + message + '", "answervarible": null, "npoints": null, "queuestate": null}}'
        input_state = '"mcqs_input_state": {}'
        ts = time.time()
        timestamp = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
        last_submission_time = '"last_submission_time": "' + timestamp + '"'
        attempts = '"attempts":' + str(self.attempts) + ''
        seed = '"seed": 1'
        done = '"done": true'

        choice = self.user_choice
        student_answers = '"student_answers":{"mcqs_answers":"' + str(choice) + '"}'

        strings = self.xblock_id.split('+')
        school = strings[0].split(':')[1]
        classname = strings[1]


        print "self.user_choice: ", self.user_choice

        displayName = self.display_name


        title = self.title


        problemId = self.problemId


        question = self.question


        choices = '| '.join(self.choices)



        userChoice = str(self.user_choice)


        skillName = self.kc

        if self.user_choice == 0 or self.user_choice is None:
            input = "no selection"
        else:
            input = self.choices[self.user_choice - 1]


        feedback = self.hint.split('|')[hintCount]


        if self.user_choice is None:
            print "self.user_choice is None!!!"
            return
        if clicktype == 'checkbutton':

            question_related ='"question_details":{"display_name":"' + displayName + '", "problem name":"' + title + '", "problemId": "' + problemId + '", "question": "' + question + '", "choices": "' + choices + '", "user_choice":"' + userChoice + '", "skillname":"' + skillName + '", "kc": "' + skillName + '", "time zone": "US/Central", "student response type": "ATTEMPT", "student response subtype": "N/A", "tutor response type": "RESULT","tutor response subtype": "N/A", "level": "N/A", "problem view": "1", "step name": "N/A", "attemp at step": "' + str(
                    self.attempts) + '", "selection": "' + userChoice + '", "Action": "Multiple Choice", "input": "' + input + '", "feedback text": "' + feedback + '", "feedback classification": "N/A", "help level": "' + str(
                    self.hint_numbers) + '", "total number hints": "' + str(len(self.hint.split(
                    '|'))) + '", "condition name": "N/A", "condition type": "N/A", "kc category": "N/A", "school": "' + school + '", "class": "' + classname + '", "cf": "N/A"}'
        else:
            question_related = '"question_details":{"display_name":"' + displayName + '", "problem name":"' + title + '", "problemId": "' + problemId + '", "question": "' + question + '", "choices": "' + choices + '", "user_choice":"' + userChoice + '","skillname":"' + skillName + '", "kc": "' + skillName + '", "time zone": "US/Central", "student response type": "HINT_REQUEST", "student response subtype": "N/A", "tutor response type": "HINT_MSG","tutor response subtype": "N/A", "level": "N/A", "problem view": "1", "step name": "N/A", "attemp at step": "' + str(
                    self.attempts) + '", "selection": "", "Action": "Multiple Choice get hint", "input": "", "feedback text": "' + feedback + '", "feedback classification": "N/A", "help level": "' + str(
                    self.hint_numbers) + '", "total number hints": "' + str(len(self.hint.split(
                    '|'))) + '", "condition name": "N/A", "condition type": "N/A", "kc category": "N/A", "school": "' + school + '", "class": "' + classname + '", "cf": "N/A"}'

        # for state column
        state = '{' + correct_map + ',' + input_state + ',' + last_submission_time + ',' + attempts + ',' + seed + ',' + done + ',' + student_answers + ',' + question_related + '}'

        # Mysql database access here:
        try:
            db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
            cursor = db.cursor()

            sql = """INSERT INTO edxapp_csmh.coursewarehistoryextended_studentmodulehistoryextended(state, created, student_module_id)
             VALUES (%s, %s, %s)"""



            cursor.execute(sql, (state, timestamp, str(student_module_id)))
            db.commit()
            print "Database finished executing mcqs..."
        except Exception as e:
            print e
            db.rollback()
            print "database rollback!"
        finally:
            db.close()

        print state

    
    
    #for skill mapping
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
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
        cursor = db.cursor();
        sql = """INSERT INTO edxapp_csmh.module_skillname(xblock_id, type, skillname, location) VALUES (%s, %s, %s, %s)"""

        try:
            cursor.execute(sql,(xblock_id, "mcqs", skillname, location_id))
            db.commit()
                
            print "Skillname has been saved in module_skillname table."
        except Exception as e:
            print e
            db.rollback()
                
            print "Database rollback!"
            return {}
        
        sql1 = """select * from edxapp_csmh.module_skillname where id = (select max(id) from edxapp_csmh.module_skillname where type='text' and skillname=%s and id< (select id from edxapp_csmh.module_skillname where xblock_id=%s));"""
        
        sql2 = """INSERT INTO edxapp_csmh.skill_mapping(assessment_id, location) VALUES (%s, %s)"""
        try: 
            cursor.execute(sql1, (skillname, xblock_id))
            match_result = cursor.fetchone()
            db.commit()
            if not cursor.rowcount:
                print "No results found"
                return {"result": "No results found."}
        except Exception as e:
            print e
            db.rollback()
            print "this skillname didn't have any paragraph match."   
            return {"exception": "this skillname didn't have any paragraph match." }
        try:
            cursor.execute(sql2, (xblock_id, match_result[4]))
            db.commit()
            print "get location matching after the skillname saved."
        except Exception as ep:
            print ep
            db.rollback()
            print "this skillname didn't have any paragraph match."  
        finally:
            db.close()
        
        return {"skillname": skillname, "xblock_id": xblock_id, "location_id": url, "paragraph_id": paragraph_id, "course_id": course_id, "max_id": match_result[0], "matching_location": match_result[4], "type": match_result[2], "xblock_id": match_result[1]}
    
    
    #for skill mapping
    @XBlock.json_handler
    def get_skill_mapping(self, data, suffix=''):
        
            
        # We don't recognize this key
        #start saving it in the database, target table: skill_mapping
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
        cursor = db.cursor();
        sql = """SELECT location from edxapp_csmh.skill_mapping where assessment_id = '%s';"""
        
        try:
            cursor.execute(sql % str(unicode(self.scope_ids.usage_id)))
            result = cursor.fetchone()
            if not cursor.rowcount:
                print "No results found"
                return {"result": "No results found."}
            url = result[0].split("$")
            course_id = url[0]
            location_id = url[1]
            paragraph_id = url[2]
            print "Got skill name from DB.", skillname
        except Exception as e:
            print e
            db.rollback()
            print "Database rollback!"
        finally:
            db.close()
        
        return {'course_id': course_id, "location_id": location_id, "paragraph_id": paragraph_id}
    
    # Mr.Dig shit ask me to do so. I can do nothing about it.
    @XBlock.json_handler
    def export_course_content(self, data, suffix=''):
        xblock_id = str(unicode(self.scope_ids.usage_id))
        type_of_xblock = "Multiple Choice Question"
        title = self.display_name
        question = self.question
        choices = '| '.join(self.choices)
        image_url = self.image_url
        correct_answer = self.correct_choice
        hint = self.hint
        problem_name = self.problemId
        skill_name = self.kc
        
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
        cursor = db.cursor();
        sql = """INSERT INTO edxapp_csmh.export_course_content(xblock_id, type_of_xblock, title, question, choices, image_url, correct_answer, hint, problem_name, skillname) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""
        
        try:
            cursor.execute(sql, (xblock_id, type_of_xblock, title, question, choices, image_url, correct_answer, hint, problem_name, skill_name))
            db.commit()
        except Exception as e:
            print e
            db.rollback()
        finally:
            db.close()
    
    
    @XBlock.json_handler
    def check_answer(self, data, suffix=''):
        """
        Check answer for submitted response
        """
        self.attempts += 1
        response = dict(correct=False)

        ans = int(data.get('ans', 0))

        # store user response
        self.user_choice = ans

        if ans == self.correct_choice:
            self.correct = True
            response['correct'] = True
        else:
            self.correct = False
            response['correct_choice'] = False
         
        
        return response
    
    @XBlock.json_handler
    def get_question_name(self, data, suffix=''):
        """
        get question name as a hint id 
        """
        return {'response': self.question}
    
    @XBlock.json_handler
    def update_question(self, data, suffix=''):
        """
        Update all the fields:
        """    
        self.display_name=str(data['problemTitle'])
        self.problemId=data['problemId']
        self.question=data['question']
        self.choices=data['choices']
        self.correct_choice=data['correct']
        self.hint=data['hint']
        self.kc=data['kc']
        self.image_url=data['imageUrl']
        
        return {'result': 'success'}

    @XBlock.json_handler
    def get_hint(self, data, suffix=''):
        """
        Give hint for the question
        """
        self.hint_numbers += 1
        response = dict(hint=self.hint)

        return {'response': self.hint}

    @XBlock.json_handler
    def change_name(self, data, suffix=''):
        """
        Change display_name for the questions
        """
        self.display_name = String(data.get('title'))
        
        return display_name
    
    @XBlock.json_handler
    def get_default_data(self, data, suffix=''):
        """
        when mcqs_edit page is on load, get all the default data from here
        """
        
        return {'display_name': self.display_name, 'title': self.title, 'problemId': self.problemId, 'kc': self.kc, 'question': self.question, 'choices': self.choices, 'correct_choice': self.correct_choice, 'hint': self.hint, 'image_url': self.image_url}
        
    @XBlock.json_handler
    def get_xblock_id(self, data, suffix=''):
        xblock_obj = self.scope_ids.usage_id
        xblock_id= (str(xblock_obj.course_key) + '+type@' + str(xblock_obj.block_type) + '+block@' + str(xblock_obj.block_id)).replace("course", "block")
        return {"xblock_id": xblock_id, "xblock_code": str(xblock_obj.block_id).replace("course", "block") }
    
    @XBlock.json_handler
    def update_display_name(self, data, suffix=''):
        self.display_name=str(data['result'])
        return {'display_name': self.display_name}
    
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
