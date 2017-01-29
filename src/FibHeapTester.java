import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
			if (i < 1_000_000 && fh.findMin().getKey() != i+1) {
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

	public static void main(String[] args) {
		testWithYourOwnEyes();
		//testDeleteMin();
	}
}
