package ch.asynk.tankontank.engine;

import java.util.Set;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;

public abstract class Board implements Disposable
{
    public interface TileBuilder
    {
        public Tile getNewTile(float cx, float cy);
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

    private final Pool<GridPoint2> gridPoint2Pool = new Pool<GridPoint2>() {
        @Override
        protected GridPoint2 newObject() {
            return new GridPoint2();
        }
    };

    private final Pool<Vector3> vector3Pool = new Pool<Vector3>() {
        @Override
        protected Vector3 newObject() {
            return new Vector3();
        }
    };

    protected Config cfg;
    private Tile[] tiles;
    protected SearchBoard searchBoard;
    private Image image;

    boolean transform;
    private Matrix4 prevTransform;
    private Matrix4 nextTransform;

    private int tileCount = 0;
    private int pawnCount = 0;
    private int animationCount = 0;
    private final Vector<Animation> animations = new Vector<Animation>(2);
    private final Vector<Animation> nextAnimations = new Vector<Animation>(2);
    private final LinkedHashSet<Tile> tilesToDraw = new LinkedHashSet<Tile>();
    protected final LinkedHashSet<Pawn> pawnsToDraw = new LinkedHashSet<Pawn>();

    protected Board()
    {
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
                this.tiles[idx] = tileBuilder.getNewTile(x, y);
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
    }

    public Tile getTile(GridPoint2 coords)
    {
        return getTile(coords.x, coords.y);
    }

