""" An XBlock for Text """

import pkg_resources
from utils import load_resource, render_template
from django.template import Template, Context

from xblock.core import XBlock
from xblock.fields import Scope, Integer, String, List, Boolean
from xblock.fragment import Fragment
from xblock.validation import ValidationMessage
from xblockutils.studio_editable import StudioEditableXBlockMixin
#for mysql db connection
import MySQLdb
import datetime
import time


class TextXBlock(XBlock, StudioEditableXBlockMixin):
    """
    Multiple Choice Question XBlock
    """
    display_name = String(default='Text')
    block_name = String(default='Text')
    #editable_fields = ('title', 'question', 'choices', 'correct_choice', 'hint')
    
    title = String(
        display_name='Problem title:',
        default='Enter a problem title here',
        scope=Scope.content, help='Problem name'
    )
    
    problemId = String(
        display_name='default problem id',
        default='Enter a problem id here',
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
        default='DEFAULT',
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
    user_choice = Integer(scope=Scope.user_state, help='Index of choice selected by User')
    correct = Boolean(default=False, scope=Scope.user_state, help='User selection is correct or not')
    
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
        #frag.add_css(self.resource_string("static/css/font-awesome/css/font-awesome.min.css"))
        frag.add_javascript(self.resource_string("static/js/src/text.js"))
        frag.initialize_js('TextXBlockInitView')
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
        
        html = Template(self.resource_string("static/html/text_edit.html")).render(Context(context))
        frag = Fragment(html)
        frag.add_javascript(self.resource_string("static/js/src/text_edit.js"))
        frag.initialize_js('TextXBlockInitStudio')
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
    
    #This method help us to find module_id for our XBlock
    def get_module_id(self, student_id, xblock_id, hintCount, clicktype):
        print '*' + student_id + '*'
        print '*' + xblock_id + '*'
        print '*' + clicktype + '*'
        try:
            db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp")
            db.autocommit(True)
            cursor = db.cursor()
            sql = """select * from edxapp.courseware_studentmodule where student_id=""" + student_id + """ and module_id='""" + xblock_id + """'"""
            cursor.execute(sql)
            student_module_id = cursor.fetchone()
            print student_module_id[0]
            
        except:
            import traceback
            traceback.print_exc()
            db.rollback()
            print "Database has been rollback!!!"
        finally:
            cursor.close()
            db.close()
        self.insert_user_activities(student_module_id[0], hintCount, clicktype)
        
    #This method help us to store the user activities in OpenEdx database:
    def insert_user_activities(self, student_module_id, hintCount, clicktype):
        # first, collect all the information
        correct_map = '"correct_map":{"mcqs_id":{"hint":"'+ self.hint + '", "hintmode": "click", "correctness":"'+ str(self.correct) + '", "msg": "'+ self.hint.split('|')[hintCount] + '", "answervarible": null, "npoints": null, "queuestate": null}}'
        input_state = '"mcqs_input_state": {}'
        ts = time.time()
        timestamp = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
        last_submission_time = '"last_submission_time": "' + timestamp + '"'
        attempts = '"attempts":'+ str(self.attempts) + ''
        seed = '"seed": 1'
        done = '"done": true' 
        student_answers = '"student_answers":{"mcqs_answers":"' + str(self.user_choice) + '"}'
        
        
        if clicktype == 'checkbutton':
            question_related = '"question_details":{"display_name":"' + self.display_name + '", "problem name":"' + self.title + '", "problemId": "' + self.problemId + '", "question": "' + self.question + '", "choices": "' + ' '.join(self.choices) + '", "user_choice":"' + str(self.user_choice) + '", "kc": "' + self.kc + '", "time zone": "US/Central", "student response type": "ATTEMPT", "student response subtype": "N/A", "tutor response type": "RESULT","tutor response subtype": "N/A", "level": "N/A", "problem view": "1", "step name": "N/A", "attemp at step": "' + str(self.attempts) + '", "selection": "' + str(self.user_choice) + '", "Action": "Multiple Choice", "input": "' + self.choices[self.user_choice - 1] + '", "feedback text": "' + self.hint.split('|')[hintCount] + '", "feedback classification": "N/A", "help level": "' + str(self.hint_numbers) + '", "total number hints": "' + str(len(self.hint.split('|'))) + '", "condition name": "N/A", "condition type": "N/A", "kc category": "N/A", "school": "N/A", "class": "N/A", "cf": "N/A"}'
        else:
            question_related = '"question_details":{"display_name":"' + self.display_name + '", "problem name":"' + self.title + '", "problemId": "' + self.problemId + '", "question": "' + self.question + '", "choices": "' + ' '.join(self.choices) + '", "user_choice":"' + str(self.user_choice) + '", "kc": "' + self.kc + '", "time zone": "US/Central", "student response type": "HINT_REQUEST", "student response subtype": "N/A", "tutor response type": "HINT_MSG","tutor response subtype": "N/A", "level": "N/A", "problem view": "1", "step name": "N/A", "attemp at step": "' + str(self.attempts) + '", "selection": "", "Action": "", "input": "", "feedback text": "' + self.hint.split('|')[hintCount] + '", "feedback classification": "N/A", "help level": "' + str(self.hint_numbers) + '", "total number hints": "' + str(len(self.hint.split('|'))) + '", "condition name": "N/A", "condition type": "N/A", "kc category": "N/A", "school": "N/A", "class": "N/A", "cf": "N/A"}'
        
        # for state column
        state = '{' + correct_map + ',' + input_state + ',' + last_submission_time + ',' + attempts + ',' + seed + ',' + done + ',' + student_answers + ',' + question_related +  '}'
        
        
        # Mysql database access here:
        
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh");
        cursor = db.cursor();
        sql = """INSERT INTO edxapp_csmh.coursewarehistoryextended_studentmodulehistoryextended(state, created, student_module_id)
         VALUES ('""" + state + """','""" + timestamp + """', '""" + str(student_module_id) + """')"""
        
        try:
            cursor.execute(sql)
            db.commit()
        except:
            db.rollback()
        finally:
            db.close()
        
        print state
        
    @XBlock.json_handler
    def get_status_when_refresh(self, data, suffix=''):
        return {"countTimes": self.count_times, "correctness": self.correct, "userChoice": self.user_choice}
    
    @XBlock.json_handler
    def set_status_when_refresh(self, data, suffix=''):
        
        self.count_times = self.count_times + 1
    
    #Get anonymous student id
    @XBlock.json_handler
    def get_student_id(self, data, suffix=''):
        # created, state, grade, max_grade, id, student_module_id
        #5c58c732aba1b9e2e708a00c3b243de7
        
        #1. base on the anony student id, we find the user_id attribute from edxapp.student_anonymoususerid
        
        print str(data.get('type'))   
        ts = time.time()
        timestamp = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
        hintCount = int(data.get('hintCount'))
        print hintCount
        
        try:
            db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp")
            db.autocommit(True)
            cursor = db.cursor()
            sql = """select * from edxapp.student_anonymoususerid where anonymous_user_id='""" + self.runtime.anonymous_student_id + """'"""
            cursor.execute(sql)
            row = cursor.fetchone()
            
            #2. base on the student_id and xblock id we can find the student_module_id
            # table: edxapp.courseware_studentmodule
            print str(row[3])

            #self.scopr_ids.usage_id: {'block_type': u'mcqs', 'block_id': u'3b74606022e047539c790c57a056c61d', 'course_key': CourseLocator(u'University1', u'Ts1', u'2014_T1', None, None)}

            xblock_obj = self.scope_ids.usage_id
            xblock_id= (str(xblock_obj.course_key) + '+type@' + str(xblock_obj.block_type) + '+block@' + str(xblock_obj.block_id)).replace("course", "block")
            print xblock_id

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
            self.get_module_id(str(row[3]), xblock_id, hintCount, str(data.get('type')))
        
        return {'user': 'something'} 
        
    
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
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh");
        cursor = db.cursor();
        sql = """INSERT INTO edxapp_csmh.module_skillname(xblock_id, type, skillname, location) VALUES (%s, %s, %s, %s)"""

        try:
            cursor.execute(sql,(xblock_id, "text", skillname, location_id))
            db.commit()
                
            print "Skillname has been saved in module_skillname table."
        except Exception as e:
            print e
            db.rollback()
                
            print "Database rollback!"
        finally:
            db.close()
        return {"skillname": skillname, "xblock_id": xblock_id, "location_id": url, "paragraph_id": paragraph_id, "course_id": course_id}
        
    
    
    @XBlock.json_handler
    def check_answer(self, data, suffix=''):
        """
        Check answer for submitted response
        """
        self.attempts = self.attempts+1
        response = dict(correct=False)

        ans = int(data.get('ans', 0))

        # store user response
        self.user_choice = ans

        if ans == self.correct_choice:
            self.correct = True
            response['correct'] = True
        else:
            self.correct = False
            response['correct_choice'] = self.correct_choice
         
        
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
        sql2 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE (type_of_xblock = "MultipleChoiceQuestion" OR type_of_xblock = "TextBoxQuestion") AND course_id = %s AND skillname = %s"""
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
        self.kc=data['kc']
        self.text_content=data['textContent']
        self.text_title=data['textTitle']
        self.text_sub_title=data['textSubTitle']
        self.image_url = data['image_url']
        
        course_id = str(self.scope_ids.usage_id.course_key)
        xblock_id = str(unicode(self.scope_ids.usage_id))
        type_of_xblock = "TextParagraph"
        title = data['textTitle']
        sub_title = data['textSubTitle']
        text = data['textContent']
        image_url = str(self.image_url)
        skillname = str(self.kc)
        # start to save the XBlock related information in the database:
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
        cursor = db.cursor();
        #for select the unique xblock
        sql0 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE xblock_id = '%s'"""
        #for insert the xblock
        sql = """INSERT INTO edxapp_csmh.export_course_content_and_skill_validation(course_id, xblock_id, type_of_xblock, title, sub_title, text, image_url, skillname) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"""
        #for update the xblock
        sql1 = """UPDATE edxapp_csmh.export_course_content_and_skill_validation SET course_id = %s, type_of_xblock = %s, title = %s, sub_title = %s, text = %s, image_url = %s, skillname = %s where xblock_id = %s"""
        #for select the same course to see if there is any other xblock type have the same id
        sql2 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE (type_of_xblock = "MultipleChoiceQuestion" OR type_of_xblock = "TextBoxQuestion") AND course_id = %s AND skillname = %s"""
        try:
            cursor.execute(sql0 % str(unicode(self.scope_ids.usage_id)))
            result = cursor.fetchone()
            if not cursor.rowcount:
                print "No any results(paragraph) found, insert a new entry to paragraph for this xblock:"
                try:
                    cursor.execute(sql, (course_id, xblock_id, type_of_xblock, title, sub_title, text, image_url, skillname))
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
                    print "I am getting error when inserting - paragraph."
                    print e
                    db.rollback()
            else:
                print "Found the related entry in database, update the entry in paragraph for this xblock."
                try:
                    cursor.execute(sql1, (course_id, type_of_xblock, title, sub_title, text, image_url, skillname, xblock_id))
                    db.commit()
                    cursor.execute(sql2, (course_id, skillname))
                    result1 = cursor.fetchone()
                    if not cursor.rowcount:
                        print "No any other xblocks have the same skillname."
                        self.setBorderColor = 1
                    else:
                        print "Found xblock with the same skillname."
                        self.setBorderColor = 0
                except Exception as e:
                    print "I am getting error when updating - paragraph."
                    print e
                    db.rollback()
        except Exception as e:
            print "I am getting error when selecting - paragraph."
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
        self.hint_numbers = self.hint_numbers + 1
        response = dict(hint=self.hint)

        return {'response': self.hint}

    @XBlock.json_handler
    def change_name(self, data, suffix=''):
        """
        Change display_name for the questions
        """
        self.display_name = String(data.get('title'));
        
        return display_name
    
    @XBlock.json_handler
    def get_default_data(self, data, suffix=''):
        """
        when mcqs_edit page is on load, get all the default data from here
        """
        
        return {'display_name': self.display_name, 'title': self.title,  'kc': self.kc, 'text_title': self.text_title, 'text_content': self.text_content, 'text_sub_title': self.text_sub_title, 'image_url': self.image_url}
    
    
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
    def get_xblock_id(self, data, suffix=''):
        xblock_obj = self.scope_ids.usage_id
        xblock_id= (str(xblock_obj.course_key) + '+type@' + str(xblock_obj.block_type) + '+block@' + str(xblock_obj.block_id)).replace("course", "block")
        return {"xblock_id": xblock_id, "xblock_code": str(xblock_obj.block_id).replace("course", "block") }
    
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
