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
    public int id;
    public GameMode mode;
    public int battle;
    public int opponent;
    public int turn;
    public int currentPlayer;
    public Date ts;
    public boolean synched;
    public String players;
    public String playersH;
    public String map;
    public String mapH;
    public String orders;
    public String ordersH;
    //
    public String oName;
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
            if (canPlay())
                s = String.format("# %s - %s - %s - %s", mode.s, bName, oName, DateFormat.getDateInstance().format(ts));
            else {
                    s = String.format("  %s - %s - %s - %s", mode.s, bName, oName, DateFormat.getDateInstance().format(ts));
            }
        }
        return s;
    }

    public boolean canPlay()
    {
        return ((mode == GameMode.SOLO) || (opponent != currentPlayer));
    }
}
