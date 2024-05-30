package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.miss.PeerLearning.SimStLogger;

public class LLMScript {
	private static String scriptName;
	private String expected_response_KBR = "";
	
	public LLMScript(String script_name){
		scriptName = script_name;
	}
	
	public String getScriptName() {
		return scriptName;
	}
	
	public String executeScript(String pythonPath,String projectPath,  String stepName, String QType, String Sol, String first_question, String correctness, String conv_history, String expected_response,ArrayList<String> all_questions, ArrayList<String> all_answers, SimStLogger logger) {
		//System.out.println("script_execute");
		//System.out.println("stepname "+stepName+" QTYPE "+QType+" Sol "+Sol+" correctness "+correctness);
		//System.out.println(" first_question "+first_question+" conv history "+conv_history);
		//System.out.println(" exp "+expected_response);
		//System.out.println();
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
			//System.out.println(pythonPath+" "+scriptPath);
			String scriptOutput;
			if(expected_response=="") {
				scriptOutput = runPythonScript(pythonPath, scriptPath, all_questions.toString(), all_answers.toString(), stepName, QType, Sol, first_question, correctness, conv_history);
				//String conv = "\nYou:Why do you think I should add 2?"+"\nTeacher:"+"to combine like terms";
				//scriptOutput = runPythonScript(pythonPath, scriptPath, "6x-2=7x+10", "WR", "add 2", "Why do you think I should add 2?", "correct", conv);
				//System.out.println(scriptOutput);
			}
			else {
				//System.out.println("JAVA KBR ");
				scriptOutput = runPythonScript(pythonPath, scriptPath, all_questions.toString(), all_answers.toString(), stepName, QType, Sol, first_question, correctness, conv_history, expected_response_KBR);
				//String exp = "Add 2 is correct because it will help us combine the like terms together. Add 2 will get rid of -2 and combine all the constant terms on right It would result in 6x-2+2=7x+10+2. We also have another constant term which is +10. We could have also get rid of +10 by performing subtract 10. Also, we could have combined the variable terms first instead of constant terms.";
				//String conv = "\nYou:Why do you think I should add 2?"+"\nTeacher:"+"to combine like terms";
				//scriptOutput = runPythonScript(pythonPath, scriptPath, "6x-2=7x+10", "WR", "add 2", "Why do you think I should add 2?", "correct", conv, exp);
				//System.out.println(scriptOutput);
			}
			//System.out.println("QLLM "+scriptOutput);
			//System.out.println("START");
			//System.out.println("Entire Script Output: "+scriptOutput);
			//System.out.println("END");
			if (scriptOutput != null)
				logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION, SimStLogger.SCRIPT_OUTPUT,
					stepName, scriptOutput, Sol, 0, "");
			else
				logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION, SimStLogger.SCRIPT_OUTPUT,
					stepName, "Script error", Sol, 0, "");
			return scriptOutput != null ? scriptOutput : "";
		}
		return "";
	}

	public String runPythonScript(String pythonPath, String scriptPath, String... arguments) {
		try {
			// Construct the command to run the Python script with arguments
			String[] command = new String[arguments.length + 2];
			//command[0] = "python";
			//command[0] = "/Users/tasmiashahriar/opt/anaconda3/bin/python3.8";
			command[0] = pythonPath;
			//System.out.println(command[0]);
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
	
	public String processResponseLLMOutput(String script_output) {
		String regexPattern = "KBR is (.*)the alignment is----";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(regexPattern, Pattern.DOTALL);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(script_output);

        // Check if the pattern matches
        if (matcher.find()) {
            // Extract the text between "The expected response--" and "the q is---"
            String extractedText = matcher.group(1);
            extractedText = extractedText.trim();
            
            //System.out.println("The extracted response text is, "+extractedText);
            //System.out.println("END");
            expected_response_KBR = extractedText.replace("\"", "");

        } else {
        	expected_response_KBR = "";
        }
        return expected_response_KBR;
	}

	public String processQ(String script_output) {
			if(!script_output.contains("the question is") && (script_output.toLowerCase().contains("since teacher") || script_output.toLowerCase().contains("teacher mentioned") || script_output.toLowerCase().contains("teacher did not mention"))) return "No question";
	        String regexPattern = "Therefore, the question is, (.*)";
	        Pattern pattern = Pattern.compile(regexPattern);
	        Matcher matcher = pattern.matcher(script_output);

	        // Check if the pattern matches
	        if (matcher.find()) {
	            // Extract the text after "Therefore, the question is,"
	            String extractedText = matcher.group(1);
	            //System.out.println("The extracted Q text is, "+extractedText);
	            //System.out.println("END");
	            if(extractedText.contains("?")) 
	            	return extractedText.replace("\"", "");
	            else return "No question";
	            	

	            // Print the extracted text
	            //System.out.println(extractedText);
	        } else if(script_output.contains("?")) {
	            //System.out.println("Pattern not found in the input text.");
	        	regexPattern ="the q is---(.*)";
	        	pattern = Pattern.compile(regexPattern);
		        matcher = pattern.matcher(script_output);

		        // Check if the pattern matches
		        if (matcher.find()) {
		            // Extract the text after "Therefore, the question is,"
		            String extractedText = matcher.group(1);
		            //System.out.println("The second extracted Q text is, "+extractedText);
		            extractedText = extractedText.replace("\"", "");
		            if(extractedText.contains("the question is,")) {
		            	regexPattern ="the question is, (.*)";
			        	pattern = Pattern.compile(regexPattern);
				        matcher = pattern.matcher(extractedText);

				        // Check if the pattern matches
				        if (matcher.find()) {
				            // Extract the text after "Therefore, the question is,"
				            String extractedText2 = matcher.group(1);
				            //System.out.println("The second extracted Q text is, "+extractedText);
				            return extractedText2.replace("\"", "");
				        }
				        else
				        	return "No question";
		            }
		            else
		            	return extractedText;
		        }
		        else {
		        	regexPattern ="the question is, (.*)";
		        	pattern = Pattern.compile(regexPattern);
			        matcher = pattern.matcher(script_output);

			        // Check if the pattern matches
			        if (matcher.find()) {
			            // Extract the text after "Therefore, the question is,"
			            String extractedText = matcher.group(1);
			            //System.out.println("The second extracted Q text is, "+extractedText);
			            return extractedText.replace("\"", "");
			        }
			        else
			        	return "No question";
		        }
	        	//return script_output;
	        }
	        else {
	            //System.out.println("Pattern not found in the input text.");
	        	return "No question";
	        }
	        
	}
	
	
}
