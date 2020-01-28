************************* Introduction to APLUS_Woz ************************************

APLUS_Woz is a web based chat application where human tutors (middle grade students) can teach SimStudent (You) how to solve algebraic equations. The main aim of building the application is to measure the quality of agent responses and if these responses affect tutor learning. In order to make this application work, you need to connect yourself as tutee in a recent chatroom through this web app. This app has all the tabs and functionalities just as same as the APLUS, but human tutor should use APLUS for the quiz and example tab. Therefore, you will have to set up APLUS in another PC that will be with the human tutor during the session and you will be connected with that PC via screen sharing. Once, human tutor clicks the example/quiz tab, there will be a loader in their window asking them to wait and a popup notifying you that human tutor wants to access the example/quiz tab. Then you need to set up the example/quiz tab in the APLUS from your screen shared session and close the popup window in our web app. Once you close the popup window, a message will appear in the human tutor side asking him to use the PC that have APLUS connection with example/quiz window opened. For the tutoring tab, you will be responsible for feeding all the steps taught by the tutor in the web app to the APLUS via your screen shared session. That's how APLUS will get updated with the steps and act accordingly in the quiz.


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