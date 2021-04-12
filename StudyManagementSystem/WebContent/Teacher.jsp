<%@ page import="edu.tamu.config.Config" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>APLUS Study Management System</title>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
</head>
<body>
<h2> Teacher Form</h2>


<a href="/StudyManagementSystem/study.jsp" >Study Form</a> &nbsp;  &nbsp;
<a href="/StudyManagementSystem/school.jsp" >School Form</a> &nbsp;  &nbsp;
<u>Teacher Form</u> &nbsp;  &nbsp;
<a href="/StudyManagementSystem/GenerateUserID.jsp">Generate User</a> 

<%
	String JDBC_DRIVER = Config.config.get("jdbcDriver");
	String DB_URL = Config.config.get("database");
	String user = Config.config.get("dbUser");
	String password = Config.config.get("dbPassword");
%>
<sql:setDataSource var="ds" driver="<%=JDBC_DRIVER%>" url="<%=DB_URL%>" user="<%=user%>" password="<%=password%>"/>
<sql:query dataSource="${ds}" sql="select study_name from study order by creation_time asc " var="study"/>


<br/>
<form>

Study Name 				<select id="studyName" name="studyName">
						<c:forEach items="${study.rows}" var="element">
								<option value="${element.study_name}"> ${element.study_name} </option>
						</c:forEach>
						</select>
						<br>
School Name             <select name="schoolName" id="schoolName">
							<option>Select a school</option>
						</select>
<br>
			<div class="input-group">
			<span class="input-group-addon">Teacher Name</span>
			<input type="text" id="teacherName" class="form-control" name="teacherName" placeholder="Enter Teacher Name" size="25">
			<span class="input-group-addon"
				style="width: 0px; padding-left: 0px; padding-right: 0px; border: none;"></span>
			<select id="existingTeacherName" class="form-control">
				<option>Select to update</option>
				
			</select> 
			</div>
Class					<input type="text" id="className" name="className"/><br>
Number of students		<input type="text" id="noofstudents" name="noofstudents"/><br>
						<div id="sql_query">
						
						</div>
						
						<br>
						<input id="deleteForm" type="button" value="Delete" name="deleteButton"/> &nbsp; &nbsp;
						<input id="submitTeacher" type="button" name="testSubmit" value="Submit">
</form>
<p id="response">
	
