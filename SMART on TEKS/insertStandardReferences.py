"""
Insert TEKS reference into the
MySQL Database
"""

from sshtunnel import SSHTunnelForwarder
import MySQLdb as db


def create_connection(paragraphs_to_teks, questions_to_teks):
    host = '127.0.0.1'
    localhost = '127.0.0.1'
    ssh_username = 'vagrant'
    ssh_private_key = '/Users/simstudent/.vagrant.d/boxes' \
                      '/eucalyptus-devstack-2016-09-01/0/virtualbox/vagrant_private_key'

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
        For Kenya

        db = MySQLdb.connect(host="165.91.232.82",
                             port=8002,
                             user="root",
                             passwd="")
        """

        run_query(conn, paragraphs_to_teks, questions_to_teks)

        conn.close()


def run_query(conn, paragraphs_to_teks, questions_to_teks):

    cur = conn.cursor()

    for xblock in paragraphs_to_teks:

        teks = xblock[4]
        cur.execute('UPDATE edxapp_csmh.export_course_content_and_skill_validation '
                    'SET TEKS_reference = %s '
                    'WHERE id = %s;', (teks, xblock[0]))

        conn.commit()

    for xblock in questions_to_teks:
        teks = xblock[4]
        cur.execute('UPDATE edxapp_csmh.export_course_content_and_skill_validation '
                    'SET TEKS_reference = %s '
                    'WHERE id = %s;', (teks, xblock[0]))

        conn.commit()


def insert_TEKS(paragraphs_to_teks, questions_to_teks):
    create_connection(paragraphs_to_teks, questions_to_teks)
