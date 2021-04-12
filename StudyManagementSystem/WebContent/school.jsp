<%@ page import="edu.tamu.config.Config" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>APLUS Study Management System</title>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
</head>
<body>
	<h2>School Form</h2>
	<%
		String JDBC_DRIVER = Config.config.get("jdbcDriver");
		String DB_URL = Config.config.get("database");
		String user = Config.config.get("dbUser");
		String password = Config.config.get("dbPassword");
	%>
<sql:setDataSource var="ds" driver="<%=JDBC_DRIVER%>" url="<%=DB_URL%>" user="<%=user%>" password="<%=password%>"/>

	<sql:query dataSource="${ds}"
		sql="select study_name from study order by creation_time asc "
		var="study" />

<a href="/StudyManagementSystem/study.jsp">Study Form</a> &nbsp;  &nbsp;
<u>School Form</u> &nbsp;  &nbsp;
<a href="/StudyManagementSystem/Teacher.jsp" >Teacher Form</a> &nbsp;  &nbsp;
<a href="/StudyManagementSystem/GenerateUserID.jsp">Generate User</a> 


	<form id="schoolForm" action="SchoolServlet" method="POST">
		Study Name <select id="studyName" name="studyName">
			<c:forEach items="${study.rows}" var="element">
				<option value="${element.study_name}">
					${element.study_name}</option>
			</c:forEach>
		</select> <br>
		<div class="input-group">
			<span class="input-group-addon">School Name</span>
			<input type="text" id="schoolName" class="form-control" name="schoolName" placeholder="Enter School Name" size="25">
			<span class="input-group-addon"
				style="width: 0px; padding-left: 0px; padding-right: 0px; border: none;"></span>
			<select id="existingSchoolName" class="form-control">
				<option>Select to update</option>
				
			</select> 
			</div>
			Pre-test <input type="date" name="pretest" id="pretestField"/><br> 
			Intervention from <input type="date" name="inter_from" id="fromField"/> 
			to <input type="date" name="inter_to" id="toField" /><br> 
			Post-test <input type="date" name="posttest" id="posttestField"/><br> 
			Delayed-test <input type="date" name="delaytest" id="delayedtestField"/><br> 
			Windows log directory <input type="text" id="windowsLog" name="windowsLog" size="45"/><br> 
			Mac log directory <input type="text" id="macLog" name="macLog" size="50"/><br> 
			<input id="onDelete" type="button" name="deleteSchool" value="Delete"/>
			&nbsp; &nbsp;
			<input id="onSubmit" type="button" name="schoolSubmit" value="Submit" />
			<p id="response">
				${status}
			</p>
	</form>
