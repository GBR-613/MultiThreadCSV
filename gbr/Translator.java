package gbr;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Translator extends Thread {

	private String str = null;
	private TranslatorThread caller;

	public Translator(String str, TranslatorThread caller) {
		this.str = str;
		this.caller = caller;
	}


	public void run() {
		caller.countOfThreadsInc();

		try {		
			String url = "https://api.google.com/translate";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Type", "application/json");

			StringBuffer content = new StringBuffer("{input_lang: ‘en’, output_lang: ‘fr’, text: \"");
			content.append(str);
			content.append("\"}");

			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());		
			wr.writeBytes(content.toString());
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				System.out.println("HTTP response code: " + responseCode);			
			} else {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				//print out the translation 
				System.out.println(response.substring(8, response.length()-2));				
			}

		} catch (Exception e1) {
			System.err.println(e1);
		} finally {		
			caller.countOfThreadsDec();			
		}

	}
}
