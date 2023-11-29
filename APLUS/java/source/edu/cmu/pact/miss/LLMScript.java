package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LLMScript {
	private static String scriptName;
	private String expected_response_KBR = "";
	
	public LLMScript(String script_name){
		scriptName = script_name;
	}
	
	public String getScriptName() {
		return scriptName;
	}
	
	public String executeScript(String projectPath, String stepName, String QType, String Sol, String first_question, String correctness, String conv_history, String expected_response) {
		//System.out.println("script_execute");
		if(scriptName != "") {
			String scriptPath = projectPath + "/"+scriptName ;
			//String scriptPath ="/Users/tasmiashahriar/Documents/GitHub/APLUS_CTI/SimStudent/Tutors/Algebra/SimStAlgebraV8"+"/chat_interface.py" ;
			//String stepName = "4=y+5";
			//String QType = "WW";
			//String Sol = "subtract 5";
			//String first_question = "Why am I wrong?";
			//String correctness = "correct";
			//String conv_history =
			//		"Student:Why am I wrong?" + "\n" +
			//				"Teacher:you need to get the varible on its own" + "\n" ;
	
			String scriptOutput;
			if(expected_response=="") 
				scriptOutput = runPythonScript(scriptPath, stepName, QType, Sol, first_question, correctness, conv_history);
			else {
				//System.out.println("JAVA KBR "+expected_response_KBR);
				scriptOutput = runPythonScript(scriptPath, stepName, QType, Sol, first_question, correctness, conv_history, expected_response_KBR);
			}
			return scriptOutput != null ? scriptOutput : "";
		}
		return "";
	}
	public String runPythonScript(String scriptPath, String... arguments) {
		try {
			// Construct the command to run the Python script with arguments
			String[] command = new String[arguments.length + 2];
			//command[0] = "python";
			command[0] = "/Users/tasmiashahriar/opt/anaconda3/bin/python3.8";
			// I had to set the pythonpath like this, ask subodh how he did it.
			command[1] = scriptPath;
			System.arraycopy(arguments, 0, command, 2, arguments.length);
//            command[1 + arguments.length] = scriptPath;

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			Process process = processBuilder.start();

			// Read script output
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder output = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line).append("\n");
			}

			int exitCode = process.waitFor();

			if (exitCode == 0) {
				return output.toString();
			} else {
				System.err.println("Error: Python script exited with non-zero status");
				return null;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void processResponseLLMOutput(String script_output) {
		String regexPattern = "The expected response--(.*?)the q is---";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(regexPattern, Pattern.DOTALL);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(script_output);

        // Check if the pattern matches
        if (matcher.find()) {
            // Extract the text between "The expected response--" and "the q is---"
            String extractedText = matcher.group(1);
            expected_response_KBR = extractedText.replace("\"", "");

        } else {
        	expected_response_KBR = "";
        }
	}

	public String processQ(String script_output) {
	
	        String regexPattern = "Therefore, the question is, (.*)";
	        Pattern pattern = Pattern.compile(regexPattern);
	        Matcher matcher = pattern.matcher(script_output);

	        // Check if the pattern matches
	        if (matcher.find()) {
	            // Extract the text after "Therefore, the question is,"
	            String extractedText = matcher.group(1);
	            return extractedText.replace("\"", "");

	            // Print the extracted text
	            //System.out.println(extractedText);
	        } else {
	            //System.out.println("Pattern not found in the input text.");
	        	return "";
	        }
	        
	}
	
	
}
