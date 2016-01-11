package ch.asynk.rustanddust;

import org.junit.Test;

import ch.asynk.rustanddust.engine.util.IterableArray;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class Util
{
    @Test
    public void testArray()
    {
        IterableArray<Object> a = new IterableArray<Object>(3);
        assertTrue(a.isEmpty());
        assertTrue(a.size() == 0);
        a.add(new Object());
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 1);
        a.add(new Object());
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 2);
        Object o = new Object();
        assertFalse(a.contains(o));
        a.add(o);
        assertTrue(a.contains(o));
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 3);
        a.add(new Object());
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 4);

        assertTrue(a.get(2) == o);

        a.clear();
        assertTrue(a.isEmpty());
        assertTrue(a.size() == 0);

        a.add(new Object());
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 1);
        a.add(new Object());
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 2);
        assertFalse(a.contains(o));
        a.add(o);
        assertTrue(a.contains(o));
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 3);
        a.add(new Object());
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 4);

        assertTrue(a.get(2) == o);
        a.add(o);
        assertTrue(a.contains(o));

        assertFalse(a.isEmpty());
        assertTrue(a.size() == 5);
        assertTrue(a.remove(o));
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 4);
        assertTrue(a.remove(o));
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 3);
        assertFalse(a.remove(o));

        assertNotNull(a.remove(1));
        assertFalse(a.isEmpty());
        assertTrue(a.size() == 2);
    }
}
