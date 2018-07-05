package gbr;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class TreeThread extends Thread {

	private boolean populateTree = true;
	private String title = "";
	private boolean splitWords = false;	
	public int threshold;
	private Node tree = null;	
	private ArrayList queue;	
	
	private String finishPopulationMarker = "__finish_Population_Marker__";

	public TreeThread(String title, boolean splitWords, int threshold) {
		this.splitWords = splitWords;		
		this.threshold = threshold;
		this.title = title;
		queue = new ArrayList();
	}


	public void proceedTree() {
		addText(finishPopulationMarker);
	}


	public  void  addText(String str) {
		try {
			synchronized(queue) {			
				queue.add(str);					
				queue.notifyAll();	
				Thread.yield();
			}	
		}
		catch (Exception ex) {			
			System.err.println(ex);
			System.exit(1);
		}
	}		

	private void addLeaf() {
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

		if (str == null)
			return;

		if (finishPopulationMarker.equals(str))
			populateTree = false;

		if (splitWords) {
			StringTokenizer st = new StringTokenizer(str);
			while(st.hasMoreTokens()) {
				addLeafText(st.nextToken());
			}
		} else {
			addLeafText(str);
		}
		Thread.yield();
	}

	private void addLeafText(String str) {
		str = str.trim();
		if (str.length() == 0)			
			return;		
		if (tree == null)
			tree = new Node(str, 1);
		else
			tree.addLeafByText(str);
	}

	public void run() {		
		while(populateTree)
			addLeaf();
		if (tree == null) {
			System.out.println(title + ": Did not succeed to build the tree!");
			return;
		}
		System.out.println(title + ": Tree is loaded");
		Node treeCount = Node.buildTreeByCount(tree, null);		
		System.out.println(title + ": Count Tree is built");
		StringBuffer buf = new StringBuffer();		
		treeCount.findMostUsedWords(this, buf);
		treeCount.printOutSortedTree(buf, title);		
		return;

	}


}
