package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.miss.PeerLearning.SimStLogger;

public class LLMClassifier {
	private static String scriptName;
	
	public LLMClassifier(String script_name){
		scriptName = script_name;
	}
	
	public String getScriptName() {
		return scriptName;
	}
	
	public String executeScript(String pythonPath,String projectPath,  String stepName, String Sol, String question, String response, SimStLogger logger) {
		
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
			
			scriptOutput = runPythonScript(pythonPath, scriptPath, question, response);
			//System.out.println("classifier LLM "+scriptOutput);
			//System.out.println(scriptOutput);
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
	
	public boolean processClassifiedResponse(String script_output) {
		if(script_output.contains("good response")) return true;
        else if (script_output.contains("bad response")) return false;
        else return false;
		
	}

	   
}
