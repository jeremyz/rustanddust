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
        return getPaths().size();
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
        if (from == to) {
            this.distance = 0;
            Path path = Path.get(0);
            path.cost = 0;
            paths.add(path);
        } else {
            this.distance = board.distance(from, to);
            findAllPaths(from, pawn.getMovementPoints(), 0, true);
        }

        // printToErr("paths", paths);
        stack.clear();
        return paths.size();
    }

    public int chooseBest()
    {
        List<Path> ps = getPaths();

        if (ps.size() > 1) {
            Path good = ps.get(0);
            for (Path path : ps) {
                if ( (path.fitness > good.fitness) || ((path.fitness == good.fitness) && (path.cost < good.cost)))
                    good = path;
            }

            ps.remove(good);
            clearPaths();
            paths.add(good);
            for (Tile tile : good.tiles)
                tiles.add(tile);
        }

        return 1;
    }

    public int chooseShortest()
    {
        List<Path> ps = getPaths();

        if (ps.size() > 1) {
            Path good = ps.get(0);
            for (Path path : ps) {
                if (path.tiles.size() < good.tiles.size())
                    good = path;
            }

            ps.remove(good);
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
            int m = (mvtLeft - next.costFrom(pawn, o));
            int f = (fitness + (next.isObjectiveFor(pawn) ? 1 : 0));
            boolean r = (roadMarch && next.road(o));

            int l = (m + (r ? pawn.getRoadMarchBonus() : 0));

            if ((next == to) && ((l >= 0) || ((stack.size() == 0) && next.atLeastOneMove(pawn)))) {
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

            if (l >= board.distance(next, to)) {
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
            System.err.println("ERROR PathBuilder not a single path found");
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
        int mvt = pawn.getMovementPoints();
        int cost = to.exitCost();
        int rBonus = (to.road(o) ? pawn.getRoadMarchBonus() : 0);

        for (Path p : getPaths()) {
            if (pathCanExit(p, mvt, cost, rBonus))
                return true;
        }
        return false;
    }

    private boolean pathCanExit(Path p, int mvt, int cost, int rBonus)
    {
        int c = (p.cost + cost);
        if ((c <= mvt) || (p.roadMarch && (c <= (mvt + rBonus))))
            return true;
        return false;
    }

    private List<Path> getPaths()
    {
        if (ctrlTiles.size() == 0)
            return paths;
        return filteredPaths;
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
        System.err.println(what + " " + paths.size() + " - " + from + " -> " + to);
        for (Path path : paths) {
            System.err.println(String.format(" - path (l:%d c:%d r:%b f:%d)", path.tiles.size(), path.cost, path.roadMarch, path.fitness));
            for(Tile tile : path.tiles)
                System.err.println("   " + tile.toString());
        }
        System.err.println();
    }
}
