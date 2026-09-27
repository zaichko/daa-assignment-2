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

    public MyLinkedList(){
        this.head = null;
        this.size = 0;
    }

    public void add(int x){
        if(head == null){
            head = new Node(x);
            size++;
            return;
        }

        Node current = head;

        while (current.next != null){
            current = current.next;
        }

        current.next = new Node(x);
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
            size++;
            return;
        }

        Node current = head;

        for (int i = 0; i < index - 1; i++){
            current = current.next;
        }

        Node newNode = new Node(x);
        newNode.next = current.next;
        current.next = newNode;
        size++;
    }

    public int remove(int index){
        if (index < 0 || index >= size){
            throw new IndexOutOfBoundsException();
        }

        if (index == 0){
            Node removed = head;

            head = head.next;
            size--;

            return removed.data;
        }

        Node previous = getNode(index - 1);
        Node removed = previous.next;

        previous.next = previous.next.next;

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

        for (int i = 0; i < index; i++){
            current = current.next;
        }

        return current;
    }

    public boolean contains(int x){

        Node current = head;

        while (current != null){
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
