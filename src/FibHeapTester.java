import java.util.*;

/**
 * Created by Ofir on 25/01/2017.
 */
public class FibHeapTester {

	public static void testDeleteMin() {
		FibonacciHeap fh = new FibonacciHeap();
		Map<Integer, FibonacciHeap.HeapNode> nodes = new HashMap<>();
		for (int i = 1; i <= 1_000_000; i++) {
			nodes.put(i, fh.insert(i));
		}
		for (int i = 1; i <= 1_000_000; i++) {
			fh.deleteMin();
			if (i < 1_000_000 && fh.findMin().getKey() != i + 1) {
				System.out.println("That's really bad");
			}
		}
		System.out.println(fh.empty() ? "Done" : "Problem! deleted all nodes yet heap is not empty");
	}

	public static void testWithYourOwnEyes() {
		FibonacciHeap h = new FibonacciHeap();
		FibonacciHeap.HeapNode node1 = h.insert(32);
		FibonacciHeap.HeapNode node2 = h.insert(52);
		FibonacciHeap.HeapNode node3 = h.insert(35);
		FibonacciHeap.HeapNode node4 = h.insert(62);
		FibonacciHeap.HeapNode node5 = h.insert(38);
		FibonacciHeap.HeapNode node6 = h.insert(23);
		FibonacciHeap.HeapNode node7 = h.insert(30);
		FibonacciHeap.HeapNode node8 = h.insert(40);
		FibonacciHeap.HeapNode node9 = h.insert(50);
		FibonacciHeap.HeapNode node10 = h.insert(70);

		h.print();
		h.deleteMin();
		h.print();
		h.decreaseKey(node5, 5);
		h.print();
		h.decreaseKey(node2, 18);
		h.print();
		h.deleteMin();
		h.print();
		h.insert(667);
		h.print();
		h.decreaseKey(node9, 16);
		h.print();
		h.decreaseKey(node4, 30);
		h.print();
		h.decreaseKey(node1, 1);
		h.print();
	}

	public static void firstSequenceTest(int m) {
		long startTime = System.currentTimeMillis();
		FibonacciHeap h = new FibonacciHeap();
		for (int i = m; i >= 1; i--) {
			h.insert(i);
		}
		long totalTime = System.currentTimeMillis() - startTime;
		System.out.println(totalTime);
		System.out.printf("m: %d, Total time: %s, Total links: %d, total cuts: %d, Potential: %d", m, String.valueOf(totalTime), h.totalLinks(), h.totalCuts(), h.potential());
		h.print();
	}

	public static void secondSequenceTest(int m) {
		long startTime = System.currentTimeMillis();
		FibonacciHeap h = new FibonacciHeap();
		for (int i = m; i >= 1; i--) {
			h.insert(i);
		}
		for (int i = 0; i < m / 2; i++) {
			h.deleteMin();
		}

		long totalTime = System.currentTimeMillis() - startTime;
		System.out.println(totalTime);
		System.out.printf("m: %d, Total time: %s, Total links: %d, total cuts: %d, Potential: %d", m, String.valueOf(totalTime), h.totalLinks(), h.totalCuts(), h.potential());
		System.out.println("\n" + h.size());
		//h.print();
	}

	public static void thirdSequenceTest(int m) {
		long startTime = System.currentTimeMillis();
		FibonacciHeap h = new FibonacciHeap();
		for (int i = m; i >= 1; i--) {
			h.insert(i);
		}
		h.deleteMin();
		h.deleteMin();
		h.deleteMin();
		h.deleteMin();
		h.deleteMin();
		h.deleteMin();
		h.print();
		long totalTime = System.currentTimeMillis() - startTime;
		System.out.println(totalTime);
		System.out.printf("m: %d, Total time: %s, Total links: %d, total cuts: %d, Potential: %d", m, String.valueOf(totalTime), h.totalLinks(), h.totalCuts(), h.potential());
		System.out.println("\n" + h.size());
		//h.print();
	}

	public enum OP {
		DELMIN, DECKEY, DEL
	}

	static Random rnd = new Random();

	public static OP randomOP() {
		//int i = rnd.nextInt(3);
		long l = System.currentTimeMillis();
		int i = (int) l % 10;
		if (i == 0 || i == 1 || i == 2 || i == 3) {
			return OP.DECKEY;
		} else if (i == 4 || i == 5 || i == 6) {
			return OP.DELMIN;
		} else {
			return OP.DEL;
		}
	}

