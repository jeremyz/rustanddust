package ch.asynk.rustanddust.util;

import java.util.Date;
import java.text.DateFormat;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.rustanddust.ui.List;
import ch.asynk.rustanddust.game.Config.GameMode;
import ch.asynk.rustanddust.engine.util.Collection;
import ch.asynk.rustanddust.engine.util.IterableArray;

public class GameRecord implements List.ListElement, Disposable, Pool.Poolable
{
    public int g;
    public int p1;
    public int p2;
    public int b;
    public GameMode m;
    public Date ts;
    public String p1Name;
    public String p2Name;
    public String bName;
    public String s;

    public static Collection<List.ListElement> list = new IterableArray<List.ListElement>(10);

    private static final Pool<GameRecord> gameRecordPool = new Pool<GameRecord>()
    {
        @Override
        protected GameRecord newObject() {
            return new GameRecord();
        }
    };

    public static void clearPool()
    {
        gameRecordPool.clear();
    }

    public static GameRecord get()
    {
        GameRecord r = gameRecordPool.obtain();
        return r;
    }

    public static GameRecord get(int idx)
    {
        return (GameRecord) list.get(idx);
    }

    public static GameRecord remove(int idx)
    {
        return (GameRecord) list.remove(idx);
    }

    public static void clearList()
    {
        for(List.ListElement r : list)
            ((GameRecord) r).dispose();
        list.clear();
    }

    public GameRecord()
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
            if (m == GameMode.SOLO)
                s = String.format("# - %s - %s - %s - %s", m.s, bName, p1Name, DateFormat.getDateInstance().format(ts));
            else {
                if (p1 == 1)
                    s = String.format("# - %s - %s - %s - %s", m.s, bName, p2Name, DateFormat.getDateInstance().format(ts));
                else
                    s = String.format("  - %s - %s - %s - %s", m.s, bName, p1Name, DateFormat.getDateInstance().format(ts));
            }
        }
        return s;
    }

    @Override
    public String toString()
    {
        return String.format("%d %d(%s) %d(%s) %d(%s) %s %s", g, p1, p1Name, p2, p2Name, b, bName, m.s, DateFormat.getDateInstance().format(ts));
    }
}
