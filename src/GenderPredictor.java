import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Integer;

public class GenderPredictor {

	//private static final String TRAIN_FILE = "data/baseline/baseline_train.csv";
	//private static final String TRAIN_FILE = "data/oversample/increased_7525_train.csv";
	//private static final String TRAIN_FILE = "data/undersample/decreased_7525_train.csv";
	private static final String TRAIN_FILE = "data/ensemble/ensemble4_train.csv";

	//private static final String TEST_FILE = "data/baseline/baseline_test.csv";
	//private static final String TEST_FILE = "data/oversample/increased_7525_test.csv";
	//private static final String TEST_FILE = "data/undersample/decreased_7525_test.csv";
	private static final String TEST_FILE = "data/ensemble/ensemble4_test.csv";

	private static final int NUM_WORD_TYPES = 5;
	private static final int NUM_TRAIN_DATA = 1399; //You need to change this for every file
	private static final int NUM_TEST_DATA = 155;

	private static final int NN = 0;
	private static final int FW = 1;
	private static final int IN = 2;
	private static final int DET = 3;
	private static final int ADJ = 4;
	private static final int FEMALE = 5;
	private static final int MALE = 6;
	private static HashMap<Integer, Integer> valueRange;

	public static void main(String[] args) {
		//fill in valueRange HashMap with number of possible values for each attribute type
		valueRange = new HashMap<Integer, Integer>();
		for(int i = 0; i < NUM_WORD_TYPES; i++){
			if(i == NN){
				valueRange.put(i, 21); //21 values for NN
			}
			else{
				valueRange.put(i, 2); //2 values for all other word types
			}
		}

		//create a 2D called Data to store the data from the file
		int[][] data = readData(TRAIN_FILE, NUM_TRAIN_DATA);
		int[][] test = readData(TEST_FILE, NUM_TEST_DATA);

		//		//Train without K-fold
		//		System.out.println("Training tree....");
		//		Tree tree = trainTree(data, data, -1);
		//		printTree(tree);
		//		pruneTree(tree, data);
		//		printTree(tree);
		boolean doKFold = false; //fale = do Test Set Accuracy


		if(doKFold) {
			//Train with K-fold
			System.out.println("Evaluating accuracy....");

			//pull out 10% at a time
			int start = 0;
			int end = NUM_TRAIN_DATA / 10 - 1;
			int tenPercent = end+1;
			double masterAccuracy = 0;

			for (int i = 0; i < 10; i ++) {
				int examplesCounter = 0;
				int testCounter = 0;
				int[][] examples = new int[data.length - tenPercent][NUM_WORD_TYPES];
				int[][] validationSet = new int[tenPercent][NUM_WORD_TYPES];

				for (int e = 0; e < data.length; e ++) {
					if (!(e >= start && e <= end)) {
						examples[examplesCounter] = data[e];
						examplesCounter ++;
					} else {
						validationSet[testCounter] = data[e];
						testCounter ++;
					}
				}

				//train tree and don't prune
				Tree eval = trainTree(examples, examples, -1); //Test Set Accuracy
				//Tree prunedTree = pruneTree(eval, validationSet); //use this to do pruned testing
				Tree prunedTree = eval; //use this to do unpruned testing

				//for each example in test set, search the tree and store the result
				int[][] results = new int[validationSet.length][2];
				for (int k = 0; k < validationSet.length; k ++) {
					int result = searchTree(validationSet[k], prunedTree);
					results[k][0] = validationSet[k][NUM_WORD_TYPES];
					results[k][1] = result;
				}
				//find number correct by comparing to truth output value
				int correct = 0;
				for (int j = 0; j < results.length; j ++) {
					if (results[j][0] == results[j][1])  correct ++;
				}
				//update accuracy
				double acc = (double) correct / tenPercent;
				masterAccuracy += acc;
				//move start and end points
				start += tenPercent;
				end += tenPercent;
			} //end K-fold
			System.out.println("Average accuracy WITHOUT pruning: " + masterAccuracy*10+ "%");
			//System.out.println("Average accuracy: " + acc*100+ "%");


			//pull out 10% at a time
			start = 0;
			end = NUM_TRAIN_DATA / 10 - 1;
			tenPercent = end+1;
			masterAccuracy = 0;
			for (int i = 0; i < 10; i ++) {
				int examplesCounter = 0;
				int testCounter = 0;
				int[][] examples = new int[data.length - tenPercent][NUM_WORD_TYPES];
				int[][] validationSet = new int[tenPercent][NUM_WORD_TYPES];

				for (int e = 0; e < data.length; e ++) {
					if (!(e >= start && e <= end)) {
						examples[examplesCounter] = data[e];
						examplesCounter ++;
					} else {
						validationSet[testCounter] = data[e];
						testCounter ++;
					}
				}

				//train tree and prune
				Tree eval = trainTree(examples, examples, -1); //Test Set Accuracy
				Tree prunedTree = pruneTree(eval, validationSet); //use this to do pruned testing
				//Tree prunedTree = eval; //use this to do unpruned testing

				//for each example in test set, search the tree and store the result
				int[][] results = new int[validationSet.length][2];
				for (int k = 0; k < validationSet.length; k ++) {
					int result = searchTree(validationSet[k], prunedTree);
					results[k][0] = validationSet[k][NUM_WORD_TYPES];
					results[k][1] = result;
				}
				//find number correct by comparing to truth output value
				int correct = 0;
				for (int j = 0; j < results.length; j ++) {
					if (results[j][0] == results[j][1])  correct ++;
				}
				//update accuracy
				double acc = (double) correct / tenPercent;
				masterAccuracy += acc;
				//move start and end points
				start += tenPercent;
				end += tenPercent;
			} //end K-fold
			System.out.println("Average accuracy with pruning: " + masterAccuracy*10+ "%");
			//System.out.println("Average accuracy: " + acc*100+ "%");
		}




		if(!doKFold) {
			//Test Accuracy
			System.out.println("Evaluating accuracy....");

			//DON'T PRUNE
			//Train tree on train data:
			int[][] examplesTestAcc = new int[data.length][NUM_WORD_TYPES];
			int[][] validationSetTestAcc = new int[test.length][NUM_WORD_TYPES];


			for (int e = 0; e < data.length; e ++) {
					examplesTestAcc[e] = data[e];
			}


			for (int t = 0; t < test.length; t ++) {
				validationSetTestAcc[t] = test[t];
			}

			//don't prune:
			Tree evalTestAcc = trainTree(examplesTestAcc, examplesTestAcc, -1);
			Tree prunedTreeTestAcc = evalTestAcc;
			System.out.println("Unpruned Tree:");
			printTree(prunedTreeTestAcc);

			//test test data on tree:
			//for each example in test set, search the tree and store the result
			int[][] resultsTestAcc = new int[validationSetTestAcc.length][2];
			for (int k = 0; k < validationSetTestAcc.length; k ++) {
				int result = searchTree(validationSetTestAcc[k], prunedTreeTestAcc);
				resultsTestAcc[k][0] = validationSetTestAcc[k][NUM_WORD_TYPES];
				resultsTestAcc[k][1] = result;
			}
			//find number correct by comparing to truth output value
			int correctTestAcc = 0;
			for (int j = 0; j < resultsTestAcc.length; j ++) {
				if (resultsTestAcc[j][0] == resultsTestAcc[j][1])  correctTestAcc ++;
			}
			//update accuracy
			double accuracyTestAcc = (double) correctTestAcc / validationSetTestAcc.length;


			//print accuracy:
			System.out.println("Test accuracy WITHOUT pruning: " + accuracyTestAcc*100+ "%");



			//PRUNE
			//Train tree on train data:
			int[][] examplesTestAcc2 = new int[data.length][NUM_WORD_TYPES];
			int[][] validationSetTestAcc2 = new int[test.length][NUM_WORD_TYPES];

			for (int e = 0; e < data.length; e ++) {
					examplesTestAcc2[e] = data[e];
			}


			for (int t = 0; t < test.length; t ++) {
				validationSetTestAcc2[t] = test[t];
			}

			//prune and don't prune:
			Tree evalTestAcc2 = trainTree(examplesTestAcc2, examplesTestAcc2, -1);
			Tree prunedTreeTestAcc2 = pruneTree(evalTestAcc2, validationSetTestAcc2);
			System.out.println("Pruned Tree:");
			printTree(prunedTreeTestAcc2);

			//test test data on tree:
			//for each example in test set, search the tree and store the result
			int[][] resultsTestAcc2 = new int[validationSetTestAcc2.length][2];
			for (int k = 0; k < validationSetTestAcc2.length; k ++) {
				int result = searchTree(validationSetTestAcc2[k], prunedTreeTestAcc2);
				resultsTestAcc2[k][0] = validationSetTestAcc2[k][NUM_WORD_TYPES];
				resultsTestAcc2[k][1] = result;
			}
			//find number correct by comparing to truth output value
			int correctTestAcc2 = 0;
			for (int j = 0; j < resultsTestAcc2.length; j ++) {
				if (resultsTestAcc2[j][0] == resultsTestAcc2[j][1])  correctTestAcc2 ++;
			}
			//update accuracy
			double accuracyTestAcc2 = (double) correctTestAcc2 / validationSetTestAcc2.length;


			//print accuracy:
			System.out.println("Test accuracy WITH pruning: " + accuracyTestAcc2*100+ "%");

		}


	}