</body>
<script>
	//before we start, send AJAX to FindSchoolByStudyServlet. This is the default selection.
	$.ajax({
		url: "FindSchoolByStudyServlet",
		type: "GET",
		data: {"studyName": $("#studyName").val()},
		success: function(data) {
			if(data.length != 0) {
				for(var i = 0; i < data.length; i++) {
					$("#existingSchoolName").append('<option value=' + data[i].schoolname + '>' + data[i].schoolname + '</option>');
				}
			}
		}
	});
	
	
	$("#studyName").change(function() { 
		$("#existingSchoolName").empty().append('<option selected="selected">Select to update</option>');
		var dash = "------------";
		$.ajax({
			url: "FindSchoolByStudyServlet",
			type: "GET",
			data: {"studyName": $("#studyName").val()},
			success: function(data) {
			if(data.length != 0) {
				  if(data[0].length > 0){
					  var schoolName = data[0];
					  for(var i=0; i< schoolName.length; i++)
						  $("#existingSchoolName").append('<option value=' + schoolName[i].schoolname + '>' + schoolName[i].schoolname + '</option>');
				  }
				   $("#existingSchoolName").append('<option value='+"dash"+'>'+dash+'</option>');
				  if(data.length > 1){
					  schoolName = data[1];
					  for(var i=0; i< schoolName.length; i++)
						  $("#existingSchoolName").append('<option value=' + schoolName[i].allschoolname + '>' + schoolName[i].allschoolname + '</option>');
				  }
			    }
			}
		});
	});
	
	
	$("#response").html('');
	
	// for submit button:
	$("#onSubmit").click(function() {
		console.log("Submitted!");
		//if create a new one:
		var userSchool = $.trim( $("#schoolName").val() );
		// flag === true means create
		var flag = true;
		//send AJAX to /SearchSchoolServlet
		$.ajax({
			type: "GET",
			url: "SearchSchoolServlet",
			success: function(data) {
				console.log(data);
				for(var i = 0; i < data.length; i++) {
					if(userSchool == data[i].schoolName) {
						flag = false;
					}
				}
				//console.log(flag);
				if(flag) {
					console.log("Start to create a new school");
					var studyName = $("#studyName").val();
					var schoolName = $("#schoolName").val();
					var pretest = $("#pretestField").val();
					var from = $("#fromField").val();
					var post = $("#posttestField").val();
					var to = $("#toField").val();
					var delayed = $("#delayedtestField").val();
					var windowslog = $("#windowsLog").val();
					var mac = $("#macLog").val();
					$.ajax({
						type: "GET",
						url: "SchoolServlet",
						data: {"studyName": studyName, "schoolName": schoolName, "pretest": pretest, "inter_from": from, "inter_to": to, 
							"posttest": post, "delaytest": delayed, "windowsLog": windowslog, "macLog": mac},
						success: function(data) {
							$("#response").html("Successfully submitted!");
							setTimeout(function() {
								$("#response").html("");
							}, 5000); 
							//after sumbitted, clear all the field:
							$("#schoolName").val('');
							$("#pretestField").val('');
							$("#fromField").val('');
							$("#posttestField").val('');
							$("#toField").val('');
							$("#delayedtestField").val('');
							$("#windowsLog").val('');
							$("#macLog").val('');
							$("#existingSchoolName").empty().append('<option selected="selected">Select to update</option>');
							$.ajax({
								type:"GET",
								url: "SearchSchoolServlet",
								success: function(data) {
									for(var i = 0; i < data.length; i++) {
										var schoolname = data[i].schoolName;
										$("#existingSchoolName").append('<option value=' + schoolname + '>' + schoolname + '</option>');
									}
								}
							});
						}
						
					});
					
				} else {
					console.log("Start udpating the existing school");
					// send servlet request to UpdateSchoolServlet action.
					var studyName = $("#studyName").val();
					var schoolName = $("#schoolName").val();
					var pretest = $("#pretestField").val();
					var from = $("#fromField").val();
					var post = $("#posttestField").val();
					var to = $("#toField").val();
					var delayed = $("#delayedtestField").val();
					var windowslog = $("#windowsLog").val();
					var mac = $("#macLog").val();
					$.ajax({
						type:"GET",
						url: "UpdateSchoolServlet",
						data: {"studyName": studyName, "schoolName": schoolName, "pretest": pretest, "inter_from": from, "inter_to": to, 
							"posttest": post, "delaytest": delayed, "windowsLog": windowslog, "macLog": mac},
						success: function() {
							// after successfully udpated
							$("#schoolName").val('');
							$("#pretestField").val('');
							$("#fromField").val('');
							$("#posttestField").val('');
							$("#toField").val('');
							$("#delayedtestField").val('');
							$("#windowsLog").val('');
							$("#macLog").val('');
							// should select the first option
							$("#existingSchoolName").val($("#existingSchoolName option:first").val());
							$("#response").html("Successfully updated!");
							setTimeout(function() {
								$("#response").html("");
							}, 5000);
						}
					});
				}
			}
		});
		
	});
	
	// for delete button:
	$("#onDelete").click(function() {
		console.log("Deleted!");
		// if delete a existing school:
		var userSchool = $("#schoolName").val();
		// flag === true means clear all the fields
		var flag = true;
		// send AJAX to /SearchSchoolServlet
		$.ajax({
			type: "GET",
			url: "SearchSchoolServlet",
			success: function(data) {
				console.log(data);
				for(var i = 0; i < data.length; i++) {
					if(userSchool == data[i].schoolName) {
						flag = false;
					}
				}
				//console.log(flag);
				if(flag) {
					console.log("Clear all the fields...");
					$("#schoolName").val('');
					$("#pretestField").val('');
					$("#fromField").val('');
					$("#posttestField").val('');
					$("#toField").val('');
					$("#delayedtestField").val('');
					$("#windowsLog").val('');
					$("#macLog").val('');
					
				} else {
					console.log("Deleted existing school in db... " + userSchool);
					// send servlet request to DeleteSchoolServlet action.
					$.ajax({
						type: "GET",
						url: "DeleteSchoolServlet",
						data: {"schoolname" : userSchool},
						success: function() {
							$("#response").html("Successfully deleted!");
							$("#schoolName").val('');
							
							// refresh the select options *****************
							$("#existingSchoolName").empty().append('<option selected="selected">Select to update</option>');
							$.ajax({
								type:"GET",
								url: "SearchSchoolServlet",
								success: function(data) {
									for(var i = 0; i < data.length; i++) {
										var schoolname = data[i].schoolName;
										$("#existingSchoolName").append('<option value=' + schoolname + '>' + schoolname + '</option>');
									}
									// after succuessfully deleted, clear all the field
									$("#schoolName").val('');
									$("#pretestField").val('');
									$("#fromField").val('');
									$("#posttestField").val('');
									$("#toField").val('');
									$("#delayedtestField").val('');
									$("#windowsLog").val('');
									$("#macLog").val('');
								}
							});
							setTimeout(function() {
								$("#response").html("");
							}, 5000); 
						}
					});
				}
			}
		});
		
	});

	$("#existingSchoolName").change(function() {
		
		var schoolName = $("#existingSchoolName").find("option:selected").text();
		console.log(schoolName);
		if(schoolName != "Select to update") {
			$("#schoolName").val(schoolName);
		} else {
			$("#schoolName").val("");
		}
		
		$.ajax({
			type:"GET",
			url: "EditServlet",
			data: {"schoolName" : schoolName},
			success: function(data) {
				
				//modify input fields:
				
				$("#pretestField").val(data.pretest);
				$("#fromField").val(data.from);
				$("#posttestField").val(data.posttest);
				$("#toField").val(data.to);
				$("#delayedtestField").val(data.delayedtest);
				$("#windowsLog").val(data.windowslog_dir);
				$("#macLog").val(data.maclog_dir);
				
				console.log(data);
			}
		});
	});
	
	
	
	
</script>
</html>