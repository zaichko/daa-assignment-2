public class MinHeap {
    int[] data;
    int size;
    int capacity;

    public long comparisonCount = 0;
    public long movementCount = 0; // swaps

    public MinHeap(){
        this.data = new int[10];
        this.size = 0;
        this.capacity = 10;
    }

    public MinHeap(int initial){
        if (initial <= 0){
            throw new IllegalArgumentException();
        }

        this.data = new int[initial];
        this.size = 0;
        this.capacity = initial;
    }

    public void resetCounters() {
        comparisonCount = 0;
        movementCount = 0;
    }

    public int getSize(){
        return this.size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public void insert(int x){
        ensureCapacity();
        data[size] = x;
        size++;
        siftUp(size - 1);
    }

    private void ensureCapacity(){
        if (size == capacity){
            capacity *= 2;
            int[] newData = new int[capacity];

            for (int i = 0; i < size; i++){
                newData[i] = data[i];
            }

            this.data = newData;
        }
    }

    private void siftUp(int index){
        while (index > 0){
            int parent = (index - 1) / 2;

            comparisonCount++;
            if (data[index] < data[parent]){
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void swap(int a, int b){
        int temp = data[a];
        data[a] = data[b];
        data[b] = temp;
        movementCount++;
    }

    public int peekMin(){
        if (size == 0){
            throw new IllegalStateException();
        }

        return data[0];
    }

    public int extractMin(){
        if (size == 0){
            throw new IllegalStateException();
        }

        int min = data[0];
        size--;

        if (size > 0){
            data[0] = data[size];
            siftDown(0);
        }

        return min;
    }

    private void siftDown(int index){
        while (true) {
            int left = (index * 2) + 1;
            int right = (index * 2) + 2;
            int smallest = index;

            if (left < size){
                comparisonCount++;
                if (data[left] < data[smallest]){
                    smallest = left;
                }
            }
            if (right < size){
                comparisonCount++;
                if (data[right] < data[smallest]){
                    smallest = right;
                }
            }

            if (smallest == index){
                break;
            }

            swap(smallest, index);
            index = smallest;
        }
    }

    public boolean isValidHeap() {
        for (int i = 0; i < size; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < size && data[left] < data[i]) return false;
            if (right < size && data[right] < data[i]) return false;
        }
        return true;
    }


}