	/*****************************************************
	* 					PRIVATE METHODS
	*****************************************************/


	private static ArrayList<Node> getNodesBFS(Tree tree, Node start){
		ArrayList<Node> forwardSet = new ArrayList<Node>();

		Queue<Node> q = new LinkedList<Node>();
		HashMap<Node, Boolean> visited = new HashMap<Node, Boolean>();
		Set<Node> nodes = tree.getNodeSet();
		for (Node node : nodes) {
			visited.put(node, false);
		}
		q.add(start);

		while (!q.isEmpty()) {
			Node n = q.remove();
			if (!visited.get(n)) {
				visited.put(n, true);
				if (!forwardSet.contains(n)) {
					forwardSet.add(n);
				}
				ArrayList<Node> children = tree.honeyILostTheKids(n);
				for (Node child : children) {
					if (!visited.get(child)) {
						q.add(child);
						if (!forwardSet.contains(child)) {
							forwardSet.add(child);
						}
					}
				}
			}
		}
		return forwardSet;
	}

	private static ArrayList<Node> getBackwardsNodeSet(Tree tree, Node start){
		ArrayList<Node> forwardSet = getNodesBFS(tree, start);
		ArrayList<Node> backwardSet = new ArrayList<Node>();
		for(int i = forwardSet.size()-1; i >= 0; i--){
			backwardSet.add(forwardSet.get(i));
		}
		return backwardSet;
	}


