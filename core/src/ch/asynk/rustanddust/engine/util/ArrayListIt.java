package ch.asynk.rustanddust.engine.util;

import java.util.ArrayList;
import java.util.Iterator;

public class ArrayListIt<E> extends ArrayList<E> implements Iterator, Iterable<E>
{
    private int i;
    private int s;

    public ArrayListIt()
    {
        super();
    }

    public ArrayListIt(int n)
    {
        super(n);
    }

    public void push(E e)
    {
        add(e);
    }

    public E pop()
    {
        return remove(size() - 1);
    }

    public void enqueue(E e)
    {
        add(e);
    }

    public E dequeue()
    {
        return remove(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<E> iterator()
    {
        this.i = 0;
        this.s = size();
        return (Iterator<E>) this;
    }

    @Override
    public boolean hasNext()
    {
        return (i < s);
    }

    @Override
    public E next()
    {
        E e = get(i);
        i += 1;
        return e;
    }

    @Override
    public void remove()
    {
        i -=1;
        s -= 1;
        remove(i);
    }
}
