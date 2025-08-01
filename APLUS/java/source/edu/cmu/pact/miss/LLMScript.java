package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.miss.PeerLearning.SimStLogger;

public class LLMScript {
	private static String scriptName;

	public LLMScript(String script_name){
		scriptName = script_name;
	}

	public String getScriptName() {
		return scriptName;
	}

	public String executeScript(String pythonPath, String projectPath, String username, String stepName, String QType, String Sol, String conv_history, SimStLogger logger) {

		if(!Objects.equals(scriptName, "")) {
			String scriptPath = projectPath + "/" + scriptName ;
			String scriptOutput = runPythonScript(pythonPath, scriptPath, username, stepName, QType, Sol, conv_history);

			if (scriptOutput == null) {
				logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION, SimStLogger.SCRIPT_OUTPUT,
						stepName, "Script Error", Sol, 0, "");
				return "";
			}

			logger.simStLog(SimStLogger.SIM_STUDENT_EXPLANATION, SimStLogger.SCRIPT_OUTPUT,
					stepName, scriptOutput, Sol, 0, "");
			return processQ(scriptOutput);
		}
		return "";
	}

	public String runPythonScript(String pythonPath, String scriptPath, String... arguments) {
		/**
		 * Will execute the openai response script with the given arguments
		 *
		 * @param pythonPath - the path to the python executable, set via system argument: pythonScriptPath
		 * @param scriptPath - the path to the python script: openai_response_script.py
		 * @param arguments  - the arguments to be passed to the script, such as all_questions, all_answers, stepName, QType, Sol, first_question, correctness, conv_history
		 * @return String output, which will be a a question, or "no question" if it is unnecessary (as deemed by the LLM).
		 * If the script fails to execute, it will return null.
		 */
		try {
			// Construct the command to run the Python script with arguments
			String[] command = new String[2 + arguments.length];
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
				System.err.println("Error: LLM question generation script exited with non-zero status: " + output);
				return null;
			}
			return output.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String processQ(String scriptOutput) {
		/**
		 * Simply parses the LLM Script output from runPythonScript to obtain the actual question that needs to be asked.
		 * Uses regex matching for extraction.
		 */
		String lower = scriptOutput.toLowerCase();

		// Early return if no question pattern and teacher-related reason
		if ((!lower.contains("the question is")
				&& !lower.contains("?")
				&& !lower.contains("no question"))
				|| lower.contains("no question")
				|| lower.contains("teacher")
				|| lower.contains("william")) {
			return "";
		}

		// List of regex patterns to try in order
		String[] patterns = {
				"therefore, the question is, (.*)",
				"the question is, (.*)"
		};

		for (String patternStr : patterns) {
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(lower);
			if (matcher.find()) {
				return matcher.group(1).replace("\"", "").trim();
			}
		}
		return "";
	}
}