	/**
	* To prune the train, we follow these steps:
	* While pruning is still improving the Tree,
	* 1. Get a list of twigs in the tree. Twigs are nodes whose children are leaves.
	* 2a. Calculate the accuracy on the test set if we did not prune that node.
	* 2b. Calculate the accuracy on the test set if we did prune that node.
	* 3. If we choose to not prune, we move on to the next node.
	* 4. If we choose to prune, replace the tree with the prunedTree.
	* @param eval
	* @param test
	* @return
	*/
	private static Tree pruneTree(Tree eval, int[][] testData){

		boolean stillImproving = false;
		double bestAccuracy = 0.0;
		do {
			stillImproving = false;
			//System.out.println("Still pruning...snip snip...");
			ArrayList<Node> twigs = eval.getTwigs();
			for (Node twig : twigs) {
				double unprunedAccuracy = getAccuracy(eval, testData);
				//Tree treeToPrune = eval;
				Tree prunedTree = prune(eval, twig, testData);
				double prunedAccuracy = getAccuracy(prunedTree, testData);

				if (prunedAccuracy > unprunedAccuracy) {
					//System.out.println("Pruned a twig!");
					stillImproving = true;
					eval = prunedTree;
					//bestAccuracy = prunedAccuracy;
				}
				//System.out.println("Best accuracy: " + bestAccuracy);
			}
		}
		while (stillImproving);
		return eval;
	}

