import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The Tree class can create a Tree object. A Tree object has a root and a
 * nodeSet, a collection of the all the nodes in the tree. You can add a new
 * Node or add an existing nodeSet to the Tree object. You can also get back the
 * root or the nodeSet.
 *
 * @author Annie K. Lamar, Sarah Walling-Bell, Lia Chin-Purcell
 * @version 4.29.2019
 */
class Tree {
	private Node root; // root of the Tree
	private Set<Node> nodeSet; // set of all Nodes in the Tree

	/**
	 * Constructor for Tree objects.
	 *
	 * @param root the root of the Tree.
	 */
	public Tree(Node root) {
		this.root = root;
		nodeSet = new HashSet<Node>();
		nodeSet.add(root);
	}

	/**
	 * Returns the root of the Tree.
	 *
	 * @return the root of the Tree.
	 */
	public Node getRoot() {
		return root;
	}

	/**
	 * Returns the nodeSet of the Tree.
	 *
	 * @return the nodeSet of the Tree.
	 */
	public Set<Node> getNodeSet() {
		return nodeSet;
	}

	public void removeNode(Node remove) {
		nodeSet.remove(remove);
	}

	/**
	 * Adds a set of Nodes to the nodeSet of the Tree.
	 *
	 * @param nodes the set of Nodes to add to the nodeSet.
	 */
	public void addToNodeSet(Set<Node> nodes) {

		for (Node n : nodes) {
			boolean goodToAdd = true;
			for (Node node : nodeSet) {
				if (n.isLeaf() == false && node.isLeaf() == false && n.getType() == node.getType()
						&& n.getParent().equals(node.getParent())) {
					goodToAdd = false;
				}
			}
			if (goodToAdd == true)
				nodeSet.add(n);
		}
	}

	public ArrayList<Node> getTwigs() {
		ArrayList<Node> twigs = new ArrayList<Node>();
		for (Node n : nodeSet) {
			ArrayList<Node> children = honeyILostTheKids(n);
			boolean onlyChildren = true;
			for (Node child : children) {
				if (!child.isLeaf()) {
					onlyChildren = false;
				}
			}
			if (onlyChildren) {
				twigs.add(n);
			}
		}
		return twigs;
	}

	public ArrayList<Node> honeyILostTheKids(Node parent) {
		ArrayList<Node> children = new ArrayList<Node>();
		for (Node node : nodeSet) {
			if (node.hasParent() == true && node.getParent().equals(parent)) {
				children.add(node);
			}
		}
		return children;
	}

	public void replaceNodeSet(Set<Node> newSet) {
		nodeSet = newSet;
	}

}
