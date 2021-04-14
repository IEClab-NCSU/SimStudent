FROM tomcat:9.0-jdk8-openjdk
RUN ln -s /usr/local/tomcat/webapps.dist/manager webapps/manager
RUN ln -s /usr/local/tomcat/webapps.dist/host-manager webapps/host-manager
RUN ln -s /usr/local/tomcat/webapps.dist/ROOT webapps/ROOT
COPY StudyManagementSystem.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]