************************* Introduction to APLUS_Woz ************************************

APLUS_Woz is a web based chat application where human tutors (middle grade students) can teach SimStudent how to solve algebraic equations by 
















**************************** Installation Guide for APLUS_Woz **************************

Pre requirements:

1. Python > 3.5 and pip3 -> Useful link for installing python > 3 in Mac along with pip: https://docs.python-guide.org/starting/install3/osx/

2. Install VirtualEnvironment -> Useful link -> https://packaging.python.org/guides/installing-using-pip-and-virtual-environments/

3. Postgresql database

4. redis-server

Once you have all the requirements in hand, do the following:

1. Download APLUS_Woz code.

2. Run redis-server 

3. Create postgresql database through the psql terminal and give the information of your database in the settings.py file. The directory of this file is APLUS_Woz/SimStudent/settings.py. You can also create a database using the information in this file. Its totally upto your preference.

4. Create virtual environment with your preferred name and source it.

5. Go to the APLUS_Woz directory and pip install all the requirements from "requirements.txt"

6. Once you installed all the requirements successfully, run "python manage.py runserver" in terminal.

7. You will be able to see the application running in your web browser.

8. You need to load all the data in your database. The URL for loading all the required data is "localhost:8000/load_pre_data"

9. The starting point for Human tutor is the URL "localhost:8000"

10. The starting point for tutee is the URL "localhost:8000/decide_chatroom"

11. If everything is configured properly, you will be able to chat between two browser window: one for human tutor and another for tutee.

12. If the database is correctly filled, the problem bank will have a list of problems along with their rating.

13. To export all the data after a session you need to hit the URL : "localhost:8000/export/all_action_logs/' or "localhost:8000/export/chat_logs/" or "localhost:8000/export/problem_entered_logs/" depending on which data you want to export. This will download a xlsx file in your download folder.