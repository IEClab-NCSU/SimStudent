FROM mysql:5.7
COPY studymanagement.sql /data/application/studymanagement.sql
EXPOSE 3306