    public Tile getTile(int col, int row)
    {
        int idx = ((col - ((row + 1) / 2))) + (row * cfg.cols);
        // Gdx.app.debug("Board", " getTile: " + col + " ; " + row + " -> " + idx);
        return tiles[idx];
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

    private void addPawnAnimation(Pawn pawn, AnimationSequence seq)
    {
        pawnsToDraw.add(pawn);
        nextAnimations.add(seq);
    }

    private void stats()
    {
        boolean print = false;

        if (tileCount != tilesToDraw.size()) {
            tileCount = tilesToDraw.size();
            print = true;
        }

        if (pawnCount != pawnsToDraw.size()) {
            pawnCount = pawnsToDraw.size();
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
            Pawn p = a.getPawn();
            if (a.animate(delta)) {
                iter.remove();
                pawnsToDraw.remove(p);
            }
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
        while (tileIter.hasNext()) {
            tileIter.next().draw(batch);
        }

        Iterator<Pawn> pawnIter = pawnsToDraw.iterator();
        while (pawnIter.hasNext()) {
            pawnIter.next().draw(batch);
        }

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
        while (iter.hasNext()) {
            iter.next().drawDebug(debugShapes);
        }

        if (transform)
            debugShapes.setTransformMatrix(prevTransform);
    }

    public void clearNodesVector(Vector<GridPoint2> points)
    {
        for (GridPoint2 point : points)
            gridPoint2Pool.free(point);
        points.clear();
    }

    private void nodesToPoints(List<SearchBoard.Node> nodes, Vector<GridPoint2> points)
    {
        // for (GridPoint2 point : points)
        //     gridPoint2Pool.free(point);
        // points.clear();

        // for (SearchBoard.Node node : nodes) {
        //     GridPoint2 point = gridPoint2Pool.obtain();
        //     point.set(node.col, node.row);
        //     points.add(point);
        // }

        int ns = nodes.size();
        int ps = points.size();

        if (ps > ns) {
            for (int i = ns; i < ps; i++)
                gridPoint2Pool.free(points.get(i));
        }

        int i = 0;
        for (SearchBoard.Node node : nodes) {
            if (i < ps) {
                points.get(i).set(node.col, node.row);
            } else {
                GridPoint2 point = gridPoint2Pool.obtain();
                point.set(node.col, node.row);
                points.add(point);
            }
            i += 1;
        }
        points.setSize(ns);
    }

    public void possibleMovesFrom(Pawn pawn, GridPoint2 coords, Vector<GridPoint2> moves)
    {
        List<SearchBoard.Node> nodes = searchBoard.possibleMovesFrom(pawn, coords.x, coords.y);
        nodesToPoints(nodes, moves);
    }

    public void possibleTargetsFrom(Pawn pawn, GridPoint2 coords, Vector<GridPoint2> targets)
    {
        List<SearchBoard.Node> nodes = searchBoard.possibleTargetsFrom(pawn, coords.x, coords.y);
        nodesToPoints(nodes, targets);
    }

    public void clearNodesSet(Set<GridPoint2> points)
    {
        for (GridPoint2 point : points)
            gridPoint2Pool.free(point);
        points.clear();
    }

    private int nodesToSet(List<Vector<SearchBoard.Node>> nodes, Set<GridPoint2> points)
    {
        // FIXME : optimize this
        for (GridPoint2 point : points)
            gridPoint2Pool.free(point);
        points.clear();

        for (Vector<SearchBoard.Node> path : nodes) {
            for (int i = 0, n = path.size(); i < n; i++) {
                // FIXME : optimize this
                GridPoint2 point = gridPoint2Pool.obtain();
                SearchBoard.Node node = path.get(i);
                point.set(node.col, node.row);
                if (!points.add(point))
                    gridPoint2Pool.free(point);
            }
        }

        return nodes.size();
    }

    public int possiblePaths(Pawn pawn, GridPoint2 from, GridPoint2 to, Set<GridPoint2> points)
    {
        List<Vector<SearchBoard.Node>> paths = searchBoard.possiblePaths(pawn, from.x, from.y, to.x, to.y);
        return nodesToSet(paths, points);
    }

    public int possiblePathsFilterToggle(GridPoint2 coords, Set<GridPoint2> points)
    {
        List<Vector<SearchBoard.Node>> paths = searchBoard.possiblePathsFilterToggle(coords.x, coords.y);
        return nodesToSet(paths, points);
    }

    public int getFinalPath(Pawn pawn, Vector<Vector3> path, Orientation finalOrientation)
    {
        List<Vector<SearchBoard.Node>> paths = searchBoard.possiblePaths();

        for (Vector3 v : path)
            vector3Pool.free(v);
        path.clear();

        if (paths.size() != 1)
            return 0;

        Vector2 pawnPos = new Vector2();
        SearchBoard.Node prevNode = null;
        Vector3 prevVector = null;
        Orientation o = Orientation.KEEP;
        GridPoint2 coords = gridPoint2Pool.obtain();

        Vector<SearchBoard.Node> nodes = paths.get(0);

        for (int i = (nodes.size() - 1); i >= 0; i--) {
            SearchBoard.Node node = nodes.get(i);

            if (prevVector == null) {
                Vector3 p = pawn.getLastPosition();
                prevVector = vector3Pool.obtain();
                prevVector.set(p.x, p.y, 0f);
            } else {
                o = Orientation.fromMove(prevNode.col, prevNode.row, node.col, node.row);
                prevVector.z = o.r();
                path.add(prevVector);
                coords.set(node.col, node.row);
                getPawnPosAt(pawn, coords, pawnPos);
                prevVector = vector3Pool.obtain();
                prevVector.set(pawnPos.x, pawnPos.y, 0f);
            }

            prevNode = node;
        }

        prevVector.z = finalOrientation.r();
        path.add(prevVector);

        gridPoint2Pool.free(coords);

        return path.size();
    }

    public boolean hasUnits(GridPoint2 coords)
    {
        return getTile(coords).hasUnits();
    }

    public boolean isOffMap(GridPoint2 coords)
    {
        return getTile(coords).isOffMap();
    }

    public boolean isOverlayEnabledOn(GridPoint2 coords, int i)
    {
        return getTile(coords).isOverlayEnabled(i);
    }

    // public void disableOverlaysOn(GridPoint2 coords)
    // {
    //     Tile tile = getTile(coords);
    //     if (tile.disableOverlays())
    //         tilesToDraw.add(tile);
    //     else
    //         tilesToDraw.remove(tile);
    // }

    public void enableOverlayOn(GridPoint2 coords, int i, boolean enable)
    {
        Tile tile = getTile(coords);
        if(tile.enableOverlay(i, enable))
            tilesToDraw.add(tile);
        else
            tilesToDraw.remove(tile);
    }

    // public Pawn removeTopPawnFrom(GridPoint2 coords)
    // {
    //     Pawn pawn = getTopPawnAt(coords);
    //     if (pawn != null)
    //         removePawnFrom(pawn, coords);
    //     return pawn;
    // }

    public Pawn getTopPawnAt(GridPoint2 coords)
    {
        return getTile(coords).getTopPawn();
    }

    private int pushPawnAt(Pawn pawn, GridPoint2 coords)
    {
        Tile tile = getTile(coords);
        tilesToDraw.add(tile);
        return tile.push(pawn);
    }

    private int removePawnFrom(Pawn pawn, GridPoint2 coords)
    {
        Tile tile = getTile(coords);
        int n = tile.remove(pawn);
        if (!tile.mustBeDrawn())
            tilesToDraw.remove(tile);
        return n;
    }

    // public Vector2 getTileCenter(GridPoint2 coords)
    // {
    //     return getTile(coords).getCenter();
    // }

    public Vector2 getPawnPosAt(Pawn pawn, GridPoint2 coords, Vector2 pos)
    {
        Vector2 center = getTile(coords).getCenter();
        return pawn.getPosAt(center, pos);
    }

    public void setPawnAt(Pawn pawn, GridPoint2 coords, Orientation o)
    {
        Vector2 pos = getPawnPosAt(pawn, coords, null);
        pawn.pushMove(pos.x, pos.y, o);
        pushPawnAt(pawn, coords);
    }

    // public void movePawnTo(Pawn pawn, GridPoint2 coords)
    // {
    //     movePawnTo(pawn, coords, Orientation.KEEP);
    // }

    // public void movePawnTo(Pawn pawn, GridPoint2 coords, Orientation o)
    // {
    //     removePawnFrom(pawn, getHexAt(pawn.getLastPosition()));

    //     pushPawnAt(pawn, coords);
    //     Vector2 pos = getPawnPosAt(pawn, coords, null);
    //     pawn.pushMove(pos.x, pos.y, o);
    // }

    public void movePawn(final Pawn pawn, Vector<Vector3> path)
    {
        removePawnFrom(pawn, getHexAt(pawn.getLastPosition()));

        final Vector3 finalPos = path.get(path.size() - 1);

        AnimationSequence seq = pawn.getMoveAnimation(path);
        seq.addAnimation(RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                pushPawnAt(pawn, getHexAt(finalPos));
            }
        }));
        addPawnAnimation(pawn, seq);
    }

    // public void resetPawnMoves(final Pawn pawn)
    // {
    //     removePawnFrom(pawn, getHexAt(pawn.getLastPosition()));

    //     AnimationSequence seq = pawn.getResetMovesAnimation();
    //     seq.addAnimation(RunnableAnimation.get(pawn, new Runnable() {
    //         @Override
    //         public void run() {
    //             GridPoint2 coords = getHexAt(pawn.getLastPosition());
    //             pushPawnAt(pawn, coords);
    //         }
    //     }));
    //     addPawnAnimation(pawn, seq);
    // }

    private GridPoint2 getHexAt(Vector3 v)
    {
        if (v == null) return null;
        return getHexAt(null, v.x, v.y);
    }

    public GridPoint2 getHexAt(GridPoint2 hex, float mx, float my)
    {
        if (hex == null) hex = new GridPoint2();

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
            hex.set(-1, -1);
        else
            hex.set(col, row);

        // Gdx.app.debug("Board", " hex: " + hex.x + " ; " + hex.y);
        return hex;
    }
}

