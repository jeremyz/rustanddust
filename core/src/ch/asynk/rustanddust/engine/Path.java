package ch.asynk.rustanddust.engine;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.rustanddust.engine.util.IterableArray;

public class Path implements Disposable, Pool.Poolable
{
    private static final Pool<Path> pathPool = new Pool<Path>() {
        @Override
        protected Path newObject() {
            return new Path();
        }
    };

    public static Path get(int size)
    {
        Path p = pathPool.obtain();
        p.init(size);
        return p;
    }

    public static void clearPool()
    {
        pathPool.clear();
    }

    public int cost;
    public int fitness;
    public boolean roadMarch;
    public IterableArray<Tile> tiles;

    public Path()
    {
        this.cost = -1;
        this.roadMarch = true;
        this.tiles = null;
        this.fitness = 0;
    }

    protected void init(int size)
    {
        if (tiles == null)
            tiles = new IterableArray<Tile>(size);
        else
            tiles.ensureCapacity(size);
        cost = -1;
        roadMarch = true;
        fitness = 0;
    }

    @Override
    public void reset()
    {
        cost = -1;
        roadMarch = true;
        tiles.clear();
    }

    @Override
    public void dispose()
    {
        tiles.clear();
        pathPool.free(this);
    }

    @Override
    public String toString()
    {
        String s = String.format("path(c:%d - r:%b - f:%d)\n", cost, roadMarch, fitness);
        for (Tile t : tiles)
            s += String.format("  %s\n", t.toString());
        return s;
    }
}
