import MySQLdb
import datetime
import time
import uuid
import pytz
import json
#from DBUtils.PooledDB import PooledDB

def print_function(par):
    print "Hello : ", par
    return


def util_get_module_id(student_id, xblock_id):
    """
    This method help us to find module_id for our XBlock
    """

    try:
        db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp")

        cursor = db.cursor()
        sql = """select * from edxapp.courseware_studentmodule where student_id= %s and module_id= %s """
        cursor.execute(sql, (str(student_id), str(xblock_id) ))
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


def util_save_user_activity(state, timestamp, student_module_id, section, subsection, unit, problemId):
    # Mysql database access here:
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
    cursor = db.cursor()

    error_flag = False

    try:
        json_object = json.loads(state, strict=False)

    except Exception as e:
        error_flag = True
        print "database rollback and start insert error messages for textbox..."
        print e
        error_message = '{"error_message": "' + str(e) + '", ' \
                        '"section": "' + section + '", ' \
                        '"subsection": "' + subsection + '", ' \
                        '"unit": "' + unit + '", ' \
                        '"problemId": "' + problemId + '"}'
        try:
            sql1 = """INSERT INTO edxapp_csmh.coursewarehistoryextended_studentmodulehistoryextended(state, created, student_module_id)
                                     VALUES (%s, %s, %s)"""

            cursor.execute(sql1, (error_message, timestamp, str(student_module_id)))
            db.commit()
        except Exception as e:
            print e
            db.rollback()

    if error_flag is False:
        try:
            sql = """INSERT INTO edxapp_csmh.coursewarehistoryextended_studentmodulehistoryextended(state, created, student_module_id)
                     VALUES (%s, %s, %s)"""

            cursor.execute(sql, (state, timestamp, str(student_module_id)))
            db.commit()
        except Exception as e:
            print e
            db.rollback()

    cursor.close()
    db.close()