	public static void anotherTest() {
		FibonacciHeap h = new FibonacciHeap();
		TreeMap<Integer, FibonacciHeap.HeapNode> nodes = new TreeMap<>();
		int m = 1_000_000;
		for (int i = 0; i <m; i++) {
			int x = rnd.nextInt(m);
			if (!nodes.containsKey(x)) {
				nodes.put(x, h.insert(x));
			}
		}
		int length = nodes.size() / 2;
		int totalDelMin = 0;
		int totalDecKey = 0;
		int totalDel = 0;
		if (h.size() != nodes.size()) {
			System.out.println("Big Prob");
			System.exit(1);
		}
		System.out.println("size is: " + h.size());
		for (int i = 0; i < length; i++) {
			OP op = randomOP();
			if (op == OP.DELMIN) {
				h.deleteMin();
				nodes.pollFirstEntry();
				totalDelMin++;
				if (h.findMin().getKey() != (nodes.firstKey().intValue())) {
					System.out.println("Problem with min maintenance. heap key: " + h.findMin().getKey() + "  real min: " + nodes.firstKey());
					System.exit(1);
				}
			} else if (op == OP.DECKEY) {
				int val = rnd.nextInt(m);
				FibonacciHeap.HeapNode node = nodes.get(val);
				if (val != 0 && node!= null) {
					int delta = rnd.nextInt(node.getKey());
					int expected = node.getKey() - delta;
					h.decreaseKey(node, delta);
					if (node.getKey() != (expected)) {
						System.out.println("Big prob with dec");
						System.exit(1);
					}
					nodes.remove(val);
					if (!nodes.containsKey(expected)) {
						nodes.put(expected, node);
					} else {
						h.delete(node);
					}
					totalDecKey++;
				}
			} else {
				int val = rnd.nextInt(m);
				FibonacciHeap.HeapNode node = nodes.get(val);
				if (node!= null) {
					int expectedSize = h.size() - 1;
					h.delete(node);
					nodes.remove(val);
					totalDel++;
					if (h.size() != expectedSize) {
						System.out.println("Big problem with size");
						System.exit(1);
					}
				}
			}

			if (h.size() != nodes.size()) {
				System.out.println("Size doesn't work");
			}
			if (h.findMin().getKey() != (nodes.firstKey().intValue())) {
				System.out.println("Problem with min");
			}
		}

		System.out.printf("DelMins: %d, Dels: %d, DecKey: %d%n", totalDelMin, totalDel, totalDecKey);
		int rootNodes = countRootNodes(h);
		int markedNodes = countMarkedNodes(h);
		if (h.potential() != rootNodes + 2*markedNodes) {
			System.out.println("Potential prob before");
			System.out.println(h.potential());
			System.out.println(rootNodes + 2*markedNodes);
		}
		while (h.size() > 1000) {
			h.deleteMin();
		}
		rootNodes = countRootNodes(h);
		markedNodes = countMarkedNodes(h);
		if (h.potential() != rootNodes + 2*markedNodes) {
			System.out.println("Potential prob");
			System.out.println(h.potential());
			System.out.println(rootNodes + 2*markedNodes);
			System.out.println("roots: " + rootNodes);
			System.out.println("marked: " + markedNodes);
		}

	}

	private static int countRootNodes(FibonacciHeap h) {
		int rootCount = 0;
		for (FibonacciHeap.HeapNode node : h) {
			rootCount++;
		}
		return rootCount;
	}

	private static int countMarkedNodes(FibonacciHeap h) {
		int markedCount = 0;
		for (FibonacciHeap.HeapNode node : h) {
			markedCount += countMarked(node);
		}
		return markedCount;
	}

	private static int countMarked (FibonacciHeap.HeapNode node) {
		int markedCount = 0;
		for (FibonacciHeap.HeapNode child : node) {
			if (child.isMarked) {
				markedCount++;
			}
			markedCount += countMarked(child);
		}
		return markedCount;
	}

	public static void bla() {
		FibonacciHeap h = new FibonacciHeap();
		FibonacciHeap.HeapNode node18 = h.insert(18);
		FibonacciHeap.HeapNode node17 = h.insert(17);
		FibonacciHeap.HeapNode node0 = h.insert(0);
		FibonacciHeap.HeapNode node1 = h.insert(1);
		FibonacciHeap.HeapNode node8 = h.insert(8);
		FibonacciHeap.HeapNode node12 = h.insert(12);
		FibonacciHeap.HeapNode node10 = h.insert(10);
		FibonacciHeap.HeapNode node5 = h.insert(5);
		FibonacciHeap.HeapNode node16 = h.insert(16);
		FibonacciHeap.HeapNode node2 = h.insert(2);
		FibonacciHeap.HeapNode node13 = h.insert(13);
		FibonacciHeap.HeapNode node9 = h.insert(9);
		h.print();

		h.delete(node13);
		h.print();
		h.delete(node2);
		h.print();
		h.delete(node16);
		h.print();
		h.delete(node12);
		h.print();
		h.delete(node8);
		h.print();
	}

	public static void main(String[] args) {
		//testWithYourOwnEyes();
		//testDeleteMin();
		//secondSequenceTest(1000);
		//thirdSequenceTest(1_000);
		anotherTest();
		//bla();
	}
}
