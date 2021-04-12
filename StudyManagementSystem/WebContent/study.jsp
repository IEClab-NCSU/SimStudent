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
<h2> Study Form</h2>

<%
	String JDBC_DRIVER = Config.config.get("jdbcDriver");
	String DB_URL = Config.config.get("database");
	String user = Config.config.get("dbUser");
	String password = Config.config.get("dbPassword");
%>
<sql:setDataSource var="ds" driver="<%=JDBC_DRIVER%>" url="<%=DB_URL%>" user="<%=user%>" password="<%=password%>"/>
<sql:query dataSource="${ds}" sql="select condition_name from conditions" var="app_condition"/>

<u>Study Form</u> &nbsp;  &nbsp;
<a href="/StudyManagementSystem/school.jsp" >School Form</a> &nbsp;  &nbsp;
<a href="/StudyManagementSystem/Teacher.jsp" >Teacher Form</a> &nbsp;  &nbsp;
<a href="/StudyManagementSystem/GenerateUserID.jsp">Generate User</a> 

<form>
	
		<div class="input-group">
			<span class="input-group-addon">Study Name</span>
			<input type="text" id="studyName" class="form-control" name="studyName" placeholder="Enter Study Name" size="25">
			<span class="input-group-addon"
				style="width: 0px; padding-left: 0px; padding-right: 0px; border: none;"></span>
			<select id="existingStudyName" class="form-control">
				<option>Select to update</option>
			</select> 
		</div>
	
	
	Condition : &nbsp;&nbsp;<input type="text" id="addCondition" name="addCondition" placeholder="Enter new condition" size="25"/>
	<input type="button" value="Add" id="addConditionButton" />
	<br>
	<div id="checkboxes">
	<c:forEach items="${app_condition.rows}" var="element">
					<input id="${element.condition_name}" class="condition" type="checkbox" name="condition_name" value="${element.condition_name}"> ${element.condition_name}<br>
	</c:forEach>
 	</div>
	Level of assignment : <input id="schoolRadio" type="radio" class="assignment" name="assignment" value="School"> School
			              <input id="classRadio" type="radio" class="assignment" name="assignment" value="Class" > Class
						  <input id="studentRadio" type="radio" class="assignment" name="assignment" value="Student"> Student<br>
	<input type="button" id="deleteStudy" value="Delete"  name="studyDelete">
	&nbsp; &nbsp;
	<input type="button" id="studySubmit" value="Submit" name="studySubmit">
	<div id="response">
		
	</div>
