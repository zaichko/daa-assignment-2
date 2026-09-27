import java.util.Random;

public class Tests {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testDynamicArray();
        testMyLinkedList();
        testMinHeap();
        crossValidateAgainstJavaCollections();

        System.out.println();
        System.out.println("Passed: " + passed + "  Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    private static void expectException(String name, Runnable r) {
        try {
            r.run();
            failed++;
            System.out.println("FAIL (expected exception): " + name);
        } catch (RuntimeException e) {
            passed++;
        }
    }

    private static void testDynamicArray() {
        // empty structure
        DynamicArray a = new DynamicArray();
        check("DA empty size==0", a.getSize() == 0);
        expectException("DA get on empty throws", () -> a.get(0));

        // one element
        a.add(42);
        check("DA one element get(0)", a.get(0) == 42);
        check("DA one element size==1", a.getSize() == 1);

        // multiple elements, order preserved
        DynamicArray b = new DynamicArray();
        for (int i = 0; i < 20; i++) b.add(i);
        boolean orderOk = true;
        for (int i = 0; i < 20; i++) if (b.get(i) != i) orderOk = false;
        check("DA multiple elements order preserved", orderOk);

        // duplicate values
        DynamicArray c = new DynamicArray();
        c.add(5); c.add(5); c.add(5);
        check("DA duplicates contains", c.contains(5));
        check("DA duplicates size", c.getSize() == 3);

        // boundary indices for add(index, x)
        DynamicArray d = new DynamicArray();
        d.add(1); d.add(2); d.add(3);
        d.add(0, 0);                 // insert at front
        d.add(d.getSize(), 4);       // insert at end (== size)
        check("DA boundary insert front", d.get(0) == 0);
        check("DA boundary insert end", d.get(d.getSize() - 1) == 4);

        // invalid indices
        expectException("DA get negative index", () -> d.get(-1));
        expectException("DA get out-of-range index", () -> d.get(999));
        expectException("DA add invalid index", () -> d.add(999, 1));
        expectException("DA remove invalid index", () -> d.remove(999));

        // resize / large input
        DynamicArray e = new DynamicArray(1); // force many resizes
        for (int i = 0; i < 10_000; i++) e.add(i);
        check("DA large input size", e.getSize() == 10_000);
        check("DA large input last value", e.get(9999) == 9999);
        check("DA capacity grew", e.getCapacity() >= 10_000);

        // remove correctness
        DynamicArray f = new DynamicArray();
        for (int i = 0; i < 5; i++) f.add(i); // 0,1,2,3,4
        int removed = f.remove(2);
        check("DA remove returns correct value", removed == 2);
        check("DA remove shifts left", f.get(2) == 3 && f.getSize() == 4);
    }

    private static void testMyLinkedList() {
        MyLinkedList a = new MyLinkedList();
        check("LL empty size==0", a.getSize() == 0);
        expectException("LL get on empty throws", () -> a.get(0));

        a.add(42);
        check("LL one element get(0)", a.get(0) == 42);

        MyLinkedList b = new MyLinkedList();
        for (int i = 0; i < 20; i++) b.add(i);
        boolean orderOk = true;
        for (int i = 0; i < 20; i++) if (b.get(i) != i) orderOk = false;
        check("LL multiple elements order preserved", orderOk);

        MyLinkedList c = new MyLinkedList();
        c.add(5); c.add(5); c.add(5);
        check("LL duplicates contains", c.contains(5));
        check("LL duplicates size", c.getSize() == 3);

        MyLinkedList d = new MyLinkedList();
        d.add(1); d.add(2); d.add(3);
        d.add(0, 0);
        d.add(d.getSize(), 4);
        check("LL boundary insert front", d.get(0) == 0);
        check("LL boundary insert end", d.get(d.getSize() - 1) == 4);

        expectException("LL get negative index", () -> d.get(-1));
        expectException("LL get out-of-range index", () -> d.get(999));
        expectException("LL add invalid index", () -> d.add(999, 1));
        expectException("LL remove invalid index", () -> d.remove(999));

        MyLinkedList e = new MyLinkedList();
        for (int i = 0; i < 10_000; i++) e.add(i);
        check("LL large input size", e.getSize() == 10_000);
        check("LL large input last value", e.get(9999) == 9999);

        MyLinkedList f = new MyLinkedList();
        for (int i = 0; i < 5; i++) f.add(i);
        int removed = f.remove(2);
        check("LL remove returns correct value", removed == 2);
        check("LL remove relinks correctly", f.get(2) == 3 && f.getSize() == 4);
    }

