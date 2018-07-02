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
		buf.append(' ');
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
	
	public int findMostUsedWords(int threshold, StringBuffer buf) {
		if (threshold <= 0)
			return 0;
		if (buf == null)		
			buf = new StringBuffer();
		if (right != null) 
			threshold = right.findMostUsedWords(threshold, buf);
		if (threshold == 0)
			return 0;				
		buf.append(text);
		buf.append(' ');
		threshold = threshold - 1;
		if (left != null) 
			threshold = left.findMostUsedWords(threshold, buf);
		return threshold;		
	}

	public void printOutSortedTree(StringBuffer buf, String title) {
		StringTokenizer st = new StringTokenizer(buf.toString());
		Node sortTree = new Node (" ", 1);
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
		System.out.print("'");
		System.out.print(currentNode.text);
		System.out.print("' ");
		printOutSortedTree(currentNode.right);								
	}	
	
}
