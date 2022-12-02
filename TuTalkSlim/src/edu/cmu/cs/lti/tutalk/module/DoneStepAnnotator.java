package edu.cmu.cs.lti.tutalk.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class DoneStepAnnotator {
	//String nickname = "";
	//ArrayList<String> key_terms;
	String[] done_state_phrases;
	
	public DoneStepAnnotator(String filename) {
		System.out.println(new File(".").getAbsolutePath());
		Path currentRelativePath = Paths.get("");
		String file = currentRelativePath.toAbsolutePath().toString()+"/"+filename.trim();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			//key_terms = new ArrayList<String>(Arrays.asList(line.split(",")));
			done_state_phrases = line.split(",");
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	public String isDoneState(String turn) {
		if(Arrays.stream(done_state_phrases).anyMatch(turn.toLowerCase()::contains)) return "DONE-1.0,NOTDONE-0.0,";
		//if(turn.contains("coefficient")) return "Y-1.0,N-0.0,";

		return "DONE-0.0,NOTDONE-1.0,";
		
	}
}
