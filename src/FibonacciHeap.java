import java.util.Iterator;

/**
 * FibonacciHeap
 * <p>
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap implements Iterable<FibonacciHeap.HeapNode> {

	public static int totalLinks = 0;
	public static int totalCuts = 0;

	private HeapNode sentinel;
	private HeapNode min;
	private int size;
	private int totalTrees;
	private int totalMarkedNodes;

	public FibonacciHeap() {
		this.sentinel = createSentinel();
		this.min = null;
		this.size = 0;
		this.totalTrees = 0;
		this.totalMarkedNodes = 0;
	}

	/**
	 * creates a dummy node with null key, that its right and left fields point to itself
	 */
	private HeapNode createSentinel() {
		HeapNode sentinel = new HeapNode();
		sentinel.right = sentinel;
		sentinel.left = sentinel;
		return sentinel;
	}

	/**
	 * The method returns true if and only if the heap is empty.
	 */
	public boolean empty() {
		return this.size == 0;
	}

	/**
	 * Creates a node (of type HeapNode) which contains the given key, and
	 * inserts it into the heap.
	 */
	public HeapNode insert(int key) {
		//create the node
		HeapNode node = new HeapNode(key);
		//append the node as a new root to the root list
		this.sentinel.appendSibling(node);
		// update minimum pointer if necessary
		if (this.min == null || node.key < this.min.key) {
			this.min = node;
		}
		this.size++;
		this.totalTrees++;
		return node;
	}

	/**
	 * Delete the node containing the minimum key.
	 */
	public void deleteMin() {
		if (this.min == null) {
			// heap is empty, no action needed
			return;
		}

		// make a root out of each of the minimum node’s children
		for (HeapNode child : this.min) {
			this.sentinel.appendSibling(child);
			child.parent = null;
		}
		//delete the minimum
		this.sentinel.deleteSibling(this.min);
		this.min = null; // consolidate function will find the new minimum
		this.size--;
		consolidate();
	}

	/**
	 * consolidates the root list by linking roots of equal degree until at most one root remains of each degree
	 * this function assumes that a rank of a node in a fibonacci heap is at most
	 * log_goldenRatio(size) = 4.78*log10(size) < 5*log10(size)
	 */
	private void consolidate() {
		if (this.size == 0) {
			// heap is empty, no action needed
			return;
		}

		// initialize an array with size of maximal possible rank
		int arraySize = (int) (5 * Math.log10(this.size + 1));
		HeapNode[] treesByRank = new HeapNode[arraySize];

		for (HeapNode root : this) {
			int rank = root.rank;
			// while there are other trees with the same rank, link them
			while (treesByRank[rank] != null) {
				HeapNode x = treesByRank[rank];
				root = link(x, root);
				treesByRank[rank] = null;
				rank++;
			}
			// store tree in the array with index = rank
			treesByRank[rank] = root;
		}

		// clear the root list and append the roots from the the list
		this.sentinel = createSentinel();
		this.totalTrees = 0;
		for (int i = 0; i < treesByRank.length; i++) {
			HeapNode tree = treesByRank[i];
			if (tree != null) {
				this.sentinel.appendSibling(tree);
				this.totalTrees++;
				if (this.min == null || tree.key < this.min.key) {
					this.min = tree;
				}
			}
		}
	}

	/**
	 * links 2 trees of the same rank by making the tree with the bigger root key a child of the other.
	 *
	 * @return the linked tree
	 * @precondition - the 2 trees are of the same rank
	 */
	private HeapNode link(HeapNode y, HeapNode x) {
		if (x.key > y.key) {
			return link(x, y);
		} else {
			this.sentinel.deleteSibling(y);
			x.child.appendSibling(y);
			x.rank++;
			y.parent = x;
			y.isMarked = false;
			totalLinks++;
			return x;
		}
	}

	/**
	 * Return the node of the heap whose key is minimal.
	 */
	public HeapNode findMin() {
		return this.min;
	}

	/**
	 * Meld the heap with heap2
	 */
	public void meld(FibonacciHeap heap2) {
		if (heap2.size == 0) {
			return;
		} else if (this.size == 0) {
			this.min = heap2.min;
			this.sentinel = heap2.sentinel;
			this.size = heap2.size;
		} else {
			if (heap2.min.key < this.min.key) {
				this.min = heap2.min;
			}
			this.size += heap2.size;
			heap2.sentinel.right.left = this.sentinel;
			HeapNode temp = this.sentinel.right;
			this.sentinel.right = heap2.sentinel.right;
			heap2.sentinel.left.right = temp;
		}
	}

	/**
	 * Return the number of elements in the heap
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Return a counters array, where the value of the i-th entry is the number
	 * of trees of order i in the heap.
	 */
	public int[] countersRep() {
		int arraySize = (int) (5 * Math.log10(this.size + 1));
		int[] arr = new int[arraySize];
		for (HeapNode node : this) {
			arr[node.rank]++;
		}
		return arr;
	}

	/**
	 * Deletes the node x from the heap.
	 */
	public void delete(HeapNode x) {
		decreaseKey(x, x.key + 1);
		deleteMin();
	}

	/**
	 * The function decreases the key of the node x by delta.
	 */
	public void decreaseKey(HeapNode x, int delta) {
		// subtract delta from x.key
		x.key = x.key - delta;

		HeapNode parent = x.parent;
		if (parent != null && x.key < parent.key) {
			// if the heap rule is broken, cut x from its parent
			cut(x, parent);
			// mark the parent or continue cutting if parent is already marked
			cascadingCut(parent);
		}

		// update the minimum if necessary
		if (x.key < this.min.key) {
			this.min = x;
		}
	}

	private void cut(HeapNode node, HeapNode parent) {
		// remove node from parent's children list and lower parent's rank
		parent.child.deleteSibling(node);
		parent.rank--;

		// append node to root list and set its parent to null
		this.sentinel.appendSibling(node);
		node.parent = null;
		this.totalTrees++;

		// if node was marked then unmark it
		if (node.isMarked) {
			this.totalMarkedNodes--;
			node.isMarked = false;
		}
		totalCuts++;
	}

	/**
	 * gets a node that one of his children was cut from him
	 * if the node wasn't marked then the function marks it
	 * if the node was already marked then the function cuts it from its parent,
	 * and then calls the function recursively with the parent
	 */
	private void cascadingCut(HeapNode node) {
		HeapNode parent = node.parent;
		if (parent == null) {
			// if node is a root no action needed
			return;
		}

		if (!node.isMarked) {
			// if node was not marked, mark it
			node.isMarked = true;
			this.totalMarkedNodes++;
		} else {
			// node was already marked, cut it from its parent
			cut(node, parent);
			// continue recursively
			cascadingCut(parent);
		}
	}

	/**
	 * This function returns the current potential of the heap, which is:
	 * Potential = #trees + 2*#marked The potential equals to the number of
	 * trees in the heap plus twice the number of marked nodes in the heap.
	 */
	public int potential() {
		return this.totalTrees + 2 * this.totalMarkedNodes;
	}

	public int totalTrees() {
		return this.totalTrees;
	}

	public int totalMarked() {
		return this.totalMarkedNodes;
	}

	@Override
	/**
	 * returns an iterator for the heap's root list
	 */
	public Iterator<HeapNode> iterator() {
		return new HeapNodeIterator(this.sentinel);
	}

	/**
	 * This static function returns the total number of link operations made
	 * during the run-time of the program. A link operation is the operation
	 * which gets as input two trees of the same rank, and generates a tree of
	 * rank bigger by one, by hanging the tree which has larger value in its
	 * root on the tree which has smaller value in its root.
	 */
	public static int totalLinks() {
		return totalLinks;
	}

	/**
	 * This static function returns the total number of cut operations made
	 * during the run-time of the program. A cut operation is the operation
	 * which diconnects a subtree from its parent (during decreaseKey/delete
	 * methods).
	 */
	public static int totalCuts() {
		return totalCuts;
	}

	public void print() {
		System.out.println("***************************************** Beginnig of output **********************************");
		for (HeapNode root : this) {
			root.print();
		}
		System.out.println("***************************************** End of output **********************************");
	}

	/**
	 * class represent a node in the heap
	 */
	public class HeapNode implements Iterable<HeapNode> {
		Integer key;

		HeapNode parent;
		HeapNode right;
		HeapNode left;
		HeapNode child;
		boolean isMarked;
		int rank;

		public HeapNode(int key) {
			this.key = key;
			this.parent = null;
			this.right = this;
			this.left = this;
			this.child = createSentinel();
			this.isMarked = false;
			this.rank = 0;
		}

		/**
		 * empty constructor to create sentinels
		 */
		private HeapNode() {
			this.key = null;
		}

		public Integer getKey() {
			return this.key;
		}

		/**
		 * appends a node to the list of siblings
		 * function assumes that there's always a sentinel node in the list
		 */
		public void appendSibling(HeapNode node) {
			node.left = this;
			node.right = this.right;
			node.right.left = node;
			this.right = node;
		}

		/**
		 * deletes a node from the list of siblings
		 * function assumes that there's always a sentinel node in the list
		 */
		public void deleteSibling(HeapNode node) {
			node.left.right = node.right;
			node.right.left = node.left;
		}

		public boolean isSentinel() {
			return this.key == null;
		}

		@Override
		/**
		 * returns an iterator for the list of children
		 */
		public Iterator<HeapNode> iterator() {
			return new HeapNodeIterator(this.child);
		}

		public void print() {
			print("", true);
		}

		private void print(String prefix, boolean isTail) {
			String suffix = this.isMarked ? "*" : "";
			System.out.println(prefix + (isTail ? "└── " : "├── ") + key + suffix);
			Iterator<HeapNode> it = this.iterator();
			while (it.hasNext()) {
				HeapNode node = it.next();
				if (it.hasNext()) {
					node.print(prefix + (isTail ? "    " : "│   "), false);
				} else {
					node.print(prefix + (isTail ? "    " : "│   "), true);
				}
			}
		}
	}

	/**
	 * class represents an iterator for a list of heap nodes
	 * class instances assume that they were initialized with a sentinel node
	 */
	public static class HeapNodeIterator implements Iterator<HeapNode> {

		HeapNode current;

		public HeapNodeIterator(HeapNode node) {
			this.current = node.right;
		}

		@Override
		public boolean hasNext() {
			return !current.isSentinel();
		}

		@Override
		public HeapNode next() {
			HeapNode node = this.current;
			current = current.right;
			return node;
		}

	}
}