	private static Tree prune(Tree tree, Node node, int[][] testData) {
		Set<Node> nodes = tree.getNodeSet();
		for (Node n : nodes) {
			if (n.equals(node)) {
				int pluralityType = childrensPluralityType(n, tree);

				//System.out.println("Setting node type: " + childrensPluralityType(n, tree));
				//set that Node to be a leaf node
				n.setLeaf(true);
				//store that Node's children
				ArrayList<Node> children = tree.honeyILostTheKids(n);
				//remove that node's children from the tree's nodeSet
				for (Node child : children) {
					tree.removeNode(child);
				}

				//set that Node's attributeType to the plurality type
				if (pluralityType == 5 || pluralityType ==6) n.setType(pluralityType);
				else {
					//need to figure out what to set plurality type to

					n.setType(MALE);
					Set<Node> maleStorage = nodes;
					tree.replaceNodeSet(maleStorage);
					double maleAccuracy = getAccuracy(tree, testData);

					n.setType(FEMALE);
					Set<Node> femaleStorage = nodes;
					tree.replaceNodeSet(femaleStorage);
					double femaleAccuracy = getAccuracy(tree, testData);

					if (femaleAccuracy >= maleAccuracy) {
						tree.replaceNodeSet(femaleStorage);
						n.setType(FEMALE);
					}
					else {
						tree.replaceNodeSet(maleStorage);
						n.setType(MALE);
					}
				}
				break;
			}
		}

		return tree;
	}

	private static int childrensPluralityType(Node node, Tree tree) {
		ArrayList<Node> children = tree.honeyILostTheKids(node);
		int females = 0;
		int males = 0;
		for (Node child : children) {
			if (child.getType() == FEMALE) {
				females ++;
			} else {
				males ++;
			}
		}
		if (females > males) {
			return FEMALE;
		} else if (males > females) {
			return MALE;
		}
		return -1;
	}


	private static double getAccuracy(Tree tree, int[][] test){

		int[][] results = new int[test.length][2];
		//classify the test data according to the tree
		for (int k = 0; k < test.length; k ++) {
			int result = searchTree(test[k], tree);
			results[k][0] = test[k][NUM_WORD_TYPES];
			results[k][1] = result;
		}

		//calculate the number of correctly classified examples
		int correct = 0;
		for (int j = 0; j < results.length; j ++) {
			if (results[j][0] == results[j][1])  correct ++;
		}

		//calculate accuracy
		double acc = (double) correct / test.length;
		return acc;
	}

	/**
	* Search the decision tree with the input example.
	* @param example the example to search the Decision tree with
	* @param tree the tree to search
	* @return Democrat or Republican
	*/
	private static int searchTree(int[] example, Tree tree) {
		Node currentNode = tree.getRoot();
		while (!currentNode.isLeaf()) {

			int wordType = currentNode.getType();
			int wordVal = example[wordType];
			ArrayList<Node> children = tree.honeyILostTheKids(currentNode);

			int leastDiff = 20;
			int indexLeastDiff = 0;

			for (int i = 0; i < children.size(); i++) {
				if (Math.abs(children.get(i).getValue() - wordVal) < leastDiff) {
					leastDiff = Math.abs(children.get(i).getValue() - wordVal);
					indexLeastDiff = i;
				}
			}
			//			System.out.println("SearchTree children size=" + children.size());
			//			System.out.println("current type=" + currentNode.getType());

			currentNode = children.get(indexLeastDiff);

		}
		return currentNode.getType();
		// return -1;
	}

	/**
	* Print out the tree.
	* @param tree the tree to print
	*/
	private static void printTree(Tree tree) {
		depthFirstSearch(tree, tree.getRoot());
	}

	/**
	* Use depth first search to print out the tree.
	* @param tree the tree to search
	* @param start the start node
	*/
	private static void depthFirstSearch(Tree tree, Node start) {
		Stack<Node> stack = new Stack<Node>();
		Set<Node> nodes = tree.getNodeSet();
		HashMap<Node, Boolean> visited = new HashMap<Node, Boolean>();
		for (Node node : nodes) {
			visited.put(node, false);
		}
		stack.push(start);
		start.changeTabCount(0);
		while (!stack.isEmpty()) {
			Node u = stack.pop();
			if (!visited.get(u)) {
				visited.put(u, true);
				//print information about the Node we just found!
				if (u.isLeaf()) {
					System.out.println(getTabs(u) + u.getValue() + " " + printOutputType(u));
				} else if (u.getValue() == -1) { //root node
					System.out.println(printWordType(u.getType()));
				} else { //not a leaf
					System.out.println(getTabs(u) + u.getValue() + printWordType(u.getType()));
				}
				ArrayList<Node> children = tree.honeyILostTheKids(u);
				for (Node child : children) {
					stack.push(child);
					child.changeTabCount(u.getTabCount()+1);
				}
			}
		}
	}



