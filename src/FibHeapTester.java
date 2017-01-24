import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ofir on 25/01/2017.
 */
public class FibHeapTester {

	public static void main(String[] args) {
		//[32, 52, 35, 62, 38, 23, 30]

		/*
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
		System.out.println(h.totalMarked());
		h.decreaseKey(node2, 18);
		h.print();
		System.out.println(h.totalTrees());
		h.deleteMin();
		h.print();

		h.delete(node1);
		h.delete(node2);
		h.delete(node3);
		System.out.println(Arrays.toString(h.countersRep()));
		h.delete(node4);
		h.delete(node5);
		h.delete(node6);
		h.delete(node7);*/

		FibonacciHeap fh = new FibonacciHeap();
		Map<Integer, FibonacciHeap.HeapNode> nodes = new HashMap<>();
		for (int i = 1; i <= 1_000_000; i++) {
			nodes.put(i, fh.insert(i));
		}
		for (int i = 1; i <= 1_000_000; i++) {
			fh.deleteMin();
			if (i == 500_000) {
				System.out.println(fh.findMin().key);
			}
		}
		System.out.println(fh.empty());
	}
}
