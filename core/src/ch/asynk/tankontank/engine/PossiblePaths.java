package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PossiblePaths implements Iterable<Vector3>
{
    private final Board board;

    public Pawn pawn;
    public Tile from;
    public Tile to;
    public Orientation orientation;
    private List<Tile> stack;
    private List<Tile> ctrlTiles;
    private List<ArrayList<Tile>> paths;
    private List<ArrayList<Tile>> filteredPaths;
    private Board.TileCollection tiles;

    public PossiblePaths(Board board, int tSize, int stSize, int ftSize, int vectSize)
    {
        this.board = board;
        this.tiles = new TileSet(board, tSize);
        this.stack = new ArrayList<Tile>(stSize);
        this.ctrlTiles = new ArrayList<Tile>(ftSize);
        this.paths = new LinkedList<ArrayList<Tile>>();
        this.filteredPaths = new LinkedList<ArrayList<Tile>>();
        this.to = null;
        this.pawn = null;
        this.orientation = Orientation.KEEP;
    }

    public void init(Pawn pawn, Tile from)
    {
        this.pawn = pawn;
        this.from = from;
    }

    public void init(Pawn pawn)
    {
        init(pawn, pawn.getTile());
    }

    public void clear()
    {
        for (List<Tile> tiles : this.paths) tiles.clear();
        for (List<Tile> tiles : this.filteredPaths) tiles.clear();
        this.stack.clear();
        this.paths.clear();
        this.ctrlTiles.clear();
        this.filteredPaths.clear();
        this.tiles.clear();
        this.to = null;
        this.orientation = Orientation.KEEP;
    }

    public int size()
    {
        if (ctrlTiles.size() == 0)
            return paths.size();
        return filteredPaths.size();
    }

    public boolean contains(Tile tile)
    {
        return tiles.contains(tile);
    }

    public void enable(int i, boolean enable)
    {
        for (Tile tile : tiles)
            board.enableOverlayOn(tile, i, enable);
    }

    public int build(Tile to)
    {
        clear();
        this.to = to;
        // from and to are not part of the path
        if (board.distance(from, to) == 1) {
            ArrayList<Tile> temp = new ArrayList<Tile>(0);
            // temp.add(from);
            // temp.add(to);
            paths.add(temp);
            for (Tile tile : temp) tiles.add(tile);
        } else {
            // stack.add(from);
            findAllPaths(from, pawn.getMovementPoints(), true);
        }

        // printToErr("paths", paths);
        stack.clear();
        return paths.size();
    }

    private void findAllPaths(Tile from, int mvtLeft, boolean roadMarch)
    {
        Tile moves[] = new Tile[6];
        board.setAdjacentTiles(from, moves);

        for(int i = 0; i < 6; i++) {
            Tile next = moves[i];
            if (next == null) continue;

            int cost = next.costFrom(pawn, board.getSide(i));
            int r = (mvtLeft - cost);
            roadMarch &= next.road(board.getSide(i));
            if (roadMarch) r += pawn.getRoadMarchBonus();

            if ((board.distance(next, to) <= r)) {
                if (next == to) {
                    ArrayList<Tile> temp = new ArrayList<Tile>(stack.size() + 1);
                    for (Tile t: stack)
                        temp.add(t);
                    // temp.add(next);
                    paths.add(temp);
                    for (Tile tile : temp) tiles.add(tile);
                } else {
                    stack.add(next);
                    findAllPaths(next, (mvtLeft - cost), roadMarch);
                    stack.remove(stack.size() - 1);
                }
            }
        }
    }

    public int toggleCtrlTile(Tile tile)
    {
        if (ctrlTiles.contains(tile))
            ctrlTiles.remove(tile);
        else
            ctrlTiles.add(tile);
        return filterPaths();
    }

    private int filterPaths()
    {
        int s = ctrlTiles.size();

        tiles.clear();
        filteredPaths.clear();
        for (ArrayList<Tile> path : paths) {
            int ok = 0;
            for (Tile filter : ctrlTiles) {
                if (path.contains(filter))
                    ok += 1;
            }
            if (ok == s) {
                if (path.size() == (s + 0)) { // from and to are not part of the path
                    filteredPaths.clear();
                    filteredPaths.add(path);
                    tiles.clear();
                    for (Tile tile : path) tiles.add(tile);
                    break;
                } else {
                    filteredPaths.add(path);
                    for (Tile tile : path) tiles.add(tile);
                }
            }
        }

        // printToErr("filteredPaths", filteredPaths);
        return filteredPaths.size();
    }

    public int pathCost(int i)
    {
        int cost = 0;
        boolean roadMarch = true;
        Tile prev = null;

        for (Tile next : paths.get(i)) {
            if (prev != null) {
                Orientation o = Orientation.fromMove(next.col, next.row, prev.col, prev.row);
                cost += next.costFrom(pawn, o);
                roadMarch &= next.road(o);
            }
            prev = next;
        }

        if (roadMarch)
            cost -= pawn.getRoadMarchBonus();
        if (cost < 1)
            cost = 1;

        return cost;
    }

    private List<Tile> getPath(int i)
    {
        if (ctrlTiles.size() == 0)
            return paths.get(0);
        return filteredPaths.get(0);
    }

    public int pathSteps(int idx)
    {
        int steps = 0;

        Tile tile = from;
        Orientation o = pawn.getOrientation();
        for (Tile next : getPath(idx)) {
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
    public Iterator<Vector3> iterator()
    {
        return new Vector3Iterator(pawn, from, to, orientation, getPath(0));
    }

    private void printToErr(String what, List<ArrayList<Tile>> paths)
    {
        System.err.println(what + " ("+paths.size()+")");
        for (ArrayList<Tile> path : paths) {
            System.err.println(" - path");
            for(Tile tile : path)
                System.err.println("   " + tile);
        }
        System.err.println();
    }
}

class Vector3Iterator implements Iterator<Vector3>
{
    private Pawn pawn;
    private Tile to;
    private Orientation o;
    private Orientation orientation;
    private Tile tile;
    private Vector2 pos = new Vector2();
    private Vector3 v = new Vector3();
    private int i;
    private List<Tile> path;

    public Vector3Iterator(Pawn pawn, Tile from, Tile to, Orientation orientation, List<Tile> path)
    {
        this.pawn = pawn;
        this.to = to;
        this.tile = from;
        this.orientation = orientation;
        this.path = path;
        this.o = pawn.getOrientation();
        this.v.set(pawn.getPosition().x, pawn.getPosition().y, o.r());
        this.i = 0;
    }

    @Override
    public boolean hasNext()
    {
        if ((tile == to) && (o == orientation))
            return false;
        return true;
    }

    @Override
    public Vector3 next()
    {
        if (tile == to) {
            v.z = orientation.r();
            o = orientation;
            return v;
        }
        Tile nextTile;
        if (i < path.size())
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
