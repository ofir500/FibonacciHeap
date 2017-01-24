
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FibonacciHeapTest {
	private static int[] createValues(int n) {
		int[] values = new int[n];
		int maxValue = n * 10;
		Random randomGenerator = new Random();

		for (int i = 0; i < n; ++i) {
			while (true) {
				int j, randInt = randomGenerator.nextInt(maxValue);

				for (j = 0; j < i && randInt != values[j]; ++j)
					;
				if (j < i) {
					continue;
				}
				values[i] = randInt;
				break;
			}
		}

		return values;
	}

	private static String vals2str(int[] values) {
		int min = values[0];
		for (int val : values) {
			min = val < min ? val : min;
		}
		String s = "size=" + values.length + " min=" + min;

		if (values.length <= 20) {
			for (int val : values) {
				s += " " + val;
			}
		} else {
			for (int j = 0; j < 5; ++j) {
				s += " " + values[j];
			}
			s += " ...";
			for (int j = 0; j < 5; ++j) {
				s += " " + values[values.length - 6 + j];
			}
		}

		return s;
	}

	private static abstract class Test implements Runnable {
		public final String name;
		private boolean failed;
		private String error;
		private String eMessage;
		private String eTrace;

		public Test(String name) {
			this.name = name;
			this.failed = false;
			this.error = "";
			this.eMessage = "";
			this.eTrace = "";
			// System.out.println("Adding test: " + name);
		}

		@Override
		public void run() {
			try {
				test();
			} catch (Exception e) {
				setFailed(e);
			}
		}

		protected abstract void test();

		public void setFailed(String error) {
			failed = true;
			this.error = error;
			// System.out.println("Failed. error: " + error);
		}

		public void setFailed(Exception e) {
			setFailed("Java Exception");
			this.eMessage = e.getMessage();
			this.eTrace = "\n";
			for (StackTraceElement el : e.getStackTrace()) {
				if (!el.getClassName().contains("FibonacciHeap")) {
					continue;
				}
				this.eTrace += "\t" + el + "\n";
			}
			// System.out.println("Exception message: " + eMessage);
			// System.out.println("Stack trace:\n" + eTrace);
		}

		public boolean failed() {
			return failed;
		}

		public String toString() {
			return String.format("%s, Faild: %s, Error: %s, " + "Exception: %s, Call stack: %s", name,
					failed ? "Y" : "N", error, eMessage, eTrace);
		}
	}

	static private class TestMeld1 extends Test {
		public TestMeld1() {
			super("Meld two empty heaps");
		}

		protected void test() {
			FibonacciHeap heap1 = new FibonacciHeap();
			FibonacciHeap heap2 = new FibonacciHeap();
			heap1.meld(heap2);
			if (!heap1.empty()) {
				setFailed("result not empty!");
			}
		}
	}

	static private class TestMeld2 extends Test {
		public TestMeld2() {
			super("Meld other empty heap");
		}

		protected void test() {
			FibonacciHeap heap1 = new FibonacciHeap();
			FibonacciHeap heap2 = new FibonacciHeap();
			FibonacciHeap.HeapNode min = heap1.insert(3);
			int size1 = heap1.size();
			heap1.meld(heap2);
			if (heap1.empty()) {
				setFailed("result empty!");
			}
			if (heap1.size() != size1 + heap2.size()) {
				setFailed("melded heap size (" + heap1.size() + "!= heap1 (" + size1 + ") + heap2 (" + heap2.size()
						+ ")");
			}
			if (heap1.findMin() != min) {
				setFailed("findMin after meld failed");
			}
		}
	}

	static private class TestMeld3 extends Test {
		public TestMeld3() {
			super("Meld this empty heap with other nonempty");
		}

		protected void test() {
			FibonacciHeap heap1 = new FibonacciHeap();
			FibonacciHeap heap2 = new FibonacciHeap();
			FibonacciHeap.HeapNode min = heap2.insert(3);
			int size1 = heap1.size();
			heap1.meld(heap2);
			if (heap1.empty()) {
				setFailed("result empty!");
			}
			if (heap2.size() != size1 + heap2.size()) {
				setFailed("melded heap size (" + heap1.size() + ") != heap1 (" + size1 + ") + heap2 (" + heap2.size()
						+ ")");
			}
			if (heap1.findMin() != min) {
				setFailed("findMin after meld failed");
			}
		}
	}

	static private class TestMeld4 extends Test {
		public TestMeld4() {
			super("Meld nonempty heap with other nonempty");
		}

		protected void test() {
			FibonacciHeap heap1 = new FibonacciHeap();
			FibonacciHeap heap2 = new FibonacciHeap();
			heap1.insert(5);
			heap1.insert(17);
			int size1 = heap1.size();
			FibonacciHeap.HeapNode min = heap2.insert(3);
			int size2 = heap2.size();

			heap1.meld(heap2);
			if (heap1.empty()) {
				setFailed("result empty!");
			}
			if (heap1.size() != size1 + size2) {
				setFailed("melded heap size (" + heap1.size() + ") != heap1 (" + size1 + ") + heap2 (" + size2 + ")");
			}
			if (heap1.findMin() != min) {
				setFailed("findMin after meld failed");
			}
		}
	}

	static private class TestMeld5 extends Test {
		public TestMeld5() {
			super("Meld large heaps");
		}

		protected void test() {
			FibonacciHeap heap1 = new FibonacciHeap();
			FibonacciHeap heap2 = new FibonacciHeap();
			int[] vals1 = createValues(500);
			int[] vals2 = createValues(500);
			int min = vals1[0];
			FibonacciHeap.HeapNode minNode = null;
			for (int v : vals1) {
				if (v < min) {
					min = v;
					minNode = heap1.insert(v);
				} else {
					heap1.insert(v);
				}
			}
			for (int v : vals2) {
				if (v < min) {
					min = v;
					minNode = heap1.insert(v);
				} else {
					heap2.insert(v);
				}
			}
			int size1 = heap1.size();
			int size2 = heap2.size();

			heap1.meld(heap2);
			if (heap1.empty()) {
				setFailed("result empty!");
			}
			if (heap1.size() != size1 + size2) {
				setFailed("melded heap size (" + heap1.size() + ") != heap1 (" + size1 + ") + heap2 (" + size2 + ")");
			}
			if (heap1.findMin() != minNode) {
				setFailed("findMin after meld failed");
			}
		}
	}

	static private class TestInsert extends Test {
		public TestInsert() {
			super("Check size each insert");
		}

		protected void test() {
			int[] vals = createValues(100);

			FibonacciHeap heap1 = new FibonacciHeap();
			for (int i = 0; i < vals.length; ++i) {
				if (heap1.size() != i) {
					setFailed("size is " + i + " but size() says " + heap1.size());
					break;
				}
				heap1.insert(vals[i]);
			}
		}
	}

	static private class TestFindMin1 extends Test {
		public TestFindMin1() {
			super("Check findMin each unsorted insert");
		}

		protected void test() {

			int[] vals = createValues(100);
			int min = vals[0];
			FibonacciHeap.HeapNode minNode = null;
			FibonacciHeap heap1 = new FibonacciHeap();

			for (int i = 0; i < vals.length; ++i) {
				if (vals[i] < min) {
					min = vals[i];
					minNode = heap1.insert(vals[i]);
				} else {
					if (minNode == null) {
						minNode = heap1.insert(vals[i]);
					} else {
						heap1.insert(vals[i]);
					}
				}
				if (heap1.findMin() != minNode) {
					setFailed("min is " + min + "but findMin() says " + heap1.findMin());
					break;
				}
			}
		}
	}

	static private class TestFindMin2 extends Test {
		public TestFindMin2() {
			super("Check findMin each sorted insert");
		}

		protected void test() {
			int[] vals = createValues(100);
			Arrays.sort(vals);
			FibonacciHeap heap1 = new FibonacciHeap();
			for (int i = vals.length - 1; i >= 0; --i) {
				FibonacciHeap.HeapNode min = heap1.insert(vals[i]);
				if (heap1.findMin() != min) {
					setFailed("min is " + vals[i] + "but findMin() says " + heap1.findMin());
					break;
				}
			}
		}
	}

	static private class TestDeleteMin extends Test {
		public TestDeleteMin() {
			super("Check findMin after each deleteMin");
		}

		protected void test() {

			int[] vals = createValues(100);
			FibonacciHeap heap1 = new FibonacciHeap();

			for (int i = 0; i < vals.length; i++) {
				heap1.insert(vals[i]);
			}

			Arrays.sort(vals);
			for (int i = 0; i < vals.length; i++) {
				if (heap1.findMin().key != vals[i]) {
					setFailed("min is " + vals[i] + " but findMin() says " + heap1.findMin());
					break;
				}
				heap1.deleteMin();
			}
		}
	}

	static private class TestDelete extends Test {
		public TestDelete() {
			super("Test delete");
		}

		@Override
		protected void test() {
			int[] vals = createValues(500000);
			FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[vals.length];
			FibonacciHeap heap1 = new FibonacciHeap();

			for (int i = 0; i < vals.length; i++) {
				nodes[i] = heap1.insert(vals[i]);
			}

			for (int i = 0; i < nodes.length; i++) {
				FibonacciHeap.HeapNode node = nodes[i];
				heap1.delete(node);
			}

			if (!heap1.empty()) {
				setFailed("Heap is not empty after deleting all nodes");
			}
		}
	}

	static private class TestDecreaseKey extends Test {
		public TestDecreaseKey() {
			super("Test decreaseKey");
		}

		@Override
		protected void test() {
			int[] vals = createValues(1000);
			FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[vals.length];
			FibonacciHeap heap1 = new FibonacciHeap();

			for (int i = 0; i < vals.length; i++) {
				nodes[i] = heap1.insert(vals[i]);
			}

			for (int i = 0; i < nodes.length; i++) {
				FibonacciHeap.HeapNode node = nodes[i];
				int totalCutsBefore = FibonacciHeap.totalCuts();
				int parentKey = -100;
				int nodeKey = -99;
				if (node.parent != null) {
					parentKey = node.parent.key;
					nodeKey = node.key;
					heap1.decreaseKey(node, nodeKey - parentKey);
					if (node.key != parentKey) {
						setFailed("Node's key should be: " + parentKey + ", instead of: " + node.key);
					}
				} else if (node.key > 0) {
					nodeKey = node.key;
					heap1.decreaseKey(node, 1);
					if (node.key != nodeKey - 1) {
						setFailed("Node's key should be: " + (nodeKey - 1) + ", instead of: " + node.key);
					}
				} else {
					heap1.delete(node);
					continue;
				}

				// Check how many cuts are supposed to happen
				int expectedCuts = 0;
				if (node.parent != null) {
					expectedCuts++;
					FibonacciHeap.HeapNode iterator = node.parent;
					while (iterator.parent != null && iterator.isMarked) {
						expectedCuts++;
						iterator = iterator.parent;
					}
				}
				if (node.key > 0) {
					heap1.decreaseKey(node, 1);
				} else {
					heap1.delete(node);
					continue;
				}
				int totalCutsAfter = FibonacciHeap.totalCuts();
				int expectedTotalCutsAfter = totalCutsBefore + expectedCuts;
				if (totalCutsAfter != expectedTotalCutsAfter) {
					setFailed("Number of total cuts is not as expected. Total found: " + totalCutsAfter
							+ " , total expected: " + expectedTotalCutsAfter + " , expected cuts for this node: "
							+ expectedCuts);
					return;
				}
				heap1.delete(node);
			}

			if (!heap1.empty()) {
				setFailed("Heap is not empty after deleting all nodes");
			}
		}
	}

	static private class TestPotential extends Test {
		public TestPotential() {
			super("Test potential by recursively counting marked nodes and then number of trees");
		}

		@Override
		protected void test() {
			int[] vals = createValues(1000);
			FibonacciHeap heap1 = new FibonacciHeap();

			for (int val : vals) {
				heap1.insert(val);
			}

			int markedCount = countMarkedNodes(heap1.findMin());
			int treesCount = countRootNodes(heap1.findMin());

			if (heap1.potential() != treesCount + 2 * markedCount) {
				setFailed(
						"Wrong potential. Number of marked nodes: " + markedCount + ", number of trees: " + treesCount);
			}
		}

		private int countRootNodes(FibonacciHeap.HeapNode min) {
			int rootCount = 0;

			FibonacciHeap.HeapNode iterator = min;
			do {
				if (!iterator.isSentinel()) {
					rootCount++;
				}
				iterator = iterator.right;
			} while (iterator != min);

			return rootCount;
		}

		private int countMarkedNodes(FibonacciHeap.HeapNode node) {
			if (node == null) {
				return 0;
			}
			int markedCount = 0;
			FibonacciHeap.HeapNode iterator = node;
			do {
				if (!iterator.isSentinel() && iterator.isMarked) {
					markedCount++;
				}
				markedCount += countMarkedNodes(node.child);
				iterator = iterator.right;
			} while (iterator != node);
			return markedCount;
		}
	}

	static private class TestEmpty extends Test {
		public TestEmpty() {
			super("Check empty/size after insert and deleteMin");
		}

		protected void test() {

			FibonacciHeap heap1 = new FibonacciHeap();
			int size = 0;

			for (int i = 10; i < 30; ++i) {
				if (!heap1.empty()) {
					setFailed("empty but empty() returns false");
					break;
				}
				if (heap1.size() != size) {
					setFailed("size is " + size + " but size() returns " + heap1.size());
					break;
				}
				for (int j = 0; j < i; ++j) {
					heap1.insert(i);
					++size;
					if (heap1.empty()) {
						setFailed("not empty but empty() returns true");
						break;
					}
					if (heap1.size() != size) {
						setFailed("size is " + size + " but size() returns " + heap1.size());
						break;
					}
				}
				for (int j = 0; j < i; ++j) {
					if (heap1.empty()) {
						setFailed("not empty but empty() returns true");
						break;
					}
					heap1.deleteMin();
					--size;
					if (heap1.size() != size) {
						setFailed("size is " + size + " but size() returns " + heap1.size());
						break;
					}
				}
				if (!heap1.empty()) {
					setFailed("empty but empty() returns false");
					break;
				}
			}
		}
	}

	static private class StatisticsTest extends Test {

		public StatisticsTest() {
			super("Perform needed set of instructions and print the statistics");
		}

		@Override
		protected void test() {
			System.out.println("Sequence 1:");
			try {
				sequence1();
			} catch (Exception e) {
				setFailed(e);
			}
			System.out.println("*********************************");
			System.out.println("Sequence 2:");
			try {
				sequence2();
			} catch (Exception e) {
				setFailed(e);
			}
		}

		private void sequence1() {
			for (int i = 0; i < 3; i++) {
				FibonacciHeap.totalCuts = 0;
				FibonacciHeap.totalLinks = 0;
				int m = (i + 1) * 1000;
				System.out.println("m = " + m);
				int[] vals = new int[m];
				for (int j = 0; j < m; j++) {
					vals[j] = m - j;
				}

				FibonacciHeap heap = new FibonacciHeap();
				long startTime = System.nanoTime();
				for (int val : vals) {
					heap.insert(val);
				}
				long endTime = System.nanoTime();
				double duration = (endTime - startTime) / 1000000.0; // time in
																		// milliseconds

				System.out.println("Run-time duration: " + String.valueOf(duration));
				System.out.println("totalLinks: " + FibonacciHeap.totalLinks());
				System.out.println("totalCuts: " + FibonacciHeap.totalCuts());
				System.out.println("Potential: " + heap.potential());
			}
		}

		private void sequence2() {
			for (int i = 0; i < 3; i++) {
				FibonacciHeap.totalCuts = 0;
				FibonacciHeap.totalLinks = 0;
				int m = (i + 1) * 1000;
				System.out.println("m = " + m);
				int[] vals = new int[m];
				for (int j = 0; j < m; j++) {
					vals[j] = m - j;
				}

				FibonacciHeap heap = new FibonacciHeap();
				long startTime = System.nanoTime();
				for (int val : vals) {
					heap.insert(val);
				}
				for (int j = 0; j < m / 2; j++) {
					heap.deleteMin();
				}
				long endTime = System.nanoTime();
				double duration = (endTime - startTime) / 1000000.0; // time in
																		// milliseconds

				System.out.println("Run-time duration: " + String.valueOf(duration));
				System.out.println("totalLinks: " + FibonacciHeap.totalLinks());
				System.out.println("totalCuts: " + FibonacciHeap.totalCuts());
				System.out.println("Potential: " + heap.potential());
			}
		}
	}

	public static void main(String[] argv) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Test[] tests = { new TestMeld1(), new TestMeld2(), new TestMeld3(), new TestMeld4(), new TestMeld5(),
				new TestInsert(), new TestFindMin1(), new TestFindMin2(), new TestDeleteMin(), new TestDelete(),
				new TestDecreaseKey(), new TestPotential(), new TestEmpty(), new StatisticsTest() };

		for (Test test : tests) {
			try {
				executor.submit(test).get(5, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				test.setFailed("Timed out. Infinite loop");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
		executor.shutdown();

		int failed = 0, i = 0;
		for (Test test : tests) {
			if (test.failed()) {
				++failed;
			}
			System.out.printf("Test %2d | " + test + "\n", ++i);
		}
		System.out.println("Failed " + failed + " Out of " + tests.length + " tests");
		System.out.printf("Grade:\n%.0f\n", 100 * (1 - (float) failed / tests.length));
		System.exit(0);
	}
}