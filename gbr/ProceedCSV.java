package gbr;
import java.io.*;
import java.util.StringTokenizer;

/*
 * This is a test to check my binary search tree code 
 */

public class ProceedCSV {

	public static void main(String[] args) {
		String filename = "Reviews.csv";
		FileInputStream file;
		Node treeOfUID = null;
		try {
			file = new FileInputStream(filename);
			InputStreamReader fileReader = new InputStreamReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);	
			bufferedReader.readLine(); //first line is meta-data
			String line;
			StringTokenizer st;			
			while ((line = bufferedReader.readLine()) != null) {					
				st = new StringTokenizer(line, ",");
				st.nextToken();
				st.nextToken();
				String userID = st.nextToken(); //third
				if (treeOfUID == null) {
					treeOfUID = new Node(userID, 1);
				} else {
					treeOfUID.addLeafByText(userID);
				}
			}
			fileReader.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (treeOfUID == null) {
			System.err.println("Did not succeed to build the tree!");
			return;
		} else {
			System.out.println("Tree is loaded");
		}

		Node treeCount = Node.buildTreeByCount(treeOfUID, null);
		System.out.println("Count Tree is built");
		StringBuffer buf = new StringBuffer();
		treeCount.findMostUsedWords(10, buf);		
		treeCount.printOutSortedTree(buf, "");
	}

}
