package ch.asynk.rustanddust.engine;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

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
    public ArrayList<Tile> tiles;

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
            tiles = new ArrayList<Tile>(size);
        else
            tiles. ensureCapacity(size);
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
}
