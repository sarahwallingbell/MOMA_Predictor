
/**
 * Nested Node class.
 *
 * @author Annie K. Lamar, Sarah Walling-Bell, Lia Chin-Purcell
 * @version 4.25.2019
 */
class Node {

	/**
	 * If the node is a non-leaf node, the attributeType is the issue number. In
	 * other examples, an attributeType is Patrons?, Hungry? Fri/Sat? If the node is
	 * a leaf node, the attributeType is 3 (REP) or 4 (DEM). In other examples, this
	 * is Yes/no. In figure 18.6, the value of the Hungry node is Full, and the type
	 * is "Hungry.". The attributeValue is 0 (zero), 1 (num), 2 (OTH). In other
	 * examples, this is "Italian." IT IS THE ATTRIBUTE YOU SPLIT ON TO GET TO THIS
	 * NODE.
	 */
	private int attributeType; // issue number for non-leaf nodes, for leaf-nodes, this is the output
	private int attributeValue; // the int that represents the value of the attribute
	private boolean isLeafNode;
	private Node parent;
	private int tabMeBaby;

	/**
	 * First constructor for Node objects.
	 *
	 * @param attributeType  attribute number for non-leaf nodes, output for leaf
	 *                       nodes
	 * @param attributeValue attribute that got us to this node
	 * @param isLeafNode     boolean to represent if this is a leaf node
	 */
	public Node(int attributeType, int attributeValue, boolean isLeafNode) {
		this.attributeType = attributeType;
		this.attributeValue = attributeValue;
		this.isLeafNode = isLeafNode;
	}

	/**
	 * Second constructor for Node objects.
	 *
	 * @param attributeType  attribute number for non-leaf nodes, output for leaf
	 *                       nodes
	 * @param attributeValue attribute that got us to this node
	 * @param isLeafNode     boolean to represent if this is a leaf node
	 * @param parent         the parent of the new Node
	 */
	public Node(Node parent, int attributeType, int attributeValue, boolean isLeafNode) {
		this.attributeType = attributeType;
		this.attributeValue = attributeValue;
		this.parent = parent;
		this.isLeafNode = isLeafNode;
		// tabMeBaby = parent.getTabCount() + 1;
	}

	public int getTabCount() {
		return tabMeBaby;
	}

	public void changeTabCount(int newCount) {
		tabMeBaby = newCount;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node newParent) {
		parent = newParent;
	}

	public int getType() {
		return attributeType;
	}

	public int getValue() {
		return attributeValue;
	}

	public void setValue(int newValue) {
		attributeValue = newValue;
	}

	public void setType(int newType) {
		attributeType = newType;
	}

	public void setLeaf(boolean leafVal) {
		isLeafNode = leafVal;
	}

	boolean isLeaf() {
		return isLeafNode;
	}

	public boolean hasParent() {
		if (parent != null)
			return true;
		return false;
	}

//	public boolean equals(Node other) {
//		if (isLeafNode == other.isLeaf() && attributeType == other.getType() && attributeValue == other.getValue()) {
//			return true;
//		}
//		return false;
//	}

	public boolean parentEquals(Node other) {
		if (isLeafNode == other.isLeaf() && attributeType == other.getType() && attributeValue == other.getValue()) {
			return true;
		}
		return false;
	}
}
