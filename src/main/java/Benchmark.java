import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Benchmark {
    static final int[] SIZES = {100, 1000, 10000, 100000};
    static final int SEED = 42;
    static final int REPEATS = 5;

    public static void main(String[] args) throws IOException {
        new File("results/tables").mkdir();

        workload1RandomAccess();
        workload2Search();
        workload3InsertRemove();
        workload4Heap();

        System.out.println("Benchmark complete. CSVs written to results/tables/");
    }

    static void workload1RandomAccess() throws IOException{
        try (PrintWriter out = csv("results/tables/workload1_random_access.csv",
                "n,structure,avg_time_ms,access_count,theoretical")){
            for (int n : SIZES){
                int[] initial = randomInts(n, new Random(SEED));
                int[] indices = randomIndices(10000, n, new Random(SEED));

                DynamicArray da = new DynamicArray(n);
                for (int v : initial){
                    da.add(v);
                }

                MyLinkedList ll = new MyLinkedList();
                for (int v : initial){
                    ll.add(v);
                }

                double daTime = 0, llTime = 0;
                long daAccess = 0, llAccess = 0;
                for (int r = 0; r < REPEATS; r++){
                    da.resetCounters();
                    long t0 = System.nanoTime();
                    for (int idx : indices){
                        da.get(idx);
                    }
                    daTime += System.nanoTime() - t0;
                    daAccess = da.accessCount;

                    ll.resetCounters();
                    long t1 = System.nanoTime();
                    for (int idx : indices){
                        ll.get(idx);
                    }
                    llTime += System.nanoTime() - t1;
                    llAccess = ll.accessCount;
                }
                out.printf("%d,DynamicArray,%.4f,%d,O(1)%n", n, msPerRepeat(daTime), daAccess);
                out.printf("%d,LinkedList,%.4f,%d,O(n)%n", n, msPerRepeat(llTime), llAccess);
            }
        }
    }

    static void workload2Search() throws IOException{
        try (PrintWriter out = csv("results/tables/workload2_search.csv",
                "n,structure,avg_time_ms,comparison_count,theoretical")){
            for (int n : SIZES){
                int[] initial = randomInts(n, new Random(SEED));
                int[] queries = randomInts(1000, new Random(SEED));

                DynamicArray da = new DynamicArray(n);
                for (int v : initial){
                    da.add(v);
                }

                MyLinkedList ll = new MyLinkedList();
                for (int v : initial){
                    ll.add(v);
                }

                double daTime = 0, llTime = 0;
                long daCmp = 0, llCmp = 0;
                for (int r = 0; r < REPEATS; r++){
                    da.resetCounters();
                    long t0 = System.nanoTime();
                    for (int q : queries) da.contains(q);
                    daTime += (System.nanoTime() - t0);
                    daCmp = da.comparisonCount;

                    ll.resetCounters();
                    long t1 = System.nanoTime();
                    for (int q : queries) ll.contains(q);
                    llTime += (System.nanoTime() - t1);
                    llCmp = ll.comparisonCount;
                }

                out.printf("%d,DynamicArray,%.4f,%d,O(n)%n", n, msPerRepeat(daTime), daCmp);
                out.printf("%d,LinkedList,%.4f,%d,O(n)%n", n, msPerRepeat(llTime), llCmp);
            }
        }
    }

    static void workload3InsertRemove() throws IOException{
        try (PrintWriter out = csv("results/tables/workload3_insert_remove.csv",
                "n,structure,position,operation,avg_time_ms,movement_count,ops_performed,theoretical")){
            for (int n : SIZES){
                runInsertRemove(out, n, "front", true);
                runInsertRemove(out, n, "middle", false);
            }
        }
    }

    static void runInsertRemove(PrintWriter out, int n, String position, boolean front){
        int removalOps = Math.min(1000, n); // cannot remove more than n elements exist

        double daInsTime = 0, llInsTime = 0;
        long daInsMove = 0, llInsMove = 0;
        for (int r = 0; r < REPEATS; r++) {
            DynamicArray da = freshDynamicArray(n);
            MyLinkedList ll = freshLinkedList(n);
            int[] values = randomInts(1_000, new Random(SEED + r));

            da.resetCounters();
            long t0 = System.nanoTime();
            for (int v : values) {
                int idx = front ? 0 : da.getSize() / 2;
                da.add(idx, v);
            }
            daInsTime += (System.nanoTime() - t0);
            daInsMove = da.movementCount;

            ll.resetCounters();
            long t1 = System.nanoTime();
            for (int v : values) {
                int idx = front ? 0 : ll.getSize() / 2;
                ll.add(idx, v);
            }
            llInsTime += (System.nanoTime() - t1);
            llInsMove = ll.movementCount;
        }

        String insTheoryDA = front ? "O(n)" : "O(n)";
        String insTheoryLL = front ? "O(1)" : "O(n)";
        out.printf("%d,DynamicArray,%s,insert,%.4f,%d,%d,%s%n", n, position, msPerRepeat(daInsTime), daInsMove, 1000, insTheoryDA);
        out.printf("%d,LinkedList,%s,insert,%.4f,%d,%d,%s%n", n, position, msPerRepeat(llInsTime), llInsMove, 1000, insTheoryLL);


        double daRemTime = 0, llRemTime = 0;
        long daRemMove = 0, llRemMove = 0;
        for (int r = 0; r < REPEATS; r++) {
            DynamicArray da = freshDynamicArray(n);
            MyLinkedList ll = freshLinkedList(n);

            da.resetCounters();
            long t0 = System.nanoTime();
            for (int i = 0; i < removalOps; i++) {
                int idx = front ? 0 : da.getSize() / 2;
                da.remove(idx);
            }
            daRemTime += (System.nanoTime() - t0);
            daRemMove = da.movementCount;

            ll.resetCounters();
            long t1 = System.nanoTime();
            for (int i = 0; i < removalOps; i++) {
                int idx = front ? 0 : ll.getSize() / 2;
                ll.remove(idx);
            }
            llRemTime += (System.nanoTime() - t1);
            llRemMove = ll.movementCount;
        }
        String remTheoryDA = "O(n)";
        String remTheoryLL = front ? "O(1)" : "O(n)";
        out.printf("%d,DynamicArray,%s,remove,%.4f,%d,%d,%s%n", n, position, msPerRepeat(daRemTime), daRemMove, removalOps, remTheoryDA);
        out.printf("%d,LinkedList,%s,remove,%.4f,%d,%d,%s%n", n, position, msPerRepeat(llRemTime), llRemMove, removalOps, remTheoryLL);
    }

    static DynamicArray freshDynamicArray(int n){
        DynamicArray da = new DynamicArray(n);
        int[] initial = randomInts(n, new Random(SEED));
        for (int v : initial){
            da.add(v);
        }
        return da;
    }

    static MyLinkedList freshLinkedList(int n){
        MyLinkedList ll = new MyLinkedList();
        int[] initial = randomInts(n, new Random(SEED));
        for (int v : initial) ll.add(v);
        return ll;
    }


    static void workload4Heap() throws IOException {
        try (PrintWriter out = csv("results/tables/workload4_heap.csv",
                "n,phase,avg_time_ms,comparison_count,theoretical")) {
            for (int n : SIZES) {
                double insTime = 0, extTime = 0;
                long insCmp = 0, extCmp = 0;
                boolean allNonDecreasing = true;

                for (int r = 0; r < REPEATS; r++) {
                    int[] values = randomInts(n, new Random(SEED + r));
                    MinHeap heap = new MinHeap(Math.max(1, n));

                    long t0 = System.nanoTime();
                    for (int v : values) heap.insert(v);
                    insTime += (System.nanoTime() - t0);
                    insCmp = heap.comparisonCount;

                    heap.resetCounters();
                    int prev = Integer.MIN_VALUE;
                    long t1 = System.nanoTime();
                    for (int i = 0; i < n; i++) {
                        int m = heap.extractMin();
                        if (m < prev) allNonDecreasing = false;
                        prev = m;
                    }
                    extTime += (System.nanoTime() - t1);
                    extCmp = heap.comparisonCount;
                }
                out.printf("%d,insert,%.4f,%d,O(log n)%n", n, msPerRepeat(insTime), insCmp);
                out.printf("%d,extractMin,%.4f,%d,O(log n)%n", n, msPerRepeat(extTime), extCmp);
                if (!allNonDecreasing) {
                    System.out.println("WARNING: non-decreasing check failed for n=" + n);
                }
            }
        }
    }

    static int[] randomInts(int count, Random rnd){
        int[] arr = new int[count];
        for (int i = 0; i < count; i++){
            arr[i] = rnd.nextInt();
        }
        return arr;
    }

    static int[] randomIndices(int count, int bound, Random rnd){
        int safeBound = Math.max(1, bound);
        int[] arr = new int[count];

        for (int i = 0; i < count; i++){
            arr[i] = rnd.nextInt(safeBound);
        }
        return arr;
    }

    static PrintWriter csv(String path, String header) throws IOException{
        PrintWriter out = new PrintWriter(new FileWriter(path));
        out.println(header);
        return out;
    }

    static double msPerRepeat(double totalNanos){
        return (totalNanos / REPEATS) / 1000000.0;
    }
}
