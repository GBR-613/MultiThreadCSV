/**
 * 
 */
package gbr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * @author brodsky
 *
 */
public class TranslatorFileLoader  extends Thread {

	public TranslatorThread threadTranslate;

	/**
	 * 
	 */
	public TranslatorFileLoader() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TranslatorFileLoader self = new TranslatorFileLoader();
		self.threadTranslate = new TranslatorThread();
		self.start();		
		self.threadTranslate.start();

	}

	private static int threshold = 1000;

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
			int i, j=0, k;
			char ch;
			StringBuffer sbBuf = new StringBuffer();
			StringBuffer nextStatement = new StringBuffer();

			while ((line = bufferedReader.readLine()) != null) {
				//System.out.println("Line #" + j);
				//j++;

				chBuf = line.toCharArray();				
				for(i=0; i<chBuf.length; i++) {

					if (chBuf[i] < 0xFF)
						sbBuf.append(chBuf[i]);
					if (chBuf[i] == ',' )
						//I don't care about end of line because they all 
						//end with '"'
						if (chBuf[i+1] == ',' ) {
							chBuf[i] = 0xFF;
							i = i + 1;									
						}
				}

				st = new StringTokenizer(sbBuf.toString(), ",");
				sbBuf.delete(0, sbBuf.length());
				if (st.countTokens() < 10)
					continue;

				st.nextToken();
				st.nextToken();
				st.nextToken();						
				st.nextToken();
				st.nextToken();	
				st.nextToken();	
				st.nextToken();	
				st.nextToken();
				st.nextToken();	
				StringBuffer summary = new StringBuffer(st.nextToken()) ;//#10
				while(st.hasMoreTokens()) {
					summary.append(' ');
					summary.append(st.nextToken()); 
				}				

				chBuf = summary.toString().toCharArray();

				// I am going to send either full statements or sections 
				// up to 'threshold' long for translation.
				// Of course the translation might be not exact if not full statements 
				// are being translated, but I suppose smart syntax and lexical analyzing 
				// is beyond of the current task.  

				for(i=0; i<chBuf.length; i++) {

					if (chBuf[i] == '<' && !htmlTag) {
						htmlTag = true;
						continue;
					}
					if (chBuf[i] == '>' && htmlTag) {
						htmlTag = false;
						continue;
					}

					if (chBuf[i] == 0xFF)
						chBuf[i] = ',';
					nextStatement.append(chBuf[i]);

					while (nextStatement.length() >= threshold) {

						for (k = 0; k < nextStatement.length(); k++) {
							//let's find end of statement first
							if (chBuf[i] == '.' || chBuf[i] == ',' || chBuf[i] == '!') {
								nextStatement.append(threadTranslate.joinMarker);
								threadTranslate.addText(nextStatement);
								break;
							} else {
								nextStatement.deleteCharAt(nextStatement.length()-1);
								i = i - 1;									
							}
						}

						for (k = 0; k < nextStatement.length(); k++) {
							// Another assumption here: there are no words 
							// longer than 'threshold'
							if (chBuf[i] == ' ') {
								nextStatement.append(threadTranslate.joinMarker);
								threadTranslate.addText(nextStatement);
								break;
							} else {
								nextStatement.deleteCharAt(nextStatement.length()-1);
								i = i - 1;
							}
						}
					}
				}

				//Now, complete the line
				if (nextStatement.length() > 0) {
					ch = nextStatement.charAt(nextStatement.length()-1);
					if (ch != '.' && ch != '?' && ch != '!' ) {
						nextStatement.append('.');
					}
				}
				threadTranslate.addText(nextStatement);
			}

			fileReader.close();
			threadTranslate.Finish();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
}
