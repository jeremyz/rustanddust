package ch.asynk.tankontank.engine;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Collection;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;

public abstract class Board implements Disposable
{
    private final Tile neighbours[] = new Tile[6];
    protected List<ArrayList<SearchBoard.Node>> paths;
    private final ArrayList<Vector3> finalPath = new ArrayList<Vector3>(10);

    public interface TileBuilder
    {
        public Tile getNewTile(float x, float y, int col, int row);
    }

    public interface TileCollection extends Collection<Tile>
    {
        public Tile first();
        public void enable(int i, boolean enable);
        public void collectPawns(PawnCollection pawns);
        public int fromNodes(Collection<SearchBoard.Node> nodes);
    }

    public interface PawnCollection extends Collection<Pawn>
    {
        public Pawn first();
        public void enable(int i, boolean enable);
        public void collectTiles(TileCollection tiles);
        public int fromNodes(Collection<SearchBoard.Node> nodes);
    }

    public static class Config
    {
        public int cols;
        public int rows;
        public int x0;          // bottom left x offset
        public int y0;          // bottom left y offset
        public int w;           // hex width
        public int dw;          // half hex : w/2
        public int s;           // hex side
        public float dh;        // hex top : s/2
        public float h;         // square height : s + dh
        public float slope;     // north-west side slope : (dh / (float) dw)
    }

    private final Pool<Vector3> vector3Pool = new Pool<Vector3>() {
        @Override
        protected Vector3 newObject() {
            return new Vector3();
        }
    };

    private Config cfg;
    private Tile[] tiles;
    private SearchBoard searchBoard;
    private Image image;

    private boolean transform;
    private Matrix4 prevTransform;
    private Matrix4 nextTransform;

    private int tileCount = 0;
    private int pawnCount = 0;
    private int animationCount = 0;
    private final ArrayList<Animation> animations = new ArrayList<Animation>(2);
    private final ArrayList<Animation> nextAnimations = new ArrayList<Animation>(2);
    private final LinkedHashSet<Tile> tilesToDraw = new LinkedHashSet<Tile>();

    protected Board(int cols, int rows)
    {
        searchBoard = new SearchBoard(this, cols, rows);
    }

    public Board(TileBuilder tileBuilder, Config cfg, Texture texture)
    {
        image = new Image(texture);
        this.cfg = cfg;
        this.tiles = new Tile[cfg.cols * cfg.rows];
        searchBoard = new SearchBoard(this, cfg.cols, cfg.rows);

        int idx = 0;
        boolean evenRow = true;
        float y = cfg.y0 - cfg.dh + cfg.s;
        for (int i = 0; i < cfg.rows; i++) {
            float x = cfg.x0 + cfg.dw;
            if (!evenRow) x += cfg.dw;
            for ( int j = 0; j < cfg.cols; j ++) {
                this.tiles[idx] = tileBuilder.getNewTile(x, y, (j + ((i + 1) / 2)), i);
                idx += 1;
                x += cfg.w;
            }
            y += cfg.h;
            evenRow = !evenRow;
        }
    }

    @Override
    public void dispose()
    {
        image.dispose();
        for (int i = 0; i < (cfg.cols * cfg.rows); i++)
            tiles[i].dispose();
        tilesToDraw.clear();
        for (int i = 0, n = nextAnimations.size(); i < n; i++)
            nextAnimations.get(i).dispose();
        animations.clear();
        for (int i = 0, n = animations.size(); i < n; i++)
            animations.get(i).dispose();
        animations.clear();
        for (Vector3 v : finalPath)
            vector3Pool.free(v);
        finalPath.clear();
    }

    public float getWidth()
    {
        return image.getWidth();
    }

    public float getHeight()
    {
        return image.getHeight();
    }

    public void setPosition(float x, float y)
    {
        image.setPosition(x, y);
        if ((x != 0.0f) || (y != 0.0f)) {
            transform = true;
            prevTransform = new Matrix4();
            nextTransform = new Matrix4();
            nextTransform.translate(x, y, 0);
        } else
            transform = false;
    }

