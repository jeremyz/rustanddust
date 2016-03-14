package ch.asynk.rustanddust.util;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.rustanddust.ui.List;
import ch.asynk.rustanddust.engine.util.Collection;
import ch.asynk.rustanddust.engine.util.IterableArray;

public class TurnRecord implements List.ListElement, Disposable, Pool.Poolable
{
    public int id;
    public int game;
    public int turn;
    public int player;
    public String hash;
    public String payload;

    public static Collection<List.ListElement> list = new IterableArray<List.ListElement>(10);

    private static final Pool<TurnRecord> turnRecordPool = new Pool<TurnRecord>()
    {
        @Override
        protected TurnRecord newObject() {
            return new TurnRecord();
        }
    };

    public static void clearPool()
    {
        turnRecordPool.clear();
    }

    public static TurnRecord get()
    {
        TurnRecord r = turnRecordPool.obtain();
        return r;
    }

    public static TurnRecord get(int idx)
    {
        return (TurnRecord) list.get(idx);
    }

    public static void clearList()
    {
        for(List.ListElement r : list)
            ((TurnRecord) r).dispose();
        list.clear();
    }

    public TurnRecord()
    {
    }

    @Override
    public void reset()
    {
    }

    @Override
    public void dispose()
    {
        turnRecordPool.free(this);
    }

    @Override
    public String s()
    {
        return String.format("turn(id): g:%d t:%d p:%d", id, game, turn, player);
    }
}