</p>
<script>

	$.ajax({
		url: "FindSchoolByStudyServlet",
		type: "GET",
		data: {"studyName": $("#studyName").val()},
		success: function(data) {
			if(data.length != 0) {
				for(var i = 0; i < data.length; i++) {
					$("#schoolName").append('<option value=' + data[i].schoolname + '>' + data[i].schoolname + '</option>');
				}
			}
		}
	});
	
	//once the study name changed:
	$("#studyName").change(function() {
		$("#schoolName").empty().append('<option selected="selected">Select a school</option>');
		var studyName = $("#studyName").val();
		$.ajax({
			url: "FindSchoolByStudyServlet",
			type: "GET",
			data: {"studyName": $("#studyName").val()},
			success: function(data) {
				console.log(data);
				if(data.length != 0){
					 // var schoolName = data[0];
					  for(var i=0; i< data.length; i++)
						  $("#schoolName").append('<option value=' + data[i].schoolname + '>' + data[i].schoolname + '</option>');
				  }
			}
		});
	});
	//once the user click the school dropbox:
	$("#schoolName").change(function() {
		clearFields();
		var schoolName = $("#schoolName option:selected").text();
		
		// send AJAX
		$("#existingTeacherName").empty().append('<option selected="selected">Select to udpate</option>');
		$.ajax({
			url: "FindTeacherBySchoolServlet",
			type: "GET",
			data: {"schoolname": schoolName},
			success: function(data) {
				if(data.length > 0) {
					// udpate the select options
					for(var i = 0; i < data.length; i++) {
						$("#existingTeacherName").append('<option value=' + data[i].teacherName + '>' + data[i].teacherName + '</option>');
					}
				}
			}
		});
		
	});
	
	// once the user select the teacher name
	$("#existingTeacherName").change(function() {
		var teacherName = $("#existingTeacherName").find("option:selected").text();
		console.log(teacherName);
		if(teacherName !== 'Select to update') {
			// base on teacher name then search classname and student nubmer ************
			$.ajax({
				url: "FindDetailsByTeacherServlet",
				type: "GET",
				data: {"teachername": teacherName},
				success: function(data) {
					console.log(data);
					// udpate the fields
					$("#teacherName").val(teacherName);
					$("#className").val(data.className);
					$("#noofstudents").val(data.numbers);
				}
			});
			
			
		}
	});
	
	// for delete button:
	$("#deleteForm").click(function() {
		// for real delete function:************* 1. delete it from db
		//send AJAX to DeleteTeacherServlet
		var teacherName = $("#teacherName").val();
		$.ajax({
				url: "FindDetailsByTeacherServlet",
				type: "GET",
				data: {"teachername": teacherName},
				success: function(data) {
					
					var studySchoolKey = data.studySchoolKey;
					var teachername = $("#teacherName").val();
					
					if(data.studySchoolKey >= 0) {
						// we found teacher infos in our db, then start to delete
						$.ajax({
							url: "DeleteTeacherServlet",
							type: "GET",
							data: {"teachername": teachername, "study_school_key": studySchoolKey},
							success: function() {
								// if we delete successfully:
								clearFields();
								$("#existingTeacherName").empty().append('<option selected="selected">Select to update</option>');
								$("#response").html("Successfully deleted!");
								setTimeout(function() {
									$("#response").html("");
								}, 4000);
							}
						});
					}
				}
			});

		// for just clear the fields: ************ 2. just clear out the fields
		//clearFields();
		//$("#existingTeacherName").empty().append('<option selected="selected">Select to update</option>');
	});
	
	// for submit button:
	$("#submitTeacher").click(function() {
		//1. if the user just want to update the existing teacher
		// send AJAX to FindOneTeacherServlet
		// need studyName, schoolName and teacherName as well
		var studyName = $("#studyName option:selected").text();
		var schoolName = $("#schoolName option:selected").text();
		var teacherName = $("#teacherName").val();
		
		$.ajax({
			url: "FindOneTeacherServlet",
			type: "GET",
			data: {"studyName": studyName, "schoolName": schoolName, "teacherName": teacherName},
			success: function(data) {
				// we found the corresponding data from db
				console.log("FindOneTeacherServlet: " + data);
				
				var studySchoolKey = data.studySchoolKey;
				var className = $("#className").val();
				var students = $("#noofstudents").val();
				var teacherName = $("#teacherName").val();
				console.log(studySchoolKey + " findone teacher servlet");
				if(data.teacherNumFormDB >= 1) {
					// which means update:
					$.ajax({
						url: "UpdateTeacherServlet",
						type: "GET",
						data: {"teachername": teacherName, "classname": className, "students": students, "studySchoolKey": studySchoolKey},
						success: function() {
							clearFields();
							$("#existingTeacherName").empty().append('<option selected="selected">Select to update</option>');
							var schoolName = $("#schoolName option:selected").text();
							$.ajax({
								url: "FindTeacherBySchoolServlet",
								type: "GET",
								data: {"schoolname": schoolName},
								success: function(data) {
									if(data.length > 0) {
										// udpate the select options
										for(var i = 0; i < data.length; i++) {
											$("#existingTeacherName").append('<option value=' + data[i].teacherName + '>' + data[i].teacherName + '</option>');
										}
									}
								}
							});
							$("#response").html("Successfully updated!");
							setTimeout(function() {
								$("#response").html("");
							}, 4000);
						}
					});
				} else {
					// we didn't find any data from db, that means create a new one
					$.ajax({
						url: "CreateTeacherServlet",
						type: "GET",
						data: {"teachername": teacherName, "classname": className, "students": students, "studySchoolKey": studySchoolKey},
						success: function() {
							clearFields();
							$("#existingTeacherName").empty().append('<option selected="selected">Select to update</option>');
							var schoolName = $("#schoolName option:selected").text();
							$.ajax({
								url: "FindTeacherBySchoolServlet",
								type: "GET",
								data: {"schoolname": schoolName},
								success: function(data) {
									if(data.length > 0) {
										// udpate the select options
										for(var i = 0; i < data.length; i++) {
											$("#existingTeacherName").append('<option value=' + data[i].teacherName + '>' + data[i].teacherName + '</option>');
										}
									}
								}
							});
							$("#response").html("Successfully created!");
							setTimeout(function() {
								$("#response").html("");
							}, 4000);
						}
					});
					
				}
			}
			
		});
			
		//2. if the user want to create a new teacher for this school and study
		
	});
	
	function clearFields() {
		$("#teacherName").val('');
		$("#className").val('');
		$("#noofstudents").val('');
	}
	
</script>
</body>
</html>