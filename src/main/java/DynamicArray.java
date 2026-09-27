public class DynamicArray {
    private int[] data;
    private int size;
    private int capacity;

    public DynamicArray(){
        this.data = new int[10];
        this.size = 0;
        this.capacity = 10;
    }

    public DynamicArray(int initial){
        if (initial <= 0){
            throw new IllegalArgumentException();
        }
        this.data = new int[initial];
        this.size = 0;
        this.capacity = initial;
    }

    public int get(int index){
        if (index < 0 || index >= size){
            throw new ArrayIndexOutOfBoundsException();
        }
        return data[index];
    }

    public void add(int x){
        resize();

        data[size] = x;
        size++;
    }

    public void add(int index, int x){
        if (index < 0 || index > size){
            throw new ArrayIndexOutOfBoundsException();
        }

        resize();

        for (int i = size; i > index; i--){
            data[i] = data[i - 1];
        }

        data[index] = x;
        size++;
    }

    public int remove(int index){
        int removed = get(index);

        for (int i = index; i < size - 1; i++){
            data[i] = data[i + 1];
        }

        size--;

        return removed;
    }

    public boolean contains(int x){
        for (int i = 0; i < size; i++){
            if (data[i] == x){
                return true;
            }
        }

        return false;
    }

    private void resize(){
        if (size == capacity){
            int newCapacity = capacity * 2;
            int[] newData = new int[newCapacity];

            for (int i = 0; i < size; i++){
                newData[i] = data[i];
            }

            this.data = newData;
            this.capacity = newCapacity;
        }
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public int[] getData() {
        return data;
    }
}