	private static String printWordType(int type){
		if(type == NN){
			return " Nouns:";
		}
		else if(type == FW){
			return " Foreign Words:";
		}
		else if(type == IN){
			return " Prepositions:";
		}
		else if(type == DET){
			return " Determiners:";
		}
		else if(type == ADJ){
			return " Adjectives:";
		}
		else{
			return "ERROR:";
		}
	}

	private static String printOutputType(Node u) {
		if (u.getType() == FEMALE) return "Female";
		return "Male";
	}

	private static String getTabs(Node u) {
		String s = new String();
		int counter = u.getTabCount();
		for (int i = 0; i < counter; i ++) {
			s += "\t";
		}
		return s;
	}

	// Initializes and populates the data matrix from file.
	private static int[][] readData(String fileName, int length){
		System.out.println("Read Data");
		int femCounter = 0;
		int maleCounter = 0;

		int[][] data = new int[length][NUM_WORD_TYPES+1];
		try{
			int dataRow = 0;
			Scanner scan = new Scanner(new File(fileName));
			while(scan.hasNextLine()){
				assert(dataRow < length);
				String line = scan.nextLine();
				String[] info = line.split(",");
				assert(info.length == 6);

				// Store the voting record for this particular person
				for(int i = 0; i < info.length; i++){
					if(i < NUM_WORD_TYPES){
						data[dataRow][i] = Integer.parseInt(info[i]);
						//					System.out.print(data[dataRow][i] + " ");
						assert(i < data[dataRow].length);
					}
					else{
						// Store the label (democrat or republican) for this person
						if(info[5].equals("Male")){
							data[dataRow][i] = MALE;
							maleCounter++;
						}
						else if(info[5].equals("Female")){
							data[dataRow][i] = FEMALE;
							femCounter++;
						}
						else{
							System.out.println("Found neither Male nor Female");
						}
					}
				}
				//			System.out.println(Arrays.toString(data[dataRow]));
				dataRow++;
			}
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println(e);
		}
		System.out.println("females " + femCounter);
		System.out.println("males " + maleCounter);
		return data;
	}

	//train tree with whole dataset
	private static Tree trainTree(int[][] examples, int[][] parent_examples, int value) {
		//base cases
		if (examples.length == 0) {
			//Plurality value needs to return a node with TYPE = plurality type (Fem, Male), and VALUE = value, IS A LEAF NODE
			return new Tree(pluralityValue(parent_examples, value));
		}
		else if (sameClassification(examples)) {
			int output = examples[0].length -1;
			//return a Tree with one node with remaining type, VALUE, and ISLEAFNODE
			return new Tree(new Node(examples[0][output], value, true));
		}
		else if ((examples[0].length-1) == 0) { //no more attributes left
			//System.out.println("Base case 3, value: " + value);
			return new Tree(pluralityValue(examples, value));
		}
		//non-base case
		else {
			int A;
			if(value == NN){
				//System.out.println("getting NN");
				A = maxImportanceNN(examples);
			}
			else{
				A = maxImportance(examples);
			}

			Node root = new Node(A, -1, false);
			Tree tree = new Tree(root);

			int valRange = valueRange.get(A);
			for (int i = 0; i < valRange; i ++) { //for each value in A
				int[][] exs = getAllExamples(examples, A, i);

				if (exs.length != 0) {
					exs = removeAttribute(exs, A);
					Tree subtree = trainTree(exs, examples, i);
					//printTree(subtree);
					//set the parent of the root of subtree to root of Tree
					subtree.getRoot().setParent(root);
					//set the value of that node to be i
					subtree.getRoot().setValue(i);
					//copy the subTree's nodeSet to the Tree's nodeSet
					tree.addToNodeSet(subtree.getNodeSet());
				} else {
					if(value >= 0 )return new Tree(pluralityValue(examples, value));
				}
			}
			return tree;
		}
	}

