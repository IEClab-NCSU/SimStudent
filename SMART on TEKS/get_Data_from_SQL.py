"""
Retrieve Contents of the course
from MySQL database
"""

from sshtunnel import SSHTunnelForwarder
import MySQLdb as db


def create_connection():

    host = '127.0.0.1'
    localhost = '127.0.0.1'
    ssh_username = 'vagrant'
    ssh_private_key = '/Users/simstudent/.vagrant.d/boxes/' \
                      'eucalyptus-devstack-2016-09-01/0/virtualbox/vagrant_private_key'

    # database variables
    user = 'root'
    password = ''
    database = 'edxapp_csmh'

    with SSHTunnelForwarder(
            (host, 2222),
            ssh_username=ssh_username,
            ssh_private_key=ssh_private_key,
            remote_bind_address=(localhost, 3306)
    ) as server:
        conn = db.connect(host=localhost,
                          port=server.local_bind_port,
                          user=user,
                          passwd=password,
                          db=database)

        """
        # For Kenya
    
        conn = db.connect(host="165.91.232.82",
                             port=8002,
                             user="root",
                             passwd="",
                             db=database)
        """
        result = run_query(conn)
        conn.close()
        return result


def run_query(conn):

    cur = conn.cursor()
    cur.execute("SELECT * FROM "
                "export_course_content_and_skill_validation;")

    result = cur.fetchall()

    return result


def get_content():

    result = create_connection()

    questions = []
    paragraphs = []

    for row in result:
        if row[1] == 'course-v1:University+CS101+2015_T1':

            if row[6] == 'TextParagraph':
                paragraphs.append((row[0], row[9]))
            else:
                questions.append((row[0], row[10]))

    return paragraphs, questions
