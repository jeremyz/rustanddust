package ch.asynk.rustanddust.engine.util;

import java.util.ArrayList;
import java.util.Iterator;

public class IterableArray<E> extends ArrayList<E> implements Collection<E>
{
    private int i;
    private int s;

    public IterableArray()
    {
        super();
    }

    public IterableArray(int n)
    {
        super(n);
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
