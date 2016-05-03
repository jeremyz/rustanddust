package ch.asynk.rustanddust.engine;

import java.util.Iterator;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Move extends Path implements Iterable<Vector3>, Iterator
{
    public enum MoveType
    {
        REGULAR,
        SET,
        ENTER,
        EXIT;
    }

    private static final Pool<Move> movePool = new Pool<Move>()
    {
        @Override
        protected Move newObject() {
            return new Move();
        }
    };

    public static Move get(Pawn pawn, Tile from, Tile to, Orientation orientation, Path path)
    {
        Move m = movePool.obtain();
        m.pawn = pawn;
        m.from = from;
        m.to = to;
        m.orientation = orientation;
        if (path != null) {
            m.init(path.tiles.size());
            m.cost = path.cost;
            m.roadMarch = path.roadMarch;
            for (Tile tile : path.tiles)
                m.tiles.add(tile);
        } else {
            m.init(0);
        }

        return m;
    }

    public static void clearPool()
    {
        movePool.clear();
    }

    public static Move getEnter(Pawn pawn, Tile from, Tile to, Orientation orientation, Path path)
    {
        Move m = get(pawn, from, to, orientation, path);
        m.type = MoveType.ENTER;
        return m;
    }

    public static Move getSet(Pawn pawn, Tile to, Orientation orientation)
    {
        Move m = get(pawn, null, to, orientation, null);
        m.type = MoveType.SET;
        m.cost = 0;
        return m;
    }

    public Pawn pawn;
    public Tile from;
    public Tile to;
    public Orientation orientation;
    public MoveType type;
    // iterator
    private int i;
    private int s;
    private Tile t;
    private boolean r;
    private Orientation o;
    private Vector3 v = new Vector3();
    private Vector2 pos = new Vector2();

    public Move()
    {
        super();
        this.pawn = null;
        this.from = null;
        this.to = null;
        this.orientation = Orientation.KEEP;
        this.type = MoveType.REGULAR;
    }

    @Override
    public void reset()
    {
        pawn = null;
        from = null;
        to = null;
        orientation = Orientation.KEEP;
        type = MoveType.REGULAR;
        super.reset();
    }

    @Override
    public void dispose()
    {
        tiles.clear();
        movePool.free(this);
    }

    public boolean isSet()
    {
        return (type == MoveType.SET);
    }

    public boolean isEnter()
    {
        return (type == MoveType.ENTER);
    }

    public boolean isRegular()
    {
        return (type == MoveType.REGULAR);
    }

    public int steps()
    {
        int steps = 0;

        Tile tile = from;
        Orientation o = pawn.getOrientation();
        for (Tile next : tiles) {
            Orientation nextO = Orientation.fromMove(tile.col, tile.row, next.col, next.row);
            if (nextO != o) {
                steps += 2;
                o = nextO;
            } else
                steps += 1;
            tile = next;
        }
        if (orientation != Orientation.fromMove(tile.col, tile.row, to.col, to.row))
            steps += 2;
        else
            steps +=1;

        return steps;
    }

    @Override
    public String toString()
    {
        if (from == null)
            return String.format("%s %s c:%d", to.toShort(), orientation, cost);
        else
            return String.format("%s->%s %s c:%d", from.toShort(), to.toShort(), orientation, cost);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Vector3> iterator()
    {
        this.i = 0;
        this.s = tiles.size();
        this.t = from;
        this.r = (from == to);
        this.o = pawn.getOrientation();
        this.v.set(pawn.getPosition().x, pawn.getPosition().y, o.r());
        return (Iterator<Vector3>) this;
    }

    @Override
    public boolean hasNext()
    {
        if ((r || (i > s)) && (o == orientation))
            return false;
        return true;
    }

    @Override
    public Vector3 next()
    {
        if (!hasNext())
            throw new java.util.NoSuchElementException();

        if (r || (i > s)) {
            v.z = orientation.r();
            o = orientation;
            return v;
        }
        Tile nextTile;
        if (i < s)
            nextTile = tiles.get(i);
        else
            nextTile = to;
        Orientation nextO = Orientation.fromMove(t.col, t.row, nextTile.col, nextTile.row);
        if (nextO != o) {
            v.z = nextO.r();
            o = nextO;
            return v;
        }
        pawn.getPosAt(nextTile, pos);
        v.x = pos.x;
        v.y = pos.y;
        t = nextTile;
        i += 1;
        return v;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