    protected Tile getTile(int col, int row)
    {
        int colOffset = ((row + 1) / 2);
        if ((col < colOffset) || (row < 0) || (row >= cfg.rows) || ((col - colOffset) >= cfg.cols))
            return null;

        return tiles[((col - colOffset)) + (row * cfg.cols)];
    }

    private void setAdjacentTiles(Tile tile, Tile tiles[])
    {
        tiles[0] = getTile((tile.getCol() - 1), (tile.getRow()));
        tiles[1] = getTile((tile.getCol()),     (tile.getRow() + 1));
        tiles[2] = getTile((tile.getCol() + 1), (tile.getRow() + 1));
        tiles[3] = getTile((tile.getCol() + 1), (tile.getRow()));
        tiles[4] = getTile((tile.getCol()),     (tile.getRow() - 1));
        tiles[5] = getTile((tile.getCol() - 1), (tile.getRow() - 1));
    }

    protected void addAnimation(Animation a)
    {
        nextAnimations.add(a);
    }

    private void stats()
    {
        boolean print = false;

        if (tileCount != tilesToDraw.size()) {
            tileCount = tilesToDraw.size();
            print = true;
        }

        if (animationCount != animations.size()) {
            animationCount = animations.size();
            print = true;
        }

        if (print)
            Gdx.app.debug("Board", " tiles:" + tileCount + " pawns:" + pawnCount + " animations:" + animationCount);
    }

    public void animate(float delta)
    {

        Iterator<Animation> iter = animations.iterator();
        while (iter.hasNext()) {
            Animation a = iter.next();
            if (a.animate(delta))
                iter.remove();
        }

        for (int i = 0, n = nextAnimations.size(); i < n; i++)
            animations.add(nextAnimations.get(i));
        nextAnimations.clear();
    }

    public void draw(Batch batch)
    {
        image.draw(batch);

        if (transform) {
            prevTransform.set(batch.getTransformMatrix());
            batch.setTransformMatrix(nextTransform);
        }

        Iterator<Tile> tileIter = tilesToDraw.iterator();
        while (tileIter.hasNext())
            tileIter.next().draw(batch);

        Iterator<Animation> animationIter = animations.iterator();
        while (animationIter.hasNext())
            animationIter.next().draw(batch);

        if (transform)
            batch.setTransformMatrix(prevTransform);
    }

    public void drawDebug(ShapeRenderer debugShapes)
    {
        stats();
        if (transform) {
            prevTransform.set(debugShapes.getTransformMatrix());
            debugShapes.setTransformMatrix(nextTransform);
        }

        Iterator<Tile> iter = tilesToDraw.iterator();
        while (iter.hasNext())
            iter.next().drawDebug(debugShapes);

        Iterator<Animation> animationIter = animations.iterator();
        while (animationIter.hasNext())
            animationIter.next().drawDebug(debugShapes);

        if (transform)
            debugShapes.setTransformMatrix(prevTransform);
    }

    protected int collectPossibleMoves(Pawn pawn, TileCollection moves)
    {
        return searchBoard.possibleMovesFrom(pawn, moves);
    }

    protected int collectPossibleTargets(Pawn pawn, PawnCollection targets)
    {
        Tile from = pawn.getTile();
        List<SearchBoard.Node> nodes = searchBoard.possibleTargetsFrom(pawn, from.getCol(), from.getRow());
        return targets.fromNodes(nodes);
    }

    protected int collectPossibleTargets(Pawn pawn, Iterator<Pawn> units, PawnCollection targets)
    {
        Tile from = pawn.getTile();
        targets.clear();
        while (units.hasNext()) {
            Pawn target = units.next();
            if (!pawn.canAttack(target)) continue;
            Tile to = target.getTile();
            if (searchBoard.collectAttack(pawn, true, target, from.getCol(), from.getRow(), to.getCol(), to.getRow()))
                targets.add(target);
        }

        return targets.size();
    }

