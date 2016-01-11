package ch.asynk.rustanddust.engine.util;

import java.util.Iterator;

public interface Collection<E> extends Iterator, Iterable<E>
{
    public void clear();

    public int size();

    public boolean isEmpty();

    public E get(int idx);

    public boolean add(E e);

    // public boolean contains(E e);
}
