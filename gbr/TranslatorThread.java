package gbr;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TranslatorThread extends Thread {

	private StringBuffer finishMarker = new StringBuffer("__finish_Marker__");
	private ArrayList queue = new ArrayList();	
	public String joinMarker = "\uFFFF";
	private static int limitOfThreads = 100;
	public Integer countOfThreads = new Integer(0);

	public TranslatorThread() {
		// TODO Auto-generated constructor stub
	}

	public void addText(StringBuffer nextStatement) {
		if (nextStatement == null)
			return;
		if (nextStatement.length() == 0)
			return;
		try {
			synchronized(queue) {			
				queue.add(nextStatement.toString());					
				queue.notifyAll();	
				Thread.yield();
			}	
		}
		catch (Exception ex) {			
			System.err.println(ex);
			System.exit(1);
		}				
		nextStatement.delete(0, nextStatement.length());
	}

	private void printStr(String str) {
		if (str == null)
			return;
		if (str.endsWith(joinMarker))
			System.out.print(str.substring(0, str.length()-1));
		else						
			System.out.println(str);
	}

	public void run() {	
		boolean keepWorking = true;
		while(keepWorking) {
			if (countOfThreads >= limitOfThreads) {
				Thread.yield();
				continue;
			}
			String str = null;
			try {
				synchronized(queue) {
					while(queue.isEmpty()) {
						queue.wait();
					}			
					str = (String)queue.get(0);			
					queue.remove(0);

				}	
			}
			catch (Exception ex) {			
				System.err.println(ex);
				System.exit(1);
			}

			if (this.finishMarker.indexOf(str) >= 0)
				keepWorking = false;
			else 									
				(new Translator(str, this)).start();
					
			Thread.yield();
		}
	}

	public void Finish() {
		addText(this.finishMarker);

	}
	
	public void countOfThreadsInc() {
		try {
			synchronized(countOfThreads) {
				countOfThreads = countOfThreads + 1;
			}
		}
		catch (Exception ex) {			
			System.err.println(ex);
			System.exit(1);
		}		
	}

	public void countOfThreadsDec() {
		try {
			synchronized(countOfThreads) {
				countOfThreads = countOfThreads - 1;
			}
		}
		catch (Exception ex) {			
			System.err.println(ex);
			System.exit(1);
		}		
	}	


}
