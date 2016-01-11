package ch.asynk.rustanddust.engine.util;

import java.util.Iterator;

public interface Collection<E> extends Iterator, Iterable<E>
{
    public int size();

    public boolean isEmpty();

    public void clear();

    public void ensureCapacity(int c);

    public boolean contains(E e);

    public E get(int idx);

    public boolean add(E e);

    public E remove(int idx);

    public boolean remove(E e);
}