	/**
	* Returns a new node with the value of the most common output of all examples.
	* @param examples
	* @param value
	* @return
	*/
	private static Node pluralityValue(int[][] examples, int value) {
		int output = examples[0].length -1;
		int pluralityType;
		int numFemale = 0;
		int numMale = 0;
		for (int[] array : examples) {
			if (array[output] == FEMALE) numFemale ++;
			if (array[output] == MALE) numMale ++;
		}
		if (numMale > numFemale) pluralityType = MALE;
		else pluralityType = FEMALE;
		Node newNode = new Node(pluralityType, value, true);
		return newNode;
	}

	/***************************************************************************/



	private static int[][] removeAttribute(int[][] examples, int attributeToRemove) {
		//return a new set of examples with that attribute removed
		//attributeToRemove is a COLUMN NUMBER
		int[][] newExamples = new int[examples.length][examples[0].length-1];
		for (int array = 0; array < examples.length; array ++) {
			int columnCounter = 0;
			for (int column = 0; column < examples[0].length; column++) {
				if (column != attributeToRemove) {
					newExamples[array][columnCounter] = examples[array][column];
					columnCounter ++;
				}
			}
		}
		return newExamples;
	}

	/**
	* Returns all examples with the desired value of the desired attribute.
	* @param examples set of examples
	* @param colNumber the attribute number
	* @param desiredNumber the value of the attribute
	* @return all examples with the desired value of the desired attribute
	*/
	private static int[][] getAllExamples(int[][] examples, int colNumber, int desiredNumber) {
		int counter = 0;
		for (int[] array : examples) {
			if (array[colNumber] == desiredNumber) counter ++;
		}
		int[][] newExamples = new int[counter][NUM_WORD_TYPES];
		int newCounter = 0;
		for (int[] array : examples) {
			if (array[colNumber] == desiredNumber) {
				newExamples[newCounter] = array;
				newCounter ++;
			}
		}
		return newExamples;
	}

	/**
	* Returns true if all remaining examples have the same output.
	* @param examples examples to consider
	* @return true if all remaining examples have the same output, false otherwise
	*/
	private static boolean sameClassification(int[][] examples) {
		int output = examples[0].length - 1;
		int firstValue = 0;
		boolean firstLoop = true;
		for (int[] array : examples) {
			if (firstLoop) { //first pass through the loop
				firstValue = array[output];
				firstLoop = false;
			} else if (array[output] != firstValue) {
				return false;
			}
		}
		return true;
	}

	private static int maxImportanceNN(int[][] examples){
		/**
		* Step one: calculate entropy of entire set
		* H(S)=(-1)[(#REP/#tot)*log_2(#REP/#tot) + (#DEM/#tot)*log_2(#DEM/#tot)]
		*/
		double numFemale = count(examples, FEMALE);
		double numMale = count(examples, MALE);

		double total = examples.length;

		double femDivide = numFemale/total;
		double malDivide = numMale/total;

		double logFemDivide = Math.log(femDivide) / Math.log(2);
		double logMalDivide = Math.log(malDivide) / Math.log(2);

		double H = (-1)*((femDivide*logFemDivide) + (malDivide*logMalDivide));

		/**
		* Step two: calculate R(A) for each attribute (i.e. column in this case)
		*/
		double[] RAvals = new double[examples[0].length-1];
		for (int a = 0; a < examples[0].length - 1; a ++) {

			double R = 0.0;

			for(int v = 0; v < 21; v++){ //v is the value with range: [0, 20]
				//Step 2.1 calculate H(Sk) for each value of attribute a
				int[][] valExamples = getAllExamples(examples, a, v);
				numFemale = count(valExamples, FEMALE);
				numMale = count(valExamples, MALE);

				total = valExamples.length; //how many zeros on issue a

				femDivide = numFemale/total;
				malDivide = numMale/total;

				logFemDivide = Math.log(femDivide) / Math.log(2);
				logMalDivide = Math.log(malDivide) / Math.log(2);

				double Hval = (-1)*((femDivide*logFemDivide) + (malDivide*logMalDivide));
				if (Double.isNaN(Hval)) Hval = 0.0;


				//Step 2.2 calculate R(A) for attribute a
				double numVal = countAttributeValues(examples, a, v);
				total = examples.length;
				double valPortion = (numVal/total)*Hval;
				R += valPortion;
			}
			RAvals[a] = R;
		}
		/**
		* Step three: calculate information gain for each attribute
		*/
		double[] IG = new double[examples[0].length-1];
		for (int i = 0; i < RAvals.length; i ++) {
			IG[i] = H - RAvals[i];
		}
		/**
		* Step four: get max information gain
		*/
		double max = -100;
		boolean firstTime = true;
		int attributeNumber = Integer.MIN_VALUE;
		for (int g = 0; g < IG.length; g ++) {
			if (firstTime == true) {
				max = IG[g];
				attributeNumber = g;
				firstTime = false;
			}
			else if (IG[g] > max) {
				max = IG[g];
				attributeNumber = g;
			}
		}
		return attributeNumber;
	}


