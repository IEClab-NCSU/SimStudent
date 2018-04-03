import MySQLdb
import datetime
import time
import uuid
import pytz

def print_function(par):
    print "Hello : ", par
    return




def util_find_condition_by_course(course_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
    cursor = db.cursor()

    # we should have only one course match one condition in this table:
    # for select the same course to see if there is any other xblock type have the same id
    sql = """select condition_name from edxapp_csmh.condition_course_match where course_id = '%s'"""
    try:
        cursor.execute(sql % course_id)
        result = cursor.fetchone()

        if not cursor.rowcount:
            #print "No any condition name found in table condition_course_match."
            return "admin"
        else:
            #print "Found condition name in table condition_course_match: " + result[0]
            return result[0]
    except Exception as e:
        print "I am getting error when finding the condition name from table condition_course_match."
        print e
        db.rollback()
    finally:
        cursor.close()
        db.close()


def util_find_school_class_bypastelid(pastel_student_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
    cursor = db.cursor()
    # we should have only one course match one condition in this table:
    # for select the same course to see if there is any other xblock type have the same id
    sql = """SELECT condition, school, class from edxapp_csmh.pastel where pastel_student_id = '%s' """
    try:
        cursor.execute(sql % pastel_student_id)
        result1 = cursor.fetchone()
        if not cursor.rowcount:
            #print "No any school name found in table pastel."
            return "admin", "admin"
        else:
            #print "Found school and class name in table pastel."
            return str(result1[0]), str(result1[1]), str(result1[2])
    except Exception as e:
        print "I am getting error when finding the school and class name from table pastel."
        print e
        db.rollback()
    finally:
        cursor.close()
        db.close()



def util_save_user_activity(state, timestamp,  student_module_id):

    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
    cursor = db.cursor()
    try:
        sql = """INSERT INTO edxapp_csmh.coursewarehistoryextended_studentmodulehistoryextended(state, created, student_module_id)
                 VALUES (%s, %s, %s)"""

        cursor.execute(sql, (state, timestamp, student_module_id))
        db.commit()
        print "Database finished executing text..."
    except Exception as e:
        print e
        db.rollback()
        print "database rollback!"
    finally:
        cursor.close()
        db.close()


def util_get_module_id(student_id, xblock_id):
    """
    This method find module_id for our XBlock
    """

    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp")
    cursor = db.cursor()
    try:
        sql = """select * from edxapp.courseware_studentmodule where student_id= '%s' and module_id= '%s' """
        cursor.execute(sql % (student_id, xblock_id))
        result = cursor.fetchone()
        module_id = int(result[0])
        return module_id

    except:
        import traceback
        traceback.print_exc()
        db.rollback()
        print "Database has been rollback!!!"
    finally:
        cursor.close()
        db.close()



def util_save_module_skillname(skillname, xblock_id, location_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh")
    cursor = db.cursor()
    sql = """SELECT * From edxapp_csmh.module_skillname WHERE xblock_id = '%s'"""

    sql1 = """INSERT INTO edxapp_csmh.module_skillname(xblock_id, type, skillname, location) VALUES (%s, %s, %s, %s)"""

    sql2 = """UPDATE edxapp_csmh.module_skillname SET type = %s, skillname = %s, location = %s WHERE xblock_id = %s"""

    try:
        cursor.execute(sql % (xblock_id))
        result = cursor.fetchone()
        if not cursor.rowcount:
            cursor.execute(sql1, (xblock_id, "text", skillname, location_id))
            db.commit()
            print "Skillname has been saved in module_skillname table."
        else:
            cursor.execute(sql2, ("text", skillname, location_id, xblock_id))
            db.commit()
            print "Skillname has been updated in module_skillname table."
    except Exception as e:
        print e
        db.rollback()
        print "Database rollback when execute - save module skill name method."
    finally:
        cursor.close()
        db.close()


def util_update_xblock_for_exporter(xblock_id, course_id, section, subsection, unit, type_of_xblock, title, sub_title, text, image_url, skillname):
    # start to save the XBlock related information in the database:
    setBorderColor = 0
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
    cursor = db.cursor()
    # for select the unique xblock
    sql0 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE xblock_id = '%s'"""
    # for insert the xblock
    sql = """INSERT INTO edxapp_csmh.export_course_content_and_skill_validation(course_id, xblock_id, section, subsection, unit, type_of_xblock, title, sub_title, text, image_url, skillname) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""
    # for update the xblock
    sql1 = """UPDATE edxapp_csmh.export_course_content_and_skill_validation SET course_id = %s, section = %s, subsection = %s, unit = %s, type_of_xblock = %s, title = %s, sub_title = %s, text = %s, image_url = %s, skillname = %s where xblock_id = %s"""
    # for select the same course to see if there is any other xblock type have the same id
    sql2 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE (type_of_xblock = "MultipleChoiceQuestion" OR type_of_xblock = "TextBoxQuestion") AND course_id = %s AND skillname = %s"""
    try:
        cursor.execute(sql0 % str(xblock_id))
        result = cursor.fetchone()
        if not cursor.rowcount:
            print "No any results(paragraph) found, insert a new entry to paragraph for this xblock:"
            try:
                cursor.execute(sql, (
                course_id, xblock_id, section, subsection, unit, type_of_xblock, title, sub_title, text, image_url,
                skillname))
                db.commit()
                cursor.execute(sql2, (course_id, skillname))
                result1 = cursor.fetchone()
                if not cursor.rowcount:
                    print "No any mcqs or textbox xblocks have the same skillname"
                    setBorderColor = 1
                else:
                    print "Found mcqs or textbox with the same skillname."
                    setBorderColor = 0
            except Exception as e:
                print "I am getting error when inserting - paragraph."
                print e
                db.rollback()
        else:
            print "Found the related entry in database, update the entry in paragraph for this xblock."
            try:
                cursor.execute(sql1, (course_id, section, subsection, unit, type_of_xblock, title, sub_title, text, image_url, skillname, xblock_id))
                db.commit()
                cursor.execute(sql2, (course_id, skillname))
                result1 = cursor.fetchone()
                if not cursor.rowcount:
                    print "No any mcqs or textbox xblocks have the same skillname."
                    setBorderColor = 1
                else:
                    print "Found mcqs or textbox xblock with the same skillname."
                    setBorderColor = 0
            except Exception as e:
                print "I am getting error when updating - paragraph."
                print e
                db.rollback()
        return setBorderColor
    except Exception as e:
        print "I am getting error when selecting - paragraph."
        print e
        db.rollback()
    finally:
        cursor.close()
        db.close()




def util_delete_xblock_for_exporter(xblock_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
    cursor = db.cursor()
    sql = """DELETE FROM edxapp_csmh.export_course_content_and_skill_validation WHERE xblock_id = '%s' """
    sql1 = """DELETE FROM edxapp_csmh.module_skillname WHERE xblock_id = '%s' """
    try:
        cursor.execute(sql % (xblock_id))
        cursor.execute(sql1 % (xblock_id))
        print "The xblock has been deleted."
        db.commit()
    except Exception as e:
        print "Error happened when we tried to delete xblock in database."
        print e
        db.rollback()
    finally:
        cursor.close()
        db.close()


def util_get_probability(skillname, student_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp", charset='utf8')
    cursor = db.cursor()
    sql = """SELECT * FROM edxapp_csmh.temporary_probability where skillname = %s and student_pastel_id = %s order by id DESC limit 1;"""

    try:
        cursor.execute(sql, (skillname, student_id))
        result = cursor.fetchone()
        if not cursor.rowcount:
            print "No any results(get_probability) found."

        return result

    except Exception as e:
        print e
        db.rollback()
    finally:
        cursor.close()
        db.close()


def util_get_pastel_student_id(user_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp", charset='utf8')
    cursor = db.cursor()

    sql = """SELECT * FROM edxapp.auth_user where id = '%s' """

    try:
        cursor.execute(sql % (user_id))
        result = cursor.fetchone()
        if not cursor.rowcount:
            print "No any results(auth_student) found."
            return
        email = str(result[7])
        print "Get username and email from DB: ", str(result[4]) + ", " + str(result[7])

        sql1 = """SELECT * FROM edxapp_csmh.pastel where email = '%s'"""
        if email != "" or email is None:
            cursor.execute(sql1 % (str(email)))
            result1 = cursor.fetchone()
            if not cursor.rowcount:
                print "No any results(pastel_student_id) found."
                return None, "admin", "admin", "admin"
            print "Get pastel_student_id from DB: ", str(result1[3])
            pastel_student_id = str(result1[3])
            school = str(result1[4])
            className = str(result1[5])
            condition = str(result1[7])
            db.commit()
            cursor.close()
            return pastel_student_id, school, className, condition
    except Exception as e:
        print e
        db.rollback()
    finally:
        cursor.close()
        db.close()



def util_get_border_color(course_id, skillname):
    # start to save the XBlock related information in the database:
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
    cursor = db.cursor()
    # for select the same course to see if there is any other xblock type have the same id
    sql = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE type_of_xblock in ("TextBoxQuestion", "MultipleChoiceQuestion") AND course_id = %s AND skillname = %s"""
    HtmlSetBorderColor = 0
    try:
        skills = skillname.split(",")
        for skill in skills:
            cursor.execute(sql, (course_id, skill.strip()))
            result1 = cursor.fetchone()
            if cursor.rowcount:
                print "Found xblock with the same skillname."
                HtmlSetBorderColor = 1
                break

        return HtmlSetBorderColor
    except Exception as e:
        print "I am getting error when getting the border color from export course content and skill validation."
        print e
        db.rollback()
    finally:
        db.close()

def util_get_skillset(course_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
    cursor = db.cursor()
    # get all the skill name from table export_course_content_and_skill_validation
    sql = """SELECT distinct skillname from edxapp_csmh.export_course_content_and_skill_validation where course_id = '%s'"""

    try:
        cursor.execute(sql % course_id)
        skillset = cursor.fetchall()
        return skillset
    except Exception as e:
        print "I am getting error when inserting - assessment."
        print e
        db.rollback()
    finally:
        db.close()

#5-1a2b3c-f1fab007-20180104111952
def util_generate_session_id(student_id):
    #return uuid.uuid4().hex[:8] + '-' + uuid.uuid4().hex[:4] + '-' + uuid.uuid4().hex[:4] + '-' +
# uuid.uuid4().hex[:4] + '-' + uuid.uuid4().hex[:12]
    ts = time.time()
    timestamp = datetime.datetime.fromtimestamp(ts, pytz.timezone('US/Central')).strftime('%Y%m%d%H%M%S')
    return student_id + '-' + uuid.uuid4().hex[:8] + '-' + timestamp