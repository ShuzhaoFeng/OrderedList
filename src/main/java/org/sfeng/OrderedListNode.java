package org.sfeng;

public class OrderedListNode<E> {
    private E data;

    private int prev;

    private int next;

    public OrderedListNode(E data) {
        this.data = data;
        this.prev = -1;
        this.next = -1;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public int getPrev() {
        return prev;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