    private static void testMinHeap() {
        MinHeap h = new MinHeap();
        check("Heap empty size==0", h.getSize() == 0 && h.isEmpty());
        expectException("Heap peekMin on empty throws", h::peekMin);
        expectException("Heap extractMin on empty throws", h::extractMin);

        // one element
        h.insert(10);
        check("Heap one element peekMin", h.peekMin() == 10);
        check("Heap one element extractMin", h.extractMin() == 10);
        check("Heap empty again after extract", h.isEmpty());

        // multiple elements + duplicates, heap property maintained after every insert
        MinHeap h2 = new MinHeap();
        int[] values = {5, 3, 8, 3, 1, 9, 1, 0, 7};
        for (int v : values) {
            h2.insert(v);
            check("Heap property holds after insert(" + v + ")", h2.isValidHeap());
        }

        // extractMin returns non-decreasing order and heap stays valid
        int prev = Integer.MIN_VALUE;
        boolean nonDecreasing = true;
        while (!h2.isEmpty()) {
            int m = h2.extractMin();
            if (m < prev) nonDecreasing = false;
            prev = m;
            if (!h2.isEmpty()) {
                check("Heap property holds after extract", h2.isValidHeap());
            }
        }
        check("Heap extractMin sequence non-decreasing", nonDecreasing);

        // large input, verify full sort + heap property under load
        MinHeap h3 = new MinHeap();
        Random rnd = new Random(42);
        int n = 5000;
        int[] input = new int[n];
        for (int i = 0; i < n; i++) {
            input[i] = rnd.nextInt(100000);
            h3.insert(input[i]);
        }
        check("Heap valid after bulk insert (n=" + n + ")", h3.isValidHeap());

        int[] extracted = new int[n];
        for (int i = 0; i < n; i++) extracted[i] = h3.extractMin();
        boolean sorted = true;
        for (int i = 1; i < n; i++) if (extracted[i] < extracted[i - 1]) sorted = false;
        check("Heap large-input extraction is non-decreasing", sorted);

        int[] expected = input.clone();
        java.util.Arrays.sort(expected);
        check("Heap large-input matches Arrays.sort reference", java.util.Arrays.equals(expected, extracted));
    }

    private static void crossValidateAgainstJavaCollections() {
        Random rnd = new Random(42);
        DynamicArray da = new DynamicArray();
        java.util.ArrayList<Integer> ref = new java.util.ArrayList<>();

        for (int i = 0; i < 2000; i++) {
            int op = rnd.nextInt(3);
            int val = rnd.nextInt(1000);
            if (op == 0 || ref.isEmpty()) {
                da.add(val);
                ref.add(val);
            } else if (op == 1) {
                int idx = rnd.nextInt(ref.size());
                da.add(idx, val);
                ref.add(idx, val);
            } else {
                int idx = rnd.nextInt(ref.size());
                int r1 = da.remove(idx);
                int r2 = ref.remove(idx);
                check("DA vs ArrayList remove match", r1 == r2);
            }
        }

        boolean match = ref.size() == da.getSize();
        for (int i = 0; match && i < ref.size(); i++) {
            if (ref.get(i) != da.get(i)) match = false;
        }
        check("DA matches java.util.ArrayList after random ops", match);

        // Min-Heap vs PriorityQueue
        MinHeap heap = new MinHeap();
        java.util.PriorityQueue<Integer> pq = new java.util.PriorityQueue<>();
        for (int i = 0; i < 5000; i++) {
            int v = rnd.nextInt(1_000_000);
            heap.insert(v);
            pq.add(v);
        }
        boolean heapMatchesPQ = true;
        while (!pq.isEmpty()) {
            if (heap.extractMin() != pq.poll()) heapMatchesPQ = false;
        }
        check("MinHeap matches java.util.PriorityQueue extraction order", heapMatchesPQ);
    }
}
