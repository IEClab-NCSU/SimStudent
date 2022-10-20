package edu.cmu.cs.lti.tutalk.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class KeyTermAnnotator {
	String nickname = "";
	//ArrayList<String> key_terms;
	String[] key_terms;
	
	public KeyTermAnnotator(String filename) {
		System.out.println(new File(".").getAbsolutePath());
		Path currentRelativePath = Paths.get("");
		String file = currentRelativePath.toAbsolutePath().toString()+"/"+filename.trim();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			//key_terms = new ArrayList<String>(Arrays.asList(line.split(",")));
			key_terms = line.split(",");
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
	public String hasKeyTerm(String turn) {
		if(Arrays.stream(key_terms).anyMatch(turn::contains)) return "Y-1.0,N-0.0,";
		//if(turn.contains("coefficient")) return "Y-1.0,N-0.0,";

		return "Y-0.0,N-1.0,";
		
	}
}
