package ch.asynk.tankontank.engine;

import java.util.Iterator;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Vector3;

public class Move extends Path implements Iterable<Vector3>
{
    public enum MoveType
    {
        REGULAR,
        SET,
        ENTER,
        EXIT;
    }

    private static final Pool<Move> movePool = new Pool<Move>() {
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

    public static Move getEnter(Pawn pawn, Tile to, Orientation orientation)
    {
        Move m = get(pawn, null, to, orientation, null);
        m.type = MoveType.ENTER;
        m.cost = to.costFrom(pawn, orientation);
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

    public boolean isEntry()
    {
        return (type == MoveType.ENTER);
    }

    public boolean isComplete()
    {
        return (type != MoveType.ENTER);
    }

    public void setExit()
    {
        type = MoveType.EXIT;
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
    public Iterator<Vector3> iterator()
    {
        return new PathIterator(pawn, from, to, orientation, tiles);
    }
}