def util_update_xblock_for_exporter(xblock_id, course_id, section, subsection, unit, type_of_xblock, title, question,image_url, correct_answer, hint, problem_name, skillname):
    ## start from here:
    # start to save the XBlock related information in the database:
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
    cursor = db.cursor();
    # for select the unique xblock_id
    sql0 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE xblock_id = '%s'"""
    # for insert the xblock information
    sql = """INSERT INTO edxapp_csmh.export_course_content_and_skill_validation(course_id, xblock_id, section, subsection, unit, type_of_xblock, title, question, image_url, correct_answer, hint, problem_name, skillname) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""
    # for update the xblock information
    sql1 = """UPDATE edxapp_csmh.export_course_content_and_skill_validation SET course_id = %s, section = %s, subsection = %s, unit = %s, title = %s, question = %s, image_url = %s, correct_answer = %s, hint = %s, problem_name = %s, skillname = %s where xblock_id = %s"""
    # for select the same course to see if there is any other xblock type have the same id
    sql2 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE (type_of_xblock = 'TextParagraph' or type_of_xblock = 'VideoXBlock') AND course_id = %s AND skillname = %s"""
    try:
        cursor.execute(sql0 % str(xblock_id))
        result = cursor.fetchone()
        if not cursor.rowcount:
            print "No any results(assessment) found, insert a new entry to assessment for textbox xblock:"
            try:
                cursor.execute(sql, (
                course_id, xblock_id, section, subsection, unit, type_of_xblock, title, question, image_url,
                correct_answer, hint, problem_name, skillname))
                db.commit()
                cursor.execute(sql2, (course_id, skillname))
                result1 = cursor.fetchone()
                if not cursor.rowcount:
                    print "No any other xblocks have the same skillname"
                    setBorderColor = 1
                else:
                    print "Found xblock with the same skillname."
                    setBorderColor = 0
            except Exception as e:
                print "I am getting error when inserting - assessment."
                print e
                db.rollback()
        else:
            print "Found the related entry in database, update the entry in assessment for this xblock."
            try:
                cursor.execute(sql1, (
                course_id, section, subsection, unit, title, question, image_url, correct_answer, hint, problem_name,
                skillname, xblock_id))
                db.commit()
                cursor.execute(sql2, (course_id, skillname))
                result1 = cursor.fetchone()
                if not cursor.rowcount:
                    print "No any other xblocks have the same skillname"
                    setBorderColor = 1
                else:
                    print "Found xblock with the same skillname."
                    setBorderColor = 0
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
        return setBorderColor



def util_delete_xblock_for_exporter(xblock_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
    cursor = db.cursor();
    sql = """DELETE FROM edxapp_csmh.export_course_content_and_skill_validation WHERE xblock_id = '%s' """
    sql1 = """DELETE FROM edxapp_csmh.module_skillname WHERE xblock_id = '%s' """
    try:
        cursor.execute(sql % (xblock_id))
        cursor.execute(sql1 % (xblock_id))
        db.commit()
    except Exception as e:
        print "Error happened when we tried to delete xblock in database."
        print e
        db.rollback()
    finally:
        db.close()


def util_get_probability(skillname, student_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp", charset='utf8');
    cursor = db.cursor();
    sql = """SELECT * FROM edxapp_csmh.temporary_probability where skillname = %s and student_pastel_id = %s order by id DESC limit 1;"""

    try:
        cursor.execute(sql, (skillname, student_id))
        result = cursor.fetchone()
        if not cursor.rowcount:
            print "No any results(get_probability) found."

        return result
        db.commit()
    except Exception as e:
        print e
        db.rollback()
    finally:
        db.close()


def util_get_pastel_student_id(user_id, course_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp", charset='utf8')
    cursor = db.cursor()

    sql = """SELECT * FROM edxapp.auth_user where id = '%s' """

    try:
        cursor.execute(sql % (user_id))
        result = cursor.fetchone()
        if not cursor.rowcount:
            print "No any results(auth_student) found."
            return None, "admin", "admin", "admin", "1", "1"

        email = str(result[7])
        print "Get username and email from DB: ", str(result[4]) + ", " + str(result[7])
        sql1 = """SELECT * FROM edxapp_csmh.pastel where email = '%s' and course_id = '%s'"""
        sql2 = """SELECT * FROM edxapp_csmh.condition_course_match where condition_name= '%s' and course_id= '%s'"""

        cursor.execute(sql1 % (email, course_id))
        result1 = cursor.fetchone()
        if not cursor.rowcount:
            print "No any results(pastel_student_id) found."
            return None, "admin", "admin", "admin", "1", "1"
        else:
            print "Get pastel_student_id from DB: ", str(result1[3])
            pastel_student_id = str(result1[3])
            school = str(result1[4])
            className = str(result1[5])
            condition = str(result1[7])

            cursor.execute(sql2 % (condition, course_id))
            result2 = cursor.fetchone()
            enable_dynamic = str(result2[3])
            enable_hiding = str(result2[4])

            db.commit()
            cursor.close()

            return pastel_student_id, school, className, condition, enable_dynamic, enable_hiding
    except Exception as e:
        print e
        db.rollback()
    finally:
        db.close()


def util_save_module_skillname(skillname, xblock_id, location_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
    cursor = db.cursor()
    sql = """SELECT * From edxapp_csmh.module_skillname WHERE xblock_id = '%s'"""
    sql1 = """INSERT INTO edxapp_csmh.module_skillname(xblock_id, type, skillname, location) VALUES (%s, %s, %s, %s)"""
    sql2 = """UPDATE edxapp_csmh.module_skillname SET type = %s, skillname = %s, location = %s WHERE xblock_id = %s"""

    try:
        cursor.execute(sql % (xblock_id))
        result = cursor.fetchone()
        if not cursor.rowcount:
            cursor.execute(sql1, (xblock_id, "textbox", skillname, location_id))
            db.commit()
            print "Skillname has been saved in module_skillname table."
        else:
            cursor.execute(sql2, ("textbox", skillname, location_id, xblock_id))
            db.commit()
    except Exception as e:
        print e
        db.rollback()
        print "Database rollback!"


def util_get_skill_mapping(xblock_id, skillname):
    # We don't recognize this key
    # start saving it in the database, target table: skill_mapping
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
    cursor = db.cursor();
    sql3 = """select location from edxapp_csmh.module_skillname where id = (select max(id) from edxapp_csmh.module_skillname where type in ("text", "video") and skillname REGEXP %s and id< (select id from edxapp_csmh.module_skillname where xblock_id=%s));"""

    try:
        rskill = ',' + skillname + ',|,' + skillname + '|^' + skillname + '$|^' + skillname + ','
        cursor.execute(sql3, (rskill, xblock_id))
        result = cursor.fetchone()
        if not cursor.rowcount:
            print "No results found"
        else:
            url = result[0].split("$")
            return url
    except Exception as e:
        print e
        db.rollback()
        print "this skillname didn't have any paragraph matched."
        return {"exception": "this skillname didn't have any paragraph matched."}
    finally:
        db.close()


def util_get_border_color(course_id, skillname):
    # start to save the XBlock related information in the database:
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
    cursor = db.cursor();
    # for select the same course to see if there is any other xblock type have the same id
    sql = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE type_of_xblock in ("TextParagraph", "VideoXBlock") AND course_id = %s AND skillname REGEXP %s"""
    try:
        rskill = ',' + skillname + ',|,' + skillname + '|^' + skillname + '$|^' + skillname + ','
        cursor.execute(sql, (course_id, rskill))
        result1 = cursor.fetchone()
        if not cursor.rowcount:
            print "No any other xblocks have the same skillname - textbox."
            HtmlSetBorderColor = 0
        else:
            print "Found xblock with the same skillname - textbox."
            HtmlSetBorderColor = 1

        return HtmlSetBorderColor
    except Exception as e:
        print "I am getting error when inserting - assessment."
        print e
        db.rollback()
    finally:
        db.close()


def util_find_condition_by_course(course_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8')
    cursor = db.cursor()

    # we should have only one course match one condition in this table:
    # for select the same course to see if there is any other xblock type have the same id
    sql = "select condition_name from edxapp_csmh.condition_course_match where course_id = '%s' " % (course_id)
    try:
        cursor.execute(sql)
        result = cursor.fetchone()
        print "result length is " + str(len(result))
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
        db.close()


def util_find_school_class_bypastelid(pastel_student_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
    cursor = db.cursor();
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
        db.close()


#5-1a2b3c-f1fab007-20180104111952
def util_generate_session_id(student_id):
    #return uuid.uuid4().hex[:8] + '-' + uuid.uuid4().hex[:4] + '-' + uuid.uuid4().hex[:4] + '-' +
# uuid.uuid4().hex[:4] + '-' + uuid.uuid4().hex[:12]
    ts = time.time()
    timestamp = datetime.datetime.fromtimestamp(ts, pytz.timezone('US/Central')).strftime('%Y%m%d%H%M%S')
    return student_id + '-' + uuid.uuid4().hex[:8] + '-' + timestamp


