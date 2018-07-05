package gbr;

import java.util.StringTokenizer;

public class Node {
	
	public Node(String text, int count) {
		this.text = text;
		this.count = count;		
	}
	
	protected String text = "";
	protected int count = 0;
	
	
	public String getText() {
		return text;
	}
	
	public int getCount() {
		return count;
	}
	
	public void increaseCount() {
		count = count + 1;
	}
		
	private void addText(String str) {
		StringBuffer buf = new StringBuffer(text);
		buf.append('\uFFFF');
		buf.append(str);
		text = buf.toString();
	}	
	
	private Node left = null;
	private Node right = null;
	
	
	public Node getLeft() {
		return left;
	}
	
	public Node getRight() {
		return right;
	}
	
	public void addLeafByText(String str) {
		if (str == null)
			return;
		int comp = text.compareTo(str);
		if (comp == 0) {			
			increaseCount();
			return;
		}
		if (comp > 0) {
			if (left == null)
				left = new Node(str, 1);
			else 
				((Node)left).addLeafByText(str);
		} else {
			if (right == null)
				right = new Node(str, 1);
			else 
				((Node)right).addLeafByText(str);			
		}
	}
	
	public void addLeafByCount(String str, int count) {
		if (this.count == count) {
			addText(str);
			return;
		}
		if (this.count > count) {
			if (left == null)
				left = new Node(str, count);
			else 
				((Node)left).addLeafByCount(str, count);
		} else {
			if (right == null)
				right = new Node(str, count);
			else 
				((Node)right).addLeafByCount(str, count);			
		}
	}
	
	public static Node buildTreeByCount(Node node, Node tree) {
		if (node == null)
			return tree;
		if (node.getRight() != null)
			tree = buildTreeByCount(node.getRight(), tree);
		if (tree == null)
			tree = new Node(node.getText(), node.getCount());
		else
			tree.addLeafByCount(node.text, node.count);
		if (node.getLeft() != null)
			tree = buildTreeByCount(node.getLeft(), tree);
		return tree;
	}
	
	public void findMostUsedWords(TreeThread caller, StringBuffer buf) {
		if (caller.threshold <= 0)
			return;
		if (buf == null)		
			buf = new StringBuffer();
		if (right != null) 
			right.findMostUsedWords(caller, buf);
		if (caller.threshold <= 0)
			return;
		/*
		buf.append(text);
		buf.append('\uFFFF');
		caller.threshold = caller.threshold - 1;
		for (int i = 0; i < text.length(); i++)
			if (text.charAt(i) == '\uFFFF')		
				caller.threshold = caller.threshold - 1;
				*/
		StringTokenizer st = new StringTokenizer(text, "\uFFFF", true);
		while(st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s.equals("\uFFFF"))
				caller.threshold = caller.threshold - 1;
			if (caller.threshold <= 0)
				return;			
			buf.append(s);
		}
		buf.append("\uFFFF");
		caller.threshold = caller.threshold - 1;
		if (left != null) 
			left.findMostUsedWords(caller, buf);
		return;		
	}

	public void printOutSortedTree(StringBuffer buf, String title) {
		StringTokenizer st = new StringTokenizer(buf.toString(), "\uFFFF");
		Node sortTree = new Node ("", 1);
		while(st.hasMoreTokens()) {
			sortTree.addLeafByText(st.nextToken());
		}		
		synchronized(System.out) { 
			System.out.println(title + " Result:");
		printOutSortedTree(sortTree);
		System.out.println();
		}
	}
	
	private void printOutSortedTree(Node currentNode) {
		if (currentNode == null)
			return;		
		printOutSortedTree(currentNode.left);
		if (currentNode.text.length() > 0) {
			System.out.print("{");
			System.out.print(currentNode.text);
			System.out.print("} ");
		}
		printOutSortedTree(currentNode.right);								
	}	
	
}
