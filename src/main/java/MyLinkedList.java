public class MyLinkedList {
    private static class Node{
        int data;
        Node next;

        Node(int data){
            this.data = data;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    public long accessCount = 0;
    public long comparisonCount = 0;
    public long movementCount = 0;

    public MyLinkedList(){
        this.head = null;
        this.size = 0;
    }

    public void resetCounters(){
        accessCount = 0;
        comparisonCount = 0;
        movementCount = 0;
    }

    public void add(int x){
        if(head == null){
            head = new Node(x);
            size++;
            return;
        }

        Node current = head;
        accessCount++;

        while (current.next != null){
            current = current.next;
            accessCount++;
        }

        current.next = new Node(x);
        movementCount++;
        size++;
    }

    public void add(int index, int x){
        if (index < 0 || index > size){
            throw new IndexOutOfBoundsException();
        }

        if (index == 0){
            Node newNode = new Node(x);
            newNode.next = head;
            this.head = newNode;
            movementCount++;
            size++;
            return;
        }

        Node current = head;

        for (int i = 0; i < index - 1; i++){
            current = current.next;
            accessCount++;
        }

        Node newNode = new Node(x);
        newNode.next = current.next;
        current.next = newNode;
        movementCount++;
        size++;
    }

    public int remove(int index){
        if (index < 0 || index >= size){
            throw new IndexOutOfBoundsException();
        }

        if (index == 0){
            Node removed = head;

            head = head.next;
            movementCount++;
            size--;

            return removed.data;
        }

        Node previous = getNode(index - 1);
        Node removed = previous.next;

        previous.next = previous.next.next;
        movementCount++;

        size--;

        return removed.data;
    }

    public int get(int index){
        Node node = getNode(index);

        return node.data;
    }

    private Node getNode(int index){
        if (index < 0 || index >= size){
            throw new IndexOutOfBoundsException();
        }

        Node current = head;
        accessCount++;

        for (int i = 0; i < index; i++){
            current = current.next;
            accessCount++;
        }

        return current;
    }

    public boolean contains(int x){

        Node current = head;

        while (current != null){
            comparisonCount++;
            if (current.data == x){
                return true;
            }
            current = current.next;
        }

        return false;
    }

    public int getSize(){
        return this.size;
    }
}
