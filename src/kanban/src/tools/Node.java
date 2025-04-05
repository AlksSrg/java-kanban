package kanban.src.tools;


public class Node<T> {
    private T task;
    private Node<T> next;
    private Node<T> prev;

    public Node(Node<T> prev, T element, Node<T> next) {

        if (prev != null && element.equals(prev.task)) {
            this.prev = prev.getPrevious();
            this.next = prev.getNext();
        } else {
            this.next = next;
            this.prev = prev;
        }
        this.task = element;
    }

    public void setNext(Node<T> element) {
        this.next = element;
    }

    public void setPrevious(Node<T> element) {
        this.prev = element;
    }

    public Node<T> getNext() {
        return this.next;
    }

    public Node<T> getPrevious() {
        return this.prev;
    }

    public T getTask() {
        return this.task;
    }

}
