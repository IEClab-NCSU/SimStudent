"""
A XBlock used to server CTAT based tutors.
See http://ctat.pact.cs.cmu.edu/ for more on CTAT tutors.
"""

import re
import uuid
# import base64
import math
import json
import pkg_resources
import requests
import bleach
import urllib2
from bs4 import BeautifulSoup
import bs4

# pylint: disable=import-error
# The xBlock package are availabe in the runtime environment.
from xblock.core import XBlock
from xblock.fields import Scope, Integer, String, Float, Boolean
from xblock.fragment import Fragment
# pylint: enable=import-error
#for mysql db connection
import MySQLdb
import datetime
import time

class CTATXBlock(XBlock):
    """
    A XBlock providing CTAT tutors.
    """
    # pylint: disable=too-many-instance-attributes
    # All of the instance variables are required.

    display_name = String(
        help="Display name of the xBlock",
        default="CTAT Tutor",
        scope=Scope.content)
    # **** xBlock tag variables ****
    width = Integer(help="Width of the tutor frame.",
                    default=690, scope=Scope.content)
    height = Integer(help="Height of the tutor frame.",
                     default=550, scope=Scope.content)

    # **** Grading variables ****
    # Required accordign to EdX's documentation in order to get grading to work
    has_score = Boolean(default=True, scope=Scope.content)
    icon_class = String(default="problem", scope=Scope.content)
    score = Integer(help="Current count of correctly completed student steps",
                    scope=Scope.user_state, default=0)
    max_problem_steps = Integer(
        help="Total number of steps",
        scope=Scope.user_state, default=1)
    max_possible_score = 1  # should this be 1.0?

    def max_score(self):
        """ The maximum raw score of the problem. """
        # For some unknown reason, errors are thrown if the return value is
        # hard coded.
        return self.max_possible_score
    attempted = Boolean(help="True if at least one step has been completed",
                        scope=Scope.user_state, default=False)
    completed = Boolean(
        help="True if all of the required steps are correctly completed",
        scope=Scope.user_state, default=False)
    weight = Float(
        display_name="Problem Weight",
        help=("Defines the number of points each problem is worth. "
              "If the value is not set, the problem is worth the sum of the "
              "option point values."),
        values={"min": 0, "step": .1},
        scope=Scope.settings
    )  # weight needs to be set to something, errors will be thrown if it does
    # not exist.

    # **** Basic interface variables ****
    src = String(help="The source html file for CTAT interface.",
                 default="https://cdn.rawgit.com/CMUCTAT/CTAT/v4.0.0/Examples/FractionAddition.html",
                 scope=Scope.settings)
    brd = String(help="The behavior graph.",
                 default="https://cdn.rawgit.com/CMUCTAT/CTAT/v4.0.0/Examples/FractionAddition.brd",
                 scope=Scope.settings)

    # **** CTATConfiguration variables ****
    logging = Boolean(help="If tutor log data should be transmitted to EdX.",
                      default="True", scope=Scope.settings)

    custom_tutor_parameters = String(
        help="Extra parameters set by the author for the tutor. " +
        "This should be JSON code. See https://github.com/CMUCTAT/CTAT/" +
        "wiki/Advanced-Topics#tutor-configuration.",
        default="{}",
        scope=Scope.settings)
    # **** User Information ****
    saveandrestore = String(help="Internal data blob used by the tracer",
                            default="", scope=Scope.user_state)
    skillstring = String(help="Internal data blob used by the tracer",
                         default="", scope=Scope.user_info)

    # **** Utility functions and methods ****
    @staticmethod
    def resource_string(path):
        """ Read in the contents of a resource file. """
        data = pkg_resources.resource_string(__name__, path)
        return data.decode("utf8")

    # **** xBlock methods ****
    def student_view(self, dummy_context=None):
        """
        Create a Fragment used to display a CTAT xBlock to a student.

        Args:
          dummy_context: unused but required as a XBlock.student_view.
        Returns:
          a Fragment object containing the HTML to display.
        """
        # pylint: disable=broad-except
        # There are a fair number of exceptions raised by raise_for_status
        # and all of them are handled the same way here.
        try:
            # See if tutor interface is reachable and add mode parameter
            tutor = requests.get(self.src, params={'mode': 'XBlock'})
            tutor.raise_for_status()
        except Exception as err:
            # Return a Fragment with the error message so that authors can
            # get some relevant information as to why the tutor is not loading.
            return Fragment((u'<p style="width:{width}px;height:{height}px;' +
                             'border:2px solid red;">Error retrieving ' +
                             'specified tutor {url} ({error})</p>').format(
                                 url=self.src,
                                 error=err,
                                 height=self.height,
                                 width=self.width))
        # pylint: enable=broad-except
        # read in template html
        html = self.resource_string("static/html/ctatxblock.html")
        # fill in the template html
        frag = Fragment(html.format(
            tutor_html=tutor.url,
            width=self.width,
            height=self.height))
        # read in the template js
        config = self.resource_string("static/js/CTATConfig.js")
        # fill in the js template
        usage_id = self.scope_ids.usage_id
        sdk_usage = isinstance(usage_id, basestring)
        frag.add_javascript(config.format(
            # meta
            guid=str(uuid.uuid4()),
            student_id=self.runtime.anonymous_student_id
            if hasattr(self.runtime, 'anonymous_student_id')
            else 'bogus-sdk-id',  # conditional here in case testing in sdk
            # class
            course=unicode(usage_id.course) if not sdk_usage else usage_id,
            org=unicode(usage_id.org) if not sdk_usage else usage_id,
            run=unicode(usage_id.run) if not sdk_usage else usage_id,
            # dataset
            course_key=unicode(usage_id.course_key)
            if not sdk_usage else usage_id,
            problem_name=unicode(usage_id),
            block_type=unicode(usage_id.block_type)
            if not sdk_usage else usage_id,
            tutor_html=tutor.url,
            # runtime
            question_file=self.brd,
            logtype=self.logging,
            saved_state=self.saveandrestore,
            skills=self.skillstring,
            completed=self.completed,
            custom=self.custom_tutor_parameters  # add checks on this?
        ))
        # Add javascript initialization code
        frag.add_javascript(self.resource_string(
            "static/js/Initialize_CTATXBlock.js"))
        # Execute javascript initialization code
        frag.initialize_js('Initialize_CTATXBlock')
        return frag

    @XBlock.json_handler
    def ctat_grade(self, data, dummy_suffix=''):
        """
        Handles updating the grade based on post request from the tutor.

        Args:
          self: the CTAT XBlock.
          data: A JSON object.
          dummy_suffix: unused but required as a XBlock.json_handler.
        Returns:
          A JSON object reporting the success or failure.
        """
        self.attempted = True
        corrects = 0
        try:
            if data.get('value') is not None:
                corrects = int(data.get('value'))
                if math.isnan(corrects):
                    corrects = 0  # check for invalid value
            if data.get('max_value') is not None:
                max_val = int(data.get('max_value'))
                if not math.isnan(max_val) and max_val > 0:
                    # only update if a valid number
                    self.max_problem_steps = max_val
        except ValueError as int_err:
            return {'result': 'fail', 'error': unicode(int_err)}
        # only change score if it increases.
        # this is done because corrects should only ever increase and
        # it deals with issues EdX has with grading, in particular
        # the multiple identical database entries issue.
        if corrects > self.score:
            self.score = corrects
            self.completed = self.score >= self.max_problem_steps
            scaled = float(self.score)/float(self.max_problem_steps)
            # trying with max of 1.
            event_data = {'value': scaled, 'max_value': 1.0}
            # pylint: disable=broad-except
            # The errors that should be checked are django errors, but there
            # type is not known at this point. This exception is designed
            # partially to learn what the possible errors are.
            try:
                self.runtime.publish(self, 'grade', event_data)
            except Exception as err:
                # return with the error message for debugging purposes.
                return {'result': 'fail', 'error': unicode(err)}
            return {'result': 'success', 'finished': self.completed,
                    'score': scaled}
            # pylint: enable=broad-except
        # report a no change situation (out of order or duplicate) with the
        # current score.
        return {'result': 'no-change', 'finished': self.completed,
                'score': float(self.score)/float(self.max_problem_steps)}

    @XBlock.json_handler
    def ctat_log(self, data, dummy_suffix=''):
        """Publish log messages from a CTAT tutor to EdX log."""
        if data.get('event_type') is None or\
           data.get('action') is None or\
           data.get('message') is None:
            return {'result': 'fail',
                    'error': 'Log request message is missing required fields.'}
        # pylint: disable=broad-except
        try:
            data.pop('event_type')
            logdata = data  # assume that data is already been checked.
            #before we print, covert it to stardard XML format
            
            
            logdata['user_id'] = self.runtime.user_id
            logdata['component_id'] = unicode(self.scope_ids.usage_id)
            
            self.runtime.publish(self, "ctatlog", logdata)
            
            content = urllib2.unquote(logdata.get('message'))
            print "======="
            print logdata.get('message')
            print "======="
            print content
            
            # Start to convert the xml to json object
            soup = BeautifulSoup(content, 'html.parser')
            
            if soup.find("selection") is not None:
                print "selection has value so need database action at this time"
                sessionId = soup.find("log_action", {"auth_token": "none"})["session_id"]
                date_time = soup.find("log_action", {"auth_token": "none"})["date_time"]
                timezone = soup.find("log_action", {"auth_token": "none"})["timezone"]
                studentResponseType = "ATTEMPT"
                correctness = soup.find("action_evaluation").string
                
                hintMessage = soup.find("tutor_advice").string
                
                problemStartTime = soup.find_all("value")[1].string
               
                stepName = soup.find_all("name")[0].string
                
                # attemptAtStep later
                attemptAtStep = 1
                conditionName = soup.find("category").string
                
                selection = soup.find("selection").string
                action = soup.find("action").string
                print "selection value: "
                print selection
                inputId = ""
                for cd in soup.findAll(text=True):
                    if isinstance(cd, bs4.CData):
                        inputId = cd
                        print inputId
                        break
                #inputId = soup.find("input").string
                cf = soup.find("custom_field")
                schoolandcourse = "course-v1:UniversityX+CS101+2015_T1".split(":")[1]
                school = schoolandcourse.split("+")[0]
                course = schoolandcourse.split("+")[1] + schoolandcourse.split("+")[2]

                obj = dict()
                obj["anonStudentId"] = self.runtime.anonymous_student_id
                obj['sessionId'] = sessionId
                obj["time"] = date_time
                obj["timezone"] = timezone
                obj["studentResponseType"] = studentResponseType
                obj["studentResponseSubType"] = ""
                obj["tutorResponseType"] = "RESULT"
                obj["tutorResponseSubType"] = ""
                obj["level"] = ""
                obj["problemName"] = ""
                obj["problemView"] = ""
                obj["problemStartTime"] = problemStartTime
                obj["stepName"] = stepName
                obj["attemptAtStep"] = attemptAtStep
                obj["outcome"] = correctness
                obj["selection"] = selection
                obj["action"] = action
                obj["input"] = inputId
                obj["feedbackText"] = hintMessage
                obj["feedbackClassification"] = ""
                obj["helpLevel"] = ""
                obj["totalNumHints"] = ""
                obj["conditionName"] = conditionName
                obj["conditionType"] = ""
                obj["kc"] = ""
                obj["kcCategory"] = ""
                obj["school"] = school
                obj["class"] = course
                obj["cf"] = cf

                #now we can call the log function:

                self.get_student_id(obj)
            else:
                print "selection is none so no database action at this time"
            
            
        except KeyError as keyerr:
            return {'result': 'fail', 'error': unicode(keyerr)}
        # General mechanism to catch a very broad category of errors.
        except Exception as err:
            return {'result': 'fail', 'error': unicode(err)}
        # pylint: enable=broad-except
        return {'result': 'success'}
    
    # get_student_id function:
    
    def get_student_id(self, data, suffix=''):
        # created, state, grade, max_grade, id, student_module_id
        #5c58c732aba1b9e2e708a00c3b243de7
        
        #1. base on the anony student id, we find the user_id attribute from edxapp.student_anonymoususerid
        
        
        
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
        
        self.get_module_id(str(row[3]), xblock_id, data)
        
    #This method help us to find module_id for our XBlock
    
    def get_module_id(self, student_id, xblock_id, data):
        print '*' + student_id + '*'
        print '*' + xblock_id + '*'
        print "Here is the data obj"
        print '*input: ' + str(data['input']) + '*'
        try:
            db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp")
            db.autocommit(True)
            cursor = db.cursor()
            sql = """select * from edxapp.courseware_studentmodule where student_id=""" + student_id + """ and module_id='""" + xblock_id + """'"""
            cursor.execute(sql)
            student_module_id = cursor.fetchone()
            print "Here is student_module_id: " 
            print student_module_id[0]
            
            
        except:
            import traceback
            traceback.print_exc()
            db.rollback()
            print "Database has been rollback!!!"
        finally:
            cursor.close()
            db.close()
        self.insert_user_activities(student_module_id[0], data)
    
    def insert_user_activities(self, student_module_id, data):
        # first, collect all the information
            #obj["anonStudentId"] = self.runtime.anonymous_student_id
            # obj['sessionId'] = sessionId
            #obj["time"] = date_time
            #obj["timezone"] = timezone
            #obj["studentResponseType"] = studentResponseType
            #obj["studentResponseSubType"] = ""
            #obj["tutorResponseType"] = "RESULT"
            #obj["tutorResponseSubType"] = ""
            #obj["level"] = ""
            #obj["problemName"] = ""
            #obj["problemView"] = ""
            #obj["problemStartTime"] = problemStartTime
            #obj["stepName"] = stepName
            #obj["attemptAtStep"] = attemptAtStep
            #obj["outcome"] = correctness
            #obj["selection"] = selection
            #obj["action"] = action
            #obj["input"] = inputId
            #obj["feedbackText"] = hintMessage
            #obj["feedbackClassification"] = ""
            #obj["helpLevel"] = ""
            #obj["totalNumHints"] = ""
            #obj["conditionName"] = conditionName
            #obj["conditionType"] = ""
            #obj["kc"] = ""
            #obj["kcCategory"] = ""
            #obj["school"] = school
            #obj["class"] = course
            #obj["cf"] = cf
        
        
        correct_map = '"correct_map":{"textbox_id":{"hint":"'+ str(data['feedbackText']) + '", "hintmode": "click", "correctness":"'+ str(data['outcome']) + '", "msg": "'+ str(data['feedbackText']) + '", "answervarible": null, "npoints": null, "queuestate": null}}'
        input_state = '"mcqs_input_state": {}'
        ts = time.time()
        timestamp = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
        last_submission_time = '"last_submission_time": "' + timestamp + '"'
        attempts = '"attempts":'+ str(data['attemptAtStep']) + ''
        seed = '"seed": 1'
        done = '"done": false' 
        student_answers = '"student_answers":{"textbox_answers":"' + str(data["input"]) + '"}'
        
        question_related = '"question_details":{"display_name":"' + self.display_name + '", "problem name":"CTAT problem", "problemId": "' + str(data['sessionId']) + '", "question": "CTAT related question", "correct_answer": "N/A", "user_answer":"' + str(data['input']) + '", "kc": "' + str(data['kc']) + '", "time zone": "' + str(data['timezone']) + '", "student response type": "' + str(data['studentResponseType']) +'", "student response subtype": "N/A", "tutor response type": "RESULT","tutor response subtype": "N/A", "level": "N/A", "problem view": "1", "step name": "' + str(data['stepName']) + '", "attemp at step": "' + str(data['attemptAtStep']) + '", "selection": "' + str(data['selection']) + '", "action": "' + str(data['action']) + '", "input": "' + str(data['input']) + '", "feedback text": "' + str(data['feedbackText']) + '", "feedback classification": "' + str(data["feedbackClassification"]) + '", "help level": "' + str(data['helpLevel']) + '", "total number hints": "' + str(data['totalNumHints']) + '", "condition name": "' + str(data['conditionName']) + '", "condition type": "' + str(data['conditionType']) + '", "kc category": "' + str(data['kcCategory']) + '", "school": "' + str(data['school']) + '", "class": "' + str(data['class']) + '", "cf": "' + str(data['cf']) + '"}'

        # for state column
        state = '{' + correct_map + ',' + input_state + ',' + last_submission_time + ',' + attempts + ',' + seed + ',' + done + ',' + student_answers + ',' + question_related +  '}'
        print "state object:"
        print state
        
        # Mysql database access here:
        
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh");
        cursor = db.cursor();
        sql = """INSERT INTO edxapp_csmh.coursewarehistoryextended_studentmodulehistoryextended(state, created, student_module_id)
         VALUES ('""" + state + """','""" + timestamp + """', '""" + str(student_module_id) + """')"""
        
        try:
            cursor.execute(sql)
            db.commit()
            print "I am done with database.."
        except:
            db.rollback()
            print "database rollback."
        finally:
            db.close()
        
    
    
    def studio_view(self, dummy_context=None):
        """" Generate what is seen in the Studio view Edit dialogue. """
        # read in template
        html = self.resource_string("static/html/ctatstudio.html")
        # Make fragment and fill out template
        frag = Fragment(html.format(
            src=self.src,
            brd=self.brd,
            width=self.width,
            height=self.height,
            custom=self.custom_tutor_parameters,
            logging='checked' if self.logging else ''))
        # read in, add, and execute javascript
        studio_js = self.resource_string("static/js/ctatstudio.js")
        frag.add_javascript(unicode(studio_js))
        frag.initialize_js('CTATXBlockStudio')
        return frag

    @staticmethod
    def validate_interface(url):
        """ Validate that the passed url is a CTAT tutor interface. """
        if url is None:
            raise Exception('No html interface file specified.')
        src = bleach.clean(url.strip(), strip=True)
        tutor = requests.get(src, params={'mode': 'XBlock'})
        tutor.raise_for_status()
        if tutor.headers['Content-Type'] != 'text/html':
            raise Exception(src + ' does not appear to be a text/html file.')
        if not re.search(r'ctat\.min\.js', tutor.text, re.I):
            raise Exception(src + ' does not appear to be a CTAT interface.')
        return src

    @staticmethod
    def validate_brd(url):
        """ Validate that the passed url is a CTAT BRD file. """
        if url is None:
            raise Exception('No BRD file specified.')
        brd = bleach.clean(url.strip(), strip=True)
        tutor = requests.get(brd)
        tutor.raise_for_status()
        if not re.search(r'<stateGraph', tutor.text):
            raise Exception(brd + ' does not appear to be a proper BRD file.')
        return brd

    @staticmethod
    def validate_number(num, default):
        """ Validate that the passed string is a number. """
        if num is None:
            return default
        return int(num)

    @staticmethod
    def validate_logging(enable_logging):
        """Validate that the string is a proper value for enabling logging."""
        logging = False
        if enable_logging is not None:
            logging = bleach.clean(enable_logging, strip=True)
            if logging.lower() == "true":
                logging = True
        return logging

    @staticmethod
    def validate_custom(json_string):
        """ Validate that the string is JSON. """
        custom = ''
        if json_string is not None:
            # bleach the string
            custom = bleach.clean(json_string, strip=True)
            # attempt to parse it as json to see if it is valid.
            # relying on this to raise a ValueError if not proper json.
            json.loads(custom)
        return custom

    @XBlock.json_handler
    def studio_submit(self, data, dummy_suffix=''):
        """
        Called when submitting the form in Studio.
        This will only modify values if all of the safty checks pass.

        Args:
          self: the CTAT XBlock.
          data: a JSON object encoding the form data from
                static/html/ctatstudio.html
          dummy_suffix: unused but required as a XBlock.json_handler.
        Returns:
          A JSON object reporting the success of the operation.
        """
        # pylint: disable=broad-except
        # This uses the generic exceptions so that we do not have to
        # generate custom error classes and this handles the classes
        # of exceptions potentially raised by requests
        try:
            valid_src = self.validate_interface(data.get('src'))
            valid_brd = self.validate_brd(data.get('brd'))
            valid_width = self.validate_number(data.get('width'), self.width)
            valid_height = self.validate_number(data.get('height'),
                                                self.height)
            valid_logging = self.validate_logging(data.get('logging'))
            valid_custom = self.validate_custom(data.get('custom'))
        except Exception as err:
            return {'result': 'fail', 'error': unicode(err)}
        # pylint: enable=broad-except
        # Only update values if all checks pass
        self.src = valid_src
        self.brd = valid_brd
        self.width = valid_width
        self.height = valid_height
        self.logging = valid_logging
        self.custom_tutor_parameters = valid_custom
        return {'result': 'success'}

    @XBlock.json_handler
    def ctat_save_problem_state(self, data, dummy_suffix=''):
        """Called from CTATLMS.saveProblemState.
        This saves the current state of the tutor after each correct action.

        Args:
          self: the CTAT XBlock.
          data: A JSON object with a 'state' field with a payload of the blob
                of 64 bit encoded data that represents the current
                state of the tutor.
          dummy_suffix: unused but required as a XBlock.json_handler.
        Returns:
          A JSON object with a 'result' field with a payload indicating the
          success status.
        """
        # should add check to de-encode64 the state and see if it is
        # the expected XML
        if data.get('state') is not None:
            self.saveandrestore = bleach.clean(data.get('state'))
            return {'result': 'success'}
        return {'result': 'failure'}

    @XBlock.json_handler
    def ctat_save_skills(self, data, dummy_suffix=''):
        """Save the current skill estimates for the student.

        Args:
          self: the CTAT XBlock
          data: the JSON object with the encoded skill string
          dummy_suffix: unused but required as a XBlock.json_handler.
        Returns:
          A JSON object with a 'result' field with a payload indicating the
          success status.
        """
        if data.get('skills') is not None:
            self.skillstring = bleach.clean(data.get('skills'), strip=True)
            return {'result': 'success'}
        return {'result': 'failure'}

    @staticmethod
    def workbench_scenarios():
        """ Prescribed XBlock method for displaying this in the workbench. """
        return [
            ("CTATXBlock",
             """<vertical_demo>
                <ctatxblock width="690" height="550"/>
                </vertical_demo>
             """),
        ]
