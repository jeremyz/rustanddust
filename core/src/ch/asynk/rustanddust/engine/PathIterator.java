package ch.asynk.rustanddust.engine;

import java.util.List;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PathIterator implements Iterator<Vector3>
{
    private Pawn pawn;
    private boolean rotation;
    private Tile to;
    private Orientation o;
    private Orientation orientation;
    private Tile tile;
    private Vector2 pos = new Vector2();
    private Vector3 v = new Vector3();
    private int i;
    private int s;
    private List<Tile> path;

    public PathIterator(Pawn pawn, Tile from, Tile to, Orientation orientation, List<Tile> path)
    {
        this.pawn = pawn;
        this.to = to;
        this.tile = from;
        this.rotation = (from == to);
        this.orientation = orientation;
        this.path = path;
        this.o = pawn.getOrientation();
        this.v.set(pawn.getPosition().x, pawn.getPosition().y, o.r());
        this.i = 0;
        this.s = path.size();
    }

    @Override
    public boolean hasNext()
    {
        if ((rotation || (i > s)) && (o == orientation))
            return false;
        return true;
    }

    @Override
    public Vector3 next()
    {
        if (!hasNext())
            throw new java.util.NoSuchElementException();

        if (rotation || (i > s)) {
            v.z = orientation.r();
            o = orientation;
            return v;
        }
        Tile nextTile;
        if (i < s)
            nextTile = path.get(i);
        else
            nextTile = to;
        Orientation nextO = Orientation.fromMove(tile.col, tile.row, nextTile.col, nextTile.row);
        if (nextO != o) {
            v.z = nextO.r();
            o = nextO;
            return v;
        }
        pawn.getPosAt(nextTile, pos);
        v.x = pos.x;
        v.y = pos.y;
        tile = nextTile;
        i += 1;
        return v;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
