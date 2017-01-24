/**
 * Created by Ofir on 25/01/2017.
 */
public class FibHeapTester {

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
		FibonacciHeap.HeapNode node1 = h.insert(32);
		FibonacciHeap.HeapNode node2 = h.insert(52);
		FibonacciHeap.HeapNode node3 = h.insert(35);
		FibonacciHeap.HeapNode node4 = h.insert(62);
		FibonacciHeap.HeapNode node5 = h.insert(38);
		FibonacciHeap.HeapNode node6 = h.insert(23);
		FibonacciHeap.HeapNode node7 = h.insert(30);
		h.print();
		h.deleteMin();
		h.print();
		h.decreaseKey(node4, 29);
		h.print();

		/*h.delete(node1);
		h.delete(node2);
		h.delete(node3);
		System.out.println(Arrays.toString(h.countersRep()));
		h.delete(node4);
		h.delete(node5);
		h.delete(node6);
		h.delete(node7);*/
	}
}
