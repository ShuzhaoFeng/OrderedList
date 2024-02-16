package org.sfeng;

import java.lang.reflect.Modifier;
import java.util.*;
import java.lang.reflect.Field;

public class OrderedList<E> implements List<E> {
    private final HashMap<Integer, E> map = new HashMap<>() {{
        put(0, null);
        put(Integer.MAX_VALUE, null);
    }};

    private int head = -1;

    private int tail = -1;

    public OrderedList(Class<E> type) {
        Map<String, Class<?>> fields = getAllFields(type);

        if (fields.containsKey("olRank")) {
            var field = fields.get("olRank");
            if (field.equals(Integer.class)) {
                int modifiers = field.getModifiers();

                if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                    throw new IllegalArgumentException("The 'olRank' field must be public, non-static and non-final");
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
        return map.size() - 2;
    }

    @Override
    public boolean isEmpty() {
        return map.size() == 2;
    }

    @Override
    public boolean contains(Object o) {
        for (E e : map.values()) {
            if (Objects.equals(e, o)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return map.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return map.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return map.values().toArray(a);
    }

    @Override
    public boolean add(E e) {
        int olRank;

        try {
            olRank = e.getClass().getField("olRank").getInt(e);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new IllegalArgumentException("The element does not have a public field named 'olRank' of type Integer", ex);
        }

        if (olRank <= 0) {
            // update rank and append to the end
        }

        if (map.containsKey(olRank)) {
            throw new IllegalArgumentException("An element with the same rank already exists");
        }

        map.put(olRank, e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
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
        map.put(0, null);
        map.put(Integer.MAX_VALUE, null);
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
        for (E e : map.values()) {
            if (i == index) {
                return e;
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
        for (Map.Entry<Integer, E> entry : map.entrySet()) {
            if (i == index) {
                E old = entry.getValue();
                entry.setValue(element);
                return old;
            }
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
        for (E e : map.values()) {
            if (Objects.equals(e, o)) {
                return i;
            }
            i++;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int i = 0;
        int last = -1;
        for (E e : map.values()) {
            if (Objects.equals(e, o)) {
                last = i;
            }
            i++;
        }

        return last;
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
}
