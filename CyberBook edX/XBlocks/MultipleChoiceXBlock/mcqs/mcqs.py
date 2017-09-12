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
from random import randint


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
    image_size = String(default="50%", scope=Scope.content)
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
    probability = Integer(default=0, scope=Scope.user_state)
    show_onandoff = Boolean(default=True, scope=Scope.user_state)
    pastel_student_id = String(scope=Scope.user_state)
    hasBeenSent = String(default = "false", scope = Scope.user_state)
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
        self.hasBeenSent = str(data.get('hasBeenSent'))
        
    
    
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

            question_related ='"question_details":{"display_name":"' + displayName + '", "student_pastel_id":"' + str(self.pastel_student_id) + '", "problem name":"' + title + '", "problemId": "' + problemId + '", "question": "' + question + '", "choices": "' + choices + '", "user_choice":"' + userChoice + '", "skillname":"' + skillName + '", "kc": "' + skillName + '", "time zone": "US/Central", "student response type": "ATTEMPT", "student response subtype": "N/A", "tutor response type": "RESULT","tutor response subtype": "N/A", "level": "N/A", "problem view": "1", "step name": "N/A", "attemp at step": "' + str(
                    self.attempts) + '", "selection": "' + userChoice + '", "Action": "Multiple Choice", "input": "' + input + '", "feedback text": "' + feedback + '", "feedback classification": "N/A", "help level": "' + str(
                    self.hint_numbers) + '", "total number hints": "' + str(len(self.hint.split(
                    '|'))) + '", "condition name": "N/A", "condition type": "N/A", "kc category": "N/A", "school": "' + school + '", "class": "' + classname + '", "cf": "N/A"}'
        else:
            question_related = '"question_details":{"display_name":"' + displayName + '", "student_pastel_id":"' + str(self.pastel_student_id) + '", "problem name":"' + title + '", "problemId": "' + problemId + '", "question": "' + question + '", "choices": "' + choices + '", "user_choice":"' + userChoice + '","skillname":"' + skillName + '", "kc": "' + skillName + '", "time zone": "US/Central", "student response type": "HINT_REQUEST", "student response subtype": "N/A", "tutor response type": "HINT_MSG","tutor response subtype": "N/A", "level": "N/A", "problem view": "1", "step name": "N/A", "attemp at step": "' + str(
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
    def get_border_color(self, data, suffix=''):
        # start to save the XBlock related information in the database:
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
        cursor = db.cursor();
        
        course_id = str(self.scope_ids.usage_id.course_key)
        skillname = self.kc
        #for select the same course to see if there is any other xblock type have the same id
        sql2 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE type_of_xblock = "TextParagraph" AND course_id = %s AND skillname = %s"""
        try:
            cursor.execute(sql2, (course_id, skillname))
            result1 = cursor.fetchone()
            if not cursor.rowcount:
                print "No any other xblocks have the same skillname"
                HtmlSetBorderColor = 0
            else:
                print "Found xblock with the same skillname."
                HtmlSetBorderColor = 1
        except Exception as e:
            print "I am getting error when inserting - assessment."
            print e
            db.rollback()
        finally:
            db.close()
        return {"setBorderColor": HtmlSetBorderColor}
    
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
        self.image_size=data['imageSize']
        
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
        # start to save the XBlock related information in the database:
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
        cursor = db.cursor();
        #for select the unique xblock_id
        sql0 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE xblock_id = '%s'"""
        #for insert the xblock information
        sql = """INSERT INTO edxapp_csmh.export_course_content_and_skill_validation(course_id, xblock_id, type_of_xblock, title, question, choices, image_url, correct_answer, hint, problem_name, skillname) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""
        #for update the xblock information
        sql1 = """UPDATE edxapp_csmh.export_course_content_and_skill_validation SET course_id = %s, type_of_xblock = %s, title = %s, question = %s, choices = %s, image_url = %s, correct_answer = %s, hint = %s, problem_name = %s, skillname = %s where xblock_id = %s"""
        #for select the same course to see if there is any other xblock type have the same id
        sql2 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE type_of_xblock = "TextParagraph" AND course_id = %s AND skillname = %s"""
        try:
            cursor.execute(sql0 % str(unicode(self.scope_ids.usage_id)))
            result = cursor.fetchone()
            if not cursor.rowcount:
                print "No any results(assessment) found, insert a new entry to assessment for this xblock:"
                try:
                    cursor.execute(sql, (course_id, xblock_id, type_of_xblock, title, question, choices, image_url, correct_answer, hint, problem_name, skillname))
                    db.commit()
                    cursor.execute(sql2, (course_id, skillname))
                    result1 = cursor.fetchone()
                    if not cursor.rowcount:
                        print "No any other xblocks have the same skillname"
                        self.setBorderColor = 1
                    else:
                        print "Found xblock with the same skillname."
                        self.setBorderColor = 0
                except Exception as e:
                    print "I am getting error when inserting - assessment."
                    print e
                    db.rollback()
            else:
                print "Found the related entry in database, update the entry in assessment for this xblock."
                try:
                    cursor.execute(sql1, (course_id, type_of_xblock, title, question, choices, image_url, correct_answer, hint, problem_name, skillname, xblock_id))
                    db.commit()
                    cursor.execute(sql2, (course_id, skillname))
                    result1 = cursor.fetchone()
                    if not cursor.rowcount:
                        print "No any other xblocks have the same skillname"
                        self.setBorderColor = 1
                    else:
                        print "Found xblock with the same skillname."
                        self.setBorderColor = 0
                except Exception as e:
                    print "I am getting error when updating - assessment."
                    print e
                    db.rollback()
        except Exception as e:
            print "I am getting error when selecting - assessment."
            print e
            db.rollback()
        finally:
            db.close()
        
        
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
        
        return {'display_name': self.display_name, 'title': self.title, 'problemId': self.problemId, 'kc': self.kc, 'question': self.question, 'choices': self.choices, 'correct_choice': self.correct_choice, 'hint': self.hint, 'image_url': self.image_url, 'image_size': self.image_size}
        
    @XBlock.json_handler
    def get_xblock_id(self, data, suffix=''):
        xblock_obj = self.scope_ids.usage_id
        xblock_id= (str(xblock_obj.course_key) + '+type@' + str(xblock_obj.block_type) + '+block@' + str(xblock_obj.block_id)).replace("course", "block")
        return {"xblock_id": xblock_id, "xblock_code": str(xblock_obj.block_id).replace("course", "block") }
    
    @XBlock.json_handler
    def update_display_name(self, data, suffix=''):
        self.display_name=str(data['result'])
        return {'display_name': self.display_name}
    
    #temporary probability method for table: edxapp_csmh.temporary_probability
    @XBlock.json_handler
    def save_temporary_probability_method(self, data, suffix=''):
        """
            start look for the database: edxapp_csmh.temporary_probability
        """

        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
        cursor = db.cursor();
        sql = """INSERT INTO edxapp_csmh.temporary_probability(student_id, question_id, skillname) VALUES (%s, %s, %s)"""
        
        try:
            cursor.execute(sql, (self.pastel_student_id, self.problemId, self.kc))
            db.commit()
        except Exception as e:
            print "Error happened when we tried to save probability to database."
            print e
            db.rollback()
        finally:
            db.close()
    
    #temporary probability method for reading probability from table: edxapp_csmh.temporary_probability
    @XBlock.json_handler
    def get_temporary_probability_method(self, data, suffix=''):
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
        cursor = db.cursor();
        sql = """select * from edxapp_csmh.temporary_probability where question_id = %s and student_id= %s;"""
        
        try:
            cursor.execute(sql ,(self.problemId, str(self.runtime.user_id)))
            result = cursor.fetchone()
            if not cursor.rowcount:
                print "No any results(probability) found."
                return
            self.probability = result[3]
            print "Get probability from DB: ", str(result[3])
            db.commit()
        except Exception as e:
            print e
            db.rollback()
        finally:
            db.close()
    
    
    #for testing only: please delete it!
    @XBlock.json_handler
    def show_on_off_method(self, data, suffix=''):
        return {"skillname": self.kc}
    
    @XBlock.json_handler
    def change_on_off_method(self, data, suffix=''):
        self.show_onandoff = False
    
    @XBlock.json_handler
    def get_studentId_and_skillname(self, data, suffix=''):
        return {"student_id": str(self.pastel_student_id), "skillname": self.kc, "question_id": self.problemId, "correctness": self.correct}
    
    @XBlock.json_handler
    def delete_xbock(self, data, suffix=''):
        xblock_id = str(data.get("xblock_id"))
        print "Start to delete xblock_id"
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
        cursor = db.cursor();
        sql = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE xblock_id = '%s' """
        sql1 = """DELETE FROM edxapp_csmh.export_course_content_and_skill_validation WHERE id = '%s' """
        try:
            cursor.execute(sql % (xblock_id))
            result = cursor.fetchone()
            if not cursor.rowcount:
                print "No any result(xblock_id) found."
            else:
                xblock_table_id = str(result[0])
                cursor.execute(sql1 % (xblock_table_id))
            db.commit()
        except Exception as e:
            print "Error happened when we tried to delete xblock in database."
            print e
            db.rollback()
        finally:
            db.close()
        print "End with deletion"
    
    
    @XBlock.json_handler
    def get_probability(self, data, suffix=''):
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp", charset='utf8');
        cursor = db.cursor();
        sql = """SELECT * FROM edxapp_csmh.temporary_probability where skillname = %s and student_id = %s order by id DESC limit 1;"""

        try:
            cursor.execute(sql ,(str(data.get('skillname')), str(data.get('student_id'))))
            result = cursor.fetchone()
            if not cursor.rowcount:
                print "No any results(get_probability) found."
                return
            
            return {'probability': str(result[5])}

            db.commit()
        except Exception as e:
            print e
            db.rollback()
        finally:
            db.close()
    
    @XBlock.json_handler
    def get_pastel_student_id(self, data, suffix=''):
        if self.pastel_student_id == '' or self.pastel_student_id is None:
            db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp", charset='utf8');
            cursor = db.cursor();
            username = ""
            email = ""

            sql = """SELECT * FROM edxapp.auth_user where id = %s"""

            try:
                cursor.execute(sql ,(str(self.runtime.user_id)))
                result = cursor.fetchone()
                if not cursor.rowcount:
                    print "No any results(auth_student) found."
                    return
                username = str(result[4])
                email = str(result[7])
                print "Get username and email from DB: ", str(result[4]) + ", " + str(result[7])

                db.commit()
            except Exception as e:
                print e
                db.rollback()
            finally:
                db.close()

            db1 = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
            cursor1 = db1.cursor();
            
            sql1 = """SELECT * FROM edxapp_csmh.pastel where name = %s and email = %s"""
            try:
                if email != "":
                    cursor1.execute(sql1, (str(username), str(email)))
                    result1 = cursor1.fetchone()
                    if not cursor1.rowcount:
                        print "No any results(pastel_student_id) found."
                        return
                    print "Get pastel_student_id from DB: ", str(result1[3])
                    self.pastel_student_id = str(result1[3])
                    db1.commit()
            except Exception as e:
                print e
                db1.rollback()
            finally:
                db1.close()
            
        return {'pastel_student_id': self.pastel_student_id, 'hasBeenSent': self.hasBeenSent}
    
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