    protected int collectMoveAssists(Pawn pawn, PawnCollection assists)
    {
        assists.clear();
        setAdjacentTiles(pawn.getTile(), neighbours);
        for (int i = 0; i < 6; i++) {
            Tile t = neighbours[i];
            if (t != null) {
                Iterator<Pawn> pawns = t.iterator();
                while(pawns.hasNext()) {
                    Pawn p = pawns.next();
                    if ((p != null) && p.canMove() && !pawn.isEnemy(p))
                        assists.add(p);
                }
            }
        }
        return assists.size();
    }

    protected int collectAttackAssists(Pawn pawn, Pawn target, Iterator<Pawn> units, PawnCollection assists)
    {
        assists.clear();
        Tile to = target.getTile();
        while (units.hasNext()) {
            Pawn p = units.next();
            if ((p == pawn) || !p.canAttack()) continue;
            Tile from = p.getTile();
            if (searchBoard.collectAttack(p, !p.canAssistAttackWithoutLos(), target, from.getCol(), from.getRow(), to.getCol(), to.getRow())) {
                if (p != pawn)
                    assists.add(p);
            }
        }

        return assists.size();
    }

    private int nodesToSet(List<ArrayList<SearchBoard.Node>> nodes, TileCollection tiles)
    {
        tiles.clear();

        for (ArrayList<SearchBoard.Node> path : nodes) {
            for (int i = 1, n = (path.size() - 1); i < n; i++) {
                SearchBoard.Node node = path.get(i);
                Tile tile = getTile(node.col, node.row);
                if (!tiles.contains(tile))
                    tiles.add(tile);
            }
        }

        return nodes.size();
    }

    protected int collectPossiblePaths(Pawn pawn, Tile to, TileCollection tiles)
    {
        Tile from = pawn.getTile();
        paths = searchBoard.possiblePaths(pawn, from.getCol(), from.getRow(), to.getCol(), to.getRow());
        return nodesToSet(paths, tiles);
    }

    protected int possiblePathsFilterToggle(Tile tile, TileCollection tiles)
    {
        paths = searchBoard.possiblePathsFilterToggle(tile.getCol(), tile.getRow());
        return nodesToSet(paths, tiles);
    }

    protected int getPathCost(Pawn pawn, int i)
    {
        return searchBoard.pathCost(pawn, paths.get(i));
    }

    protected int getCoordinatePath(Pawn pawn, int idx, ArrayList<Vector3> path, Orientation finalOrientation)
    {
        for (Vector3 v : path)
            vector3Pool.free(v);
        path.clear();

        Vector2 tmpCoords = new Vector2();

        Vector3 p = pawn.getPosition();
        Vector3 v = vector3Pool.obtain();
        v.set(p.x, p.y, 0f);
        Orientation prevOrientation = pawn.getOrientation();

        ArrayList<SearchBoard.Node> nodes = paths.get(idx);
        SearchBoard.Node prevNode = nodes.get(0);
        // Gdx.app.debug("Board", "getCoordinatePath()");
        // Gdx.app.debug("Board", "  " + prevNode);

        for (int i = 1, n = nodes.size(); i < n; i++) {
            SearchBoard.Node node = nodes.get(i);
            // Gdx.app.debug("Board", "  " + node);
            Orientation o = Orientation.fromMove(prevNode.col, prevNode.row, node.col, node.row);
            if ((o != Orientation.KEEP) && (o != prevOrientation)) {
                v.z = o.r();
                path.add(v);
                v = vector3Pool.obtain();
            }
            pawn.getPosAt(getTile(node.col, node.row), tmpCoords);
            v.set(tmpCoords.x, tmpCoords.y, o.r());
            path.add(v);
            prevOrientation = o;
            v = vector3Pool.obtain();
            v.set(tmpCoords.x, tmpCoords.y, 0f);

            prevNode = node;
        }

        if (finalOrientation != prevOrientation) {
            v.z = finalOrientation.r();
            path.add(v);
        } else {
            vector3Pool.free(v);
        }

        // Gdx.app.debug("Board", " =>");
        // for (Vector3 vector :path)
        //     Gdx.app.debug("Board", "  " + vector);

        return path.size();
    }

