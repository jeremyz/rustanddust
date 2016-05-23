package ch.asynk.rustanddust.util;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.rustanddust.ui.List;
import ch.asynk.rustanddust.engine.util.Collection;
import ch.asynk.rustanddust.engine.util.IterableArray;

public class PlayerRecord implements List.ListElement, Disposable, Pool.Poolable
{
    public int id;
    public String name;
    public String email;
    public String hash;
    public String s;

    public static Collection<List.ListElement> list = new IterableArray<List.ListElement>(10);

    private static final Pool<PlayerRecord> gameRecordPool = new Pool<PlayerRecord>()
    {
        @Override
        protected PlayerRecord newObject() {
            return new PlayerRecord();
        }
    };

    public static void clearPool()
    {
        gameRecordPool.clear();
    }

    public static PlayerRecord get()
    {
        PlayerRecord r = gameRecordPool.obtain();
        return r;
    }

    public static PlayerRecord get(int idx)
    {
        return (PlayerRecord) list.get(idx);
    }

    public static PlayerRecord remove(int idx)
    {
        return (PlayerRecord) list.remove(idx);
    }

    public static void clearList()
    {
        for(List.ListElement r : list)
            ((PlayerRecord) r).dispose();
        list.clear();
    }

    public PlayerRecord()
    {
    }

    @Override
    public void reset()
    {
        this.s = null;
    }

    @Override
    public void dispose()
    {
        gameRecordPool.free(this);
    }

    @Override
    public String s()
    {
        if (s == null) {
            s = String.format("%s - %s", name , (hasEmail() ? "OK" : "-"));
        }
        return s;
    }

    public boolean hasEmail()
    {
        return (this.email != null && !this.email.isEmpty());
    }
}
