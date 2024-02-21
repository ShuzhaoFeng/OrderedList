package org.sfeng;

import java.lang.reflect.Modifier;
import java.util.*;
import java.lang.reflect.Field;

public class OrderedList<E> {
    private final HashMap<Integer, OrderedListNode<E>> map = new HashMap<>() {{
        put(0, new OrderedListNode<>(null));
        put(Integer.MAX_VALUE, new OrderedListNode<>(null));
    }};

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

    public int size() {
        return map.size() - 2;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

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

    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int current = 0;

            public boolean hasNext() {
                OrderedListNode<E> node = map.get(current);
                return node != null && node.getNext() != -1;
            }

            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                current = map.get(current).getNext();
                return map.get(current).getData();
            }
        };
    }

    public Object[] toArray() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public <T> T[] toArray(T[] a) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public List<E> toList() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    public boolean addAll(Collection<? extends E> c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void clear() {
        map.clear();
        map.put(0, new OrderedListNode<>(null));
        map.put(Integer.MAX_VALUE, new OrderedListNode<>(null));
        head = -1;
        tail = -1;
    }

    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrderedList<?> that = (OrderedList<?>) o;

        return Objects.equals(map, that.map) &&
                head == that.head &&
                tail == that.tail;
    }

    public int hashCode() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public E get(int rank) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public E set(int rank, E element) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void add(int rank, E element) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public E remove(int rank) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public ListIterator<E> listIterator() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public ListIterator<E> listIterator(int index) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public List<E> subList(int fromRank, int toRank) {
        // TODO
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