    public void enableOverlayOn(Tile tile, int i, boolean enable)
    {
        if(tile.enableOverlay(i, enable))
            tilesToDraw.add(tile);
        else
            tilesToDraw.remove(tile);
    }

    public void enableOverlayOn(Tile tile, int i, Orientation o, boolean enable)
    {
        if(tile.enableOverlay(i, enable, o.r()))
            tilesToDraw.add(tile);
        else
            tilesToDraw.remove(tile);
    }

    private int pushPawnOnto(Pawn pawn, Tile tile)
    {
        tilesToDraw.add(tile);
        return tile.push(pawn);
    }

    public int removePawn(Pawn pawn)
    {
        Tile tile = pawn.getTile();
        int n = tile.remove(pawn);
        if (!tile.mustBeDrawn())
            tilesToDraw.remove(tile);
        return n;
    }

    public Pawn setPawnOnto(Pawn pawn, Tile tile, Orientation o)
    {
        return setPawnOnto(pawn, tile, o.r());
    }

    public Pawn setPawnOnto(Pawn pawn, Tile tile, float r)
    {
        pawn.setOnTile(tile, r);
        pushPawnOnto(pawn, tile);
        return pawn;
    }

    protected void movePawn(final Pawn pawn, int cost, Orientation o, RunnableAnimation whenDone)
    {
        getCoordinatePath(pawn, 0, finalPath, o);
        removePawn(pawn);

        AnimationSequence seq = pawn.getMoveAnimation(finalPath, 2);
        seq.addAnimation(RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                // FIXME pawn.getTile() is not ok
                Vector2 center = pawn.getCenter();
                setPawnOnto(pawn, getTileAt(center.x, center.y), pawn.getRotation());
            }
        }));
        seq.addAnimation(whenDone);
        addAnimation(seq);
        pawn.move(cost);
    }

    protected void rotatePawn(final Pawn pawn, Orientation o, RunnableAnimation whenDone)
    {
        Vector3 p = pawn.getPosition();
        Vector3 v = vector3Pool.obtain();
        v.set(p.x, p.y, o.r());
        AnimationSequence seq = pawn.getRotateAnimation(v, 1);
        seq.addAnimation(whenDone);
        addAnimation(seq);
        vector3Pool.free(v);
        pawn.rotate(o);
    }

    protected void revertLastPawnMove(final Pawn pawn, RunnableAnimation whenDone)
    {
        removePawn(pawn);

        AnimationSequence seq = pawn.getRevertLastMoveAnimation(2);
        seq.addAnimation(RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                pushPawnOnto(pawn, pawn.getTile());
            }
        }));
        seq.addAnimation(whenDone);
        addAnimation(seq);
        pawn.revertLastMove();
    }

    public Tile getTileAt(float mx, float my)
    {
        // compute row
        float y = (my - cfg.y0);
        int row = (int) (y / cfg.h);
        boolean oddRow = ((row % 2) == 1);
        if (y < 0.f) {
            row = -1;
            oddRow = true;
        }

        // compute col
        float x = (mx - cfg.x0);
        if (oddRow) x -= cfg.dw;
        int col = (int) (x / cfg.w);
        if (x < 0.f)
            col = -1;

        int colOffset = ((row +1) / 2);

        // check upper boundaries
        float dy = (y - (row * cfg.h));
        if (dy > cfg.s) {
            dy -= cfg.s;
            float dx = (x - (col * cfg.w));
            col += colOffset;
            if (dx < cfg.dw) {
                if ((dx * cfg.slope) < dy) {
                    // upper left corner
                    row += 1;
                    colOffset = ((row +1) / 2);
                }
            } else {
                if (((cfg.w - dx) * cfg.slope) < dy) {
                    // upper right corner
                    row += 1;
                    col += 1;
                    colOffset = ((row +1) / 2);
                }
            }
        } else
            col += colOffset;

        // validate hex
        if ((col < colOffset) || (row < 0) || (row >= cfg.rows) || ((col - colOffset) >= cfg.cols))
            return null;

        return getTile(col, row);
    }
}

