import MySQLdb
import datetime
import time

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
        sql = """select * from edxapp.courseware_studentmodule where student_id=""" + str(student_id) + """ and module_id='""" + str(xblock_id) + """'"""
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


def util_save_user_activity(state, timestamp,  student_module_id):
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


def util_save_module_skillname(skillname, xblock_id, location_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
    cursor = db.cursor();
    sql = """INSERT INTO edxapp_csmh.module_skillname(xblock_id, type, skillname, location) VALUES (%s, %s, %s, %s)"""

    try:
        cursor.execute(sql, (xblock_id, "mcqs", skillname, location_id))
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
        return {"exception": "this skillname didn't have any paragraph match."}

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

    return match_result


def util_update_xblock_for_exporter(xblock_id, course_id, section, subsection, unit, type_of_xblock, title, question, choices, image_url, correct_answer, hint, problem_name, skillname):
    # start to save the XBlock related information in the database:
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
    cursor = db.cursor();
    # for select the unique xblock_id
    sql0 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE xblock_id = '%s'"""
    # for insert the xblock information
    sql = """INSERT INTO edxapp_csmh.export_course_content_and_skill_validation(course_id, xblock_id, section, subsection, unit, type_of_xblock, title, question, choices, image_url, correct_answer, hint, problem_name, skillname) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""
    # for update the xblock information
    sql1 = """UPDATE edxapp_csmh.export_course_content_and_skill_validation SET course_id = %s, section = %s, subsection = %s, unit = %s, type_of_xblock = %s, title = %s, question = %s, choices = %s, image_url = %s, correct_answer = %s, hint = %s, problem_name = %s, skillname = %s where xblock_id = %s"""
    # for select the same course to see if there is any other xblock type have the same id
    sql2 = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE type_of_xblock = "TextParagraph" AND course_id = %s AND skillname = %s"""

    try:
        cursor.execute(sql0 % xblock_id)
        result = cursor.fetchone()
        if not cursor.rowcount:
            print "No any results(assessment) found, insert a new entry to assessment for this xblock:"
            try:
                cursor.execute(sql, (
                course_id, xblock_id, section, subsection, unit, type_of_xblock, title, question, choices, image_url,
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
                course_id, section, subsection, unit, type_of_xblock, title, question, choices, image_url,
                correct_answer, hint, problem_name, skillname, xblock_id))
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


def util_get_pastel_student_id(user_id):
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp", charset='utf8');
    cursor = db.cursor();

    email = ""

    sql = """SELECT * FROM edxapp.auth_user where id = '%s' """

    try:
        cursor.execute(sql % (user_id))
        result = cursor.fetchone()
        if not cursor.rowcount:
            print "No any results(auth_student) found."
            return

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

    sql1 = """SELECT * FROM edxapp_csmh.pastel where email = '%s'"""
    try:
        if email != "":
            cursor1.execute(sql1 % (str(email)))
            result1 = cursor1.fetchone()
            if not cursor1.rowcount:
                print "No any results(pastel_student_id) found."
                return
            print "Get pastel_student_id from DB: ", str(result1[3])
            pastel_student_id = str(result1[3])
            db1.commit()
            return pastel_student_id
    except Exception as e:
        print e
        db1.rollback()
    finally:
        db1.close()


def util_get_skill_mapping(assessment_id):
    # We don't recognize this key
    # start saving it in the database, target table: skill_mapping
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
    cursor = db.cursor();
    sql = """SELECT location from edxapp_csmh.skill_mapping where assessment_id = '%s';"""

    try:
        cursor.execute(sql % assessment_id)
        result = cursor.fetchone()
        if not cursor.rowcount:
            print "No results found"
            return {"result": "No results found."}
        url = result[0].split("$")
        print "Got skill name from DB."
        return url
    except Exception as e:
        print e
        db.rollback()
        print "Database rollback!"
    finally:
        db.close()


def util_get_border_color(course_id, skillname):
    # start to save the XBlock related information in the database:
    db = MySQLdb.connect("127.0.0.1", "root", "", "edxapp_csmh", charset='utf8');
    cursor = db.cursor();
    # for select the same course to see if there is any other xblock type have the same id
    sql = """SELECT * FROM edxapp_csmh.export_course_content_and_skill_validation WHERE type_of_xblock = "TextParagraph" AND course_id = %s AND skillname = %s"""
    try:
        cursor.execute(sql, (course_id, skillname))
        result1 = cursor.fetchone()
        if not cursor.rowcount:
            print "No any other xblocks have the same skillname"
            HtmlSetBorderColor = 0
        else:
            print "Found xblock with the same skillname."
            HtmlSetBorderColor = 1

        return HtmlSetBorderColor
    except Exception as e:
        print "I am getting error when inserting - assessment."
        print e
        db.rollback()
    finally:
        db.close()