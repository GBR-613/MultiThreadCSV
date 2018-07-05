package gbr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/*
 * This is "production" version utilizing multiple CPU capabilities.  
 * 
 * Assumptions:
 * 1. All profile names appear correctly, so I should count ProfileName rather than UserId
 * 2. I count words in either Summary and Text
 * 3. I count words in case insensitive manner
 * 4. Not sure in which encoding the text appears... I assume it is ASCII Latin-1, 
 *    and those chars that are not are simply wrong.   
 */		

public class ProceedCSVEx extends Thread {

	private TreeThread threadProfiles;
	private TreeThread threadFood;
	private TreeThread threadWords;

	public ProceedCSVEx(){
		int threshold = 1000;
		threadProfiles = new TreeThread("Most active users", false, threshold);
		threadFood = new TreeThread("Most commented food items", false, threshold);
		threadWords = new TreeThread("Most used words", true, threshold);
	}

	public static void main(String[] args) {
		ProceedCSVEx runner = new ProceedCSVEx();
		runner.start();
		runner.threadWords.start();
		runner.threadProfiles.start();
		runner.threadFood.start();				
	}	

	public void run() {
		String filename = "Reviews.csv";
		FileInputStream file;		
		try {
			file = new FileInputStream(filename);
			InputStreamReader fileReader = new InputStreamReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);	
			bufferedReader.readLine(); //first line is meta-data
			String line;
			StringTokenizer st;
			char [] chBuf;
			boolean htmlTag = false;
			int i;
			StringBuffer sbBuf = new StringBuffer();

			while ((line = bufferedReader.readLine()) != null) {
				st = new StringTokenizer(line, ",");

				if (st.countTokens() < 9)
					continue;

				st.nextToken();
				threadFood.addText(st.nextToken());//2nd
				st.nextToken();						
				threadProfiles.addText(st.nextToken());//4th
				st.nextToken();	
				st.nextToken();	
				st.nextToken();	
				st.nextToken();	
				StringBuffer summary = new StringBuffer(st.nextToken()) ;//#9
				while(st.hasMoreTokens()) {
					summary.append(' ');
					summary.append(st.nextToken()); 
				}
				chBuf = summary.toString().toCharArray();				
				for(i=0; i<chBuf.length; i++) {
					if (chBuf[i] == '<') {
						htmlTag = true;
						continue;
					}
					if (chBuf[i] == '>' && htmlTag) {
						htmlTag = false;
						continue;
					}
					if (htmlTag)
						continue;
					if (chBuf[i] != '"' && chBuf[i] < 0xFF)
						sbBuf.append(chBuf[i]);
				}

				threadWords.addText(sbBuf.toString());
				sbBuf.delete(0, sbBuf.length());
			}

			System.gc();

			threadProfiles.proceedTree();
			threadFood.proceedTree();
			threadWords.proceedTree();

			fileReader.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
