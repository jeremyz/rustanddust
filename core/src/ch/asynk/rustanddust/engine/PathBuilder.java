package ch.asynk.rustanddust.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.badlogic.gdx.utils.Disposable;

public class PathBuilder implements Disposable
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

    public PathBuilder(Board board, int tSize, int stSize, int ftSize, int vectSize)
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

    public void initRotation(Pawn pawn, Orientation o)
    {
        init(pawn, pawn.getTile());
        build(pawn.getTile());
        orientation = o;
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
        this.clearPaths();
    }

    private void clearPaths()
    {
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
        findAllPaths(from, pawn.getMovementPoints(), 0, true);

        // printToErr("paths", paths);
        stack.clear();
        return paths.size();
    }

    public int choosePath()
    {
        if (paths.size() > 1) {
            int f = Integer.MAX_VALUE;
            Path good = null;
            for (Path path : paths) {
                if (path.fitness < f) {
                    good = path;
                    f = path.fitness;
                }
            }

            paths.remove(good);
            clearPaths();
            paths.add(good);
            for (Tile tile : good.tiles)
                tiles.add(tile);
        }

        return 1;
    }

    private void findAllPaths(Tile from, int mvtLeft, int fitness, boolean roadMarch)
    {
        Tile moves[] = new Tile[6];
        board.setAdjacentTiles(from, moves);

        for(int i = 0; i < 6; i++) {
            Tile next = moves[i];
            if ((next == null) || next.isOffMap()) continue;

            Orientation o = board.getSide(i);
            int n = next.costFrom(pawn, o);
            boolean r = next.road(o);
            int f = (fitness + 1 + (r ? 0 : 1));
            if (next.isObjectiveFor(pawn)) f -= 4;

            int m = (mvtLeft - n);
            r &= roadMarch;

            int l = (m + (r ? pawn.getRoadMarchBonus() : 0));

            if (board.distance(next, to) <= l) {
                if (next == to) {
                    Path path = Path.get(stack.size() + 1);
                    for (Tile t: stack) {
                        path.tiles.add(t);
                        tiles.add(t);
                    }
                    path.roadMarch = r;
                    path.fitness = f;
                    path.cost = (pawn.getMovementPoints() - m);
                    paths.add(path);
                }
                stack.add(next);
                findAllPaths(next, m, f, r);
                stack.remove(stack.size() - 1);
            }
        }
    }

    public int toggleCtrlTile(Tile tile, boolean quick)
    {
        if (ctrlTiles.contains(tile))
            ctrlTiles.remove(tile);
        else
            ctrlTiles.add(tile);
        return filterPaths(quick);
    }

    private int filterPaths(boolean quick)
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
                if (quick && path.tiles.size() == (s + 0)) { // from and to are not part of the path
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
        if (size() != 1) {
            System.err.println("ERROR not a sigle path found");
            printToErr("paths", paths);
            return null;
        }

        return Move.get(pawn, from, to, orientation, getPath(0));
    }

    public Move getExitMove()
    {
        Move move = getMove();
        move.type = Move.MoveType.EXIT;
        return move;
    }

    public boolean canExit(Orientation o)
    {
        List<Path> ps;
        if (ctrlTiles.size() == 0)
            ps = paths;
        else
            ps = filteredPaths;

        int mvt = pawn.getMovementPoints();
        int rBonus = pawn.getRoadMarchBonus();
        boolean road =  to.road(o);
        int cost = to.exitCost();

        for (Path p : ps) {
            int c = (p.cost + cost);
            if ((c <= mvt) || (p.roadMarch && road && (c <= (mvt + rBonus))))
                return true;
        }
        return false;
    }

    public Path getPath(int i)
    {
        if (ctrlTiles.size() == 0)
            return paths.get(i);
        return filteredPaths.get(i);
    }

    public void setExit(Orientation o)
    {
        orientation = o;
        Path path = getPath(0);
        if (from != to) {
            path.cost += 1;
            path.tiles.add(to);
        }
        to = board.getAdjTileAt(to, o);
    }

    private void printToErr(String what, List<Path> paths)
    {
        System.err.println(what + pawn + " ("+paths.size()+") " + from + " -> " + to);
        for (Path path : paths) {
            System.err.println(String.format(" - path (l:%d c:%d r:%b f:%d)", path.tiles.size(), path.cost, path.roadMarch, path.fitness));
            for(Tile tile : path.tiles)
                System.err.println("   " + tile.toString());
        }
        System.err.println();
    }
}
