package ch.asynk.tankontank.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PossiblePaths implements Disposable
{
    private final Board board;

    public Pawn pawn;
    public Tile from;
    public Tile to;
    public int distance;
    public Orientation orientation;
    private List<Tile> stack;
    private List<Tile> ctrlTiles;
    private List<Path> paths;
    private List<Path> filteredPaths;
    private HashSet<Tile> tiles;

    public PossiblePaths(Board board, int tSize, int stSize, int ftSize, int vectSize)
    {
        this.board = board;
        this.tiles = new LinkedHashSet<Tile>(tSize);
        this.stack = new ArrayList<Tile>(stSize);
        this.ctrlTiles = new ArrayList<Tile>(ftSize);
        this.paths = new LinkedList<Path>();
        this.filteredPaths = new LinkedList<Path>();
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

    public boolean isSet()
    {
        return (to != null);
    }

    @Override
    public void dispose()
    {
        clear();
    }

    public void clear()
    {
        this.to = null;
        this.distance = -1;
        this.orientation = Orientation.KEEP;
        for (Path path : this.paths) path.dispose();
        this.tiles.clear();
        this.stack.clear();
        this.ctrlTiles.clear();
        this.paths.clear();
        this.filteredPaths.clear();
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
        this.distance = board.distance(from, to);
        if (distance < 2) {
            Orientation o = Orientation.fromMove(to.col, to.row, from.col, from.row);
            Path path = Path.get(0);
            path.roadMarch = to.road(o);
            path.cost = to.costFrom(pawn, o);
            paths.add(path);
        } else {
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
            if ((next == null) || next.isOffMap()) continue;

            Orientation o = board.getSide(i);
            int m = (mvtLeft - next.costFrom(pawn, o));
            boolean r = roadMarch & next.road(o);

            int l = (m + (r ? pawn.getRoadMarchBonus() : 0));

            if ((board.distance(next, to) <= l)) {
                if (next == to) {
                    Path path = Path.get(stack.size() + 1);
                    for (Tile t: stack) {
                        path.tiles.add(t);
                        tiles.add(t);
                    }
                    path.roadMarch = r;
                    path.cost = (pawn.getMovementPoints() - m);
                    paths.add(path);
                } else {
                    stack.add(next);
                    findAllPaths(next, m, r);
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
        for (Path path : paths) {
            int ok = 0;
            for (Tile filter : ctrlTiles) {
                if (path.tiles.contains(filter))
                    ok += 1;
            }
            if (ok == s) {
                if (path.tiles.size() == (s + 0)) { // from and to are not part of the path
                    filteredPaths.clear();
                    filteredPaths.add(path);
                    tiles.clear();
                    for (Tile tile : path.tiles) tiles.add(tile);
                    break;
                } else {
                    filteredPaths.add(path);
                    for (Tile tile : path.tiles) tiles.add(tile);
                }
            }
        }

        // printToErr("filteredPaths", filteredPaths);
        return filteredPaths.size();
    }

    public int pathCost(int i)
    {
        return paths.get(i).cost;
    }

    public Move getMove()
    {
        if (size() != 1)
            return null;

        return Move.get(pawn, from, to, orientation, getPath(0));
    }

    public Path getPath(int i)
    {
        if (ctrlTiles.size() == 0)
            return paths.get(i);
        return filteredPaths.get(i);
    }

    public void setExit(Orientation exit)
    {
        Path path = getPath(0);
        path.cost += 1;
        path.tiles.add(to);
        to = board.getAdjTileAt(to, exit);
    }

    private void printToErr(String what, List<Path> paths)
    {
        System.err.println(what + pawn + " ("+paths.size()+") " + from + " -> " + to);
        for (Path path : paths) {
            System.err.println(String.format(" - path (l:%d c:%d r:%b)", path.tiles.size(), path.cost, path.roadMarch));
            for(Tile tile : path.tiles)
                System.err.println("   " + tile.toString());
        }
        System.err.println();
    }
}
