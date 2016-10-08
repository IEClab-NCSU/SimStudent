import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class dataFormatter {


	public static void main(String[] args) {
		try {
			FileReader file = new FileReader(args[0]);
			BufferedReader scan = new BufferedReader(file);
			String currLine;
			int num = 1;
			String lesson = args[0].substring(0,3);
			String skill = args[0].substring(4,7);
			String cell = "cell";
			System.out.println("num\tlesson\tstudent\tskill\tcell\tright\teol");
			while ((currLine = (scan.readLine())) != null) {
				String right = "";
				String currWord = "";
				String currId = "";
				int index = 0;
				for (int i = 0; i < currLine.length(); i++) {
					if (currLine.charAt(i) == ' ') {
						if (index == 0) {
							currId = currWord;
							currWord = "";
							index++;
						}
						else {
							right = currWord;
							System.out.println(num + "\t" + lesson + "\t" + currId + "\t" + skill + "\t" + cell + "\t" + right);
							num++;
							index++;
							currWord = "";
						}
					}
					else {
						currWord += currLine.charAt(i);
					}
				}
			}
			scan.close();
		} catch (IOException e) {
			System.out.println("Problem Reading Data File");
		}
	}

}
