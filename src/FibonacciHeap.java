import java.util.Arrays;
import java.util.Iterator;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap implements Iterable<FibonacciHeap.HeapNode> {

	private static double LOG_GOLDEN_RATIO = Math.log10(1.6);
	public static int totalLinks = 0;
	public static int totalCuts = 0;

	HeapNode sentinel;
	private HeapNode min;
	private int size;

	public FibonacciHeap() {
		this.sentinel = createSentinel();
		this.min = null;
		this.size = 0;
	}

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
		if (this.min == null) {
			return;
		}

		for (HeapNode child : this.min) {
			this.sentinel.appendSibling(child);
			child.parent = null;
		}
		this.sentinel.deleteSibling(this.min);
		this.min = null; // consolidate function will find the new minimum
		this.size--;
		consolidate();
	}

	private void consolidate() {
		if (this.size == 0) {
			return;
		}
		int arraySize = ((int) (Math.log10(this.size) / LOG_GOLDEN_RATIO)) + 1;
		HeapNode[] treesByRank = new HeapNode[arraySize];
		for (HeapNode root : this) {
			int d = root.rank;
			while (d < arraySize && treesByRank[d] != null) {
				HeapNode y = treesByRank[d];
				root = link(y, root);
				treesByRank[d] = null;
				d++;
			}
			treesByRank[d] = root;
		}

		this.sentinel = createSentinel();
		for (int i = 0; i < treesByRank.length; i++) {
			HeapNode tree = treesByRank[i];
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
	 * 
	 */
	public int[] countersRep() {
		int arraySize = (int) (Math.ceil((Math.log10(this.size) / LOG_GOLDEN_RATIO))) + 1;
		int[] arr = new int[arraySize];
		for (HeapNode node : this) {
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
	 * This function returns the current potential of the heap, which is:
	 * Potential = #trees + 2*#marked The potential equals to the number of
	 * trees in the heap plus twice the number of marked nodes in the heap.
	 */
	public int potential() {
		int trees = 0;
		int totalMarked = 0;
		for (HeapNode root : this) {
			trees++;
			totalMarked += countMarked(root);
		}
		return trees + 2 * totalMarked;
	}

	public int countMarked(HeapNode node) {
		int totalMarked = 0;
		for (HeapNode child : node) {
			if (child.isMarked) {
				totalMarked++;
			}
			totalMarked += countMarked(child);
		}
		return totalMarked;
	}

	@Override
	public Iterator<HeapNode> iterator() {
		return new HeapNodeIterator(this.sentinel);
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
		/*FibonacciHeap fb = new FibonacciHeap();
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
		System.out.println("Min: " + fb.findMin().key);*/
		/*FibonacciHeap fb1 = new FibonacciHeap();
		fb1.insert(1);
		fb1.insert(2);
		fb1.insert(3);
		FibonacciHeap fb2 = new FibonacciHeap();
		fb2.insert(4);
		fb2.insert(5);
		fb2.insert(6);
		fb1.meld(fb2);
		for (HeapNode root : fb1) {
			System.out.println(root.key);
		}*/
		/*for (int i = 1 ; i <= 100; i++) {
			int a = (int) (Math.log10(i) / LOG_GOLDEN_RATIO);
			a++;
			System.out.println(i + ": " + a);
		}*/
		//[32, 52, 35, 62, 38, 23, 30]
		FibonacciHeap h = new FibonacciHeap();
		HeapNode node1 = h.insert(32);
		HeapNode node2 = h.insert(52);
		HeapNode node3 = h.insert(35);
		HeapNode node4 = h.insert(62);
		HeapNode node5 = h.insert(38);
		HeapNode node6 = h.insert(23);
		HeapNode node7 = h.insert(30);
		h.delete(node1);
		h.delete(node2);
		h.delete(node3);
		System.out.println(Arrays.toString(h.countersRep()));
		h.delete(node4);
		h.delete(node5);
		h.delete(node6);
		h.delete(node7);
	}

	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap (for example
	 * HeapNode), do it in this file, not in another file
	 * 
	 */
	public class HeapNode implements  Iterable<HeapNode> {
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

		@Override
		public Iterator<HeapNode> iterator() {
			return new HeapNodeIterator(this.child);
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