</form>
<script>
	$("#addConditionButton").click(function() {
		
		
		var newCondition = $.trim($("#addCondition").val());
		if( newCondition != '' ) {
			
			// then start to send AJAX
			$.ajax({
				url: "CreateConditionServlet",
				type: "GET",
				data: {"condition": newCondition},
				success: function() {
					// refresh the checkbox
					addCheckbox();
				}
			});
			
		} else {
			$("#addCondition").val('');
		}
		
	});
	
	$("#deleteStudy").click(function() {
		var studyName = $("#studyName").val();
		$.ajax({
			url: "FindOneStudyServlet",
			type: "GET",
			data: {"studyName": studyName},
			success: function(data) {
				// 1 means we found studyName in our db
				var numbers = data.numbers;
				if(numbers == 1) {
					// then update
					console.log("I am start to delete study form from db..");
					$.ajax({
						url: "DeleteStudyServlet",
						type: "GET",
						data: {"studyName": studyName},
						success: function() {
							$("#studyName").val("");
							refreshOptions();
							clearFields();
						}
					});
				} else {
					// then clear
					console.log("I am start to clear study form...");
					$("#studyName").val("");
					clearFields();
				}
			}
		});
	});
	
	// submit button
	$("#studySubmit").click(function() {
		// check if the studyName already exist in db
		var studyName = $("#studyName").val();
		var condition_name = [];
		var checkboxValues = $("input:checkbox:checked").map(function() {
			return $(this).val();
		});
		console.log("working on check box value: " + checkboxValues.get());
		condition_name = checkboxValues.get();
		var assignment = $("input:radio[name=assignment]:checked").val();
		console.log(studyName + ", " + condition_name + " " + assignment);
		$.ajax({
			url: "FindOneStudyServlet",
			type: "GET",
			data: {"studyName": studyName},
			success: function(data) {
				// 1 means we found studyName in our db
				var numbers = data.numbers;
				if(numbers == 1) {
					// then update
					console.log("I am start to update study form..");
					
					$.ajax({
						url: "UpdateStudyServlet",
						type: "GET",
						data: {"studyName": studyName, "condition_name": condition_name, "assignment": assignment},
						success: function(data) {
							console.log("Got something back from servlet");
							//then update the view:
							refreshOptions();
							clearFields();
							$("#studyName").val("");
							$("#response").html("Successfully updated!");
							setTimeout(function() {
								$("#response").html("");
							}, 4000);
						}
					});
					
				} else {
					// then create
					console.log("I am start to create a new study...");
					$.ajax({
						url: "CreateStudyServlet",
						type: "GET",
						data: {"studyName": studyName, "condition_name": condition_name, "assignment": assignment},
						success: function() {
							// refresh the select options:
							refreshOptions();
							clearFields();
							$("#studyName").val("");
							$("#response").html("Successfully submitted!");
							setTimeout(function() {
								$("#response").html("");
							}, 4000);
						}
					});
				}
			}
		});
	});

	$.ajax({
		url: "SearchStudyServlet",
		type: "GET",
		success: function(data) {
			for(var i = 0; i < data.length; i++) {
				var studyName = data[i].studyName;
				$("#existingStudyName").append('<option value=' + studyName + '>' + studyName + '</option>');
			}
		}
	});
	
	$("#existingStudyName").change(function() {
			
			var studyName = $("#existingStudyName").find("option:selected").text();
			console.log(studyName);
			if(studyName != "Select to update") {
				$("#studyName").val(studyName);
				// before we send ajax, clear the field.
				clearFields();
				$.ajax({
					type:"GET",
					url: "SearchStudyDetailsServlet",
					data: {"studyName" : studyName},
					success: function(data) {
						
						//modify input fields:************************
						// data contains: coditions and level_of_assignmant
						for(var i = 0; i < data.conditions.length; i++) {
							$("#" + data.conditions[i]).prop("checked", true);
							
						}
					
					
						if(data.level_of_assignment ===  "Student" ) {
							$("#studentRadio").prop("checked", true);
						} else if(data.level_of_assignment === "School") {
							$("#schoolRadio").prop("checked", true);
						} else {
							$("#classRadio").prop("checked", true);
						}
						console.log(data);
					}
				}); 
			} else {
				$("#studyName").val("");
				
				clearFields();
			}
			
			
		});
	
	function clearFields() {
		$('input[type=checkbox]').each(function() { 
			this.checked = false; 
		});
		$("#studentRadio").prop("checked", false);
		$("#schoolRadio").prop("checked", false);
		$("#classRadio").prop("checked", false);
	}
	
	function refreshOptions() {
		// before refresh, clear out all the fields
		$("#existingStudyName").empty().append('<option>Select to update</option>');;
		$.ajax({
			url: "SearchStudyServlet",
			type: "GET",
			success: function(data) {
				for(var i = 0; i < data.length; i++) {
					var studyName = data[i].studyName;
					$("#existingStudyName").append('<option value=' + studyName + '>' + studyName + '</option>');
				}
			}
		});
	}
	
	function addCheckbox() {
		// get the condition name first
		var conditionName = $.trim($("#addCondition").val());
		
		$("#checkboxes").append("<input type='checkbox' class='condition' id='" + 
				conditionName + "' name='" + conditionName + "' value='" + conditionName + "'>" + conditionName);
	}
</script>
</body>
</html>