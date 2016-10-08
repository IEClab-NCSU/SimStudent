cd ~/Developer/SimStudentServlet/WebContent
ant clean
ant dep
ant compileTutorJs
scp -r ~/Developer/SimStudentServlet/WebContent/ctatjslib oli:~/watson
scp ~/Developer/SimStudentServlet/WebContent/tutor.html oli:~/watson


