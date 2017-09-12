<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>APLUS Study Management System</title>
</head>
<body>
<h2> Generate user ID</h2>
<sql:setDataSource var="ds" driver="com.mysql.jdbc.Driver" url="jdbc:mysql://kona.education.tamu.edu:3306/studymanagement" user="simstudent" password="simstudent"/>
<sql:query dataSource="${ds}" sql="select study_name from study order by creation_time asc " var="study"/>

<a href="/StudyManagementSystem/study.jsp">Study Form</a> &nbsp;  &nbsp;
<a href="/StudyManagementSystem/school.jsp" >School Form</a> &nbsp;  &nbsp;
<a href="/StudyManagementSystem/Teacher.jsp" >Teacher Form</a> &nbsp;  &nbsp;
<u>Generate User</u> 

<form action="StudentConditionMapping" method="POST" >
Study Name				<select name="studyName">
						<c:forEach items="${study.rows}" var="element">
								<option value="${element.study_name}"> ${element.study_name} </option>
						</c:forEach>
						</select>
						<br>
	<input type="submit" name="">
</form>
</body>
</html>