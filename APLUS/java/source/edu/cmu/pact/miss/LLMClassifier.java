package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import edu.cmu.pact.miss.PeerLearning.SimStLogger;

public class LLMClassifier {
	private static String scriptName;

	public LLMClassifier(String script_name){
		scriptName = script_name;
	}

	public String getScriptName() {
		return scriptName;
	}

	public boolean executeScript(String pythonPath, String projectPath, String stepName, String qtype, String sol, String question, String answer, SimStLogger logger) {
		if(!Objects.equals(scriptName, "")) {
			String scriptPath = projectPath + "/" + scriptName ;
			String scriptOutput = runPythonScript(pythonPath, scriptPath, stepName, qtype, sol, question, answer);

			if (scriptOutput == null) {
				logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION, SimStLogger.SCRIPT_OUTPUT,
						stepName, "Script Error", sol, 0, "");
				return false;
			}
			logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION, SimStLogger.SCRIPT_OUTPUT,
					stepName, scriptOutput, sol, 0, "");
			return scriptOutput.contains("This response is good");
		}
		return false;
	}

	public String runPythonScript(String pythonPath, String scriptPath, String... arguments) {
		try {
			// Construct the command to run the Python script with arguments
			String[] command = new String[arguments.length + 2];
			command[0] = pythonPath;
			command[1] = scriptPath;
			System.arraycopy(arguments, 0, command, 2, arguments.length);

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
			if (exitCode == 1) {
				System.err.println("Error: LLM response classification script exited with non-zero status - " + output.toString());
				return null;
			}
			return output.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}