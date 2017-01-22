import java.util.Iterator;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap {

	private static double LOG_2 = Math.log10(2);
	public static int totalLinks = 0;
	public static int totalCuts = 0;

	HeapNode sentinel;
	private HeapNode min;
	private int size;

	public FibonacciHeap() {
		this.sentinel = new HeapNode();
		this.sentinel.right = this.sentinel;
		this.sentinel.left = this.sentinel;
		this.min = null;
		this.size = 0;
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
		HeapNode node = new HeapNode(key);
		this.sentinel.appendSibling(node);
		if (this.min == null || node.key < this.min.key) {
			this.min = node;
		}
		this.size++;
		return node;
	}

	/**
	 * Delete the node containing the minimum key.
	 */
	public void deleteMin() {
		HeapNode z = this.min;
		if (z == null) {
			return;
		}

		HeapNode node = z.child;
		Iterator<HeapNode> iterator = new HeapNodeIterator(node);
		while (iterator.hasNext()) {
			HeapNode x = iterator.next();
			this.sentinel.appendSibling(x);
			x.parent = null;
		}

		this.min = null;
		this.sentinel.deleteSibling(z);
		this.size--;
		consolidate();
	}

	private void consolidate() {
		if (this.size == 0) {
			return;
		}
		int arraySize = (int) (Math.ceil((Math.log10(this.size) / LOG_2))) + 1;
		HeapNode[] trees = new HeapNode[arraySize];
		Iterator<HeapNode> iterator = new HeapNodeIterator(this.sentinel);
		while (iterator.hasNext()) {
			HeapNode x = iterator.next();
			int d = x.rank;
			while (d < arraySize && trees[d] != null) {
				HeapNode y = trees[d];
				x = link(y, x);
				trees[d] = null;
				d = d + 1;
			}
			trees[d] = x;
		}

		this.min = null;
		this.sentinel = new HeapNode();
		this.sentinel.right = this.sentinel;
		this.sentinel.left = this.sentinel;
		for (int i = 0; i < trees.length; i++) {
			HeapNode tree = trees[i];
			if (tree != null) {
				this.sentinel.appendSibling(tree);
				if (this.min == null || tree.key < this.min.key) {
					this.min = tree;
				}
			}
		}
	}

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
	 *
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
			this.sentinel.right = heap2.sentinel.right;
		}
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 * 
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Return a counters array, where the value of the i-th entry is the number
	 * of trees of order i in the heap.
	 * 
	 */
	public int[] countersRep() {
		int arraySize = (int) (Math.ceil((Math.log10(this.size) / LOG_2))) + 1;
		int[] arr = new int[arraySize];
		Iterator<HeapNode> it = new HeapNodeIterator(this.sentinel);
		while (it.hasNext()) {
			HeapNode node = it.next();
			arr[node.rank]++;
		}
		return arr;
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap.
	 *
	 */
	public void delete(HeapNode x) {
		decreaseKey(x, x.key + 1);
		deleteMin();
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of
	 * the heap should be updated to reflect this chage (for example, the
	 * cascading cuts procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta) {
		x.key = x.key - delta;
		HeapNode y = x.parent;
		if (y != null && x.key < y.key) {
			cut(x, y);
			cascadingCut(y);
		}
		if (x.key < this.min.key) {
			this.min = x;
		}
	}

	private void cut(HeapNode x, HeapNode y) {
		y.child.deleteSibling(x);
		y.rank--;
		this.sentinel.appendSibling(x);
		x.parent = null;
		x.isMarked = false;
		totalCuts++;
	}

	private void cascadingCut(HeapNode y) {
		HeapNode z = y.parent;
		if (z == null) {
			return;
		}

		if (!y.isMarked) {
			y.isMarked = true;
		} else {
			cut(y, z);
			cascadingCut(z);
		}
	}

	/**
	 * public int potential()
	 *
	 * This function returns the current potential of the heap, which is:
	 * Potential = #trees + 2*#marked The potential equals to the number of
	 * trees in the heap plus twice the number of marked nodes in the heap.
	 */
	public int potential() {
		int trees = 0;
		int totalMarked = 0;
		Iterator<HeapNode> iterator = new HeapNodeIterator(this.sentinel);
		while (iterator.hasNext()) {
			trees++;
			totalMarked += countMarked(iterator.next());
		}
		return trees + 2 * totalMarked;
	}

	public int countMarked(HeapNode node) {
		int totalMarked = 0;
		Iterator<HeapNode> iterator = new HeapNodeIterator(node.child);
		while (iterator.hasNext()) {
			HeapNode child = iterator.next();
			if (child.isMarked) {
				totalMarked++;
			}
			totalMarked += countMarked(child);
		}
		return totalMarked;
	}

	/**
	 * public static int totalLinks()
	 *
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
	 * public static int totalCuts()
	 *
	 * This static function returns the total number of cut operations made
	 * during the run-time of the program. A cut operation is the operation
	 * which diconnects a subtree from its parent (during decreaseKey/delete
	 * methods).
	 */
	public static int totalCuts() {
		return totalCuts;
	}

	public static void main(String[] args) {
		FibonacciHeap fb = new FibonacciHeap();
		fb.insert(5);
		fb.insert(6);
		HeapNode node7 = fb.insert(7);
		fb.insert(3);
		fb.insert(2);
		fb.deleteMin();
		System.out.println("Min: " + fb.findMin().key);
		fb.deleteMin();
		System.out.println("Min: " + fb.findMin().key);
		fb.deleteMin();
		System.out.println("Min: " + fb.findMin().key);
		fb.decreaseKey(node7, 2);
		System.out.println("Min: " + fb.findMin().key);
	}

	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap (for example
	 * HeapNode), do it in this file, not in another file
	 * 
	 */
	public class HeapNode {
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
			this.child = new HeapNode();
			this.child.right = this.child;
			this.child.left = this.child;
			this.isMarked = false;
			this.rank = 0;
		}

		private HeapNode() {
			this.key = null;
		}

		public void appendSibling(HeapNode node) {
			node.left = this;
			node.right = this.right;
			node.right.left = node;
			this.right = node;
		}

		public void deleteSibling(HeapNode node) {
			node.left.right = node.right;
			node.right.left = node.left;
		}

		public boolean isSentinel() {
			return this.key == null;
		}
	}

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