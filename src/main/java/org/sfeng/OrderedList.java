package org.sfeng;

import java.lang.reflect.Modifier;
import java.util.*;
import java.lang.reflect.Field;

public class OrderedList<E> implements List<E> {
    private final HashMap<Integer, OrderedListNode<E>> map = new HashMap<>();

    private int head = -1;

    private int tail = -1;

    private final Field olRank;

    public OrderedList(Class<E> type){
        Map<String, Class<?>> fields = getAllFields(type);

        if (fields.containsKey("olRank")) {
            var field = fields.get("olRank");
            if (field.equals(Integer.class)) {
                int modifiers = field.getModifiers();

                if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                    throw new IllegalArgumentException("The 'olRank' field must be public, non-static and non-final");
                }

                try {
                    olRank = type.getField("olRank");
                } catch (NoSuchFieldException ex) {
                    throw new IllegalArgumentException("The class must have a public field named 'olRank' of type Integer");
                }
            } else {
                throw new IllegalArgumentException("The 'olRank' field must be of type Integer");
            }
        } else {
            throw new IllegalArgumentException("The class must have a public field named 'olRank' of type Integer");
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        try {
            int rank = olRank.getInt(o);

            return Objects.equals(map.get(rank).getData(), o);
        } catch (IllegalAccessException ex) {
            return false;
        }
    }

    public boolean containsIgnoreRank(Object o) {
        for (OrderedListNode<E> node : map.values()) {
            if (node.getData() == o) return true;

            if (node.getData().getClass() != o.getClass()) continue;

            for (Map.Entry<String, Class<?>> entry : getAllFields(o.getClass()).entrySet()) {
                try {
                    Field field = o.getClass().getField(entry.getKey());
                    if (field.get(o) != field.get(node.getData())) {
                        return false;
                    }
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public Iterator<E> iterator() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E e) {
        int rank;

        try {
            rank = olRank.getInt(e);

            // if the rank is not set, update rank to append at the end
            if (rank <= 0) {
                rank = computeRank(tail, Integer.MAX_VALUE);

                if (rank == -1) {
                    rank = rippleBalanceLoad(size() - 1);
                }

                e.getClass().getField("olRank").setInt(e, rank);
                tail = rank;
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new IllegalArgumentException("The element does not have a public field named 'olRank' of type Integer", ex);
        }

        if (map.containsKey(rank)) {
            throw new IllegalArgumentException("An element with the same rank already exists");
        }

        // TODO: add element to map, attach prev and next
        return true;
    }

    @Override
    public boolean remove(Object o) {
        for (Map.Entry<Integer, OrderedListNode<E>> entry : map.entrySet()) {
            if (Objects.equals(entry.getValue(), o)) {

                if (entry.getKey() == head) {
                    // TODO
                }

                if (entry.getKey() == tail) {
                    // TODO
                }

                map.remove(entry.getKey());

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            add(e);
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        map.clear();
        head = -1;
        tail = -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrderedList<?> that = (OrderedList<?>) o;

        return Objects.equals(map, that.map) &&
                head == that.head &&
                tail == that.tail;
    }

    @Override
    public int hashCode() {
        return Objects.hash(map, head, tail);
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }

        int i = 0;
        for (OrderedListNode<E> node : map.values()) {
            if (i == index) {
                return node.getData();
            }

            i++;
        }

        return null;
    }

    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }

        int i = 0;
        OrderedListNode<E> current = map.get(head);

        while (current != null) {
            if (i == index) {
                E old = current.getData();

                try {
                    int rank = olRank.getInt(old);

                    if (rank <= 0) {
                        rank = computeRank(current.getPrev(), current.getNext());

                        if (rank == -1) {
                            rank = rippleBalanceLoad(size() - 1);
                        }

                        olRank.setInt(element, rank);
                    }
                } catch (IllegalAccessException ex) {
                    throw new IllegalArgumentException("The element does not have a public field named 'olRank' of type Integer", ex);
                }

                current.setData(element);
                return old;
            }

            current = map.get(current.getNext());

            i++;
        }

        return null;
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        int i = 0;
        OrderedListNode<E> current = map.get(head);

        while (current != null) {
            if (Objects.equals(current.getData(), o)) {
                return i;
            }
            current = map.get(current.getNext());

            i++;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int i = 0;
        OrderedListNode<E> current = map.get(tail);

        while (current != null) {
            if (Objects.equals(current.getData(), o)) {
                return i;
            }
            current = map.get(current.getPrev());

            i++;
        }

        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size();
            }

            @Override
            public E next() {
                if (cursor >= size()) {
                    throw new NoSuchElementException();
                }

                return get(cursor++);
            }

            @Override
            public boolean hasPrevious() {
                return cursor > 0;
            }

            @Override
            public E previous() {
                if (cursor <= 0) {
                    throw new NoSuchElementException();
                }

                return get(--cursor);
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                return cursor - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(E e) {
                OrderedList.this.set(cursor - 1, e);
            }

            @Override
            public void add(E e) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }

        return new ListIterator<>() {
            private int cursor = index;

            @Override
            public boolean hasNext() {
                return cursor < size();
            }

            @Override
            public E next() {
                if (cursor >= size()) {
                    throw new NoSuchElementException();
                }

                return get(cursor++);
            }

            @Override
            public boolean hasPrevious() {
                return cursor > 0;
            }

            @Override
            public E previous() {
                if (cursor <= 0) {
                    throw new NoSuchElementException();
                }

                return get(--cursor);
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                return cursor - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(E e) {
                OrderedList.this.set(cursor - 1, e);
            }

            @Override
            public void add(E e) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    private Map<String, Class<?>> getAllFields(Class<?> type) {
        Map<String, Class<?>> fields = new HashMap<>();

        for (Field field : type.getDeclaredFields()) {
            fields.put(field.getName(), field.getClass());
        }

        Class<?> superClass = type.getSuperclass();

        if (superClass != null) {
            fields.putAll(getAllFields(superClass));
        }

        return fields;
    }

    private int computeRank(int before, int after) {
        if (after - before < 2) return -1;

        return (before + after) / 2;
    }

    private int rippleBalanceLoad(int index) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
