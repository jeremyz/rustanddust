package ch.asynk.rustanddust.engine;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.rustanddust.engine.util.Collection;
import ch.asynk.rustanddust.engine.util.IterableArray;
import ch.asynk.rustanddust.engine.util.IterableStack;
import ch.asynk.rustanddust.engine.util.IterableSet;

public class PathBuilder implements Disposable
{
    private final Board board;

    public Pawn pawn;
    public Tile from;
    public Tile to;
    public int distance;
    public Orientation orientation;
    private IterableStack<Tile> stack;
    private Collection<Tile> ctrlTiles;
    private Collection<Path> paths;
    private Collection<Path> filteredPaths;
    private IterableSet<Tile> tiles;

    public PathBuilder(Board board, int tSize, int stSize, int ftSize, int psSize)
    {
        this.board = board;
        this.tiles = new IterableSet<Tile>(tSize);
        this.stack = new IterableStack<Tile>(stSize);
        this.ctrlTiles = new IterableArray<Tile>(ftSize);
        this.paths = new IterableArray<Path>(psSize);
        this.filteredPaths = new IterableArray<Path>(psSize);
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
            path.cost = 1;
            path.roadMarch = false;
            paths.add(path);
        } else {
            this.distance = board.distance(from, to);
            findAllPaths(from, pawn.getMovementPoints(), 0, true);
            beSmart();
        }

        // printToErr("paths", getPaths());
        stack.clear();
        return getPaths().size();
    }

    public int buildShortest(Tile to)
    {
        clear();
        this.to = to;
        this.distance = board.distance(from, to);
        findAllPaths(from, pawn.getMovementPoints(), 0, true);
        chooseShortest();

        // printToErr("paths", getPaths());
        stack.clear();
        return getPaths().size();
    }

    private void beSmart()
    {
        Tile o = null;
        boolean only = true;
        for (Tile t : tiles) {
            if (t.isObjectiveFor(pawn)) {
                if (o == null)
                    o = t;
                else {
                    only = false;
                    break;
                }
            }
        }

        if (o == null)
            chooseShortest();
        else if (only) {
            toggleCtrlTile(o, false);
            chooseBest();
        }
    }

    public int chooseBest()
    {
        Collection<Path> ps = getPaths();

        if (ps.size() > 1) {
            Path good = null;
            for (Path p : ps)
                good = best(good, p);
            keepOnly(good);
        }

        return size();
    }

    public int chooseShortest()
    {
        Collection<Path> ps = getPaths();

        if (ps.size() > 1) {
            Path good = ps.get(0);
            for (Path path : ps) {
                if (path.tiles.size() < good.tiles.size())
                    good = path;
            }
            keepOnly(good);
        }

        return size();
    }

    public int chooseExit(Orientation o)
    {
        Collection<Path> ps = getPaths();

        Path good = ps.get(0);
        int mvt = pawn.getMovementPoints();
        int cost = to.exitCost();
        int rBonus = (to.roadFrom(o) ? pawn.getRoadMarchBonus() : 0);

        if (ps.size() > 1) {
            good = null;
            for (Path p : getPaths()) {
                if (pathCanExit(p, mvt, cost, rBonus))
                    good = best(good, p);
            }

            keepOnly(good);
        }

        if (!pathCanExit(good, mvt, cost, rBonus))
            throw new RuntimeException("chosen path can't exit");

        orientation = o;
        if (from != to) {
            good.cost += 1;
            good.tiles.add(to);
        }
        to = board.getAdjTileAt(to, o);

        return size();
    }

    private Path best(Path a, Path b)
    {
        if (a == null)
            return b;
           // better fitness or equal and ...
        if ((b.fitness > a.fitness) || ((b.fitness == a.fitness) &&
                // smaller cost or equal and ...
                ((b.cost < a.cost) || ((b.cost == a.cost) &&
                    // roadmarch or same roadmarch and ...
                    ((b.roadMarch && !a.roadMarch) || ((b.roadMarch == a.roadMarch) &&
                        // smaller
                        (b.tiles.size() < a.tiles.size())
                    ))
                ))
            ))
            return b;
        return a;
    }

    private void keepOnly(Path path)
    {
        paths.remove(path);
        clearPaths();
        paths.add(path);
        for (Tile tile : path.tiles)
            tiles.add(tile);
    }

    private void findAllPaths(Tile from, int mvtLeft, int fitness, boolean roadMarch)
    {
        Tile moves[] = new Tile[6];
        board.setAdjacentTiles(from, moves);

        for (int i = 0; i < 6; i++) {
            Tile next = moves[i];
            if ((next == null) || next.isOffMap()) continue;

            Orientation o = board.getSide(i);
            int m = (mvtLeft - next.costFrom(pawn, o));
            int f = (fitness + (next.isObjectiveFor(pawn) ? 1 : 0));
            boolean r = (roadMarch && next.roadFrom(o));

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
                stack.push(next);
                findAllPaths(next, m, f, r);
                stack.pop();
            }
        }
    }

    public int toggleCtrlTile(Tile tile, boolean shortest)
    {
        if (ctrlTiles.contains(tile))
            ctrlTiles.remove(tile);
        else
            ctrlTiles.add(tile);
        return filterPaths(shortest);
    }

    private int filterPaths(boolean shortest)
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
                if (shortest && path.tiles.size() == (s + 0)) { // from and to are not part of the path
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

    public Move getEnterMove(Pawn pawn, Tile from)
    {
        // getPath(0).tiles.insert(this.from, 0);
        // this.from = from;
        int cost = pawn.getSpentMovementPoints();
        pawn.reset();
        Move move = getMove();
        move.type = Move.MoveType.ENTER;
        move.cost = cost + ((this.from == this.to) ? 0 : move.cost);
        return move;
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
        int rBonus = (to.roadFrom(o) ? pawn.getRoadMarchBonus() : 0);

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

    private Collection<Path> getPaths()
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

    private void printToErr(String what, Collection<Path> paths)
    {
        System.err.println(what + " " + paths.size() + " - " + from + " -> " + to);
        for (Path path : paths) {
            System.err.println(String.format(" - path (l:%d c:%d r:%b f:%d)", path.tiles.size(), path.cost, path.roadMarch, path.fitness));
            for (Tile tile : path.tiles)
                System.err.println("   " + tile.toString());
        }
        System.err.println();
    }
}