	private static int maxImportance(int[][] examples) {
		/**
		* Step one: calculate entropy of entire set
		* H(S)=(-1)[(#REP/#tot)*log_2(#REP/#tot) + (#DEM/#tot)*log_2(#DEM/#tot)]
		*/
		double numFemale = count(examples, FEMALE);
		double numMale = count(examples, MALE);

		double total = examples.length;

		double femDivide = numFemale/total;
		double malDivide = numMale/total;

		double logFemDivide = Math.log(femDivide) / Math.log(2);
		double logMalDivide = Math.log(malDivide) / Math.log(2);

		double H = (-1)*((femDivide*logFemDivide) + (malDivide*logMalDivide));

		/**
		* Step two: calculate R(A) for each attribute (i.e. column in this case)
		*/
		double[] RAvals = new double[examples[0].length-1];
		for (int a = 0; a < examples[0].length - 1; a ++) {

			//Step 2.1 calculate H(Sk) for each value of attribute a
			//value: zero
			int[][] zeroExamples = getAllExamples(examples, a, 0);
			numFemale = count(zeroExamples, FEMALE);
			numMale = count(zeroExamples, MALE);

			total = zeroExamples.length; //how many zeros on issue a

			femDivide = numFemale/total;
			malDivide = numMale/total;

			logFemDivide = Math.log(femDivide) / Math.log(2);
			logMalDivide = Math.log(malDivide) / Math.log(2);

			double Hzero = (-1)*((femDivide*logFemDivide) + (malDivide*logMalDivide));
			if (Double.isNaN(Hzero)) Hzero = 0.0;

			//value: num
			int[][] numExamples = getAllExamples(examples, a, 1);
			numFemale = count(numExamples, FEMALE);
			numMale = count(numExamples, MALE);

			total = numExamples.length; //how many zeros on issue a

			femDivide = numFemale/total;
			malDivide = numMale/total;

			logFemDivide = Math.log(femDivide) / Math.log(2);
			logMalDivide = Math.log(malDivide) / Math.log(2);

			double Hnum = (-1)*((femDivide*logFemDivide) + (malDivide*logMalDivide));
			if (Double.isNaN(Hnum)) Hnum = 0.0;

			//Step 2.2 calculate R(A) for attribute a
			double numzero = countAttributeValues(examples, a, 0);
			double numnum = countAttributeValues(examples, a, 1);
			total = examples.length;
			double zeroPortion = (numzero/total)*Hzero;
			double numPortion = (numnum/total)*Hnum;
			double R = zeroPortion + numPortion;
			RAvals[a] = R;
		}
		/**
		* Step three: calculate information gain for each attribute
		*/
		double[] IG = new double[examples[0].length-1];
		for (int i = 0; i < RAvals.length; i ++) {
			IG[i] = H - RAvals[i];
		}
		/**
		* Step four: get max information gain
		*/
		double max = -100;
		boolean firstTime = true;
		int attributeNumber = Integer.MIN_VALUE;
		for (int g = 0; g < IG.length; g ++) {
			if (firstTime == true) {
				max = IG[g];
				attributeNumber = g;
				firstTime = false;
			}
			else if (IG[g] > max) {
				max = IG[g];
				attributeNumber = g;
			}
		}
		return attributeNumber;
	}

	private static double count(int[][] examples, int output) {
		double number = 0.0;
		for (int[] array : examples) {
			if (array[examples[0].length-1] == output) number += 1.0;
		}
		return number;
	}

	private static double countAttributeValues(int[][] examples, int attribute, int input) {
		double count = 0.0;
		for (int[] array : examples) {
			if (array[attribute] == input) count += 1.0;
		}
		return count;
	}

}
